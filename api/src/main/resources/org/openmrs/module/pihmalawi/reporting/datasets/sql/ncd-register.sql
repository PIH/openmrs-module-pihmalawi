/*
SET @location = 'neno district hospital';
SET @endDate = '2026-03-09';*/

CALL create_last_ncd_outcome_at_facility(@endDate, @location);
SELECT
    lfo.identifier as identifier,
    op.start_date AS registration_date,
    first_name,
    last_name,
    gender,
    FLOOR((DATEDIFF(@endDate, mp.birthdate) / 365.25)) AS current_age,
    FLOOR((DATEDIFF(op.start_date, mp.birthdate) / 365.25)) AS age_at_enrolment,
    CASE
        WHEN FLOOR((DATEDIFF(op.start_date, mp.birthdate) / 365.25)) <= 15 THEN FLOOR((DATEDIFF(op.start_date, mp.birthdate) / 365.25))
        END AS below_15_yrs,
    CASE
        WHEN FLOOR((DATEDIFF(op.start_date, mp.birthdate) / 365.25)) > 15 THEN FLOOR((DATEDIFF(op.start_date, mp.birthdate) / 365.25))
        END AS 15_yrs_older,
    village,
    traditional_authority,
    district,
    hiv_diagnosis,
    hiv_diagnosis_date,
    dm_diagnosis,
    dm_diagnosis_date,
    dm_outcome,
    dm_outcome_date,
    htn_diagnosis,
    htn_diagnosis_date,
    htn_outcome,
    htn_outcome_date,
    asthma_copd_diagnosis,
    asthma_copd_diagnosis_date,
    asthma_copd_outcome,
    asthma_copd_outcome_date,
    ckd_diagnosis,
    ckd_diagnosis_date,
    ckd_outcome,
    ckd_outcome_date,
    chf_diagnosis,
    chf_diagnosis_date,
    chf_outcome,
    chf_outcome_date,
    ncd_other_diagnosis,
    ncd_other_diagnosis_date,
    ncd_other_outcome,
    ncd_other_outcome_date,
    scd_diagnosis,
    scd_diagnosis_date,
    scd_outcome,
    scd_outcome_date,
    mh_diagnosis,
    mh_diagnosis_date,
    mh_outcome,
    mh_outcome_date,
    epilepsy_diagnosis,
    epilepsy_diagnosis_date,
    epilepsy_outcome,
    epilepsy_outcome_date,
    lfo.location as location
FROM
    last_ncd_facility_outcome lfo
        JOIN
    mw_patient mp ON mp.patient_id = lfo.pat
        JOIN
    (SELECT
         patient_id,
         MIN(start_date) AS start_date,
         GROUP_CONCAT(DISTINCT program
                SEPARATOR ', ') AS programs_enrolled
     FROM
         omrs_program_state
     WHERE
         program IN ('Chronic care program' , 'mental health program')
       AND start_date <= @endDate
     GROUP BY patient_id) op ON op.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         dhi.patient_id,
         CASE
             WHEN diagnosis_type_1_diabetes = 'Type 1 diabetes' THEN 'Type 1 diabetes'
             WHEN diagnosis_type_2_diabetes = 'Type 2 diabetes' THEN 'Type 2 diabetes'
             END AS dm_diagnosis,
         MIN(visit_date) AS dm_diagnosis_date,
         MAX(state) AS dm_outcome,
         MAX(start_date) AS dm_outcome_date
     FROM
         mw_diabetes_hypertension_initial dhi
             JOIN omrs_program_state ops ON ops.patient_id = dhi.patient_id
     WHERE
         diagnosis_type_1_diabetes IS NOT NULL
        OR diagnosis_type_2_diabetes IS NOT NULL
         AND workflow = 'Diabetes Hypertension treatment'
     GROUP BY patient_id) dm ON dm.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         dhi.patient_id,
         diagnosis_hypertension AS htn_diagnosis,
         diagnosis_hypertension,
         MIN(visit_date) AS htn_diagnosis_date,
         MAX(state) AS htn_outcome,
         MAX(start_date) AS htn_outcome_date
     FROM
         mw_diabetes_hypertension_initial dhi
             JOIN omrs_program_state ops ON ops.patient_id = dhi.patient_id
     WHERE
         diagnosis_hypertension = 'Hypertension'
       AND workflow = 'Diabetes Hypertension treatment'
     GROUP BY patient_id) htn ON htn.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         mai.patient_id,
         CASE
             WHEN diagnosis_asthma = 'Asthma' THEN 'Asthma'
             WHEN diagnosis_copd = 'Chronic obstructive pulmonary disease' THEN 'Chronic obstructive pulmonary disease'
             END AS asthma_copd_diagnosis,
         MIN(visit_date) AS asthma_copd_diagnosis_date,
         MAX(state) AS asthma_copd_outcome,
         MAX(start_date) AS asthma_copd_outcome_date
     FROM
         mw_asthma_initial mai
             JOIN omrs_program_state ops ON ops.patient_id = mai.patient_id
             AND workflow = 'Asthma treatment'
     GROUP BY patient_id) ast ON ast.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         msc.patient_id,
         'SCD' AS scd_diagnosis,
         MIN(visit_date) AS scd_diagnosis_date,
         MAX(state) AS scd_outcome,
         MAX(start_date) AS scd_outcome_date
     FROM
         mw_sickle_cell_disease_initial msc
             JOIN omrs_program_state ops ON ops.patient_id = msc.patient_id
             AND workflow = 'Sickle cell disease treatment'
     GROUP BY patient_id) scd ON scd.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         mhi.patient_id,
         'Mental Health' AS mh_diagnosis,
         MIN(visit_date) AS mh_diagnosis_date,
         MAX(state) AS mh_outcome,
         MAX(start_date) AS mh_outcome_date
     FROM
         mw_mental_health_initial mhi
             JOIN omrs_program_state ops ON ops.patient_id = mhi.patient_id
             AND workflow = 'Mental health treatment'
     GROUP BY patient_id) mh ON mh.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         mei.patient_id,
         'Epilepsy' AS epilepsy_diagnosis,
         MIN(visit_date) AS epilepsy_diagnosis_date,
         MAX(state) AS epilepsy_outcome,
         MAX(start_date) AS epilepsy_outcome_date
     FROM
         mw_epilepsy_initial mei
             JOIN omrs_program_state ops ON ops.patient_id = mei.patient_id
             AND workflow = 'Epilepsy treatment'
     GROUP BY patient_id) epi ON epi.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         mci.patient_id,
         'CKD' AS ckd_diagnosis,
         MIN(visit_date) AS ckd_diagnosis_date,
         MAX(state) AS ckd_outcome,
         MAX(start_date) AS ckd_outcome_date
     FROM
         mw_ckd_initial mci
             JOIN omrs_program_state ops ON ops.patient_id = mci.patient_id
             AND workflow = 'Chronic Kidney disease treatment'
     GROUP BY patient_id) ckd ON ckd.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         mci.patient_id,
         'CHF' AS chf_diagnosis,
         MIN(visit_date) AS chf_diagnosis_date,
         MAX(state) AS chf_outcome,
         MAX(start_date) AS chf_outcome_date
     FROM
         mw_chf_initial mci
             JOIN omrs_program_state ops ON ops.patient_id = mci.patient_id
             AND workflow = 'Chronic Heart Failure treatment'
     GROUP BY patient_id) chf ON chf.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         mno.patient_id,
         'NCD Other' AS ncd_other_diagnosis,
         MIN(visit_date) AS ncd_other_diagnosis_date,
         MAX(state) AS ncd_other_outcome,
         MAX(start_date) AS ncd_other_outcome_date
     FROM
         mw_ncd_other_initial mno
             JOIN omrs_program_state ops ON ops.patient_id = mno.patient_id
             AND workflow = 'NCD Other treatment'
     GROUP BY patient_id) oth ON oth.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
         patient_id,
         'HIV' AS hiv_diagnosis,
         MIN(visit_date) AS hiv_diagnosis_date
     FROM
         mw_art_initial
     GROUP BY patient_id) hv ON hv.patient_id = lfo.pat
GROUP BY lfo.pat , lfo.identifier
ORDER BY SUBSTRING_INDEX(lfo.identifier, ' ', 1) , CAST(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(lfo.identifier, ' ', 2),
                                                                             ' ',
                                                                             - 1))
    AS UNSIGNED) , lfo.identifier , lfo.pat;
