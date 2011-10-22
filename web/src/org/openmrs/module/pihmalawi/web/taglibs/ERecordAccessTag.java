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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;

public class ERecordAccessTag extends BodyTagSupport {

	public static final long serialVersionUID = 128234353L;

	private final Log log = LogFactory.getLog(getClass());

	private Integer patientId;
	private Integer formId;
	private Integer encounterTypeId;
	private boolean readonly = false;
	private String programWorkflowStates;
	private Integer patientIdentifierType;

	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();

		Patient p = Context.getPatientService().getPatient(getPatientId());
		Form f = Context.getFormService().getForm(getFormId());
		EncounterType initialEncounterType = Context.getEncounterService()
				.getEncounterType(getencounterTypeId());

		try {
			if (f == null || initialEncounterType == null) {
				o.write("Not available: Wrong configuration");
				release();
				return SKIP_BODY;
			}
			if (!isInProgramWorkflowState(p, getProgramWorkflowStatesAsList())) {
				o.write("Not available: Inactive program state");
			} else {
				if (!hasIdentifierType(p, getPatientIdentifierType())) {
					o.write("Not available: No identifier");
				} else {
					// if (!hasIdentifierForEnrollmentLocation(p,
					// getPatientIdentifierType())) {
					// o.write("Not available: No identifier for enrollment location");
					// } else {
					if (isReadonly()) {
						o.write("Not available");
					} else {
						if (p.isDead()) {
							o.write("Not available: Patient dead");
						} else {
							o.write(createNewCardHtmlTag(p, f));
						}
					}
				}
			}

			List<Encounter> initials = Context.getEncounterService()
					.getEncounters(p, null, null, null, null,
							Arrays.asList(initialEncounterType), null, false);
			if (!initials.isEmpty()) {
				o.write("<br/>");
			}
			for (Encounter initial : initials) {
				if (!isInProgramWorkflowState(p,
						getProgramWorkflowStatesAsList())) {
					o.write(createViewCardHtmlTag(p, f, initial,
							"Readonly: Inactive program state"));
				} else if (!hasIdentifierType(p, getPatientIdentifierType())) {
					o.write(createViewCardHtmlTag(p, f, initial,
							"Readonly: No identifier"));
				} else
				// todo: enrollment location seems to be hacky
				// if (!hasIdentifierForEnrollmentLocation(p,
				// getPatientIdentifierType())) {
				// o.write(createViewCardHtmlTag(p, f, initial,
				// "Readonly: No identifier for enrollment location"));
				// } else
				if (isReadonly()) {
					o.write(createViewCardHtmlTag(p, f, initial, null));
				} else if (p.isDead()) {
					o.write(createViewCardHtmlTag(p, f, initial, null));
				} else
					o.write(createEditCardHtmlTag(p, f, initial));
				o.write("<br/>");
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
		Location enrollmentLocation = currentEnrollmentLocation(p,
				getProgramWorkflowStatesAsList());
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
		PatientState ps = new Helper().getMostRecentStateAtDate(p,
				programWorkflowStates.get(0).getProgramWorkflow(), new Date());
		if (ps != null) {
			// PatientProgram pp = (PatientProgram) ps.getPatientProgram();
			// if (pp != null) {
			// return pp.getLocation();
			// }
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
		String created = "Created: "
				+ formatDate(initialEncounter.getEncounterDatetime());
		Integer encounterId = initialEncounter.getId();
		String details = created;
		String encounterType = initialEncounter.getEncounterType().getName();
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
				+ "</a> ("
				+ details
				+ ") ("
				+ additionalMessage + ")";
	}

	private String createEditCardHtmlTag(Patient p, Form f,
			Encounter initialEncounter) {
		String created = "Created: "
				+ formatDate(initialEncounter.getEncounterDatetime());
		Integer encounterId = initialEncounter.getId();
		String details = created;
		return "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId="
				+ encounterId
				+ "&mode=EDIT\">Edit "
				+ f.getName()
				+ "</a> (" + details + ")";
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
		encounterTypeId = null;
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

	public Integer getencounterTypeId() {
		return encounterTypeId;
	}

	public void setencounterTypeId(Integer encounterTypeId) {
		this.encounterTypeId = encounterTypeId;
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
