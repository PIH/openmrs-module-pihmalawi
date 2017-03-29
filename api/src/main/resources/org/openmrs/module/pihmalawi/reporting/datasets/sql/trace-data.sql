
/************************************************************************

  TRACE Report Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @endDate = now();
  -- set @location = 'Matandani Rural Health Center';
  -- set @minWeeks = 2;
  -- set @maxWeeks = 6;
  -- set @phase1 = TRUE;

  The TRACE Report will include this dataset several times for various combinations of the above parameters

*************************************************************************/

CALL create_rpt_identifiers(@location);
CALL create_rpt_active_eid(@endDate, @location);
CALL create_rpt_active_art(@endDate, @location);
CALL create_rpt_active_ncd(@endDate, @location);
CALL create_rpt_priority_patients(@endDate);
CALL create_rpt_trace_criteria(@endDate, @location, @minWeeks, @maxWeeks, @phase1);


SELECT        t.patient_id,
              p.village,
              p.vhw,
              p.first_name,
              p.last_name,
              i.eid_number,
              i.art_number,
              i.ncd_number,
              art.last_visit_date as art_last_visit_date,
              art.last_appt_date as art_last_appt_date,
              round(art.days_late_appt / 7, 1) as art_weeks_out_of_care,
              eid.last_visit_date as eid_last_visit_date,
              eid.last_appt_date as eid_last_appt_date,
              round(eid.days_late_appt / 7, 1) as eid_weeks_out_of_care,
              ncd.last_visit_date as ncd_last_visit_date,
              ncd.last_appt_date as ncd_last_appt_date,
              round(ncd.days_late_appt / 7, 1) as ncd_weeks_out_of_care,
              d.diagnoses,
              c.priority_criteria,
              group_concat(t.criteria ORDER BY t.criteria asc SEPARATOR ', ') as trace_criteria
FROM          rpt_trace_criteria t
INNER JOIN    mw_patient p on t.patient_id = p.patient_id
LEFT JOIN     rpt_identifiers i on i.patient_id = p.patient_id
LEFT JOIN     rpt_active_art art on art.patient_id = p.patient_id
LEFT JOIN     rpt_active_eid eid on eid.patient_id = p.patient_id
LEFT JOIN     rpt_active_ncd ncd on ncd.patient_id = p.patient_id
LEFT JOIN     ( select patient_id, group_concat(priority ORDER BY priority asc SEPARATOR ', ') as priority_criteria from rpt_priority_patients GROUP BY patient_id) c on c.patient_id = p.patient_id
LEFT JOIN     ( select patient_id, group_concat(diagnosis ORDER BY diagnosis asc SEPARATOR ', ') as diagnoses from mw_ncd_diagnoses where diagnosis_date <= @endDate GROUP BY patient_id) d on d.patient_id = p.patient_id
GROUP BY      t.patient_id
ORDER BY      if(p.vhw is null, 1, 0), p.vhw, p.village, p.last_name
;
