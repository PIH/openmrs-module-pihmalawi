<%@ include file="/WEB-INF/template/include.jsp" %>

<style type="text/css">
	.mdrListLabel {
		font-size: .8em;
	}
	.mdrListValue {
		font-size: .8em;
		font-weight: bold;
		color:blue;
	}
	.mdrListBigValue { 
		font-weight: bold;
		font-size: 1.0em;
	}
	.mdrListTable td {
		vertical-align: top;
		white-space:nowrap;
	}
</style>

<c:choose>
	<c:when test="${empty model.data}">
		<br/>
		<i>Il n'ya pas de patients</i>
	</c:when>
	<c:otherwise>
		<table cellspacing="0" cellpadding="2" border="1" class="mdrListTable" width="99%">	
			<tbody>
				<c:forEach var="patient" items="${model.data}" varStatus="s">
		
					<tr class="${s.index % 2 == 0 ? 'oddRow' : 'evenRow'}">
						<td>
							<span class="mdrListBigValue">
								<a href="${pageContext.request.contextPath}/module/mdrtb/mdrtbPatientOverview.form?patientId=${patient.patientId}">
									${patient.fullName}
								</a>
							</span><br/>
							<span class="mdrListLabel">No. Dossier:</span>
							<span class="mdrListValue">${patient.mdrtbId}</span><br/>
							<span class="mdrListLabel">Age:</span>
							<span class="mdrListValue">${patient.age}</span>&nbsp;|&nbsp;
							<span class="mdrListLabel">Sexe:</span>
							<span class="mdrListValue">${patient.gender}</span><br/>
							<span class="mdrListLabel">Statut VIH:</span> 
							<span class="mdrListValue">
								${patient.hivStatus}
								<c:if test="${patient.hivStatusDate != null}">
									(<openmrs:formatDate format="dd/MM/yyyy" date="${patient.hivStatusDate}"/>)
								</c:if>
							</span>
						</td>
						<td style="white-space:normal;">
							<span class="mdrListLabel">Regime Actuel:</span> 
							<span class="mdrListValue"><br/>${patient.currentRegimen}</span>
							<br/>
							<span class="mdrListLabel">Date Hebergement:</span>
							<span class="mdrListValue"><openmrs:formatDate format="dd/MM/yyyy" date="${patient.hospitalizedDate}"/></span>
							&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
							<span class="mdrListLabel">Date Ambulatoires:</span>
							<span class="mdrListValue"><openmrs:formatDate format="dd/MM/yyyy" date="${patient.ambulatoryDate}"/></span>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>