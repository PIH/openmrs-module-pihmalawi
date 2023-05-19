package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;

import javax.servlet.http.HttpServletRequest;
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
    }
}
