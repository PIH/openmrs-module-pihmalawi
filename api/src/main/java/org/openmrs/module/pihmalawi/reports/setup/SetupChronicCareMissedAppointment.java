package org.openmrs.module.pihmalawi.reports.setup;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.renderer.ChronicCareMissedAppointmentBreakdownRenderer;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupChronicCareMissedAppointment extends
		SetupGenericMissedAppointment {

	boolean upperNeno;

	ChronicCareMetadata ccMetadata = new ChronicCareMetadata();

	public SetupChronicCareMissedAppointment(ReportHelper helper) {
		super(helper);

		configure(
				"Chronic Care Missed Appointment Neno", "ccappt",
				ccMetadata.getChronicCareTreatmentStatusWorkflow(),
				Arrays.asList(ccMetadata.getNenoHospital()),
				ChronicCareMissedAppointmentBreakdownRenderer.class.getName()
		);
	}

	protected Map excelOverviewProperties() {
		Map properties = new HashMap();
		properties.put("title", "Chronic Care Missed Appointment Neno");
		properties.put("baseCohort", "In CC");
		properties.put("loc1name", "Neno");
		return properties;
	}

	public void setup(boolean useTestPatientCohort) throws Exception {
		super.setup();
	}

	@Override
	protected List<EncounterType> getEncounterTypes() {
		return ccMetadata.getChronicCareEncounterTypes();
	}

	@Override
	protected PatientIdentifierType getPatientIdentifierType() {
		return Context.getPatientService().getPatientIdentifierTypeByName(
				"National id");
	}

	protected void createBaseCohort(PeriodIndicatorReportDefinition rd) {
		rd.setBaseCohortDefinition(h.cohortDefinition(reportTag + ": In CC_"),
				ParameterizableUtil
						.createParameterMappings("onDate=${endDate}"));

		addColumnForLocations(rd, "In CC", "In CC_", "base");
		addColumnForLocations(rd, "In CC 1 week ago", "In CC 1 week ago_",
				"base1");
		addColumnForLocations(rd, "In CC 2 weeks ago", "In CC 2 weeks ago_",
				"base2");
	}

	protected void createCohortDefinitions() {
		super.createCohortDefinitions();

		// In CC at end of period
		// InProgramCohortDefinition iscd = new InProgramCohortDefinition();
		// iscd.setName(reportTag + ": In CC_");
		// iscd.setPrograms(Arrays.asList(Context.getProgramWorkflowService()
		// .getProgramByName("CHRONIC CARE PROGRAM")));
		// iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		// h.replaceCohortDefinition(iscd);

		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(ReportingConstants.LOCATION_PARAMETER);
		ecd.addParameter(new Parameter("onDate", "onDate", Date.class));
		// ecd.addParameter(new Parameter("locationList", "locationList",
		// List.class));
		ecd.setName(reportTag + ": In CC_");
		ecd.setEncounterTypeList(ccMetadata.getChronicCareEncounterTypes());
		h.replaceCohortDefinition(ecd);
	}

	protected void createDimension_unused() {
		// location-specific
		CohortDefinitionDimension md = new CohortDefinitionDimension();
		md.setName(reportTag + ": program location_");
		md.addParameter(new Parameter("endDate", "End Date", Date.class));
		md.addParameter(new Parameter("startDate", "Start Date", Date.class));
		md.addParameter(new Parameter("location", "Location", Location.class));

		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("onDate", "${endDate}");
		// m2.put("locationList", Arrays.asList(location1));
		md.addCohortDefinition("loc1",
				h.cohortDefinition(reportTag + ": In CC_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("onDate", "${endDate}");
		// m2.put("locationList", Arrays.asList(location2));
		md.addCohortDefinition("loc2",
				h.cohortDefinition(reportTag + ": In CC_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("onDate", "${endDate}");
		// m2.put("locationList", Arrays.asList(location3));
		md.addCohortDefinition("loc3",
				h.cohortDefinition(reportTag + ": In CC_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("onDate", "${endDate}");
		// m2.put("locationList", Arrays.asList(location4));
		md.addCohortDefinition("loc4",
				h.cohortDefinition(reportTag + ": In CC_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("onDate", "${endDate}");
		// m2.put("locationList", Arrays.asList(location5));
		md.addCohortDefinition("loc5",
				h.cohortDefinition(reportTag + ": In CC_"), m2);
		m2 = new HashMap<String, Object>();
		m2.put("onDate", "${endDate}");
		// m2.put("locationList", Arrays.asList(location6));
		md.addCohortDefinition("loc6",
				h.cohortDefinition(reportTag + ": In CC_"), m2);
		h.replaceDimensionDefinition(md);
	}

	protected void createIndicators() {
		super.createIndicators();

		h.newCountIndicator(reportTag + ": In CC_", reportTag + ": In CC_",
				"onDate=${endDate}");
		h.newCountIndicator(reportTag + ": In CC 1 week ago_", reportTag
				+ ": In CC_", "onDate=${endDate-1w}");
		h.newCountIndicator(reportTag + ": In CC 2 weeks ago_", reportTag
				+ ": In CC_", "onDate=${endDate-2w}");
	}

	public void deleteReportElements() {
		super.deleteReportElements();

		h.purgeDefinition(CohortDefinition.class, reportTag + ": In CC_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": Person name filter_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": In CC for appointment test_");
		purgeIndicator(reportTag + ": In CC");
	}
}
