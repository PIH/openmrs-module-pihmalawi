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
            jq("#visit-flowsheet-section").show();
        });
    };

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
            $hf = jq(data);
            $hf.find(".visit-table-header").remove();
            var $body = $hf.find(".visit-table-body").detach();
            var questions = $body.find(".visit-question");

            var newTableBody = jq("<tbody>");
            $hf.find(".visit-table").append(newTableBody);
            jq.each( questions, function( i, question ) {
                var label = jq(question).find(".visit-question-label");
                var field = jq(question).find(".visit-question-field");
                var unit = jq(question).find(".visit-question-unit");
                var row = jq("<tr>");
                jq(row).append(jq("<td>").append(label));
                jq(row).append(jq("<td>").append(field).append(unit));
                jq(newTableBody).append(row);
                label.show();
                unit.show();
            });
            jq('#visit-edit-section').append($hf);
            jq(".form-action-link").hide();
            jq("#header-section").hide();
            jq("#visit-flowsheet-section").hide();
        });
    };

    mastercard.loadVisitTable = function() {
        jq("#header-section").show();

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
            var newRow = jq(data).find(".visit-table-row");
            newRow.find('.visit-question-label').hide();
            newRow.find('.visit-question-unit').hide();
            jq('#visit-table-row-'+encId).replaceWith(newRow);
        });
    }

}( window.mastercard = window.mastercard || {}, jQuery ));