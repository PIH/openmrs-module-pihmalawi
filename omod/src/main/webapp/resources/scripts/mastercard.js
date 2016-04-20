//Uses the namespace pattern from http://stackoverflow.com/a/5947280
// expects to extend htmlForm defined in the core HFE module
(function( mastercard, jq, undefined) {

    var patientId = null;
    var patientBirthdate = null;
    var headerForm = null;
    var headerEncounterId = null;
    var flowsheets = [];
    var viewOnly = false;
    var currentlyEditingFormName = null;
    var currentlyEditingEncounterId = null;
    var htmlformJs = null;
    var defaultLocationId = null;
    var validationErrors = {};
    var dirty = false;
    var heightMap = null;
    var loadingEncounters = [];

    function Flowsheet(formName, encounterIds) {
        this.formName = formName;
        this.encounterIds = encounterIds;

        this.addEncounterId = function(eId) {
            if (this.encounterIds.indexOf(eId) < 0) {
                this.encounterIds.push(eId);
            }
        };

        this.removeEncounterId = function(eId) {
            var index = this.encounterIds.indexOf(eId);
            if (index >= 0) {
                this.encounterIds.splice( index, 1 );
            }
        };
    }

    mastercard.setPatientId = function(pId) {
        patientId = pId;
    };

    mastercard.setHeaderForm = function(formName) {
        headerForm = formName;
    };

    mastercard.setHeaderEncounterId = function(eId) {
        headerEncounterId = eId;
    };

    mastercard.addFlowsheet = function(formName, encounterIds) {
        flowsheets.push(new Flowsheet(formName, encounterIds));
    };

    mastercard.getFlowsheet = function(formName) {
        for (var i=0; i<flowsheets.length; i++) {
            if (flowsheets[i].formName == formName) {
                return flowsheets[i];
            }
        }
    };

    mastercard.hasFlowsheetEncounters = function() {
        for (var i=0; i<flowsheets.length; i++) {
            if (flowsheets[i].encounterIds.length > 0) {
                return true;
            }
        }
        return false
    };

    mastercard.removeEncounterId = function(eId) {
        for (var i=0; i<flowsheets.length; i++) {
            flowsheets[i].removeEncounterId(eId);
        }
    }

    mastercard.setCurrentlyEditingFormName = function(formName) {
        currentlyEditingFormName = formName;
        mastercard.clearDirty();
    };

    mastercard.setCurrentlyEditingEncounterId = function(encId) {
        currentlyEditingEncounterId = encId;
        mastercard.clearDirty();
    };

    mastercard.setHtmlFormJs = function(htmlform) {
        htmlformJs = htmlform;
        htmlForm.setSuccessFunction(function(result) {
            mastercard.successFunction(result);
        });
        htmlform.getBeforeSubmit().push(isValidForSubmission);
    };

    mastercard.setDefaultLocationId = function(locationId) {
        defaultLocationId = locationId;
    };

    mastercard.isDirty = function() {
        return dirty;
    };

    mastercard.setDirty = function() {
        dirty = true;
    };

    mastercard.clearDirty = function() {
        dirty = false;
    };

    var isValidForSubmission = function() {
        return jq.isEmptyObject(validationErrors);
    };

    mastercard.focusFirstObs = function() {
        var firstObsField = jq(".focus-field :input:visible:enabled:first");
        if (firstObsField && firstObsField.length > 0) {
            firstObsField.focus();
        }
        else {
            var firstField = jq(":input:visible:enabled:first");
            if (firstField && firstField.length > 0) {
                firstField.focus();
            }
        }
    };

    mastercard.printForm = function() {
        window.print();
    };

    mastercard.backToPatientDashboard = function() {
        document.location.href='/'+OPENMRS_CONTEXT_PATH+'/patientDashboard.form?patientId='+patientId;
    }

    mastercard.showErrorMessage = function(msg) {
        jq("#error-message-section").show().html(msg + '<a href="#" style="padding-left:20px;" onclick="mastercard.clearErrorMessage();">Clear<span >');
    };

    mastercard.clearErrorMessage = function(msg) {
        jq("#error-message-section").empty().hide();
    };

    mastercard.viewHeader = function() {
        loadHtmlFormForEncounter(headerForm, headerEncounterId, false, function(data) {
            jq('#header-section').html(data);
            mastercard.toggleViewFlowsheet();
        });
    };

    mastercard.enableViewOnly = function() {
        viewOnly = true;
    };

    mastercard.validateFlowsheetHeight = function() {
        if (heightMap) {
            if ( loadingEncounters.length > 0 ) {
                // encounters are still loading, give it another second
                setTimeout(mastercard.validateFlowsheetHeight, 1000);
                return true;
            }
            mastercard.clearErrorMessage();
            jq(".visit-table").find(".td-error").removeClass("td-error");
            for (var key in heightMap) {
                if (heightMap.hasOwnProperty(key)) {
                    var heightArray = heightMap[key];
                    if (heightArray) {
                           var values = extractHeightValues(heightArray);
                            for (i = 0; i < heightArray.length; i++) {
                                var heightInfo = heightArray[i];
                                if ( isHeightOutsideOfStandardDeviation(values, heightInfo.height) )  {
                                    // this height obs looks "abnormal"
                                    var encounterId = heightInfo.encounterId;
                                    jq("#visit-table-row-" + encounterId).find("#heightEntered").closest('td').addClass("td-error");
                                    var error = 'Please check height for '
                                        + heightInfo.encounterDateTime.format("MMM Do YYYY");
                                    console.log(error);
                                    mastercard.showErrorMessage(error);
                                }
                            }
                    }
                }
            }
        }
    }

    mastercard.toggleViewFlowsheet = function() {
        jq('#header-section').show();
        jq(".form-action-link").show();
        if (viewOnly) {
            jq("#edit-header-link").hide();
        }
        jq("#delete-button").hide();
        jq("#cancel-button").hide();
        jq('#visit-edit-section').hide();
        mastercard.setCurrentlyEditingFormName(null);
        mastercard.setCurrentlyEditingEncounterId(null);
        mastercard.showVisitTable();
    }

    mastercard.enterHeader = function() {
        mastercard.setCurrentlyEditingFormName(headerForm);
        mastercard.setCurrentlyEditingEncounterId(headerEncounterId);
        loadHtmlFormForEncounter(headerForm, headerEncounterId, true, function(data) {
            jq('#header-section').html(data);
            setupFormCustomizations(jq('#header-section'));
            showLinksForEditMode();
            jq(".flowsheet-section").hide(); // TODO: Change mastercard.gsp so that it has .flowsheet-section classes on all flowsheets
            mastercard.focusFirstObs();
        });
    };

    mastercard.deleteCurrentEncounter = function() {
        if (currentlyEditingFormName == headerForm) {
            if (mastercard.hasFlowsheetEncounters()) {
                mastercard.showErrorMessage('You cannot delete a mastercard that has recorded visits');
            }
            else {
                deleteEncounter(headerEncounterId, function(data) {
                    if (data.success) {
                        document.location.href = '/'+OPENMRS_CONTEXT_PATH+'/patientDashboard.form?patientId='+patientId;
                    }
                    else {
                        mastercard.showErrorMessage(data.message);
                    }
                });
            }
        }
        else {
            deleteEncounter(currentlyEditingEncounterId, function(data) {
                if (data.success) {
                    jq("#visit-table-row-" + currentlyEditingEncounterId).remove(); // Remove old row for this encounter
                    mastercard.removeEncounterId(currentlyEditingEncounterId);
                    mastercard.toggleViewFlowsheet();
                    for (var i=0; i<flowsheets.length; i++) {
                        var flowsheet = flowsheets[i];
                        if (flowsheet.length == 0) {
                            jq("#flowsheet-section-"+currentlyEditingFormName).children().remove();
                        }
                    }
                }
                else {
                    mastercard.showErrorMessage(data.message);
                }
            });
        }
    };

    var deleteEncounter = function(encId, callback) {
        if (confirm('Are you sure you wish to delete this?')) {
            var deleteUrl = emr.fragmentActionLink('pihmalawi', 'encounterAction', 'delete', {
                "encounter": encId,
                "reason": 'Deleted on mastercard'
            });
            jq.getJSON(deleteUrl, function (data) {
                callback(data);
            });
        }
    }

    mastercard.enterVisit = function(formName) {
        mastercard.setCurrentlyEditingFormName(formName);
        loadHtmlFormForEncounter(formName, null, true, function(data) {
            jq('#flowsheet-edit-section-'+formName).html(data).show();
            setupFormCustomizations(jq('#flowsheet-edit-section-'+formName));
            showLinksForEditMode();
            jq("#header-section").hide();
            jq(".flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    };

    mastercard.editVisit = function(formName, encId) {
        mastercard.setCurrentlyEditingFormName(formName);
        mastercard.setCurrentlyEditingEncounterId(encId);
        loadHtmlFormForEncounter(formName, encId, true, function(data) {
            jq('#flowsheet-edit-section-'+formName).html(data).show();
            setupFormCustomizations(jq('#flowsheet-edit-section-'+formName));
            showLinksForEditMode();
            jq("#header-section").hide();
            jq(".flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    };

    mastercard.cancelEdit = function() {
        if (currentlyEditingFormName == headerForm) {
            if (headerEncounterId == null) {
                document.location.href = '/'+OPENMRS_CONTEXT_PATH+'/patientDashboard.form?patientId='+patientId;
            }
            else {
                mastercard.viewHeader();
            }
        }
        else {
            jq('.flowsheet-edit-section').empty();
            mastercard.toggleViewFlowsheet();
        }
        return false;
    };

    mastercard.successFunction = function(result) {
        if (currentlyEditingFormName == headerForm) {
            mastercard.setHeaderEncounterId(result.encounterId);
            mastercard.viewHeader();
        }
        else {
            jq("#visit-table-row-"+result.encounterId).remove(); // Remove old row for this encounter
            var currentFlowsheet = mastercard.getFlowsheet(currentlyEditingFormName);
            currentFlowsheet.addEncounterId(result.encounterId);
            mastercard.loadIntoFlowsheet(currentlyEditingFormName, result.encounterId, true); // Add new row for this encounter
        }
        return false;
    };

    mastercard.showVisitTable = function() {
        jq(".flowsheet-edit-section").hide();
        jq(".flowsheet-section").show();
        mastercard.validateFlowsheetHeight();
    };

    mastercard.loadVisitTable = function() {
        jq("#header-section").show();
        heightMap = new Object();
        for (var i=0; i<flowsheets.length; i++) {
            var fs = flowsheets[i];
            for (var j=0; j<fs.encounterIds.length; j++) {
                loadingEncounters.push(fs.encounterIds[j]);
                mastercard.loadIntoFlowsheet(fs.formName, fs.encounterIds[j]);
            }
        }
    };

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
    var recordHeightInfo = function (heightField, newVisitMoment, encounterId, formName) {
        if (heightMap ==  null ) {
            heightMap = new Object();
        }
        if (heightField) {
            var currentHeight = parseInt(heightField.text(), 10);
            if (currentHeight > 0 ) {
                var heightInfo = null;
                var formHeight = heightMap[formName];
                if (formHeight == null) {
                    formHeight = [];
                } else {
                    heightInfo = findEncounterInArray(formHeight, encounterId);
                }
                if (heightInfo == null) {
                    heightInfo = new Object();
                    heightInfo.encounterId = encounterId;
                    formHeight.push(heightInfo);
                }
                heightInfo.encounterDateTime = newVisitMoment;
                heightInfo.height = currentHeight;

                heightMap[formName] = formHeight;
            }
        }
        return true;
    }

    /**
     * Loads a row into the visit table
     * This will insert the row based on the encounter date such that it is in date descending order
     * This relies upon the htmlform having the following structure:
     *  The entire form structured as a table with class visit-table
     *  The data entry form in a single row with class visit-table-row
     *  The encounter date tag in a cell with class .visit-date
     */
    mastercard.loadIntoFlowsheet = function(formName, encId, showVisitTable) {
        loadHtmlFormForEncounter(formName, encId, false, function(data) {
            showVisitTable = showVisitTable || false;
            var newRow = jq(data).find(".visit-table-row");
            var newVisitMoment = extractVisitMoment(newRow);
            var heightField = jq(data).find("#heightEntered :first-child");
            recordHeightInfo(heightField, newVisitMoment, encId, formName);
            var section = jq("#flowsheet-section-"+formName);
            var table = section.find(".visit-table");
            var inserted = false;
            if (table && table.length > 0) {
                addLinksToVisitRow(newRow, formName, encId);
                var existingRows = table.find(".visit-table-row")
                jq.each(existingRows, function(index, currentRow) {
                    var currentMoment = extractVisitMoment(currentRow);
                    if (currentMoment.isBefore(newVisitMoment) && !inserted) {
                        newRow.insertBefore(currentRow);
                        inserted = true;
                    }
                    else {
                        if (index == (existingRows.length-1) && !inserted) {
                            newRow.insertAfter(currentRow);
                            inserted = true;
                        }
                    }
                });
                if (!inserted) {
                    table.find(".visit-table-body").append(newRow);
                }
            } else {
                table = jq(data).find(".visit-table");
                addLinksToVisitRow(table.find(".visit-table-row"), formName, encId);
                section.append(table);
            }

            if (showVisitTable) {
                jq('.flowsheet-edit-section').empty();
                mastercard.toggleViewFlowsheet();
            }
        });
    }

    var addLinksToVisitRow = function(row, formName, encId) {
        if (!viewOnly) {
            var rowId = row.attr("id");
            if (!rowId || rowId.length == 0) {
                rowId = "visit-table-row"
            }
            row.attr("id", rowId + "-" + encId);

            var visitDateCell = jq(row).find(".visit-date");
            var existingDateCell = visitDateCell.html();
            var editLink = jq('<a href="#" onclick="mastercard.editVisit(\'' + formName + '\', ' + encId + ');">' + existingDateCell + '</a>');
            visitDateCell.empty().append(editLink);
        }
    }

    /**
     * Takes in a table row, expecting a td of class .visit-date with a view-mode encounter date div of class .value
     * Extracts this into a moment in order to compare with other rows.
     */
    var extractVisitMoment = function(row) {
        var dateStr = jq(row).find(".visit-date .value").html();
        return parseDateField(dateStr);
    };

    var parseDateField = function(strVal) {
        if (strVal && strVal.length > 0) {
            return moment(strVal, "DD/MM/YYYY");
        }
    };

    /**
     * Loads the htmlform for a given encounter, calling the function passed in as 'action' with the htmlform results
     */
    var loadHtmlFormForEncounter = function(formName, encounterId, editMode, action) {
        jq.get(emr.pageLink('pihmalawi', 'htmlForm', {
            "patient": patientId,
            "encounter": encounterId,
            "editMode": editMode,
            "formName": formName
        }), action).always( function() {
            var index = loadingEncounters.indexOf(encounterId);
            if (index > -1) {
                // the encounter visit was successfully loaded into the table
                loadingEncounters.splice(index, 1);
            }
        });
    };

    var showLinksForEditMode = function() {
        jq(".form-action-link").hide();
        if (currentlyEditingEncounterId != null) { // TODO
            jq("#delete-button").show();
        }
        jq("#cancel-button").show();
    };

    var setupFormCustomizations = function(html) {

        // Configure defaults we want to apply across any suitable form
        var locationInput = jq(html).find("#visitLocation").children().first();
        if (!locationInput.val() || locationInput.val().length == 0) {
            locationInput.val(defaultLocationId);
        }
        // set mastercard default location
        locationInput = jq(html).find("#mastercardLocation").children().first();
        if (!locationInput.val() || locationInput.val().length == 0) {
            locationInput.val(defaultLocationId);
        }

        // Configure validations we want to apply across any suitable form
        validationErrors = {};
        var apptDateInput = jq(html).find("#appointmentDate :first-child");
        var visitDateInput = jq(html).find("#visitDate :first-child");
        if (apptDateInput && visitDateInput) {
            validateAppointmentDate(apptDateInput, visitDateInput);
            apptDateInput.change(function () {
                validateAppointmentDate(apptDateInput, visitDateInput);
            });
            visitDateInput.change(function () {
                validateAppointmentDate(apptDateInput, visitDateInput);
            });
        }

        var heightInput = jq(html).find("#heightInput :first-child");
        var weightInput = jq(html).find("#weightInput :first-child");
        if (heightInput) {
            validateHeight(heightInput, visitDateInput);
            heightInput.change( function () {
                event.stopImmediatePropagation();
                validateHeightAndWeight(heightInput, weightInput);
                validateHeight(heightInput, visitDateInput);
            });
            heightInput.blur (function (event) {
                event.stopImmediatePropagation();
                validateHeight(heightInput, visitDateInput);
            });
        }
        if (weightInput) {
            weightInput.change( function () {
                event.stopImmediatePropagation();
                validateHeightAndWeight(heightInput, weightInput);
            });
        }

        // Configure warning if navigating away from form
        jq(html).find(':input').change(function () {
            mastercard.setDirty();
        });

        // Set up togglable fields
        jq(html).find("[data-toggle-source]").each(function() {
            var toggleCheckbox = jq(this).find("input:checkbox");
            var toggleTargetId = jq(this).data("toggle-target");
            var toggleTarget = jq("#"+toggleTargetId);
            jq(toggleCheckbox).change(function() {
                toggleEnabledDisabled(toggleTarget, jq(this).prop("checked"));
            });
            toggleEnabledDisabled(toggleTarget, jq(toggleCheckbox).prop("checked"));
        });
    };

    var toggleEnabledDisabled = function(toggleTarget, enable) {
        if (enable) {
            jq(toggleTarget).find(":input").prop("disabled", false);
        }
        else {
            jq(toggleTarget).find(":input").prop("disabled", true).val("");
        }
    };

    function compare(a,b) {
        if ( a.encounterDateTime.isBefore(b.encounterDateTime) ) {
            return -1;
        } else if ( a.encounterDateTime.isAfter(b.encounterDateTime) ) {
            return 1;
        } else {
            return 0;
        }
    }

    function extractHeightValues(heightArray) {
        var heightValues = null;
        if (heightArray && heightArray.length > 0) {
            heightValues = [];
            for (i = 0; i < heightArray.length; i++) {
                var heightInfo = heightArray[i];
                heightValues.push(heightInfo.height);
            }
        }

        return heightValues;
    }

    function isHeightOutsideOfStandardDeviation(values, height) {
        var outside = false;
        if ( values.length > 0 ) {
            var mean = jStat.mean(values);
            var stDev = jStat.stdev(values);
            console.log("height = " + height + ";  mean= " + mean + "; stDev= " + stDev);
            if (Math.abs(height - mean) > stDev ) {
                outside = true;
            }
        }
        return outside;
    }

    function standardDeviation(values) {
        var stdDev = -1;

        if ( values && values.length > 0 ) {
            stdDev = jStat.stdev(values);
        }

        return stdDev;
    }

    function averageHeight(heightArray) {
        var avg = -1;
        if (heightArray) {
            var jstat = this.jStat(heightArray);
            var sum = 0;
            var arrayLength = heightArray.length;
            for (i = 0; i < arrayLength; i++) {
                var heightInfo = heightArray[i];
                sum += parseInt(heightInfo.height, 10);
            }
            var avg = sum/arrayLength;
        }
        return avg;
    }

    function validateAdultHeight(patientHeight) {
        var error = null;
        if ( patientHeight > 0 && heightMap) {
            if (currentlyEditingFormName ) {
                var heightArray = heightMap[currentlyEditingFormName];
                if (heightArray) {
                    var values = extractHeightValues(heightArray);
                    if (isHeightOutsideOfStandardDeviation(values, patientHeight)) {
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
        if ( patientHeight > 0 && heightMap) {
            if (currentlyEditingFormName ) {
                var heightArray = heightMap[currentlyEditingFormName];
                if (heightArray) {
                    heightArray.sort(compare);
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
        mastercard.clearErrorMessage();
        if ( patientHeight > 0 && patientWeight > 0 && patientHeight < patientWeight) {
            error = "The height should be larger than the weight";
            if (errorField) {
                errorField.show();
                errorField.text(error);
            }
            mastercard.showErrorMessage(error);
        }
        return error;
    }

    var validateHeight = function(heightField, visitDateField) {
        var patientHeight = parseInt(heightField.val());
        if ( patientHeight ) {
            var validationError = null;
            var visitMoment = parseDateField(visitDateField.val());
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
            mastercard.clearErrorMessage();
            if (validationError) {
                var errorField = heightField.siblings(".field-error");
                if (errorField) {
                    errorField.show();
                    errorField.text(validationError);
                }
                mastercard.showErrorMessage(validationError);
                heightField.className = 'illegalValue';
            }
        }
    }

    var validateAppointmentDate = function(apptDateField, visitDateField) {
        var apptMoment = parseDateField(apptDateField.val());
        var visitMoment = parseDateField(visitDateField.val());
        var err = null;
        if (apptMoment && visitMoment) {
            var monthDiff = apptMoment.diff(visitMoment, 'months', true);
            if (monthDiff < 0) {
                err = 'Cannot be in the past';
            }
            else if (monthDiff > 6) {
                err = 'Must be within 6 months of visit date';
            }
        }
        else if (apptDateField[0] && !apptMoment) {
            err = "Required"
        }
        return toggleError(apptDateField, err)
    };

    var toggleError = function(field, errorMessage) {
        var errorDiv = field.siblings(".field-error");
        if (errorMessage) {
            errorDiv.html(errorMessage).show();
            validationErrors[field.attr("id")] = errorMessage;
        }
        else {
            errorDiv.html("").hide();
            delete validationErrors[field.attr("id")];
        }
        if (isValidForSubmission()) {
            jq(".submitButton").removeAttr("disabled");
        }
        else {
            jq(".submitButton").attr("disabled","disabled");
        }
    };

}( window.mastercard = window.mastercard || {}, jQuery ));