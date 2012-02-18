package org.openmrs.module.pihmalawi.reports.setup;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.pihmalawi.reports.dataset.FindPatientsToMergeSoundexDataSetDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupFindPatientsToMergeSoundex {

	private Helper h;

	public SetupFindPatientsToMergeSoundex(Helper helper) {
		h = helper;
	}

	public ReportDefinition setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition();
		h.replaceReportDefinition(rd);
//		createSoundexBreakdown(rd);
		createSoundexBreakdownPreART(rd);
		createSoundexBreakdownHCC(rd);
//		createSoundexSwapFirstLastNameBreakdown(rd);
		return rd;
	}

	protected ReportDesign createSoundexBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		FindPatientsToMergeSoundexDataSetDefinition dsd = new FindPatientsToMergeSoundexDataSetDefinition();
		m.put("patients", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, "Find patients to merge Soundex_", m);
	}

	protected ReportDesign createSoundexBreakdownHCC(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		FindPatientsToMergeSoundexDataSetDefinition dsd = new FindPatientsToMergeSoundexDataSetDefinition();
		dsd.setEncounterTypesToLookForDuplicates(Arrays.asList(
				h.encounterType("PART_INITIAL"),
				h.encounterType("PART_FOLLOWUP")));
		dsd.setEncounterTypesForSummary(Arrays.asList(
				h.encounterType("PART_INITIAL"),
				h.encounterType("PART_FOLLOWUP"),
				h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP")));
		dsd.setProgramWorkflowForSummary(h.programWorkflow("HIV program",
				"Treatment status"));
		dsd.setPatientIdentifierTypeRequiredToLookForDuplicates(Context.getPatientService()
				.getPatientIdentifierTypeByName("HCC Number"));
		m.put("patients", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd,
				"Find patients to merge in HCC Soundex_", m);
	}

	protected ReportDesign createSoundexBreakdownPreART(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		FindPatientsToMergeSoundexDataSetDefinition dsd = new FindPatientsToMergeSoundexDataSetDefinition();
		dsd.setEncounterTypesToLookForDuplicates(Arrays.asList(
				h.encounterType("PART_INITIAL"),
				h.encounterType("PART_FOLLOWUP")));
		dsd.setEncounterTypesForSummary(Arrays.asList(
				h.encounterType("PART_INITIAL"),
				h.encounterType("PART_FOLLOWUP"),
				h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP"),
				h.encounterType("EXPOSED_CHILD_INITIAL"),
				h.encounterType("EXPOSED_CHILD_FOLLOWUP")));
		dsd.setProgramWorkflowForSummary(h.programWorkflow("HIV program",
				"Treatment status"));
		m.put("patients", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd,
				"Find patients to merge in Pre-ART Soundex_", m);
	}

	protected ReportDesign createSoundexSwapFirstLastNameBreakdown(
			ReportDefinition rd) throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		FindPatientsToMergeSoundexDataSetDefinition dsd = new FindPatientsToMergeSoundexDataSetDefinition();
		dsd.setSwapFirstLastName(true);
		m.put("patients", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd,
				"Find patients to merge Soundex Swap First & Last Name_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("Find patients to merge")) {
				rs.purgeReportDesign(rd);
			}
		}

		h.purgeDefinition(DataSetDefinition.class,
				"Find patients to merge (SLOW)_ Data Set");
		h.purgeDefinition(ReportDefinition.class,
				"Find patients to merge (SLOW)_");
		h.purgeAll("merge: ");
	}

	private ReportDefinition createReportDefinition() {

		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName("Find patients to merge (SLOW)_");
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setupDataSetDefinition();

		// hiv specific
		EncounterCohortDefinition docd = new EncounterCohortDefinition();
		docd.setEncounterTypeList(ApzuReportElementsArt.hivEncounterTypes());
		docd.setName("merge: HIV Encounters");
		h.replaceCohortDefinition(docd);

		docd = new EncounterCohortDefinition();
		docd.setEncounterTypeList(Arrays.asList(h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP")));
		docd.setName("merge: ART Encounters");
		h.replaceCohortDefinition(docd);

		// just to catch all patients
		AgeCohortDefinition all = new AgeCohortDefinition();
		all.setName("merge: all patients");
		h.replaceCohortDefinition(all);
		CohortIndicator i = h.newCountIndicator("merge: Patients_",
				all.getName(), h.parameterMap());
		PeriodIndicatorReportUtil
				.addColumn(rd, "patients", "Patients", i, null);

		return rd;
	}

}
