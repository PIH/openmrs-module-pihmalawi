-- ## report_uuid = F889749C-36CA-40CC-A631-93E6730EC0A0
-- ## design_uuid = 6279E31E-702B-4754-97E1-99641E7CC4E5
-- ## report_name = Data Quality -  Duplicate encounters
-- ## report_description = Report listing patients with duplicate encounters(same encounter type on the same day)  (revision: November 2022)
-- ## parameter = startDate|Start Date|java.util.Date
-- ## parameter = endDate|End Date|java.util.Date


# SET @startDate = '2022-01-01';
# SET @endDate = '2022-11-29';

select distinct a.patient_id as 'PID',
                a.encounter_id as 'EncounterId',
                a.encounter_type as 'EncounterTypeId',
                t.name as 'EncounterType',
                a.encounter_datetime as 'EncounterDateTime',
                a.voided as 'Voided'
from encounter a
  inner join encounter_type t on a.encounter_type = t.encounter_type_id
  inner join encounter b on a.patient_id=b.patient_id and a.encounter_type=b.encounter_type and a.encounter_datetime = b.encounter_datetime
where a.encounter_id <> b.encounter_id and a.voided=0 and b.voided = 0 and a.encounter_datetime > @startDate and a.encounter_datetime < @endDate
order by a.patient_id desc, a.encounter_datetime desc;
