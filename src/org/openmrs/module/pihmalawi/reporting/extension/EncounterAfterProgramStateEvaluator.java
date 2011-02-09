package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { EncounterAfterProgramStateCohortDefinition.class })
public class EncounterAfterProgramStateEvaluator implements
		CohortDefinitionEvaluator {

	protected static final Log log = LogFactory
			.getLog(EncounterAfterProgramStateEvaluator.class);

	public Cohort evaluate(CohortDefinition cohortDefinition,
			EvaluationContext context) {
		Helper h = new Helper();
		Cohort result = new Cohort();

		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context
				.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(
						0);
		EncounterAfterProgramStateCohortDefinition definition = (EncounterAfterProgramStateCohortDefinition) cohortDefinition;
		PatientService ps = Context.getPatientService();
		EncounterService es = Context.getEncounterService();

		Location enrollmentLoc = definition.getEnrollmentLocation();
		List<Location> clinicLocs = definition.getClinicLocations();
		List<EncounterType> unwantedEncounterTypes = definition
				.getEncounterTypesAfterChangeToTerminalState();
		Date onDate = definition.getOnDate();
		List<ProgramWorkflowState> terminalStates = definition
				.getTerminalStates();

		// get all patients in terminal state at location
		Cohort patients = q.getPatientsInStatesAtLocation(terminalStates,
				onDate, onDate, enrollmentLoc);

		// loop through every patient
		for (Integer id : patients.getMemberIds()) {
			Patient p = ps.getPatient(id);

			// get date of change to terminal state
			PatientState mostRecentState = h.getMostRecentStateAtLocation(p,
					terminalStates, enrollmentLoc, sessionFactory()
							.getCurrentSession());
			if (mostRecentState != null
					&& mostRecentState.getStartDate() != null) {
				// check if more recent encounter happened after date of change
				// to terminal state at this location
				for (Location clinicLoc : clinicLocs) {
					// 1d = 1000*60*60*24
					Date oneDayLater = new Date(mostRecentState.getStartDate().getTime() + 86400000);
					List<Encounter> encounters = es.getEncounters(p, clinicLoc,
							oneDayLater, null, null,
							unwantedEncounterTypes, null, false);
					if (!encounters.isEmpty()) {
						result.addMember(p.getId());
					}
				}
			}
		}
		return result;
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

}