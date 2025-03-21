SET @scd_encounter_initial=(select encounter_type_id from encounter_type where name = 'SICKLE_CELL_DISEASE_INITIAL');

SET @cc_program_id = (select program_id from program where name='CHRONIC CARE PROGRAM');

SET @cc_state_on_treatment = (select program_workflow_state_id from program_workflow_state where uuid = '5925718D-EA5E-43EB-9AE2-1CB342D8E318');
SET @cc_state_in_advanced_care = (select program_workflow_state_id from program_workflow_state where uuid = 'E0381FF3-2976-41F0-B853-28E842400E84');
SET @cc_state_transferred_out = (select program_workflow_state_id from program_workflow_state where uuid = '41AF39C1-7CE6-47E0-9BA7-9FD7C0354C12');
SET @cc_state_died = (select program_workflow_state_id from program_workflow_state where uuid = 'D79B02C2-B473-47F1-A51C-6D40B2242B9C');
SET @cc_state_discharged = (select program_workflow_state_id from program_workflow_state where uuid = '42ACC789-C2BB-4EAA-8AC2-0BE7D0F5D4E8');
SET @cc_state_defaulted = (select program_workflow_state_id from program_workflow_state where uuid = '19CEF51A-0823-4876-A8AF-7285B7077494');
SET @cc_state_treatment_stopped = (select program_workflow_state_id from program_workflow_state where uuid = '9F6F188C-42AB-45D8-BC8B-DBE78948072D');

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
         left join encounter e on pp.patient_id = e.patient_id and e.encounter_type = @scd_encounter_initial and e.voided = 0
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

