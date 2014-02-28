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

import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi
 */
@Component
public class HivCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.hiv.";

	@Autowired
	private DataFactory df;

    @Autowired
    private HivMetadata hivMetadata;

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

	@DocumentedDefinition(value = "hasAnHccNumber")
	public CohortDefinition getPatientsWithAnHccNumber() {
		return df.getPatientsWithIdentifierOfType(hivMetadata.getHccNumberIdentifierType());
	}

	@DocumentedDefinition(value = "hasAnArvNumber")
	public CohortDefinition getPatientsWithAnArvNumber() {
		return df.getPatientsWithIdentifierOfType(hivMetadata.getArvNumberIdentifierType());
	}

	@DocumentedDefinition(value = "enrolledInHivProgramByEndDate")
	public CohortDefinition getEverEnrolledInHivProgramByEndDate() {
		return df.getEverEnrolledInProgramByEndDate(hivMetadata.getHivProgram());
	}

	@DocumentedDefinition(value = "enrolledInHivProgramDuringPeriod")
	public CohortDefinition getEnrolledInHivProgramDuringPeriod() {
		return df.getEnrolledInProgramDuringPeriod(hivMetadata.getHivProgram());
	}

	@DocumentedDefinition(value = "everEnrolledInArtAtLocationByEndDate")
	public CohortDefinition getStartedInExposedChildStateDuringPeriod() {
		return df.getStartedInStateDuringPeriod(hivMetadata.getOnArvsState());
	}

	@DocumentedDefinition(value = "everEnrolledInArtAtLocationByEndDate")
	public CohortDefinition getEverEnrolledInArtAtLocationByEndDate() {
		return df.getEverEnrolledInStateAtLocationByEndDate(hivMetadata.getOnArvsState());
	}

	@DocumentedDefinition(value = "hadPreArtEncounterBeforeStartDate")
	public CohortDefinition getPatientsWithAPreArtEncounterBeforeStartDate() {
		return df.getAnyEncounterOfTypesBeforeStartDate(hivMetadata.getPreArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadArtEncounterBeforeStartDate")
	public CohortDefinition getPatientsWithAnArtEncounterBeforeStartDate() {
		return df.getAnyEncounterOfTypesBeforeStartDate(hivMetadata.getArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadHivEncounterBeforeStartDate")
	public CohortDefinition getPatientsWithAnHivEncounterBeforeStartDate() {
		return df.getAnyEncounterOfTypesBeforeStartDate(hivMetadata.getHivEncounterTypes());
	}

	@DocumentedDefinition(value = "hadPreArtEncounterDuringPeriod")
	public CohortDefinition getPatientsWithAPreArtEncounterDuringPeriod() {
		return df.getAnyEncounterOfTypesDuringPeriod(hivMetadata.getPreArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadArtEncounterDuringPeriod")
	public CohortDefinition getPatientsWithAnArtEncounterDuringPeriod() {
		return df.getAnyEncounterOfTypesDuringPeriod(hivMetadata.getArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadPreArtEncounterByEndDate")
	public CohortDefinition getPatientsWithAPreArtEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(hivMetadata.getPreArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadPreArtEncounterWithinMonthsOfEndDate")
	public CohortDefinition getPatientsWithAPreArtEncounterWithinMonthsOfEndDate(int numMonths) {
		return df.getAnyEncounterOfTypesWithinMonthsByEndDate(hivMetadata.getPreArtEncounterTypes(), numMonths);
	}

	@DocumentedDefinition(value = "hadArtEncounterByEndDate")
	public CohortDefinition getPatientsWithAnArtEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(hivMetadata.getArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadHivEncounterByEndDate")
	public CohortDefinition getPatientsWithAnHivEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(hivMetadata.getHivEncounterTypes());
	}

	@DocumentedDefinition(value = "hadHivEncounterDuringPeriod")
	public CohortDefinition getPatientsWithAnHivEncounterDuringPeriod() {
		return df.getAnyEncounterOfTypesDuringPeriod(hivMetadata.getHivEncounterTypes());
	}

	@DocumentedDefinition(value = "inPreArtStateOnEndDate")
	public CohortDefinition getPatientsInPreArtStateOnEndDate() {
		return df.getCurrentlyInStateOnEndDate(hivMetadata.getPreArtState());
	}
}
