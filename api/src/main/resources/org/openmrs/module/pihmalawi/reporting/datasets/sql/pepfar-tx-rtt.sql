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
    CASE
        WHEN x.gender = 'F' THEN 'Female'
        ELSE 'Male'
    END AS gender,
    CASE
        WHEN cd4_less_than_two_hundred IS NULL THEN 0
        ELSE cd4_less_than_two_hundred
    END AS cd4_less_than_two_hundred,
    CASE
        WHEN cd4_equal_to_or_greater_than_two_hundred IS NULL THEN 0
        ELSE cd4_equal_to_or_greater_than_two_hundred
    END AS cd4_equal_to_or_greater_than_two_hundred,
    CASE
        WHEN cd4_unknown_or_not_done IS NULL THEN 0
        ELSE cd4_unknown_or_not_done
    END AS cd4_unknown_or_not_done,
    '0' AS not_eligible_for_cd4,
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
                WHEN age <= 11 AND gender = 'M' THEN '< 1 year'
                WHEN age <= 11 AND gender = 'F' THEN '< 1 year'
                WHEN age >= 12 AND age <= 59 AND gender = 'M' THEN '1-4 years'
                WHEN age >= 12 AND age <= 59 AND gender = 'F' THEN '1-4 years'
                WHEN
                    age >= 60 AND age <= 119
                        AND gender = 'M'
                THEN
                    '5-9 years'
                WHEN
                    age >= 60 AND age <= 119
                        AND gender = 'F'
                THEN
                    '5-9 years'
                WHEN
                    age >= 120 AND age <= 179
                        AND gender = 'M'
                THEN
                    '10-14 years'
                WHEN
                    age >= 120 AND age <= 179
                        AND gender = 'F'
                THEN
                    '10-14 years'
                WHEN
                    age >= 180 AND age <= 239
                        AND gender = 'M'
                THEN
                    '15-19 years'
                WHEN
                    age >= 180 AND age <= 239
                        AND gender = 'F'
                THEN
                    '15-19 years'
                WHEN
                    age >= 240 AND age <= 299
                        AND gender = 'M'
                THEN
                    '20-24 years'
                WHEN
                    age >= 240 AND age <= 299
                        AND gender = 'F'
                THEN
                    '20-24 years'
                WHEN
                    age >= 300 AND age <= 359
                        AND gender = 'M'
                THEN
                    '25-29 years'
                WHEN
                    age >= 300 AND age <= 359
                        AND gender = 'F'
                THEN
                    '25-29 years'
                WHEN
                    age >= 360 AND age <= 419
                        AND gender = 'M'
                THEN
                    '30-34 years'
                WHEN
                    age >= 360 AND age <= 419
                        AND gender = 'F'
                THEN
                    '30-34 years'
                WHEN
                    age >= 420 AND age <= 479
                        AND gender = 'M'
                THEN
                    '35-39 years'
                WHEN
                    age >= 420 AND age <= 479
                        AND gender = 'F'
                THEN
                    '35-39 years'
                WHEN
                    age >= 480 AND age <= 539
                        AND gender = 'M'
                THEN
                    '40-44 years'
                WHEN
                    age >= 480 AND age <= 539
                        AND gender = 'F'
                THEN
                    '40-44 years'
                WHEN
                    age >= 540 AND age <= 599
                        AND gender = 'M'
                THEN
                    '45-49 years'
                WHEN
                    age >= 540 AND age <= 599
                        AND gender = 'F'
                THEN
                    '45-49 years'
                WHEN
                    age >= 600 AND age <= 659
                        AND gender = 'M'
                THEN
                    '50-54 years'
                WHEN
                    age >= 600 AND age <= 659
                        AND gender = 'F'
                THEN
                    '50-54 years'
                WHEN
                    age >= 660 AND age <= 719
                        AND gender = 'M'
                THEN
                    '55-59 years'
                WHEN
                    age >= 660 AND age <= 719
                        AND gender = 'F'
                THEN
                    '55-59 years'
                WHEN
                    age >= 720 AND age <= 779
                        AND gender = 'M'
                THEN
                    '60-64 years'
                WHEN
                    age >= 720 AND age <= 779
                        AND gender = 'F'
                THEN
                    '60-64 years'
                WHEN
                    age >= 780 AND age <= 839
                        AND gender = 'M'
                THEN
                    '65-69 years'
                WHEN
                    age >= 780 AND age <= 839
                        AND gender = 'F'
                THEN
                    '65-69 years'
                WHEN
                    age >= 840 AND age <= 899
                        AND gender = 'M'
                THEN
                    '70-74 years'
                WHEN
                    age >= 840 AND age <= 899
                        AND gender = 'F'
                THEN
                    '70-74 years'
                WHEN
                    age >= 900 AND age <= 959
                        AND gender = 'M'
                THEN
                    '75-79 years'
                WHEN
                    age >= 900 AND age <= 959
                        AND gender = 'F'
                THEN
                    '75-79 years'
                WHEN
                    age >= 960 AND age <= 1019
                        AND gender = 'M'
                THEN
                    '80-84 years'
                WHEN
                    age >= 960 AND age <= 1019
                        AND gender = 'F'
                THEN
                    '80-84 years'
                WHEN
                    age >= 1020 AND age <= 1079
                        AND gender = 'M'
                THEN
                    '85-89 years'
                WHEN
                    age >= 1020 AND age <= 1079
                        AND gender = 'F'
                THEN
                    '85-89 years'
                WHEN age >= 1080 AND gender = 'M' THEN '90 plus years'
                WHEN age >= 1080 AND gender = 'F' THEN '90 plus years'
            END AS age_group,
            gender AS 'gender',
            COUNT(IF((months_out_of_care < 3), 1, NULL)) AS returned_less_than_three_months,
            COUNT(IF((months_out_of_care >= 3
                AND months_out_of_care <= 5), 1, NULL)) AS returned_three_to_five_months,
            COUNT(IF((months_out_of_care >= 6), 1, NULL)) AS returned_six_months_plus,
            COUNT(IF(((months_out_of_care < 3)
                OR (months_out_of_care BETWEEN 3 AND 5)
                OR (months_out_of_care >= 6))
                AND cd4_count_at_restart IS NOT NULL
                AND cd4_count_at_restart < 200, 1, NULL)) AS cd4_less_than_two_hundred,
            COUNT(IF(months_out_of_care BETWEEN 3 AND 5
                AND cd4_count_at_restart IS NOT NULL
                AND cd4_count_at_restart >= 200, 1, NULL)) AS cd4_equal_to_or_greater_than_two_hundred,
            COUNT(IF(((months_out_of_care < 3)
                OR (months_out_of_care BETWEEN 3 AND 5)
                OR (months_out_of_care >= 6))
                AND cd4_count_at_restart IS NULL, 1, NULL)) AS cd4_unknown_or_not_done
    FROM
        (SELECT
        opi.identifier,
            gender,
            FLOOR(DATEDIFF(@endDate, mwp.birthdate) / @birthDateDivider) AS age,
            last_appt_date,
            default_date,
            first_visit_date,
            TIMESTAMPDIFF(MONTH, default_date, first_visit_date) AS months_out_of_care,
            visits.cd4_count AS cd4_count_at_restart
    FROM
        mw_patient mwp
    LEFT JOIN (SELECT
        patient_id,
            MAX(visit_date) AS visit_date,
            MAX(next_appointment_date) AS last_appt_date,
            DATE_ADD(MAX(next_appointment_date), INTERVAL 28 DAY) AS default_date
    FROM
        mw_art_visits
    WHERE
        visit_date < @startDate
    GROUP BY patient_id) patient_visit ON patient_visit.patient_id = mwp.patient_id
    JOIN omrs_patient_identifier opi ON mwp.patient_id = opi.patient_id
    JOIN (SELECT
        index_desc,
            opi.patient_id AS pat,
            opi.identifier,
            index_descending.state,
            index_descending.location,
            index_descending.program,
            start_date,
            program_state_id,
            end_date
    FROM
        (SELECT
        @r:=IF(@u = patient_id, @r + 1, 1) index_desc,
            location,
            state,
            program,
            start_date,
            end_date,
            patient_id,
            program_state_id,
            @u:=patient_id
    FROM
        omrs_program_state, (SELECT @r:=1) AS r, (SELECT @u:=0) AS u
    WHERE
        program = 'HIV program'
            AND start_date < @startDate
            AND location = @location
    ORDER BY patient_id DESC , start_date DESC , program_state_id DESC) index_descending
    JOIN omrs_patient_identifier opi ON index_descending.patient_id = opi.patient_id
        AND opi.location = index_descending.location
        AND opi.type = 'ARV Number'
    WHERE
        index_desc = 1) ops ON opi.patient_id = ops.pat
        AND opi.location = ops.location
    LEFT JOIN (SELECT
        patient_visit.patient_id,
            next_appointment_date,
            first_visit_date,
            cd4_at_restart.cd4_count
    FROM
        last_facility_outcome AS ops
    LEFT JOIN (SELECT
        map.patient_id,
            map.visit_date AS followup_visit_date,
            map.next_appointment_date AS next_appointment_date
    FROM
        mw_art_followup map
    JOIN (SELECT
        patient_id,
            MAX(visit_date) AS visit_date,
            MAX(next_appointment_date) AS next_appointment_date
    FROM
        mw_art_followup
    WHERE
        visit_date <= @endDate
    GROUP BY patient_id) map1 ON map.patient_id = map1.patient_id
        AND map.visit_date = map1.visit_date) patient_visit ON patient_visit.patient_id = ops.pat
    LEFT JOIN (SELECT
        map.patient_id, map.visit_date AS first_visit_date
    FROM
        mw_art_followup map
    JOIN (SELECT
        patient_id, MIN(visit_date) AS first_visit_date
    FROM
        mw_art_followup
    WHERE
        visit_date BETWEEN @startDate AND @endDate
    GROUP BY patient_id) map1 ON map.patient_id = map1.patient_id
        AND map.visit_date = map1.first_visit_date) patient_first_visit ON patient_first_visit.patient_id = ops.pat
    LEFT JOIN (SELECT
        t.patient_id, t.visit_date, t.cd4_count
    FROM
        mw_art_followup_testing t) cd4_at_restart ON cd4_at_restart.patient_id = patient_first_visit.patient_id
        AND cd4_at_restart.visit_date = patient_first_visit.first_visit_date
    WHERE
        state = 'On antiretrovirals'
            AND FLOOR(DATEDIFF(@endDate, next_appointment_date)) < @defaultCutOff
            AND start_date >= @startDate
            AND start_date <= @endDate) visits ON visits.patient_id = mwp.patient_id
    WHERE
        opi.type = 'ARV Number'
            AND ((state = 'On antiretrovirals'
            AND FLOOR(DATEDIFF(DATE_SUB(@startDate, INTERVAL 1 DAY), last_appt_date)) > @defaultCutOff)
            OR state IN ('patient defaulted' , 'treatment stopped'))
            AND first_visit_date IS NOT NULL) sub
    GROUP BY age_group , gender
    ORDER BY gender , age_group) cohort ON x.age_group = cohort.age_group
        AND x.gender = cohort.gender
ORDER BY sort_value;