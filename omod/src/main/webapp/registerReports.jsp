<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>Register and Remove Reports</h2>

<h3>HIV Reports</h3>

ARV Quarterly: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_arvquarterly.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_arvquarterly.form">Remove</a>
<br/>

ART Appointment Adherence: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_artappadherence.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_artappadherence.form">Remove</a>
<br/>

<h4>Upper Neno</h4>
ART Missed Appointments: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_artmissedappointment.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_artmissedappointment.form">Remove</a>
<br/>

Pre-ART Missed Appointments: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_partmissedappointment.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_partmissedappointment.form">Remove</a>
<br/>

HCC Missed Appointments: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_hccmissedappointment.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_hccmissedappointment.form">Remove</a>
<br/>

<h4>Lower Neno</h4>
ART Missed Appointments Lower Neno: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_artmissedappointment_lowerneno.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_artmissedappointment_lowerneno.form">Remove</a>
<br/>

Pre-ART Missed Appointments Lower Neno: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_partmissedappointment_lowerneno.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_partmissedappointment_lowerneno.form">Remove</a>
<br/>

HCC Missed Appointments: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_hccmissedappointment_lowerneno.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_hccmissedappointment_lowerneno.form">Remove</a>
<br/>

<h3>Chronic Care</h3>
Chronic Care Missed Appointments: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_chroniccaremissedappointment.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_chroniccaremissedappointment.form">Remove</a>
<br/>

Chronic Care Appointment Adherence: <a href="${pageContext.request.contextPath}/module/pihmalawi/register_chroniccareappadherence.form">(Re) register</a>
<a href="${pageContext.request.contextPath}/module/pihmalawi/remove_chroniccareappadherence.form">Remove</a>
<br/>

<%@ include file="/WEB-INF/template/footer.jsp"%>