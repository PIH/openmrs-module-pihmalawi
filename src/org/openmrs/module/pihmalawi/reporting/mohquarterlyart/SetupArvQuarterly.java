package org.openmrs.module.pihmalawi.reporting.mohquarterlyart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.HasAgeOnStartedStateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAfterStartedStateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.ObsAfterStateStartCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.ReinitiatedCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.StateRelativeToStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
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

public class SetupArvQuarterly {
	
	protected static final Log log = LogFactory
	.getLog(HibernatePihMalawiQueryDao.class);

	private final Concept CONCEPT_APPOINTMENT_DATE;
	
	private final Concept PULMONARY_TUBERCULOSIS;
	
	private final Concept EXTRA_PULMONARY_TUBERCULOSIS;
	
	private final Concept PTB_WITHIN_THE_PAST_TWO_YEARS;

	private final Program PROGRAM;

	Helper h = new Helper();

	private final ProgramWorkflowState STATE_DIED;

	private final ProgramWorkflowState STATE_ON_ART;

	private final ProgramWorkflowState STATE_STOPPED;

	private final ProgramWorkflowState STATE_TRANSFERRED_OUT;
	
	private final EncounterType ART_INITIAL_ENCOUNTER;
	
	private final EncounterType ART_FOLLOWUP_ENCOUNTER;
	
	private final EncounterType HIV_STAGING_ENCOUNTER;
	

	/** little hack to have a start date. maybe we could live without it */
	private static final String MIN_DATE_PARAMETER = "${startDate-5000m}";

	public SetupArvQuarterly(Helper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV PROGRAM");
		STATE_DIED = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("PATIENT DIED");
		STATE_ON_ART = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("ON ANTIRETROVIRALS");
		STATE_STOPPED = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("TREATMENT STOPPED");
		STATE_TRANSFERRED_OUT = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("PATIENT TRANSFERRED OUT");
		CONCEPT_APPOINTMENT_DATE = Context.getConceptService()
				.getConceptByName("APPOINTMENT DATE");
		ART_INITIAL_ENCOUNTER = Context.getEncounterService().getEncounterType("ART_INITIAL");
		ART_FOLLOWUP_ENCOUNTER = Context.getEncounterService().getEncounterType("ART_FOLLOWUP");
		HIV_STAGING_ENCOUNTER = Context.getEncounterService().getEncounterType("HIV STAGING");
		
		PULMONARY_TUBERCULOSIS = Context.getConceptService().getConcept("PULMONARY TUBERCULOSIS");
		EXTRA_PULMONARY_TUBERCULOSIS = Context.getConceptService().getConcept("EXTRAPULMONARY TUBERCULOSIS (EPTB)");
		PTB_WITHIN_THE_PAST_TWO_YEARS = Context.getConceptService().getConcept("PTB WITHIN THE PAST TWO YEARS"); // PULMONARY TUBERCULOSIS WITHIN THE LAST 2 YEARS
	}

	public void setup() throws Exception {
		delete();

		createCohortDefinitions();
		createDimensions();
		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createIndicators(rd);
		h.replaceReportDefinition(rd);
		h.createXlsOverview(rd, "Arv_Quarterly.xls", "ARV QUARTERLY (Excel)_", null);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("ARV QUARTERLY (Excel)_".equals(rd.getName().toString()) ){
				rs.purgeReportDesign(rd);
			}
		}
		
		h.purgeDefinition(DataSetDefinition.class, "ARV Quarterly_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "ARV Quarterly_");
		h.purgeAll("arvquarterly: ");
	}

	private PeriodIndicatorReportDefinition createReportDefinition() {
		
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setBaseCohortDefinition(h.cohortDefinition("arvquarterly: On ART at location_"), h.parameterMap("startedOnOrAfter",
				MIN_DATE_PARAMETER, "startedOnOrBefore", "${endDate}",
				"location", "${location}"));
		rd.setName("ARV Quarterly_");
		rd.setupDataSetDefinition();
		rd.addDimension("registered", h.cohortDefinitionDimension("arvquarterly: Total registered by timeframe_"), ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		return rd;
	}
	
	private void createDimensions() {
		CohortDefinitionDimension totalRegisteredTimeframe = new CohortDefinitionDimension();
		totalRegisteredTimeframe.setName("arvquarterly: Total registered by timeframe_");
		totalRegisteredTimeframe.addParameter(new Parameter("startDate", "Start Date", Date.class));
		totalRegisteredTimeframe.addParameter(new Parameter("endDate", "End Date", Date.class));
		totalRegisteredTimeframe.addParameter(new Parameter("location", "Location", Location.class));
		totalRegisteredTimeframe.addCohortDefinition("quarter", h.cohortDefinition("arvquarterly: On ART at location_"), h.parameterMap(
				"startedOnOrAfter", "${startDate}",
				"startedOnOrBefore", "${endDate}", 
				"location", "${location}"));
		
		h.replaceDefinition(totalRegisteredTimeframe);
	}

	private void createCohortDefinitions() {
		List<EncounterType> artEncounterTypeList = new ArrayList<EncounterType>();
		artEncounterTypeList.add(ART_INITIAL_ENCOUNTER);
		artEncounterTypeList.add(ART_FOLLOWUP_ENCOUNTER);
		
		// on ART at location (used for base cohort, dimension, and registered numbers)
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName("arvquarterly: On ART at location_");
		pscd.setState(STATE_ON_ART);
		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		
		ReinitiatedCohortDefinition rcd = new ReinitiatedCohortDefinition();
		rcd.setName("arvquarterly: reinitiated_");
		rcd.setEncounterTypeList(artEncounterTypeList);
		rcd.addParameter(new Parameter("onOrAfter", "startDate", Date.class));
		rcd.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		rcd.addParameter(new Parameter("locationList", "Location List", List.class));
		h.replaceCohortDefinition(rcd);
		
		// generic coded obs (used for transferred in)
        CodedObsCohortDefinition genericcocd = new CodedObsCohortDefinition();
        genericcocd.setName("arvquarterly: generic_coded_obs_");
        genericcocd.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        genericcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        genericcocd.addParameter(new Parameter("question", "Question", Concept.class));
        genericcocd.addParameter(new Parameter("valueList", "Value List", List.class));
        genericcocd.addParameter(new Parameter("locationList", "Location List", List.class));
        genericcocd.addParameter(new Parameter("encounterTypeList", "Encounter Type List", List.class));
        genericcocd.setTimeModifier(TimeModifier.LAST);
        genericcocd.setOperator(SetComparator.IN);
		h.replaceCohortDefinition(genericcocd);
		
		// gender
		GenderCohortDefinition malecd = new GenderCohortDefinition();
		malecd.setName("arvquarterly: males_");
		malecd.setMaleIncluded(true);
		h.replaceCohortDefinition(malecd);
		
		GenderCohortDefinition femalecd = new GenderCohortDefinition();
        femalecd.setName("arvquarterly: females_");
        femalecd.setFemaleIncluded(true);
        h.replaceCohortDefinition(femalecd);
        
        // pregnancy
        CodedObsCohortDefinition pregnantcocd = new CodedObsCohortDefinition();
        pregnantcocd.setName("arvquarterly: pregnant_or_not_");
        pregnantcocd.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        pregnantcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        pregnantcocd.addParameter(new Parameter("locationList", "Location List", List.class));
        pregnantcocd.addParameter(new Parameter("valueList", "Value List", List.class));
        pregnantcocd.setQuestion(Context.getConceptService().getConcept(
				"PREGNANCY STATUS"));
        pregnantcocd.setTimeModifier(TimeModifier.ANY);
        pregnantcocd.setOperator(SetComparator.IN);
        pregnantcocd.setEncounterTypeList(artEncounterTypeList);
		h.replaceCohortDefinition(pregnantcocd);
		
		// pregnant within one month
		ObsAfterStateStartCohortDefinition pregnantoasscd = new ObsAfterStateStartCohortDefinition();
		pregnantoasscd.setName("arvquarterly: pregnant_within_month_or_not_");
		pregnantoasscd.addParameter(new Parameter("startedOnOrAfter", "Started On Or After", Date.class));
		pregnantoasscd.addParameter(new Parameter("endedOnOrBefore", "Ended On Or Before", Date.class));
		pregnantoasscd.addParameter(new Parameter("locationList", "Location List", List.class));
		pregnantoasscd.addParameter(new Parameter("valueList", "Value List", List.class));
		pregnantoasscd.setQuestion(Context.getConceptService().getConcept("PREGNANCY STATUS"));
		pregnantoasscd.setState(STATE_ON_ART);
		pregnantoasscd.setTimeModifier(TimeModifier.ANY);
		pregnantoasscd.setOperator(SetComparator.IN);
		pregnantoasscd.setEncounterTypeList(artEncounterTypeList);
		h.replaceCohortDefinition(pregnantoasscd);
		
		// age started ART
		HasAgeOnStartedStateCohortDefinition ageAtARTInitiation = new HasAgeOnStartedStateCohortDefinition();
		ageAtARTInitiation.setName("arvquarterly: Age at ART initiation_");
		ageAtARTInitiation.setState(STATE_ON_ART);
		ageAtARTInitiation.addParameter(new Parameter("startedOnOrAfter", "Started On Or After", Date.class));
		ageAtARTInitiation.addParameter(new Parameter("startedOnOrBefore", "Started On Or Before", Date.class));
		ageAtARTInitiation.addParameter(new Parameter("location", "Location", 
				Location.class));
		ageAtARTInitiation.addParameter(new Parameter("minAge", "Min Age", Integer.class));
		ageAtARTInitiation.addParameter(new Parameter("maxAge", "Max Age", Integer.class));
		ageAtARTInitiation.addParameter(new Parameter("minAgeUnit", "Min Age Unit", DurationUnit.class));
		ageAtARTInitiation.addParameter(new Parameter("maxAgeUnit", "max Age Unit", DurationUnit.class));
		h.replaceCohortDefinition(ageAtARTInitiation);
		
		// reason for starting ARV
        CodedObsCohortDefinition reasonARTcocd = new CodedObsCohortDefinition();
        reasonARTcocd.setName("arvquarterly: reason_started_ART_");
        reasonARTcocd.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        reasonARTcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        reasonARTcocd.addParameter(new Parameter("locationList", "Location List", List.class));
        reasonARTcocd.addParameter(new Parameter("valueList", "value list", List.class));
        reasonARTcocd.setQuestion(Context.getConceptService().getConcept(
				"REASON FOR ART ELIGIBILITY"));
        reasonARTcocd.setTimeModifier(TimeModifier.LAST);
        reasonARTcocd.setOperator(SetComparator.IN);
        reasonARTcocd.setEncounterTypeList(Arrays.asList(ART_INITIAL_ENCOUNTER));
		h.replaceCohortDefinition(reasonARTcocd);
		
		// stage conditions at start of ART obs
        CodedObsCohortDefinition stage_conditions_start_ARTcocd = new CodedObsCohortDefinition();
        stage_conditions_start_ARTcocd.setName("arvquarterly: stage_conditions_at_start_ART_coded_obs_");
        stage_conditions_start_ARTcocd.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        stage_conditions_start_ARTcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        stage_conditions_start_ARTcocd.addParameter(new Parameter("locationList", "Location List", List.class));
        stage_conditions_start_ARTcocd.addParameter(new Parameter("valueList", "Value List", List.class));
        stage_conditions_start_ARTcocd.setQuestion(Context.getConceptService().getConcept("WHO STAGES CRITERIA PRESENT"));
        stage_conditions_start_ARTcocd.setTimeModifier(TimeModifier.ANY);
        stage_conditions_start_ARTcocd.setOperator(SetComparator.IN);
		stage_conditions_start_ARTcocd.setEncounterTypeList(Arrays.asList(HIV_STAGING_ENCOUNTER));
		h.replaceCohortDefinition(stage_conditions_start_ARTcocd);
		
		InverseCohortDefinition i_stage_conditions_cocd = new InverseCohortDefinition();
		i_stage_conditions_cocd.setBaseDefinition(stage_conditions_start_ARTcocd);
		i_stage_conditions_cocd.setName("arvquarterly: inverse_stage_conditions_at_start_ART_coded_obs_");
		i_stage_conditions_cocd.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
		i_stage_conditions_cocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
		i_stage_conditions_cocd.addParameter(new Parameter("locationList", "Location List", List.class));
		i_stage_conditions_cocd.addParameter(new Parameter("valueList", "Value List", List.class));
		h.replaceCohortDefinition(i_stage_conditions_cocd);
		
		// in state at location (used for primary outcomes)
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("arvquarterly: In state at location_");
		islcd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		// Defaulters (used for primary outcomes)
		// todo: only take appointment dates from art clinic. not sure how to
		// get them though
		// todo: defaulted calculated from next app date, but should be taken
		// from last visit and expected date running out of arvs. for now we
		// assume this is the same
		DateObsCohortDefinition dod = new DateObsCohortDefinition();
		dod.setName("arvquarterly: Missed Appointment_");
		dod.setTimeModifier(TimeModifier.MAX);
		dod.setQuestion(CONCEPT_APPOINTMENT_DATE);
		dod.setOperator1(RangeComparator.LESS_THAN);
		dod.setOperator2(RangeComparator.GREATER_EQUAL);
		dod.setEncounterTypeList(artEncounterTypeList);
		dod.addParameter(new Parameter("onOrAfter", "startDate", Date.class));
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		dod.addParameter(new Parameter("locationList", "Location List", List.class));
		dod.addParameter(new Parameter("value1", "to", Date.class));
		dod.addParameter(new Parameter("value2", "from", Date.class));
		h.replaceCohortDefinition(dod);
		
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("arvquarterly: Had Appointment_");
		ecd.setEncounterTypeList(artEncounterTypeList);
		ecd.addParameter(new Parameter("onOrAfter", "startDate", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		ecd.addParameter(new Parameter("locationList", "Location List", List.class));
		h.replaceCohortDefinition(ecd);
		
		// in state after started state (used for primary outcomes --died)
		InStateAfterStartedStateCohortDefinition diedAfterARTInitiation = new InStateAfterStartedStateCohortDefinition();
		diedAfterARTInitiation.setName("arvquarterly: ART started at enrollment location relative to started DIED at enrollment location on date_");
		diedAfterARTInitiation.setPrimaryState(STATE_DIED);
		diedAfterARTInitiation.setRelativeState(STATE_ON_ART);
		diedAfterARTInitiation.addParameter(new Parameter("onDate", "On Date", Date.class));
		diedAfterARTInitiation.addParameter(new Parameter("primaryStateLocation", "Primary State Location", 
				Location.class));
		diedAfterARTInitiation.addParameter(new Parameter("relativeStateLocation", "Relative State Location", 
				Location.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetAmount", "Offset Amount", Date.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetDuration", "Offset Duration", Integer.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetWithin", "Offset Within", Boolean.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetUnit", "Offset Unit", Date.class));
		h.replaceCohortDefinition(diedAfterARTInitiation);
		
		// (used for side effects) -- use Double(1) and Double(0) for true/false
        NumericObsCohortDefinition genericncd = new NumericObsCohortDefinition();
        genericncd.setName("arvquarterly: generic_numeric_obs_");
        genericncd.addParameter(new Parameter("onDate", "On Date", Date.class));
        genericncd.addParameter(new Parameter("locationList", "Location List", List.class));
        genericncd.addParameter(new Parameter("question", "Question", Concept.class));
        genericncd.addParameter(new Parameter("value1", "Value 1", List.class));
        genericncd.setTimeModifier(TimeModifier.LAST);
        genericncd.setOperator1(RangeComparator.EQUAL);
        genericncd.setEncounterTypeList(artEncounterTypeList);
		h.replaceCohortDefinition(genericncd);        
		
		// current tb status
        CodedObsCohortDefinition current_tb_status = new CodedObsCohortDefinition();
        current_tb_status.setName("arvquarterly: current_tb_status_");
        current_tb_status.addParameter(new Parameter("onDate", "On Date", Date.class));
        current_tb_status.addParameter(new Parameter("locationList", "Location List", List.class));
        current_tb_status.addParameter(new Parameter("valueList", "value list", List.class));
        current_tb_status.setQuestion(Context.getConceptService().getConcept(
				"TB_STATUS"));
        current_tb_status.setTimeModifier(TimeModifier.LAST);
        current_tb_status.setOperator(SetComparator.IN);
        current_tb_status.setEncounterTypeList(artEncounterTypeList);
		h.replaceCohortDefinition(current_tb_status);
	}
	
	private void createIndicators(PeriodIndicatorReportDefinition rd) {
		
		i6_registered(rd);
		
		i7_on_art_first_time(rd);
		
		i8_on_art_after_stopped(rd);
		
		i9_transferred_in_on_art(rd);

		i10_males(rd);
		
		i11_females_not_pregnant(rd);
		
		i12_females_pregnant(rd);
		
		i13_infants_at_ART_initiation(rd);
		
		i14_children_at_ART_initiation(rd);
		
		i15_adults_at_ART_initiation(rd);
		
		i16_reason_started_ART(rd);
		
		i17_reason_started_ART(rd);
		
		i18_reason_started_ART(rd);
		
		i19_reason_started_ART(rd);
		
		i20_reason_started_ART(rd);
		
		i21_reason_started_ART(rd);
		
		i22_reason_started_ART(rd);
		
		i23_reason_started_ART(rd);
		
		i24_reason_started_ART(rd);
		
		i25_reason_started_ART(rd);
		
		i26_stage_conditions_no_tb(rd);
		
		i27_stage_conditions_tb_2_years(rd);
		
		i28_stage_conditions_current_tb(rd);
		
		i29_stage_conditions_kaposis_sarcoma(rd);
		
		i30_alive(rd);
		
		i31_died_1month_after_ART(rd);
		
		i32_died_2month_after_ART(rd);
		
		i33_died_3month_after_ART(rd);
		
		i34_died_more_3month_after_ART(rd);

		i35_died(rd);
		
		i36_defaulted(rd);
		
		i37_stopped(rd);

		i38_transferred(rd);
		
		i39_total_adverse_outcomes(rd);
		
		// 40 not in report
		
		// 41-47 ARV Regimens -- not in report for now
		
		i48_patients_with_side_effects(rd);
		
		// 49-50 Adherence to Regimens -- not in report for now
		
		i51_current_tb_status_unknown(rd);
		
		i52_current_tb_status_not_suspected(rd);
		
		i53_current_tb_status_suspected(rd);
		
		i54_current_tb_status_confirmed_no_treatment(rd);
		
		i55_current_tb_status_confirmed_treatment(rd);
	}

	private void i6_registered(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Total registered in quarter_",
				"arvquarterly: On ART at location_", h.parameterMap(
						"startedOnOrAfter", "${startDate}",
						"startedOnOrBefore", "${endDate}", "location",
						"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "6_quarter",
				"Total registered in quarter", i, null);

		i = h.newCountIndicator("arvquarterly: Total registered ever_",
				"arvquarterly: On ART at location_", h.parameterMap(
						"startedOnOrAfter", MIN_DATE_PARAMETER,
						"startedOnOrBefore", "${endDate}", "location",
						"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "6_ever",
				"Total registered ever", i, null);
	}
	
	private void i7_on_art_first_time(PeriodIndicatorReportDefinition rd) {
		
		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		baseCohortDefs.put("StartedART", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: On ART at location_"), h.parameterMap(
				"startedOnOrAfter", MIN_DATE_PARAMETER, 
				"startedOnOrBefore", "${endDate}", 
				"location", "${location}")));
		baseCohortDefs.put("Reinitiated", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: reinitiated_"), h.parameterMap(
				"onOrAfter", "${startDate}",
				"onOrBefore", "${endDate}",
				"locationList", "${location}")));
		baseCohortDefs.put("TransferIn", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: generic_coded_obs_"), h.parameterMap(
				"onOrAfter", "${startDate}",
				"onOrBefore", "${endDate}",
				"question", Context.getConceptService().getConcept("EVER RECEIVED ART?"), // ToDo: shouldn't this be a coded concept?
				"valueList", Arrays.asList(Context.getConceptService().getConcept("YES")),
				"locationList", "${location}",
				"encounterTypeList", Arrays.asList(ART_INITIAL_ENCOUNTER))));
		
		CohortIndicator i = h.createCompositionIndicator("arvquarterly: on_art_first_time_",
			 "AND NOT", h.parameterMap("startDate","${startDate}","endDate","${endDate}", "location", "${location}"), baseCohortDefs);
		
		PeriodIndicatorReportUtil.addColumn(rd, "7_quarter", "FT: Patients initiated on ART first time", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "7_ever", "FT: Patients initiated on ART first time", i,
				null);
	}
	
	private void i8_on_art_after_stopped(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: reinitiated_indicator_",
				"arvquarterly: reinitiated_", h.parameterMap(
						"onOrAfter", "${startDate}",
						"onOrBefore", "${endDate}",
						"locationList", "${location}"
						));
		
		PeriodIndicatorReportUtil.addColumn(rd, "8_quarter", "Patients reinitiated on ART", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "8_ever", "Patients reinitiated on ART", i,
				null);
	}
	
	private void i9_transferred_in_on_art(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Ever received ART from initial encounter obs_",
				"arvquarterly: generic_coded_obs_", h.parameterMap(
						"onOrAfter", "${startDate}",
						"onOrBefore", "${endDate}",
						"question", Context.getConceptService().getConcept("EVER RECEIVED ART?"), // ToDo: shouldn't this be a coded concept?
						"valueList", Arrays.asList(Context.getConceptService().getConcept("YES")),
						"locationList", "${location}",
						"encounterTypeList", Arrays.asList(ART_INITIAL_ENCOUNTER)
						));
		
		PeriodIndicatorReportUtil.addColumn(rd, "9_quarter", "TI: Patients transferred in on ART", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "9_ever", "TI: Patients transferred in on ART", i,
				null);
	}
	
private void i10_males(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator("arvquarterly: males on ART ever_", "arvquarterly: males_");
		
		PeriodIndicatorReportUtil.addColumn(rd, "10_quarter", "Males (all ages)", i,
				h.hashMap("registered", "quarter")); 
		PeriodIndicatorReportUtil.addColumn(rd, "10_ever", "Males (all ages)", i,
				null);
	}

private void i11_females_not_pregnant(PeriodIndicatorReportDefinition rd) {
	
	Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
	baseCohortDefs.put("Females", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: females_"), null));
	baseCohortDefs.put("Pregnant", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: pregnant_or_not_"), h.parameterMap(
			"onOrAfter", "${startDate}", 
			"onOrBefore", "${endDate}", 
			"locationList", "${location}",
			"valueList", Arrays.asList(Context.getConceptService().getConcept("YES"))))); // using AND NOT composition
	
	CohortIndicator i = h.createCompositionIndicator("arvquarterly: non-pregnant_females", "AND NOT", h.parameterMap("startDate", "${startDate}", "endDate","${endDate}", "location", "${location}"), baseCohortDefs);
	
	PeriodIndicatorReportUtil.addColumn(rd, "11_quarter", "Non-pregnant females (all ages)", i, h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "11_ever", "Non-pregnant females (all ages)", i, null);
}

private void i12_females_pregnant(PeriodIndicatorReportDefinition rd) {
	
	Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
	baseCohortDefs.put("Females", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: females_"), null));
	baseCohortDefs.put("Pregnant", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: pregnant_or_not_"), h.parameterMap(
			"onOrAfter", "${startDate}", 
			"onOrBefore", "${endDate}",
			"locationList", "${location}",
			"valueList", Arrays.asList(Context.getConceptService().getConcept("YES")))));
	baseCohortDefs.put("MonthPregnant", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: pregnant_within_month_or_not_"), h.parameterMap(
			"startedOnOrAfter", "${startDate}", 
			"endedOnOrBefore", "${endDate}",
			"locationList", "${location}",
			"valueList", Arrays.asList(Context.getConceptService().getConcept("YES")))));
	
	CohortIndicator i = h.createCompositionIndicator("arvquarterly: pregnant females", "Females AND (Pregnant OR MonthPregnant)", h.parameterMap("startDate", "${startDate}", "endDate","${endDate}", "location", "${location}"), baseCohortDefs, true);
	
	PeriodIndicatorReportUtil.addColumn(rd, "12_quarter", "Pregnant females (all ages)", i, h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "12_ever", "Pregnant females (all ages)", i, null);
}

private void i13_infants_at_ART_initiation(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Infant at ART initiation_",
			"arvquarterly: Age at ART initiation_", h.parameterMap(
					"startedOnOrAfter", MIN_DATE_PARAMETER,
					"startedOnOrBefore", "${endDate}", 
					"location", "${location}",
					"maxAge", 17,
					"maxAgeUnit", DurationUnit.MONTHS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "13_quarter", "Infant at ART initiation", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "13_ever", "Infant at ART initiation", i,
			null);
}

private void i14_children_at_ART_initiation(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Child at ART initiation_",
			"arvquarterly: Age at ART initiation_", h.parameterMap(
					"startedOnOrAfter", MIN_DATE_PARAMETER,
					"startedOnOrBefore", "${endDate}", 
					"location", "${location}",
					"minAge", 18,
					"minAgeUnit", DurationUnit.MONTHS,
					"maxAge", 14,
					"maxAgeUnit", DurationUnit.YEARS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "14_quarter", "Child at ART initiation", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "14_ever", "Child at ART initiation", i,
			null);
}

private void i15_adults_at_ART_initiation(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Adult at ART initiation_",
			"arvquarterly: Age at ART initiation_", h.parameterMap(
					"startedOnOrAfter", MIN_DATE_PARAMETER,
					"startedOnOrBefore", "${endDate}", 
					"location", "${location}",
					"minAge", 15,
					"minAgeUnit", DurationUnit.YEARS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "15_quarter", "Adult at ART initiation", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "15_ever", "Adult at ART initiation", i,
			null);
}

private void i16_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_unknown_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("UNKNOWN"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "16_quarter", "Unknown", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "16_ever", "Unknown", i,
			null);
}

private void i17_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_CD4_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("CD4 COUNT LESS THAN 350"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "17_quarter", "CD4 Count Less Than 350", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "17_ever", "CD4 Count Less Than 350", i,
			null);
}

private void i18_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_who_stage_3_both_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE III ADULT AND PEDS"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "18_quarter", "WHO Stage 3 Adults And Peds", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "18_ever", "WHO Stage 3 Adults And Peds", i,
			null);
}

private void i19_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_who_stage_4_both_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE IV ADULT AND PEDS"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "19_quarter", "WHO Stage 4 Adults And Peds", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "19_ever", "WHO Stage 4 Adults And Peds", i,
			null);
}

private void i20_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_who_stage_3_adults_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE III ADULT"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "20_quarter", "WHO Stage 3 Adults", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "20_ever", "WHO Stage 3 Adults", i,
			null);
}

private void i21_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_who_stage_4_adults_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE IV ADULT"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "21_quarter", "WHO Stage 4 Adults", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "21_ever", "WHO Stage 4 Adults", i,
			null);
}

private void i22_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_who_stage_3_peds_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE III PEDS"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "22_quarter", "WHO Stage 3 Peds", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "22_ever", "WHO Stage 3 Peds", i,
			null);
}

private void i23_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_who_stage_4_peds_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE IV PEDS"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "23_quarter", "WHO Stage 4 Peds", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "23_ever", "WHO Stage 4 Peds", i,
			null);
}

private void i24_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_LC_with_stage_2_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("LYMPHOCYTE COUNT BELOW THRESHOLD WITH WHO STAGE 2")))); // todo:swap this out
	
	PeriodIndicatorReportUtil.addColumn(rd, "24_quarter", "Lymphocyte Count Below Threshold With WHO Stage 2", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "24_ever", "Lymphocyte Count Below Threshold With WHO Stage 2", i,
			null);
}

private void i25_reason_started_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: reason_started_ART_severe_hiv_infant_",
			"arvquarterly: reason_started_ART_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("PRESUMED SEVERE HIV CRITERIA IN INFANTS")))); // todo:swap this out
	
	PeriodIndicatorReportUtil.addColumn(rd, "25_quarter", "Presumed Severe HIV Criteria In Infants", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "25_ever", "Presumed Severe HIV Criteria In Infants", i,
			null);
}

private void i26_stage_conditions_no_tb(PeriodIndicatorReportDefinition rd) {
	
	List<Concept> tbList = new ArrayList<Concept>();
	tbList.add(PULMONARY_TUBERCULOSIS);
	tbList.add(EXTRA_PULMONARY_TUBERCULOSIS);
	tbList.add(PTB_WITHIN_THE_PAST_TWO_YEARS);
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_no_tb_",
			"arvquarterly: inverse_stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", tbList));
	
	PeriodIndicatorReportUtil.addColumn(rd, "26_quarter", "No TB", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "26_ever", "No TB", i,
			null);
}

private void i27_stage_conditions_tb_2_years(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_tb_2_years_",
			"arvquarterly: stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(PTB_WITHIN_THE_PAST_TWO_YEARS)));
	
	PeriodIndicatorReportUtil.addColumn(rd, "27_quarter", "TB within the last 2 years", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "27_ever", "TB within the last 2 years", i,
			null);
}

private void i28_stage_conditions_current_tb(PeriodIndicatorReportDefinition rd) {
	
	List<Concept> currentTBList = new ArrayList<Concept>();
	currentTBList.add(PULMONARY_TUBERCULOSIS);
	currentTBList.add(EXTRA_PULMONARY_TUBERCULOSIS);
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_current_tb_",
			"arvquarterly: stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", currentTBList));
	
	PeriodIndicatorReportUtil.addColumn(rd, "28_quarter", "Current episode of TB", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "28_ever", "Current episode of TB", i,
			null);
}

private void i29_stage_conditions_kaposis_sarcoma(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_kaposis_sarcoma_",
			"arvquarterly: stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrAfter", "${startDate}",
					"onOrBefore", "${endDate}",
					"locationList", "${location}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("KAPOSIS SARCOMA"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "29_quarter", "Kaposi’s Sarcoma", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "29_ever", "Kaposi’s Sarcoma", i,
			null);
}

private void i30_alive(PeriodIndicatorReportDefinition rd) {
	
	Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
	baseCohortDefs.put("Alive", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_ON_ART, "location", "${location}")));
	baseCohortDefs.put("Defaulted", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: Missed Appointment_"), h.parameterMap("value1","${endDate-8w}", "onOrBefore", "${endDate}", "locationList", "${location}")));
	baseCohortDefs.put("Died", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_DIED, "location", "${location}")));
	baseCohortDefs.put("Stopped", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_STOPPED, "location", "${location}")));
	baseCohortDefs.put("TransferredOut", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_TRANSFERRED_OUT, "location", "${location}")));

	
	CohortIndicator i = h.createCompositionIndicator("arvquarterly: alive_",
		 "AND NOT", h.parameterMap("endDate","${endDate}", "location", "${location}"), baseCohortDefs);
	
	PeriodIndicatorReportUtil.addColumn(rd, "30", "Total alive and On ART",
			i, null);
}
	
private void i31_died_1month_after_ART(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Started ART state any location within one month before started DIED state at enrollment location on date_",
				"arvquarterly: ART started at enrollment location relative to started DIED at enrollment location on date_", h.parameterMap(
						"onDate", "${endDate}", 
						"primaryStateLocation", "${location}",
						"relativeStateLocation", "${location}",
						"offsetAmount", 1,
						"offsetDuration", 1,
						"offsetWithin", true,
						"offsetUnit", DurationUnit.MONTHS
						));
		PeriodIndicatorReportUtil.addColumn(rd, "31", "Died within the 1st month after ART initiation", i,
				null);
	}

private void i32_died_2month_after_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Started ART state any location within two months before started DIED state at enrollment location on date_",
			"arvquarterly: ART started at enrollment location relative to started DIED at enrollment location on date_", h.parameterMap(
					"onDate", "${endDate}", 
					"primaryStateLocation", "${location}",
					"relativeStateLocation", "${location}",
					"offsetAmount", 2,
					"offsetDuration", 1,
					"offsetWithin", true,
					"offsetUnit", DurationUnit.MONTHS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "32", "Died within the 2nd month after ART initiation", i,
			null);
}

private void i33_died_3month_after_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Started ART state any location within three months before started DIED state at enrollment location on date_",
			"arvquarterly: ART started at enrollment location relative to started DIED at enrollment location on date_", h.parameterMap(
					"onDate", "${endDate}",
					"primaryStateLocation", "${location}",
					"relativeStateLocation", "${location}",
					"offsetAmount", 3,
					"offsetDuration", 1,
					"offsetWithin", true,
					"offsetUnit", DurationUnit.MONTHS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "33", "Died within the 3rd month after ART initiation", i,
			null);
}

private void i34_died_more_3month_after_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Started ART state any location more than three months before started DIED state at enrollment location on date_",
			"arvquarterly: ART started at enrollment location relative to started DIED at enrollment location on date_", h.parameterMap(
					"onDate", "${endDate}",
					"primaryStateLocation", "${location}",
					"relativeStateLocation", "${location}",
					"offsetAmount", 3,
					"offsetDuration", -1,
					"offsetWithin", false,
					"offsetUnit", DurationUnit.MONTHS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "34", "Died more than 3 months after ART initiation", i,
			null);
}

	private void i35_died(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator("arvquarterly: Died total_",
				"arvquarterly: In state at location_", h.parameterMap(
						"onDate", "${endDate}", 
						"state", STATE_DIED, 
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "35", "Died total", i, null);
	}
	
	private void i36_defaulted(PeriodIndicatorReportDefinition rd) {

		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		baseCohortDefs.put("Defaulted", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: Missed Appointment_"), h.parameterMap("value1","${endDate-8w}", "onOrBefore", "${endDate}", "locationList", "${location}")));
		baseCohortDefs.put("Died", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_DIED, "location", "${location}")));
		baseCohortDefs.put("Stopped", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_STOPPED, "location", "${location}")));
		baseCohortDefs.put("TransferredOut", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_TRANSFERRED_OUT, "location", "${location}")));
		
		CohortIndicator i = h.createCompositionIndicator("arvquarterly: Defaulted_",
			 "AND NOT", h.parameterMap("endDate","${endDate}", "location", "${location}"), baseCohortDefs);
		
		PeriodIndicatorReportUtil.addColumn(rd, "36", "Defaulted (more than 2 months overdue after expected to have run out of ARVs)", i, null);
	}
	
	private void i37_stopped(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Stopped taking ARVs_",
				"arvquarterly: In state at location_", h.parameterMap(
						"onDate", "${endDate}", 
						"state", STATE_STOPPED, 
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "37", "Stopped taking ARVs", i,
				null);
	}
	
	private void i38_transferred(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Transferred out_",
				"arvquarterly: In state at location_", h.parameterMap(
						"onDate", "${endDate}", 
						"state", STATE_TRANSFERRED_OUT,
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "38", "Transferred out", i,
				null);
	}
	
	private void i39_total_adverse_outcomes(PeriodIndicatorReportDefinition rd) {

		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		baseCohortDefs.put("Defaulted", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: Missed Appointment_"), h.parameterMap("value1","${endDate-8w}", "onOrBefore", "${endDate}", "locationList", "${location}")));
		baseCohortDefs.put("Died", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_DIED, "location", "${location}")));
		baseCohortDefs.put("Stopped", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_STOPPED, "location", "${location}")));
		baseCohortDefs.put("TransferredOut", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_TRANSFERRED_OUT, "location", "${location}")));
		
		CohortIndicator i = h.createCompositionIndicator("arvquarterly: total_adverse_outcomes_",
			 "OR", h.parameterMap("endDate","${endDate}", "location", "${location}"), baseCohortDefs);
		
		PeriodIndicatorReportUtil.addColumn(rd, "39", "Total adverse outcomes = Died total + Defaulted + Stopped + TO", i, null);
	}
	
	private void i48_patients_with_side_effects(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: patients_with_side_effects_",
				"arvquarterly: generic_numeric_obs_", h.parameterMap(
						"onDate", "${endDate}", 
						"locationList", "${location}",
						"question", Context.getConceptService().getConcept("DOES PATIENT HAVE ADVERSE EFFECTS"),
						"value1", new Double(1)));
		
		PeriodIndicatorReportUtil.addColumn(rd, "48", "Total patients with side effects", i,
				null);
	}
	
	private void i51_current_tb_status_unknown(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: tb_unknown_",
				"arvquarterly: current_tb_status_", h.parameterMap(
						"onDate", "${endDate}",
						"locationList", "${location}",
						"valueList", Arrays.asList(Context.getConceptService().getConcept("UNKNOWN"))));
		
		PeriodIndicatorReportUtil.addColumn(rd, "51", "TB unknown", i,
				null);
	}
	
	private void i52_current_tb_status_not_suspected(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: tb_not_suspected_",
				"arvquarterly: current_tb_status_", h.parameterMap(
						"onDate", "${endDate}",
						"locationList", "${location}",
						"valueList", Arrays.asList(Context.getConceptService().getConcept("TB NOT SUSPECTED"))));
		
		PeriodIndicatorReportUtil.addColumn(rd, "52", "TB not suspected", i,
				null);
	}
	
	private void i53_current_tb_status_suspected(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: tb_suspected_",
				"arvquarterly: current_tb_status_", h.parameterMap(
						"onDate", "${endDate}",
						"locationList", "${location}",
						"valueList", Arrays.asList(Context.getConceptService().getConcept("TB SUSPECTED"))));
		
		PeriodIndicatorReportUtil.addColumn(rd, "53", "TB suspected", i,
				null);
	}
	
	private void i54_current_tb_status_confirmed_no_treatment(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: tb_confirmed_no_treatment_",
				"arvquarterly: current_tb_status_", h.parameterMap(
						"onDate", "${endDate}",
						"locationList", "${location}",
						"valueList", Arrays.asList(Context.getConceptService().getConcept("CONFIRMED TB NOT ON TREATMENT"))));
		
		PeriodIndicatorReportUtil.addColumn(rd, "54", "TB confirmed Not on TB treatment", i,
				null);
	}
	
private void i55_current_tb_status_confirmed_treatment(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: tb_confirmed_treatment_",
				"arvquarterly: current_tb_status_", h.parameterMap(
						"onDate", "${endDate}",
						"locationList", "${location}",
						"valueList", Arrays.asList(Context.getConceptService().getConcept("CONFIRMED TB ON TREATMENT"))));
		
		PeriodIndicatorReportUtil.addColumn(rd, "55", "TB confirmed On TB treatment", i,
				null);
	}
}