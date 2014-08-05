package org.openmrs.module.pihmalawi.reporting;

import org.junit.Ignore;
import org.openmrs.Cohort;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.CompositionEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.PatientEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class QuickTest extends StandaloneContextSensitiveTest {

	@Autowired
	DataFactory df;

	@Autowired
	ChronicCareEncounterQueryLibrary ccEncounterQueries;

	@Autowired
	ChronicCareCohortDefinitionLibrary ccCohorts;

	@Autowired
	EncounterQueryService encounterQueryService;

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue(df.getStartDateParameter().getName(), DateUtil.getDateTime(2014,1,1));
		context.addParameterValue(df.getEndDateParameter().getName(), DateUtil.getDateTime(2014,3,31));

		Cohort hypertensionPats = cohortDefinitionService.evaluate(ccCohorts.getPatientsWithHypertensionDiagnosisByEndDate(), context);
		System.out.println("Found " + hypertensionPats.getSize() + " hypertension pats");

		PatientEncounterQuery encountersForPatients = new PatientEncounterQuery(ccCohorts.getPatientsWithHypertensionDiagnosisByEndDate());
		EncounterQueryResult hypertensionEncounters = encounterQueryService.evaluate(encountersForPatients, context);
		System.out.println("Found " + hypertensionEncounters.getSize() + " encounters of any type in any date range for hypertension pats");

		EncounterQuery followupEncounterQuery = ccEncounterQueries.getChronicCareFollowupEncountersDuringPeriod();
		EncounterQueryResult ccFollowupEncounters = encounterQueryService.evaluate(followupEncounterQuery, context);
		System.out.println("Found " + ccFollowupEncounters.getSize() + " chronic care followup encounters");

		CompositionEncounterQuery composition = df.getEncountersInAll(encountersForPatients, followupEncounterQuery);
		EncounterQueryResult q3Composite = encounterQueryService.evaluate(composition, context);
		System.out.println("Found " + q3Composite.getSize() + " chronic care followup encounters for hypertension patients with composition");

		Set<Integer> q3 = new HashSet<Integer>(hypertensionEncounters.getMemberIds());
		q3.retainAll(ccFollowupEncounters.getMemberIds());

		System.out.println("Found " + q3.size() + " chronic care followup encounters for hypertension patients");
	}
}
