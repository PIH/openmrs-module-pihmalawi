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
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TbMetadata extends CommonMetadata {

	public static String TB_PROGRAM = "TB program";
	public static String TB_PROGRAM_TREATMENT_STATUS = "Treatment status";
	public static String TB_PROGRAM_TREATMENT_STATUS_ON_TREATMENT = "Currently in treatment";

	public static String TB_INITIAL_ENCOUNTER_TYPE = "TB_INITIAL";
	public static String TB_FOLLOWUP_ENCOUNTER_TYPE = "TB_FOLLOWUP";

	public static final String DISTRICT_TB_NUMBER = "District TB Number";

	public Program getTbProgram() {
		return getProgram(TB_PROGRAM);
	}

	public ProgramWorkflow getTreatmentStatusWorkfow() {
		return getProgramWorkflow(TB_PROGRAM, TB_PROGRAM_TREATMENT_STATUS);
	}

	public ProgramWorkflowState getOnTreatmentState() {
		return getProgramWorkflowState(TB_PROGRAM, TB_PROGRAM_TREATMENT_STATUS, TB_PROGRAM_TREATMENT_STATUS_ON_TREATMENT);
	}

	public EncounterType getTbInitialEncounterType() {
		return getEncounterType(TB_INITIAL_ENCOUNTER_TYPE);
	}

	public EncounterType getTbFollowupEncounterType() {
		return getEncounterType(TB_FOLLOWUP_ENCOUNTER_TYPE);
	}

	public List<EncounterType> getTbEncounterTypes() {
		List<EncounterType> l = new ArrayList<EncounterType>();
		l.add(getTbInitialEncounterType());
		l.add(getTbFollowupEncounterType());
		return l;
	}

	public PatientIdentifierType getDistrictTbNumberIdentifierType() {
		return getPatientIdentifierType(DISTRICT_TB_NUMBER);
	}
}