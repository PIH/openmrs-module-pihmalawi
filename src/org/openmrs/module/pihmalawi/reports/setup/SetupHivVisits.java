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

public class SetupHivVisits {

	Helper h = new Helper();

	public SetupHivVisits(Helper helper) {
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
			if (rd.getName().equals("HIV Visits_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class,
		"HIV Visits_ Data Set");
		h.purgeDefinition(DataSetDefinition.class,
		"hivvisits: encounters");
		h.purgeDefinition(ReportDefinition.class, "HIV Visits_");
		h.purgeAll("hivvisits:");
	}

	private ReportDefinition createReportDefinition() {
		EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();
		dsd.setName("hivvisits: encounters");
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
		rd.setName("HIV Visits_");
		Map<String, Mapped<? extends DataSetDefinition>> map = new HashMap<String, Mapped<? extends DataSetDefinition>>();
		map.put("art_initial",
				new Mapped<DataSetDefinition>(dsd, h.parameterMap(
						"encounterTypes", Arrays.asList(h
								.encounterType("ART_INITIAL")),
						"encounterDatetimeOnOrBefore", "${endDate}",
						"encounterDatetimeOnOrAfter", "${startDate}",
						"patientIdentifierTypes", Arrays.asList(Context
								.getPatientService().getPatientIdentifierType(
										"ARV Number")))));
		map.put("art_followup",
				new Mapped<DataSetDefinition>(dsd,
						h.parameterMap("encounterTypes", Arrays.asList(h
								.encounterType("ART_FOLLOWUP")),
								"encounterDatetimeOnOrBefore", "${endDate}",
								"encounterDatetimeOnOrAfter", "${startDate}",
								"patientIdentifierTypes", Arrays
										.asList(Context.getPatientService()
												.getPatientIdentifierType(
														"ARV Number")))));
		map.put("part_initial",
				new Mapped<DataSetDefinition>(dsd, h.parameterMap(
						"encounterTypes", Arrays.asList(h
								.encounterType("PART_INITIAL")),
						"encounterDatetimeOnOrBefore", "${endDate}",
						"encounterDatetimeOnOrAfter", "${startDate}",
						"patientIdentifierTypes", Arrays.asList(Context
								.getPatientService().getPatientIdentifierType(
										"HCC Number")))));
		map.put("part_followup",
				new Mapped<DataSetDefinition>(dsd,
						h.parameterMap("encounterTypes", Arrays.asList(h
								.encounterType("PART_FOLLOWUP")),
								"encounterDatetimeOnOrBefore", "${endDate}",
								"encounterDatetimeOnOrAfter", "${startDate}",
								"patientIdentifierTypes", Arrays
										.asList(Context.getPatientService()
												.getPatientIdentifierType(
														"HCC Number")))));
		map.put("exposed_child_initial",
				new Mapped<DataSetDefinition>(dsd, h.parameterMap(
						"encounterTypes", Arrays.asList(h
								.encounterType("EXPOSED_CHILD_INITIAL")),
						"encounterDatetimeOnOrBefore", "${endDate}",
						"encounterDatetimeOnOrAfter", "${startDate}",
						"patientIdentifierTypes", Arrays.asList(Context
								.getPatientService().getPatientIdentifierType(
										"HCC Number")))));
		map.put("exposed_child_followup",
				new Mapped<DataSetDefinition>(dsd,
						h.parameterMap("encounterTypes", Arrays.asList(h
								.encounterType("EXPOSED_CHILD_FOLLOWUP")),
								"encounterDatetimeOnOrBefore", "${endDate}",
								"encounterDatetimeOnOrAfter", "${startDate}",
								"patientIdentifierTypes", Arrays
										.asList(Context.getPatientService()
												.getPatientIdentifierType(
														"HCC Number")))));
		rd.setDataSetDefinitions(map);
		rd.addParameter(new Parameter("startDate", "Start date", Date.class));
		rd.addParameter(new Parameter("endDate", "End date", Date.class));
		h.replaceReportDefinition(rd);

		return rd;
	}
}
