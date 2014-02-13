package org.openmrs.module.pihmalawi.reports.extension;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { PatientStateAtLocationCohortDefinition.class })
public class PatientStateAtLocationEvaluator implements CohortDefinitionEvaluator {
	
	public PatientStateAtLocationEvaluator() {
	}
	
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		// todo, fixme if you want
		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
		    HibernatePihMalawiQueryDao.class).get(0);
		PatientStateAtLocationCohortDefinition definition = (PatientStateAtLocationCohortDefinition) cohortDefinition;
		
		Cohort c = q.getPatientsHavingStatesAtLocation(definition.getState(), definition.getStartedOnOrAfter(), definition
		        .getStartedOnOrBefore(), definition.getEndedOnOrAfter(), definition.getEndedOnOrBefore(), definition
		        .getLocation());

		return new EvaluatedCohort(c, cohortDefinition, context);
	}
}
