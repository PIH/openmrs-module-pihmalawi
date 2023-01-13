<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")


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

    #list-vlr {
        table-layout: auto;
        display: block;
        overflow-x: auto;
    }

    .spinner {
        position: absolute;
        left: 40%;
        top: 50%;
        height:160px;
        width:160px;
        margin:0px auto;
        -webkit-animation: rotation .6s infinite linear;
        -moz-animation: rotation .6s infinite linear;
        -o-animation: rotation .6s infinite linear;
        animation: rotation .6s infinite linear;
        border-left:6px solid rgba(0,174,239,.15);
        border-right:6px solid rgba(0,174,239,.15);
        border-bottom:6px solid rgba(0,174,239,.15);
        border-top:6px solid rgba(0,174,239,.8);
        border-radius:100%;
    }

    @-webkit-keyframes rotation {
        from {-webkit-transform: rotate(0deg);}
        to {-webkit-transform: rotate(359deg);}
    }
    @-moz-keyframes rotation {
        from {-moz-transform: rotate(0deg);}
        to {-moz-transform: rotate(359deg);}
    }
    @-o-keyframes rotation {
        from {-o-transform: rotate(0deg);}
        to {-o-transform: rotate(359deg);}
    }
    @keyframes rotation {
        from {transform: rotate(0deg);}
        to {transform: rotate(359deg);}
    }
</style>

<script type="text/javascript">
  var breadcrumbs = [
    { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
    { label: "${ ui.message("Import VL Results")}" }
  ];
</script>

<div class="container" id="importVLR-app" ng-controller="ImportVLRController">

    <div ng-if="processing" class="spinner"></div>
    <div ng-if="processing || (vlrList && vlrList.length > 0)">
        <h3>${ ui.message("Viral Load Results") }</h3>
        <button ng-disabled="processing" type="button" class="confirm ng-cloak" ng-click="importAllVLR()">
            <span ng-show="processing">
                ${ ui.message("Processing file") }
            </span>
            <span ng-show="!processing">
                ${ ui.message("Import All") }
            </span>
        </button>
        <br><br>
        <table id="list-vlr" cellspacing="0" cellpadding="2">
            <thead>
            <tr>
                <th ng-repeat="columnName in headerList">{{ columnName }}</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="vlr in vlrList" ng-style="{'background': (vlr.completed)  ? 'greenyellow' : ''}">
                <td>{{ vlr.rowNumber }} </td>
                <td>{{ vlr.labId }} </td>
                <td ng-style="{'background': (vlr.facilityName !== getLocationName(vlr))  ? '#ffb3b5' : ''}">{{ vlr.artClinicNo }} </td>
                <td ng-style="{'background': (!vlr.patientId && !processing) ? '#ffb3b5' : ''}">
                    <a ng-show="vlr.patientId" href="{{mastercardPage}}{{vlr.patientId}}">
                        {{ vlr.identifier }}
                    </a>
                    <span ng-show="!vlr.patientId">{{ vlr.identifier }}</span>
                </td>
                <td>{{ vlr.sex }} </td>
                <td>{{ vlr.dob ? displayDate(vlr.dob) : '' }} </td>
                <td>{{ vlr.age }} </td>
                <td ng-style="{'background': (!vlr.encounter && vlr.patientId)  ? '#ffb3b5' : ''}">{{ displayDate(vlr.collectionDate) }} </td>
                <td>{{ vlr.reasonForTest }} </td>
                <td ng-style="{'background': (vlr.emrResult && (vlr.emrResult != displayCsvResult(vlr.result)))  ? '#ffb3b5' : ''}">{{ vlr.emrResult }}</td>
                <td>{{ displayCsvResult(vlr.result) }} </td>
                <td>
                    <button ng-disabled="!(vlr.result) || !vlr.patientId || !vlr.encounter || (vlr.emrResult && (vlr.emrResult != displayCsvResult(vlr.result))) || (vlr.facilityName !== getLocationName(vlr)) || vlr.completed" type="button" ng-click="importVLR(vlr, true)">${ ui.message("Import") }</button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <div ng-if="vlrList == null || vlrList.length == 0">
        <h1>${ ui.message("Select file that contains VL Results") }:</h1>
        <input type="file" on-read-file="showContent(fileContent)" />
        <br>
    </div>


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
