<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("pihmalawi", "mastercard.js")
    ui.includeJavascript("htmlformentryui", "htmlForm.js")
%>

<!-- TODO: Move this into the scripts folder and load like the above -->
<script src="/openmrs/moduleResources/pihmalawi/htmlform_common.js" type="text/javascript"></script>

<script type="text/javascript">

    mastercard.setPatientId(${ patient.patient.patientId });
    mastercard.setHeaderForm('${ headerForm }');
    mastercard.setHeaderEncounterId(${ headerEncounter == null ? null : headerEncounter.encounterId });
    mastercard.setVisitForm('${ visitForm }');

    <% visitEncounters.each { visitEncounter -> %>
        mastercard.addVisitEncounterId(${ visitEncounter.encounterId });
    <% } %>

    htmlForm.setSuccessFunction(function(result) {
        mastercard.successFunction(result);
    });

    jq(document).ready( function() {

        <% if (headerEncounter == null) { %>
            mastercard.enterHeader();
        <% } else { %>
            mastercard.viewHeader();
            mastercard.loadVisitTable();
        <% } %>
        mastercard.focusFirstObs();
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
    table .visit-table {
        width:100%;
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
    #error-message-section {
        display:none;
        padding:10px;
        font-weight:bold;
        color:red;
        border: 1px dotted red;
        width:75%;
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
    .units {
        padding-left:10px;
    }
    .error {
        padding:5px;
        font-weight: bold;
        color:red;
    }
    .visit-edit-table th {
        text-align: left;
        white-space: nowrap;
        padding:10px;
    }
    .visit-edit-table td {
        text-align: left;
        width: 100%;
        padding:10px;
    }
    .toast-container {
        display:none;
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

<div id="mastercard-app">

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
        <a class="form-action-link" id="edit-header-link" onclick="mastercard.enterHeader();">
            <i class="icon-pencil"></i>
            Edit Header
        </a>
        <a class="form-action-link" id="new-visit-link" onclick="mastercard.enterVisit();">
            <i class="icon-pencil"></i>
            Enter New Visit
        </a>
        <a class="form-action-link" id="print-form-link" onclick="mastercard.printForm();">
            <i class="icon-print"></i>
            ${ ui.message("uicommons.print") }
        </a>
        <a class="form-action-link" id="cancel-button" onclick="mastercard.cancelEdit();">
            <i class="icon-circle-arrow-left"></i>
            ${ ui.message("uicommons.cancel") }
        </a>
        <a class="form-action-link" id="delete-button" onclick="mastercard.deleteCurrentEncounter();">
            <i class="icon-remove"></i>
            ${ ui.message("uicommons.delete") }
        </a>
    </div>

    <div id="error-message-section"></div>

    <div id="header-section"></div>

    <div id="visit-flowsheet-section"></div>

    <div id="visit-edit-section"></div>

    <br/>
</div>
