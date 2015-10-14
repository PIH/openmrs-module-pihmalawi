<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("pihmalawi", "mastercard.js")
    ui.includeJavascript("htmlformentryui", "htmlForm.js")
%>

<!-- TODO: Move this into the scripts folder and load like the above -->
<script src="/openmrs/moduleResources/pihmalawi/htmlform_common.js" type="text/javascript"></script>

<script type="text/javascript">

    function loadHtmlFormForEncounter(section, encounterId, mode) {
        jq.get(emr.pageLink('pihmalawi', 'htmlForm', {
            "patient": ${patient.id},
            "encounter": encounterId,
            "editMode": mode == 'edit',
            "formName": section == 'header' ? '${ headerForm }' : '${ visitForm }',
            "returnUrl": '${ ui.thisUrl() }'
        }), function(data) {
            if (section == 'header') {
                jq('#header-section').html(data);
            }
            else {
                var existingVisitTable = jq(".visit-table-body");
                if (existingVisitTable.length > 0) {
                    jq(existingVisitTable).append(jq(data).find(".visit-table-row"));
                }
                else {
                    jq('#visit-flowsheet-section').append(data);
                }
            }
        });
    };

    function viewHeader() {
        loadHtmlFormForEncounter('header', '${ headerEncounter.encounterId }', 'view');
        jq(".form-action-link").show();
        jq("#delete-button").hide();
        jq("#cancel-button").hide();
    }

    function editHeader() {
        loadHtmlFormForEncounter('header', '${ headerEncounter.encounterId }', 'edit');
        jq(".form-action-link").hide();
        jq("#delete-button").show();
        jq("#cancel-button").show();
    }

    jq(document).ready( function() {

        <% if (headerEncounter == null) { %>
            loadHtmlFormForEncounter('header', null, 'edit');
        <% } else { %>
            viewHeader();
        <% } %>

        <% visitEncounters.each { visitEncounter -> %>
            loadHtmlFormForEncounter('visit-flowsheet', ${ visitEncounter.encounterId }, 'view');
        <% } %>

        // In edit mode, make sure that focus is on the first obs element to enter
        var firstObsField = jq(".obs-field:first");
        if (firstObsField > 0) {
            firstObsField.children()[0].focus();
        }

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

<div id="mastercard-app" ng-controller="MastercardCtrl" ng-init="init(${patient.id}, '${headerForm}', '${visitForm}')">

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
        <a class="form-action-link" id="edit-header-link" onclick="editHeader();">
            <i class="icon-pencil"></i>
            ${ ui.message("uicommons.edit") }
        </a>
        <a class="form-action-link" id="print-form-link" ng-click="printForm()" ng-hide="editingHeader">
            <i class="icon-print"></i>
            ${ ui.message("uicommons.print") }
        </a>
        <a class="form-action-link" id="cancel-button" onclick="viewHeader();">
            <i class="icon-circle-arrow-left"></i>
            ${ ui.message("uicommons.cancel") }
        </a>
        <a class="form-action-link" id="delete-button">
            <i class="icon-remove"></i>
            ${ ui.message("uicommons.delete") }
        </a>
    </div>

    <div id="header-section"></div>

    <div id="visit-flowsheet-section"></div>

    <br/>
</div>

<script type="text/javascript">
    angular.bootstrap('#mastercard-app', [ 'mastercard' ]);
</script>