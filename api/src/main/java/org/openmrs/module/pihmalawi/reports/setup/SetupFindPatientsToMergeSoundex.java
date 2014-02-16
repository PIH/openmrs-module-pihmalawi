package org.openmrs.module.pihmalawi.reports.setup;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SetupFindPatientsToMergeSoundex {

	private ReportHelper h;
	private HivMetadata hivMetadata;

	public SetupFindPatientsToMergeSoundex(ReportHelper helper) {
		h = helper;
		hivMetadata = new HivMetadata();
	}

	public ReportDefinition setup() throws Exception {
		delete();
		ReportDefinition rd = createReportDefinition();
		h.replaceReportDefinition(rd);
		createSoundexBreakdownPreART(rd);
		createSoundexBreakdownHCC(rd);
		return rd;
	}

	protected ReportDesign createSoundexBreakdownHCC(ReportDefinition rd) throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		FindPatientsToMergeSoundexDataSetDefinition dsd = new FindPatientsToMergeSoundexDataSetDefinition();
		dsd.setEncounterTypesToLookForDuplicates(hivMetadata.getPreArtEncounterTypes());
		dsd.setEncounterTypesForSummary(hivMetadata.getHivEncounterTypes());
		dsd.setProgramWorkflowForSummary(hivMetadata.getTreatmentStatusWorkfow());
		dsd.setPatientIdentifierTypeRequiredToLookForDuplicates(hivMetadata.getHccNumberIdentifierType());
		m.put("patients", new Mapped<DataSetDefinition>(dsd, null));
		return h.createHtmlBreakdown(rd, "Find patients to merge in HCC Soundex_", m);
	}

	protected ReportDesign createSoundexBreakdownPreART(ReportDefinition rd) throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		FindPatientsToMergeSoundexDataSetDefinition dsd = new FindPatientsToMergeSoundexDataSetDefinition();
		dsd.setEncounterTypesToLookForDuplicates(hivMetadata.getPreArtEncounterTypes());
		dsd.setEncounterTypesForSummary(hivMetadata.getHivAndExposedChildEncounterTypes());
		dsd.setProgramWorkflowForSummary(hivMetadata.getTreatmentStatusWorkfow());
		m.put("patients", new Mapped<DataSetDefinition>(dsd, null));
		return h.createHtmlBreakdown(rd, "Find patients to merge in Pre-ART Soundex_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("Find patients to merge")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "Find patients to merge (SLOW)_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "Find patients to merge (SLOW)_");
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
		docd.setEncounterTypeList(hivMetadata.getArtEncounterTypes());
		docd.setName("merge: ART Encounters");
		h.replaceCohortDefinition(docd);

		// just to catch all patients
		AgeCohortDefinition all = new AgeCohortDefinition();
		all.setName("merge: all patients");
		h.replaceCohortDefinition(all);
		CohortIndicator i = h.newCountIndicator("merge: Patients_", all.getName(), h.parameterMap());
		PeriodIndicatorReportUtil.addColumn(rd, "patients", "Patients", i, null);

		return rd;
	}

}
