var contextPath = window.location.href.split('/')[3];
var apiBaseUrl = "/" + contextPath + "/ws/rest/v1";

var getVisitDate = function() {
  var visitDateInput = jq("span#visitDate input[type='hidden']").val();
  // the Visit Date is in the format YYYY-MM-DD
  var dateParts = visitDateInput.split("-");
  // month is 0-based, that's why we need dataParts[1] - 1
  var visitDate = new Date(+dateParts[0], dateParts[1] - 1, +dateParts[2]);
  return visitDate;
}

var checkForDuplicateEnc = function(encTypeUuid, patientUuid) {
  var returnValue = false;
  var currentVisitDate = getVisitDate();
  var visitMonth = currentVisitDate.getMonth() + 1;
  var fromDate = currentVisitDate.getFullYear() + "-" + visitMonth + "-" + currentVisitDate.getDate();

  jq.ajax({
    async: false,
    datatype: "json",
    url: apiBaseUrl + "/encounter",
    data: {
      encounterType: encTypeUuid,
      patient: patientUuid,
      fromdate: fromDate,
      todate: fromDate
    }
  }).done( function(data) {
    if (data.results.length > 0 ) {
      // there is already an encounter of this type on this date
      jq("#visitDateError").text("Please check the mastercard! An encounter of this type on this day was already entered:  " + data.results[0].display).show();
      flowsheet.toggleError(jq("#visitDateError"), "Duplicate");
      returnValue = false;
    } else {
      returnValue = true;
    }
  }).fail(function() {
    console.log("failed to check for duplicate encounters");
    returnValue = true;
  });

  return returnValue;
}
