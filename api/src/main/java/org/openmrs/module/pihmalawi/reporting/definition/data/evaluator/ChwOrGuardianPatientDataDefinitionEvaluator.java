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
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ChwOrGuardianPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluates a ChwPatientDataDefinition to produce a PatientData
 */
@Handler(supports = ChwOrGuardianPatientDataDefinition.class, order = 50)
public class ChwOrGuardianPatientDataDefinitionEvaluator implements PatientDataEvaluator {

	@Autowired
	private BasePatientDataLibrary baseLibrary;

	@Autowired
	private PatientDataService patientDataService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		ChwOrGuardianPatientDataDefinition def = (ChwOrGuardianPatientDataDefinition) definition;
		EvaluatedPatientData pd = new EvaluatedPatientData(def, context);

		PatientData chwData = patientDataService.evaluate(Mapped.mapStraightThrough(baseLibrary.getChw()), context);
		pd.getData().putAll(chwData.getData());

		PatientData guardianData = patientDataService.evaluate(Mapped.mapStraightThrough(baseLibrary.getParentOrGuardian()), context);
		for (Integer pId : guardianData.getData().keySet()) {
			if (!pd.getData().containsKey(pId)) {
				pd.getData().put(pId, guardianData.getData().get(pId) + " (Guardian)");
			}
		}

		return pd;
	}
}
