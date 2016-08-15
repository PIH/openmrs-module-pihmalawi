-- ## report_uuid = 2fe281be-3ff4-11e6-9d69-0f1641034c73
-- ## design_uuid = 3ed8dbbe-3ff4-11e6-9d69-0f1641034c73
-- ## report_name = High Viral Load Report
-- ## report_description = Report listing patients with a high viral load from last laboratory test. 
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = min_vl|Minimum Viral Load|java.lang.Integer

-- Report lists patients who have a high viral load based on the last available 
-- viral load observation (simply searches obs and joins demographic information). 

drop temporary table if exists temp_recent_regimen;
drop temporary table if exists temp_regimen_start;
drop temporary table if exists temp_viral_loads;
DROP FUNCTION IF EXISTS getPreviousVL;
DROP FUNCTION IF EXISTS getPreviousVLDate;

DELIMITER $$

CREATE FUNCTION getPreviousVL(PID INT, endDate DATETIME)
    RETURNS DOUBLE
   DETERMINISTIC
BEGIN
	DECLARE returnVL DOUBLE;
	select value_numeric into returnVL 
	from 
		(select * from obs where concept_id = 856 and person_id = PID and obs_datetime < endDate and voided = 0 order by 		obs_datetime desc) o
	limit 1 
	offset 1;
	
	return returnVL;
END
$$

DELIMITER $$

CREATE FUNCTION getPreviousVLDate(PID INT,endDate DATETIME)
    RETURNS DATETIME
   DETERMINISTIC
BEGIN
	DECLARE returnVLDate DATETIME;
	select obs_datetime into returnVLDate 
	from 
		(select * from obs where concept_id = 856 and person_id = PID and obs_datetime < endDate and voided = 0 order by obs_datetime desc) o
	limit 1 
	offset 1;
	
	return returnVLDate;
END
$$

-- Needed to create a temporary table to grab the most recent ART regimen
-- You could probably encapsulate this in a function, which might perform better.
create temporary table temp_recent_regimen as
select o.person_id, o.obs_datetime, o.value_coded
from obs o
join (select person_id, max(obs_datetime) as maxobsd from obs where concept_id in (8169,8170,8171,8172) and obs_datetime < @endDate group by person_id) om on o.obs_datetime = om.maxobsd and o.person_id =om.person_id
where concept_id in (8169,8170,8171,8172) 
and voided = 0;

-- Needed to create a temporary table to grab the start of ART regimen
-- You could probably encapsulate this in a function, which might perform better.
create temporary table temp_regimen_start as
select trr.person_id, o.obs_datetime
from temp_recent_regimen trr
join obs o on o.value_coded = trr.value_coded and o.person_id = trr.person_id
where o.voided = 0
and o.obs_datetime < @endDate;

select o.person_id as PID, -- Fields to show 
identifier as Identifer, 
given_name as First, 
family_name as Last,
p.gender as Gender,
floor(datediff(@endDate,p.birthdate)/365.25) as "Age (yrs)",
pa.city_village as "Village",
pa.county_district as "T/A",
date_format(trs.obs_datetime,'%Y-%m-%d') as "Regimen Start Date",
cn.name as "Current Regimen",
date_format(getPreviousVLDate(o.person_id,@endDate),'%Y-%m-%d') as "Previous VL Date",
getPreviousVL(o.person_id,@endDate) as "Previous VL",
date_format(e.encounter_datetime,'%Y-%m-%d') as "Viral Load Date",
l.name as "Viral Load Location",
o.value_numeric as "Viral Load", 
case 
	when (datediff(@endDate,e.encounter_datetime) > 90) then
		"[ ]"
	else
		"N/A"
end as "Re-test Eligible"

-- Base search (on viral load observations)
from (select * from (select * from (select value_numeric, encounter_id, obs_datetime, person_id from obs where concept_id = 856 and obs_datetime <= @endDate and voided=0 order by obs_datetime desc) oii group by oii.person_id) oi where value_numeric > @min_vl) o
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
-- Get most recent regimen
left join temp_recent_regimen trr on trr.person_id = o.person_id
join concept_name cn on cn.concept_id = trr.value_coded
-- Get most recent regimen start date
left join (select * from (select * from temp_regimen_start order by obs_datetime asc) trsi group by person_id) trs on trs.person_id = o.person_id
order by l.name;

-- Clean up temporary tables
drop temporary table if exists temp_recent_regimen;
drop temporary table if exists temp_regimen_start;
DROP FUNCTION IF EXISTS getPreviousVL;
DROP FUNCTION IF EXISTS getPreviousVLDate;