#!/bin/bash

# a bit of fiddeling to quickly add at least app dates from a text file 
# can but useful in case sync got stuck and some concept should still be recovered from the child system

# note that his can't run by itself as it will need adjustments

# create an inital export of the sync items via mysql with something like
# select e.encounter_type, e.location_id, e.patient_id, o.value_datetime 
#   from encounter e, obs o
#   where o.encounter_id=e.encounter_id and obs.date_created > '2011-11-02' and o.concept_id-5096
#   order by e.encounter_type
# adjust if needed and then find out the matching form, htmlform ids to process these encounter types

export TIMEFORMAT=$'%3R'
OUTPUTFILE=output.txt

source run_report.config

wget --quiet --keep-session-cookies --save-cookies cookies.txt --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet

while read l; do
  echo "  Process line $l"
  
  ENCOUNTER_TYPE=`echo $l | awk -F'|' '{print $2}' | sed "s/ //g"`
  LOCATION_ID=`echo $l | awk -F'|' '{print $3}' | sed "s/ //g"`
  PATIENT_ID=`echo $l | awk -F'|' '{print $4}' | sed "s/ //g"`
  APP_DATE=`echo $l | awk -F'|' '{print $5}' | sed "s/ 00:00:00//g"`
  APP_DATE_YEAR=`echo $APP_DATE | awk -F'-' '{print $1}'`
  APP_DATE_MONTH=`echo $APP_DATE | awk -F'-' '{print $2}'`
  APP_DATE_DAY=`echo $APP_DATE | awk -F'-' '{print $3}'`

  #echo $ENCOUNTER_TYPE $LOCATION_ID $PATIENT_ID $APP_DATE

  # remember that the OBS value will/might change with changing the HTML forms

  # ART Visit
  #FORM_ID=65
  #HTMLFORM_ID=25
  #OBS="&w1=16576&w3=35&w5=02%2F11%2F2011&w8=&w10=&w12=&_w14=&_w16=&_w18=&_w20=&_w22=&_w24=&w28=&w30=&w32=&w36=&_w38=&w40=&w42=$APP_DATE_DAY%2F$APP_DATE_MONTH%2F$APP_DATE_YEAR"

  # Pre-ART Visit
  #FORM_ID=67
  #HTMLFORM_ID=27
  #OBS="&w1=16576&w3=35&w5=02%2F11%2F2011&w8=&w10=&w12=&_w14=&_w16=&_w18=&_w20=&_w22=&_w24=&w28=&w30=&w32=&w36=&_w38=&w40=&w42=$APP_DATE_DAY%2F$APP_DATE_MONTH%2F$APP_DATE_YEAR"

  # Exposed Visit
  FORM_ID=69
  HTMLFORM_ID=29
  OBS="&w1=16576&w3=35&w5=02%2F11%2F2011&w8=&w10=&w12=&w26=$APP_DATE_DAY%2F$APP_DATE_MONTH%2F$APP_DATE_YEAR"
  
  PATIENT=$PATIENT_ID

  # remember to update fromModifiedTimestamp
  
  wget --keep-session-cookies --load-cookies cookies.txt --output-document=output.htm --post-data "personId=$PATIENT&htmlFormId=$HTMLFORM_ID&formModifiedTimestamp=1319868648000&encounterModifiedTimestamp=0&closeAfterSubmission=$OBS" "$BASE_URL/module/htmlformentry/htmlFormEntry.form?personId=$PATIENT&patientId=$PATIENT&returnUrl=&formId=$FORM_ID"
  
done < dumped_appointents.txt