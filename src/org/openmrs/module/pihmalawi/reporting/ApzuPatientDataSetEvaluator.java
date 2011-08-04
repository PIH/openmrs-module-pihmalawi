package org.openmrs.module.pihmalawi.reporting;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.web.WebConstants;

@Handler(supports = { ApzuPatientDataSetDefinition.class })
public class ApzuPatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	protected Helper h = new Helper();

	public ApzuPatientDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		final Concept DEFAULTER_ACTION_TAKEN = Context.getConceptService()
				.getConceptByName("DEFAULTER ACTION TAKEN");
		final Concept CD4_COUNT = Context.getConceptService().getConceptByName(
				"CD4 COUNT");
		final Concept APPOINTMENT_DATE = Context.getConceptService()
				.getConceptByName("APPOINTMENT DATE");
		final Concept WEIGHT = Context.getConceptService().getConceptByName(
				"WEIGHT (KG)");
		final Concept HEIGHT = Context.getConceptService().getConceptByName(
				"HEIGHT (CM)");

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		ApzuPatientDataSetDefinition definition = (ApzuPatientDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition
				.getPatientIdentifierType();
		Collection<EncounterType> encounterTypes = definition
				.getEncounterTypes();

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();
		Location locationParameter = (Location) context
				.getParameterValue("location");
		Date endDateParameter = (Date) context.getParameterValue("endDate");
		if (endDateParameter == null) {
			// default to today
			endDateParameter = new Date();
		}

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
				if (pi != null
						&& pi.getLocation() != null
						&& locationParameter != null
						&& pi.getLocation().getId() == locationParameter
								.getId()) {
					// move preferred prefixed id to front if location was
					// specified
					patientLink = link + patientLink;
				} else {
					patientLink += link;
				}
			}
			c = new DataSetColumn("#", "#", String.class);
			row.addColumnValue(c, patientLink);
			// arvNumbers
			if (definition.getIncludeArvNumber()) {
				arvNumbers(p, row);
			}
			// given
			c = new DataSetColumn("Given", "Given", String.class);
			row.addColumnValue(c, p.getGivenName());
			// family
			c = new DataSetColumn("Last", "Last", String.class);
			row.addColumnValue(c, p.getFamilyName());
			// age
			c = new DataSetColumn("Age (yr)", "Age (yr)", Integer.class);
			row.addColumnValue(c, p.getAge(endDateParameter));
			// dob
			c = new DataSetColumn("Birthdate", "Birthdate", Integer.class);
			row.addColumnValue(c, formatEncounterDate(p.getBirthdate()));
			// age in months
			c = new DataSetColumn("Age (mth)", "Age (mth)", Integer.class);
			row.addColumnValue(c, getAgeInMonths(p, endDateParameter));
			// sex
			c = new DataSetColumn("M/F", "M/F", String.class);
			row.addColumnValue(c, p.getGender());

			if (definition.getIncludeProgramOutcome()) {
				PatientState ps = null;
				if (locationParameter == null) {
					// outcome from endDate, hopefully the one you are interested in
					ps = h.getMostRecentStateAtDate(p,
							definition.getProgram(), endDateParameter,
							sessionFactory().getCurrentSession());
				} else {
					// enrollment outcome from location
					ps = h.getMostRecentStateAtLocation(p,
						definition.getProgram(), locationParameter,
						sessionFactory().getCurrentSession());
				}
				c = new DataSetColumn("Outcome", "Outcome", String.class);
				row.addColumnValue(c, ps.getState().getConcept().getName()
						.getName());
				c = new DataSetColumn("Outcome Date", "Outcome Date",
						String.class);
				row.addColumnValue(c, formatEncounterDate(ps.getStartDate()));
			}

			// last visit & loc
			List<Encounter> encounters = Context.getEncounterService()
					.getEncounters(p, null, null, null, null, encounterTypes,
							null, false);
			c = new DataSetColumn("Last visit", "Last visit", String.class);
			if (!encounters.isEmpty()) {
				c = new DataSetColumn("Last visit", "Last visit", String.class);
				Encounter e = encounters.get(encounters.size() - 1);
				row.addColumnValue(c,
						formatEncounterDate(e.getEncounterDatetime()) + " ("
								+ e.getLocation() + ")");
				if (definition.getIncludeFirstVisit()) {
					c = new DataSetColumn("First visit", "First visit",
							String.class);
					e = encounters.get(0);
					row.addColumnValue(c,
							formatEncounterDate(e.getEncounterDatetime()));
				}
				// rvd from last encounter
				// String rvd = "";
				// Set<Obs> observations = e.getObs();
				// for (Obs o : observations) {
				// if
				// (o.getConcept().equals(Context.getConceptService().getConceptByName("APPOINTMENT DATE")))
				// {
				// rvd = o.getValueAsString(Context.getLocale());
				// c = new DataSetColumn("RVD", "RVD", String.class);
				// row.addColumnValue(c, h(rvd));
				// }
				// }
			} else {
				row.addColumnValue(c, h(""));
			}
			// rvd
			String rvd = "";
			// getting rvd from valid encounters
			encounters = Context.getEncounterService().getEncounters(p, null,
					null, null, null, encounterTypes, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList(Context.getPersonService().getPerson(
							p.getPersonId())), encounters,
					Arrays.asList(APPOINTMENT_DATE), null, null, null, null, 1,
					null, null, null, false);
			// getting rvd from any concept of person across encounters
			// Context.getObsService().getObservationsByPersonAndConcept(p,
			// Context.getConceptService().getConceptByName("APPOINTMENT DATE"));
			Iterator<Obs> i = obs.iterator();
			if (i.hasNext()) {
				rvd = i.next().getValueAsString(Context.getLocale());
			}
			c = new DataSetColumn("RVD", "RVD", String.class);
			row.addColumnValue(c, h(rvd));
			// vhw given & last
			c = new DataSetColumn("VHW", "VHW", String.class);
			String vhw = "";
			List<Relationship> ships = Context.getPersonService()
					.getRelationshipsByPerson(p);
			for (Relationship r : ships) {
				if (r.getRelationshipType().equals(
						Context.getPersonService().getRelationshipTypeByName(
								"Patient/Village Health Worker"))) {
					vhw = r.getPersonB().getGivenName() + " "
							+ r.getPersonB().getFamilyName();
				} else if (r.getRelationshipType().equals(
						Context.getPersonService().getRelationshipTypeByName(
								"Patient/Guardian"))) {
					vhw = r.getPersonB().getGivenName() + " "
							+ r.getPersonB().getFamilyName() + " (Guardian)";
				}
			}
			row.addColumnValue(c, h(vhw));
			// village
			c = new DataSetColumn("Village", "Village", String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));

			// columns for cross-checking
			if (definition.getIncludeMissedAppointmentColumns()) {
				// verified
				c = new DataSetColumn("verified", "verified", String.class);
				row.addColumnValue(c, h(""));
				// not verified
				c = new DataSetColumn("unable to verify", "unable to verify",
						String.class);
				row.addColumnValue(c, h(""));
				// missed data entry
				c = new DataSetColumn("missed data entry", "missed data entry",
						String.class);
				row.addColumnValue(c, h(""));
			}
			// comment
			String comment = "";
			if (definition.getIncludeDefaulterActionTaken()) {
				obs = Context.getObsService()
						.getObservationsByPersonAndConcept(p,
								DEFAULTER_ACTION_TAKEN);
				for (Obs o : obs) {
					comment += o.getValueAsString(Context.getLocale());
					comment += ":&nbsp;("
							+ formatEncounterDate(o.getObsDatetime()) + ") ";
				}
				obs = Context.getObsService()
						.getObservationsByPersonAndConcept(p, CD4_COUNT);
				if (obs.iterator().hasNext()) {
					Obs o = obs.iterator().next();
					comment += "CD4:&nbsp;"
							+ o.getValueAsString(Context.getLocale());
					comment += ":&nbsp;("
							+ formatEncounterDate(o.getObsDatetime()) + ") ";
				}
				List<Encounter> encs = Context.getEncounterService()
						.getEncountersByPatient(p);
				if (encs.size() > 0) {
					Encounter e = encs.get(encs.size() - 1);
					comment += e.getEncounterType().getName();
					comment += ":&nbsp;("
							+ formatEncounterDate(e.getEncounterDatetime())
							+ ") ";
				}
			}
			if (definition.getIncludeWeight()) {
				// enrollment outcome
				List<Encounter> es = Context.getEncounterService()
						.getEncounters(p, null, null, endDateParameter, null,
								definition.getEncounterTypes(), null, false);
				obs = Context.getObsService().getObservations(
						Arrays.asList((Person) p), es, Arrays.asList(WEIGHT),
						null, null, null, null, 1, null, null,
						endDateParameter, false);
				if (obs.iterator().hasNext()) {
					Obs o = obs.iterator().next();
					c = new DataSetColumn("Weight", "Weight", String.class);
					row.addColumnValue(c, (o.getValueNumeric()));
				}
			}

			if (definition.getIncludeMostRecentVitals()) {
				// enrollment outcome
				List<Encounter> es = Context.getEncounterService()
						.getEncounters(p, null, null, endDateParameter, null,
								null, null, false);
				obs = Context.getObsService().getObservations(
						Arrays.asList((Person) p), es, Arrays.asList(HEIGHT),
						null, null, null, null, 1, null, null,
						endDateParameter, false);
				if (obs.iterator().hasNext()) {
					Obs o = obs.iterator().next();
					c = new DataSetColumn("Height (cm)", "Height (cm)",
							String.class);
					row.addColumnValue(c, (o.getValueNumeric()));
				}
				obs = Context.getObsService().getObservations(
						Arrays.asList((Person) p), es, Arrays.asList(WEIGHT),
						null, null, null, null, 1, null, null,
						endDateParameter, false);
				if (obs.iterator().hasNext()) {
					Obs o = obs.iterator().next();
					c = new DataSetColumn("Weight (kg)", "Weight (kg)",
							String.class);
					row.addColumnValue(c, (o.getValueNumeric()));
					c = new DataSetColumn("Vitals date", "Vitals date",
							String.class);
					row.addColumnValue(c,
							formatEncounterDate(o.getObsDatetime()));
				}
			}

			if (definition.getIncludeChronicCareDiagnosis()) {
				chronicCare(p, row);
			}

			if (definition.getIncludeProgramEnrollments()) {
				programEnrollments(p, row);
			}

			c = new DataSetColumn("comment", "comment", String.class);
			row.addColumnValue(c, h(comment));
			
			additionalColumns(p, row, locationParameter, endDateParameter);

			dataSet.addRow(row);
		}
		return dataSet;
	}

	protected void additionalColumns(Patient p, DataSetRow row, Location locationParameter, Date endDateParameter) {
		// template method, empty by intention
	}

	private void arvNumbers(Patient p, DataSetRow row) {
		PatientIdentifierType piType = Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number");
		String arvs = "";
		if (piType != null) {
			for (PatientIdentifier pi : p.getPatientIdentifiers(piType)) {
				if (pi != null && pi.getLocation() != null) {
					arvs += pi.getIdentifier() + " ";
				}
			}
			DataSetColumn c = new DataSetColumn("ARV #", "ARV #", String.class);
			row.addColumnValue(c, arvs);
		}
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

	private int getAgeInMonths(Patient p, Date onDate) {
		Calendar bday = Calendar.getInstance();
		bday.setTime(p.getBirthdate());

		Calendar onDay = Calendar.getInstance();
		onDay.setTime(onDate);

		int bDayMonths = bday.get(Calendar.YEAR) * 12
				+ bday.get(Calendar.MONTH);
		int onDayMonths = onDay.get(Calendar.YEAR) * 12
				+ onDay.get(Calendar.MONTH);

		return onDayMonths - bDayMonths;
	}

	private void chronicCare(Patient p, DataSetRow row) {
		DataSetColumn c;
		List<Obs> obs;
		Iterator<Obs> i;
		// chronic care additions
		// deceased
		c = new DataSetColumn("Status", "Status", String.class);
		boolean deceased = (p.getDead() /*
										 * || p.getCauseOfDeath() != null || p
										 * .getDeathDate() != null
										 */);
		row.addColumnValue(c, (deceased ? "died" : "&nbsp;"));

		// cc diagnosis
		String diag = "";
		obs = Context.getObsService().getObservationsByPersonAndConcept(
				p,
				Context.getConceptService().getConceptByName(
						"CHRONIC CARE DIAGNOSIS"));
		i = obs.iterator();
		while (i.hasNext()) {
			diag += i.next().getValueAsString(Context.getLocale()) + ", ";
		}
		c = new DataSetColumn("Diagnosis", "Diagnosis", String.class);
		row.addColumnValue(c, h(diag));
	}

	private void programEnrollments(Patient p, DataSetRow row) {
		DataSetColumn c;
		c = new DataSetColumn("Enrollments", "Enrollments", String.class);
		String programs = "";

		// just collect everything latest program enrollment you can find
		Set<PatientState> pss = h.getMostRecentStates(p, sessionFactory()
				.getCurrentSession());
		if (pss != null) {
			Iterator<PatientState> i = pss.iterator();
			while (i.hasNext()) {
				PatientState ps = i.next();
				programs += ps.getPatientProgram().getProgram().getName()
						+ ":&nbsp;" + ps.getState().getConcept().getName()
						+ "&nbsp;(since&nbsp;"
						+ formatEncounterDate(ps.getStartDate()) + ") ";
			}
		}
		row.addColumnValue(c, h(programs));
	}

	protected String formatEncounterDate(Date encounterDatetime) {
		return new SimpleDateFormat("yyyy-MM-dd").format(encounterDatetime);
	}

	protected String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

}
