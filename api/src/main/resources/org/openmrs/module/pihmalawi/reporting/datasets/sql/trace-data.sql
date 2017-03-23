
-- ## parameter = endDate|End Date|java.util.Date
set @endDate = now();
set @location = 'Matandani Rural Health Center';
set @minWeeks = 2;
set @phase1 = TRUE;

/************************************************************************
  Create base trace table and load it with all patients who are active in ART, EID, or NCD programs
  TODO: Should we include anyone "active" in the HIV program who is not in ART, or EID?  eg. old PART patients?
*************************************************************************/

DROP TEMPORARY TABLE IF EXISTS trace_patients;
CREATE TEMPORARY TABLE trace_patients (
  patient_id            INT NOT NULL,
  active_eid            BOOLEAN,
  active_art            BOOLEAN,
  active_ncd            BOOLEAN,
  eid_number            VARCHAR(50),
  art_number            VARCHAR(50),
  ncd_number            VARCHAR(50),
  last_art_visit_date   DATE,
  last_art_visit_days   INT,
  last_appt_date_art    DATE,
  days_late_appt_art    INT,
  days_to_next_appt_art INT,
  last_eid_visit_date   DATE,
  last_eid_visit_days   INT,
  last_appt_date_eid    DATE,
  days_late_appt_eid    INT,
  days_to_next_appt_eid INT
);

INSERT INTO trace_patients (patient_id, active_eid, eid_number)
  SELECT patient_id, TRUE, eid_number FROM mw_eid_register WHERE location = @location and (end_date IS NULL OR end_date > @endDate)
;

INSERT INTO trace_patients (patient_id, active_art, art_number)
  SELECT patient_id, TRUE, art_number FROM mw_art_register WHERE location = @location and (end_date IS NULL OR end_date > @endDate)
ON DUPLICATE KEY UPDATE active_art = values(active_art), art_number = values(art_number);
;

INSERT INTO trace_patients (patient_id, active_ncd, ncd_number)
  SELECT patient_id, TRUE, ncd_number FROM mw_ncd_register WHERE location = @location and (end_date IS NULL OR end_date > @endDate)
ON DUPLICATE KEY UPDATE active_ncd = values(active_ncd), ncd_number = values(ncd_number)
;

/************************************************************************
  Get EID visit and appointment data
  We get the next appointment date from the last visit, not just the latest
  appointment date obs, in case it is left blank on the last visit
*************************************************************************/

UPDATE      trace_patients t
  INNER JOIN (
               SELECT    patient_id, max(visit_date) as last_visit
               FROM      mw_eid_visits
               WHERE     visit_date <= @endDate
               AND       location = @location
               GROUP BY   patient_id
             ) v on t.patient_id = v.patient_id
SET
  t.last_eid_visit_date = v.last_visit,
  t.last_eid_visit_days = datediff(@endDate, v.last_visit)
;

UPDATE      trace_patients t
  INNER JOIN  mw_eid_visits v ON t.patient_id = v.patient_id and t.last_eid_visit_date = v.visit_date and v.location = @location
SET         t.last_appt_date_eid = v.next_appointment_date;

UPDATE      trace_patients
SET         days_late_appt_eid = datediff(@endDate, last_appt_date_eid)
WHERE       last_appt_date_eid IS NOT NULL
AND         last_appt_date_eid < @endDate;

UPDATE      trace_patients
SET         days_to_next_appt_eid = datediff(last_appt_date_eid, @endDate)
WHERE       last_appt_date_eid IS NOT NULL
AND         last_appt_date_eid >= @endDate;

/************************************************************************
  Get ART visit and appointment data
  We get the next appointment date from the last visit, not just the latest
  appointment date obs, in case it is left blank on the last visit
*************************************************************************/

UPDATE      trace_patients t
INNER JOIN (
              SELECT    patient_id, max(visit_date) as last_visit
              FROM      mw_art_visits
              WHERE     visit_date <= @endDate
              AND       location = @location
              GROUP BY  patient_id
           ) v on t.patient_id = v.patient_id
SET
  t.last_art_visit_date = v.last_visit,
  t.last_art_visit_days = datediff(@endDate, v.last_visit)
;

UPDATE      trace_patients t
INNER JOIN  mw_art_visits v ON t.patient_id = v.patient_id and t.last_art_visit_date = v.visit_date and v.location = @location
SET         t.last_appt_date_art = v.next_appointment_date;

UPDATE      trace_patients
SET         days_late_appt_art = datediff(@endDate, last_appt_date_art)
WHERE       last_appt_date_art IS NOT NULL
AND         last_appt_date_art < @endDate;

UPDATE      trace_patients
SET         days_to_next_appt_art = datediff(last_appt_date_art, @endDate)
WHERE       last_appt_date_art IS NOT NULL
AND         last_appt_date_art >= @endDate;






/*
     * Late HIV:  Active in HIV program and late for a appointment by the specified range


public CohortDefinition getLateHiv(TraceType traceType) {
CohortDefinition lateForHivVisit = df.getPatientsLateForAppointment(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes(), traceType.getMinDaysInclusive(), traceType.getMaxDaysInclusive());
return df.getPatientsInAll(getActiveHiv(), lateForHivVisit);
}


 */

SELECT p.first_name, p.last_name, p.birthdate, p.village, p.vhw, t.* FROM trace_patients t
inner join mw_patient p on t.patient_id = p.patient_id
where t.active_eid = TRUE ;