package org.openmrs.module.pihmalawi.reporting;

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
import org.openmrs.module.pihmalawi.reporting.repository.ArtReportElements;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
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
					helper.programWorkflow("HIV program", "Treatment status"),
					Context.getLocationService().getLocation(
							"Neno District Hospital"), Context
							.getLocationService().getLocation("Magaleta HC"),
							Context.getLocationService().getLocation("Nsambe HC"),
							Context.getLocationService().getLocation("Neno Mission HC"),
							Context.getLocationService().getLocation("Matandani Rural Health Center"),
							Context.getLocationService().getLocation("Ligowe HC"),
					false);
		} else {
			configure(
					"ART Missed Appointment Lower Neno",
					"artappt",
					helper.programWorkflow("HIV program", "Treatment status"),
					Context.getLocationService().getLocation(
							"Lisungwi Community Hospital"), Context
							.getLocationService().getLocation("Chifunga HC"),
							Context.getLocationService().getLocation("Matope HC"),
							Context.getLocationService().getLocation("Zalewa HC"),
							Context.getLocationService().getLocation("Nkhula Falls RHC"),
							Context.getLocationService().getLocation("Luwani RHC"),
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
			properties.put("loc5name", "Matandani");
			properties.put("loc6name", "Ligowe");
		} else {
			properties.put("title", "ART Missed Appointment - Lower Neno");
			properties.put("baseCohort", "On ART");
			properties.put("loc1name", "Lisungwi");
			properties.put("loc2name", "Chifunga");
			properties.put("loc3name", "Matope");
			properties.put("loc4name", "Zalewa");
			properties.put("loc5name", "Nkhula Falls");
			properties.put("loc6name", "Luwani");
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
		rd.setBaseCohortDefinition(h.cohortDefinition(reportTag + ": On ART or internal transfer_"),
				ParameterizableUtil
						.createParameterMappings("endDate=${endDate}"));

		addColumnForLocations(rd, "On ART", "On ART or internal transfer_", "base");
		addColumnForLocations(rd, "On ART 1 week ago", "On ART or internal transfer 1 week ago_",
				"base1");
		addColumnForLocations(rd, "On ART 2 weeks ago", "On ART or internal transfer 2 weeks ago_",
				"base2");
	}

	protected void createCohortDefinitions() {
		super.createCohortDefinitions();

		CohortDefinition cd1 = null;
		CohortDefinition cd2 = null;
		
		cd1 = ArtReportElements.onArtOnDate(reportTag);

		cd2 = ArtReportElements.transferredInternallyOnDate(reportTag);
		

//		ArtReportElements.everOnArtStartedOnOrBefore(reportTag);
//		CohortDefinition internal = ArtReportElements.transferredInternallyFromArt(reportTag);
//		
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(reportTag + ": On ART or internal transfer_");
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								cd1,
								h.parameterMap("onDate", "${endDate}")));
		ccd.getSearches().put(
				"2",
				new Mapped(cd2, h.parameterMap("onDate", "${endDate}")));
		ccd.setCompositionString("1 OR 2");
		h.replaceCohortDefinition(ccd);

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
//		 ccd = new CompositionCohortDefinition();
//		ccd.setName(reportTag + ": On ART for appointment test_");
//		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
//		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
//		ccd.addParameter(new Parameter("location", "Location", Location.class));
//		ccd.getSearches()
//				.put("1",
//						new Mapped(
//								helpercohortDefinition(reportTag + ": On ART_"),
//								ParameterizableUtil
//										.createParameterMappings("endDate=${endDate}")));
//		ccd.getSearches().put(
//				"2",
//				new Mapped(helpercohortDefinition(reportTag
//						+ ": Person name filter_"), ParameterizableUtil
//						.createParameterMappings("family_name=appointment")));
//		ccd.setCompositionString("1 AND 2");
//		helperreplaceCohortDefinition(ccd);

	}

	protected void createIndicators() {
		super.createIndicators();

		h.newCountIndicator(reportTag + ": On ART or internal transfer_", reportTag + ": On ART or internal transfer_",
				"endDate=${endDate}");
		h.newCountIndicator(reportTag + ": On ART or internal transfer 1 week ago_", reportTag
				+ ": On ART or internal transfer_", "endDate=${endDate-1w}");
		h.newCountIndicator(reportTag + ": On ART or internal transfer 2 weeks ago_", reportTag
				+ ": On ART or internal transfer_", "endDate=${endDate-2w}");
	}

	public void deleteReportElements() {
		super.deleteReportElements();

		h.purgeDefinition(CohortDefinition.class, reportTag + ": On ART_");
		h.purgeDefinition(CohortDefinition.class, reportTag + ": On ART or internal transfer_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": Person name filter_");
		h.purgeDefinition(CohortDefinition.class, reportTag
				+ ": On ART for appointment test_");
		purgeIndicator(reportTag + ": On ART");
	}
	
	public  CohortDefinition transferredInternallyFromArtAtLocation(String prefix) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("onDate", "${onDate}");
		parameterMap.put("state", h.workflowState("HIV program", "Treatment status",
			"Transferred internally"));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName(prefix + ": Transferred internally from location from On ART_");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition(prefix + ": In state at location from On ART_"),
										parameterMap));
		cd.setCompositionString("1");
		h.replaceCohortDefinition(cd);
		return cd;
	}
	
	public  CohortDefinition inStateFromArtAtLocation(String prefix) {
		// Ever On Art at location with state
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName(prefix + ": In state at location from On ART_");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		cd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition(prefix
										+ ": In state at location_"),
								ParameterizableUtil
										.createParameterMappings("onDate=${onDate},location=${location},state=${state}")));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("endDate", "${onDate}");
		map.put("location", "${location}");
		map.put("state", h.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		cd.getSearches().put(
				"2",
				new Mapped(h.cohortDefinition(prefix
						+ ": Ever enrolled in program at location with state_"),
						map));
		cd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(cd);
		return cd;
	}
	
	public  CohortDefinition transferredInternallyFromArt(String prefix) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("onDate", "${onDate}");
		parameterMap.put("state", h.workflowState("HIV program", "Treatment status",
			"Transferred internally"));

		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName(prefix + ": Transferred internally from On ART_");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.getSearches()
		.put("1",
				new Mapped(
						h.cohortDefinition(prefix + ": In state_"),
								h.parameterMap("onDate", "${onDate}", "state", h.workflowState("HIV program", "Treatment status",
			"Transferred internally"))));
		cd.getSearches()
		.put("2",
				new Mapped(
						h.cohortDefinition(prefix + ": Ever on ART_"),
								h.parameterMap("startedOnOrBefore", "${onDate}")));
		cd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(cd);
		return cd;		
	}
	

}
