package org.openmrs.module.pihmalawi.reporting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
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
							"Lisungwi Community Hospital"), 
							Context.getLocationService().getLocation("Chifunga HC"),
							Context.getLocationService().getLocation("Matope HC"),
					true);

		}
	}

	public void setup(boolean useTestPatientCohort) throws Exception {
		super.setup();
	}

	@Override
	protected List<EncounterType> getEncounterTypes() {
		return Arrays.asList(h.encounterType("PART_INITIAL"), h.encounterType("PART_FOLLOWUP"));
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
		iscd.setName(reportTag + ": Ever Following_");
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

		// no paper record, simplified as i should check the obs in this encounter
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName(reportTag + ": no paper record_");
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date markDate = dfm.parse("2011-04-07");
			ecd.setOnOrAfter(markDate);
//			ecd.setOnOrBefore(markDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ecd.setEncounterTypeList(Arrays.asList(h.encounterType("ADMINISTRATION")));
		h.replaceCohortDefinition(ecd);

		// Not marked as inactive == has paper record
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(reportTag + ": Following_");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition(reportTag + ": Ever Following_"),
								ParameterizableUtil
										.createParameterMappings("onDate=${onDate}")));
		ccd.getSearches().put(
				"2",
				new Mapped(h.cohortDefinition(reportTag
						+ ": no paper record_"), ParameterizableUtil
						.createParameterMappings("")));
		ccd.setCompositionString("1 AND NOT 2");
		h.replaceCohortDefinition(ccd);
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
