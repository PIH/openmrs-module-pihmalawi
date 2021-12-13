<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")

    ui.includeCss("pihmalawi", "bootstrap.css")

    ui.includeJavascript("uicommons", "handlebars/handlebars.js")
    ui.includeJavascript("uicommons", "moment.min.js")

%>

<script type="text/javascript">
  var breadcrumbs = [
    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
    { label: "${ ui.message("Export Patients")}" }
  ];
</script>

<h3>${  ui.message("Export Patients") }</h3>

<div class="container" id="exportPatients-app" >

    <table id="list-patients" cellspacing="0" cellpadding="2">
        <thead>
        <tr>
            <th>${ ui.message("coreapps.patient.identifier") }</th>
            <th>${ ui.message("coreapps.person.name")}</th>
            <th>${ ui.message("coreapps.gender") }</th>
            <th>${ ui.message("coreapps.age") }</th>
            <th>${ ui.message("coreapps.person.address") }</th>
            <th>${ ui.message("Date Changed") }</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <% patients.each { %>
        <tr>
            <td> ${ it.patientId }</td>
            <td>
                    ${ ui.format(it.personName) }
            </td>
            <td>
                ${ ui.format(it.gender) }
            </td>
            <td>
                ${ ui.format(it.age) }
            </td>
            <td>
                <% addressHierarchyLevels.each { addressLevel -> %>
                <% if(it.personAddress && it.personAddress[addressLevel]) { %>
                ${it.personAddress[addressLevel]}<% if(addressLevel != addressHierarchyLevels.last()){%>,<%}%>
                <% }%>
                <% } %>
            </td>
            <td>
                ${it.dateCreated}
            </td>
            <td>
                <button type="button" >${ ui.message("Export") }</button>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <% if ( (patients != null) && (patients.size() > 0) ) { %>
    ${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#list-patients",
                                                             options: [
                                                                     bFilter: true,
                                                                     bJQueryUI: true,
                                                                     bLengthChange: false,
                                                                     iDisplayLength: 10,
                                                                     sPaginationType: '\"full_numbers\"',
                                                                     bSort: false,
                                                                     sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
                                                             ]
    ]) }
    <% } %>

</div>
