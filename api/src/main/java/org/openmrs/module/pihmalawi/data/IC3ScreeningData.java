/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.data;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.module.pihmalawi.alert.AlertDefinition;
import org.openmrs.module.pihmalawi.alert.AlertEngine;
import org.openmrs.module.pihmalawi.metadata.IC3ScreeningMetadata;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Retains live IC3 Screening Data for each patient
 */
@Component
public class IC3ScreeningData extends LivePatientDataSet {

    @Autowired
    private IC3ScreeningMetadata screeningMetadata;

    public Map<Location, Cohort> getPatientsWithAppointmentsByEnrolledLocation(Date appointmentDate) {
        Map<Location, Cohort> ret = new HashMap<Location, Cohort>();
        Cohort appts = evaluateCohort(baseCohorts.getPatientsWithScheduledAppointmentOnEndDate(), appointmentDate, null);
        for (Location location : hivMetadata.getSystemLocations()) {
            Cohort enrolled = evaluateCohort(baseCohorts.getPatientsActiveInHivOrChronicCareProgramAtLocationOnEndDate(), appointmentDate, location);
            ret.put(location, PatientIdSet.intersect(appts, enrolled));
        }
        return ret;
    }

    public Cohort getPatientsWithAppointmentsAtLocation(Date appointmentDate, Location location) {
        Cohort appts = evaluateApptCohort(baseCohorts.getPatientsWithScheduledAppointmentDuringEndDate(), appointmentDate, null);
        Cohort enrolled = evaluateCohort(baseCohorts.getPatientsActiveInHivOrChronicCareProgramAtLocationOnEndDate(), appointmentDate, location);
        return CohortUtil.intersect(appts, enrolled);
    }

    public Cohort getPatientsWithAVisitAtLocation(Date endDate, Location location) {
        return evaluateCohort(baseCohorts.getPatientsWithAVisitOnEndDateAtLocation(), endDate, location);
    }

    @DocumentedDefinition("mostRecentBPScreening")
    public PatientDataDefinition getMostRecentBPScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getBloodPressureScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentBPScreeningDate")
    public PatientDataDefinition getMostRecentBPScreeningDate() {
        return  df.convert(getMostRecentBPScreening(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentNutritionScreening")
    public PatientDataDefinition getMostRecentNutritionScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getNutritionScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentNutritionScreeningDate")
    public PatientDataDefinition getMostRecentNutritionScreeningDate() {
        return  df.convert(getMostRecentNutritionScreening(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentAdherenceScreening")
    public PatientDataDefinition getMostRecentAdherenceScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getAdherenceScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentAdherenceScreeningDate")
    public PatientDataDefinition getMostRecentAdherenceScreeningDate() {
        return  df.convert(getMostRecentAdherenceScreening(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentEIDScreening")
    public PatientDataDefinition getMostRecentEIDScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getEIDScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentEIDScreeningDate")
    public PatientDataDefinition getMostRecentEIDScreeningDate() {
        return  df.convert(getMostRecentEIDScreening(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentHTCScreening")
    public PatientDataDefinition getMostRecentHTCScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getHTCScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentHTCScreeningDate")
    public PatientDataDefinition getMostRecentHTCScreeningDate() {
        return  df.convert(getMostRecentHTCScreening(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentVLScreening")
    public PatientDataDefinition getMostRecentVLScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getVLScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentVLScreeningDate")
    public PatientDataDefinition getMostRecentVLScreeningDate() {
        return  df.convert(getMostRecentVLScreening(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentTBScreening")
    public PatientDataDefinition getMostRecentTBScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getTBScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentTBScreeningDate")
    public PatientDataDefinition getMostRecentTBScreeningDate() {
        return  df.convert(getMostRecentTBScreening(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentTBTestResults")
    public PatientDataDefinition getMostRecentTBTestResults() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getTBTestResultsEncounterType());
    }

    @DocumentedDefinition("mostRecentTBTestResultsDate")
    public PatientDataDefinition getMostRecentTBTestResultsDate() {
        return  df.convert(getMostRecentTBTestResults(), df.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition("mostRecentNurseScreening")
    public PatientDataDefinition getMostRecentNurseScreening() {
        return df.getMostRecentEncounterOfType(screeningMetadata.getNurseScreeningEncounterType());
    }

    @DocumentedDefinition("mostRecentNurseScreeningDate")
    public PatientDataDefinition getMostRecentNurseScreeningDate() {
        return  df.convert(getMostRecentNurseScreening(), df.getEncounterDatetimeConverter());
    }

    /**
     * @return the data elements included in the screening data for each patient
     */
    @Override
    public PatientDataSetDefinition getDataSetDefinition() {
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        dsd.setName("IC3 Screening Data");
        dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);
        dsd.addParameter(ReportingConstants.LOCATION_PARAMETER);
        addColumn(dsd, INTERNAL_ID, builtInPatientData.getPatientId()); // This column is required by the framework
        addColumn(dsd, "patient_uuid", basePatientData.getUuid());
        addColumn(dsd, "first_name", builtInPatientData.getPreferredGivenName());
        addColumn(dsd, "last_name", builtInPatientData.getPreferredFamilyName());
        addColumn(dsd, "village", basePatientData.getVillage());
        addColumn(dsd, "traditional_authority", basePatientData.getTraditionalAuthority());
        addColumn(dsd, "district", basePatientData.getDistrict());
        addColumn(dsd, "phone_number", basePatientData.getPhoneNumber()); // TODO: See if this is what we want or to use obs?
        addColumn(dsd, "vhw", basePatientData.getChw());
        addColumn(dsd, "gender", builtInPatientData.getGender());
        addColumn(dsd, "birthdate", basePatientData.getBirthdate());
        addColumn(dsd, "age_years", basePatientData.getAgeAtEndInYears());
        addColumn(dsd, "age_months", basePatientData.getAgeAtEndInMonths());
        addColumn(dsd, "age_days", basePatientData.getAgeAtEndInDays());
        addColumn(dsd, "deceased", builtInPatientData.getVitalStatusDead());
        addColumn(dsd, "identifiers", basePatientData.getAllIdentifiers());
        addColumn(dsd, "hcc_number", hivPatientData.getHccNumberAtLocation()); // TODO: See if this is what we want
        addColumn(dsd, "art_number", hivPatientData.getArvNumberAtLocation()); // TODO: See if this is what we want
        addColumn(dsd, "ncd_number", ccPatientData.getChronicCareNumberAtLocation()); // TODO: See if this is what we want
        addColumn(dsd, "ic3d_number", basePatientData.getIC3DIdentifier());
        addColumn(dsd, "current_checkin_referral", df.getMostRecentObsOnDate(screeningMetadata.getSourceOfReferralConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "cc_treatment_status", ccPatientData.getMostRecentNcdTreatmentStatusStateByEndDate());
        addColumn(dsd, "last_visit_date", basePatientData.getLatestVisitDateByEndDate());
        addColumn(dsd, "last_appt_date", basePatientData.getLatestNextAppointmentDateValueByEndDate());
        addColumn(dsd, "hiv_treatment_status", hivPatientData.getMostRecentHivTreatmentStatusStateByEndDate());
        addColumn(dsd, "art_start_date", hivPatientData.getEarliestOnArvsStateStartDateByEndDate());
        addColumn(dsd, "eid_start_date", hivPatientData.getEarliestExposedChildStateStartDateByEndDate());
        addColumn(dsd, "viral_load_tests", hivPatientData.getAllViralLoadsByEndDate());
        addColumn(dsd, "last_viral_load_collection_date", hivPatientData.getLatestViralLoadByEndDate("specimenDate"));
        addColumn(dsd, "last_viral_load_result_date", hivPatientData.getLatestViralLoadResultDateByEndDate());
        addColumn(dsd, "last_viral_load_date", hivPatientData.getLatestViralLoadByEndDate("effectiveDate"));
        addColumn(dsd, "last_viral_load_type", hivPatientData.getLatestViralLoadByEndDate("reasonForTest"));
        addColumn(dsd, "last_viral_load_numeric", hivPatientData.getLatestViralLoadByEndDate("resultNumeric"));
        addColumn(dsd, "last_viral_load_less_than_numeric", hivPatientData.getLatestViralLoadByEndDate("lessThanResultNumeric"));
        addColumn(dsd, "last_viral_load_ldl", hivPatientData.getLatestViralLoadByEndDate("resultLdl"));
        addColumn(dsd, "last_art_regimen_change_date", hivPatientData.getLatestArtRegimenChangeByEndDate("regimenDate"));
        addColumn(dsd, "last_art_line_regimen_change_date", hivPatientData.getLatestArtLineRegimenChangeByEndDate("regimenDate"));
        addColumn(dsd, "last_adherence_counselling_session_number", df.getMostRecentObsByEndDate(hivMetadata.getAdherenceCounselingSessionNumberConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "last_adherence_counselling_session_date", df.getMostRecentObsByEndDate(hivMetadata.getAdherenceCounselingSessionNumberConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "chronic_care_diagnoses", ccPatientData.getAllChronicCareDiagnosesByEndDate());
        addColumn(dsd, "hiv_tests", hivPatientData.getAllHivTestResultsByEndDate());
        addColumn(dsd, "last_hiv_dna_pcr_result", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "testResult"));
        addColumn(dsd, "last_hiv_dna_pcr_result_date", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "effectiveDate"));
        addColumn(dsd, "first_hiv_dna_pcr_result", hivPatientData.getFirstHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "testResult"));
        addColumn(dsd, "first_hiv_dna_pcr_result_date", hivPatientData.getFirstHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "effectiveDate"));
        addColumn(dsd, "last_hiv_rapid_test_result", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivRapidTest(), "testResult"));
        addColumn(dsd, "last_hiv_rapid_test_result_date", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivRapidTest(), "effectiveDate"));
        addColumn(dsd, "last_hba1c_result_date", df.getMostRecentObsByEndDate(ccMetadata.getHbA1cConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_blood_sugar_result_date", df.getMostRecentObsByEndDate(ccMetadata.getBloodSugarTestResultConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_blood_sugar_test_type", df.getMostRecentObsOnDate(ccMetadata.getBloodSugarTestTypeConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "current_blood_sugar_result_date", df.getMostRecentObsOnDate(ccMetadata.getBloodSugarTestResultConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_blood_sugar_result", df.getMostRecentObsOnDate(ccMetadata.getBloodSugarTestResultConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "last_fasting_blood_sugar_result_date", df.getMostRecentObsByEndDate(ccMetadata.getFastingBloodSugarGlucoseConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_fasting_blood_sugar_result_date", df.getMostRecentObsOnDate(ccMetadata.getFastingBloodSugarGlucoseConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_fasting_blood_sugar_result", df.getMostRecentObsOnDate(ccMetadata.getFastingBloodSugarGlucoseConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "last_creatinine_result_date", df.getMostRecentObsByEndDate(ccMetadata.getCreatinineConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_creatinine_result", df.getMostRecentObsByEndDate(ccMetadata.getCreatinineConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "last_cervical_cancer_screening_result", df.getMostRecentObsByEndDate(screeningMetadata.getCervicalCancerScreeningResultsConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "last_cervical_cancer_screening_date", df.getMostRecentObsByEndDate(screeningMetadata.getCervicalCancerScreeningResultsConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_bmi", basePatientData.getLatestBmiNumericValueByEndDate());
        addColumn(dsd, "current_weight", df.getMostRecentObsOnDate(ccMetadata.getWeightConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "last_height", df.getMostRecentObsByEndDate(ccMetadata.getHeightConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "current_height", df.getMostRecentObsOnDate(ccMetadata.getHeightConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "current_muac", df.getMostRecentObsOnDate(ccMetadata.getMUACConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "family_history_diabetes", ccPatientData.getFamilyHistoryOfDiabetesByEndDate());
        addColumn(dsd, "last_breastfeeding_status", hivPatientData.getLatestBreastfeedingStatusValueByEndDate());
        addColumn(dsd, "is_pregnant", df.getMostRecentObsOnDate(ccMetadata.getIsPatientPregnantConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "tb_results_next_steps", df.getMostRecentObsOnDate(screeningMetadata.getRecommendedNextStepsConcept(), Arrays.asList(screeningMetadata.getTBTestResultsEncounterType()), df.getObsValueCodedConverter()));
        addColumn(dsd, "current_symptoms", df.getAllRecentObsOnDate(ccMetadata.getConcept(ccMetadata.SYMPTOM_PRESENT_CONCEPT), null, df.getObsValueCodedCollectionConverter()));
        addColumn(dsd, "current_diastolic_bp", df.getMostRecentObsOnDate(ccMetadata.getDiastolicBloodPressureConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "current_systolic_bp", df.getMostRecentObsOnDate(ccMetadata.getSystolicBloodPressureConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "current_sputum_collection", df.getMostRecentObsOnDate(screeningMetadata.getSputumCollectedConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "last_bp_screening_datetime", getMostRecentBPScreeningDate());
        addColumn(dsd, "last_nutrition_screening_datetime", getMostRecentNutritionScreeningDate());
        addColumn(dsd, "last_adherence_screening_datetime", getMostRecentAdherenceScreeningDate());
        addColumn(dsd, "last_eid_screening_datetime", getMostRecentEIDScreeningDate());
        addColumn(dsd, "last_htc_screening_datetime", getMostRecentHTCScreeningDate());
        addColumn(dsd, "last_vl_screening_datetime", getMostRecentVLScreeningDate());
        addColumn(dsd, "last_tb_screening_datetime", getMostRecentTBScreeningDate());
        addColumn(dsd, "last_tb_test_results_datetime", getMostRecentTBTestResultsDate());
        addColumn(dsd, "last_nurse_screening_datetime", getMostRecentNutritionScreeningDate());
        addColumn(dsd, "current_bp_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getBPScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_nutrition_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getNutritionScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_adherence_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getAdherenceScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_eid_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getEIDScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_htc_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getHTCScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_vl_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getVLScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_tb_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getTBScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "current_nurse_screening_clinician_referral_datetime", df.getMostRecentObsOnDate(screeningMetadata.getReferToScreeningStationConcept(), screeningMetadata.getNurseScreeningStationConcept(), null, df.getObsDatetimeConverter()));
        return dsd;
    }

    /**
     * @return the AlertDefinitions to evaluate for this data set
     */
    public List<AlertDefinition> getAlertDefinitions() {
        AlertEngine alertEngine = new AlertEngine();
        List<AlertDefinition> alertDefinitions = alertEngine.getAlertDefinitions();
        return alertDefinitions;
    }
}
