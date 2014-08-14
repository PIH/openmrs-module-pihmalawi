/**
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
package org.openmrs.module.pihmalawi.reporting.definition.cohort.definition;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class InvalidIdentifierCohortDefinition extends BaseCohortDefinition {

	private static final long serialVersionUID = 1L;

	@ConfigurationProperty(required = true)
	private PatientIdentifierType identifierType;

	@ConfigurationProperty(required = true)
	private String identifierFormat;

	public InvalidIdentifierCohortDefinition() {
		super();
	}

	public PatientIdentifierType getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(PatientIdentifierType identifierType) {
		this.identifierType = identifierType;
	}

	public String getIdentifierFormat() {
		return identifierFormat;
	}

	public void setIdentifierFormat(String identifierFormat) {
		this.identifierFormat = identifierFormat;
	}
}