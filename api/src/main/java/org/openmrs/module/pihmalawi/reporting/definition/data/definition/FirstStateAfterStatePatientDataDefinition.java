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

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;
import java.util.List;

/**
 * If a location is passed in, this will return the the preferred identifier for the patient at this location
 * If a location is not passed in, this will return the preferred identifier for the patient at their currently enrolled location for the given program
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.FirstStateAfterStatePatientDataDefinition")
public class FirstStateAfterStatePatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	//***** PROPERTIES *****

	@ConfigurationProperty(required=true)
	private List<ProgramWorkflowState> precedingStates;

	@ConfigurationProperty(required=false)
	private Location location;

	@ConfigurationProperty(required=false)
	private Date startedOnOrBefore;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public FirstStateAfterStatePatientDataDefinition() {
		super();
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return PatientState.class;
	}
	
	//****** PROPERTY ACCESS ******

	public List<ProgramWorkflowState> getPrecedingStates() {
		return precedingStates;
	}

	public void setPrecedingStates(List<ProgramWorkflowState> precedingStates) {
		this.precedingStates = precedingStates;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Date getStartedOnOrBefore() {
		return startedOnOrBefore;
	}

	public void setStartedOnOrBefore(Date startedOnOrBefore) {
		this.startedOnOrBefore = startedOnOrBefore;
	}
}