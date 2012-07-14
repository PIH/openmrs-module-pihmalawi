package org.openmrs.module.pihmalawi.reports.setup;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.MetadataLookup;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.dataset.AppointmentAdherencePatientDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.dataset.HtmlBreakdownDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.renderer.ArtRegisterBreakdownRenderer;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
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

public class SetupArtRegister {

	ReportHelper h = new ReportHelper();

	public SetupArtRegister(ReportHelper helper) {
		h = helper;
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition("artreg");
		h.replaceReportDefinition(rd);
		createHtmlBreakdown(rd, "ART Register_");
//		createAppointmentAdherenceBreakdown(rd);

		rd = createReportDefinitionForAllLocations("artregcomplete");
		h.replaceReportDefinition(rd);
		createHtmlBreakdown(rd, "ART Register For All Locations_");

		return new ReportDefinition[] { rd };
	}

	private ReportDesign createAppointmentAdherenceBreakdown(ReportDefinition rd) throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		AppointmentAdherencePatientDataSetDefinition dsd = new AppointmentAdherencePatientDataSetDefinition();
		dsd.setEncounterTypes(Arrays.asList(MetadataLookup.encounterType("ART_FOLLOWUP")));
		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));

		m.put("breakdown", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, "ART Register Appointment Adherence_", m);
	}

	protected ReportDesign createHtmlBreakdown(ReportDefinition rd, String name)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		HtmlBreakdownDataSetDefinition dsd = new HtmlBreakdownDataSetDefinition();
		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setHtmlBreakdownPatientRowClassname(ArtRegisterBreakdownRenderer.class
				.getName());

		m.put("breakdown", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, name, m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("ART Register")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "ART Register_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "ART Register_");
		h.purgeDefinition(DataSetDefinition.class, "ART Register For All Locations (SLOW)_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "ART Register For All Locations (SLOW)_");
		h.purgeAll("artreg");
		h.purgeAll("artregcomplete");
	}

	private ReportDefinition createReportDefinition(String prefix) {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.setName("ART Register_");
		rd.setupDataSetDefinition();

		CohortDefinition cd = ApzuReportElementsArt
				.artEverEnrolledAtLocationOnDate(prefix);
		CohortIndicator i = h.newCountIndicator(prefix + ": Register_", cd
				.getName(), h.parameterMap("location", "${location}",
				"startedOnOrBefore", "${endDate}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "breakdown", "Breakdown", i, null);

		return rd;
	}

	private ReportDefinition createReportDefinitionForAllLocations(String prefix) {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setName("ART Register For All Locations (SLOW)_");
		rd.setupDataSetDefinition();

		CohortDefinition cd = ApzuReportElementsArt
				.artEverEnrolledOnDate(prefix);
		CohortIndicator i = h.newCountIndicator(prefix + ": Register For All Locations", cd
				.getName(), h.parameterMap(
				"startedOnOrBefore", "${endDate}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "breakdown", "Breakdown", i, null);

		return rd;
	}
}
