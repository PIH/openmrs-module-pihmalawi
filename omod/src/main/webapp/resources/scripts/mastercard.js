//Uses the namespace pattern from http://stackoverflow.com/a/5947280
// expects to extend htmlForm defined in the core HFE module
(function( flowsheet, jq, undefined) {

    console.log("Adding extensions to flowsheet");

    var fsExt = {};

    fsExt.heightMap = null;

    fsExt.afterShowVisitTable = function(flowsheet) {
        if (typeof fsExt.heightMap !== "undefined" && fsExt.heightMap != null) {
            flowsheet.clearErrorMessage();
            jq(".visit-table").find(".td-error").removeClass("td-error");
            for (var key in fsExt.heightMap) {
                if (fsExt.heightMap.hasOwnProperty(key)) {
                    var heightArray = fsExt.heightMap[key];
                    if (heightArray) {
                        var values = fsExt.extractHeightValues(heightArray);
                        for (i = 0; i < heightArray.length; i++) {
                            var heightInfo = heightArray[i];
                            if ( fsExt.isHeightOutsideOfStandardDeviation(values, heightInfo.height) )  {
                                // this height obs looks "abnormal"
                                var encounterId = heightInfo.encounterId;
                                jq("#visit-table-row-" + encounterId).find("#heightEntered").closest('td').addClass("td-error");
                                var error = 'Please check height for ' + heightInfo.encounterDateTime.format("MMM Do YYYY");
                                console.log(error);
                                flowsheet.showErrorMessage(error);
                            }
                        }
                    }
                }
            }
        }
    };

    fsExt.beforeLoadVisitTable = function(flowsheet) {
        fsExt.heightMap = new Object();
    }

    fsExt.beforeLoadVisitRow = function(flowsheet, formName, encounterId, data) {

        var newRow = jq(data).find(".visit-table-row");
        var newVisitMoment = flowsheet.extractVisitMoment(newRow);
        var heightField = jq(data).find("#heightEntered :first-child");

        if (fsExt.heightMap == null) {
            fsExt.heightMap = new Object();
        }
        if (heightField) {
            var currentHeight = parseInt(heightField.text(), 10);
            if (currentHeight > 0) {
                var heightInfo = null;
                var formHeight = fsExt.heightMap[formName];
                if (formHeight == null) {
                    formHeight = [];
                }
                else {
                    heightInfo = findEncounterInArray(formHeight, encounterId);
                }
                if (heightInfo == null) {
                    heightInfo = new Object();
                    heightInfo.encounterId = encounterId;
                    formHeight.push(heightInfo);
                }
                heightInfo.encounterDateTime = newVisitMoment;
                heightInfo.height = currentHeight;

                fsExt.heightMap[formName] = formHeight;
            }
        }

        function findEncounterInArray(array, encounterId) {
            var item = null;
            if (array && array.length > 0 ) {
                for (var i = 0; i < array.length; i++) {
                    if (array[i].encounterId == encounterId ) {
                        item = array[i];
                        break;
                    }
                }
            }
            return item;
        }
    };

    fsExt.afterSetupForm = function(flowsheet, html) {

        setupLocationDefaults(flowsheet, html);
        setupAppointmentDateValidation(flowsheet, html);
        setupHbA1cValidation(flowsheet, html);
        setupHeightAndWeightValidation(flowsheet, html);
        setupHtnDmValidation(flowsheet, html);
        setupChronicCareDiagnosisValidation(flowsheet, html);
        setupViralLoadValidation(flowsheet, html);

        function setupLocationDefaults(flowsheet, html) {

            // Configure defaults we want to apply across any suitable form
            var locationInput = jq(html).find("#visitLocation").children().first();
            if (!locationInput.val() || locationInput.val().length == 0) {
                locationInput.val(flowsheet.getDefaultLocationId());
            }
            // set flowsheet default location
            locationInput = jq(html).find("#flowsheetLocation").children().first();
            if (!locationInput.val() || locationInput.val().length == 0) {
                locationInput.val(flowsheet.getDefaultLocationId());
            }

        }



        function setupAppointmentDateValidation(flowsheet, html) {

            // Configure validations we want to apply across any suitable form
            var apptDateInput = jq(html).find("#appointmentDate").children("input[type=hidden]").first();
            var visitDateInput = jq(html).find("#visitDate").children("input[type=hidden]").first();
            if (apptDateInput && visitDateInput) {
                validateAppointmentDate(apptDateInput, visitDateInput);
                apptDateInput.change(function () {
                    validateAppointmentDate(apptDateInput, visitDateInput);
                });
                visitDateInput.change(function () {
                    validateAppointmentDate(apptDateInput, visitDateInput);
                });
            }

            function validateAppointmentDate(apptDateField, visitDateField) {
                var apptMoment = fsExt.parseDateFromHiddenInput(apptDateField);
                var visitMoment = fsExt.parseDateFromHiddenInput(visitDateField);
                var err = null;
                if (apptMoment && visitMoment) {
                    var monthDiff = apptMoment.diff(visitMoment, 'months', true);
                    if (monthDiff < 0) {
                        err = 'Cannot be in the past';
                    }
                    else if (monthDiff > 12) {
                        err = 'Must be within 12 months of visit date';
                    }
                }
                else if (apptDateField[0] && !apptDateField.parent().is(':hidden') && !apptMoment) {  // bit of a hack, if the field is parent hidden, we assume there is a provided reference value
                    err = "Required"
                }
                return flowsheet.toggleError(apptDateField, err)
            }
        }

      function setupHbA1cValidation(flowsheet, html) {
        var hba1cEntered = jq(html).find("#hba1c > input");
        if (hba1cEntered) {
            validateHbA1cValue(hba1cEntered);
            hba1cEntered.change(function() {
                validateHbA1cValue(hba1cEntered);
            });
        }

        function validateHbA1cValue(hba1cField) {
          var err = null;
          if (hba1cField.length > 0) {
              // if the field is not empty
              var hba1cValue = hba1cField[0].value;
              var hba1cIntValue = parseInt(hba1cValue);
              if (isNaN(hba1cIntValue) || hba1cIntValue < 2 || hba1cIntValue > 16) {
                err = "HbA1C must be a Number greater than 1 and less than 17"
              }
          }
          return flowsheet.toggleError(hba1cField, err);
        }
      }

        function setupHeightAndWeightValidation(flowsheet, html) {

            var heightInput = jq(html).find("#heightInput :first-child");
            var weightInput = jq(html).find("#weightInput :first-child");
            var visitDateInput = jq(html).find("#visitDate").children("input[type=hidden]").first();

            if (heightInput) {
                validateHeight(heightInput, visitDateInput);
                heightInput.change(function () {
                    event.stopImmediatePropagation();
                    validateHeightAndWeight(heightInput, weightInput);
                    validateHeight(heightInput, visitDateInput);
                });
                heightInput.blur(function (event) {
                    event.stopImmediatePropagation();
                    validateHeight(heightInput, visitDateInput);
                });
            }
            if (weightInput) {
                weightInput.change(function () {
                    event.stopImmediatePropagation();
                    validateHeightAndWeight(heightInput, weightInput);
                });
            }
        }

        function validateHeight(heightField, visitDateField) {
            var patientHeight = parseInt(heightField.val());
            if ( patientHeight ) {
                var validationError = null;
                var visitMoment = fsExt.parseDateFromHiddenInput(visitDateField);
                var patientBirthdate = jq("#patientBirthdate");
                var birthdateMoment = null;
                var ageAtVisitTime = null;
                if (patientBirthdate) {
                    birthdateMoment = moment(patientBirthdate.text(), "DD/MMM/YYYY");
                }
                if (birthdateMoment) {
                    ageAtVisitTime = visitMoment.diff(birthdateMoment, 'years');
                }
                if (ageAtVisitTime >= 20) {
                    validationError = validateAdultHeight(patientHeight);
                } else {
                    validationError = validateChildHeight(patientHeight, visitMoment);
                }
                flowsheet.clearErrorMessage();
                if (validationError) {
                    var errorField = heightField.siblings(".field-error");
                    if (errorField) {
                        errorField.show();
                        errorField.text(validationError);
                    }
                    flowsheet.showErrorMessage(validationError);
                    heightField.className = 'illegalValue';
                }
            }
        }

        function validateAdultHeight(patientHeight) {
            var error = null;
            if ( patientHeight > 0 && fsExt.heightMap) {
                if (flowsheet.getCurrentlyEditingFormName() ) {
                    var heightArray = fsExt.heightMap[flowsheet.getCurrentlyEditingFormName()];
                    if (heightArray) {
                        var values = fsExt.extractHeightValues(heightArray);
                        if (fsExt.isHeightOutsideOfStandardDeviation(values, patientHeight)) {
                            // the patient height for this visit looks "abnormal"
                            error = 'Adult height is an outlier please check!';
                        }
                    }
                }
            }
            return error;
        }

        function validateChildHeight(patientHeight, visitMoment) {
            var error = null;
            if ( patientHeight > 0 && fsExt.heightMap) {
                if (flowsheet.getCurrentlyEditingFormName() ) {
                    var heightArray = fsExt.heightMap[flowsheet.getCurrentlyEditingFormName()];
                    if (heightArray) {
                        heightArray.sort(function compare(a,b) {
                            if ( a.encounterDateTime.isBefore(b.encounterDateTime) ) {
                                return -1;
                            }
                            else if ( a.encounterDateTime.isAfter(b.encounterDateTime) ) {
                                return 1;
                            }
                            else {
                                return 0;
                            }
                        });
                        for (i = 0; i < heightArray.length; i++) {
                            var heightInfo = heightArray[i];
                            if ( visitMoment.isAfter(heightInfo.encounterDateTime) ) {
                                if ( patientHeight < heightInfo.height ) {
                                    // the child height cannot have a smaller value than a previous visit
                                    error = 'Child height should not decrease overtime. On a previous visit ('
                                        + heightInfo.encounterDateTime.format("MMM Do YY")
                                        + ") we had a recorded height of: " + heightInfo.height + " cm";
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return error;
        }

        function validateHeightAndWeight(heightField, weightField) {
            var error = null;
            var patientHeight = parseInt(heightField.val());
            var patientWeight = parseInt(weightField.val());
            var errorField = heightField.siblings(".field-error");
            if (errorField) {
                errorField.text("");
            }
            flowsheet.clearErrorMessage();
            if ( patientHeight > 0 && patientWeight > 0 && patientHeight < patientWeight) {
                error = "The height should be larger than the weight";
                if (errorField) {
                    errorField.show();
                    errorField.text(error);
                }
                flowsheet.showErrorMessage(error);
            }
            return error;
        }

        function setupChronicCareDiagnosisValidation (flowsheet, html) {
            // function finds all checkbox diagnosis items in the dx-checkbox-item
            // class and activates toggleError if none are checked

            // get array of checkbox items
            var diagnosisDict = [];
            $('.dx-checkbox-item:visible').each(function (i, el) {
                var dxItem = $(el).find(':checkbox');
                diagnosisDict.push(dxItem);
            });

            // Only do validation if there are checkbox diagnosis items
            if (diagnosisDict.length > 0) {
                // check if any are checked on page load
                ensureDiagnosisChecked(diagnosisDict[0], diagnosisDict);
                // check if any are checked anytime any element changes
                for (i = 0; i < diagnosisDict.length; i++) {
                    var dxElement = diagnosisDict[i];
                    dxElement.change( function() {
                        ensureDiagnosisChecked(diagnosisDict[0], diagnosisDict);
                    });
                }
            }
        }

        function setupHtnDmValidation(flowsheet, html) {
            // put in for HTN DM Lab form
            // If an HIV Result is clicked - set a hidden HIV Test Date
            // to the encounter date.
            jq(html).find("#hivTestDate > input[type=text]").remove(); // make hiv sample date hidden
            jq(html).find("#hivRadioResult > input").click(function () {
                hivTestDate = jq(html).find("#visitDate > input[type=hidden]").val(); // get visit date
                jq(html).find("#hivTestDate > input[type=hidden]").val(hivTestDate); // set sample date to visit date
            });

            // Allow user to select either type 1 or type 2 only for diabetes
            if ($('#diabetes-type-1-dx :checkbox').is(':checked') || $('#diabetes-type-2-dx :checkbox').is(':checked')) {
                validateDiabetesDiagnosisType();
            }

            $("#diabetes-type-1-dx").change(function () {
                if ($('#diabetes-type-1-dx :checkbox').is(':checked')) {
                    $("#diabetes-type-2-dx").find(":input").prop('disabled', true);
                } else {
                    $("#diabetes-type-2-dx").find(":input").prop('disabled', false);
                }
            });

            $("#diabetes-type-2-dx").change(function () {
                if ($('#diabetes-type-2-dx :checkbox').is(':checked')) {
                    $("#diabetes-type-1-dx").find(":input").prop('disabled', true);
                } else {
                    $("#diabetes-type-1-dx").find(":input").prop('disabled', false);
                }
            });
        }

        function ensureDiagnosisChecked(field, dxList) {
            // function checks whether any diagnosis in a list of diagnosis
            // fields (dxList) are checked and toggles an error on *field*
            // if no diagnoses are checked
	        // note that all diagnoses in the list must be on the form
	        // for an error to toggle on
            var dxChecked = false; // any diagnosis in list checked (initialize)
            var dxIterBool = false; // diagnosis item in list checked (initialize)
            var nDxRequired = dxList.length; // number of diagnosis fields expected
            var nDxActual = 0; // Actual diagnosis fields (initialize)
            // iterate over diagnosis list and check if any are checked
            for (i = 0; i < dxList.length; i++) {
                nDxActual += dxList[i].length;
                dxIterBool = dxList[i].is(':checked');
                dxChecked = (dxChecked || dxIterBool);
            }
            // if all diagnoses are unchecked toggleError
            // else return no error
            var err = null;
            if (!dxChecked && (nDxActual == nDxRequired)) {
                err = 'Must enter at least one diagnosis!';
            }
            return flowsheet.toggleError(field, err);
        }

        function validateDiabetesDiagnosisType() {
            var type1diagnosis = $('#diabetes-type-1-dx :checkbox').is(':checked');
            var type2diagnosis = $('#diabetes-type-2-dx :checkbox').is(':checked');

            if (type1diagnosis && type2diagnosis) {
                $("#diabetes-type-1-dx").find(":input").prop('disabled', false);
                $("#diabetes-type-2-dx").find(":input").prop('disabled', false);
            } else if (type1diagnosis || type2diagnosis) {
                if (type1diagnosis) {
                    $("#diabetes-type-2-dx").find(":input").prop('disabled', true);
                } else {
                    $("#diabetes-type-1-dx").find(":input").prop('disabled', true);
                }
            } else {
                $("#diabetes-type-1-dx").find(":input").prop('disabled', false);
                $("#diabetes-type-2-dx").find(":input").prop('disabled', false);
            }
        }

        function setupViralLoadValidation(flowsheet, html) {
            var vlNumericField = jq(html).find("#vl-numeric :first-child");
            var vlLessThanField = jq(html).find("#vl-less-than :first-child");
            var vlLdlField = jq(html).find("#vl-LDL :checkbox");

            if ((typeof vlNumericField.val() !== "undefined") &&
              ( typeof vlLessThanField.val() !== "undefined") &&
              ( typeof vlLdlField.is(':checked') !== "undefined")) {


              vlNumericField.change(function () {
                event.stopImmediatePropagation();
                var tempVal = vlNumericField.val();
                if (typeof tempVal !== 'undefined' && !Number.isNaN(Number.parseInt(tempVal, 10))) {
                    vlLessThanField.val('');
                    vlLdlField.prop('checked', false);
                }

              });

              vlLessThanField.change(function () {
                event.stopImmediatePropagation();
                var tempVal = vlLessThanField.val();
                if (typeof tempVal !== 'undefined' && !Number.isNaN(Number.parseInt(tempVal, 10))) {
                  vlNumericField.val('');
                  vlLdlField.prop('checked', false);
                }
              });

              vlLdlField.change(function () {
                var ldlVal = vlLdlField.is(':checked');
                event.stopImmediatePropagation();
                if (typeof ldlVal !== "undefined" && ldlVal === true) {
                    vlNumericField.val('');
                    vlLessThanField.val('');
                }
              });

            }

        }
    };

    fsExt.extractHeightValues = function(heightArray) {
        var heightValues = null;
        if (heightArray && heightArray.length > 0) {
            heightValues = [];
            for (i = 0; i < heightArray.length; i++) {
                var heightInfo = heightArray[i];
                heightValues.push(heightInfo.height);
            }
        }

        return heightValues;
    };

    fsExt.isHeightOutsideOfStandardDeviation = function(values, height) {
        var outside = false;
        if ( values.length > 0 ) {
            var mean = jStat.mean(values);
            var stDev = jStat.stdev(values);
            if (Math.abs(height - mean) > stDev ) {
                outside = true;
            }
        }
        return outside;
    };

    fsExt.parseDateFromHiddenInput = function(hiddenInput) {
        if (hiddenInput && hiddenInput.val()) {
            return moment(hiddenInput.val(), "YYYY-MM-DD");
        }
    }

    flowsheet.setFlowsheetExtension(fsExt);

}( window.flowsheet = window.flowsheet || {}, jQuery ));
