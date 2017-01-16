-- ## report_uuid = 17742B5A-CEBF-47F1-8D60-540DCF61A2A4
-- ## design_uuid = 3425B69A-14C9-4AE6-AC05-FE78859D63C4
-- ## report_name = IC3 Register Sql Routines
-- ## report_description = Helper procedures and fucntions to produce IC3 report

-- Stored Procedures


-- Document this one. 
DROP PROCEDURE IF EXISTS updateReportTable;

DELIMITER ;;
CREATE PROCEDURE updateReportTable(IN colName VARCHAR(50))
BEGIN

	SET @s=CONCAT('UPDATE warehouse_ic3_cohort tc, temp_obs_vector tt SET tc.',colName,' = tt.obs WHERE tc.PID = tt.PID;');
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

END;;
DELIMITER ;

-- getNumericObsBeforeDate(cid, endDate, firstLast, colName)
-- INPUTS: 		cid - observation concept id
--				endDate - end Date of report
-- 				colName - temporary table to write to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets last obs for concept id before (or on) end date and writes observation to report 
-- table for cohort (one per patient).
-- Procedure assumes obs_datetime is relevant last date (may not be accurate for obs groups) and
-- that internal patient identifiers are supplied by the PID in the warehouse_ic3_cohort table.

DROP PROCEDURE IF EXISTS getNumericObsBeforeDate;

DELIMITER ;;
CREATE PROCEDURE getNumericObsBeforeDate(IN cid INT, IN endDate DATE, IN firstLast VARCHAR(50), IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs NUMERIC
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getNumericObsBeforeDate!';
	END IF;

	SET @s=CONCAT('insert into temp_obs_vector
							(PID, obs)
					select  PID, value_numeric
					from warehouse_ic3_cohort tt
					left join (select * from 
								(select * from obs 
								where concept_id = ', CONCAT(cid),
								' and obs_datetime <= \'', endDate,
								'\' and voided = 0 
								order by obs_datetime ', @upDown, 
								') oi group by person_id) o 
					on o.person_id = tt.PID;');
	
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

END;;
DELIMITER ;

-- getCodedObsBeforeDate(cid, endDate, firstLast, colName)
-- INPUTS: 		cid - observation concept id
--				endDate - end Date of report
-- 				colName - temporary table to write to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets last obs for concept id before (or on) end date and writes observation to report 
-- table for cohort (one per patient).
-- Procedure assumes obs_datetime is relevant last date (may not be accurate for obs groups) and
-- that internal patient identifiers are supplied by the PID in the warehouse_ic3_cohort table.

DROP PROCEDURE IF EXISTS getCodedObsBeforeDate;

DELIMITER ;;
CREATE PROCEDURE getCodedObsBeforeDate(IN cid INT, IN endDate DATE, IN firstLast VARCHAR(50), IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs VARCHAR(255)
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getNumericObsBeforeDate!';
	END IF;

	SET @s=CONCAT('insert into temp_obs_vector
							(PID, obs)
					select  PID, get_concept_name(value_coded)
					from warehouse_ic3_cohort tt
					left join (select * from 
								(select * from obs 
								where concept_id = ', CONCAT(cid),
								' and obs_datetime <= \'', endDate,
								'\' and voided = 0 
								order by obs_datetime ', @upDown, 
								') oi group by person_id) o 
					on o.person_id = tt.PID;');
	
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

END;;
DELIMITER ;


-- getDatetimeObsBeforeDate(cid, endDate, firstLast, colName)
-- INPUTS: 		cid - observation concept id
--				endDate - end Date of report
-- 				colName - temporary table to write to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets last obs for concept id before (or on) end date and writes observation to report 
-- table for cohort (one per patient).
-- Procedure assumes obs_datetime is relevant last date (may not be accurate for obs groups) and
-- that internal patient identifiers are supplied by the PID in the warehouse_ic3_cohort table.

DROP PROCEDURE IF EXISTS getDatetimeObsBeforeDate;

DELIMITER ;;
CREATE PROCEDURE getDatetimeObsBeforeDate(IN cid INT, IN endDate DATE, IN firstLast VARCHAR(50), IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs DATE
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getDatetimeObsBeforeDate!';
	END IF;

	SET @s=CONCAT('insert into temp_obs_vector
							(PID, obs)
					select  PID, value_datetime
					from warehouse_ic3_cohort tt
					left join (select * from 
								(select * from obs 
								where concept_id = ', CONCAT(cid),
								' and obs_datetime <= \'', endDate,
								'\' and voided = 0 
								order by obs_datetime ', @upDown, 
								') oi group by person_id) o 
					on o.person_id = tt.PID;');
	
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

END;;
DELIMITER ;



-- getAllIdentifiers(cid, endDate, idTypes, colName)
-- INPUTS: 		endDate - end Date of report
--				colName - column to write data to
--				idTypes - string list of id types to consider (e.g., '1,2,3')
-- Procedure gets a list of all ARV numbers before end date and writes this list to report
-- table (one per patient). 
-- Procedure assumes date_created is relevant last date and that internal patient identifiers 
-- are supplied by the PID in the warehouse_ic3_cohort table. Procedure gets IDs that were created before
-- end date. 

DROP PROCEDURE IF EXISTS getAllIdentifiers;

DELIMITER ;;
CREATE PROCEDURE getAllIdentifiers(IN endDate DATE, IN idTypes VARCHAR(50), IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs VARCHAR(100) default NULL 
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);
	
	SET @s=CONCAT('insert into temp_obs_vector
					(PID, obs)
					select  PID, 
							group_concat(pi.identifier separator \', \') as id_string
					from warehouse_ic3_cohort tc
					left join (select patient_id, identifier 
								from patient_identifier 
								where identifier_type in (', idTypes,
								') and date_created <= @endDate 
								and voided = 0) pi
					on tc.PID = pi.patient_id
					group by PID;');
				
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

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

DELIMITER ;;
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
END;;
DELIMITER ;


-- getEncounterDatetimeBeforeEndDate(encounterTypes, endDate, firstLast, colName)
-- INPUTS: 		encounterTypes - string of encounter types to consider (e.g., '1,2,3')
--				endDate - end date of report
--				colName - column to write data to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets the last encounter for given encounter types before end date and writes this 
-- list to report table (one per patient). 
-- Procedure assumes date_created is relevant last date and that internal patient identifiers 
-- are supplied by the PID in the warehouse_ic3_cohort table. Procedure gets IDs that were created before
-- end date. 

DROP PROCEDURE IF EXISTS getEncounterDatetimeBeforeEndDate;

DELIMITER ;;
CREATE PROCEDURE getEncounterDatetimeBeforeEndDate(IN encounterTypes VARCHAR(50), IN endDate DATE, IN firstLast VARCHAR(50), IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs DATE default NULL 
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getEncounterDatetimeBeforeEndDate!';
	END IF;


	SET @s=CONCAT('insert into temp_obs_vector
					(PID, obs)
					select PID, encounter_datetime 
					from warehouse_ic3_cohort tc
					left join (select * from 
								(select patient_id, encounter_datetime 
								from encounter e
								where encounter_type in (', encounterTypes,
								') and encounter_datetime <= @endDate 
								and voided = 0
								order by encounter_datetime ', @upDown,') ei
					group by patient_id) e
					on tc.PID = e.patient_id;');
				
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

END;;
DELIMITER ;

-- getEncounterLocationBeforeEndDate(encounterTypes, endDate, firstLast, colName)
-- INPUTS: 		encounterTypes - string of encounter types to consider (e.g., '1,2,3')
--				endDate - end date of report
--				colName - column to write data to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets the last encounter for given encounter types before end date and writes this 
-- list to report table (one per patient). 
-- Procedure assumes date_created is relevant last date and that internal patient identifiers 
-- are supplied by the PID in the warehouse_ic3_cohort table. Procedure gets IDs that were created before
-- end date. 

DROP PROCEDURE IF EXISTS getEncounterLocationBeforeEndDate;

DELIMITER ;;
CREATE PROCEDURE getEncounterLocationBeforeEndDate(IN encounterTypes VARCHAR(50), IN endDate DATE, IN firstLast VARCHAR(50), IN colName VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs VARCHAR(50) default NULL 
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getEncounterDatetimeBeforeEndDate!';
	END IF;


	SET @s=CONCAT('insert into temp_obs_vector
					(PID, obs)
					select PID, name 
					from warehouse_ic3_cohort tc
					left join (select * from 
								(select patient_id, l.name 
								from encounter e
								join location l on l.location_id = e.location_id
								where encounter_type in (', encounterTypes,
								') and encounter_datetime <= @endDate 
								and voided = 0
								order by encounter_datetime ', @upDown,') ei
					group by patient_id) e
					on tc.PID = e.patient_id;');
				
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

END;;
DELIMITER ;


-- updateProgramsEnrollmentDate()
-- Procedure that retrieves ART and NCD enrollment dates from the warehouse_program_enrollment table

DROP PROCEDURE IF EXISTS updateProgramsEnrollmentDate;

DELIMITER ;;
CREATE PROCEDURE `updateProgramsEnrollmentDate`()
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
		programId INT(11),
  		dateEnrolled DATE default NULL
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	insert into temp_obs_vector(PID, programId, dateEnrolled)
		select PID, programId, Min(dateEnrolled)
		from `warehouse_program_enrollment`
		group by PID, programId;

	-- update ART enrollment date
	UPDATE warehouse_ic3_cohort tc, temp_obs_vector tt
	SET tc.artEnrollmentDate = tt.dateEnrolled WHERE tc.PID = tt.PID and tt.programId=1;

	-- update NCD enrollment date
	UPDATE warehouse_ic3_cohort tc, temp_obs_vector tt
	SET tc.ncdEnrollmentDate = tt.dateEnrolled WHERE tc.PID = tt.PID and tt.programId=10;

END;;
DELIMITER ;


DROP PROCEDURE IF EXISTS updateRecentRegimen;

DELIMITER ;;
CREATE PROCEDURE updateRecentRegimen()
BEGIN

	DROP TEMPORARY TABLE IF EXISTS recentRegimenObs;
	create temporary table recentRegimenObs (
  		id INT not null auto_increment primary key,
  		pid INT(11) not NULL,
  		cid INT(11) not NULL,
  		recentRegimen INT(11)
	);
	CREATE INDEX PID_index ON recentRegimenObs (PID);
	
	insert into recentRegimenObs
		(pid, cid, recentRegimen)
	select pid, cid, recentRegimen
	from (select * from 
			(select person_id as pid, concept_id as cid, value_coded as recentRegimen
			from obs 
			where concept_id = 8169 
			and obs_datetime < @endDate
			and voided = 0 
			order by obs_datetime desc) oi 
			group by oi.pid) o
	where pid in (select PID from warehouse_ic3_cohort);

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		pid INT(11) not NULL,
  		recentRegimen VARCHAR(255) default NULL,
  		recentRegimenStart DATE default NULL
	);
	
	CREATE INDEX PID_index ON temp_obs_vector (PID);
	
	insert into temp_obs_vector 
		(pid, recentRegimen, recentRegimenStart)
	select pid, get_concept_name(recentRegimen) as recentRegimen, obs_datetime as recentRegimenStart
		from (select * 
				from obs o
				join recentRegimenObs rro 
				on rro.pid = o.person_id 
				and rro.recentRegimen = o.value_coded 
				where concept_id = 8169
				order by obs_datetime asc) oi
				group by oi.person_id;
	
	UPDATE warehouse_ic3_cohort tc, temp_obs_vector tt 
	SET tc.lastArtRegimenStart = tt.recentRegimenStart
	WHERE tc.PID = tt.PID;
	
	UPDATE warehouse_ic3_cohort tc, temp_obs_vector tt 	
	SET tc.lastArtRegimen = tt.recentRegimen 
	WHERE tc.PID = tt.PID;

END;;
DELIMITER ;


-- updateFirstViralLoad()
-- Procedure that retrieves First Viral Load for patients

DROP PROCEDURE IF EXISTS updateFirstViralLoad;

DELIMITER $$
CREATE PROCEDURE `updateFirstViralLoad`()
BEGIN
	declare done INTEGER DEFAULT 0;
	declare v_patientId int;
	declare v_visitDate datetime;
	declare v_numeric double;
	declare insert_visitDate datetime;
	declare insert_numeric double;
	declare ldl_visitDate datetime;

	DECLARE viralLoadCur CURSOR FOR
		select  PID, obs_datetime, value_numeric
		from warehouse_ic3_cohort tt
		left join (
		select * from
		(select person_id,obs_datetime, value_numeric
		from obs where concept_id = 856 and voided = 0 order by obs_datetime asc) oi
		group by person_id) o on o.person_id = tt.PID ;
	declare continue handler for not found set done=1;

	set done = 0;
    open viralLoadCur;
    viralLoop: loop
        fetch viralLoadCur into v_patientId, v_visitDate,v_numeric;
        if done = 1 then leave viralLoop; end if;

		select MIN(obs_datetime) into ldl_visitDate
		from obs where concept_id=8561
		and value_coded = 2257 and voided =0
		and person_id = v_patientId;

		if (ldl_visitDate is null ) then
			set insert_visitDate = v_visitDate;
			set insert_numeric = v_numeric;
		elseif (v_visitDate is null) then
			set insert_visitDate = ldl_visitDate;
			set insert_numeric = 0;
		elseif ( datediff(ldl_visitDate, v_visitDate) <= 0 ) then
			set insert_visitDate = ldl_visitDate;
			set insert_numeric = 0;
		else
			set insert_visitDate = v_visitDate;
			set insert_numeric = v_numeric;
		end if;

		UPDATE warehouse_ic3_cohort tc
		set firstViralLoadDate=insert_visitDate, firstViralLoadResult = insert_numeric
		where PID = v_patientId;

	end loop viralLoop;
    close viralLoadCur;

END$$
DELIMITER ;

-- updateLastViralLoad()
-- Procedure that retrieves last Viral Load results for patients

DROP PROCEDURE IF EXISTS updateLastViralLoad;

DELIMITER $$
CREATE PROCEDURE `updateLastViralLoad`()
BEGIN
	declare done INTEGER DEFAULT 0;
	declare v_patientId int;
	declare v_visitDate datetime;
	declare v_numeric double;
	declare insert_visitDate datetime;
	declare insert_numeric double;
	declare ldl_visitDate datetime;

	DECLARE viralLoadCur CURSOR FOR
		select  PID, obs_datetime, value_numeric
		from warehouse_ic3_cohort tt
		left join (
		select * from
		(select person_id,obs_datetime, value_numeric
		from obs where concept_id = 856 and voided = 0 order by obs_datetime desc) oi
		group by person_id) o on o.person_id = tt.PID ;
	declare continue handler for not found set done=1;

	set done = 0;
    open viralLoadCur;
    viralLoop: loop
        fetch viralLoadCur into v_patientId, v_visitDate,v_numeric;
        if done = 1 then leave viralLoop; end if;

		select MAX(obs_datetime) into ldl_visitDate
		from obs where concept_id=8561
		and value_coded = 2257 and voided =0
		and person_id = v_patientId;

		if (ldl_visitDate is null ) then
			set insert_visitDate = v_visitDate;
			set insert_numeric = v_numeric;
		elseif (v_visitDate is null) then
			set insert_visitDate = ldl_visitDate;
			set insert_numeric = 0;
		elseif ( datediff(ldl_visitDate, v_visitDate) >= 0 ) then
			set insert_visitDate = ldl_visitDate;
			set insert_numeric = 0;
		else
			set insert_visitDate = v_visitDate;
			set insert_numeric = v_numeric;
		end if;

		UPDATE warehouse_ic3_cohort tc
		set lastViralLoadDate=insert_visitDate, lastViralLoadResult = insert_numeric
		where PID = v_patientId;

	end loop viralLoop;
    close viralLoadCur;

END$$
DELIMITER ;

-- getEncounterDateForCodedObs(cid, endDate, firstLast, colName)
-- INPUTS: 		cid - observation concept id
--				endDate - end Date of report
-- 				colName - temporary table to write to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets last obs for concept id before (or on) end date and writes observation to report 
-- table for cohort (one per patient).
-- Procedure assumes obs_datetime is relevant last date (may not be accurate for obs groups) and
-- that internal patient identifiers are supplied by the PID in the warehouse_ic3_cohort table.

DROP PROCEDURE IF EXISTS getEncounterDateForCodedObs;

DELIMITER ;;

CREATE PROCEDURE getEncounterDateForCodedObs(IN cid INT, IN vcid VARCHAR(255), IN endDate DATE, IN firstLast VARCHAR(50), IN colName VARCHAR(50))
BEGIN
	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs DATE
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getEncounterDateForCodedObs!';
	END IF;

	SET @s=CONCAT('insert into temp_obs_vector
							(PID, obs)
					select PID, encounter_datetime
					from warehouse_ic3_cohort wc
					left join (select * from 
								(select person_id, e.encounter_datetime 
									from obs 
									join encounter e on e.encounter_id = obs.encounter_id
									where concept_id = ', CONCAT(cid),
									' and value_coded in (', vcid,
									') and e.voided = 0
									and obs.voided = 0				
									order by obs_datetime ', @upDown,') oi
								group by person_id) o 
								on o.person_id = wc.PID;');
	
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

END;;
DELIMITER ;

-- getEncounterLocationForCodedObs(cid, endDate, firstLast, colName)
-- INPUTS: 		cid - observation concept id
--				endDate - end Date of report
-- 				colName - temporary table to write to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets last obs for concept id before (or on) end date and writes observation to report 
-- table for cohort (one per patient).
-- Procedure assumes obs_datetime is relevant last date (may not be accurate for obs groups) and
-- that internal patient identifiers are supplied by the PID in the warehouse_ic3_cohort table.

DROP PROCEDURE IF EXISTS getEncounterLocationForCodedObs;

DELIMITER ;;

CREATE PROCEDURE getEncounterLocationForCodedObs(IN cid INT, IN vcid VARCHAR(255), IN endDate DATE, IN firstLast VARCHAR(50), IN colName VARCHAR(50))
BEGIN
	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs VARCHAR(50)
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getEncounterLocationForCodedObs!';
	END IF;

	SET @s=CONCAT('insert into temp_obs_vector
							(PID, obs)
					select PID, name
					from warehouse_ic3_cohort wc
					left join (select * from 
								(select person_id, name 
									from obs 
									join encounter e on e.encounter_id = obs.encounter_id
									join location l on l.location_id = e.location_id
									where concept_id = ', CONCAT(cid),
									' and value_coded in (', vcid,
									') and e.voided = 0
									and obs.voided = 0				
									order by obs_datetime ', @upDown,') oi
								group by person_id) o 
								on o.person_id = wc.PID;');
	
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	CALL updateReportTable(colName);

END;;
DELIMITER ;

-- getLastOutcomeForProgram(programId, endDate, colOutcomeName, colOutcomeDateName)
-- INPUTS: 		programId - program ID, ART=1, NCD=10
--				endDate - end Date of report
-- 				colOutcomeName - column name in the cohort table where to store last outcome value
--				colOutcomeNameDate - column name in the cohort table where to store the date of the last outcome
-- Procedure gets last program outcome before (or on) end date and writes outcome and outcome date to 
-- report table for cohort (one per patient).

DROP PROCEDURE IF EXISTS getLastOutcomeForProgram;

DELIMITER $$
CREATE PROCEDURE `getLastOutcomeForProgram`(IN programId INT, IN endDate DATE, IN colOutcomeName VARCHAR(100), IN colOutcomeDateName VARCHAR(100))
BEGIN
	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		lastOutcomeDate DATE default NULL,
		lastOutcome VARCHAR(255)
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	SET @s=CONCAT('insert into temp_obs_vector
					(PID, lastOutcomeDate, lastOutcome)
					select tc.PID, stateStartDate, patientState
					from warehouse_ic3_cohort tc
					left join (
						select * from 
								(select pid, stateStartDate, patientState
								from warehouse_program_enrollment w
								where programId=', programId, ' and stateStartDate <= @endDate		
								order by stateStartDate desc) ei
						group by pid) w 
					on tc.PID = w.PID ; ');
				
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	SET @u=CONCAT('UPDATE warehouse_ic3_cohort tc, temp_obs_vector tt SET tc.',colOutcomeDateName,' = tt.lastOutcomeDate, tc.',colOutcomeName,' = tt.lastOutcome WHERE tc.PID = tt.PID;');
	PREPARE stmt2 FROM @u;
	EXECUTE stmt2;
	DEALLOCATE PREPARE stmt2;
END$$
DELIMITER ;




