-- ## report_uuid = 5d445530-63c5-11e5-a9f6-d60697e5b5db
-- ## design_uuid = d69619a5-faf9-48cf-80e4-57ad1e7c047c
-- ## report_name = EID HIV Testing Report
-- ## report_description = Report indicating EID patients in need of HIV testing
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = location|Location|org.openmrs.Location

-- Get initial patient data set
create or replace view temp_obs_enc_initial as select patient_id, encounter.encounter_id, encounter_type, encounter_datetime, encounter.date_created, obs_group_id, concept_id, value_coded, value_datetime, value_text, value_numeric from encounter join obs on obs.encounter_id = encounter.encounter_id where obs.voided = 0 and encounter.voided=0 and (encounter_type = 20 or encounter_type = 92);

-- Get hiv obs to join to
create or replace view temp_hiv_test_group as select obs_group_id, value_datetime from obs where obs.voided = 0 and concept_id = 6108;

-- Set report columns
select PS.patient_id as IID,
identifier as PID, 
given_name as First, 
family_name as Last,
date_format(Last_Encounter,"%Y-%m-%d") as Last_Encounter,
location.name as Enrollment_Location,
date_format(birthdate,"%Y-%m-%d") as Birthday,
round(datediff(@endDate,birthdate)/30,1) as month_age,
-- Define test needed
case when (datediff(@endDate,birthdate) is not null) then
	if(datediff(@endDate,birthdate)>=730,"Two Year Rapid",if(datediff(@endDate,birthdate)>=365,"One Year Rapid",if(datediff(@endDate,birthdate)>=42,"Enrollment PCR","None")))
end as 'Status',
-- Get appropriate test date obs
case 
	when (datediff(@endDate,birthdate) >=  730 ) then
		case when (datediff(obs1.value_datetime,birthdate) >= 730) then
			ifnull(date_format(obs1.value_datetime,"%Y-%m-%d"),"No Record")
		else
			"No Record"
		end
	when (datediff(@endDate,birthdate) >=  365 ) then
		case when (datediff(obs1.value_datetime,birthdate) >= 365) then
			ifnull(date_format(obs1.value_datetime,"%Y-%m-%d"),"No Record")
		else
			"No Record"
		end
	when (datediff(@endDate,birthdate) >=  42 ) then
		case when (datediff(obs1.value_datetime,birthdate) >= 42) then
			ifnull(date_format(obs1.value_datetime,"%Y-%m-%d"),"No Record")
		else
			"No Record"
		end
	else 
		"N/A"
end as "Test Date",
-- Get appropriate test result obs
case 
	when (datediff(@endDate,birthdate) >=  730 ) then
		case when (datediff(obs1.value_datetime,birthdate) >= 730) then
			ifnull(if(obs2.value_coded=664,"Neg",if(obs2.value_coded=703,"Pos",if(obs2.value_coded=1138,"Inc","No Record"))),"No Record")
		else
			"No Record"
		end
	when (datediff(@endDate,birthdate) >=  365 ) then
		case when (datediff(obs1.value_datetime,birthdate) >= 365) then
			ifnull(if(obs2.value_coded=664,"Neg",if(obs2.value_coded=703,"Pos",if(obs2.value_coded=1138,"Inc","No Record"))),"No Record")
		else
			"No Record"
		end
	when (datediff(@endDate,birthdate) >=  42 ) then
		case when (datediff(obs1.value_datetime,birthdate) >= 42) then
			ifnull(if(obs2.value_coded=664,"Neg",if(obs2.value_coded=703,"Pos",if(obs2.value_coded=1138,"Inc","No Record"))),"No Record")
		else
			"No Record"
		end
	else 
		"N/A"
end as "Test Result"

-- Ensure patient is enrolled
from 
	(select * from 
	(select state, end_date, patient_state.voided, patient_id, patient_program.date_created, patient_state.patient_program_id, patient_program.location_id
	from patient_state
	join patient_program on patient_state.patient_program_id = patient_program.patient_program_id
	where (end_date > @endDate or end_date IS NULL) 
	and patient_program.program_id = 1
	and patient_program.voided = 0
	and patient_program.location_id = @location
	and (patient_program.date_completed > @endDate or patient_program.date_completed is NULL)
	and state=120
	and patient_state.start_date < @endDate
	and  (patient_state.end_date > @endDate or patient_state.end_date is NULL)
	and patient_state.voided = 0
	order by patient_program.date_created desc) PSi 
	group by patient_id) PS
	
	-- Get initial encounter information 
join (select * from (select * from encounter where voided = 0 and (encounter_type = 20 or encounter_type = 92) order by encounter.encounter_datetime desc) ehi group by patient_id) encounter_initial on encounter_initial.patient_id =PS.patient_id	

-- Get identifier
left join (select * from (select patient_identifier.patient_id, identifier from patient_identifier where identifier_type in (13,19) and location_id = @location and voided=0 order by patient_identifier.identifier_type desc) pii group by patient_id) PI on PI.patient_id = PS.patient_id

-- Get birthdate
join (select voided, person.person_id, person.birthdate from person where person.voided=0) pii on pii.person_id = PS.patient_id

-- Get name
join (select * from (select person_name.family_name, person_name.given_name, person_name.person_id from person_name where person_name.voided = 0 order by person_name.person_id) pnii group by	pnii.person_id) PN on PN.person_id = PS.patient_id

-- Get most recent encounter	
join 
	(select * from (select patient_id, encounter_datetime as Last_Encounter, location_id as ENC_loc
	from encounter 
	where encounter_type in (92,93) and encounter_datetime <@endDate and encounter_datetime > date_sub(@endDate,interval 1 year) and voided = 0 order by encounter_datetime desc) ENCi
	group by patient_id) ENC 
	on PS.patient_id = ENC.patient_id 

-- Get location
join location on location.location_id = PS.location_id

-- Get most recent observation before end date
left join 
	(select * from 
		(select temp_obs_enc_initial.value_datetime, patient_id 
			from temp_obs_enc_initial join temp_hiv_test_group 
			on temp_hiv_test_group.obs_group_id = temp_obs_enc_initial.obs_group_id 
			where concept_id = 6108 
			and temp_hiv_test_group.value_datetime < @endDate
			order by temp_hiv_test_group.value_datetime desc) obs1i 
		group by patient_id) obs1 on encounter_initial.patient_id = obs1.patient_id 
left join 
	(select * from 
		(select temp_obs_enc_initial.value_coded, patient_id 
		from temp_obs_enc_initial join temp_hiv_test_group 
		on temp_hiv_test_group.obs_group_id = temp_obs_enc_initial.obs_group_id 
		where concept_id = 2169 
		and temp_hiv_test_group.value_datetime < @endDate
		order by temp_hiv_test_group.value_datetime desc) obs2i 
	group by patient_id) obs2 on encounter_initial.patient_id = obs2.patient_id 
 	