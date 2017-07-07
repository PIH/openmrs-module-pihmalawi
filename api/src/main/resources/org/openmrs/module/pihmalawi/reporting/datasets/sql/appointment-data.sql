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

SELECT	last_name,
				first_name,
				gender,
				TIMESTAMPDIFF(YEAR, birthdate, @endDate) as age,
				village,
				vhw,
				eid_number,
				art_number,
				ncd_number,
				alert,
				actions
FROM 			    rpt_patients_expected e
INNER JOIN 		mw_patient p on p.patient_id = e.patient_id
INNER JOIN 		rpt_identifiers i on i.patient_id = e.patient_id
LEFT JOIN 		(SELECT patient_id,
						group_concat(alert SEPARATOR ', ') as alert, 
						group_concat(action SEPARATOR ', ') as actions
				 FROM rpt_appt_alerts GROUP BY patient_id) a 
				 ON a.patient_id = e.patient_id
ORDER BY last_name, first_name;




