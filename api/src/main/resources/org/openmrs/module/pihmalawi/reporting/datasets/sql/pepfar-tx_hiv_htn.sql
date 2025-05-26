/***************************************************************************
PEPFAR TB PREV REPORT

The number of people living with HIV (PLHIV) on ART who are diagnosed and controlled for
hypertension (HTN) during the reporting period.
***************************************************************************/

SET @defaultOneMonth = 30;
SET @birthDateDivider = 30;

SET @dayOfEndDate = DAY(@endDate);
set @startDate=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);


call create_age_groups();
call create_last_art_outcome_at_facility(@endDate,@location);

select sort_value,x.age_group, CASE WHEN x.gender="F" then 'Female' else 'Male' end as gender,
CASE WHEN tx_curr is null then 0 else tx_curr end as tx_curr,
CASE WHEN diagnosed_htn is null then 0 else diagnosed_htn end as diagnosed_htn,
CASE WHEN screened_for_htn is null then 0 else screened_for_htn end as screened_for_htn,
CASE WHEN newly_diagnosed is null then 0 else newly_diagnosed end as newly_diagnosed,
CASE WHEN controlled_htn is null then 0 else controlled_htn end as controlled_htn
 from
age_groups as x
LEFT OUTER JOIN
(
SELECT CASE
	WHEN age <= 11 and gender = "M" THEN "< 1 year"
	WHEN age <= 11 and gender = "F" THEN "< 1 year"
	WHEN age >=12 and age <= 59 and gender = "M" THEN "1-4 years"
	WHEN age >=12 and age <= 59 and gender = "F" THEN "1-4 years"
	WHEN age >=60 and age <= 119 and gender = "M" THEN "5-9 years"
	WHEN age >=60 and age <= 119 and gender = "F" THEN "5-9 years"
	WHEN age >=120 and age <= 179 and gender = "M" THEN "10-14 years"
	WHEN age >=120 and age <= 179 and gender = "F" THEN "10-14 years"
	WHEN age >=180 and age <= 239 and gender = "M" THEN "15-19 years"
	WHEN age >=180 and age <= 239 and gender = "F" THEN "15-19 years"
	WHEN age >=240 and age <= 299 and gender = "M" THEN "20-24 years"
	WHEN age >=240 and age <= 299 and gender = "F" THEN "20-24 years"
	WHEN age >=300 and age <= 359 and gender = "M" THEN "25-29 years"
	WHEN age >=300 and age <= 359 and gender = "F" THEN "25-29 years"
	WHEN age >=360 and age <= 419 and gender = "M" THEN "30-34 years"
	WHEN age >=360 and age <= 419 and gender = "F" THEN "30-34 years"
	WHEN age >=420 and age <= 479 and gender = "M" THEN "35-39 years"
	WHEN age >=420 and age <= 479 and gender = "F" THEN "35-39 years"
	WHEN age >=480 and age <= 539 and gender = "M" THEN "40-44 years"
	WHEN age >=480 and age <= 539 and gender = "F" THEN "40-44 years"
	WHEN age >=540 and age <= 599 and gender = "M" THEN "45-49 years"
	WHEN age >=540 and age <= 599 and gender = "F" THEN "45-49 years"
	WHEN age >=600 and age <= 659 and gender = "M" THEN "50-54 years"
	WHEN age >=600 and age <= 659 and gender = "F" THEN "50-54 years"
	WHEN age >=660 and age <= 719 and gender = "M" THEN "55-59 years"
	WHEN age >=660 and age <= 719 and gender = "F" THEN "55-59 years"
	WHEN age >=720 and age <= 779 and gender = "M" THEN "60-64 years"
	WHEN age >=720 and age <= 779 and gender = "F" THEN "60-64 years"
	WHEN age >=780 and age <= 839 and gender = "M" THEN "65-69 years"
	WHEN age >=780 and age <= 839 and gender = "F" THEN "65-69 years"
	WHEN age >=840 and age <= 899 and gender = "M" THEN "70-74 years"
	WHEN age >=840 and age <= 899 and gender = "F" THEN "70-74 years"
	WHEN age >=900 and age <= 959 and gender = "M" THEN "75-79 years"
	WHEN age >=900 and age <= 959 and gender = "F" THEN "75-79 years"
	WHEN age >=960 and age <= 1019 and gender = "M" THEN "80-84 years"
	WHEN age >=960 and age <= 1019 and gender = "F" THEN "80-84 years"
	WHEN age >=1020 and age <= 1079 and gender = "M" THEN "85-89 years"
	WHEN age >=1020 and age <= 1079 and gender = "F" THEN "85-89 years"
	WHEN age >=1080 and gender = "M" THEN "90 plus years"
	WHEN age >=1080 and gender = "F" THEN "90 plus years"
END as age_group,gender,
    COUNT(IF((state = 'On antiretrovirals' and floor(datediff(@endDate,last_appt_date)) <=  @defaultOneMonth), 1, NULL)) as tx_curr,

    COUNT(IF((systolic_bp >= 140 OR diastolic_bp >= 90) AND patient_id NOT IN (
    select patient_id from omrs_patient_identifier where type = "ARV Number" and location != @location)
    and patient_id IN(select patient_id from omrs_patient_identifier where type = "ARV Number" and location = @location), 1, NULL)) as diagnosed_htn,

    COUNT(IF(followup_visit_date BETWEEN @startDate AND @endDate and patient_id NOT IN (
    select patient_id from omrs_patient_identifier where type = "ARV Number" and location != @location)
    and (systolic_bp is not null OR diastolic_bp is not null)
    and patient_id IN(select patient_id from omrs_patient_identifier where type = "ARV Number" and location = @location), 1, NULL)) as screened_for_htn,

    COUNT(IF(initial_visit_date BETWEEN @startDate AND @endDate and transfer_in_date is null
    and (systolic_bp >= 140 OR diastolic_bp >= 90)
    and patient_id NOT IN (select patient_id from omrs_patient_identifier where type = "ARV Number" and location != @location)
    and patient_id IN(select patient_id from omrs_patient_identifier where type = "ARV Number" and location = @location), 1, NULL)) as newly_diagnosed,

    COUNT(IF(followup_visit_date BETWEEN @startDate AND @endDate and patient_id NOT IN (
    select patient_id from omrs_patient_identifier where type = "ARV Number" and location != @location)
    and (systolic_bp < 140 AND diastolic_bp < 90)
    and patient_id IN(select patient_id from omrs_patient_identifier where type = "ARV Number" and location = @location), 1, NULL)) as controlled_htn

from
(
select distinct(mwp.patient_id), opi.identifier, mwp.first_name, mwp.last_name, ops.program, ops.state,ops.start_date,program_state_id,  mwp.gender,
 If(ops.state = "On antiretrovirals",floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider),floor(datediff(ops.start_date,mwp.birthdate)/@birthDateDivider)) as age,
 ops.location, patient_visit.last_appt_date, patient_visit.art_regimen as current_regimen,
 patient_visit.pregnant_or_lactating, patient_initial_visit.initial_pregnant_or_lactating, patient_visit.followup_visit_date, patient_initial_visit.initial_visit_date,
 patient_initial_visit.transfer_in_date, systolic_bp, diastolic_bp
from  mw_patient mwp
LEFT join (
    select map.patient_id, map.visit_date as followup_visit_date, map.next_appointment_date as last_appt_date, map.art_regimen,
    map.pregnant_or_lactating, systolic_bp, diastolic_bp
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
            where opi.type = "ARV Number"
)sub1
 group by age_group,gender, location
 order by gender,age_group,location, state
 ) cohort on x.age_group = cohort.age_group
 and x.gender = cohort.gender
 order by sort_value;