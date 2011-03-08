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
import org.openmrs.module.pihmalawi.reporting.Event;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.HasAgeOnStartedStateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAfterStartedStateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.StateRelativeToStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
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
	
	private final Concept PTB_WITHIN_THE_PAST_TWO_YEARS;

	private final Program PROGRAM;

	Helper h = new Helper();

	private final ProgramWorkflowState STATE_DIED;

	private final ProgramWorkflowState STATE_ON_ART;

	private final ProgramWorkflowState STATE_STOPPED;

	private final ProgramWorkflowState STATE_TRANSFERRED_OUT;
	
	private final EncounterType ART_INITIAL_ENCOUNTER;
	

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
		
		PULMONARY_TUBERCULOSIS = Context.getConceptService().getConcept("PULMONARY TUBERCULOSIS");
		
		PTB_WITHIN_THE_PAST_TWO_YEARS = Context.getConceptService().getConcept("PTB WITHIN THE PAST TWO YEARS");
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
			log.info("ReportDesign=*"+rd.getName().toString()+"*");
			if ("ARV QUARTERLY (Excel)_".equals(rd.getName().toString()) ){
				log.info("^^^^^^^^^^^^^^^ purging Report Design="+rd.getName());
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
		// On ART at location
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName("arvquarterly: On ART at location_");
		pscd.setState(STATE_ON_ART);
		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		
		// gender
		GenderCohortDefinition malecd = new GenderCohortDefinition();
		malecd.setName("arvquarterly: males_");
		malecd.setMaleIncluded(true);
		h.replaceCohortDefinition(malecd);
		
		GenderCohortDefinition femalecd = new GenderCohortDefinition();
        femalecd.setName("arvquarterly: females_");
        femalecd.setFemaleIncluded(true);
        h.replaceCohortDefinition(femalecd);
        
        // generic coded obs
        CodedObsCohortDefinition genericcocd = new CodedObsCohortDefinition();
        genericcocd.setName("arvquarterly: generic_coded_obs_");
        genericcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        genericcocd.addParameter(new Parameter("question", "Question", Concept.class));
        genericcocd.addParameter(new Parameter("valueList", "Value List", List.class));
        genericcocd.addParameter(new Parameter("encounterTypeList", "Encounter Type List", List.class));
        genericcocd.setTimeModifier(TimeModifier.LAST);
        genericcocd.setOperator(SetComparator.IN);
		h.replaceCohortDefinition(genericcocd);
		
		// started state after stopped state
		StateRelativeToStateCohortDefinition srtscd = new StateRelativeToStateCohortDefinition();
		srtscd.setName("arvquarterly: started state after stopped state (at enrollment location on date)_");
		srtscd.addParameter(new Parameter("primaryStateEvent", "Primary State Event",
				Event.class));
		srtscd.addParameter(new Parameter("primaryState", "Primary State",
				ProgramWorkflowState.class));
		srtscd.addParameter(new Parameter("relativeStateEvent", "Relative State Event",
				Event.class));
		srtscd.addParameter(new Parameter("relativeState", "Relative State",
				ProgramWorkflowState.class));
		srtscd.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
		srtscd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
		srtscd.addParameter(new Parameter("primaryStateLocation", "Primary State Location", 
				Location.class));
		srtscd.addParameter(new Parameter("relativeStateLocation", "Relative State Location", 
				Location.class));
		srtscd.setOffsetAmount(0);
		h.replaceCohortDefinition(srtscd);
		
		// NOT started state after stopped state
		InverseCohortDefinition not_srtscd = new InverseCohortDefinition();
		not_srtscd.setBaseDefinition(srtscd);
		not_srtscd.setName("arvquarterly: NOT started state after stopped state (at enrollment location on date)_");
		not_srtscd.addParameter(new Parameter("primaryStateEvent", "Primary State Event",
				Event.class));
		not_srtscd.addParameter(new Parameter("primaryState", "Primary State",
				ProgramWorkflowState.class));
		not_srtscd.addParameter(new Parameter("relativeStateEvent", "Relative State Event",
				Event.class));
		not_srtscd.addParameter(new Parameter("relativeState", "Relative State",
				ProgramWorkflowState.class));
		not_srtscd.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
		not_srtscd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
		not_srtscd.addParameter(new Parameter("primaryStateLocation", "Primary State Location", 
				Location.class));
		not_srtscd.addParameter(new Parameter("relativeStateLocation", "Relative State Location", 
				Location.class));
		h.replaceCohortDefinition(not_srtscd);
		
		// stage conditions at start of ART obs
        CodedObsCohortDefinition stage_conditions_start_ARTcocd = new CodedObsCohortDefinition();
        stage_conditions_start_ARTcocd.setName("arvquarterly: stage_conditions_at_start_ART_coded_obs_");
        stage_conditions_start_ARTcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        stage_conditions_start_ARTcocd.addParameter(new Parameter("valueList", "Value List", List.class));
        stage_conditions_start_ARTcocd.setQuestion(Context.getConceptService().getConcept("WHO STAGES CRITERIA PRESENT"));
        stage_conditions_start_ARTcocd.setTimeModifier(TimeModifier.LAST);
        stage_conditions_start_ARTcocd.setOperator(SetComparator.IN);
		stage_conditions_start_ARTcocd.setEncounterTypeList(Arrays.asList(ART_INITIAL_ENCOUNTER));
		h.replaceCohortDefinition(stage_conditions_start_ARTcocd);
		
		InverseCohortDefinition i_stage_conditions_cocd = new InverseCohortDefinition();
		i_stage_conditions_cocd.setBaseDefinition(stage_conditions_start_ARTcocd);
		i_stage_conditions_cocd.setName("arvquarterly: inverse_stage_conditions_at_start_ART_coded_obs_");
		i_stage_conditions_cocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
		i_stage_conditions_cocd.addParameter(new Parameter("valueList", "Value List", List.class));
		h.replaceCohortDefinition(i_stage_conditions_cocd);
		
		// generic boolean obs -- use Double(0) and Double(1)
        NumericObsCohortDefinition genericncd = new NumericObsCohortDefinition();
        genericncd.setName("arvquarterly: generic_numeric_obs_");
        genericncd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        genericncd.addParameter(new Parameter("question", "Question", Concept.class));
        genericncd.addParameter(new Parameter("value1", "Value 1", List.class));
        genericncd.setTimeModifier(TimeModifier.LAST);
        genericncd.setOperator1(RangeComparator.EQUAL);
		//cocd.setEncounterTypeList(encounterTypeList); ???
		h.replaceCohortDefinition(genericncd);        
        
        // pregnancy
		// todo: only take obs from specific encounters??
        CodedObsCohortDefinition pregnantcocd = new CodedObsCohortDefinition();
        pregnantcocd.setName("arvquarterly: pregnant_or_not_");
        pregnantcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        pregnantcocd.addParameter(new Parameter("valueList", "value list", List.class));
        pregnantcocd.setQuestion(Context.getConceptService().getConcept(
				"PREGNANCY STATUS"));
        pregnantcocd.setTimeModifier(TimeModifier.LAST);
        pregnantcocd.setOperator(SetComparator.IN);
		//cocd.setEncounterTypeList(encounterTypeList); ???
		h.replaceCohortDefinition(pregnantcocd);
		
		// reason for starting ARV
		// todo: only take obs from specific encounters??
        CodedObsCohortDefinition reasonARTcocd = new CodedObsCohortDefinition();
        reasonARTcocd.setName("arvquarterly: reason_started_ARV_");
        reasonARTcocd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        reasonARTcocd.addParameter(new Parameter("valueList", "value list", List.class));
        reasonARTcocd.setQuestion(Context.getConceptService().getConcept(
				"REASON ANTIRETROVIRALS STARTED"));
        reasonARTcocd.setTimeModifier(TimeModifier.LAST);
        reasonARTcocd.setOperator(SetComparator.IN);
		//cocd.setEncounterTypeList(encounterTypeList); ???
		h.replaceCohortDefinition(reasonARTcocd);
        
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
	
		// In state at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("arvquarterly: In state at location_");
		islcd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		// Died after ART initiation
		InStateAfterStartedStateCohortDefinition diedAfterARTInitiation = new InStateAfterStartedStateCohortDefinition();
		diedAfterARTInitiation.setName("arvquarterly: ART started any location relative to started DIED at enrollment location on date_");
		diedAfterARTInitiation.setPrimaryState(STATE_DIED);
		diedAfterARTInitiation.setRelativeState(STATE_ON_ART);
		diedAfterARTInitiation.addParameter(new Parameter("onDate", "On Date", Date.class));
		diedAfterARTInitiation.addParameter(new Parameter("primaryStateLocation", "Primary State Location", 
				Location.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetAmount", "Offset Amount", Date.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetDuration", "Offset Duration", Integer.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetWithin", "Offset Within", Boolean.class));
		diedAfterARTInitiation.addParameter(new Parameter("offsetUnit", "Offset Unit", Date.class));
		h.replaceCohortDefinition(diedAfterARTInitiation);
		
		// Defaulters
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
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		dod.addParameter(new Parameter("value1", "to", Date.class));
		dod.addParameter(new Parameter("value2", "from", Date.class));
		h.replaceCohortDefinition(dod);
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
		
		i16_reason_started_ART_placeholder(rd);
		
		i17_reason_started_ART_placeholder(rd);
		
		i18_reason_started_ART_placeholder(rd);
		
		i19_reason_started_ART_placeholder(rd);
		
		i20_reason_started_ART_WHO_3(rd);
		
		i21_reason_started_ART_WHO_4(rd);
		
		i22_reason_started_ART_placeholder(rd);
		
		i23_stage_conditions_no_tb(rd);
		
		i24_stage_conditions_tb_2_years(rd);
		
		i25_stage_conditions_current_tb(rd);
		
		i26_stage_conditions_kaposis_sarcoma(rd);
		
		i27_alive(rd);
		
		i28_died_1month_after_ART(rd);
		
		i29_died_2month_after_ART(rd);
		
		i30_died_3month_after_ART(rd);
		
		i31_died_more_3month_after_ART(rd);

		i32_died(rd);
		
		i33_defaulted(rd);
		
		i34_stopped(rd);

		i35_transferred(rd);
		
		i45_patients_with_side_effects(rd);
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
		// should we include other locations??
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: NOT Ended STOPPED state enrollment location before Started On ART state at enrollment location in window_",
				"arvquarterly: NOT started state after stopped state (at enrollment location on date)_", h.parameterMap(
						"primaryStateEvent", Event.STARTED,
						"primaryState", STATE_ON_ART,
						"relativeStateEvent", Event.STOPPED,
						"relativeState", STATE_STOPPED,
						"onOrAfter", "${startDate}",
						"onOrBefore", "${endDate}",
						"primaryStateLocation", "${location}",
						"relativeStateLocation", "${location}"
						));
		
		PeriodIndicatorReportUtil.addColumn(rd, "7_quarter", "FT: Patients initiated on ART first time", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "7_ever", "FT: Patients initiated on ART first time", i,
				null);
	}
	
	private void i8_on_art_after_stopped(PeriodIndicatorReportDefinition rd) {
		// todo: what about excluding defaulters??
		// stopping is also be counted at other locations???
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Ended STOPPED state enrollment location before Started On ART state at enrollment location in window",
				"arvquarterly: started state after stopped state (at enrollment location on date)_", h.parameterMap(
						"primaryStateEvent", Event.STARTED,
						"primaryState", STATE_ON_ART,
						"relativeStateEvent", Event.STOPPED,
						"relativeState", STATE_STOPPED,
						"onOrAfter", "${startDate}",
						"onOrBefore", "${endDate}",
						"primaryStateLocation", "${location}",
						"relativeStateLocation", "${location}"
						));
		
		PeriodIndicatorReportUtil.addColumn(rd, "8_quarter", "Re: Patients re-initiated on ART", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "8_ever", "Re: Patients re-initiated on ART", i,
				null);
	}
	
	private void i9_transferred_in_on_art(PeriodIndicatorReportDefinition rd) {
		// todo: what about excluding defaulters??
		// should stopping also be counted at other locations???
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Ever received ART from initial encounter obs_",
				"arvquarterly: generic_coded_obs_", h.parameterMap(
						"onOrBefore", "${endDate}",
						"question", Context.getConceptService().getConcept("PREGNANCY STATUS"),  // todo: change to EVER_RECEIVED_ART OBS
						"valueList", Arrays.asList(Context.getConceptService().getConcept("YES")),
						"encounterTypeList", Arrays.asList(ART_INITIAL_ENCOUNTER)
						));
		
		PeriodIndicatorReportUtil.addColumn(rd, "9_quarter", "Placeholder for -- TI: Patients transferred in on ART", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "9_ever", "Placeholder for -- TI: Patients transferred in on ART", i,
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
	baseCohortDefs.put("Pregnant", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: pregnant_or_not_"), h.parameterMap("onOrBefore", "${endDate}", "valueList", Arrays.asList(Context.getConceptService().getConcept("NO")))));
	
	CohortIndicator i = h.createCompositionIndicator("Non-Pregnant_Females", "AND NOT", h.parameterMap("endDate","${endDate}"), baseCohortDefs);
	
	PeriodIndicatorReportUtil.addColumn(rd, "11_quarter", "Non-pregnant females (all ages)", i, h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "11_ever", "Non-pregnant females (all ages)", i, null);
}

private void i12_females_pregnant(PeriodIndicatorReportDefinition rd) {
	
	Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
	baseCohortDefs.put("Females", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: females_"), null));
	baseCohortDefs.put("Pregnant", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: pregnant_or_not_"), h.parameterMap("onOrBefore", "${endDate}", "valueList", Arrays.asList(Context.getConceptService().getConcept("YES")))));
	
	CohortIndicator i = h.createCompositionIndicator("Pregnant_Females", "AND", h.parameterMap("endDate","${endDate}"), baseCohortDefs);
	
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
					"maxAge", 18,
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
					"maxAge", 15,
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

private void i16_reason_started_ART_placeholder(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: placeholder16 at ART initiation_",
			"arvquarterly: reason_started_ARV_", h.parameterMap(
					"onOrBefore", "${endDate}", 
					"valueList", Arrays.asList(Context.getConceptService().getConcept("TOTAL MATERNAL TO CHILD TRANSMISSION PROPHYLAXIS"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "16_quarter", "Placeholder16", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "16_ever", "Placeholder16", i,
			null);
}

private void i17_reason_started_ART_placeholder(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: placeholder17 at ART initiation_",
			"arvquarterly: reason_started_ARV_", h.parameterMap(
					"onOrBefore", "${endDate}", 
					"valueList", Arrays.asList(Context.getConceptService().getConcept("PULMONARY TUBERCULOSIS"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "17_quarter", "Placeholder17", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "17_ever", "Placeholder17", i,
			null);
}

private void i18_reason_started_ART_placeholder(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: placeholder18 at ART initiation_",
			"arvquarterly: reason_started_ARV_", h.parameterMap(
					"onOrBefore", "${endDate}", 
					"valueList", Arrays.asList(Context.getConceptService().getConcept("TRANSFER IN"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "18_quarter", "Placeholder18", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "18_ever", "Placeholder18", i,
			null);
}

private void i19_reason_started_ART_placeholder(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: placeholder19 at ART initiation_",
			"arvquarterly: reason_started_ARV_", h.parameterMap(
					"onOrBefore", "${endDate}", 
					"valueList", Arrays.asList(Context.getConceptService().getConcept("TREATMENT"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "19_quarter", "Placeholder19", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "19_ever", "Placeholder19", i,
			null);
}

private void i20_reason_started_ART_WHO_3(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: WHO Stage 3 at ART initiation_",
			"arvquarterly: reason_started_ARV_", h.parameterMap(
					"onOrBefore", "${endDate}", 
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE 3"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "20_quarter", "WHO stage 3", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "20_ever", "WHO stage 3", i,
			null);
}

private void i21_reason_started_ART_WHO_4(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: WHO Stage 4 at ART initiation_",
			"arvquarterly: reason_started_ARV_", h.parameterMap(
					"onOrBefore", "${endDate}", 
					"valueList", Arrays.asList(Context.getConceptService().getConcept("WHO STAGE 4"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "21_quarter", "WHO stage 4", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "21_ever", "WHO stage 4", i,
			null);
}

private void i22_reason_started_ART_placeholder(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: placeholder22 at ART initiation_",
			"arvquarterly: reason_started_ARV_", h.parameterMap(
					"onOrBefore", "${endDate}", 
					"valueList", Arrays.asList(Context.getConceptService().getConcept("TREATMENT"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "22_quarter", "Placeholder22", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "22_ever", "Placeholder22", i,
			null);
}

private void i23_stage_conditions_no_tb(PeriodIndicatorReportDefinition rd) {
	
	List<Concept> tbList = new ArrayList<Concept>();
	tbList.add(PULMONARY_TUBERCULOSIS);
	tbList.add(PTB_WITHIN_THE_PAST_TWO_YEARS);
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_no_tb_",
			"arvquarterly: inverse_stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrBefore", "${endDate}",
					"valueList", tbList));
	
	PeriodIndicatorReportUtil.addColumn(rd, "23_quarter", "No TB", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "23_ever", "No TB", i,
			null);
}

private void i24_stage_conditions_tb_2_years(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_tb_2_years_",
			"arvquarterly: stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrBefore", "${endDate}",
					"valueList", Arrays.asList(PTB_WITHIN_THE_PAST_TWO_YEARS)));
	
	PeriodIndicatorReportUtil.addColumn(rd, "24_quarter", "TB within the last 2 years", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "24_ever", "TB within the last 2 years", i,
			null);
}

private void i25_stage_conditions_current_tb(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_current_tb_",
			"arvquarterly: stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrBefore", "${endDate}",
					"valueList", Arrays.asList(PULMONARY_TUBERCULOSIS)));
	
	PeriodIndicatorReportUtil.addColumn(rd, "25_quarter", "Current episode of TB", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "25_ever", "Current episode of TB", i,
			null);
}

private void i26_stage_conditions_kaposis_sarcoma(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: stage_conditions_kaposis_sarcoma_",
			"arvquarterly: stage_conditions_at_start_ART_coded_obs_", h.parameterMap(
					"onOrBefore", "${endDate}",
					"valueList", Arrays.asList(Context.getConceptService().getConcept("KAPOSIS SARCOMA"))));
	
	PeriodIndicatorReportUtil.addColumn(rd, "26_quarter", "Kaposi’s Sarcoma", i,
			h.hashMap("registered", "quarter"));
	PeriodIndicatorReportUtil.addColumn(rd, "26_ever", "Kaposi’s Sarcoma", i,
			null);
}

private void i27_alive(PeriodIndicatorReportDefinition rd) {
	// done: excluding defaulters -- I need to test this!
	
	Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
	baseCohortDefs.put("Alive", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_ON_ART, "location", "${location}")));
	baseCohortDefs.put("Defaulted", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: Missed Appointment_"), h.parameterMap("value1","${endDate-8w}", "onOrBefore", "${endDate}")));
	
	CohortIndicator i = h.createCompositionIndicator("Alive",
		 "AND NOT", h.parameterMap("endDate","${endDate}", "location", "${location}"), baseCohortDefs);
	
	PeriodIndicatorReportUtil.addColumn(rd, "27", "Total alive and On ART",
			i, null);
}
	
private void i28_died_1month_after_ART(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Started ART state any location within one month before started DIED state at enrollment location on date_",
				"arvquarterly: ART started any location relative to started DIED at enrollment location on date_", h.parameterMap(
						"onDate", "${endDate}", 
						"primaryStateLocation", "${location}",
						"offsetAmount", 1,
						"offsetDuration", 1,
						"offsetWithin", true,
						"offsetUnit", DurationUnit.MONTHS
						));
		PeriodIndicatorReportUtil.addColumn(rd, "28", "Died within the 1st month after ART initiation", i,
				null);
	}

private void i29_died_2month_after_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Started ART state any location within two months before started DIED state at enrollment location on date_",
			"arvquarterly: ART started any location relative to started DIED at enrollment location on date_", h.parameterMap(
					"onDate", "${endDate}", 
					"primaryStateLocation", "${location}",
					"offsetAmount", 2,
					"offsetDuration", 1,
					"offsetWithin", true,
					"offsetUnit", DurationUnit.MONTHS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "29", "Died within the 2nd month after ART initiation", i,
			null);
}

private void i30_died_3month_after_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Started ART state any location within three months before started DIED state at enrollment location on date_",
			"arvquarterly: ART started any location relative to started DIED at enrollment location on date_", h.parameterMap(
					"onDate", "${endDate}",
					"primaryStateLocation", "${location}",
					"offsetAmount", 3,
					"offsetDuration", 1,
					"offsetWithin", true,
					"offsetUnit", DurationUnit.MONTHS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "30", "Died within the 3rd month after ART initiation", i,
			null);
}

private void i31_died_more_3month_after_ART(PeriodIndicatorReportDefinition rd) {
	
	CohortIndicator i = h.newCountIndicator(
			"arvquarterly: Started ART state any location more than three months before started DIED state at enrollment location on date_",
			"arvquarterly: ART started any location relative to started DIED at enrollment location on date_", h.parameterMap(
					"onDate", "${endDate}",
					"primaryStateLocation", "${location}",
					"offsetAmount", 3,
					"offsetDuration", -1,
					"offsetWithin", false,
					"offsetUnit", DurationUnit.MONTHS
					));
	PeriodIndicatorReportUtil.addColumn(rd, "31", "Died more than 3 months after ART initiation", i,
			null);
}

	private void i32_died(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator("arvquarterly: Died total_",
				"arvquarterly: In state at location_", h.parameterMap("onDate",
						"${endDate}", "state", STATE_DIED, "location",
						"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "32", "Died total", i, null);
	}
	
	private void i33_defaulted(PeriodIndicatorReportDefinition rd) {

		Map<String, Mapped<? extends CohortDefinition>> baseCohortDefs = new LinkedHashMap<String, Mapped<? extends CohortDefinition>>();
		baseCohortDefs.put("Defaulted", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: Missed Appointment_"), h.parameterMap("value1","${endDate-8w}", "onOrBefore", "${endDate}")));
		baseCohortDefs.put("Died", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_DIED, "location", "${location}")));
		baseCohortDefs.put("Stopped", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_STOPPED, "location", "${location}")));
		baseCohortDefs.put("TransferredOut", new Mapped<CohortDefinition>(h.cohortDefinition("arvquarterly: In state at location_"), h.parameterMap("onDate", "${endDate}", "state", STATE_TRANSFERRED_OUT, "location", "${location}")));
		
		CohortIndicator i = h.createCompositionIndicator("arvquarterly: Defaulted_",
			 "AND NOT", h.parameterMap("endDate","${endDate}", "location", "${location}"), baseCohortDefs);
		
		PeriodIndicatorReportUtil.addColumn(rd, "33", "Defaulted (more than 2 months overdue after expected to have run out of ARVs)", i, null);
	}
	
	private void i34_stopped(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Stopped taking ARVs_",
				"arvquarterly: In state at location_", h.parameterMap("onDate",
						"${endDate}", "state", STATE_STOPPED, "location",
						"${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "34", "Stopped taking ARVs", i,
				null);
	}
	
	private void i35_transferred(PeriodIndicatorReportDefinition rd) {
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: Transferred out_",
				"arvquarterly: In state at location_", h.parameterMap("onDate",
						"${endDate}", "state", STATE_TRANSFERRED_OUT,
						"location", "${location}"));
		PeriodIndicatorReportUtil.addColumn(rd, "35", "Transferred out", i,
				null);
	}
	
	private void i45_patients_with_side_effects(PeriodIndicatorReportDefinition rd) {
		
		CohortIndicator i = h.newCountIndicator(
				"arvquarterly: patients_with_side_effects_",
				"arvquarterly: generic_numeric_obs_", h.parameterMap(
						"onOrBefore", "${endDate}", 
						"question", Context.getConceptService().getConcept("DOES PATIENT HAVE ADVERSE EFFECTS"),
						"value1", new Double(1)));
		
		PeriodIndicatorReportUtil.addColumn(rd, "45_quarter", "Total patients with side effects", i,
				h.hashMap("registered", "quarter"));
		PeriodIndicatorReportUtil.addColumn(rd, "45_ever", "Total patients with side effects", i,
				null);
	}
}