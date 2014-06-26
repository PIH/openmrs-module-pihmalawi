package org.openmrs.module.pihmalawi.reports.dataset;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.reports.renderer.KsRegisterBreakdownRenderer;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class KsRegisterBreakdownTest extends StandaloneContextSensitiveTest {

	@Override
	public void performTest() throws Exception {
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

		DataSetUtil.printDataSet(ds, System.out);
	}
}
