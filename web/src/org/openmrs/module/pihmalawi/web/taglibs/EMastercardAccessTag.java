package org.openmrs.module.pihmalawi.web.taglibs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
	
	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();
		
		Patient p = Context.getPatientService().getPatient(getPatientId());
		Form f = Context.getFormService().getForm(getFormId());
		EncounterType initialEncounterType = Context.getEncounterService().getEncounterType(getInitialEncounterTypeId());
		
		try {
			if (f == null || initialEncounterType == null) {
				o.write("(not available)");
			} else {
				List<Encounter> initials = Context.getEncounterService().getEncounters(p, null, null, null, null, Arrays.asList(initialEncounterType), null, false);
				if (initials.size()>1) {
					o.write("Multiple Initial Encounters found, " + f.getName() + " not available");
				} else if (initials.size() == 0) {
					if (isReadonly()) {
						o.write(f.getName() + " not available");
					} else {
						o.write("<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?personId=" + p.getPersonId() + "&patientId=" + p.getPatientId() + "&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=" + f.getFormId() + "\">Create new " + f.getName() + "</a>");
					}
				} else if (initials.size() == 1) {
					EncounterType followupEncounterType = Context.getEncounterService().getEncounterType(getFollowupEncounterTypeId());
					List<Encounter> followups = Context.getEncounterService().getEncounters(p, null, null, null, null, Arrays.asList(followupEncounterType), null, false);
					String created = "created: " + formatDate(initials.get(0).getEncounterDatetime());
					String visited = "visited: no";
					String rvd = "appointment: none";
					if (!followups.isEmpty()) {
						Encounter lastFollowup = followups.get(followups.size() -1);
						visited = "visited: " + formatDate(lastFollowup.getEncounterDatetime()) + " at " + lastFollowup.getLocation().getName();
						Concept appt = Context.getConceptService().getConcept(APPOINTMENT_DATE);
						List<Obs> os = Context.getObsService().getObservations(Arrays.asList((Person) p), Arrays.asList(lastFollowup), Arrays.asList(appt), null, null, null, null, 1, null, null, null, false);
						if (!os.isEmpty()) {
							rvd = "appointment: " + formatDate(os.get(0).getValueDatetime());
						}
					}
					Encounter encounter =  initials.get(0);
					Integer encounterId = encounter.getId();
					String details = "(" + created + ", " + visited + ", " + rvd + ")";
					if (isReadonly()) {
						String encounterType =encounter.getEncounterType().getName();
						String location = encounter.getLocation().getName();
						String encounterDate = formatDate(encounter.getEncounterDatetime());
						// TODO: clash in OpenMRS 1.7?
						String provider = ""; //encounter.getProvider().getGivenName() + " " + encounter.getProvider().getFamilyName();
						o.write("<a href=\"javascript:void(0)\" onClick=\"loadUrlIntoEncounterPopup('" + encounterType + "@" + location + " | " + encounterDate + " | " + provider + "', '/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + encounterId + "&inPopup=true'); return false;\">View " + f.getName() + "</a> " + details);
					} else {
						o.write("<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + encounterId + "&mode=EDIT\">Edit " + f.getName() + "</a> " + details);
					}
				}
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

}
