package org.openmrs.module.pihmalawi.reports.extension;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { AppointmentAdherenceCohortDefinition.class })
public class AppointmentAdherenceEvaluator implements CohortDefinitionEvaluator {
	
	public AppointmentAdherenceEvaluator() {
	}
	
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		// todo, fixme if you want
		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
		    HibernatePihMalawiQueryDao.class).get(0);
		AppointmentAdherenceCohortDefinition definition = (AppointmentAdherenceCohortDefinition) cohortDefinition;
		
		Cohort c = q.getPatientsAppointmentAdherence(definition.getEncounterTypes(), definition.getAppointmentConcept(), definition.getFromDate(), definition.getToDate(), definition.getMinimumAdherence(), definition.getMaximumAdherence());
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
}
