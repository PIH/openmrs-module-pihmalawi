/*

  Mental Health Monthly Indicators Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  * set @endDate = now();
  * set @startDate = DATE_ADD(@endDate,INTERVAL -1 MONTH)
  * set @location = 'Neno District Hospital'

*/

set @startDate = DATE_ADD(@endDate,INTERVAL -1 MONTH);

CALL create_rpt_mh_data(@endDate, @location);

DROP TABLE IF EXISTS rpt_mh_indicators;
CREATE TABLE rpt_mh_indicators (
  id INT not null auto_increment primary key,
  indicator VARCHAR(255) default NULL,
  description VARCHAR(255) default NULL,
  indicator_type VARCHAR(255) default NULL,
  indicator_value NUMERIC default NULL
);

/*
	OMDC015_M_NC - Number of Male patients under 15 patients with new cases of chronic organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC015_M_NC', 'New Male cases under 15 yo patients with chronic organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	OMDC16_M_NC - Number of Male patients over 16 yo patients with new cases of chronic organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC16_M_NC', 'New Male cases over 16 yo patients with chronic organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	OMDC015_F_NC - Number of Female patients under 15 yo patients with new cases of chronic organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC015_F_NC', 'New Female cases under 15 yo patients with chronic organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	OMDC16_F_NC - Number of Female patients over 16 yo patients with new cases of chronic organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC16_F_NC', 'New Female cases over 16 yo patients with chronic organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	OMDC015_M_SC - Number of Male patients under 15 with a chronic organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC015_M_SC', 'Number of Male patients under 15 with a chronic organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	OMDC16_M_SC - Number of Male patients over 16 with a chronic organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC16_M_SC', 'Number of Male patients over 16 with a chronic organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	OMDC015_F_SC - Number of Female patients under 15 with a chronic organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC015_F_SC', 'Number of Female patients under 15 with a chronic organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	OMDC16_F_SC - Number of Female patients over 16 with a chronic organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDC16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDC16_F_SC', 'Number of Female patients over 16 with a chronic organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_chronic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;


/*
	OMDA015_M_NC - Number of Male patients under 15 patients with new cases of ACUTE organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA015_M_NC', 'New Male cases under 15 yo patients with ACUTE organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	OMDA16_M_NC - Number of Male patients over 16 yo patients with new cases of ACUTE organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA16_M_NC', 'New Male cases over 16 yo patients with ACUTE organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	OMDA015_F_NC - Number of Female patients under 15 yo patients with new cases of ACUTE organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA015_F_NC', 'New Female cases under 15 yo patients with ACUTE organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	OMDA16_F_NC - Number of Female patients over 16 yo patients with new cases of ACUTE organic mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA16_F_NC', 'New Female cases over 16 yo patients with ACUTE organic mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	OMDA015_M_SC - Number of Male patients under 15 with a ACUTE organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA015_M_SC', 'Number of Male patients under 15 with a ACUTE organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	OMDA16_M_SC - Number of Male patients over 16 with a ACUTE organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA16_M_SC', 'Number of Male patients over 16 with a ACUTE organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	OMDA015_F_SC - Number of Female patients under 15 with a ACUTE organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA015_F_SC', 'Number of Female patients under 15 with a ACUTE organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	OMDA16_F_SC - Number of Female patients over 16 with a ACUTE organic mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OMDA16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OMDA16_F_SC', 'Number of Female patients over 16 with a ACUTE organic mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_organic_mental_disorder_acute is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	AUMD015_M_NC - Number of Male patients under 15 patients with new cases of Alcohol use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD015_M_NC', 'New Male cases under 15 yo patients with Alcohol use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	AUMD16_M_NC - Number of Male patients over 16 yo patients with new cases of Alcohol use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD16_M_NC', 'New Male cases over 16 yo patients with Alcohol use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	AUMD015_F_NC - Number of Female patients under 15 yo patients with new cases of Alcohol use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD015_F_NC', 'New Female cases under 15 yo patients with Alcohol use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	AUMD16_F_NC - Number of Female patients over 16 yo patients with new cases of Alcohol use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD16_F_NC', 'New Female cases over 16 yo patients with Alcohol use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	AUMD015_M_SC - Number of Male patients under 15 with a Alcohol use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD015_M_SC', 'Number of Male patients under 15 with a Alcohol use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	AUMD16_M_SC - Number of Male patients over 16 with a Alcohol use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD16_M_SC', 'Number of Male patients over 16 with a Alcohol use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	AUMD015_F_SC - Number of Female patients under 15 with a Alcohol use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD015_F_SC', 'Number of Female patients under 15 with a Alcohol use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	AUMD16_F_SC - Number of Female patients over 16 with a Alcohol use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'AUMD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'AUMD16_F_SC', 'Number of Female patients over 16 with a Alcohol use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_alcohol_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	DUMD015_M_NC - Number of Male patients under 15 patients with new cases of Drug use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD015_M_NC', 'New Male cases under 15 yo patients with Drug use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	DUMD16_M_NC - Number of Male patients over 16 yo patients with new cases of Drug use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD16_M_NC', 'New Male cases over 16 yo patients with Drug use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	DUMD015_F_NC - Number of Female patients under 15 yo patients with new cases of Drug use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD015_F_NC', 'New Female cases under 15 yo patients with Drug use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	DUMD16_F_NC - Number of Female patients over 16 yo patients with new cases of Drug use mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD16_F_NC', 'New Female cases over 16 yo patients with Drug use mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	DUMD015_M_SC - Number of Male patients under 15 with a Drug use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD015_M_SC', 'Number of Male patients under 15 with a Drug use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	DUMD16_M_SC - Number of Male patients over 16 with a Drug use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD16_M_SC', 'Number of Male patients over 16 with a Drug use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	DUMD015_F_SC - Number of Female patients under 15 with a Drug use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD015_F_SC', 'Number of Female patients under 15 with a Drug use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	DUMD16_F_SC - Number of Female patients over 16 with a Drug use mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DUMD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DUMD16_F_SC', 'Number of Female patients over 16 with a Drug use mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_drug_use_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;


/*
	SHZ015_M_NC - Number of Male patients under 15 patients with new cases of Schizophrenia mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ015_M_NC', 'New Male cases under 15 yo patients with Schizophrenia disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	SHZ16_M_NC - Number of Male patients over 16 yo patients with new cases of Schizophrenia mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ16_M_NC', 'New Male cases over 16 yo patients with Schizophrenia mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	SHZ015_F_NC - Number of Female patients under 15 yo patients with new cases of Schizophrenia mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ015_F_NC', 'New Female cases under 15 yo patients with Schizophrenia mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	SHZ16_F_NC - Number of Female patients over 16 yo patients with new cases of Schizophrenia mental disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ16_F_NC', 'New Female cases over 16 yo patients with Schizophrenia mental disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	SHZ015_M_SC - Number of Male patients under 15 with a Schizophrenia mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ015_M_SC', 'Number of Male patients under 15 with a Schizophrenia mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	SHZ16_M_SC - Number of Male patients over 16 with a Schizophrenia mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ16_M_SC', 'Number of Male patients over 16 with a Schizophrenia mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	SHZ015_F_SC - Number of Female patients under 15 with a Schizophrenia mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ015_F_SC', 'Number of Female patients under 15 with a Schizophrenia mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	SHZ16_F_SC - Number of Female patients over 16 with a Schizophrenia mental disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SHZ16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SHZ16_F_SC', 'Number of Female patients over 16 with a Schizophrenia mental disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizophrenia is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	ATPD015_M_NC - Number of Male patients under 15 patients with new cases of Acute & transient psychotic disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD015_M_NC', 'New Male cases under 15 yo patients with Acute & transient psychotic disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	ATPD16_M_NC - Number of Male patients over 16 yo patients with new cases of Acute & transient psychotic disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD16_M_NC', 'New Male cases over 16 yo patients with Acute & transient psychotic disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	ATPD015_F_NC - Number of Female patients under 15 yo patients with new cases of Acute & transient psychotic disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD015_F_NC', 'New Female cases under 15 yo patients with Acute & transient psychotic disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	ATPD16_F_NC - Number of Female patients over 16 yo patients with new cases of Acute & transient psychotic disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD16_F_NC', 'New Female cases over 16 yo patients with Acute & transient psychotic disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	ATPD015_M_SC - Number of Male patients under 15 with a Acute & transient psychotic disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD015_M_SC', 'Number of Male patients under 15 with a Acute & transient psychotic disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	ATPD16_M_SC - Number of Male patients over 16 with a Acute & transient psychotic disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD16_M_SC', 'Number of Male patients over 16 with a Acute & transient psychotic disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	ATPD015_F_SC - Number of Female patients under 15 with a Acute & transient psychotic disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD015_F_SC', 'Number of Female patients under 15 with a Acute & transient psychotic disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	ATPD16_F_SC - Number of Female patients over 16 with a Acute & transient psychotic disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ATPD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ATPD16_F_SC', 'Number of Female patients over 16 with a Acute & transient psychotic disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_acute_and_transient_psychotic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	SAD015_M_NC - Number of Male patients under 15 patients with new cases of Schizo-affective disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD015_M_NC', 'New Male cases under 15 yo patients with Schizo-affective disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	SAD16_M_NC - Number of Male patients over 16 yo patients with new cases of Schizo-affective disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD16_M_NC', 'New Male cases over 16 yo patients with Schizo-affective disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	SAD015_F_NC - Number of Female patients under 15 yo patients with new cases of Schizo-affective disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD015_F_NC', 'New Female cases under 15 yo patients with Schizo-affective disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	SAD16_F_NC - Number of Female patients over 16 yo patients with new cases of Schizo-affective disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD16_F_NC', 'New Female cases over 16 yo patients with Schizo-affective disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	SAD015_M_SC - Number of Male patients under 15 with a Schizo-affective disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD015_M_SC', 'Number of Male patients under 15 with a Schizo-affective disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	SAD16_M_SC - Number of Male patients over 16 with a Schizo-affective disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD16_M_SC', 'Number of Male patients over 16 with a Schizo-affective disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	SAD015_F_SC - Number of Female patients under 15 with a Schizo-affective disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD015_F_SC', 'Number of Female patients under 15 with a Schizo-affective disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	SAD16_F_SC - Number of Female patients over 16 with a Schizo-affective disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SAD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SAD16_F_SC', 'Number of Female patients over 16 with a Schizo-affective disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_schizoaffective_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	MANIC015_M_NC - Number of Male patients under 15 patients with new cases of Mood Affective Disorder (MANIC)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC015_M_NC', 'New Male cases under 15 yo patients with Mood Affective Disorder (MANIC)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	MANIC16_M_NC - Number of Male patients over 16 yo patients with new cases of Mood Affective Disorder (MANIC)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC16_M_NC', 'New Male cases over 16 yo patients with Mood Affective Disorder (MANIC)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	MANIC015_F_NC - Number of Female patients under 15 yo patients with new cases of Mood Affective Disorder (MANIC)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC015_F_NC', 'New Female cases under 15 yo patients with Mood Affective Disorder (MANIC)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	MANIC16_F_NC - Number of Female patients over 16 yo patients with new cases of Mood Affective Disorder (MANIC)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC16_F_NC', 'New Female cases over 16 yo patients with Mood Affective Disorder (MANIC)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	MANIC015_M_SC - Number of Male patients under 15 with a Mood Affective Disorder (MANIC) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC015_M_SC', 'Number of Male patients under 15 with a Mood Affective Disorder (MANIC) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	MANIC16_M_SC - Number of Male patients over 16 with a Mood Affective Disorder (MANIC) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC16_M_SC', 'Number of Male patients over 16 with a Mood Affective Disorder (MANIC) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	MANIC015_F_SC - Number of Female patients under 15 with a Mood Affective Disorder (MANIC) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC015_F_SC', 'Number of Female patients under 15 with a Mood Affective Disorder (MANIC) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	MANIC16_F_SC - Number of Female patients over 16 with a Mood Affective Disorder (MANIC) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MANIC16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MANIC16_F_SC', 'Number of Female patients over 16 with a Mood Affective Disorder (MANIC) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_manic is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	DPS015_M_NC - Number of Male patients under 15 patients with new cases of Mood Affective Disorder (Depression)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS015_M_NC', 'New Male cases under 15 yo patients with Mood Affective Disorder (Depression)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	DPS16_M_NC - Number of Male patients over 16 yo patients with new cases of Mood Affective Disorder (Depression)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS16_M_NC', 'New Male cases over 16 yo patients with Mood Affective Disorder (Depression)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	DPS015_F_NC - Number of Female patients under 15 yo patients with new cases of Mood Affective Disorder (Depression)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS015_F_NC', 'New Female cases under 15 yo patients with Mood Affective Disorder (Depression)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	DPS16_F_NC - Number of Female patients over 16 yo patients with new cases of Mood Affective Disorder (Depression)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS16_F_NC', 'New Female cases over 16 yo patients with Mood Affective Disorder (Depression)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	DPS015_M_SC - Number of Male patients under 15 with a Mood Affective Disorder (Depression) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS015_M_SC', 'Number of Male patients under 15 with a Mood Affective Disorder (Depression) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	DPS16_M_SC - Number of Male patients over 16 with a Mood Affective Disorder (Depression) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS16_M_SC', 'Number of Male patients over 16 with a Mood Affective Disorder (Depression) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	DPS015_F_SC - Number of Female patients under 15 with a Mood Affective Disorder (Depression) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS015_F_SC', 'Number of Female patients under 15 with a Mood Affective Disorder (Depression) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	DPS16_F_SC - Number of Female patients over 16 with a Mood Affective Disorder (Depression) dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DPS16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DPS16_F_SC', 'Number of Female patients over 16 with a Mood Affective Disorder (Depression) dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mood_affective_disorder_depression is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	ANX015_M_NC - Number of Male patients under 15 patients with new cases of Anxiety Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX015_M_NC', 'New Male cases under 15 yo patients with Anxiety Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake < 16
;

/*
	ANX16_M_NC - Number of Male patients over 16 yo patients with new cases of Anxiety Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX16_M_NC', 'New Male cases over 16 yo patients with Anxiety Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	ANX015_F_NC - Number of Female patients under 15 yo patients with new cases of Anxiety Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX015_F_NC', 'New Female cases under 15 yo patients with Anxiety Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	ANX16_F_NC - Number of Female patients over 16 yo patients with new cases of Anxiety Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX16_F_NC', 'New Female cases over 16 yo patients with Anxiety Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	ANX015_M_SC - Number of Male patients under 15 with a Anxiety Disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX015_M_SC', 'Number of Male patients under 15 with a Anxiety Disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	ANX16_M_SC - Number of Male patients over 16 with a Anxiety Disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX16_M_SC', 'Number of Male patients over 16 with a Anxiety Disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	ANX015_F_SC - Number of Female patients under 15 with a Anxiety Disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX015_F_SC', 'Number of Female patients under 15 with a Anxiety Disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	ANX16_F_SC - Number of Female patients over 16 with a Anxiety Disorder dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ANX16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ANX16_F_SC', 'Number of Female patients over 16 with a Anxiety Disorder dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_anxiety_disorder is not null
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit >= 16
;

/*
	EPL015_M_NC - Number of Male patients under 15 patients with new cases of Epilepsy
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL015_M_NC', 'New Male cases under 15 yo patients with Epilepsy', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and epilepsyIntakeVisitDate> @startDate
         and epilepsyIntakeVisitDate <@endDate
         and epilepsyIntakeLocation = @location
         and gender= 'M'
         and ageAtEpilepsyIntake < 16
;

/*
	EPL16_M_NC - Number of Male patients over 16 yo patients with new cases of Epilepsy
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL16_M_NC', 'New Male cases over 16 yo patients with Epilepsy', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and epilepsyIntakeVisitDate> @startDate
         and epilepsyIntakeVisitDate <@endDate
         and epilepsyIntakeLocation = @location
         and gender= 'M'
         and ageAtEpilepsyIntake >= 16
;

/*
	EPL015_F_NC - Number of Female patients under 15 yo patients with new cases of Epilepsy
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL015_F_NC', 'New Female cases under 15 yo patients with Epilepsy', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and epilepsyIntakeVisitDate> @startDate
         and epilepsyIntakeVisitDate <@endDate
         and epilepsyIntakeLocation = @location
         and gender = 'F'
         and ageAtEpilepsyIntake < 16
;

/*
	EPL16_F_NC - Number of Female patients over 16 yo patients with new cases of Epilepsy
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL16_F_NC', 'New Female cases over 16 yo patients with Epilepsy', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and epilepsyIntakeVisitDate> @startDate
         and epilepsyIntakeVisitDate <@endDate
         and epilepsyIntakeLocation = @location
         and gender = 'F'
         and ageAtEpilepsyIntake >= 16
;

/*
	EPL015_M_SC - Number of Male patients under 15 with a Epilepsy dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL015_M_SC', 'Number of Male patients under 15 with a Epilepsy dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and lastEpilepsyVisitDate > @startDate
         and lastEpilepsyVisitDate < @endDate
         and lastEpilepsyVisitLocation = @location
         and gender= 'M'
         and ageAtLastEpilepsyVisit < 16
;

/*
	EPL16_M_SC - Number of Male patients over 16 with a Epilepsy dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL16_M_SC', 'Number of Male patients over 16 with a Epilepsy dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and lastEpilepsyVisitDate > @startDate
         and lastEpilepsyVisitDate < @endDate
         and lastEpilepsyVisitLocation = @location
         and gender= 'M'
         and ageAtLastEpilepsyVisit >= 16
;

/*
	EPL015_F_SC - Number of Female patients under 15 with a Epilepsy dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL015_F_SC', 'Number of Female patients under 15 with a Epilepsy dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and lastEpilepsyVisitDate > @startDate
         and lastEpilepsyVisitDate < @endDate
         and lastEpilepsyVisitLocation = @location
         and gender= 'F'
         and ageAtLastEpilepsyVisit < 16
;

/*
	EPL16_F_SC - Number of Female patients over 16 with a Epilepsy dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'EPL16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'EPL16_F_SC', 'Number of Female patients over 16 with a Epilepsy dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	epilepsyDx is not null
         and lastEpilepsyVisitDate > @startDate
         and  lastEpilepsyVisitDate < @endDate
         and lastEpilepsyVisitLocation = @location
         and gender= 'F'
         and  ageAtLastEpilepsyVisit >= 16
;

/*
	OTH015_M_NC - Number of Male patients under 15 patients with new cases of Other MH dx
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH015_M_NC', 'New Male cases under 15 yo patients with Other MH dx ', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	OTH16_M_NC - Number of Male patients over 16 yo patients with new cases of Other MH dx
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH16_M_NC', 'New Male cases over 16 yo patients with Other MH dx ', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and  ageAtMHIntake >= 16
;

/*
	OTH015_F_NC - Number of Female patients under 15 yo patients with new cases of Other MH dx
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH015_F_NC', 'New Female cases under 15 yo patients with Other MH dx ', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake < 16
;

/*
	OTH16_F_NC - Number of Female patients over 16 yo patients with new cases of Other MH dx
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH16_F_NC', 'New Female cases over 16 yo patients with Other MH dx ', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and mhIntakeVisitDate> @startDate
         and  mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'F'
         and  ageAtMHIntake >= 16
;

/*
	OTH015_M_SC - Number of Male patients under 15 with a Other MH dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH015_M_SC', 'Number of Male patients under 15 with a Other MH dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit < 16
;

/*
	OTH16_M_SC - Number of Male patients over 16 with a Other MH dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH16_M_SC', 'Number of Male patients over 16 with a Other MH dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and  ageAtLastMHVisit >= 16
;

/*
	OTH015_F_SC - Number of Female patients under 15 with a Other MH dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH015_F_SC', 'Number of Female patients under 15 with a Other MH dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and lastMHVisitDate > @startDate
         and  lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and  ageAtLastMHVisit < 16
;

/*
	OTH16_F_SC - Number of Female patients over 16 with a Other MH dx  dx
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'OTH16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'OTH16_F_SC', 'Number of Female patients over 16 with a Other MH dx who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	(dx_mh_other_1 is not null or dx_mh_other_2 is not null or dx_mh_other_3 is not null)
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

select * from rpt_mh_indicators;
