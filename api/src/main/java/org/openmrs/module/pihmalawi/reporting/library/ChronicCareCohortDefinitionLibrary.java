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

import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	@DocumentedDefinition(value = "hadChronicCareEncounterByEndDate")
	public CohortDefinition getPatientsWithAChronicCareEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(metadata.getChronicCareEncounterTypes());
	}
}
