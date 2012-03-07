#!/bin/bash

# make sure the soundex person_name_code from the touchscreens are
# recalculated for openmrs

LOGFILE=$$.log
exec > $LOGFILE 2>&1

START=`date +%Y%m%d-%H%M`
date >/home/emradmin/rebuild_person_name_codes.log
/home/emradmin/bart2/script/console <<EOF
PersonNameCode.rebuild_person_name_codes
exit
EOF
date >>/home/emradmin/rebuild_person_name_codes.log
echo "DONE" >>/home/emradmin/rebuild_person_name_codes.log
STOP=`date +%Y%m%d-%H%M`

MAIL=apzu-emr@apzu.pih.org
PATH=$PATH:/bin:/usr/bin:/home/emradmin/pihmalawi/scripts

TODAY=`date +%Y%m%d`

# echo "Process run from $START to $STOP" | 
mailx -s "emr: Upper Neno rebuild_person_name_codes $TODAY" "$MAIL" < $LOGFILE
