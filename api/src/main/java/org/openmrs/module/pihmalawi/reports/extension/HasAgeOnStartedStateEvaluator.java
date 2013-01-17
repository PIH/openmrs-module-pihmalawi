package org.openmrs.module.pihmalawi.reports.extension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { HasAgeOnStartedStateCohortDefinition.class })
public class HasAgeOnStartedStateEvaluator implements CohortDefinitionEvaluator {

	protected static final Log log = LogFactory
			.getLog(InStateAfterStartedStateEvaluator.class);

	public Cohort evaluate(CohortDefinition cohortDefinition,
			EvaluationContext context) {

		ProgramHelper h = new ProgramHelper();
		Cohort result = new Cohort();

		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context
				.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(
						0);
		HasAgeOnStartedStateCohortDefinition definition = (HasAgeOnStartedStateCohortDefinition) cohortDefinition;
		PatientService ps = Context.getPatientService();

		ProgramWorkflowState state = definition.getState();
		Location location = definition.getLocation();
		Date startedOnOrAfter = definition.getStartedOnOrAfter();
		Date startedOnOrBefore = DateUtil.getEndOfDayIfTimeExcluded(definition.getStartedOnOrBefore());
		
		Integer minAge = definition.getMinAge();
		DurationUnit minAgeUnit = definition.getMinAgeUnit();
		Integer maxAge = definition.getMaxAge();
		DurationUnit maxAgeUnit = definition.getMaxAgeUnit();

		// get all patients who started primary state in report window, enrolled at
		// location
		Cohort primaryPatients = q.getPatientsHavingStatesAtLocation(state,
				startedOnOrAfter, startedOnOrBefore, null, null, location);

		for (Integer id : primaryPatients.getMemberIds()) {
			Patient p = ps.getPatient(id);

			// go back through same primary state patients to get all instances
			// of primary state start and end dates
			// Can this redundancy be improved???
			List<PatientState> patientStateList = h
					.getPatientStatesByWorkflowAtLocation(p, state,
							location);
			
			//*************  WOULD HAVE TO BE THE FIRST TIME STARTING ARVS???
			
			// is this list in order from first to last???
			for(PatientState patientState : patientStateList) {
				Date stateStartDate = patientState.getStartDate();
				
				// if state instance is NOT in report window, skip it -- should use onOrAfter and OnOrBefore but if dates include times, no biggie
				// why would start date ever be null??? -- line this up with db query logic somehow???
				if (!(stateStartDate.after(startedOnOrAfter) && stateStartDate.before(startedOnOrBefore))) 
					continue;
				
				Date patientBirthdate = p.getBirthdate();
				
				if(patientBirthdate == null)
					continue;
				
				Date maxDate = null;
				Date minDate = null;
				
				boolean minAgeOk = true;
				if(minAge != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(stateStartDate);
					cal.add(minAgeUnit.getCalendarField(), -minAgeUnit.getFieldQuantity()*minAge);
					cal.add(Calendar.DATE,1);
					maxDate = cal.getTime();
					if(!patientBirthdate.before(maxDate))
						minAgeOk = false;
				}
				
				boolean maxAgeOk = true;
				if(maxAge != null) {
					Calendar cal = Calendar.getInstance(); 
					cal.setTime(stateStartDate);
					cal.add(maxAgeUnit.getCalendarField(), -(maxAgeUnit.getFieldQuantity()*maxAge+1));
					minDate = cal.getTime();
					if(!patientBirthdate.after(minDate))
						maxAgeOk = false;
				}
				
				if(minAgeOk && maxAgeOk) {
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