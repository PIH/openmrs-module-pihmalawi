//Uses the namespace pattern from http://stackoverflow.com/a/5947280
// expects to extend htmlForm defined in the core HFE module
(function( mastercard, jq, undefined) {

    var patientId = null;
    var headerForm = null;
    var headerEncounterId = null;
    var visitForm = null;
    var visitEncounterIds = [];
    var mode = '';

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
        visitEncounterIds.push(eId);
    };

    mastercard.setMode = function(m) {
        mode = m;
    };

    mastercard.focusFirstObs = function() {
        var firstObsField = jq(".obs-field:first");
        if (firstObsField > 0) {
            firstObsField.children()[0].focus();
        }
    };

    mastercard.successFunction = function(result) {
        if (mode == 'enterHeader') {
            mastercard.setHeaderEncounterId(result.encounterId);
            mastercard.viewHeader();
        }
        else {
            mastercard.addVisitEncounterId(result.encounterId);
            mastercard.loadVisitIntoFlowsheet(result.encounterId);
            mastercard.toggleViewFlowsheet();
        }
        return false;
    }

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
    }

    mastercard.enterHeader = function() {
        mastercard.setMode('enterHeader');
        loadHtmlFormForEncounter(headerForm, headerEncounterId, true, function(data) {
            jq('#header-section').html(data);
            jq(".form-action-link").hide();
            jq("#delete-button").show();
            jq("#cancel-button").show();
            jq("#visit-flowsheet-section").hide();
        });
    };

    mastercard.enterVisit = function() {
        mastercard.setMode("enterVisit");
        loadHtmlFormForEncounter(visitForm, null, true, function(data) {
            jq('#visit-edit-section').html(data).show();
            jq(".form-action-link").hide();
            jq("#header-section").hide();
            jq("#visit-flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    };

    mastercard.editVisit = function(encId) {
        mastercard.setMode("enterVisit");
        loadHtmlFormForEncounter(visitForm, encId, true, function(data) {
            jq('#visit-edit-section').html(data).show();
            jq(".form-action-link").hide();
            jq("#header-section").hide();
            jq("#visit-flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    }

    mastercard.cancelVisitEdit = function() {
        jq('#visit-edit-section').empty();
        mastercard.toggleViewFlowsheet();
        return false;
    }

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
                addLinksToVisitRow(table, encId);
                section.append(table);
            }
        });
    }

    var addLinksToVisitRow = function(row, encId) {
        var visitDateCell = jq(row).find(".visit-date");
        var existingDateCell = visitDateCell.html();
        var editLink = jq('<a href="#" onclick="mastercard.editVisit('+encId+');">' + existingDateCell + '</a>');
        visitDateCell.empty().append(editLink);
        console.log(visitDateCell.html());
    }

    /**
     * Takes in a table row, expecting a td of class .visit-date with a view-mode encounter date div of class .value
     * Extracts this into a moment in order to compare with other rows.
     */
    var extractVisitMoment = function(row) {
        var dateStr = jq(row).find(".visit-date .value").html();
        return moment(dateStr, "DD MMM YYYY");
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

}( window.mastercard = window.mastercard || {}, jQuery ));