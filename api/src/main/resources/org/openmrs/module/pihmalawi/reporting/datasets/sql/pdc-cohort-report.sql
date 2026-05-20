/* 30 for age in months*/
SET @birthDateDivider = 365.25;


CALL create_last_pdc_outcome_at_facility(@endDate, @location);

DROP TABLE IF EXISTS mem_facility_outcome;
CREATE TABLE mem_facility_outcome ENGINE=MEMORY AS
SELECT * FROM pdc_last_facility_outcome;

ALTER TABLE mem_facility_outcome ADD INDEX (pat);

SELECT
    MAX(lfo.identifier) AS identifier,
    mp.first_name,
    mp.last_name,
    lfo.state AS outcome,
    lfo.start_date AS outcome_date,
    mp.gender,
    IF(lfo.state = 'On treatment',
       FLOOR(DATEDIFF(@endDate, mp.birthdate) / @birthDateDivider),
       FLOOR(DATEDIFF(lfo.start_date, mp.birthdate) / @birthDateDivider)) AS age,
    mp.birthdate,
    GROUP_CONCAT(DISTINCT mpd.diagnosis ORDER BY mpd.diagnosis SEPARATOR ', ') AS diagnosis_list,
    lfo.location AS location,
    MAX(mpi.visit_date) AS registration_date,
    o.last_visit_date,
    ob.appointment_date
FROM
    mem_facility_outcome lfo
        JOIN
    mw_patient mp ON mp.patient_id = lfo.pat
        LEFT JOIN
    mw_pdc_initial mpi ON mpi.patient_id = lfo.pat
        JOIN
    (
        SELECT obs.patient_id, MAX(obs.obs_date) AS last_visit_date
        FROM omrs_obs obs
        WHERE obs.encounter_type LIKE '%PDC%'
          AND obs.patient_id IN (SELECT pat FROM mem_facility_outcome)
        GROUP BY obs.patient_id
    ) o ON o.patient_id = lfo.pat
        LEFT JOIN
    (
        SELECT obs.patient_id, MAX(obs.value_date) AS appointment_date
        FROM omrs_obs obs
        WHERE obs.concept = 'Appointment date'
          AND obs.value_date IS NOT NULL
          AND obs.patient_id IN (SELECT pat FROM mem_facility_outcome)
        GROUP BY obs.patient_id
    ) ob ON ob.patient_id = lfo.pat
        LEFT JOIN
    mw_pdc_diagnoses mpd ON mpd.patient_id = lfo.pat
GROUP BY
    lfo.pat,
    lfo.state,
    lfo.start_date,
    lfo.location,
    mp.first_name,
    mp.last_name,
    mp.gender,
    mp.birthdate,
    o.last_visit_date,
    ob.appointment_date
ORDER BY
    SUBSTRING_INDEX(MAX(lfo.identifier), ' ', 1),
    CAST(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(MAX(lfo.identifier), ' ', 2), ' ', -1)) AS UNSIGNED),
    MAX(lfo.identifier),
    lfo.pat;

DROP TABLE IF EXISTS mem_facility_outcome;