SET @birthDateDivider = 365.25;

call create_last_tb_outcome_at_facility(@endDate,@location);

SELECT
    opi.identifier AS identifier,
    first_name,
    last_name,
    gender,
    IF(lfo.state = 'On treatment',
        FLOOR(DATEDIFF(@endDate, mp.birthdate) / @birthDateDivider),
        FLOOR(DATEDIFF(lfo.start_date, mp.birthdate) / @birthDateDivider)) AS age,
    birthdate,
    lfo.state AS outcome,
    lfo.start_date AS outcome_date,
    lfo.location AS outcome_location,
    mti.regimen_rhze AS first_regimen_rhze,
    mti.initiation_month_weight AS enrolment_weight,
    mti.visit_date AS enrolment_date,
    mti.location AS enrolment_location,
    initiation_month_smear_test,
    initiation_month_smear_test_date,
    initiation_month_culture_test,
    initiation_month_culture_test_date,
    initiation_month_xpert_test,
    initiation_month_xpert_test_date,
    initiation_month_lam_test,
    initiation_month_lam_test_date,
    CASE
        WHEN mtf_latest.rhze_regimen IS NOT NULL THEN 'RHZE'
        WHEN mtf_latest.rh_regimen IS NOT NULL THEN 'RH'
        WHEN mtf_latest.meningitis_regimen IS NOT NULL THEN 'Meningitis Regimen'
        ELSE NULL
    END AS current_regimen,
    CASE
        WHEN mtf_latest.rhze_regimen IS NOT NULL THEN mtf_latest.rhze_regimen
        WHEN mtf_latest.rh_regimen IS NOT NULL THEN mtf_latest.rh_regimen
        WHEN mtf_latest.meningitis_regimen IS NOT NULL THEN mtf_latest.meningitis_regimen
        ELSE NULL
    END AS current_regimen_pill_count,
    mtf_latest.visit_date AS current_regimen_date,
    mtf_latest.location AS current_regimen_location,
    next_appointment_date
FROM
    last_tb_facility_outcome lfo
        JOIN
    omrs_patient_identifier opi ON lfo.pat = opi.patient_id
        AND lfo.location = opi.location
        AND type = 'TB program identifier'
        JOIN
    mw_patient mp ON lfo.pat = mp.patient_id
        JOIN
    mw_tb_initial mti ON mti.patient_id = lfo.pat
        LEFT JOIN
    mw_tb_test_results mtr ON mtr.patient_id = lfo.pat
left join (
    SELECT
        patient_id,
        MAX(visit_date) AS max_visit_date
    FROM mw_tb_followup
    WHERE (rhze_regimen is not null OR rh_regimen is not null OR meningitis_regimen is not null)
    GROUP BY patient_id
) AS latest_followup_date ON lfo.pat = latest_followup_date.patient_id
left join mw_tb_followup mtf_latest ON lfo.pat = mtf_latest.patient_id
    AND mtf_latest.visit_date = latest_followup_date.max_visit_date
    AND (mtf_latest.rhze_regimen is not null OR mtf_latest.rh_regimen is not null OR mtf_latest.meningitis_regimen is not null)
ORDER BY
    CAST(SUBSTRING_INDEX(opi.identifier, '/', 1) AS UNSIGNED), -- Extracts 'NNO X' part, then casts to number
    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(opi.identifier, ' ', 1), '/', -1) AS UNSIGNED), -- Extracts year, e.g., '24' or '25'
    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(opi.identifier, ' ', 2), ' ', -1) AS UNSIGNED) -- Extracts the number after the slash, e.g., '1' or '2'
;