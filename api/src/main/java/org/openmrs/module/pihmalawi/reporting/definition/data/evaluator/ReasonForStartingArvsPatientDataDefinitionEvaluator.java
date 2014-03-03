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
package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ReasonForStartingArvsPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Evaluates a ReasonForStartingArvsPatientDataDefinition to produce a PatientData
 */
@Handler(supports = ReasonForStartingArvsPatientDataDefinition.class, order = 50)
public class ReasonForStartingArvsPatientDataDefinitionEvaluator implements PatientDataEvaluator {

	@Autowired
	private HivPatientDataLibrary hivLibrary;

	@Autowired
	private PatientDataService patientDataService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedPatientData pd = new EvaluatedPatientData(definition, context);

		Map<String, PatientDataDefinition> m = new LinkedHashMap<String, PatientDataDefinition>();
		m.put("CD4", hivLibrary.getFirstArtInitialCd4Count());
		m.put("KS", hivLibrary.getFirstArtInitialKsSideEffectsWorsening());
		m.put("TB", hivLibrary.getFirstArtInitialTbTreatmentStatus());
		m.put("STAGE", hivLibrary.getFirstArtInitialWhoStage());
		m.put("TLC", hivLibrary.getFirstArtInitialCd4Percent());
		m.put("PSHD", hivLibrary.getFirstArtInitialPresumedSevereHivPresent());

		for (String questionKey : m.keySet()) {
			PatientDataDefinition def = m.get(questionKey);
			PatientData data = patientDataService.evaluate(Mapped.mapStraightThrough(def), context);
			for (Map.Entry<Integer, Object> e : data.getData().entrySet()) {
				Map<String, Object> reasonsForPatient = (Map<String, Object>)pd.getData().get(e.getKey());
				if (reasonsForPatient == null) {
					reasonsForPatient = new LinkedHashMap<String, Object>();
					pd.getData().put(e.getKey(), reasonsForPatient);
				}
				reasonsForPatient.put(questionKey, e.getValue());
			}
		}

		return pd;
	}
}
