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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KaposiSarcomaMetadata extends CommonMetadata {

	public static String PATIENT_EVALUATION_ENCOUNTER_TYPE = "PATIENT EVALUATION";
	public static String CHEMOTHERAPY_ENCOUNTER_TYPE = "CHEMOTHERAPY";

	public EncounterType getPatientEvaluationEncounterType() {
		return getEncounterType(PATIENT_EVALUATION_ENCOUNTER_TYPE);
	}

	public EncounterType getChemotherapyEncounterType() {
		return getEncounterType(CHEMOTHERAPY_ENCOUNTER_TYPE);
	}

	public List<EncounterType> getKaposiSarcomaEncounterTypes() {
		List<EncounterType> l = new ArrayList<EncounterType>();
		l.add(getPatientEvaluationEncounterType());
		l.add(getChemotherapyEncounterType());
		return l;
	}
}