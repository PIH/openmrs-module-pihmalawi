package org.openmrs.module.pihmalawi.reports.dataset;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

@ContextConfiguration(locations = {"classpath:openmrs-servlet.xml"}, inheritLocations = true)
public class AppointmentAdherencePatientDataSetTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Test
	public void testAdherenceDataSet() throws Exception {
		if (getLoadCount() == 1) {
			authenticate();
			HivMetadata hivMetadata = new HivMetadata();

			AppointmentAdherencePatientDataSetDefinition dsd = new AppointmentAdherencePatientDataSetDefinition();
			dsd.setEncounterTypes(Arrays.asList(hivMetadata.getArtFollowupEncounterType()));
			dsd.setPatientIdentifierType(Context.getPatientService().getPatientIdentifierTypeByName("ARV Number"));

			EvaluationContext context = new EvaluationContext();
			Cohort baseCohort = new Cohort("61878");
			context.setBaseCohort(baseCohort);
			context.addParameterValue("startDate", DateUtil.getDateTime(2006, 1, 1));
			context.addParameterValue("endDate", DateUtil.getDateTime(2012, 11, 30));
			context.addParameterValue("location", hivMetadata.getLuwaniHc());

			DataSetDefinitionService svc = Context.getService(DataSetDefinitionService.class);
			DataSet dataset = svc.evaluate(dsd, context);
			DataSetUtil.printDataSet(dataset, System.out);
		}
	}
}
