package org.openmrs.module.pihmalawi.web.taglibs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;

public class EMastercardAccessTag extends BodyTagSupport {

	private static final String APPOINTMENT_DATE = "Appointment date";

	public static final long serialVersionUID = 128234353L;

	private final Log log = LogFactory.getLog(getClass());

	private Integer patientId;
	private Integer formId;
	private Integer initialEncounterTypeId;
	private Integer followupEncounterTypeId;
	private boolean readonly = false;
	private String programWorkflowStates;
	private Integer patientIdentifierType;
	private boolean includeAppointmentInfo = true;

	public int doStartTag() throws JspException {
		
		JspWriter o = pageContext.getOut();
		try {
			Patient p = Context.getPatientService().getPatient(getPatientId());
			Form f = Context.getFormService().getForm(getFormId());
			EncounterType initialEncounterType = Context.getEncounterService()
					.getEncounterType(getInitialEncounterTypeId());

			if (f == null || initialEncounterType == null) {
				o.write("Not available: Wrong configuration");
				release();
				return SKIP_BODY;
			}
			List<Encounter> initials = Context.getEncounterService()
					.getEncounters(p, null, null, null, Arrays.asList(f),
							Arrays.asList(initialEncounterType), null, false);
			if (initials.size() > 1) {
				o.write("Not available: Multiple " + f.getName()
						+ " forms found");
				release();
				return SKIP_BODY;
			}
			List<ProgramWorkflowState> stateList = Helper.getProgramWorkflowStatesFromCsvIds(programWorkflowStates);
			ProgramWorkflow workflow = (stateList == null || stateList.isEmpty() ? null : stateList.get(0).getProgramWorkflow());
			if (initials.size() == 0) {
				if (!Helper.isInProgramWorkflowState(p, stateList)) {
					o.write("Not available: Inactive program state (" + f.getName() + ")");
				} else {
					if (!Helper.hasIdentifierType(p, getPatientIdentifierType())) {
						o.write("Not available: No identifier (" + f.getName() + ")");
					} else {
						if (!Helper.hasIdentifierForEnrollmentLocation(p, getPatientIdentifierType(), workflow)) {
							o.write("Not available: No identifier for current enrollment location (" + f.getName() + ")");
						} else {
							if (isReadonly()) {
								o.write("Not available (" + f.getName() + ")");
							} else {
								if (p.isDead()) {
									o.write("Not available: Patient dead (" + f.getName() + ")");
								} else {
									o.write(createNewCardHtmlTag(p, f));
								}
							}
						}
					}
				}
				release();
				return SKIP_BODY;
			}
			if (initials.size() == 1) {
				if (!Helper.isInProgramWorkflowState(p, stateList)) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0),
							"Readonly: Inactive program state"));
					release();
					return SKIP_BODY;
				}
				if (!Helper.hasIdentifierType(p, getPatientIdentifierType())) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0),
							"Readonly: No identifier"));
					release();
					return SKIP_BODY;
				}
				if (!Helper.hasIdentifierForEnrollmentLocation(p,
						getPatientIdentifierType(), workflow)) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0),
							"Readonly: No identifier for current enrollment location"));
					release();
					return SKIP_BODY;
				}
				if (isReadonly()) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0), null));
					release();
					return SKIP_BODY;
				}
				if (p.isDead()) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0), null));
					release();
					return SKIP_BODY;
				}
				o.write(createEditCardHtmlTag(p, f, initials.get(0)));
				release();
				return SKIP_BODY;
			}
		} catch (Throwable e) {
			try {
				o.write("Unknown error, call help!");
			} catch (IOException e1) {
			}
			log.error("Could not write to pageContext", e);
		}
		release();
		return SKIP_BODY;
	}

	protected String createViewCardHtmlTag(Patient p, Form f,
			Encounter initialEncounter, String additionalMessage) {
		EncounterType followupEncounterType = Context.getEncounterService()
				.getEncounterType(getFollowupEncounterTypeId());
		List<Encounter> followups = Context.getEncounterService()
				.getEncounters(p, null, null, null, null,
						Arrays.asList(followupEncounterType), null, false);
		String created = "Created: "
				+ Helper.formatDate(initialEncounter.getEncounterDatetime());
		String visited = "Visited: no";
		String rvd = "Appointment: none";
		if (!followups.isEmpty()) {
			Encounter lastFollowup = followups.get(followups.size() - 1);
			visited = "Visited: "
					+ Helper.formatDate(lastFollowup.getEncounterDatetime()) + " at "
					+ lastFollowup.getLocation().getName();
			Concept appt = Context.getConceptService().getConcept(
					APPOINTMENT_DATE);
			List<Obs> os = Context.getObsService().getObservations(
					Arrays.asList((Person) p), Arrays.asList(lastFollowup),
					Arrays.asList(appt), null, null, null, null, 1, null, null,
					null, false);
			if (!os.isEmpty()) {
				rvd = "Appointment: "
						+ Helper.formatDate(os.get(0).getValueDatetime());
			}
		}
		Integer encounterId = initialEncounter.getId();
		String details = created + ", " + visited + ", " + rvd;
			String encounterType = initialEncounter.getEncounterType()
					.getName();
			String location = initialEncounter.getLocation().getName();
			String encounterDate = Helper.formatDate(initialEncounter
					.getEncounterDatetime());
			// TODO: clash in OpenMRS 1.7?
			String provider = ""; // encounter.getProvider().getGivenName() +
									// " " +
									// encounter.getProvider().getFamilyName();
			return "<a href=\"javascript:void(0)\" onClick=\"loadUrlIntoEncounterPopup('"
					+ encounterType
					+ "@"
					+ location
					+ " | "
					+ encounterDate
					+ " | "
					+ provider
					+ "', '/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId="
					+ encounterId
					+ "&inPopup=true'); return false;\">View "
					+ f.getName()
					+ "</a><br/>"
					+ (includeAppointmentInfo ? details + "<br/>" : "")
					+ "(" + additionalMessage + ")";
	}

	protected String createEditCardHtmlTag(Patient p, Form f,
			Encounter initialEncounter) {
		EncounterType followupEncounterType = Context.getEncounterService()
				.getEncounterType(getFollowupEncounterTypeId());
		List<Encounter> followups = Context.getEncounterService()
				.getEncounters(p, null, null, null, null,
						Arrays.asList(followupEncounterType), null, false);
		String created = "Created: "
				+ Helper.formatDate(initialEncounter.getEncounterDatetime());
		String visited = "Visited: no";
		String rvd = "Appointment: none";
		if (!followups.isEmpty()) {
			Encounter lastFollowup = followups.get(followups.size() - 1);
			visited = "Visited: "
					+ Helper.formatDate(lastFollowup.getEncounterDatetime()) + " at "
					+ lastFollowup.getLocation().getName();
			Concept appt = Context.getConceptService().getConcept(
					APPOINTMENT_DATE);
			List<Obs> os = Context.getObsService().getObservations(
					Arrays.asList((Person) p), Arrays.asList(lastFollowup),
					Arrays.asList(appt), null, null, null, null, 1, null, null,
					null, false);
			if (!os.isEmpty()) {
				rvd = "Appointment: "
						+ Helper.formatDate(os.get(0).getValueDatetime());
			}
		}
		Integer encounterId = initialEncounter.getId();
		String details = created + ", " + visited + ", " + rvd;
		return "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId="
				+ encounterId
				+ "&mode=EDIT\">Edit "
				+ f.getName()
				+ "</a><br/>"
				+ (includeAppointmentInfo ? details + "<br/>" : "");
	}

	protected String createNewCardHtmlTag(Patient p, Form f) {
		return "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?personId="
				+ p.getPersonId()
				+ "&patientId="
				+ p.getPatientId()
				+ "&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId="
				+ f.getFormId() + "\">Create new " + f.getName() + "</a>";
	}

	public int doEndTag() {
		patientId = null;
		formId = null;
		initialEncounterTypeId = null;
		followupEncounterTypeId = null;
		readonly = false;
		patientIdentifierType = null;
		programWorkflowStates = null;
		
		return EVAL_PAGE;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public Integer getInitialEncounterTypeId() {
		return initialEncounterTypeId;
	}

	public void setInitialEncounterTypeId(Integer initialEncounterTypeId) {
		this.initialEncounterTypeId = initialEncounterTypeId;
	}

	public Integer getFollowupEncounterTypeId() {
		return followupEncounterTypeId;
	}

	public void setFollowupEncounterTypeId(Integer followupEncounterTypeId) {
		this.followupEncounterTypeId = followupEncounterTypeId;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getProgramWorkflowStates() {
		return programWorkflowStates;
	}

	public void setProgramWorkflowStates(String programWorkflowStates) {
		this.programWorkflowStates = programWorkflowStates;
	}

	public Integer getPatientIdentifierType() {
		return patientIdentifierType;
	}

	public void setPatientIdentifierType(Integer patientIdentifierType) {
		this.patientIdentifierType = patientIdentifierType;
	}

	public boolean isIncludeAppointmentInfo() {
		return includeAppointmentInfo;
	}

	public void setIncludeAppointmentInfo(boolean includeAppointmentInfo) {
		this.includeAppointmentInfo = includeAppointmentInfo;
	}
}
