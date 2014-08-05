package org.openmrs.module.pihmalawi.reporting.definition.cohort.evaluator;

import org.openmrs.Cohort;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.PatientSearchCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientSearchCohortDefinitionEvaluatorTest extends StandaloneContextSensitiveTest {

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {

		PatientSearchCohortDefinition cd = new PatientSearchCohortDefinition();
		cd.setSearchPhrase("Mike El");

		EvaluationContext context = new EvaluationContext();

		Cohort c = cohortDefinitionService.evaluate(cd, context);
		System.out.println(c.getMemberIds());
	}
}
