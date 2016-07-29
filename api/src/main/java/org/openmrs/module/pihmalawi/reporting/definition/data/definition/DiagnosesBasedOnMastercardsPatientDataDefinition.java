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

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

import java.util.List;

/**
 * Diagnoses Based on Mastercards
 */
public class DiagnosesBasedOnMastercardsPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public DiagnosesBasedOnMastercardsPatientDataDefinition() {
		super();
	}

	/**
	 * Name only Constructor
	 */
	public DiagnosesBasedOnMastercardsPatientDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}
}