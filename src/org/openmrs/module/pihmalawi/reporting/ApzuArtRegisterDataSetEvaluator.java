package org.openmrs.module.pihmalawi.reporting;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
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

@Handler(supports = { ApzuArtRegisterDataSetDefinition.class })
public class ApzuArtRegisterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	protected Helper h = new Helper();

	private List<EncounterType> ENCOUNTER_TYPES;

	private Concept CD4_COUNT;

	private Concept APPOINTMENT_DATE;

	private Concept WEIGHT;

	private PatientIdentifierType ART_PATIENT_IDENTIFIER_TYPE;

	private PatientIdentifierType HCC_PATIENT_IDENTIFIER_TYPE;

	private ProgramWorkflowState ON_ART_STATE;

	private ProgramWorkflowState PART_STATE;

	private ProgramWorkflowState EXPOSED_STATE;

	private Concept ARV_STARTDATE;

	private Concept WHO_STAGE;

	private Concept PSHD;

	private Concept KS;

	private Concept TB;

	private Concept CD4;

	private Concept TLC;

	private EncounterType ART_INITIAL;

	public ApzuArtRegisterDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {

		ENCOUNTER_TYPES = Arrays.asList(h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP"));
		CD4_COUNT = Context.getConceptService().getConceptByName("CD4 count");
		APPOINTMENT_DATE = Context.getConceptService().getConceptByName(
				"Appointment date");
		WEIGHT = Context.getConceptService().getConceptByName("Weight (kg)");
		ARV_STARTDATE = Context.getConceptService().getConceptByName("Start date 1st line ARV");
		WHO_STAGE =  Context.getConceptService().getConceptByName("WHO stage");
		PSHD =  Context.getConceptService().getConceptByName("Presumed severe HIV criteria present");
		KS =  Context.getConceptService().getConceptByName("Kaposis sarcoma side effects worsening while on ARVs?");
		TB =  Context.getConceptService().getConceptByName("Tuberculosis treatment status");
		CD4 =  Context.getConceptService().getConceptByName("CD4 count");
		TLC =  Context.getConceptService().getConceptByName("Cd4%");
		ART_INITIAL = h.encounterType("ART_INITIAL");
		ART_PATIENT_IDENTIFIER_TYPE = Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number");
		HCC_PATIENT_IDENTIFIER_TYPE = Context.getPatientService()
				.getPatientIdentifierTypeByName("HCC Number");
		ON_ART_STATE = Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals");
		PART_STATE = Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("Pre-ART (Continue)");
		EXPOSED_STATE = Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("Exposed Child (Continue)");

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

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

		// sorting like this is expensive and should be somehow provided by the
		// framework!
		sortByIdentifier(patients, ART_PATIENT_IDENTIFIER_TYPE,
				locationParameter);

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();
			DataSetColumn c = null;

			try {
				// todo, get current id and/or preferred one first
				String patientLink = "";
				// todo, don't hardcode server
				String url = "http://emr:8080/" + WebConstants.WEBAPP_NAME;
				List<PatientIdentifier> pis = p
						.getPatientIdentifiers(ART_PATIENT_IDENTIFIER_TYPE);
				for (PatientIdentifier pi : pis) {
					if (pi != null
							&& pi.getLocation() != null
							&& locationParameter != null
							&& pi.getLocation().getId() == locationParameter
									.getId()) {
						// take id for location
						patientLink = "<a href="
								+ url
								+ "/patientDashboard.form?patientId="
								+ p.getId()
								+ ">"
								+ (pi != null ? formatPatientIdentifier(pi
										.getIdentifier()) : "(none)") + "</a> ";
					}
				}
				c = new DataSetColumn("Current ARV #", "Current ARV #",
						String.class);
				row.addColumnValue(c, h(patientLink));

				// art
				identifiers(p, ART_PATIENT_IDENTIFIER_TYPE, row);
				changeToStateDate(p, locationParameter, ON_ART_STATE, row);
				enrollmentDate(p, locationParameter, ON_ART_STATE, row);
				// 1st time on art
				firstTimeChangeToStateDate(p, ON_ART_STATE,
						"1st time On antiretrovirals", row);
				firstTimeEnrollment(p, ON_ART_STATE, row);

				// hcc
				identifiers(p, HCC_PATIENT_IDENTIFIER_TYPE, row);
				firstTimeChangeToStateDate(p, PART_STATE,
						"1st time in Pre-ART", row);
				firstTimeChangeToStateDate(p, EXPOSED_STATE,
						"1st time in Exposed Child", row);

				// art_initial
				artInitialEncounter(p, row);

				// demographics
				c = new DataSetColumn("Given", "Given", String.class);
				row.addColumnValue(c, p.getGivenName());
				c = new DataSetColumn("Last", "Last", String.class);
				row.addColumnValue(c, p.getFamilyName());
//				c = new DataSetColumn("Age at Init (yr)", "Age at Init (yr)", Integer.class);
//				row.addColumnValue(c, p.getAge(endDateParameter));
				c = new DataSetColumn("Age (yr)", "Age (yr)", Integer.class);
				row.addColumnValue(c, p.getAge(endDateParameter));
				c = new DataSetColumn("Birthdate", "Birthdate", Integer.class);
				row.addColumnValue(c, formatEncounterDate(p.getBirthdate()));
				c = new DataSetColumn("Age (mth)", "Age (mth)", Integer.class);
				row.addColumnValue(c, getAgeInMonths(p, endDateParameter));
				c = new DataSetColumn("M/F", "M/F", String.class);
				row.addColumnValue(c, p.getGender());
				c = new DataSetColumn("Village", "Village", String.class);
				row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));

				vhw(p, row);

				// outcome & date at loc
				outcome(p, locationParameter, row);
				// most recent outcome, date, loc
				mostRecentOutcome(p, endDateParameter, row);

				// cd4 & weight
				mostRecentNumericObs(p, WEIGHT, endDateParameter, row);
				mostRecentNumericObs(p, CD4_COUNT, endDateParameter, row);
				// todo firstNumericObs

				// reason starting arvs
				reasonStartingArvs(p, endDateParameter, row);
				// date 1st time arvs
				mostRecentDatetimeObs(p, ARV_STARTDATE, endDateParameter, row);

				// last visit & loc & rvd
				lastVisit(p, row);

				// other enrollments
				programEnrollments(p, row);

			} catch (Throwable t) {
				t.printStackTrace();
			}
			dataSet.addRow(row);
		}
		return dataSet;
	}

	private void mostRecentOutcome(Patient p,
			Date endDate, DataSetRow row) {
		PatientState ps = null;
		// enrollment outcome from location
		ps = h.getMostRecentStateAtDate(p, ON_ART_STATE.getProgramWorkflow(), endDate);

		DataSetColumn c1 = new DataSetColumn("Most recent Outcome", "Most recent Outcome", String.class);
		DataSetColumn c2 = new DataSetColumn("Most recent Outcome change date", "Most recent Outcome change Date",
				String.class);
		DataSetColumn c3 = new DataSetColumn("Most recent Outcome change loc", "Most recent Outcome change loc",
				String.class);
		if (ps != null) {
			row.addColumnValue(c1, ps.getState().getConcept().getName()
					.getName());
			row.addColumnValue(c2, formatEncounterDate(ps.getStartDate()));
			row.addColumnValue(c2, formatEncounterDate(ps.getStartDate()));
			row.addColumnValue(c3, h.getEnrollmentLocation(ps.getPatientProgram(), sessionFactory().getCurrentSession()));
		} else {
			row.addColumnValue(c1, h(""));
			row.addColumnValue(c2, h(""));
			row.addColumnValue(c3, h(""));
		}
	}

	private void reasonStartingArvs(Patient p, Date endDate, DataSetRow row) {
		List<Encounter> es = Context.getEncounterService().getEncounters(p,
				null, null, endDate, null, Arrays.asList(ART_INITIAL), null, false);
		String reasons = "";

		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(CD4), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += " " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(KS), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += " " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(TB), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += " " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(WHO_STAGE), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += " " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(TLC), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += " " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(PSHD), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += " " + o.getValueAsString(Context.getLocale());
		}
		DataSetColumn c = new DataSetColumn("ARV start reasons", "ARV start reasons", String.class);
		row.addColumnValue(c, reasons);
	}

	private void firstTimeEnrollment(Patient p, ProgramWorkflowState state,
			DataSetRow row) {
		DataSetColumn c = new DataSetColumn("1st Enrollment date (ART or HCC)",
				"1st Enrollment date (ART or HCC)", String.class);
		row.addColumnValue(
				c,
				formatEncounterDate(firstTimeInState(p,
						state.getProgramWorkflow().getProgram(), state)
						.getPatientProgram().getDateEnrolled()));

		// first on art at
		c = new DataSetColumn("1st Enrollment date (ART or HCC) location",
				"1st Enrollment date (ART or HCC) location", String.class);
		row.addColumnValue(
				c,
				firstTimeInStateAtLocation(p, state.getProgramWorkflow()
						.getProgram(), state));
	}

	private void enrollmentDate(Patient p, Location locationParameter,
			ProgramWorkflowState state, DataSetRow row) {
		DataSetColumn c = new DataSetColumn("Enrollment date (ART or HCC)",
				"Enrollment date (ART or HCC)", String.class);
		List<PatientState> states = h.getPatientStatesByWorkflowAtLocation(p,
				state, locationParameter, sessionFactory().getCurrentSession());
		PatientState firstState = states.get(0);
		row.addColumnValue(c, formatEncounterDate(firstState
				.getPatientProgram().getDateEnrolled()));
	}

	private void outcome(Patient p, Location locationParameter, DataSetRow row) {
		PatientState ps = null;
		// enrollment outcome from location
		ps = h.getMostRecentStateAtLocation(p,
				ON_ART_STATE.getProgramWorkflow(), locationParameter,
				sessionFactory().getCurrentSession());

		DataSetColumn c = new DataSetColumn("Outcome", "Outcome", String.class);
		if (ps != null) {
			row.addColumnValue(c, ps.getState().getConcept().getName()
					.getName());
		}
		c = new DataSetColumn("Outcome change date", "Outcome change Date",
				String.class);
		if (ps != null) {
			row.addColumnValue(c, formatEncounterDate(ps.getStartDate()));
		}
	}

	private void changeToStateDate(Patient p, Location locationParameter,
			ProgramWorkflowState state, DataSetRow row) {
		DataSetColumn c = new DataSetColumn(state.getConcept().getName(
				Context.getLocale())
				+ " date", state.getConcept().getName(Context.getLocale())
				+ " date", String.class);
		List<PatientState> states = h.getPatientStatesByWorkflowAtLocation(p,
				state, locationParameter, sessionFactory().getCurrentSession());
		PatientState firstState = states.get(0);
		row.addColumnValue(c, formatEncounterDate(firstState.getStartDate()));
	}

	private void firstTimeChangeToStateDate(Patient p,
			ProgramWorkflowState state, String header, DataSetRow row) {
		DataSetColumn c1 = new DataSetColumn(header + " date",
				header + " date", String.class);
		DataSetColumn c2 = new DataSetColumn(header + " location", header
				+ " location", String.class);
		PatientState ps = firstTimeInState(p, state.getProgramWorkflow()
				.getProgram(), state);
		if (ps != null) {
			row.addColumnValue(c1, formatEncounterDate(ps.getStartDate()));
			// first on art at
			row.addColumnValue(
					c2,
					firstTimeInStateAtLocation(p, state.getProgramWorkflow()
							.getProgram(), state));
		} else {
			row.addColumnValue(c1, h(""));
			row.addColumnValue(c2, h(""));
		}
	}

	private void mostRecentNumericObs(Patient p, Concept concept, Date endDate,
			DataSetRow row) {
		List<Encounter> es = Context.getEncounterService().getEncounters(p,
				null, null, endDate, null, null, null, false);
		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(concept), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			DataSetColumn c = new DataSetColumn(concept.getName(
					Context.getLocale()).getName(), concept.getName(
					Context.getLocale()).getName(), String.class);
			row.addColumnValue(c, o.getValueNumeric());

			c = new DataSetColumn(concept.getName(Context.getLocale())
					.getName() + " Date", concept.getName(Context.getLocale())
					.getName() + " Date", String.class);
			row.addColumnValue(c, formatEncounterDate(o.getObsDatetime()));
		}
	}

	private void mostRecentDatetimeObs(Patient p, Concept concept, Date endDate,
			DataSetRow row) {
		List<Encounter> es = Context.getEncounterService().getEncounters(p,
				null, null, endDate, null, null, null, false);
		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(concept), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			DataSetColumn c = new DataSetColumn(concept.getName(
					Context.getLocale()).getName(), concept.getName(
					Context.getLocale()).getName(), String.class);
			row.addColumnValue(c, formatEncounterDate(o.getValueDatetime()));
		}
	}

	private void lastVisit(Patient p, DataSetRow row) {
		List<Encounter> encounters = Context.getEncounterService()
				.getEncounters(p, null, null, null, null, ENCOUNTER_TYPES,
						null, false);
		DataSetColumn c1 = new DataSetColumn("Last visit date",
				"Last visit date", String.class);
		DataSetColumn c2 = new DataSetColumn("Last visit loc",
				"Last visit loc", String.class);
		DataSetColumn c3 = new DataSetColumn("Last visit appt date",
				"Last visit appt date", String.class);
		if (!encounters.isEmpty()) {
			Encounter e = encounters.get(encounters.size() - 1);
			row.addColumnValue(c1,
					formatEncounterDate(e.getEncounterDatetime()));
			row.addColumnValue(c2, e.getLocation());

			// rvd from last encounter
			Set<Obs> observations = e.getObs();
			for (Obs o : observations) {
				if (o.getConcept().equals(APPOINTMENT_DATE)) {
					row.addColumnValue(c3,
							formatEncounterDate(o.getValueDatetime()));
					break;
				}
			}
		} else {
			row.addColumnValue(c1, h(""));
			row.addColumnValue(c2, h(""));
			row.addColumnValue(c3, h(""));
		}
	}

	private void artInitialEncounter(Patient p, DataSetRow row) {
		List<Encounter> encounters = Context.getEncounterService()
				.getEncounters(p, null, null, null, null,
						Arrays.asList(ART_INITIAL), null,
						false);
		DataSetColumn c1 = new DataSetColumn("Initial Encounter date",
				"Initial Encounter date", String.class);
		DataSetColumn c2 = new DataSetColumn("Initial Encounter loc",
				"Initial Encounter loc", String.class);
		if (!encounters.isEmpty()) {
			Encounter e = encounters.get(encounters.size() - 1);
			row.addColumnValue(c1,
					formatEncounterDate(e.getEncounterDatetime()));
			row.addColumnValue(c2, e.getLocation());
		} else {
			row.addColumnValue(c1, h(""));
			row.addColumnValue(c2, h(""));
		}
	}

	private void vhw(Patient p, DataSetRow row) {
		DataSetColumn c;
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
	}

	private void sortByIdentifier(List<Patient> patients,
			final PatientIdentifierType patientIdentifierType,
			final Location locationParameter) {
		Collections.sort(patients, new Comparator<Patient>() {
			@Override
			public int compare(Patient p1, Patient p2) {

				String p1ids = allIds(patientIdentifierType, locationParameter,
						p1);
				String p2ids = allIds(patientIdentifierType, locationParameter,
						p2);

				return p1ids.compareToIgnoreCase(p2ids);
			}

			private String allIds(
					final PatientIdentifierType patientIdentifierType,
					final Location locationParameter, Patient p) {
				String allIds = "";
				List<PatientIdentifier> pis = null;
				if (patientIdentifierType == null) {
					pis = p.getActiveIdentifiers();
				} else {
					pis = p.getPatientIdentifiers(patientIdentifierType);
				}
				for (PatientIdentifier pi : pis) {
					String id = (pi != null ? formatPatientIdentifier(pi
							.getIdentifier()) : "(none) ");
					if (pi != null
							&& pi.getLocation() != null
							&& locationParameter != null
							&& pi.getLocation().getId() == locationParameter
									.getId()) {
						// move preferred prefixed id to front if location was
						// specified
						allIds = id + allIds;
					} else {
						allIds += id;
					}
				}
				return allIds;
			}
		});
	}

	private void identifiers(Patient p, PatientIdentifierType piType,
			DataSetRow row) {
		String arvs = "";
		if (piType != null) {
			for (PatientIdentifier pi : p.getPatientIdentifiers(piType)) {
				if (pi != null && pi.getLocation() != null) {
					arvs += pi.getIdentifier() + " ";
				}
			}
			DataSetColumn c = new DataSetColumn(piType.getName() + "s",
					piType.getName() + "s", String.class);
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
						+ formatEncounterDate(ps.getStartDate()) + "),<br/>";
			}
		}
		row.addColumnValue(c, h(programs));
	}

	private String formatEncounterDate(Date encounterDatetime) {
		if (encounterDatetime == null) {
			return "<Unknown>";
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(encounterDatetime);
	}

	private String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	private Location firstTimeInStateAtLocation(Patient p, Program program,
			ProgramWorkflowState firstTimeInState) {
		PatientState ps = h.getFirstTimeInState(p, program, firstTimeInState);
		if (ps != null) {
			return h.getEnrollmentLocation(ps.getPatientProgram(),
					sessionFactory().getCurrentSession());
		}
		return null;
	}

	private PatientState firstTimeInState(Patient p, Program program,
			ProgramWorkflowState firstTimeInState) {
		PatientState ps = h.getFirstTimeInState(p, program, firstTimeInState);
		return ps;
	}

}
