package org.openmrs.module.pihmalawi.web.taglibs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;

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

	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();

		Patient p = Context.getPatientService().getPatient(getPatientId());
		Form f = Context.getFormService().getForm(getFormId());
		EncounterType initialEncounterType = Context.getEncounterService()
				.getEncounterType(getInitialEncounterTypeId());

		try {
			if (f == null || initialEncounterType == null) {
				o.write("Not available: Wrong configuration");
				release();
				return SKIP_BODY;
			}
			List<Encounter> initials = Context.getEncounterService()
					.getEncounters(p, null, null, null, null,
							Arrays.asList(initialEncounterType), null, false);
			if (initials.size() > 1) {
				o.write("Not available: Multiple " + f.getName()
						+ " forms found");
				release();
				return SKIP_BODY;
			}
			if (initials.size() == 0) {
				if (!isInProgramWorkflowState(p,
						getProgramWorkflowStatesAsList())) {
					o.write("Not available: Wrong program state");
				} else {
					if (!hasIdentifierType(p, getPatientIdentifierType())) {
						o.write("Not available: No identifier");
					} else {
//						if (!hasIdentifierForEnrollmentLocation(p,
//								getPatientIdentifierType())) {
//							o.write("Not available: No identifier for enrollment location");
//						} else {
							if (isReadonly()) {
								o.write("Not available");
							} else {
								if (p.isDead()) {
									o.write("Not available: Patient dead");
								} else {
									o.write(createNewCardHtmlTag(p, f));
								}
							}
//						}
					}
				}
				release();
				return SKIP_BODY;
			}
			if (initials.size() == 1) {
				if (!isInProgramWorkflowState(p,
						getProgramWorkflowStatesAsList())) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0),
							"Readonly: Wrong state"));
					release();
					return SKIP_BODY;
				}
				if (!hasIdentifierType(p, getPatientIdentifierType())) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0),
							"Readonly: No identifier"));
					release();
					return SKIP_BODY;
				}
				// todo: enrollment location seems to be hacky
//				if (!hasIdentifierForEnrollmentLocation(p,
//						getPatientIdentifierType())) {
//					o.write(createViewCardHtmlTag(p, f, initials.get(0),
//							"Readonly: No identifier for enrollment location"));
//					release();
//					return SKIP_BODY;
//				}
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

	private boolean hasIdentifierForEnrollmentLocation(Patient p,
			Integer identifierType) {
		if (patientIdentifierType == null) {
			// no identifiertype specified, simple accept
			return true;
		}
		List<PatientIdentifier> pis = p.getPatientIdentifiers(Context
				.getPatientService().getPatientIdentifierType(identifierType));
		Location enrollmentLocation = currentEnrollmentLocation(p, getProgramWorkflowStatesAsList());
		for (PatientIdentifier pi : pis) {
			if (pi.getLocation() != null && pi.getLocation() != null
					&& pi.getLocation().getId() == enrollmentLocation.getId()) {
				return true;
			}
		}
		return false;
	}

	private Location currentEnrollmentLocation(Patient p,
			List<ProgramWorkflowState> programWorkflowStates) {
		PatientState ps = new Helper().getMostRecentStateAtDate(p, programWorkflowStates.get(0).getProgramWorkflow(), new Date());
		if (ps != null) {
//			PatientProgram pp = (PatientProgram) ps.getPatientProgram();
//			if (pp != null) {
//				return pp.getLocation();
//			}
		}
		return null;
	}

	private boolean hasIdentifierType(Patient p, Integer patientIdentifierType) {
		if (patientIdentifierType == null) {
			// no identifiertype specified, simple accept
			return true;
		}
		PatientIdentifierType pit = Context.getPatientService()
				.getPatientIdentifierType(patientIdentifierType);
		return !p.getPatientIdentifiers(pit).isEmpty();
	}

	private boolean isInProgramWorkflowState(Patient p,
			List<ProgramWorkflowState> programWorkflowStates) {
		if (programWorkflowStates == null || programWorkflowStates.isEmpty()) {
			// no states specified, simply accept
			return true;
		}
		PatientState ps = new Helper().getMostRecentStateAtDate(p,
				programWorkflowStates.get(0).getProgramWorkflow(), new Date());
		if (ps != null && programWorkflowStates.contains(ps.getState())) {
			return true;
		}
		return false;
	}

	private String createViewCardHtmlTag(Patient p, Form f,
			Encounter initialEncounter, String additionalMessage) {
		EncounterType followupEncounterType = Context.getEncounterService()
				.getEncounterType(getFollowupEncounterTypeId());
		List<Encounter> followups = Context.getEncounterService()
				.getEncounters(p, null, null, null, null,
						Arrays.asList(followupEncounterType), null, false);
		String created = "Created: "
				+ formatDate(initialEncounter.getEncounterDatetime());
		String visited = "Visited: no";
		String rvd = "Appointment: none";
		if (!followups.isEmpty()) {
			Encounter lastFollowup = followups.get(followups.size() - 1);
			visited = "Visited: "
					+ formatDate(lastFollowup.getEncounterDatetime()) + " at "
					+ lastFollowup.getLocation().getName();
			Concept appt = Context.getConceptService().getConcept(
					APPOINTMENT_DATE);
			List<Obs> os = Context.getObsService().getObservations(
					Arrays.asList((Person) p), Arrays.asList(lastFollowup),
					Arrays.asList(appt), null, null, null, null, 1, null, null,
					null, false);
			if (!os.isEmpty()) {
				rvd = "Appointment: "
						+ formatDate(os.get(0).getValueDatetime());
			}
		}
		Integer encounterId = initialEncounter.getId();
		String details = created + ", " + visited + ", " + rvd;
			String encounterType = initialEncounter.getEncounterType()
					.getName();
			String location = initialEncounter.getLocation().getName();
			String encounterDate = formatDate(initialEncounter
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
					+ details
					+ "<br/>("
					+ additionalMessage + ")";
	}

	private String createEditCardHtmlTag(Patient p, Form f,
			Encounter initialEncounter) {
		EncounterType followupEncounterType = Context.getEncounterService()
				.getEncounterType(getFollowupEncounterTypeId());
		List<Encounter> followups = Context.getEncounterService()
				.getEncounters(p, null, null, null, null,
						Arrays.asList(followupEncounterType), null, false);
		String created = "Created: "
				+ formatDate(initialEncounter.getEncounterDatetime());
		String visited = "Visited: no";
		String rvd = "Appointment: none";
		if (!followups.isEmpty()) {
			Encounter lastFollowup = followups.get(followups.size() - 1);
			visited = "Visited: "
					+ formatDate(lastFollowup.getEncounterDatetime()) + " at "
					+ lastFollowup.getLocation().getName();
			Concept appt = Context.getConceptService().getConcept(
					APPOINTMENT_DATE);
			List<Obs> os = Context.getObsService().getObservations(
					Arrays.asList((Person) p), Arrays.asList(lastFollowup),
					Arrays.asList(appt), null, null, null, null, 1, null, null,
					null, false);
			if (!os.isEmpty()) {
				rvd = "Appointment: "
						+ formatDate(os.get(0).getValueDatetime());
			}
		}
		Integer encounterId = initialEncounter.getId();
		String details = created + ", " + visited + ", " + rvd;
		return "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId="
				+ encounterId
				+ "&mode=EDIT\">Edit "
				+ f.getName()
				+ "</a><br/>"
				+ details;
	}

	private String createNewCardHtmlTag(Patient p, Form f) {
		return "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?personId="
				+ p.getPersonId()
				+ "&patientId="
				+ p.getPatientId()
				+ "&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId="
				+ f.getFormId() + "\">Create new " + f.getName() + "</a>";
	}

	private String formatDate(Date date) {
		if (date == null) {
			return "??-???-????";
		}
		return new SimpleDateFormat("dd-MMM-yyyy").format(date);
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

	public List<ProgramWorkflowState> getProgramWorkflowStatesAsList() {
		if (programWorkflowStates == null || programWorkflowStates.isEmpty()) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(programWorkflowStates, ",");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		while (st.hasMoreTokens()) {
			String id = st.nextToken().trim();
			ProgramWorkflowState state = Context.getProgramWorkflowService()
					.getState(new Integer(id));
			states.add(state);
		}
		return states;
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
}
