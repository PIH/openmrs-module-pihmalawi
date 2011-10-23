package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupTbRegister {

	/** List of encounter included in report */
	private final List<EncounterType> ENCOUNTER_TYPES;

	Helper h = new Helper();

	public SetupTbRegister(Helper helper) {
		h = helper;
		ENCOUNTER_TYPES = Arrays.asList(
				h.encounterType("TB_INITIAL")
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

		ApzuTbRegisterDataSetDefinition dsd = new ApzuTbRegisterDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeFirstVisit(false);
		dsd.setIncludeMissedAppointmentColumns(false);
		dsd.setIncludeProgramEnrollments(true);
		dsd.setIncludeMostRecentVitals(true);
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("National id"));
		dsd.setEncounterTypes(ENCOUNTER_TYPES);		

		return h.createHtmlBreakdown(rd, "Tuberculosis Register_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals("Tuberculosis Register_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class,
				"Tuberculosis Register_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "Tuberculosis Register_");
		h.purgeAll("tb:");
	}

	private ReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.setName("Tuberculosis Register_");
		rd.setupDataSetDefinition();

		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(new Parameter("locationList", "location",
				Location.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore",
				Date.class));
		ecd.setName("tb: Register_");
		ecd.setEncounterTypeList(ENCOUNTER_TYPES);
		h.replaceCohortDefinition(ecd);
		CohortIndicator i = h.newCountIndicator("tb: Register_",
				"tb: Register_", h.parameterMap("locationList", "${location}", "onOrBefore", "${endDate}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "register", "Register", i, null);

		return rd;
	}
}
