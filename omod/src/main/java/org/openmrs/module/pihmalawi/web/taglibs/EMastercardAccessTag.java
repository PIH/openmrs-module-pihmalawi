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
import org.openmrs.module.pihmalawi.Utils;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.pihmalawi.metadata.EncounterTypes;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	private String condition=null;
	private String conditionAnswer=null;

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
            if (initialEncounterType == null && getInitialEncounterTypeId() !=null ) {
                initialEncounterType = Context.getEncounterService().getEncounterType(getInitialEncounterTypeId());
            }
			Concept conditionConcept = null;
			List<Concept> conditionConceptAnswers = null;
			if (StringUtils.isNotBlank(getCondition()) && StringUtils.isNotBlank(getConditionAnswer())){
				conditionConcept = Context.getConceptService().getConceptByUuid(getCondition());
				conditionConceptAnswers = Helper.getConceptsFromString(getConditionAnswer());
			}

            // Ensure valid form and initial encounter type passed in
			if (f == null || initialEncounterType == null) {
				o.write("Not available: Wrong configuration");
				release();
				return SKIP_BODY;
			}

            // Ensure no more than one initial encounter is found
			List<Encounter> initials = Utils.getEncounters(p, initialEncounterType);
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
							if (conditionConcept !=null && conditionConceptAnswers !=null && (conditionConceptAnswers.size() > 0) && !Helper.hasCondition(p, conditionConcept, conditionConceptAnswers)) {
								List<String> diagnosis = new ArrayList<String>();
								for (Concept concept : conditionConceptAnswers) {
									diagnosis.add(concept.getDisplayString());
								}
								o.write("Not available: " + StringUtils.join(diagnosis, ",") + " diagnosis (" + f.getName() + ")");
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

		Map<String, String> headerForms = new LinkedHashMap<String, String>();
		headerForms.put(HivMetadata.PRE_ART_INITIAL, "preart_mastercard");
		headerForms.put(EncounterTypes.ASTHMA_INITIAL.name(), "asthma_mastercard");
		headerForms.put(EncounterTypes.HTN_DIABETES_INITIAL.name(), "htn_dm_mastercard");
		headerForms.put(EncounterTypes.EPILEPSY_INITIAL.name(), "epilepsy_mastercard");
		headerForms.put(EncounterTypes.PALLIATIVE_INITIAL.name(), "palliative_mastercard");
		headerForms.put(EncounterTypes.CHF_INITIAL.name(), "chf_mastercard");
		headerForms.put(EncounterTypes.CKD_INITIAL.name(), "ckd_mastercard");
		headerForms.put(EncounterTypes.NCD_OTHER_INITIAL.name(), "ncd_other_mastercard");
		headerForms.put(EncounterTypes.MENTAL_HEALTH_INITIAL.name(), "mental_health_mastercard");
		headerForms.put(EncounterTypes.CHRONIC_CARE_INITIAL.name(), "ncd_mastercard");
		headerForms.put(HivMetadata.EXPOSED_CHILD_INITIAL, "eid_mastercard");
		headerForms.put(EncounterTypes.ART_INITIAL.name(), "art_mastercard");
		headerForms.put(EncounterTypes.PDC_INITIAL.name(), "pdc_mastercard");
		headerForms.put(EncounterTypes.PDC_DEVELOPMENTAL_DELAY_INITIAL.name(), "pdc_developmental_delay_mastercard");
		headerForms.put(EncounterTypes.PDC_TRISOMY21_INITIAL.name(), "pdc_trisomy_21_mastercard");
		headerForms.put(EncounterTypes.PDC_CLEFT_CLIP_PALLET_INITIAL.name(),"pdc_cleft_lip_palate_mastercard");
		headerForms.put(EncounterTypes.PDC_OTHER_DIAGNOSIS_INITIAL.name(),"pdc_other_diagnosis_mastercard");

		Map<String, List<String>> flowsheetForms = new LinkedHashMap<String, List<String>>();
        flowsheetForms.put(HivMetadata.PRE_ART_INITIAL, Arrays.asList("preart_visit"));
        flowsheetForms.put(EncounterTypes.ASTHMA_INITIAL.name(), Arrays.asList("asthma_visit","asthma_peak_flow","asthma_hospital"));
        flowsheetForms.put(EncounterTypes.HTN_DIABETES_INITIAL.name(), Arrays.asList("htn_dm_labs","htn_dm_annual_labs","htn_dm_hospital","htn_dm_visit"));
        flowsheetForms.put(EncounterTypes.EPILEPSY_INITIAL.name(), Arrays.asList("epilepsy_visit"));
		flowsheetForms.put(EncounterTypes.PALLIATIVE_INITIAL.name(), Arrays.asList("palliative_visit"));
		flowsheetForms.put(EncounterTypes.CHF_INITIAL.name(), Arrays.asList("chf_echocardiogram","chf_ekg","chf_cxr","chf_quarterly_screening","chf_annual_screening","chf_history_of_hospitalizations","chf_visit"));
		flowsheetForms.put(EncounterTypes.CKD_INITIAL.name(), Arrays.asList("ckd_quarterly_screening","ckd_annual_screening","ckd_imaging","ckd_history_of_hospitalizations","ckd_visit"));
		flowsheetForms.put(EncounterTypes.NCD_OTHER_INITIAL.name(), Arrays.asList("ncd_other_quarterly_screening","ncd_other_annual_screening","ncd_other_hospitalizations","ncd_other_visit"));
		flowsheetForms.put(EncounterTypes.MENTAL_HEALTH_INITIAL.name(), Arrays.asList("mental_health_visit"));
        flowsheetForms.put(EncounterTypes.CHRONIC_CARE_INITIAL.name(), Arrays.asList("ncd_visit"));
		flowsheetForms.put(HivMetadata.EXPOSED_CHILD_INITIAL, Arrays.asList("eid_visit", "eid_test_results"));
		flowsheetForms.put(EncounterTypes.ART_INITIAL.name(), Arrays.asList("viral_load_test_results", "art_visit"));
		flowsheetForms.put(EncounterTypes.PDC_DEVELOPMENTAL_DELAY_INITIAL.name(), Arrays.asList("pdc_history_of_hospitalizations","pdc_complications","pdc_vision_test","pdc_hearing_test","pdc_radiology","pdc_developmental_delay_visit"));
		flowsheetForms.put(EncounterTypes.PDC_TRISOMY21_INITIAL.name(), Arrays.asList("pdc_history_of_hospitalizations","pdc_complications","pdc_vision_test","pdc_hearing_test","pdc_radiology","pdc_trisomy_21_visit"));
		flowsheetForms.put(EncounterTypes.PDC_CLEFT_CLIP_PALLET_INITIAL.name(), Arrays.asList("pdc_history_of_hospitalizations","pdc_complications","pdc_vision_test","pdc_hearing_test","pdc_radiology","pdc_cleft_lip_palate_visit"));
		flowsheetForms.put(EncounterTypes.PDC_OTHER_DIAGNOSIS_INITIAL.name(), Arrays.asList("pdc_history_of_hospitalizations","pdc_complications","pdc_vision_test","pdc_hearing_test","pdc_radiology","pdc_other_diagnosis_visit"));

		// hack to append the byConcept to the few forms that we fetch "byConcept" instead of by encounter type
		// TODO: move this into a more organized customization
		String byConcept = "";

		if (f.getName().equals("Viral Load Tests")) {
			byConcept = CommonMetadata.HIV_VIRAL_LOAD_TEST_SET;
		}

		String encType = f.getEncounterType().getName();
		String headerForm = headerForms.get(encType);
		List<String> flowsheets = flowsheetForms.get(encType);

		if (headerForm != null ) {
			StringBuilder sb = new StringBuilder();
			sb.append("/openmrs/htmlformentryui/htmlform/flowsheet.page?");
			sb.append("headerForm=pihmalawi:htmlforms/").append(headerForm).append(".xml");
			if (flowsheets != null) {
				for (String flowsheet : flowsheets) {
					sb.append("&flowsheets=pihmalawi:htmlforms/").append(flowsheet).append(".xml");
				}
			}

			if (StringUtils.isNotBlank(byConcept)) {
				sb.append("&byConcept=").append(byConcept);
			}

			sb.append("&dashboardUrl=legacyui&customizationProvider=pihmalawi&customizationFragment=mastercard");
			return sb.toString();
		}
		return null;
    }

	protected String createViewCardHtmlTag(Patient p, Form f, Encounter initialEncounter, String additionalMessage) {
        String link = "";
        String newMasterCardConfig = getNewMasterCardConfiguration(f);
        if (newMasterCardConfig != null) {
            link = "<a href=\"" + newMasterCardConfig + "&viewOnly=true&patientId="+p.getPatientId()+"\">";
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
            link = "<a href=\"" + newMasterCardConfig + "&patientId="+p.getPatientId()+"\">";
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

			String uuid = UUID.randomUUID().toString();

			// special case here to allow us to pre-pick the encounter date
			link = "<a onclick=\"window.location.href='"
					+ newMasterCardConfig
					+ "&patientId=" + p.getPatientId()
					+ "&encounterDate=' + $j.datepicker.formatDate('yy-mm-dd', $j.datepicker.parseDate('dd/mm/yy', $j('#date-" + uuid + "').val()))"
					+ "\">";

			return link + "Create new " + f.getName() + "</a> on " + dateTag("date-" + uuid, "date-" + uuid);
		}
        else {

        	link = "<a href=\"/openmrs/module/htmlformentry/htmlFormEntry.form?personId="
                    + p.getPersonId()
                    + "&patientId="
                    + p.getPatientId()
                    + "&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId="
                    + f.getFormId() + "\">";

			return link + "Create new " + f.getName() + "</a>";
        }
    }

    protected String getDetails(Patient p, Encounter initialEncounter) {
        EncounterType followupEncounterType = null;
        if (StringUtils.isNotBlank(getFollowupEncounterTypeName())) {
            followupEncounterType = Context.getEncounterService().getEncounterType(getFollowupEncounterTypeName());
        }
        if (followupEncounterType == null && (getFollowupEncounterTypeId() != null) ) {
            followupEncounterType = Context.getEncounterService().getEncounterType(getFollowupEncounterTypeId());
        }
        List<Encounter> followups = Utils.getEncounters(p, followupEncounterType);
        String created = "Created: " + Helper.formatDate(initialEncounter.getEncounterDatetime());
        String visited = "Visited: no";
        String rvd = "Appointment: none";
        if (!followups.isEmpty()) {
            Encounter lastFollowup = followups.get(followups.size() - 1);
            visited = "Visited: " + Helper.formatDate(lastFollowup.getEncounterDatetime()) + " at " + lastFollowup.getLocation().getName();
            Concept appt = Context.getConceptService().getConcept(APPOINTMENT_DATE);
            Date startOfDay = DateUtil.getStartOfDay(lastFollowup.getEncounterDatetime());
            Date endOfDay = DateUtil.getEndOfDay(lastFollowup.getEncounterDatetime());
            List<Obs> os = Context.getObsService().getObservations(Arrays.asList((Person) p), null, Arrays.asList(appt), null, null, null, Arrays.asList("dateCreated"), 1, null, startOfDay, endOfDay, false);
            if (!os.isEmpty()) {
                rvd = "Appointment: " + Helper.formatDate(os.get(0).getValueDatetime());
            }
        }
        String details = created + ", " + visited + ", " + rvd;
        return details;
    }

	private String dateTag(String id, String name) {
		String today = Context.getDateFormat().format(new Date());
		return "<input type=\"text\" id=\"" + id + "\" name=\"" + name + "\" size=\"10\" onClick=\"showCalendar(this)\" value=\"" + today + "\" />";
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
		condition = null;
		conditionAnswer = null;

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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getConditionAnswer() {
		return conditionAnswer;
	}

	public void setConditionAnswer(String conditionAnswer) {
		this.conditionAnswer = conditionAnswer;
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
