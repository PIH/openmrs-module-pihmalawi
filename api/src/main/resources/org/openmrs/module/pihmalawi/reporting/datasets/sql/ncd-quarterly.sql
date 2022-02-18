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
  SELECT 'NCD-H1N', 'Hypertension patients enrolled and active in care', 'At date', count(*)
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
  SELECT 'NCD-H2N', 'Hypertension patients newly registered during reporting period',
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
	Active asthma diagnosed patients who had disease severity observation recorded at last visit  (concept: 8410)
	and the answer was severity at "intermittent" or "mild persistent"

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
  SELECT 'NCD-A7N', 'Asthma patients hospitalized for the condition since last visit ',
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

/*
	NCD-D1N - Diabetes patients enrolled and active in care
	Number of patients with a chronic care diagnosis of "Type 1 diabetes" or "Type 2 diabetes"
	who have an "On treatment" status for the chronic care program at the facility on report end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D1N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D1N', 'Diabetes patients enrolled and active in care', 'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND dmDx is NOT NULL
         AND ncdCurrentLocation=@location
;

/*
	NCD-D2N - Diabetes patients newly registered during reporting period
	Patients with a new chronic care diagnosis of "Type 1 Diabetes" in the last 3 months up to end date for report
	+ (PLUS) Patients with a new chronic care diagnosis of "Type 2 Diabetes" in the last 3 months up to end date for report
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D2N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D2N', 'Diabetes patients newly registered during reporting period',
    'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	dmDx is NOT NULL
         AND dmDxDate >= @startDate
         AND ncdCurrentLocation=@location
;

/*
	NCD-D3N - Diabetes patients currently enrolled patients that have ever experienced a complication -
	Number of patients with a chronic care diagnosis of "Type 1 diabetes" or "Type 2 diabetes"
	who have an "On treatment" status for the chronic care program at the facility on report end date,
	who have ever had any of the complications in the "DIABETES HYPERTENSION INITIAL"
	recorded in the past (Cardovascular disease, retinopathy, renal disease, stroke/TIA, PVD, Neuropathy, Sexual dysfunction)
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D3N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D3N', 'Diabetes patients currently enrolled patients that have ever experienced a complication',
    'At date', count(*)
  FROM rpt_ic3_data_table r, mw_diabetes_hypertension_initial h
  WHERE r.patient_id=h.patient_id
        AND r.dmDx='X'
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
	NCD-D4N - Diabetes Type 1 patients enrolled and active in care
	Number of patients with a chronic care diagnosis of "Type 1 diabetes" who have an "On treatment" status
	for the chronic care program at the facility on report end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D4N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D4N', 'Diabetes Type 1 patients enrolled and active in care', 'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND dmDx1 is NOT NULL
         AND ncdCurrentLocation=@location
;

/*
	NCD-D5N - Diabetes Type 1 patients newly registered during reporting period
	Patients with a new chronic care diagnosis of "Type 1 Diabetes" in the last 3 months up to end date for report
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D5N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D5N', 'Diabetes Type 1 patients newly registered during reporting period',
    'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	dmDx1 is NOT NULL
         AND dmDxDate >= @startDate
         AND ncdCurrentLocation=@location
;

/*
	NCD-D6N - Diabetes Type 1 patients who have defaulted during the reporting period
	"Type 1 diabetes" diagnosis patients whose last given appointment date exceeded 2 months / 8 weeks within the last 3 months up to the report
	end date but did not have the outcomes (transferred out, died, treatment stopped) in the period
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D6N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D6N', 'Diabetes Type 1 patients who have defaulted during the reporting period',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE currentNcdState not in ('Patient transferred out', 'Patient died', 'Treatment stopped')
        AND dmDx1 is not null
        AND @startDate <= DATE_ADD(nextHtnDmAppt,INTERVAL +56 DAY) and DATE_ADD(nextHtnDmAppt,INTERVAL +56 DAY) <= @endDate
        AND ncdCurrentLocation=@location
;

/*
	NCD-D7N - Diabetes Type 2 patients enrolled and active in care
	Number of patients with a chronic care diagnosis of "Type 2 diabetes" who have an "On treatment" status
	for the chronic care program at the facility on report end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D7N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D7N', 'Diabetes Type 2 patients enrolled and active in care', 'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	currentNcdState = "On treatment"
         AND dmDx2 is NOT NULL
         AND ncdCurrentLocation=@location
;

/*
	NCD-D8N - Diabetes Type 2 patients newly registered during reporting period
	Patients with a new chronic care diagnosis of "Type 2 Diabetes" in the last 3 months up to end date for report
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D8N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D8N', 'Diabetes Type 2 patients newly registered during reporting period',
    'At date', count(*)
  FROM 	rpt_ic3_data_table
  WHERE 	dmDx2 is NOT NULL
         AND dmDxDate >= @startDate
         AND ncdCurrentLocation=@location
;

/*
	NCD-D9N - Diabetes Type 2 patients who have defaulted during the reporting period
	"Type 2 diabetes" diagnosis patients whose last given appointment date exceeded 2 months / 8 weeks within the last 3 months up to the report
	end date but did not have the outcomes (transferred out, died, treatment stopped) in the period
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D9N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D9N', 'Diabetes Type 2 patients who have defaulted during the reporting period',
    'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE currentNcdState not in ('Patient transferred out', 'Patient died', 'Treatment stopped')
        AND dmDx2 is not null
        AND @startDate <= DATE_ADD(nextHtnDmAppt,INTERVAL +56 DAY) and DATE_ADD(nextHtnDmAppt,INTERVAL +56 DAY) <= @endDate
        AND ncdCurrentLocation=@location
;

/*
	NCD-D10N - Type 1 patients with visit in last 3 months
	"Type 1 diabetes" diagnosis patients with a visit in the last 3 months up to reporting end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D10N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D10N', 'Type 1 patients with visit in last 3 months', 'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE dmDx1 is not null
        AND lastHtnDmVisitDate is not null
        AND lastHtnDmVisitDate >= @startDate
        AND ncdCurrentLocation=@location
;

/*
	NCD-D11N - Type 2 patients with visit in last 3 months
	"Type 2 diabetes" diagnosis patients with a visit in the last 3 months up to reporting end date
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D11N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D11N', 'Type 2 patients with visit in last 3 months', 'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE dmDx2 is not null
        AND lastHtnDmVisitDate is not null
        AND lastHtnDmVisitDate >= @startDate
        AND ncdCurrentLocation=@location
;

/*
	NCD-D12N - Type 1 patients with a visit in the last three months, with FBS ( <=120 mg/dL)
	"Type 1 diabetes" diagnosis patients with a visit in the last 3 months up to reporting end date
	and a fingerstick recording at most recent visit with a value of <120 (concept : 887)
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D12N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D12N', 'Type 1 patients with a visit in the last three months, with FBS (<=120 mg/dL)', 'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE dmDx1 is not null
        AND lastHtnDmVisitDate is not null
        AND lastHtnDmVisitDate >= @startDate
        AND fingerStickAtLastVisit is not null
        AND fingerStickAtLastVisit <=120
        AND ncdCurrentLocation=@location
;

/*
	NCD-D13N - Type 2 patients with a visit in the last three months, with FBS (<=120 mg/dL)
	"Type 2 diabetes" diagnosis patients with a visit in the last 3 months up to reporting end date
	and a fingerstick recording at most recent visit with a value of <120 (concept : 887)
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D13N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D13N', 'Type 2 patients with a visit in the last three months, with FBS (<=120 mg/dL)', 'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE dmDx2 is not null
        AND lastHtnDmVisitDate is not null
        AND lastHtnDmVisitDate >= @startDate
        AND fingerStickAtLastVisit is not null
        AND fingerStickAtLastVisit <=120
        AND ncdCurrentLocation=@location
;

/*
	NCD-D14N - Type 2 patients on [long-acting or short-acting] Insulin
	"Type 2 diabetes" diagnosis patients with a recording of long acting
	or short acting insulin at most recent visit (concept : 1193)
	and answers (concept: 6750) OR (concept : 282)
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'NCD-D14N';
INSERT INTO rpt_ic3_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'NCD-D14N', 'Type 2 patients on [long-acting or short-acting] Insulin', 'At date', count(*)
  FROM rpt_ic3_data_table
  WHERE dmDx2 is not null
        AND lastHtnDmVisitDate is not null
        AND lastHtnDmVisitDate >= @startDate
        AND (longActingInsulin is not null or shortActingInsulin is not null)
        AND ncdCurrentLocation=@location
;

select * from rpt_ic3_indicators;
