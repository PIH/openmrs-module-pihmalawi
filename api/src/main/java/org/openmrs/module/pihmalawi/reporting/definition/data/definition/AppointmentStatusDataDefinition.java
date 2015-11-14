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

import org.openmrs.EncounterType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.List;

/**
 * Returns the status of a patients appointment for a given encounter type
 */
@Localized("pihmalawi.AppointmentDataDefinition")
public class AppointmentStatusDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

    @ConfigurationProperty
    private List<ProgramWorkflowState> activeStates;

    @ConfigurationProperty
    private List<EncounterType> encounterTypes;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public AppointmentStatusDataDefinition() {
		super();
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return AppointmentInfo.class;
	}

    public List<ProgramWorkflowState> getActiveStates() {
        return activeStates;
    }

    public void setActiveStates(List<ProgramWorkflowState> activeStates) {
        this.activeStates = activeStates;
    }

    public List<EncounterType> getEncounterTypes() {
        return encounterTypes;
    }

    public void setEncounterTypes(List<EncounterType> encounterTypes) {
        this.encounterTypes = encounterTypes;
    }
}