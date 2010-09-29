package org.openmrs.module.pihmalawi.reporting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
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
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.CreateInitialDataSet;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.test.annotation.Rollback;

public class ReportingSetupHivWeekly {
	
	Helper h = new Helper();
	
	public ReportingSetupHivWeekly(Helper helper) {
		h = helper;
	}
	
	public void setupHivWeekly(boolean b) throws Exception {
		try {
			// In state
			InStateCohortDefinition iscd = new InStateCohortDefinition();
			iscd.setName("In state_");
			iscd.addParameter(new Parameter("states", "State", ProgramWorkflowState.class));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// On ART
			iscd = new InStateCohortDefinition();
			iscd.setName("On ART_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// Defaulted
			iscd = new InStateCohortDefinition();
			iscd.setName("Defaulted_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DEFAULTED")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// Following
			iscd = new InStateCohortDefinition();
			iscd.setName("Treatment stopped_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "TREATMENT STOPPED")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// Lost to followup
			iscd = new InStateCohortDefinition();
			iscd.setName("Lost to followup_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "LOST TO FOLLOWUP")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// Transferred out
			iscd = new InStateCohortDefinition();
			iscd.setName("Transferred out_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT TRANSFERRED OUT")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// Died
			iscd = new InStateCohortDefinition();
			iscd.setName("Died_");
			iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DIED")));
			iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
			h.replaceCohortDefinition(iscd);
			
			// Started ART during period
			PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
			pscd.setName("Started ART_");
			pscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
			pscd.addParameter(new Parameter("startedOnOrBefore", "endDate", Date.class));
			h.replaceCohortDefinition(pscd);
			
			// On ART before end date
			pscd = new PatientStateCohortDefinition();
			pscd.setName("Ever On ART_");
			pscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
			pscd.addParameter(new Parameter("startedOnOrBefore", "endDate", Date.class));
			h.replaceCohortDefinition(pscd);
			
			// Ever enrolled in program at location as of end date
			SqlCohortDefinition scd = new SqlCohortDefinition();
			scd
			        .setQuery("SELECT p.patient_id FROM patient p"
			                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND pp.location_id = :location"
			                + " WHERE p.voided = 0 GROUP BY p.patient_id");
			scd.setName("Ever enrolled in program at location_");
			scd.addParameter(new Parameter("endDate", "End Date", Date.class));
			scd.addParameter(new Parameter("location", "Location", Location.class));
			scd.addParameter(new Parameter("program", "Program", Program.class));
			h.replaceCohortDefinition(scd);
			
			// Enrolled in program at location as of end date
			 scd = new SqlCohortDefinition();
			scd
			        .setQuery("SELECT p.patient_id FROM patient p"
			                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND (pp.date_completed IS NULL OR pp.date_completed > :endDate) AND pp.location_id = :location"
			                + " WHERE p.voided = 0 GROUP BY p.patient_id");
			scd.setName("Enrolled in program_");
			scd.addParameter(new Parameter("endDate", "End Date", Date.class));
			scd.addParameter(new Parameter("location", "Location", Location.class));
			scd.addParameter(new Parameter("program", "Program", Program.class));
			h.replaceCohortDefinition(scd);
			
			// State in program at location as of end date
			 scd = new SqlCohortDefinition();
			scd
			        .setQuery("SELECT p.patient_id FROM patient p"
			                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND pp.location_id = :location"
			                + " WHERE p.voided = 0 GROUP BY p.patient_id");
			scd.setName("Enrolled in program_");
			scd.addParameter(new Parameter("endDate", "End Date", Date.class));
			scd.addParameter(new Parameter("location", "Location", Location.class));
			scd.addParameter(new Parameter("program", "Program", Program.class));
			h.replaceCohortDefinition(scd);
			
			// -----------------------------------------------------
			// indicators
			
			h.newCountIndicator("On ART_", "On ART_", "onDate=${endDate}");
			h.newCountIndicator("On ART 1 week ago_", "On ART_", "onDate=${endDate-1w}");
			h.newCountIndicator("On ART 2 weeks ago_", "On ART_", "onDate=${endDate-2w}");
			
			h.newCountIndicator("Died_", "Died_", "onDate=${endDate}");
			h.newCountIndicator("Died 1 week ago_", "Died_", "onDate=${endDate-1w}");
			h.newCountIndicator("Died 2 weeks ago_", "Died_", "onDate=${endDate-2w}");
			
			h.newCountIndicator("Defaulted_", "Defaulted_", "onDate=${endDate}");
			h.newCountIndicator("Defaulted 1 week ago_", "Defaulted_", "onDate=${endDate-1w}");
			h.newCountIndicator("Defaulted 2 weeks ago_", "Defaulted_", "onDate=${endDate-2w}");
			
			h.newCountIndicator("Transferred out_", "Transferred out_", "onDate=${endDate}");
			h.newCountIndicator("Transferred out 1 week ago_", "Transferred out_", "onDate=${endDate-1w}");
			h.newCountIndicator("Transferred out 2 weeks ago_", "Transferred out_", "onDate=${endDate-2w}");
			
			h.newCountIndicator("Treatment stopped_", "Treatment stopped_", "onDate=${endDate}");
			h.newCountIndicator("Treatment stopped 1 week ago_", "Treatment stopped_", "onDate=${endDate-1w}");
			h.newCountIndicator("Treatment stopped 2 weeks ago_", "Treatment stopped_", "onDate=${endDate-2w}");
			
			h.newCountIndicator("Lost to followup_", "Lost to followup_", "onDate=${endDate}");
			h.newCountIndicator("Lost to followup 1 week ago_", "Lost to followup_", "onDate=${endDate-1w}");
			h.newCountIndicator("Lost to followup 2 weeks ago_", "Lost to followup_", "onDate=${endDate-2w}");
			
			h.newCountIndicator("Died_", "Died_", "onDate=${endDate}");
			h.newCountIndicator("Died 1 week ago_", "Died_", "onDate=${endDate-1w}");
			h.newCountIndicator("Died 2 weeks ago_", "Died_", "onDate=${endDate-2w}");
			
			h.newCountIndicator("Ever on ART_", "Ever on ART_", "startedOnOrBefore=${endDate}");
			h.newCountIndicator("Ever on ART 1 week ago_", "Ever on ART_", "startedOnOrBefore=${endDate-1w}");
			h.newCountIndicator("Ever on ART 2 weeks ago_", "Ever on ART_", "startedOnOrBefore=${endDate-2w}");
			
			h.newCountIndicator("Started ART_", "Started ART_",
			    "startedOnOrAfter=${endDate-1w},startedOnOrBefore=${endDate}");
			h.newCountIndicator("Started ART 1 week ago_", "Started ART_",
			    "startedOnOrAfter=${endDate-2w},startedOnOrBefore=${endDate-1w}");
			h.newCountIndicator("Started ART 2 weeks ago_", "Started ART_",
			    "startedOnOrAfter=${endDate-3w},startedOnOrBefore=${endDate-2w}");
			
			// --------------------------------------------------------------
			// dimensions
//			/*
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
			md.addCohortDefinition("Neno", h.cohortDefinition("Enrolled in program_"), m2);
			m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Nsambe HC"));
			md.addCohortDefinition("Nsambe", h.cohortDefinition("Enrolled in program_"), m2);
			m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Magaleta HC"));
			md.addCohortDefinition("Magaleta", h.cohortDefinition("Enrolled in program_"), m2);
			h.replaceDefinition(md);
//*/
			// hiv program location ever
			 md = new CohortDefinitionDimension();
			md.setName("HIV program location ever_");
			md.addParameter(new Parameter("endDate", "End Date", Date.class));
			// todo, why are location and startdate for me mandatory?
			md.addParameter(new Parameter("startDate", "Start Date", Date.class));
			md.addParameter(new Parameter("location", "Location", Location.class));
			 m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Neno District Hospital"));
			md.addCohortDefinition("Neno", h.cohortDefinition("Ever enrolled in program at location_"), m2);
			m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Nsambe HC"));
			md.addCohortDefinition("Nsambe", h.cohortDefinition("Ever enrolled in program at location_"), m2);
			m2 = new HashMap<String, Object>();
			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
			m2.put("endDate", "${endDate}");
			m2.put("location", Context.getLocationService().getLocation("Magaleta HC"));
			md.addCohortDefinition("Magaleta", h.cohortDefinition("Ever enrolled in program at location_"), m2);
			h.replaceDefinition(md);
//			*/

			// -------------------------------------------------------
			// reports
			// hiv weekly report
			boolean useTestPatientCohort = false;
			String cohort = (useTestPatientCohort ? "Ever On ART for test_" : "Ever On ART_");
			PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
			rd.setName("HIV Weekly Report_");
			rd.addDimension("Location", h.cohortDefinitionDimension("HIV program location_"));
			rd.addDimension("Location ever", h.cohortDefinitionDimension("HIV program location ever_"));
			rd.setBaseCohortDefinition(h.cohortDefinition(cohort), ParameterizableUtil
			        .createParameterMappings("startedOnOrBefore=${endDate}"));
			
			addColumnForLocationsEver(rd, "Ever on ART", "Ever on ART_", "ini");
			addColumnForLocationsEver(rd, "Ever on ART 1 week ago", "Ever on ART 1 week ago_", "ini1");
			addColumnForLocationsEver(rd, "Ever on ART 2 weeks ago", "Ever on ART 2 weeks ago_", "ini2");
			
			addColumnForLocations(rd, "Started ART", "Started ART_", "ini");
			addColumnForLocations(rd, "Started ART 1 week ago", "Started ART 1 week ago_", "ini1");
			addColumnForLocations(rd, "Started ART 2 weeks ago", "Started ART 2 weeks ago_", "ini2");
			
			addColumnForLocations(rd, "On ART", "On ART_", "art");
			addColumnForLocations(rd, "On ART 1 week ago", "On ART 1 week ago_", "art1");
			addColumnForLocations(rd, "On ART 2 weeks ago", "On ART 2 weeks ago_", "art2");
			
			addColumnForLocationsEver(rd, "Ever Died", "Died_", "died");
			addColumnForLocationsEver(rd, "Ever Died 1 week ago", "Died 1 week ago_", "died1");
			addColumnForLocationsEver(rd, "Ever Died 2 weeks ago", "Died 2 weeks ago_", "died2");
			
			addColumnForLocationsEver(rd, "Ever Defaulted", "Defaulted_", "def");
			addColumnForLocationsEver(rd, "Ever Defaulted 1 week ago", "Defaulted 1 week ago_", "def1");
			addColumnForLocationsEver(rd, "Ever Defaulted 2 weeks ago", "Defaulted 2 weeks ago_", "def2");
			
			addColumnForLocationsEver(rd, "Ever Treatment stopped", "Treatment stopped_", "stp");
			addColumnForLocationsEver(rd, "Ever Treatment stopped 1 week ago", "Treatment stopped 1 week ago_", "stp1");
			addColumnForLocationsEver(rd, "Ever Treatment stopped 2 weeks ago", "Treatment stopped 2 weeks ago_", "stp2");
			
			addColumnForLocationsEver(rd, "Ever Transferred out", "Transferred out_", "tra");
			addColumnForLocationsEver(rd, "Ever Transferred out 1 week ago", "Transferred out 1 week ago_", "tra1");
			addColumnForLocationsEver(rd, "Ever Transferred out 2 weeks ago", "Transferred out 2 weeks ago_", "tra2");
			
			addColumnForLocationsEver(rd, "Ever Lost to followup", "Lost to followup_", "tra");
			addColumnForLocationsEver(rd, "Ever Lost to followup 1 week ago", "Lost to followup 1 week ago_", "tra1");
			addColumnForLocationsEver(rd, "Ever Lost to followup 2 weeks ago", "Lost to followup 2 weeks ago_", "tra2");
			
			h.replaceReportDefinition(rd);
			

			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addColumnForLocations(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator,
	                                  String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Nsambe"));
	}
	
	public void addColumnForLocationsEver(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator,
	                                      String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicator), h.hashMap("Location ever", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicator), h.hashMap("Location ever", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicator), h.hashMap("Location ever", "Nsambe"));
	}
	
}
