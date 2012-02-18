package org.openmrs.module.pihmalawi.reports.setup.outdated;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.pihmalawi.reports.experimental.ApzuPatientDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reports.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
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
import org.openmrs.serialization.SerializationException;

public class SetupPreArtWeekly {

	Helper h = new Helper();

	public SetupPreArtWeekly(Helper helper) {
		h = helper;
	}

	public void setup(boolean b) throws Exception {
		delete();

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions((PeriodIndicatorReportDefinition) h
				.findDefinition(PeriodIndicatorReportDefinition.class,
						"Pre-ART Weekly_"));
		h.createXlsOverview(rd, "Pre-ART_Weekly_Overview.xls",
				"Pre-ART Weekly Overview", excelOverviewProperties());
		ReportDesign rdes = createHtmlBreakdown(rd);
	}

	protected ReportDesign createHtmlBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(true);
		m.put("cd4lowloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("cd4medloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("cd4lowloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("cd4medloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("cd4lowloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("cd4medloc3", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(getPatientIdentifierType());
		dsd.setEncounterTypes(getEncounterTypes());

		return h.createHtmlBreakdown(rd, "Pre-ART Weekly Breakdown_", m);
	}

	private Collection<EncounterType> getEncounterTypes() {
		Collection<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(Context.getEncounterService().getEncounterType(
				"PART_INITIAL"));
		encounterTypes.add(Context.getEncounterService().getEncounterType(
				"PART_FOLLOWUP"));
		return encounterTypes;
	}

	private PatientIdentifierType getPatientIdentifierType() {
		return Context.getPatientService().getPatientIdentifierTypeByName(
				"PART Number");
	}

	protected Map excelOverviewProperties() {
		Map properties = new HashMap();
		properties.put("title", "Pre-ART Weekly - Upper Neno");
		properties.put("loc1name", "Neno");
		properties.put("loc2name", "Magaleta");
		properties.put("loc3name", "Nsambe");
		return properties;
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Pre-ART Weekly Breakdown_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("Pre-ART Weekly Overview".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(PeriodIndicatorReportDefinition.class,
				"Pre-ART Weekly_");
		h.purgeDefinition(DataSetDefinition.class, "Pre-ART Weekly_ Data Set");

		h.purgeAll("part: ");
	}

	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.addParameter(new Parameter("startDate", "Start date (Monday)",
				Date.class));
		rd.addParameter(new Parameter("endDate", "End date (Sunday)",
				Date.class));
		rd.setName("Pre-ART Weekly_");
		h.replaceReportDefinition(rd);
		return rd;
	}

	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		genericCohortDefinitions();

		// ever on pre-art on date at location
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("part: Ever on Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
				"1",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.setCompositionString("1");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "ever", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		// no paper record, simplified as i should check the obs in this
		// encounter
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("part: no paper record_");
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date markDate = dfm.parse("2011-04-07");
			ecd.setOnOrAfter(markDate);
			// ecd.setOnOrBefore(markDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ecd.setEncounterTypeList(Arrays.asList(h
				.encounterType("ADMINISTRATION")));
		h.replaceCohortDefinition(ecd);

		// on pre-art on date at location, excluding missing records/patients
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Pre-ART on date at location_");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"Pre-ART (Continue)"), "onDate", "${onDate}",
										"location", "${location}")));
		ccd.getSearches().put(
				"2",
				new Mapped(h.cohortDefinition("part: no paper record_"),
						ParameterizableUtil.createParameterMappings("")));
		ccd.setCompositionString("1 AND NOT 2");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "part", ccd,
				h.parameterMap("onDate", "${endDate}"));

		// on pre-art on date at location, including missing records/patients
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: All Pre-ART on date at location_");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"Pre-ART (Continue)"), "onDate", "${onDate}",
										"location", "${location}")));
		ccd.setCompositionString("1");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "all", ccd,
				h.parameterMap("onDate", "${endDate}"));

		// new on pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: New on Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
				"1",
				new Mapped(h
						.cohortDefinition("part: State change at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrAfter", "${startedOnOrAfter}",
								"startedOnOrBefore", "${startedOnOrBefore}",
								"location", "${location}")));
		ccd.setCompositionString("1");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "new", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		// died from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Died from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"Patient died"), "onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("part: Not having state at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"On antiretrovirals"),
										"startedOnOrBefore",
										"${startedOnOrBefore}",
										"startedOnOrAfter",
										"${startedOnOrAfter-100y}", "location",
										"${location}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "died", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		// HIV negativ from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: HIV negativ from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"PATIENT HIV NEGATIVE"), "onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("part: Not having state at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"On antiretrovirals"),
										"startedOnOrBefore",
										"${startedOnOrBefore}",
										"startedOnOrAfter",
										"${startedOnOrAfter-100y}", "location",
										"${location}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "neg", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		// transfered out from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Transferred out from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"Patient transferred out"), "onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("part: Not having state at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"On antiretrovirals"),
										"startedOnOrBefore",
										"${startedOnOrBefore}",
										"startedOnOrAfter",
										"${startedOnOrAfter-100y}", "location",
										"${location}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "trans", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		// internal transfer from pre-art on date at location
		/*
		 * ccd = new CompositionCohortDefinition();
		 * ccd.setName("part: Transferred internally from Pre-ART at location_"
		 * ); ccd.addParameter(new Parameter("startedOnOrAfter",
		 * "startedOnOrAfter", Date.class)); ccd.addParameter(new
		 * Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		 * ccd.addParameter(new Parameter("location", "location",
		 * Location.class)); ccd.getSearches().put( "1", new
		 * Mapped(h.cohortDefinition("part: In state on date at location_"),
		 * h.parameterMap("state", h.workflowState( "HIV program",
		 * "Treatment status", "Transferred internally"), "onDate",
		 * "${startedOnOrBefore}", "location", "${location}")));
		 * ccd.getSearches().put( "2", new
		 * Mapped(h.cohortDefinition("part: Not having state at location_"),
		 * h.parameterMap("state", h.workflowState( "HIV program",
		 * "Treatment status", "On antiretrovirals"), "startedOnOrBefore",
		 * "${startedOnOrBefore}", "startedOnOrAfter",
		 * "${startedOnOrAfter-100y}", "location", "${location}")));
		 * ccd.getSearches().put( "3", new
		 * Mapped(h.cohortDefinition("part: Having state at location_"),
		 * h.parameterMap("state", h.workflowState( "HIV program",
		 * "Treatment status", "Pre-ART (Continue)"), "startedOnOrBefore",
		 * "${startedOnOrBefore}", "startedOnOrAfter",
		 * "${startedOnOrAfter-100y}", "location", "${location}")));
		 * ccd.setCompositionString("1 AND 2 AND 3");
		 * h.replaceCohortDefinition(ccd); newCountIndicator(rd, "int", ccd,
		 * h.parameterMap("startedOnOrBefore", "${endDate}", "startedOnOrAfter",
		 * "${startDate}"));
		 */
		// treatment stopped from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Treatment stopped from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"Treatment stopped"), "onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("part: Not having state at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"On antiretrovirals"),
										"startedOnOrBefore",
										"${startedOnOrBefore}",
										"startedOnOrAfter",
										"${startedOnOrAfter-100y}", "location",
										"${location}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "stp", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		// moved to art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Ever On ART from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
				"2",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "On antiretrovirals"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.setCompositionString("2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "eart", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		// moved to art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: On ART from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								h.parameterMap("state", h.workflowState(
										"HIV program", "Treatment status",
										"On antiretrovirals"), "onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h
						.cohortDefinition("part: Having state at location_"), h
						.parameterMap("state", h.workflowState("HIV program",
								"Treatment status", "Pre-ART (Continue)"),
								"startedOnOrBefore", "${startedOnOrBefore}",
								"startedOnOrAfter", "${startedOnOrAfter-100y}",
								"location", "${location}")));
		ccd.setCompositionString("1 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "art", ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		cd4CohortDefs(rd);
	}

	private void cd4CohortDefs(PeriodIndicatorReportDefinition rd) {
		NumericObsCohortDefinition nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count available (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//		nocd.setQuestion(Context.getConceptService().getConceptByName(
//				"CD4 count"));
		 nocd.setQuestion(Context.getConceptService().getConceptByName(
		 "CLINICIAN REPORTED CD4"));
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("PART_INITIAL"), Context
				.getEncounterService().getEncounterType("PART_FOLLOWUP")));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		CohortIndicator i = h.newCountIndicator(nocd.getName(), nocd.getName(),
				h.parameterMap("onOrBefore", "${endDate}", "onOrAfter",
						"${endDate-6m}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "tcd4", nocd.getName(), i, null);
		cd4ForLocation(rd,
				"part: Pre-ART CD4 Count available (last 6 months)_",
				"part: Total CD4 Count available (last 6 months)_", "cd4");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count from Lab (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CD4 count"));
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("LAB")));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "labtcd4", nocd.getName(), i,
				null);
		cd4ForLocation(rd, "part: Pre-ART CD4 Count from Lab (last 6 months)_",
				"part: Total CD4 Count from Lab (last 6 months)_", "labcd4");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4% available (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CD4 PERCENT"));
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("PART_INITIAL"), Context
				.getEncounterService().getEncounterType("PART_FOLLOWUP")));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "tcd4p", nocd.getName(), i,
				null);
		cd4ForLocation(rd, "part: Pre-ART CD4% available (last 6 months)_",
				"part: Total CD4% available (last 6 months)_", "cd4p");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count >500 (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//		nocd.setQuestion(Context.getConceptService().getConceptByName(
//		"CD4 count"));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CLINICIAN REPORTED CD4"));
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("PART_INITIAL"), Context
				.getEncounterService().getEncounterType("PART_FOLLOWUP")));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setOperator1(RangeComparator.GREATER_THAN);
		nocd.setValue1(500.0);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "tcd4hig", nocd.getName(), i,
				null);
		cd4ForLocation(rd, "part: Pre-ART CD4 Count >500 (last 6 months)_",
				"part: Total CD4 Count >500 (last 6 months)_", "cd4hig");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count >500 from Lab (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CD4 count"));
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("LAB")));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setOperator1(RangeComparator.GREATER_THAN);
		nocd.setValue1(500.0);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "labtcd4hig", nocd.getName(),
				i, null);
		cd4ForLocation(rd,
				"part: Pre-ART CD4 Count >500 from Lab (last 6 months)_",
				"part: Total CD4 Count >500 from Lab (last 6 months)_",
				"labcd4hig");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count <250 (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//		nocd.setQuestion(Context.getConceptService().getConceptByName(
//		"CD4 count"));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CLINICIAN REPORTED CD4"));
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("PART_INITIAL"), Context
				.getEncounterService().getEncounterType("PART_FOLLOWUP")));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setOperator1(RangeComparator.LESS_THAN);
		nocd.setValue1(250.0);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "tcd4low", nocd.getName(), i,
				null);
		cd4ForLocation(rd, "part: Pre-ART CD4 Count <250 (last 6 months)_",
				"part: Total CD4 Count <250 (last 6 months)_", "cd4low");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count <250 from Lab (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CD4 count"));
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("LAB")));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setOperator1(RangeComparator.LESS_THAN);
		nocd.setValue1(250.0);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "labtcd4low", nocd.getName(),
				i, null);
		cd4ForLocation(rd,
				"part: Pre-ART CD4 Count <250 from Lab (last 6 months)_",
				"part: Total CD4 Count <250 from Lab (last 6 months)_",
				"labcd4low");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count >=250 and <=500 (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//		nocd.setQuestion(Context.getConceptService().getConceptByName(
//		"CD4 count"));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CLINICIAN REPORTED CD4"));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("PART_INITIAL"), Context
				.getEncounterService().getEncounterType("PART_FOLLOWUP")));
		nocd.setOperator1(RangeComparator.GREATER_EQUAL);
		nocd.setValue1(250.0);
		nocd.setOperator2(RangeComparator.LESS_EQUAL);
		nocd.setValue2(500.0);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "tcd4med", nocd.getName(), i,
				null);
		cd4ForLocation(rd,
				"part: Pre-ART CD4 Count >=250 and <=500 (last 6 months)_",
				"part: Total CD4 Count >=250 and <=500 (last 6 months)_",
				"cd4med");

		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: Total CD4 Count >=250 and <=500 from Lab (last 6 months)_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName(
				"CD4 count"));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("LAB")));
		nocd.setOperator1(RangeComparator.GREATER_EQUAL);
		nocd.setValue1(250.0);
		nocd.setOperator2(RangeComparator.LESS_EQUAL);
		nocd.setValue2(500.0);
		h.replaceCohortDefinition(nocd);
		i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap(
				"onOrBefore", "${endDate}", "onOrAfter", "${endDate-6m}"));
		PeriodIndicatorReportUtil.addColumn(rd, "labtcd4med", nocd.getName(),
				i, null);
		cd4ForLocation(
				rd,
				"part: Pre-ART CD4 Count >=250 and <=500 from Lab (last 6 months)_",
				"part: Total CD4 Count >=250 and <=500 from Lab (last 6 months)_",
				"labcd4med");
	}

	private void cd4ForLocation(PeriodIndicatorReportDefinition rd,
			String name, String cd4Query, String key) {
		CompositionCohortDefinition ccd;
		ccd = new CompositionCohortDefinition();
		ccd.setName(name);
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: Pre-ART on date at location_"),
								h.parameterMap("onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches().put(
				"2",
				new Mapped(h.cohortDefinition(cd4Query), h.parameterMap(
						"onOrBefore", "${startedOnOrBefore}", "onOrAfter",
						"${startedOnOrBefore-6m}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, key, ccd, h.parameterMap("startedOnOrBefore",
				"${endDate}", "startedOnOrAfter", "${startDate}"));

		ccd = new CompositionCohortDefinition();
		ccd.setName(name + "under 5");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: Pre-ART on date at location_"),
								h.parameterMap("onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches().put(
				"2",
				new Mapped(h.cohortDefinition(cd4Query), h.parameterMap(
						"onOrBefore", "${startedOnOrBefore}", "onOrAfter",
						"${startedOnOrBefore-6m}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h.cohortDefinition("part: Age_"), h.parameterMap(
						"effectiveDate", "${startedOnOrBefore}", "minAge", 0,
						"maxAge", 4)));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, key + "u5", ccd, h.parameterMap(
				"startedOnOrBefore", "${endDate}", "startedOnOrAfter",
				"${startDate}"));

		ccd = new CompositionCohortDefinition();
		ccd.setName(name + " 5 and above");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: Pre-ART on date at location_"),
								h.parameterMap("onDate",
										"${startedOnOrBefore}", "location",
										"${location}")));
		ccd.getSearches().put(
				"2",
				new Mapped(h.cohortDefinition(cd4Query), h.parameterMap(
						"onOrBefore", "${startedOnOrBefore}", "onOrAfter",
						"${startedOnOrBefore-6m}")));
		ccd.getSearches().put(
				"3",
				new Mapped(h.cohortDefinition("part: Age_"), h.parameterMap(
						"effectiveDate", "${startedOnOrBefore}", "minAge", 5,
						"maxAge", 999)));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, key + "5a", ccd, h.parameterMap(
				"startedOnOrBefore", "${endDate}", "startedOnOrAfter",
				"${startDate}"));
	}

	private void newCountIndicator(PeriodIndicatorReportDefinition rd,
			String indicatorKey, CohortDefinition nocd,
			Map<String, Object> parameterMap) {
		newCountIndicator(rd, indicatorKey, nocd, parameterMap, null);
	}

	private void newCountIndicator(PeriodIndicatorReportDefinition rd,
			String indicatorKey, CohortDefinition nocd,
			Map<String, Object> parameterMap,
			Map<String, String> dimensionOptions) {
		parameterMap.put("location", h.location("Neno District Hospital"));
		CohortIndicator i = h.newCountIndicator(nocd.getName() + " (Neno)",
				nocd.getName(), parameterMap);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc1",
				nocd.getName(), i, dimensionOptions);

		parameterMap.put("location", h.location("Nsambe HC"));
		i = h.newCountIndicator(nocd.getName() + " (Nsambe)", nocd.getName(),
				parameterMap);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc3",
				nocd.getName(), i, dimensionOptions);

		parameterMap.put("location", h.location("Magaleta HC"));
		i = h.newCountIndicator(nocd.getName() + " (Magaleta)", nocd.getName(),
				parameterMap);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc2",
				nocd.getName(), i, dimensionOptions);
	}

	private void genericCohortDefinitions() {
		// generic
		// In state on date at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("part: In state on date at location_");
		islcd.addParameter(new Parameter("state", "state",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);

		// In state at location
		islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("part: In state at location_");
		islcd.addParameter(new Parameter("state", "state",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		islcd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);

		// having state at location
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName("part: Having state at location_");
		pscd.addParameter(new Parameter("state", "state",
				ProgramWorkflowState.class));
		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);

		// not having state at location
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("part: Not having state at location_");
		ccd.addParameter(new Parameter("state", "state",
				ProgramWorkflowState.class));
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: Having state at location_"),
								ParameterizableUtil
										.createParameterMappings("state=${state},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd.setCompositionString("NOT 1");
		h.replaceCohortDefinition(ccd);

		// state change at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: State change at location_");
		ccd.addParameter(new Parameter("state", "state",
				ProgramWorkflowState.class));
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: Having state at location_"),
								ParameterizableUtil
										.createParameterMappings("state=${state},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("part: In state on date at location_"),
								ParameterizableUtil
										.createParameterMappings("state=${state},onDate=${startedOnOrAfter-1d},location=${location}")));
		ccd.setCompositionString("1 AND (NOT 2)");
		h.replaceCohortDefinition(ccd);

		// new in state at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: New in state at location_");
		ccd.addParameter(new Parameter("state", "state",
				ProgramWorkflowState.class));
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("part: Having state at location_"),
								ParameterizableUtil
										.createParameterMappings("state=${fromState},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("part: Having state at location_"),
								ParameterizableUtil
										.createParameterMappings("state=${toState},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);

		AgeCohortDefinition acd = new AgeCohortDefinition();
		acd.setName("part: Age_");
		acd.addParameter(new Parameter("minAge", "minAge", Integer.class));
		acd.addParameter(new Parameter("maxAge", "maxAge", Integer.class));
		acd.addParameter(new Parameter("effectiveDate", "effectiveDate",
				Date.class));
		h.replaceCohortDefinition(acd);
	}
}
