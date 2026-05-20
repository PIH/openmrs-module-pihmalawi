/* 30 for age in months*/
SET @birthDateDivider = 365;

CALL create_last_art_outcome_at_facility(@endDate, @location);

SELECT
    CONVERT(SUBSTRING_INDEX(MAX(opi.identifier), ' ', -1), SIGNED) AS sort_value,
    MAX(opi.identifier) AS identifier,
    MAX(l.source_key) AS location_key,
    mwp.first_name,
    mwp.last_name,
    ops.state,
    ops.start_date,
    mwp.gender,
    IF(ops.state = "On antiretrovirals",
       FLOOR(DATEDIFF(@endDate, mwp.birthdate) / @birthDateDivider),
       FLOOR(DATEDIFF(ops.start_date, mwp.birthdate) / @birthDateDivider)) AS age,
    ops.location,
    patient_visit.last_appt_date,
    patient_visit.current_regimen,
    patient_visit.ctx_960,
    patient_visit.ctx_960_pills,
    patient_visit.inh_300,
    patient_visit.inh_300_pills,
    patient_visit.rfp_150,
    patient_visit.rfp_150_pills,
    patient_visit.pregnant_or_lactating_at_last_visit,
    patient_initial_visit.pregnant_or_lactating_at_initial,
    patient_initial_visit.initial_visit_date,
    patient_initial_visit.transfer_in_date
FROM mw_patient mwp

         LEFT JOIN (
    SELECT
        map.patient_id,
        MAX(map.next_appointment_date) AS last_appt_date,
        MAX(map.art_regimen) AS current_regimen,
        MAX(map.ctx_960) AS ctx_960,
        MAX(map.ctx_960_pills) AS ctx_960_pills,
        MAX(map.inh_300) AS inh_300,
        MAX(map.inh_300_pills) AS inh_300_pills,
        MAX(map.rfp_150) AS rfp_150,
        MAX(map.rfp_150_pills) AS rfp_150_pills,
        MAX(map.pregnant_or_lactating) AS pregnant_or_lactating_at_last_visit
    FROM mw_art_followup map
             JOIN (
        SELECT patient_id, MAX(visit_date) AS max_visit_date
        FROM mw_art_followup
        WHERE visit_date <= @endDate
        GROUP BY patient_id
    ) map1 ON map.patient_id = map1.patient_id AND map.visit_date = map1.max_visit_date
    GROUP BY map.patient_id
) patient_visit ON patient_visit.patient_id = mwp.patient_id

         LEFT JOIN (
    SELECT
        mar.patient_id,
        MAX(mar.visit_date) AS initial_visit_date,
        MAX(mar.pregnant_or_lactating) AS pregnant_or_lactating_at_initial,
        MAX(mar.transfer_in_date) AS transfer_in_date
    FROM mw_art_initial mar
             JOIN (
        SELECT patient_id, MAX(visit_date) AS max_visit_date
        FROM mw_art_initial
        WHERE visit_date <= @endDate
        GROUP BY patient_id
    ) mar1 ON mar.patient_id = mar1.patient_id AND mar.visit_date = mar1.max_visit_date
    GROUP BY mar.patient_id
) patient_initial_visit ON patient_initial_visit.patient_id = mwp.patient_id

         JOIN omrs_patient_identifier opi ON mwp.patient_id = opi.patient_id AND opi.type = "ARV Number"
         JOIN last_facility_outcome AS ops ON mwp.patient_id = ops.pat AND opi.location = ops.location
         JOIN lookup_location AS l ON ops.location = l.target_value

GROUP BY
    mwp.patient_id,
    mwp.first_name,
    mwp.last_name,
    ops.state,
    ops.start_date,
    mwp.gender,
    mwp.birthdate,
    ops.location,
    patient_visit.last_appt_date,
    patient_visit.current_regimen,
    patient_visit.ctx_960,
    patient_visit.ctx_960_pills,
    patient_visit.inh_300,
    patient_visit.inh_300_pills,
    patient_visit.rfp_150,
    patient_visit.rfp_150_pills,
    patient_visit.pregnant_or_lactating_at_last_visit,
    patient_initial_visit.pregnant_or_lactating_at_initial,
    patient_initial_visit.initial_visit_date,
    patient_initial_visit.transfer_in_date

ORDER BY sort_value;