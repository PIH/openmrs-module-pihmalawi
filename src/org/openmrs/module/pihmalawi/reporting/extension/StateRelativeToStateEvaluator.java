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

@Handler(supports = { StateRelativeToStateCohortDefinition.class })
public class StateRelativeToStateEvaluator implements CohortDefinitionEvaluator {

	protected static final Log log = LogFactory
			.getLog(StateRelativeToStateEvaluator.class);

	public Cohort evaluate(CohortDefinition cohortDefinition,
			EvaluationContext context) {

		Helper h = new Helper();
		Cohort result = new Cohort();

		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context
				.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(
						0);
		StateRelativeToStateCohortDefinition definition = (StateRelativeToStateCohortDefinition) cohortDefinition;
		PatientService ps = Context.getPatientService();

		Location relativeStateLocation = definition.getRelativeStateLocation();
		Location primaryStateLocation = definition.getPrimaryStateLocation();
		Date onOrAfter = definition.getOnOrAfter();
		Date onOrBefore = definition.getOnOrBefore();
		Date onDate = definition.getOnDate();
		ProgramWorkflowState relativeState = definition.getRelativeState();
		ProgramWorkflowState primaryState = definition.getPrimaryState();
		Integer offsetAmount = definition.getOffsetAmount();
		Integer offsetDuration = definition.getOffsetDuration();
		DurationUnit offsetUnit = definition.getOffsetUnit();
		Event primaryStateEvent = definition
				.getPrimaryStateEvent(); // started or stopped state??
		Event relativeStateEvent = definition
				.getRelativeStateEvent();  // started or stopped state??
		BeforeAfter beforeAfter = definition.getBeforeAfter();

		if (onDate != null) {
			onOrAfter = onDate;
			onOrBefore = onDate;
		}
		
		// duration should be optional, assumed to be the same as amount unless specified
		if(offsetDuration == -1) {
			offsetDuration = offsetAmount;
		}
		
		log.warn("offsetAmount="+offsetAmount);
		log.warn("offsetDuration="+offsetDuration);

		switch (beforeAfter) {
		case AFTER:
			// interval has primary state AFTER relative state
			offsetAmount = -offsetAmount;
			offsetDuration = -offsetDuration;
			break;
		}

		// get all patients for primary state in report window, enrolled at
		// location
		Cohort primaryPatients = null;
		switch (primaryStateEvent) {
		case STARTED:
			primaryPatients = q.getPatientsHavingStatesAtLocation(primaryState,
					onOrAfter, onOrBefore, null, null, primaryStateLocation);
			break;
		case STOPPED:
			primaryPatients = q.getPatientsHavingStatesAtLocation(primaryState,
					null, null, onOrAfter, onOrBefore, primaryStateLocation);
			break;
		}

		for (Integer id : primaryPatients.getMemberIds()) {
			Patient p = ps.getPatient(id);

			// go back through same primary state patients to get all instances
			// of primary state start and end dates
			// Can this redundancy be improved???
			List<PatientState> patientStateList = h
					.getPatientStatesByWorkflowAtLocation(p, primaryState,
							primaryStateLocation, sessionFactory()
									.getCurrentSession());
			for (PatientState patientState : patientStateList) {
				
				Date primaryStateStartDate = patientState.getStartDate(); // ***** CAN THIS BE NULL???
				Date primaryStateEndDate = patientState.getEndDate();
				Date offsetEndDate = null;
				Date offsetStartDate = null;
				Cohort relativePatients = null;

				// get start and end date of PRIMARY state for relative state
				// date range
				switch (primaryStateEvent) {
				case STARTED:
					if (primaryStateStartDate == null)
						continue;
					// if instance did NOT occur in report window, skip it
					if (!(primaryStateStartDate.after(onOrAfter) && primaryStateStartDate
							.before(onOrBefore))) {
						continue;
					} else { // ***********  REVERSE names of offsetStartDate and offsetEndDate -- confusing now
						offsetStartDate = calculateInterval(
								primaryStateStartDate, offsetUnit, (offsetAmount - offsetDuration)); // what should be excluded
						offsetEndDate = calculateInterval(
								primaryStateStartDate, offsetUnit, offsetAmount); // the offset
					}

					// get patients with RELATIVE state at location in date range
					// *** Change method name to having State not States????
					switch (relativeStateEvent) {
					case STARTED:
						if (beforeAfter.equals(BeforeAfter.AFTER)) {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState,
											offsetEndDate,
											offsetStartDate, null, null,
											relativeStateLocation);
						} else {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState,
											offsetStartDate,
											offsetEndDate, null, null,
											relativeStateLocation);
						}
						break;
					case STOPPED:
						if (beforeAfter.equals(BeforeAfter.AFTER)) {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState, null, null,
											offsetEndDate,
											offsetStartDate,
											relativeStateLocation);
						} else {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState, null, null,
											offsetStartDate,
											offsetEndDate,
											relativeStateLocation);
						}
						break;
					}
					break;
				case STOPPED:
					if (primaryStateEndDate == null)
						continue;
					// if instance did NOT occur in report window, skip it
					if (!(primaryStateEndDate.after(onOrAfter) && primaryStateEndDate
							.before(onOrBefore))) {
						continue;
					} else {
						offsetStartDate = calculateInterval(
								primaryStateEndDate, offsetUnit, (offsetAmount - offsetDuration)); // what should be excluded
						offsetEndDate = calculateInterval(
								primaryStateEndDate, offsetUnit, offsetAmount); // the offset
					}

					// get patients with RELATIVE state at location in date range
					// *** Change name to having State????
					switch (relativeStateEvent) {
					case STARTED:
						if (beforeAfter.equals(BeforeAfter.AFTER)) {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState,
											offsetEndDate,
											offsetStartDate, null, null,
											relativeStateLocation);
						} else {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState, offsetStartDate,
											offsetEndDate, null, null,
											relativeStateLocation);
						}
						break;
					case STOPPED:
						if (beforeAfter.equals(BeforeAfter.AFTER)) {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState, null, null,
											offsetEndDate,
											offsetStartDate,
											relativeStateLocation);
						} else {
							relativePatients = q
									.getPatientsHavingStatesAtLocation(
											relativeState, null, null,
											offsetStartDate,
											offsetEndDate,
											relativeStateLocation);
						}
						break;
					}
					break;
				}

				// check if patient belongs in both relative state cohort and
				// primary state cohort
				if (relativePatients.contains(p.getPatientId())) {
					result.addMember(p.getId());
				}
			}
		}
		return result;
	}

	private Date calculateInterval(Date date, DurationUnit offsetUnit,
			Integer offsetAmount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(offsetUnit.getCalendarField(), offsetUnit.getFieldQuantity()
				* offsetAmount);
		return cal.getTime();
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}
}