package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
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
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.serialization.SerializationException;

public class SetupArtWeeklyVisit {
	
	Helper h = null;
	
	public SetupArtWeeklyVisit(Helper helper) {
		h = helper;
	}
	
	public void setupHivWeekly(boolean useTestPatientCohort) throws Exception {
		deleteReportElements();
		
		createCohortDefinitions();
		createIndicators();
		createDimension();
		ReportDefinition rd = createReportDefinition(useTestPatientCohort);
		h.createXlsOverview(rd, "ART_Weekly_Visit_Overview.xls", "ART Weekly Visit Overview (Excel)_");
		//		h.createGenericPatientDesignBreakdown(rd, "Simple Patient Renderer_", "ART_Weekly_Visit_Breakdown_SimpleReportRendererResource.xml");
		createHtmlBreakdownArt(rd);
		createHtmlBreakdownEmr(rd);
	}
	
	private ReportDesign createHtmlBreakdownArt(ReportDefinition rd) throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		
		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		m.put("noappndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdndh", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdmgt", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdnsm", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noapplsi", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdlsi", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappcfa", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdcfa", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappmte", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdmte", new Mapped<DataSetDefinition>(dsd, null));
		
		return h.createHtmlBreakdown(rd, "ART Weekly Visit Breakdown (ART Team)_", m);
	}
	
	private ReportDesign createHtmlBreakdownEmr(ReportDefinition rd) throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		
		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		m.put("noappndh", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdndh", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappmgt", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdmgt", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappnsm", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdnsm", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noapplsi", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdlsi", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappmte", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdmte", new Mapped<DataSetDefinition>(dsd, null));
		
		m.put("noappcfa", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdcfa", new Mapped<DataSetDefinition>(dsd, null));
		
		return h.createHtmlBreakdown(rd, "ART Weekly Visit Breakdown (EMR Team)_", m);
	}
	
	private ReportDefinition createReportDefinition(boolean useTestPatientCohort) throws IOException {
		String cohort = (useTestPatientCohort ? "artvst: Alive On ART for appointment test_" : "artvst: Alive On ART_");
		// art appointment report
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName("ART Weekly Visit_");
		rd.setupDataSetDefinition();
		rd.addDimension("Location", h.cohortDefinitionDimension("artvst: ART program location_"));
		rd.setBaseCohortDefinition(h.cohortDefinition(cohort), ParameterizableUtil
		        .createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		addColumnForLocations(rd, "On ART", "On ART (appt)_", "art");
		addColumnForLocations(rd, "On ART 1 week ago", "On ART (appt) 1 week ago_", "art1");
		addColumnForLocations(rd, "On ART 2 weeks ago", "On ART (appt) 2 weeks ago_", "art2");
		
		addColumnForLocations(rd, "No appointment", "No appointment_", "noapp");
		addColumnForLocations(rd, "No appointment 1 week ago", "No appointment 1 week ago_", "noapp1");
		addColumnForLocations(rd, "No appointment 2 weeks ago", "No appointment 2 weeks ago_", "noapp2");
		
		addColumnForLocations(rd, "Missed appointment >2 <=3 weeks", "Missed appointment >2 <=3 weeks_", "2msd");
		addColumnForLocations(rd, "Missed appointment >2 <=3 weeks 1 week ago",
		    "Missed appointment >2 <=3 weeks 1 week ago_", "2msd1");
		addColumnForLocations(rd, "Missed appointment >2 <=3 weeks 2 weeks ago",
		    "Missed appointment >2 <=3 weeks 2 weeks ago_", "2msd2");
		
		addColumnForLocations(rd, "Missed appointment >3 <=8 weeks", "Missed appointment >3 <=8 weeks_", "3msd");
		addColumnForLocations(rd, "Missed appointment >3 <=8 weeks 1 week ago",
		    "Missed appointment >3 <=8 weeks 1 week ago_", "3msd1");
		addColumnForLocations(rd, "Missed appointment >3 <=8 weeks 2 weeks ago",
		    "Missed appointment >3 <=8 weeks 2 weeks ago_", "3msd2");
		
		addColumnForLocations(rd, "Missed appointment >8 <=12 weeks", "Missed appointment >8 <=12 weeks_", "8msd");
		addColumnForLocations(rd, "Missed appointment >8 <=12 weeks 1 week ago",
		    "Missed appointment >8 <=12 weeks 1 week ago_", "8msd1");
		addColumnForLocations(rd, "Missed appointment >8 <=12 weeks 2 weeks ago",
		    "Missed appointment >8 <=12 weeks 2 weeks ago_", "8msd2");
		
		addColumnForLocations(rd, "Missed appointment >12 weeks", "Missed appointment >12 weeks_", "12msd");
		addColumnForLocations(rd, "Missed appointment >12 weeks 1 week ago", "Missed appointment >12 weeks 1 week ago_",
		    "12msd1");
		addColumnForLocations(rd, "Missed appointment >12 weeks 2 weeks ago", "Missed appointment >12 weeks 2 weeks ago_",
		    "12msd2");
		
		h.replaceReportDefinition(rd);
		
		return rd;
	}
	
	private void createDimension() {
		// location-specific
		CohortDefinitionDimension md = new CohortDefinitionDimension();
		md.setName("artvst: ART program location_");
		md.addParameter(new Parameter("endDate", "End Date", Date.class));
		md.addParameter(new Parameter("startDate", "Start Date", Date.class));
		md.addParameter(new Parameter("location", "Location", Location.class));
		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Neno District Hospital"));
		md.addCohortDefinition("Neno", h.cohortDefinition("artvst: Enrolled in program (appt)_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Nsambe HC"));
		md.addCohortDefinition("Nsambe", h.cohortDefinition("artvst: Enrolled in program (appt)_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Magaleta HC"));
		md.addCohortDefinition("Magaleta", h.cohortDefinition("artvst: Enrolled in program (appt)_"), m2);
		
		m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Lisungwi Community Hospital"));
		md.addCohortDefinition("Lisungwi", h.cohortDefinition("artvst: Enrolled in program (appt)_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Matope HC"));
		md.addCohortDefinition("Matope", h.cohortDefinition("artvst: Enrolled in program (appt)_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
		m2.put("endDate", "${endDate}");
		m2.put("location", Context.getLocationService().getLocation("Chifunga HC"));
		md.addCohortDefinition("Chifunga", h.cohortDefinition("artvst: Enrolled in program (appt)_"), m2);
		
		h.replaceDimensionDefinition(md);
	}
	
	private void createIndicators() {
		// On ART
		h.newCountIndicator("artvst: On ART (appt)_", "artvst: On ART (appt)_", "onDate=${endDate}");
		h.newCountIndicator("artvst: On ART (appt) 1 week ago_", "artvst: On ART (appt)_", "onDate=${endDate-1w}");
		h.newCountIndicator("artvst: On ART (appt) 2 weeks ago_", "artvst: On ART (appt)_", "onDate=${endDate-2w}");
		
		// Missed appointments
		h.newCountIndicator("artvst: Missed Appointment >2 <=3 weeks_", "artvst: Missed Appointment_",
		    "value1=${endDate-2w},value2=${endDate-3w},onOrBefore=${endDate}");
		h.newCountIndicator("artvst: Missed Appointment >2 <=3 weeks 1 week ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-3w},value2=${endDate-4w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("artvst: Missed Appointment >2 <=3 weeks 2 weeks ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-4w},value2=${endDate-5w},onOrBefore=${endDate-2w}");
		
		h.newCountIndicator("artvst: Missed Appointment >3 <=8 weeks_", "artvst: Missed Appointment_",
		    "value1=${endDate-3w},value2=${endDate-8w},onOrBefore=${endDate}");
		h.newCountIndicator("artvst: Missed Appointment >3 <=8 weeks 1 week ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-4w},value2=${endDate-9w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("artvst: Missed Appointment >3 <=8 weeks 2 weeks ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-5w},value2=${endDate-10w},onOrBefore=${endDate-2w}");
		
		h.newCountIndicator("artvst: Missed Appointment >8 <=12 weeks_", "artvst: Missed Appointment_",
		    "value1=${endDate-8w},value2=${endDate-12w},onOrBefore=${endDate}");
		h.newCountIndicator("artvst: Missed Appointment >8 <=12 weeks 1 week ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-9w},value2=${endDate-13w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("artvst: Missed Appointment >8 <=12 weeks 2 weeks ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-10w},value2=${endDate-14w},onOrBefore=${endDate-2w}");
		
		h.newCountIndicator("artvst: Missed Appointment >12 weeks_", "artvst: Missed Appointment_",
		    "value1=${endDate-12w},onOrBefore=${endDate}");
		h.newCountIndicator("artvst: Missed Appointment >12 weeks 1 week ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-13w},onOrBefore=${endDate-1w}");
		h.newCountIndicator("artvst: Missed Appointment >12 weeks 2 weeks ago_", "artvst: Missed Appointment_",
		    "value1=${endDate-14w},onOrBefore=${endDate-2w}");
		
		// No appointment
		h.newCountIndicator("artvst: No appointment_", "artvst: No appointment_", "onOrBefore=${endDate}");
		h.newCountIndicator("artvst: No appointment 1 week ago_", "artvst: No appointment_", "onOrBefore=${endDate-1w}");
		h.newCountIndicator("artvst: No appointment 2 weeks ago_", "artvst: No appointment_", "onOrBefore=${endDate-2w}");
	}
	
	private void createCohortDefinitions() {
		// On ART at end of period
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("artvst: On ART (appt)_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM").getWorkflowByName("TREATMENT STATUS")
		        .getStateByName("ON ANTIRETROVIRALS"));
		// internal transfers are still under responsibility of original clinic
		states.add(Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM").getWorkflowByName("TREATMENT STATUS")
		        .getStateByName("TRANSFERRED INTERNALLY"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "endDate", Date.class));
		h.replaceCohortDefinition(iscd);
		
		// Missed Appointment
		DateObsCohortDefinition dod = new DateObsCohortDefinition();
		dod.setName("artvst: Missed Appointment_");
		dod.setTimeModifier(TimeModifier.MAX);
		dod.setQuestion(Context.getConceptService().getConceptByName("APPOINTMENT DATE"));
		dod.setOperator1(RangeComparator.LESS_THAN);
		dod.setOperator2(RangeComparator.GREATER_EQUAL);
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		dod.addParameter(new Parameter("value1", "to", Date.class));
		dod.addParameter(new Parameter("value2", "from", Date.class));
		h.replaceCohortDefinition(dod);
		
		// No Appointment
		dod = new DateObsCohortDefinition();
		dod.setName("artvst: No Appointment_");
		dod.setTimeModifier(TimeModifier.NO);
		dod.setQuestion(Context.getConceptService().getConceptByName("APPOINTMENT DATE"));
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(dod);
		
		// Death
		BirthAndDeathCohortDefinition badd = new BirthAndDeathCohortDefinition();
		badd.setName("artvst: Death_");
		badd.addParameter(new Parameter("diedOnOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(badd);
		
		// Alive
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("artvst: Alive_");
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("artvst: Death_"), ParameterizableUtil
		            .createParameterMappings("diedOnOrBefore=${endDate}")));
		ccd.setCompositionString("NOT 1");
		h.replaceCohortDefinition(ccd);
		
		// Alive On ART
		ccd = new CompositionCohortDefinition();
		ccd.setName("artvst: Alive On ART_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("artvst: Alive_"), ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("artvst: On ART (appt)_"), ParameterizableUtil
		            .createParameterMappings("onDate=${endDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		// Enrolled in program at location as of end date
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd
		        .setQuery("SELECT p.patient_id FROM patient p"
		                + " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND (pp.date_completed IS NULL OR pp.date_completed > :endDate) AND pp.location_id = :location"
		                + " WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.setName("artvst: Enrolled in program (appt)_");
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("program", "Program", Program.class));
		h.replaceCohortDefinition(scd);
		
		// Filter by patient names for testing
		scd = new SqlCohortDefinition();
		scd.setName("artvst: Person name filter_");
		scd
		        .setQuery("SELECT p.patient_id FROM patient p "
		                + "INNER JOIN person_name n ON p.patient_id = n.person_id AND n.voided = 0 AND n.family_name = :family_name "
		                + "WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.addParameter(new Parameter("family_name", "Family name", String.class));
		h.replaceCohortDefinition(scd);
		
		// Alive On ART with test names
		ccd = new CompositionCohortDefinition();
		ccd.setName("artvst: Alive On ART for appointment test_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("artvst: Alive On ART_"), ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("artvst: Person name filter_"), ParameterizableUtil
		            .createParameterMappings("family_name=appointment")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);
	}
	
	public void deleteReportElements() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("ART Weekly Visit Overview (Excel)_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("ART Weekly Visit Breakdown (ART Team)_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("ART Weekly Visit Breakdown (EMR Team)_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		// todo, also purge internal dataset for ART Appointments_
		h.purgeDefinition(PeriodIndicatorReportDefinition.class, "ART Weekly Visit_");
		h.purgeDefinition(DataSetDefinition.class, "ART Weekly Visit_ Data Set");
		
		h.purgeDimension("artvst: ART program location_");
		
		h.purgeDefinition(CohortDefinition.class, "artvst: On ART (appt)_");
		h.purgeDefinition(CohortDefinition.class, "artvst: Missed Appointment_");
		h.purgeDefinition(CohortDefinition.class, "artvst: No Appointment_");
		h.purgeDefinition(CohortDefinition.class, "artvst: Death_");
		h.purgeDefinition(CohortDefinition.class, "artvst: Alive_");
		h.purgeDefinition(CohortDefinition.class, "artvst: Alive on ART_");
		h.purgeDefinition(CohortDefinition.class, "artvst: Enrolled in program (appt)_");
		h.purgeDefinition(CohortDefinition.class, "artvst: Person name filter_");
		h.purgeDefinition(CohortDefinition.class, "artvst: Alive On ART for appointment test_");
		purgeIndicator("artvst: On ART (appt)");
		purgeIndicator("artvst: Missed Appointment >2 <=3 weeks");
		purgeIndicator("artvst: Missed Appointment >3 <=8 weeks");
		purgeIndicator("artvst: Missed Appointment >8 <=12 weeks");
		purgeIndicator("artvst: Missed Appointment >12 weeks");
		purgeIndicator("artvst: No appointment");
	}
	
	private void purgeIndicator(String name) {
		h.purgeDefinition(CohortIndicator.class, name + "_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago_");
	}
	
	public void addColumnForLocations(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator,
	                                  String indicatorKey) {
		// location-specific
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " at Neno", h
		        .cohortIndicator("artvst: " + indicator), h.hashMap("Location", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " at Magaleta", h
		        .cohortIndicator("artvst: " + indicator), h.hashMap("Location", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " at Nsambe", h
		        .cohortIndicator("artvst: " + indicator), h.hashMap("Location", "Nsambe"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "lsi", displayNamePrefix + " at Lisgunwi", h
		        .cohortIndicator("artvst: " + indicator), h.hashMap("Location", "Lisungwi"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mte", displayNamePrefix + " at Matope", h
		        .cohortIndicator("artvst: " + indicator), h.hashMap("Location", "Matope"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "cfa", displayNamePrefix + " at Chifunga", h
		        .cohortIndicator("artvst: " + indicator), h.hashMap("Location", "Chifunga"));
	}
}
