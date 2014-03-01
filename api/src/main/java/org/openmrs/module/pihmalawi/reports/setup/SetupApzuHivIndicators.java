package org.openmrs.module.pihmalawi.reports.setup;

import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupApzuHivIndicators {

	protected ReportHelper h = null;

	protected String reportName = null;

	protected String reportTag = null;

	protected List<Location> locations = null;

	HivMetadata hivMetadata = new HivMetadata();

	public SetupApzuHivIndicators(ReportHelper helper) {
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
				Context.getLocationService().getLocation("Luwani RHC"),
				Context.getLocationService().getLocation(
						"Lisungwi Community Hospital"), Context
						.getLocationService().getLocation("Chifunga HC"),
				Context.getLocationService().getLocation("Matope HC"), Context
						.getLocationService().getLocation("Zalewa HC"), Context
						.getLocationService().getLocation("Nkhula Falls RHC")));
	}

	protected void configure(String reportName, String reportTag,
			List<Location> locations) {
		this.reportName = reportName;
		this.reportTag = reportTag;
		this.locations = locations;
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);

		h.replaceReportDefinition(rd);

		h.createXlsOverview(rd, "org/openmrs/module/pihmalawi/reporting/reports/ApzuHivIndicatorsReport.xls",
				"APZU HIV Indicators (Excel)_", excelOverviewProperties());
		return (ReportDefinition[]) Arrays.asList(rd).toArray();
	}

	protected Map<? extends Object, ? extends Object> excelOverviewProperties() {
		Map properties = new HashMap();
		properties.put("title", "APZU HIV Indicators");
		properties.put("baseCohort", "On ART");
		properties.put("loc1name", "Neno");
		properties.put("loc2name", "Magaleta");
		properties.put("loc3name", "Nsambe");
		properties.put("loc4name", "Neno Parish");
		properties.put("loc5name", "Matandani");
		properties.put("loc6name", "Ligowe");
		properties.put("loc7name", "Luwani");
		properties.put("loc8name", "Lisungwi");
		properties.put("loc9name", "Chifunga");
		properties.put("loc10name", "Matope");
		properties.put("loc11name", "Zalewa");
		properties.put("loc12name", "Nkhula Falls");
		return properties;
	}

	protected PeriodIndicatorReportDefinition createReportDefinition()
			throws IOException {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setName(reportName + "_");
		rd.setupDataSetDefinition();
		return rd;
	}

	protected void createCohortDefinitions(
			PeriodIndicatorReportDefinition rd) {

		CohortDefinition partPeriod = ApzuReportElementsArt
				.partEnrolledAtLocationInPeriod(reportTag);
		CohortDefinition partEver = ApzuReportElementsArt
				.partEverEnrolledAtLocationOnDate(reportTag);
		CohortDefinition artEver = ApzuReportElementsArt
				.artEverEnrolledAtLocationOnDate(reportTag);
		CohortDefinition partActive = ApzuReportElementsArt
				.partActiveWithDefaultersAtLocationOnDate(reportTag);
		CohortDefinition partMissingAppointment = ApzuReportElementsArt
				.partMissedAppointmentAtLocationOnDate(reportTag);
		CohortDefinition artActive = ApzuReportElementsArt
				.artActiveWithDefaultersAtLocationOnDate(reportTag);
		CohortDefinition hivDied = ApzuReportElementsArt
				.hivDiedAtLocationOnDate(reportTag);
		CohortDefinition hivDefaultedOnDate = ApzuReportElementsArt
				.hivDefaultedAtLocationOnDate(reportTag);
		CohortDefinition hivDefaultedDuringPeriod = ApzuReportElementsArt
				.hivDefaultedAtLocationDuringPeriod(reportTag);
		CohortDefinition artPeriod = ApzuReportElementsArt
				.artEnrolledAtLocationInPeriod(reportTag);
		CohortDefinition artMissingAppointment = ApzuReportElementsArt
				.artMissedAppointmentAtLocationOnDate(reportTag);
		CohortDefinition onArt = ApzuReportElementsArt
				.artActiveAtLocationOnDate(reportTag);
		CohortDefinition partOnArt = ApzuReportElementsArt
				.partOnArtAtLocationInPeriod(reportTag, partEver, artPeriod);

		// monthly
		addColumnForLocations(rd, reportTag + ": Pre-ART enrolled", "3m",
				partPeriod, h.parameterMap("startedOnOrBefore", "${endDate}",
						"startedOnOrAfter", "${startDate}"));

		addColumnForLocations(rd, reportTag + ": Pre-ART active", "4m",
				partActive, h.parameterMap("onDate", "${endDate}"));

		addColumnForLocations(rd, reportTag + ": Pre-ART Started ART", "5m",
				partOnArt, h.parameterMap("startedOnOrAfter", "${startDate}",
						"startedOnOrBefore", "${endDate}"));

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

		// 7c: Add a line for patients who have been on ART but were confirmed defaulted during the period, and who are still defaulted at the end of the period
		CompositionCohortDefinition artDefaultedDuringPeriod = new CompositionCohortDefinition();
		artDefaultedDuringPeriod.setName(reportTag + ": ART defaulted_");
		artDefaultedDuringPeriod.addParameter(new Parameter("location", "location", Location.class));
		artDefaultedDuringPeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		artDefaultedDuringPeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		artDefaultedDuringPeriod.getSearches().put("artEver", new Mapped(artEver, h.parameterMap("startedOnOrBefore", "${onOrBefore}", "location", "${location}")));
		artDefaultedDuringPeriod.getSearches().put("defaultedDuringPeriod",new Mapped(hivDefaultedDuringPeriod, h.parameterMap("startedOnOrAfter", "${onOrAfter}", "startedOnOrBefore", "${onOrBefore}", "location", "${location}")));
		artDefaultedDuringPeriod.getSearches().put("defaultedAtEnd",new Mapped(hivDefaultedOnDate, h.parameterMap("onDate", "${onOrBefore}", "location", "${location}")));
		artDefaultedDuringPeriod.setCompositionString("artEver AND defaultedDuringPeriod AND defaultedAtEnd");
		h.replaceCohortDefinition(artDefaultedDuringPeriod);

		addColumnForLocations(rd, reportTag + ": Defaulted", "7cm", artDefaultedDuringPeriod, h.parameterMap("onOrAfter", "${startDate}", "onOrBefore", "${endDate}"));

		// quaterly

		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName(reportTag + ": Pre-ART visits_");
		ecd.setEncounterTypeList(hivMetadata.getPreArtEncounterTypes());
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		h.replaceCohortDefinition(ecd);
		CompositionCohortDefinition partActiveWithVisit = new CompositionCohortDefinition();
		partActiveWithVisit.setName(reportTag + ": Pre-ART visit_");
		partActiveWithVisit.addParameter(new Parameter("location", "location",
				Location.class));
		partActiveWithVisit.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		partActiveWithVisit.getSearches().put(
				"part",
				new Mapped(partActive, h.parameterMap("onDate",
						"${startedOnOrBefore}", "location", "${location}")));
		partActiveWithVisit.getSearches().put(
				"visit",
				new Mapped(ecd, h.parameterMap("onOrBefore",
						"${startedOnOrBefore}",
						"onOrAfter", "${startedOnOrBefore-6m}")));
		partActiveWithVisit.setCompositionString("part AND visit");
		h.replaceCohortDefinition(partActiveWithVisit);
		addColumnForLocations(rd, reportTag + ": Pre-ART with visit", "3q",
				partActiveWithVisit,
				h.parameterMap("startedOnOrBefore", "${endDate}"));

		NumericObsCohortDefinition nocd = new NumericObsCohortDefinition();
		nocd.setName(reportTag + ": Total CD4 Count recorded_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"Clinician reported to CD4"));
		nocd.setEncounterTypeList(Arrays.asList(hivMetadata.getPreArtFollowupEncounterType()));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(reportTag + ": Active Pre-ART CD4 count_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partActive, h.parameterMap("onDate", "${endDate}",
						"location", "${location}")));
		ccd.getSearches()
				.put("cd4",
						new Mapped(
								nocd,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-6m}")));
		ccd.setCompositionString("part AND cd4");
		h.replaceCohortDefinition(ccd);
		addColumnForLocations(rd, reportTag + ": Pre-ART with CD4", "4q", ccd,
				h.parameterMap("endDate", "${endDate}"));

		addColumnForLocations(rd, reportTag + ": ART ever", "5q", artEver,
				h.parameterMap("startedOnOrBefore", "${endDate}"));

		ecd = new EncounterCohortDefinition();
		ecd.setName(reportTag + ": ART visits_");
		ecd.setEncounterTypeList(hivMetadata.getArtEncounterTypes());
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		h.replaceCohortDefinition(ecd);
		CompositionCohortDefinition artActiveWithVisit = new CompositionCohortDefinition();
		artActiveWithVisit.setName(reportTag + ": ART visit_");
		artActiveWithVisit.addParameter(new Parameter("location", "location",
				Location.class));
		artActiveWithVisit.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		artActiveWithVisit.getSearches().put(
				"art",
				new Mapped(artActive, h.parameterMap("onDate",
						"${startedOnOrBefore}", "location", "${location}")));
		artActiveWithVisit.getSearches().put(
				"visit",
				new Mapped(ecd, h.parameterMap("onOrBefore",
						"${startedOnOrBefore}", 
						"onOrAfter", "${startedOnOrBefore-3m}")));
		artActiveWithVisit.setCompositionString("art AND visit");
		h.replaceCohortDefinition(artActiveWithVisit);
		addColumnForLocations(rd, reportTag + ": ART with visit", "6q",
				artActiveWithVisit,
				h.parameterMap("startedOnOrBefore", "${endDate}"));

		CompositionCohortDefinition died = new CompositionCohortDefinition();
		died.setName(reportTag + ": Died_");
		died.addParameter(new Parameter("location", "location", Location.class));
		died.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		died.getSearches().put(
				"died3monthsago",
				new Mapped(hivDied, h.parameterMap("onDate",
						"${startedOnOrBefore-3m}", "location", "${location}")));
		died.getSearches().put(
				"diednow",
				new Mapped(hivDied, h.parameterMap("onDate",
						"${startedOnOrBefore}", "location", "${location}")));
		died.getSearches().put(
				"artever",
				new Mapped(artEver, h.parameterMap("startedOnOrBefore",
						"${startedOnOrBefore}", "location", "${location}")));
		died.setCompositionString("artever AND diednow AND NOT died3monthsago");
		h.replaceCohortDefinition(died);
		addColumnForLocations(rd, reportTag + ": died", "7q", died,
				h.parameterMap("startedOnOrBefore", "${endDate}"));

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

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith(reportName)) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "APZU HIV Indicators_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "APZU HIV Indicators_");
		h.purgeAll(reportTag);
	}
}
