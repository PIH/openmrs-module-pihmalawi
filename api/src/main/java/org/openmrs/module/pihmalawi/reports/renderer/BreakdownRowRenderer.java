package org.openmrs.module.pihmalawi.reports.renderer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.web.WebConstants;

public abstract class BreakdownRowRenderer {

	protected Log log = LogFactory.getLog(this.getClass());

	private Map<String, Concept> conceptCache = null;
	private Map<String, Program> programCache = null;
	private Map<String, ProgramWorkflow> programWorkflowCache = null;
	private Map<String, EncounterType> encounterTypeCache = null;
	private Map<String, ProgramWorkflowState> programWorkflowStateCache = null;
	private Map<String, PatientIdentifierType> patientIdentifierTypeCache = null;
	
	ProgramHelper h = new ProgramHelper();

	public BreakdownRowRenderer() {
		conceptCache = new HashMap<String, Concept>();
		programCache = new HashMap<String, Program>();
		programWorkflowCache = new HashMap<String, ProgramWorkflow>();
		encounterTypeCache = new HashMap<String, EncounterType>();
		programWorkflowStateCache = new HashMap<String, ProgramWorkflowState>();
		patientIdentifierTypeCache = new HashMap<String, PatientIdentifierType>();
	}
	
	public DataSetRow renderRow(Patient p,
			PatientIdentifierType patientIdentifierType,
			Location locationParameter, Date startDateParameter,
			Date endDateParameter) {
		return null;
	}

	protected void addCol(DataSetRow row, String label, String value) {
		DataSetColumn c = new DataSetColumn(label, label, String.class);
		row.addColumnValue(c, h(value));
	}

	protected String preferredIdentifierAtLocation(Patient p, PatientIdentifierType identifierType, Location locationParameter) {
		String ret = "";
		List<PatientIdentifier> pis = p.getPatientIdentifiers(identifierType);
		for (PatientIdentifier pi : pis) {
			if (pi != null && pi.getLocation() != null && locationParameter != null && pi.getLocation().getId() == locationParameter.getId()) {
				ret = formatPatientIdentifier(pi.getIdentifier());
				if (pi.isPreferred()) {
					return ret;
				}
			}
		}
		return ret;
	}

	protected String patientLink(Patient p,
			PatientIdentifierType identifierType, Location locationParameter) {
		// todo, get current id and/or preferred one first
		String patientLink = "(not applicable)";
		// todo, don't hardcode server
		String url = "http://emr:8080/" + WebConstants.WEBAPP_NAME;
		List<PatientIdentifier> pis = p.getPatientIdentifiers(identifierType);
		for (PatientIdentifier pi : pis) {
			if (pi != null && pi.getLocation() != null
					&& locationParameter != null
					&& pi.getLocation().getId() == locationParameter.getId()) {
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
		return patientLink;
	}

	private String formatPatientIdentifier(String id) {
		try {
			if (id.endsWith(" HCC")) {
				int firstSpace = id.indexOf(" ");
				int lastSpace = id.lastIndexOf(" ");
				String number = id.substring(firstSpace + 1, lastSpace);
				try {
					DecimalFormat f = new java.text.DecimalFormat("0000");
					number = f.format(new Integer(number));
				} catch (Exception e) {
					// error while converting
					return id;
				}
				return id.substring(0, firstSpace) + "-" + number + "-HCC";
			} else {
				if (id.lastIndexOf(" ") > 0) {
					// for now assume that an id without leading zeros is there
					// when
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
		} catch (Exception e) {
			return "(error)";
		}
	}

	protected String h(String s) {
		return ("".equals(s) || s == null ? "" : s);
	}

	protected void addDemographicCols(DataSetRow row, Patient p,
			Date endDateParameter) {
		try {
			DataSetColumn c = null;
			c = new DataSetColumn("Given name", "Given name", String.class);
			row.addColumnValue(c, p.getGivenName());
			c = new DataSetColumn("Lastname", "Lastname", String.class);
			row.addColumnValue(c, p.getFamilyName());
			c = new DataSetColumn("Birthdate", "Birthdate", Date.class);
			row.addColumnValue(c, p.getBirthdate());
			c = new DataSetColumn("Current Age (yr)", "Current Age (yr)",
					Integer.class);
			row.addColumnValue(c, p.getAge(endDateParameter));
			c = new DataSetColumn("Current Age (mth)", "Current Age (mth)",
					Integer.class);
			row.addColumnValue(c, getAgeInMonths(p, endDateParameter));
			c = new DataSetColumn("M/F", "M/F", String.class);
			row.addColumnValue(c, p.getGender());
			c = new DataSetColumn("Village", "Village", String.class);
			if (p.getPersonAddress() != null) {
				row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addDemographicTaDistrictCols(DataSetRow row, Patient p,
			Date endDateParameter) {
		try {
			DataSetColumn c2 = new DataSetColumn("TA", "TA", String.class);
			DataSetColumn c3 = new DataSetColumn("District", "District", String.class);
			if (p.getPersonAddress() != null) {
				row.addColumnValue(c2, h(p.getPersonAddress().getCountyDistrict()));
				row.addColumnValue(c3, h(p.getPersonAddress().getStateProvince()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addAllEnrollmentsCol(DataSetRow row, Patient p) {
		try {
			DataSetColumn c;
			c = new DataSetColumn("All Enrollments (not filtered)", "All Enrollments (not filtered)",
					String.class);
			String programs = "";

			// just collect everything latest program enrollment you can find
			Set<PatientState> pss = h.getMostRecentStates(p);
			if (pss != null) {
				Iterator<PatientState> i = pss.iterator();
				while (i.hasNext()) {
					PatientState ps = i.next();
					programs += ps.getPatientProgram().getProgram().getName()
							+ ": " + ps.getState().getConcept().getName()
							+ " (since "
							+ formatEncounterDate(ps.getStartDate()) + "); ";
				}
			}
			row.addColumnValue(c, h(programs));
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected String formatEncounterDate(Date encounterDatetime) {
		if (encounterDatetime == null) {
			return "<Unknown>";
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(encounterDatetime);
	}

	protected int getAgeInMonths(Patient p, Date onDate) {
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

	public String identifiers(Patient p, PatientIdentifierType piType) {
		String arvs = "";
		if (piType != null) {
			for (PatientIdentifier pi : p.getPatientIdentifiers(piType)) {
				if (pi != null && pi.getLocation() != null) {
					arvs += formatPatientIdentifier(pi.getIdentifier()) + " ";
				}
			}
		}
		return arvs;
	}

	protected SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	protected void addVhwCol(DataSetRow row, Patient p) {
		try {
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
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addLastVisitCols(DataSetRow row, Patient p,
			List<EncounterType> encounterTypes, String visitClassification) {
		try {
			List<Encounter> encounters = Context.getEncounterService()
					.getEncounters(p, null, null, null, null, encounterTypes,
							null, false);
			DataSetColumn c1 = new DataSetColumn("Last visit date " + visitClassification + " (not filtered)",
					"Last visit date " + visitClassification, Date.class);
			DataSetColumn c2 = new DataSetColumn("Last visit loc",
					"Last visit loc", String.class);
			DataSetColumn c3 = new DataSetColumn("Last visit appt date",
					"Last visit appt date", Date.class);
			DataSetColumn c4 = new DataSetColumn("Last visit type",
					"Last visit type", String.class);
			if (!encounters.isEmpty()) {
				Encounter e = encounters.get(encounters.size() - 1);
				row.addColumnValue(c1, e.getEncounterDatetime());
				row.addColumnValue(c2, e.getLocation());

				// rvd from last encounter
				Set<Obs> observations = e.getObs();
				for (Obs o : observations) {
					if (o.getConcept().equals(lookupConcept("Appointment date"))) {
						row.addColumnValue(c3, o.getValueDatetime());
						break;
					}
				}
				row.addColumnValue(c4, e.getEncounterType().getName());
			} else {
				row.addColumnValue(c1, h("(no encounter found)"));
				row.addColumnValue(c2, h(""));
				row.addColumnValue(c3, h(""));
				row.addColumnValue(c4, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}

	}

	protected void addVisitColsOfVisitX(DataSetRow row, Patient p,
			List<EncounterType> encounterTypes, int visitNumber, String visitClassification) {
		try {
			List<Encounter> encounters = Context.getEncounterService()
					.getEncounters(p, null, null, null, null, encounterTypes,
							null, false);
			DataSetColumn c1 = new DataSetColumn("Visit #" + visitNumber + " date " + visitClassification + " (not filtered)",
					"Visit #" + visitNumber + " date " + visitClassification + " (not filtered)", Date.class);
			DataSetColumn c2 = new DataSetColumn("Visit #" + visitNumber + " loc",
					"Visit #" + visitNumber + " loc", String.class);
			DataSetColumn c3 = new DataSetColumn("Visit #" + visitNumber + " appt date",
					"Visit #" + visitNumber + " appt date", Date.class);
			DataSetColumn c4 = new DataSetColumn("Visit #" + visitNumber + " type",
					"Visit #" + visitNumber + " type", String.class);
			if (encounters.size() >= visitNumber) {
				Encounter e = encounters.get(visitNumber - 1);
				row.addColumnValue(c1, e.getEncounterDatetime());
				row.addColumnValue(c2, e.getLocation());

				// rvd from last encounter
				Set<Obs> observations = e.getObs();
				for (Obs o : observations) {
					if (o.getConcept().equals(lookupConcept("Appointment date"))) {
						row.addColumnValue(c3, o.getValueDatetime());
						break;
					}
				}
				row.addColumnValue(c4, e.getEncounterType().getName());
			} else {
				row.addColumnValue(c1, h("(no encounter found)"));
				row.addColumnValue(c2, h(""));
				row.addColumnValue(c3, h(""));
				row.addColumnValue(c4, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addOutcomeFromStateCols(DataSetRow row, Patient p,
			Location locationParameter, ProgramWorkflow pw, ProgramWorkflowState stateBeforeStateChange) {
		try {
			PatientState ps = null;
			// enrollment outcome from location
			ps = h.getMostRecentStateAtLocation(p, pw, locationParameter
			);

			DataSetColumn c = new DataSetColumn("Outcome", "Outcome", String.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getState().getConcept().getName().getName());
			}
			c = new DataSetColumn("Outcome change date", "Outcome change Date", Date.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getStartDate());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addOutcomeCols(DataSetRow row, Patient p,
			Location locationParameter, Date endDate, ProgramWorkflow pw) {
		try {
			PatientState ps = null;
			if (locationParameter != null) {
				// enrollment outcome from location
				ps = h.getMostRecentStateAtLocationAndDate(p, pw, locationParameter, endDate
				);
			} else {
				ps = h.getMostRecentStateAtDate(p, pw, endDate);
			}

			DataSetColumn c = new DataSetColumn("Outcome", "Outcome",
					String.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getState().getConcept().getName()
						.getName());
			}
			c = new DataSetColumn("Outcome change date", "Outcome change Date", Date.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getStartDate());
			}
			c = new DataSetColumn("Outcome location", "Outcome location",
					String.class);
			if (ps != null && locationParameter == null) {
				// register for all locations
				row.addColumnValue(c, h.getEnrollmentLocation(ps
						.getPatientProgram()));
			} else if (ps != null && locationParameter != null) {
				row.addColumnValue(c, locationParameter);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentOutcomeWithinDatabaseCols(DataSetRow row,
			Patient p, ProgramWorkflow pw) {
		try {
			PatientState ps = null;
			ps = h.getMostRecentState(p, pw);

			DataSetColumn c1 = new DataSetColumn("Last Outcome in DB (not filtered)",
					"Last Outcome in DB", String.class);
			DataSetColumn c2 = new DataSetColumn("Last Outcome change date",
					"Last Outcome change Date", Date.class);
			DataSetColumn c3 = new DataSetColumn("Last Outcome change loc",
					"Last Outcome change loc", String.class);
			if (ps != null) {
				row.addColumnValue(c1, ps.getState().getConcept().getName().getName());
				row.addColumnValue(c2, ps.getStartDate());
				row.addColumnValue(c3, h.getEnrollmentLocation(ps.getPatientProgram()));
			} else {
				row.addColumnValue(c1, h(""));
				row.addColumnValue(c2, h(""));
				row.addColumnValue(c3, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}

	}

	protected void addFirstTimeChangeToStateDateCols(DataSetRow row, Patient p,
			ProgramWorkflowState state, String header, Date endDate) {
		DataSetColumn c1 = new DataSetColumn(header + " date", header + " date", Date.class);
		DataSetColumn c2 = new DataSetColumn(header + " location", header + " location", String.class);
		PatientState ps = h.getFirstTimeInState(p, state.getProgramWorkflow().getProgram(), state, endDate);
		if (ps != null) {
			row.addColumnValue(c1, ps.getStartDate());
			// first on art at
			row.addColumnValue(c2, firstTimeInStateAtLocation(p, state.getProgramWorkflow().getProgram(), state, endDate));
		} else {
			row.addColumnValue(c1, h(""));
			row.addColumnValue(c2, h(""));
		}
	}

	protected void addEnrollmentDateCols(DataSetRow row, Patient p, Location locationParameter, Program program, String label) {
		try {
			DataSetColumn c = new DataSetColumn(label, label, Date.class);
			if (locationParameter != null) {
				PatientProgram pp = h.getMostRecentProgramEnrollmentAtLocation(p, program, locationParameter);
				row.addColumnValue(c, pp.getDateEnrolled());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addEnrollmentDateCols(DataSetRow row, Patient p,
			Location locationParameter, ProgramWorkflowState state, String label) {
		try {
			DataSetColumn c = new DataSetColumn(label, label, Date.class);
			if (locationParameter != null) {
				List<PatientState> states = h.getPatientStatesByWorkflowAtLocation(p, state, locationParameter);
				PatientState firstState = states.get(0);
				row.addColumnValue(c, firstState.getPatientProgram().getDateEnrolled());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addFirstTimeEnrollmentCols(DataSetRow row, Patient p,
			ProgramWorkflowState state, Date endDate, String label) {
		try {
			DataSetColumn c = new DataSetColumn(label, label, Date.class);
			row.addColumnValue(c, h.getFirstTimeInState(p, state.getProgramWorkflow().getProgram(), state, endDate).getPatientProgram().getDateEnrolled());

			// first on art at
			c = new DataSetColumn(label + " location", label + " location", String.class);
			row.addColumnValue(c, firstTimeInStateAtLocation(p, state.getProgramWorkflow().getProgram(), state, endDate));
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addFirstEncounterCols(DataSetRow row, Patient p,
			EncounterType encounterType, String label, Date endDate) {
		try {
			List<Encounter> encounters = Context.getEncounterService()
					.getEncounters(p, null, null, endDate, null,
							Arrays.asList(encounterType), null, false);
			DataSetColumn c1 = new DataSetColumn(label + " date", label
					+ " date", Date.class);
			DataSetColumn c2 = new DataSetColumn(label + " location", label
					+ " location", String.class);
			if (!encounters.isEmpty()) {
				Encounter e = encounters.get(encounters.size() - 1);
				row.addColumnValue(c1, e.getEncounterDatetime());
				row.addColumnValue(c2, e.getLocation());
			} else {
				row.addColumnValue(c1, h(""));
				row.addColumnValue(c2, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addFirstChangeToStateCols(DataSetRow row, Patient p,
			ProgramWorkflowState state, Date endDate, String label) {
		try {
			DataSetColumn c1 = new DataSetColumn(label + " date", label
					+ " date", Date.class);
			DataSetColumn c2 = new DataSetColumn(label + " location", label
					+ " location", String.class);
			PatientState ps = h.getFirstTimeInState(p, state.getProgramWorkflow()
					.getProgram(), state, endDate);
			if (ps != null) {
				row.addColumnValue(c1, ps.getStartDate());
				row.addColumnValue(
						c2,
						firstTimeInStateAtLocation(p, state
								.getProgramWorkflow().getProgram(), state, endDate));
			} else {
				row.addColumnValue(c1, h(""));
				row.addColumnValue(c2, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentVitalsCols(DataSetRow row, Patient p,
			Date endDateParameter) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDateParameter, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es,
					Arrays.asList(lookupConcept("Height (cm)")), null, null,
					null, null, 1, null, null, endDateParameter, false);
			DataSetColumn c = null;
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				c = new DataSetColumn("Height (cm)", "Height (cm)",
						String.class);
				row.addColumnValue(c, (o.getValueNumeric()));
			}
			obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es,
					Arrays.asList(lookupConcept("Weight (kg)")), null, null,
					null, null, 1, null, null, endDateParameter, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				c = new DataSetColumn("Weight (kg)", "Weight (kg)",
						String.class);
				row.addColumnValue(c, (o.getValueNumeric()));
				c = new DataSetColumn("Weight date", "Weight date",
						Date.class);
				row.addColumnValue(c, o.getObsDatetime());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentDatetimeObsCols(DataSetRow row, Patient p,
			Concept concept, Date endDate) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDate, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(concept),
					null, null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				DataSetColumn c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName(),
						"Last "
								+ concept.getName(Context.getLocale())
										.getName(), Date.class);
				row.addColumnValue(c, o.getValueDatetime());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentObsCols(DataSetRow row, Patient p,
			Concept concept, Date endDate) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDate, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(concept),
					null, null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				DataSetColumn c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName(),
						"Last "
								+ concept.getName(Context.getLocale())
										.getName(), String.class);
				row.addColumnValue(c, o.getValueAsString(Context.getLocale()));

				c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", "Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", Date.class);
				row.addColumnValue(c, o.getObsDatetime());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentNumericObsCols(DataSetRow row, Patient p,
			Concept concept, Date endDate) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDate, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(concept),
					null, null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				DataSetColumn c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName(),
						"Last "
								+ concept.getName(Context.getLocale())
										.getName(), String.class);
				row.addColumnValue(c, o.getValueNumeric());

				c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", "Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", Date.class);
				row.addColumnValue(c, o.getObsDatetime());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addReasonStartingArvsCols(DataSetRow row, Patient p, Date endDate) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDate, null, Arrays.asList(lookupEncounterType("ART_INITIAL")), null, false);
			String reasons = "";

			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("CD4 count")), null,
					null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				reasons += ", CD4: " + o.getValueAsString(Context.getLocale());
			}
			obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Kaposis sarcoma side effects worsening while on ARVs?")), null,
					null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				reasons += ", KS: " + o.getValueAsString(Context.getLocale());
			}
			obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Tuberculosis treatment status")), null,
					null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				reasons += ", TB: " + o.getValueAsString(Context.getLocale());
			}
			obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("WHO stage")), null,
					null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				reasons += ", STAGE: " + o.getValueAsString(Context.getLocale());
			}
			obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Cd4%")), null,
					null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				reasons += ", TLC: " + o.getValueAsString(Context.getLocale());
			}
			obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Presumed severe HIV criteria present")), null,
					null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				reasons += ", PSHD: " + o.getValueAsString(Context.getLocale());
			}
			DataSetColumn c = new DataSetColumn("ARV start reasons", "ARV start reasons", String.class);
			row.addColumnValue(c, reasons);
		} catch (Exception e) {
			log.error(e);
		}
	}

	private Location firstTimeInStateAtLocation(Patient p, Program program,
			ProgramWorkflowState firstTimeInState, Date endDate) {
		PatientState ps = h.getFirstTimeInState(p, program, firstTimeInState, endDate);
		if (ps != null) {
			return h.getEnrollmentLocation(ps.getPatientProgram()
			);
		}
		return null;
	}
/*
	private PatientState firstTimeInState(Patient p, Program program,
			ProgramWorkflowState firstTimeInState) {
		PatientState ps = h.getFirstTimeInState(p, program, firstTimeInState);
		return ps;
	}

	private PatientState firstTimeInState(Patient p, Program program,
			ProgramWorkflowState firstTimeInState, Date endDate) {
		PatientState ps = h.getFirstTimeInState(p, program, firstTimeInState, endDate);
		return ps;
	}
*/
	protected PatientProgram currentPatientProgram(Program program,
			Patient patient) {
		List<org.openmrs.PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(patient, program, null, null, new Date(),
						null, false);
		if (pps.size() == 1) {
			return (PatientProgram) pps.get(0);
		}
		return null;
	}

	protected PatientIdentifierType lookupPatientIdentifierType(String name) {
		if (!patientIdentifierTypeCache.containsKey(name)) {
			patientIdentifierTypeCache.put(name, Context.getPatientService().getPatientIdentifierTypeByName(name));
		}
		return patientIdentifierTypeCache.get(name);
	}

	protected Concept lookupConcept(String name) {
		if (!conceptCache.containsKey(name)) {
			conceptCache.put(name, Context.getConceptService().getConceptByName(name));
		}
		return conceptCache.get(name);
	}

	protected EncounterType lookupEncounterType(String string) {
		if (!encounterTypeCache.containsKey(string)) {
			encounterTypeCache.put(string, Context.getEncounterService().getEncounterType(string));
		}
		return encounterTypeCache.get(string); 
	}

	protected ProgramWorkflow lookupProgramWorkflow(String program,
			String workflow) {
		if (!programWorkflowCache.containsKey(""+program+workflow)) {
			programWorkflowCache.put(""+program+workflow, lookupProgram(program).getWorkflowByName(workflow));
		} 
		return programWorkflowCache.get(""+program+workflow);
	}

	protected Program lookupProgram(String program) {
		if (!programCache.containsKey(program)) {
			programCache.put(program, Context.getProgramWorkflowService().getProgramByName(
				program));
		}
		return programCache.get(program);
	}

	protected ProgramWorkflowState lookupProgramWorkflowState(String program,
			String workflow, String state) {
		if (!programWorkflowStateCache.containsKey(""+program+workflow+state)) {
			programWorkflowStateCache.put(""+program+workflow+state, Context.getProgramWorkflowService().getProgramByName(program)
				.getWorkflowByName(workflow).getStateByName(state));
		}
		return programWorkflowStateCache.get(""+program+workflow+state);
	}

}
