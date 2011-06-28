package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupKSRegister {

	/** List of encounter included in report */
	private final List<EncounterType> ENCOUNTER_TYPES;

	Helper h = new Helper();

	public SetupKSRegister(Helper helper) {
		h = helper;
		ENCOUNTER_TYPES = Arrays.asList(
				h.encounterType("PATIENT EVALUATION"),
				h.encounterType("CHEMOTHERAPY")
				);
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition();
		h.replaceReportDefinition(rd);
		createHtmlBreakdown(rd);

		return new ReportDefinition[] { rd };
	}

	protected ReportDesign createHtmlBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeFirstVisit(false);
		dsd.setIncludeMissedAppointmentColumns(false);
		dsd.setIncludeChronicCareDiagnosis(false);
		dsd.setIncludeArvNumber(true);
		dsd.setIncludeProgramEnrollments(true);
		dsd.setIncludeMostRecentVitals(false);
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("KS Number"));
		dsd.setEncounterTypes(ENCOUNTER_TYPES);		

		return h.createHtmlBreakdown(rd, "KS Register_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals("KS Register_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class,
				"KS_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "KS Register_");
		h.purgeAll("ks:");
	}

	private ReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.setName("KS Register_");
		rd.setupDataSetDefinition();

		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(ReportingConstants.LOCATION_PARAMETER);
		ecd.setName("ks: Register_");
		ecd.setEncounterTypeList(ENCOUNTER_TYPES);
		h.replaceCohortDefinition(ecd);
		CohortIndicator i = h.newCountIndicator("ks: Register_",
				"ks: Register_", h.parameterMap("location", "${location}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "register", "Register", i, null);

		return rd;
	}
}
