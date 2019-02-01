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
import org.openmrs.module.pihmalawi.common.BMI;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.BmiPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.NutritionHistoryPatientDataDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates a BmiPatientDataDefinition to produce a PatientData
 */
@Handler(supports = BmiPatientDataDefinition.class, order = 50)
public class BmiPatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	private PatientDataService patientDataService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        BmiPatientDataDefinition def = (BmiPatientDataDefinition) definition;
		EvaluatedPatientData pd = new EvaluatedPatientData(def, context);

		EvaluationContext childContext = context.shallowCopy();
		if (def.getEndDate() != null) {
		    childContext.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), def.getEndDate());
        }

		NutritionHistoryPatientDataDefinition nutritionHistoryDef = new NutritionHistoryPatientDataDefinition();
		nutritionHistoryDef.setEndDate(def.getEndDate());

		EvaluatedPatientData nutritionHistory = patientDataService.evaluate(nutritionHistoryDef, childContext);

		for (Integer pId : nutritionHistory.getData().keySet()) {

			// nutrition history is sorted with most recent first, so we take the first BMI we find
			List<Object> nutritionHistoryForPatient = (List<Object>) nutritionHistory.getData().get(pId);

			BMI bmi = null;
			for (Object entry : nutritionHistoryForPatient) {
				if (entry instanceof BMI) {
					bmi = (BMI) entry;
					break;
				}
			}
			pd.getData().put(pId, bmi);

		}

		return pd;
	}
}
