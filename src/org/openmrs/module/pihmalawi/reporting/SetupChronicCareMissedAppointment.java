package org.openmrs.module.pihmalawi.reporting;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

public class SetupChronicCareMissedAppointment extends
		SetupGenericMissedAppointment {

	boolean upperNeno;

	public SetupChronicCareMissedAppointment(Helper helper) {
		super(helper);
		configure(
				"Chronic Care Missed Appointment Neno",
				"ccappt",
				Context.getProgramWorkflowService().getProgramByName(
						"CHRONIC CARE PROGRAM"), Context.getLocationService()
						.getLocation("Neno District Hospital"), Context
						.getLocationService().getLocation("Magaleta HC"),
				Context.getLocationService().getLocation("Nsambe HC"), false);
	}

	protected Map excelOverviewProperties() {
		Map properties = new HashMap();
		properties.put("title", "Chronic Care Missed Appointment Neno");
		properties.put("baseCohort", "In CC");
		properties.put("loc1name", "Neno");
		properties.put("loc2name", "(unused)");
		properties.put("loc3name", "(unused)");
		return properties;
	}

	public void setup(boolean useTestPatientCohort) throws Exception {
		super.setup();
	}

	@Override
	protected List<EncounterType> getEncounterTypes() {
		return Arrays.asList(h.encounterType("CHRONIC_CARE_INITIAL"),
				h.encounterType("CHRONIC_CARE_FOLLOWUP"));
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
		InProgramCohortDefinition iscd = new InProgramCohortDefinition();
		iscd.setName(reportTag + ": In CC_");
		iscd.setPrograms(Arrays.asList(Context.getProgramWorkflowService()
				.getProgramByName("CHRONIC CARE PROGRAM")));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
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
