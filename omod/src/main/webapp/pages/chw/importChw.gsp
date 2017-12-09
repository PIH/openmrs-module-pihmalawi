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

    <div ng-if="headerList && headerList.length != 0">
        <h3>${ ui.message("List of CHWs") }</h3>
        <button type="button" class="confirm" ng-click="importAllChw()">${ ui.message("Import All") }</button>
        <table id="list-chws" cellspacing="0" cellpadding="2">
            <thead>
            <tr>
                <th ng-repeat="columnName in headerList">{{ columnName }}</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="chw in chwList">
                <td>{{ chw.id }} </td>
                <td>
                    <a ng-show="chw.identifier" href="{{providerPage}}{{chw.personId}}">
                        {{ chw.identifier }}
                    </a>
                </td>
                <td>{{ chw.firstName }} </td>
                <td>{{ chw.lastName }} </td>
                <td>{{ chw.healthCenter }} </td>
                <td>{{ chw.role }} </td>
                <td ng-if="chw.gender">{{ chw.gender }} </td>
                <td ng-if="chw.age">{{ chw.age }} </td>
                <td ng-if="chw.district">{{ chw.district }} </td>
                <td ng-if="chw.ta">{{ chw.ta }} </td>
                <td ng-if="chw.gvh">{{ chw.gvh }} </td>
                <td ng-if="chw.village">{{ chw.village }} </td>

                <td>
                    <button type="button" ng-click="importChw(chw, true)">${ ui.message("Import") }</button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <h1>${ ui.message("Select file that contains CHW records") }:</h1>
    <input type="file" on-read-file="showContent(fileContent)" />
    <br>


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
