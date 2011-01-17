#!/bin/sh

MAIL=cneumann@pih.org

TODAY=`date +%Y%m%d`

NOW=`date +%d`%2F`date +%m`%2F`date +%Y`
ONE_WEEK_AGO=`date --date "6 Days ago" +%d`%2F`date --date "6 Days ago" +%m`%2F`date --date "6 Days ago" +%Y`
#ONE_MONTH_AGO=`date -v-1m -v+1d +%d`%2F`date -v-1m -v+1d +%m`%2F`date -v-1m -v+1d +%Y`
#ONE_WEEK_AGO=`date -v-6d +%d`%2F`date -v-6d +%m`%2F`date -v-6d +%Y`
#ONE_MONTH_AGO=`date -v-1m -v+1d +%d`%2F`date -v-1m -v+1d +%m`%2F`date -v-1m -v+1d +%Y`

# Pre-ART Missed Appointment
#./run_report.sh \
#  "Pre-ART Missed Appointment_" \
#  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
#  "Pre-ART Missed Appointment Overview (Excel)_" \
#  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
#  "" \
#  Pre_ART_Missed_Appointment-`echo $TODAY`.xls

# HIV Weekly Outcome
./run_report.sh \
  "HIV Weekly Outcome_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "HIV Weekly Outcome Overview (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  HIV_Weekly_Outcome-`echo $TODAY`.xls
echo "" | mailx -a ART_Missed_Appointment-20110117.xls -s "emr: Upper Neno ART Missed Appointments 20110117" christian.neumann@gmx.de

# ART Missed Appointment
./run_report.sh \
  "ART Missed Appointment_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "ART Missed Appointment Overview (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  ART_Missed_Appointment-`echo $TODAY`.xls

./run_report.sh \
  "ART Missed Appointment_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "ART Missed Appointment Breakdown (>=2 weeks <3 weeks)_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  ART_Missed_Appointment_between_2_and_3_weeks-`echo $TODAY`.html

./run_report.sh \
  "ART Missed Appointment_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "ART Missed Appointment Breakdown (>=3 weeks)_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  ART_Missed_Appointment_more_than_3_weeks-`echo $TODAY`.html

# Weekly Encounter by Location
./run_report.sh \
  "Weekly Encounter By Location_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "Weekly Encounter By Location.xls (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  Weekly_Encounter_By_Location-`echo $TODAY`.xls

# Weekly Encounter by User
./run_report.sh \
  "Weekly Encounter By User_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "Weekly Encounter By User.xls (Excel)_" \
  org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer \
  "" \
  Weekly_Encounter_By_User-`echo $TODAY`.xls
