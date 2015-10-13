-- ## report_uuid = 5d445530-63c5-11e5-a9f6-d60697e5b5db
-- ## design_uuid = e15c1a59-5d50-11e5-a151-e82aea237783
-- ## report_name = EID HIV Testing Report
-- ## report_description = Report indicating EID patients in need of HIV testing
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = location|Location|org.openmrs.Location

create or replace view temp_obs_enc_initial as select patient_id, encounter.encounter_id, encounter_type, encounter_datetime, encounter.date_created, concept_id, value_coded, value_datetime, value_text, value_numeric from encounter join obs on obs.encounter_id = encounter.encounter_id where obs.voided = 0 and encounter.voided=0 and (encounter_type = 20 or encounter_type = 92);

select PS.patient_id as IID,
identifier as PID, 
given_name as First, 
family_name as Last,
date_format(Last_Encounter,"%Y-%m-%d") as Last_Encounter,
location.name as Enrollment_Location,
date_format(birthdate,"%Y-%m-%d") as Birthday,
round(datediff(curdate(),birthdate)/30,1) as month_age,
case when (datediff(curdate(),birthdate) is not null) then
	if(datediff(curdate(),birthdate)>730,"Two Year Rapid",if(datediff(curdate(),birthdate)>365,"One Year Rapid",if(datediff(curdate(),birthdate)>42,"Enrollment PCR","None")))
end as 'Status',
case when (datediff(curdate(),birthdate) is not null) then
	if(datediff(curdate(),birthdate)>730,ifnull(date_format(obs5.value_datetime,"%Y-%m-%d"),"No Record"),if(datediff(curdate(),birthdate)>365,ifnull(date_format(obs3.value_datetime,"%Y-%m-%d"),"No Record"),if(datediff(curdate(),birthdate)>42,ifnull(date_format(obs1.value_datetime,"%Y-%m-%d"),"No Record"),"N/A")))
end as 'Test_date',
case when (datediff(curdate(),birthdate) is not null) then
	if(datediff(curdate(),birthdate)>730,
	ifnull(if(obs6.value_coded=664,"Neg",if(obs6.value_coded=703,"Pos",if(obs6.value_coded=1138,"Inc","No Record"))),"No Record"),
	if(datediff(curdate(),birthdate)>365,
	ifnull(if(obs4.value_coded=664,"Neg",if(obs4.value_coded=703,"Pos",if(obs4.value_coded=1138,"Inc","No Record"))),"No Record"),
	if(datediff(curdate(),birthdate)>42,
	ifnull(if(obs2.value_coded=664,"Neg",if(obs2.value_coded=703,"Pos",if(obs2.value_coded=1138,"Inc","No Record"))),"No Record"),"N/A")))
	else "No Record"
end as 'Test_result'

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
	
join (select * from (select * from encounter where voided = 0 and (encounter_type = 20 or encounter_type = 92) order by encounter.encounter_datetime desc) ehi group by patient_id) encounter_initial on encounter_initial.patient_id =PS.patient_id	

left join (select * from (select patient_identifier.patient_id, identifier from patient_identifier where identifier_type in (13,19) and voided=0 order by patient_identifier.identifier_type desc) pii group by patient_id) PI on PI.patient_id = PS.patient_id

join (select voided, person.person_id, person.birthdate from person where person.voided=0) pii on pii.person_id = PS.patient_id
	
join (select * from (select person_name.family_name, person_name.given_name, person_name.person_id from person_name where person_name.voided = 0 order by person_name.person_id) pnii group by	pnii.person_id) PN on PN.person_id = PS.patient_id
	
join 
	(select * from (select patient_id, encounter_datetime as Last_Encounter, location_id as ENC_loc
	from encounter 
	where encounter_type in (92,93) and encounter_datetime <@endDate and encounter_datetime > date_sub(@endDate,interval 1 year) and voided = 0 order by encounter_datetime desc) ENCi
	group by patient_id) ENC 
	on PS.patient_id = ENC.patient_id -- Patients with P-ART Initial or followup between startDate and endDate	
	
join location on location.location_id = PS.location_id

left join (select * from (select * from temp_obs_enc_initial where concept_id = 8047 order by encounter_datetime desc) obs1i group by patient_id) obs1 on encounter_initial.patient_id = obs1.patient_id 
left join (select * from (select * from temp_obs_enc_initial where concept_id = 8056 order by encounter_datetime desc) obs2i group by patient_id) obs2 on encounter_initial.patient_id = obs2.patient_id 
left join (select * from (select * from temp_obs_enc_initial where concept_id = 8069 order by encounter_datetime desc) obs3i group by patient_id) obs3 on encounter_initial.patient_id = obs3.patient_id 
left join (select * from (select * from temp_obs_enc_initial where concept_id = 8078 order by encounter_datetime desc) obs4i group by patient_id) obs4 on encounter_initial.patient_id = obs4.patient_id 
left join (select * from (select * from temp_obs_enc_initial where concept_id = 8070 order by encounter_datetime desc) obs5i group by patient_id) obs5 on encounter_initial.patient_id = obs5.patient_id 
left join (select * from (select * from temp_obs_enc_initial where concept_id = 8079 order by encounter_datetime desc) obs6i group by patient_id) obs6 on encounter_initial.patient_id = obs6.patient_id 


 	