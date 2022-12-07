/*
Number of ART patients who experienced who had no clinical contact or ARV drug pickup
for greater than 30 days since the last expected contact during any
previous reporting period and have successfully restarted ARVs within the reporting period
and remained on treatment until the end of the reporting period.

Report requires the following parameters:
@location
@startDate
@endDate
*/

/* 30 for age in months*/
SET @birthDateDivider = 30;
SET @defaultCutOff = 30;

call create_age_groups();
call create_last_art_outcome_at_facility(@endDate,@location);

SELECT
    sort_value,
    x.age_group,
	CASE WHEN x.gender = "F" THEN "Female" ELSE "Male" END as gender,
    CASE
        WHEN returned_less_than_three_months IS NULL THEN 0
        ELSE returned_less_than_three_months
    END AS returned_less_than_three_months,
    CASE
        WHEN returned_three_to_five_months IS NULL THEN 0
        ELSE returned_three_to_five_months
    END AS returned_three_to_five_months,
    CASE
        WHEN returned_six_months_plus IS NULL THEN 0
        ELSE returned_six_months_plus
    END AS returned_six_months_plus
FROM
    age_groups AS x
        LEFT OUTER JOIN
    (SELECT
        CASE
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
END as age_group,gender AS 'gender',COUNT(IF((months_out_of_care < 3), 1, NULL)) as returned_less_than_three_months,
COUNT(IF((months_out_of_care >= 3 and months_out_of_care <= 5), 1, NULL)) as returned_three_to_five_months,
COUNT(IF((months_out_of_care >= 6), 1, NULL)) as returned_six_months_plus
from
(
select opi.identifier, gender,floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider) as age,
last_appt_date,default_date, first_visit_date, TIMESTAMPDIFF(MONTH, default_date, first_visit_date) as months_out_of_care
from  mw_patient mwp
LEFT join (
	select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as last_appt_date , DATE_ADD(MAX(next_appointment_date), INTERVAL 28 day) as default_date
    from mw_art_visits where visit_date < @startDate
group by patient_id
            ) patient_visit
            on patient_visit.patient_id = mwp.patient_id
join omrs_patient_identifier opi
on mwp.patient_id = opi.patient_id

JOIN
        (SELECT
index_desc,
            opi.patient_id as pat,
            opi.identifier,
            index_descending.state,
            index_descending.location,
            index_descending.program,
start_date,
            program_state_id,
            end_date
FROM (SELECT
            @r:= IF(@u = patient_id, @r + 1,1) index_desc,
            location,
            state,
            program,
            start_date,
            end_date,
            patient_id,
            program_state_id,
            @u:= patient_id
      FROM omrs_program_state,
                    (SELECT @r:= 1) AS r,
                    (SELECT @u:= 0) AS u
                    where program = "HIV program"
                    and start_date < @startDate
                    and location =  @location
            ORDER BY patient_id DESC, start_date DESC, program_state_id DESC
            ) index_descending
            join omrs_patient_identifier opi on index_descending.patient_id = opi.patient_id
            and opi.location = index_descending.location
            and opi.type = "ARV Number"
            where index_desc = 1)
            ops
            on opi.patient_id = ops.pat and opi.location = ops.location
            left join (
            select patient_visit.patient_id, next_appointment_date, first_visit_date
            from last_facility_outcome as ops
            LEFT join (
	select map.patient_id, map.visit_date as followup_visit_date, map.next_appointment_date as next_appointment_date
    from mw_art_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as next_appointment_date from mw_art_followup where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date) patient_visit
            on patient_visit.patient_id = ops.pat
            LEFT join (
	select map.patient_id, map.visit_date as first_visit_date
    from mw_art_followup map
join
(
	select patient_id,MIN(visit_date) as first_visit_date
    from mw_art_followup where visit_date between @startDate and @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.first_visit_date) patient_first_visit
            on patient_first_visit.patient_id = ops.pat
            where
            state = "On antiretrovirals" and floor(datediff(@endDate,next_appointment_date)) <  @defaultCutOff
            and start_date >= @startDate and start_date <= @endDate
            ) visits on visits.patient_id = mwp.patient_id
            where opi.type = "ARV Number"
            and ((state = "On antiretrovirals" and floor(datediff(DATE_SUB(@startDate, INTERVAL 1 day),last_appt_date)) >  @defaultCutOff)
            or state IN ("patient defaulted","treatment stopped"))
            and first_visit_date is not null
			) sub
    GROUP BY age_group , gender
    ORDER BY gender , age_group) cohort ON x.age_group = cohort.age_group
        AND x.gender = cohort.gender
ORDER BY sort_value;