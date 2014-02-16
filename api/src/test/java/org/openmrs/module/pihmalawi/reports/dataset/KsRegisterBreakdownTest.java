package org.openmrs.module.pihmalawi.reports.dataset;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.renderer.KsRegisterBreakdownRenderer;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.OutputStream;
import java.util.*;

@Ignore
@ContextConfiguration(locations = {"classpath:openmrs-servlet.xml"}, inheritLocations = true)
public class KsRegisterBreakdownTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	@Test
	public void testKsRenderer() throws Exception {

		Location location = Context.getLocationService().getLocation("Neno District Hospital");

		PatientIdentifierType ksNumber = Context.getPatientService().getPatientIdentifierTypeByName("KS Number");

		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(Context.getEncounterService().getEncounterType("PATIENT EVALUATION"));
		encounterTypes.add(Context.getEncounterService().getEncounterType("CHEMOTHERAPY"));

		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setLocationList(Arrays.asList(location));
		ecd.setEncounterTypeList(encounterTypes);

		EvaluatedCohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(ecd, new EvaluationContext());

		SimpleDataSet ds = new SimpleDataSet(new SimplePatientDataSetDefinition(), new EvaluationContext());
		KsRegisterBreakdownRenderer renderer = new KsRegisterBreakdownRenderer();
		for (Integer patientId : baseCohort.getMemberIds()) {
			Patient p = Context.getPatientService().getPatient(patientId);
			ds.addRow(renderer.renderRow(p, ksNumber, location, new Date(), new Date()));
		}

		printDataSet(ds, System.out);
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
