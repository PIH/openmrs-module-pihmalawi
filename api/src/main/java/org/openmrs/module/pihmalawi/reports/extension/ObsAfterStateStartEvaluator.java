package org.openmrs.module.pihmalawi.reports.extension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { ObsAfterStateStartCohortDefinition.class })
public class ObsAfterStateStartEvaluator implements CohortDefinitionEvaluator {

	protected static final Log log = LogFactory
			.getLog(ObsAfterStateStartEvaluator.class);

	public Cohort evaluate(CohortDefinition cohortDefinition,
			EvaluationContext context) {

		ProgramHelper h = new ProgramHelper();
		Cohort result = new Cohort();

		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context
				.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(
						0);
		ObsAfterStateStartCohortDefinition definition = (ObsAfterStateStartCohortDefinition) cohortDefinition;
		PatientService ps = Context.getPatientService();

		List<Location> locationList = definition.getLocationList();
		Date startedOnOrAfter = definition.getStartedOnOrAfter();
		Date endedOnOrBefore = DateUtil.getEndOfDayIfTimeExcluded(definition.getEndedOnOrBefore());
		ProgramWorkflowState state = definition.getState();
		Concept question = definition.getQuestion();
		TimeModifier timeModifier = definition.getTimeModifier();
		List<EncounterType> encounterTypeList = definition.getEncounterTypeList();
		SetComparator sc = definition.getOperator();
		List<Concept> valueList = definition.getValueList();
				
		Calendar cal = Calendar.getInstance();

		// get all patients who started state in report window, enrolled at
		// location
		Cohort patients = q.getPatientsHavingStatesAtLocation(state,
					startedOnOrAfter, endedOnOrBefore, null, null, locationList.get(0));

		for (Integer id : patients.getMemberIds()) {
			Patient p = ps.getPatient(id);
			// go back through same patients to get all instances
			// of primary state start and end dates
			// Can this redundancy be improved???
			List<PatientState> patientStateList = h
					.getPatientStatesByWorkflowAtLocation(p, state,
							locationList.get(0), sessionFactory()
									.getCurrentSession());

			for(PatientState patientState : patientStateList) {
				Date stateStartDate = patientState.getStartDate();
				Date stateEndDate = patientState.getEndDate();
				
				// if state instance is NOT in report window, skip it -- should use onOrAfter and OnOrBefore but if dates include times, no biggie
				// why would start date ever be null??? -- line this up with db query logic somehow???
				 if(stateStartDate == null || (stateEndDate !=null && stateEndDate.before(startedOnOrAfter)) || stateStartDate.after(endedOnOrBefore))
					continue;
				
				// get window BETWEEN primary state start date and interval, to be used for relative state date range
				cal.setTime(stateStartDate);
				cal.add(Calendar.MONTH, 1);
				Date offsetDate = cal.getTime(); // the offset
								
				// get patients who have obs in the time frame
				Cohort patientsWithObs = Context.getService(CohortQueryService.class).getPatientsHavingDiscreteObs(
						timeModifier, question, null,
						stateStartDate, offsetDate,
						locationList, encounterTypeList,
						sc, valueList);

				// check if patient belongs in both first state cohort and second state cohort
				if(patientsWithObs.contains(p.getPatientId())) {
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
