angular.module('mastercard', [ 'ui.bootstrap' ])

    .controller('MastercardCtrl', [ '$scope', '$http', function($scope, $http) {

        $scope.patientId = null;
        $scope.mode = null;
        $scope.headerForm = null;
        $scope.visitForm = null;
        $scope.editingHeader = false;

        $scope.init = function(patientId, mode, headerForm, visitForm) {
            $scope.patientId = patientId;
            $scope.mode = mode;
            $scope.headerForm = headerForm;
            $scope.visitForm = visitForm;
            $scope.editingHeader = (mode == 'editHeader');
        };

        $scope.printForm = function() {
            window.print();
        }

        $scope.reloadPageInNewMode = function(mode) {
            emr.navigateTo({"provider": "pihmalawi", "page": "mastercard", "query": {
                "patientId": $scope.patientId,
                "mode": mode,
                "headerForm": $scope.headerForm,
                "visitForm": $scope.visitForm
            }});
        }

    }])
