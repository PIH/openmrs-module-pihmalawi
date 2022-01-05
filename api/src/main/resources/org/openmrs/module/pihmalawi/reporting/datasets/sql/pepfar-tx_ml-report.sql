
/* 30 for age in months*/
SET @birthDateDivider = 30;
--SET @location = "Neno District Hospital";
--SET @startDate = "2020-01-01";
--SET @endDate = "2021-06-30";
SET @defaultOneMonth = 28;

call create_age_groups();
call create_last_art_outcome_at_facility(@endDate,@location);

select sort_value,x.age_group, x.gender,
CASE WHEN patient_died is null then 0 else patient_died end as "Died",
CASE WHEN  ITT_less_3months is null then 0 else ITT_less_3months end as "IIT_3mo_or_less_mo",
CASE WHEN  ITT_less_3to5_months is null then 0 else ITT_less_3to5_months end as "IIT_3to5_mo",
CASE WHEN  ITT_6months_plus_months is null then 0 else ITT_6months_plus_months end as "IIT_6plus_mo",
CASE WHEN patient_transferred_out is null then 0 else patient_transferred_out end as "Transferred_out",
CASE WHEN treatment_stopped is null then 0 else treatment_stopped end as "Refused_Stopped"
 from
age_groups as x
LEFT OUTER JOIN
(
SELECT CASE
WHEN age <= 5 and gender = "M" THEN "0-5 months"
WHEN age <= 5 and gender = "F" THEN "0-5 months"
    WHEN age >=6 and age <= 11 and gender = "M" THEN "6-11 months"
WHEN age >=6 and age <= 11 and gender = "F" THEN "6-11 months"
WHEN age >=12 and age <= 23 and gender = "M" THEN "12-23 months"
WHEN age >=12 and age <= 23 and gender = "F" THEN "12-23 months"
    WHEN age >=24 and age <= 59 and gender = "M" THEN "2-4 years"
WHEN age >=24 and age <= 59 and gender = "F" THEN "2-4 years"
WHEN age >=60 and age <= 119 and gender = "M" THEN "5-9 years"
WHEN age >=60 and age <= 119 and gender = "F" THEN "5-9 years"
WHEN age >=120 and age <= 179 and gender = "M" THEN "10-14 years"
WHEN age >=120 and age <= 179 and gender = "F" THEN "10-14 years"
    WHEN age >=180 and age <= 215 and gender = "M" THEN "15-17 years"
WHEN age >=180 and age <= 215 and gender = "F" THEN "15-17 years"
WHEN age >=216 and age <= 239 and gender = "M" THEN "18-19 years"
WHEN age >=216 and age <= 239 and gender = "F" THEN "18-19 years"
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
    WHEN age >=600 and gender = "M" THEN "50 plus years"
WHEN age >=600 and gender = "F" THEN "50 plus years"
END as age_group,gender as "gender",
	COUNT(IF(((state = 'patient defaulted' and start_date BETWEEN @startDate AND @endDate )
    or (state = 'On antiretrovirals' and floor(datediff(@endDate,last_appt_date)) >=  @defaultOneMonth))
    and patient_id in (select patient_id from mw_art_initial where (datediff( date_add(last_appt_date, interval @defaultOneMonth DAY) , visit_date)) <=  90) , 1, NULL))
    as ITT_less_3months,

	COUNT(IF(((state = 'patient defaulted' and start_date BETWEEN @startDate AND @endDate )
    or (state = 'On antiretrovirals' and floor(datediff(@endDate,last_appt_date)) >=  @defaultOneMonth))
    and patient_id in (select patient_id from mw_art_initial where ((datediff( date_add(last_appt_date, interval @defaultOneMonth DAY) , visit_date)) >=  90 and
    (datediff( date_add(last_appt_date, interval @defaultOneMonth DAY), visit_date)) <=179)) , 1, NULL))
    as ITT_less_3to5_months,

    COUNT(IF(((state = 'patient defaulted' and start_date BETWEEN @startDate AND @endDate )
    or (state = 'On antiretrovirals' and floor(datediff(@endDate,last_appt_date)) >=  @defaultOneMonth))
    and patient_id in (select patient_id from mw_art_initial where (datediff( date_add(last_appt_date, interval @defaultOneMonth DAY) , visit_date)) >=  180) , 1, NULL))
    as ITT_6months_plus_months,

    COUNT(IF((state = 'Patient Died' and start_date BETWEEN @startDate AND @endDate), 1, NULL)) as patient_died,
    COUNT(IF((state = 'Treatment Stopped' and start_date BETWEEN @startDate AND @endDate), 1, NULL)) as treatment_stopped,
     COUNT(IF((state = 'Patient transferred out' and start_date BETWEEN @startDate AND @endDate), 1, NULL)) as patient_transferred_out
from
(
select distinct(mwp.patient_id), opi.identifier, mwp.first_name, mwp.last_name, ops.program, ops.state,ops.start_date,program_state_id,  mwp.gender,
 If(ops.state = "On antiretrovirals",floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider),floor(datediff(ops.start_date,mwp.birthdate)/@birthDateDivider)) as age,
 ops.location, patient_visit.last_appt_date, patient_visit.art_regimen as current_regimen, patient_visit.hiv_preventive_therapy, patient_visit.therapy_pills,
 patient_visit.pregnant_or_lactating
from  mw_patient mwp
LEFT join (
	select map.patient_id, map.visit_date, map.next_appointment_date as last_appt_date, map.art_regimen, map.hiv_preventive_therapy,map.therapy_pills,
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
	select mar.patient_id, mar.visit_date as initial_visit_date
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