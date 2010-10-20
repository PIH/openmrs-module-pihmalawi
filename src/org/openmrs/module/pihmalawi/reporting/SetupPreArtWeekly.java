package org.openmrs.module.pihmalawi.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;

public class SetupPreArtWeekly {
	
	Helper h = new Helper();
	
	public SetupPreArtWeekly(Helper helper) {
		h = helper;
	}
	
	public void setup(boolean b) throws Exception {
		//		deleteReportElements();
		
		// one for the heck of it and the show
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("artvst: On ART (appt)_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM").getWorkflowByName("TREATMENT STATUS")
		        .getStateByName("ON ANTIRETROVIRALS"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		h.newCountIndicator("artvst: On ART (appt)_", "artvst: On ART (appt)_", "onDate=${endDate}");

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions((PeriodIndicatorReportDefinition) h.findDefinition(PeriodIndicatorReportDefinition.class, "Pre-ART Weekly_"));
		//		createIndicators();
		//		createDimensions();
		//		createArtOverviewExcelDesign(rd);
		//		//		//			createSimplePatientDesign(rd);
		//		ReportDesign rdes = createApzuPatientDesign(rd);
		//		h.render(rdes, rd);
		//		return rd;
	}
	
	public void deleteReportElements() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Pre-ART Weekly_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		// todo, also purge internal dataset for ART Appointments_
		h.purgeDefinition(PeriodIndicatorReportDefinition.class, "Pre-ART Weekly_");
		h.purgeDefinition(DataSetDefinition.class, "Pre-ART Weekly_ Data Set");
		
	}
	
	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		
//		rd.addDimension("Location", h.cohortDefinitionDimension("ART program location_"));
//		rd.setBaseCohortDefinition(h.cohortDefinition(cohort), ParameterizableUtil
//		        .createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		rd.setName("Pre-ART Weekly_");
//		rd.setupDataSetDefinition();
//		PeriodIndicatorReportUtil.addColumn(rd, "ndh", " at Neno", h
//	        .cohortIndicator("artvst: On ART (appt)_"), null);

		h.replaceReportDefinition(rd);
		return rd;
	}
	
	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		genericCohortDefinitions();
		
		// on pre-art on date at location
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("part: Pre-ART on date at location_");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("part: In state on date at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING"), "onDate", "${onDate}", "location", "${location}")));
		ccd.setCompositionString("1");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "part", ccd, h.parameterMap("onDate", "${endDate}"));
		
		// new on pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: New on Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("part: State change at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING"), "startedOnOrAfter", "${startedOnOrAfter}",
		        "startedOnOrBefore", "${startedOnOrBefore}", "location", "${location}")));
		ccd.setCompositionString("1");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "new", ccd, h.parameterMap("startedOnOrBefore", "${endDate}", "startedOnOrAfter", "${startDate}"));

		// died from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Died from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("part: In state on date at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "PATIENT DIED"), "onDate", "${startedOnOrBefore}", "location",
		        "${location}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("part: Not having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.getSearches().put(
		    "3",
		    new Mapped(h.cohortDefinition("part: Having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "died", ccd, h.parameterMap("startedOnOrBefore", "${endDate}", "startedOnOrAfter", "${startDate}"));
		
		// HIV negativ from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: HIV negativ from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("part: In state on date at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "PATIENT HIV NEGATIVE"), "onDate", "${startedOnOrBefore}", "location",
		        "${location}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("part: Not having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.getSearches().put(
		    "3",
		    new Mapped(h.cohortDefinition("part: Having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "neg", ccd, h.parameterMap("startedOnOrBefore", "${endDate}", "startedOnOrAfter", "${startDate}"));
		
		// transfered out from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Transferred out from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("part: In state on date at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "PATIENT TRANSFERRED OUT"), "onDate", "${startedOnOrBefore}", "location",
		        "${location}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("part: Not having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.getSearches().put(
		    "3",
		    new Mapped(h.cohortDefinition("part: Having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "trans", ccd, h.parameterMap("startedOnOrBefore", "${endDate}", "startedOnOrAfter", "${startDate}"));
		
		// treatment stopped from pre-art on date at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: Treatment stopped from Pre-ART at location_");
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("part: In state on date at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "TREATMENT STOPPED"), "onDate", "${startedOnOrBefore}", "location",
		        "${location}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("part: Not having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.getSearches().put(
		    "3",
		    new Mapped(h.cohortDefinition("part: Having state at location_"), h.parameterMap("state", h.workflowState(
		        "HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING"), "startedOnOrBefore", "${startedOnOrBefore}",
		        "startedOnOrAfter", "${startedOnOrAfter}", "location", "${location}")));
		ccd.setCompositionString("1 AND 2 AND 3");
		h.replaceCohortDefinition(ccd);
		newCountIndicator(rd, "stp", ccd, h.parameterMap("startedOnOrBefore", "${endDate}", "startedOnOrAfter", "${startDate}"));
		
		NumericObsCohortDefinition nocd = new NumericObsCohortDefinition();
		nocd.setName("part: CD4 Count available_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName("CD4 COUNT"));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		CohortIndicator i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter", "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "cd4", nocd.getName(), i, null);
		
		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: CD4% available_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName("CD4 PERCENT"));
		nocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(nocd);
		 i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter", "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "cd4%", nocd.getName(), i, null);
		
		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: CD4 Count >500_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName("CD4 COUNT"));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setOperator1(RangeComparator.GREATER_THAN);
		nocd.setValue1(500.0);
		h.replaceCohortDefinition(nocd);
		 i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter", "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "cd4hig", nocd.getName(), i, null);
		
		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: CD4 Count <250_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName("CD4 COUNT"));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setOperator1(RangeComparator.LESS_THAN);
		nocd.setValue1(250.0);
		h.replaceCohortDefinition(nocd);
		 i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter", "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "cd4low", nocd.getName(), i, null);
		
		nocd = new NumericObsCohortDefinition();
		nocd.setName("part: CD4 Count >=250 and <=500_");
		nocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		nocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		nocd.setQuestion(Context.getConceptService().getConceptByName("CD4 COUNT"));
		nocd.setTimeModifier(TimeModifier.LAST);
		nocd.setOperator1(RangeComparator.GREATER_EQUAL);
		nocd.setValue1(250.0);
		nocd.setOperator2(RangeComparator.LESS_EQUAL);
		nocd.setValue2(500.0);
		h.replaceCohortDefinition(nocd);
		 i = h.newCountIndicator(nocd.getName(), nocd.getName(), h.parameterMap("onOrBefore", "${endDate}", "onOrAfter", "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "cd4med", nocd.getName(), i, null);
		
	}
	
	private void newCountIndicator(PeriodIndicatorReportDefinition rd, String indicatorKey, CohortDefinition nocd, Map<String, Object> parameterMap) {
		parameterMap.put("location", h.location("Neno District Hospital"));
		CohortIndicator i = h.newCountIndicator(nocd.getName() + " (Neno)", nocd.getName(), parameterMap);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", nocd.getName(), i, null);

		parameterMap.put("location", h.location("Nsambe HC"));
		 i = h.newCountIndicator(nocd.getName() + " (Nsambe)", nocd.getName(), parameterMap);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", nocd.getName(), i, null);
		
		parameterMap.put("location", h.location("Magaleta HC"));
		 i = h.newCountIndicator(nocd.getName() + " (Magaleta)", nocd.getName(), parameterMap);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", nocd.getName(), i, null);
	}
	
	private void genericCohortDefinitions() {
		// generic
		// In state on date at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("part: In state on date at location_");
		islcd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		// In state at location
		islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("part: In state at location_");
		islcd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		islcd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		// having state at location
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName("part: Having state at location_");
		pscd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		
		// not having state at location
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("part: Not having state at location_");
		ccd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd
		        .getSearches()
		        .put(
		            "1",
		            new Mapped(
		                    h.cohortDefinition("part: Having state at location_"),
		                    ParameterizableUtil
		                            .createParameterMappings("state=${state},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd.setCompositionString("NOT 1");
		h.replaceCohortDefinition(ccd);
		
		// state change at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: State change at location_");
		ccd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd
		        .getSearches()
		        .put(
		            "1",
		            new Mapped(
		                    h.cohortDefinition("part: Having state at location_"),
		                    ParameterizableUtil
		                            .createParameterMappings("state=${state},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("part: In state on date at location_"), ParameterizableUtil
		            .createParameterMappings("state=${state},onDate=${startedOnOrAfter-1d},location=${location}")));
		ccd.setCompositionString("1 AND (NOT 2)");
		h.replaceCohortDefinition(ccd);
		
		// new in state at location
		ccd = new CompositionCohortDefinition();
		ccd.setName("part: New in state at location_");
		ccd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
		ccd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd
		        .getSearches()
		        .put(
		            "1",
		            new Mapped(
		                    h.cohortDefinition("part: Having state at location_"),
		                    ParameterizableUtil
		                            .createParameterMappings("state=${fromState},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd
		        .getSearches()
		        .put(
		            "2",
		            new Mapped(
		                    h.cohortDefinition("part: Having state at location_"),
		                    ParameterizableUtil
		                            .createParameterMappings("state=${toState},startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore},location=${location}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
	}
	
}