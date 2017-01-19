-- ## report_uuid = 897C0E0A-1F8A-4ABD-AFE2-054146227668
-- ## design_uuid = FFA51EA2-D483-43F2-9FE8-5B0AF619E8A0
-- ## report_name = IC3 Register Report
-- ## report_description = Report listing IC3 patients
-- ## parameter = reportEndDate|End Date|java.util.Date

-- Report lists all patients who are in enlisted in any of the CC programs or ART program.

-- Create an empty table
CALL createIc3RegisterTable();
-- Create cohort with demographic data
CALL createIc3RegisterCohort();


-- Call Routines to fill columns
-- ---------------------------
-- Warehousing 
CALL warehouseProgramEnrollment();
-- Demographics
CALL getAllIdentifiers(@reportEndDate,'4','allArtIds');
CALL getAllIdentifiers(@reportEndDate,'19','allPreArtIds');
CALL getAllIdentifiers(@reportEndDate,'21','allCccIds');
-- General Visits and outcomes
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastVisitLocation');
CALL updateIc3EnrollmentInfo(@reportEndDate);
CALL updateProgramsEnrollmentDate();
-- HIV Program Information
CALL updateFirstViralLoad();
CALL updateLastViralLoad();
CALL getLastOutcomeForProgram(1, @reportEndDate, 'lastArtOutcome', 'lastArtOutcomeDate');
CALL getLastOutcomeForProgram(10, @reportEndDate, 'lastNcdOutcome', 'lastNcdOutcomeDate');
CALL getEncounterDatetimeBeforeEndDate('9,10', @reportEndDate, 'last', 'lastArtVisitDate');
CALL getEncounterDatetimeBeforeEndDate('9,10', @reportEndDate, 'first', 'firstArtVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10', @reportEndDate, 'last', 'lastArtVisitLocation');
CALL getDatetimeObsBeforeDate(6132, @reportEndDate, 'last', 'artInitialDate');
CALL updateRecentRegimen(@reportEndDate);
CALL getCodedObsFromEncounterBeforeDate(7459, '10', @reportEndDate, 'last', 'lastTbValue');
-- Hypertension Information
CALL getDiagnosisDate(3683, '903', 6774, @reportEndDate, 'firstHtnDxDate');
CALL getEncounterDateForCodedObs('1193', '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463,88,8462', @reportEndDate, 'first', 'firstHtnMedsDate');
CALL getEncounterDateForCodedObs('1193', '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463', @reportEndDate, 'last', 'lastHtnMedsDate');
CALL getEncounterLocationForCodedObs('1193', '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463', @reportEndDate, 'last', 'lastHtnMedsLocation');
CALL getBloodPressureBeforeDate(@reportEndDate, 'first', 'firstBpDate', 'firstBp');
CALL getBloodPressureBeforeDate(@reportEndDate, 'last', 'lastBpDate', 'lastBp');
-- Hypertension Meds
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '3187,250,8466', @reportEndDate, 'last', 'diuretic');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '3182,1242,3183,8465', @reportEndDate, 'last', 'calciumChannelBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '3186,254,8464', @reportEndDate, 'last', 'aceIInhibitor');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '8463', @reportEndDate, 'last', 'betaBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '8462', @reportEndDate, 'last', 'statin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '88', @reportEndDate, 'last', 'otherHtnMeds');
-- Diabetes Information
CALL getDiagnosisDate(3683, '6409,6410,3720', 6774, @reportEndDate, 'firstDmDxDate');
CALL getCodedObsWithValuesFromEncounterBeforeDate('3683', '29', '6409,6410', @reportEndDate, 'last', 'diabetesType');
CALL getEncounterDateForCodedObs('1193', '4052,8413,4046', @reportEndDate, 'first', 'firstDmMedsDate');
CALL getEncounterDateForCodedObs('1193', '4052,8413,4046', @reportEndDate, 'last', 'lastDmMedsDate');
CALL getEncounterLocationForCodedObs('1193', '4052,8413,4046', @reportEndDate, 'last', 'lastDmMedsLocation');
CALL getBloodGlucoseBeforeDate(@reportEndDate, 'first', 'firstGlucoseMonitoringDate','firstVisitHba1c','firstVisitRandomBloodSugar','firstVisitFastingBloodSugar');
CALL getBloodGlucoseBeforeDate(@reportEndDate, 'last', 'lastGlucoseMonitoringDate','lastVisitHba1c','lastVisitRandomBloodSugar','lastVisitFastingBloodSugar');
-- Diabetes Meds
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '282', @reportEndDate, 'last', 'shortActingRegularInsulin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '6750', @reportEndDate, 'last', 'longActingInsulin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '4052', @reportEndDate, 'last', 'metformin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '4046', @reportEndDate, 'last', 'glibenclamide');
-- Epilepsy Information
CALL getDiagnosisDate(3683, '155', 6774, @reportEndDate, 'firstEpilepsyDxDate');
CALL getEncounterDateForCodedObs('1193', '238,273,920', @reportEndDate, 'first', 'firstEpilepsyMedsDate');
CALL getEncounterDateForCodedObs('1193', '238,273,920', @reportEndDate, 'last', 'lastEpilepsyMedsDate');
CALL getEncounterLocationForCodedObs('1193', '238,273,920', @reportEndDate, 'last', 'lastEpilepsyMedsLocation');
CALL getEncounterDateForObs(7924, @reportEndDate, 'first', 'firstSeizuresDate');
CALL getNumericObsBeforeDate(7924, @reportEndDate, 'first', 'firstSeizures');
CALL getEncounterDateForObs(7924, @reportEndDate, 'last', 'lastSeizuresDate');
CALL getNumericObsBeforeDate(7924, @reportEndDate, 'last', 'lastSeizures');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '123', '8531,8532,8533,8534,8535,8536,8537', @reportEndDate, 'last', 'seizureTriggers');
-- Asthma Information (added COPD)
CALL getDiagnosisDate(3683, '5', 6774, @reportEndDate, 'firstAsthmaDxDate');
CALL getEncounterDateForCodedObs('1193,8474', '798,1240,8471,8472,8473,5622', @reportEndDate, 'first', 'firstAsthmaMedsDate');
CALL getEncounterDateForCodedObs('1193,8474', '798,1240,8471,8472,8473,5622', @reportEndDate, 'last', 'lastAsthmaMedsDate');
CALL getEncounterLocationForCodedObs('1193,8474', '798,1240,8471,8472,8473,5622', @reportEndDate, 'last', 'lastAsthmaMedsLocation');
CALL getEncounterDateForCodedObs('8410', '1905,8405,8406,8407,8408,8409', @reportEndDate, 'first', 'firstAsthmaSeverityDate');
CALL getCodedObsBeforeDate(8410, @reportEndDate, 'first', 'firstAsthmaSeverity');
CALL getEncounterDateForCodedObs('8410', '1905,8405,8406,8407,8408,8409', @reportEndDate, 'last', 'lastAsthmaSeverityDate');
CALL getCodedObsBeforeDate(8410, @reportEndDate, 'last', 'lastAsthmaSeverity');
CALL getDiagnosisDate(3683, '3716', 6774, @reportEndDate, 'copdDiagnosisDate');
-- Asthma Meds
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '8471,978', @reportEndDate, 'last', 'inhaledBAgonist');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '8472,1240', @reportEndDate, 'last', 'inhaledSteroid');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '8473', @reportEndDate, 'last', 'oralSteroid');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '5622', @reportEndDate, 'last', 'otherAsthmaMeds');

-- Mental Health Information
CALL getDiagnosisDate(3683, '467,207,8419,8487,2719,8488,8489,8562,8563,8491,8420,8580,8581', 6774, @reportEndDate, 'firstMentalHealthDxDate');
CALL getEncounterDateForCodedObs('1193', '914,4047,927,920,920,4060,8498,4045,8582,8583,8237,8584,6408', @reportEndDate, 'first', 'firstMentalHealthMedsDate');
CALL getEncounterDateForCodedObs('1193', '914,4047,927,920,920,4060,8498,4045,8582,8583,8237,8584,6408', @reportEndDate, 'last', 'lastMentalHealthMedsDate');        
CALL getEncounterLocationForCodedObs('1193', '914,4047,927,920,920,4060,8498,4045,8582,8583,8237,8584,6408', @reportEndDate, 'last', 'lastMentalHealthMedsLocation');
-- BMI Information
CALL getEncounterDateForObs(5089, @reportEndDate, 'last', 'lastWeightDate');
CALL getNumericObsBeforeDate(5090, @reportEndDate, 'last', 'lastHeight');
CALL getNumericObsBeforeDate(5089, @reportEndDate, 'last', 'lastWeight');
-- Diagnoses Logic
CALL diagnosesLogic(); -- Must be last!


-- Print report using select - update any column names or reorder here
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
  hivAtLeastOneNcd,
  atLeastTwoNcds,
  htnAndDm,
  hivAndDm,
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
  otherHtnMeds,
  firstDmDxDate,
  diabetesType,
  firstDmMedsDate,
  lastDmMedsDate,
  lastDmMedsLocation,
  firstGlucoseMonitoringDate,
  firstVisitHba1c,
  firstVisitRandomBloodSugar,
  firstVisitFastingBloodSugar,
  lastGlucoseMonitoringDate,
  lastVisitHba1c,
  lastVisitRandomBloodSugar,
  lastVisitFastingBloodSugar,
  shortActingRegularInsulin,
  longActingInsulin,
  metformin,
  glibenclamide,
  firstEpilepsyDxDate,
  firstEpilepsyMedsDate,
  lastEpilepsyMedsDate,
  lastEpilepsyMedsLocation,
  firstSeizuresDate,
  firstSeizures,
  lastSeizuresDate,
  lastSeizures,
  seizureTriggers,
  firstAsthmaDxDate,
  firstAsthmaMedsDate,
  lastAsthmaMedsDate,
  lastAsthmaMedsLocation,
  firstAsthmaSeverityDate,
  firstAsthmaSeverity,
  lastAsthmaSeverityDate,
  lastAsthmaSeverity,
  inhaledBAgonist,
  inhaledSteroid,
  oralSteroid,
  otherAsthmaMeds,
  copdDiagnosisDate,
  firstMentalHealthDxDate,
  firstMentalHealthMedsDate,
  lastMentalHealthMedsDate,
  lastMentalHealthMedsLocation,
  lastWeightDate,
  lastHeight,
  lastWeight,
  round(lastWeight/POWER(lastHeight/100,2),1) as bmi
FROM warehouseCohortTable;







