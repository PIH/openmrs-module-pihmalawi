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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtIntake < 16
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
         and  ageAtIntake >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
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
         and  ageAtLastVisit < 16
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
         and  ageAtLastVisit >= 16
;

select * from rpt_mh_indicators;
