#!/bin/sh

# cronjob entry
# 0 2 1 * * /home/emradmin/script_reports/run_monthly_reports.sh >> /dev/null

AREA=Lower_Neno

MAIL=apzu-emr@apzu.pih.org
PATH=$PATH:/bin:/usr/bin:/home/emradmin/pihmalawi/scripts

TODAY=`date +%Y%m%d`

NOW=`date +%d`%2F`date +%m`%2F`date +%Y`
YESTERDAY=`date --date "1 Day ago" +%d`%2F`date --date "1 Day ago" +%m`%2F`date --date "1 Day ago" +%Y`

# use this on (older) Ubuntu and RedHat systems
ONE_WEEK_AGO=`date --date "6 Days ago" +%d`%2F`date --date "6 Days ago" +%m`%2F`date --date "6 Days ago" +%Y`
# todo: it should be - 1 month + 1 day, but it is 1 month ago
ONE_MONTH_AGO=`date --date "1 Month ago" +%d`%2F`date --date "1 Month ago" +%m`%2F`date --date "1 Month ago" +%Y`

# use this at least on MacOS
#ONE_WEEK_AGO=`date -v-6d +%d`%2F`date -v-6d +%m`%2F`date -v-6d +%Y`
#ONE_MONTH_AGO=`date -v-1m -v+1d +%d`%2F`date -v-1m -v+1d +%m`%2F`date -v-1m -v+1d +%Y`

# ARV Regimen
FILE=ARV_Regimen-`echo $TODAY`.xls
run_report.sh \
  "ARV Regimen_" \
  "userEnteredParams%5BendDate%5D=$YESTERDAY" \
  "ARV Regimen (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA ARV Regimen $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history
  
# APZU HIV Indicators
FILE=APZU_HIV_Indicators-`echo $TODAY`.xls
run_report.sh \
  "APZU HIV Indicators_" \
  "userEnteredParams%5BstartDate%5D=$ONE_MONTH_AGO&userEnteredParams%5BendDate%5D=$YESTERDAY" \
  "APZU HIV Indicators (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA APZU HIV Indicators $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

