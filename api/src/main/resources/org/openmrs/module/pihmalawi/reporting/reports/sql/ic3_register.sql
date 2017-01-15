-- ## report_uuid = 897C0E0A-1F8A-4ABD-AFE2-054146227668
-- ## design_uuid = FFA51EA2-D483-43F2-9FE8-5B0AF619E8A0
-- ## report_name = IC3 Register Report
-- ## report_description = Report listing IC3 patients

-- Report lists all patients who are in enlisted in any of the CC programs or ART program.


-- Temporary variables for testing
set @endDate = "2017-01-01";
-- SET @@group_concat_max_len = 15000;

CALL warehouseProgramEnrollment();

-- Refresh temp table
drop table if exists warehouse_ic3_cohort;

-- Create Table in which to store cohort
create table warehouse_ic3_cohort (
  id INT not null auto_increment primary key,
  PID INT(11) not NULL,
  identifier VARCHAR(50) default NULL,
  allPreArtIds VARCHAR(100) default NULL,
  allArtIds VARCHAR(100) default NULL,
  allCccIds VARCHAR(100) default NULL,
  lastVisitDate DATE default NULL,
  lastVisitLocation VARCHAR(50) default NULL,
  birthdate DATE not NULL,
  gender VARCHAR(50) not NULL,
  age INT(11) not NULL,
  artEnrollmentDate DATE,
  firstARTVisitDate DATE default NULL,
  artInitialDate DATE default NULL,
  lastArtVisitDate DATE default NULL,
  lastArtVisitLocation VARCHAR(50) default NULL,
  lastTbValue VARCHAR(255) default NULL,
  firstViralLoadDate Date,
  firstViralLoadResult NUMERIC,
  lastViralLoadDate Date,
  lastViralLoadResult NUMERIC,
  lastArtRegimenStart DATE default NULL,
  lastArtRegimen VARCHAR(255) default NULL, 
  ncdEnrollmentDate DATE,

  SERUM_GLUCOSE NUMERIC default NULL,
  SYSTOLIC NUMERIC default NULL,
  DIASTOLIC NUMERIC default NULL

);
CREATE INDEX PID_ic3_index ON warehouse_ic3_cohort(PID);

-- Create Initial Cohort With Basic Demographic Data

insert into warehouse_ic3_cohort
			(PID, identifier, birthdate, gender, age)
select		pp.patient_id, pi.identifier, p.birthdate, p.gender, floor(datediff(@endDate,birthdate)/365)
from	 	(select * from 
				(select patient_id, patient_program_id 
					from patient_program 
					where voided = 0 
					and program_id in (1,10) 
					and (date_enrolled <= @endDate or date_enrolled is NULL)
					order by date_enrolled DESC) 
				ppi group by patient_id) pp -- Most recent Program Enrollment
join 		(select patient_program_id 
				from patient_state 
				where voided = 0 
				and state in (1,7,83) 
				and start_date < @endDate 
				group by patient_program_id) xps 
			on xps.patient_program_id = pp.patient_program_id -- Ensure have been On ARVs, Pre-ART (continue), and CC continue
join 		(select * from person where voided = 0) p on p.person_id = pp.patient_id -- remove voided persons
join 		(select * 
				from encounter 
				where encounter_datetime <= @endDate 
				and encounter_type in (9,10,11,12,67,69,29,115,118,119,122,123,124,125) 
				and voided = 0 
				group by patient_id) e 
			on e.patient_id = pp.patient_id
join 		(select patient_id, identifier from 
				(select * 
				from patient_identifier 
				where voided = 0 
				and identifier_type in (4, 19, 21) 
				order by date_created desc) pii 
			group by patient_id) pi 
			on pi.patient_id = pp.patient_id -- Ensure HCC/ARV/NCD identifier
order by 	pp.patient_id asc;

-- Call Routines
CALL updateProgramsEnrollmentDate();
CALL updateFirstViralLoad();
CALL updateLastViralLoad();
CALL getNumericObsBeforeDate(887, @endDate, 'last','SERUM_GLUCOSE');
CALL getNumericObsBeforeDate(5085, @endDate, 'last','SYSTOLIC');
CALL getNumericObsBeforeDate(5086, @endDate, 'last','DIASTOLIC');
CALL getAllIdentifiers(@endDate,'4','allArtIds');
CALL getAllIdentifiers(@endDate,'19','allPreArtIds');
CALL getAllIdentifiers(@endDate,'21','allCccIds');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @endDate, 'last', 'lastVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @endDate, 'last', 'lastVisitLocation');
CALL getEncounterDatetimeBeforeEndDate('9,10', @endDate, 'last', 'lastArtVisitDate');
CALL getEncounterDatetimeBeforeEndDate('9,10', @endDate, 'first', 'firstArtVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10', @endDate, 'last', 'lastArtVisitLocation');

CALL updateRecentRegimen();
CALL getCodedObsBeforeDate(7459, @endDate, 'last', 'lastTbValue');
CALL getDatetimeObsBeforeDate(6132, @endDate, 'last', 'artInitialDate');


 SELECT
      PID as PID,
      identifier,
      allArtIds,
      allCccIds,
      lastVisitDate,
      lastVisitLocation,
      birthdate,
      gender,
      age,
      artEnrollmentDate,
      firstARTVisitDate,
      artInitialDate,
      lastARTVisitDate,
      lastArtVisitLocation,
      lastTbValue,
      firstViralLoadDate,
      firstViralLoadResult,
      lastViralLoadDate,
      lastViralLoadResult,
      lastArtRegimenStart,
      lastArtRegimen,
      ncdEnrollmentDate
      -- SERUM_GLUCOSE,
      -- SYSTOLIC,
      -- DIASTOLIC
 FROM warehouse_ic3_cohort;
  
  




