package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class QuickProgramsFormController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10)); 
	}

	@SuppressWarnings("deprecation")
    @RequestMapping("/module/quickprograms/enrollInProgramWithStateOnDateAtLocation.form")
	public ModelAndView enroll(HttpServletRequest request,
	                           HttpServletResponse response,
	                           @RequestParam("returnPage") String returnPage,
	                           @RequestParam("patientId") Integer patientId,
	                           @RequestParam("programId") Integer programId,
	                           @RequestParam("programworkflowStateId") Integer stateId,
	                           @RequestParam("dateEnrolled") Date enrollmentDate,
	                           @RequestParam(value="locationId", required=false) Integer locationId
	                           ) throws ServletException, IOException {

		// mostly stolen from the ProgramLocation code, which usually isn't a good idea...
		if (returnPage == null) {
			throw new IllegalArgumentException("must specify a returnPage parameter in a call to enroll()");
		}

		Patient patient = Context.getPatientService().getPatient(patientId);
		
		ProgramWorkflowService pws = Context.getService(ProgramWorkflowService.class);
		Program program = pws.getProgram(programId);
		ProgramWorkflowState state = pws.getState(stateId);

		Location location = null;
		if (locationId != null) {
			location = Context.getLocationService().getLocation(locationId);
		}

		// enroll patient
		PatientProgram pp = enrollInProgram(request, patient, program, enrollmentDate, location, null);
		// set initial state
		pws.changeToState(pp, state.getProgramWorkflow(), state, enrollmentDate);
		
		return new ModelAndView(new RedirectView(returnPage));
	}

	private PatientProgram enrollInProgram(HttpServletRequest request,
			Patient patient, Program program, Date enrollmentDate,
			Location location, Date completionDate) {

		ProgramWorkflowService pws = Context.getService(ProgramWorkflowService.class);
		List<org.openmrs.PatientProgram> pps = pws.getPatientPrograms(patient,
				program, null, completionDate, enrollmentDate, null, false);

		if (!pps.isEmpty()) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Program.error.already");
			return null;
		} 
		else {
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
