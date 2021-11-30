SET @defaultCutOff = 60;

call create_last_art_outcome_at_facility(@endDate,@location);

select opi.identifier,mwp.gender,mwp.birthdate as dob,
CASE WHEN (map.weight is null or map.weight = "")  THEN (SELECT weight FROM mw_art_followup where patient_id = map.patient_id  and weight is not null order by patient_id DESC limit 1) ELSE map.weight END as weight,
 map.art_regimen, map.arvs_given, map.visit_date as dispense_date
    from mw_art_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as last_appt_date from mw_art_followup where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
join mw_patient mwp
on mwp.patient_id = map.patient_id
 join omrs_patient_identifier opi
on mwp.patient_id = opi.patient_id and opi.location = @location and opi.type = "ARV number"
where map.patient_id in (select pat from last_facility_outcome where state = "On antiretrovirals")
and floor(datediff(@endDate,map.next_appointment_date)) <=  @defaultCutOff;