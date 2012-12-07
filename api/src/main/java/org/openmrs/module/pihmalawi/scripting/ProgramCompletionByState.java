package org.openmrs.module.pihmalawi.scripting;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;

public class ProgramCompletionByState {
	
	
	private static Log log = LogFactory.getLog(ProgramCompletionByState.class);
	
	public ProgramCompletionByState() {
	}
	
	public void run() throws Exception {
		importThroughSessions();
	}
	
	private void importThroughSessions() throws Exception {
		ProgramWorkflowService pws = Context.getProgramWorkflowService();

		// manually collected pre art numbers where the paper record is missing
		// use wget script to process
		int[] patientProgramIds = { 1173 };
		
		for (int ppId : patientProgramIds) {
			PatientProgram pp = pws.getPatientProgram(ppId);
			PatientState ps = pp.getCurrentState(pws.getWorkflow(1));
			if (ps.getState().getTerminal() && pp.getDateCompleted() == null) {
				pp.setDateCompleted(ps.getStartDate());
			}
		}
	}
	
}
