package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupGenericMissedAppointment {

	protected Helper h = null;

	protected ProgramWorkflow programWorkflow = null;

	protected String reportName = null;

	protected String reportTag = null;

	protected Location location1 = null;

	protected Location location3 = null;

	protected Location location4 = null;

	protected Location location2 = null;

	protected Location location5 = null;

	protected Location location6 = null;

	protected boolean includeDefaulterActionTaken = false;

	public SetupGenericMissedAppointment(Helper helper) {
		h = helper;
	}

	protected void configure(String reportName, String reportTag,
			ProgramWorkflow programWorkflow, Location location1, Location location2,
			Location location3, Location location4, boolean includeDefaulterActionTaken) {
		this.reportName = reportName;
		this.reportTag = reportTag;
		this.programWorkflow = programWorkflow;
		this.location1 = location1;
		this.location2 = location2;
		this.location3 = location3;
		this.location4 = location4;
		this.includeDefaulterActionTaken = includeDefaulterActionTaken;
	}

	protected void configure(String reportName, String reportTag,
			ProgramWorkflow programWorkflow, Location location1, Location location2,
			Location location3, Location location4, Location location5, Location location6, boolean includeDefaulterActionTaken) {
		this.reportName = reportName;
		this.reportTag = reportTag;
		this.programWorkflow = programWorkflow;
		this.location1 = location1;
		this.location2 = location2;
		this.location3 = location3;
		this.location4 = location4;
		this.location5 = location5;
		this.location6 = location6;
		this.includeDefaulterActionTaken = includeDefaulterActionTaken;
	}

	public ReportDefinition[] setup() throws Exception {
		deleteReportElements();

		createCohortDefinitions();
		createIndicators();
		createDimension();
		ReportDefinition rd = createReportDefinition();
		h.createXlsOverview(rd, "generic_missed_appointment_overview.xls",
				reportName + " Overview (Excel)_", excelOverviewProperties());
		ReportDesign rdes = createHtmlBreakdownExternal(rd);
		// h.render(rdes, rd);
		createHtmlBreakdownInternal(rd);
//		createHtmlBreakdownDefaulterGis(rd);
		return (ReportDefinition[]) Arrays.asList(rd).toArray();
	}

	protected Map<? extends Object, ? extends Object> excelOverviewProperties() {
		return null;
	}

	protected ReportDesign createHtmlBreakdownExternal(ReportDefinition rd)
			throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(includeDefaulterActionTaken);
		m.put("3msdloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc6", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc6", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc6", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(getPatientIdentifierType());
		dsd.setEncounterTypes(getEncounterTypes());
		if (programWorkflow != null) {
			dsd.setProgramWorkflow(programWorkflow);
			dsd.setIncludeProgramOutcome(true);
		}

		return h.createHtmlBreakdown(rd,
				reportName + " Breakdown (>=3 weeks)_", m);
	}

	protected ReportDesign createHtmlBreakdownDefaulterGis(ReportDefinition rd)
			throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ArcGisPatientDataSetDefinition dsd = new ArcGisPatientDataSetDefinition();
		m.put("8msdloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("3msdloc6", new Mapped<DataSetDefinition>(dsd, null));
		m.put("8msdloc6", new Mapped<DataSetDefinition>(dsd, null));
		m.put("12msdloc6", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(getPatientIdentifierType());

		return h.createHtmlBreakdown(rd,
				reportName + " ArcGIS Breakdown (>=8 weeks)_", m);
	}

	protected List<EncounterType> getEncounterTypes() {
		return null;
	}

	protected PatientIdentifierType getPatientIdentifierType() {
		return null;
	}

	protected ReportDesign createHtmlBreakdownInternal(ReportDefinition rd)
			throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(includeDefaulterActionTaken);
		m.put("noapploc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdloc1", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noapploc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdloc2", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noapploc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdloc3", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noapploc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdloc4", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noapploc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdloc5", new Mapped<DataSetDefinition>(dsd, null));
		m.put("noapploc6", new Mapped<DataSetDefinition>(dsd, null));
		m.put("2msdloc6", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(getPatientIdentifierType());
		dsd.setEncounterTypes(getEncounterTypes());
		if (programWorkflow != null) {
			dsd.setProgramWorkflow(programWorkflow);
			dsd.setIncludeProgramOutcome(true);
		}

		return h.createHtmlBreakdown(rd, reportName
				+ " Breakdown (>=2 weeks <3 weeks)_", m);
	}

	protected ReportDefinition createReportDefinition() throws IOException {
		// art appointment report
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.addParameter(new Parameter("endDate", "End date (Sunday)",
				Date.class));

		rd.setName(reportName + "_");
		rd.setupDataSetDefinition();
		rd.addDimension("Location",
				h.cohortDefinitionDimension(reportTag + ": program location_"));

		createBaseCohort(rd);

		addColumnForLocations(rd, "No appointment", "No appointment_", "noapp");
		addColumnForLocations(rd, "No appointment 1 week ago",
				"No appointment 1 week ago_", "noapp1");
		addColumnForLocations(rd, "No appointment 2 weeks ago",
				"No appointment 2 weeks ago_", "noapp2");

		addColumnForLocations(rd, "Missed appointment >2 <=3 weeks",
				"Missed appointment >2 <=3 weeks_", "2msd");
		addColumnForLocations(rd, "Missed appointment >2 <=3 weeks 1 week ago",
				"Missed appointment >2 <=3 weeks 1 week ago_", "2msd1");
		addColumnForLocations(rd,
				"Missed appointment >2 <=3 weeks 2 weeks ago",
				"Missed appointment >2 <=3 weeks 2 weeks ago_", "2msd2");

		addColumnForLocations(rd, "Missed appointment >3 <=8 weeks",
				"Missed appointment >3 <=8 weeks_", "3msd");
		addColumnForLocations(rd, "Missed appointment >3 <=8 weeks 1 week ago",
				"Missed appointment >3 <=8 weeks 1 week ago_", "3msd1");
		addColumnForLocations(rd,
				"Missed appointment >3 <=8 weeks 2 weeks ago",
				"Missed appointment >3 <=8 weeks 2 weeks ago_", "3msd2");

		addColumnForLocations(rd, "Missed appointment >8 <=12 weeks",
				"Missed appointment >8 <=12 weeks_", "8msd");
		addColumnForLocations(rd,
				"Missed appointment >8 <=12 weeks 1 week ago",
				"Missed appointment >8 <=12 weeks 1 week ago_", "8msd1");
		addColumnForLocations(rd,
				"Missed appointment >8 <=12 weeks 2 weeks ago",
				"Missed appointment >8 <=12 weeks 2 weeks ago_", "8msd2");

		addColumnForLocations(rd, "Missed appointment >12 weeks",
				"Missed appointment >12 weeks_", "12msd");
		addColumnForLocations(rd, "Missed appointment >12 weeks 1 week ago",
				"Missed appointment >12 weeks 1 week ago_", "12msd1");
		addColumnForLocations(rd, "Missed appointment >12 weeks 2 weeks ago",
				"Missed appointment >12 weeks 2 weeks ago_", "12msd2");

		h.replaceReportDefinition(rd);

		return rd;
	}

	protected void createBaseCohort(PeriodIndicatorReportDefinition rd) {
		// template method
	}

	protected void createDimension() {
		// location-specific
		CohortDefinitionDimension md = new CohortDefinitionDimension();
		md.setName(reportTag + ": program location_");
		md.addParameter(new Parameter("endDate", "End Date", Date.class));
		md.addParameter(new Parameter("startDate", "Start Date", Date.class));
		md.addParameter(new Parameter("location", "Location", Location.class));
		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("program", programWorkflow.getProgram());
		m2.put("endDate", "${endDate}");
		m2.put("location", location1);
		md.addCohortDefinition("loc1",
				h.cohortDefinition(reportTag + ": Enrolled in program_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", programWorkflow.getProgram());
		m2.put("endDate", "${endDate}");
		m2.put("location", location2);
		md.addCohortDefinition("loc2",
				h.cohortDefinition(reportTag + ": Enrolled in program_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("program", programWorkflow.getProgram());
		m2.put("endDate", "${endDate}");
		m2.put("location", location3);
		md.addCohortDefinition("loc3",
				h.cohortDefinition(reportTag + ": Enrolled in program_"), m2);
		if (location4 != null) {
			m2 = new HashMap<String, Object>();
			m2.put("program", programWorkflow.getProgram());
			m2.put("endDate", "${endDate}");
			m2.put("location", location4);
			md.addCohortDefinition("loc4",
					h.cohortDefinition(reportTag + ": Enrolled in program_"), m2);
		}
		if (location5 != null) {
			m2 = new HashMap<String, Object>();
			m2.put("program", programWorkflow.getProgram());
			m2.put("endDate", "${endDate}");
			m2.put("location", location5);
			md.addCohortDefinition("loc5",
					h.cohortDefinition(reportTag + ": Enrolled in program_"), m2);
		}
		if (location6 != null) {
			m2 = new HashMap<String, Object>();
			m2.put("program", programWorkflow.getProgram());
			m2.put("endDate", "${endDate}");
			m2.put("location", location6);
			md.addCohortDefinition("loc6",
					h.cohortDefinition(reportTag + ": Enrolled in program_"), m2);
		}
		h.replaceDimensionDefinition(md);
	}

	protected void createIndicators() {
		// Missed appointments
		h.newCountIndicator(reportTag + ": Missed Appointment >2 <=3 weeks_",
				reportTag + ": Missed Appointment_",
				"value1=${endDate-2w},value2=${endDate-3w},onOrBefore=${endDate}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >2 <=3 weeks 1 week ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-3w},value2=${endDate-4w},onOrBefore=${endDate-1w}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >2 <=3 weeks 2 weeks ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-4w},value2=${endDate-5w},onOrBefore=${endDate-2w}");

		h.newCountIndicator(reportTag + ": Missed Appointment >3 <=8 weeks_",
				reportTag + ": Missed Appointment_",
				"value1=${endDate-3w},value2=${endDate-8w},onOrBefore=${endDate}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >3 <=8 weeks 1 week ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-4w},value2=${endDate-9w},onOrBefore=${endDate-1w}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >3 <=8 weeks 2 weeks ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-5w},value2=${endDate-10w},onOrBefore=${endDate-2w}");

		h.newCountIndicator(reportTag + ": Missed Appointment >8 <=12 weeks_",
				reportTag + ": Missed Appointment_",
				"value1=${endDate-8w},value2=${endDate-12w},onOrBefore=${endDate}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >8 <=12 weeks 1 week ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-9w},value2=${endDate-13w},onOrBefore=${endDate-1w}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >8 <=12 weeks 2 weeks ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-10w},value2=${endDate-14w},onOrBefore=${endDate-2w}");

		h.newCountIndicator(reportTag + ": Missed Appointment >12 weeks_",
				reportTag + ": Missed Appointment_",
				"value1=${endDate-12w},onOrBefore=${endDate}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >12 weeks 1 week ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-13w},onOrBefore=${endDate-1w}");
		h.newCountIndicator(reportTag
				+ ": Missed Appointment >12 weeks 2 weeks ago_", reportTag
				+ ": Missed Appointment_",
				"value1=${endDate-14w},onOrBefore=${endDate-2w}");

		// No appointment
		h.newCountIndicator(reportTag + ": No appointment_", reportTag
				+ ": No appointment_", "onOrBefore=${endDate}");
		h.newCountIndicator(reportTag + ": No appointment 1 week ago_",
				reportTag + ": No appointment_", "onOrBefore=${endDate-1w}");
		h.newCountIndicator(reportTag + ": No appointment 2 weeks ago_",
				reportTag + ": No appointment_", "onOrBefore=${endDate-2w}");
	}

	protected void createCohortDefinitions() {
		// Missed Appointment
		DateObsCohortDefinition dod = new DateObsCohortDefinition();
		dod.setName(reportTag + ": Missed Appointment_");
		dod.setTimeModifier(TimeModifier.MAX);
		dod.setQuestion(Context.getConceptService().getConceptByName(
				"APPOINTMENT DATE"));
		dod.setOperator1(RangeComparator.LESS_THAN);
		dod.setOperator2(RangeComparator.GREATER_EQUAL);
		dod.setEncounterTypeList(getEncounterTypes());
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		dod.addParameter(new Parameter("value1", "to", Date.class));
		dod.addParameter(new Parameter("value2", "from", Date.class));
		h.replaceCohortDefinition(dod);

		// No Appointment
		dod = new DateObsCohortDefinition();
		dod.setName(reportTag + ": No Appointment_");
		dod.setTimeModifier(TimeModifier.NO);
		dod.setQuestion(Context.getConceptService().getConceptByName(
				"APPOINTMENT DATE"));
		dod.setEncounterTypeList(getEncounterTypes());
		dod.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		h.replaceCohortDefinition(dod);

		// Enrolled in program at location as of end date
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setQuery("SELECT p.patient_id FROM patient p"
				+ " INNER JOIN patient_program pp ON p.patient_id = pp.patient_id AND pp.voided = 0 AND pp.program_id = :program AND pp.date_enrolled <= :endDate AND (pp.date_completed IS NULL OR pp.date_completed > :endDate) AND pp.location_id = :location"
				+ " WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.setName(reportTag + ": Enrolled in program_");
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("program", "Program", Program.class));
		h.replaceCohortDefinition(scd);
		
		AgeCohortDefinition acd = new AgeCohortDefinition();
		// there clearly is a more efficient way...
		acd.setName(reportTag + ": intentionally empty_");
		acd.setMinAge(1000);
		acd.setMinAgeUnit(DurationUnit.YEARS);
		h.replaceCohortDefinition(acd);
		h.newCountIndicator(reportTag
				+ ": intentionally empty_", reportTag
				+ ": intentionally empty_",
				"");
	}

	public void deleteReportElements() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals(reportName + " Overview (Excel)_")) {
				rs.purgeReportDesign(rd);
			}
			if (rd.getName().equals(reportName + " Breakdown (>=3 weeks)_")) {
				rs.purgeReportDesign(rd);
			}
			if (rd.getName().equals(reportName + " ArcGIS Breakdown (>=8 weeks)_")) {
				rs.purgeReportDesign(rd);
			}
			if (rd.getName().equals(
					reportName + " Breakdown (>=2 weeks <3 weeks)_")) {
				rs.purgeReportDesign(rd);
			}
		}
//		h.purgeAll(reportTag);
		h.purgeDefinition(PeriodIndicatorReportDefinition.class, reportName
				+ "_");
		h.purgeDefinition(DataSetDefinition.class, reportName + "_ Data Set");

		h.purgeDimension(reportTag + ": program location_");

		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": Missed Appointment_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": No Appointment_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": Enrolled in program_");
		purgeIndicator(reportTag + ": Missed Appointment >2 <=3 weeks");
		purgeIndicator(reportTag + ": Missed Appointment >3 <=8 weeks");
		purgeIndicator(reportTag + ": Missed Appointment >8 <=12 weeks");
		purgeIndicator(reportTag + ": Missed Appointment >12 weeks");
		purgeIndicator(reportTag + ": No appointment");
	}

	protected void purgeIndicator(String name) {
		h.purgeDefinition(CohortIndicator.class, name + "_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago_");
	}

	protected void addColumnForLocations(PeriodIndicatorReportDefinition rd,
			String displayNamePrefix, String indicator, String indicatorKey) {
		// location-specific
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc1",
				displayNamePrefix + " (Location 1)",
				h.cohortIndicator(reportTag + ": " + indicator),
				h.hashMap("Location", "loc1"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc2",
				displayNamePrefix + " (Location 2)",
				h.cohortIndicator(reportTag + ": " + indicator),
				h.hashMap("Location", "loc2"));
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc3",
				displayNamePrefix + " (Location 3)",
				h.cohortIndicator(reportTag + ": " + indicator),
				h.hashMap("Location", "loc3"));
		if (location4 != null) {
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc4",
					displayNamePrefix + " (Location 4)",
					h.cohortIndicator(reportTag + ": " + indicator),
					h.hashMap("Location", "loc4"));
		} else {
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc4",
					displayNamePrefix + " (Location 4)",
					h.cohortIndicator(reportTag + ": intentionally empty_"),
					h.hashMap());
		}
		if (location5 != null) {
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc5",
					displayNamePrefix + " (Location 5)",
					h.cohortIndicator(reportTag + ": " + indicator),
					h.hashMap("Location", "loc5"));
		} else {
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc5",
					displayNamePrefix + " (Location 5)",
					h.cohortIndicator(reportTag + ": intentionally empty_"),
					h.hashMap());
		}
		if (location6 != null) {
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc6",
					displayNamePrefix + " (Location 6)",
					h.cohortIndicator(reportTag + ": " + indicator),
					h.hashMap("Location", "loc6"));
		} else {
			PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "loc6",
					displayNamePrefix + " (Location 6)",
					h.cohortIndicator(reportTag + ": intentionally empty_"),
					h.hashMap());
		}
	}
}
