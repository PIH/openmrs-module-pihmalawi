SET @defaultCutOff = 60;
call create_age_groups();
call create_last_art_outcome_at_facility(@endDate,@location);

select sort_value, "Neno" as district, x.age_group, x.gender,
CASE WHEN 3hp_new is null then 0 else 3hp_new end as "3hp_new_on_art",
CASE WHEN 6h_new is null then 0 else 6h_new end as "6h_new_on_art",
CASE WHEN 3hp_prev is null then 0 else 3hp_prev end as "3hp_previous_on_art",
CASE WHEN 6h_prev is null then 0 else 6h_prev end as "6h_previous_on_art"

 from
age_groups as x
LEFT OUTER JOIN
(
SELECT CASE
WHEN age <= 11 and gender = "M" THEN "<1 year"
WHEN age <= 11 and gender = "F" THEN "<1 year"
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
WHEN age >=540 and age <= 599 and gender = "F" THEN "45-49 years"
WHEN age >=600 and age <=659 and gender = "M" THEN "50-54 years"
WHEN age >=600 and age <=659 and gender = "F" THEN "50-54 years"
WHEN age >=660 and age <=719 and gender = "M" THEN "55-59 years"
WHEN age >=660 and age <=719 and gender = "F" THEN "55-59 years"
WHEN age >=720 and age <=779 and gender = "M" THEN "60-64 years"
WHEN age >=720 and age <=779 and gender = "F" THEN "60-64 years"
WHEN age >=780 and age <=839 and gender = "M" THEN "65-69 years"
WHEN age >=780 and age <=839 and gender = "F" THEN "65-69 years"
WHEN age >=840 and age <=899 and gender = "M" THEN "70-74 years"
WHEN age >=840 and age <=899 and gender = "F" THEN "70-74 years"
WHEN age >=900 and age <=959 and gender = "M" THEN "75-79 years"
WHEN age >=900 and age <=959 and gender = "F" THEN "75-79 years"
WHEN age >=960 and age <=1019 and gender = "M" THEN "80-84 years"
WHEN age >=960 and age <=1019 and gender = "F" THEN "80-84 years"
WHEN age >=1020 and gender = "M" THEN "90 plus years"
WHEN age >=1020 and gender = "F" THEN "90 plus years"
END as age_group,gender as "gender",

       COUNT(IF(initial_visit_date BETWEEN @startDate AND @endDate and transfer_in_date is null
		and patient_id IN(select patient_id from mw_art_followup where inh_300 is not null and location = @location), 1, NULL))
        as 3hp_new,

    COUNT(IF(initial_visit_date BETWEEN @startDate AND @endDate and transfer_in_date is null
	and patient_id NOT IN (select patient_id from mw_art_followup where rfp_150 is NOT NULL and location != @location)
    and patient_id IN(select patient_id from mw_art_followup where inh_300 is null and location = @location), 1, NULL)
    ) as 6h_new,
    COUNT(IF(initial_visit_date < @startDate and state = 'On antiretrovirals'  and floor(datediff(@endDate,last_appt_date)) <=  @defaultOneMonth
    and (( inh_300 is not null) and ( rfp_150 is not null)), 1, NULL)) as 3hp_prev,

    COUNT(IF(initial_visit_date < @startDate and state = 'On antiretrovirals'  and floor(datediff(@endDate,last_appt_date)) <=  @defaultOneMonth
    and (( inh_300 is not null) and ( rfp_150 is null)), 1, NULL)) as 6h_prev

from
(
select distinct(mwp.patient_id), opi.identifier, mwp.first_name, mwp.last_name, ops.program, ops.state,ops.start_date,program_state_id,
mwp.gender,
 If(ops.state = "On antiretrovirals",floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider),
 floor(datediff(ops.start_date,mwp.birthdate)/@birthDateDivider)) as age,
 ops.location, patient_visit.last_appt_date, patient_visit.art_regimen as current_regimen, patient_initial_visit.initial_visit_date,
 patient_initial_visit.transfer_in_date, inh_300, inh_300_pills, rfp_150, rfp_150_pills , pyridoxine, ctx_960
from  mw_patient mwp
LEFT join (
	select map.patient_id, map.visit_date as followup_visit_date, map.next_appointment_date as last_appt_date, map.art_regimen,
    map.inh_300, map.inh_300_pills, map.rfp_150, map.rfp_150_pills, pyridoxine, ctx_960
    from mw_art_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as last_appt_date from mw_art_followup
    where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date) patient_visit
            on patient_visit.patient_id = mwp.patient_id
LEFT join (
	select mar.patient_id, mar.visit_date as initial_visit_date,
    mar.transfer_in_date
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
