/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reporting.library;

import org.openmrs.Concept;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PresenceOrAbsenceCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi
 */
@Component
public class ChronicCareCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.chronicCare.";

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCarePatientDataLibrary ccPatientData;

    @Autowired
    private ChronicCareMetadata metadata;

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

	// Encounters

	@DocumentedDefinition(value = "hadChronicCareInitialVisitDuringPeriod")
	public CohortDefinition getPatientsWithChronicCareInitialVisitDuringPeriod() {
		return df.getAnyEncounterOfTypesDuringPeriod(Arrays.asList(metadata.getChronicCareInitialEncounterType()));
	}

	@DocumentedDefinition(value = "hadChronicCareEncounterByEndDate")
	public CohortDefinition getPatientsWithAChronicCareEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "hadChronicCareEncounterWithin3MonthsOfEndDate")
	public CohortDefinition getPatientsWithChronicCareEncounterWithin3MonthsOfEndDate() {
		return df.getAnyEncounterOfTypesWithinMonthsByEndDate(metadata.getChronicCareEncounterTypes(), 3);
	}

	@DocumentedDefinition(value = "hadChronicCareEncounterWithin1MonthOfEndDate")
	public CohortDefinition getPatientsWithNoChronicCareEncounterWithin1MonthOfEndDate() {
		return df.getAnyEncounterOfTypesWithinMonthsByEndDate(metadata.getChronicCareEncounterTypes(), 1);
	}

	// Obs

	@DocumentedDefinition(value = "hasAsthmaDiagnosisByEndDate")
	public CohortDefinition getPatientsWithAsthmaDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getAsthmaConcept());
	}

	@DocumentedDefinition(value = "hasDiabetesDiagnosisByEndDate")
	public CohortDefinition getPatientsWithDiabetesDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getDiabetesConcept());
	}

	@DocumentedDefinition(value = "hasEpilepsyDiagnosisByEndDate")
	public CohortDefinition getPatientsWithEpilepsyDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getEpilepsyConcept());
	}

	@DocumentedDefinition(value = "hasHeartFailureDiagnosisByEndDate")
	public CohortDefinition getPatientsWithHeartFailureDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getHeartFailureConcept());
	}

	@DocumentedDefinition(value = "hasHypertensionDiagnosisByEndDate")
	public CohortDefinition getPatientsWithHypertensionDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getHypertensionConcept());
	}

	@DocumentedDefinition(value = "hasOtherNonCodedDiagnosisByEndDate")
	public CohortDefinition getPatientsWithOtherNonCodedDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getOtherNonCodedConcept());
	}

	@DocumentedDefinition(value = "hospitalizedDuringPeriod")
	public CohortDefinition getPatientsHospitalizedDuringPeriod() {
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getHospitalizedSinceLastVisitConcept(), Arrays.asList(metadata.getTrueConcept()));
	}

	@DocumentedDefinition(value = "hospitalizedForNcdDuringPeriod")
	public CohortDefinition getPatientsHospitalizedForNcdDuringPeriod() {
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getHospitalizedForNcdSinceLastVisitConcept(), Arrays.asList(metadata.getYesConcept()));
	}

	@DocumentedDefinition(value = "patientsOnBeclomethasoneDuringPeriod")
	public CohortDefinition getPatientsOnBeclomethasoneDuringPeriod() {
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getCurrentDrugsUsedConcept(), Arrays.asList(metadata.getBeclomethasoneConcept()));
	}

	@DocumentedDefinition(value = "patientsWithMoreThanMildPersistentAsthmaDuringPeriod")
	public CohortDefinition getPatientsWithMoreThanMildPersistentAsthmaDuringPeriod() {
		List<Concept> answers = Arrays.asList(metadata.getModeratePersistentConcept(), metadata.getSeverePersistentConcept(), metadata.getSevereUncontrolledConcept());
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getAsthmaClassificationConcept(), answers);
	}

	@DocumentedDefinition(value = "patientsOnInsulinDuringPeriod")
	public CohortDefinition getPatientsOnInsulinDuringPeriod() {
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getCurrentDrugsUsedConcept(), Arrays.asList(metadata.getInsulinConcept()));
	}

	@DocumentedDefinition(value = "patientsWithNumberOfSeizuresRecordedDuringPeriod")
	public CohortDefinition getPatientsWithNumberOfSeizuresRecordedDuringPeriod() {
		return df.getPatientsWithAnyObsDuringPeriod(metadata.getNumberOfSeizuresConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "patientsWithMoreThanTwoSeizuresPerMonthRecordedDuringPeriod")
	public CohortDefinition getPatientsWithMoreThanTwoSeizuresPerMonthRecordedDuringPeriod() {
		return df.getPatientsWithNumericObsDuringPeriod(metadata.getNumberOfSeizuresConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 2.0);
	}

	@DocumentedDefinition(value = "patientsWithSystolicBloodPressureOver180DuringPeriod")
	public CohortDefinition getPatientsWithSystolicBloodPressureOver180DuringPeriod() {
		return df.getPatientsWithNumericObsDuringPeriod(metadata.getSystolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 180.0);
	}

	@DocumentedDefinition(value = "patientsWithDiastolicBloodPressureOver110DuringPeriod")
	public CohortDefinition getPatientsWithDiastolicBloodPressureOver110DuringPeriod() {
		return df.getPatientsWithNumericObsDuringPeriod(metadata.getDiastolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 110.0);
	}

	@DocumentedDefinition(value = "patientsOnMoreThanOneHypertensionMedicationDuringPeriod")
	public CohortDefinition getPatientsOnMoreThanOneHypertensionMedicationDuringPeriod() {
		PresenceOrAbsenceCohortDefinition cd = new PresenceOrAbsenceCohortDefinition();
		for (Concept med : metadata.getHypertensionMedicationConcepts()) {
			CohortDefinition onDrug = df.getPatientsWithCodedObsDuringPeriod(metadata.getCurrentDrugsUsedConcept(), Arrays.asList(med));
			cd.addCohortToCheck(Mapped.mapStraightThrough(onDrug));
		}
		cd.setPresentInAtLeast(2);
		return cd;
	}

	@DocumentedDefinition(value = "newPatientsReferredFromOPDDuringPeriod")
	public CohortDefinition getNewPatientsReferredFromOPDDuringPeriod() {
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getOutpatientConsultationConcept()));
	}

	@DocumentedDefinition(value = "newPatientsReferredFromInpatientWardDuringPeriod")
	public CohortDefinition getNewPatientsReferredFromInpatientWardDuringPeriod() {
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getInpatientWardConcept()));
	}

	@DocumentedDefinition(value = "newPatientsReferredFromHealthCenterDuringPeriod")
	public CohortDefinition getNewPatientsReferredFromHealthCenterDuringPeriod() {
		return df.getPatientsWithCodedObsDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getHealthCenterConcept()));
	}

	@DocumentedDefinition(value = "newPatientsReferredDuringPeriod")
	public CohortDefinition getNewPatientsReferredDuringPeriod() {
		return df.getPatientsWithAnyObsDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getChronicCareInitialEncounterType()));
	}

	@DocumentedDefinition(value = "patientsWhoseMostRecentAppointmentDateIsMoreThanOneMonthBeforeEndDate")
	public CohortDefinition getPatientsWhoseMostRecentAppointmentDateIsMoreThanOneMonthBeforeEndDate() {
		PatientDataDefinition data = ccPatientData.getMostRecentChronicCareAppointmentDateByEndDate();
		return df.getPatientsWhoseLatestDateIsOlderThanTimeByEndDate(data, 1, DurationUnit.MONTHS);
	}

	// Programs

	@DocumentedDefinition(value = "enrolledInChronicCareProgramDuringPeriod")
	public CohortDefinition getPatientsEnrolledInChronicCareProgramDuringPeriod() {
		return df.getEnrolledInProgramDuringPeriod(metadata.getChronicCareProgram());
	}

	@DocumentedDefinition(value = "inOnTreatmentStateAtLocationOnEndDate")
	public CohortDefinition getPatientsInOnTreatmentStateAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(metadata.getChronicCareStatusOnTreatment());
	}

	// Helper methods

	protected CohortDefinition hasDiagnosisByEndDate(Concept diagnosis) {
		return df.getPatientsWithCodedObsByEndDate(metadata.getChronicCareDiagnosisConcept(), Arrays.asList(diagnosis));
	}
}
