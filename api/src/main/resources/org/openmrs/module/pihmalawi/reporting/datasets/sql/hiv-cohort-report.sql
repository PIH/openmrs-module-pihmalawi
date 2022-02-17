
/* 30 for age in months*/
SET @birthDateDivider = 365;

call create_last_art_outcome_at_facility(@endDate, @location);

select  opi.identifier, l.source_key as location_key, mwp.first_name, mwp.last_name, ops.state,ops.start_date, mwp.gender,
 If(ops.state = "On antiretrovirals",floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider),floor(datediff(ops.start_date,mwp.birthdate)/@birthDateDivider)) as age,
 ops.location, patient_visit.last_appt_date, patient_visit.art_regimen as current_regimen, patient_visit.hiv_preventive_therapy, patient_visit.therapy_pills,
 patient_visit.pregnant_or_lactating as pregnant_or_lactating_at_last_visit, patient_initial_visit.initial_pregnant_or_lactating as pregnant_or_lactating_at_initial,
 patient_initial_visit.initial_visit_date,patient_initial_visit.transfer_in_date
from  mw_patient mwp
LEFT join (
select map.patient_id, map.visit_date as followup_visit_date, map.next_appointment_date as last_appt_date, map.art_regimen, map.hiv_preventive_therapy,map.therapy_pills,
    map.pregnant_or_lactating
    from mw_art_followup map
join
(
select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as last_appt_date from mw_art_followup where visit_date <= @endDate
group by patient_id
) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date) patient_visit
            on patient_visit.patient_id = mwp.patient_id
LEFT join (
select mar.patient_id, mar.visit_date as initial_visit_date,
    mar.pregnant_or_lactating as initial_pregnant_or_lactating, mar.transfer_in_date
    from mw_art_initial mar
join
(
select patient_id,MAX(visit_date) as visit_date  from mw_art_initial where visit_date <= @endDate
group by patient_id
) mar1
ON mar.patient_id = mar1.patient_id and mar.visit_date = mar1.visit_date) patient_initial_visit
            on patient_initial_visit.patient_id = mwp.patient_id
join omrs_patient_identifier opi
on mwp.patient_id = opi.patient_id
JOIN
         last_facility_outcome as ops
            on opi.patient_id = ops.pat and opi.location = ops.location
            join lookup_location as l on ops.location = l.target_value
            where opi.type = "ARV Number"