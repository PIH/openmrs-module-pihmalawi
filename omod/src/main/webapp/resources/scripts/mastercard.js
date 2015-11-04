//Uses the namespace pattern from http://stackoverflow.com/a/5947280
// expects to extend htmlForm defined in the core HFE module
(function( mastercard, jq, undefined) {

    var patientId = null;
    var headerForm = null;
    var headerEncounterId = null;
    var visitForm = null;
    var visitEncounterIds = [];
    var visitEncounterEdit = null;
    var htmlformJs = null;
    var mode = '';
    var defaultLocationId = null;
    var validationErrors = {};

    mastercard.setPatientId = function(pId) {
        patientId = pId;
    };

    mastercard.setHeaderForm = function(formName) {
        headerForm = formName;
    };

    mastercard.setHeaderEncounterId = function(eId) {
        headerEncounterId = eId;
    };

    mastercard.setVisitForm = function(formName) {
        visitForm = formName;
    };

    mastercard.addVisitEncounterId = function(eId) {
        if (visitEncounterIds.indexOf(eId) < 0) {
            visitEncounterIds.push(eId);
        }
    };

    mastercard.removeVisitEncounterId = function(eId) {
        var index = visitEncounterIds.indexOf(eId);
        if (index >= 0) {
            visitEncounterIds.splice( index, 1 );
        }
    };

    mastercard.setHtmlFormJs = function(htmlform) {
        htmlformJs = htmlform;
        htmlForm.setSuccessFunction(function(result) {
            mastercard.successFunction(result);
        });
        htmlform.getBeforeSubmit().push(isValidForSubmission);
    }

    mastercard.setMode = function(m) {
        mode = m;
    };

    mastercard.setDefaultLocationId = function(locationId) {
        defaultLocationId = locationId;
    }

    var isValidForSubmission = function() {
        return jq.isEmptyObject(validationErrors);
    }

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

    mastercard.successFunction = function(result) {
        if (mode == 'enterHeader') {
            mastercard.setHeaderEncounterId(result.encounterId);
            mastercard.viewHeader();
        }
        else {
            jq("#visit-table-row-"+result.encounterId).remove(); // Remove old row for this encounter
            mastercard.addVisitEncounterId(result.encounterId);
            mastercard.loadVisitIntoFlowsheet(result.encounterId); // Add new row for this encounter
            mastercard.toggleViewFlowsheet();
        }
        return false;
    };

    mastercard.viewHeader = function() {
        loadHtmlFormForEncounter(headerForm, headerEncounterId, false, function(data) {
            jq('#header-section').html(data);
            mastercard.toggleViewFlowsheet();
        });
    };

    mastercard.toggleViewFlowsheet = function() {
        jq('#header-section').show();
        jq(".form-action-link").show();
        jq("#delete-button").hide();
        jq("#cancel-button").hide();
        jq('#visit-edit-section').hide();
        mastercard.setMode('view');
        mastercard.showVisitTable();
        visitEncounterEdit = null;
    }

    mastercard.enterHeader = function() {
        mastercard.setMode('enterHeader');
        loadHtmlFormForEncounter(headerForm, headerEncounterId, true, function(data) {
            jq('#header-section').html(data);
            setupFormCustomizations(jq('#header-section'));
            showLinksForEditMode();
            jq("#visit-flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    };

    mastercard.deleteCurrentEncounter = function() {
        if (mode == 'enterHeader') {
            if (visitEncounterIds.length > 0) {
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
            if (confirm('Are you sure you wish to delete this visit?')) {
                deleteEncounter(visitEncounterEdit, function(data) {
                    if (data.success) {
                        jq("#visit-table-row-" + visitEncounterEdit).remove(); // Remove old row for this encounter
                        mastercard.removeVisitEncounterId(visitEncounterEdit);
                        mastercard.toggleViewFlowsheet();
                    }
                    else {
                        mastercard.showErrorMessage(data.message);
                    }
                });
            }
        }
    };

    var deleteEncounter = function(encId, callback) {
        var deleteUrl = emr.fragmentActionLink('pihmalawi', 'encounterAction', 'delete', {
            "encounter": encId,
            "reason": 'Deleted on mastercard'
        });
        jq.getJSON(deleteUrl, function(data) {
            callback(data);
        });
    }

    mastercard.enterVisit = function() {
        mastercard.setMode("enterVisit");
        loadHtmlFormForEncounter(visitForm, null, true, function(data) {
            jq('#visit-edit-section').html(data).show();
            setupFormCustomizations(jq('#visit-edit-section'));
            showLinksForEditMode();
            jq("#header-section").hide();
            jq("#visit-flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    };

    mastercard.editVisit = function(encId) {
        mastercard.setMode("editVisit");
        visitEncounterEdit = encId;
        loadHtmlFormForEncounter(visitForm, encId, true, function(data) {
            jq('#visit-edit-section').html(data).show();
            setupFormCustomizations(jq('#visit-edit-section'));
            showLinksForEditMode();
            jq("#header-section").hide();
            jq("#visit-flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    };

    mastercard.cancelEdit = function() {
        if (mode == 'enterHeader') {
            if (headerEncounterId == null) {
                document.location.href = '/'+OPENMRS_CONTEXT_PATH+'/patientDashboard.form?patientId='+patientId;
            }
            else {
                mastercard.viewHeader();
            }
        }
        else {
            jq('#visit-edit-section').empty();
            mastercard.toggleViewFlowsheet();
        }
        return false;
    };

    mastercard.showVisitTable = function() {
        jq("#visit-flowsheet-section").show();
    };

    mastercard.loadVisitTable = function() {
        jq("#header-section").show();
        for (var i=0; i<visitEncounterIds.length; i++) {
            mastercard.loadVisitIntoFlowsheet(visitEncounterIds[i]);
        }
    };

    /**
     * Loads a row into the visit table
     * This will insert the row based on the encounter date such that it is in date descending order
     * This relies upon the htmlform having the following structure:
     *  The entire form structured as a table with class visit-table
     *  The data entry form in a single row with class visit-table-row
     *  The encounter date tag in a cell with class .visit-date
     */
    mastercard.loadVisitIntoFlowsheet = function(encId) {
        loadHtmlFormForEncounter(visitForm, encId, false, function(data) {
            var section = jq("#visit-flowsheet-section");
            var table = section.find(".visit-table");
            if (table && table.length > 0) {
                var newRow = jq(data).find(".visit-table-row");
                addLinksToVisitRow(newRow, encId);
                var newVisitMoment = extractVisitMoment(newRow);
                var existingRows = table.find(".visit-table-row")
                var inserted = false;
                jq.each(existingRows, function(index, currentRow) {
                    var currentMoment = extractVisitMoment(currentRow);
                    if (currentMoment.isBefore(newVisitMoment) && !inserted) {
                        newRow.insertBefore(currentRow);
                        inserted = true;
                    }
                    else {
                        if (index == (existingRows.length-1) && !inserted) {
                            newRow.insertAfter(currentRow);
                        }
                    }
                });
            }
            else {
                table = jq(data).find(".visit-table");
                addLinksToVisitRow(table.find(".visit-table-row"), encId);
                section.append(table);
            }
        });
    }

    var addLinksToVisitRow = function(row, encId) {
        var rowId = row.attr("id");
        if (!rowId || rowId.length == 0) {
            rowId = "visit-table-row"
        }
        row.attr("id", rowId + "-" + encId);

        var visitDateCell = jq(row).find(".visit-date");
        var existingDateCell = visitDateCell.html();
        var editLink = jq('<a href="#" onclick="mastercard.editVisit('+encId+');">' + existingDateCell + '</a>');
        visitDateCell.empty().append(editLink);
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
            return moment(strVal, "DD MMM YYYY");
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
        }), action);
    };

    var showLinksForEditMode = function() {
        jq(".form-action-link").hide();
        if ((mode == 'enterHeader' && headerEncounterId != null) || mode == 'editVisit') {
            jq("#delete-button").show();
        }
        jq("#cancel-button").show();
    };

    var setupFormCustomizations = function(html) {

        // Configure defaults we want to apply across any suitable form
        var locationInput = jq(html).find("#visitLocation :first-child");
        if (!locationInput.val() || locationInput.val().length == 0) {
            locationInput.val(defaultLocationId);
        }

        // Configure validations we want to apply across any suitable form
        validationErrors = {};
        var apptDateInput = jq(html).find("#appointmentDate :first-child");
        var visitDateInput = jq(html).find("#visitDate :first-child");
        if (apptDateInput && visitDateInput) {
            apptDateInput.change(function () {
                validateAppointmentDate(apptDateInput, visitDateInput);
            });
            visitDateInput.change(function () {
                validateAppointmentDate(apptDateInput, visitDateInput);
            });
        }
    };

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
        return toggleError(apptDateField, err)
    }

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
    }

}( window.mastercard = window.mastercard || {}, jQuery ));