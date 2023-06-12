/*

  Mental Health Monthly Indicators Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  * set @endDate = now();
  * set @startDate = DATE_ADD(@endDate,INTERVAL -1 MONTH)
  * set @location = 'Neno District Hospital'

*/


SET @dayOfEndDate = DAY(@endDate);
SET @startDate =
  (SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 1 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 1 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 1 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 1 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);

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

/*add MH new diagnoses */
#Somatoform Disorder
/*
	SMD015_M_NC - Number of Male patients under 15 patients with new cases of Somatoform Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD015_M_NC', 'New Male cases under 15 yo patients with Somatoform Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_somatoform_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	SMD16_M_NC - Number of Male patients over 16 yo patients with new cases of Somatoform Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD16_M_NC', 'New Male cases over 16 yo patients with Somatoform Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_somatoform_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	SMD015_F_NC - Number of Female patients under 15 yo patients with new cases of Somatoform Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD015_F_NC', 'New Female cases under 15 yo patients with Somatoform Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_somatoform_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	SMD16_F_NC - Number of Female patients over 16 yo patients with new cases of Somatoform Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD16_F_NC', 'New Female cases over 16 yo patients with Somatoform Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_somatoform_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	SMD015_M_SC - Number of Male patients under 15 with a Somatoform Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD015_M_SC', 'Number of Male patients under 15 with a Somatoform Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_somatoform_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	SMD16_M_SC - Number of Male patients over 16 with a Somatoform Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD16_M_SC', 'Number of Male patients over 16 with a Somatoform Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_somatoform_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	SMD015_F_SC - Number of Female patients under 15 with a Somatoform Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD015_F_SC', 'Number of Female patients under 15 with a Somatoform Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_somatoform_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	SMD16_F_SC - Number of Female patients over 16 with a Somatoform Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SMD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SMD16_F_SC', 'Number of Female patients over 16 with a Somatoform Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_somatoform_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Personality/Behavioural Disorder
/*
	PBD015_M_NC - Number of Male patients under 15 patients with new cases of Personality Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD015_M_NC', 'New Male cases under 15 yo patients with Personality Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_personality_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	PBD16_M_NC - Number of Male patients over 16 yo patients with new cases of Personality Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD16_M_NC', 'New Male cases over 16 yo patients with Personality Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_personality_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	PBD015_F_NC - Number of Female patients under 15 yo patients with new cases of Personality Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD015_F_NC', 'New Female cases under 15 yo patients with Personality Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_personality_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	PBD16_F_NC - Number of Female patients over 16 yo patients with new cases of Personality Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD16_F_NC', 'New Female cases over 16 yo patients with Personality Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_personality_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	PBD015_M_SC - Number of Male patients under 15 with a Personality Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD015_M_SC', 'Number of Male patients under 15 with a Personality Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_personality_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	PBD16_M_SC - Number of Male patients over 16 with a Personality Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD16_M_SC', 'Number of Male patients over 16 with a Personality Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_personality_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	PBD015_F_SC - Number of Female patients under 15 with a Personality Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD015_F_SC', 'Number of Female patients under 15 with a Personality Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_personality_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	PBD16_F_SC - Number of Female patients over 16 with a Personality Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PBD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PBD16_F_SC', 'Number of Female patients over 16 with a Personality Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_personality_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Mental Retardation Disorder
/*
	MRD015_M_NC - Number of Male patients under 15 patients with new cases of Mental Retardation Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD015_M_NC', 'New Male cases under 15 yo patients with Mental Retardation Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_mental_retardation_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	MRD16_M_NC - Number of Male patients over 16 yo patients with new cases of Mental Retardation Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD16_M_NC', 'New Male cases over 16 yo patients with Mental Retardation Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mental_retardation_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	MRD015_F_NC - Number of Female patients under 15 yo patients with new cases of Mental Retardation Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD015_F_NC', 'New Female cases under 15 yo patients with Mental Retardation Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_mental_retardation_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	MRD16_F_NC - Number of Female patients over 16 yo patients with new cases of Mental Retardation Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD16_F_NC', 'New Female cases over 16 yo patients with Mental Retardation Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mental_retardation_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	MRD015_M_SC - Number of Male patients under 15 with a Mental Retardation Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD015_M_SC', 'Number of Male patients under 15 with a Mental Retardation Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mental_retardation_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	MRD16_M_SC - Number of Male patients over 16 with a Mental Retardation Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD16_M_SC', 'Number of Male patients over 16 with a Mental Retardation Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mental_retardation_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	MRD015_F_SC - Number of Female patients under 15 with a Mental Retardation Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD015_F_SC', 'Number of Female patients under 15 with a Mental Retardation Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mental_retardation_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	MRD16_F_SC - Number of Female patients over 16 with a Mental Retardation Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'MRD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'MRD16_F_SC', 'Number of Female patients over 16 with a Mental Retardation Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_mental_retardation_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Psychological Developmental Disorder
/*
	PDD015_M_NC - Number of Male patients under 15 patients with new cases of Psychological Developmental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD015_M_NC', 'New Male cases under 15 yo patients with Psychological Developmental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_psych_development_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	PDD16_M_NC - Number of Male patients over 16 yo patients with new cases of Psychological Developmental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD16_M_NC', 'New Male cases over 16 yo patients with Psychological Developmental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_psych_development_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	PDD015_F_NC - Number of Female patients under 15 yo patients with new cases of Psychological Developmental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD015_F_NC', 'New Female cases under 15 yo patients with Psychological Developmental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_psych_development_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	PDD16_F_NC - Number of Female patients over 16 yo patients with new cases of Psychological Developmental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD16_F_NC', 'New Female cases over 16 yo patients with Psychological Developmental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_psych_development_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	PDD015_M_SC - Number of Male patients under 15 with a Psychological Developmental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD015_M_SC', 'Number of Male patients under 15 with a Psychological Developmental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_psych_development_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	PDD16_M_SC - Number of Male patients over 16 with a Psychological Developmental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD16_M_SC', 'Number of Male patients over 16 with a Psychological Developmental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_psych_development_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	PDD015_F_SC - Number of Female patients under 15 with a Psychological Developmental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD015_F_SC', 'Number of Female patients under 15 with a Psychological Developmental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_psych_development_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	PDD16_F_SC - Number of Female patients over 16 with a Psychological Developmental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PDD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PDD16_F_SC', 'Number of Female patients over 16 with a Psychological Developmental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_psych_development_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Stress Reaction Adjustment Disorder
/*
	SRAD015_M_NC - Number of Male patients under 15 patients with new cases of Stress Reaction Adjustment Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD015_M_NC', 'New Male cases under 15 yo patients with Stress Reaction Adjustment Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_stress_reactive_adjustment_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	SRAD16_M_NC - Number of Male patients over 16 yo patients with new cases of Stress Reaction Adjustment Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD16_M_NC', 'New Male cases over 16 yo patients with Stress Reaction Adjustment Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_stress_reactive_adjustment_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	SRAD015_F_NC - Number of Female patients under 15 yo patients with new cases of Stress Reaction Adjustment Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD015_F_NC', 'New Female cases under 15 yo patients with Stress Reaction Adjustment Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_stress_reactive_adjustment_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	SRAD16_F_NC - Number of Female patients over 16 yo patients with new cases of Stress Reaction Adjustment Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD16_F_NC', 'New Female cases over 16 yo patients with Stress Reaction Adjustment Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_stress_reactive_adjustment_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	SRAD015_M_SC - Number of Male patients under 15 with a Stress Reaction Adjustment Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD015_M_SC', 'Number of Male patients under 15 with a Stress Reaction Adjustment Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_stress_reactive_adjustment_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	SRAD16_M_SC - Number of Male patients over 16 with a Stress Reaction Adjustment Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD16_M_SC', 'Number of Male patients over 16 with a Stress Reaction Adjustment Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_stress_reactive_adjustment_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	SRAD015_F_SC - Number of Female patients under 15 with a Stress Reaction Adjustment Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD015_F_SC', 'Number of Female patients under 15 with a Stress Reaction Adjustment Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_stress_reactive_adjustment_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	SRAD16_F_SC - Number of Female patients over 16 with a Stress Reaction Adjustment Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'SRAD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'SRAD16_F_SC', 'Number of Female patients over 16 with a Stress Reaction Adjustment Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_stress_reactive_adjustment_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Puerperal Mental Disorder
/*
	PMD015_M_NC - Number of Male patients under 15 patients with new cases of Puerperal Mental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD015_M_NC', 'New Male cases under 15 yo patients with Puerperal Mental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_puerperal_mental_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	PMD16_M_NC - Number of Male patients over 16 yo patients with new cases of Puerperal Mental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD16_M_NC', 'New Male cases over 16 yo patients with Puerperal Mental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_puerperal_mental_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	PMD015_F_NC - Number of Female patients under 15 yo patients with new cases of Puerperal Mental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD015_F_NC', 'New Female cases under 15 yo patients with Puerperal Mental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_puerperal_mental_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	PMD16_F_NC - Number of Female patients over 16 yo patients with new cases of Puerperal Mental Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD16_F_NC', 'New Female cases over 16 yo patients with Puerperal Mental Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_puerperal_mental_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	PMD015_M_SC - Number of Male patients under 15 with a Puerperal Mental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD015_M_SC', 'Number of Male patients under 15 with a Puerperal Mental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_puerperal_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	PMD16_M_SC - Number of Male patients over 16 with a Puerperal Mental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD16_M_SC', 'Number of Male patients over 16 with a Puerperal Mental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_puerperal_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	PMD015_F_SC - Number of Female patients under 15 with a Puerperal Mental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD015_F_SC', 'Number of Female patients under 15 with a Puerperal Mental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_puerperal_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	PMD16_F_SC - Number of Female patients over 16 with a Puerperal Mental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'PMD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'PMD16_F_SC', 'Number of Female patients over 16 with a Puerperal Mental Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_puerperal_mental_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Mood Affective Disorder (Bipolar)
/*
	BPMAD015_M_NC - Number of Male patients under 15 patients with new cases of Mood Affective Disorder (Bipolar)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD015_M_NC', 'New Male cases under 15 yo patients with Mood Affective Disorder (Bipolar)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_bipolar_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	BPMAD16_M_NC - Number of Male patients over 16 yo patients with new cases of Mood Affective Disorder (Bipolar)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD16_M_NC', 'New Male cases over 16 yo patients with Mood Affective Disorder (Bipolar)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_bipolar_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	BPMAD015_F_NC - Number of Female patients under 15 yo patients with new cases of Mood Affective Disorder (Bipolar)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD015_F_NC', 'New Female cases under 15 yo patients with Mood Affective Disorder (Bipolar)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_bipolar_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	BPMAD16_F_NC - Number of Female patients over 16 yo patients with new cases of Mood Affective Disorder (Bipolar)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD16_F_NC', 'New Female cases over 16 yo patients with Mood Affective Disorder (Bipolar)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_bipolar_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	BPMAD015_M_SC - Number of Male patients under 15 with a Mood Affective Disorder (Bipolar)
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD015_M_SC', 'Number of Male patients under 15 with a Mood Affective Disorder (Bipolar) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_bipolar_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	BPMAD16_M_SC - Number of Male patients over 16 with a Mood Affective Disorder (Bipolar)
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD16_M_SC', 'Number of Male patients over 16 with a Mood Affective Disorder (Bipolar) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_bipolar_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	BPMAD015_F_SC - Number of Female patients under 15 with a Puerperal Mental Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD015_F_SC', 'Number of Female patients under 15 with a Mood Affective Disorder (Bipolar) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_bipolar_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	BPMAD16_F_SC - Number of Female patients over 16 with a Mood Affective Disorder (Bipolar)
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'BPMAD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'BPMAD16_F_SC', 'Number of Female patients over 16 with a Mood Affective Disorder (Bipolar) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_bipolar_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Dissociative Conversion Disorder
/*
	DCD015_M_NC - Number of Male patients under 15 patients with new cases of Dissociative Conversion Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD015_M_NC', 'New Male cases under 15 yo patients with Dissociative Conversion Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_dissociative_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	DCD16_M_NC - Number of Male patients over 16 yo patients with new cases of Dissociative Conversion Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD16_M_NC', 'New Male cases over 16 yo patients with Dissociative Conversion Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_dissociative_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	DCD015_F_NC - Number of Female patients under 15 yo patients with new cases of Dissociative Conversion Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD015_F_NC', 'New Female cases under 15 yo patients with Dissociative Conversion Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_dissociative_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	DCD16_F_NC - Number of Female patients over 16 yo patients with new cases of Dissociative Conversion Disorder
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD16_F_NC', 'New Female cases over 16 yo patients with Dissociative Conversion Disorder', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_dissociative_mood_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	DCD015_M_SC - Number of Male patients under 15 with a Dissociative Conversion Dissociative Conversion Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD015_M_SC', 'Number of Male patients under 15 with a Dissociative Conversion Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_dissociative_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	DCD16_M_SC - Number of Male patients over 16 with a Dissociative Conversion Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD16_M_SC', 'Number of Male patients over 16 with a Dissociative Conversion Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_dissociative_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	DCD015_F_SC - Number of Female patients under 15 with a Dissociative Conversion Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD015_F_SC', 'Number of Female patients under 15 with a Dissociative Conversion Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_dissociative_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	DCD16_F_SC - Number of Female patients over 16 with a Dissociative Conversion Disorder
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'DCD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'DCD16_F_SC', 'Number of Female patients over 16 with a Dissociative Conversion Disorder who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_dissociative_mood_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

#Hyperkinetic Conductal Disorder  (ADHD)
/*
	ADHD015_M_NC - Number of Male patients under 15 patients with new cases of Hyperkinetic Conductal Disorder  (ADHD)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD015_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD015_M_NC', 'New Male cases under 15 yo patients with Hyperkinetic Conductal Disorder  (ADHD)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE  dx_hyperkinetic_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake < 16
;

/*
	ADHD16_M_NC - Number of Male patients over 16 yo patients with new cases of Hyperkinetic Conductal Disorder  (ADHD)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD16_M_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD16_M_NC', 'New Male cases over 16 yo patients with Hyperkinetic Conductal Disorder  (ADHD)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_hyperkinetic_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender= 'M'
         and ageAtMHIntake >= 16
;

/*
	ADHD015_F_NC - Number of Female patients under 15 yo patients with new cases of Hyperkinetic Conductal Disorder  (ADHD)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD015_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD015_F_NC', 'New Female cases under 15 yo patients with Hyperkinetic Conductal Disorder  (ADHD)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	 dx_hyperkinetic_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake < 16
;

/*
	ADHD16_F_NC - Number of Female patients over 16 yo patients with new cases of Hyperkinetic Conductal Disorder  (ADHD)
	at the facility on report end date
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD16_F_NC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD16_F_NC', 'New Female cases over 16 yo patients with Hyperkinetic Conductal Disorder  (ADHD)', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_hyperkinetic_disorder is not null
		 and mhIntakeVisitDate> @startDate
         and mhIntakeVisitDate <@endDate
         and mhIntakeLocation = @location
         and gender = 'F'
         and ageAtMHIntake >= 16
;

/*
	ADHD015_M_SC - Number of Male patients under 15 with a Dissociative Hyperkinetic Conductal Disorder  (ADHD)
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD015_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD015_M_SC', 'Number of Male patients under 15 with a Hyperkinetic Conductal Disorder  (ADHD) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_hyperkinetic_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit < 16
;

/*
	ADHD16_M_SC - Number of Male patients over 16 with a Hyperkinetic Conductal Disorder  (ADHD)
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD16_M_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD16_M_SC', 'Number of Male patients over 16 with a Hyperkinetic Conductal Disorder  (ADHD) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_hyperkinetic_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'M'
         and ageAtLastMHVisit >= 16
;

/*
	ADHD015_F_SC - Number of Female patients under 15 with a Hyperkinetic Conductal Disorder  (ADHD)
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD015_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD015_F_SC', 'Number of Female patients under 15 with a Hyperkinetic Conductal Disorder  (ADHD) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_hyperkinetic_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit < 16
;

/*
	ADHD16_F_SC - Number of Female patients over 16 with a Hyperkinetic Conductal Disorder  (ADHD)
	who had a visit (subsequent case) at the facility within the reporting period
*/
DELETE from rpt_mh_indicators WHERE indicator = 'ADHD16_F_SC';
INSERT INTO rpt_mh_indicators
(indicator, description, indicator_type, indicator_value)
  SELECT 'ADHD16_F_SC', 'Number of Female patients over 16 with a Hyperkinetic Conductal Disorder  (ADHD) who had a visit', 'At date', count(*)
  FROM 	rpt_mh_data_table
  WHERE 	dx_hyperkinetic_disorder is not null
         and lastMHVisitDate > @startDate
         and lastMHVisitDate < @endDate
         and visitLocation = @location
         and gender= 'F'
         and ageAtLastMHVisit >= 16
;

select * from rpt_mh_indicators;
