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

}
