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
import org.openmrs.module.pihmalawi.common.TraceCriteria;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import static org.openmrs.module.pihmalawi.common.TraceConstants.TraceType;

/**
 * Returns a list of matching criteria that would make a patient eligible for TRACE, or an empty list if not
 */
public class TraceCriteriaPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

    @ConfigurationProperty
    private TraceType traceType;

    @ConfigurationProperty
    private Location location;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public TraceCriteriaPatientDataDefinition() {
		super();
	}

	/**
	 * Name only Constructor
	 */
	public TraceCriteriaPatientDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return TraceCriteria.class;
	}

    //***** PROPERTY ACCESS *****


    public TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TraceType traceType) {
        this.traceType = traceType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}