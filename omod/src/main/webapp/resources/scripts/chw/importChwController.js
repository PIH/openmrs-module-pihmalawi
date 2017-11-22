angular.module('importChwApp', ['ngDialog'])
    .service('ImportChwService', ['$q', '$http',
        function($q, $http) {
            var CONSTANTS = {
                URLS: {
                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                    VISIT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/visit",
                    ENCOUNTER: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/encounter",
                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs"
                }
            };
        }])
    .controller('ImportChwController', ['$q', '$scope', 'ImportChwService', 'ngDialog',
        function($q, $scope, ImportChwService, ngDialog) {

            $scope.content = null;
            $scope.errorMessage = null;
            $scope.chwContent = null;


            $scope.showContent = function(fileContent){
                $scope.errorMessage = null;
                $scope.chwContent = fileContent;
                $scope.content = $scope.chwContent;
            };

            $scope.importChwFile = function() {
                if ($scope.chwContent) {
                    console.log("Import CHWs");
                }
            }
        }])
    .directive('onReadFile', function ($parse) {
        return {
            restrict: 'A',
            scope: false,
            link: function(scope, element, attrs) {
                var fn = $parse(attrs.onReadFile);

                element.on('change', function(onChangeEvent) {
                    var reader = new FileReader();

                    reader.onload = function(onLoadEvent) {
                        scope.$apply(function() {
                            fn(scope, {fileContent:onLoadEvent.target.result});
                        });
                    };
                    reader.readAsText((onChangeEvent.srcElement || onChangeEvent.target).files[0]);
                });
            }
        }
    });