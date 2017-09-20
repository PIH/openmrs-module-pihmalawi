/*

  IC3 Indicators Dataset
  Requires Pentaho Warehouse tables
  Expected parameters:

  * set @endDate = now();
  * set @endDate = DATE_ADD(@endDate,INTERVAL -30 DAY)
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

select * from rpt_ic3_indicators;


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
		currentHivState = "On antiretrovirals")
AND 	lastIc3Visit > DATE_ADD(@endDate,INTERVAL -91 DAY)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M9D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M9D', 'Proportion IC3 Clients currently enrolled with visit last 3m', 'At date', indicator_value
FROM rpt_ic3_indicators
WHERE 	indicator = 'IC3-M3'
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
AND 	lastArtVisit > DATE_ADD(@endDate,INTERVAL -91 DAY)
;
-- denominator
DELETE from rpt_ic3_indicators WHERE indicator = 'IC3-M10D';
INSERT INTO rpt_ic3_indicators
	(indicator, description, indicator_type, indicator_value)
SELECT 'IC3-M10D', 'Proportion ART Clients currently enrolled with visit last 3m', 'At date', indicator_value
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
AND 	lastNcdVisit > DATE_ADD(@endDate,INTERVAL -91 DAY)
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