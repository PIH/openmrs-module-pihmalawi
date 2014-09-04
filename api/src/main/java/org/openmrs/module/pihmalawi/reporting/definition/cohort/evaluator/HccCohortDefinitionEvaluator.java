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
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.HccCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HccCohortDefinition.class })
public class HccCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		HccCohortDefinition cd = (HccCohortDefinition) cohortDefinition;
		EvaluationContext childContext = context.shallowCopy();
		childContext.addParameterValue("location", cd.getLocation());
		childContext.addParameterValue("endDate", cd.getEndDate());
		Cohort c = new Cohort();
		add(hivCohorts.getStartedPreArtWithHccNumberAtLocationByEndDate(), childContext, c);
		add(hivCohorts.getStartedExposedChildWithHccNumberAtLocationByEndDate(), childContext, c);
		if (cd.getIncludeOldPreArtPatients()) {
			add(hivCohorts.getStartedPreArtIncludingOldPatientsAtLocationByEndDate(), childContext, c);
		}
		return new EvaluatedCohort(c, cd, context);
	}

	public void add(CohortDefinition cd, EvaluationContext context, Cohort c) throws EvaluationException {
		Cohort pats = cohortDefinitionService.evaluate(cd, context);
		c.getMemberIds().addAll(pats.getMemberIds());
	}
}