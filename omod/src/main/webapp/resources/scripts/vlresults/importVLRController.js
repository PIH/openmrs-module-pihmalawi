angular.module('importVLRApp', ['ngDialog'])
  .service('ImportVLRService', ['$q', '$http',
    function($q, $http) {
      var CONSTANTS = {
        URLS: {
          PERSON: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/person",
          PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
          ENCOUNTER: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/encounter",
          PATIENT_ART_MASTERCARD: "/" + OPENMRS_CONTEXT_PATH + "/htmlformentryui/htmlform/flowsheet.page?headerForm=pihmalawi:htmlforms/art_mastercard.xml&flowsheets=pihmalawi:htmlforms/viral_load_test_results.xml&flowsheets=pihmalawi:htmlforms/art_visit.xml&dashboardUrl=legacyui&customizationProvider=pihmalawi&customizationFragment=mastercard&patientId="
        },
        VL_SCREENING_ENCOUNTER_TYPE: "9959A261-2122-4AE1-A89D-1CA444B712EA",
        VL_TEST_SET: "83931c6d-0e5a-4302-b8ce-a31175b6475e",
        HIV_VIRAL_LOAD: "654a7694-977f-11e1-8993-905e29aff6c1",
        REASON_FOR_TESTING: "164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        ROUTINE: "e0821812-955d-11e7-abc4-cec278b6b50a",
        CONFIRMED: "65590f06-977f-11e1-8993-905e29aff6c1",
        TARGET: "e0821df8-955d-11e7-abc4-cec278b6b50a",
        TRUE: "655e2f90-977f-11e1-8993-905e29aff6c1",
        LDL: "e97b36a2-16f5-11e6-b6ba-3e1d05defe78",
        LESS_THAN_LIMIT: "69e87644-5562-11e9-8647-d663bd873d93",
        PATIENT_CUSTOM_REP: "v=custom:(uuid,id,display,person:(uuid,display,gender,age,birthdate,birthdateEstimated,dead,deathDate,causeOfDeath))",
        PERSON_CUSTOM_REP: "v=custom:(uuid,personId,display,gender,age,birthdate,birthdateEstimated,names,addresses)",
        ENCOUNTER_CUSTOM_REP: "v=custom:(uuid,patient:(uuid),location:(uuid),encounterType:(uuid),encounterDatetime,voided,obs:(uuid,display,concept:(uuid),person:(uuid),obsDatetime,location:(uuid),encounter:(uuid),comment,valueCodedName:(uuid),groupMembers:(uuid,display,person:(uuid),concept:(uuid),obsDatetime,value,valueCodedName:(uuid),voided),voided,value:(uuid)))"
      };

      var LOCATIONS_MAP = new Map([
        [3701, { code: "CFGA", name: "Chifunga HC", uuid: "0d4166a0-5ab4-11e0-870c-9f6107fee88e", locationId: 3701 }],
        [3702, { code: "LGWE", name:"Ligowe HC", uuid: "0d417e38-5ab4-11e0-870c-9f6107fee88e", locationId: 3702 }],
        [3703, { code: "LSI", name: "Lisungwi Community Hospital", uuid: "0d416376-5ab4-11e0-870c-9f6107fee88e", locationId: 3703 }],
        [3704, { code: "LWAN", name: "Luwani", uuid: "0d416506-5ab4-11e0-870c-9f6107fee88e", locationId: 3704 }],
        [3705, { code: "MGT", name:"Magaleta", uuid: "0d414eae-5ab4-11e0-870c-9f6107fee88e", locationId: 3705 }],
        [3706, { code: "MTDN", name:"Matandani Health Center", uuid: "0d415200-5ab4-11e0-870c-9f6107fee88e", locationId: 3706 }],
        [3708, { code: "LSI", name:"Midzemba HC", uuid: "0d4182e8-5ab4-11e0-870c-9f6107fee88e", locationId: 3708 }],
        [3709, { code: "NNO", name: "Neno District Hospital", uuid: "0d414ce2-5ab4-11e0-870c-9f6107fee88e", locationId: 3709 }],
        [3710, { code: "NOP", name:"Neno Mission HC", uuid: "0d41505c-5ab4-11e0-870c-9f6107fee88e", locationId: 3710 }],
        [3711, { code: "NSM", name:"Nsambe HC", uuid: "0d416830-5ab4-11e0-870c-9f6107fee88e", locationId: 3711 }],
        [3712, { code: "NKA", name:"Nkhula", uuid: "0d4169b6-5ab4-11e0-870c-9f6107fee88e", locationId: 3712 }],
        [3713, { code: "ZLA", name:"Zalewa HC", uuid: "0d417fd2-5ab4-11e0-870c-9f6107fee88e", locationId: 3713 }],
        [3714, { code: "DAM", name: "Dambe Health Center", uuid: "976dcd06-c40e-4e2e-a0de-35a54c7a52ef", locationId: 3714 }]
      ]);

      this.CONSTANTS = CONSTANTS;
      this.LOCATIONS_MAPS = LOCATIONS_MAP;


      function buildVlResultObs(vlRecord) {
        var vlResultObs = {};
        if (vlRecord.result) {
          var value = parseInt(vlRecord.result);
          if (!isNaN(value)) {
            if (value == 1) {
              //LDL
              vlResultObs.concept = {
                "uuid": CONSTANTS.LDL
              };
              vlResultObs.value = CONSTANTS.TRUE;

            } else {
              vlResultObs.concept = {
                "uuid": CONSTANTS.HIV_VIRAL_LOAD
              };
              // this is a numeric value
              vlResultObs.value = value;
            }
          } else if(vlRecord.result.startsWith("<")){
            vlResultObs.concept = {
              "uuid": CONSTANTS.LESS_THAN_LIMIT
            };
            // LESS THAN LIMIT
            vlResultObs.value = vlRecord.result.substring(1);
          }
        }

        return vlResultObs;
      }

      function buildReasonForTestingObs(vlRecord) {
        var vlReasonForTestingObs = {
          concept: {
            uuid: CONSTANTS.REASON_FOR_TESTING
          }
        };
        if (vlRecord.reasonForTest) {
          if (vlRecord.reasonForTest.toUpperCase().includes("ROUTINE")) {
            vlReasonForTestingObs.value = CONSTANTS.ROUTINE;
          } else if (vlRecord.reasonForTest.toUpperCase().includes("TARGET")) {
            vlReasonForTestingObs.value = CONSTANTS.TARGET;
          }
        }
        return vlReasonForTestingObs;
      }

      function parseEncounter(encounter, vlResultObs, reasonForTestingObs) {

        var resultObs = angular.copy(vlResultObs);
        resultObs.uuid = null;
        var vlObs =  {
          "concept": CONSTANTS.VL_TEST_SET,
          "valueCodedName": null
        };
        if (encounter.obs && resultObs) {
          var vlResultAdded = false;
          for (var i=0; i < encounter.obs.length; i++) {
            var parentObs = encounter.obs[i];
            if (!parentObs.voided && parentObs.concept.uuid === CONSTANTS.VL_TEST_SET) {
              vlObs.uuid = parentObs.uuid;
              var members = [];
              var hasReasonForTesting = false;
              angular.forEach(parentObs.groupMembers, function (childObs) {
                var groupMember = {
                  "uuid": childObs.uuid,
                  "concept": childObs.concept
                };
                if (childObs.value) {
                  if (typeof childObs.value.uuid !== "undefined" && childObs.value.uuid) {
                    // this is because REST GET returns coded obs value like this:
                    // "value": {
                    //    "uuid": "655e2f90-977f-11e1-8993-905e29aff6c1"
                    // }
                    // BUT, obs REST POST accepts obs value like in the following format:
                    // "value": "655e2f90-977f-11e1-8993-905e29aff6c1";
                    groupMember.value = childObs.value.uuid;
                  } else {
                    // non-coded values(numeric/text) values are the same in GET and POST representation
                    groupMember.value = childObs.value;
                  }
                }
                if (groupMember.concept.uuid === CONSTANTS.REASON_FOR_TESTING && groupMember.value) {
                  // Reason for Testing has been entered during sample collection
                  hasReasonForTesting = true;
                }
                if (resultObs.concept.uuid == groupMember.concept.uuid) {
                  // this encounter already has a VL result, and we will update its value
                  resultObs.uuid = groupMember.uuid;
                  members.push(resultObs);
                  vlResultAdded = true;
                } else {
                  members.push(groupMember);
                }
              });
              if (!vlResultAdded) {
                members.push(resultObs);
              }
              if (!hasReasonForTesting) {
                members.push(reasonForTestingObs);
              }
              vlObs.groupMembers = members;
              break;
            }
          }
        }

        var updatedEncounter = {
          "encounterDatetime": encounter.encounterDatetime,
          "patient": encounter.patient,
          "location": encounter.location,
          "encounterType": encounter.encounterType,
          "voided": false,
          "voidReason": null,
          "obs": [vlObs]
        };
        return updatedEncounter;
      }

      function getRestDate(dateObj) {
        var returnDate = dateObj;
        var newDate = new Date(dateObj);

        if (newDate) {
          var dd = newDate.getDate();
          var mm = newDate.getMonth()+1;
          var yyyy = newDate.getFullYear();
          if(dd<10) {
            dd='0' + dd;
          }
          if(mm<10) {
            mm='0' + mm;
          }
          returnDate = yyyy + '-' +  mm + '-' + dd;
        }
        return returnDate;
      }

      this.getPatient = function(vlRecord) {
        return $http.get(CONSTANTS.URLS.PATIENT + "?q=" + vlRecord.identifier + "&" + CONSTANTS.PATIENT_CUSTOM_REP)
          .then(function (response) {
            if ( response.status == 200 ) {
              if (response.data && response.data.results && response.data.results.length == 1) {
                //only one patient found with this ID
                var patient = response.data.results[0];
                vlRecord.patientId = patient.id;
                vlRecord.patientUuid = patient.uuid;
              }
              return vlRecord;
            } else {
              return "";
            }
          }, function( error ) {
            console.log("failed to retrieve patient record with ID: " + vlRecord.identifier , error);
          });
      };

      this.getVLScreeningEncounter = function(vlRecord) {
        return $http.get(CONSTANTS.URLS.ENCOUNTER + "?patient=" + vlRecord.patientUuid
          + "&encounterType=" + CONSTANTS.VL_SCREENING_ENCOUNTER_TYPE
          + "&fromdate=" + getRestDate(vlRecord.collectionDate)
          + "&todate=" + getRestDate(vlRecord.collectionDate)
          + "&" + CONSTANTS.ENCOUNTER_CUSTOM_REP)
          .then(function (response) {
            if ( response.status == 200 ) {
              if (response.data && response.data.results && response.data.results.length > 0) {

                var encounter = response.data.results[0];
                vlRecord.encounter = encounter;
              }
              return vlRecord;
            } else {
              return "";
            }
          }, function( error ) {
            console.log("failed to retrieve VL screening encounter for patient: " + vlRecord.identifier , error);
          });
      };

      this.updateVLScreeningEncounter = function(vlRecord) {
        if (vlRecord.encounter && vlRecord.encounter.uuid) {
          var postUrl = CONSTANTS.URLS.ENCOUNTER + "/" + vlRecord.encounter.uuid;
          var vlResultObs = buildVlResultObs(vlRecord);
          var reasonForTestingObs = buildReasonForTestingObs(vlRecord);
          var updatedEncounter = parseEncounter(vlRecord.encounter, vlResultObs, reasonForTestingObs);
          return $http.post(postUrl , updatedEncounter)
            .then(function (response) {
              if (response.status == 200 && response.data) {
                  vlRecord.encounter = response.data;
                  vlRecord.completed = true;
              }
              return vlRecord;
            }, function (error) {
              console.log("failed to retrieve VL screening encounter for patient: " + vlRecord.identifier, error);
            });
        }
      };

    }])
  .controller('ImportVLRController', ['$q', '$scope', 'ImportVLRService', 'ngDialog',
    function($q, $scope, ImportVLRService, ngDialog) {

      $scope.content = null;
      $scope.mastercardPage = ImportVLRService.CONSTANTS.URLS.PATIENT_ART_MASTERCARD;
      $scope.errorMessage = null;
      $scope.vlrContent = null;
      $scope.headerList = [
        "ART Clinic No",
        "Identifier",
        "Facility Name",
        "Sex",
        "DOB",
        "Age",
        "Collection Date",
        "Reason for Test",
        "Result"
      ];
      $scope.pendingImportVLR = null;
      $scope.vlrList = [];
      $scope.processing = true;



      // This will parse a delimited string into an array of
      // arrays. The default delimiter is the comma, but this
      // can be overriden in the second argument.
      function CSVToArray (strData, strDelimiter) {

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

      function parsePatientIdentifier(clinicNumber) {
        var patientIdentifier = "";

        if (clinicNumber) {
          var clinicNo = clinicNumber.split("-", 2);
          if (clinicNo && clinicNo.length > 1) {
            var locationNode = ImportVLRService.LOCATIONS_MAPS.get(parseInt(clinicNo[0].trim()));
            if ( typeof locationNode !== 'undefined' && locationNode) {
              patientIdentifier = locationNode.code + " " + clinicNo[1].trim();
            } else {
              patientIdentifier = parseInt(clinicNo[0].trim()) + " not found";
            }
          }
        }
        return patientIdentifier;
      };

      function importVLResult(vlr) {
        var deferred = $q.defer();
        if (vlr.encounter && vlr.encounter.uuid) {
          ImportVLRService.updateVLScreeningEncounter(vlr).then(function(data) {
            deferred.resolve(data);
          });
        }

        return deferred.promise;
      }

      $scope.importVLR = function(vlr, showDialog){
        importVLResult(vlr);
      };

      function importAllResults() {
        var promises = [];
        if (angular.isDefined($scope.vlrList) && $scope.vlrList.length > 0) {
          angular.forEach($scope.vlrList, function(vlrObj) {
            if (typeof vlrObj.patientId !== 'undefined' && vlrObj.collectionDate && vlrObj.encounter && !vlrObj.completed) {
              promises.push(importVLResult(vlrObj));
            }
          });
        }
        return $q.all(promises);
      }

      $scope.importAllVLR = function(){
        $scope.processing = true;
        importAllResults().then(function(results) {
          console.log("number of results imported = " + results.length);
          $scope.processing = false;
        });

      };

      function getVlEncounters() {
        var promises = [];
        if (angular.isDefined($scope.vlrList) && $scope.vlrList.length > 0) {
          angular.forEach($scope.vlrList, function(vlrObj) {
            if (typeof vlrObj.patientUuid !== 'undefined' && vlrObj.patientUuid.length > 0
              && vlrObj.collectionDate) {
              promises.push(ImportVLRService.getVLScreeningEncounter(vlrObj));
            }
          });
        }
        return $q.all(promises);
      }

      function getPatientIdentifier() {
        var promises = [];

        if (angular.isDefined($scope.vlrList) && $scope.vlrList.length > 0) {
          angular.forEach($scope.vlrList, function(vlrObj) {
            var patientId = parsePatientIdentifier(vlrObj.artClinicNo);
            if (typeof patientId !== 'undefined' && patientId.length > 0) {
              vlrObj.identifier = patientId;
              promises.push(ImportVLRService.getPatient(vlrObj));
            }
          });
        }
        return $q.all(promises);
      }

      $scope.displayDate = function(dateObj) {
        var returnDate = dateObj;
        var newDate = new Date(dateObj);

        if (newDate) {
          var dd = newDate.getDate();
          var mm = newDate.getMonth()+1;
          var yyyy = newDate.getFullYear();
          if(dd<10) {
            dd='0' + dd;
          }
          if(mm<10) {
            mm='0' + mm;
          }
          returnDate = mm + '/' + dd + '/' + yyyy;
        }
        return returnDate;
      };

      $scope.showContent = function(fileContent){
        $scope.errorMessage = null;
        $scope.vlrContent = fileContent;
        $scope.content = $scope.vlrContent;
        var arrayOfData = CSVToArray($scope.vlrContent);
        if (arrayOfData.length > 0 ){
          //$scope.headerList = arrayOfData[0];
          $scope.pendingImportVLR = arrayOfData;
        }
        for (i = 0; i < $scope.pendingImportVLR.length; i++) {
          var vlrValues = $scope.pendingImportVLR[i];
          if (vlrValues.length >  24) {
            if ( vlrValues[0] ) {
              var vlrObj = {};
              vlrObj.facilityName = vlrValues[2];
              vlrObj.artClinicNo = vlrValues[7];
              vlrObj.identifier = "";
              vlrObj.sex = vlrValues[8];
              vlrObj.dob = Date.parse(vlrValues[9]+'T00:00:00');
              vlrObj.age = vlrValues[10];
              // by appending T00:00:00 we prevent the Date from displaying different based on the local timezone
              vlrObj.collectionDate = Date.parse(vlrValues[13]+'T00:00:00');
              vlrObj.reasonForTest = vlrValues[17];
              vlrObj.result = vlrValues[24].trim();
              vlrObj.personId = 0;
              $scope.vlrList.push(vlrObj);
            }
          }
        }
        getPatientIdentifier().then(function(data) {
          getVlEncounters().then(function(results) {
            $scope.processing = false;
          });
        });
      };

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
