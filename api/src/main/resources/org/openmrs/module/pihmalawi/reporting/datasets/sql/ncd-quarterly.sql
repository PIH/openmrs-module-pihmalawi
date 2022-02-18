/*

  IC3 Indicators Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  * set @endDate = now();
  * set @startDate = DATE_ADD(@endDate,INTERVAL -90 DAY)
  * set @location = 'Neno District Hospital'

*/

set @startDate = DATE_ADD(@endDate,INTERVAL -90 DAY);

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
	for the chronic care program at the facility on report end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H1N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H1N', 'Patients enrolled and active in care', 'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND htnDx is NOT NULL
         AND ncdCurrentLocation=@location
;

/*
	NCD-H2N - Hypertension patients with a new chronic care diagnosis (concept : 3683) of "hypertension" (concept : 903)
	in the last 3 months up to end date for report
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H2N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H2N', 'Patients newly registered during reporting period',
    'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND htnDx is NOT NULL
         AND htnDxDate >= @startDate
         AND ncdCurrentLocation=@location
;

/*
	NCD-H3N - Hypertension patients whose last given appointment date exceeded 2 months / 8 weeks
	within the last 3 months up to the report end date but did not have the outcomes (transferred out, died, treatment stopped) in the period
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H3N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H3N', 'Hypertension patients who have defaulted during the reporting period',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE currentNcdState not in ('Patient transferred out', 'Patient died', 'Treatment stopped')
        AND nextHtnDmAppt is not null
        AND @startDate <= DATE_ADD(nextHtnDmAppt,INTERVAL +56 DAY) and DATE_ADD(nextHtnDmAppt,INTERVAL +56 DAY) <= @endDate
        AND ncdCurrentLocation=@location
;

/*
	NCD-H4N - Hypertension patients who had any "DIABETES HYPERTENSION FOLLOWUP" encounter at the location
	for the report in the last 3 months to the end date
	and a chronic care diagnosis of "hypertension" at that "DIABETES HYPERTENSION FOLLOWUP" encounter
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H4N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H4N', 'Hypertension patients with a visit in last 3 months',
    'At date', count(*)
  FROM rpt_ic3_data_table r, mw_diabetes_hypertension_followup h
  WHERE r.patient_id=h.patient_id and r.htnDx='X' and h.visit_date >= @startDate and h.visit_date <= @endDate
        and h.location=@location
;

/*
	NCD-H5N - Hypertension - Number of patients with a chronic care diagnosis of "hypertension" who have an "On treatment" status for the chronic care program at the facility on report end date, who have ever had any of the complications in the "DIABETES HYPERTENSION INITIAL" recorded in the past (Cardovascular disease, retinopathy, renal disease, stroke/TIA, PVD, Neuropathy, Sexual dysfunction)
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H5N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H5N', 'Currently enrolled patients that have ever experienced a complication',
    'At date', count(*)
  FROM rpt_ic3_data_table r, mw_diabetes_hypertension_initial h
  WHERE r.patient_id=h.patient_id
        AND r.htnDx='X'
        AND r.currentNcdState ='On treatment'
        AND r.ncdCurrentLocation=@location
        AND (h.cardiovascular_disease is not null
             OR h.retinopathy is not null
             OR h.renal_disease is not null
             OR h.stroke_and_tia is not null
             OR h.peripheral_vascular_disease is not null
             OR h.neuropathy is not null
             OR h.sexual_disorder is not null
        )
;

/*
	NCD-H6N - Hypertension patients who had a value for CV risk (concept: 8460) recorded in the last 3 months up to the reporting end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H6N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H6N', 'Hypertension Patients with CV risk % assessed during visit in last 3 months',
    'At date', count(*)
  FROM rpt_ic3_data_table r, mw_diabetes_hypertension_followup f
  WHERE  r.patient_id=f.patient_id
         and r.htnDx='X'
         and f.cardiovascular_risk is not null
         and f.visit_date >= @startDate and f.visit_date <= @endDate
         and f.location=@location
;

/*
	NCD-H7N - Patients with a chronic care diagnosis of "hypertension" and a "DIABETES HYPERTENSION FOLLOWUP" encounter recorded in the last 3 months
	with a Sytolic blood pressure of <140 and Diastolic blook pressure of <90 at last visit
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H7N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H7N', 'Patients with a visit in last 3 months (excluding new patients) that have BP below 140/90',
    'At date', count(*)
  FROM 	rpt_ic3_data_table r, mw_diabetes_hypertension_followup f
  WHERE 	r.patient_id = f.patient_id
         AND r.htnDx='X'
         AND f.visit_date >= @startDate and f.visit_date <= @endDate
         AND f.bp_stystolic is not null AND f.bp_diastolic is not null
         AND f.bp_stystolic <140 AND f.bp_diastolic < 90
         AND f.location=@location
;

/*
	NCD-H8N - Hypertension patients who had the observation of "hospitalization since last visit" (concept - 1715)
	recorded at last visit in the quarter up to the end date of the report
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-H8N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-H8N', 'Hypertension Patients hospitalized for the condition since last visit ',
    'At date', count(*)
  FROM rpt_ic3_data_table r
  WHERE  r.htnDx='X'
         and r.htnDmHospitalizedSinceLastVisit = 'Yes'
         and r.lastNcdVisit >= @startDate and r.lastNcdVisit <= @endDate
         and r.ncdCurrentLocation=@location
;

/*
	NCD-A1N - Number of patients with a chronic care diagnosis of "asthma" who have an "On treatment" status
	for the chronic care program at the facility on report end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-A1N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-A1N', 'Asthma patients enrolled and active in care', 'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND asthmaDx is NOT NULL
         AND ncdCurrentLocation=@location
;

/*
	NCD-A2N - Patients with a new chronic care diagnosis of "asthma" in the last 3 months up to end date for report
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-A2N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-A2N', 'Patients newly registered during reporting period',
    'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND asthmaDx is NOT NULL
         AND asthmaDxDate >= @startDate
         AND ncdCurrentLocation=@location
;

/*
	NCD-A3N - Patients who have defaulted during the reporting period -
	Asthma patients (diagnosis) patients whose last given appointment date exceeded 2 months / 8 weeks
	within the last 3 months up to the report end date
	but did not have the outcomes (transferred out, died, treatment stopped) in the period
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-A3N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-A3N', 'Patients who have defaulted during the reporting period',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE asthmaDx is not null
        AND currentNcdState not in ('Patient transferred out', 'Patient died', 'Treatment stopped')
        AND nextAsthmaAppt is not null
        AND @startDate <= DATE_ADD(nextAsthmaAppt,INTERVAL +56 DAY) and DATE_ADD(nextAsthmaAppt,INTERVAL +56 DAY) <= @endDate
        AND ncdCurrentLocation=@location
;

/*
	NCD-A4N - Asthma patients with a visit in last 3 months
	Patients who had any "ASTHMA FOLLOWUP" encounter at the location for the report in the last 3 months to the end date
	and a chronic care diagnosis of "asthma" at that "ASTHMA FOLLOWUP" encounter
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-A4N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-A4N', 'Asthma patients with a visit in last 3 months',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE asthmaDx is not null
        AND lastAsthmaVisitDate is not null
        AND @startDate <= lastAsthmaVisitDate and lastAsthmaVisitDate <= @endDate
        AND ncdCurrentLocation=@location
;

/*
	NCD-A5N - Asthma patients with disease severity recorded at most recent visit
	Active asthma diagnosed patients who had any disease severity observation recorded at last visit  (concept: 8410)
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-A5N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-A5N', 'Asthma patients with disease severity recorded at most recent visit',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE asthmaDx is not null
        AND lastAsthmaVisitDate is not null
        AND @startDate <= lastAsthmaVisitDate and lastAsthmaVisitDate <= @endDate
        AND asthmaClassificationAtLastVisit is not null
        AND ncdCurrentLocation=@location
;

/*
	NCD-A6N - Asthma patients with disease controlled (severity at intermittent or "mild persistent" at last visit)
	Active asthma diagnosed patients who had disease severity observation recorded at last visit  (concept: 8410) and the answer was severity at "intermittent" or "mild persistent"

*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-A6N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-A6N', 'Asthma patients with disease severity recorded at most recent visit',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE asthmaDx is not null
        AND lastAsthmaVisitDate is not null
        AND @startDate <= lastAsthmaVisitDate and lastAsthmaVisitDate <= @endDate
        AND asthmaClassificationAtLastVisit in ('Intermittent', 'Mild persistent')
        AND ncdCurrentLocation=@location
;

/*
	NCD-A7N - Asthma patients who were hospitalized for the condition since last visit
	Asthma diagnosed patients who had an "ASTHMA HOSPITALIZATION" encounter recorded at last visit

*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-A7N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-A7N', 'Patients hospitalized for the condition since last visit ',
    'At date', count(*)
  FROM rpt_ic3_data_table r, omrs_encounter e
  WHERE r.patient_id = e.patient_id
        AND e.encounter_type = 'ASTHMA HOSPITALIZATION'
        AND @startDate <= e.encounter_date AND e.encounter_date <= @endDate
        AND r.asthmaDx is not null
        AND r.lastAsthmaVisitDate is not null
        AND @startDate <= r.lastAsthmaVisitDate and r.lastAsthmaVisitDate <= @endDate
        AND r.ncdCurrentLocation=@location
;

select * from rpt_ic3_indicators;
