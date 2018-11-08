package org.openmrs.module.pihmalawi.web.taglibs;

import org.apache.commons.lang.StringUtils;
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
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.pihmalawi.metadata.EncounterTypes;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMastercardAccessTag extends BodyTagSupport {

	private static final String APPOINTMENT_DATE = "Appointment date";

	public static final long serialVersionUID = 128234353L;

	private final Log log = LogFactory.getLog(getClass());

	private Integer patientId;
	private Integer formId;
    private String formName;
	private Integer initialEncounterTypeId;
    private String initialEncounterTypeName;
	private Integer followupEncounterTypeId;
    private String followupEncounterTypeName;
	private boolean readonly = false;
	private String programWorkflowStates;
	private Integer patientIdentifierType;
	private boolean includeAppointmentInfo = true;

	public int doStartTag() throws JspException {

		JspWriter o = pageContext.getOut();
		try {
			Patient p = Context.getPatientService().getPatient(getPatientId());
            Form f = null;
            if (StringUtils.isNotBlank(getFormName())) {
                f = Context.getFormService().getForm(getFormName());
            }
            if (f == null) {
                f = Context.getFormService().getForm(getFormId());
            }
			EncounterType initialEncounterType = null;
            if (StringUtils.isNotBlank(getInitialEncounterTypeName())) {
                initialEncounterType = Context.getEncounterService().getEncounterType(getInitialEncounterTypeName());
            }
            if (initialEncounterType == null ) {
                initialEncounterType = Context.getEncounterService().getEncounterType(getInitialEncounterTypeId());
            }

            // Ensure valid form and initial encounter type passed in
			if (f == null || initialEncounterType == null) {
				o.write("Not available: Wrong configuration");
				release();
				return SKIP_BODY;
			}

            // Ensure no more than one initial encounter is found
			List<Encounter> initials = Context.getEncounterService().getEncounters(p, null, null, null, null, Arrays.asList(initialEncounterType), null, false);
			if (initials.size() > 1) {
				o.write("Not available: Multiple " + f.getName() + " forms found");
				release();
				return SKIP_BODY;
			}

            // Ensure that the patient has a valid program enrollment, identifier, and state
			List<ProgramWorkflowState> stateList = Helper.getProgramWorkflowStatesFromCsvIds(programWorkflowStates);
			ProgramWorkflow workflow = (stateList == null || stateList.isEmpty() ? null : stateList.get(0).getProgramWorkflow());
			if (initials.size() == 0) {
                if (!Helper.userHasEditPrivilege()) {
                    o.write("Not available: User does not have privileges to edit patient");
                } else if (!Helper.isInProgramWorkflowState(p, stateList)) {
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
                if (!Helper.userHasEditPrivilege()) {
                    o.write(createViewCardHtmlTag(p, f, initials.get(0), null));
                    release();
                    return SKIP_BODY;
                }
				if (!Helper.isInProgramWorkflowState(p, stateList)) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0), "Readonly: Inactive program state"));
					release();
					return SKIP_BODY;
				}
				if (!Helper.hasIdentifierType(p, getPatientIdentifierType())) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0), "Readonly: No identifier"));
					release();
					return SKIP_BODY;
				}
				if (!Helper.hasIdentifierForEnrollmentLocation(p, getPatientIdentifierType(), workflow)) {
					o.write(createViewCardHtmlTag(p, f, initials.get(0), "Readonly: No identifier for current enrollment location"));
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
		}
        catch (Throwable e) {
			try {
				o.write("Unknown error, call help!");
			}
            catch (IOException e1) {
			}
			log.error("Could not write to pageContext", e);
		}
		release();
		return SKIP_BODY;
	}

    protected String getNewMasterCardConfiguration(Form f) {
        Map<String, String> m = new HashMap<String, String>();

        m.put(HivMetadata.PRE_ART_INITIAL, "headerForm=preart_mastercard&flowsheets=preart_visit");
        m.put(EncounterTypes.ASTHMA_INITIAL.name(), "headerForm=asthma_mastercard&flowsheets=asthma_visit&flowsheets=asthma_peak_flow&flowsheets=asthma_hospital");
        m.put(EncounterTypes.HTN_DIABETES_INITIAL.name(), "headerForm=htn_dm_mastercard&flowsheets=htn_dm_labs&flowsheets=htn_dm_annual_labs&flowsheets=htn_dm_hospital&flowsheets=htn_dm_visit");
        m.put(EncounterTypes.EPILEPSY_INITIAL.name(), "headerForm=epilepsy_mastercard&flowsheets=epilepsy_visit");
		m.put(EncounterTypes.PALLIATIVE_INITIAL.name(), "headerForm=palliative_mastercard&flowsheets=palliative_visit");
		m.put(EncounterTypes.CHF_INITIAL.name(), "headerForm=chf_mastercard&flowsheets=chf_quarterly_screening&flowsheets=chf_annual_screening&flowsheets=chf_history_of_hospitalizations&flowsheets=chf_visit");
		m.put(EncounterTypes.CKD_INITIAL.name(), "headerForm=ckd_mastercard&flowsheets=ckd_quarterly_screening&flowsheets=ckd_annual_screening&flowsheets=ckd_imaging&flowsheets=ckd_history_of_hospitalizations&flowsheets=ckd_visit");
        m.put(EncounterTypes.MENTAL_HEALTH_INITIAL.name(), "headerForm=mental_health_mastercard&flowsheets=mental_health_visit");
        m.put(EncounterTypes.CHRONIC_CARE_INITIAL.name(), "headerForm=ncd_mastercard&flowsheets=ncd_visit");
		m.put(HivMetadata.EXPOSED_CHILD_INITIAL, "headerForm=eid_mastercard&flowsheets=eid_visit");

		if (f.getName().equals("Viral Load Tests") && f.getEncounterType().getName().equals("ART_FOLLOWUP")) {
			m.put(EncounterTypes.ART_FOLLOWUP.name(), "headerForm=blank_header&flowsheets=viral_load_test_results&requireObs=" + CommonMetadata.HIV_VIRAL_LOAD_TEST_SET);
		} else if (f.getEncounterType().getName().equals("ART_INITIAL")) {
			m.put(EncounterTypes.ART_INITIAL.name(), "headerForm=art_mastercard&flowsheets=art_annual_screening&flowsheets=art_visit");
		}
		return m.get(f.getEncounterType().getName());
    }

	protected String createViewCardHtmlTag(Patient p, Form f, Encounter initialEncounter, String additionalMessage) {
        String link = "";
        String newMasterCardConfig = getNewMasterCardConfiguration(f);
        if (newMasterCardConfig != null) {
            link = "<a href=\"/openmrs/pihmalawi/mastercard.page?" + newMasterCardConfig + "&viewOnly=true&patientId="+p.getPatientId()+"\">";
        }
        else {
            link = "<a href=\"javascript:void(0)\" onClick=\"loadUrlIntoEncounterPopup('"
                    + initialEncounter.getEncounterType().getName()
                    + "@"
                    + initialEncounter.getLocation().getName()
                    + " | "
                    + Helper.formatDate(initialEncounter.getEncounterDatetime())
                    + " | "
                    + ""
                    + "', '/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId="
                    + initialEncounter.getId()
                    + "&inPopup=true'); return false;\">";
        }
        return link + "View " + f.getName() + "</a><br/>"
                + (includeAppointmentInfo ? getDetails(p, initialEncounter) + "<br/>" : "")
                + "(" + additionalMessage + ")";
	}

	protected String createEditCardHtmlTag(Patient p, Form f, Encounter initialEncounter) {
        String link = "";
        String newMasterCardConfig = getNewMasterCardConfiguration(f);
        if (newMasterCardConfig != null) {
            link = "<a href=\"/openmrs/pihmalawi/mastercard.page?" + newMasterCardConfig + "&patientId="+p.getPatientId()+"\">";
        }
        else {
            link = "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + initialEncounter.getId() + "&mode=EDIT\">";
        }
		if (f.getName().equals("Viral Load Tests") || f.getName().equals("EID Test Results")) {
			return link + "Edit " + f.getName() + "</a><br/>";
        } else {
			return link + "Edit " + f.getName() + "</a><br/>" + (includeAppointmentInfo ? getDetails(p, initialEncounter) + "<br/>" : "");
		}
	}

    protected String createNewCardHtmlTag(Patient p, Form f) {
        String link = "";
        String newMasterCardConfig = getNewMasterCardConfiguration(f);
        if (newMasterCardConfig != null) {
            link = "<a href=\"/openmrs/pihmalawi/mastercard.page?" + newMasterCardConfig + "&patientId="+p.getPatientId()+"\">";
        }
        else {
            link = "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?personId="
                    + p.getPersonId()
                    + "&patientId="
                    + p.getPatientId()
                    + "&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId="
                    + f.getFormId() + "\">";
        }
        return link + "Create new " + f.getName() + "</a>";
    }

    protected String getDetails(Patient p, Encounter initialEncounter) {
        EncounterType followupEncounterType = null;
        if (StringUtils.isNotBlank(getFollowupEncounterTypeName())) {
            followupEncounterType = Context.getEncounterService().getEncounterType(getFollowupEncounterTypeName());
        }
        if (followupEncounterType == null) {
            followupEncounterType = Context.getEncounterService().getEncounterType(getFollowupEncounterTypeId());
        }
        List<Encounter> followups = Context.getEncounterService().getEncounters(p, null, null, null, null, Arrays.asList(followupEncounterType), null, false);
        String created = "Created: " + Helper.formatDate(initialEncounter.getEncounterDatetime());
        String visited = "Visited: no";
        String rvd = "Appointment: none";
        if (!followups.isEmpty()) {
            Encounter lastFollowup = followups.get(followups.size() - 1);
            visited = "Visited: " + Helper.formatDate(lastFollowup.getEncounterDatetime()) + " at " + lastFollowup.getLocation().getName();
            Concept appt = Context.getConceptService().getConcept(APPOINTMENT_DATE);
            List<Obs> os = Context.getObsService().getObservations(Arrays.asList((Person) p), Arrays.asList(lastFollowup), Arrays.asList(appt), null, null, null, null, 1, null, null, null, false);
            if (!os.isEmpty()) {
                rvd = "Appointment: " + Helper.formatDate(os.get(0).getValueDatetime());
            }
        }
        String details = created + ", " + visited + ", " + rvd;
        return details;
    }

	public int doEndTag() {
		patientId = null;
		formId = null;
        formName = null;
		initialEncounterTypeId = null;
        initialEncounterTypeName = null;
		followupEncounterTypeId = null;
        followupEncounterTypeName = null;
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

    public String getFormName() { return formName; }

    public void setFormName(String formName) { this.formName = formName; }

	public Integer getInitialEncounterTypeId() {
		return initialEncounterTypeId;
	}

	public void setInitialEncounterTypeId(Integer initialEncounterTypeId) {
		this.initialEncounterTypeId = initialEncounterTypeId;
	}

    public String getInitialEncounterTypeName() {
        return initialEncounterTypeName;
    }

    public void setInitialEncounterTypeName(String initialEncounterTypeName) {
        this.initialEncounterTypeName = initialEncounterTypeName;
    }

    public Integer getFollowupEncounterTypeId() {
		return followupEncounterTypeId;
	}

	public void setFollowupEncounterTypeId(Integer followupEncounterTypeId) {
		this.followupEncounterTypeId = followupEncounterTypeId;
	}

    public String getFollowupEncounterTypeName() {
        return followupEncounterTypeName;
    }

    public void setFollowupEncounterTypeName(String followupEncounterTypeName) {
        this.followupEncounterTypeName = followupEncounterTypeName;
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
