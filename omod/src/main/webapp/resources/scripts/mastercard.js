//Uses the namespace pattern from http://stackoverflow.com/a/5947280
// expects to extend htmlForm defined in the core HFE module
(function( mastercard, jq, undefined) {

    var patientId = null;
    var headerForm = null;
    var headerEncounterId = null;
    var visitForm = null;
    var visitEncounterIds = [];

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

    mastercard.appendVisitEncounterId = function(eId) {
        visitEncounterIds.push(eId);
    };

    mastercard.prependVisitEncounterId = function(eId) {
        visitEncounterIds.unshift(eId);
    };

    mastercard.focusFirstObs = function() {
        var firstObsField = jq(".obs-field:first");
        if (firstObsField > 0) {
            firstObsField.children()[0].focus();
        }
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
        jq("#visit-flowsheet-section").show();
        jq('#visit-edit-section').hide();
    }

    mastercard.enterHeader = function() {
        loadHtmlFormForEncounter(headerForm, null, true, function(data) {
            jq('#header-section').html(data);
            jq(".form-action-link").hide();
            jq("#visit-flowsheet-section").hide();
        });
    };

    mastercard.editHeader = function() {
        loadHtmlFormForEncounter(headerForm, headerEncounterId, true, function(data) {
            jq('#header-section').html(data);
            jq(".form-action-link").hide();
            jq("#delete-button").show();
            jq("#cancel-button").show();
            jq("#visit-flowsheet-section").hide();
        });
    };

    mastercard.enterVisit = function() {
        loadHtmlFormForEncounter(visitForm, null, true, function(data) {
            jq('#visit-edit-section').html(data).show();
            jq(".form-action-link").hide();
            jq("#header-section").hide();
            jq("#visit-flowsheet-section").hide();
            mastercard.focusFirstObs();
        });
    };

    mastercard.cancelVisitEdit = function() {
        jq('#visit-edit-section').hide();
        mastercard.toggleViewFlowsheet();
    }

    mastercard.showVisitTable = function() {
        jq("#visit-flowsheet-section").show();
    };

    mastercard.loadVisitTable = function() {
        jq("#header-section").show();

        for (var i=0; i<visitEncounterIds.length; i++) {
            loadVisitFormRow(i, visitEncounterIds[i]);
        }
    };

    var loadHtmlFormForEncounter = function(formName, encounterId, editMode, action) {
        jq.get(emr.pageLink('pihmalawi', 'htmlForm', {
            "patient": patientId,
            "encounter": encounterId,
            "editMode": editMode,
            "formName": formName
        }), action);
    };

    var loadVisitFormRow = function(index, encId) {
        loadHtmlFormForEncounter(visitForm, encId, false, function(data) {
            if (index == 0) {
                jq('.visit-table-header').replaceWith(jq(data).find(".visit-table-header"));
            }
            var newRow = jq(data).find(".visit-table-row");
            newRow.find('.visit-question-label').hide();
            newRow.find('.visit-question-unit').hide();
            jq('#visit-table-row-'+encId).replaceWith(newRow);
        });
    }

}( window.mastercard = window.mastercard || {}, jQuery ));