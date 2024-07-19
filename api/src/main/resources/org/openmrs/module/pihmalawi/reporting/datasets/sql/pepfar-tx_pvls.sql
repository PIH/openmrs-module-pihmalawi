/*********************************************************************

PEPFAR TX_PVLS REPORT
Percentage of ART patients with a suppressed viral load (VL) result (<1000 copies/ml) documented in
the medical or laboratory records/laboratory information systems (LIS) within the past 12 months
**********************************************************************/

SET @defaultCutOff = 30;
set @birthDateDivider=30;

set @dayOfEndDate = DAY(@endDate);

set @startOfTheYear=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);

call create_age_groups();
call create_last_art_outcome_at_facility(@endDate,@location);

Select sort_value,x.age_group, CASE WHEN x.gender = "F" THEN "Female" ELSE "Male" END as gender,
CASE WHEN active is null then 0 else active end as tx_curr,
CASE WHEN due_for_vl is null then 0 else due_for_vl end as due_for_vl,
CASE WHEN routine_samples_drawn is null then 0 else routine_samples_drawn end as routine_samples_drawn,
CASE WHEN target_samples_drawn is null then 0 else target_samples_drawn end as target_samples_drawn,
CASE WHEN routine_low_vl_less_than_1000_copies is null then 0 else routine_low_vl_less_than_1000_copies end as routine_low_vl_less_than_1000_copies,
CASE WHEN routine_high_vl_more_than_1000_copies is null then 0 else routine_high_vl_more_than_1000_copies end as routine_high_vl_more_than_1000_copies,
CASE WHEN targeted_low_vl_less_than_1000_copies is null then 0 else targeted_low_vl_less_than_1000_copies end as targeted_low_vl_less_than_1000_copies,
CASE WHEN targeted_high_vl_more_than_1000_copies is null then 0 else targeted_high_vl_more_than_1000_copies end as targeted_high_vl_more_than_1000_copies


From
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
END as age_group,gender as "gender",
COUNT(IF((state = 'On antiretrovirals'), 1, NULL)) as active,
COUNT(IF((due_over_1_year='yes'), 1, NULL)) as due_for_vl,
COUNT(IF((reason_for_test='Routine'), 1, NULL)) as routine_samples_drawn,
COUNT(IF((reason_for_test='Target'), 1, NULL)) as target_samples_drawn,
COUNT(IF((routine_low_vl_less_than_1000_copies='routine low'), 1, NULL)) as routine_low_vl_less_than_1000_copies,
COUNT(IF((routine_high_vl_more_than_1000_copies='routine high'), 1, NULL)) as routine_high_vl_more_than_1000_copies,
COUNT(IF((targeted_low_vl_less_than_1000_copies='targeted low'), 1, NULL)) as targeted_low_vl_less_than_1000_copies,
COUNT(IF((targeted_high_vl_more_than_1000_copies='targeted low'), 1, NULL)) as targeted_high_vl_more_than_1000_copies

from(

SELECT opi.identifier, vl.location, mwp.gender,
 floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider) as age, last_visit_date, next_appointment_date,
 state,
reason_for_test, test_date,
CASE
WHEN ldl IS NOT NULL THEN 'LDL'
WHEN other_results IS NOT NULL THEN other_results
WHEN viral_load_result IS NOT NULL THEN viral_load_result
WHEN less_than_limit IS NOT NULL THEN less_than_limit
END AS Result,
CASE WHEN (DATEDIFF(next_appointment_date,test_date)>365) THEN "yes"
ELSE "no" END AS due_over_1_year,
 IF(
    (less_than_limit < 1000 OR viral_load_result < 1000 OR ldl = 'True') AND reason_for_test = 'routine', 'routine low', NULL
) AS routine_low_vl_less_than_1000_copies,
IF(
  (less_than_limit>1000 or viral_load_result>1000) and reason_for_test='routine', 'routine high', null
) as routine_high_vl_more_than_1000_copies,

 IF(
    (less_than_limit < 1000 OR viral_load_result < 1000 OR ldl = 'True') AND reason_for_test = 'target', 'targeted low', NULL
) AS targeted_low_vl_less_than_1000_copies,
IF(
  (less_than_limit>1000 or viral_load_result>1000) and reason_for_test='target', 'target high', null
) as targeted_high_vl_more_than_1000_copies

FROM
(
select avl1.patient_id, location, reason_for_test, visit_date as test_date, lab_location, viral_load_result,less_than_limit, ldl,other_results
from mw_art_viral_load avl1
join
(
	select patient_id,MAX(visit_date) as test_date
    from mw_art_viral_load where visit_date <= @startOfTheYear
	group by patient_id
	) avl2
ON avl1.patient_id = avl2.patient_id and avl1.visit_date = avl2.test_date)vl

LEFT JOIN (
SELECT maf.patient_id, maf.visit_date AS last_visit_date, next_appointment_date
FROM mw_art_followup maf
JOIN (
SELECT patient_id, MAX(visit_date) AS visit_date
FROM mw_art_followup WHERE visit_date <= @startOfTheYear
GROUP BY patient_id
) map1 ON maf.patient_id = map1.patient_id AND maf.visit_date = map1.visit_date
) mmaf ON mmaf.patient_id = vl.patient_id

join mw_patient mwp
on mwp.patient_id = vl.patient_id

join omrs_patient_identifier opi
on opi.patient_id=mmaf.patient_id and opi.type='arv number'
left join last_facility_outcome lfo
on lfo.pat=mmaf.patient_id
WHERE  next_appointment_date BETWEEN @startOfTheYear AND @endDate
and DATEDIFF(next_appointment_date,vl.test_date)>365  and vl.location=@location
and reason_for_test in ('Routine', 'Target')
) sub
group by age_group,gender
 ) cohort on x.age_group = cohort.age_group
 and x.gender = cohort.gender
 order by sort_value;