package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Calendar;
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
import org.openmrs.module.pihmalawi.reporting.BeforeAfter;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.Event;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { OnStateAfterStartedStateCohortDefinition.class })
public class OnStateAfterStartedStateEvaluator implements CohortDefinitionEvaluator {

	protected static final Log log = LogFactory
			.getLog(OnStateAfterStartedStateEvaluator.class);

	public Cohort evaluate(CohortDefinition cohortDefinition,
			EvaluationContext context) {

		Helper h = new Helper();
		Cohort result = new Cohort();

		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context
				.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(
						0);
		OnStateAfterStartedStateCohortDefinition definition = (OnStateAfterStartedStateCohortDefinition) cohortDefinition;
		PatientService ps = Context.getPatientService();

		Location primaryStateLocation = definition.getPrimaryStateLocation();
		Date onDate = definition.getOnDate();
		ProgramWorkflowState relativeState = definition.getRelativeState();
		ProgramWorkflowState primaryState = definition.getPrimaryState();
		Integer offsetAmount = definition.getOffsetAmount();
		Integer offsetDuration = definition.getOffsetDuration();
		DurationUnit offsetUnit = definition.getOffsetUnit();
		boolean within = definition.getOffsetWithin();
		Calendar cal = Calendar.getInstance();
		
		if(offsetDuration == -1) {
			offsetDuration = offsetAmount;
		}

		// get all patients for primary state at end of report window, enrolled at
		// location
		Cohort primaryPatients = q.getPatientsInStateAtLocation(primaryState,
					onDate, onDate, primaryStateLocation);
			

		for (Integer id : primaryPatients.getMemberIds()) {
			Patient p = ps.getPatient(id);

			// go back through same primary state patients to get all instances
			// of primary state start and end dates
			// Can this redundancy be improved???
			List<PatientState> patientStateList = h
					.getPatientStatesByWorkflowAtLocation(p, primaryState,
							primaryStateLocation, sessionFactory()
									.getCurrentSession());

			for(PatientState patientState : patientStateList) {
				Date primaryStateStartDate = patientState.getStartDate();
				Date primaryStateEndDate = patientState.getEndDate();
				
				// if state instance is NOT in report window, skip it -- should use onOrAfter and OnOrBefore but if dates include times, no biggie
				// why would start date ever be null??? -- line this up with db query logic somehow???
				 if((primaryStateStartDate == null) || ! (primaryStateEndDate == null || primaryStateEndDate.after(onDate)) && (primaryStateStartDate.before(onDate)))
					continue;
				
				// get window BETWEEN primary state start date and interval, to be used for relative state date range
				cal.setTime(primaryStateStartDate);
				cal.add(offsetUnit.getCalendarField(), -offsetUnit.getFieldQuantity()*offsetAmount);
				Date offsetDate = cal.getTime(); // the offset
				
				cal.setTime(primaryStateStartDate);
				cal.add(offsetUnit.getCalendarField(), -offsetUnit.getFieldQuantity()*(offsetAmount - offsetDuration));
				Date offsetExclusionDate = cal.getTime(); // what should be excluded
				
				// get patients who started relative state at location according to time interval
				Cohort relativePatients = null;
				if(within) { // interval between primary state start date and offsetDate
					relativePatients = q
				.getPatientsHavingStatesAtLocation(
						relativeState,
						offsetDate,
						offsetExclusionDate, null, null,
						null);
				} else { // interval is itself excluded, use period before offsetDate
					relativePatients = q
					.getPatientsHavingStatesAtLocation(
							relativeState,
							null,
							offsetDate, null, null,
							null);
				}
				// check if patient belongs in both first state cohort and second state cohort
				if(relativePatients.contains(p.getPatientId())) {
					result.addMember(p.getId());
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
