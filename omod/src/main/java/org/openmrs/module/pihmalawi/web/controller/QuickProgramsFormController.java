package org.openmrs.module.pihmalawi.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientProgramAttribute;
import org.openmrs.Program;
import org.openmrs.ProgramAttributeType;
import org.openmrs.ProgramWorkflow;
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
import java.util.Set;

import static org.openmrs.module.pihmalawi.PihMalawiConstants.TRANSFERRED_OUT_PROGRAM_ATTRIBUTE_TYPE;

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
	                           @RequestParam(value="locationId", required=true) Integer locationId
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
		} else {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "pihmalawi.program.loocation.required");
			return new ModelAndView(new RedirectView(returnPage));
		}

		PatientProgram pp = isPatientEnrolledInProgram(patient, program, enrollmentDate);
		if ( pp == null ) {
			// enroll patient
			pp = enrollInProgram(request, patient, program, enrollmentDate, location, null);
		}
		if (pp != null ) {
			// set initial state
			pp.transitionToState(state, enrollmentDate);
			pws.savePatientProgram(pp);
		}
		return new ModelAndView(new RedirectView(returnPage));
	}

	@RequestMapping("/module/quickprograms/patientDied.form")
	public ModelAndView patientDied(
									@RequestParam("returnPage") String returnPage,
									@RequestParam("patientProgramId") Integer patientProgramId,
									@RequestParam("programworkflowStateId") Integer stateId,
									@RequestParam("dateStateChanged") Date dateStateChanged
	) throws ServletException, IOException {

		if (returnPage == null) {
			throw new IllegalArgumentException("must specify a returnPage parameter in a call to transfer()");
		}

		ProgramWorkflowService pws = Context.getService(ProgramWorkflowService.class);
		ProgramWorkflowState pwState = null;
		if ( stateId != null ) {
			pwState = pws.getState(stateId);
		}

		if (patientProgramId != null) {
			PatientProgram patientProgram = pws.getPatientProgram(patientProgramId);
			if (patientProgram != null) {
				if (pwState != null) {
					patientProgram.transitionToState(pwState, dateStateChanged);
					pws.savePatientProgram(patientProgram);

					ProgramWorkflow programWorkflow = pwState.getProgramWorkflow();
					//check if this program has other workflows
					Set<ProgramWorkflow> workflows = patientProgram.getProgram().getWorkflows();
					if (workflows.size() > 1) {
						for (ProgramWorkflow workflow : workflows) {
							if (!workflow.getProgramWorkflowId().equals(programWorkflow.getProgramWorkflowId())) {
								//we found another program workflow different than the one was passed on
								Set<ProgramWorkflowState> states = workflow.getStates();
								if (states != null && states.size() > 0) {
									//look for Patient died state
									for (ProgramWorkflowState state : states) {
										if (state.getConcept().getUuid().equalsIgnoreCase(pwState.getConcept().getUuid())) {
											//we found the corresponding state in a different program workflow
											patientProgram.transitionToState(state, dateStateChanged);
											pws.savePatientProgram(patientProgram);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return new ModelAndView(new RedirectView(returnPage));
	}

	@RequestMapping("/module/quickprograms/transferredOutToLocation.form")
	public ModelAndView transferOut(HttpServletRequest request,
							   HttpServletResponse response,
							   @RequestParam("returnPage") String returnPage,
							   @RequestParam("patientId") Integer patientId,
							   @RequestParam("programId") Integer programId,
							   @RequestParam("patientProgramId") Integer patientProgramId,
							   @RequestParam("programworkflowStateId") Integer stateId,
							   @RequestParam("dateTransferredOut") Date transferredOutDate,
							   @RequestParam(value="transferredOutLocation", required=true) String transferredOutLocation
	) throws ServletException, IOException {

		if (returnPage == null) {
			throw new IllegalArgumentException("must specify a returnPage parameter in a call to transfer()");
		}

        ProgramWorkflowService pws = Context.getService(ProgramWorkflowService.class);
        ProgramWorkflowState pwState = null;
        if ( stateId != null ) {
            pwState = pws.getState(stateId);
        }
        ProgramAttributeType transferredOutAttrType = pws.getProgramAttributeTypeByUuid(TRANSFERRED_OUT_PROGRAM_ATTRIBUTE_TYPE);

        if (StringUtils.isNotBlank(transferredOutLocation)) {
            PatientProgram patientProgram = null;
            if (patientProgramId != null) {
                patientProgram = pws.getPatientProgram(patientProgramId);
                if (patientProgram != null) {
                    List<PatientProgramAttribute> activeAttributes = patientProgram.getActiveAttributes(transferredOutAttrType);
                    if (activeAttributes != null && activeAttributes.size() > 0) {
                        //this patient program already has an attribute of this type,
                        // just update its value
                        //activeAttributes.get(0).setValue(transferredOutLocation);
						activeAttributes.get(0).setValueReferenceInternal(transferredOutLocation);
                    } else {
                        //add new program attribute
                        PatientProgramAttribute prgAttrib = new PatientProgramAttribute();
                        prgAttrib.setAttributeType(transferredOutAttrType);
                        prgAttrib.setValueReferenceInternal(transferredOutLocation);
						patientProgram.setAttribute(prgAttrib);
                    }
                    if ( pwState != null ) {
                        patientProgram.transitionToState(pwState, transferredOutDate);
                    }
                    pws.savePatientProgram(patientProgram);
                }
            }
        }

		return new ModelAndView(new RedirectView(returnPage));
	}

	private PatientProgram isPatientEnrolledInProgram(Patient patient, Program program, Date enrollmentDate) {
		PatientProgram patientProgram = null;
		ProgramWorkflowService pws = Context.getService(ProgramWorkflowService.class);
		List<PatientProgram> patientPrograms = pws.getPatientPrograms(patient, program, null, null, enrollmentDate, null, false);
		if (patientPrograms != null && !patientPrograms.isEmpty()) {
			return patientPrograms.get(0);
		}
		return patientProgram;
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
