package org.openmrs.module.pihmalawi.reporting;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.CreateInitialDataSet;
import org.springframework.test.annotation.Rollback;

public class ReportingSetupHivIndicators {
	
	Helper h = new Helper();
	
	public ReportingSetupHivIndicators(Helper helper) {
		h = helper;
	}

	public void setupHivWeekly(boolean b) throws Exception {
		try {
			// Enrolled in program at location before end date
			SqlCohortDefinition scd = new SqlCohortDefinition();
			scd.setName("Enrolled in program at location before end date_");
			scd
			        .setQuery("SELECT p.patient_id FROM patient p "
			                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND pp.location_id = :location"
			                + " WHERE p.voided = 0 GROUP BY p.patient_id");
			scd.addParameter(new Parameter("endDate", "End Date", Date.class));
			scd.addParameter(new Parameter("startDate", "Start date", Location.class));
			scd.addParameter(new Parameter("program", "Program", Program.class));
			h.replaceCohortDefinition(scd);
			
			// Enrolled in program at location as of end date
			scd = new SqlCohortDefinition();
			scd
			        .setQuery("SELECT p.patient_id FROM patient p"
			                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND (pp.date_completed IS NULL OR pp.date_completed > :endDate) AND pp.location_id = :location"
			                + " WHERE p.voided = 0 GROUP BY p.patient_id");
			scd.setName("Enrolled in program at location as of end date_");
			scd.addParameter(new Parameter("endDate", "End Date", Date.class));
			scd.addParameter(new Parameter("location", "Location", Location.class));
			scd.addParameter(new Parameter("program", "Program", Program.class));
			h.replaceCohortDefinition(scd);
			
			// On ART before end date
			PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
			pscd.setName("On ART before end date_");
			pscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
			pscd.addParameter(new Parameter("startedOnOrBefore", "endDate", Date.class));
			h.replaceCohortDefinition(pscd);
			
			// Has state before end date
			InStateCohortDefinition iscd = new InStateCohortDefinition();
			iscd.setName("Has state before end date_");
			iscd.addParameter(new Parameter("states", "State", ProgramWorkflowState.class));
			iscd.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// Started ART during period
			pscd = new PatientStateCohortDefinition();
			pscd.setName("Started ART during period_");
			pscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
			pscd.addParameter(new Parameter("startedOnOrAfter", "startDate", Date.class));
			pscd.addParameter(new Parameter("startedOnOrBefore", "endDate", Date.class));
			h.replaceCohortDefinition(pscd);
			
			// On ART at end of period
			iscd = new InStateCohortDefinition();
			iscd.setName("On ART at end of period_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// HIV following at end of period
			iscd = new InStateCohortDefinition();
			iscd.setName("HIV Following at end of period_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// On ART before end date && has state before end date
			CompositionCohortDefinition ccd = new CompositionCohortDefinition();
			ccd.setName("On ART before end date && has state before end date_");
			ccd.setCompositionString("1 AND 2");
			ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
			ccd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
			ccd.addSearch("2", h.cohortDefinition("Has state before end date_"), ParameterizableUtil
			        .createParameterMappings("onOrBefore=${endDate},states=${state}"));
			ccd.addSearch("1", h.cohortDefinition("On ART before end date_"), ParameterizableUtil
			        .createParameterMappings("startedOnOrBefore=${endDate}"));
			h.replaceCohortDefinition(ccd);
			
			// -----------------------------------------------------
			// indicators
			
			// Started ART during period
			h.newCountIndicator("Started ART during period_", "Started ART during period_",
			    "startedOnOrAfter=${startDate},startedOnOrBefore=${endDate}");
			h.newCountIndicator("Started ART week before period_", "Started ART during period_",
			    "startedOnOrAfter=${startDate-1w},startedOnOrBefore=${endDate-1w}");
			h.newCountIndicator("Started ART two weeks before period_", "Started ART during period_",
			    "startedOnOrAfter=${startDate-2w},startedOnOrBefore=${endDate-2w}");
			
			// On ART
			h.newCountIndicator("On ART at end of period_", "On ART at end of period_", "onDate=${endDate}");
			h.newCountIndicator("On ART week before period_", "On ART at end of period_", "onDate=${endDate-1w}");
			h.newCountIndicator("On ART two weeks before period_", "On ART at end of period_", "onDate=${endDate-2w}");
			
			// Died before end date after starting ART
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("endDate", "${endDate}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DIED"));
			CohortIndicator i = CohortIndicator.newCountIndicator("Died before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			// Died week before period after starting ART
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-1w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DIED"));
			i = CohortIndicator.newCountIndicator("Died week before period after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			// Died two weeks before period after starting ART
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-2w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DIED"));
			i = CohortIndicator.newCountIndicator("Died two weeks before period after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			// Defaulted before end date after starting ART
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DEFAULTED"));
			i = CohortIndicator.newCountIndicator("Defaulted before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-1w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DEFAULTED"));
			i = CohortIndicator.newCountIndicator("Defaulted week before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-2w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DEFAULTED"));
			i = CohortIndicator.newCountIndicator("Defaulted two weeks before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			// Treatment stopped before end date after starting ART
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "TREATMENT STOPPED"));
			i = CohortIndicator.newCountIndicator("Treatment stopped before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-1w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "TREATMENT STOPPED"));
			i = CohortIndicator.newCountIndicator("Treatment stopped week before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-2w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "TREATMENT STOPPED"));
			i = CohortIndicator.newCountIndicator("Treatment stopped two weeks before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			// Transferred out before end date after starting ART
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT TRANSFERRED OUT"));
			i = CohortIndicator.newCountIndicator("Transferred out before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-1w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT TRANSFERRED OUT"));
			i = CohortIndicator.newCountIndicator("Transferred out week before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			m = new HashMap<String, Object>();
			m.put("endDate", "${endDate-2w}");
			m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT TRANSFERRED OUT"));
			i = CohortIndicator.newCountIndicator("Transferred out two weeks before end date after starting ART_",
			    new Mapped<CohortDefinition>(h.cohortDefinition("On ART before end date && has state before end date_"), m),
			    null);
			h.addDefaultIndicatorParameter(i);
			h.replaceDefinition(i);
			
			// --------------------------------------------------------------
			// dimensions
			
			/*
			// hiv program location
			CohortDefinitionDimension md = new CohortDefinitionDimension();
			md.setName("HIV program location_");
			md.addParameter(new Parameter("endDate", "End Date", Date.class));
			// todo, why are location and startdate for me mandatory?
			md.addParameter(new Parameter("startDate", "Start Date", Date.class));
			md.addParameter(new Parameter("location", "Location", Location.class));
			Map<String, Object> m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Neno District Hospital"));
			md.addCohortDefinition("Neno", h.cohortDefinition("Enrolled in program at location as of end date_"), m2);
			m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Nsambe HC"));
			md.addCohortDefinition("Nsambe", h.cohortDefinition("Enrolled in program at location as of end date_"), m2);
			m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Magaleta HC"));
			md.addCohortDefinition("Magaleta", h.cohortDefinition("Enrolled in program at location as of end date_"), m2);
			h.replaceDefinition(md);
			*/

			// -------------------------------------------------------
			// reports
			// hiv weekly report
			PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
			rd.setName("HIV Weekly Report_");
			rd.addDimension("Location", h.cohortDefinitionDimension("HIV program location_"));
			
			addColumnForLocations(rd, "Started ART in period", "Started ART during period_", "ini");
			addColumnForLocations(rd, "Started ART last week", "Started ART week before period_", "ini1");
			addColumnForLocations(rd, "Started ART two weeks ago", "Started ART two weeks before period_", "ini2");
			
			addColumnForLocations(rd, "On ART at end of period", "On ART at end of period_", "art");
			addColumnForLocations(rd, "On ART week before period", "On ART week before period_", "art1");
			addColumnForLocations(rd, "On ART two weeks before period", "On ART two weeks before period_", "art2");
			
			addColumnForLocations(rd, "Died before end date", "Died before end date after starting ART_", "died");
			addColumnForLocations(rd, "Died week before period", "Died week before period after starting ART_", "died1");
			addColumnForLocations(rd, "Died two weeks before", "Died two weeks before period after starting ART_", "died2");
			
			addColumnForLocations(rd, "Defaulted before end date", "Defaulted before end date after starting ART_", "def");
			addColumnForLocations(rd, "Defaulted week before end date", "Defaulted week before end date after starting ART_", "def1");
			addColumnForLocations(rd, "Defaulted two weeks before end date",
			    "Defaulted two weeks before end date after starting ART_", "def2");
			
			addColumnForLocations(rd, "Treatment stopped before end date",
			    "Treatment stopped before end date after starting ART_", "stp");
			addColumnForLocations(rd, "Treatment stopped week before end date",
			    "Treatment stopped week before end date after starting ART_", "stp1");
			addColumnForLocations(rd, "Treatment stopped two weeks before end date",
			    "Treatment stopped two weeks before end date after starting ART_", "stp2");
			
			addColumnForLocations(rd, "Transferred out before end date",
			    "Transferred out before end date after starting ART_", "tra");
			addColumnForLocations(rd, "Transferred out week before end date",
			    "Transferred out week before end date after starting ART_", "tra1");
			addColumnForLocations(rd, "Transferred out two weeks before end date",
			    "Transferred out two weeks before end date after starting ART_", "tra2");
			
			h.replaceReportDefinition(rd);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addColumnForLocations(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator, String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Nsambe"));
	}
	
}
