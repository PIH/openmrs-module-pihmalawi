<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>

<h2>${location.name} <openmrs:formatDate format="dd/MM/yyyy" date="${runDate}"/></h2>

<openmrs:portlet id="summaryPortlet" moduleId="pihmalawi" url="${view}" patientIds="${patientIds}" parameters="locationId=${location.locationId}" />

<%@ include file="/WEB-INF/template/footer.jsp" %>
