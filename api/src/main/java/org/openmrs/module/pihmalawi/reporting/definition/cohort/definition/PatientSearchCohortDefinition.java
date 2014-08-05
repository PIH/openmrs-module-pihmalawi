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

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

public class PatientSearchCohortDefinition extends BaseCohortDefinition {

	private static final long serialVersionUID = 1L;

	@ConfigurationProperty(required = true)
	private String searchPhrase;

	@ConfigurationProperty
	private Boolean soundexEnabled = Boolean.FALSE;

	public PatientSearchCohortDefinition() {
		super();
	}

	public String getSearchPhrase() {
		return searchPhrase;
	}

	public void setSearchPhrase(String searchPhrase) {
		this.searchPhrase = searchPhrase;
	}

	public Boolean getSoundexEnabled() {
		return soundexEnabled;
	}

	public void setSoundexEnabled(Boolean soundexEnabled) {
		this.soundexEnabled = soundexEnabled;
	}
}