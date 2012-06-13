package org.openmrs.module.pihmalawi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.spi.StateFactory;

import org.hibernate.Query;
import org.hibernate.Session;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

public class ProgramHelper {
	
	public PatientState getFirstTimeInState(Patient p, Program program, ProgramWorkflowState firstTimeInState) {
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p, program, null, null, null, null, false);
		for (PatientProgram pp : pps) {
			// hope that the first found pp is also first in time
			// should be refactored to use statesInWorkflow() as an intermediate solution
			if (!pp.isVoided()) {
				HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
				ArrayList<Long> stupidListConverter = new ArrayList<Long>();
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& ps.getState().getId().equals(firstTimeInState.getId())
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						stupidListConverter.add(ps.getStartDate().getTime());
					}
				}
				Collections.<Long> sort(stupidListConverter);

				for (Long key : stupidListConverter) {
					PatientState state = (PatientState) validPatientStates
							.get(key);
					// just take the first one
					return state;
				}
			}
		}
		return null;
	}

	public PatientState getMostRecentStateAtLocation(Patient p,
			List<ProgramWorkflowState> programWorkflowStates,
			Location enrollmentLocation, Session hibernateSession) {
		PatientState state = null;
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflowStates.get(0).getProgramWorkflow().getProgram(),
						null, null, null, null, false);
		List<Integer> programWorkflowStateIds = new ArrayList<Integer>();
		for (ProgramWorkflowState pws : programWorkflowStates) {
			programWorkflowStateIds.add(pws.getId());
		}
		for (PatientProgram pp : pps) {
			// hope that the first found pp is also first in time
			Location location = getEnrollmentLocation(pp, hibernateSession);
			if (!pp.isVoided() && location != null
					&& location.getId().equals(enrollmentLocation.getId())) {
				HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
				ArrayList<Long> stupidListConverter = new ArrayList<Long>();
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& programWorkflowStateIds.contains(ps.getState().getId())
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						stupidListConverter.add(ps.getStartDate().getTime());
					}
				}
				Collections.<Long> sort(stupidListConverter);

				for (Long key : stupidListConverter) {
					// just take the last one and hope it is the most recent one
					state = (PatientState) validPatientStates.get(key);
				}
			}
		}
		return state;
	}

	// sorry, but this a haaack
	// assumes there is only one programworkflow per program
	public PatientState getMostRecentStateAtLocation_hack(Patient p,
			Program program,
			Location enrollmentLocation, Session hibernateSession) {
		PatientState state = null;
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						program,
						null, null, null, null, false);
		for (PatientProgram pp : pps) {
			// hope that the first found pp is also first in time
			Location location = getEnrollmentLocation(pp, hibernateSession);
			if (!pp.isVoided() && location != null
					&& location.getId().equals(enrollmentLocation.getId())) {
				HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
				ArrayList<Long> stupidListConverter = new ArrayList<Long>();
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						stupidListConverter.add(ps.getStartDate().getTime());
					}
				}
				Collections.<Long> sort(stupidListConverter);

				for (Long key : stupidListConverter) {
					// just take the last one and hope it is the most recent one
					state = (PatientState) validPatientStates.get(key);
				}
			}
		}
		return state;
	}
	
	public PatientState getMostRecentStateAtLocation(Patient p,
			ProgramWorkflow programWorkflow,
			Location enrollmentLocation, Session hibernateSession) {
		List<PatientState> lastStateOfAllPatientPrograms = new ArrayList<PatientState>();
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflow.getProgram(),
						null, null, null, null, false);
		
		// get all last states of patientprograms
		for (PatientProgram pp : pps) {
			Location programLocation = getEnrollmentLocation(pp, hibernateSession);
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
	
	public PatientState getStateAfterStateAtLocation(Patient p,
			ProgramWorkflow programWorkflow, List<ProgramWorkflowState> referenceStates,
			Location enrollmentLocation, Session hibernateSession) {
		List<PatientProgram> ppsWithReferenceState = new ArrayList<PatientProgram>();
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflow.getProgram(),
						null, null, null, null, false);

		for (PatientProgram pp : pps) {
			Location programLocation = getEnrollmentLocation(pp, hibernateSession);
			if ((enrollmentLocation == null) || (programLocation != null && enrollmentLocation != null && programLocation.getId().equals(enrollmentLocation.getId()))) {
				List<PatientState> states = statesInWorkflow(pp, programWorkflow);
				for (PatientState state : states) {
					if (containedIn(state.getState(), referenceStates)) {
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
			if (containedIn(state.getState(), referenceStates)) {
				// looks like we found our referencestate, check if there is another one following
				if (i + 1 < states.size()) {
					stateAfterState = states.get(i+1);
				} else {
					stateAfterState = state;
				}
			}
		}
		return stateAfterState;
	}
	
	private boolean containedIn(ProgramWorkflowState state,
			List<ProgramWorkflowState> referenceStates) {
		for(ProgramWorkflowState pws : referenceStates) {
			if (pws.getId() == state.getId()) {
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

	public PatientState getMostRecentStateAtDate(Patient p, ProgramWorkflow programWorkflow,
			Date endDate) {
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflow.getProgram(),
						null, null, null, null, false);
		PatientState lastStateOnDate = null;
		
		try {
			for (PatientProgram pp : pps) {
//				if (pp.getActive(endDate)) {
					// assuming there is only on active patientprogram (might be wrong)
					List<PatientState> states = statesInWorkflow(pp, programWorkflow);
					for (PatientState state : states) {
						if (state.getStartDate().getTime() < endDate.getTime()) {
							// assuming the states is ordered
							lastStateOnDate = state;
						}
					}
//				}
			}
		} catch (Throwable t) {
			// shouldn't happen, but it does...
		}
		return lastStateOnDate;
	}

	public List<PatientState> getPatientStatesByWorkflowAtLocation(Patient p,
			ProgramWorkflowState programWorkflowState,
			Location enrollmentLocation, Session hibernateSession) {
		
		Integer programWorkflowStateId = programWorkflowState.getId();
		
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflowState.getProgramWorkflow().getProgram(),
						null, null, null, null, false);
		
		// list of patientstates (patient, workflow)
		List<PatientState> patientStateList = new ArrayList<PatientState>();
		// hope that the first found pp is also first in time
		for (PatientProgram pp : pps) {
			if(enrollmentLocation == null) {
				if (!pp.isVoided()) {
					for (PatientState ps : pp.getStates()) {
						if (!ps.isVoided()
								&& programWorkflowStateId.equals(ps.getState().getId())
								&& ps.getStartDate() != null) {
							
							patientStateList.add(ps);
						}
					}
				}
			} else {
				Location location = getEnrollmentLocation(pp, hibernateSession);
				if (!pp.isVoided() && location != null
						&& location.getId().equals(enrollmentLocation.getId())) {
					for (PatientState ps : pp.getStates()) {
						if (!ps.isVoided()
								&& programWorkflowStateId.equals(ps.getState().getId())
								&& ps.getStartDate() != null) {
							
							patientStateList.add(ps);
						}
					}
				}
			}
		}
		
		return patientStateList;
	}

	public Location getEnrollmentLocation(PatientProgram pp,
			Session hibernateSession) {
		String sql = "select location_id from patient_program where patient_program_id = "
				+ pp.getId();

		Query query = hibernateSession.createSQLQuery(sql.toString());
		// assume there is only one
		if (!query.list().isEmpty() && query.list().get(0) != null) {
			return Context.getLocationService().getLocation(
					((Integer) (query.list().get(0))).intValue());
		}
		return null;
	}

	public Set<PatientState> getMostRecentStates(Patient p,
			Session currentSession) {
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						null, null, null, null, null, false);
		Set<PatientState> allStates = new HashSet<PatientState>();
		for (PatientProgram pp : pps) {
			allStates.addAll(pp.getCurrentStates());
		}
		return allStates;
	}
}
