SET @asthma_encounter_initial=(select encounter_type_id from encounter_type where name = 'ASTHMA_INITIAL');
SET @cc_program_id = (select program_id from program where name='CHRONIC CARE PROGRAM');

SET @cc_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = '66882650-977f-11e1-8993-905e29aff6c1');
SET @cc_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = '7c4d2e56-c8c2-11e8-9bc6-0242ac110001');
SET @cc_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = '6688275e-977f-11e1-8993-905e29aff6c1');
SET @cc_state_died = (select program_workflow_state_id from program_workflow_state where uuid = '6688286c-977f-11e1-8993-905e29aff6c1');
SET @cc_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = '6688297a-977f-11e1-8993-905e29aff6c1');
SET @cc_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = '3a4eb919-b942-4c9c-ba0e-defcebe5cd4b');
SET @cc_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = 'dbe76d47-dbc4-4608-a578-97b6b62d9f63');

SET @asthma_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = '7f2fc125-f9bc-4195-b879-3060a386468a');
SET @asthma_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = '8f395143-5f5a-4171-8e10-aef931e16bcf');
SET @asthma_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = '51398403-593f-4df3-8dcf-5f467f60612b');
SET @asthma_state_died = (select program_workflow_state_id from program_workflow_state where uuid = 'f58f10a5-7ce4-4e52-b5fd-128531730d12');
SET @asthma_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = 'bad2b98c-0741-476a-9024-9d3d7680c5b5');
SET @asthma_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = '1b427904-f99b-4441-84d6-a9e421d5d6b2');
SET @asthma_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = '0a441fb0-f29d-46b2-a8b8-709adce58d0d');

DROP TABLE IF EXISTS asthma_patient_state_migration;

create table asthma_patient_state_migration (
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

-- select all non-voided old Chronic Care treatment statuses for patients with a Asthma Initial encounter
insert into asthma_patient_state_migration(
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
         inner join encounter e on pp.patient_id = e.patient_id and e.encounter_type = @asthma_encounter_initial and e.voided = 0
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

-- migrate former Chronic Care treatment statuses to the new Asthma treatment statuses
update asthma_patient_state_migration m1, asthma_patient_state_migration m2
set m1.new_state = case
                       when m2.old_state = @cc_state_on_treatment then @asthma_state_on_treatment
                       when m2.old_state = @cc_state_in_advanced_care then @asthma_state_in_advanced_care
                       when m2.old_state = @cc_state_transferred_out then @asthma_state_transferred_out
                       when m2.old_state = @cc_state_died then @asthma_state_died
                       when m2.old_state = @cc_state_discharged then @asthma_state_discharged
                       when m2.old_state = @cc_state_defaulted then @asthma_state_defaulted
                       when m2.old_state = @cc_state_treatment_stopped then @asthma_state_treatment_stopped
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
from asthma_patient_state_migration where new_state is not null;
