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
package org.openmrs.module.pihmalawi.reporting.definition.dataset.definition;

import org.openmrs.Location;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

/**
 * Returns IC3 Appointment Data
 */
public class IC3AppoinmentDataSetDefinition extends BaseDataSetDefinition {

	@ConfigurationProperty
	private Date endDate;

	@ConfigurationProperty
	private Location location;

	@ConfigurationProperty
	private Boolean advancedCare = Boolean.FALSE;

	/**
	 * Constructor
	 */
	public IC3AppoinmentDataSetDefinition() {}

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

	public Boolean getAdvancedCare() {
		return advancedCare;
	}

	public void setAdvancedCare(Boolean advancedCare) {
		this.advancedCare = advancedCare;
	}
}
