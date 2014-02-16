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
package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChronicCareMetadata extends CommonMetadata {

	public static String CHRONIC_CARE_PROGRAM = "Chronic care program";
	public static String CHRONIC_CARE_PROGRAM_TREATMENT_STATUS = "Chronic care treatment status";

	public static String CHRONIC_CARE_INITIAL = "CHRONIC_CARE_INITIAL";
	public static String CHRONIC_CARE_FOLLOWUP = "CHRONIC_CARE_FOLLOWUP";

	public Program getChronicCareProgram() {
		return getProgram(CHRONIC_CARE_PROGRAM);
	}

	public ProgramWorkflow getChronicCareTreatmentStatusWorkflow() {
		return getProgramWorkflow(CHRONIC_CARE_PROGRAM, CHRONIC_CARE_PROGRAM_TREATMENT_STATUS);
	}

	public EncounterType getChronicCareInitialEncounterType() {
		return getEncounterType(CHRONIC_CARE_INITIAL);
	}

	public EncounterType getChronicCareFollowupEncounterType() {
		return getEncounterType(CHRONIC_CARE_FOLLOWUP);
	}

	public List<EncounterType> getChronicCareEncounterTypes() {
		List<EncounterType> l = new ArrayList<EncounterType>();
		l.add(getChronicCareInitialEncounterType());
		l.add(getChronicCareFollowupEncounterType());
		return l;
	}
}