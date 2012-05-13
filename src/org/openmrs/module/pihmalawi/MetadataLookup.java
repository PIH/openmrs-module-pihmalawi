package org.openmrs.module.pihmalawi;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;

public class MetadataLookup {

	public static Concept concept(String string) {
		return Context.getConceptService().getConcept(string);
	}

	public static EncounterType encounterType(String string) {
		return Context.getEncounterService().getEncounterType(string);
	}

	public static ProgramWorkflowState workflowState(String program,
			String workflow, String state) {
		ProgramWorkflowState s = Context.getProgramWorkflowService()
				.getProgramByName(program).getWorkflowByName(workflow)
				.getStateByName(state);
		if (s == null) {
			throw new RuntimeException("Couldn't find ProgramWorkflowState "
					+ state);
		}
		return s;
	}

	public static Program program(String program) {
		Program s = Context.getProgramWorkflowService().getProgramByName(
				program);
		;
		if (s == null) {
			throw new RuntimeException("Couldn't find Program " + s);
		}
		return s;
	}

	public static ProgramWorkflow programWorkflow(String program,
			String workflow) {
		return program(program).getWorkflowByName(workflow);
	}

	public static Location location(String location) {
		Location s = Context.getLocationService().getLocation(location);
		if (s == null) {
			throw new RuntimeException("Couldn't find Location " + location);
		}
		return s;
	}
}
