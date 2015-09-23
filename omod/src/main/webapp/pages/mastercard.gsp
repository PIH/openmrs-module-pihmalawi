<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("pihmalawi", "mastercard.js")
%>

<!-- TODO: Move this into the scripts folder and load like the above -->
<script src="/openmrs/moduleResources/pihmalawi/htmlform_common.js" type="text/javascript"></script>

<script type="text/javascript">
    jq(document).ready( function() {

        // In edit mode, make sure that focus is on the first obs element to enter
        jq(".obs-field:first").children()[0].focus();

        // In view mode, convert all of the visit htmlform tables into a single table
        var visitTables = jq(".visit-table").detach();
        if (visitTables && visitTables.length > 0) {
            var firstTable = visitTables[0];
            var firstTableBody = jq(firstTable).find(".visit-table-body :first");
            for (var i=1; i<visitTables.length; i++) {
                jq(visitTables[i]).find(".visit-table-row").appendTo(firstTableBody);
            }
        }
        jq("#visit-flowsheet-section").children().remove();
        jq("#visit-flowsheet-section").append(firstTable).show();

    } );
</script>

<style>
    body {
        width: 99%;
        max-width: none;
        font-size: smaller;
    }
    td {
        vertical-align: middle;
    }
    #form-actions {
        float: right;
    }
    .form-action-link {
        padding-left: 10px; padding-right:10px;
    }
    form input, form select, form textarea, form ul.select, form label, .form input, .form select, .form textarea, .form ul.select .form label {
        display: inline;
        min-width: inherit;
    }
    form input[type="checkbox"], form input[type="radio"], .form input[type="checkbox"], .form input[type="radio"] {
        float: inherit;
    }
    .left-cell {
        padding-right:5px; border-right:1px solid #DDD;
    }
    .right-cell {
        padding-left:5px;
    }
    table .visit-table-header td {
        border: 1px dotted #DDD;
        vertical-align: middle;
        text-align:center;
        background-color: rgb(255, 253, 247);
    }
    table .visit-table-body td {
        text-align: center;
    }
    #header-section {
        width:100%;
    }
    #visit-flowsheet-section {
        width:100%;
        padding-top:5px;
        display: none;
    }
    .visit-table-header {
        border: 2px solid #DDD;
    }
    #alert-section {
        padding:10px;
        border-bottom: 1px solid black;
        margin-bottom:10px;
    }
    #alert-table {
        color: red;
    }
    .nowrap {
        white-space: nowrap;
    }
    .value {
        font-weight:bold;
    }
</style>

<style type="text/css" media="print">
    @page {
        size: landscape;
        margin:.2in;
    }
    .hide-when-printing {
        display: none;
    }
    body {
        font-size: .3em;
    }
</style>

<div id="mastercard-app" ng-controller="MastercardCtrl" ng-init="init(${patient.id}, '${mode}', '${headerForm}', '${visitForm}')">

    <% if (!alerts.empty) { %>
        <div id="alert-section" class="hide-when-printing">
            <table id="alert-table">
                <% alerts.each { alert -> %>
                    <tr>
                        <td class="alert">${ alert }</td>
                    </tr>
                <% } %>
            </table>
        </div>
    <% } %>

    <div id="form-actions" class="hide-when-printing">
        <a class="form-action-link" id="edit-header-link" ng-click="reloadPageInNewMode('editHeader')" ng-hide="editingHeader">
            <i class="icon-pencil"></i>
            ${ ui.message("uicommons.edit") }
        </a>
        <a class="form-action-link" id="print-form-link" ng-click="printForm()" ng-hide="editingHeader">
            <i class="icon-print"></i>
            ${ ui.message("uicommons.print") }
        </a>
        <a class="form-action-link" id="cancel-button" ng-click="reloadPageInNewMode('')" ng-show="editingHeader">
            <i class="icon-circle-arrow-left"></i>
            ${ ui.message("uicommons.cancel") }
        </a>
        <a class="form-action-link" id="delete-button" ng-show="editingHeader">
            <i class="icon-remove"></i>
            ${ ui.message("uicommons.delete") }
        </a>
    </div>

    <div id="header-section">
        ${ ui.includeFragment(headerFragmentProvider, headerFragmentName, [
                patient: patient.patient,
                encounter: headerEncounter,
                definitionUiResource: headerFormResource,
                returnUrl: returnUrl
        ]) }
    </div>

    <div id="visit-flowsheet-section">
        <% visitEncounters.each { visitEncounter -> %>
        <tr>
            ${ ui.includeFragment(visitFragmentProvider, visitFragmentName, [
                    patient: patient.patient,
                    encounter: visitEncounter,
                    definitionUiResource: visitFormResource,
                    returnUrl: returnUrl
            ]) }
        </tr>
        <% } %>
    </div>

    <br/>
</div>

<script type="text/javascript">
    angular.bootstrap('#mastercard-app', [ 'mastercard' ]);
</script>