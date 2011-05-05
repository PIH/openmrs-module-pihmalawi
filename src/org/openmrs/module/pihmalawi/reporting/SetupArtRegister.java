package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.repository.ArtReportElements;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupArtRegister {

	/** List of encounter included in report */
	private final List<EncounterType> ENCOUNTER_TYPES;

	Helper h = new Helper();

	public SetupArtRegister(Helper helper) {
		h = helper;
		ENCOUNTER_TYPES = Arrays.asList(
				h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP")
				);
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition("artreg");
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
		dsd.setIncludeMissedappointmentColumns(false);
		dsd.setIncludeProgramOutcome(true);
		dsd.setIncludeWeight(true);
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setProgram(h.program("HIV PROGRAM"));
		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setEncounterTypes(ENCOUNTER_TYPES);		

		return h.createHtmlBreakdown(rd, "ART Register_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals("ART Register_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class,
				"ART_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "ART Register_");
		h.purgeAll("artreg");
	}

	private ReportDefinition createReportDefinition(String prefix) {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.setName("ART Register_");
		rd.setupDataSetDefinition();
		
		CohortDefinition cd = ArtReportElements.everOnArtWithLocationStartedOnOrBefore(prefix);
		CohortIndicator i = h.newCountIndicator(prefix + "Register_",
				cd.getName(), h.parameterMap("location", "${location}", "startedOnOrBefore", "${endDate}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "register", "Register", i, null);
		
		return rd;
	}
}
