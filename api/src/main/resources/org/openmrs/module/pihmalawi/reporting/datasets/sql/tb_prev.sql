/***************************************************************************

PEPFAR TB PREV REPORT
Use OpenMRS data warehouse tables and stored procedures

Proportion of ART patients who started on a standard course of TB Preventive
 Treatment (TPT) in the previous reporting period who completed therapy.
***************************************************************************/

SET @defaultOneMonth = 30;
SET @birthDateDivider = 30;

SET @dayOfEndDate = DAY(@endDate);
SET @startDate =
  (SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 2 DAY)
      ELSE @endDate
    END);
SET @sixMonthsStartDate= (SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);
SET @sevenMonthsStartDate=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);
SET @thirteenMonthsStartDate=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);


call create_age_groups();
call create_last_art_outcome_at_facility(@endDate,@location);

select sort_value,x.age_group, CASE WHEN x.gender="F" then 'Female' else 'Male' end as gender,
CASE WHEN new_start_three_hp is null then 0 else new_start_three_hp end as new_start_three_hp,
CASE WHEN new_start_six_h is null then 0 else new_start_six_h end as new_start_six_h,
CASE WHEN old_start_three_hp is null then 0 else old_start_three_hp end as previous_start_three_hp,
CASE WHEN old_start_six_h is null then 0 else old_start_six_h end as previous_start_six_h,
CASE WHEN completed_new_start_three_hp is null then 0 else completed_new_start_three_hp end as completed_new_start_three_hp,
CASE WHEN completed_new_start_six_h is null then 0 else completed_new_start_six_h end as completed_new_start_six_h,
CASE WHEN completed_old_three_hp is null then 0 else completed_old_three_hp end as completed_old_three_hp,
CASE WHEN completed_old_six_h is null then 0 else completed_old_six_h end as completed_old_six_h
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
COUNT(IF(((initial_visit_date >= @sixMonthsStartDate and transfer_in_date is null) or start_date >= @sixMonthsStartDate)
and ((first_inh_300 is not null and first_rfp_150 is not null) or first_rfp_inh is not null),1,NULL)) as new_start_three_hp,
COUNT(IF(((initial_visit_date >= @sixMonthsStartDate and transfer_in_date is null) or start_date >= @sixMonthsStartDate)
and (first_inh_300 is not null and first_rfp_150 is null),1,NULL)) as new_start_six_h,
COUNT(IF(initial_visit_date < @sixMonthsStartDate
and ((first_inh_300 is not null and first_rfp_150 is not null) or first_rfp_inh is not null),1,NULL)) as old_start_three_hp,
COUNT(IF(initial_visit_date < @sixMonthsStartDate
and (first_inh_300 is not null and first_rfp_150 is null),1,NULL)) as old_start_six_h,
COUNT(IF(((initial_visit_date >= @sixMonthsStartDate and transfer_in_date is null) or start_date >= @sixMonthsStartDate)
and
( coalesce(total_6_months_rfp_150_pills,0) + coalesce(total_6_months_rfp_inh_pills,0) ) >= 33,1,NULL )) as completed_new_start_three_hp,
COUNT(IF(((initial_visit_date >= @sixMonthsStartDate and transfer_in_date is null) or start_date >= @sixMonthsStartDate)
and total_1_yr_inh_300_pills >= 144, 1, null)) as completed_new_start_six_h,
COUNT(IF(initial_visit_date < @sixMonthsStartDate
and ( coalesce(total_6_months_rfp_150_pills,0)  + coalesce(total_6_months_rfp_inh_pills,0) ) >= 33,1,NULL )) as completed_old_three_hp,
COUNT(IF(initial_visit_date < @sixMonthsStartDate
and total_1_yr_inh_300_pills >= 144, 1, null)) as completed_old_six_h
from
(
select *
from (
select distinct(mwp.patient_id) as patient_id, opi.identifier,ops.state,ops.start_date, mwp.gender,
 If(ops.state = "On antiretrovirals",floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider),floor(datediff(ops.start_date,mwp.birthdate)/@birthDateDivider)) as age,
 ops.location, patient_visit.last_appt_date,patient_visit.followup_visit_date, patient_visit.art_regimen as current_regimen,
 patient_visit.pregnant_or_lactating, patient_initial_visit.initial_pregnant_or_lactating, patient_initial_visit.initial_visit_date,
 patient_initial_visit.transfer_in_date,first_ipt_date,first_inh_300,first_inh_300_pills,first_rfp_150,first_rfp_150_pills,first_rfp_inh,first_rfp_inh_pills,last_inh_300,last_inh_300_pills,last_rfp_150,
 last_rfp_150_pills,last_rfp_inh,last_rfp_inh_pills,last_ipt_date,previous_inh_300,previous_inh_300_pills,previous_rfp_150,
 previous_rfp_150_pills,previous_rfp_inh,previous_rfp_inh_pills,previous_ipt_date,
 total_1_yr_inh_300_pills, total_1_yr_rfp_150_pills,total_1_yr_rfp_inh_pills, total_6_months_inh_300_pills,
 total_6_months_rfp_150_pills, total_6_months_rfp_inh_pills
from  mw_patient mwp
LEFT join (
	select map.patient_id, map.visit_date as followup_visit_date, map.next_appointment_date as last_appt_date, map.art_regimen,
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
	select map.patient_id, map.visit_date as last_ipt_date, map.inh_300 as last_inh_300,
    map.inh_300_pills as last_inh_300_pills,map.rfp_150 as last_rfp_150,map.rfp_150_pills as last_rfp_150_pills,
    map.rfp_inh as last_rfp_inh,map.rfp_inh_pills as last_rfp_inh_pills
    from mw_art_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_art_followup where visit_date between @sixMonthsStartDate and @endDate and
    ((inh_300 is not null or inh_300_pills is not null) or (rfp_150 is not null or rfp_150_pills is not null) or (rfp_inh is not null or rfp_inh_pills is not null))
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date) last_ipt_visit
            on last_ipt_visit.patient_id = mwp.patient_id


LEFT join (
	select map.patient_id, map.visit_date as previous_ipt_date, map.inh_300 as previous_inh_300,
    map.inh_300_pills as previous_inh_300_pills,map.rfp_150 as previous_rfp_150,map.rfp_150_pills as previous_rfp_150_pills,
    map.rfp_inh as previous_rfp_inh,map.rfp_inh_pills as previous_rfp_inh_pills
    from mw_art_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date
    from mw_art_followup where visit_date <= DATE_SUB(@sixMonthsStartDate, INTERVAL 1 DAY) and
    ((inh_300 is not null or inh_300_pills is not null) or (rfp_150 is not null or rfp_150_pills is not null) or (rfp_inh is not null or rfp_inh_pills is not null))
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date) before_start_date_ipt_visit
            on before_start_date_ipt_visit.patient_id = mwp.patient_id



LEFT join (
	select map.patient_id, map.visit_date as first_ipt_date, map.next_appointment_date as last_appt_date, map.art_regimen,
    map.pregnant_or_lactating, map.inh_300 as first_inh_300,map.inh_300_pills as first_inh_300_pills,
    map.rfp_150 as first_rfp_150,map.rfp_150_pills as first_rfp_150_pills, map.rfp_inh as first_rfp_inh,map.rfp_inh_pills as first_rfp_inh_pills
    from mw_art_followup map
join
(
	select patient_id,MIN(visit_date) as visit_date
    from mw_art_followup where visit_date between @sixMonthsStartDate and @endDate and
    ((inh_300 is not null or inh_300_pills is not null) or (rfp_150 is not null or rfp_150_pills is not null) or (rfp_inh is not null or rfp_inh_pills is not null))
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date) min_patient_visit
            on min_patient_visit.patient_id = mwp.patient_id

left join
(
	select patient_id,SUM(inh_300_pills) as total_1_yr_inh_300_pills, sum(rfp_150_pills) as total_1_yr_rfp_150_pills,
    SUM(rfp_inh_pills) as total_1_yr_rfp_inh_pills
    from mw_art_followup where visit_date between @thirteenMonthsStartDate and @endDate
    group by patient_id
	) map2
ON map2.patient_id = mwp.patient_id
left join
(
	select patient_id,SUM(inh_300_pills) as total_6_months_inh_300_pills, sum(rfp_150_pills) as total_6_months_rfp_150_pills,
    SUM(rfp_inh_pills) as total_6_months_rfp_inh_pills
    from mw_art_followup where visit_date between @sevenMonthsStartDate and @endDate
    group by patient_id
	) map3
ON map3.patient_id = mwp.patient_id
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

) x where first_ipt_date >= @sixMonthsStartDate
) sub1
 group by age_group,gender, location
 order by gender,age_group,location, state
 ) cohort on x.age_group = cohort.age_group
 and x.gender = cohort.gender
 order by sort_value;