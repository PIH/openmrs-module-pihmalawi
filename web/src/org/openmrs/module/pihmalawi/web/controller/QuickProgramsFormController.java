package org.openmrs.module.pihmalawi.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.programlocation.PatientProgram;
import org.openmrs.module.programlocation.ProgramWorkflowService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class QuickProgramsFormController {

	@RequestMapping("/module/pihmalawi/enrollInProgramWithStateOnDateAtLocation.form")
	public ModelAndView enroll(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// mostly stolen from the ProgramLocation code, which usually isn't a good idea...
		String returnPage = request.getParameter("returnPage");
		if (returnPage == null) {
			throw new IllegalArgumentException(
					"must specify a returnPage parameter in a call to enroll()");
		}

		String patientIdStr = request.getParameter("patientId");
		String programIdStr = request.getParameter("programId");
		String enrollmentDateStr = request.getParameter("dateEnrolled");
		String locationIdStr = request.getParameter("locationId");
		String stateIdStr = request.getParameter("programworkflowStateId");
		ProgramWorkflowService pws = Context.getService(ProgramWorkflowService.class);

		// make sure we parse dates the same was as if we were using the
		// initBinder + property editor method
		CustomDateEditor cde = new CustomDateEditor(Context.getDateFormat(),
				true, 10);
		cde.setAsText(enrollmentDateStr);
		Date enrollmentDate = (Date) cde.getValue();
		Patient patient = Context.getPatientService().getPatient(Integer.valueOf(patientIdStr));
		Location location;
		try {
			location = Context.getLocationService().getLocation(
					Integer.valueOf(locationIdStr));
		} catch (Exception e) {
			location = null;
		}
		Program program = pws.getProgram(Integer.valueOf(programIdStr));
		ProgramWorkflowState state = Context.getProgramWorkflowService().getState(new Integer(stateIdStr));

		// enroll patient
		PatientProgram pp = enrollInProgram(request, patient, program, enrollmentDate, location, null);
		// set initial state
		pws.changeToState(pp, state.getProgramWorkflow(), state, enrollmentDate);
		
		return new ModelAndView(new RedirectView(returnPage));
	}

	private PatientProgram enrollInProgram(HttpServletRequest request,
			Patient patient, Program program, Date enrollmentDate,
			Location location, Date completionDate) {

		ProgramWorkflowService pws = Context
				.getService(ProgramWorkflowService.class);
		List<org.openmrs.PatientProgram> pps = pws.getPatientPrograms(patient,
				program, null, completionDate, enrollmentDate, null, false);

		if (!pps.isEmpty()) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					"Program.error.already");
			return null;
		} else {
			PatientProgram pp = new PatientProgram();
			pp.setPatient(patient);
			pp.setLocation(location);
			pp.setProgram(program);
			pp.setDateEnrolled(enrollmentDate);
			pp.setDateCompleted(completionDate);
			Context.getProgramWorkflowService().savePatientProgram(pp);
			return pp;
		}
	}
}
