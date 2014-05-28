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

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.Cd4DataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates a Cd4DataDefinition to produce a PatientData
 */
@Handler(supports = Cd4DataDefinition.class, order = 50)
public class Cd4DataEvaluator implements PatientDataEvaluator {

	@Autowired
	private HivMetadata metadata;

	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		Cd4DataDefinition def = (Cd4DataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.personId", "o");
		q.from(Obs.class, "o");
		q.wherePersonIn("o.personId", context);
		q.whereInAny("o.concept", metadata.getCd4CountConcept(), metadata.getClinicianReportedCd4Concept());
		q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
		q.orderAsc("o.obsDatetime");

		List<Object[]> results = evaluationService.evaluateToList(q);
		for (Object[] row : results) {
			Integer pId = (Integer)row[0];
			Obs o = (Obs)row[1];
			List<Obs> obsForPatient = (List<Obs>)c.getData().get(pId);
			if (obsForPatient == null) {
				obsForPatient = new ArrayList<Obs>();
				c.getData().put(pId, obsForPatient);
			}
			obsForPatient.add(o);
		}

		return c;
	}
}
