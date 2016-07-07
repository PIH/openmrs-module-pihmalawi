-- ## report_uuid = 2fe281be-3ff4-11e6-9d69-0f1641034c73
-- ## design_uuid = 3ed8dbbe-3ff4-11e6-9d69-0f1641034c73
-- ## report_name = High Viral Load Report
-- ## report_description = Report listing patients with a high viral load
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = min_vl|Minimum CD4|java.lang.Integer

select o.person_id as PID, -- Fields to show 
identifier as Identifer, 
given_name as First, 
family_name as Last,
p.gender as Gender,
floor(datediff(@endDate,p.birthdate)/365.25) as "Age (yrs)",
pa.city_village as "Village",
pa.county_district as "T/A",
date_format(e.encounter_datetime,'%Y-%m-%d') as "Viral Load Date",
l.name as "Viral Load Location",
o.value_numeric as "Viral Load"

-- Base search (on viral load observations)
from (select * from (select value_numeric, encounter_id, obs_datetime, person_id from obs where concept_id = 856 and obs_datetime <= @endDate and voided=0 order by obs_datetime desc) oi where value_numeric > @min_vl group by oi.person_id) o
-- Get identifier
left join (select * from (select patient_identifier.patient_id, identifier from patient_identifier where identifier_type in (19,4) and voided=0 order by patient_identifier.identifier_type asc) pii group by patient_id) PI on PI.patient_id = o.person_id
-- Get name
join (select * from (select person_name.family_name, person_name.given_name, person_name.person_id from person_name where person_name.voided = 0 order by person_name.person_id) pnii group by pnii.person_id) PN on PN.person_id = o.person_id
-- Get birthdate and gender
join (select person_id, birthdate, gender from person where voided=0) p on p.person_id = o.person_id
-- Get Last Encounter Information
join encounter e on e.encounter_id = o.encounter_id
-- Get Location Information
join location l on l.location_id = e.location_id
-- Get Address Information
join (select * from (select person_id, city_village, county_district from person_address where voided = 0 order by date_created desc) pai group by pai.person_id) pa on pa.person_id = o.person_id
order by l.name