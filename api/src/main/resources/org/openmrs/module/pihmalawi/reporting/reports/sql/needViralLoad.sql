-- ## report_uuid = 7b3473c8-4005-11e6-9d69-0f1641034c73
-- ## design_uuid = 8155129e-4005-11e6-9d69-0f1641034c73
-- ## report_name = Need Viral Load Report
-- ## report_description = Report listing patients due for a viral load test
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = location|Location|org.openmrs.Location

-- Get initial patient data set
select PS.patient_id as PID, 
identifier as Identifer, 
given_name as First, 
family_name as Last,
p.gender as Gender,
floor(datediff(@endDate,p.birthdate)/365.25) as "Age (yrs)",
pa.city_village as "Village",
pa.county_district as "T/A",
date_format(PS.start_date,"%Y-%m-%d") as "ART Enrollment",
value_numeric "Last VL Test Result",
date_format(oi.obs_datetime,"%Y-%m-%d") as "Last VL Test Date",
case 
	when (datediff(@endDate,PS.start_date) >=  182) then
		case 
			when (isnull(oi.value_numeric)) then
				"1 Needs VL Test: No Result Recorded"
			else
				case
					when (datediff(@endDate,oi.obs_datetime) >= 730) then
						"2 Needs VL Test: No result in last two years"
					else
						"3 No Test Needed: VL Test in last two years"	
						
				end		
		end
	else
		"4 Not eligible: Less than 6 months on ART"
end as "Status",
date_format(oai.value_datetime,"%Y-%m-%d") as "Next Appointment"
		
from 
	(select * from 
	(select state, start_date, end_date, patient_state.voided, patient_id, patient_program.date_created, patient_state.patient_program_id
	from patient_state
	join patient_program on patient_state.patient_program_id = patient_program.patient_program_id
	where (patient_state.end_date >= @endDate or patient_state.end_date is NULL)	
	and (patient_program.date_completed >= @endDate or patient_program.date_completed is NULL)
	and patient_program.date_enrolled < @endDate
	and patient_state.start_date < @endDate
	and patient_program.program_id = 1
	and patient_program.voided = 0
	and patient_state.voided = 0
	and state=7
	and patient_program.location_id = @location
	order by patient_program.date_created desc) PSi 
	group by patient_id) PS
-- Get viral load observations
left join (select * from (select person_id, value_numeric, obs_datetime from obs where concept_id = 856 and obs_datetime < @endDate and voided = 0 order by obs_datetime) oii group by oii.person_id) oi on oi.person_id = PS.patient_id
-- Get identifier
left join (select * from (select patient_identifier.patient_id, identifier from patient_identifier where identifier_type in (19,4) and voided=0 order by patient_identifier.identifier_type asc) pii group by patient_id) PI on PI.patient_id = PS.patient_id
-- Get name
join (select * from (select person_name.family_name, person_name.given_name, person_name.person_id from person_name where person_name.voided = 0 order by person_name.person_id) pnii group by pnii.person_id) PN on PN.person_id = PS.patient_id 
-- Get birthdate and gender
join (select person_id, birthdate, gender from person where voided=0) p on p.person_id = PS.patient_id
-- Get Address Information
left join (select * from (select person_id, city_village, county_district from person_address where voided = 0 order by date_created desc) pai group by pai.person_id) pa on pa.person_id = PS.patient_id
-- Get Address Information
left join (select * from (select person_id, value_datetime from obs where concept_id = 5096 and obs_datetime < @endDate and voided = 0 order by date_created desc) oaii group by oaii.person_id) oai on oai.person_id = PS.patient_id
having (Status != "4 Not eligible: Less than 6 months on ART" and Status != "3 No Test Needed: VL Test in last two years")
order by oai.value_datetime asc, Status asc

