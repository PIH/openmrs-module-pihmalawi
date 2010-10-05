package org.openmrs.module.pihmalawi.reporting;

import java.io.FileOutputStream;
import java.io.IOException;
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
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.CreateInitialDataSet;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.test.annotation.Rollback;

public class SetupHivWeeklyOutcome {
	
	Helper h = new Helper();
	
	public SetupHivWeeklyOutcome(Helper helper) {
		h = helper;
	}
	
	public void setupHivWeekly(boolean b) throws Exception {
		deleteReportElements();
		
		createCohortDefinitions();
		createIndicators();
		createDimensions();
		ReportDefinition rd = createReportDefinition();
		createArtOverviewExcelDesign(rd);
		//		//			createSimplePatientDesign(rd);
		ReportDesign rdes = createApzuPatientDesign(rd);
		//		h.render(rdes, rd);
		//		return rd;
	}
	
	public void deleteReportElements() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Simple Patient Renderer_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("HIV Weekly Outcome Overview_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("HIV Weekly Outcome Breakdown_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		// todo, also purge internal dataset for ART Appointments_
		h.purgeDefinition(PeriodIndicatorReportDefinition.class, "HIV Weekly Outcome_");
		
		h.purgeDimension("HIV program location_");
		h.purgeDimension("HIV program location ever_");
		
		purgeIndicator("Defaulted");
		purgeIndicator("Died");
		purgeIndicator("Enrolled in program");
		purgeIndicator("Ever enrolled in program at location with state");
		purgeIndicator("Ever enrolled in program at location");
		purgeIndicator("Ever On ART at location with state");
		purgeIndicator("Ever On ART");
		purgeIndicator("In State");
		purgeIndicator("Lost to followup");
		purgeIndicator("ART Patient visits");
		purgeIndicator("On ART");
		purgeIndicator("Started ART");
		purgeIndicator("Transferred out");
		purgeIndicator("Treatment stopped");
		
		purgeIndicatorForLocationWithState("Ever Died");
		purgeIndicatorForLocationWithState("Ever Defaulted");
		purgeIndicatorForLocationWithState("Ever Transferred out");
		purgeIndicatorForLocationWithState("Ever Treatment stopped");
		purgeIndicatorForLocationWithState("Ever Lost to Followup");
		
		// dependent elements
		h.purgeDefinition(CohortDefinition.class, "Ever On ART at location with state_");
		
		h.purgeDefinition(CohortDefinition.class, "Defaulted_");
		h.purgeDefinition(CohortDefinition.class, "Died_");
		h.purgeDefinition(CohortDefinition.class, "Enrolled in program_");
		h.purgeDefinition(CohortDefinition.class, "Ever enrolled in program at location with state_");
		h.purgeDefinition(CohortDefinition.class, "Ever enrolled in program at location_");
		h.purgeDefinition(CohortDefinition.class, "Ever On ART_");
		h.purgeDefinition(CohortDefinition.class, "In State_");
		h.purgeDefinition(CohortDefinition.class, "Lost to followup_");
		h.purgeDefinition(CohortDefinition.class, "On ART_");
		h.purgeDefinition(CohortDefinition.class, "ART Patients visits ever_");
		h.purgeDefinition(CohortDefinition.class, "Started ART_");
		h.purgeDefinition(CohortDefinition.class, "Transferred out_");
		h.purgeDefinition(CohortDefinition.class, "Treatment stopped_");
	}
	
	private void purgeIndicator(String name) {
		h.purgeDefinition(CohortIndicator.class, name + "_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago_");
	}
	
	private void purgeIndicatorForLocationWithState(String name) {
		h.purgeDefinition(CohortIndicator.class, name + " from On ART at Neno_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART at Magaleta_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART at Nsambe_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART 1 week ago at Neno_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART 1 week ago at Magaleta_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART 1 week ago at Nsambe_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART 2 weeks ago at Neno_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART 2 weeks ago at Magaleta_");
		h.purgeDefinition(CohortIndicator.class, name + " from On ART 2 weeks ago at Nsambe_");
	}
	
	private ReportDefinition createReportDefinition() {
		// hiv weekly report
		boolean useTestPatientCohort = false;
		String cohort = (useTestPatientCohort ? "Ever On ART for test_" : "Ever On ART_");
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName("HIV Weekly Outcome_");
		rd.setupDataSetDefinition();
		rd.addDimension("Location", h.cohortDefinitionDimension("HIV program location_"));
		rd.addDimension("Location ever", h.cohortDefinitionDimension("HIV program location ever_"));
		rd.setBaseCohortDefinition(h.cohortDefinition(cohort), ParameterizableUtil
		        .createParameterMappings("startedOnOrBefore=${endDate}"));
		
		addColumnForLocationsEver(rd, "Ever on ART", "Ever on ART_", "ever");
		addColumnForLocationsEver(rd, "Ever on ART 1 week ago", "Ever on ART 1 week ago_", "ever1");
		addColumnForLocationsEver(rd, "Ever on ART 2 weeks ago", "Ever on ART 2 weeks ago_", "ever2");
		
		addColumnForLocationsEver(rd, "ART Patient visits", "ART Patient visits_", "vst");
		addColumnForLocationsEver(rd, "ART Patient visits 1 week ago", "ART Patient visits 1 week ago_", "vst1");
		addColumnForLocationsEver(rd, "ART Patient visits 2 weeks ago", "ART Patient visits 2 weeks ago_", "vst2");
		
		addColumnForLocations(rd, "Started ART", "Started ART_", "ini");
		addColumnForLocations(rd, "Started ART 1 week ago", "Started ART 1 week ago_", "ini1");
		addColumnForLocations(rd, "Started ART 2 weeks ago", "Started ART 2 weeks ago_", "ini2");
		
		addColumnForLocations(rd, "Started ART from Following during period", "Started ART from Following during period_", "new");
		addColumnForLocations(rd, "Started ART from Following during period 1 week ago", "Started ART from Following during period 1 week ago_", "new1");
		addColumnForLocations(rd, "Started ART from Following during period 2 weeks ago", "Started ART from Following during period 2 weeks ago_", "new2");
		
		addColumnForLocations(rd, "On ART", "On ART_", "art");
		addColumnForLocations(rd, "On ART 1 week ago", "On ART 1 week ago_", "art1");
		addColumnForLocations(rd, "On ART 2 weeks ago", "On ART 2 weeks ago_", "art2");
		
		addColumnForLocationsWithState(rd, "Ever Died from On ART", "Ever Died from On ART", "died");
		addColumnForLocationsWithState(rd, "Ever Died from On ART 1 week ago", "Ever Died from On ART 1 week ago", "died1");
		addColumnForLocationsWithState(rd, "Ever Died from On ART 2 weeks ago", "Ever Died from On ART 2 weeks ago", "died2");
		
		addColumnForLocationsWithState(rd, "Ever Defaulted from On ART", "Ever Defaulted from On ART", "def");
		addColumnForLocationsWithState(rd, "Ever Defaulted from On ART 1 week ago", "Ever Defaulted from On ART 1 week ago",
		    "def1");
		addColumnForLocationsWithState(rd, "Ever Defaulted from On ART 2 weeks ago",
		    "Ever Defaulted from On ART 2 weeks ago", "def2");
		
		addColumnForLocationsWithState(rd, "Ever Treatment stopped from On ART", "Ever Treatment stopped from On ART", "stp");
		addColumnForLocationsWithState(rd, "Ever Treatment stopped from On ART 1 week ago",
		    "Ever Treatment stopped from On ART 1 week ago", "stp1");
		addColumnForLocationsWithState(rd, "Ever Treatment stopped from On ART 2 weeks ago",
		    "Ever Treatment stopped from On ART 2 weeks ago", "stp2");
		
		addColumnForLocationsWithState(rd, "Ever Transferred out from On ART", "Ever Transferred out from On ART", "tra");
		addColumnForLocationsWithState(rd, "Ever Transferred out from On ART 1 week ago",
		    "Ever Transferred out from On ART 1 week ago", "tra1");
		addColumnForLocationsWithState(rd, "Ever Transferred out from On ART 2 weeks ago",
		    "Ever Transferred out from On ART 2 weeks ago", "tra2");
		
		addColumnForLocationsWithState(rd, "Ever Lost to Followup from On ART", "Ever Lost to Followup from On ART", "lost");
		addColumnForLocationsWithState(rd, "Ever Lost to Followup from On ART 1 week ago",
		    "Ever Lost to Followup from On ART 1 week ago", "lost1");
		addColumnForLocationsWithState(rd, "Ever Lost to Followup from On ART 2 weeks ago",
		    "Ever Lost to Followup from On ART 2 weeks ago", "lost2");
		
		h.replaceReportDefinition(rd);
		return rd;
	}
	
	private void createSimplePatientDesign(ReportDefinition rd) throws IOException {
		final ReportDesign design = new ReportDesign();
		design.setName("Simple Patient Renderer_");
		design.setReportDefinition(rd);
		design.setRendererType(CohortDetailReportRenderer.class);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("designFile");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
		    "HIV_Weekly_Outcome_Breakdown_SimpleReportRendererResource.xml");
		resource.setContents(IOUtils.toByteArray(is));
		design.addResource(resource);
		resource.setReportDesign(design);
		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
	}
	
	private void createArtOverviewExcelDesign(ReportDefinition rd) throws IOException {
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("HIV Weekly Outcome Overview.xls");
		resource.setExtension("xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("HIV_Weekly_Outcome_Overview.xls");
		resource.setContents(IOUtils.toByteArray(is));
		final ReportDesign design = new ReportDesign();
		design.setName("HIV Weekly Outcome Overview_");
		design.setReportDefinition(rd);
		design.setRendererType(ExcelTemplateRenderer.class);
		design.addResource(resource);
		resource.setReportDesign(design);
		
		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
	}
	
	private ReportDesign createApzuPatientDesign(ReportDefinition rd) throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new HashMap<String, Mapped<? extends DataSetDefinition>>();
		
		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		m.put("evermgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("evernsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("defndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("defmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("defnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("stpndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("stpmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("stpnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("diedndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("diedmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("diednsm", new Mapped<DataSetDefinition>(dsd, null));
		
		ReportingSerializer serializer = new ReportingSerializer();
		String designXml = serializer.serialize(m);
		
		final ReportDesign design = new ReportDesign();
		design.setName("HIV Weekly Outcome Breakdown_");
		design.setReportDefinition(rd);
		design.setRendererType(CohortDetailReportRenderer.class);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("designFile"); // Note: You must name your resource exactly like this for it to work
		resource.setContents(designXml.getBytes());
		design.addResource(resource);
		resource.setReportDesign(design);
		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
		return design;
	}
	
	private void createDimensions() {
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
	}
	
	private void createIndicators() {
		h.newCountIndicator("On ART_", "On ART_", "onDate=${endDate}");
		h.newCountIndicator("On ART 1 week ago_", "On ART_", "onDate=${endDate-1w}");
		h.newCountIndicator("On ART 2 weeks ago_", "On ART_", "onDate=${endDate-2w}");
		
		newCountIndicatorForLocationsWithState("Ever Died from On ART", "Ever On ART at location with state_", h
		        .workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DIED"));
		
		newCountIndicatorForLocationsWithState("Ever Defaulted from On ART", "Ever On ART at location with state_", h
		        .workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT DEFAULTED"));
		
		newCountIndicatorForLocationsWithState("Ever Transferred out from On ART", "Ever On ART at location with state_", h
		        .workflowState("HIV PROGRAM", "TREATMENT STATUS", "PATIENT TRANSFERRED OUT"));
		
		newCountIndicatorForLocationsWithState("Ever Treatment Stopped from On ART", "Ever On ART at location with state_",
		    h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "TREATMENT STOPPED"));
		
		newCountIndicatorForLocationsWithState("Ever Lost to Followup from On ART", "Ever On ART at location with state_", h
		        .workflowState("HIV PROGRAM", "TREATMENT STATUS", "LOST TO FOLLOWUP"));
		
		h.newCountIndicator("Ever on ART_", "Ever on ART_", "startedOnOrBefore=${endDate}");
		h.newCountIndicator("Ever on ART 1 week ago_", "Ever on ART_", "startedOnOrBefore=${endDate-1w}");
		h.newCountIndicator("Ever on ART 2 weeks ago_", "Ever on ART_", "startedOnOrBefore=${endDate-2w}");
		
		
		h.newCountIndicator("Started ART from Following during period_", "Started ART from Following during period_", "startDate=${endDate-1w},endDate=${endDate}");
		h.newCountIndicator("Started ART from Following during period 1 week ago_", "Started ART from Following during period_", "startDate=${endDate-2w},endDate=${endDate-1w}");
		h.newCountIndicator("Started ART from Following during period 2 weeks ago_", "Started ART from Following during period_", "startDate=${endDate-3w},endDate=${endDate-2w}");
		
		h.newCountIndicator("Started ART_", "Started ART_", "startedOnOrAfter=${endDate-1w},startedOnOrBefore=${endDate}");
		h.newCountIndicator("Started ART 1 week ago_", "Started ART_",
		    "startedOnOrAfter=${endDate-2w},startedOnOrBefore=${endDate-1w}");
		h.newCountIndicator("Started ART 2 weeks ago_", "Started ART_",
		    "startedOnOrAfter=${endDate-3w},startedOnOrBefore=${endDate-2w}");

		h.newCountIndicator("ART Patient visits_", "ART Patient visits_", "onOrAfter=${endDate-1w},onOrBefore=${endDate}");
		h.newCountIndicator("ART Patient visits 1 week ago_", "ART Patient visits_", "onOrAfter=${endDate-2w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("ART Patient visits 2 weeks ago_", "ART Patient visits_", "onOrAfter=${endDate-3w},onOrBefore=${endDate-2w}");
}
	
	private void newCountIndicatorForLocationsWithState(String namePrefix, String cohort, ProgramWorkflowState state) {
		h.newCountIndicator(namePrefix + " at Neno_", cohort, h.parameterMap("endDate", "${endDate}", "location", h
		        .location("Neno District Hospital"), "state", state));
		h.newCountIndicator(namePrefix + " at Magaleta_", cohort, h.parameterMap("endDate", "${endDate}", "location", h
		        .location("Magaleta HC"), "state", state));
		h.newCountIndicator(namePrefix + " at Nsambe_", cohort, h.parameterMap("endDate", "${endDate}", "location", h
		        .location("Nsambe HC"), "state", state));
		h.newCountIndicator(namePrefix + " 1 week ago at Neno_", cohort, h.parameterMap("endDate", "${endDate-1w}",
		    "location", h.location("Neno District Hospital"), "state", state));
		h.newCountIndicator(namePrefix + " 1 week ago at Magaleta_", cohort, h.parameterMap("endDate", "${endDate-1w}",
		    "location", h.location("Magaleta HC"), "state", state));
		h.newCountIndicator(namePrefix + " 1 week ago at Nsambe_", cohort, h.parameterMap("endDate", "${endDate-1w}",
		    "location", h.location("Nsambe HC"), "state", state));
		h.newCountIndicator(namePrefix + " 2 weeks ago at Neno_", cohort, h.parameterMap("endDate", "${endDate-2w}",
		    "location", h.location("Neno District Hospital"), "state", state));
		h.newCountIndicator(namePrefix + " 2 weeks ago at Magaleta_", cohort, h.parameterMap("endDate", "${endDate-2w}",
		    "location", h.location("Magaleta HC"), "state", state));
		h.newCountIndicator(namePrefix + " 2 weeks ago at Nsambe_", cohort, h.parameterMap("endDate", "${endDate-2w}",
		    "location", h.location("Nsambe HC"), "state", state));
	}
	
	private void createCohortDefinitions() {
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
		
		// Treatment stopped
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
		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd);
		
		// Following during period
		iscd = new InStateCohortDefinition();
		iscd.setName("Following_");
		iscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "FOLLOWING")));
		iscd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Started ART from Following during period
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("Started ART from Following during period_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("Started ART_"), ParameterizableUtil
		            .createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("Following_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);

		// On ART before end date
		pscd = new PatientStateCohortDefinition();
		pscd.setName("Ever On ART_");
		pscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
		pscd.addParameter(new Parameter("startedOnOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(pscd);
		
		// Patient visits
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("ART Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService().getEncounterType("ART_INITIAL"), Context.getEncounterService().getEncounterType("ART_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locations", "location", Location.class));
		h.replaceCohortDefinition(ecd);
		
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
		
		// Ever enrolled in program at location with state as of end date
		scd = new SqlCohortDefinition();
		scd.setName("Ever enrolled in program at location with state_");
		String sql = "" + "SELECT pp.patient_id"
		        + " FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps"
		        + " WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id"
		        + "   AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id"
		        + "   AND pws.program_workflow_id = 1 AND ps.state = :state "
		        + "   AND pp.location_id = :location AND (ps.end_date <= :endDate OR ps.end_date IS NULL)"
		        + "   AND pw.retired = 0 AND pp.voided = 0 AND pws.retired = 0 AND ps.voided = 0"
		        + " GROUP BY pp.patient_id;";
		scd.setQuery(sql);
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		h.replaceCohortDefinition(scd);
		
		// Ever On Art at location with state
		 ccd = new CompositionCohortDefinition();
		ccd.setName("Ever On ART at location with state_");
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("Ever enrolled in program at location with state_"), ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},location=${location},state=${state}")));
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("endDate", "${endDate}");
		m.put("location", "${location}");
		m.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"));
		ccd.getSearches().put("2", new Mapped(h.cohortDefinition("Ever enrolled in program at location with state_"), m));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
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
	}
	
	private void addColumnForLocations(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator,
	                                  String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Nsambe"));
	}
	
	private void addColumnForLocationsEver(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator,
	                                      String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicator), h.hashMap("Location ever", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicator), h.hashMap("Location ever", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicator), h.hashMap("Location ever", "Nsambe"));
	}
	
	private void addColumnForLocationsWithState(PeriodIndicatorReportDefinition rd, String displayNamePrefix,
	                                           String indicatorFragment, String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicatorFragment + " at Neno_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicatorFragment + " at Magaleta_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicatorFragment + " at Nsambe_"), null);
	}
}
