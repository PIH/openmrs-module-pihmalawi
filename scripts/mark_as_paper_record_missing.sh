#!/bin/bash

# quick hack to mark many pre-art patient records as still missing
# invoke with /create_encounter.sh 2>logging.txt and check the times for each create encounter request

# couldn't find a better way without Internet connection to manipulate time output
export TIMEFORMAT=$'%3R'
OUTPUTFILE=output.txt

source run_report.config

# process these patient ids
for p in `cat mark_as_paper_record_missing_patients.txt`; do
  echo "  Patient $p"

#  echo "  OpenMRS Login"
  wget  --keep-session-cookies --save-cookies cookies.txt --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet

  # Encounter details
  # Patient Administration Form
  FORM=38
  HTML_FORM=13
  # Obs
  # w1 = 2 # location: nno
  # w3 = 16576 # provider: unknown
  # w5 = 08/04/20113 date
  # w8 = 1482 # program: hiv program
  # w10 = 6360 # RECORD MISSING: Pre-ART Mastercard
  OBS="&w1=2&w3=16576&w5=07%2F04%2F2011&w8=1482&w10=6360"
  PATIENT=$p

#  echo "  Create Encounter"
  time wget  --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data "closeAfterSubmission=&encounterModifiedTimestamp=0&formModifiedTimestamp=1301922535000&htmlFormId=$HTML_FORM&personId=$PATIENT$OBS" "$BASE_URL/module/htmlformentry/htmlFormEntry.form?personId=$PATIENT&patientId=$PATIENT&returnUrl=%2fopenmrs%2fpatientDashboard.form&formId=$FORM"

done

echo "  Cleanup"
rm -f cookies.txt index.* output.*
