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
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retains live IC3 Screening Data for each patient
 */
@Component
public class IC3ScreeningData extends LivePatientDataSet {

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
        Cohort appts = evaluateCohort(baseCohorts.getPatientsWithScheduledAppointmentOnEndDate(), appointmentDate, null);
        Cohort enrolled = evaluateCohort(baseCohorts.getPatientsActiveInHivOrChronicCareProgramAtLocationOnEndDate(), appointmentDate, location);
        return PatientIdSet.intersect(appts, enrolled);
    }

    public Cohort getPatientsWithAVisitAtLocation(Date endDate, Location location) {
        return evaluateCohort(baseCohorts.getPatientsWithAVisitOnEndDateAtLocation(), endDate, location);
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
        addColumn(dsd, "identifiers", basePatientData.getAllIdentifiers());
        addColumn(dsd, "hcc_number", hivPatientData.getHccNumberAtLocation()); // TODO: See if this is what we want
        addColumn(dsd, "art_number", hivPatientData.getArvNumberAtLocation()); // TODO: See if this is what we want
        addColumn(dsd, "ncd_number", ccPatientData.getChronicCareNumberAtLocation()); // TODO: See if this is what we want
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
        addColumn(dsd, "last_viral_load_ldl", hivPatientData.getLatestViralLoadByEndDate("resultLdl"));
        addColumn(dsd, "last_art_regimen_change_date", hivPatientData.getLatestArtRegimenChangeByEndDate("regimenDate"));
        addColumn(dsd, "last_adherence_counselling_session_number", df.getMostRecentObsByEndDate(hivMetadata.getAdherenceCounselingSessionNumberConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "last_adherence_counselling_session_date", df.getMostRecentObsByEndDate(hivMetadata.getAdherenceCounselingSessionNumberConcept(), null, df.getObsValueDatetimeConverter()));
        addColumn(dsd, "chronic_care_diagnoses", ccPatientData.getAllChronicCareDiagnosesByEndDate());
        addColumn(dsd, "hiv_tests", hivPatientData.getAllHivTestResultsByEndDate());
        addColumn(dsd, "last_hiv_dna_pcr_result", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "testResult"));
        addColumn(dsd, "last_hiv_dna_pcr_result_date", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "effectiveDate"));
        addColumn(dsd, "last_hiv_rapid_test_result", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivRapidTest(), "testResult"));
        addColumn(dsd, "last_hiv_rapid_test_result_date", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivRapidTest(), "effectiveDate"));
        addColumn(dsd, "last_hba1c_result_date", df.getMostRecentObsByEndDate(ccMetadata.getHbA1cConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_blood_sugar_result_date", df.getMostRecentObsByEndDate(ccMetadata.getBloodSugarTestResultConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_creatinine_result_date", df.getMostRecentObsByEndDate(ccMetadata.getCreatinineConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_creatinine_result", df.getMostRecentObsByEndDate(ccMetadata.getCreatinineConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "last_bmi", basePatientData.getLatestBmiNumericValueByEndDate());
        addColumn(dsd, "last_muac", df.getMostRecentObsOnEndDate(ccMetadata.getMUACConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "family_history_diabetes", ccPatientData.getFamilyHistoryOfDiabetesByEndDate());
        addColumn(dsd, "last_breastfeeding_status", hivPatientData.getLatestBreastfeedingStatusValueByEndDate());
        addColumn(dsd, "is_pregnant", df.getMostRecentObsOnEndDate(ccMetadata.getIsPatientPregnantConcept(), null, df.getObsValueCodedConverter()));
        addColumn(dsd, "current_diastolic_bp", df.getMostRecentObsOnEndDate(ccMetadata.getDiastolicBloodPressureConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "current_systolic_bp", df.getMostRecentObsOnEndDate(ccMetadata.getSystolicBloodPressureConcept(), null, df.getObsValueNumericConverter()));
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
