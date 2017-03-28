
-- ## parameter = endDate|End Date|java.util.Date
-- set @endDate = now();
-- set @location = 'Matandani Rural Health Center';
-- set @minWeeks = 2;
-- set @maxWeeks = 6;
-- set @phase1 = TRUE;

/************************************************************************
  TODO: Answer the below questions

  - Do we want to include patients who are late for EID?  We are.
  - Do we want to include patients who are still enrolled in HIV, but not in ART or EID?
  - Similar to this, do we limit the trace criteria to ART, EID, and NCD patients (eg. not enrolled in HIV Program broadly).  We are.
  - Should we include the patient's HCC Number under EID Number, if they were Pre-ART but not EID?  We are not.
  - For visits and appointment dates, should we include visits outside of the enrollment location (eg. see 10016351)?  We are not.

*************************************************************************/

CALL create_rpt_identifiers(@location);
CALL create_rpt_active_eid(@endDate, @location);
CALL create_rpt_active_art(@endDate, @location);
CALL create_rpt_active_ncd(@endDAte, @location);
CALL create_rpt_trace_criteria(@endDate, @location, @minWeeks, @maxWeeks, @phase1);


SELECT        t.patient_id,
              p.village,
              p.vhw,
              p.first_name,
              p.last_name,
              i.eid_number,
              i.art_number,
              i.ncd_number,
              art.last_visit_date,
              art.last_appt_date,
              round(art.days_late_appt / 7, 1) as art_weeks_out_of_care,
              group_concat(t.criteria ORDER BY t.criteria asc SEPARATOR ', ') as trace_criteria
FROM          rpt_trace_criteria t
INNER JOIN    mw_patient p on t.patient_id = p.patient_id
LEFT JOIN     rpt_identifiers i on i.patient_id = p.patient_id
LEFT JOIN     rpt_active_art art on art.patient_id = p.patient_id
LEFT JOIN     rpt_active_eid eid on eid.patient_id = p.patient_id
GROUP BY      t.patient_id
ORDER BY      if(p.vhw is null, 1, 0), p.vhw, p.village, p.last_name
;

-- TODO:   diagnoses (basePatientData.getDiagnosesBasedOnMastercards()) - not needed for phase 1