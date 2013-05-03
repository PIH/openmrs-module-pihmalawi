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
package org.openmrs.module.pihmalawi.reports.extension;

import org.openmrs.Location;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

/**
 * Definition of an EncounterAndObs DataSet
 */
@Localized("pihmalawi.ChronicCareVisitDataSetDefinition")
public class ChronicCareVisitDataSetDefinition extends BaseDataSetDefinition {

	//***** PROPERTIES *****

	@ConfigurationProperty(group="which")
	public TimeQualifier which = TimeQualifier.ANY;

	@ConfigurationProperty(group="when")
	public Date fromDate;

	@ConfigurationProperty(group="when")
	public Date toDate;

	@ConfigurationProperty(group="where")
	public Location location;

	@ConfigurationProperty(group="who")
	public boolean limitedToPatientsEnrolledAtEnd;

	//***** CONSTRUCTORS *****

	public ChronicCareVisitDataSetDefinition() {
		super();
	}

	/**
	 * Public constructor with name and description
	 */
	public ChronicCareVisitDataSetDefinition(String name, String description) {
		super(name, description);
	}
	
	//****** PROPERTY ACCESS ******

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public TimeQualifier getWhich() {
		return which;
	}

	public void setWhich(TimeQualifier which) {
		this.which = which;
	}

	public boolean getLimitedToPatientsEnrolledAtEnd() {
		return limitedToPatientsEnrolledAtEnd;
	}

	public void setLimitedToPatientsEnrolledAtEnd(boolean limitedToPatientsEnrolledAtEnd) {
		this.limitedToPatientsEnrolledAtEnd = limitedToPatientsEnrolledAtEnd;
	}
}
