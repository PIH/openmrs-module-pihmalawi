package org.openmrs.module.pihmalawi.reports.setup;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.renderer.ArtMissedAppointmentBreakdownRenderer;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupArtMissedAppointment extends SetupGenericMissedAppointment {

	protected boolean upperNeno;
	protected HivMetadata hivMetadata;

	public SetupArtMissedAppointment(ReportHelper helper, boolean upperNeno) {
		super(helper);
		hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
		this.upperNeno = upperNeno;
		if (upperNeno) {
			configure("ART Missed Appointment Upper Neno", "artappt", hivMetadata.getTreatmentStatusWorkfow(), hivMetadata.getUpperNenoHivStaticLocations(), ArtMissedAppointmentBreakdownRenderer.class.getName());
		} else {
			configure("ART Missed Appointment Lower Neno", "artappt", hivMetadata.getTreatmentStatusWorkfow(), hivMetadata.getLowerNenoHivStaticLocations(), ArtMissedAppointmentBreakdownRenderer.class.getName());
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
			properties.put("loc7name", "Luwani");
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
		return hivMetadata.getArtEncounterTypes();
	}

	@Override
	protected PatientIdentifierType getPatientIdentifierType() {
		return Context.getPatientService().getPatientIdentifierTypeByName(
				"ARV Number");
	}

	protected void createBaseCohort(PeriodIndicatorReportDefinition rd) {
		rd.setBaseCohortDefinition(h.cohortDefinition(reportTag + ": On ART or internal transfer_"), ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		addColumnForLocations(rd, "On ART", "On ART or internal transfer_", "base");
		addColumnForLocations(rd, "On ART 1 week ago", "On ART or internal transfer 1 week ago_", "base1");
		addColumnForLocations(rd, "On ART 2 weeks ago", "On ART or internal transfer 2 weeks ago_", "base2");
	}

	protected void createCohortDefinitions() {
		super.createCohortDefinitions();

		CohortDefinition cd1 = ApzuReportElementsArt.onArtOnDate(reportTag);
		CohortDefinition cd2 = ApzuReportElementsArt.transferredInternallyOnDate(reportTag);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(reportTag + ": On ART or internal transfer_");
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches().put("1", new Mapped(cd1, h.parameterMap("onDate", "${endDate}")));
		ccd.getSearches().put("2", new Mapped(cd2, h.parameterMap("onDate", "${endDate}")));
		ccd.setCompositionString("1 OR 2");
		h.replaceCohortDefinition(ccd);
	}

	protected void createIndicators() {
		super.createIndicators();
		h.newCountIndicator(reportTag + ": On ART or internal transfer_", reportTag + ": On ART or internal transfer_", "endDate=${endDate}");
		h.newCountIndicator(reportTag + ": On ART or internal transfer 1 week ago_", reportTag + ": On ART or internal transfer_", "endDate=${endDate-1w}");
		h.newCountIndicator(reportTag + ": On ART or internal transfer 2 weeks ago_", reportTag + ": On ART or internal transfer_", "endDate=${endDate-2w}");
	}

	public void deleteReportElements() {
		super.deleteReportElements();
		h.purgeDefinition(CohortDefinition.class, reportTag + ": On ART_");
		h.purgeDefinition(CohortDefinition.class, reportTag + ": On ART or internal transfer_");
		h.purgeDefinition(CohortDefinition.class, reportTag + ": Person name filter_");
		h.purgeDefinition(CohortDefinition.class, reportTag + ": On ART for appointment test_");
		purgeIndicator(reportTag + ": On ART");
	}
}
