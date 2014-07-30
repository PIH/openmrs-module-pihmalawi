package org.openmrs.module.pihmalawi.reporting;

import org.junit.Ignore;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.springframework.beans.factory.annotation.Autowired;

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

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue(df.getStartDateParameter().getName(), DateUtil.getDateTime(2014,1,1));
		context.addParameterValue(df.getEndDateParameter().getName(), DateUtil.getDateTime(2014,3,31));

		EncounterQueryResult baseEncounters = encounterQueryService.evaluate(ccEncounterQueries.getChronicCareFollowupEncountersDuringPeriod(), context);
		System.out.println("Found " + baseEncounters.getSize() + " base encounters");

		EncounterQueryResult r1 = encounterQueryService.evaluate(ccEncounterQueries.getChronicCareEncountersWithSystolicBloodPressureOver180DuringPeriod(), context);
		System.out.println("Found " + r1.getSize() + " with high sbp");

		EncounterQueryResult r2 = encounterQueryService.evaluate(ccEncounterQueries.getChronicCareEncountersWithDiastolicBloodPressureOver110DuringPeriod(), context);
		System.out.println("Found " + r2.getSize() + " with high dbp");

		Set<Integer> all = r1.getMemberIds();
		all.addAll(r2.getMemberIds());
		System.out.println("Found " + all.size() + " with high bp");
	}
}
