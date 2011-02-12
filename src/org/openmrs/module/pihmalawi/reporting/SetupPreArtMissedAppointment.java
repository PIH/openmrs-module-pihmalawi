package org.openmrs.module.pihmalawi.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

public class SetupPreArtMissedAppointment extends SetupGenericMissedAppointment {

	boolean upperNeno;

	public SetupPreArtMissedAppointment(Helper helper, boolean upperNeno) {
		super(helper);
		this.upperNeno = upperNeno;
		if (upperNeno) {
			configure(
					"Pre-ART Missed Appointment",
					"partappt",
					Context.getProgramWorkflowService().getProgramByName(
							"HIV PROGRAM"),
					Context.getLocationService().getLocation(
							"Neno District Hospital"), Context
							.getLocationService().getLocation("Magaleta HC"),
					Context.getLocationService().getLocation("Nsambe HC"), true);
		} else {
			configure(
					"Pre-ART Missed Appointment",
					"partappt",
					Context.getProgramWorkflowService().getProgramByName(
							"HIV PROGRAM"),
					Context.getLocationService().getLocation(
							"Lisungwi Community Hospital"), Context
							.getLocationService().getLocation("Matope HC"),
					Context.getLocationService().getLocation("Chifunga HC"),
					true);

		}
	}

	public void setup(boolean useTestPatientCohort) throws Exception {
		super.setup();
	}

	@Override
	protected Collection<EncounterType> getEncounterTypes() {
		Collection<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(Context.getEncounterService().getEncounterType(
				"PART_INITIAL"));
		encounterTypes.add(Context.getEncounterService().getEncounterType(
				"PART_FOLLOWUP"));
		return encounterTypes;
	}

	@Override
	protected PatientIdentifierType getPatientIdentifierType() {
		return Context.getPatientService().getPatientIdentifierTypeByName(
				"PART Number");
	}

	protected Map excelOverviewProperties() {
		Map properties = new HashMap();
		if (upperNeno) {
			properties.put("title", "Pre-ART Missed Appointment - Upper Neno");
			properties.put("baseCohort", "Following");
			properties.put("loc1name", "Neno");
			properties.put("loc2name", "Magaleta");
			properties.put("loc3name", "Nsambe");
		} else {
			properties.put("title", "Pre-ART Missed Appointment - Lower Neno");
			properties.put("baseCohort", "Following");
			properties.put("loc1name", "Lisungwi");
			properties.put("loc2name", "Chifunga");
			properties.put("loc3name", "Matope");
		}
		return properties;
	}

	protected void createBaseCohort(PeriodIndicatorReportDefinition rd) {
		// String cohort = (useTestPatientCohort ?
		// "artvst: Alive On ART for appointment test_" :
		// "artvst: Alive On ART_");
		rd.setBaseCohortDefinition(h.cohortDefinition(reportTag
				+ ": Following_"), ParameterizableUtil
				.createParameterMappings("onDate=${endDate}"));

		addColumnForLocations(rd, "Following", "Following_", "base");
		addColumnForLocations(rd, "Following 1 week ago",
				"Following 1 week ago_", "base1");
		addColumnForLocations(rd, "Following 2 weeks ago",
				"Following 2 weeks ago_", "base2");
	}

	protected void createCohortDefinitions() {
		super.createCohortDefinitions();

		// Following at end of period
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": Following_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV PROGRAM")
				.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("FOLLOWING"));
		// internal transfers are still under responsibility of original clinic
		// states.add(Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM").getWorkflowByName("TREATMENT STATUS")
		// .getStateByName("TRANSFERRED INTERNALLY"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
	}

	protected void createIndicators() {
		super.createIndicators();

		h.newCountIndicator(reportTag + ": Following_", reportTag
				+ ": Following_", "onDate=${endDate}");
		h.newCountIndicator(reportTag + ": Following 1 week ago_", reportTag
				+ ": Following_", "onDate=${endDate-1w}");
		h.newCountIndicator(reportTag + ": Following 2 weeks ago_", reportTag
				+ ": Following_", "onDate=${endDate-2w}");
	}

	public void deleteReportElements() {
		super.deleteReportElements();

		h.purgeDefinition(CohortDefinition.class, reportTag + ": Following_");
		purgeIndicator(reportTag + ": Following");
	}
}
