package org.openmrs.module.pihmalawi.reports.setup;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;

public class SetupApzuHivIndicators {

	protected Helper h = null;

	protected String reportName = null;

	protected String reportTag = null;

	protected List<Location> locations = null;

	public SetupApzuHivIndicators(Helper helper) {
		h = helper;
		configure("APZU HIV Indicators", "apzuhiv", Arrays.asList(
				Context.getLocationService().getLocation(
						"Neno District Hospital"),
				Context.getLocationService().getLocation("Magaleta HC"),
				Context.getLocationService().getLocation("Nsambe HC"),
				Context.getLocationService().getLocation("Neno Mission HC"),
				Context.getLocationService().getLocation(
						"Matandani Rural Health Center"),
				Context.getLocationService().getLocation("Ligowe HC"),
				Context.getLocationService().getLocation(
						"Lisungwi Community Hospital"), Context
						.getLocationService().getLocation("Chifunga HC"),
				Context.getLocationService().getLocation("Matope HC"), Context
						.getLocationService().getLocation("Zalewa HC"), Context
						.getLocationService().getLocation("Nkhula Falls RHC"),
				Context.getLocationService().getLocation("Luwani RHC")));
	}

	protected void configure(String reportName, String reportTag,
			List<Location> locations) {
		this.reportName = reportName;
		this.reportTag = reportTag;
		this.locations = locations;
	}

	public ReportDefinition[] setup() throws Exception {
		deleteReportElements();

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);

		h.replaceReportDefinition(rd);

		 h.createXlsOverview(rd, "APZU_HIV_Indicators.xls",
		 "APZU HIV Indicators (Excel)_", excelOverviewProperties());
		return (ReportDefinition[]) Arrays.asList(rd).toArray();
	}

	protected Map<? extends Object, ? extends Object> excelOverviewProperties() {
		Map properties = new HashMap();
		properties.put("title", "ART Missed Appointment - Upper Neno");
		properties.put("baseCohort", "On ART");
		properties.put("loc1name", "Neno");
		properties.put("loc2name", "Magaleta");
		properties.put("loc3name", "Nsambe");
		properties.put("loc4name", "Neno Mission");
		properties.put("loc5name", "Matandani");
		properties.put("loc6name", "Ligowe");
		properties.put("loc7name", "Lisungwi");
		properties.put("loc8name", "Chifunga");
		properties.put("loc9name", "Matope");
		properties.put("loc10name", "Zalewa");
		properties.put("loc11name", "Nkhula Falls");
		properties.put("loc12name", "Luwani");
		return properties;
	}

	protected PeriodIndicatorReportDefinition createReportDefinition()
			throws IOException {
		// art appointment report
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);

		rd.setName(reportName + "_");
		rd.setupDataSetDefinition();

		return rd;
	}

	protected void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {

		CohortDefinition partEver = ApzuReportElementsArt
				.partEverEnrolledAtLocationStartedOnOrBefore(reportTag);
		CohortDefinition artEver = ApzuReportElementsArt
				.artEverEnrolledAtLocationStartedOnOrBefore(reportTag);
		CohortDefinition partOnArt = ApzuReportElementsArt
				.partPeriodOnArtAtLocation(reportTag, partEver, artEver);
		CohortDefinition partActive = ApzuReportElementsArt
				.partActiveWithDefaultersAtLocationOnOrBefore(reportTag);
		CohortDefinition partMissingAppointment = ApzuReportElementsArt
				.partMissedAppointmentAtLocationOnOrBefore(reportTag);

		CompositionCohortDefinition partActiveWithoutDefaulters = new CompositionCohortDefinition();
		partActiveWithoutDefaulters.setName(reportTag + ": Pre-ART active_");
		partActiveWithoutDefaulters.addParameter(new Parameter("location",
				"location", Location.class));
		partActiveWithoutDefaulters.addParameter(new Parameter(
				"startedOnOrBefore", "startedOnOrBefore", Date.class));
		partActiveWithoutDefaulters.getSearches().put(
				"part",
				new Mapped(partActive, h.parameterMap("onDate",
						"${startedOnOrBefore}", "location", "${location}")));
		partActiveWithoutDefaulters.getSearches().put(
				"defaulted",
				new Mapped(partMissingAppointment, h.parameterMap("onOrBefore",
						"${startedOnOrBefore}", "location", "${location}",
						"value1", "${startedOnOrBefore-8w}")));
		partActiveWithoutDefaulters
				.setCompositionString("part AND NOT defaulted");
		h.replaceCohortDefinition(partActiveWithoutDefaulters);

		addColumnForLocations(rd, reportTag + ": Pre-ART enrolled", "3m",
				partEver, h.parameterMap("startedOnOrBefore", "${endDate}",
						"startedOnOrAfter", "${startDate}"));

		addColumnForLocations(rd, reportTag + ": Pre-ART active", "4m",
				partActiveWithoutDefaulters,
				h.parameterMap("startedOnOrBefore", "${endDate}"));

		addColumnForLocations(rd, reportTag + ": Pre-ART Started ART", "5m",
				partOnArt, h.parameterMap("startedOnOrAfter", "${startDate}",
						"startedOnOrBefore", "${endDate}"));

		CohortDefinition artPeriod = ApzuReportElementsArt
				.artPeriodEnrolledAtLocation(reportTag);
		CohortDefinition artMissingAppointment = ApzuReportElementsArt
				.artMissedAppointmentAtLocationOnOrBefore(reportTag);
		CohortDefinition onArt = ApzuReportElementsArt
				.onArtAtLocationOnDate(reportTag);

		CompositionCohortDefinition artMissing3Weeks = new CompositionCohortDefinition();
		artMissing3Weeks.setName(reportTag + ": ART missing 3 weeks_");
		artMissing3Weeks.addParameter(new Parameter("location", "location",
				Location.class));
		artMissing3Weeks.addParameter(new Parameter("onOrBefore", "onOrBefore",
				Date.class));
		artMissing3Weeks.getSearches().put(
				"art",
				new Mapped(onArt, h.parameterMap("onDate", "${onOrBefore}",
						"location", "${location}")));
		artMissing3Weeks.getSearches().put(
				"missing",
				new Mapped(artMissingAppointment, h.parameterMap("onOrBefore",
						"${onOrBefore}", "location", "${location}", "value1",
						"${onOrBefore-3w}")));
		artMissing3Weeks.setCompositionString("art AND missing");
		h.replaceCohortDefinition(artMissing3Weeks);

		CompositionCohortDefinition artMissing2Months = new CompositionCohortDefinition();
		artMissing2Months.setName(reportTag + ": ART missing 2 months_");
		artMissing2Months.addParameter(new Parameter("location", "location",
				Location.class));
		artMissing2Months.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		artMissing2Months.getSearches().put(
				"art",
				new Mapped(onArt, h.parameterMap("onDate", "${onOrBefore}",
						"location", "${location}")));
		artMissing2Months.getSearches().put(
				"missing",
				new Mapped(artMissingAppointment, h.parameterMap("onOrBefore",
						"${onOrBefore}", "location", "${location}", "value1",
						"${onOrBefore-2m}")));
		artMissing2Months.setCompositionString("art AND missing");
		h.replaceCohortDefinition(artMissing2Months);

		addColumnForLocations(rd, reportTag + ": ART enrolled", "6m",
				artPeriod, h.parameterMap("startedOnOrBefore", "${endDate}",
						"startedOnOrAfter", "${startDate}"));
		addColumnForLocations(rd, reportTag + ": ART overdue 3 weeks", "7am",
				artMissing3Weeks, h.parameterMap("onOrBefore", "${endDate}"));
		addColumnForLocations(rd, reportTag + ": ART overdue 2 months", "7bm",
				artMissing2Months, h.parameterMap("onOrBefore", "${endDate}"));

	}

	private void addColumnForLocations(PeriodIndicatorReportDefinition rd,
			String displayNamePrefix, String indicatorKey, CohortDefinition cd,
			Map<String, Object> parameterMap) {
		int count = 0;
		for (Location location : locations) {
			parameterMap.put("location", location);
			count++;
			CohortIndicator i = h.newCountIndicator(displayNamePrefix + " loc"
					+ count + "_", cd, parameterMap);
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc"
					+ count, displayNamePrefix + " (Location " + count + ")",
					i, null);
		}
	}

	public void deleteReportElements() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith(reportName)) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeAll(reportTag);
	}
}
