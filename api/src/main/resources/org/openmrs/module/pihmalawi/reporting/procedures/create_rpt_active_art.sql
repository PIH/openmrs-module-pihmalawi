DELIMITER $$

/************************************************************************
  Get Active ART patients, including ART visit and appointment data
  We get the next appointment date from the last visit, not just the latest
  appointment date obs, in case it is left blank on the last visit
*************************************************************************/
DROP PROCEDURE IF EXISTS create_rpt_active_art;
$$
CREATE PROCEDURE create_rpt_active_art(IN _endDate DATE, IN _location VARCHAR(255)) BEGIN

  DROP TEMPORARY TABLE IF EXISTS rpt_active_art;
  CREATE TEMPORARY TABLE rpt_active_art (
    patient_id        INT NOT NULL,
    art_number        VARCHAR(50),
    last_visit_date   DATE,
    last_visit_days   INT,
    last_appt_date    DATE,
    days_late_appt    INT,
    days_to_next_appt INT
  );
  CREATE INDEX rpt_active_art_patient_id_idx ON rpt_active_art(patient_id);

  INSERT INTO rpt_active_art (patient_id, art_number)
  SELECT patient_id, art_number FROM mw_art_register WHERE location = _location and (end_date IS NULL OR end_date > _endDate);

  UPDATE      rpt_active_art t
  INNER JOIN (
               SELECT    patient_id, max(visit_date) as last_visit
               FROM      mw_art_visits
               WHERE     visit_date <= _endDate AND location = _location
               GROUP BY  patient_id
             ) v on t.patient_id = v.patient_id
  SET
    t.last_visit_date = v.last_visit,
    t.last_visit_days = datediff(_endDate, v.last_visit)
  ;

  UPDATE      rpt_active_art t
  INNER JOIN  mw_art_visits v ON t.patient_id = v.patient_id and t.last_visit_date = v.visit_date and v.location = _location
  SET         t.last_appt_date = v.next_appointment_date;

  UPDATE      rpt_active_art
  SET         days_late_appt = datediff(_endDate, last_appt_date)
  WHERE       last_appt_date IS NOT NULL
  AND         last_appt_date < _endDate;

  UPDATE      rpt_active_art
  SET         days_to_next_appt = datediff(last_appt_date, _endDate)
  WHERE       last_appt_date IS NOT NULL
  AND         last_appt_date >= _endDate;

END
$$

DELIMITER ;