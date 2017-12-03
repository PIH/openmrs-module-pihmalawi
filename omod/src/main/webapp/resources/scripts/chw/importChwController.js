angular.module('importChwApp', ['ngDialog'])
    .service('ImportChwService', ['$q', '$http',
        function($q, $http) {
            var CONSTANTS = {
                URLS: {
                    PERSON: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/person",
                    PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
                    PROVIDER: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/provider",
                    IDENTIFIER_SOURCE: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/idgen/identifiersource"
                },
                PATIENT_CUSTOM_REP: "v=custom:(uuid,display,identifiers:(uuid,identifier,identifierType:(uuid),preferred),person:(uuid,display,gender,age,birthdate,birthdateEstimated,dead,deathDate,causeOfDeath,names,addresses,attributes))",
                PROVIDER_CUSTOM_REP: "?v=custom:(uuid,identifier,display,person:(uuid,personId,display,gender,age,birthdate,birthdateEstimated,names,addresses))"
            };

            var locationsMap = new Map([
                ["Neno District Hospital", "NNO"],
                ["Chifunga", "CFGA"],
                ["Dambe" , "DMB"],
                ["Lisungwi" , "LSI"]]);

            this.getNextIdentifier = function (chw) {

                var generateIdentifiers = {
                    generateIdentifiers: true,
                    comment: "new CHW ID",
                    numberToGenerate: 1,
                    sourceUuid: "bda36c8c-8fe4-40fa-9ce4-ea151bb39c7d"
                };

                return $http.post(CONSTANTS.URLS.IDENTIFIER_SOURCE, generateIdentifiers).then(function(resp) {
                    if (resp.status == 201) {
                        // identifier generated
                        if (resp.data && resp.data.identifiers && resp.data.identifiers.length > 0) {
                            // one identifier has been generated
                            return locationsMap.get(chw.healthCenter) + " " + resp.data.identifiers[0];
                        }
                        return null;
                    } else {
                        return null;
                    }
                }, function (error) {
                    console.log("failed to generate identifier: " + JSON.stringify(error, undefined, 4));
                });

            };

            this.getProvider = function (chw) {

                return $http.get(CONSTANTS.URLS.PROVIDER + "?" 
                    + CONSTANTS.PROVIDER_CUSTOM_REP + '&q=' 
                    + chw.firstName + " " + chw.lastName).then(function(resp) {
                    if (resp.status == 200) {
                        return resp.data;
                    } else {
                        return null;
                    }
                }, function (error) {
                    console.log(JSON.stringify(error, undefined, 4));
                });
            };
            
            this.createPerson = function (chw) {
                var person = {
                    gender: chw.gender,
                    //age: chw.age, not reliable information at this time
                    names: [{
                        givenName: chw.firstName,
                        familyName: chw.lastName
                    }],
                    addresses: [{
                        preferred: true,
                        stateProvince: chw.district,
                        countyDistrict: chw.ta,
                        cityVillage: chw.village
                    }]
                };

                return $http.post(CONSTANTS.URLS.PERSON, person).then(function(resp) {
                    if (resp.status == 201) {
                        // person created
                        return resp.data;
                    } else {
                        return null;
                    }
                }, function (error) {
                    console.log("failed to create person: " + chw.firstName + " " + chw.lastName + JSON.stringify(error, undefined, 4));
                });
            };

            this.createProvider = function (provider) {

                return $http.post(CONSTANTS.URLS.PROVIDER, provider).then(function(resp) {
                    if (resp.status == 201) {
                        // provider created
                        return resp.data;
                    } else {
                        return null;
                    }
                }, function (error) {
                    console.log("failed to create provider: " + provider.person.display + JSON.stringify(error, undefined, 4));
                });
            };

        }])
    .controller('ImportChwController', ['$q', '$scope', 'ImportChwService', 'ngDialog',
        function($q, $scope, ImportChwService, ngDialog) {

            $scope.content = null;
            $scope.errorMessage = null;
            $scope.chwContent = null;
            $scope.headerList = null;
            $scope.pendingImportChws = null;
            $scope.chwList = [];



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
            };

            $scope.importChw = function(chw, showDialog){

                var newProvider ={};
                newProvider.person = null;
                newProvider.identifier = null;

                ImportChwService.getProvider(chw).then( function (provider) {
                    console.log("provider = " + provider.results.length);
                    if (provider.results.length == 0 ) {
                        //no provider found
                        // create person
                        ImportChwService.createPerson(chw).then( function (person) {
                            console.log("person created: " + person);
                            if (person) {
                                newProvider.person = person.uuid;
                            }

                            ImportChwService.getNextIdentifier(chw).then( function (identifier) {
                                console.log("identifier = " + identifier);
                                if (identifier ) {
                                    //create provider record
                                    newProvider.identifier = identifier;
                                    ImportChwService.createProvider(newProvider).then( function (respProvider) {
                                        if (respProvider) {
                                            console.log("newProvider has been created = " + respProvider);
                                        }
                                    }, function (providerError) {
                                        console.log("failed to create provider record");
                                    });
                                } else {
                                    console.log("failed to generate identifier");
                                }
                            }, function(identifierError) {
                                console.log("failed to generate Identifier");
                            } );
                        }, function (personError) {
                            console.log("failed to create person");
                        })
                    } else {
                        console.log("provider already present in the system");
                    }
                }, function (error) {
                    console.log("failed to find provider");
                });
            };

            $scope.showContent = function(fileContent){
                $scope.errorMessage = null;
                $scope.chwContent = fileContent;
                $scope.content = $scope.chwContent;
                var arrayOfData = $scope.CSVToArray($scope.chwContent);
                if (arrayOfData.length > 0 ){
                    $scope.headerList = arrayOfData[0];
                    $scope.pendingImportChws = arrayOfData.slice(1);
                }
                for (i = 0; i < $scope.pendingImportChws.length; i++) {
                    var chwValues = $scope.pendingImportChws[i];
                    if (chwValues.length ==  11) {
                        var chwObj = {};

                        chwObj.id = chwValues[0];
                        chwObj.firstName = chwValues[1];
                        chwObj.lastName = chwValues[2];
                        chwObj.gender = chwValues[3];
                        chwObj.age = chwValues[4];
                        chwObj.healthCenter = chwValues[5];
                        chwObj.district = chwValues[6];
                        chwObj.ta = chwValues[7];
                        chwObj.gvh = chwValues[8];
                        chwObj.village = chwValues[9];
                        chwObj.seniorChw = chwValues[10];

                        $scope.chwList.push(chwObj);
                    }
                }
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