/*
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
package org.openmrs.module.pihmalawi.metadata.group;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PatientIdentifierConverter;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class TreatmentGroup {

	@Autowired
	DataFactory df;

	public abstract String getName();
	public abstract Program getProgram();
	public abstract ProgramWorkflow getStatusWorkflow();
	public abstract List<ProgramWorkflowState> getActiveStates();
	public abstract PatientIdentifierType getIdentifierType();
	public abstract List<EncounterType> getEncounterTypes();

	// Data points related to treatment group

	public String getIdentifierTypeShortName() {
		return getIdentifierType().getName().replace(" Number", " #");
	}

	public PatientDataDefinition getPreferredIdentifierDefinition() {
		return df.getPreferredProgramIdentifierAtLocation(getIdentifierType(), getProgram(), new PatientIdentifierConverter());
	}

	public PatientDataDefinition getAllIdentifiersDefinition() {
		return df.getAllIdentifiersOfType(getIdentifierType(), df.getIdentifierCollectionConverter());
	}

	public PatientDataDefinition getCurrentStateAtLocationDefinition(DataConverter converter) {
		return df.getMostRecentStateForWorkflowAtLocationByEndDate(getStatusWorkflow(), converter);
	}

	public PatientDataDefinition getCurrentStateDefinition(DataConverter converter) {
		return df.getMostRecentStateForWorkflowByEndDate(getStatusWorkflow(), converter);
	}

	public EncounterQuery getAllEncountersDefinition() {
		return df.getEncountersOfTypeByEndDate(getEncounterTypes());
	}
}
