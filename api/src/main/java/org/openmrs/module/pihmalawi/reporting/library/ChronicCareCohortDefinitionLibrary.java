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
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi
 */
@Component
public class ChronicCareCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.chronicCare.";

	@Autowired
	private DataFactory df;

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

	@DocumentedDefinition(value = "hadChronicCareEncounterWithin6MonthsOfEndDate")
	public CohortDefinition getPatientsWithChronicCareEncounterWithin6MonthsOfEndDate() {
		return df.getAnyEncounterOfTypesWithinMonthsByEndDate(metadata.getChronicCareEncounterTypes(), 6);
	}

	// Obs

	@DocumentedDefinition(value = "hasAsthmaDiagnosisByEndDate")
	public CohortDefinition getPatientsWithAsthmaDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getAsthmaConcept());
	}

	@DocumentedDefinition(value = "hasDiabetesDiagnosisByEndDate")
	public CohortDefinition getPatientsWithDiabetesaDiagnosisByEndDate() {
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
