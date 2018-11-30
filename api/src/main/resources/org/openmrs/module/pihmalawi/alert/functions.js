// ***** Functions that can be used within the alert scripting *****

/**
 * Returns full years, rounded down, between the two timestamps
 * To return a positive result, timestamp1 must be greater (more recent) than timestamp2
 * So calculating an age would be yearsBetween(today, birthdate)
 */
function yearsBetween(timestamp1, timestamp2) {
    var d1 = new Date(+timestamp1);
    var d2 = new Date(+timestamp2);
    var years = d1.getFullYear() - d2.getFullYear();  // Start with difference in calendar year
    var monthDiff = d1.getMonth() - d2.getMonth();
    var dayDiff = d1.getDate() - d2.getDate();
    if (monthDiff < 0 || (monthDiff == 0 && dayDiff < 0)) {  // If it is an earlier month, or earlier day in same month, subtract 1
        years--;
    }
    return years;
}

/**
 * Returns full months, rounded down, between the two timestamps
 * To return a positive result, timestamp1 must be greater (more recent) than timestamp2
 * So calculating an age in months would be yearsBetween(today, birthdate)
 */
function monthsBetween(timestamp1, timestamp2) {
    var d1 = new Date(+timestamp1);
    var d2 = new Date(+timestamp2);
    var years = d1.getFullYear() - d2.getFullYear();  // Start with difference in calendar year
    var months = d1.getMonth() - d2.getMonth(); // Add or subtract the months apart
    var dayDiff = d1.getDate() - d2.getDate(); // Subtract a month if the day of the month is earlier
    return years*12 + months - (dayDiff >= 0 ? 0 : 1);
}

/**
 * Returns full days, rounded down, between the two timestamps
 * To return a positive result, timestamp1 must be greater (more recent) than timestamp2
 * So calculating an age in months would be yearsBetween(today, birthdate)
 */
function daysBetween(timestamp1, timestamp2) {
    var msPerDay = 1000*60*60*24;
    return Math.floor((timestamp1 - timestamp2)/msPerDay);
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
    if (arrayToCheck) {
        for (var i = 0; i < arrayToCheck.length; i++) {
            for (var j = 0; j< valueArrayToCheck.length; j++) {
                if (arrayToCheck[i] === valueArrayToCheck[j]) {
                    return true;
                }
            }
        }
    }
    return false;
}

/**
 * Returns false if valueToCheck is found in the arrayToCheck, true otherwise
 */
function hasNot(arrayToCheck, valueToCheck) {
    return !has(arrayToCheck, valueToCheck);
}
