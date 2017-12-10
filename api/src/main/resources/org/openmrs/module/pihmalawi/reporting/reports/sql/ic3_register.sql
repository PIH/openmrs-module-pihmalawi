-- ## report_uuid = 897C0E0A-1F8A-4ABD-AFE2-054146227668
-- ## design_uuid = FFA51EA2-D483-43F2-9FE8-5B0AF619E8A0
-- ## report_name = IC3 Register Report
-- ## report_description = Report listing IC3 patients
-- ## parameter = reportEndDate|End Date|java.util.Date

-- Report lists all patients who are in enlisted in any of the CC programs or ART program.
select encounter_type_id into @ART_FOLLOWUP from encounter_type where uuid = '664b8650-977f-11e1-8993-905e29aff6c1';
select encounter_type_id into @DH_F from encounter_type where uuid = '66079de4-a8df-11e5-bf7f-feff819cdc9f';
select encounter_type_id into @E_I from encounter_type where uuid = 'D8CBF1B9-EC74-4858-8764-2350E2A9925B';
select encounter_type_id into @E_F from encounter_type where uuid = '1EEDD2F6-EF28-4409-8E8C-F4FEC0746E72';
select encounter_type_id into @M_I from encounter_type where uuid = '3F94849C-F245-4593-BCC8-879EAEA29168';
select encounter_type_id into @M_F from encounter_type where uuid = 'D51F45F8-0EEA-4231-A7E9-C45D57F1CBA1';
select encounter_type_id into @A_I from encounter_type where uuid = 'a95dc43f-925c-11e5-a1de-e82aea237783';
select encounter_type_id into @A_F from encounter_type where uuid = 'f4596df5-925c-11e5-a1de-e82aea237783';

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
CALL getIdentifierForProgram(1, '4,19', @reportEndDate, 'activeHivId');
CALL getIdentifierForProgram(10, '21', @reportEndDate, 'activeCCCId');
-- General Visits and outcomes
CALL getEncounterDatetimeBeforeEndDate('67,69,29,@DH_F,@A_I,@A_F,@E_I,@E_F,@M_I,@M_F', @reportEndDate, 'last', 'lastNcdVisitDate');
CALL getEncounterLocationBeforeEndDate('67,69,29,@DH_F,@A_I,@A_F,@E_I,@E_F,@M_I,@M_F', @reportEndDate, 'last', 'lastNcdVisitLocation');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12', @reportEndDate, 'first', 'firstHivVisitDate');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12', @reportEndDate, 'last', 'lastHivVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12', @reportEndDate, 'last', 'lastHivVisitLocation');
CALL getEncounterDatetimeBeforeEndDate('9,10,11,12,67,69,29,@DH_F,@A_I,@A_F,@E_I,@E_F,@M_I,@M_F', @reportEndDate, 'last', 'lastVisitDate');
CALL getEncounterLocationBeforeEndDate('9,10,11,12,67,69,29,@DH_F,@A_I,@A_F,@E_I,@E_F,@M_I,@M_F', @reportEndDate, 'last', 'lastVisitLocation');
CALL getEncounterDatetimeBeforeEndDate('29,@DH_F', @reportEndDate, 'last', 'lastHtnDmVisitDate');
CALL getEncounterDatetimeBeforeEndDate('@E_I,@E_F', @reportEndDate, 'last', 'lastEpilepsyVisitDate');
CALL getEncounterDatetimeBeforeEndDate('@A_I,@A_F', @reportEndDate, 'last', 'lastChronicLungVisitDate');
CALL getEncounterDatetimeBeforeEndDate('@M_I,@M_F', @reportEndDate, 'last', 'lastMentalHealthVisitDate');
CALL updateIc3EnrollmentInfo(@reportEndDate);
CALL updateProgramsEnrollmentDate();
-- HIV Program Information
CALL updateFirstViralLoad(@reportEndDate);
CALL updateLastViralLoad(@reportEndDate);
CALL getLastOutcomeForProgram(1, @reportEndDate, 'lastHivOutcome', 'lastHivOutcomeDate');
CALL everDefaultedByProgram('HIV Program', 'everDefaultedHiv');
CALL getLastOutcomeForProgram(10, @reportEndDate, 'lastNcdOutcome', 'lastNcdOutcomeDate');
CALL everDefaultedByProgram('NCD Program', 'everDefaultedNcd');
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
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '8466,1243,99,4061', @reportEndDate, 'last', 'diuretic');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '8465,3187,250', @reportEndDate, 'last', 'calciumChannelBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '8464,1242,3182,3183', @reportEndDate, 'last', 'aceIInhibitor');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '8463,3186,8612,254', @reportEndDate, 'last', 'betaBlocker');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '8462,8613,8614,8210', @reportEndDate, 'last', 'statin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '88,929,7121', @reportEndDate, 'last', 'otherHtnMeds');
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
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '282', @reportEndDate, 'last', 'shortActingRegularInsulin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '6750', @reportEndDate, 'last', 'longActingInsulin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '4052', @reportEndDate, 'last', 'metformin');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '69,@DH_F', '4046', @reportEndDate, 'last', 'glibenclamide');
-- Epilepsy Information
CALL getDiagnosisBoolean(3683, '155', @reportEndDate, 'epilepsyDx');
CALL getDiagnosisDate(3683, '155', 6774, @reportEndDate, 'first', 'firstEpilepsyDxDate');
CALL getEpilepsyOnsetDate(@reportEndDate, 'last', 'epilepsyOnsetDate');
CALL getEncounterDateForCodedObs('1193', '238,273,920', @reportEndDate, 'first', 'firstEpilepsyMedsDate');
CALL getEncounterDateForCodedObs('1193', '238,273,920', @reportEndDate, 'last', 'lastEpilepsyMedsDate');
CALL getEncounterLocationForCodedObs('1193', '238,273,920', @reportEndDate, 'last', 'lastEpilepsyMedsLocation');
CALL getEncounterDateForObs(7924, @reportEndDate, 'first', 'firstSeizuresDate');
CALL getNumericObsBeforeDate(7924, @reportEndDate, 'first', 'firstSeizures');
CALL getEncounterDateForObs(7924, @reportEndDate, 'last', 'lastSeizuresDate');
CALL getNumericObsBeforeDate(7924, @reportEndDate, 'last', 'lastSeizures');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193', '@E_F', '8531,8532,8533,8534,8535,8536,8537', @reportEndDate, 'last', 'seizureTriggers');
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
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,@A_F', '8471,978', @reportEndDate, 'last', 'inhaledBAgonist');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,@A_F', '8472,1240', @reportEndDate, 'last', 'inhaledSteroid');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,@A_F', '8473', @reportEndDate, 'last', 'oralSteroid');
CALL getCodedObsWithValuesFromEncounterBeforeDate('1193,8474', '69,@A_F', '5622', @reportEndDate, 'last', 'otherAsthmaMeds');
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
CALL getAppointmentDateForEncounter(@ART_FOLLOWUP, @reportEndDate, 'artAptDate');
CALL getAppointmentDateForEncounter(@DH_F, @reportEndDate, 'htnDmAptDate');
CALL getAppointmentDateForEncounter(@E_F, @reportEndDate, 'epilepsyAptDate');
CALL getAppointmentDateForEncounter(@M_F, @reportEndDate, 'mentalHealthAptDate');
CALL getAppointmentDateForEncounter(@A_F, @reportEndDate, 'chronicLungAptDate');

-- Print report using select - update any column names or reorder here
SELECT
  PID,
  identifier as "Patient Identifier",
  allPreArtIds as "All HCC Identifiers",
  allArtIds as "All ART Identifiers",
  allCccIds as "ALL Chronic Care Identifiers",
  activeHivId as "Active HIV Identifier",
  activeCCCId as "Active Chronic Care Identifier",
  date_format(ic3EnrollmentDate,'%d/%m/%Y') as "Date of First Enrollment in HIV/Chronic Care Programs",
  ic3FirstProgramEnrolled as "First Program Enrollment",
  date_format(ncdEnrollmentDate,'%d/%m/%Y') as "NCD Program Enrollment Date",
  lastNcdOutcome as "NCD Program Outcome",
  everDefaultedNcd as "Ever Defaulted in NCD",
  date_format(lastNcdOutcomeDate,'%d/%m/%Y') as "NCD Program Outcome Date",
  lastNcdVisitLocation as "NCD last visit location",
  date_format(lastNcdVisitDate,'%d/%m/%Y') as "NCD last visit date",
  date_format(lastHtnDmVisitDate,'%d/%m/%Y') as "Last Hypertension/Diabetes encounter date",
  date_format(lastEpilepsyVisitDate,'%d/%m/%Y') as "Last Epilepsy encounter date",
  date_format(lastChronicLungVisitDate,'%d/%m/%Y') as "Last Chronic Lung Diseasee Enounter date",
  date_format(lastMentalHealthVisitDate,'%d/%m/%Y') as "Last Mental Health encounter date",
  date_format(hivEnrollmentDate,'%d/%m/%Y') as "HIV Program Enrollment Date",
  lastHivOutcome as "HIV Program Outcome",
  everDefaultedHiv as "Ever Defaulted in HIV",
  date_format(lastHivOutcomeDate,'%d/%m/%Y') as "HIV Program Outcome Date",
  lastHivVisitLocation as "HIV last visit location",
  date_format(lastHivVisitDate,'%d/%m/%Y') as "HIV last visit date",
  date_format(lastVisitDate,'%d/%m/%Y') as "Last IC3 visit date",
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
  date_format(birthdate,'%d/%m/%Y') as "Birthdate",
  gender as "Gender",
  ageAtFirstEnrollment as "Age at First Enrollment",
  age as "Current Age",
-- VHW here  
  date_format(firstHivVisitDate,'%d/%m/%Y') as "HIV Program first visit date",
  date_format(artInitialDate,'%d/%m/%Y') as "ART initial date",
  lastTbValueInHiv as "Last TB status in HIV program", 
  date_format(lastTbDateInHiv,'%d/%m/%Y') as "Last TB Status Date",
  date_format(firstViralLoadDate,'%d/%m/%Y') as "Date of first viral load",
  firstViralLoadResult as "First viral load result",
  date_format(lastViralLoadDate,'%d/%m/%Y') as "Date of most recent viral load",
  lastViralLoadResult as "Most recent viral load result",
  lastViralLoadWeight as "Weight for Most recent viral load",
  date_format(lastArtRegimenStart,'%d/%m/%Y') as "ART Current regimen start date",
  lastArtRegimen as "ART Current Regimen",
  htnDx as "Hypertension Diagnosis",
  date_format(firstHtnDxDate,'%d/%m/%Y') as "Hypertension Diagnosis Date",
  date_format(firstHtnMedsDate,'%d/%m/%Y') as "First Hypertension Treatment Date",
  date_format(lastHtnMedsDate,'%d/%m/%Y') as "Last Hypertension Treatment Date",
  lastHtnMedsLocation as "Last Hypertension Treatment Location",
  firstBp as "First BP Measurment",
  date_format(firstBpDate,'%d/%m/%Y') as "First BP Measurement Date",
  lastBp as "Most recent BP Measurement",  
  date_format(lastBpDate,'%d/%m/%Y') as "Most recent BP Measurement Date",
  diuretic as "Diuretics given at last visit",  
  calciumChannelBlocker as "CCBs given at last visit",  
  aceIInhibitor as "ACE-Is given at last visit",  
  betaBlocker as "Beta Blockers given at last visit",  
  statin as "Statins given at last visit",  
  otherHtnMeds as "Other HTN medications given at last visit",
  dmDx as "Diabetes Diagnosis",
  date_format(firstDmDxDate,'%d/%m/%Y') as "Diabetes Diagnosis Date",
  diabetesType as "Diabetes Type",
  firstDmMedsDate "First Diabetes Treatment Date",
  date_format(lastDmMedsDate,'%d/%m/%Y') as "Last Diabetes Treatment Date",
  lastDmMedsLocation as "Last Diabetes Treatment Location",
  date_format(firstGlucoseMonitoringDate,'%d/%m/%Y') as "Date of first visit with glucose monitoring",
  firstVisitHba1c as "HbA1c at first visit",
  firstVisitRandomBloodSugar as "Random blood sugar at first visit",
  firstVisitFastingBloodSugar as "Fasting blood sugar at first visit",
  date_format(lastGlucoseMonitoringDate,'%d/%m/%Y') as "Date of last visit with glucose monitoring",
  lastVisitHba1c as "HbA1c at last visit",
  lastVisitRandomBloodSugar as "Random blood sugar at last visit",
  lastVisitFastingBloodSugar as "Fasting blood sugar at first visit",
  shortActingRegularInsulin as "Short Acting Insulin given at last visit",  
  longActingInsulin as "Short Acting Insulin given at last visit",  
  metformin as "Metformin given at last visit",  
  glibenclamide as "Glibenclamide given at last visit",  
  epilepsyDx as "Epilepsy Diagnosis",
  date_format(firstEpilepsyDxDate,'%d/%m/%Y') as "Epilepsy Diagnosis Date",
  date_format(epilepsyOnsetDate,'%d/%m/%Y') as "Epilepsy Onset Date",
  date_format(firstEpilepsyMedsDate,'%d/%m/%Y') as "First Epilepsy Treatment Date",
  date_format(lastEpilepsyMedsDate,'%d/%m/%Y') as "Last Epilepsy Treatment Date",
  lastEpilepsyMedsLocation as "First Epilepsy Treatment Location",
  date_format(firstSeizuresDate,'%d/%m/%Y') as "Date seizures first reported to clinician",
  firstSeizures as "Number of seizures first recorded",
  date_format(lastSeizuresDate,'%d/%m/%Y') as "Date seizures last reported to clinician",
  lastSeizures as "Last number of seizures recorded",
  seizureTriggers as "Seizure triggers",
  asthmaDx as "Asthma Diagnosis",
  date_format(firstAsthmaDxDate,'%d/%m/%Y') as "Asthma Diagnosis Date",
  copdDx as "COPD Diagnosis",
  date_format(copdDiagnosisDate,'%d/%m/%Y') as "COPD Diagnosis Date",
  date_format(firstChronicLungMedsDate,'%d/%m/%Y') as "First Chronic Lung Treatment Date",
  date_format(lastChronicLungMedsDate,'%d/%m/%Y') as "Last Chronic Lung Treatment Date",
  lastChronicLungMedsLocation as "Last Chronic Lung Treatment Location",
  date_format(firstAsthmaSeverityDate,'%d/%m/%Y') as "First Asthma Severity Date",
  firstAsthmaSeverity as "First Asthma Severity",
  date_format(lastAsthmaSeverityDate,'%d/%m/%Y') as "Last Asthma Severity Date",
  lastAsthmaSeverity as "Last Asthma Severity",
  inhaledBAgonist as "Inhaled B-Agonist given at last visit",
  inhaledSteroid as "Inhaled steroid given at last visit",
  oralSteroid as "Oral steroid given at last visit",
  otherAsthmaMeds as "Other Chronic Lung medications given at last visit",
  mentalDx as "Mental Health Diagnosis",
  mentalDxList as "Mental Health Diagnoses",
  date_format(firstMentalHealthDxDate,'%d/%m/%Y') as "Mental Health Diagnosis Date",
  date_format(firstMentalHealthMedsDate,'%d/%m/%Y') as "First Mental Health Treatment Date",
  date_format(lastMentalHealthMedsDate,'%d/%m/%Y') "Last Mental Health Treatement Date",
  lastMentalHealthMedsLocation "Last Mental Health Treatment Location",
  date_format(lastWeightDate,'%d/%m/%Y') "Last Weight Date",
  lastHeight "Last Height",
  lastWeight "Last Weight",
  CASE WHEN age >= 19 
    THEN round(lastWeight/POWER(lastHeight/100,2),1)
  END AS BMI,
  date_format(artAptDate,'%d/%m/%Y') as "Next ART Appointment",
  date_format(htnDmAptDate,'%d/%m/%Y') as "Next Hypertension/Diabetes Appointment",
  date_format(epilepsyAptDate,'%d/%m/%Y') as "Next Epilepsy Appointment",
  date_format(chronicLungAptDate,'%d/%m/%Y') as "Next Chronic Lung Appointment",
  date_format(mentalHealthAptDate,'%d/%m/%Y') as "Next Mental Health Appointment"
FROM warehouseCohortTable;




