package org.openmrs.module.pihmalawi.reporting;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.reports.HivDataQualityReport;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;

public class QuickTest extends StandaloneContextSensitiveTest {

	@Autowired
	DataFactory df;

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	ChronicCareMetadata chronicCareMetadata;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	HivEncounterQueryLibrary hivEncounterQueries;

	@Autowired
	ReportDefinitionService reportDefinitionService;

	@Autowired
	DataSetDefinitionService dataSetDefinitionService;

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

		EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();
		BasicEncounterQuery q = new BasicEncounterQuery();
		q.setEncounterTypes(hivMetadata.getExposedChildEncounterTypes());
		q.setOnOrAfter(DateUtil.getDateTime(2014, 7, 1));
		q.setOnOrBefore(DateUtil.getDateTime(2014, 7, 7));
		q.addLocation(hivMetadata.getNenoHospital());

		dsd.addRowFilter(Mapped.mapStraightThrough(q));

		DataSet data = dataSetDefinitionService.evaluate(dsd, context);
		DataSetUtil.printDataSet(data, System.out);
	}
}
