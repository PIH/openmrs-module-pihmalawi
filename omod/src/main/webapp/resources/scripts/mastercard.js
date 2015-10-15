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

    mastercard.viewHeader = function() {
        loadHtmlFormForEncounter(headerForm, headerEncounterId, false, function(data) {
            jq('#header-section').html(data);
            jq(".form-action-link").show();
            jq("#delete-button").hide();
            jq("#cancel-button").hide();
        });
    };

    mastercard.enterHeader = function() {
        loadHtmlFormForEncounter(headerForm, null, true, function(data) {
            jq('#header-section').html(data);
            jq(".form-action-link").hide();
        });
    };

    mastercard.editHeader = function() {
        loadHtmlFormForEncounter(headerForm, headerEncounterId, true, function(data) {
            jq('#header-section').html(data);
            jq(".form-action-link").hide();
            jq("#delete-button").show();
            jq("#cancel-button").show();
        });
    };

    mastercard.viewVisitTable = function() {
        // First load the header
        loadHtmlFormForEncounter(visitForm, null, true, function(data) {
            jq('.visit-table-header').replaceWith(jq(data).find(".visit-table-header"));
        });

        // Now load all the rows
        for (var i=0; i<visitEncounterIds.length; i++) {
            loadVisitFormRow(visitEncounterIds[i]);
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

    var loadVisitFormRow = function(encId) {
        loadHtmlFormForEncounter(visitForm, encId, false, function(data) {
            jq('#visit-table-row-'+encId).replaceWith(jq(data).find(".visit-table-row"));
        });
    }

}( window.mastercard = window.mastercard || {}, jQuery ));