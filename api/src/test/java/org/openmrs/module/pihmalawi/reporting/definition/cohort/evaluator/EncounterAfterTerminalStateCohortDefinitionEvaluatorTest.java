package org.openmrs.module.pihmalawi.reporting.definition.cohort.evaluator;

import org.junit.Ignore;
import org.openmrs.Cohort;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.EncounterAfterTerminalStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class EncounterAfterTerminalStateCohortDefinitionEvaluatorTest extends StandaloneContextSensitiveTest {

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {

		EncounterAfterTerminalStateCohortDefinition cd = new EncounterAfterTerminalStateCohortDefinition();
		cd.setEncounterTypes(hivMetadata.getHivAndExposedChildEncounterTypes());
		cd.setProgram(hivMetadata.getHivProgram());
		cd.setEndDate(DateUtil.getDateTime(2014, 8, 1));

		EvaluationContext context = new EvaluationContext();

		Cohort c = cohortDefinitionService.evaluate(cd, context);
		System.out.println("Num patients found: " + c.size());
		System.out.println(c.getMemberIds());
	}
}
