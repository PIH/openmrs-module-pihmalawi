-- ## report_uuid = 7b3473c8-4005-11e6-9d69-0f1641034c73
-- ## design_uuid = 8155129e-4005-11e6-9d69-0f1641034c73
-- ## report_name = Need Viral Load Report
-- ## report_description = Report listing patients due for a viral load test.
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = location|Location|org.openmrs.Location

-- Report finds patients who need a viral load test. Current setup is to 
-- find patients enrolled in ART > 6 months who have not had a test (1)
-- or patients who have not had a test in 2 years (2). Uses days (6 months
-- = 182 days, 2 years = 730 days). Can also list those who had a test in 
-- the last two years (3) and those who have not been ART for 6 months (4)
-- by modifying the "having statement."


-- Avoid error by ensuring this table is not there
drop temporary table if exists temp_recent_regimen;
drop temporary table if exists temp_regimen_start;

-- Needed to create a temporary table to grab the most recent ART regimen
create temporary table temp_recent_regimen as
select o.person_id, o.obs_datetime, o.value_coded
from obs o
join (select person_id, max(obs_datetime) as maxobsd from obs where concept_id in (8169,8170,8171,8172) and obs_datetime < @endDate and voided = 0 group by person_id) om on o.obs_datetime = om.maxobsd and o.person_id =om.person_id
where concept_id in (8169,8170,8171,8172) 
and voided = 0;

-- Needed to create a temporary table to grab the start of ART regimen
create temporary table temp_regimen_start as
select trr.person_id, o.obs_datetime
from temp_recent_regimen trr
join obs o on o.value_coded = trr.value_coded and o.person_id = trr.person_id
where concept_id in (8169,8170,8171,8172) 
and o.voided = 0
and o.obs_datetime < @endDate;


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
date_format(trs.start_date,"%Y-%m-%d") as "Regimen Start",
cn.name as "Current Regimen",
value_numeric "Last VL Test Result",
date_format(oi.obs_datetime,"%Y-%m-%d") as "Last VL Test Date",
case 
	when (datediff(@endDate,trs.start_date) >=  182) then
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
		case
			when isnull(datediff(@endDate,trs.start_date)) then
				"1 No regimen start date"
			else
				"4 Not eligible: Less than 6 months on ART"
			end
end as "Status",
date_format(e.value_datetime,"%Y-%m-%d") as "Next Appointment"
		
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
-- Get start of regimen 
left join (select * from (select person_id, obs_datetime as start_date from temp_regimen_start order by obs_datetime asc) trsi group by person_id) trs on trs.person_id = PS.patient_id
-- Get identifier
left join (select * from (select patient_identifier.patient_id, identifier from patient_identifier where identifier_type in (19,4) and voided=0 order by patient_identifier.identifier_type asc) pii group by patient_id) PI on PI.patient_id = PS.patient_id
-- Get name
join (select * from (select person_name.family_name, person_name.given_name, person_name.person_id from person_name where person_name.voided = 0 order by person_name.person_id) pnii group by pnii.person_id) PN on PN.person_id = PS.patient_id 
-- Get last encounter and next appointment
left join (select * from (select patient_id, value_datetime, encounter_datetime from encounter join obs on obs.encounter_id = encounter.encounter_id where encounter.voided = 0 and obs.voided = 0 and concept_id = 5096 and encounter_datetime <= @endDate order by encounter_datetime desc) ei group by ei.patient_id) e on e.patient_id = PS.patient_id
-- Get birthdate and gender
join (select * from (select person_id, birthdate, gender from person where voided=0 order by date_created desc) pi group by person_id) p on p.person_id = PS.patient_id
-- Get most recent regimen
left join (select * from (select * from temp_recent_regimen order by obs_datetime desc) trri group by person_id) trr on trr.person_id = PS.patient_id
join concept_name cn on cn.concept_id = trr.value_coded
-- Get Address Information
left join (select * from (select person_id, city_village, county_district from person_address where voided = 0 order by date_created desc) pai group by pai.person_id) pa on pa.person_id = PS.patient_id
having (Status != "4 Not eligible: Less than 6 months on ART" and Status != "3 No Test Needed: VL Test in last two years")
order by e.value_datetime asc, Status asc;

-- Clean up temporary tables
drop temporary table if exists temp_recent_regimen;
drop temporary table if exists temp_regimen_start;