package org.openmrs.module.pihmalawi.reports.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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

public class SetupPihQuarterlyCrossSite {
	
	private final Concept CONCEPT_HIV_DNA_PCR;
	
	private final Concept CONCEPT_ID_DNA_PCR_NEGATIVE;
	
	private final Concept CONCEPT_ID_DNA_PCR_POSITIVE;
	
	private final Concept CONCEPT_ID_DNA_PCR_INDETERMINATE;
	
	private static final Date MIN_DATE_PARAMETER = new Date(100, 1, 1);
	
	private final Program PROGRAM_EID_PROGRAM;
	
	private final Program PROGRAM_HIV_PROGRAM;
	
	private final EncounterType ENCOUNTER_TYPE_LAB;
	
	private final EncounterType ENCOUNTER_TYPE_PART_INITIAL;
	
	private final EncounterType ENCOUNTER_TYPE_PART_FOLLOWUP;
	
	private final EncounterType ENCOUNTER_TYPE_ART_INITIAL;
	
	private final EncounterType ENCOUNTER_TYPE_ART_FOLLOWUP;
	
	private final EncounterType ENCOUNTER_TYPE_EID_INITIAL;
	
	private final EncounterType ENCOUNTER_TYPE_EID_FOLLOWUP;
	
	private final Concept CONCEPT_CD4_COUNT;
	
	private final Concept CONCEPT_CD4_COUNT_MASTERCARD;
	
	private final Concept CONCEPT_WEIGHT;
	
	private final Concept CONCEPT_DNA_PCR;
	
	private final ProgramWorkflowState STATE_FOLLOWING;
	
	private final ProgramWorkflowState STATE_ON_ANTIRETROVIRALS;
	
	Helper h = new Helper();
	
	public SetupPihQuarterlyCrossSite(Helper helper) {
		h = helper;
		CONCEPT_HIV_DNA_PCR = Context.getConceptService()
		        .getConceptByName("HIV DNA polymerase chain reaction");
		CONCEPT_ID_DNA_PCR_NEGATIVE = Context.getConceptService().getConceptByIdOrName("703");
		CONCEPT_ID_DNA_PCR_POSITIVE = Context.getConceptService().getConceptByIdOrName("664");
		CONCEPT_ID_DNA_PCR_INDETERMINATE = Context.getConceptService().getConceptByIdOrName("1138");
		PROGRAM_EID_PROGRAM = Context.getProgramWorkflowService().getProgramByName("z_deprecated Early infant diagnosis program");
		PROGRAM_HIV_PROGRAM = Context.getProgramWorkflowService().getProgramByName("HIV program");
		ENCOUNTER_TYPE_LAB = Context.getEncounterService().getEncounterType("LAB");
		ENCOUNTER_TYPE_PART_INITIAL = Context.getEncounterService().getEncounterType("PART_INITIAL");
		ENCOUNTER_TYPE_PART_FOLLOWUP = Context.getEncounterService().getEncounterType("PART_FOLLOWUP");
		ENCOUNTER_TYPE_ART_INITIAL = Context.getEncounterService().getEncounterType("ART_INITIAL");
		ENCOUNTER_TYPE_ART_FOLLOWUP = Context.getEncounterService().getEncounterType("ART_FOLLOWUP");
		ENCOUNTER_TYPE_EID_INITIAL = Context.getEncounterService().getEncounterType("EID_INITIAL");
		ENCOUNTER_TYPE_EID_FOLLOWUP = Context.getEncounterService().getEncounterType("EID_FOLLOWUP");
		CONCEPT_CD4_COUNT = Context.getConceptService().getConceptByName("CD4 count");
		CONCEPT_CD4_COUNT_MASTERCARD = Context.getConceptService().getConceptByName("Clinician reported to CD4");
		CONCEPT_WEIGHT = Context.getConceptService().getConceptByName("Weight (kg)");
		CONCEPT_DNA_PCR = Context.getConceptService().getConceptByName("CONCEPT_DNA_PCR");
		STATE_FOLLOWING = PROGRAM_HIV_PROGRAM.getWorkflowByName("Treatment status").getStateByName("Pre-ART (Continue)");
		STATE_ON_ANTIRETROVIRALS = PROGRAM_HIV_PROGRAM.getWorkflowByName("Treatment status").getStateByName(
		    "On antiretrovirals");
		
	}
	
	public void setup() throws Exception {
		delete();
		
		createDimensions();
		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);
		h.replaceReportDefinition(rd);
		h.createXlsOverview(rd, "Quartely Cross Site Indicator Form 10.15.xls",
		    "Quartely Cross Site Indicator Form 10.15.xls (Excel)_", null);
	}
	
	private void createDimensions() {
		AgeCohortDefinition acd = new AgeCohortDefinition();
		acd.setName("xsite: Age_");
		acd.addParameter(new Parameter("minAge", "minAge", Integer.class));
		acd.addParameter(new Parameter("maxAge", "maxAge", Integer.class));
		acd.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		h.replaceCohortDefinition(acd);
		
		CohortDefinitionDimension md = new CohortDefinitionDimension();
		md.setName("xsite: Age_");
		md.addParameter(new Parameter("endDate", "endDate", Date.class));
		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("minAge", 15);
		m2.put("maxAge", 500);
		m2.put("effectiveDate", "${endDate}");
		md.addCohortDefinition("Adult", acd, m2);
		m2 = new HashMap<String, Object>();
		m2.put("minAge", 0);
		m2.put("maxAge", 14);
		m2.put("effectiveDate", "${endDate}");
		md.addCohortDefinition("Child", acd, m2);
		h.replaceDimensionDefinition(md);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals("Quartely Cross Site Indicator Form 10.15.xls (Excel)_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDimension("xsite: Age_");
		h.purgeDefinition(DataSetDefinition.class, "PIH Quarterly Cross Site_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "PIH Quarterly Cross Site_");
		h.purgeAll("xsite: ");
	}
	
	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.addParameter(new Parameter("startDate", "Start date (Start of quarter)", Date.class));
		rd.addParameter(new Parameter("endDate", "End date (End of quarter)", Date.class));

		rd.setName("PIH Quarterly Cross Site_");
		rd.setupDataSetDefinition();
		rd.addDimension("Age", h.cohortDefinitionDimension("xsite: Age_"), h.parameterMap("endDate", "${endDate}"));
		return rd;
	}
	
	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		genericCohortDefinitions(rd);
	}
	
	public CohortDefinition exposedEnrolledAtLocationInPeriod(
			String prefix) {
		PatientStateCohortDefinition pscd2 = new PatientStateCohortDefinition();
		pscd2.setName(prefix + ": Exposed Child at location_");
		pscd2.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status",
				"Exposed Child (Continue)")));
		pscd2.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd2.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		h.replaceCohortDefinition(pscd2);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
//
//		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
//		ccd.setName(prefix + ": In Exposed at location_");
//		ccd.addParameter(new Parameter("startedOnOrAfter",
//				"startedOnOrAfter", Date.class));
//		ccd.addParameter(new Parameter("startedOnOrBefore",
//				"startedOnOrBefore", Date.class));
//		ccd.getSearches().put(
//				"exposed",
//				new Mapped(pscd2, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}", "startedOnOrAfter", "${startedOnOrAfter}")));
//		ccd.getSearches().put(
//				"hccnumber",
//				new Mapped(scd, h
//						.parameterMap()));
//		ccd.setCompositionString("exposed AND hccnumber");
//		h.replaceCohortDefinition(ccd);

		return pscd2;
	}


	private void genericCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		SqlCohortDefinition hcc = new SqlCohortDefinition();
		hcc.setName("xsite: HCC number_");
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0;";
		hcc.setQuery(sql);
		h.replaceCohortDefinition(hcc);
		SqlCohortDefinition arv = new SqlCohortDefinition();
		arv.setName("xsite: ARV number_");
		 sql = "select patient_id from patient_identifier where identifier_type=4 and voided=0;";
		arv.setQuery(sql);
		h.replaceCohortDefinition(arv);

		ProgramEnrollmentCohortDefinition pecd = new ProgramEnrollmentCohortDefinition();
		pecd.setName("xsite: In HIV program_");
		pecd.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		pecd.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		pecd.setPrograms(Arrays.asList(PROGRAM_HIV_PROGRAM));
		h.replaceCohortDefinition(pecd);
		CohortDefinition exp = exposedEnrolledAtLocationInPeriod("xsite");
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: HIV program_");
		ccd.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(pecd, h.parameterMap("enrolledOnOrAfter", MIN_DATE_PARAMETER,
		        "enrolledOnOrBefore", "${enrolledOnOrBefore}")));
		ccd.getSearches().put(
			    "2",
			    new Mapped(exp, h.parameterMap("startedOnOrBefore", "${enrolledOnOrBefore}", "startedOnOrAfter",
			        MIN_DATE_PARAMETER)));
		ccd.getSearches().put(
			    "3",
			    new Mapped(hcc, h.parameterMap()));
		ccd.getSearches().put(
			    "4",
			    new Mapped(arv, h.parameterMap()));
		ccd.setCompositionString("1 AND NOT 2 AND (3 OR 4)");
		h.replaceCohortDefinition(ccd);
		

		// hiv q1
		CohortIndicator i = h.newCountIndicator("xsite: HIV-Q1 (Pre-ART, ART ever)", "xsite: HIV program_", h
		        .parameterMap("enrolledOnOrAfter", MIN_DATE_PARAMETER, "enrolledOnOrBefore", "${endDate}"));
		addDimensionColumn(rd, "hivq1", "Ever registered in the HIV program", i, null);
		
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("xsite: Pre-ART visits_");
		ecd.setEncounterTypeList(Arrays.asList(ENCOUNTER_TYPE_PART_INITIAL, ENCOUNTER_TYPE_PART_FOLLOWUP));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		h.replaceCohortDefinition(ecd);
		
		ecd = new EncounterCohortDefinition();
		ecd.setName("xsite: HIV visits_");
		ecd.setEncounterTypeList(Arrays.asList(ENCOUNTER_TYPE_PART_INITIAL, ENCOUNTER_TYPE_PART_FOLLOWUP,
		    ENCOUNTER_TYPE_ART_INITIAL, ENCOUNTER_TYPE_ART_FOLLOWUP));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		h.replaceCohortDefinition(ecd);
		
		 ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: HIV with first visits in period_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: HIV program_"), h.parameterMap("enrolledOnOrAfter", MIN_DATE_PARAMETER,
		        "enrolledOnOrBefore", "${endDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: HIV visits_"), h.parameterMap("onOrBefore", "${startDate}", "onOrAfter",
		        MIN_DATE_PARAMETER)));
		ccd.getSearches().put(
		    "3",
		    new Mapped(h.cohortDefinition("xsite: HIV visits_"), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter",
		        "${startDate}")));
		ccd.setCompositionString("1 AND 3 AND NOT 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q2
		i = h.newCountIndicator("xsite: HIV-Q2 (Pre-ART, ART in period)", "xsite: HIV with first visits in period_", h
		        .parameterMap("startDate", "${startDate}", "endDate", "${endDate}"));
		addDimensionColumn(rd, "hivq2", "Enrolled in HIV care during the quarter", i, null);
		
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("xsite: In state inner_");
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		iscd.addParameter(new Parameter("states", "states", ProgramWorkflowState.class));
		h.replaceCohortDefinition(iscd);
		 ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: In state_");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.addParameter(new Parameter("states", "states", ProgramWorkflowState.class));
		ccd.getSearches().put(
			    "2",
			    new Mapped(iscd, h.parameterMap("onDate", "${onDate}", "states",
			        "${states}")));
		ccd.getSearches().put(
			    "3",
			    new Mapped(hcc, h.parameterMap()));
		ccd.getSearches().put(
			    "1",
			    new Mapped(arv, h.parameterMap()));
		ccd.setCompositionString("2 AND (3 OR 1)");
		h.replaceCohortDefinition(ccd);

		
		// hiv q3
		i = h.newCountIndicator("xsite: HIV-Q3 (On Pre-ART)", "xsite: In state_", h.parameterMap("onDate", "${endDate}",
		    "states", STATE_FOLLOWING));
		addDimensionColumn(rd, "hivq3", "HIV patients who have not started ART", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Pre-ART without any visits_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_FOLLOWING)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Pre-ART visits_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND NOT 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q3b
		i = h.newCountIndicator("xsite: HIV-Q3b (On Pre-ART)", "xsite: Pre-ART without any visits_", h.parameterMap(
		    "startDate", MIN_DATE_PARAMETER, "endDate", "${endDate}"));
		addDimensionColumn(rd, "hivq3b", "HIV patients who have not started ART without any visit", i, null);
		
		// hiv q3c
		i = h.newCountIndicator("xsite: HIV-Q3c (On Pre-ART)", "xsite: Pre-ART without any visits_", h.parameterMap(
		    "startDate", "${endDate-24m}", "endDate", "${endDate}"));
		addDimensionColumn(rd, "hivq3c", "HIV patients who have not started ART without visits in last 24 months", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active Pre-ART visits_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_FOLLOWING)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Pre-ART visits_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q4
		i = h.newCountIndicator("xsite: HIV-Q4 (Active Pre-ART visits_)", "xsite: Active Pre-ART visits_", h.parameterMap(
		    "endDate", "${endDate}", "startDate", "${endDate-6m}"));
		addDimensionColumn(rd, "hivq4", "Patients with a visit recorded in the last 6 months", i, null);
		
		// hiv q4b
		i = h.newCountIndicator("xsite: HIV-Q4 (Active Pre-ART visits_) in the last 12 months",
		    "xsite: Active Pre-ART visits_", h.parameterMap("endDate", "${endDate}", "startDate", "${endDate-12m}"));
		addDimensionColumn(rd, "hivq4b", "Patients with a visit recorded in the last 12 months", i, null);
		
		// hiv q4c
		i = h.newCountIndicator("xsite: HIV-Q4 (Active Pre-ART visits_) in the last 18 months",
		    "xsite: Active Pre-ART visits_", h.parameterMap("endDate", "${endDate}", "startDate", "${endDate-18m}"));
		addDimensionColumn(rd, "hivq4c", "Patients with a visit recorded in the last 18 months", i, null);
		
		// hiv q4d
		i = h.newCountIndicator("xsite: HIV-Q4 (Active Pre-ART visits_) in the last 24 months",
		    "xsite: Active Pre-ART visits_", h.parameterMap("endDate", "${endDate}", "startDate", "${endDate-24m}"));
		addDimensionColumn(rd, "hivq4d", "Patients with a visit recorded in the last 24 months", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Without active Pre-ART visits_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_FOLLOWING)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Pre-ART visits_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND NOT 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q4e
		i = h.newCountIndicator("xsite: HIV-Q4b (Without active Pre-ART visits_)",
		    "xsite: Without active Pre-ART visits_", h.parameterMap("endDate", "${endDate}", "startDate", "${endDate-6m}"));
		addDimensionColumn(rd, "hivq4b", "Patients without a visit recorded in the last six months", i, null);
		
		iscd = new InStateCohortDefinition();
		iscd.setName("xsite: Having state in period_");
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		iscd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		iscd.addParameter(new Parameter("states", "states", ProgramWorkflowState.class));
		h.replaceCohortDefinition(iscd);
		
		// hiv q5
		i = h.newCountIndicator("xsite: HIV-Q5 (Ever on ART)", "xsite: Having state in period_", h.parameterMap(
		    "onOrAfter", MIN_DATE_PARAMETER, "onOrBefore", "${endDate}", "states", STATE_ON_ANTIRETROVIRALS));
		addDimensionColumn(rd, "hivq5", "Ever on ART", i, null);
		
		// hiv q6
		i = h.newCountIndicator("xsite: HIV-Q6 (On ART)", "xsite: In state_", h.parameterMap("onDate", "${endDate}",
		    "states", STATE_ON_ANTIRETROVIRALS));
		addDimensionColumn(rd, "hivq6", "ART patients who do not have a final program exit", i, null);
		
		ecd = new EncounterCohortDefinition();
		ecd.setName("xsite: ART visits_");
		ecd.setEncounterTypeList(Arrays.asList(ENCOUNTER_TYPE_ART_INITIAL, ENCOUNTER_TYPE_ART_FOLLOWUP));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		h.replaceCohortDefinition(ecd);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active ART visits_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_ON_ANTIRETROVIRALS)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: ART visits_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q7
		i = h.newCountIndicator("xsite: HIV-Q7 (Active ART visits_)", "xsite: Active ART visits_", h.parameterMap(
		    "endDate", "${endDate}", "startDate", "${endDate-3m}"));
		addDimensionColumn(rd, "hivq7", "Patients with a visit recorded in the three months", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Without active ART visits_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_ON_ANTIRETROVIRALS)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: ART visits_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND NOT 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q7b
		i = h.newCountIndicator("xsite: HIV-Q7b (Active ART visits_)", "xsite: Without active ART visits_", h
		        .parameterMap("endDate", "${endDate}", "startDate", "${endDate-3m}"));
		addDimensionColumn(rd, "hivq7b", "Patients without a visit recorded in the three months", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: ART with first visits in period_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: HIV program_"), h.parameterMap("enrolledOnOrAfter", MIN_DATE_PARAMETER,
		        "enrolledOnOrBefore", "${endDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: ART visits_"), h.parameterMap("onOrBefore", "${startDate}", "onOrAfter",
		        MIN_DATE_PARAMETER)));
		ccd.getSearches().put(
		    "3",
		    new Mapped(h.cohortDefinition("xsite: ART visits_"), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter",
		        "${startDate}")));
		ccd.setCompositionString("1 AND 3 AND NOT 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q8
		i = h.newCountIndicator("xsite: HIV-Q8 (Started On ART)", "xsite: ART with first visits in period_", h
		        .parameterMap("startDate", "${startDate}", "endDate", "${endDate}"));
		addDimensionColumn(rd, "hivq8", "Initiated ART during the quarter", i, null);
		
		NumericObsCohortDefinition nocd = new NumericObsCohortDefinition();
		nocd.setName("xsite: Total CD4 Count recorded 1_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(CONCEPT_CD4_COUNT);
		nocd.setEncounterTypeList(Arrays.asList(ENCOUNTER_TYPE_PART_INITIAL, ENCOUNTER_TYPE_PART_FOLLOWUP,
		    ENCOUNTER_TYPE_ART_INITIAL, ENCOUNTER_TYPE_ART_FOLLOWUP));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		
		 nocd = new NumericObsCohortDefinition();
		nocd.setName("xsite: Total CD4 Count recorded 2_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(CONCEPT_CD4_COUNT_MASTERCARD);
		nocd.setEncounterTypeList(Arrays.asList(ENCOUNTER_TYPE_PART_INITIAL, ENCOUNTER_TYPE_PART_FOLLOWUP,
		    ENCOUNTER_TYPE_ART_INITIAL, ENCOUNTER_TYPE_ART_FOLLOWUP));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Total CD4 Count recorded_");
		ccd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ccd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: Total CD4 Count recorded 1_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Total CD4 Count recorded 2_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		ccd.setCompositionString("1 OR 2");
		h.replaceCohortDefinition(ccd);

		nocd = new NumericObsCohortDefinition();
		nocd.setName("xsite: Total CD4 Count measured_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(CONCEPT_CD4_COUNT);
		nocd.setEncounterTypeList(Arrays.asList(ENCOUNTER_TYPE_LAB));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active Pre-ART CD4 count_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_FOLLOWING)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Total CD4 Count recorded_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q9
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${endDate-6m}"));
		addDimensionColumn(rd, "hivq9", "CD4 count recorded in the last two quarters", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active Pre-ART CD4 count measured_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_FOLLOWING)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Total CD4 Count measured_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q9b
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${endDate-6m}"));
		addDimensionColumn(rd, "hivq9b", "CD4 count measured in the last two quarters", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active ART CD4 count_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_ON_ANTIRETROVIRALS)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Total CD4 Count recorded_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q10
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${endDate-6m}"));
		addDimensionColumn(rd, "hivq10", "CD4 count recorded in the last two quarters", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active ART CD4 count measured_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: In state_"), h.parameterMap("onDate", "${endDate}", "states",
		        STATE_ON_ANTIRETROVIRALS)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Total CD4 Count measured_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q10b
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${endDate-6m}"));
		addDimensionColumn(rd, "hivq10b", "CD4 count measured in the last two quarters", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Pre-ART or ART visits_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: Pre-ART visits_"), h.parameterMap("onOrBefore", "${endDate}",
		        "onOrAfter", "${startDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: ART visits_"), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter",
		        "${startDate}")));
		ccd.setCompositionString("1 OR 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q11 denominator
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${startDate}"));
		addDimensionColumn(rd, "hivq11_den", "Patients with HIV visits in period", i, null);
		
		nocd = new NumericObsCohortDefinition();
		nocd.setName("xsite: Weight available_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(CONCEPT_WEIGHT);
		nocd.setEncounterTypeList(Arrays.asList(ENCOUNTER_TYPE_ART_INITIAL, ENCOUNTER_TYPE_ART_FOLLOWUP,
		    ENCOUNTER_TYPE_PART_INITIAL, ENCOUNTER_TYPE_PART_FOLLOWUP));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active HIV weight_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: Pre-ART or ART visits_"), h.parameterMap("endDate", "${endDate}",
		        "startDate", "${startDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Weight available_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q11
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${startDate}"));
		addDimensionColumn(rd, "hivq11", "Patients with weight recorded", i, null);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Active HIV without weight_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: Pre-ART or ART visits_"), h.parameterMap("endDate", "${endDate}",
		        "startDate", "${startDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Weight available_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND NOT 2");
		h.replaceCohortDefinition(ccd);
		
		// hiv q11b
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${startDate}"));
		addDimensionColumn(rd, "hivq11b", "Patients without weight recorded", i, null);
		
		createEid(rd);
	}
	
	private void createEid(PeriodIndicatorReportDefinition rd) {
		BirthAndDeathCohortDefinition badcd = new BirthAndDeathCohortDefinition();
		badcd.setName("xsite: Birth date in period_");
		badcd.addParameter(new Parameter("bornOnOrAfter", "bornOnOrAfter", Date.class));
		badcd.addParameter(new Parameter("bornOnOrBefore", "bornOnOrBefore", Date.class));
		h.replaceCohortDefinition(badcd);
		
		ProgramEnrollmentCohortDefinition pecd = new ProgramEnrollmentCohortDefinition();
		pecd.setName("xsite: EID program_");
		pecd.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		pecd.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		pecd.setPrograms(Arrays.asList(PROGRAM_EID_PROGRAM));
		h.replaceCohortDefinition(pecd);
		
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: Infants turned 13 weeks during review quarter_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: EID program_"), h.parameterMap("enrolledOnOrBefore", "${endDate}",
		        "enrolledOnOrAfter", MIN_DATE_PARAMETER)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Birth date in period_"), h.parameterMap("bornOnOrBefore",
		        "${endDate-13w}", "bornOnOrAfter", "${startDate-13w}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// eid q1-den
		CohortIndicator i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}",
		    "startDate", "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "eidq1_den",
		    "Infants who turned thirteen weeks old during the review quarter", i, null);
		
		//		NumericObsCohortDefinition nocd = new NumericObsCohortDefinition();
		//		nocd.setName("xsite: DNA-PCR available_");
		//		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		//		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		//		nocd.setQuestion(CONCEPT_DNA_PCR);
		//		nocd.setTimeModifier(TimeModifier.ANY);
		//		h.replaceCohortDefinition(nocd);
		
		CodedObsCohortDefinition cocd = new CodedObsCohortDefinition();
		cocd.setName("xsite: DNA-PCR result available_");
		cocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cocd.setQuestion(CONCEPT_HIV_DNA_PCR);
		cocd.setTimeModifier(TimeModifier.ANY);
		cocd.setOperator(SetComparator.IN);
		List<Concept> concepts = new ArrayList<Concept>();
		concepts.add(CONCEPT_ID_DNA_PCR_INDETERMINATE); //"INDETERMINATE"));
		concepts.add(CONCEPT_ID_DNA_PCR_POSITIVE); //"POSITIVE"));
		concepts.add(CONCEPT_ID_DNA_PCR_NEGATIVE); //"NEGATIVE"));
		cocd.setValueList(concepts);
		h.replaceCohortDefinition(cocd);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("xsite: EID with DNA-PCR result available_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("xsite: EID program_"), h.parameterMap("enrolledOnOrBefore", "${endDate}",
		        "enrolledOnOrAfter", MIN_DATE_PARAMETER)));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("xsite: Birth date in period_"), h.parameterMap("bornOnOrBefore",
		        "${endDate-13w}", "bornOnOrAfter", "${startDate-13w}")));
		ccd.getSearches().put(
		    "3",
		    new Mapped(h.cohortDefinition("xsite: DNA-PCR result available_"), h.parameterMap("onOrBefore", "${endDate}",
		        "onOrAfter", MIN_DATE_PARAMETER)));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		
		// eid q1
		i = h.newCountIndicator(ccd.getName(), ccd.getName(), h.parameterMap("endDate", "${endDate}", "startDate",
		    "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "eidq1",
		    "Infants who turned thirteen weeks during the review quarter with DNA PCR", i, null);
	}
	
	private void addDimensionColumn(PeriodIndicatorReportDefinition rd, String key, String displayName, CohortIndicator i,
	                                Object object) {
		PeriodIndicatorReportUtil.addColumn(rd, key + "_a", displayName + " (Adult)", i, h.hashMap("Age", "Adult"));
		PeriodIndicatorReportUtil.addColumn(rd, key + "_c", displayName + " (Child)", i, h.hashMap("Age", "Child"));
		//		PeriodIndicatorReportUtil.addColumn(rd, key + "-?", displayName + " (?)", i, h.hashMap("Age", "?"));
		PeriodIndicatorReportUtil.addColumn(rd, key + "_all", displayName + " (all)", i, null);
	}
}
