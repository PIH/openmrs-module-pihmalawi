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
package org.openmrs.module.pihmalawi.reporting.definition.data.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.List;

/**
 * Returns a List of all CD4 count observations, both clinician reported and lab recorded
 * with obsDatetime less than or equal to the configured endDate.
 * Results will be ordered chronologically from earliest to most recent, so to get the
 * latest cd4 obs, you would retrieve the last value in the returned list for each patient
 */
@Localized("pihmalawi.Cd4DataDefinition")
public class Cd4DataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public Cd4DataDefinition() {
		super();
	}

	@ConfigurationProperty
	private Date endDate;

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
