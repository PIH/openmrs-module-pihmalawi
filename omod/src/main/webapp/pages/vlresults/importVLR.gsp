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

    ui.includeJavascript("pihmalawi", "vlresults/importVLRController.js")
%>
<style type="text/css">
.notFoundPatient {
    background-color: #ffb3b5;
}
</style>

<script type="text/javascript">
  var breadcrumbs = [
    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
    { label: "${ ui.message("Import VL Results")}" }
  ];
</script>

<div class="container" id="importVLR-app" ng-controller="ImportVLRController">

    <div ng-if="headerList && headerList.length != 0">
        <h3>${ ui.message("Viral Load Results") }</h3>
        <button type="button" class="confirm" ng-click="importAllVLR()">${ ui.message("Import All") }</button>
        <table id="list-vlr" cellspacing="0" cellpadding="2">
            <thead>
            <tr>
                <th ng-repeat="columnName in headerList">{{ columnName }}</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="vlr in vlrList" ng-style="{'background': !vlr.patientId ? '#ffb3b5' : ''}">
                <td>{{ vlr.artClinicNo }} </td>
                <td>
                    <a ng-show="vlr.patientId" href="{{dashboardPage}}{{vlr.patientId}}">
                        {{ vlr.identifier }}
                    </a>
                    <span ng-show="!vlr.patientId">{{ vlr.identifier }}</span>
                </td>
                <td>{{ vlr.facilityName }} </td>
                <td>{{ vlr.sex }} </td>
                <td>{{ vlr.dob }} </td>
                <td>{{ vlr.age }} </td>
                <td ng-if="vlr.collectionDate">{{ vlr.collectionDate }} </td>
                <td ng-if="vlr.reasonForTest">{{ vlr.reasonForTest }} </td>
                <td ng-if="vlr.dateOfReceiving">{{ vlr.dateOfReceiving }} </td>
                <td ng-if="vlr.dateOfTesting">{{ vlr.dateOfTesting }} </td>
                <td ng-if="vlr.result">{{ vlr.result }} </td>
                <td>
                    <button ng-disabled="!vlr.patientId" type="button" ng-click="importVLR(vlr, true)">${ ui.message("Import") }</button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <h1>${ ui.message("Select file that contains VL Results") }:</h1>
    <input type="file" on-read-file="showContent(fileContent)" />
    <br>


</div>

<script type="text/javascript">
  angular.module('importVLRApp');
  angular.bootstrap("#importVLR-app", [ "importVLRApp" ]);
  jq(function () {
    jq(document).on('sessionLocationChanged', function () {
      window.location.reload();
    });
  });
</script>