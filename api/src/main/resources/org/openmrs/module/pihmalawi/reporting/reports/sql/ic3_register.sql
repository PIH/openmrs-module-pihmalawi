-- ## report_uuid = 897C0E0A-1F8A-4ABD-AFE2-054146227668
-- ## design_uuid = FFA51EA2-D483-43F2-9FE8-5B0AF619E8A0
-- ## report_name = IC3 Register Report
-- ## report_description = Report listing IC3 patients
-- ## parameter = reportEndDate|End Date|java.util.Date

-- Report lists all patients who are in enlisted in any of the CC programs or ART program.

-- Create an empty table
CALL createIc3RegisterTable();
-- Create cohort with demographic data
CALL createIc3RegisterCohort(@reportEndDate);


-- Call Routines to fill columns
-- ---------------------------
-- Warehousing 
CALL warehouseProgramEnrollment();
-- Demographics
CALL getAllIdentifiers(@reportEndDate,'4','allArtIds');
CALL getAllIdentifiers(@reportEndDate,'19','allPreArtIds');
CALL getAllIdentifiers(@reportEndDate,'21','allCccIds');
-- General Visits and outcomes
CALL getEncounterDatetimeBeforeEndDate('67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastNcdVisitDate');
CALL getEncounterLocationBeforeEndDate('67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastNcdVisitLocation');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12', @reportEndDate, 'first', 'firstHivVisitDate');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12', @reportEndDate, 'last', 'lastHivVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12', @reportEndDate, 'last', 'lastHivVisitLocation');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12,67,69,29,115,118,119,122,123,124,125', @reportEndDate, 'last', 'lastVisitLocation');
CALL getEncounterDatetimeBeforeEndDate('29,115', @reportEndDate, 'last', 'lastHtnDmVisitDate');
CALL getEncounterDatetimeBeforeEndDate('122,123', @reportEndDate, 'last', 'lastEpilepsyVisitDate');
CALL getEncounterDatetimeBeforeEndDate('118,119', @reportEndDate, 'last', 'lastChronicLungVisitDate');
CALL getEncounterDatetimeBeforeEndDate('124,125', @reportEndDate, 'last', 'lastMentalHealthVisitDate');
CALL updateIc3EnrollmentInfo(@reportEndDate);
CALL updateProgramsEnrollmentDate();
-- HIV Program Information
CALL updateFirstViralLoad(@reportEndDate);
CALL updateLastViralLoad(@reportEndDate);
CALL getLastOutcomeForProgram(1, @reportEndDate, 'lastHivOutcome', 'lastHivOutcomeDate');
CALL getLastOutcomeForProgram(10, @reportEndDate, 'lastNcdOutcome', 'lastNcdOutcomeDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12', @reportEndDate, 'last', 'lastHivVisitLocation');
CALL getDatetimeObsBeforeDate(6132, @reportEndDate, 'last', 'artInitialDate');
CALL updateRecentRegimen(@reportEndDate);
CALL getCodedObsFromEncounterBeforeDate(7459, '9,10,11,12', @reportEndDate, 'last', 'lastTbValueInHiv');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12', @reportEndDate, 'last', 'lastTbDateInHiv');

-- Hypertension Information
CALL getDiagnosisBoolean(3683, '903', @reportEndDate, 'htnDx');
CALL getDiagnosisDate(3683, '903', 6774, @reportEndDate, 'first', 'firstHtnDxDate');
CALL getEncounterDateForCodedObs('1193', '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463,88,8462', @reportEndDate, 'first', 'firstHtnMedsDate');
CALL getEncounterDateForCodedObs('1193', '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463', @reportEndDate, 'last', 'lastHtnMedsDate');
CALL getEncounterLocationForCodedObs('1193', '3182,3187,1242,250,3186,3183,254,8466,8465,8464,8463', @reportEndDate, 'last', 'lastHtnMedsLocation');
CALL getBloodPressureBeforeDate(@reportEndDate, 'first', 'firstBpDate', 'firstBp');
CALL getBloodPressureBeforeDate(@reportEndDate, 'last', 'lastBpDate', 'lastBp');
-- Hypertension Meds
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '8466,1243,99,4061', @reportEndDate, 'last', 'diuretic');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '8465,3187,250', @reportEndDate, 'last', 'calciumChannelBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '8464,1242,3182,3183', @reportEndDate, 'last', 'aceIInhibitor');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '8463,3186,8612,254', @reportEndDate, 'last', 'betaBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '8462,8613,8614,8210', @reportEndDate, 'last', 'statin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,115', '88,929,7121', @reportEndDate, 'last', 'otherHtnMeds');
-- Diabetes Information
CALL getDiagnosisBoolean(3683, '6409,6410,3720', @reportEndDate, 'dmDx');
CALL getDiagnosisDate(3683, '6409,6410,3720', 6774, @reportEndDate, 'first', 'firstDmDxDate');
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
CALL getDiagnosisBoolean(3683, '155', @reportEndDate, 'epilepsyDx');
CALL getDiagnosisDate(3683, '155', 6774, @reportEndDate, 'first', 'firstEpilepsyDxDate');
CALL getEncounterDateForCodedObs('1193', '238,273,920', @reportEndDate, 'first', 'firstEpilepsyMedsDate');
CALL getEncounterDateForCodedObs('1193', '238,273,920', @reportEndDate, 'last', 'lastEpilepsyMedsDate');
CALL getEncounterLocationForCodedObs('1193', '238,273,920', @reportEndDate, 'last', 'lastEpilepsyMedsLocation');
CALL getEncounterDateForObs(7924, @reportEndDate, 'first', 'firstSeizuresDate');
CALL getNumericObsBeforeDate(7924, @reportEndDate, 'first', 'firstSeizures');
CALL getEncounterDateForObs(7924, @reportEndDate, 'last', 'lastSeizuresDate');
CALL getNumericObsBeforeDate(7924, @reportEndDate, 'last', 'lastSeizures');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '123', '8531,8532,8533,8534,8535,8536,8537', @reportEndDate, 'last', 'seizureTriggers');
-- Asthma Information (added COPD)
CALL getDiagnosisBoolean(3683, '5', @reportEndDate, 'asthmaDx');
CALL getDiagnosisDate(3683, '5', 6774, @reportEndDate, 'first', 'firstAsthmaDxDate');
CALL getEncounterDateForCodedObs('1193,8474', '798,1240,8471,8472,8473,5622', @reportEndDate, 'first', 'firstChronicLungMedsDate');
CALL getEncounterDateForCodedObs('1193,8474', '798,1240,8471,8472,8473,5622', @reportEndDate, 'last', 'lastChronicLungMedsDate');
CALL getEncounterLocationForCodedObs('1193,8474', '798,1240,8471,8472,8473,5622', @reportEndDate, 'last', 'lastChronicLungMedsLocation');
CALL getEncounterDateForCodedObs('8410', '1905,8405,8406,8407,8408,8409', @reportEndDate, 'first', 'firstAsthmaSeverityDate');
CALL getCodedObsBeforeDate(8410, @reportEndDate, 'first', 'firstAsthmaSeverity');
CALL getEncounterDateForCodedObs('8410', '1905,8405,8406,8407,8408,8409', @reportEndDate, 'last', 'lastAsthmaSeverityDate');
CALL getCodedObsBeforeDate(8410, @reportEndDate, 'last', 'lastAsthmaSeverity');
CALL getDiagnosisBoolean(3683, '3716', @reportEndDate, 'copdDx');
CALL getDiagnosisDate(3683, '3716', 6774, @reportEndDate, 'first', 'copdDiagnosisDate');
-- Asthma Meds
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '8471,978', @reportEndDate, 'last', 'inhaledBAgonist');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '8472,1240', @reportEndDate, 'last', 'inhaledSteroid');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '8473', @reportEndDate, 'last', 'oralSteroid');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,119', '5622', @reportEndDate, 'last', 'otherAsthmaMeds');
-- Mental Health Information
CALL getDiagnosisBoolean(3683, '467,207,8419,8487,2719,8488,8489,8562,8563,8491,8420,8580,8581', @reportEndDate, 'mentalDx');
CALL getDiagnosisList(3683, '467,207,8419,8487,2719,8488,8489,8562,8563,8491,8420,8580,8581', @reportEndDate, 'mentalDxList');
CALL getDiagnosisDate(3683, '467,207,8419,8487,2719,8488,8489,8562,8563,8491,8420,8580,8581', 6774, @reportEndDate, 'first', 'firstMentalHealthDxDate');
CALL getEncounterDateForCodedObs('1193', '914,4047,927,920,920,4060,8498,4045,8582,8583,8237,8584,6408', @reportEndDate, 'first', 'firstMentalHealthMedsDate');
CALL getEncounterDateForCodedObs('1193', '914,4047,927,920,920,4060,8498,4045,8582,8583,8237,8584,6408', @reportEndDate, 'last', 'lastMentalHealthMedsDate');        
CALL getEncounterLocationForCodedObs('1193', '914,4047,927,920,920,4060,8498,4045,8582,8583,8237,8584,6408', @reportEndDate, 'last', 'lastMentalHealthMedsLocation');
-- BMI Information
CALL getEncounterDateForObs(5089, @reportEndDate, 'last', 'lastWeightDate');
CALL getNumericObsBeforeDate(5090, @reportEndDate, 'last', 'lastHeight');
CALL getNumericObsBeforeDate(5089, @reportEndDate, 'last', 'lastWeight');
-- Diagnoses Logic
CALL diagnosesLogic(); -- Must be last!
-- Appointments
CALL getAppointmentDateForEncounter('10', @reportEndDate, 'artAptDate');
CALL getAppointmentDateForEncounter('115', @reportEndDate, 'htnDmAptDate');
CALL getAppointmentDateForEncounter('123', @reportEndDate, 'epilepsyAptDate');
CALL getAppointmentDateForEncounter('125', @reportEndDate, 'mentalHealthAptDate');
CALL getAppointmentDateForEncounter('119', @reportEndDate, 'chronicLungAptDate');

-- Print report using select - update any column names or reorder here
SELECT
  PID,
  identifier as "Patient Identifier",
  allPreArtIds as "All HCC Identifiers",
  allArtIds as "All ART Identifiers",
  allCccIds as "ALL Chronic Care Identifiers",
  date_format(ic3EnrollmentDate,'%Y-%m-%d') as "Date of First Enrollment in HIV/Chronic Care Programs",
  ic3FirstProgramEnrolled as "First Program Enrollment",
  date_format(ncdEnrollmentDate,'%Y-%m-%d') as "NCD Program Enrollment Date",
  lastNcdOutcome as "NCD Program Outcome",
  date_format(lastNcdOutcomeDate,'%Y-%m-%d') as "NCD Program Outcome Date",
  lastNcdVisitLocation as "NCD last visit location",
  date_format(lastNcdVisitDate,'%Y-%m-%d') as "NCD last visit date",
  date_format(lastHtnDmVisitDate,'%Y-%m-%d') as "Last Hypertension/Diabetes encounter date",
  date_format(lastEpilepsyVisitDate,'%Y-%m-%d') as "Last Epilepsy encounter date",
  date_format(lastChronicLungVisitDate,'%Y-%m-%d') as "Last Chronic Lung Diseasee Enounter date",
  date_format(lastMentalHealthVisitDate,'%Y-%m-%d') as "Last Mental Health encounter date",
  date_format(hivEnrollmentDate,'%Y-%m-%d') as "HIV Program Enrollment Date",
  lastHivOutcome as "HIV Program Outcome",
  date_format(lastHivOutcomeDate,'%Y-%m-%d') as "HIV Program Outcome Date",
  lastHivVisitLocation as "HIV last visit location",
  date_format(lastHivVisitDate,'%Y-%m-%d') as "HIV last visit date",
  date_format(lastVisitDate,'%Y-%m-%d') as "Last IC3 visit date",
  lastVisitLocation as "Last IC3 visit location",
  hivAtLeastOneNcd as "HIV and at least one NCD",
  atLeastTwoNcds as "At least two NCDs",
  htnAndDm as "Hypertension and Diabetes",
  hivAndDm as "HIV and Diabetes",
  firstName as "Given Name",
  lastName as "Last Name",
  village as "Village",
  ta as "TA",
  district as "District",
  date_format(birthdate,'%Y-%m-%d') as "Birthdate",
  gender as "Gender",
  ageAtFirstEnrollment as "Age at First Enrollment",
  age as "Current Age",
-- VHW here  
  date_format(firstHivVisitDate,'%Y-%m-%d') as "HIV Program first visit date",
  date_format(artInitialDate,'%Y-%m-%d') as "ART initial date",
  lastTbValueInHiv as "Last TB status in HIV program", 
  lastTbDateInHiv as "Last TB Status Date",
  date_format(firstViralLoadDate,'%Y-%m-%d') as "Date of first viral load",
  firstViralLoadResult as "First viral load result",
  date_format(lastViralLoadDate,'%Y-%m-%d') as "Date of most recent viral load",
  lastViralLoadResult as "Most recent viral load result",
  lastArtRegimenStart as "ART Current regimen start date",
  lastArtRegimen as "ART Current Regimen",
  htnDx as "Hypertension Diagnosis",
  date_format(firstHtnDxDate,'%Y-%m-%d') as "Hypertension Diagnosis Date",
  date_format(firstHtnMedsDate,'%Y-%m-%d') as "First Hypertension Treatment Date",
  date_format(lastHtnMedsDate,'%Y-%m-%d') as "Last Hypertension Treatment Date",
  lastHtnMedsLocation as "Last Hypertension Treatment Location",
  firstBp as "First BP Measurment",
  date_format(firstBpDate,'%Y-%m-%d') as "First BP Measurement Date",
  lastBp as "Most recent BP Measurement",  
  date_format(lastBpDate,'%Y-%m-%d') as "Most recent BP Measurement Date",  
  diuretic as "Diuretics given at last visit",  
  calciumChannelBlocker as "CCBs given at last visit",  
  aceIInhibitor as "ACE-Is given at last visit",  
  betaBlocker as "Beta Blockers given at last visit",  
  statin as "Statins given at last visit",  
  otherHtnMeds as "Other HTN medications given at last visit",
  dmDx as "Diabetes Diagnosis",
  date_format(firstDmDxDate,'%Y-%m-%d') as "Diabetes Diagnosis Date",
  diabetesType as "Diabetes Type",
  firstDmMedsDate "First Diabetes Treatment Date",
  date_format(lastDmMedsDate,'%Y-%m-%d') as "Last Diabetes Treatment Date",
  lastDmMedsLocation as "Last Diabetes Treatment Location",
  date_format(firstGlucoseMonitoringDate,'%Y-%m-%d') as "Date of first visit with glucose monitoring",
  firstVisitHba1c as "HbA1c at first visit",
  firstVisitRandomBloodSugar as "Random blood sugar at first visit",
  firstVisitFastingBloodSugar as "Fasting blood sugar at first visit",
  date_format(lastGlucoseMonitoringDate,'%Y-%m-%d') as "Date of last visit with glucose monitoring",
  lastVisitHba1c as "HbA1c at last visit",
  lastVisitRandomBloodSugar as "Random blood sugar at last visit",
  lastVisitFastingBloodSugar as "Fasting blood sugar at first visit",
  shortActingRegularInsulin as "Short Acting Insulin given at last visit",  
  longActingInsulin as "Short Acting Insulin given at last visit",  
  metformin as "Metformin given at last visit",  
  glibenclamide as "Glibenclamide given at last visit",  
  epilepsyDx as "Epilepsy Diagnosis",
  date_format(firstEpilepsyDxDate,'%Y-%m-%d') as "Epilepsy Diagnosis Date",
  date_format(firstEpilepsyMedsDate,'%Y-%m-%d') as "First Epilepsy Treatment Date",
  date_format(lastEpilepsyMedsDate,'%Y-%m-%d') as "Last Epilepsy Treatment Date",
  lastEpilepsyMedsLocation as "First Epilepsy Treatment Location",
  date_format(firstSeizuresDate,'%Y-%m-%d') as "Date seizures first reported to clinician",
  firstSeizures as "Number of seizures first recorded",
  date_format(lastSeizuresDate,'%Y-%m-%d') as "Date seizures last reported to clinician",
  lastSeizures as "Last number of seizures recorded",
  seizureTriggers as "Seizure triggers",
  asthmaDx as "Asthma Diagnosis",
  date_format(firstAsthmaDxDate,'%Y-%m-%d') as "Asthma Diagnosis Date",
  copdDx as "COPD Diagnosis",
  date_format(copdDiagnosisDate,'%Y-%m-%d') as "COPD Diagnosis Date",  
  date_format(firstChronicLungMedsDate,'%Y-%m-%d') as "First Chronic Lung Treatment Date",
  date_format(lastChronicLungMedsDate,'%Y-%m-%d') as "Last Chronic Lung Treatment Date",
  lastChronicLungMedsLocation as "Last Chronic Lung Treatment Location",
  date_format(firstAsthmaSeverityDate,'%Y-%m-%d') as "First Asthma Severity Date",
  firstAsthmaSeverity as "First Asthma Severity",
  date_format(lastAsthmaSeverityDate,'%Y-%m-%d') as "Last Asthma Severity Date",
  lastAsthmaSeverity as "Last Asthma Severity",
  inhaledBAgonist as "Inhaled B-Agonist given at last visit",
  inhaledSteroid as "Inhaled steroid given at last visit",
  oralSteroid as "Oral steroid given at last visit",
  otherAsthmaMeds as "Other Chronic Lung medications given at last visit",
  mentalDx as "Mental Health Diagnosis",
  mentalDxList as "Mental Health Diagnoses",
  date_format(firstMentalHealthDxDate,'%Y-%m-%d') as "Mental Health Diagnosis Date",
  date_format(firstMentalHealthMedsDate,'%Y-%m-%d') as "First Mental Health Treatment Date",
  lastMentalHealthMedsDate "Last Mental Health Treatement Date",
  lastMentalHealthMedsLocation "Last Mental Health Treatment Location",
  lastWeightDate "Last Weight Date",
  lastHeight "Last Height",
  lastWeight "Last Weight",
  CASE WHEN age >= 19 
    THEN round(lastWeight/POWER(lastHeight/100,2),1)
  END AS BMI,
  artAptDate as "Next ART Appointment",
  htnDmAptDate as "Next Hypertension/Diabetes Appointment",
  epilepsyAptDate as "Next Epilepsy Appointment",
  chronicLungAptDate as "Next Chronic Lung Appointment",
  mentalHealthAptDate as "Next Mental Health Appointment"
FROM warehouseCohortTable;




