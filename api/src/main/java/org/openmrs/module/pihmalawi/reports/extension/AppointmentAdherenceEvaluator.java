package org.openmrs.module.pihmalawi.reports.extension;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@Handler(supports = { AppointmentAdherenceCohortDefinition.class })
public class AppointmentAdherenceEvaluator implements CohortDefinitionEvaluator {
	
	@Autowired
	EncounterService encounterService;
	
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		AppointmentAdherenceCohortDefinition cd = (AppointmentAdherenceCohortDefinition) cohortDefinition;

		// build adherence structure for easy(?) navigation
		AppointmentAdherence aa = new AppointmentAdherence();

		Date toDate = DateUtil.getEndOfDay(cd.getToDate());
		List<Encounter> encounters = encounterService.getEncounters(null, null, cd.getFromDate(), toDate, null, cd.getEncounterTypes(), null, false);
		for (Encounter e : encounters) {
			aa.addEncounter(e.getPatient(), e);
		}

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
						Arrays.asList(e), Arrays.asList(cd.getAppointmentConcept()),
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
			if (adherence >= cd.getMinimumAdherence() && adherence <= cd.getMaximumAdherence()) {
				matchingPatients.addMember(p.getId());
			}

		}

		return new EvaluatedCohort(matchingPatients, cohortDefinition, context);
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
