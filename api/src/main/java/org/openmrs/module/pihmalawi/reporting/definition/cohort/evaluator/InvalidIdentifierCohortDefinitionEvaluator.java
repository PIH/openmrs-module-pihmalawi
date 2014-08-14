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
package org.openmrs.module.pihmalawi.reporting.definition.cohort.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.InvalidIdentifierCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { InvalidIdentifierCohortDefinition.class })
public class InvalidIdentifierCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	HivMetadata metadata;

	@Autowired
	EvaluationService evaluationService;

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		InvalidIdentifierCohortDefinition cd = (InvalidIdentifierCohortDefinition) cohortDefinition;
		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select 	distinct p.patient_id");
		q.append("from 		patient_identifier i, patient p");
		q.append("where 	i.patient_id = p.patient_id");
		q.append("and		i.voided = 0 and p.voided = 0");
		q.append("and		i.identifier_type = " + cd.getIdentifierType().getPatientIdentifierTypeId());
		q.append("and (");
		if (cd.getIdentifierFormat().contains("<location>")) {
			int numLocations = 0;
			for (String locCode : metadata.getLocationShortNames().values()) {
				q.append(numLocations == 0 ? "" : " and ");
				q.append("identifier NOT regexp '"+cd.getIdentifierFormat().replace("<location>", locCode)+"'");
				numLocations++;
			}
		}
		else {
			q.append("identifier NOT regexp '").append(cd.getIdentifierFormat()).append("'");
		}
		q.append(")");

		Cohort c = new Cohort(evaluationService.evaluateToList(q, Integer.class, context));
		return new EvaluatedCohort(c, cd, context);
	}
}