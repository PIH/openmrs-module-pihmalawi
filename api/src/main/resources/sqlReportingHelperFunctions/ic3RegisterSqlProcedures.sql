-- This file represents a set of procedures used in the IC3 Register Report. 
--
-- Procedures Include: 
--			createIc3RegisterTable
--			createIc3RegisterCohort
--			warehouseProgramEnrollment
--			updateIc3EnrollmentInfo
--			updateRecentRegimen
--			updateProgramsEnrollmentDate
--			updateFirstViralLoad
--			updateLastViralLoad
--			getLastOutcomeForProgram
--			getBloodGlucoseBeforeDate
--			getBloodPressureBeforeDate


-- createIc3RegisterTable()
--
-- Procedure creates an empty table for IC3 register with columns and correct datatypes

DROP PROCEDURE IF EXISTS createIc3RegisterTable;

DELIMITER $$
CREATE PROCEDURE createIc3RegisterTable()
BEGIN
	-- Refresh temp table
	drop table if exists warehouseCohortTable;

	-- Create Table in which to store cohort
	create table warehouseCohortTable (
	  id INT not null auto_increment primary key,
	  PID INT(11) not NULL,
	  identifier VARCHAR(255) default NULL,
	  allPreArtIds VARCHAR(100) default NULL,
	  allArtIds VARCHAR(100) default NULL,
	  allCccIds VARCHAR(100) default NULL,
	  ic3EnrollmentDate DATE,
	  ic3FirstProgramEnrolled VARCHAR(255),
	  lastVisitDate DATE default NULL,
	  lastVisitLocation VARCHAR(255) default NULL,
	  birthdate DATE not NULL,
	  gender VARCHAR(255) not NULL,
	  ageAtFirstEnrollment INT(11),
	  age INT(11) not NULL,
	  hivAtLeastOneNcd VARCHAR(100) default NULL,
	  atLeastTwoNcds VARCHAR(100) default NULL,
	  htnAndDm VARCHAR(100) default NULL,
	  hivAndDm VARCHAR(100) default NULL,
	  artEnrollmentDate DATE,
	  lastArtOutcome VARCHAR(255),
	  lastArtOutcomeDate DATE, 
	  firstARTVisitDate DATE default NULL,
	  artInitialDate DATE default NULL,
	  lastArtVisitDate DATE default NULL,
	  lastArtVisitLocation VARCHAR(255) default NULL,
	  lastTbValue VARCHAR(255) default NULL,
	  firstViralLoadDate Date,
	  firstViralLoadResult NUMERIC,
	  lastViralLoadDate Date,
	  lastViralLoadResult NUMERIC,
	  lastArtRegimenStart DATE default NULL,
	  lastArtRegimen VARCHAR(255) default NULL, 
	  ncdEnrollmentDate DATE default NULL,
	  lastNcdOutcome VARCHAR(255),
	  lastNcdOutcomeDate DATE, 
	  firstHtnDxDate DATE default NULL,
	  firstHtnMedsDate DATE default NULL,
	  lastHtnMedsDate DATE default NULL,
	  lastHtnMedsLocation VARCHAR(255) default NULL,
	  firstBp VARCHAR(255) default NULL,
	  firstBpDate DATE default NULL,
	  lastBp VARCHAR(255) default NULL,  
	  lastBpDate DATE default NULL,
	  diuretic VARCHAR(255) default NULL,  
	  calciumChannelBlocker VARCHAR(255) default NULL,  
	  aceIInhibitor VARCHAR(255) default NULL,  
	  betaBlocker VARCHAR(255) default NULL,  
	  statin VARCHAR(255) default NULL,  
	  otherHtnMeds VARCHAR(255) default NULL,
	  firstDmDxDate DATE default NULL,
	  diabetesType VARCHAR(255) default NULL,
	  firstDmMedsDate DATE default NULL,
	  lastDmMedsDate DATE default NULL,
	  lastDmMedsLocation VARCHAR(255) default NULL,
	  firstGlucoseMonitoringDate DATE default NULL,
	  firstVisitHba1c NUMERIC default NULL,
	  firstVisitRandomBloodSugar NUMERIC default NULL, 
	  firstVisitFastingBloodSugar NUMERIC default NULL,
	  lastGlucoseMonitoringDate DATE default NULL,
	  lastVisitHba1c NUMERIC default NULL,
	  lastVisitRandomBloodSugar NUMERIC default NULL, 
	  lastVisitFastingBloodSugar NUMERIC default NULL,
	  shortActingRegularInsulin VARCHAR(255) default NULL,
	  longActingInsulin VARCHAR(255) default NULL,
	  metformin VARCHAR(255) default NULL,
	  glibenclamide VARCHAR(255) default NULL,
	  firstEpilepsyDxDate DATE default NULL,
	  firstEpilepsyMedsDate DATE default NULL,
	  lastEpilepsyMedsDate DATE default NULL,
	  lastEpilepsyMedsLocation VARCHAR(255) default NULL,
	  firstSeizuresDate DATE default NULL,
	  firstSeizures INT(11) default NULL,
	  lastSeizuresDate DATE default NULL,
	  lastSeizures INT(11) default NULL,
	  seizureTriggers VARCHAR(255) default NULL,
	  firstAsthmaDxDate DATE default NULL,
	  firstAsthmaMedsDate DATE default NULL,
	  lastAsthmaMedsDate DATE default NULL,
	  lastAsthmaMedsLocation VARCHAR(255) default NULL,
	  firstAsthmaSeverity VARCHAR(255) default NULL,
	  firstAsthmaSeverityDate DATE default NULL,
	  lastAsthmaSeverityDate DATE default NULL,
	  lastAsthmaSeverity VARCHAR(255) default NULL,
	  inhaledBAgonist VARCHAR(255) default NULL,
	  inhaledSteroid VARCHAR(255) default NULL,
	  oralSteroid VARCHAR(255) default NULL,
	  otherAsthmaMeds VARCHAR(255) default NULL,	  
	  copdDiagnosisDate DATE default NULL,
	  firstMentalHealthDxDate DATE default NULL,
	  firstMentalHealthMedsDate DATE default NULL,
	  lastMentalHealthMedsDate DATE default NULL,
	  lastMentalHealthMedsLocation VARCHAR(255) default NULL,
	  lastWeightDate DATE default NULL,
	  lastHeight NUMERIC default NULL,
	  lastWeight NUMERIC default NULL,
	  BMI NUMERIC default NULL

	);
	CREATE INDEX PID_ic3_index ON warehouseCohortTable(PID);
	

END$$
DELIMITER ;

-- createIc3RegisterCohort()
--
-- Procedure defines cohort and adds cohort (plus demographic data) to IC3 register table

DROP PROCEDURE IF EXISTS createIc3RegisterCohort;

DELIMITER $$
CREATE PROCEDURE createIc3RegisterCohort()
BEGIN

	-- Create Initial Cohort With Basic Demographic Data
	insert into warehouseCohortTable
				(PID, identifier, birthdate, gender, age)
	select		pp.patient_id, pi.identifier, p.birthdate, p.gender, floor(datediff(@reportEndDate,birthdate)/365)
	from	 	(select * from 
					(select patient_id, patient_program_id 
						from patient_program 
						where voided = 0 
						and program_id in (1,10) 
						and (date_enrolled <= @reportEndDate or date_enrolled is NULL)
						order by date_enrolled DESC) 
					ppi group by patient_id) pp -- Most recent Program Enrollment
	join 		(select patient_program_id 
					from patient_state 
					where voided = 0 
					and state in (1,7,83) 
					and start_date < @reportEndDate 
					group by patient_program_id) xps 
				on xps.patient_program_id = pp.patient_program_id -- Ensure have been On ARVs, Pre-ART (continue), and CC continue
	join 		(select * from person where voided = 0) p on p.person_id = pp.patient_id -- remove voided persons
	join 		(select patient_id, identifier from 
					(select * 
					from patient_identifier 
					where voided = 0 
					and identifier_type in (4, 19, 21) 
					order by date_created desc) pii 
				group by patient_id) pi 
				on pi.patient_id = pp.patient_id -- Ensure HCC/ARV/NCD identifier
	order by 	pp.patient_id asc;
	

END$$
DELIMITER ;

-- warehouseProgramEnrollment()
-- Creates a warehouse of program enrollment data

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
				getConceptName(pws.concept_id) as patientState,
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


-- updateIc3EnrollmentInfo()
-- Procedure that calculates the first enrollment date across the IC3 programs

DROP PROCEDURE IF EXISTS updateIc3EnrollmentInfo;

DELIMITER ;;
CREATE PROCEDURE `updateIc3EnrollmentInfo`(IN endDate DATE)
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
		programName VARCHAR(50),
  		dateEnrolled DATE default NULL
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	insert into temp_obs_vector(PID, programName, dateEnrolled)
		select PID, programName, dateEnrolled
		from (select * from 
		(select * from 
			warehouse_program_enrollment 
			where dateEnrolled < endDate
			order by dateEnrolled asc) wpei 
		group by PID) wpe;

	-- update ic3 enrollment info
	UPDATE warehouseCohortTable tc, temp_obs_vector tt
	SET tc.ic3EnrollmentDate = tt.dateEnrolled, 
		tc.ic3FirstProgramEnrolled = tt.programName,
		tc.ageAtFirstEnrollment=floor(datediff(tt.dateEnrolled,tc.birthdate)/365) 
	WHERE tc.PID = tt.PID ;

END;;
DELIMITER ;

-- updateRecentRegimen()
-- Updates most recent regimen and regimen start date on cohort table

DROP PROCEDURE IF EXISTS updateRecentRegimen;

DELIMITER ;;
CREATE PROCEDURE updateRecentRegimen(IN endDate DATE)
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
			and obs_datetime < endDate
			and voided = 0 
			order by obs_datetime desc) oi 
			group by oi.pid) o
	where pid in (select PID from warehouseCohortTable);

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
	select pid, getConceptName(recentRegimen) as recentRegimen, obs_datetime as recentRegimenStart
		from (select * 
				from obs o
				join recentRegimenObs rro 
				on rro.pid = o.person_id 
				and rro.recentRegimen = o.value_coded 
				where concept_id = 8169
				order by obs_datetime asc) oi
				group by oi.person_id;
	
	UPDATE warehouseCohortTable tc, temp_obs_vector tt 
	SET tc.lastArtRegimenStart = tt.recentRegimenStart
	WHERE tc.PID = tt.PID;
	
	UPDATE warehouseCohortTable tc, temp_obs_vector tt 	
	SET tc.lastArtRegimen = tt.recentRegimen 
	WHERE tc.PID = tt.PID;

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
	UPDATE warehouseCohortTable tc, temp_obs_vector tt
	SET tc.artEnrollmentDate = tt.dateEnrolled WHERE tc.PID = tt.PID and tt.programId=1;

	-- update NCD enrollment date
	UPDATE warehouseCohortTable tc, temp_obs_vector tt
	SET tc.ncdEnrollmentDate = tt.dateEnrolled WHERE tc.PID = tt.PID and tt.programId=10;

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
		from warehouseCohortTable tt
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

		UPDATE warehouseCohortTable tc
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
		from warehouseCohortTable tt
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

		UPDATE warehouseCohortTable tc
		set lastViralLoadDate=insert_visitDate, lastViralLoadResult = insert_numeric
		where PID = v_patientId;

	end loop viralLoop;
    close viralLoadCur;

END$$

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
					from warehouseCohortTable tc
					left join (
						select * from 
								(select pid, stateStartDate, patientState
								from warehouse_program_enrollment w
								where programId=', programId, ' and stateStartDate <= \'', endDate, 
								'\' order by stateStartDate desc) ei
						group by pid) w 
					on tc.PID = w.PID ; ');
				
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;
	SET @s = NULL;

	SET @u=CONCAT('UPDATE warehouseCohortTable tc, temp_obs_vector tt SET tc.',colOutcomeDateName,' = tt.lastOutcomeDate, tc.',colOutcomeName,' = tt.lastOutcome WHERE tc.PID = tt.PID;');
	PREPARE stmt2 FROM @u;
	EXECUTE stmt2;
	DEALLOCATE PREPARE stmt2;
	SET @u = NULL;

END$$
DELIMITER ;

-- getBloodGlucoseBeforeDate(endDate, first/last, colName1, colName2, colName3, colName4)
-- INPUTS:		endDate - end Date of report
-- 				colObsDate - column to write obs date to
-- 				colHba1c - column to write hba1c obs to
-- 				colRandom - column to write random glucose obs to
-- 				colFast - column to write fasting glucose obs to
--				firstLast - either 'first' or 'last' to specify first/last test
-- Procedure gets first/last blood glucose measurements for a cohort of patients. Needed to write this
-- specialized function, because of some changes in the way blood glucose is recorded - this reports
-- the observations back in a consistent way. 

DROP PROCEDURE IF EXISTS getBloodGlucoseBeforeDate;

DELIMITER ;;
CREATE PROCEDURE getBloodGlucoseBeforeDate(IN endDate DATE, IN firstLast VARCHAR(50), IN colObsDate VARCHAR(100), IN colHba1c VARCHAR(100), IN colRandom VARCHAR(100), IN colFast VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obsDate DATE default NULL,
  		hba1c NUMERIC default NULL,
  		fasting NUMERIC default NULL,
  		random NUMERIC default NULL
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getBloodGlucoseBeforeDate!';
	END IF;

	set @s=CONCAT('insert into temp_obs_vector
						(PID, obsDate, hba1c, fasting, random)
	select oAll.person_id as PID, 
		oAll.obs_datetime as obsDate, 
		oHba1c.value_numeric as hba1c,
		CASE WHEN value_coded = 6379 
			THEN oGen.value_numeric 
		WHEN oFast.value_numeric is NOT NULL
			THEN oFast.value_numeric
		ELSE NULL
		END AS fasting,
		CASE WHEN value_coded IS NULL AND oGen.value_numeric IS NOT NULL
			THEN oGen.value_numeric 
		WHEN oRandom.value_numeric is NOT NULL
			THEN oRandom.value_numeric
		ELSE NULL		
		END AS random
	from (select * from 
			(select person_id, encounter_id, obs_datetime, value_numeric 
			from obs 
			where concept_id in (887,8447,8448,6422) 
			and obs_datetime <= \'', endDate, '\'
			and voided = 0 order by obs_datetime ', @upDown,
			') aAlli 
			group by person_id) oAll 
	left join (select person_id, encounter_id, value_coded 
				from obs 
				where concept_id = 6381 
				and value_coded = 6379 
				and voided = 0) oCheck 
				on oCheck.encounter_id = oAll.encounter_id
	left join (select person_id, encounter_id, value_numeric 
				from obs 
				where concept_id = 887 
				and voided = 0) oGen on oGen.encounter_id = oAll.encounter_id
	left join (select person_id, encounter_id, value_numeric 
				from obs 
				where concept_id = 8447 
				and voided = 0) oRandom 
				on oRandom.encounter_id = oAll.encounter_id
	left join (select person_id, encounter_id, value_numeric 
				from obs 
				where concept_id = 8448 
				and voided = 0) oFast 
				on oFast.encounter_id = oAll.encounter_id
	left join (select person_id, encounter_id, value_numeric 
				from obs 
				where concept_id = 6422 
				and voided = 0) oHba1c 
				on oHba1c.encounter_id = oAll.encounter_id;');
	
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	set @s=CONCAT('UPDATE warehouseCohortTable tc, temp_obs_vector tt 
					SET tc.', colObsDate,' = tt.obsDate
					WHERE tc.PID = tt.PID;');

	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	set @s=CONCAT('UPDATE warehouseCohortTable tc, temp_obs_vector tt 
					SET tc.', colHba1c,' = tt.hba1c
					WHERE tc.PID = tt.PID;');

	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;	

	set @s=CONCAT('UPDATE warehouseCohortTable tc, temp_obs_vector tt 
					SET tc.', colRandom,' = tt.random
					WHERE tc.PID = tt.PID;');

	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;				

	set @s=CONCAT('UPDATE warehouseCohortTable tc, temp_obs_vector tt 
					SET tc.', colFast,' = tt.fasting
					WHERE tc.PID = tt.PID;');	

	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;	
	SET @s = NULL;					

END;;
DELIMITER ;

-- getBloodPressureBeforeDate(endDate, first/last, colName1, colName2)
-- INPUTS: 		cid - observation concept id
--				endDate - end Date of report
-- 				colName - temporary table to write to
--				firstLast - either 'first' or 'last' to specify first/last encounter
-- Procedure gets last systolic and diastolic values (from same encounter) and puts them together
-- in a string and writes out to the reporting table. 

DROP PROCEDURE IF EXISTS getBloodPressureBeforeDate;

DELIMITER ;;
CREATE PROCEDURE getBloodPressureBeforeDate(IN endDate DATE, IN firstLast VARCHAR(50), IN bpDateCol VARCHAR(100), IN bpCol VARCHAR(100))
BEGIN

	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;
	create temporary table temp_obs_vector (
  		id INT not null auto_increment primary key,
  		PID INT(11) not NULL,
  		obs_datetime DATE default NULL,
  		obs VARCHAR(100)
	);
	CREATE INDEX PID_index ON temp_obs_vector (PID);

	IF firstLast = 'first' THEN
		       set @upDown = 'asc';
	ELSEIF firstLast = 'last' THEN
	       set @upDown = 'desc';
	ELSE select 'Must specify first/last encounter in getBloodPressureBeforeDate!';
	END IF;

	SET @s=CONCAT('insert into temp_obs_vector
							(PID, obs_datetime, obs)
					select o.person_id, e.encounter_datetime as obs_datetime, concat_ws(\'/\',o1.value_numeric, o2.value_numeric) as obs 
					from (select * 
							from (select person_id, encounter_id 
									from obs 
									where concept_id in (5085,5086)
									and voided = 0 
									order by obs_datetime ', @upDown, ') 
							oi group by person_id) o
					join (select encounter_id, encounter_datetime 
							from encounter 
							where encounter_datetime <= (\'', CONCAT(endDate),'\')
							and voided = 0) e 
							on e.encounter_id = o.encounter_id
					left join (select encounter_id, value_numeric 
								from obs 
								where concept_id = 5085) o1 
								on o1.encounter_id = e.encounter_id
					left join (select encounter_id, value_numeric 
								from obs 
								where concept_id = 5086) o2 
								on o2.encounter_id = e.encounter_id;');
	
	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	set @s=CONCAT('UPDATE warehouseCohortTable tc, temp_obs_vector tt 
					SET tc.', bpDateCol,' = tt.obs_datetime
					WHERE tc.PID = tt.PID;');

	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;

	set @s=CONCAT('UPDATE warehouseCohortTable tc, temp_obs_vector tt 
					SET tc.', bpCol,' = tt.obs
					WHERE tc.PID = tt.PID;');

	PREPARE stmt1 FROM @s;
	EXECUTE stmt1;
	DEALLOCATE PREPARE stmt1;					

END;;
DELIMITER ;


-- diagnosesLogic()
-- 

DROP PROCEDURE IF EXISTS diagnosesLogic;

DELIMITER ;;
CREATE PROCEDURE diagnosesLogic()
BEGIN
	DROP TEMPORARY TABLE IF EXISTS temp_obs_vector;

	create temporary table temp_obs_vector as 
	select PID, 
			HIV, 
			HTN, 
			DM, 
			EPILEPSY, 
			ASTHMA, 
			MENTAL, 
			(HTN + DM + EPILEPSY + ASTHMA + MENTAL) AS totalNCDs,
			CASE WHEN HIV=1 AND (HTN + DM + EPILEPSY + ASTHMA + MENTAL) > 0 
				THEN "yes" 
				ELSE "no" 
			END AS hivAtLeastOneNcd,
			CASE WHEN (HTN + DM + EPILEPSY + ASTHMA + MENTAL) > 1 
				THEN "yes" 
				ELSE "no" 
			END AS atLeastTwoNcds,
			CASE WHEN (HTN + DM) = 2 
				THEN "yes" 
				ELSE "no" 
			END AS htnAndDm,
			CASE WHEN (HIV + DM) = 2 
				THEN "yes" 
				ELSE "no" 
			END AS hivAndDm
	from (select PID,
			CASE WHEN artEnrollmentDate IS NOT NULL 
				THEN 1 
				ELSE 0 
			END AS HIV,
			CASE WHEN firstHtnMedsDate IS NOT NULL 
				THEN 1 
				ELSE 0 
			END as HTN,
			CASE WHEN firstDmDxDate IS NOT NULL 
				THEN 1 
				ELSE 0 
			END as DM,
			CASE WHEN firstEpilepsyMedsDate IS NOT NULL 
				THEN 1 
				ELSE 0 
			END as EPILEPSY,
			CASE WHEN firstAsthmaMedsDate IS NOT NULL 
				THEN 1 
				ELSE 0 
			END as ASTHMA,
			CASE WHEN firstMentalHealthMedsDate IS NOT NULL 
				THEN 1 
				ELSE 0 
			END as MENTAL						
			from warehouseCohortTable) it;

	UPDATE warehouseCohortTable tc, temp_obs_vector tt 
	SET tc.hivAtLeastOneNcd = tt.hivAtLeastOneNcd
	WHERE tc.PID = tt.PID;

	UPDATE warehouseCohortTable tc, temp_obs_vector tt 
	SET tc.atLeastTwoNcds = tt.atLeastTwoNcds
	WHERE tc.PID = tt.PID;	

	UPDATE warehouseCohortTable tc, temp_obs_vector tt 
	SET tc.htnAndDm = tt.htnAndDm
	WHERE tc.PID = tt.PID;	

	UPDATE warehouseCohortTable tc, temp_obs_vector tt 
	SET tc.hivAndDm = tt.hivAndDm
	WHERE tc.PID = tt.PID;	


END;;
DELIMITER ;