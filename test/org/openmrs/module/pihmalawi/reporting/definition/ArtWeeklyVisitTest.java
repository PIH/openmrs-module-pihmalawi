package org.openmrs.module.pihmalawi.reporting.definition;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupArtWeeklyVisit;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.TestUtil;

@SkipBaseSetup
public class ArtWeeklyVisitTest extends BaseModuleContextSensitiveTest {
	
	Helper h = new Helper();
	
	//		Helper h = new TransientHelper();
	
	@Before
	public void setup() throws Exception {
		//		executeDataSet("/home/xian/Desktop/xian.xml");
		// test with test2test
		authenticate();
		
		new SetupArtWeeklyVisit(h).setupHivWeekly(true);
	}
	
	//	@Test
	public void testXmlSetup() throws Exception {
		TestUtil.printOutTableContents(getConnection(), "users", "patient", "program", "concept", "concept_name");
		
		List<Patient> l = Context.getPatientService().getAllPatients();
		for (Patient p : l) {
			System.out.println(p.getGivenName() + " " + p.getFamilyName());
		}
	}
	
	@Test
	public void testArtApps() throws Exception {
		PeriodIndicatorReportDefinition rd = (PeriodIndicatorReportDefinition) h.findDefinition(
		    PeriodIndicatorReportDefinition.class, "ART Appointments_");
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", Context.getLocationService().getLocation(2));
		ReportData data = rs.evaluate(rd, context);
		DataSet ds = data.getDataSets().values().iterator().next();
		
		assertColumnValue(ds, "artndh", 1);
		assertColumnValue(ds, "artndh", 1);
		assertColumnValue(ds, "artndh", 1);
		assertColumnValue(ds, "artndh", 1);
		assertColumnValue(ds, "artndh", 1);
		assertColumnValue(ds, "artndh", 1);
	}
	
	private void assertColumnValue(DataSet ds, String columnName, int expected) {
		IndicatorResult ir = (IndicatorResult) ds.iterator().next().getColumnValue(columnName);
		Assert.assertEquals(expected, ir.getValue().intValue());
    }
}
