#!/bin/sh

# 0 2 1/3 * * /home/emradmin/script_reports/run_quarterly_reports.sh >> /dev/null

echo "START"

AREA=Upper_Neno

MAIL=apzu-emr@apzu.pih.org
PATH=$PATH:/bin:/usr/bin:/home/emradmin/pihmalawi/scripts

TODAY=`date +%Y%m%d`

NOW=`date +%d`%2F`date +%m`%2F`date +%Y`
YESTERDAY=`date --date "1 Day ago" +%d`%2F`date --date "1 Day ago" +%m`%2F`date --date "1 Day ago" +%Y`

# use this on (older) Ubuntu and RedHat systems
ONE_WEEK_AGO=`date --date "6 Days ago" +%d`%2F`date --date "6 Days ago" +%m`%2F`date --date "6 Days ago" +%Y`
# todo: it should be - 1 month + 1 day, but it is 1 month ago
ONE_MONTH_AGO=`date --date "1 Month ago" +%d`%2F`date --date "1 Month ago" +%m`%2F`date --date "1 Month ago" +%Y`
THREE_MONTHS_AGO=`date --date "3 Months ago" +%d`%2F`date --date "3 Months ago" +%m`%2F`date --date "3 Months ago" +%Y`

# use this at least on MacOS
#ONE_WEEK_AGO=`date -v-6d +%d`%2F`date -v-6d +%m`%2F`date -v-6d +%Y`
#ONE_MONTH_AGO=`date -v-1m -v+1d +%d`%2F`date -v-1m -v+1d +%m`%2F`date -v-1m -v+1d +%Y`

# PIH xSite Indicators
FILE=Quarterly_Cross_Site_Indicator_`echo $AREA`-`echo $YESTERDAY`.xls
run_report.sh \
  "PIH Quarterly Cross Site_" \
  "userEnteredParams%5BstartDate%5D=$THREE_MONTHS_AGO&userEnteredParams%5BendDate%5D=$YESTERDAY" \
  "Quarterly Cross Site Indicator Form 10.15.xls (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA PIH XSite $YESTERDAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history
  
# APZU HIV Indicators
FILE=APZU_HIV_Indicators_Quarterly_`echo $AREA`-`echo $YESTERDAY`.xls
run_report.sh \
  "APZU HIV Indicators_" \
  "userEnteredParams%5BstartDate%5D=$THREE_MONTHS_AGO&userEnteredParams%5BendDate%5D=$YESTERDAY" \
  "APZU HIV Indicators (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA Quarterly APZU HIV Indicators $YESTERDAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

# HIV Visits
FILE=HIV_Visits-`echo $TODAY`.xls
run_report.sh \
  "HIV Visits_" \
  "userEnteredParams%5BstartDate%5D=$THREE_MONTHS_AGO&userEnteredParams%5BendDate%5D=$YESTERDAY" \
  "Excel (Default)" \
  org.openmrs.module.reporting.report.renderer.XlsReportRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA HIV Visits $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

# ART Register For All Locations (SLOW)_
FILE=ART_Register_All_Locations-`echo $TODAY`.html
run_report.sh \
  "ART Register For All Locations (SLOW)_" \
  "userEnteredParams%5BendDate%5D=$YESTERDAY" \
  "ART Register For All Locations_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA ART Register ALL Locations  $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

# after all this heavy work, schedule a reboot of the whole system
sleep 600
# this requires the setuid bit and isn't the most secure way: sudo chmod ug+s /sbin/reboot
/sbin/reboot
