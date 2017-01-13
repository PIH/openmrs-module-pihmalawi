-- ## report_uuid = 897C0E0A-1F8A-4ABD-AFE2-054146227668
-- ## design_uuid = FFA51EA2-D483-43F2-9FE8-5B0AF619E8A0
-- ## report_name = IC3 Register Report
-- ## report_description = Report listing IC3 patients

-- Report lists all patients who are in enlisted in any of the CC programs or ART program.

CALL warehouseProgramEnrollment();

-- Refresh temp table
drop table if exists tempCohort;

-- Create Table in which to store cohort
create table tempCohort (
  id INT not null auto_increment primary key,
  PID INT(11) not NULL,
  identifier VARCHAR(50) default NULL,
  ALL_ARVs VARCHAR(100) default NULL,
  birthdate DATE not NULL,
  gender VARCHAR(50) not NULL,
  outcome VARCHAR(50) default NULL,
  age INT(11) not NULL,
  SERUM_GLUCOSE NUMERIC default NULL,
  SYSTOLIC NUMERIC default NULL,
  DIASTOLIC NUMERIC default NULL
);

-- Create Initial Cohort With Basic Demographic Data
insert into tempCohort
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
join 		(select patient_id, identifier from (select * from patient_identifier where voided = 0 and identifier_type in (4, 19) order by date_created desc) pii group by patient_id) pi on pi.patient_id = pp.patient_id -- Ensure HCC identifier
order by 	pp.patient_id asc;

-- Call Routines
CALL getLastNumericObsBeforeDate(887, @endDate,'SERUM_GLUCOSE');
CALL getLastNumericObsBeforeDate(5085, @endDate,'SYSTOLIC');
CALL getLastNumericObsBeforeDate(5086, @endDate,'DIASTOLIC');
CALL getAllARVNumbers(@endDate,'ALL_ARVs');


SELECT
     `PID` as PID,
     `identifier` as Identifier,
     `ALL_ARVs` as ALL_ARVs,
     `birthdate` as Birthdate,
     `gender` as Gender,
     `outcome` as ProgramOutcome,
     `age` as Age,
     `SERUM_GLUCOSE` as SERUM_GLUCOSE,
     `SYSTOLIC` as SYSTOLIC,
     `DIASTOLIC` as DIASTOLIC
FROM `temp_cohort`;


select * from tempCohort;





