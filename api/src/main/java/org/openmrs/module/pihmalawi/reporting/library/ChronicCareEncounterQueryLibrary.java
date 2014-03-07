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
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi Chronic Care Program
 */
@Component("pihMalawiChronicCareEncounterQueryLibrary")
public class ChronicCareEncounterQueryLibrary extends BaseDefinitionLibrary<EncounterQuery> {

    public static final String PREFIX = "pihmalawi.encounterQuery.chronicCare.";

    @Autowired
    private ChronicCareMetadata metadata;

    @Override
    public Class<? super EncounterQuery> getDefinitionType() {
        return EncounterQuery.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

	@DocumentedDefinition(value = "chronicCareFollowupEncountersByEndDate")
	public EncounterQuery getChronicCareFollowupEncountersByEndDate() {
		BasicEncounterQuery q = new BasicEncounterQuery();
		q.addEncounterType(metadata.getChronicCareFollowupEncounterType());
		q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
		return new MappedParametersEncounterQuery(q, ObjectUtil.toMap("onOrBefore=endDate"));
	}
}
