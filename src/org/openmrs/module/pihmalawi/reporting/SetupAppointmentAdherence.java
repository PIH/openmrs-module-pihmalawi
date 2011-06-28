package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.AppointmentAdherenceCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
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

public class SetupAppointmentAdherence {

	private static final Concept APPOINTMENT_CONCEPT = Context
			.getConceptService().getConceptByName("APPOINTMENT DATE");

	Helper h = new Helper();

	private final ProgramWorkflowState STATE;

	private final String PREFIX;
	private final String NAME;

	private final List<EncounterType> ENCOUNTERTYPES;

	private boolean fixedHistoricAdherence = false;
	
	public SetupAppointmentAdherence(Helper helper, String prefix,
			String name, ProgramWorkflowState state,
			List<EncounterType> encounterTypes, boolean fixedHistoricAdherence) {
		h = helper;
		STATE = state;
		PREFIX = prefix;
		NAME = name;
		ENCOUNTERTYPES = encounterTypes;
		this.fixedHistoricAdherence = fixedHistoricAdherence;
	}

	public void setup() throws Exception {
		delete();

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);
		h.replaceReportDefinition(rd);
		createHtmlBreakdownExternal(rd);
	}

	protected ReportDesign createHtmlBreakdownExternal(
			PeriodIndicatorReportDefinition rd) throws IOException,
			SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeMissedAppointmentColumns(false);
		int columns = rd.getIndicatorDataSetDefinition().getColumns().size();
		for (int i = 1; i <= columns; i++) {
			m.put("" + i, new Mapped<DataSetDefinition>(dsd, null));
		}
		m.put("initiated", new Mapped<DataSetDefinition>(dsd, null));
		dsd.setEncounterTypes(null);
		dsd.setPatientIdentifierType(null);

		return h.createHtmlBreakdown(rd, NAME
				+ " Appointment Adherence Breakdown_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals(NAME + " Appointment Adherence Breakdown_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, NAME
				+ " Appointment Adherence_ Data Set");
		h.purgeDefinition(ReportDefinition.class, NAME
				+ " Appointment Adherence_");
		h.purgeAll(PREFIX + " ");
	}

	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName(NAME + " Appointment Adherence_");
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setupDataSetDefinition();
		return rd;
	}

	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {

		if (STATE != null) {
			InStateCohortDefinition iscd = new InStateCohortDefinition();
			iscd.setName(PREFIX + " In state_");
			iscd.addParameter(new Parameter("onOrAfter", "onOrAfter",
					Date.class));
			iscd.setStates(Arrays.asList(STATE));
			h.replaceCohortDefinition(iscd);
		}

		AppointmentAdherenceCohortDefinition aacd = new AppointmentAdherenceCohortDefinition();
		aacd.setName(PREFIX + " Appointment Adherence_");
		aacd.setEncounterTypes(ENCOUNTERTYPES);
		aacd.setAppointmentConcept(APPOINTMENT_CONCEPT);
		aacd.addParameter(new Parameter("fromDate", "fromDate", Date.class));
		aacd.addParameter(new Parameter("toDate", "toDate", Date.class));
		aacd.addParameter(new Parameter("minimumAdherence", "minimumAdherence",
				Integer.class));
		aacd.addParameter(new Parameter("maximumAdherence", "maximumAdherence",
				Integer.class));
		h.replaceCohortDefinition(aacd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.addParameter(new Parameter("minimumAdherence", "minimumAdherence",
				Integer.class));
		ccd.addParameter(new Parameter("maximumAdherence", "maximumAdherence",
				Integer.class));
		ccd.setName(PREFIX + " " + NAME + " Appointment Adherence_");
		if (STATE != null) {
			ccd.getSearches().put(
					"state",
					new Mapped(h.cohortDefinition(PREFIX + " In state_"), h
							.parameterMap("onOrAfter", "${endDate}")));
		}
		ccd.getSearches().put(
				"appadherence",
				new Mapped(h.cohortDefinition(PREFIX
						+ " Appointment Adherence_"), h.parameterMap(
						"fromDate", "${startDate}", "toDate", "${endDate}",
						"minimumAdherence", "${minimumAdherence}",
						"maximumAdherence", "${maximumAdherence}")));
		if (STATE != null) {
			ccd.setCompositionString("state AND appadherence");
		} else {
			ccd.setCompositionString("appadherence");
		}
		h.replaceCohortDefinition(ccd);

		// not enough data (most likely only one visit where app date is out of scope
		adherenceIndicator(rd, -1, -1);
		adherenceIndicator(rd, 0, 0);
		adherenceIndicator(rd, 1, 9);
		adherenceIndicator(rd, 10, 19);
		adherenceIndicator(rd, 20, 29);
		adherenceIndicator(rd, 30, 39);
		adherenceIndicator(rd, 40, 49);
		adherenceIndicator(rd, 50, 59);
		adherenceIndicator(rd, 60, 69);
		adherenceIndicator(rd, 70, 79);
		adherenceIndicator(rd, 80, 89);
		adherenceIndicator(rd, 90, 99);
		adherenceIndicator(rd, 100, 100);
	}

	private void adherenceIndicator(PeriodIndicatorReportDefinition rd,
			int min, int max) {
		String minString  = String.format("%03d", min);
		if (min == -1) {
			minString = "-001";
		}
		String maxString = String.format("%03d", max);
		if (max == -1) {
			maxString = "-001";
		}
		
		if (!fixedHistoricAdherence) {
			// calc adherence for given period
			CohortIndicator i = h.newCountIndicator(PREFIX + " " + NAME
					+ " Appointment Adherence " + minString + "-" + maxString + "_", PREFIX
					+ " " + NAME + " Appointment Adherence_", h.parameterMap(
					"startDate", "${startDate}", "endDate", "${endDate}",
					"minimumAdherence", min, "maximumAdherence", max));
			PeriodIndicatorReportUtil.addColumn(rd, "appad" + minString + "-" + maxString, NAME
					+ " appointment adherence " + minString + "-" + maxString, i, null);
		} else {
			int intervallInMonths = 3;
			for (int period = 0; period < 8; period++) {
			// calc adherence for hardcoded periods
			CohortIndicator i = h.newCountIndicator(PREFIX + " " + NAME
					+ " Appointment Adherence " + minString + "-" + maxString + " " + period + "months ago" + "_", PREFIX
					+ " " + NAME + " Appointment Adherence_", h.parameterMap(
					"startDate", "${startDate-" + (period * intervallInMonths) + "m}", "endDate", "${endDate-" + (period * intervallInMonths) + "m}",
					"minimumAdherence", min, "maximumAdherence", max));
			PeriodIndicatorReportUtil.addColumn(rd, "appad" + period + "ago" + minString + "-" + maxString, NAME
					+ " appointment adherence " + minString + "-" + maxString + " " + period + " months ago", i, null);
			}
		}
	}
}
