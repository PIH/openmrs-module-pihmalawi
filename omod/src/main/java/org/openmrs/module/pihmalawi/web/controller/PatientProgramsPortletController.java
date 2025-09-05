package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatientProgramsPortletController extends PihMalawiPortletController {

    @Override
    protected String getPortletUrl() {
        return "/module/pihmalawi/portlets/patientPrograms";
    }

    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
        if (!model.containsKey("programs")) {
            List<Program> programs = Context.getProgramWorkflowService().getAllPrograms();
            model.put("programs", programs);
        }
        if (!model.containsKey("locations")) {
            List<Location> locations = Context.getLocationService().getAllLocations();
            model.put("locations", locations);
        }
        Map<PatientProgram, Map<ProgramWorkflow, List<PatientState>>> patientStatesByWorkflow = new HashMap<>();
        Map<PatientProgram, Map<ProgramWorkflow, PatientState>> latestStateByWorkflow = new HashMap<>();
        List<PatientProgram> patientPrograms = (List<PatientProgram>) model.get("patientPrograms");
        for (PatientProgram patientProgram : patientPrograms) {
            patientStatesByWorkflow.put(patientProgram, new LinkedHashMap<>());
            latestStateByWorkflow.put(patientProgram, new HashMap<>());
            for (ProgramWorkflow workflow : patientProgram.getProgram().getWorkflows()) {
                List<PatientState> states = patientProgram.statesInWorkflow(workflow, false);
                patientStatesByWorkflow.get(patientProgram).put(workflow, states);
                latestStateByWorkflow.get(patientProgram).put(workflow, states.isEmpty() ? null : states.get(states.size() - 1));
            }
        }
        model.put("patientStatesByWorkflow", patientStatesByWorkflow);
        model.put("latestStateByWorkflow", latestStateByWorkflow);
    }
}
