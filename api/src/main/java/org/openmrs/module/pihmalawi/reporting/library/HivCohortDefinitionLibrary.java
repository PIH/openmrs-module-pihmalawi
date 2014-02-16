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

import org.openmrs.Location;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi
 */
@Component("pihMalawiCohortDefinitionLibrary")
public class HivCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.hiv.";

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

	@DocumentedDefinition(value = "everEnrolledInArtAtLocationByEndDate")
	public CohortDefinition getEverEnrolledInArtAtLocationByEndDate() {
		PatientStateAtLocationCohortDefinition cd = new PatientStateAtLocationCohortDefinition();
		cd.setState(hivMetadata.getOnArvsState());
		cd.addParameter(new Parameter("startedOnOrBefore", "Started on or before", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return new MappedParametersCohortDefinition(cd, "startedOnOrBefore", ReportingConstants.END_DATE_PARAMETER.getName(), "location", ReportingConstants.LOCATION_PARAMETER.getName());
	}
}
