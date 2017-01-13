-- Stored Procedures

-- getLastNumericObsBeforeDate(cid, endDate, tempTableName)
-- INPUTS: 		cid - observation concept id
--				endDate - end Date of report
-- 				tempTableName - temporary table to write to
-- Procedure provides a table of the last observation before end date for a cohort of patients.
-- Procedure assumes obs_datetime is relevant last date (may not be accurate for obs groups) and
-- that internal patient identifiers are supplied by the PID in the tempCohort table. 

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
	from tempCohort tt
	left join (select * from 
				(select * from obs 
				where concept_id = cid 
				and obs_datetime <= endDate 
				and voided = 0 
				order by obs_datetime desc) 
				oi group by person_id) o 
	on o.person_id = tt.PID; 

	SET @s=CONCAT('UPDATE tempCohort tc, temp_obs_vector tt SET tc.',colName,' = tt.obs WHERE tc.id = tt.id;');
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

END;;
DELIMITER ;

-- getAllARVNumbers(cid, endDate, tempTableName)
-- INPUTS: 		endDate - end Date of report
--				tempTableName - temporary table to write to
-- Procedure provides a table of a concatenated list of patient ARV numbers before end date 
-- for a cohort of patients.
-- Procedure assumes date_created is relevant last date and that internal patient identifiers 
-- are supplied by the PID in the tempCohort table. 

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
				from tempCohort tc
				left join patient_identifier pi on tc.PID = pi.patient_id
				where identifier_type in (4,19) 
				and voided = 0 
				and date_created <= @endDate 
				group by patient_id;

	SET @s=CONCAT('UPDATE tempCohort tc, temp_obs_vector tt SET tc.',colName,' = tt.obs WHERE tc.id = tt.id;');
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS warehouseProgramEnrollment;

DELIMITER ;;
CREATE PROCEDURE `warehouseProgramEnrollment`()
BEGIN

-- Refresh warehouse_program_enrollment
drop table if exists warehouse_program_enrollment;

-- Create Table in which to store patient program enrollment data
create table warehouse_program_enrollment (
	id INT not null auto_increment primary key,
	PID INT(11) not NULL,
	programId INT(11),
	programName VARCHAR(50) default NULL,
	dateEnrolled DATETIME,
	birthdate DATE,
	ageYearsAtEnrollment TINYINT,
	locationId TINYINT,
	locationName VARCHAR(255),
	completionDate datetime,
	ageYearsAtCompletion TINYINT,
	patientState  VARCHAR(255),
	stateStartDate DATE,
	stateEndDate	DATE,
	stateDateChanged DATETIME
);

insert into warehouse_program_enrollment(
PID, programId, programName,
dateEnrolled, birthdate, ageYearsAtEnrollment, locationId, locationName,
completionDate, ageYearsAtCompletion, patientState, stateStartDate,
stateEndDate, stateDateChanged)
select
			p.person_id as PID,
            pg.program_id as programId,
			pg.name as programName,
            pp.date_enrolled as dateEnrolled,
            p.birthdate as birthdate,
			if (p.birthdate is null or pp.date_enrolled is null, null, TIMESTAMPDIFF(YEAR, p.birthdate, pp.date_enrolled)) as ageYearsAtEnrollment,
			pp.location_id, l.name as locationName,
			pp.date_completed as completionDate,
			if (p.birthdate is null or pp.date_enrolled is null, null, TIMESTAMPDIFF(YEAR, p.birthdate, pp.date_completed)) as ageYearsAtCompletion,
			get_concept_name(pws.concept_id) as patientState,
			ps.start_date as stateStartDate,
			ps.end_date as stateEndDate,
			ps.date_changed stateDateChanged
from        person p
inner join  patient_program pp on p.person_id = pp.patient_id
inner join  program pg on pp.program_id = pg.program_id
inner  join patient_state ps on pp.patient_program_id = ps.patient_program_id
inner  join program_workflow_state pws on ps.state=pws.program_workflow_state_id
left outer join location l on pp.location_id = l.location_id
where       pp.voided = 0 and p.voided = 0 and pg.program_id in (1,10) -- only HIV and CC programs
			and ps.voided =0
and			pp.date_enrolled is not null
order by patient_id, ps.start_date ;

END;;
DELIMITER ;

DROP FUNCTION IF EXISTS get_concept_name;

DELIMITER $$
CREATE FUNCTION `get_concept_name`(concept_id INT) RETURNS varchar(255) CHARSET utf8
    DETERMINISTIC
BEGIN

	DECLARE conceptName VARCHAR(255);
	select cn.name into conceptName
        from concept c, concept_name cn
        where c.concept_id=cn.concept_id
            and c.concept_id = concept_id and cn.voided=0
            and cn.locale='en'
            and cn.concept_name_type = 'FULLY_SPECIFIED' limit 1;

RETURN conceptName;
END$$
DELIMITER ;

