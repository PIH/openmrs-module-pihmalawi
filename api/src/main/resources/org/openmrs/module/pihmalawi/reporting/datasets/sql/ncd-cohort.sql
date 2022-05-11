
/* 30 for age in months*/
SET @birthDateDivider = 365;

call create_last_ncd_outcome_at_facility(@endDate,@location);



select distinct(mwp.patient_id), opi.identifier, mwp.first_name, mwp.last_name,  mwp.gender,mwp.birthdate,
 datediff(@endDate,mwp.birthdate)/@birthDateDivider as age, ops.program, ops.state,ops.start_date, ops.location, patient_visit.last_appt_date
from  mw_patient mwp
LEFT join (
	SELECT patient_id, MAX(obs_date) as visit_date, max(value_date) as last_appt_date FROM omrs_obs
where concept = "Appointment date" and encounter_type IN ("MENTAL_HEALTH_FOLLOWUP","ASTHMA_FOLLOWUP","EPILEPSY_FOLLOWUP","CHRONIC_CARE_FOLLOWUP",
"DIABETES HYPERTENSION FOLLOWUP","CKD_FOLLOWUP","CHF_FOLLOWUP","NCD_OTHER_FOLLOWUP") and obs_date <= @endDate
group by patient_id
            ) patient_visit
            on patient_visit.patient_id = mwp.patient_id
join omrs_patient_identifier opi
on mwp.patient_id = opi.patient_id
JOIN
         last_ncd_facility_outcome as ops
            on opi.patient_id = ops.pat and opi.location = ops.location
            where opi.type = "Chronic Care Number"
