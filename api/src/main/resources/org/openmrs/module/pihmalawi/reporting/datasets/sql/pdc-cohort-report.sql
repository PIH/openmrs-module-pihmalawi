/* 30 for age in months*/
SET @birthDateDivider = 365.25;

CALL create_last_pdc_outcome_at_facility(@endDate, @location);

SELECT
    opi.identifier as identifier,
    first_name,
    last_name,
    lfo.state AS outcome,
    lfo.start_date AS outcome_date,
    gender,
    IF(lfo.state = 'On treatment',
        FLOOR(DATEDIFF(@endDate, mp.birthdate) / @birthDateDivider),
        FLOOR(DATEDIFF(lfo.start_date, mp.birthdate) / @birthDateDivider)) AS age,
	birthdate,
    GROUP_CONCAT(DISTINCT mpd.diagnosis
        ORDER BY mpd.diagnosis
        SEPARATOR ', ') AS diagnosis_list,
    lfo.location as location,
    mpi.visit_date AS registration_date,
    last_visit_date,
    appointment_date
FROM
    pdc_last_facility_outcome lfo
        JOIN
    omrs_patient_identifier opi ON opi.patient_id = lfo.pat
        AND lfo.location = opi.location
        AND opi.type = 'pdc identifier'
        JOIN
    mw_patient mp ON mp.patient_id = lfo.pat
        JOIN
    mw_pdc_initial mpi ON lfo.pat = mpi.patient_id
        JOIN
    (SELECT
        patient_id, MAX(obs_date) AS last_visit_date
    FROM
        omrs_obs
    WHERE
        encounter_type LIKE '%PDC%'
    GROUP BY patient_id) o ON o.patient_id = lfo.pat
        LEFT JOIN
    (SELECT
        patient_id, MAX(value_date) AS appointment_date
    FROM
        omrs_obs
    WHERE
        concept = 'Appointment date'
            AND value_date IS NOT NULL
    GROUP BY patient_id) ob ON ob.patient_id = lfo.pat
        LEFT JOIN
    mw_pdc_diagnoses mpd ON mpd.patient_id = lfo.pat
GROUP BY lfo.pat , opi.identifier , mp.first_name , mp.last_name , lfo.state , lfo.start_date , mp.gender , lfo.location , mpi.visit_date , o.last_visit_date , ob.appointment_date
ORDER BY SUBSTRING_INDEX(opi.identifier, ' ', 1) , CAST(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(opi.identifier, ' ', 2),
                ' ',
                - 1))
    AS UNSIGNED) , opi.identifier , lfo.pat;