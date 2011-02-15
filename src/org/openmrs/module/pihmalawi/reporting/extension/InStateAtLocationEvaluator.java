package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { InStateAtLocationCohortDefinition.class })
public class InStateAtLocationEvaluator implements CohortDefinitionEvaluator {
	
	public InStateAtLocationEvaluator() {
	}
	
	public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		// todo, fixme if you want
		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
		    HibernatePihMalawiQueryDao.class).get(0);
		InStateAtLocationCohortDefinition definition = (InStateAtLocationCohortDefinition) cohortDefinition;
		Date onOrAfter = definition.getOnOrAfter();
		Date onOrBefore = definition.getOnOrBefore();
		if (definition.getOnDate() != null) {
			onOrAfter = definition.getOnDate();
			onOrBefore = definition.getOnDate();
		}
		
		return q.getPatientsInStateAtLocation(definition.getState(), onOrAfter, onOrBefore, definition.getLocation());
	}
}
