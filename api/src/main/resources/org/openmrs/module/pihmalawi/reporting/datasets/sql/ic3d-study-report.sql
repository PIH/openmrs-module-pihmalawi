/************************************************************************

  IC3D DATA ENTRY REPORT
  Requires OpenMRS warehouse database Tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @startDate = '2021-12-12';
  -- set @endDate = now();
  -- set @location = 'Matandani Rural Health Center';

Report will support the IC3D study easily extract patient data
*************************************************************************/

call create_last_mental_health_outcome_at_facility(@endDate,@location);
call create_last_art_outcome_at_facility(@endDate,@location);
call create_last_ncd_outcome_without_mental_at_facility(@endDate,@location);

 select * from
 (
select mh.pat,
CASE WHEN mh.pat is not null THEN (select identifier from omrs_patient_identifier where type='Chronic Care Number' and location=@location and patient_id = mh.pat  limit 1) END cc_number,
CASE WHEN mh.pat is not null THEN (select identifier from omrs_patient_identifier where type='ARV Number' and location=@location and patient_id = mh.pat limit 1) END arv_number,
CASE WHEN mh.pat is not null THEN (select identifier from omrs_patient_identifier where type='IC3D Identifier' and patient_id = mh.pat  limit 1) END ic3d_identifier,
mp.first_name, mp.last_name,
avl.visit_date as viral_visit_date,
mh.state as mental_health_state,
lfo.state as hiv_state,
nfo.state  as cc_state,
diagnosis_mood_affective_disorder_depression,
diagnosis_date_mood_affective_disorder_depression,
mhi.visit_date as mental_visit_date,
reason_for_test, viral_load_result, less_than_limit, ldl,
mdbhi.diagnosis_type_1_diabetes, mdbhi.diagnosis_hypertension,
mdbhf.bp_stystolic, mdbhf.bp_diastolic,mdbhf.finger_stick,ef.seizure_since_last_visit, ef.number_of_seizures,
mai.diagnosis_asthma,mai.diagnosis_copd, maf.asthma_severity,
case when ef.visit_date is not null then "Yes" else "No" end as epilepitic,
case when mdbhi.diagnosis_hypertension is not null then "Yes" else "No" end as hypertensive,
case when mdbhi.diagnosis_type_1_diabetes is not null then "Yes" else "No" end as type_1_diabetes,
case when mdbhi.diagnosis_type_2_diabetes is not null then "Yes" else "No" end as type_2_diabetes,
case when mai.diagnosis_asthma is not null then "Yes" else "No" end as asthmatic
from last_mental_facility_outcome mh
left join
last_facility_outcome lfo
on mh.pat = lfo.pat
left join
last_ncd_without_mental_facility_outcome nfo
on mh.pat = nfo.pat
left join
(
select map.patient_id, map.visit_date as visit_date,
map.diagnosis_mood_affective_disorder_depression,map.diagnosis_date_mood_affective_disorder_depression
from mw_mental_health_initial map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_mental_health_initial where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
) mhi
 on mhi.patient_id = mh.pat
left join
(
select map.patient_id, map.visit_date as visit_date,
map.reason_for_test, map.viral_load_result, map.less_than_limit, map.ldl
from mw_art_viral_load map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_art_viral_load where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
) avl
on lfo.pat = avl.patient_id
left join (
select map.patient_id,map.visit_date, map.diagnosis_type_1_diabetes,map.diagnosis_type_1_diabetes_date,
map.diagnosis_type_2_diabetes, map.diagnosis_type_2_diabetes_date,
map.diagnosis_hypertension, map.diagnosis_hypertension_date
from mw_diabetes_hypertension_initial map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_diabetes_hypertension_initial where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
) mdbhi
on mdbhi.patient_id = nfo.pat
left join (
select map.patient_id,map.visit_date, map.bp_diastolic,map.bp_stystolic, map.hba1c,
map.fasting_blood_sugar as finger_stick
from mw_diabetes_hypertension_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_diabetes_hypertension_followup where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
) mdbhf
on mdbhf.patient_id = nfo.pat
left join (
select map.patient_id,map.visit_date, map.seizure_since_last_visit, map.number_of_seizures
from mw_epilepsy_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_epilepsy_followup where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
) ef
on ef.patient_id = nfo.pat
left join
(
select map.patient_id,map.visit_date, map.diagnosis_asthma,map.diagnosis_copd
from mw_asthma_initial map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_asthma_initial where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
) mai
on mai.patient_id = nfo.pat
left join
(
select map.patient_id,map.visit_date, map.asthma_severity
from mw_asthma_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_asthma_followup where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
) maf
on maf.patient_id = nfo.pat
left join mw_patient mp
on mh.pat = mp.patient_id
left join
omrs_patient_identifier dev
on dev.patient_id = nfo.pat
where (diagnosis_mood_affective_disorder_depression is not null
or diagnosis_date_mood_affective_disorder_depression is not null)
and mhi.visit_date >= @startDate
group by mh.pat) x
where ic3d_identifier is not null;