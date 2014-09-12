package org.openmrs.module.pihmalawi.common;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ProgramHelper {
	
	public PatientState getFirstTimeInState(Patient p, Program program, ProgramWorkflowState firstTimeInState, Date endDate) {
		return getFirstTimeInStateAtLocation(p, program, firstTimeInState, endDate, null);
	}

	public PatientState getFirstTimeInStateAtLocation(Patient p, Program program, ProgramWorkflowState firstTimeInState, Date endDate, Location location) {
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, program, null, endDate, null, null, false);
		Map<Long, PatientState> validPatientStates = new TreeMap<Long, PatientState>();
		for (PatientProgram pp : pps) {
			List<PatientState> states = statesInWorkflow(pp, firstTimeInState.getProgramWorkflow());
			if (states != null) {
				Location enrollmentLocation = getEnrollmentLocation(pp);
				for (PatientState ps : states) {
					if (ps.getStartDate().compareTo(endDate) <= 0 && ps.getState().getId().equals(firstTimeInState.getId())) {
						if (location == null || location.equals(enrollmentLocation)) {
							validPatientStates.put(ps.getStartDate().getTime(), ps);
						}
					}
				}
			}
		}
		if (!validPatientStates.isEmpty()) {
			return validPatientStates.values().iterator().next();
		}
		return null;
	}

	public PatientProgram getMostRecentProgramEnrollment(Patient p, Program program, Date asOfDate) {
		PatientProgram ret = null;
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, program, null, asOfDate, null, null, false);
		for (PatientProgram pp : pps) {
			if (!pp.isVoided()) {
				if (ret == null || pp.getDateEnrolled().after(ret.getDateEnrolled())) {
					ret = pp;
				}
			}
		}
		return ret;
	}

	public PatientProgram getMostRecentProgramEnrollmentAtLocation(Patient p, Program program, Location enrollmentLocation) {
		PatientProgram ret = null;
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, program, null, null, null, null, false);
		for (PatientProgram pp : pps) {
			Location location = getEnrollmentLocation(pp);
			if (!pp.isVoided() && location != null && location.getId().equals(enrollmentLocation.getId())) {
				if (ret == null || pp.getDateEnrolled().after(ret.getDateEnrolled())) {
					ret = pp;
				}
			}
		}
		return ret;
	}

	public PatientState getMostRecentStateAtLocation(Patient p, ProgramWorkflow programWorkflow, Location enrollmentLocation) {
		List<PatientState> lastStateOfAllPatientPrograms = new ArrayList<PatientState>();
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, programWorkflow.getProgram(), null, null, null, null, false);
		
		// get all last states of patientprograms
		for (PatientProgram pp : pps) {
			Location programLocation = getEnrollmentLocation(pp);
			if (programLocation != null && enrollmentLocation != null && programLocation.getId().equals(enrollmentLocation.getId())) {
				List<PatientState> states = statesInWorkflow(pp, programWorkflow);
				if (states != null && !states.isEmpty()) {
					lastStateOfAllPatientPrograms.add(states.get(states.size() - 1));
//					log.debug("lastStatesOfAllPatientPrograms " + p.getPatientId() + " " + states.get(states.size() - 1).getState().getConcept().getName());
				}
			}
		}
		// figure out which patientprogram is last
		PatientState lastState = null;
		for (PatientState state : lastStateOfAllPatientPrograms) {
			if (state.getPatientProgram().getDateCompleted() == null) {
				// assume only one uncompleted program is possible (although not the case)
				return state; 
			} else {
				// otherwise assume the order of patientprograms is sequentially ordered (although not the case)
				lastState = state;
			}
		}
		return lastState;
	}
	
	public PatientState getMostRecentStateAtLocationAndDate(Patient p, ProgramWorkflow programWorkflow, Location enrollmentLocation, Date endDate) {
		List<PatientState> lastStateOfAllPatientPrograms = new ArrayList<PatientState>();
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, programWorkflow.getProgram(), null, null, null, null, false);
		
		// get all last states of patientprograms as of enddate
		for (PatientProgram pp : pps) {
			Location programLocation = getEnrollmentLocation(pp);
			if (programLocation != null && enrollmentLocation != null && programLocation.getId().equals(enrollmentLocation.getId())) {
				List<PatientState> states = statesInWorkflow(pp, programWorkflow);
				if (states != null && !states.isEmpty()) {
					for (int i = states.size(); i > 0; i--) {
						if (states.get(i - 1).getStartDate().getTime() <= endDate.getTime()) {
							lastStateOfAllPatientPrograms.add(states.get(i - 1));
							break;
						}
					}
				}
			}
		}
		// figure out which patientprogram is last
		PatientState lastState = null;
		for (PatientState state : lastStateOfAllPatientPrograms) {
			if (state.getPatientProgram().getDateCompleted() == null) {
				// assume only one uncompleted program is possible (although not the case)
				return state; 
			} else {
				// otherwise assume the order of patientprograms is sequentially ordered (although not the case)
				lastState = state;
			}
		}
		return lastState;
	}
	
	public PatientState getStateAfterStateAtLocation(Patient p, ProgramWorkflow programWorkflow, List<ProgramWorkflowState> referenceStates, Location enrollmentLocation, Date endDate) {
		List<PatientProgram> ppsWithReferenceState = new ArrayList<PatientProgram>();
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, programWorkflow.getProgram(), null, null, null, null, false);

		for (PatientProgram pp : pps) {
			Location programLocation = getEnrollmentLocation(pp);
			if ((enrollmentLocation == null) || (programLocation != null && programLocation.getId().equals(enrollmentLocation.getId()))) {
				List<PatientState> states = statesInWorkflow(pp, programWorkflow);
				for (PatientState state : states) {
					if (containedIn(state.getState(), referenceStates) && state.getStartDate().getTime() <= endDate.getTime()) {
						// we found a patient program with our reference state, keep it
						ppsWithReferenceState.add(state.getPatientProgram());
						break;
					}
				}
			}
		}
		
		// now figure out which state is closest to what we want and get his potential successor state
		PatientProgram lastRelevantPatientProgram = null;
		for (PatientProgram pp : ppsWithReferenceState) {
			if (pp.getDateCompleted() == null) {
				// assume only one uncompleted program is possible (although not the case)
				lastRelevantPatientProgram = pp; 
				break;
			} else {
				// otherwise assume the order of patientprograms is sequentially ordered (although not the case)
				lastRelevantPatientProgram = pp;
			}
		}
		
		// get the next state from the referencestate (might be still the same)
		List<PatientState> states = statesInWorkflow(lastRelevantPatientProgram, programWorkflow);
		PatientState stateAfterState = null;
		for (int i = 0; i < states.size(); i++) {
			PatientState state = states.get(i);
			if (state.getStartDate().getTime() <= endDate.getTime() && containedIn(state.getState(), referenceStates)) {
				// looks like we found our referencestate, check if there is another one following
				if (i + 1 < states.size() && states.get(i+1).getStartDate().getTime() <= endDate.getTime()) {
					stateAfterState = states.get(i+1);
				} else {
					stateAfterState = state;
				}
			}
		}
		return stateAfterState;
	}
	
	private boolean containedIn(ProgramWorkflowState state, List<ProgramWorkflowState> referenceStates) {
		for(ProgramWorkflowState pws : referenceStates) {
			if (pws.equals(state)) {
				return true;
			}
		}
		return false;
	}
	
	// quick hack copied from bugfix for PatientProgram from ProgramLocation module
	// once OpenMRS can handle same-day-transitions this could be removed
	private List<PatientState> statesInWorkflow(PatientProgram patientProgram, ProgramWorkflow programWorkflow) {
		List<PatientState> ret = new ArrayList<PatientState>();
		for (PatientState st : patientProgram.getStates()) {
			if (st.getState().getProgramWorkflow().equals(programWorkflow) && !st.getVoided()) {
				ret.add(st);
			}
		}
		Collections.sort(ret, new Comparator<PatientState>() {
			
			public int compare(PatientState left, PatientState right) {
				// check if one of the states is active 
				if (left.getActive()) {
					return 1;
				}
				if (right.getActive()) {
					return -1;
				}
				return OpenmrsUtil.compareWithNullAsEarliest(left.getStartDate(), right.getStartDate());
			}
		});
		return ret;
	}

	public PatientState getMostRecentStateAtDate(Patient p, ProgramWorkflow programWorkflow, Date endDate) {
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, programWorkflow.getProgram(), null, null, null, null, false);
		PatientState lastStateOnDate = null;
		try {
			for (PatientProgram pp : pps) {
				List<PatientState> states = statesInWorkflow(pp, programWorkflow);
				for (PatientState state : states) {
					if (state.getStartDate().getTime() <= endDate.getTime()) {
						// assuming the states is ordered
						lastStateOnDate = state;
					}
				}
			}
		} catch (Throwable t) {
			// shouldn't happen, but it does...
		}
		return lastStateOnDate;
	}

	public PatientState getMostRecentState(Patient p, ProgramWorkflow programWorkflow) {
		return getMostRecentStateAtDate(p, programWorkflow, new Date());
	}

	public List<PatientState> getPatientStatesByWorkflowAtLocation(Patient p, ProgramWorkflowState programWorkflowState, Location enrollmentLocation) {
		
		Integer programWorkflowStateId = programWorkflowState.getId();
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, programWorkflowState.getProgramWorkflow().getProgram(), null, null, null, null, false);
		
		// list of patientstates (patient, workflow)
		List<PatientState> patientStateList = new ArrayList<PatientState>();
		// hope that the first found pp is also first in time
		for (PatientProgram pp : pps) {
			if(enrollmentLocation == null) {
				if (!pp.isVoided()) {
					for (PatientState ps : pp.getStates()) {
						if (!ps.isVoided() && programWorkflowStateId.equals(ps.getState().getId()) && ps.getStartDate() != null) {
							patientStateList.add(ps);
						}
					}
				}
			}
			else {
				Location location = getEnrollmentLocation(pp);
				if (!pp.isVoided() && location != null
						&& location.getId().equals(enrollmentLocation.getId())) {
					for (PatientState ps : pp.getStates()) {
						if (!ps.isVoided() && programWorkflowStateId.equals(ps.getState().getId()) && ps.getStartDate() != null) {
							patientStateList.add(ps);
						}
					}
				}
			}
		}
		
		return patientStateList;
	}

	public Location getEnrollmentLocation(PatientProgram pp) {
		try {
			Context.addProxyPrivilege("SQL Level Access");
			String sql = "select location_id from patient_program where patient_program_id = " + pp.getId();
			List<List<Object>> ret = Context.getAdministrationService().executeSQL(sql, true);
			// assume there is only one
			if (!ret.isEmpty()) {
				List<Object> o = ret.get(0);
				if (o != null && !o.isEmpty()) {
					Object id = o.get(0);
					if (id != null) {
						return Context.getLocationService().getLocation((Integer)id);
					}
				}
			}
			return null;
		}
		finally {
			Context.removeProxyPrivilege("SQL Level Access");
		}
	}

	public Set<PatientState> getMostRecentStates(Patient p) {
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, null, null, null, null, null, false);
		Set<PatientState> allStates = new HashSet<PatientState>();
		for (PatientProgram pp : pps) {
			allStates.addAll(pp.getCurrentStates());
		}
		return allStates;
	}

	public Set<PatientState> getActiveStatesOnDate(Patient p, Date d) {
		List<PatientProgram> pps = Context.getProgramWorkflowService().getPatientPrograms(p, null, null, null, null, null, false);
		Set<PatientState> allStates = new HashSet<PatientState>();
		for (PatientProgram pp : pps) {
			for (PatientState ps : pp.getStates()) {
				if (ps.getActive(d)) {
					allStates.add(ps);
				}
			}
		}
		return allStates;
	}
}
