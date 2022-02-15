/*

  IC3 Indicators Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  * set @endDate = now();
  * set @startDate = DATE_ADD(@endDate,INTERVAL -90 DAY)
  * set @location = 'Neno District Hospital'

*/


CALL create_rpt_ic3_data(@endDate, @location);

DROP TABLE IF EXISTS rpt_ic3_indicators;
CREATE TABLE rpt_ic3_indicators (
  id INT not null auto_increment primary key,
  indicator VARCHAR(255) default NULL,
  description VARCHAR(255) default NULL,
  indicator_type VARCHAR(255) default NULL,
  indicator_value NUMERIC default NULL
);

/*
	NCD-H1N - Number of patients with a chronic care diagnosis of "hypertension" who have an "On treatment" status
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H1N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H1N', 'Number of patients with a chronic care diagnosis of "hypertension" who have an "On treatment" status for the chronic care program at the facility on report end date', 'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND		htnDx is NOT NULL
         AND ncdCurrentLocation=@location
;

/*
	NCD-H2N - Hypertension patients newly registered during reporting period
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H2N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H2N', 'Patients with a new chronic care diagnosis (concept : 3683) of "hypertension" (concept : 903) in the last 3 months up to end date for report ',
    'At date', count(*)
  FROM 	rpt_ic3_data_table r, mw_diabetes_hypertension_initial h
  WHERE 	r.patient_id = h.patient_id
         AND		h.diagnosis_hypertension ='Hypertension' and h.diagnosis_hypertension_date is not null
         and h.diagnosis_hypertension_date > @startDate and h.diagnosis_hypertension_date <  @endDate
         AND h.location=@location
;

/*
	NCD-H3N - Hypertension patients who have defaulted during the reporting period
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H3N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H3N', 'Patients whose last given appointment date exceeded 2 months / 8 weeks within the last 3 months up to the report end date but did not have the outcomes (transferred out, died, treatment stopped) in the period',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE currentNcdState not in ('Patient transferred out', 'Patient died', 'Treatment stopped')
        and nextHtnDmAppt >= @startDate and nextHtnDmAppt <= DATE_ADD(@endDate,INTERVAL -56 DAY)
        AND ncdCurrentLocation=@location
;

/*
	NCD-H4N - Hypertension patients with a visit in last 3 months
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H4N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H4N', 'Patients who had any "DIABETES HYPERTENSION FOLLOWUP" encounter at the location for the report in the last 3 months to the end date and a chronic care diagnosis of "hypertension" at that "DIABETES HYPERTENSION FOLLOWUP" encounter',
    'At date', count(*)
  FROM rpt_ic3_data_table r, mw_diabetes_hypertension_followup h
  WHERE r.patient_id=h.patient_id and r.htnDx='X' and h.visit_date >= DATE_ADD(@endDate,INTERVAL -90 DAY) and h.visit_date <= @endDate
        and h.location=@location
;

select * from rpt_ic3_indicators;
