<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")

    ui.includeCss("pihmalawi", "bootstrap.css")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.13.0.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-translate.min.js")
    ui.includeJavascript("uicommons", "angular-translate-loader-url.min.js")
    ui.includeJavascript("uicommons", "services/session.js")
    ui.includeJavascript("uicommons", "services/appFrameworkService.js")
    ui.includeJavascript("uicommons", "model/user-model.js")
    ui.includeJavascript("uicommons", "model/encounter-model.js")
    ui.includeJavascript("uicommons", "model/visit-model.js")
    ui.includeJavascript("uicommons", "filters/display.js")
    ui.includeJavascript("uicommons", "filters/serverDate.js")
    ui.includeJavascript("uicommons", "directives/select-drug.js")
    ui.includeJavascript("uicommons", "directives/select-concept-from-list.js")
    ui.includeJavascript("uicommons", "directives/select-order-frequency.js")
    ui.includeJavascript("uicommons", "handlebars/handlebars.js")
    ui.includeJavascript("uicommons", "moment.min.js")

    ui.includeJavascript("pihmalawi", "chw/importChwController.js")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("Import CHW")}" }
    ];
</script>

<div class="container" id="importChw-app" ng-controller="ImportChwController">

    <h1>${ ui.message("Select file that contains CHW records") }:</h1>
    <input type="file" on-read-file="showContent(fileContent)" />
    <br>
    <div ng-if="content && !errorMessage">
        <button type="button" class="confirm" ng-click="importChwFile()">${ ui.message("Import") }</button>
        <br>
        <br>
        <pre>{{ content }}</pre>
    </div>

</div>

<script type="text/javascript">
    angular.module('importChwApp');
    angular.bootstrap("#importChw-app", [ "importChwApp" ]);
    jq(function () {
        jq(document).on('sessionLocationChanged', function () {
            window.location.reload();
        });
    });
</script>
