package org.openmrs.module.pihmalawi.reporting;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.web.WebConstants;

@Handler(supports = { ApzuAppointmentAdherencePatientDataSetDefinition.class })
public class ApzuAppointmentAdherencePatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	private final static long MILLISECONDS_PER_WEEK = (long) 7 * 24 * 60 * 60
			* 1000;

	public ApzuAppointmentAdherencePatientDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		final Concept concept = Context.getConceptService().getConceptByName(
		"APPOINTMENT DATE");

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		ApzuAppointmentAdherencePatientDataSetDefinition definition = (ApzuAppointmentAdherencePatientDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition
				.getPatientIdentifierType();
		List<EncounterType> ets = definition.getEncounterTypes();

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();
		Date endDateParameter = (Date) context.getParameterValue("endDate");
		if (endDateParameter == null) {
			// default to today
			endDateParameter = new Date();
		}
		Date startDateParameter = (Date) context.getParameterValue("startDate");
		if (startDateParameter == null) {
			startDateParameter = new Date(0);
		}
		Location location = (Location) context.getParameterValue("location");

		// By default, get all patients
		if (cohort == null) {
			cohort = Context.getPatientSetService().getAllPatients();
		}

		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(
				cohort.getMemberIds());

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();
			DataSetColumn c = null;

			// todo, get current id and/or preferred one first
			String patientLink = "";
			// todo, don't hardcode server
			String url = "http://emr:8080/" + WebConstants.WEBAPP_NAME;
			List<PatientIdentifier> pis = null;
			if (patientIdentifierType == null) {
				pis = p.getActiveIdentifiers();
			} else {
				pis = p.getPatientIdentifiers(patientIdentifierType);
			}
			for (PatientIdentifier pi : pis) {
				String link = "<a href="
						+ url
						+ "/patientDashboard.form?patientId="
						+ p.getId()
						+ ">"
						+ (pi != null ? formatPatientIdentifier(pi
								.getIdentifier()) : "(none)") + "</a> ";
				patientLink += link;
			}
			c = new DataSetColumn("#", "#", String.class);
			row.addColumnValue(c, patientLink);
			// given
			c = new DataSetColumn("Given", "Given", String.class);
			row.addColumnValue(c, p.getGivenName());
			// family
			c = new DataSetColumn("Last", "Last", String.class);
			row.addColumnValue(c, p.getFamilyName());
			// dob
			c = new DataSetColumn("Birthdate", "Birthdate", Integer.class);
			row.addColumnValue(c, formatEncounterDate(p.getBirthdate()));
			// sex
			c = new DataSetColumn("M/F", "M/F", String.class);
			row.addColumnValue(c, p.getGender());
			// village
			c = new DataSetColumn("Village", "Village", String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));

			// app adherence
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					location, startDateParameter, endDateParameter, null, ets, null, false);
			List<Obs> obses = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(concept), null,
					null, null, null, null, null, startDateParameter, endDateParameter, false);

			int visits = obses.size() > 1 ? obses.size() - 1 : 0;
			int visitOnTime = 0;
			int missedBy1Week = 0;
			int missedBy2Weeks = 0;
			int missedBy3Weeks = 0;
			int missedBy4Weeks = 0;
			int missedBy8Weeks = 0;
			int missedMoreThan8Weeks = 0;

			for (int appointments = obses.size() - 1; appointments > 0; appointments--) {

				Date dayAfterEncounterDate = DateUtil.getEndOfDay(obses.get(appointments).getEncounter().getEncounterDatetime());
				Date dayAfterNextAppointmentDate = DateUtil.getEndOfDay(obses.get(appointments).getValueDatetime());
				Date dayAfterNextAppointmentDateBuffer1Week = addWeeks(DateUtil.getEndOfDay(dayAfterNextAppointmentDate), 1);
				Date dayAfterNextAppointmentDateBuffer2Weeks = addWeeks(DateUtil.getEndOfDay(dayAfterNextAppointmentDate), 2);
				Date dayAfterNextAppointmentDateBuffer3Weeks = addWeeks(DateUtil.getEndOfDay(dayAfterNextAppointmentDate), 3);
				Date dayAfterNextAppointmentDateBuffer4Weeks = addWeeks(DateUtil.getEndOfDay(dayAfterNextAppointmentDate), 4);
				Date dayAfterNextAppointmentDateBuffer8Weeks = addWeeks(DateUtil.getEndOfDay(dayAfterNextAppointmentDate), 8);

				if (visitHappened(p, location, dayAfterEncounterDate,
						dayAfterNextAppointmentDate, ets)) {
					visitOnTime++;
				} else if (visitHappened(p, location, dayAfterEncounterDate,
						dayAfterNextAppointmentDateBuffer1Week, ets)) {
					missedBy1Week++;
				} else if (visitHappened(p, location, dayAfterEncounterDate,
						dayAfterNextAppointmentDateBuffer2Weeks, ets)) {
					missedBy2Weeks++;
				} else if (visitHappened(p, location, dayAfterEncounterDate,
						dayAfterNextAppointmentDateBuffer3Weeks, ets)) {
					missedBy3Weeks++;
				} else if (visitHappened(p, location, dayAfterEncounterDate,
						dayAfterNextAppointmentDateBuffer4Weeks, ets)) {
					missedBy4Weeks++;
				} else if (visitHappened(p, location, dayAfterEncounterDate,
						dayAfterNextAppointmentDateBuffer8Weeks, ets)) {
					missedBy8Weeks++;
				} else {
					missedMoreThan8Weeks++;
				}
			}
			c = new DataSetColumn("consideredvisits", "consideredvisits", String.class);
			row.addColumnValue(c, visits);
			c = new DataSetColumn("ontime", "ontime", String.class);
			row.addColumnValue(c, indicator(visitOnTime, visits));
			c = new DataSetColumn("missedBy1Week", "missedBy1Week", String.class);
			row.addColumnValue(c, indicator(missedBy1Week, visits));
			c = new DataSetColumn("missedBy2Weeks", "missedBy2Weeks", String.class);
			row.addColumnValue(c, indicator(missedBy2Weeks, visits));
			c = new DataSetColumn("missedBy3Weeks", "missedBy3Weeks", String.class);
			row.addColumnValue(c, indicator(missedBy3Weeks, visits));
			c = new DataSetColumn("missedBy4Weeks", "missedBy4Weeks", String.class);
			row.addColumnValue(c, indicator(missedBy4Weeks, visits));
			c = new DataSetColumn("missedBy8Weeks", "missedBy8Weeks", String.class);
			row.addColumnValue(c, indicator(missedBy8Weeks, visits));
			c = new DataSetColumn("missedMoreThan8Weeks", "missedMoreThan8Weeks", String.class);
			row.addColumnValue(c, indicator(missedMoreThan8Weeks, visits));

			dataSet.addRow(row);
		}
		return dataSet;
	}

	private String indicator(int visitOnTime, int visits) {
		if (visits == 0 || visitOnTime == 0) return "";
		return ((float) visitOnTime / visits) * 100 + "";
	}

	private String formatPatientIdentifier(String id) {
		if (id.lastIndexOf(" ") > 0) {
			// for now assume that an id without leading zeros is there when
			// there is a space
			String number = id.substring(id.lastIndexOf(" ") + 1);
			try {
				DecimalFormat f = new java.text.DecimalFormat("0000");
				number = f.format(new Integer(number));
			} catch (Exception e) {
				// error while converting
				return id;
			}
			return id.substring(0, id.lastIndexOf(" ")) + "-" + number;
		}
		return id;
	}

	protected String formatEncounterDate(Date encounterDatetime) {
		return new SimpleDateFormat("yyyy-MM-dd").format(encounterDatetime);
	}

	protected String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}
	
	private boolean visitHappened(Patient p, Location location,
			Date dayAfterAppointmentDate, Date dayAfterNextAppointmentDate,
			List<EncounterType> ets) {
		return !Context
				.getEncounterService()
				.getEncounters(p, location, dayAfterAppointmentDate,
						dayAfterNextAppointmentDate, null, ets, null, false)
				.isEmpty();
	}

	private Date addWeeks(Date date, int weeks) {
		return new Date(date.getTime() + weeks * MILLISECONDS_PER_WEEK);
	}


}
