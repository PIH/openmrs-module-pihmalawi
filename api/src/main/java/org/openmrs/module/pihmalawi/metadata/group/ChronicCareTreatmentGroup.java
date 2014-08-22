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
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ChronicCareTreatmentGroup extends TreatmentGroup {

	@Autowired
	ChronicCareMetadata ccMetadata;

	@Override
	public String getName() {
		return "CC";
	}

	@Override
	public Program getProgram() {
		return ccMetadata.getChronicCareProgram();
	}

	@Override
	public ProgramWorkflow getStatusWorkflow() {
		return ccMetadata.getChronicCareTreatmentStatusWorkflow();
	}

	@Override
	public List<ProgramWorkflowState> getActiveStates() {
		return Arrays.asList(ccMetadata.getChronicCareStatusOnTreatment());
	}

	@Override
	public PatientIdentifierType getIdentifierType() {
		return ccMetadata.getChronicCareNumber();
	}

	@Override
	public List<EncounterType> getEncounterTypes() {
		return ccMetadata.getChronicCareEncounterTypes();
	}

	@Override
	public String getIdentifierTypeShortName() {
		return "CCC ID";
	}
}
