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

import org.openmrs.PatientState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.FirstStateAfterStatePatientDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramStatesForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates a FirstStateAfterStatePatientDataDefinition to produce a PatientData
 */
@Handler(supports=FirstStateAfterStatePatientDataDefinition.class, order=50)
public class FirstStateAfterStatePatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	PatientDataService patientDataService;

	/**
	 * @see org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		FirstStateAfterStatePatientDataDefinition def = (FirstStateAfterStatePatientDataDefinition) definition;
		EvaluatedPatientData ret = new EvaluatedPatientData(def, context);

		ProgramStatesForPatientDataDefinition hivProgramStates = new ProgramStatesForPatientDataDefinition();
		hivProgramStates.setWorkflow(hivMetadata.getTreatmentStatusWorkfow());
		hivProgramStates.setStartedOnOrBefore(def.getStartedOnOrBefore());
		hivProgramStates.setLocation(def.getLocation());

		PatientData stateData = patientDataService.evaluate(hivProgramStates, context);
		for (Integer pId : stateData.getData().keySet()) {
			List<PatientState> states = (List<PatientState>)stateData.getData().get(pId);
			if (states != null) {
				boolean addNext = false;
				for (PatientState state : states) {
					if (addNext) {
						ret.getData().put(pId, state);
					}
					addNext = def.getPrecedingStates().contains(state.getState());

					// Ensure that if not states are found after a match, that the state itself is returned
					if (addNext && ret.getData().get(pId) == null) {
						ret.getData().put(pId, state);
					}
				}
			}
		}

		return ret;
	}
}
