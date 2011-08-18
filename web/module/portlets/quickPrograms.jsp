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

<table cellspacing="0" cellpadding="2">
	<tr>
		<td>HIV Program:</td>
		<td><pihmalawi:quickPrograms patientId="${model.patientId}" stateIds="7,87" initialStateIds="1,7" terminalStateIds="2" /><br /></td>
	</tr>
	<tr>
		<td>Chronic Care Program:</td>
		<td><pihmalawi:quickPrograms patientId="${model.patientId}" stateIds="" initialStateIds="83" terminalStateIds="" /></td>
	</tr>
</table>