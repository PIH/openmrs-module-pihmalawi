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
            $scope.headerList = null;
            $scope.pendingImportChws = null;


            // This will parse a delimited string into an array of
            // arrays. The default delimiter is the comma, but this
            // can be overriden in the second argument.
            $scope.CSVToArray = function(strData, strDelimiter) {

                            // Check to see if the delimiter is defined. If not,
                            // then default to comma.
                                            strDelimiter = (strDelimiter || ",");
                            // Create a regular expression to parse the CSV values.
                                            var objPattern = new RegExp(
                                                (
                            // Delimiters.
                                                    "(\\" + strDelimiter + "|\\r?\\n|\\r|^)" +
                            // Quoted fields.
                                                    "(?:\"([^\"]*(?:\"\"[^\"]*)*)\"|" +
                            // Standard fields.
                                                    "([^\"\\" + strDelimiter + "\\r\\n]*))"
                                                ),
                                                "gi"
                                            );
                            // Create an array to hold our data. Give the array
                            // a default empty first row.
                                            var arrData = [[]];
                            // Create an array to hold our individual pattern
                            // matching groups.
                                            var arrMatches = null;
                            // Keep looping over the regular expression matches
                            // until we can no longer find a match.
                                            while (arrMatches = objPattern.exec( strData )){
                            // Get the delimiter that was found.
                                                var strMatchedDelimiter = arrMatches[ 1 ];
                            // Check to see if the given delimiter has a length
                            // (is not the start of string) and if it matches
                            // field delimiter. If id does not, then we know
                            // that this delimiter is a row delimiter.
                                                if (
                                                    strMatchedDelimiter.length &&
                                                    (strMatchedDelimiter != strDelimiter)
                                                ){
                            // Since we have reached a new row of data,
                            // add an empty row to our data array.
                                                    arrData.push( [] );
                                                }
                            // Now that we have our delimiter out of the way,
                            // let's check to see which kind of value we
                            // captured (quoted or unquoted).
                                                if (arrMatches[ 2 ]){
                            // We found a quoted value. When we capture
                            // this value, unescape any double quotes.
                        var strMatchedValue = arrMatches[ 2 ].replace(
                            new RegExp( "\"\"", "g" ),
                            "\""
                        );
                    } else {
                        // We found a non-quoted value.
                        var strMatchedValue = arrMatches[ 3 ];
                    }
                        // Now that we have our value string, let's add
                        // it to the data array.
                        arrData[ arrData.length - 1 ].push( strMatchedValue );
                }
                    // Return the parsed data.
                    return( arrData );
            }


            $scope.showContent = function(fileContent){
                $scope.errorMessage = null;
                $scope.chwContent = fileContent;
                $scope.content = $scope.chwContent;
                var arrayOfData = $scope.CSVToArray($scope.chwContent);
                if (arrayOfData.length > 0 ){
                    $scope.headerList = arrayOfData[0];
                    $scope.pendingImportChws = arrayOfData.slice(1);
                }
                angular.forEach(arrayOfData, function(providerRecord) {
                    console.log(providerRecord);
                });
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