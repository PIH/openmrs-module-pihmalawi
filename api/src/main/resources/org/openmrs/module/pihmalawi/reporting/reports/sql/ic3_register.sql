-- ## report_uuid = 897C0E0A-1F8A-4ABD-AFE2-054146227668
-- ## design_uuid = FFA51EA2-D483-43F2-9FE8-5B0AF619E8A0
-- ## report_name = IC3 Register Report
-- ## report_description = Report listing IC3 patients

-- Report lists all patients who are in enlisted in any of the CC programs or ART program.


drop table if exists temp_cohort;

set @endDate = curdate();

-- Create Table in Which to Store Report
create table temp_cohort (
  id INT not null auto_increment primary key,
  PID INT(11) not NULL,
  identifier VARCHAR(50) default NULL,
  ALL_ARVs VARCHAR(100) default NULL,
  birthdate DATE not NULL,
  gender VARCHAR(50) not NULL,
  outcome VARCHAR(50) default NULL,
  age INT(11) not NULL,
  SERUM_GLUCOSE NUMERIC,
  SYSTOLIC NUMERIC,
  DIASTOLIC NUMERIC
);

-- Create Initial Cohort With Basic Demographic Data
insert into temp_cohort
			(PID, identifier, birthdate, gender, outcome, age)
select		pp.patient_id, pi.identifier, p.birthdate, p.gender, cn.name, floor(datediff(@endDate,birthdate)/365)
from	 	(select * from 
				(select patient_id, patient_program_id 
					from patient_program 
					where voided = 0 
					and program_id = 1 
					and (date_enrolled <= @endDate or date_enrolled is NULL)
					order by date_enrolled DESC) 
				ppi group by patient_id) pp -- Most recent HIV Program Enrollment
join 		(select patient_program_id 
				from patient_state 
				where voided = 0 
				and state in (1,7) 
				and start_date < @endDate 
				group by patient_program_id) xps 
			on xps.patient_program_id = pp.patient_program_id -- Ensure have been On ARVs or Pre-ART (continue)
left join 		(select patient_program_id, state from 
					(select * 
					from patient_state 
					where voided = 0 
					and (end_date is NULL or end_date < @endDate) 
					order by start_date DESC) psi
				group by patient_program_id) ps on ps.patient_program_id = pp.patient_program_id -- Most recent outcome
left join 	program_workflow_state pws on pws.program_workflow_state_id = ps.state
left join 		(select concept_id, name from concept_name where voided = 0 group by concept_id) cn on cn.concept_id = pws.concept_id
join 		(select * from person where voided = 0) p on p.person_id = pp.patient_id -- remove voided persons
join 		(select patient_id, identifier from (select * from patient_identifier where voided = 0 and identifier_type in (4,19) order by date_created desc) pii group by patient_id) pi on pi.patient_id = pp.patient_id -- Ensure HCC identifier
order by 	pp.patient_id asc;


-- Stored Procedures
DROP PROCEDURE IF EXISTS getLastNumericObsBeforeDate;

DELIMITER ;;
CREATE PROCEDURE getLastNumericObsBeforeDate(IN cid INT, IN endDate DATE, IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs NUMERIC
	);

	insert into temp_obs_vector
			(PID, obs)
	select  PID, value_numeric
	from temp_cohort tt
	left join (select * from 
				(select * from obs 
				where concept_id = cid 
				and obs_datetime <= endDate 
				and voided = 0 
				order by obs_datetime desc) 
				oi group by person_id) o 
	on o.person_id = tt.PID
	where o.person_id = tt.PID; 

	SET @s=CONCAT('UPDATE temp_cohort tc, temp_obs_vector tt SET tc.',colName,' = tt.obs WHERE tc.PID = tt.PID;');
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS getAllARVNumbers;

DELIMITER ;;
CREATE PROCEDURE getAllARVNumbers(IN endDate DATE, IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs VARCHAR(100) default NULL 
	);

	insert into temp_obs_vector
			(PID, obs)
	select  patient_id, group_concat(pi.identifier separator ', ') as id_string
			from patient_identifier pi 
			join temp_cohort tc on tc.PID = pi.patient_id
			where identifier_type in (4,19) 
			and voided = 0 
			and date_created <= endDate 
			group by patient_id;

	SET @s=CONCAT('UPDATE temp_cohort tc, temp_obs_vector tt SET tc.',colName,' = tt.obs WHERE tc.PID = tt.PID;');
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

END;;
DELIMITER ;

CALL getLastNumericObsBeforeDate(887, @endDate,'SERUM_GLUCOSE');
CALL getLastNumericObsBeforeDate(5085, @endDate,'SYSTOLIC');
CALL getLastNumericObsBeforeDate(5086, @endDate,'DIASTOLIC');
CALL getAllARVNumbers(@endDate,'ALL_ARVs');

SELECT
     `PID` as PID,
     `identifier` as Identifier,
     `birthdate` as Birthdate,
     `gender` as Gender,
     `outcome` as ProgramOutcome,
     `age` as Age,
     `SERUM_GLUCOSE` as SERUM_GLUCOSE,
     `SYSTOLIC` as SYSTOLIC,
     `DIASTOLIC` as DIASTOLIC
FROM `temp_HCC_cohort`;


-- select @obs_value;

-- select * from temp_cohort;

-- select 	PID, value_numeric 
-- from temp_cohort tt
-- left join (select * from (select * from obs where concept_id = 887 and obs_datetime <= "2016-01-01" and voided = 0 order by obs_datetime desc) oi group by person_id) o on o.person_id = tt.PID; 


