package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsClassLoader;

public class SetupArtWeeklyVisit {
	
	Helper h = null;
	
	public SetupArtWeeklyVisit(Helper helper) {
		h = helper;
	}
	
	public ReportDefinition setupHivWeekly(boolean useTestPatientCohort) throws Exception {
		deleteReportElements();
		
		createCohortDefinitions();
		createIndicators();
		createDimension();
		ReportDefinition rd = createReportDefinition(useTestPatientCohort);
		createArtOverviewExcelDesign(rd);
		//			createSimplePatientDesign(rd);
		ReportDesign rdes = createApzuPatientDesign(rd);
		h.render(rdes, rd);
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
		    "ART_Weekly_Visit_Breakdown_SimpleReportRendererResource.xml");
		resource.setContents(IOUtils.toByteArray(is));
		design.addResource(resource);
		resource.setReportDesign(design);
		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
	}
	
	private void createArtOverviewExcelDesign(ReportDefinition rd) throws IOException {
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("ART Weekly Visit Overview.xls");
		resource.setExtension("xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("ART_Weekly_Visit_Overview.xls");
		resource.setContents(IOUtils.toByteArray(is));
		final ReportDesign design = new ReportDesign();
		design.setName("ART Weekly Visit Overview_");
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
		m.put("noappndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noappmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noappnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noencndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noencmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noencnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdnsm", new Mapped<DataSetDefinition>(dsd, null));
		
		ReportingSerializer serializer = new ReportingSerializer();
		String designXml = serializer.serialize(m);
		
		final ReportDesign design = new ReportDesign();
		design.setName("ART Weekly Visit Breakdown_");
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
	
	private ReportDefinition createReportDefinition(boolean useTestPatientCohort) throws IOException {
		String cohort = (useTestPatientCohort ? "Alive On ART for appointment test_" : "Alive On ART_");
		// art appointment report
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName("ART Weekly Visit_");
		rd.setupDataSetDefinition();
		rd.addDimension("Location", h.cohortDefinitionDimension("ART program location_"));
		rd.setBaseCohortDefinition(h.cohortDefinition(cohort), ParameterizableUtil
		        .createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		addColumnForLocations(rd, "On ART", "On ART (appt)_", "art");
		addColumnForLocations(rd, "On ART 1 week ago", "On ART (appt) 1 week ago_", "art1");
		addColumnForLocations(rd, "On ART 2 weeks ago", "On ART (appt) 2 weeks ago_", "art2");
		
		addColumnForLocations(rd, "With appointment", "With appointment_", "app");
		addColumnForLocations(rd, "With appointment 1 week ago", "With appointment 1 week ago_", "app1");
		addColumnForLocations(rd, "With appointment 2 weeks ago", "With appointment 2 weeks ago_", "app2");
		
		addColumnForLocations(rd, "No appointment", "No appointment_", "noapp");
		addColumnForLocations(rd, "No appointment 1 week ago", "No appointment 1 week ago_", "noapp1");
		addColumnForLocations(rd, "No appointment 2 weeks ago", "No appointment 2 weeks ago_", "noapp2");
		
		addColumnForLocations(rd, "No Encounter", "No ART Encounter_", "noenc");
		addColumnForLocations(rd, "No Encounter 1 week ago", "No ART Encounter 1 week ago_", "noenc1");
		addColumnForLocations(rd, "No Encounter 2 weeks ago", "No ART Encounter 2 weeks ago_", "noenc2");
		
		addColumnForLocations(rd, "Missed appointment", "Missed appointment_", "msd");
		addColumnForLocations(rd, "Missed appointment 1 week ago", "Missed appointment 1 week ago_", "msd1");
		addColumnForLocations(rd, "Missed appointment 2 weeks ago", "Missed appointment 2 weeks ago_", "msd2");
		//		addColumnForLocations(rd, "Missed appointment >2 weeks", "Missed appointment >2 weeks_", "2msd");
		//	    addColumnForLocations(rd, "Missed appointment >2 weeks 1 week ago", "Missed appointment >2 weeks 1 week ago_",
		//	        "2msd1");
		//	    addColumnForLocations(rd, "Missed appointment >2 weeks 2 weeks ago", "Missed appointment >2 weeks 2 weeks ago_",
		//	        "2msd2");
		addColumnForLocations(rd, "Missed appointment >3 weeks", "Missed appointment >3 weeks_", "3msd");
		addColumnForLocations(rd, "Missed appointment >3 weeks 1 week ago", "Missed appointment >3 weeks 1 week ago_",
		    "3msd1");
		addColumnForLocations(rd, "Missed appointment >3 weeks 2 weeks ago", "Missed appointment >3 weeks 2 weeks ago_",
		    "3msd2");
		//			addColumnForLocations(rd, "Missed appointment >1 month", "Missed appointment >1 month_");
		addColumnForLocations(rd, "Missed appointment >2 months", "Missed appointment >2 months_", "8msd");
		addColumnForLocations(rd, "Missed appointment >2 months 1 week ago", "Missed appointment >2 months 1 week ago_",
		    "8msd1");
		addColumnForLocations(rd, "Missed appointment >2 months 2 weeks ago", "Missed appointment >2 months 2 weeks ago_",
		    "8msd2");
		addColumnForLocations(rd, "Missed appointment >12 weeks", "Missed appointment >12 weeks_", "12msd");
		addColumnForLocations(rd, "Missed appointment >12 weeks 1 week ago", "Missed appointment >12 weeks 1 week ago_",
		    "12msd1");
		addColumnForLocations(rd, "Missed appointment >12 weeks 2 weeks ago", "Missed appointment >12 weeks 2 weeks ago_",
		    "12msd2");
		
		h.replaceReportDefinition(rd);
		
		return rd;
	}
	
	private void createDimension() {
		// hiv program location
		CohortDefinitionDimension md = new CohortDefinitionDimension();
		md.setName("ART program location_");
		md.addParameter(new Parameter("endDate", "End Date", Date.class));
		// todo, why are location and startdate for me mandatory?
		md.addParameter(new Parameter("startDate", "Start Date", Date.class));
		md.addParameter(new Parameter("location", "Location", Location.class));
		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Neno District Hospital"));
		md.addCohortDefinition("Neno", h.cohortDefinition("Enrolled in program (appt)_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Nsambe HC"));
		md.addCohortDefinition("Nsambe", h.cohortDefinition("Enrolled in program (appt)_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Magaleta HC"));
		md.addCohortDefinition("Magaleta", h.cohortDefinition("Enrolled in program (appt)_"), m2);
		h.replaceDimensionDefinition(md);
	}
	
	private void createIndicators() {
		// On ART
		h.newCountIndicator("On ART (appt)_", "On ART (appt)_", "onDate=${endDate}");
		h.newCountIndicator("On ART (appt) 1 week ago_", "On ART (appt)_", "onDate=${endDate-1w}");
		h.newCountIndicator("On ART (appt) 2 weeks ago_", "On ART (appt)_", "onDate=${endDate-2w}");
		
		// With appointment, shortcut taken
		h.newCountIndicator("With Appointment_", "Missed Appointment_", "value1=${endDate+10y},onOrBefore=${endDate}");
		h.newCountIndicator("With Appointment 1 week ago_", "Missed Appointment_",
		    "value1=${endDate+10y},onOrBefore=${endDate-1w}");
		h.newCountIndicator("With Appointment 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate+10y},onOrBefore=${endDate-2w}");
		
		// Missed appointments
		h.newCountIndicator("Missed Appointment_", "Missed Appointment_", "value1=${endDate},onOrBefore=${endDate}");
		h.newCountIndicator("Missed Appointment 1 week ago_", "Missed Appointment_",
		    "value1=${endDate-1w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("Missed Appointment 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate-2w},onOrBefore=${endDate-2w}");
		h.newCountIndicator("Missed Appointment >1 week_", "Missed Appointment_",
		    "value1=${endDate-1w},onOrBefore=${endDate}");
		h.newCountIndicator("Missed Appointment >1 week 1 week ago_", "Missed Appointment_",
		    "value1=${endDate-2w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("Missed Appointment >1 week 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate-3w},onOrBefore=${endDate-2w}");
		h.newCountIndicator("Missed Appointment >2 weeks_", "Missed Appointment_",
		    "value1=${endDate-2w},onOrBefore=${endDate}");
		h.newCountIndicator("Missed Appointment >2 weeks 1 week ago_", "Missed Appointment_",
		    "value1=${endDate-3w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("Missed Appointment >2 weeks 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate-4w},onOrBefore=${endDate-2w}");
		h.newCountIndicator("Missed Appointment >3 weeks_", "Missed Appointment_",
		    "value1=${endDate-3w},onOrBefore=${endDate}");
		h.newCountIndicator("Missed Appointment >3 weeks 1 week ago_", "Missed Appointment_",
		    "value1=${endDate-4w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("Missed Appointment >3 weeks 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate-5w},onOrBefore=${endDate-2w}");
		h.newCountIndicator("Missed Appointment >1 month_", "Missed Appointment_",
		    "value1=${endDate-4w},onOrBefore=${endDate}");
		h.newCountIndicator("Missed Appointment >1 month 1 week ago_", "Missed Appointment_",
		    "value1=${endDate-5w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("Missed Appointment >1 month 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate-6w},onOrBefore=${endDate-2w}");
		h.newCountIndicator("Missed Appointment >2 months_", "Missed Appointment_",
		    "value1=${endDate-8w},onOrBefore=${endDate}");
		h.newCountIndicator("Missed Appointment >2 months 1 week ago_", "Missed Appointment_",
		    "value1=${endDate-9w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("Missed Appointment >2 months 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate-10w},onOrBefore=${endDate-2w}");
		h.newCountIndicator("Missed Appointment >12 weeks_", "Missed Appointment_",
		    "value1=${endDate-12w},onOrBefore=${endDate}");
		h.newCountIndicator("Missed Appointment >12 weeks 1 week ago_", "Missed Appointment_",
		    "value1=${endDate-13w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("Missed Appointment >12 weeks 2 weeks ago_", "Missed Appointment_",
		    "value1=${endDate-14w},onOrBefore=${endDate-2w}");
		
		// No appointment
		h.newCountIndicator("No appointment_", "No appointment_", "onOrBefore=${endDate}");
		h.newCountIndicator("No appointment 1 week ago_", "No appointment_", "onOrBefore=${endDate-1w}");
		h.newCountIndicator("No appointment 2 weeks ago_", "No appointment_", "onOrBefore=${endDate-2w}");
		
		// No ART Encounter
		h.newCountIndicator("No ART Encounter_", "No ART Encounter_", "onOrBefore=${endDate}");
		h.newCountIndicator("No ART Encounter 1 week ago_", "No ART Encounter_", "onOrBefore=${endDate-1w}");
		h.newCountIndicator("No ART Encounter 2 weeks ago_", "No ART Encounter_", "onOrBefore=${endDate-2w}");
	}
	
	private void createCohortDefinitions() {
		// On ART at end of period
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("On ART (appt)_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM").getWorkflowByName("TREATMENT STATUS")
		        .getStateByName("ON ANTIRETROVIRALS"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Missed Appointment
		DateObsCohortDefinition dod = new DateObsCohortDefinition();
		dod.setName("Missed Appointment_");
		dod.setTimeModifier(TimeModifier.MAX);
		dod.setQuestion(Context.getConceptService().getConceptByName("APPOINTMENT DATE"));
		dod.setOperator1(RangeComparator.LESS_THAN);
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		dod.addParameter(new Parameter("value1", "endDate", Date.class));
		h.replaceCohortDefinition(dod);
		
		// No Appointment
		dod = new DateObsCohortDefinition();
		dod.setName("No Appointment_");
		dod.setTimeModifier(TimeModifier.NO);
		dod.setQuestion(Context.getConceptService().getConceptByName("APPOINTMENT DATE"));
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(dod);
		
		// Death
		BirthAndDeathCohortDefinition badd = new BirthAndDeathCohortDefinition();
		badd.setName("Death_");
		badd.addParameter(new Parameter("diedOnOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(badd);
		
		// Alive
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("Alive_");
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("Death_"), ParameterizableUtil
		            .createParameterMappings("diedOnOrBefore=${endDate}")));
		ccd.setCompositionString("NOT 1");
		h.replaceCohortDefinition(ccd);
		
		// Alive On ART
		ccd = new CompositionCohortDefinition();
		ccd.setName("Alive On ART_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.getSearches().put("1",
		    new Mapped(h.cohortDefinition("Alive_"), ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		ccd.getSearches().put("2",
		    new Mapped(h.cohortDefinition("On ART (appt)_"), ParameterizableUtil.createParameterMappings("onDate=${endDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// Enrolled in program at location as of end date
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd
		        .setQuery("SELECT p.patient_id FROM patient p"
		                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND (pp.date_completed IS NULL OR pp.date_completed > :endDate) AND pp.location_id = :location"
		                + " WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.setName("Enrolled in program (appt)_");
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("program", "Program", Program.class));
		h.replaceCohortDefinition(scd);
		
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("No ART Encounter_");
		List<EncounterType> types = new ArrayList<EncounterType>();
		types.add(Context.getEncounterService().getEncounterType("ART_INITIAL"));
		types.add(Context.getEncounterService().getEncounterType("ART_FOLLOWUP"));
		ecd.setEncounterTypeList(types);
		ecd.setAtLeastCount(1);
		ecd.setReturnInverse(true);
		ecd.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(ecd);
		
		// Filter by patient names for testing
		scd = new SqlCohortDefinition();
		scd.setName("Person name filter_");
		scd
		        .setQuery("SELECT p.patient_id FROM patient p "
		                + "INNER JOIN person_name n ON p.patient_id = n.person_id AND n.voided = 0 AND n.family_name = :family_name "
		                + "WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.addParameter(new Parameter("family_name", "Family name", String.class));
		h.replaceCohortDefinition(scd);
		
		// Alive On ART with test names
		ccd = new CompositionCohortDefinition();
		ccd.setName("Alive On ART for appointment test_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("Alive On ART_"), ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("Person name filter_"), ParameterizableUtil
		            .createParameterMappings("family_name=appointment")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
	}
	
	public void deleteReportElements() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Simple Patient Renderer_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("ART Weekly Visit Overview_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("ART Weekly Visit Breakdown_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		// todo, also purge internal dataset for ART Appointments_
		h.purgeDefinition(PeriodIndicatorReportDefinition.class, "ART Weekly Visit_");
		
		h.purgeDimension("ART program location_");
		
		h.purgeDefinition(CohortDefinition.class, "On ART (appt)_");
		h.purgeDefinition(CohortDefinition.class, "Missed Appointment_");
		h.purgeDefinition(CohortDefinition.class, "No Appointment_");
		h.purgeDefinition(CohortDefinition.class, "Death_");
		h.purgeDefinition(CohortDefinition.class, "Alive_");
		h.purgeDefinition(CohortDefinition.class, "Alive on ART_");
		h.purgeDefinition(CohortDefinition.class, "Enrolled in program (appt)_");
		h.purgeDefinition(CohortDefinition.class, "No ART Encounter_");
		h.purgeDefinition(CohortDefinition.class, "Person name filter_");
		h.purgeDefinition(CohortDefinition.class, "Alive On ART for appointment test_");
		purgeIndicator("On ART (appt)");
		purgeIndicator("With Appointment");
		purgeIndicator("Missed Appointment");
		purgeIndicator("Missed Appointment >1 week");
		purgeIndicator("Missed Appointment >2 weeks");
		purgeIndicator("Missed Appointment >3 weeks");
		purgeIndicator("Missed Appointment >1 month");
		purgeIndicator("Missed Appointment >2 months");
		purgeIndicator("Missed Appointment >12 weeks");
		purgeIndicator("No appointment");
		purgeIndicator("No ART Encounter");
	}
	
	private void purgeIndicator(String name) {
		h.purgeDefinition(CohortIndicator.class, name + "_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago_");
	}
	
	public void addColumnForLocations(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator,
	                                  String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Nsambe"));
		
		//	PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		//        .cohortIndicator(indicator), null /*h.hashMap("Location", "Neno")*/);
		//PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		//        .cohortIndicator(indicator), null /*h.hashMap("Location", "Magaleta")*/);
		//PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		//        .cohortIndicator(indicator), null /*h.hashMap("Location", "Nsambe")*/);
	}
}
