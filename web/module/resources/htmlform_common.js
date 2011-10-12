/*
 * Common JS features to 'enrich' Malawi HTML Forms
 * 
 * author: shamelessly stolen from Andrew (Lesoothoo) Chi 
 */

function repeat(chr,count){
    var str = "";
    for(var x=0; x < count; x++) {str += chr;}
    return str;
}

String.prototype.padL = function(width,pad){
    if (!width ||width < 1)
        return this;

    if (!pad) pad=" ";
    var length = width - this.length
    if (length < 1) return this.substr(0,width);

    return (repeat(pad,length) + this).substr(0,width);
}

Date.prototype.formatDate = function(){
  var month = this.getMonth() + 1;
  var mm = month.toString().padL(2,"0");
  var yyyy = this.getFullYear().toString();
  var dd = this.getDate().toString().padL(2,"0");
  return dd + "/" + mm + "/" + yyyy;
}

function getElement(elementName){
  if(elementName){
    var elArray=document.getElementsByName(elementName);
    if(elArray && elArray.length==1 && elArray[0]){
      return elArray[0];
    }
  }
  return null;
}

function enGbStrToDate(strDate){
  var dates = strDate.split('/', 3);
  if(dates.length != 3){
    return null;
  }

  var da = dates[0];
  var mo = dates[1] - 1;
  var ye = dates[2];
  var d = new Date(ye, mo, da, 0, 0, 0, 0);
  return d;
}

function dateCompare(date, minDate, maxDate){
  if(date <= minDate){
    return -1;
  }else if(date > maxDate){
    return 1;
  }
  return 0;
}

function checkDate(newDateInputObj, minDateInputObj, maxBaseDateInputObj, addMonths, errorDivId){
  if(!newDateInputObj || !minDateInputObj || !maxBaseDateInputObj){
    return;
  }

  // if user clears the values, turn off the warnings
  if(newDateInputObj.value == ""){
    newDateInputObj.className = 'legalValue';
    clearError(errorDivId);
  }else{
    var date = enGbStrToDate(newDateInputObj.value);
    if(!date){
      clearError(errorDivId);
      newDateInputObj.className = 'illegalValue';
      showError(errorDivId, "Please select a valid date");
      newDateInputObj.value = 'Invalid';
      return;
    }

    var minDate = enGbStrToDate(minDateInputObj.value);
    // default the minDate so that the validation passes
    if(!minDate){
      minDate = new Date(date);
      minDate.setDate(minDate.getDate()-1);
    }
    var maxDate = enGbStrToDate(maxBaseDateInputObj.value);
    if(!maxDate){
      maxDate = new Date(date);
      maxDate.setDate(maxDate.getDate()+1);
    }else{
      maxDate.setMonth(maxDate.getMonth() + addMonths);
    }

    var res = dateCompare(date, minDate, maxDate);
    if(res != 0){
      var errorMsg;
      if(res < 0){
        errorMsg = date.formatDate() + " is invalid, date must be > " + minDate.formatDate();
      }else if(res > 0){
        errorMsg = date.formatDate() + " is invalid, date must be <= " + maxDate.formatDate();
      }
      clearError(errorDivId);
      newDateInputObj.className = 'illegalValue';
      showError(errorDivId, errorMsg);
      // set the value of the date to an invalid value so form doesn't submit
      newDateInputObj.value = 'Invalid';
    }else{
      newDateInputObj.className = 'legalValue';
      clearError(errorDivId);
    }
  }
}

function iCaseCmp (opt1, opt2) {
  // not case sensitive
  return opt1.text.toLowerCase() < opt2.text.toLowerCase() ? -1 :
    opt1.text.toLowerCase() > opt2.text.toLowerCase() ? 1 : 0;
}

function sortListBox (pListBox, start) {
  var options = new Array(pListBox.options.length);
  for(var i = 0; i < start; i++){
    options[i] = new Option ("", "");
  }

  for(var i = start; i < options.length; i++){
    options[i] =
      new Option (pListBox.options[i].text,
                  pListBox.options[i].value);
  }
  options.sort(iCaseCmp);

  for (var i = start; i < options.length; i++){
    pListBox.options[i] = options[i];
  }
}

function setEncounterLocation(locationElement, location) {
  if(locationElement && locationElement.options && locationElement.options.length>0){
    for(var i = 0; i < locationElement.options.length; i++){
	  if(locationElement.options[i].text == location){
	    locationElement.selectedIndex = i;
	     break;
	  }
	}
  }
}

function checkEnGbLocale (locale) {
  if(locale != "en_GB"){
    alert('Please set your locale to English (United Kingdom) so that your date format is dd/mm/yyyy');
    history.go(-1);
    return;
  }	
}

function getHttpGetParam(name){
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&amp;]"+name+"=([^&amp;#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if(results == null){
    return "";
  }else{
    return results[1];
  }
}

function validateAppointmentDate(encounterDateElement, appointmentDateElement, appointmentDateErrorDivElement, monthLimit) {
  appointmentDateElement.onchange=function(){checkDate(appointmentDateElement, encounterDateElement, encounterDateElement, monthLimit, appointmentDateErrorDivElement);};
  
  // since we depend on the encounter date for some of these,
  // check appropriately after change
  encounterDateElement.onchange = function(){
    if(appointmentDateElement) appointmentDateElement.onchange();
  };
}

function writeDefaultEncounterActions(patientId) {
	
	/* hack in the link to void encounters based on parsing the current url */
	/* seems only to work reliably in firefox */
	/* is hopefully fixed with later version of html form entry */
	var paramName = "encounterId";

	/* regex stuff to parse out url parameters */
	paramName = paramName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&amp;]"+paramName+"=([^&amp;#]*)";
	var regex = new RegExp(regexS );
	var results = regex.exec( window.location.href );
	if(results != null ) {
		paramValue = results[1];

		/* create openmrs url to go back to Dashboard */ 
		document.write(" <a href=" + window.location.protocol + "//" + window.location.host + "/openmrs/patientDashboard.form?patientId=" + patientId + ">Cancel</a>");

		/* create openmrs url to edit demographics */ 
		document.write(" <a href=" + window.location.protocol + "//" + window.location.host + "/openmrs/admin/patients/newPatient.form?patientId=" + patientId + ">Edit demographics</a>");

		/* create openmrs url to void encounter only if edit */
		document.write(" <a href=" + window.location.protocol + "//" + window.location.host + "/openmrs/admin/encounters/encounter.form?encounterId=" + paramValue + ">Void encounter</a>");
	}

}