<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "moment.min.js")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("pihmalawi", "mastercard.js")
%>

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
</style>

<style type="text/css" media="print">
    @page {
        size: landscape;
    }
    .hide-when-printing {
        display: none;
    }
    body {
        font-size: .3em;
    }
</style>

<div id="mastercard-app" ng-controller="MastercardCtrl" ng-init="init(${patient.id}, ${encounter.id}, '${mode}')">

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

        <div id="header-view-section">
            ${ ui.includeFragment(headerFragmentProvider, headerFragmentName, [
                    patient: patient.patient,
                    encounter: encounter,
                    definitionUiResource: "pihmalawi:htmlforms/" + headerForm + ".xml",
                    returnUrl: returnUrl
            ]) }
        </div>

    </div>

    <br/>
</div>

<script type="text/javascript">
    angular.bootstrap('#mastercard-app', [ 'mastercard' ]);
</script>