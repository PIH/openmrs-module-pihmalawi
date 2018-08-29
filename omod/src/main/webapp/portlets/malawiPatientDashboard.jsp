<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="pihmalawi" uri="/WEB-INF/view/module/pihmalawi/taglib/pihmalawi.tld" %>

<script type="text/javascript">
    <%-- TODO: FORMATDATE AND PARSEDATE ARE TERRIBLE HACKS --%>
    function parseDate_2(date) {
        if (date == null || date == '')
            return '';
    <c:choose>
        <c:when test="${model.locale == 'fr' || model.locale == 'en_GB'}">
        // dd/mm/yyyy 01/34/6789
        return date.substring(6,10) + '-' + date.substring(3,5) + '-' + date.substring(0,2);
    </c:when>
        <c:otherwise>
        // mm/dd/yyyy 01/34/6789
        return date.substring(6,10) + '-' + date.substring(0,2) + '-' + date.substring(3,5);
    </c:otherwise>
        </c:choose>
    }

    function changeToState(patientProgramId, workflowId, stateId, dateField) {
        var onDate = parseDate_2(DWRUtil.getValue(dateField));
        DWRProgramWorkflowService.changeToState(patientProgramId, workflowId, stateId, onDate, function() {
            refreshPage();
        });
    }
</script>


<c:set var="personId" value="${model.personId}" />
<c:set var="patientId" value="${model.patientId}" />
<c:set var="PccOnTreatmentWorkflowState" value="7c1f852e-5120-4371-8136-f64614f5dfc7"/>
<c:set var="PccTreatmentStoppedWorkflowState" value="b35ed57c-7d54-4795-b678-f0947a135fda"/>
<c:set var="PccTransferedOutWorkflowState" value="e92017b9-45cf-41b9-bc69-a5b0232544c1"/>
<c:set var="PccDefaultedWorkflowState" value="0f034ef4-3f70-4514-a020-5fb928fc3394"/>
<c:set var="PccDiedWorkflowState" value="4bed1c08-1fe9-4972-8e7e-e93323c9f2c4"/>

<openmrs:globalProperty key="pihmalawi.showOldChronicCareCard" var="showOldChronicCareCard" defaultValue="true"/>
<openmrs:globalProperty key="pihmalawi.upperOrLowerNeno" var="upperOrLowerNeno" defaultValue="UPPER_NENO"/>

<table cellspacing="0" cellpadding="2">
    <tr>
        <td>ART Patient Card:</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="64" initialEncounterTypeId="9" followupEncounterTypeId="10" patientIdentifierType="4" programWorkflowStates="7"/></td>
    </tr>
        <c:set var="artInitialEncounter" value="" />
        <openmrs:forEachEncounter encounters="${model.patientEncounters}"
                                  type="9" num="1" var="enc">
            <c:if test="${ not empty enc }">
                <c:set var="artInitialEncounter" value="true" />
            </c:if>
        </openmrs:forEachEncounter>
        <c:choose>
            <c:when test="${ not empty artInitialEncounter }">
                <tr>
                    <td>Viral Load Tests:</td>
                    <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Viral Load Tests" initialEncounterTypeId="9" followupEncounterTypeId="10" patientIdentifierType="4" programWorkflowStates="7"/></td>
                </tr>
            </c:when>
        </c:choose>
    <tr>
        <td>Pre-ART Patient Card:</td>
        <c:choose>
            <c:when test="${ not empty artInitialEncounter }">
                <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="66" initialEncounterTypeId="11" followupEncounterTypeId="12" patientIdentifierType="19" programWorkflowStates="1" readonly="true"/> (Readonly: ART Initial Encounter)</td>
            </c:when>
            <c:otherwise>
                <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="66" initialEncounterTypeId="11" followupEncounterTypeId="12" patientIdentifierType="19" programWorkflowStates="1"/></td>
            </c:otherwise>
        </c:choose>
    </tr>
    <tr>
        <c:set var="artInitialEncounter" value="" />
        <openmrs:forEachEncounter encounters="${model.patientEncounters}" type="9" num="1" var="enc">
            <c:if test="${ not empty enc }">
                <c:set var="artInitialEncounter" value="true" />
            </c:if>
        </openmrs:forEachEncounter>
        <td>Exposed Child Patient Card:</td>
        <c:choose>
            <c:when test="${ not empty artInitialEncounter }">
                <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="68" initialEncounterTypeId="92" followupEncounterTypeId="93" patientIdentifierType="19" programWorkflowStates="120" readonly="true"/> (Readonly: ART Initial Encounter)</td>
            </c:when>
            <c:otherwise>
                <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="68" initialEncounterTypeId="92" followupEncounterTypeId="93" patientIdentifierType="19" programWorkflowStates="120"/></td>
            </c:otherwise>
        </c:choose>
    </tr>
    <tr>
        <td><br /></td>
    </tr>
    <tr>
        <td>Chronic Care Record:</td>
        <c:if test="${showOldChronicCareCard == 'true'}">
            <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="54" initialEncounterTypeId="67" followupEncounterTypeId="69" patientIdentifierType="21" programWorkflowStates="83"/></td
        </c:if>
        <c:if test="${showOldChronicCareCard != 'true'}">
            <td>&NonBreakingSpace;</td
        </c:if>
    </tr>
    <tr>
        <td>&NonBreakingSpace;</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Hypertension and Diabetes eMastercard" initialEncounterTypeName="DIABETES HYPERTENSION INITIAL VISIT" followupEncounterTypeName="DIABETES HYPERTENSION FOLLOWUP" programWorkflowStates="83" patientIdentifierType="21"/></td>
    </tr>
    <tr>
        <td>&NonBreakingSpace;</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Chronic Lung Disease eMastercard" initialEncounterTypeName="ASTHMA_INITIAL" followupEncounterTypeName="ASTHMA_FOLLOWUP" programWorkflowStates="83" patientIdentifierType="21"/></td>
    </tr>
    <tr>
        <td>&NonBreakingSpace;</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Epilepsy eMastercard" initialEncounterTypeName="EPILEPSY_INITIAL" followupEncounterTypeName="EPILEPSY_FOLLOWUP" programWorkflowStates="83" patientIdentifierType="21"/></td>
    </tr>
    <tr>
        <td>&NonBreakingSpace;</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Mental Health eMastercard" initialEncounterTypeName="MENTAL_HEALTH_INITIAL" followupEncounterTypeName="MENTAL_HEALTH_FOLLOWUP" programWorkflowStates="83" patientIdentifierType="21"/></td>
    </tr>
    <tr>
        <td>&NonBreakingSpace;</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Chronic Heart Disease eMastercard" initialEncounterTypeName="CHF_INITIAL" followupEncounterTypeName="CHF_FOLLOWUP" programWorkflowStates="83" patientIdentifierType="21"/></td>
    </tr>
    <tr>
        <td>&NonBreakingSpace;</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Chronic Kidney Disease eMastercard" initialEncounterTypeName="CKD_INITIAL" followupEncounterTypeName="CKD_FOLLOWUP" programWorkflowStates="83" patientIdentifierType="21"/></td>
    </tr>

    <tr>
        <td><br /></td>
    </tr>

    <tr>
        <td>Palliative Care Record:</td>
            <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formName="Palliative Care Mastercard" initialEncounterTypeName="PALLIATIVE_INITIAL" followupEncounterTypeName="PALLIATIVE_FOLLOWUP" programWorkflowStates="${PccOnTreatmentWorkflowState}" patientIdentifierType="22"/></td>
    </tr>

    <tr>
        <td><br /></td>
    </tr>

    <tr>
        <td>Tuberculosis Record:</td>
        <td><pihmalawi:eRecordAccess patientId="${model.patientId}" formId="21" encounterTypeId="14" programWorkflowStates="92" patientIdentifierType="7"/></td>
    </tr>
    <tr>
        <td><br /></td>
    </tr>
    <tr>
        <openmrs:forEachEncounter encounters="${model.patientEncounters}" type="24" num="1" var="enc">
            <c:if test="${ not empty enc }">
                <c:set var="ctEncounter" value="true" />
            </c:if>
        </openmrs:forEachEncounter>
        <openmrs:forEachEncounter encounters="${model.patientEncounters}" type="17" num="1" var="enc">
            <c:if test="${ not empty enc }">
                <c:set var="evaluationEncounter" value="true" />
            </c:if>
        </openmrs:forEachEncounter>
        <td>Kaposis Sarcoma Flowsheet:</td>
        <openmrs:hasPrivilege privilege="Edit Patients">
            <c:choose>
                <c:when test="${ evaluationEncounter || ctEncounter }">
                    <c:set var="eMastercardFormId" value="62" />
                    <td><a href="${pageContext.request.contextPath}/pihmalawi/mastercard.page?headerForm=ks_header&flowsheets=ks_eval&flowsheets=ks_chemo&patientId=${patientId}&requireEncounter=false">Edit KS file</a></td>
                </c:when>
                <c:otherwise>
                    <c:set var="eMastercardFormId" value="62" />
                    <td><a href="${pageContext.request.contextPath}/pihmalawi/mastercard.page?headerForm=ks_header&flowsheets=ks_eval&flowsheets=ks_chemo&patientId=${patientId}&requireEncounter=false">Create new KS file</a></td>
                </c:otherwise>
            </c:choose>
        </openmrs:hasPrivilege>
        <openmrs:hasPrivilege privilege="Edit Patients" inverse="true">
            <td>Not available: User does not have privileges to edit patient</td>
        </openmrs:hasPrivilege>
    </tr>
</table>
</div> <!-- end <div class="portlet" id="pihmalawi.malawiPatientDashboard">-->
</div> <!-- end <div class="box">-->
<br />

<div class="boxHeader${model.patientVariation}"><openmrs:message code="pihmalawi.quickprograms.sectionTitle" /></div>
<div class="box${model.patientVariation}"><openmrs:message code="" />
<div class="portlet" id="pihmalawi.quickPrograms">
<table cellspacing="0" cellpadding="2">
    <tr>
        <td>HIV Program:</td>
        <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="120,7" stateIds="7" terminalStateIds="2,12,119" /><br /></td>
    </tr>
    <tr>
        <td>Chronic Care Program:</td>
        <c:if test="${upperOrLowerNeno == 'UPPER_NENO'}">
            <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="83" terminalStateIds="84,86,140,141" defaultLocation="2"/><br /></td
        </c:if>
        <c:if test="${upperOrLowerNeno == 'LOWER_NENO'}">
            <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="83" terminalStateIds="84,86,140,154" defaultLocation="2"/><br /></td
        </c:if>
    </tr>
    <tr>
        <td>Palliative Care Program:</td>
        <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="${PccOnTreatmentWorkflowState}" terminalStateIds="${PccTreatmentStoppedWorkflowState},${PccTransferedOutWorkflowState},${PccDefaultedWorkflowState},${PccDiedWorkflowState}" defaultLocation="2"/><br /></td>
    </tr>
    <tr>
        <td>TB Program:</td>
        <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="92" defaultLocation="2"/></td>
    </tr>
</table>
<!-- <div class="portlet"> is automatically close by openmrs:portlet  from patientOverview.jsp-->
<!-- <div class="box"> is automatically close by openmrs:extensionPoint  from patientOverview.jsp-->