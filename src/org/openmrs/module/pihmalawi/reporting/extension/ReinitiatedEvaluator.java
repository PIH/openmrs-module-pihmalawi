package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { ReinitiatedCohortDefinition.class })
public class ReinitiatedEvaluator implements CohortDefinitionEvaluator  {
	
	public ReinitiatedEvaluator() { }
	
	public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		Program PROGRAM = Context.getProgramWorkflowService().getProgramByName("HIV program");
		ProgramWorkflowState STATE_DIED = PROGRAM.getWorkflowByName("Treatment status").getStateByName("Patient died");
		ProgramWorkflowState STATE_STOPPED = PROGRAM.getWorkflowByName("Treatment status").getStateByName("Treatment stopped");
		ProgramWorkflowState STATE_TRANSFERRED_OUT = PROGRAM.getWorkflowByName("Treatment status").getStateByName("Patient transferred out");
		
		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
			    HibernatePihMalawiQueryDao.class).get(0);
		CohortQueryService cohortService = Context.getService(CohortQueryService.class);
		
		ReinitiatedCohortDefinition cd = (ReinitiatedCohortDefinition) cohortDefinition;
		
		Date currentStartDate = cd.getOnOrAfter();
		Date endDate = DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.YEAR, -5);
		Date reportStartDate = cal.getTime();
		
		// must have appointment in quarter to 'reinitiate' in report
		Cohort hasAppointmentInReportWindow = Context.getService(CohortQueryService.class).getPatientsHavingEncounters(
	    		currentStartDate, endDate,
	    		cd.getLocationList(), cd.getEncounterTypeList(), null,
	    		null, null);
		
		// increment 3 months in the past and begin checking for defaulters and recording old appointments
		cal.setTime(currentStartDate);
		cal.add(Calendar.MONTH, -3);
		currentStartDate = cal.getTime();
		
		cal.setTime(endDate);
		cal.add(Calendar.MONTH, -3);
		endDate = cal.getTime();
		
		Cohort hasAppointmentPast = new Cohort();
		
		Cohort ret = new Cohort();
		while(endDate.after(reportStartDate)) {
						
			cal.setTime(endDate);
			cal.add(Calendar.HOUR, -8 * 24 * 7); // 8 weeks
			Date value1 = cal.getTime();
			
			// defaulters in a given quarter in the past
			Cohort defaulters = cohortService.getPatientsHavingRangedObs(
					TimeModifier.MAX, Context.getConceptService()
					.getConceptByName("Appointment date"), null,
					null, endDate,
					cd.getLocationList(), cd.getEncounterTypeList(),
					RangeComparator.LESS_THAN, value1,
					RangeComparator.GREATER_EQUAL, null);
			
			// people to exclude from defaulters list
			Cohort stopped = q.getPatientsInStateAtLocation(STATE_STOPPED, currentStartDate, endDate, cd.getLocationList().get(0));
			Cohort died = q.getPatientsInStateAtLocation(STATE_DIED, currentStartDate, endDate, cd.getLocationList().get(0));
			Cohort transferredOut = q.getPatientsInStateAtLocation(STATE_TRANSFERRED_OUT, currentStartDate, endDate, cd.getLocationList().get(0));
			
			defaulters = Cohort.subtract(defaulters, stopped);
			defaulters = Cohort.subtract(defaulters, died);
			defaulters = Cohort.subtract(defaulters, transferredOut);
						
			// if defaulted this quarter and has appointment in report window 
			// but not before report window and after defaulting 
			// (as this would count as reinitiating in the quarter with the FIRST appointment after default)
			for(Integer patientId : defaulters.getMemberIds()) {
				if(hasAppointmentInReportWindow.contains(patientId) && !hasAppointmentPast.contains(patientId)) {
					ret.addMember(patientId);
				}
			}
			
			// this comes after adding patients to cohort so that 
			// people with appointments before defaulting but within this quarter
			// are not excluded from return
			hasAppointmentPast = Cohort.union(hasAppointmentPast, Context.getService(CohortQueryService.class).getPatientsHavingEncounters(
		    		currentStartDate, endDate,
		    		cd.getLocationList(), cd.getEncounterTypeList(), null,
		    		null, null));
						
			cal.setTime(currentStartDate);
			cal.add(Calendar.MONTH, -3);
			currentStartDate = cal.getTime();
			
			cal.setTime(endDate);
			cal.add(Calendar.MONTH, -3);
			endDate = cal.getTime();
		}
		 
		 return ret;
	}

}
