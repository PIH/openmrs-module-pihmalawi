package org.openmrs.module.pihmalawi.reports.setup;


import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

@Ignore
public class SetupChronicCareRegisterTest extends BaseModuleContextSensitiveTest {

	@Autowired
	HivMetadata hivMetadata;

	protected ReportDefinitionService getReportDefinitionService() {
		return Context.getService(ReportDefinitionService.class);
	}

	@Test
	public void testReport() throws Exception {
		SetupChronicCareRegister setup = new SetupChronicCareRegister(new ReportHelper());
		setup.setup();

		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("endDate", DateUtil.getDateTime(2013,9,30));
		context.addParameterValue("location", hivMetadata.getNenoHospital());

		ReportData rd = getReportDefinitionService().evaluate(setup.reportDefinition, context);
		ReportRenderer renderer = setup.reportDesign.getRendererType().newInstance();
		String argument = setup.reportDesign.getUuid() + ":xls";
		File outFile = new File(SystemUtils.getJavaIoTmpDir(), renderer.getFilename(setup.reportDefinition, argument));
		FileOutputStream fos = new FileOutputStream(outFile);
		renderer.render(rd, argument, fos);
		fos.close();
	}

	// Helper method no longer used here but might want again
	public void testIndicator(CohortIndicatorDataSetDefinition dsd, String name, EvaluationContext context) throws Exception {
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		for (CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn column : dsd.getColumns()) {
			if (column.getName().startsWith(name)) {
				Cohort indicatorCohort = cds.evaluate(column.getIndicator().getParameterizable().getCohortDefinition(), context);
				System.out.println(column.getName() + ": " + indicatorCohort.size());
				if (column.getDimensionOptions() != null) {
					for (String dimensionKey : column.getDimensionOptions().keySet()) {
						System.out.println("Broken down by " + dimensionKey + "...");
						Mapped<CohortDefinitionDimension> dimension = dsd.getDimension(dimensionKey);
						for (String option : dimension.getParameterizable().getOptionKeys()) {
							Cohort dimensionCohort = cds.evaluate(dimension.getParameterizable().getCohortDefinition(option), context);
							System.out.println(option + ": " + dimensionCohort.size() + ".  Intersected indicator = " + Cohort.intersect(indicatorCohort, dimensionCohort).size());
						}
					}
				}
			}
		}
	}

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_neno_19x?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		return p;
	}
}
