/************************************************************************

  Appointment Report Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @startDate = now();
  -- set @endDate = now();
  -- set @location = 'Matandani Rural Health Center';

  Need to decide if this is a one off SQL report or something else. 

*************************************************************************/

CALL create_rpt_identifiers(@location);
CALL create_rpt_active_eid(@endDate, @location);
CALL create_rpt_active_art(@endDate, @location);
CALL create_rpt_active_ncd(@endDate, @location);

CALL create_rpt_appt_alerts(@endDate);

create_rpt_patients_expected(@endDate, @endDate, @location);

SELECT		 	'☐' as "Client Arrived",
				'☐' as "VHW Present",
				last_name as "Last Name",
				first_name as "Given Name",
				gender as "M/F",
				TIMESTAMPDIFF(YEAR, birthdate, @endDate) as "Age",
				village as "Village",
				vhw as "VHW",
				eid_number as "HCC Number",
				art_number as "ARV Number",
				ncd_number as "NCD Number",
				alert as "Alert",
				action as "Action"
FROM 			rpt_patients_expected e
INNER JOIN 		mw_patient p on p.patient_id = e.patient_id
INNER JOIN 		rpt_identifiers i on i.patient_id = e.patient_id
LEFT JOIN 		(SELECT patient_id, 
						group_concat(alert SEPARATOR ', ') as alert, 
						group_concat(action SEPARATOR ', ') as action 
				 FROM rpt_appt_alerts GROUP BY patient_id) a 
				 ON a.patient_id = e.patient_id;




