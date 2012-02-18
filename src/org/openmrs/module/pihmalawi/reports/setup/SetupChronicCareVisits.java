package org.openmrs.module.pihmalawi.reports.setup;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition.ColumnDisplayFormat;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;

public class SetupChronicCareVisits {

	Helper h = new Helper();

	public SetupChronicCareVisits(Helper helper) {
		h = helper;
	}

	public ReportDefinition[] setup() throws Exception {
		delete();
		ReportDefinition rd = createReportDefinition();
		return new ReportDefinition[] { rd };
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals("Chronic Care Visits_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class,
				"Chronic Care Visits_ Data Set");
		h.purgeDefinition(DataSetDefinition.class,
		"chronicvisits: encounters");
		h.purgeDefinition(ReportDefinition.class, "Chronic Care Visits_");
		h.purgeAll("chronicvisits:");
	}

	private ReportDefinition createReportDefinition() {
		EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();
		dsd.setName("chronicvisits: encounters");
		dsd.addParameter(new Parameter("encounterTypes",
				"encounterTypes", EncounterType.class));
		dsd.addParameter(new Parameter("encounterDatetimeOnOrAfter",
				"encounterDatetimeOnOrAfter", Date.class));
		dsd.addParameter(new Parameter("encounterDatetimeOnOrBefore",
				"encounterDatetimeOnOrBefore", Date.class));
		dsd.addParameter(new Parameter("patientIdentifierTypes",
				"patientIdentifierTypes", PatientIdentifierType.class));
		dsd.setColumnDisplayFormat(Arrays
				.asList(ColumnDisplayFormat.BEST_SHORT_NAME));
		h.replaceDataSetDefinition(dsd);

		ReportDefinition rd = new ReportDefinition();
		rd.setName("Chronic Care Visits_");
		Map<String, Mapped<? extends DataSetDefinition>> map = new HashMap<String, Mapped<? extends DataSetDefinition>>();
		map.put("initial",
				new Mapped<DataSetDefinition>(dsd, h.parameterMap(
						"encounterTypes", Arrays.asList(h
								.encounterType("CHRONIC_CARE_INITIAL")),
						"encounterDatetimeOnOrBefore", "${endDate}",
						"encounterDatetimeOnOrAfter", "${startDate}",
						"patientIdentifierTypes", Arrays.asList(Context
								.getPatientService().getPatientIdentifierType(
										"National ID")))));
		map.put("followup",
				new Mapped<DataSetDefinition>(dsd,
						h.parameterMap("encounterTypes", Arrays.asList(h
								.encounterType("CHRONIC_CARE_FOLLOWUP")),
								"encounterDatetimeOnOrBefore", "${endDate}",
								"encounterDatetimeOnOrAfter", "${startDate}",
								"patientIdentifierTypes", Arrays
										.asList(Context.getPatientService()
												.getPatientIdentifierType(
														"National ID")))));
		rd.setDataSetDefinitions(map);
		rd.addParameter(new Parameter("startDate", "Start date", Date.class));
		rd.addParameter(new Parameter("endDate", "End date", Date.class));
		h.replaceReportDefinition(rd);

		return rd;
	}
}
