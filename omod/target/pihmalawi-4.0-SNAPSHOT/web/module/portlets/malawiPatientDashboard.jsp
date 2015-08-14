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


<table cellspacing="0" cellpadding="2">
    <tr>
        <td>ART Patient Card:</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="64" initialEncounterTypeId="9" followupEncounterTypeId="10" patientIdentifierType="4" programWorkflowStates="7"/></td>
    </tr>
    <tr>
        <c:set var="artInitialEncounter" value="" />
        <openmrs:forEachEncounter encounters="${model.patientEncounters}"
                                  type="9" num="1" var="enc">
            <c:if test="${ not empty enc }">
                <c:set var="artInitialEncounter" value="true" />
            </c:if>
        </openmrs:forEachEncounter>
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
        <td>HIV Tests:</td>
        <td>
            <!-- 'Creative' way in using the eMastercards; Followups are not used here, just make sure that there is one and only one DNA-PCR testing form -->
            <pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="70" initialEncounterTypeId="13" followupEncounterTypeId="13" patientIdentifierType="19" programWorkflowStates="120" includeAppointmentInfo="false"/>
            <br/>
            <openmrs:forEachEncounter encounters="${model.patientEncounters}" type="12" num="1" var="enc">
                <c:if test="${ not empty enc }">
                    <c:set var="partEncounter" value="true" />
                </c:if>
            </openmrs:forEachEncounter>
            <openmrs:forEachEncounter encounters="${model.patientEncounters}" type="13" num="1" var="enc">
                <c:if test="${ not empty enc }">
                    <c:set var="cd4Encounter" value="true" />
                </c:if>
            </openmrs:forEachEncounter>
            <c:choose>
                <c:when test="${ partEncounter || cd4Encounter }">
                    <c:set var="eMastercardFormId" value="63" />
                    <a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=${eMastercardFormId}">View CD4 Logbook</a>
                </c:when>
                <c:otherwise>
                    CD4 Logbook not available
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <td><br /></td>
    </tr>
    <tr>
        <td>Chronic Care Record:</td>
        <td><pihmalawi:eMastercardAccess patientId="${model.patientId}" formId="54" initialEncounterTypeId="67" followupEncounterTypeId="69" programWorkflowStates="83"/></td>
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
        <c:choose>
            <c:when test="${ evaluationEncounter || ctEncounter }">
                <c:set var="eMastercardFormId" value="62" />
                <td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=${eMastercardFormId}">Edit KS file</a></td>
            </c:when>
            <c:otherwise>
                <c:set var="eMastercardFormId" value="62" />
                <td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=${eMastercardFormId}">Create new KS file</a></td>
            </c:otherwise>
        </c:choose>
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
        <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="120,1,7" stateIds="7,87" terminalStateIds="2,12" /><br /></td>
    </tr>
    <tr>
        <td>Chronic Care Program:</td>
        <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="83" terminalStateIds="84,85,140" defaultLocation="2"/></td>
    </tr>
    <tr>
        <td>TB Program:</td>
        <td><pihmalawi:quickPrograms patientId="${model.patientId}" initialStateIds="92" defaultLocation="2"/></td>
    </tr>
</table>
<!-- <div class="portlet"> is automatically close by openmrs:portlet  from patientOverview.jsp-->
<!-- <div class="box"> is automatically close by openmrs:extensionPoint  from patientOverview.jsp-->