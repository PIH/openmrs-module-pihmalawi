var validateDxDate = function () {
  var validationResult = false;
  jq(".dx-selected input[type='checkbox']").each(function (index) {
    var isDxChecked = this.checked;
    var tdParent = jq(this).parent().parent();
    var dateFieldId = tdParent.data("toggle-target");
    var dxDateSpan = jq("#" + dateFieldId);
    var dateValue = getField(dateFieldId + ".value").val();

    if (dxDateSpan) {
      if (isDxChecked) {
        // make dxDate field required
        if (dateValue) {
          validationResult = true;
          flowsheet.toggleError(getField( dateFieldId + '.value'), null);
        } else {
          flowsheet.toggleError(getField( dateFieldId + '.value'), "Required");
          validationResult = false;
          return validationResult;
        }
      } else {
        validationResult = true;
        flowsheet.toggleError(getField( dateFieldId + '.value'), null);
      }
    }
  });
  return validationResult;
}

function updateDxDate() {

  jq(".dx-date-required input[type='text']").change(function () {
    var parentDateId = jq(this).parent().attr('id');
    var dateValue = jq(this).val();

    if (dateValue) {
      flowsheet.toggleError(getField( parentDateId + '.value'), null);
      jq("#" + parentDateId).find(".field-error").remove();
    }
  });

}

function requireDxDate() {
  jq(".dx-selected input[type='checkbox']").change( function() {
    var isDxChecked = this.checked;
    var tdParent = jq(this).parent().parent();
    var dateFieldId = tdParent.data("toggle-target");
    var dxDateSpan = jq("#" + dateFieldId);

    if ( dxDateSpan ) {
      if ( isDxChecked ) {
        // make dxDate field required
        flowsheet.toggleError(getField( dateFieldId + '.value'), "Required");
      } else {
        // remove the required requirement for the dxDate field
        dxDateSpan.find(".required").remove();
        flowsheet.toggleError(getField( dateFieldId + '.value'), null);
      }
    }
  });
}
