angular.module('mastercard', [ 'ui.bootstrap' ])

    .controller('MastercardCtrl', [ '$scope', '$http', function($scope, $http) {

        $scope.patientId = null;
        $scope.headerForm = null;
        $scope.visitForm = null;

        $scope.init = function(patientId, headerForm, visitForm) {
            $scope.patientId = patientId;
            $scope.headerForm = headerForm;
            $scope.visitForm = visitForm;
        };

        $scope.printForm = function() {
            window.print();
        }

        $scope.loadHtmlFormForEncounter = function(sectionId, encounterId, mode, section) {

            var providerName = 'htmlformentryui';
            var fragmentName = (mode == 'edit' ? 'htmlform/enterHtmlForm' : 'htmlform/viewEncounterWithHtmlForm');
            var resourceName = 'pihmalawi:htmlforms/' + (section == 'header' ? $scope.headerForm : $scope.visitForm) + '.xml';

            emr.getFragmentActionWithCallback(providerName, fragmentName, "getAsHtml", {"encounterId": encounterId, "definitionUiResource": resourceName}, function(result) {
                jq('#'+sectionId).html(result.html);
            });

        };

    }])
