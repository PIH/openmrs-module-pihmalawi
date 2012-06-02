#!/bin/bash

# quick hack to simulate creation of many, many encounters through the webapp
# invoke with /create_encounter.sh 2>logging.txt and check the times for each create encounter request

# couldn't find a better way without Internet connection to manipulate time output
export TIMEFORMAT=$'%3R'
OUTPUTFILE=output.txt

source run_report.config

# process these patient ids
for p in `cat create_encounter_400_patients.txt`; do
  echo "  Patient $p"

#  echo "  OpenMRS Login"
  wget --quiet --keep-session-cookies --save-cookies cookies.txt --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet

  # create 5 encounters per patient

  # Encounter details
  FORM=19
  OBS="&w1=18&w3=16576&w5=17%2F03%2F2011&w8=500&w10="
  PATIENT=$p

#  echo "  Create Encounter"
  time wget --quiet --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data "personId=$PATIENT&htmlFormId=$FORM&formModifiedTimestamp=1298713092000&encounterModifiedTimestamp=0&closeAfterSubmission=$OBS" "$BASE_URL/module/htmlformentry/htmlFormEntry.form?personId=$PATIENT&patientId=$PATIENT&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=$FORM"
  time wget --quiet --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data "personId=$PATIENT&htmlFormId=$FORM&formModifiedTimestamp=1298713092000&encounterModifiedTimestamp=0&closeAfterSubmission=$OBS" "$BASE_URL/module/htmlformentry/htmlFormEntry.form?personId=$PATIENT&patientId=$PATIENT&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=$FORM"
  time wget --quiet --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data "personId=$PATIENT&htmlFormId=$FORM&formModifiedTimestamp=1298713092000&encounterModifiedTimestamp=0&closeAfterSubmission=$OBS" "$BASE_URL/module/htmlformentry/htmlFormEntry.form?personId=$PATIENT&patientId=$PATIENT&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=$FORM"
  time wget --quiet --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data "personId=$PATIENT&htmlFormId=$FORM&formModifiedTimestamp=1298713092000&encounterModifiedTimestamp=0&closeAfterSubmission=$OBS" "$BASE_URL/module/htmlformentry/htmlFormEntry.form?personId=$PATIENT&patientId=$PATIENT&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=$FORM"
  time wget --quiet --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data "personId=$PATIENT&htmlFormId=$FORM&formModifiedTimestamp=1298713092000&encounterModifiedTimestamp=0&closeAfterSubmission=$OBS" "$BASE_URL/module/htmlformentry/htmlFormEntry.form?personId=$PATIENT&patientId=$PATIENT&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=$FORM"

done

echo "  Cleanup"
rm -f cookies.txt index.* output.*
