package org.openmrs.module.pihmalawi.reports.setup;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.dataset.HtmlBreakdownDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.renderer.HccRegisterBreakdownRenderer;
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

public class SetupPreArtRegister {

	ReportHelper h = new ReportHelper();

	public SetupPreArtRegister(ReportHelper helper) {
		h = helper;
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition("partreg");
		h.replaceReportDefinition(rd);
		createHtmlBreakdown(rd, "Pre-ART Register_");

		rd = createReportDefinitionForAllLocations("partregcomplete");
		h.replaceReportDefinition(rd);
		createHtmlBreakdown(rd, "Pre-ART Register For All Locations_");

		return new ReportDefinition[] { rd };
	}

	protected ReportDesign createHtmlBreakdown(ReportDefinition rd, String name)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		HtmlBreakdownDataSetDefinition dsd = new HtmlBreakdownDataSetDefinition();
		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("z_deprecated PART Number"));
		dsd.setHtmlBreakdownPatientRowClassname(HccRegisterBreakdownRenderer.class
				.getName());

		m.put("breakdown", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, name, m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("Pre-ART Register")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "Pre-ART Register (incl. old patients)_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "Pre-ART Register (incl. old patients)_");
		h.purgeDefinition(DataSetDefinition.class, "Pre-ART Register For All Locations (incl. old patients) (SLOW)_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "Pre-ART Register For All Locations (incl. old patients) (SLOW)_");
		h.purgeAll("partreg");
		h.purgeAll("partregcomplete");
	}

	private ReportDefinition createReportDefinition(String prefix) {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.setName("Pre-ART Register (incl. old patients)_");
		rd.setupDataSetDefinition();

		CohortDefinition partEver = ApzuReportElementsArt.partEverEnrolledIncludingOldPatientsAtLocationOnDate(prefix);

		CohortIndicator i = h.newCountIndicator(prefix + "Register_", partEver
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
		rd.setName("Pre-ART Register For All Locations (incl. old patients) (SLOW)_");
		rd.setupDataSetDefinition();

		CohortDefinition partEver = ApzuReportElementsArt.partEverEnrolledIncludingOldPatientsOnDate(prefix);

		CohortIndicator i = h.newCountIndicator(prefix + ": Register For All Locations", partEver
				.getName(), h.parameterMap(
				"startedOnOrBefore", "${endDate}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "breakdown", "Breakdown", i, null);

		return rd;
	}

}
