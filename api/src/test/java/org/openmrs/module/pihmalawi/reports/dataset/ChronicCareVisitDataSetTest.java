package org.openmrs.module.pihmalawi.reports.dataset;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reports.setup.SetupChronicCareVisits;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Ignore
@ContextConfiguration(locations = {"classpath:openmrs-servlet.xml"}, inheritLocations = true)
public class ChronicCareVisitDataSetTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	@Test
	@Ignore
	public void testDataSet() throws Exception {

		ChronicCareMetadata ccMetadata = new ChronicCareMetadata();

		ReportDefinition rd = SetupChronicCareVisits.createReportDefinition();

		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("fromDate", DateUtil.getDateTime(2013, 1, 1));
		context.addParameterValue("toDate", DateUtil.getDateTime(2013, 3, 31));
		context.addParameterValue("location", ccMetadata.getNenoHospital());
		context.addParameterValue("which", TimeQualifier.LAST);
		context.addParameterValue("limitedToPatientsEnrolledAtEnd", false);

		ReportDefinitionService svc = Context.getService(ReportDefinitionService.class);
		ReportData data = svc.evaluate(rd, context);

		XlsReportRenderer renderer = new XlsReportRenderer();

		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "test.xls";
		FileOutputStream fos = new FileOutputStream(outFile);
		renderer.render(data, "", fos);

		System.out.println("Results renderered to " + outFile);
	}

	/**
	 * Prints the passed dataset to the console
	 */
	public static void printDataSet(DataSet d, OutputStream out) {

		Map<DataSetColumn, Integer> columnLengthMap = new LinkedHashMap<DataSetColumn, Integer>();
		for (DataSetColumn c : d.getMetaData().getColumns()) {
			columnLengthMap.put(c, c.getLabel().length());
		}
		for (Iterator<DataSetRow> i = d.iterator(); i.hasNext();) {
			DataSetRow r = i.next();
			for (DataSetColumn c : r.getColumnValues().keySet()) {
				String val = ObjectUtil.nvlStr(r.getColumnValue(c), "");
				if (columnLengthMap.get(c) < val.length()) {
					columnLengthMap.put(c, val.length());
				}
			}
		}

		StringBuilder output = new StringBuilder();
		for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
			StringBuilder n = new StringBuilder(c.getKey().getLabel());
			while (n.length() < c.getValue()) {
				n.append(" ");
			}
			output.append(n.toString() + "\t");
		}
		output.append("\n");
		for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
			StringBuilder n = new StringBuilder();
			while (n.length() < c.getValue()) {
				n.append("-");
			}
			output.append(n.toString() + "\t");
		}
		output.append("\n");
		for (Iterator<DataSetRow> i = d.iterator(); i.hasNext();) {
			DataSetRow r = i.next();
			for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
				StringBuilder n = new StringBuilder(ObjectUtil.nvlStr(r.getColumnValue(c.getKey()), ""));
				while (n.length() < c.getValue()) {
					n.append(" ");
				}
				output.append(n + "\t");
			}
			output.append("\n");
		}
		System.out.println(output.toString());
	}
}
