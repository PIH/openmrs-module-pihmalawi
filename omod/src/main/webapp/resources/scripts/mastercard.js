angular.module('mastercard', [ 'ui.bootstrap' ])

    .controller('MastercardCtrl', [ '$scope', '$http', function($scope, $http) {

        $scope.patientId = null;
        $scope.encounterId = null;
        $scope.mode = null;
        $scope.editingHeader = false;

        $scope.init = function(patientId, encounterId, mode) {
            $scope.patientId = patientId;
            $scope.encounterId = encounterId;
            $scope.mode = mode;
            $scope.editingHeader = (mode == 'editHeader');
        };

        $scope.printForm = function() {
            window.print();
        }

        $scope.reloadPageInNewMode = function(mode) {
            emr.navigateTo({"provider": "pihmalawi", "page": "mastercard", "query": {
                "patientId": $scope.patientId,
                "encounterId": $scope.encounterId,
                "mode": mode
            }});
        }

    }])
