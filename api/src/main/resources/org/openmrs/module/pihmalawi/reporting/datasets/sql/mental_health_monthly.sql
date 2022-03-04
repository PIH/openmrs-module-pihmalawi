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

select * from rpt_mh_indicators;
