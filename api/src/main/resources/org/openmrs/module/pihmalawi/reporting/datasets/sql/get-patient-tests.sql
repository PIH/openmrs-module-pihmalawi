/************************************************************************

  Patient Lab Tests Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @patientUuid = 'UUID_of_a_patient';

*************************************************************************/

select p.patient_id,
       p.patient_uuid,
       p.identifier,
       p.first_name,
       p.last_name,
       p.gender,
       p.birthdate,
       t.date_collected,
       t.test_type,
       t.date_result_entered,
       t.result_coded,
       t.result_numeric,
       t.result_exception
from omrs_patient p
  inner join mw_lab_tests t on p.patient_id = t.patient_id
where  p.patient_uuid = @patientUuid;
