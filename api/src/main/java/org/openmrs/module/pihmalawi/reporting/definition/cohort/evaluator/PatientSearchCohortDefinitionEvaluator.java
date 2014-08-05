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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.namephonetics.NamePhoneticsService;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.PatientSearchCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Handler(supports = { PatientSearchCohortDefinition.class })
public class PatientSearchCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		PatientSearchCohortDefinition cd = (PatientSearchCohortDefinition) cohortDefinition;
		Cohort c = new Cohort();
		c.getMemberIds().addAll(getPatientsMatchingIdentifier(cd.getSearchPhrase(), context));
		if (c.getMemberIds().isEmpty()) {
			c.getMemberIds().addAll(getPatientsMatchingName(cd.getSearchPhrase(), cd.getSoundexEnabled() == Boolean.TRUE, context));
		}
		return new EvaluatedCohort(c, cd, context);
	}

	protected Set<Integer> getPatientsMatchingIdentifier(String searchString, EvaluationContext context) {
		if (ObjectUtil.isNull(searchString)) {
			return new HashSet<Integer>();
		}
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("distinct pi.patient.patientId");
		q.from(PatientIdentifier.class, "pi");
		q.whereEqual("pi.patient.voided", false);
		for (String searchComponent : searchString.replace("  ", " ").trim().split(" ")) {
			q.whereLike("pi.identifier", searchComponent);
		}
		List<Integer> l = evaluationService.evaluateToList(q, Integer.class, context);
		return new HashSet<Integer>(l);
	}

	protected Set<Integer> getPatientsMatchingName(String searchString, boolean isSoundex, EvaluationContext context) {
		Set<Integer> ret = new HashSet<Integer>();
		if (ObjectUtil.notNull(searchString)) {
			Set<Integer> personIds = new HashSet<Integer>();
			if (isSoundex) {
				Set<PersonName> matchingNames = Context.getService(NamePhoneticsService.class).getMatchingPersonNames(searchString);
				for (PersonName pn : matchingNames) {
					personIds.add(pn.getPerson().getPersonId());
				}
			}
			else {
				HqlQueryBuilder q = new HqlQueryBuilder();
				q.select("pn.person.personId");
				q.from(PersonName.class, "pn");
				for (String searchComponent : searchString.replace("  ", " ").trim().split(" ")) {
					q.startGroup();
					q.whereLike("pn.givenName", searchComponent);
					q.or();
					q.whereLike("pn.familyName", searchComponent);
					q.or();
					q.whereLike("pn.familyName2", searchComponent);
					q.endGroup();
				}
				List<Integer> l = evaluationService.evaluateToList(q, Integer.class, context);
				personIds.addAll(l);
			}
			HqlQueryBuilder q = new HqlQueryBuilder();
			q.select("p.patientId").from(Patient.class, "p").whereIdIn("patientId", personIds);
			ret.addAll(evaluationService.evaluateToList(q, Integer.class, context));
		}

		return ret;
	}
}