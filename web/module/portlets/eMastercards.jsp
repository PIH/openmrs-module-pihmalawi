<%@ include file="/WEB-INF/template/include.jsp"%>
aa
<%=request.getHeader("referer") %>
aaa

script type="text/javascript"
top.location.href = "admin/modules/module.list";
script

<c:set var="personId" value="${model.personId}" />
<c:set var="patientId" value="${model.patientId}" />

<table cellspacing="0" cellpadding="2">
<!--
	<tr>
		<c:set var="eMastercardFormId" value="57" />
		<c:set var="eMastercardLabel" value="ART Mastercard" />
		<c:set var="initialEncounterTypeId" value="9" />

		<c:set var="initialEncounterId" value="" />
		<c:set var="initialEncounterDatetime" value="" />
		<c:set var="multipleInitialEncounters" value="" />
		<openmrs:forEachEncounter encounters="${model.patientEncounters}"
			type="${initialEncounterTypeId}" num="2" var="enc">
			<c:if test="${ not empty initialEncounterId }">
				<c:set var="multipleInitialEncounters" value="true" />
			</c:if>
			<c:set var="initialEncounterId" value="${enc.encounterId}" />
			<c:set var="initialEncounterDatetime" value="${enc.encounterDatetime}" />
		</openmrs:forEachEncounter>
		<td>${eMastercardLabel}:</td>
		<c:choose>
			<c:when test="${ not empty multipleInitialEncounters }">
				<td colspan="3">Multiple Initial Encounters found, eMastercard not available</td>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${ not empty initialEncounterId }">
						<td>Create new Card</td>
						<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?encounterId=${initialEncounterId}&mode=EDIT">Edit Card (created <openmrs:formatDate type="xml" date="${initialEncounterDatetime}"/>)</a></td>
						<td></td>
					</c:when>
					<c:otherwise>
						<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=${eMastercardFormId}">Create new Card</a></td>
						<td>Edit Card</td>
						<td></td>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</tr>
-->
	<tr>
		<c:set var="eMastercardFormId" value="58" />
		<c:set var="eMastercardLabel" value="Pre-ART Mastercard" />
		<c:set var="initialEncounterTypeId" value="11" />

		<c:set var="initialEncounterId" value="" />
		<c:set var="initialEncounterDatetime" value="" />
		<c:set var="multipleInitialEncounters" value="" />
		<c:set var="artInitialEncounter" value="" />

		<openmrs:forEachEncounter encounters="${model.patientEncounters}"
			type="${initialEncounterTypeId}" num="2" var="enc">
			<c:if test="${ not empty initialEncounterId }">
				<c:set var="multipleInitialEncounters" value="true" />
			</c:if>
			<c:set var="initialEncounterId" value="${enc.encounterId}" />
			<c:set var="initialEncounterDatetime" value="${enc.encounterDatetime}" />
		</openmrs:forEachEncounter>
		<openmrs:forEachEncounter encounters="${model.patientEncounters}"
			type="10" num="1" var="enc">
			<c:if test="${ not empty enc }">
				<c:set var="artInitialEncounter" value="true" />
			</c:if>
		</openmrs:forEachEncounter>
		<td>${eMastercardLabel}:</td>
		<c:choose>
			<c:when test="${ not empty artInitialEncounter }">
				<td colspan="3">ART followup encounter exists, ${eMastercardLabel} not available</td>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${ not empty multipleInitialEncounters }">
						<td colspan="3">Multiple Initial Encounters found, eMastercard not available</td>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${ not empty initialEncounterId }">
								<td>Create new Card</td>
								<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?encounterId=${initialEncounterId}&mode=EDIT">Edit Card (created <openmrs:formatDate type="xml" date="${initialEncounterDatetime}"/>)</a></td>
								<td></td>
							</c:when>
							<c:otherwise>
								<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=${eMastercardFormId}">Create new Card</a></td>
								<td>Edit Card</td>
								<td></td>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<c:set var="eMastercardFormId" value="28" />
		<c:set var="eMastercardLabel" value="EID Mastercard" />
		<c:set var="initialEncounterTypeId" value="20" />

		<c:set var="initialEncounterId" value="" />
		<c:set var="initialEncounterDatetime" value="" />
		<c:set var="multipleInitialEncounters" value="" />
		<c:set var="artInitialEncounter" value="" />

		<openmrs:forEachEncounter encounters="${model.patientEncounters}"
			type="${initialEncounterTypeId}" num="2" var="enc">
			<c:if test="${ not empty initialEncounterId }">
				<c:set var="multipleInitialEncounters" value="true" />
			</c:if>
			<c:set var="initialEncounterId" value="${enc.encounterId}" />
			<c:set var="initialEncounterDatetime" value="${enc.encounterDatetime}" />
		</openmrs:forEachEncounter>
		<openmrs:forEachEncounter encounters="${model.patientEncounters}"
			type="10" num="1" var="enc">
			<c:if test="${ not empty enc }">
				<c:set var="artInitialEncounter" value="true" />
			</c:if>
		</openmrs:forEachEncounter>
		<td>${eMastercardLabel}:</td>
		<c:choose>
			<c:when test="${ not empty artInitialEncounter }">
				<td colspan="3">ART followup encounter exists, ${eMastercardLabel} not available</td>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${ not empty multipleInitialEncounters }">
						<td colspan="3">Multiple Initial Encounters found, eMastercard not available</td>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${ not empty initialEncounterId }">
								<td>Create new Card</td>
								<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?encounterId=${initialEncounterId}&mode=EDIT">Edit Card (created <openmrs:formatDate type="xml" date="${initialEncounterDatetime}"/>)</a></td>
								<td></td>
							</c:when>
							<c:otherwise>
								<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=${eMastercardFormId}">Create new Card</a></td>
								<td>Edit Card</td>
								<td></td>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<td><br /></td>
	</tr>
	<tr>
		<c:set var="eMastercardFormId" value="54" />
		<c:set var="eMastercardLabel" value="Chronic Care Record" />
		<c:set var="initialEncounterTypeId" value="67" />

		<c:set var="initialEncounterId" value="" />
		<c:set var="initialEncounterDatetime" value="" />
		<c:set var="multipleInitialEncounters" value="" />

		<openmrs:forEachEncounter encounters="${model.patientEncounters}"
			type="${initialEncounterTypeId}" num="2" var="enc">
			<c:if test="${ not empty initialEncounterId }">
				<c:set var="multipleInitialEncounters" value="true" />
			</c:if>
			<c:set var="initialEncounterId" value="${enc.encounterId}" />
			<c:set var="initialEncounterDatetime" value="${enc.encounterDatetime}" />
		</openmrs:forEachEncounter>
		<td>${eMastercardLabel}:</td>
		<c:choose>
			<c:when test="${ not empty multipleInitialEncounters }">
				<td colspan="3">Multiple Initial Encounters found, eMastercard not available</td>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${ not empty initialEncounterId }">
						<td>Create new Card</td>
						<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?encounterId=${initialEncounterId}&mode=EDIT">Edit Card (created <openmrs:formatDate type="xml" date="${initialEncounterDatetime}"/>)</a></td>
						<td></td>
					</c:when>
					<c:otherwise>
						<td><a href="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=${eMastercardFormId}">Create new Card</a></td>
						<td>Edit Card</td>
						<td></td>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<td><br /></td>
	</tr>
	<tr>
		<td>TB Treatment Card:</td>
		<td>(todo)</td>
	</tr>
	<tr>
		<td><br /></td>
	</tr>
	<tr>
		<td>KS Flowsheet:</td>
		<td>(todo)</td>
	</tr>
</table>
