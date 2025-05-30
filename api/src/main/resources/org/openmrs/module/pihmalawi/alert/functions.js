// ***** Functions that can be used within the alert scripting *****

var concepts = {
    'true': '655e2f90-977f-11e1-8993-905e29aff6c1',
    'false': '655e3148-977f-11e1-8993-905e29aff6c1',
    'yes': '65576354-977f-11e1-8993-905e29aff6c1',
    'no': '6557646c-977f-11e1-8993-905e29aff6c1'
};

/**
 * Returns full years, rounded down, between the two timestamps
 * To return a positive result, timestamp1 must be greater (more recent) than timestamp2
 * So calculating an age would be yearsBetween(today, birthdate)
 */
function yearsBetween(timestamp1, timestamp2) {
    let d1 = new Date(+timestamp1);
    let d2 = new Date(+timestamp2);
    let years = d1.getFullYear() - d2.getFullYear();  // Start with difference in calendar year
    let monthDiff = d1.getMonth() - d2.getMonth();
    let dayDiff = d1.getDate() - d2.getDate();
    if (monthDiff < 0 || (monthDiff == 0 && dayDiff < 0)) {  // If it is an earlier month, or earlier day in same month, subtract 1
        years--;
    }
   //java.lang.System.out.println("d1:" + d1 + "; d2: " + d2 + "; years = " + years);
    return Math.floor(years);
}

/**
 * Returns full months, rounded down, between the two timestamps
 * To return a positive result, timestamp1 must be greater (more recent) than timestamp2
 * So calculating an age in months would be yearsBetween(today, birthdate)
 */
function monthsBetween(timestamp1, timestamp2) {
    let d1 = new Date(+timestamp1);
    let d2 = new Date(+timestamp2);
    let years = d1.getFullYear() - d2.getFullYear();  // Start with difference in calendar year
    let months = d1.getMonth() - d2.getMonth(); // Add or subtract the months apart
    let dayDiff = d1.getDate() - d2.getDate(); // Subtract a month if the day of the month is earlier
    return Math.floor(years*12 + months - (dayDiff >= 0 ? 0 : 1));
}

/**
 * Returns full days, rounded down, between the two timestamps
 * To return a positive result, timestamp1 must be greater (more recent) than timestamp2
 * TODO "full days" means "24-hour periods between", is this what we want?
 */
function daysBetween(timestamp1, timestamp2) {
    var msPerDay = 1000*60*60*24;
    return Math.floor((timestamp1 - timestamp2)/msPerDay);
}

function sameDay(timestamp1, timestamp2) {
  var d1 = new Date(+timestamp1);
  var d2 = new Date(+timestamp2);
  return d1.getFullYear() === d2.getFullYear() &&
    d1.getMonth() === d2.getMonth() &&
    d1.getDate() === d2.getDate();
}

/**
 * Returns true if the passed argument is undefined, null or empty
 */
function missing(arg1) {
    return !arg1 || arg1 == null || arg1 == "";
}

/**
 * Returns true if valueToCheck is found in the arrayToCheck, false otherwise
 */
function has(arrayToCheck, valueToCheck) {
    if (arrayToCheck) {
        for (var i = 0; i < arrayToCheck.length; i++) {
            if (arrayToCheck[i] === valueToCheck) {
                return true;
            }
        }
    }
    return false;
}

/**
 * Returns true if any in the valueArrayToCheck is found in the arrayToCheck, false otherwise
 */
function hasAny(arrayToCheck, valueArrayToCheck) {

    if (arrayToCheck && valueArrayToCheck) {
      var arrayFromList;
      if (arrayToCheck instanceof java.lang.Object) {
        // we have to convert Java ArrayList to JavaScript array
        arrayFromList = arrayToCheck.toArray();
      } else {
        arrayFromList = arrayToCheck;
      }

      if (arrayFromList && arrayFromList.length > 0) {
        for (var i = 0; i < arrayFromList.length; i++) {
          for (var j = 0; j < valueArrayToCheck.length; j++) {
            if (arrayFromList[i] == valueArrayToCheck[j]) {
              return true;
            }
          }
        }
      }
    }
    return false;
}

/**
 * Returns true if any in the valueArrayToCheck is found in the arrayToCheck, false otherwise
 */
function hasChronicCareDiagnosis(arrayToCheck, valueArrayToCheck) {

  if (arrayToCheck && valueArrayToCheck) {
    var arrayFromList;
    if (arrayToCheck instanceof java.lang.Object) {
      // we have to convert Java ArrayList to JavaScript array
      arrayFromList = arrayToCheck.toArray();
    } else {
      arrayFromList = arrayToCheck;
    }

    if (arrayFromList && arrayFromList.length > 0) {
      for (var i = 0; i < arrayFromList.length; i++) {
        var obj = arrayFromList[i];
        var objValue = obj.get('value').toUpperCase();
        for (var j = 0; j < valueArrayToCheck.length; j++) {
          var valueToCheck = valueArrayToCheck[j].toUpperCase();
          if (objValue == valueToCheck) {
            return true;
          }
        }
      }
    }
  }
  return false;
}

function getTestFirstDateByTypeAndValue(objectArray, testType, resultValue, returnProperty) {

  if (objectArray && testType && resultValue && returnProperty) {
    var arrayFromList;
    if (objectArray instanceof java.lang.Object) {
      // we have to convert Java ArrayList to JavaScript array
      arrayFromList = objectArray.toArray();
    } else {
      arrayFromList = objectArray;
    }


    if (arrayFromList && arrayFromList.length > 0) {
      for (var i = 0; i < arrayFromList.length; i++) {
        var obj = arrayFromList[i];
        var objTestType = obj.get('testType').toUpperCase();
        var objResultValue = obj.get('result').toUpperCase();

        if ((objTestType == testType.toUpperCase()) && (objResultValue == resultValue.toUpperCase())) {
          var objReturnValue = obj.get(returnProperty);
          if (objReturnValue) {
            if (objReturnValue instanceof java.util.Date) {
              return objReturnValue.getTime();
            } else {
              return objReturnValue;
            }
          }
        }
      }
    }
  }
  return null;
}

function isDueForVL(art_start_date, last_art_line_regimen_change_date, last_viral_load_date, today) {

  var dueForVL = false;

  if (!missing(last_art_line_regimen_change_date)) {
    //java.lang.System.out.println("monthsBetween(today, last_viral_load_date): " + monthsBetween(today, last_viral_load_date));
    dueForVL = ((monthsBetween(today, last_art_line_regimen_change_date) < 24) && (monthsBetween(today, last_viral_load_date) >= 6) ) || ((monthsBetween(today, last_art_line_regimen_change_date) >= 24) && (monthsBetween(today, last_viral_load_date) >= 12) );
  } else if (!missing(art_start_date)){
    dueForVL = ((monthsBetween(today, art_start_date) < 24) && (monthsBetween(today, last_viral_load_date) >= 6)) || ((monthsBetween(today, art_start_date) >= 24) && (monthsBetween(today, last_viral_load_date) >= 12));
  }
  //java.lang.System.out.println("dueForVL = " + dueForVL);
  return dueForVL;
}

/**
 * Returns false if valueToCheck is found in the arrayToCheck, true otherwise
 */
function hasNot(arrayToCheck, valueToCheck) {
    return !has(arrayToCheck, valueToCheck);
}

function isTrue(conceptUuid) {
    return conceptUuid === concepts['true'] || conceptUuid === concepts['yes']
}

function isFalse(conceptUuid) {
    return conceptUuid === concepts['false'] || conceptUuid === concepts['no']
}
