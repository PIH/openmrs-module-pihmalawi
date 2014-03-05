package org.openmrs.module.pihmalawi.reports.setup;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.Metadata;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.dataset.HtmlBreakdownDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.renderer.GenericApzuBreakdownRenderer;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupAppointmentsForLocation {

	private final String PREFIX = "appt";

	ReportHelper h = new ReportHelper();
	HivMetadata hivMetadata = new HivMetadata();

	public SetupAppointmentsForLocation(ReportHelper helper) {
		h = helper;
	}

	public void setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition();
		h.replaceReportDefinition(rd);
		createHtmlBreakdown(rd);
	}

	protected ReportDesign createHtmlBreakdown(ReportDefinition rd)
			throws IOException, SerializationException {
		// location-specific
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		HtmlBreakdownDataSetDefinition dsd = new HtmlBreakdownDataSetDefinition();
		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number"));
		dsd.setHtmlBreakdownPatientRowClassname(GenericApzuBreakdownRenderer.class
				.getName());

		m.put("breakdown", Mapped.mapStraightThrough(dsd));

		return h.createHtmlBreakdown(rd, "Appointments_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("Appointments")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class,
				"Appointments_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "Appointments_");
		h.purgeAll(PREFIX);
	}

	private ReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setName("Appointments_");
		rd.setupDataSetDefinition();

		// hiv specific
		DateObsCohortDefinition docd = new DateObsCohortDefinition();
		docd.addParameter(new Parameter("locationList", "Location", Location.class));
		docd.addParameter(new Parameter("value1", "Appointment Date",
				Date.class));
		docd.setEncounterTypeList(ApzuReportElementsArt.hivEncounterTypes());
		docd.setQuestion(hivMetadata.getAppointmentDateConcept());
		docd.setTimeModifier(TimeModifier.LAST);
		docd.setName(PREFIX + ": HIV Appointments");
		docd.setOperator1(RangeComparator.EQUAL);
		h.replaceCohortDefinition(docd);

		// just to catch all patients
		AgeCohortDefinition all = new AgeCohortDefinition();
		all.setName(PREFIX + ": all patients");
		h.replaceCohortDefinition(all);
		CohortIndicator i = h.newCountIndicator(PREFIX + "Patients_", all.getName(),
				h.parameterMap());
		PeriodIndicatorReportUtil
				.addColumn(rd, "breakdown", "Breakdown", i, null);

		return rd;
	}
}
