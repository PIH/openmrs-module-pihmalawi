package org.openmrs.module.pihmalawi.reports.setup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.pihmalawi.reports.extension.HasAgeOnStartedStateCohortDefinition;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.pihmalawi.reports.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;

public class SetupHccQuarterly {

	protected static final Log log = LogFactory
			.getLog(HibernatePihMalawiQueryDao.class);

	Helper h = new Helper();

	private final Program PROGRAM;

	private final ProgramWorkflowState STATE_ON_ART;
	private final ProgramWorkflowState STATE_PRE_ART;
	private final ProgramWorkflowState STATE_EXPOSED;

	private final EncounterType ART_INITIAL_ENCOUNTER;
	private final EncounterType ART_FOLLOWUP_ENCOUNTER;
	private final EncounterType PART_INITIAL_ENCOUNTER;
	private final EncounterType PART_FOLLOWUP_ENCOUNTER;
	private final EncounterType EXPOSED_CHILD_FOLLOWUP_ENCOUNTER;

	private CohortDefinition hccEnrolledInPeriod;
	private CohortDefinition hivDied;
	private CohortDefinition partEnrolledInPeriod;
	private CohortDefinition partEver;
	private CohortDefinition artEver;
	private CohortDefinition partDied;
	private CohortDefinition partTransferredOut;
	private CohortDefinition partOnArt;
	private CohortDefinition hccMissingAppointment;
	private CohortDefinition partActive;
	private CohortDefinition hivTransferredOut;
	private CohortDefinition exposedEnrolledInPeriod;
	private CohortDefinition exposedEver;

	/** little hack to have a start date. maybe we could live without it */
	private static final String MIN_DATE_PARAMETER = "${startDate-5000m}";

	public SetupHccQuarterly(Helper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		STATE_ON_ART = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals");
		STATE_PRE_ART = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("Pre-ART (Continue)");
		STATE_EXPOSED = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("Exposed Child (Continue)");
		ART_INITIAL_ENCOUNTER = Context.getEncounterService().getEncounterType(
				"ART_INITIAL");
		ART_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
				.getEncounterType("ART_FOLLOWUP");
		PART_INITIAL_ENCOUNTER = Context.getEncounterService()
				.getEncounterType("PART_INITIAL");
		PART_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
				.getEncounterType("PART_FOLLOWUP");
		EXPOSED_CHILD_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
				.getEncounterType("EXPOSED_CHILD_FOLLOWUP");
	}

	public void setup() throws Exception {
		delete();

		createCohortDefinitions();
		createDimensions();
		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createIndicators(rd);
		h.replaceReportDefinition(rd);
		h.createXlsOverview(rd, "Hcc_Quarterly.xls", "HCC Quarterly (Excel)_",
				null);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("HCC Quarterly (Excel)_".equals(rd.getName().toString())) {
				rs.purgeReportDesign(rd);
			}
		}

		h.purgeDimension("hccquarterly: Total registered by timeframe_");
		h.purgeDefinition(DataSetDefinition.class, "HCC Quarterly_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "HCC Quarterly_");
		h.purgeAll("hccquarterly: ");
	}

	private PeriodIndicatorReportDefinition createReportDefinition() {

		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setBaseCohortDefinition(hccEnrolledInPeriod, h.parameterMap(
				"startedOnOrAfter", "${startDate-100y}", "startedOnOrBefore",
				"${endDate}", "location", "${location}"));
		rd.setName("HCC Quarterly_");
		rd.setupDataSetDefinition();
		rd.addDimension(
				"registered",
				h.cohortDefinitionDimension("hccquarterly: Total registered by timeframe_"),
				ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		return rd;
	}

	private void createDimensions() {
		CohortDefinitionDimension totalRegisteredTimeframe = new CohortDefinitionDimension();
		totalRegisteredTimeframe
				.setName("hccquarterly: Total registered by timeframe_");
		totalRegisteredTimeframe.addParameter(new Parameter("startDate",
				"Start Date", Date.class));
		totalRegisteredTimeframe.addParameter(new Parameter("endDate",
				"End Date", Date.class));
		totalRegisteredTimeframe.addParameter(new Parameter("location",
				"Location", Location.class));
		totalRegisteredTimeframe.addCohortDefinition("quarter",
				hccEnrolledInPeriod, h.parameterMap("startedOnOrAfter",
						"${startDate}", "startedOnOrBefore", "${endDate}",
						"location", "${location}"));

		h.replaceDefinition(totalRegisteredTimeframe);
	}

	private void createCohortDefinitions() {
		List<EncounterType> artEncounterTypeList = new ArrayList<EncounterType>();
		artEncounterTypeList.add(ART_INITIAL_ENCOUNTER);
		artEncounterTypeList.add(ART_FOLLOWUP_ENCOUNTER);

		hivDied = ApzuReportElementsArt.hivDiedAtLocationOnDate("hccquarterly");
		hivTransferredOut = ApzuReportElementsArt
				.hivTransferredOutAtLocationOnDate("hccquarterly");
		partEnrolledInPeriod = ApzuReportElementsArt
				.partEnrolledAtLocationInPeriod("hccquarterly");
		partEver = ApzuReportElementsArt
				.partEverEnrolledAtLocationOnDate("hccquarterly");
		exposedEver = ApzuReportElementsArt
				.exposedEverEnrolledAtLocationOnDate("hccquarterly");
		exposedEnrolledInPeriod = ApzuReportElementsArt
				.exposedEnrolledAtLocationInPeriod("hccquarterly");
		hccEnrolledInPeriod = ApzuReportElementsArt
				.hccEnrolledAtLocationInPeriod("hccquarterly",
						partEnrolledInPeriod, exposedEnrolledInPeriod);
		artEver = ApzuReportElementsArt
				.artEverEnrolledAtLocationOnDate("hccquarterly");
		partDied = ApzuReportElementsArt.partDiedAtLocationOnDate(
				"hccquarterly", partEver, artEver, hivDied);
		partTransferredOut = ApzuReportElementsArt
				.partTransferredOutAtLocationOnDate("hccquarterly", partEver,
						artEver, hivTransferredOut);
		partOnArt = ApzuReportElementsArt.partOnArtAtLocationOnDate(
				"hccquarterly", partEver, artEver);
		hccMissingAppointment = ApzuReportElementsArt
				.hccMissedAppointmentAtLocationOnDate("hccquarterly");
		partActive = ApzuReportElementsArt
				.partActiveWithDefaultersAtLocationOnDate("hccquarterly");

		// in state at location (used for primary outcomes)
		// todo, exclude patients ever been in On ART at this location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("hccquarterly: In state at location_");
		islcd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
	}

	private void createIndicators(PeriodIndicatorReportDefinition rd) {

		i6_registered(rd);

		// i7_first_time_enrollment(rd);

		// i8_reenrolled(rd);

		// i9_transferred_in(rd);

		i10_males(rd);

		i11_i12_females(rd);

		i13_i14_i15_i16_age(rd);

		i17_i18_exposed_part(rd);

		i19_i20_21_i22_part_alive_startedArvs_defaulted_died_transferred(rd);
	}

	private void i17_i18_exposed_part(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"hccquarterly: Total exposed in quarter_", exposedEnrolledInPeriod, h
						.parameterMap("startedOnOrAfter", "${startDate}",
								"startedOnOrBefore", "${endDate}", "location",
								"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "17_quarter",
				"Total exposed in quarter", i, null);

		i = h.newCountIndicator("hccquarterly: Total exposed ever_",
				exposedEnrolledInPeriod, h.parameterMap("startedOnOrAfter",
						"${startDate-100y}", "startedOnOrBefore", "${endDate}",
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "17_ever",
				"Total exposed ever", i, null);

		i = h.newCountIndicator("hccquarterly: Total part in quarter_",
				partEnrolledInPeriod, h.parameterMap("startedOnOrAfter", "${startDate}",
						"startedOnOrBefore", "${endDate}", "location",
						"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "18_quarter",
				"Total Pre-ART in quarter", i, null);

		i = h.newCountIndicator("hccquarterly: Total part ever_", partEnrolledInPeriod, h
				.parameterMap("startedOnOrAfter", "${startDate-100y}",
						"startedOnOrBefore", "${endDate}", "location",
						"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "18_ever",
				"Total Pre-ART ever", i, null);
	}

	private void i6_registered(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"hccquarterly: Total registered in quarter_",
				hccEnrolledInPeriod, h.parameterMap("startedOnOrAfter",
						"${startDate}", "startedOnOrBefore", "${endDate}",
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "6_quarter",
				"Total registered in quarter", i, null);

		i = h.newCountIndicator("hccquarterly: Total registered ever_",
				hccEnrolledInPeriod, h.parameterMap("startedOnOrAfter",
						"${startDate-100y}", "startedOnOrBefore", "${endDate}",
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "6_ever",
				"Total registered ever", i, null);
	}

	private void i10_males(PeriodIndicatorReportDefinition rd) {

		GenderCohortDefinition malecd = new GenderCohortDefinition();
		malecd.setName("hccquarterly: males_");
		malecd.setMaleIncluded(true);
		h.replaceCohortDefinition(malecd);

		CohortIndicator i = h.newCountIndicator(
				"hccquarterly: males in HCC ever_", malecd);

		PeriodIndicatorReportUtil.addColumn(rd, "10_quarter",
				"Males (all ages)", i, h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "10_ever", "Males (all ages)",
				i, null);
	}

	private void i11_i12_females(PeriodIndicatorReportDefinition rd) {
		/*
		 * todo, pregnancy unclear // gender GenderCohortDefinition femalecd =
		 * new GenderCohortDefinition();
		 * femalecd.setName("hccquarterly: females_");
		 * femalecd.setFemaleIncluded(true);
		 * h.replaceCohortDefinition(femalecd);
		 * 
		 * // pregnancy CodedObsCohortDefinition pregnantcocd = new
		 * CodedObsCohortDefinition();
		 * pregnantcocd.setName("hccquarterly: pregnant_or_not_");
		 * pregnantcocd.addParameter(new Parameter("onOrAfter", "On Or After",
		 * Date.class)); pregnantcocd.addParameter(new Parameter("onOrBefore",
		 * "On Or Before", Date.class)); pregnantcocd.addParameter(new
		 * Parameter("locationList", "Location List", List.class));
		 * pregnantcocd.addParameter(new Parameter("valueList", "Value List",
		 * List.class));
		 * pregnantcocd.setQuestion(Context.getConceptService().getConcept(
		 * "PREGNANCY STATUS")); pregnantcocd.setTimeModifier(TimeModifier.ANY);
		 * pregnantcocd.setOperator(SetComparator.IN);
		 * pregnantcocd.setEncounterTypeList
		 * (Arrays.asList(PART_INITIAL_ENCOUNTER, PART_FOLLOWUP_ENCOUNTER));
		 * h.replaceCohortDefinition(pregnantcocd);
		 * 
		 * // pregnant within one month ObsAfterStateStartCohortDefinition
		 * pregnantoasscd = new ObsAfterStateStartCohortDefinition();
		 * pregnantoasscd
		 * .setName("hccquarterly: pregnant_within_month_or_not_");
		 * pregnantoasscd.addParameter(new Parameter("startedOnOrAfter",
		 * "Started On Or After", Date.class)); pregnantoasscd.addParameter(new
		 * Parameter("endedOnOrBefore", "Ended On Or Before", Date.class));
		 * pregnantoasscd.addParameter(new Parameter("locationList",
		 * "Location List", List.class)); pregnantoasscd.addParameter(new
		 * Parameter("valueList", "Value List", List.class));
		 * pregnantoasscd.setQuestion(Context.getConceptService().getConcept(
		 * "PREGNANCY STATUS")); pregnantoasscd.setState(STATE_ON_ART);
		 * pregnantoasscd.setTimeModifier(TimeModifier.ANY);
		 * pregnantoasscd.setOperator(SetComparator.IN);
		 * pregnantoasscd.setEncounterTypeList(Arrays.asList(
		 * PART_INITIAL_ENCOUNTER, PART_FOLLOWUP_ENCOUNTER));
		 * h.replaceCohortDefinition(pregnantoasscd);
		 * 
		 * // i11 non-pregnant Map<String, Mapped<? extends CohortDefinition>>
		 * baseCohortDefs = new LinkedHashMap<String, Mapped<? extends
		 * CohortDefinition>>(); baseCohortDefs.put("Females", new
		 * Mapped<CohortDefinition>(femalecd, null)); baseCohortDefs.put(
		 * "Pregnant", new Mapped<CohortDefinition>(pregnantcocd,
		 * h.parameterMap( "onOrAfter", "${startDate}", "onOrBefore",
		 * "${endDate}", "locationList", "${location}", "valueList",
		 * Arrays.asList(Context.getConceptService().getConcept( "Yes"))))); //
		 * using // AND // NOT // composition
		 * 
		 * CohortIndicator i = h.createCompositionIndicator(
		 * "hccquarterly: non-pregnant_females", "AND NOT", h
		 * .parameterMap("startDate", "${startDate}", "endDate", "${endDate}",
		 * "location", "${location}"), baseCohortDefs);
		 * 
		 * PeriodIndicatorReportUtil.addColumn(rd, "11_quarter",
		 * "Non-pregnant females (all ages)", i, h.hashMap("registered",
		 * "quarter")); PeriodIndicatorReportUtil.addColumn(rd, "11_ever",
		 * "Non-pregnant females (all ages)", i, null);
		 * 
		 * // i12 pregnant baseCohortDefs = new LinkedHashMap<String, Mapped<?
		 * extends CohortDefinition>>(); baseCohortDefs.put("Females", new
		 * Mapped<CohortDefinition>(femalecd, null)); baseCohortDefs.put(
		 * "Pregnant", new Mapped<CohortDefinition>(pregnantcocd,
		 * h.parameterMap( "onOrAfter", "${startDate}", "onOrBefore",
		 * "${endDate}", "locationList", "${location}", "valueList",
		 * Arrays.asList(Context.getConceptService().getConcept( "Yes")))));
		 * baseCohortDefs.put( "MonthPregnant", new
		 * Mapped<CohortDefinition>(pregnantoasscd, h.parameterMap(
		 * "startedOnOrAfter", "${startDate}", "endedOnOrBefore", "${endDate}",
		 * "locationList", "${location}", "valueList",
		 * Arrays.asList(Context.getConceptService().getConcept( "Yes")))));
		 * 
		 * i = h.createCompositionIndicator("hccquarterly: pregnant females",
		 * "Females AND (Pregnant OR MonthPregnant)", h.parameterMap(
		 * "startDate", "${startDate}", "endDate", "${endDate}", "location",
		 * "${location}"), baseCohortDefs, true);
		 * 
		 * PeriodIndicatorReportUtil.addColumn(rd, "12_quarter",
		 * "Pregnant females (all ages)", i, h.hashMap("registered",
		 * "quarter")); PeriodIndicatorReportUtil.addColumn(rd, "12_ever",
		 * "Pregnant females (all ages)", i, null);
		 */
	}

	private void i13_i14_i15_i16_age(PeriodIndicatorReportDefinition rd) {

		// age enrolled in HCC
		HasAgeOnStartedStateCohortDefinition ageAtPreARTEnrollment = new HasAgeOnStartedStateCohortDefinition();
		ageAtPreARTEnrollment
				.setName("hccquarterly: Age at Pre-ART enrollment_");
		ageAtPreARTEnrollment.setState(STATE_PRE_ART);
		ageAtPreARTEnrollment.addParameter(new Parameter("startedOnOrAfter",
				"Started On Or After", Date.class));
		ageAtPreARTEnrollment.addParameter(new Parameter("startedOnOrBefore",
				"Started On Or Before", Date.class));
		ageAtPreARTEnrollment.addParameter(new Parameter("location",
				"Location", Location.class));
		ageAtPreARTEnrollment.addParameter(new Parameter("minAge", "Min Age",
				Integer.class));
		ageAtPreARTEnrollment.addParameter(new Parameter("maxAge", "Max Age",
				Integer.class));
		ageAtPreARTEnrollment.addParameter(new Parameter("minAgeUnit",
				"Min Age Unit", DurationUnit.class));
		ageAtPreARTEnrollment.addParameter(new Parameter("maxAgeUnit",
				"max Age Unit", DurationUnit.class));
		h.replaceCohortDefinition(ageAtPreARTEnrollment);
		HasAgeOnStartedStateCohortDefinition ageAtExposedEnrollment = new HasAgeOnStartedStateCohortDefinition();
		ageAtExposedEnrollment
				.setName("hccquarterly: Age at Exposed enrollment_");
		ageAtExposedEnrollment.setState(STATE_EXPOSED);
		ageAtExposedEnrollment.addParameter(new Parameter("startedOnOrAfter",
				"Started On Or After", Date.class));
		ageAtExposedEnrollment.addParameter(new Parameter("startedOnOrBefore",
				"Started On Or Before", Date.class));
		ageAtExposedEnrollment.addParameter(new Parameter("location",
				"Location", Location.class));
		ageAtExposedEnrollment.addParameter(new Parameter("minAge", "Min Age",
				Integer.class));
		ageAtExposedEnrollment.addParameter(new Parameter("maxAge", "Max Age",
				Integer.class));
		ageAtExposedEnrollment.addParameter(new Parameter("minAgeUnit",
				"Min Age Unit", DurationUnit.class));
		ageAtExposedEnrollment.addParameter(new Parameter("maxAgeUnit",
				"max Age Unit", DurationUnit.class));
		h.replaceCohortDefinition(ageAtExposedEnrollment);
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hccquarterly: Age at HCC enrollment_");
		ccd.addParameter(new Parameter("startedOnOrAfter",
				"Started On Or After", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"Started On Or Before", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.addParameter(new Parameter("minAge", "Min Age", Integer.class));
		ccd.addParameter(new Parameter("maxAge", "Max Age", Integer.class));
		ccd.addParameter(new Parameter("minAgeUnit", "Min Age Unit",
				DurationUnit.class));
		ccd.addParameter(new Parameter("maxAgeUnit", "max Age Unit",
				DurationUnit.class));
		ccd.getSearches().put(
				"part",
				new Mapped(ageAtPreARTEnrollment, h.parameterMap(
						"startedOnOrBefore", "${startedOnOrBefore}",
						"startedOnOrAfter", "${startedOnOrAfter}", "location",
						"${location}", "minAge", "${minAge}", "maxAge",
						"${maxAge}", "minAgeUnit", "${minAgeUnit}",
						"maxAgeUnit", "${maxAgeUnit}")));
		ccd.getSearches().put(
				"exposed",
				new Mapped(ageAtExposedEnrollment, h.parameterMap(
						"startedOnOrBefore", "${startedOnOrBefore}",
						"startedOnOrAfter", "${startedOnOrAfter}", "location",
						"${location}", "minAge", "${minAge}", "maxAge",
						"${maxAge}", "minAgeUnit", "${minAgeUnit}",
						"maxAgeUnit", "${maxAgeUnit}")));
		ccd.setCompositionString("exposed OR part");
		h.replaceCohortDefinition(ccd);

		CohortIndicator i = h.newCountIndicator(
				"hccquarterly: Infants below 2 months at enrollment_", ccd, h
						.parameterMap("startedOnOrAfter", MIN_DATE_PARAMETER,
								"startedOnOrBefore", "${endDate}", "location",
								"${location}", "minAge", 0, "minAgeUnit",
								DurationUnit.MONTHS, "maxAge", 1, "maxAgeUnit",
								DurationUnit.MONTHS));
		PeriodIndicatorReportUtil.addColumn(rd, "13_quarter",
				"Infants below 2 months at enrollment", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "13_ever",
				"Infants below 2 months at enrollment", i, null);

		i = h.newCountIndicator(
				"hccquarterly: Children below 24 months at enrollment_", ccd, h
						.parameterMap("startedOnOrAfter", MIN_DATE_PARAMETER,
								"startedOnOrBefore", "${endDate}", "location",
								"${location}", "minAge", 2, "minAgeUnit",
								DurationUnit.MONTHS, "maxAge", 23,
								"maxAgeUnit", DurationUnit.MONTHS));
		PeriodIndicatorReportUtil.addColumn(rd, "14_quarter",
				"Children below 24 months at enrollment", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "14_ever",
				"Children below 24 months at enrollment", i, null);

		i = h.newCountIndicator("hccquarterly: Children at HCC enrollment_",
				ccd, h.parameterMap("startedOnOrAfter", MIN_DATE_PARAMETER,
						"startedOnOrBefore", "${endDate}", "location",
						"${location}", "minAge", 24, "minAgeUnit",
						DurationUnit.MONTHS, "maxAge", 14, "maxAgeUnit",
						DurationUnit.YEARS));
		PeriodIndicatorReportUtil.addColumn(rd, "15_quarter",
				"Children at HCC enrollment", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "15_ever",
				"Children at HCC enrollment", i, null);

		i = h.newCountIndicator("hccquarterly: Adults at HCC enrollment_", ccd,
				h.parameterMap("startedOnOrAfter", MIN_DATE_PARAMETER,
						"startedOnOrBefore", "${endDate}", "location",
						"${location}", "minAge", 15, "minAgeUnit",
						DurationUnit.YEARS, "maxAge", 2000, "maxAgeUnit",
						DurationUnit.YEARS)); // yes, 2000 will exclude jesus...
		PeriodIndicatorReportUtil.addColumn(rd, "16_quarter",
				"Adults at HCC enrollment", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "16_ever",
				"Adults at HCC enrollment", i, null);
	}

	private void i19_i20_21_i22_part_alive_startedArvs_defaulted_died_transferred(
			PeriodIndicatorReportDefinition rd) {

		// i19 part alive
		// in part and not missing appointment
		CompositionCohortDefinition partActiveWithoutDefaulters = new CompositionCohortDefinition();
		partActiveWithoutDefaulters.setName("hccquarterly: Pre-ART active_");
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
				new Mapped(hccMissingAppointment, h.parameterMap("onOrBefore",
						"${startedOnOrBefore}", "location", "${location}",
						"value1", "${startedOnOrBefore-8w}")));
		partActiveWithoutDefaulters
				.setCompositionString("part AND NOT defaulted");
		h.replaceCohortDefinition(partActiveWithoutDefaulters);
		CohortIndicator i = h.newCountIndicator("hccquarterly: Pre-ART active",
				partActiveWithoutDefaulters, h.parameterMap(
						"startedOnOrBefore", "${endDate}", "location",
						"${location}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "19", "Pre-ART active", i, null);

		// i20 part started art
		i = h.newCountIndicator("hccquarterly: Pre-ART Started ART_",
				partOnArt, h.parameterMap("startedOnOrBefore", "${endDate}",
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "20", "Pre-ART started ART", i,
				null);

		// i21 part transferred out
		i = h.newCountIndicator("hccquarterly: Pre-ART Transferred out_",
				partTransferredOut, h.parameterMap("startedOnOrBefore",
						"${endDate}", "location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "21",
				"Pre-ART transferred out", i, null);

		// i22 part defaulted
		// in part and missing appointment
		CompositionCohortDefinition partDefaulted = new CompositionCohortDefinition();
		partDefaulted.setName("hccquarterly: Pre-ART defaulted_");
		partDefaulted.addParameter(new Parameter("location", "location",
				Location.class));
		partDefaulted.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		partDefaulted.getSearches().put(
				"part",
				new Mapped(partActive, h.parameterMap("onDate",
						"${startedOnOrBefore}", "location", "${location}")));
		partDefaulted.getSearches().put(
				"defaulted",
				new Mapped(hccMissingAppointment, h.parameterMap("onOrBefore",
						"${startedOnOrBefore}", "location", "${location}",
						"value1", "${startedOnOrBefore-8w}")));
		partDefaulted.setCompositionString("part AND defaulted");
		h.replaceCohortDefinition(partDefaulted);
		i = h.newCountIndicator("hccquarterly: Pre-ART defaulted_",
				partDefaulted, h.parameterMap("startedOnOrBefore",
						"${endDate}", "location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "22", "Pre-ART defaulted", i,
				null);

		// i23 part died
		i = h.newCountIndicator("hccquarterly: Pre-ART Died_", partDied, h
				.parameterMap("startedOnOrBefore", "${endDate}", "location",
						"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "23", "Pre-ART died", i, null);

		// any other outcome
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hccquarterly: Pre-ART any other outcome_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"partEver",
				new Mapped(partEnrolledInPeriod, h.parameterMap("startedOnOrAfter",
						"${startedOnOrBefore-100y}", "startedOnOrBefore",
						"${startedOnOrBefore}", "location", "${location}")));
		ccd.getSearches().put(
				"part",
				new Mapped(partActiveWithoutDefaulters, h.parameterMap(
						"startedOnOrBefore", "${startedOnOrBefore}",
						"location", "${location}")));
		ccd.getSearches().put(
				"partOnArt",
				new Mapped(partOnArt, h.parameterMap("startedOnOrBefore",
						"${startedOnOrBefore}", "location", "${location}")));
		ccd.getSearches().put(
				"partTransferredOut",
				new Mapped(partTransferredOut, h.parameterMap(
						"startedOnOrBefore", "${startedOnOrBefore}",
						"location", "${location}")));
		ccd.getSearches().put(
				"partDefaulted",
				new Mapped(partDefaulted, h.parameterMap("startedOnOrBefore",
						"${startedOnOrBefore}", "location", "${location}")));
		ccd.setCompositionString("partEver AND NOT (part OR partOnArt OR partTransferredOut OR partDefaulted)");
		h.replaceCohortDefinition(ccd);
		i = h.newCountIndicator("hccquarterly: Pre-ART any other outcome_",
				ccd, h.parameterMap("startedOnOrBefore", "${endDate}",
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "23_check",
				"Pre-ART any other outcome", i, null);

	}

}