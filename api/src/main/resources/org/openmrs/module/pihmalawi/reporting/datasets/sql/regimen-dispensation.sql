SET @defaultCutOff = 60;

call create_last_art_outcome_at_facility(@endDate,@location);

select opi.identifier,
CASE WHEN mwp.gender="F" then 'Female' else 'Male' end as gender,
mwp.birthdate as dob, mai.visit_date as art_start_date,
CASE WHEN (map.weight is null or map.weight = "")  THEN (SELECT weight FROM mw_art_followup where patient_id = map.patient_id
and weight is not null order by patient_id DESC limit 1) ELSE map.weight END as weight,
 substring_index(map.art_regimen,':',1) as art_regimen,
 concat(substring_index(map.art_regimen,':',-1), '(' , map.arvs_given, ')' ) as arvs_given, map.visit_date as dispense_date, opi.location,
CASE
WHEN ldl IS NOT NULL THEN 'LDL'
WHEN other_results IS NOT NULL THEN other_results
WHEN viral_load_result IS NOT NULL THEN viral_load_result
WHEN less_than_limit IS NOT NULL THEN less_than_limit
ELSE NULL
END as vl_result,
 date_of_vl_result
    from mw_art_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as last_appt_date
    from mw_art_followup where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
join mw_patient mwp
on mwp.patient_id = map.patient_id
join mw_art_initial mai
on mwp.patient_id = mai.patient_id
 join omrs_patient_identifier opi
on mwp.patient_id = opi.patient_id and opi.location = @location and opi.type = "ARV number"
left join
(
select avl1.patient_id, visit_date as date_of_vl_result, viral_load_result,less_than_limit, ldl,other_results
from mw_art_viral_load avl1
join
(
	select patient_id,MAX(visit_date) as date_of_vl_result
    from mw_art_viral_load where visit_date <= @endDate
	group by patient_id
	) avl2
ON avl1.patient_id = avl2.patient_id and avl1.visit_date = avl2.date_of_vl_result
) avl
on map.patient_id = avl.patient_id
where map.patient_id in (select pat from last_facility_outcome where state = "On antiretrovirals")
and floor(datediff(@endDate,map.next_appointment_date)) <=  @defaultCutOff;