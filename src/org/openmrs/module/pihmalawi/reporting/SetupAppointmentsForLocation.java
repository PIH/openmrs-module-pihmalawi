package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.repository.ArtReportElements;
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

	Helper h = new Helper();

	public SetupAppointmentsForLocation(Helper helper) {
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

		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeFirstVisit(false);
		dsd.setIncludeMissedAppointmentColumns(false);
		dsd.setIncludeProgramOutcome(false);
		dsd.setIncludeWeight(false);
		dsd.setIncludeProgramEnrollments(true);
		m.put("patients", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, "Appointments_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals("Appointments_")) {
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
		docd.setEncounterTypeList(ArtReportElements.hivEncounterTypes());
		docd.setQuestion(h.concept("APPOINTMENT DATE"));
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
				.addColumn(rd, "patients", "Patients", i, null);

		return rd;
	}
}
