SET @mh_encounter_initial=(select encounter_type_id from encounter_type where name = 'MENTAL_HEALTH_INITIAL');
SET @epilepsy_encounter_initial = (select encounter_type_id from encounter_type where name = 'EPILEPSY_INITIAL');
SET @mh_program_id = (select program_id from program where name='MENTAL HEALTH CARE PROGRAM');
SET @cc_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = '5925718D-EA5E-43EB-9AE2-1CB342D8E318');
SET @mh_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = '2F76D426-56A9-4651-B253-A2299B442C09');
SET @epilepsy_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = 'CB86C6FE-4263-4A4C-AF54-49D5308459D4');
SET @cc_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = 'E0381FF3-2976-41F0-B853-28E842400E84');
SET @mh_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = '79F2CAB1-E674-433E-AF42-447678FDB443');
SET @epilepsy_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = '61190A43-95FF-4C84-8A3F-DD7F5354171C');
SET @cc_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = '41AF39C1-7CE6-47E0-9BA7-9FD7C0354C12');
SET @epilepsy_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = 'F63ED5E5-1707-43FA-BCA0-CE271C338AE2');
SET @mh_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = 'CE543EAB-40A0-4021-9264-E8FFE835759F');
SET @cc_state_died = (select program_workflow_state_id from program_workflow_state where uuid = 'D79B02C2-B473-47F1-A51C-6D40B2242B9C');
SET @epilepsy_state_died = (select program_workflow_state_id from program_workflow_state where uuid = 'FB0B61BD-A641-499B-BA87-421DC7E1CA2C');
SET @mh_state_died = (select program_workflow_state_id from program_workflow_state where uuid = '5FB84A00-8AEC-42F5-8CE0-6006A1B58653');
SET @cc_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = '42ACC789-C2BB-4EAA-8AC2-0BE7D0F5D4E8');
SET @epilepsy_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = 'B7FADD7E-6143-4BA8-90F3-629F79D02CD9');
SET @mh_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = '6C704865-5355-412B-9A9F-46489C301B6B');

SET @cc_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = '19CEF51A-0823-4876-A8AF-7285B7077494');
SET @epilepsy_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = '96D5D27B-31CB-4BC7-AA5C-C6EC28B121DE');
SET @mh_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = 'A1F672EA-EB8D-4B7D-8193-146C309AF348');

SET @cc_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = '9F6F188C-42AB-45D8-BC8B-DBE78948072D');
SET @epilepsy_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = 'AAE431AF-96E6-477F-B15E-2E5C66B20AEF');
SET @mh_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = '6633F174-E20C-4D03-B6CB-3EBD2433EE75');

DROP TABLE IF EXISTS mh_patient_state_migration;

create table mh_patient_state_migration (
                                            state_migration_id int PRIMARY KEY AUTO_INCREMENT,
                                            patient_id int,
                                            patient_state_id int,
                                            patient_program_id int,
                                            old_state int,
                                            new_state int,
                                            start_date DATETIME,
                                            end_date DATETIME,
                                            creator int,
                                            date_created DATETIME,
                                            voided tinyint(1) NOT NULL DEFAULT '0',
                                            uuid   char(38),
                                            encounter_id int,
                                            encounter_type int,
                                            encounter_datetime DATETIME
);

-- select all non-voided old Mental Health treatment statuses (former Chronic Care treatment statuses)

insert into mh_patient_state_migration(
    patient_id,
    patient_state_id,
    patient_program_id,
    old_state,
    start_date,
    end_date,
    creator,
    date_created,
    uuid,
    encounter_id,
    encounter_type,
    encounter_datetime
)
select
    pp.patient_id,
    ps.patient_state_id,
    ps.patient_program_id,
    ps.state,
    ps.start_date,
    ps.end_date,
    ps.creator,
    now(),
    uuid(),
    e.encounter_id,
    e.encounter_type,
    e.encounter_datetime
from patient_state ps
         inner join patient_program pp on ps.patient_program_id = pp.patient_program_id
         inner join program_workflow_state pws on ps.state=pws.program_workflow_state_id
         left join encounter e on pp.patient_id = e.patient_id and (e.encounter_type = @mh_encounter_initial or e.encounter_type = @epilepsy_encounter_initial) and e.voided = 0
where  pp.program_id = @mh_program_id
  and ps.state in (
                   @cc_state_on_treatment,
                   @cc_state_in_advanced_care,
                   @cc_state_transferred_out,
                   @cc_state_died,
                   @cc_state_discharged,
                   @cc_state_defaulted,
                   @cc_state_treatment_stopped
    ) and ps.voided = 0
order by ps.patient_program_id desc;

SET SQL_SAFE_UPDATES = 0;

			-- migrate former Chronic Care treatment statuses to the new MH treatment and Epilepsy treatment statuses
update mh_patient_state_migration m1, mh_patient_state_migration m2
set m1.new_state = case
    when m2.old_state = @cc_state_on_treatment and (m2.encounter_type is null or m2.encounter_type = @mh_encounter_initial) then @mh_state_on_treatment
    when m2.old_state = @cc_state_on_treatment and (m2.encounter_type = @epilepsy_encounter_initial) then @epilepsy_state_on_treatment
    when m2.old_state = @cc_state_in_advanced_care and (m2.encounter_type is null or m2.encounter_type = @mh_encounter_initial) then @mh_state_in_advanced_care
    when m2.old_state = @cc_state_in_advanced_care and (m2.encounter_type = @epilepsy_encounter_initial) then @epilepsy_state_in_advanced_care
    when m2.old_state = @cc_state_transferred_out and (m2.encounter_type is null or m2.encounter_type = @mh_encounter_initial) then @mh_state_transferred_out
    when m2.old_state = @cc_state_transferred_out and (m2.encounter_type = @epilepsy_encounter_initial) then @epilepsy_state_transferred_out
    when m2.old_state = @cc_state_died and (m2.encounter_type is null or m2.encounter_type = @mh_encounter_initial) then @mh_state_died
    when m2.old_state = @cc_state_died and (m2.encounter_type = @epilepsy_encounter_initial) then @epilepsy_state_died
    when m2.old_state = @cc_state_discharged and (m2.encounter_type is null or m2.encounter_type = @mh_encounter_initial) then @mh_state_discharged
    when m2.old_state = @cc_state_discharged and (m2.encounter_type = @epilepsy_encounter_initial) then @epilepsy_state_discharged
    when m2.old_state = @cc_state_defaulted and (m2.encounter_type is null or m2.encounter_type = @mh_encounter_initial) then @mh_state_defaulted
    when m2.old_state = @cc_state_defaulted and (m2.encounter_type = @epilepsy_encounter_initial) then @epilepsy_state_defaulted
    when m2.old_state = @cc_state_treatment_stopped and (m2.encounter_type is null or m2.encounter_type = @mh_encounter_initial) then @mh_state_treatment_stopped
    when m2.old_state = @cc_state_treatment_stopped and (m2.encounter_type = @epilepsy_encounter_initial) then @epilepsy_state_treatment_stopped
    else null
end
where m1.state_migration_id = m2.state_migration_id;

			SET SQL_SAFE_UPDATES = 1;

			-- add new patient states with the migrated treatment statuses
insert into patient_state(
    patient_program_id,
    state,
    start_date,
    end_date,
    creator,
    date_created,
    uuid
)
select
    patient_program_id,
    new_state,
    start_date,
    end_date,
    creator,
    date_created,
    uuid
from mh_patient_state_migration where new_state is not null;

SET SQL_SAFE_UPDATES = 0;

			-- void old MH patient states that were migrated to the new Epilepsy and MH treatment workflow states
update patient_state ps, mh_patient_state_migration m
set ps.voided = 1
where ps.patient_state_id = m.patient_state_id and m.new_state is not null;

SET SQL_SAFE_UPDATES = 1;