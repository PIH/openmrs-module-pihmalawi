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
package org.openmrs.module.pihmalawi.reporting.data.definition;

import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * If a location is passed in, this will return the the preferred identifier for the patient at this location
 * If a location is not passed in, this will return the preferred identifier for the patient at their currently enrolled location for the given program
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ProgramPatientIdentifierDataDefinition")
public class ProgramPatientIdentifierDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	//***** PROPERTIES *****

	@ConfigurationProperty(required=true)
	private PatientIdentifierType identifierType;

	@ConfigurationProperty(required=true)
	private Program program;

	@ConfigurationProperty(required=false)
	private Location location;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public ProgramPatientIdentifierDataDefinition() {
		super();
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return PatientIdentifier.class;
	}
	
	//****** PROPERTY ACCESS ******

	public PatientIdentifierType getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(PatientIdentifierType identifierType) {
		this.identifierType = identifierType;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}