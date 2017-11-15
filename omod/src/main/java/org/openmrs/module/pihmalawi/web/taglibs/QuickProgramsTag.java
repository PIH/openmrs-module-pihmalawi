/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.web.taglibs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.PatientProgram;
import org.openmrs.util.OpenmrsConstants;

/**
 * Encapsulates the functionality for providing a widget for changing program enrollment states
 */
public class QuickProgramsTag extends BodyTagSupport {

	public static final long serialVersionUID = 128233353L;

	private final Log log = LogFactory.getLog(getClass());

	private Integer patientId;
	private String stateIds;
	private String initialStateIds;
	private String terminalStateIds;
	private String defaultLocation;

	/**
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();

		try {

			List<ProgramWorkflowState> states = getStates(stateIds);
			List<ProgramWorkflowState> initialStates = getStates(initialStateIds);
			List<ProgramWorkflowState> terminalStates = getStates(terminalStateIds);
			
			ProgramWorkflow programWorkflow = initialStates.get(0).getProgramWorkflow();
			Program program = programWorkflow.getProgram();
			Patient patient = Context.getPatientService().getPatient(patientId);
			
			PatientState currentState = null;
			if (currentPatientProgram(program, patient) != null) {
				currentState = currentPatientProgram(program, patient).getCurrentState(initialStates.get(0).getProgramWorkflow());
			}

			boolean quickProgramsAvailable = false;
			if (!patient.isDead()) {
				if (!hasOpenProgramWorkflow(programWorkflow, patient)) {
					// no or closed program enrollment available
					for (ProgramWorkflowState pws : initialStates) {
						quickProgramsAvailable = true;
						o.write(enrollForm(program, patient, pws));
					}
				} 
				else {
					// open program enrollment available
					for (ProgramWorkflowState pws : states) {
						if (!(currentState != null && currentState.getState().equals(pws))) {
							quickProgramsAvailable = true;
							o.write(changeToStateSubmitTag("Change",
								currentPatientProgram(program, patient),
								pws.getProgramWorkflow(), pws,
								"'dateForPWS" + pws.getId() + "'")
								+ " to "
								+ pws.getConcept().getName()
								+ " on "
								+ dateTag("dateForPWS" + pws.getId(), "dateForPWS") 
								+ " at " + currentPatientProgram(program, patient).getLocation().getName() + "<br/>");
						}
					}
					for (ProgramWorkflowState pws : terminalStates) {
						if (!(currentState != null && currentState.getState().equals(pws))) {
							quickProgramsAvailable = true;
							o.write(changeToStateSubmitTag("Complete",
								currentPatientProgram(program, patient),
								pws.getProgramWorkflow(), pws,
								"'dateForPWS" + pws.getId() + "'")
								+ " with "
								+ pws.getConcept().getName()
								+ " on "
								+ dateTag("dateForPWS" + pws.getId(), "dateForPWS")
								+ " at " + currentPatientProgram(program, patient).getLocation().getName() + "<br/>");
						}
					}
				}
			}
			if (!quickProgramsAvailable) {
				o.write("(not available)<br/>");
			}
		} 
		catch (Throwable e) {
			try {
				o.write("Unknown error, call help!");
			} 
			catch (IOException e1) { }
			log.error("Could not write to pageContext", e);
		}
		release();
		return SKIP_BODY;
	}

	/**
	 * Private method for constructing a new program enrollment form
	 */
	private String enrollForm(Program program, Patient patient, ProgramWorkflowState pws) {
		String s = "";
		s += "<form method=\"post\" action=\"/openmrs/module/quickprograms/enrollInProgramWithStateOnDateAtLocation.form\">\n";
		s += "<input id=\"quickEnrollSubmitButton-" + pws.getId() + "\" type=\"submit\" value=\"Enroll\"/>\n";
		s += "<input type=\"hidden\" name=\"method\" value=\"enroll\"/>\n";
		s += "<input type=\"hidden\" name=\"patientId\" value=\"" + patient.getId() + "\"/>\n";
		s += "<input type=\"hidden\" name=\"returnPage\" value=\"/openmrs/patientDashboard.form?patientId=" + patient.getId() + "\"/>\n";
		s += "<input type=\"hidden\" name=\"programId\" value=\"" + program.getId() + "\"/>\n";
		s += "<input type=\"hidden\" name=\"programworkflowStateId\" value=\"" + pws.getId() + "\"/>\n";
		s += " with " + pws.getConcept().getName() + " on \n";
		s += dateTag("dateEnrolled-" + pws.getId(), "dateEnrolled") + "\n";
		s += " at <select name=\"locationId\">\n";
		s += "<option value=\"\">Choose a location...</option>\n";
		String defaultLocationId = getDefaultLocation();
		if (defaultLocationId == null || "".equals(defaultLocationId)) {
			// no default location in jsp specified, so check if the user profile has one
			defaultLocationId = Context.getUserContext().getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION);
		}
		for (Location l : Context.getLocationService().getAllLocations(false)) {
			if (defaultLocationId != null && !"".equals(defaultLocationId) && l.getId().equals(new Integer(defaultLocationId))) {
				s += "<option value=\"" + l.getId() + "\" selected>" + l.getName() + "</option>\n";
			} else {
				s += "<option value=\"" + l.getId() + "\">" + l.getName() + "</option>\n";
			}
		}
		s += "</select>\n";				
		s += "</form>";
		return s;
	}

	/**
	 * Utility method for retrieving the current Patient Program for a given Program and Patient
	 */
	private PatientProgram currentPatientProgram(Program program, Patient patient) {
		List<org.openmrs.PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(patient, program, null, null, new Date(), null, false);
		if (pps.size() > 0) {
			return (PatientProgram) pps.get(0);
		}
		return null;
	}

	/**
	 * Utility method for writing a change state field
	 */
	private String changeToStateSubmitTag(String label,
			PatientProgram patientProgram, ProgramWorkflow workflow,
			ProgramWorkflowState state, String dateField) {
		return "<input type=\"button\" value=\"" + label
				+ "\" onClick=\"changeToState(" + patientProgram.getId() + ", "
				+ workflow.getId() + ", " + state.getId() + ", " + dateField
				+ ")\" />";
	}

	/**
	 * Utility method for constructing a date field
	 */
	private String dateTag(String id, String name) {
		String today = Context.getDateFormat().format(new Date());
		return "<input type=\"text\" id=\"" + id + "\" name=\"" + name + "\" size=\"10\" onClick=\"showCalendar(this)\" value=\"" + today + "\" />";
	}

	/**
	 * Private utility method for returning whether a patient has a currently active state in the passed program workflow
	 */
	private boolean hasOpenProgramWorkflow(ProgramWorkflow programWorkflow, Patient patient) {
		// hm, it feels like there should be a *much* better way...
		for (org.openmrs.PatientProgram pp : Context.getProgramWorkflowService()
				.getPatientPrograms(patient, programWorkflow.getProgram(), null, null, new Date(), null, false)) {
			for (PatientState ps : pp.getStates()) {
				if (ps.getState().getProgramWorkflow().equals(programWorkflow)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Private utility method for returning a List of States from a comma separated string of state ids
	 */
	@SuppressWarnings("deprecation")
    private List<ProgramWorkflowState> getStates(String ids) {
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		if (StringUtils.isNotBlank(ids)) {
			StringTokenizer st = new StringTokenizer(ids, ",");
			while (st.hasMoreTokens()) {
				String id = st.nextToken().trim();
				states.add(Context.getProgramWorkflowService().getState(new Integer(id)));
			}
		}
		return states;
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	public int doEndTag() {
		patientId = null;
		terminalStateIds = null;
		stateIds = null;
		initialStateIds = null;
		defaultLocation = null;
		return EVAL_PAGE;
	}
	
    /**
     * @return the patientId
     */
    public Integer getPatientId() {
    	return patientId;
    }
	
    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(Integer patientId) {
    	this.patientId = patientId;
    }
	
    /**
     * @return the stateIds
     */
    public String getStateIds() {
    	return stateIds;
    }
	
    /**
     * @param stateIds the stateIds to set
     */
    public void setStateIds(String stateIds) {
    	this.stateIds = stateIds;
    }
	
    /**
     * @return the initialStateIds
     */
    public String getInitialStateIds() {
    	return initialStateIds;
    }
	
    /**
     * @param initialStateIds the initialStateIds to set
     */
    public void setInitialStateIds(String initialStateIds) {
    	this.initialStateIds = initialStateIds;
    }
	
    /**
     * @return the terminalStateIds
     */
    public String getTerminalStateIds() {
    	return terminalStateIds;
    }

    /**
     * @param terminalStateIds the terminalStateIds to set
     */
    public void setTerminalStateIds(String terminalStateIds) {
    	this.terminalStateIds = terminalStateIds;
    }
	
    /**
     * @return the defaultLocation
     */
    public String getDefaultLocation() {
    	return defaultLocation;
    }
	
    /**
     * @param defaultLocation the defaultLocation to set
     */
    public void setDefaultLocation(String defaultLocation) {
    	this.defaultLocation = defaultLocation;
    }
}
