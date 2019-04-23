/************************************************************************

  Consecutive High Viral Load Report Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @endDate = now();
  -- set @viralLoad = 1000;

*************************************************************************/


set @startDate = "1900-01-01";

SELECT mlt.patient_id - 10000000 as pid, 
	   mp.identifier as id,
	   mp.first_name as first,
	   mp.last_name as last,
	   mp.gender,
	   floor(datediff(@endDate,mp.birthdate)/365.25) as age,	   
	   mp.traditional_authority as ta,
	   mp.village,
	   mp.vhw,	   
	   result_numeric as lvl, 
	   date_collected as lvldate, 
	   previous as pvl,  
	   previous_date as pvldate
FROM mw_lab_tests mlt
JOIN (select patient_id as pid, result_numeric as previous, date_collected as previous_date 
	  from mw_lab_tests 
	  where lab_test_id = latest_test_result_by_date_entered(patient_id, "Viral Load", @startDate, @endDate, 1)) mlt2 
ON mlt2.pid = mlt.patient_id
JOIN mw_patient mp on mp.patient_id = mlt.patient_id
WHERE lab_test_id = latest_test_result_by_date_entered(mlt.patient_id, "Viral Load", @startDate, @endDate, 0)
HAVING result_numeric > @viralLoad
AND previous > @viralLoad
ORDER BY mlt.date_collected desc;