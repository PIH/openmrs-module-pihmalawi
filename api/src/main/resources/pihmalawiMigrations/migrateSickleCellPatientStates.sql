SET @scd_encounter_initial=(select encounter_type_id from encounter_type where name = 'SICKLE_CELL_DISEASE_INITIAL');
SET @cc_program_id = (select program_id from program where name='CHRONIC CARE PROGRAM');

SET @cc_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = '66882650-977f-11e1-8993-905e29aff6c1');
SET @cc_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = '7c4d2e56-c8c2-11e8-9bc6-0242ac110001');
SET @cc_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = '6688275e-977f-11e1-8993-905e29aff6c1');
SET @cc_state_died = (select program_workflow_state_id from program_workflow_state where uuid = '6688286c-977f-11e1-8993-905e29aff6c1');
SET @cc_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = '6688297a-977f-11e1-8993-905e29aff6c1');
SET @cc_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = '3a4eb919-b942-4c9c-ba0e-defcebe5cd4b');
SET @cc_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = 'dbe76d47-dbc4-4608-a578-97b6b62d9f63');

SET @scd_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = 'C2B106C6-18B6-4342-B2E7-FAA0540E6DC2');
SET @scd_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = '03A8A8DF-E95E-4875-B730-2D3CD86502EF');
SET @scd_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = 'A843A2AE-FB7B-48B2-A5C2-73A82890D709');
SET @scd_state_died = (select program_workflow_state_id from program_workflow_state where uuid = '5E228F5D-BA90-4F25-9524-E79ABAEFA01F');
SET @scd_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = 'A3EA7AD8-FB32-4567-A19E-C3F6E9E33C7B');
SET @scd_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = 'E850CEB4-B01B-47E0-AED2-4BAB1EE2A645');
SET @scd_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = '15072B28-46E9-412D-BCA4-3C96803C15AD');

DROP TABLE IF EXISTS scd_patient_state_migration;

create table scd_patient_state_migration (
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

-- select all non-voided old Chronic Care treatment statuses for patients with a Sickle Cell Disease Initial encounter
insert into scd_patient_state_migration(
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
         inner join encounter e on pp.patient_id = e.patient_id and e.encounter_type = @scd_encounter_initial and e.voided = 0
where  pp.program_id = @cc_program_id
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

-- migrate former Chronic Care treatment statuses to the new Sickle Cell Disease treatment statuses
update scd_patient_state_migration m1, scd_patient_state_migration m2
set m1.new_state = case
                       when m2.old_state = @cc_state_on_treatment then @scd_state_on_treatment
                       when m2.old_state = @cc_state_in_advanced_care then @scd_state_in_advanced_care
                       when m2.old_state = @cc_state_transferred_out then @scd_state_transferred_out
                       when m2.old_state = @cc_state_died then @scd_state_died
                       when m2.old_state = @cc_state_discharged then @scd_state_discharged
                       when m2.old_state = @cc_state_defaulted then @scd_state_defaulted
                       when m2.old_state = @cc_state_treatment_stopped then @scd_state_treatment_stopped
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
from scd_patient_state_migration where new_state is not null;
