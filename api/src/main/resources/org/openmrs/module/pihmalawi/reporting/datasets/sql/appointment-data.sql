/************************************************************************

  Appointment Report Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @endDate = now();
  -- set @location = 'Matandani Rural Health Center';

*************************************************************************/

CALL create_rpt_identifiers(@location);
CALL create_rpt_active_eid(@endDate, @location);
CALL create_rpt_active_art(@endDate, @location);
CALL create_rpt_active_ncd(@endDate, @location);

CALL create_rpt_appt_alerts(@endDate);

CALL create_rpt_patients_expected(@endDate, @endDate, @location);

SELECT	patient_uuid,
	p.last_name,
	p.first_name,
	p.gender,
	TIMESTAMPDIFF(YEAR, p.birthdate, @endDate) as age,
	p.village,
	p.traditional_authority,
	p.district,
	p.phone_number,
	p.vhw,
	eid_number,
	art_number,
	ncd_number,
  e.last_visit_date,
	e.last_appt_date,
	alert,
	actions
FROM 			    rpt_patients_expected e
	INNER JOIN 		mw_patient p on p.patient_id = e.patient_id
	INNER JOIN 		omrs_patient o on p.patient_id = o.patient_id
	INNER JOIN 		rpt_identifiers i on i.patient_id = e.patient_id
	LEFT JOIN 		(SELECT patient_id,
									group_concat(alert SEPARATOR ', ') as alert,
									group_concat(action SEPARATOR ', ') as actions
								FROM rpt_appt_alerts GROUP BY patient_id) a
		ON a.patient_id = e.patient_id
ORDER BY last_name, first_name;




