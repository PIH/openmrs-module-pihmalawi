package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.repository.ArtReportElements;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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
import org.openmrs.serialization.SerializationException;

public class SetupHivWeeklyOutcome {
	
	Helper h = new Helper();
	
	public SetupHivWeeklyOutcome(Helper helper) {
		h = helper;
	}
	
	public void setup(boolean b) throws Exception {
		delete();
		
		createCohortDefinitions();
		createIndicators();
		createDimensions();
		ReportDefinition rd = createReportDefinition();
		h.createXlsOverview(rd, "HIV_Weekly_Outcome_Overview_Lower_Neno.xls", "HIV Weekly Outcome Overview Lower Neno (Excel)_", null);
		h.createXlsOverview(rd, "HIV_Weekly_Outcome_Overview_Upper_Neno.xls", "HIV Weekly Outcome Overview Upper Neno (Excel)_", null);
		//		h.createGenericPatientDesignBreakdown(rd, "Simple Patient Renderer_", "HIV_Weekly_Outcome_Breakdown_SimpleReportRendererResource.xml");
		
//		ReportDesign rdes = createHtmlBreakdownArt(rd);
		//		h.render(rdes, rd);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("HIV Weekly Outcome Overview Lower Neno (Excel)_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("HIV Weekly Outcome Overview Upper Neno (Excel)_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("HIV Weekly Outcome Breakdown (HTML)_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(PeriodIndicatorReportDefinition.class, "HIV Weekly Outcome_");
		h.purgeDefinition(DataSetDefinition.class, "HIV Weekly Outcome_ Data Set");
		h.purgeDimension("hiv: HIV program location_");
		h.purgeDimension("hiv: HIV program location ever_");
		h.purgeAll("hiv:");
	}
	
	private ReportDefinition createReportDefinition() {
		boolean useTestPatientCohort = false;
		String cohort = (useTestPatientCohort ? "hiv: Ever On ART for test_" : "hiv: Ever On ART_");
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.addParameter(new Parameter("endDate", "End date (Sunday)", Date.class));
		rd.setName("HIV Weekly Outcome_");
		rd.setupDataSetDefinition();
		rd.addDimension("Location", h.cohortDefinitionDimension("hiv: HIV program location_"));
		rd.addDimension("Location ever", h.cohortDefinitionDimension("hiv: HIV program location ever_"));
		//		rd.setBaseCohortDefinition(h.cohortDefinition(cohort), ParameterizableUtil
		//		        .createParameterMappings("startedOnOrBefore=${endDate}"));
		
		//				addColumnForLocationsEver(rd, "Ever on ART", "Ever on ART_", "ever");
		//				addColumnForLocationsEver(rd, "Ever on ART 1 week ago", "Ever on ART 1 week ago_", "ever1");
		//				addColumnForLocationsEver(rd, "Ever on ART 2 weeks ago", "Ever on ART 2 weeks ago_", "ever2");
		
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "ART Patient visits", "ART Patient visits", "vst");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "ART Patient visits 1 week ago",
		    "ART Patient visits 1 week ago", "vst1");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "ART Patient visits 2 weeks ago",
		    "ART Patient visits 2 weeks ago", "vst2");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "EID Patient visits", "EID Patient visits", "evst");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "EID Patient visits 1 week ago",
		    "EID Patient visits 1 week ago", "evst1");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "EID Patient visits 2 weeks ago",
		    "EID Patient visits 2 weeks ago", "evst2");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "Pre-ART Patient visits", "Pre-ART Patient visits",
		    "pvst");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "Pre-ART Patient visits 1 week ago",
		    "Pre-ART Patient visits 1 week ago", "pvst1");
		new SetupHivWeeklyVisits(h).addColumnForLocationsForVisits(rd, "Pre-ART Patient visits 2 weeks ago",
		    "Pre-ART Patient visits 2 weeks ago", "pvst2");
		
		addColumnForLocations(rd, "Started ART", "Started ART_", "ini");
		addColumnForLocations(rd, "Started ART 1 week ago", "Started ART 1 week ago_", "ini1");
		addColumnForLocations(rd, "Started ART 2 weeks ago", "Started ART 2 weeks ago_", "ini2");
		
		addColumnForLocations(rd, "Started ART from Following during period", "Started ART from Following during period_",
		    "new");
		addColumnForLocations(rd, "Started ART from Following during period 1 week ago",
		    "Started ART from Following during period 1 week ago_", "new1");
		addColumnForLocations(rd, "Started ART from Following during period 2 weeks ago",
		    "Started ART from Following during period 2 weeks ago_", "new2");
		
		addColumnForLocations(rd, "On ART", "On ART_", "art");
		addColumnForLocations(rd, "On ART 1 week ago", "On ART 1 week ago_", "art1");
		addColumnForLocations(rd, "On ART 2 weeks ago", "On ART 2 weeks ago_", "art2");
		
		addColumnForLocationsWithState(rd, "Treatment stopped from on ART", "Treatment stopped from on ART", "stop");
		addColumnForLocationsWithState(rd, "Treatment stopped from on ART 1 week ago",
		    "Treatment stopped from on ART 1 week ago", "stop1");
		addColumnForLocationsWithState(rd, "Treatment stopped from on ART 2 weeks ago",
		    "Treatment stopped from on ART 2 weeks ago", "stop2");
		
		addColumnForLocationsWithState(rd, "Died from On ART", "Died from On ART", "died");
		addColumnForLocationsWithState(rd, "Died from On ART 1 week ago", "Died from On ART 1 week ago", "died1");
		addColumnForLocationsWithState(rd, "Died from On ART 2 weeks ago", "Died from On ART 2 weeks ago", "died2");
		
		addColumnForLocationsWithState(rd, "Transferred out from On ART", "Transferred out from On ART", "tra");
		addColumnForLocationsWithState(rd, "Transferred out from On ART 1 week ago",
		    "Transferred out from On ART 1 week ago", "tra1");
		addColumnForLocationsWithState(rd, "Transferred out from On ART 2 weeks ago",
		    "Transferred out from On ART 2 weeks ago", "tra2");
		
		addColumnForLocationsWithState(rd, "Transferred internally from On ART",
		    "Transferred internally from On ART", "int");
		addColumnForLocationsWithState(rd, "Transferred internally from On ART 1 week ago",
		    "Transferred internally from On ART 1 week ago", "int1");
		addColumnForLocationsWithState(rd, "Transferred internally from On ART 2 weeks ago",
		    "Transferred internally from On ART 2 weeks ago", "int2");
		
		h.replaceReportDefinition(rd);
		
		return rd;
	}
	
	private ReportDesign createHtmlBreakdownArt(ReportDefinition rd) throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		
		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		m.put("artndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("artmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("artnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("defmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("defmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("defnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("stpndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("stpmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("stpnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("diedndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("diedmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("diednsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("tranndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("tranmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("trannsm", new Mapped<DataSetDefinition>(dsd, null));
		
		return h.createHtmlBreakdown(rd, "HIV Weekly Outcome Breakdown_", m);
	}
	
	private void createDimensions() {
		
		// hiv program location
		// Enrolled in program at location as of end date
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd
		        .setQuery("SELECT p.patient_id FROM patient p"
		                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND (pp.date_completed IS NULL OR pp.date_completed > :endDate) AND pp.location_id = :location"
		                + " WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.setName("hiv: Enrolled in program_");
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("program", "Program", Program.class));
		h.replaceCohortDefinition(scd);
		
		CohortDefinitionDimension md = new CohortDefinitionDimension();
		md.setName("hiv: HIV program location_");
		md.addParameter(new Parameter("endDate", "End Date", Date.class));
		md.addParameter(new Parameter("startDate", "Start Date", Date.class));
		md.addParameter(new Parameter("location", "Location", Location.class));
		
		for (Location l : ArtReportElements.hivStaticLocations()) {
		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV program"));
		m2.put("endDate", "${endDate}");
		m2.put("location", l);
		md.addCohortDefinition(l.getName(), h.cohortDefinition("hiv: Enrolled in program_"), m2);
		}
		h.replaceDefinition(md);
		
		// Ever enrolled in program at location as of end date
		 scd = new SqlCohortDefinition();
		scd
		        .setQuery("SELECT p.patient_id FROM patient p"
		                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND pp.location_id = :location"
		                + " WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.setName("hiv: Ever enrolled in program at location_");
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("program", "Program", Program.class));
		h.replaceCohortDefinition(scd);
		
		// hiv program location ever
		md = new CohortDefinitionDimension();
		md.setName("hiv: HIV program location ever_");
		md.addParameter(new Parameter("endDate", "End Date", Date.class));
		md.addParameter(new Parameter("startDate", "Start Date", Date.class));
		md.addParameter(new Parameter("location", "Location", Location.class));
		
		for (Location l : ArtReportElements.hivStaticLocations()) {
			Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV program"));
		m2.put("endDate", "${endDate}");
		m2.put("location", l);
		md.addCohortDefinition(l.getName(), h.cohortDefinition("hiv: Ever enrolled in program at location_"), m2);
		}
		h.replaceDefinition(md);
	}

	private void createIndicators() {
		h.newCountIndicator("hiv: On ART_", "hiv: On ART_", "onDate=${endDate}");
		h.newCountIndicator("hiv: On ART 1 week ago_", "hiv: On ART_", "onDate=${endDate-1w}");
		h.newCountIndicator("hiv: On ART 2 weeks ago_", "hiv: On ART_", "onDate=${endDate-2w}");
		
		newCountIndicatorForLocationsWithState("hiv: Died from On ART", "hiv: In state at location from On ART_", h
		        .workflowState("HIV program", "Treatment status", "Patient died"));
		
		newCountIndicatorForLocationsWithState("hiv: Transferred out from On ART",
		    "hiv: In state at location from On ART_", h.workflowState("HIV program", "Treatment status",
		        "Patient transferred out"));
		
		newCountIndicatorForLocationsWithState("hiv: Transferred internally from On ART",
		    "hiv: In state at location from On ART_", h.workflowState("HIV program", "Treatment status",
		        "Transferred internally"));
		
		newCountIndicatorForLocationsWithState("hiv: Treatment stopped from On ART", "hiv: In state at location from On ART_",
		    h.workflowState("HIV program", "Treatment status", "Treatment stopped"));
		
		h.newCountIndicator("hiv: Ever on ART_", "hiv: Ever on ART_", "startedOnOrBefore=${endDate}");
		h.newCountIndicator("hiv: Ever on ART 1 week ago_", "hiv: Ever on ART_", "startedOnOrBefore=${endDate-1w}");
		h.newCountIndicator("hiv: Ever on ART 2 weeks ago_", "hiv: Ever on ART_", "startedOnOrBefore=${endDate-2w}");
		
		h.newCountIndicator("hiv: Started ART from Following during period_",
		    "hiv: Started ART from Following during period_", "startDate=${endDate-1w},endDate=${endDate}");
		h.newCountIndicator("hiv: Started ART from Following during period 1 week ago_",
		    "hiv: Started ART from Following during period_", "startDate=${endDate-2w},endDate=${endDate-1w}");
		h.newCountIndicator("hiv: Started ART from Following during period 2 weeks ago_",
		    "hiv: Started ART from Following during period_", "startDate=${endDate-3w},endDate=${endDate-2w}");
		
		h.newCountIndicator("hiv: Started ART_", "hiv: Started ART_",
		    "startedOnOrAfter=${endDate-1w},startedOnOrBefore=${endDate}");
		h.newCountIndicator("hiv: Started ART 1 week ago_", "hiv: Started ART_",
		    "startedOnOrAfter=${endDate-2w},startedOnOrBefore=${endDate-1w}");
		h.newCountIndicator("hiv: Started ART 2 weeks ago_", "hiv: Started ART_",
		    "startedOnOrAfter=${endDate-3w},startedOnOrBefore=${endDate-2w}");
		
		new SetupHivWeeklyVisits(h).newCountIndicatorForVisits("hiv: ART Patient visits", "hiv: ART Patient visits_");
		new SetupHivWeeklyVisits(h)
		        .newCountIndicatorForVisits("hiv: Pre-ART Patient visits", "hiv: Pre-ART Patient visits_");
		new SetupHivWeeklyVisits(h).newCountIndicatorForVisits("hiv: EID Patient visits", "hiv: EID Patient visits_");
	}
	
	private void newCountIndicatorForLocationsWithState(String namePrefix, String cohort, ProgramWorkflowState state) {
		for (Location l : ArtReportElements.hivStaticLocations()) {
		h.newCountIndicator(namePrefix + " (" + l.getName() + ")_", cohort, h.parameterMap("onDate", "${endDate}", "location", l, "state", state));
		h.newCountIndicator(namePrefix + " 1 week ago (" + l.getName() + ")_", cohort, h.parameterMap("onDate", "${endDate-1w}",
		    "location", l, "state", state));
		h.newCountIndicator(namePrefix + " 2 weeks ago (" + l.getName() + ")_", cohort, h.parameterMap("onDate", "${endDate-2w}",
		    "location", l, "state", state));
		}
	}
	
	private void createCohortDefinitions() {
		// In state
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("hiv: In state_");
		iscd.addParameter(new Parameter("states", "State", ProgramWorkflowState.class));
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// In state at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("hiv: In state at location_");
		islcd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "endDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		// On ART
		iscd = new InStateCohortDefinition();
		iscd.setName("hiv: On ART_");
		iscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status", "On antiretrovirals")));
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Treatment stopped
		iscd = new InStateCohortDefinition();
		iscd.setName("hiv: Treatment stopped_");
		iscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status", "Treatment stopped")));
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Transferred out
		iscd = new InStateCohortDefinition();
		iscd.setName("hiv: Transferred out_");
		iscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status", "Patient transferred out")));
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Died
		iscd = new InStateCohortDefinition();
		iscd.setName("hiv: Died_");
		iscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status", "Patient died")));
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Started ART during period
		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
		pscd.setName("hiv: Started ART_");
		pscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status", "On antiretrovirals")));
		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd);
		
		// Following during period
		iscd = new InStateCohortDefinition();
		iscd.setName("hiv: Following_");
		iscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status", "Pre-ART (Continue)")));
		iscd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Started ART from Following during period
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hiv: Started ART from Following during period_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("hiv: Started ART_"), ParameterizableUtil
		            .createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("hiv: Following_"), ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// On ART before end date
		pscd = new PatientStateCohortDefinition();
		pscd.setName("hiv: Ever On ART_");
		pscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status", "On antiretrovirals")));
		pscd.addParameter(new Parameter("startedOnOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(pscd);
		
		// Patient visits
		new SetupHivWeeklyVisits(h).createCohortDefinitions();
		
		// Ever enrolled in program at location with state as of end date
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hiv: Ever enrolled in program at location with state_");
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
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("hiv: In state at location from On ART_");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		cd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("hiv: In state at location_"), ParameterizableUtil
		            .createParameterMappings("onDate=${onDate},location=${location},state=${state}")));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("endDate", "${onDate}");
		map.put("location", "${location}");
		map.put("state", h.workflowState("HIV program", "Treatment status", "On antiretrovirals"));
		cd.getSearches().put("2",
		    new Mapped(h.cohortDefinition("hiv: Ever enrolled in program at location with state_"), map));
		cd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(cd);
	}
	
	private void addColumnForLocations(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator,
	                                   String indicatorKey) {
		for (Location l : ArtReportElements.hivStaticLocations()) {
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + ArtReportElements.hivSiteCode(l), displayNamePrefix + " (" + l.getName() + ")", h
			        .cohortIndicator("hiv: " + indicator), h.hashMap("Location", l.getName()));
		}
	}
	
	private void addColumnForLocationsWithState(PeriodIndicatorReportDefinition rd, String displayNamePrefix,
	                                            String indicatorFragment, String indicatorKey) {
		for (Location l : ArtReportElements.hivStaticLocations()) {
		CohortIndicator cohortIndicator = h.cohortIndicator("hiv: " + indicatorFragment + " (" + l.getName() + ")_");
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + ArtReportElements.hivSiteCode(l), displayNamePrefix + " (" + l.getName() + ")",
		    cohortIndicator, null);
		}
	}
}
