package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.query.db.hibernate.HibernateCohortQueryDAO;
import org.openmrs.module.reporting.common.DateUtil;

public class HibernatePihMalawiQueryDao {

	protected static final Log log = LogFactory
			.getLog(HibernatePihMalawiQueryDao.class);

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	public Cohort getPatientsInStatesAtLocation(
			List<ProgramWorkflowState> programWorkflowStates, Date onOrAfter,
			Date onOrBefore, Location location) {

		List<Location> locationList = new ArrayList<Location>();
		if (location != null) {
			locationList.add(location);
		}

		return getPatientsInStatesAtLocations(programWorkflowStates, onOrAfter,
				onOrBefore, locationList);
	}

	public Cohort getPatientsInStateAtLocations(
			ProgramWorkflowState programWorkflowState, Date onOrAfter,
			Date onOrBefore, List<Location> locations) {

		List<ProgramWorkflowState> programWorkflowStateList = new ArrayList<ProgramWorkflowState>();
		if (programWorkflowState != null) {
			programWorkflowStateList.add(programWorkflowState);
		}

		return getPatientsInStatesAtLocations(programWorkflowStateList,
				onOrAfter, onOrBefore, locations);
	}

	public Cohort getPatientsInStateAtLocation(
			ProgramWorkflowState programWorkflowState, Date onOrAfter,
			Date onOrBefore, Location location) {

		List<ProgramWorkflowState> programWorkflowStateList = new ArrayList<ProgramWorkflowState>();
		if (programWorkflowState != null) {
			programWorkflowStateList.add(programWorkflowState);
		}

		return getPatientsInStatesAtLocation(programWorkflowStateList,
				onOrAfter, onOrBefore, location);
	}

	public Cohort getPatientsInStatesAtLocations(
			List<ProgramWorkflowState> programWorkflowStates, Date onOrAfter,
			Date onOrBefore, List<Location> locations) {
		
		onOrBefore = DateUtil.getEndOfDayIfTimeExcluded(onOrBefore);

		List<Integer> stateIds = new ArrayList<Integer>();
		if (programWorkflowStates != null) {
			for (ProgramWorkflowState state : programWorkflowStates) {
				stateIds.add(state.getId());
			}
		}

		List<Integer> locationIds = new ArrayList<Integer>();
		if (locations != null) {
			for (Location location : locations) {
				locationIds.add(location.getId());
			}
		}

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_state ps ");
		sql.append("  inner join patient_program pp on ps.patient_program_id = pp.patient_program_id ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where ps.voided = false and pp.voided = false and p.voided = false ");

		// optional clauses
		if (stateIds != null && !stateIds.isEmpty())
			sql.append(" and ps.state in (:stateIds) ");
		if (onOrAfter != null)
			sql.append(" and (ps.end_date is null or ps.end_date >= :onOrAfter) ");
		if (onOrBefore != null)
			sql.append(" and (ps.start_date is null or ps.start_date <= :onOrBefore) ");
		if (locationIds != null && !locationIds.isEmpty())
			sql.append(" and pp.location_id in (:locationIds) ");

		sql.append(" group by pp.patient_id ");

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());
		if (stateIds != null && !stateIds.isEmpty())
			query.setParameterList("stateIds", stateIds);
		if (onOrAfter != null)
			query.setDate("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setDate("onOrBefore", onOrBefore);
		if (locationIds != null && !locationIds.isEmpty())
			query.setParameterList("locationIds", locationIds);
		
		Cohort returnCohort = new Cohort(query.list());
		
		//System.out.println("%%%%%%%%%%%%%%% query members="+returnCohort.getCommaSeparatedPatientIds());
		//System.out.println("%%%%%%%%%%%%%%% query size="+returnCohort.size());

		return returnCohort;
	}

	public Cohort getPatientsHavingStatesAtLocation(
			ProgramWorkflowState programWorkflowState, Date startedOnOrAfter,
			Date startedOnOrBefore, Date endedOnOrAfter, Date endedOnOrBefore,
			Location location) {
		
		endedOnOrBefore = DateUtil.getEndOfDayIfTimeExcluded(endedOnOrBefore);

		List<Integer> stateIds = new ArrayList<Integer>();
		stateIds.add(programWorkflowState.getId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_state ps ");
		sql.append("  inner join patient_program pp on ps.patient_program_id = pp.patient_program_id ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where ps.voided = false and pp.voided = false and p.voided = false ");

		// Create a list of clauses
		if (stateIds != null && !stateIds.isEmpty())
			sql.append(" and ps.state in (:stateIds) ");
		if (startedOnOrAfter != null)
			sql.append(" and ps.start_date >= :startedOnOrAfter ");
		if (startedOnOrBefore != null)
			sql.append(" and ps.start_date <= :startedOnOrBefore ");
		if (endedOnOrAfter != null)
			sql.append(" and ps.end_date >= :endedOnOrAfter ");
		if (endedOnOrBefore != null)
			sql.append(" and ps.end_date <= :endedOnOrBefore ");
		if (location != null)
			sql.append(" and pp.location_id = :location ");

		sql.append(" group by pp.patient_id ");
		log.debug("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());

		if (stateIds != null && !stateIds.isEmpty())
			query.setParameterList("stateIds", stateIds);
		if (startedOnOrAfter != null)
			query.setDate("startedOnOrAfter", startedOnOrAfter);
		if (startedOnOrBefore != null)
			query.setDate("startedOnOrBefore", startedOnOrBefore);
		if (endedOnOrAfter != null)
			query.setDate("endedOnOrAfter", endedOnOrAfter);
		if (endedOnOrBefore != null)
			query.setDate("endedOnOrBefore", endedOnOrBefore);
		if (location != null)
			query.setInteger("location", location.getId());
		
		Cohort returnCohort = new Cohort(query.list());

		return returnCohort;
	}

	public Cohort getPatientsInProgramAtLocation(List<Program> programs,
			Date onOrAfter, Date onOrBefore, Location location) {
		
		onOrBefore = DateUtil.getEndOfDayIfTimeExcluded(onOrBefore);
		
		List<Integer> programIds = new ArrayList<Integer>();
		for (Program program : programs)
			programIds.add(program.getProgramId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_program pp ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where pp.voided = false and p.voided = false ");

		// optional clauses
		if (programIds != null && !programIds.isEmpty())
			sql.append(" and pp.program_id in (:programIds) ");
		if (onOrAfter != null)
			sql.append(" and (pp.date_completed is null or pp.date_completed >= :onOrAfter) ");
		if (onOrBefore != null)
			sql.append(" and (pp.date_enrolled is null or pp.date_enrolled <= :onOrBefore) ");
		if (location != null)
			sql.append(" and pp.location_id = :location ");

		sql.append(" group by pp.patient_id ");
		log.debug("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				sql.toString());
		if (programIds != null && !programIds.isEmpty())
			query.setParameterList("programIds", programIds);
		if (onOrAfter != null)
			query.setDate("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setDate("onOrBefore", onOrBefore);
		if (location != null)
			query.setInteger("location", location.getId());
		
		Cohort returnCohort = new Cohort(query.list());
		
		return returnCohort;
	}

	public Cohort getPatientsAppointmentAdherence(
			List<EncounterType> encounterTypes, Concept appointmentConcept,
			Date fromDate, Date toDate, Integer minimumAdherence,
			Integer maximumAdherence) {
		List<Encounter> encounters = Context.getEncounterService()
				.getEncounters(null, null, fromDate, toDate, null,
						encounterTypes, null, false);

		toDate = DateUtil.getEndOfDay(toDate);

		// build adherence structure for easy(?) navigation
		AppointmentAdherence aa = new AppointmentAdherence();
		for (Encounter e : encounters) {
			aa.addEncounter(e.getPatient(), e);
		}

		// HibernateCohortQueryDAO dao = (HibernateCohortQueryDAO)
		// Context.getRegisteredComponents(HibernateCohortQueryDAO.class).get(0);
		// Set<Integer> patientIds =
		// dao.getPatientsHavingObs(appointmentConcept.getId(), null, modifier,
		// value, fromDate, toDate, providers, encounterType)

		Cohort matchingPatients = new Cohort();
		ObsService os = Context.getObsService();
		// match adherence
		Set<Patient> patients = aa.getPatients();
		for (Patient p : patients) {
			int missed = 0;
			int ontime = 0;

			// loop over all patients
			Object[] es = aa.getEncounters(p).toArray();
			for (int i = 0; i < es.length; i++) {
				Encounter e = (Encounter) es[i];
				// loop over all encounters of this patient in order
				// todo and ouch, three MUST be a better way to get a specific
				// obs from a specific encounter
				List<Obs> obses = os.getObservations(Arrays.asList((Person) p),
						Arrays.asList(e), Arrays.asList(appointmentConcept),
						null, null, null, null, null, null, null, null, false);

				Date appointmentDate = (obses != null && !obses.isEmpty()) ? DateUtil
						.getStartOfDay(obses.get(0).getValueDatetime()) : null;

				if (appointmentDate != null && appointmentDate.before(toDate)) {
					// assume this was last encounter and pre-set nextVisitDate
					// to end of period
					Date nextVisitDate = toDate;
					if (i + 1 < es.length) {
						// next encounter happened
						nextVisitDate = DateUtil
								.getStartOfDay(((Encounter) es[i + 1])
										.getEncounterDatetime());
					}
					// todo, add buffer period
					if (nextVisitDate.equals(appointmentDate) || nextVisitDate.before(appointmentDate)) {
						ontime++;
					} else {
						missed++;
					}
				}
			}

			float f = (1 - (float) missed / (float) (missed + ontime));
			int adherence = (int) (f * 100);
			if (missed == 0 && ontime == 0) {
				// not enough data
				adherence = -1;
			}
			if (adherence >= minimumAdherence && adherence <= maximumAdherence) {
				matchingPatients.addMember(p.getId());
			}

		}
		return matchingPatients;
	}

	class AppointmentAdherence {
		HashMap<Patient, SortedMap<Date, Encounter>> patientsEncounters = new HashMap<Patient, SortedMap<Date, Encounter>>();

		void addEncounter(Patient patient, Encounter e) {
			if (patientsEncounters.containsKey(patient)) {
				patientsEncounters.get(patient)
						.put(e.getEncounterDatetime(), e);
			} else {
				TreeMap<Date, Encounter> tm = new TreeMap<Date, Encounter>();
				tm.put(e.getEncounterDatetime(), e);
				patientsEncounters.put(patient, tm);
			}
		}

		public Collection<Encounter> getEncounters(Patient p) {
			return patientsEncounters.get(p).values();
		}

		Set<Patient> getPatients() {
			return patientsEncounters.keySet();
		}
	}
}
