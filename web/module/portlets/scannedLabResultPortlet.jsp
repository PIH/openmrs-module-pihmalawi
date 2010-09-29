<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ taglib prefix="pih" uri="/WEB-INF/view/module/pihmalawi/resources/pihmalawi.tld" %>

<openmrs:globalProperty key="pihmalawi.scannedLabResultConceptId" var="scannedLabResultConceptId"/>
<br/>
<c:if test="${!empty param.message}">
	<span style="color:red; font-weight:bold; font-size:small;"><spring:message code="${param.message}"/></span>
	<br/><br/>
</c:if>

<table class="portletTable" style="font-size:small;">
	<tr>
		<th align="left"><spring:message code="Encounter.title"/></th>
		<th align="left"><spring:message code="pihmalawi.scannedLabResult"/></th>
		<th align="left"><spring:message code="general.action"/></th>
	</tr>
	<openmrs:forEachObs conceptId="${scannedLabResultConceptId}" var="o" obs="${model.patientObs}" descending="true">
		<form action="${pageContext.request.contextPath}/module/pihmalawi/voidObs.form">
			<input type="hidden" name="patientId" value="${model.patientId}"/>
			<input type="hidden" name="obsId" value="${o.obsId}"/>
			<tr>
				<td style="white-space:nowrap;">
					<openmrs:formatDate date="${o.obsDatetime}" />
					&nbsp;(${o.encounter.encounterType.name})
				</td>
				<td style="white-space:nowrap;">
					<a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${o.obsId}&view=download&viewType=download">
						<pih:complexObs obsId="${o.obsId}" var="complexObs">
							${complexObs.complexData.title}
						</pih:complexObs>
					</a>
				</td>
				<td style="white-space:nowrap;"><input type="submit" value="[X]" onclick="return confirm('Please confirm you wish to permanantly delete this.');"></td>
			</tr>
		</form>
	</openmrs:forEachObs>
	<form method="post" action="${pageContext.request.contextPath}/module/pihmalawi/addScannedResult.form" enctype="multipart/form-data">
		<input type="hidden" name="patientId" value="${model.patientId}"/>
		<input type="hidden" name="conceptId" value="${scannedLabResultConceptId}"/>
		<tr>
			<td>
				<select name="encounterId">
					<option value=""><spring:message code="general.choose"/>...</option>
					<c:forEach items="${model.patientEncounters}" var="encounter">
						<option value="${encounter.encounterId}">
							<openmrs:formatDate date="${encounter.encounterDatetime}" format="${dateFormat}"/>
							&nbsp;(${encounter.encounterType.name})
						</option>
					</c:forEach>
				</select>
			</td>
			<td><input type="file" name="obsValue" size="50" /></td>
			<td><input type="submit" value="<spring:message code="general.add"/>"/></td>
		</tr>
	</form>
</table>	
