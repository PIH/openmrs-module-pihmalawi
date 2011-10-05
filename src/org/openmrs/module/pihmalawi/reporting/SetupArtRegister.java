package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.repository.ArtReportElements;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupArtRegister {

	/** List of encounter included in report */
	private final List<EncounterType> ENCOUNTER_TYPES;

	Helper h = new Helper();

	public SetupArtRegister(Helper helper) {
		h = helper;
		ENCOUNTER_TYPES = Arrays.asList(h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP"));
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition("artreg");
		h.replaceReportDefinition(rd);
		createHtmlBreakdown(rd);
		createCD4History(rd);
		createBMIHistory(rd);
		createWeightBreakdown(rd);
		createAppointmentAdherenceHistory(rd);

		return new ReportDefinition[] { rd };
	}

	protected ReportDesign createHtmlBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeFirstVisit(false);
		dsd.setIncludeMissedAppointmentColumns(false);
		dsd.setIncludeProgramOutcome(true);
		dsd.setIncludeWeight(true);
		dsd.setFirstTimeInProgramWorkflowState(Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"));
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setProgramWorkflow(h.programWorkflow("HIV program", "Treatment status"));
		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setEncounterTypes(ENCOUNTER_TYPES);

		return h.createHtmlBreakdown(rd, "ART Register_", m);
	}

	protected ReportDesign createWeightBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuNumericConceptPatientDataSetDefinition dsd = new ApzuNumericConceptPatientDataSetDefinition();
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setNumericConcept(Context.getConceptService().getConceptByName(
				"Weight (kg)"));

		return h.createHtmlBreakdown(rd, "ART Register Weight_", m);
	}

	protected ReportDesign createBMIHistory(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuBMIPatientDataSetDefinition dsd = new ApzuBMIPatientDataSetDefinition();
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setNumericConcept(Context.getConceptService().getConceptByName(
		"Weight (kg)"));

		return h.createHtmlBreakdown(rd, "ART Register BMI_", m);
	}

	protected ReportDesign createCD4History(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuNumericConceptPatientDataSetDefinition dsd = new ApzuNumericConceptPatientDataSetDefinition();
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setNumericConcept(Context.getConceptService().getConceptByName(
				"CD4 count"));

		return h.createHtmlBreakdown(rd, "ART Register CD4_", m);
	}

	protected ReportDesign createAppointmentAdherenceHistory(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		ApzuAppointmentAdherencePatientDataSetDefinition dsd = new ApzuAppointmentAdherencePatientDataSetDefinition();
		m.put("register", new Mapped<DataSetDefinition>(dsd, null));

		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setEncounterTypes(Arrays.asList(Context.getEncounterService()
				.getEncounterType("ART_INITIAL"), Context.getEncounterService()
				.getEncounterType("ART_FOLLOWUP")));
		return h.createHtmlBreakdown(rd, "ART Register Appointment Adherence",
				m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("ART Register")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "ART_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "ART Register_");
		h.purgeAll("artreg");
	}

	private ReportDefinition createReportDefinition(String prefix) {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.setName("ART Register_");
		rd.setupDataSetDefinition();

		CohortDefinition cd = ArtReportElements
				.everOnArtAtLocationStartedOnOrBefore(prefix);
		CohortIndicator i = h.newCountIndicator(prefix + "Register_", cd
				.getName(), h.parameterMap("location", "${location}",
				"startedOnOrBefore", "${endDate}"));
		PeriodIndicatorReportUtil
				.addColumn(rd, "register", "Register", i, null);

		return rd;
	}
}
