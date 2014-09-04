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
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

public class HccCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty
	private Date endDate;

	@ConfigurationProperty
	private Location location;

	@ConfigurationProperty
	private Boolean includeOldPreArtPatients = Boolean.FALSE;

	public HccCohortDefinition() {
		super();
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Boolean getIncludeOldPreArtPatients() {
		return includeOldPreArtPatients;
	}

	public void setIncludeOldPreArtPatients(Boolean includeOldPreArtPatients) {
		this.includeOldPreArtPatients = includeOldPreArtPatients;
	}
}