package org.openmrs.module.pihmalawi.reporting;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.InvalidIdentifierCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.reports.HivDataQualityReport;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;

public class QuickTest extends StandaloneContextSensitiveTest {

	@Autowired
	DataFactory df;

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	BaseCohortDefinitionLibrary baseCohorts;

	@Autowired
	ReportDefinitionService reportDefinitionService;

	@Autowired
	IndicatorService indicatorService;

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	HivDataQualityReport hivDataQualityReport;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {
		LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.TRACE);

		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("user", Context.getUserService().getUserByUsername("cgoliath"));

		ReportDefinition rd = reportDefinitionService.getDefinitions("HIV Data Quality_", true).get(0);
		CohortIndicatorDataSetDefinition dsd = (CohortIndicatorDataSetDefinition) rd.getDataSetDefinitions().values().iterator().next().getParameterizable();

		for (CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn c : dsd.getColumns()) {
			if (c.getName().equals("format")) {
				CohortIndicatorResult r = (CohortIndicatorResult)indicatorService.evaluate(c.getIndicator(), context);
				Cohort oldCohort = r.getCohort();

				InvalidIdentifierCohortDefinition cd = new InvalidIdentifierCohortDefinition();
				cd.setIdentifierType(hivMetadata.getArvNumberIdentifierType());
				cd.setIdentifierFormat("^<location> [1-9][0-9]?[0-9]?[0-9]?$");
				Cohort newCohort = cohortDefinitionService.evaluate(cd, context);

				System.out.println("Old: " + oldCohort.getMemberIds());
				System.out.println("New" + newCohort.getMemberIds());
			}
		}
	}
}
