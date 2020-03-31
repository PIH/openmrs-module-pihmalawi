angular.module('importVLRApp', ['ngDialog'])
  .service('ImportVLRService', ['$q', '$http',
    function($q, $http) {
      var CONSTANTS = {
        URLS: {
          PERSON: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/person",
          PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
          ENCOUNTER: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/encounter",
          PATIENT_DASHBOARD_PAGE: "/" + OPENMRS_CONTEXT_PATH + "/patientDashboard.form?patientId="
        },
        VL_SCREENING_ENCOUNTER_TYPE: "9959A261-2122-4AE1-A89D-1CA444B712EA",
        PATIENT_CUSTOM_REP: "v=custom:(uuid,id,display,person:(uuid,display,gender,age,birthdate,birthdateEstimated,dead,deathDate,causeOfDeath))",
        PERSON_CUSTOM_REP: "v=custom:(uuid,personId,display,gender,age,birthdate,birthdateEstimated,names,addresses)",
        ENCOUNTER_CUSTOM_REP: "v=custom:(uuid,patient:(uuid),location:(uuid),encounterType:(uuid),encounterDatetime,voided,obs:(uuid,display,concept:(uuid,display),person:(uuid),obsDatetime,location:(uuid),encounter:(uuid),comment,valueCodedName:(uuid),groupMembers:(uuid,display,person:(uuid),concept:(uuid,display),obsDatetime,value,valueCodedName:(uuid),voided),voided,value:(uuid)))"
      };

      var LOCATIONS_MAP = new Map([
        [3704 , { code: "LWAN", name: "Luwani", uuid: "0d416506-5ab4-11e0-870c-9f6107fee88e", locationId: 3704 }],
        [3705 , { code: "MGT", name:"Magaleta", uuid: "0d414eae-5ab4-11e0-870c-9f6107fee88e", locationId: 3705 }],
        [3709, {code: "NNO", name: "Neno District Hospital", uuid: "0d414ce2-5ab4-11e0-870c-9f6107fee88e", locationId: 3709 }]
      ]);

      this.CONSTANTS = CONSTANTS;
      this.LOCATIONS_MAPS = LOCATIONS_MAP;

      this.getPatient = function(vlrRecord) {
        return $http.get(CONSTANTS.URLS.PATIENT + "?q=" + vlrRecord.identifier + "&" + CONSTANTS.PATIENT_CUSTOM_REP)
          .then(function (response) {
            if ( response.status == 200 ) {
              if (response.data && response.data.results && response.data.results.length == 1) {
                //only one patient found with this ID
                var patient = response.data.results[0];
                vlrRecord.patientId = patient.id;
                vlrRecord.patientUuid = patient.uuid;
              }
              return vlrRecord;
            } else {
              return "";
            }
          }, function( error ) {
            console.log("failed to retrieve patient record with ID: " + vlrRecord.identifier , error);
          });
      };

      this.getVLScreeningEncounter = function(vlRecord) {
        return $http.get(CONSTANTS.URLS.ENCOUNTER + "?patient=" + vlRecord.patientUuid
          + "&encounterType=" + CONSTANTS.VL_SCREENING_ENCOUNTER_TYPE
          + "&fromdate=" + vlRecord.collectionDate
          + "&todate=" + vlRecord.collectionDate
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
            console.log("failed to retrieve VL screening encounter for patient: " + vlrRecord.identifier , error);
          });
      };

    }])
  .controller('ImportVLRController', ['$q', '$scope', 'ImportVLRService', 'ngDialog',
    function($q, $scope, ImportVLRService, ngDialog) {

      $scope.content = null;
      $scope.dashboardPage = ImportVLRService.CONSTANTS.URLS.PATIENT_DASHBOARD_PAGE;
      $scope.errorMessage = null;
      $scope.vlrContent = null;
      $scope.headerList = null;
      $scope.pendingImportVLR = null;
      $scope.vlrList = [];
      $scope.processing = false;



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
            }
          }
        }
        return patientIdentifier;
      };

      function importVLResult(vlr) {
      }

      $scope.importVLR = function(vlr, showDialog){
        importVLResult(vlr);
      };

      $scope.importAllVLR = function(chw, showDialog){

      };

      function getVlEncounters() {
        var promises = [];
        if (angular.isDefined($scope.vlrList) && $scope.vlrList.length > 0) {
          angular.forEach($scope.vlrList, function(vlrObj) {
            if (typeof vlrObj.patientUuid !== 'undefined' && vlrObj.patientUuid.length > 0
              && vlrObj.collectionDate.length > 0) {
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
      };

      $scope.showContent = function(fileContent){
        $scope.processing = true;
        $scope.errorMessage = null;
        $scope.vlrContent = fileContent;
        $scope.content = $scope.vlrContent;
        var arrayOfData = CSVToArray($scope.vlrContent);
        if (arrayOfData.length > 0 ){
          $scope.headerList = arrayOfData[0];
          $scope.pendingImportVLR = arrayOfData.slice(1);
        }
        for (i = 0; i < $scope.pendingImportVLR.length; i++) {
          var vlrValues = $scope.pendingImportVLR[i];
          if (vlrValues.length ==  11) {
            if ( vlrValues[0] ) {
              var vlrObj = {};
              vlrObj.artClinicNo = vlrValues[0];
              vlrObj.identifier = vlrValues[1];
              vlrObj.facilityName = vlrValues[2];
              vlrObj.sex = vlrValues[3];
              vlrObj.dob = vlrValues[4];
              vlrObj.age = vlrValues[5];
              vlrObj.collectionDate = vlrValues[6];
              vlrObj.reasonForTest = vlrValues[7];
              vlrObj.dateOfReceiving = vlrValues[8];
              vlrObj.dateOfTesting = vlrValues[9];
              vlrObj.result = vlrValues[10];
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
