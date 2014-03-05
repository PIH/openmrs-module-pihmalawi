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

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.EncounterAndObs;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.EncounterAndObsPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Evaluates a ChwPatientDataDefinition to produce a PatientData
 */
@Handler(supports = EncounterAndObsPatientDataDefinition.class, order = 50)
public class EncounterAndObsPatientDataDefinitionEvaluator implements PatientDataEvaluator {

	@Autowired
	private PatientDataService patientDataService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EncounterAndObsPatientDataDefinition def = (EncounterAndObsPatientDataDefinition)definition;
		EvaluatedPatientData ret = new EvaluatedPatientData(def, context);

		EvaluationContext childContext = EvaluationContext.cloneForChild(context, def.getWhichEncounters());
		PatientData encounters = patientDataService.evaluate(def.getWhichEncounters(), childContext);

		for (Integer pId : encounters.getData().keySet()) {
			Object encounterObject = encounters.getData().get(pId);
			List<EncounterAndObs> l = new ArrayList<EncounterAndObs>();
			List<Encounter> patientEncounters = (encounterObject instanceof List ? (List)encounterObject : Arrays.asList((Encounter)encounterObject));
			for (Encounter e : patientEncounters) {
				EncounterAndObs encounterAndObs = new EncounterAndObs(e);
				for (Obs o : e.getAllObs()) {
					if (!o.isVoided() && def.getConcepts().contains(o.getConcept())) {
						encounterAndObs.addObs(o);
					}
				}
				l.add(encounterAndObs);
				if (encounterAndObs.getObs().size() > 1) {
					System.out.println("Encounter " + encounterAndObs.getEncounter().getEncounterId() + " has " + encounterAndObs.getObs().size() + " Obs for " + def.getConcepts());
				}
			}
			ret.getData().put(pId, l);
		}

		return ret;
	}
}
