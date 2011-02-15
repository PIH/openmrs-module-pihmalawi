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
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { StateRelativeToStateCohortDefinition.class })
public class StateRelativeToStateEvaluator  implements
CohortDefinitionEvaluator {
	
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

Location firstStateLocation = definition.getFirstStateLocation();
Location secondStateLocation = definition.getSecondStateLocation();
Date onOrAfter = definition.getOnOrAfter();
Date onOrBefore = definition.getOnOrBefore();
Date onDate = definition.getOnDate();
ProgramWorkflowState firstState = definition.getFirstState();
ProgramWorkflowState secondState = definition.getSecondState();
Integer offsetAmount = definition.getOffsetAmount();
DurationUnit offsetUnit = definition.getOffsetUnit();
Calendar cal = Calendar.getInstance();

if (onDate != null) {
	onOrAfter = onDate;
	onOrBefore = onDate;
}

// get all patients in second state in window, enrolled at location 
Cohort patients = q.getPatientsInStateAtLocation(secondState, onOrAfter, onOrBefore, secondStateLocation);

// loop through every patient -- **************** why not just use patients cohort instead of ids????
for (Integer id : patients.getMemberIds()) {
	Patient p = ps.getPatient(id);

	 // go back through same patients to get all instances of second state start date
	// Can this redundancy be improved???
	List<PatientState> patientStateList = h.getPatientStatesByWorkflowAtLocation(p,
			secondState, secondStateLocation, sessionFactory()
					.getCurrentSession());
	for(PatientState patientState : patientStateList) {
		Date secondStateStartDate = patientState.getStartDate();
		
		// if second state start date is NOT in report window, skip it
		if(! (secondStateStartDate.after(onOrAfter) && secondStateStartDate.before(onOrBefore)))
			continue;
		
		// get window BETWEEN second state start date and interval, to be used for first state date range
		cal.setTime(secondStateStartDate);
		cal.add(offsetUnit.getCalendarField(), -offsetUnit.getFieldQuantity()*offsetAmount);
		Date intervalCalculatedDate = cal.getTime();
		
		// get patients who had first state at location in time interval
		// ************** maybe better to lookup patient and see if firststate happened in interval and if so, add patient to list
		Cohort patients2 = q.getPatientsInStateAtLocation(firstState,
				intervalCalculatedDate, secondStateStartDate, firstStateLocation);
		// check if patient belongs in both first state cohort and second state cohort
		if(patients2.contains(p.getPatientId())) {
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
