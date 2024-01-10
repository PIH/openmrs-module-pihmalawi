/* 30 for age in months*/
SET @birthDateDivider = 365;

call create_last_teen_club_outcome_at_facility(@endDate,@location);
call create_last_art_outcome_at_facility(@endDate, @location);

SELECT
    CONVERT( SUBSTRING_INDEX(opi.identifier, ' ', - 1) , SIGNED) AS sort_value,
    opi.identifier,
    l.source_key AS location_key,
    mwp.first_name,
    mwp.last_name,
    ops.state AS teen_club_state,
    lfo.state AS hiv_state,
    ops.start_date AS date_joined_teen_club,
    mwp.gender AS gender,
    FLOOR((DATEDIFF(@endDate, mwp.birthdate) / @birthDateDivider)) AS age,
    mwp.birthdate AS dob,
    ops.location AS location,
    patient_teen_visit.last_appt_date AS last_appt_date,
    patient_visit.art_regimen AS current_regimen,
    CASE
        WHEN ldl IS NOT NULL THEN 'LDL'
        WHEN other_results IS NOT NULL THEN other_results
        WHEN viral_load_result IS NOT NULL THEN viral_load_result
        WHEN less_than_limit IS NOT NULL THEN less_than_limit
    END AS result,
    test_date,
    patient_initial_visit.initial_visit_date AS date_initiated_in_art,
    FLOOR((DATEDIFF(@endDate, ops.start_date) / 30)) AS duration_in_teen_club
FROM
    mw_patient mwp
        LEFT JOIN
    (SELECT
        mtcf.patient_id,
            mtcf.visit_date AS followup_visit_date,
            mtcf.next_appointment AS last_appt_date
    FROM
        mw_teen_club_followup mtcf
    JOIN (SELECT
        patient_id,
            MAX(visit_date) AS visit_date,
            MAX(next_appointment) AS last_appt_date
    FROM
        mw_teen_club_followup
    WHERE
        visit_date <= @endDate
    GROUP BY patient_id) mtcf1 ON mtcf.patient_id = mtcf1.patient_id
        AND mtcf.visit_date = mtcf1.visit_date) patient_teen_visit ON patient_teen_visit.patient_id = mwp.patient_id
        LEFT JOIN
    (SELECT
        map.patient_id,
            map.visit_date AS followup_visit_date,
            map.next_appointment_date AS last_appt_date,
            map.art_regimen
    FROM
        mw_art_followup map
    JOIN (SELECT
        patient_id,
            MAX(visit_date) AS visit_date,
            MAX(next_appointment_date) AS last_appt_date
    FROM
        mw_art_followup
    WHERE
        visit_date <= @endDate
    GROUP BY patient_id) map1 ON map.patient_id = map1.patient_id
        AND map.visit_date = map1.visit_date) patient_visit ON patient_visit.patient_id = mwp.patient_id
        LEFT JOIN
    (SELECT
        mar.patient_id,
            mar.visit_date AS initial_visit_date,
            mar.pregnant_or_lactating AS initial_pregnant_or_lactating,
            mar.transfer_in_date
    FROM
        mw_art_initial mar
    JOIN (SELECT
        patient_id, MAX(visit_date) AS visit_date
    FROM
        mw_art_initial
    WHERE
        visit_date <= @endDate
    GROUP BY patient_id) mar1 ON mar.patient_id = mar1.patient_id
        AND mar.visit_date = mar1.visit_date) patient_initial_visit ON patient_initial_visit.patient_id = mwp.patient_id
        JOIN
    omrs_patient_identifier opi ON mwp.patient_id = opi.patient_id
        JOIN
    last_facility_outcome lfo ON opi.patient_id = lfo.pat
        AND opi.location = lfo.location
        JOIN
    last_teen_club_facility_outcome AS ops ON opi.patient_id = ops.pat
        AND opi.location = ops.location
        JOIN
    lookup_location AS l ON ops.location = l.target_value
        LEFT JOIN
    (SELECT
        avl1.patient_id,
            visit_date AS test_date,
            lab_location,
            viral_load_result,
            less_than_limit,
            ldl,
            other_results
    FROM
        mw_art_viral_load avl1
    JOIN (SELECT
        patient_id, MAX(visit_date) AS test_date
    FROM
        mw_art_viral_load
    WHERE
        visit_date <= @endDate
    GROUP BY patient_id) avl2 ON avl1.patient_id = avl2.patient_id
        AND avl1.visit_date = avl2.test_date) avl ON avl.patient_id = lfo.pat
WHERE
    opi.type = 'ARV Number'
        AND ops.program = 'Teen club program'
        AND ops.location = @location
ORDER BY sort_value