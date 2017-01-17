-- ## report_uuid = 897C0E0A-1F8A-4ABD-AFE2-054146227668
-- ## design_uuid = FFA51EA2-D483-43F2-9FE8-5B0AF619E8A0
-- ## report_name = IC3 Register Report
-- ## report_description = Report listing IC3 patients

-- Report lists all patients who are in enlisted in any of the CC programs or ART program.


-- Temporary variables for testing
set @reportEndDate = "2017-01-01";
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
  ic3EnrollmentDate DATE,
  ic3FirstProgramEnrolled VARCHAR(50),
  lastVisitDate DATE default NULL,
  lastVisitLocation VARCHAR(50) default NULL,
  birthdate DATE not NULL,
  gender VARCHAR(50) not NULL,
  ageAtFirstEnrollment INT(11),
  age INT(11) not NULL,
  artEnrollmentDate DATE,
  lastArtOutcome VARCHAR(255),
  lastArtOutcomeDate DATE, 
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
  ncdEnrollmentDate DATE default NULL,
  lastNcdOutcome VARCHAR(255),
  lastNcdOutcomeDate DATE, 
  firstHtnDxDate DATE default NULL,
  firstHtnMedsDate DATE default NULL,
  lastHtnMedsDate DATE default NULL,
  lastHtnMedsLocation VARCHAR(50) default NULL,
  firstBp VARCHAR(50) default NULL,
  firstBpDate DATE default NULL,
  lastBp VARCHAR(50) default NULL,  
  lastBpDate DATE default NULL,
  diuretic VARCHAR(50) default NULL,  
  calciumChannelBlocker VARCHAR(50) default NULL,  
  aceIInhibitor VARCHAR(50) default NULL,  
  betaBlocker VARCHAR(50) default NULL,  
  statin VARCHAR(50) default NULL,  
  otherHtnMeds VARCHAR(50) default NULL

);
CREATE INDEX PID_ic3_index ON warehouse_ic3_cohort(PID);

-- Create Initial Cohort With Basic Demographic Data

insert into warehouse_ic3_cohort
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

-- Call Routines
CALL updateProgramsEnrollmentDate();
CALL updateIc3EnrollmentInfo(@reportEndDate);
CALL updateFirstViralLoad();
CALL updateLastViralLoad();
CALL getAllIdentifiers(@reportEndDate,'4','allArtIds');
CALL getAllIdentifiers(@reportEndDate,'19','allPreArtIds');
CALL getAllIdentifiers(@reportEndDate,'21','allCccIds');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastVisitLocation');
-- get ART last outcome
CALL getLastOutcomeForProgram(1, @reportEndDate, 'lastArtOutcome', 'lastArtOutcomeDate');
-- get NCD last outcome
CALL getLastOutcomeForProgram(10, @reportEndDate, 'lastNcdOutcome', 'lastNcdOutcomeDate');
CALL getEncounterDatetimeBeforeEndDate('9,10', @reportEndDate, 'last', 'lastArtVisitDate');
CALL getEncounterDatetimeBeforeEndDate('9,10', @reportEndDate, 'first', 'firstArtVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10', @reportEndDate, 'last', 'lastArtVisitLocation');
CALL getEncounterDateForCodedObs(3683, '903', @reportEndDate, 'first', 'firstHtnDxDate');
CALL getEncounterDateForCodedObs(1193, '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463,88,8462', @reportEndDate, 'first', 'firstHtnMedsDate');
CALL getEncounterDateForCodedObs(1193, '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463', @reportEndDate, 'last', 'lastHtnMedsDate');
CALL getEncounterLocationForCodedObs(1193, '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463', @reportEndDate, 'last', 'lastHtnMedsLocation');

CALL getBloodPressureBeforeDate(@reportEndDate, 'first', 'firstBpDate', 'firstBp');
CALL getBloodPressureBeforeDate(@reportEndDate, 'last', 'lastBpDate', 'lastBp');

CALL updateRecentRegimen(@reportEndDate);
CALL getCodedObsFromEncounterBeforeDate(7459, '10', @reportEndDate, 'last', 'lastTbValue');
CALL getDatetimeObsBeforeDate(6132, @reportEndDate, 'last', 'artInitialDate');

CALL getCodedObsWithValuesFromEncounterBeforeDate(1193, '69,115', '3187,250,8466', @reportEndDate, 'last', 'diuretic');
CALL getCodedObsWithValuesFromEncounterBeforeDate(1193, '69,115', '3182,1242,3183,8465', @reportEndDate, 'last', 'calciumChannelBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate(1193, '69,115', '3186,254,8464', @reportEndDate, 'last', 'aceIInhibitor');
CALL getCodedObsWithValuesFromEncounterBeforeDate(1193, '69,115', '8463', @reportEndDate, 'last', 'betaBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate(1193, '69,115', '8463', @reportEndDate, 'last', 'statin');
CALL getCodedObsWithValuesFromEncounterBeforeDate(1193, '69,115', '88', @reportEndDate, 'last', 'otherHtnMeds');


 SELECT
      PID,
      identifier,
      allArtIds,
      allCccIds,
      ic3EnrollmentDate,
      ic3FirstProgramEnrolled,
      lastVisitDate,
      lastVisitLocation,
      birthdate,
      gender,
      ageAtFirstEnrollment,
      age,
      artEnrollmentDate,
      lastArtOutcome,
      lastArtOutcomeDate,
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
      ncdEnrollmentDate,
      lastNcdOutcome,
      lastNcdOutcomeDate,
      firstHtnDxDate,
      firstHtnMedsDate,
      lastHtnMedsDate,
      lastHtnMedsLocation,
      firstBp,
      firstBpDate,
      lastBp,  
      lastBpDate,
      diuretic,  
      calciumChannelBlocker,  
      aceIInhibitor,  
      betaBlocker,  
      statin,  
      otherHtnMeds

 FROM warehouse_ic3_cohort;
  

  




