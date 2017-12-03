/*

  IC3 Indicators Dataset
  Requires Pentaho Warehouse tables
  Expected parameters:

  * set @endDate = now();
  * set @startDate = DATE_ADD(@endDate,INTERVAL -30 DAY)
  * location is hardcoded as NULL since report is to be run for all of upper / lower Neno

*/

set @location=NULL;

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
	IC3-M1
	Total New ART Clients currently enrolled
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M1';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M1', 'Total New ART Clients Enrolled', 'Period', count(*) 
FROM rpt_ic3_data_table
WHERE artStartDate >= @startDate 
AND artStartDate <= @endDate;		

/* 
	IC3-M2
	Total New NCD Clients currently enrolled
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M2';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M2', 'Total New NCD Clients Enrolled', 'Period', count(*) 
FROM rpt_ic3_data_table
WHERE ncdStartDate >= @startDate 
AND ncdStartDate <= @endDate;	

/* 
	IC3-M3
	Total clients with either NCD "On Treatment" or "On Antiretroviral"; exclude EID
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M3';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M3', 'Total IC3 Clients Enrolled', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	( 	(currentHivState = "On antiretrovirals")
		OR
			(currentNcdState = "On treatment")	
		);

/* 
	IC3-M4
	ART care current enrollments
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M4';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M4', 'ART care current enrollments', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentHivState = "On antiretrovirals"	
;

/* 
	IC3-M5
	EID care current enrollments
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M5';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M5', 'EID care current enrollments', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentHivState = "Exposed Child (Continue)"	
;


/*
	IC3-M6
	NCD care current enrollments
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M6';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M6', 'NCD care current enrollments', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentNcdState = "On treatment"	
;

/*
	IC3-M7
	Dual diagnosis: HIV-NCD care current enrollments
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M7';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M7', 'Dual diagnosis: HIV-NCD care current enrollments', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentNcdState = "On treatment"	
AND 	currentHivState = "On antiretrovirals"
;

/*
	IC3-M8
	Palliative care current enrollments 
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M8';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M8', 'Palliative care current enrollments ', 'At date', NULL;

/*
	IC3-M9
	Proportion IC3 Clients currently enrolled with visit last 3m
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M9N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M9N', 'Proportion IC3 Clients currently enrolled with visit last 3m', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	(currentNcdState = "On treatment"
		OR
		currentHivState = "On antiretrovirals"
		OR
		currentHivState = "Exposed Child (Continue)")
AND 	lastIc3Visit >= DATE_ADD(@endDate,INTERVAL -91 DAY)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M9D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M9D', 'Proportion IC3 Clients currently enrolled with visit last 3m', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE 	(currentNcdState = "On treatment"
		OR
		currentHivState = "On antiretrovirals"
		OR
		currentHivState = "Exposed Child (Continue)")
;

/*
	IC3-M10
	Proportion ART clients currently enrolled with visit in the last 3m
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M10N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M10N', 'Proportion ART clients currently enrolled with visit in the last 3m', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentHivState = "On antiretrovirals"
AND 	lastArtVisit >= DATE_ADD(@endDate,INTERVAL -91 DAY)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M10D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M10D', 'Proportion ART Clients currently enrolled with visit in the last 3m', 'At date', indicator_value
FROM rpt_ic3_indicators
WHERE 	indicator = 'IC3-M4'
;

/*
	IC3-M11
	Proportion NCD clients currently enrolled with visit in the last 3m
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M11N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M11N', 'Proportion NCD clients currently enrolled with visit in the last 3m', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentNcdState = "On treatment"
AND 	lastNcdVisit >= DATE_ADD(@endDate,INTERVAL -91 DAY)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M11D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M11D', 'Proportion NCD clients currently enrolled with visit in the last 3m', 'At date', indicator_value
FROM rpt_ic3_indicators
WHERE 	indicator = 'IC3-M6'
;

/*
	IC3-M12
	Proportion Dual diagnosis (HIV-NCD) clients currently enrolled with visit in the last 3m
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M12N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M12N', 'Proportion Dual diagnosis (HIV-NCD) clients currently enrolled with visit in the last 3m', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentNcdState = "On treatment"	
AND 	currentHivState = "On antiretrovirals"
AND 	lastNcdVisit >= DATE_ADD(@endDate,INTERVAL -91 DAY)
AND 	lastArtVisit >= DATE_ADD(@endDate,INTERVAL -91 DAY)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M12D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M12D', 'Proportion Dual diagnosis (HIV-NCD) clients currently enrolled with visit in the last 3m', 'At date', indicator_value
FROM rpt_ic3_indicators
WHERE 	indicator = 'IC3-M7'
;

/*
	IC3-M13
	Proportion HIV-NCD Clients with next appointments for all conditions scheduled SAME day
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M13N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M13N', 'Proportion HIV-NCD Clients with next appointments for all conditions scheduled SAME day', 'At date', count(*) 
FROM rpt_ic3_data_table
WHERE 	currentNcdState = "On treatment"	
AND 	currentHivState = "On antiretrovirals"
AND 	latestAppt = nextArtAppt
AND 	(latestAppt = nextHtnDmAppt OR nextHtnDmAppt IS NULL)
AND 	(latestAppt = nextAsthmaAppt OR nextAsthmaAppt IS NULL)
AND 	(latestAppt = nextEpilepsyAppt OR nextEpilepsyAppt IS NULL)
AND 	(latestAppt = nextMentalHealthAppt OR nextMentalHealthAppt IS NULL)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M13D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M13D', 'Proportion HIV-NCD Clients with next appointments for all conditions scheduled SAME day', 'At date', indicator_value
FROM rpt_ic3_indicators
WHERE 	indicator = 'IC3-M7'
;

/*
	IC3-M14
	Proportion of eligible clients with viral load result in last 2y
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M14N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M14N', 'Proportion of eligible clients with viral load result in last 2y', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE DATEDIFF(@endDate, artStartDate) >= 182
AND DATEDIFF(@endDate,lastViralLoadTest) <= 730
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M14D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M14D', 'Proportion of eligible clients with viral load result in last 2y', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE DATEDIFF(@endDate, artStartDate) >= 182
;

/*
	IC3-M15
	Universal Health Coverage for 2nd Line: Proportion of expected clients on 2nd line treatment (7a, 8a, 9a, 9P, 10a, 11a, 11P, 12a, 12P, Other)
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M15N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M15N', 'Proportion of expected clients on 2nd line treatment', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE lastArtRegimen in ('7A: TDF / 3TC + LPV/r', '8A: AZT / 3TC + LPV/r', '9A: ABC / 3TC + LPV/r', '10A: TDF / 3TC + LPV/r', '11A: AZT / 3TC + LPV', 'Other')
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M15D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M15D', 'Proportion of expected clients on 2nd line treatment', 'At date', indicator_value*0.15
FROM rpt_ic3_indicators
WHERE 	indicator = 'IC3-M4'
;

/*
	IC3-M16
	Proportion of new EID enrollments with Mother's Status and ART ID indicated
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M16N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M16N', 'Proportion of new EID enrollments with Mother\'s Status and ART ID indicated', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE eidStartDate >= @startDate 
AND eidStartDate <= @endDate
AND motherArtNumber IS NOT NULL
AND motherHivStatus IS NOT NULL
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M16D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M16D', 'Proportion of new EID enrollments with Mother\'s Status and ART ID indicated', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE eidStartDate >= @startDate 
AND eidStartDate <= @endDate
;

/*
	IC3-M17
	Proportion of Hypertension patients with Blood Pressure recorded in the last visit
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M17N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M17N', 'Proportion of Hypertension patients with Blood Pressure recorded in the last visit', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE htnDx is NOT NULL
AND lastNcdVisit <= @endDate
AND systolicBp is NOT NULL
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M17D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M17D', 'Proportion of Hypertension patients with Blood Pressure recorded in the last visit', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE htnDx is NOT NULL
AND lastNcdVisit <= @endDate
;

/*
	IC3-M18
	Proportion of Hypertensive clients with SBP 180+ or DBP 110+ at last visit
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M18N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M18N', 'Proportion of Hypertensive clients with SBP 180+ or DBP 110+ at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE htnDx is NOT NULL
AND currentNcdState = "On Treatment"
AND (systolicBp > 180 OR diastolicBp > 110)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M18D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M18D', 'Proportion of Hypertensive clients with SBP 180+ or DBP 110+ at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE htnDx is NOT NULL
AND currentNcdState = "On Treatment"
;

/*
	IC3-M19
	Proportion of Diabetes patients with FS OR HBA1C recorded at last visit
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M19N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M19N', 'Proportion of Diabetes patients with FS OR HBA1C recorded at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE dmDx is NOT NULL
AND currentNcdState = "On Treatment"
AND (fingerStick IS NOT NULL or hba1c IS NOT NULL)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M19D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M19D', 'Proportion of Diabetes patients with FS OR HBA1C recorded at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE dmDx is NOT NULL
AND currentNcdState = "On Treatment"
;

/*
	IC3-M20
	Proportion Diabetes clients with finger stick value 200+ OR HBA1C 7% or over at most recent
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M20N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M20N', 'Proportion Diabetes clients with finger stick value 200+ OR HBA1C 7% or over at most recent
', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE dmDx is NOT NULL
AND currentNcdState = "On Treatment"
AND (fingerStick> 200 or hba1c >= 7)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M20D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M20D', 'Proportion Diabetes clients with finger stick value 200+ OR HBA1C 7% or over at most recent', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE dmDx is NOT NULL
AND currentNcdState = "On Treatment"
;

/*
	IC3-M21
	Proportion Epilepsy patients with seizures recorded at last visit
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M21N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M21N', 'Proportion Epilepsy patients with seizures recorded at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE seizuresSinceLastVisit IS NOT NULL
AND seizures IS NOT NULL
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M21D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M21D', 'Proportion Epilepsy patients with seizures recorded at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE seizuresSinceLastVisit IS NOT NULL
;

/*
	IC3-M22
	Proportion patients with asthma diagnosis with disease severity recorded at most recent visit
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M22N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M22N', 'Proportion patients with asthma diagnosis with disease severity recorded at most recent visit', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE asthmaDx is NOT NULL
AND currentNcdState = "On Treatment"
AND asthmaClassification IS NOT NULL
AND lastAsthmaVisitDate <= @endDate
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M22D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M22D', 'Proportion patients with asthma diagnosis with disease severity recorded at most recent visit', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE asthmaDx is NOT NULL
AND currentNcdState = "On Treatment"
AND lastAsthmaVisitDate <= @endDate
;

/*
	IC3-M23
	Proportion patients with asthma severity of "moderate persistent" or "severe persistent" or "uncontrolled" at last visit
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M23N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M23N', 'Proportion patients with asthma severity of \"moderate persistent\" or \"severe persistent\" or \"uncontrolled\" at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE asthmaDx is NOT NULL
AND currentNcdState = "On Treatment"
AND asthmaClassification IN ("Moderate persistent", "Severe persistent", "Uncontrolled")
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M23D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M23D', 'Proportion patients with asthma severity of \"moderate persistent\" or \"severe persistent\" or \"uncontrolled\" at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE asthmaDx is NOT NULL
AND currentNcdState = "On Treatment"
;

/*
	IC3-M24
	Total mental health clients currently enrolled in care
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M24';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M24', 'Total mental health clients currently enrolled in care', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE lastMentalHealthVisitDate is NOT NULL
;

/*
	IC3-M25
	Proportion of patients with report of no suicide risk at at last visit
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M25N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M25N', 'Proportion of patients with report of no suicide risk at at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE lastMentalHealthVisitDate is NOT NULL
AND suicideRisk = 'No'
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M25D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M25D', 'Proportion of patients with report of no suicide risk at at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE lastMentalHealthVisitDate is NOT NULL
AND suicideRisk IS NOT NULL
;

/*
	IC3-M26
	Proportion of patients who are able to function (daily living)
*/
-- numerator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M26N';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M26N', 'Proportion of patients with report of no suicide risk at at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE lastMentalHealthVisitDate is NOT NULL
AND ablePerformDailyActivities = 'Yes'
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M26D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M26D', 'Proportion of patients with report of no suicide risk at at last visit', 'At date', count(*)
FROM rpt_ic3_data_table
WHERE lastMentalHealthVisitDate is NOT NULL
AND ablePerformDailyActivities IS NOT NULL
;

/*
	IC3-M27
	ART client with Outcome - "On antiretroviral" AND had visit last 3m
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M27';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M27', 'ART client with Outcome - "On antiretroviral" AND had visit last 3m', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE lastArtVisit >= DATE_ADD(@endDate,INTERVAL -91 DAY)
AND currentHivState = "On antiretrovirals"
;

/*
	IC3-M28
	ART client with Outcome - "On antiretroviral" AND NO visit last 3m
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M28';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M28', 'ART client with Outcome - "On antiretroviral" AND NO visit last 3m', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE (lastArtVisit < DATE_ADD(@endDate,INTERVAL -91 DAY) OR lastArtVisit IS NULL)
AND currentHivState = "On antiretrovirals"
;

/*
	IC3-M29
	ART client with Outcome - Lost to Follow-up WITH outcome change date in reporting month
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M29';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M29', 'ART client with Outcome - Lost to Follow-up WITH outcome change date in reporting month', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE hivCurrentStateStart <= @endDate
AND hivCurrentStateStart >= @startDate
AND currentHivState = "Patient defaulted"
;

/*
	IC3-M30
	ART client with Outcome - Transferred out WITH outcome change date in reporting month
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M30';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M30', 'ART client with Outcome - Transferred out WITH outcome change date in reporting month', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE hivCurrentStateStart <= @endDate
AND hivCurrentStateStart >= @startDate
AND currentHivState = "Patient transferred out"
;

/*
	IC3-M31
	ART client with Outcome - Died WITH outcome change date in reporting month
*/
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M31';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M31', 'ART client with Outcome - Died WITH outcome change date in reporting month', 'Period', count(*)
FROM rpt_ic3_data_table
WHERE hivCurrentStateStart <= @endDate
AND hivCurrentStateStart >= @startDate
AND currentHivState = "Patient died"
;

select * from rpt_ic3_indicators;
