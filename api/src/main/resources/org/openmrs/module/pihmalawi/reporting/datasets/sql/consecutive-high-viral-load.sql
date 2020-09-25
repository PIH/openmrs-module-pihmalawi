/************************************************************************

  Consecutive High Viral Load Report Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @endDate = now();
  -- set @viralLoad = 1000;

*************************************************************************/

# Need to include start date since this is not a user specified parameter
set @startDate = "1900-01-01";

# create a temporary lookup table with regimen information
drop table if exists regimenStartTable;
create temporary table regimenStartTable as
select patient_id, value_coded as regimen, obs_date as start_date from (select * 
	from omrs_obs 
	where concept like 'Malawi Antiretroviral drugs%'
	and obs_date <= @endDate
	order by obs_date desc) i
group by patient_id, value_coded;

CREATE INDEX idx ON regimenStartTable (patient_id);

# duplicate this table
# need to do this because otherwise the main query will fail trying to open regimenStartTable twice in the main query
drop table if exists regimenStartTable2;
create temporary table regimenStartTable2 as
select * from regimenStartTable; 

CREATE INDEX idx ON regimenStartTable2 (patient_id);

# main query 
SELECT mlt.patient_id - 10000000 as pid, 
	   mp.identifier as id,
	   mp.first_name as first,
	   mp.last_name as last,
	   mp.gender,
	   floor(datediff(@endDate,mp.birthdate)/365.25) as age,
	   visit_date as lastVisitDate,
	   mav.location as lastVisitLocation,
	   next_appointment_date as nextApptDate,
	   get_regimen(mlt.patient_id, 0) as currentRegimen,	
	   get_regimen_start(mlt.patient_id, 0) as currentRegimenStartDate,
	   get_regimen(mlt.patient_id, 1) as previousRegimen,
	   get_regimen_start(mlt.patient_id, 1) as previousRegimenStartDate,	
	   mp.traditional_authority as ta,
	   mp.village,
	   mp.chw,
	   result_numeric as lvl,
	   date_collected as lvldate,
	   previous as pvl,
	   previous_date as pvldate
FROM mw_lab_tests mlt
JOIN (select patient_id as pid, result_numeric as previous, date_collected as previous_date
	  from mw_lab_tests
	  where lab_test_id = latest_test_result_by_sample_date(patient_id, "Viral Load", @startDate, @endDate, 1)) mlt2
	ON mlt2.pid = mlt.patient_id
JOIN (select patient_id, visit_date, location, next_appointment_date
	  from (select * from mw_art_visits where visit_date < @endDate order by visit_date desc) mavi
	  group by patient_id) mav
	ON mav.patient_id = mlt.patient_id
JOIN mw_art_register mar on mar.patient_id = mlt.patient_id
JOIN mw_patient mp on mp.patient_id = mlt.patient_id
JOIN (select * from
		(select patient_id, value_coded as regimen from omrs_obs where concept like 'Malawi Antiretroviral drugs%' and obs_date <= @endDate order by obs_date desc) oi
	  group by patient_id) mo
	  ON mo.patient_id = mlt.patient_id
WHERE lab_test_id = latest_test_result_by_sample_date(mlt.patient_id, "Viral Load", @startDate, @endDate, 0)
AND (outcome = "On antiretrovirals" OR outcome is NULL)
HAVING result_numeric > @viralLoad
AND previous > @viralLoad
ORDER BY mlt.date_collected desc;

# clean up
DROP TABLE IF EXISTS regimenStartTable;
DROP TABLE IF EXISTS regimenStartTable2;