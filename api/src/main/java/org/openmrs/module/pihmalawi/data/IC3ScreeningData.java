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

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

/**
 * Retains an updated copy of the IC3 Screening Data Set
 * Undertakes a full refresh once per day
 * Provides means to update for a given patient or all patients in a cohort on demand
 */
@Component
public class IC3ScreeningData extends LivePatientDataSet {

    @Override
    public CohortDefinition constructCohortDefinition() {
        CohortDefinition appts = baseCohorts.getPatientsWithScheduledAppointmentOnEndDate();
        CohortDefinition active = baseCohorts.getPatientsWithAnActiveVisit();
        CohortDefinition completed = baseCohorts.getPatientsWithACompletedVisitOnEndDate();
        return df.getPatientsInAny(appts, active, completed);
    }

    @Override
    public DataSetDefinition constructDataSetDefinition() {
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        dsd.setName("IC3 Screening Data");
        dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);
        addColumn(dsd, "patientId", builtInPatientData.getPatientId());
        addColumn(dsd, "uuid", basePatientData.getUuid());
        addColumn(dsd, "birthdate", basePatientData.getBirthdate());
        addColumn(dsd, "age_years", basePatientData.getAgeAtEndInYears());
        addColumn(dsd, "age_months", basePatientData.getAgeAtEndInMonths());
        addColumn(dsd, "age_days", basePatientData.getAgeAtEndInDays());
        addColumn(dsd, "hiv_treatment_status", hivPatientData.getMostRecentHivTreatmentStatusStateByEndDate());
        addColumn(dsd, "art_start_date", hivPatientData.getEarliestOnArvsStateStartDateByEndDate());
        addColumn(dsd, "eid_start_date", hivPatientData.getEarliestExposedChildStateStartDateByEndDate());
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
        addColumn(dsd, "last_hiv_dna_pcr_result", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "testResult"));
        addColumn(dsd, "last_hiv_dna_pcr_result_date", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivDnaPcrTest(), "effectiveDate"));
        addColumn(dsd, "last_hiv_rapid_test_result", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivRapidTest(), "testResult"));
        addColumn(dsd, "last_hiv_rapid_test_result_date", hivPatientData.getHivTestResultPropertyByEndDate(hivMetadata.getHivRapidTest(), "effectiveDate"));
        addColumn(dsd, "last_hba1c_result_date", df.getMostRecentObsByEndDate(ccMetadata.getHbA1cConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_blood_sugar_result_date", df.getMostRecentObsByEndDate(ccMetadata.getBloodSugarTestResultConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_creatinine_result_date", df.getMostRecentObsByEndDate(ccMetadata.getCreatinineConcept(), null, df.getObsDatetimeConverter()));
        addColumn(dsd, "last_creatinine_result", df.getMostRecentObsByEndDate(ccMetadata.getCreatinineConcept(), null, df.getObsValueNumericConverter()));
        addColumn(dsd, "last_bmi", basePatientData.getLatestBmiNumericValueByEndDate());
        addColumn(dsd, "family_history_diabetes", ccPatientData.getFamilyHistoryOfDiabetesByEndDate());
        addColumn(dsd, "last_breastfeeding_status", hivPatientData.getLatestBreastfeedingStatusValueByEndDate());
        return dsd;
    }
}
