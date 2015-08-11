<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "i18n/angular-locale_" + context.locale.toString().toLowerCase() + ".js")
%>

<h1>
    ${ ui.message("pihmalawi.title") } Test Page
</h1>

This is a test page to confirm UI Framework is working

<h3>All Locations in the System</h3>
<table style="width:100%;">

<% locations.each { loc -> %>
    <tr>
        <td>${ loc.id }</td>
        <td>${ loc.uuid }</td>
        <td>${ loc.name }</td>
    </tr>
<% } %>

</table>