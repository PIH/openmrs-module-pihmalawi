package org.openmrs.module.pihmalawi.reports.extension;

import java.util.ArrayList;
import java.util.Date;
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
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { InStateAtDateObsCohortDefinition.class })
public class InStateAtDateObsEvaluator implements CohortDefinitionEvaluator {

	protected static final Log log = LogFactory
	.getLog(InStateAtDateObsEvaluator.class);

	public Cohort evaluate(CohortDefinition cohortDefinition,
		EvaluationContext context) {
	
		ProgramHelper h = new ProgramHelper();
		Cohort result = new Cohort();
		
		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context
				.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(
						0);
		InStateAtDateObsCohortDefinition definition = (InStateAtDateObsCohortDefinition) cohortDefinition;
		PatientService ps = Context.getPatientService();
		
		Date onOrAfter = definition.getOnOrAfter();
		Date onOrBefore = definition.getOnOrBefore();
		ProgramWorkflowState state = definition.getState();
		Location location = definition.getLocation();
		List<Location> locationList = new ArrayList<Location>();
		locationList.add(location);
		List<EncounterType> encounterTypeList = definition.getEncounterTypeList();
		TimeModifier timeModifier = definition.getTimeModifier();
		Concept question = definition.getQuestion();
		
		//get all patients who have an appointment in the report window, enrolled at location
		Cohort patientsWithAppointments = Context.getService(CohortQueryService.class).getPatientsHavingRangedObs(
				timeModifier, question, null,
				onOrAfter, onOrBefore,
				locationList, encounterTypeList,
				null, null,
				null, null);
			
		
		for (Integer id : patientsWithAppointments.getMemberIds()) {
			Patient p = ps.getPatient(id);
			Date appointmentDate = null;
		
			// go back through patients to get last instance
			// of appointment date
			// Can this redundancy be improved???
			List<Encounter> encounterList = Context.getEncounterService().getEncounters(p, location, onOrAfter, onOrBefore, null, encounterTypeList, null, false);
			
			for(Encounter encounter : encounterList) {
				Set<Obs> obsSet = encounter.getAllObs();
				for(Obs o : obsSet) {
					if(o.getConcept().equals(definition.getQuestion())) {
						if(appointmentDate == null || appointmentDate.before(o.getValueDatetime())) {
								appointmentDate = o.getValueDatetime();
						}
					}
				}
			}
			
			// exclude defaulters??? -- probably easier to do that with a composition at the question level 
			//since we are interested if they defaulted at the end of the period, not at the appointment date
			
			List<PatientState> patientStateList = h
			.getPatientStatesByWorkflowAtLocation(p, state,
					location);
			
			for(PatientState patientState : patientStateList) {
				if(patientState.getState().equals(state) && patientState.getActive(appointmentDate)) {
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