angular.module('importVLRApp', ['ngDialog'])
  .service('ImportVLRService', ['$q', '$http',
    function($q, $http) {
      var CONSTANTS = {
        URLS: {
          PERSON: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/person",
          PATIENT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient",
          PROVIDER: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/provider",
          PROVIDER_ATTRIBUTE: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/provider/{UUID}/attribute",
          PROVIDERMANAGEMENT_ROLE: "/" + OPENMRS_CONTEXT_PATH + "/providermanagement/providerEdit/assignProviderRoleToPerson.action",
          IDENTIFIER_SOURCE: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/idgen/identifiersource",
          PROVIDER_PAGE: "/" + OPENMRS_CONTEXT_PATH + "/coreapps/providermanagement/editProvider.page?personId=",
        },
        PATIENT_CUSTOM_REP: "v=custom:(uuid,display,identifiers:(uuid,identifier,identifierType:(uuid),preferred),person:(uuid,display,gender,age,birthdate,birthdateEstimated,dead,deathDate,causeOfDeath,names,addresses,attributes))",
        PROVIDER_CUSTOM_REP: "v=custom:(uuid,identifier,display,person:(uuid,personId,display,gender,age,birthdate,birthdateEstimated,names,addresses))",
        PERSON_CUSTOM_REP: "v=custom:(uuid,personId,display,gender,age,birthdate,birthdateEstimated,names,addresses)",
        CHW_IDENTIFIER_SOURCE: "CHW Identifier Source"
      };

      var locationsMap = new Map([
        ["Neno District Hospital", {code: "NNO", uuid: "0d414ce2-5ab4-11e0-870c-9f6107fee88e"}],
        ["Chifunga", { code: "CFGA", uuid: "0d4166a0-5ab4-11e0-870c-9f6107fee88e" }],
        ["Dambe" , { code: "DAM", uuid: "976dcd06-c40e-4e2e-a0de-35a54c7a52ef" } ],
        ["Lisungwi" , { code: "LSI", uuid: "0d416376-5ab4-11e0-870c-9f6107fee88e" }],
        ["Luwani" , { code: "LWAN", uuid: "0d416506-5ab4-11e0-870c-9f6107fee88e" }],
        ["Nsambe" , { code: "NSM", uuid: "0d416830-5ab4-11e0-870c-9f6107fee88e" }],
        ["Ligowe" , { code: "LGWE", uuid: "0d417e38-5ab4-11e0-870c-9f6107fee88e" }],
        ["Matandani" , { code: "MTDN", uuid: "0d415200-5ab4-11e0-870c-9f6107fee88e" }],
        ["Magaleta" , { code: "MGT", uuid: "0d414eae-5ab4-11e0-870c-9f6107fee88e" }],
        ["Neno Parish" , { code: "NOP", uuid: "ca86238f-eab4-4c55-b244-2a8c82e86ecd" }],
        ["Matope" , { code: "MTE", uuid: "0d416b3c-5ab4-11e0-870c-9f6107fee88e" }],
        ["Zalewa" , { code: "ZLA", uuid: "0d417fd2-5ab4-11e0-870c-9f6107fee88e" }],
        ["Nkula" , { code: "NKA", uuid: "0d4169b6-5ab4-11e0-870c-9f6107fee88e" }],
        ["Midzemba" , { code: "MIHC", uuid: "0d4182e8-5ab4-11e0-870c-9f6107fee88e" }]
      ]);

      this.CONSTANTS = CONSTANTS;

      this.getIdentifierSource = function () {
        return $http.get(CONSTANTS.URLS.IDENTIFIER_SOURCE + "?v=full").then(function(resp) {
          var idSource= null;
          if (resp.status == 200) {
            for (j = 0; j < resp.data.results.length; j++){
              if (resp.data.results[0].name === CONSTANTS.CHW_IDENTIFIER_SOURCE) {
                idSource = resp.data.results[0].uuid;
                break;
              }
            }

          }
          return idSource;
        }, function (error) {
          console.log(JSON.stringify(error, undefined, 4));
        });
      };


      this.getPersonId = function (uuid) {
        return $http.get(CONSTANTS.URLS.PERSON + "/" + uuid + "?"
          + CONSTANTS.PERSON_CUSTOM_REP ).then(function(resp) {
          if (resp.status == 200) {
            return resp.data;
          } else {
            return null;
          }
        }, function (error) {
          console.log(JSON.stringify(error, undefined, 4));
        });
      };

    }])
  .controller('ImportVLRController', ['$q', '$scope', 'ImportVLRService', 'ngDialog',
    function($q, $scope, ImportVLRService, ngDialog) {

      $scope.content = null;
      $scope.providerPage = ImportVLRService.CONSTANTS.URLS.PROVIDER_PAGE;
      $scope.errorMessage = null;
      $scope.vlrContent = null;
      $scope.headerList = null;
      $scope.pendingImportVLR = null;
      $scope.vlrList = [];



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

      function importVLResult(vlr) {
      }

      $scope.importVLR = function(vlr, showDialog){
        importVLResult(vlr);
      };

      $scope.importAllVLR = function(chw, showDialog){

      };

      $scope.goToProviderPage = function(chw) {
        if (chw.personId) {
          window.location.href = $scope.providerPage + chw.personId;
        }
      };

      $scope.showContent = function(fileContent){
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
