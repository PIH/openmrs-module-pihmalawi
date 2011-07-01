package org.openmrs.module.pihmalawi.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

public class SetupArtMissedAppointment extends SetupGenericMissedAppointment {

	boolean upperNeno;

	public SetupArtMissedAppointment(Helper helper, boolean upperNeno) {
		super(helper);
		this.upperNeno = upperNeno;
		if (upperNeno) {
			configure(
					"ART Missed Appointment Upper Neno",
					"artappt",
					Context.getProgramWorkflowService().getProgramByName(
							"HIV PROGRAM"),
					Context.getLocationService().getLocation(
							"Neno District Hospital"), Context
							.getLocationService().getLocation("Magaleta HC"),
							Context.getLocationService().getLocation("Nsambe HC"),
							Context.getLocationService().getLocation("Neno Mission HC"),
					false);
		} else {
			configure(
					"ART Missed Appointment Lower Neno",
					"artappt",
					Context.getProgramWorkflowService().getProgramByName(
							"HIV PROGRAM"),
					Context.getLocationService().getLocation(
							"Lisungwi Community Hospital"), Context
							.getLocationService().getLocation("Chifunga HC"),
							Context.getLocationService().getLocation("Matope HC"),
							Context.getLocationService().getLocation("Zalewa HC"),
							Context.getLocationService().getLocation("Nkhula Falls RHC"),
							null,
					false);
		}
	}

	protected Map excelOverviewProperties() {
		Map properties = new HashMap();
		if (upperNeno) {
			properties.put("title", "ART Missed Appointment - Upper Neno");
			properties.put("baseCohort", "On ART");
			properties.put("loc1name", "Neno");
			properties.put("loc2name", "Magaleta");
			properties.put("loc3name", "Nsambe");
			properties.put("loc4name", "Neno Mission");
		} else {
			properties.put("title", "ART Missed Appointment - Lower Neno");
			properties.put("baseCohort", "On ART");
			properties.put("loc1name", "Lisungwi");
			properties.put("loc2name", "Chifunga");
			properties.put("loc3name", "Matope");
			properties.put("loc4name", "Zalewa");
			properties.put("loc5name", "Nkhula Falls");
		}
		return properties;
	}

	public void setup(boolean useTestPatientCohort) throws Exception {
		super.setup();
	}

	@Override
	protected List<EncounterType> getEncounterTypes() {
		return Arrays.asList(h.encounterType("ART_INITIAL"), h.encounterType("ART_FOLLOWUP"));
	}

	@Override
	protected PatientIdentifierType getPatientIdentifierType() {
		return Context.getPatientService().getPatientIdentifierTypeByName(
				"ARV Number");
	}

	protected void createBaseCohort(PeriodIndicatorReportDefinition rd) {
		// String cohort = (useTestPatientCohort ?
		// "artvst: Alive On ART for appointment test_" :
		// "artvst: Alive On ART_");
		rd.setBaseCohortDefinition(h.cohortDefinition(reportTag + ": On ART_"),
				ParameterizableUtil
						.createParameterMappings("onDate=${endDate}"));

		addColumnForLocations(rd, "On ART", "On ART_", "base");
		addColumnForLocations(rd, "On ART 1 week ago", "On ART 1 week ago_",
				"base1");
		addColumnForLocations(rd, "On ART 2 weeks ago", "On ART 2 weeks ago_",
				"base2");
	}

	protected void createCohortDefinitions() {
		super.createCohortDefinitions();

		// On ART at end of period
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": On ART_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV PROGRAM")
				.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("ON ANTIRETROVIRALS"));
		// internal transfers are still under responsibility of original clinic
		// states.add(Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM").getWorkflowByName("TREATMENT STATUS")
		// .getStateByName("TRANSFERRED INTERNALLY"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);

		// Filter by patient names for testing
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(reportTag + ": Person name filter_");
		scd.setQuery("SELECT p.patient_id FROM patient p "
				+ "INNER JOIN person_name n ON p.patient_id = n.person_id AND n.voided = 0 AND n.family_name = :family_name "
				+ "WHERE p.voided = 0 GROUP BY p.patient_id");
		scd.addParameter(new Parameter("family_name", "Family name",
				String.class));
		h.replaceCohortDefinition(scd);

		// Alive On ART with test names
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(reportTag + ": On ART for appointment test_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition(reportTag + ": On ART_"),
								ParameterizableUtil
										.createParameterMappings("endDate=${endDate}")));
		ccd.getSearches().put(
				"2",
				new Mapped(h.cohortDefinition(reportTag
						+ ": Person name filter_"), ParameterizableUtil
						.createParameterMappings("family_name=appointment")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);

	}

	protected void createIndicators() {
		super.createIndicators();

		h.newCountIndicator(reportTag + ": On ART_", reportTag + ": On ART_",
				"onDate=${endDate}");
		h.newCountIndicator(reportTag + ": On ART 1 week ago_", reportTag
				+ ": On ART_", "onDate=${endDate-1w}");
		h.newCountIndicator(reportTag + ": On ART 2 weeks ago_", reportTag
				+ ": On ART_", "onDate=${endDate-2w}");
	}

	public void deleteReportElements() {
		super.deleteReportElements();

		h.purgeDefinition(CohortDefinition.class, reportTag + ": On ART_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": Person name filter_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": On ART for appointment test_");
		purgeIndicator(reportTag + ": On ART");
	}
}
