#!/bin/sh

# cronjob entry
# 0 23 * * Sun /home/emradmin/script_reports/run_weekly_reports.sh >> /dev/null

AREA="Lower Neno"

echo "START"

MAIL=apzu-emr@apzu.pih.org
PATH=$PATH:/bin:/usr/bin:/home/emradmin/pihmalawi/scripts

TODAY=`date +%Y%m%d`

NOW=`date +%d`%2F`date +%m`%2F`date +%Y`

# use this on (older) Ubuntu and RedHat systems
ONE_WEEK_AGO=`date --date "6 Days ago" +%d`%2F`date --date "6 Days ago" +%m`%2F`date --date "6 Days ago" +%Y`
# todo: it should be - 1 month + 1 day, but it is 1 month ago
ONE_MONTH_AGO=`date --date "1 Month ago" +%d`%2F`date --date "1 Month ago" +%m`%2F`date --date "1 Month ago" +%Y`

# use this at least on MacOS
#ONE_WEEK_AGO=`date -v-6d +%d`%2F`date -v-6d +%m`%2F`date -v-6d +%Y`
#ONE_MONTH_AGO=`date -v-1m -v+1d +%d`%2F`date -v-1m -v+1d +%m`%2F`date -v-1m -v+1d +%Y`

# ART Missed Appointment
FILE=ART_Missed_Appointment-`echo $TODAY`.xls
run_report.sh \
  "ART Missed Appointment `echo $AREA`_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "ART Missed Appointment $AREA Overview (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA ART Missed Appointment Overview $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

FILE=ART_Missed_Appointment_between_2_and_3_weeks-`echo $TODAY`.html
run_report.sh \
  "ART Missed Appointment `echo $AREA`_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "ART Missed Appointment $AREA Breakdown (>=2 weeks <3 weeks)_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA ART Missed Appointment Breakdown >=2 <3 weeks $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

FILE=ART_Missed_Appointment_more_than_3_weeks-`echo $TODAY`.html
run_report.sh \
  "ART Missed Appointment `echo $AREA`_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "ART Missed Appointment $AREA Breakdown (>=3 weeks)_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA ART Missed Appointment Breakdown >=3 weeks $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

# HCC Missed Appointment
FILE=HCC_Missed_Appointment-`echo $TODAY`.xls
run_report.sh \
  "HCC Missed Appointment `echo $AREA`_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "HCC Missed Appointment $AREA Overview (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA HCC Missed Appointment Overview $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

FILE=HCC_Missed_Appointment_between_2_and_3_weeks-`echo $TODAY`.html
run_report.sh \
  "HCC Missed Appointment `echo $AREA`_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "HCC Missed Appointment $AREA Breakdown (>=2 weeks <3 weeks)_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA HCC Missed Appointment Breakdown >=2 <3 weeks $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

FILE=HCC_Missed_Appointment_more_than_3_weeks-`echo $TODAY`.html
run_report.sh \
  "HCC Missed Appointment `echo $AREA`_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "HCC Missed Appointment $AREA Breakdown (>=3 weeks)_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA HCC Missed Appointment Breakdown >=3 weeks $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

# HIV Data Quality
FILE=HIV_Data_Quality-`echo $TODAY`.html
run_report.sh \
  "HIV Data Quality_" \
  "userEnteredParams%5BendDate%5D=$NOW" \
  "SimpleHtmlReportRenderer" \
  org.openmrs.module.reporting.report.renderer.SimpleHtmlReportRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA HIV Data Quality $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history
 
# HIV Data Quality For All Users
FILE=HIV_Data_Quality-`echo $TODAY`.xls
run_report.sh \
  "HIV Data Quality For All Users_" \
  "userEnteredParams%5BendDate%5D=$NOW" \
  "HIV Data Quality For All Users.xls (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA HIV Data Quality For All Users $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

# Weekly Encounter by Location
FILE=Weekly_Encounter_By_Location-`echo $TODAY`.xls
run_report.sh \
  "Weekly Encounter By Location_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "Weekly Encounter By Location.xls (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA Weekly Encounter by Location $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

sleep 320

# Weekly Encounter by User
FILE=Weekly_Encounter_By_User-`echo $TODAY`.xls
run_report.sh \
  "Weekly Encounter By User_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "Weekly Encounter By User.xls (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  $FILE
echo "" | mailx -a $FILE -s "emr: $AREA Weekly Encounter by User $TODAY" "$MAIL"
mv $FILE /home/emradmin/pihmalawi/scripts/history

