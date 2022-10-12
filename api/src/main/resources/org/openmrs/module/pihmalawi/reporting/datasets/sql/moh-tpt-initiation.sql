/************************************************************************

MOH TPT NEW INITIATION REPORT
Use OpenMRS data warehouse tables and stored procedures

Query aggregates patients starting TB preventive therapy,
3HP (3 months of Isoniazid and Rifapentine)
and 6H(6 months of daily isoniazid for TB preventive therapy).
*************************************************************************/

/* 30 for age in months*/
SET @birthDateDivider = 30;
SET @endDate= date_sub(date_add(@startDate, interval 3 month),interval 1 day);
SET @defaultOneMonth = 30;
SET @district="Neno";

call create_age_groups();
call create_hiv_cohort(@startDate,@endDate,@location,@birthDateDivider);

select sort_value,@district as district,x.age_group, x.gender,
CASE WHEN new_start_three_hp is null then 0 else new_start_three_hp end as new_start_three_hp,
CASE WHEN new_start_six_h is null then 0 else new_start_six_h end as new_start_six_h,
CASE WHEN old_start_three_hp is null then 0 else old_start_three_hp end as previous_start_three_hp,
CASE WHEN old_start_six_h is null then 0 else old_start_six_h end as previous_start_six_h
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
COUNT(IF(((initial_visit_date >= @startDate and transfer_in_date is null) or start_date >= @startDate)
and (first_inh_300 is not null and first_rfp_150 is not null),1,NULL)) as new_start_three_hp,
COUNT(IF(((initial_visit_date >= @startDate and transfer_in_date is null) or start_date >= @startDate)
and (first_inh_300 is not null and first_rfp_150 is null),1,NULL)) as new_start_six_h,
COUNT(IF(initial_visit_date < @startDate and previous_ipt_date < date_sub(@startDate, interval 1 month)
and (first_inh_300 is not null and first_rfp_150 is not null),1,NULL)) as old_start_three_hp,
COUNT(IF(initial_visit_date < @startDate and previous_ipt_date < date_sub(@startDate, interval 2 month)
and (first_inh_300 is not null and first_rfp_150 is null),1,NULL)) as old_start_six_h
from
(
	select * from hiv_cohort where
	(first_inh_300 is not null or first_rfp_150 is not null)
and first_ipt_date >= @startDate
)sub1
 group by age_group,gender, location
 order by gender,age_group,location, state
 ) cohort on x.age_group = cohort.age_group
 and x.gender = cohort.gender
 order by sort_value;