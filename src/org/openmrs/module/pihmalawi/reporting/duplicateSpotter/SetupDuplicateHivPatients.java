package org.openmrs.module.pihmalawi.reporting.duplicateSpotter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.InProgramAtProgramLocationCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.serialization.SerializationException;

public class SetupDuplicateHivPatients {

	private Helper h;
	private Date onOrAfter;

	public SetupDuplicateHivPatients(Helper helper) {
		h = helper;

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -6);
		onOrAfter = c.getTime();
	}

	public ReportDefinition setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition();
		h.replaceReportDefinition(rd);
		createNnoEncounterBreakdown(rd);
		createSoundexBreakdown(rd);
		createSoundexSwapFirstLastNameBreakdown(rd);
		return rd;
	}

	protected ReportDesign createNnoEncounterBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		DuplicateSpotterDataSetDefinition dsd = new DuplicateSpotterDataSetDefinition();
		dsd.setNnoEncounterMatching(true);
		dsd.setSoundexCheck(false);
		dsd.setOnOrAfter(onOrAfter);
		m.put("dup: 1", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd,
				"Duplicate HIV patients: NNO Encounter_", m);
	}

	protected ReportDesign createSoundexBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		DuplicateSpotterDataSetDefinition dsd = new DuplicateSpotterDataSetDefinition();
		dsd.setNnoEncounterMatching(false);
		dsd.setSoundexCheck(true);
		dsd.setOnOrAfter(onOrAfter);
		m.put("entryHIV", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, "Duplicate HIV patients: Soundex_", m);
	}

	protected ReportDesign createSoundexSwapFirstLastNameBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		DuplicateSpotterDataSetDefinition dsd = new DuplicateSpotterDataSetDefinition();
		dsd.setNnoEncounterMatching(false);
		dsd.setSoundexCheck(true);
		dsd.setSoundexSwapFirstLastName(true);
		dsd.setOnOrAfter(onOrAfter);
		m.put("entryHIV", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, "Duplicate HIV patients: Soundex Swap First & Last Name_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("Duplicate HIV patients")) {
				rs.purgeReportDesign(rd);
			}
		}

		h.purgeDefinition(DataSetDefinition.class,
				"Duplicate HIV patients (SLOW)_ Data Set");
		h.purgeDefinition(ReportDefinition.class,
				"Duplicate HIV patients (SLOW)_");
		h.purgeAll("dup: ");
	}

	private ReportDefinition createReportDefinition() {

		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName("Duplicate HIV patients (SLOW)_");
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setupDataSetDefinition();

		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("dup: touchscreen HIV encounter_");
		ecd.setOnOrAfter(onOrAfter);
		ecd.setEncounterTypeList(Arrays.asList(h.encounterType("REGISTRATION")));
		ecd.setLocationList(Arrays.asList(h
				.location("Neno District Hospital - ART Clinic (NNO)")));
		h.replaceCohortDefinition(ecd);
		CohortIndicator i = h.newCountIndicator(
				"dup: touchscreen HIV encounter_",
				"dup: touchscreen HIV encounter_", h.parameterMap());
		PeriodIndicatorReportUtil.addColumn(rd, "touchHIV",
				"Touchscreen HIV encounter", i, null);

		ecd = new EncounterCohortDefinition();
		ecd.setName("dup: data entry HIV encounter_");
		ecd.setOnOrAfter(onOrAfter);
		ecd.setEncounterTypeList(Arrays.asList(h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP"),
				h.encounterType("PART_INITIAL"),
				h.encounterType("PART_FOLLOWUP"),
				h.encounterType("EID_INITIAL"), h.encounterType("EID_FOLLOWUP")));
		ecd.setLocationList(Arrays.asList(h.location("Neno District Hospital")));
		h.replaceCohortDefinition(ecd);
		i = h.newCountIndicator("dup: data entry HIV encounter_",
				"dup: data entry HIV encounter_", h.parameterMap());
		PeriodIndicatorReportUtil.addColumn(rd, "entryHIV",
				"Data entry HIV encounter", i, null);

		InverseCohortDefinition icd = new InverseCohortDefinition();
		icd.setName("dup: no data entry HIV encounter_");
		icd.setBaseDefinition(ecd);
		h.replaceCohortDefinition(icd);
		i = h.newCountIndicator("dup: no data entry HIV encounter_",
				"dup: no data entry HIV encounter_", h.parameterMap());
		PeriodIndicatorReportUtil.addColumn(rd, "noentryHIV",
				"No Data entry HIV encounter", i, null);

		// at location
		InProgramAtProgramLocationCohortDefinition iplcd = new InProgramAtProgramLocationCohortDefinition();
		iplcd.setName("dup: in HIV program_");
		iplcd.setLocation(h.location("Neno District Hospital"));
		iplcd.setPrograms(Arrays.asList(Context.getProgramWorkflowService()
				.getProgramByName("HIV PROGRAM")));
		h.replaceCohortDefinition(iplcd);
		i = h.newCountIndicator("dup: in HIV program_", "dup: in HIV program_",
				h.parameterMap());
		PeriodIndicatorReportUtil.addColumn(rd, "HIV", "In HIV program_", i,
				null);

		icd = new InverseCohortDefinition();
		icd.setName("dup: not in HIV program_");
		icd.setBaseDefinition(iplcd);
		h.replaceCohortDefinition(icd);
		i = h.newCountIndicator("dup: not in HIV program_",
				"dup: not in HIV program_", h.parameterMap());
		PeriodIndicatorReportUtil.addColumn(rd, "noHIV", "Not in HIV program",
				i, null);

		composition(rd, "dup: touchscreen and no data entry_", "dup: 1",
				"dup: touchscreen HIV encounter_",
				"dup: data entry HIV encounter_", "1 AND NOT 2");
		composition(rd, "dup: touchscreen and not in hiv program_", "dup: 2",
				"dup: touchscreen HIV encounter_", "dup: in HIV program_",
				"1 AND NOT 2");

		// todo: patient with arv number, but without national identifier

		return rd;
	}

	private void composition(PeriodIndicatorReportDefinition rd, String name,
			String key, String cohort1, String cohort2, String composition) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(name);
		ccd.getSearches().put("1",
				new Mapped(h.cohortDefinition(cohort1), h.parameterMap()));
		ccd.getSearches().put("2",
				new Mapped(h.cohortDefinition(cohort2), h.parameterMap()));
		ccd.setCompositionString(composition);
		h.replaceCohortDefinition(ccd);
		CohortIndicator i = h.newCountIndicator(name, name, h.parameterMap());
		PeriodIndicatorReportUtil.addColumn(rd, key, name, i, null);
	}
}
