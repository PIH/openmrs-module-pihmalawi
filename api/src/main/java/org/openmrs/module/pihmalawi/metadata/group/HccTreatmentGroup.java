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
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class HccTreatmentGroup extends TreatmentGroup {

	@Autowired
	HivMetadata hivMetadata;

	@Override
	public String getName() {
		return "HCC";
	}

	@Override
	public Program getProgram() {
		return hivMetadata.getHivProgram();
	}

	@Override
	public ProgramWorkflow getStatusWorkflow() {
		return hivMetadata.getTreatmentStatusWorkfow();
	}

	@Override
	public List<ProgramWorkflowState> getActiveStates() {
		return Arrays.asList(hivMetadata.getExposedChildState(), hivMetadata.getPreArtState());
	}

	@Override
	public PatientIdentifierType getIdentifierType() {
		return hivMetadata.getHccNumberIdentifierType();
	}

	@Override
	public List<EncounterType> getEncounterTypes() {
		List<EncounterType> l = new ArrayList<EncounterType>();
		l.addAll(hivMetadata.getPreArtEncounterTypes());
		l.addAll(hivMetadata.getExposedChildEncounterTypes());
		return l;
	}
}
