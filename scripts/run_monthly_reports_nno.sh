#!/bin/sh

TODAY=`date +%Y%m%d`

NOW=`date +%d`%2F`date +%m`%2F`date +%Y`
ONE_WEEK_AGO=`date -v-6d +%d`%2F`date -v-6d +%m`%2F`date -v-6d +%Y`
ONE_MONTH_AGO=`date -v-1m -v+1d +%d`%2F`date -v-1m -v+1d +%m`%2F`date -v-1m -v+1d +%Y`

# HIV Program Changes
./run_report.sh \
  "HIV PROGRAM Changes_" \
  "userEnteredParams%5BstartDate%5D=$ONE_MONTH_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "HIV PROGRAM Changes Breakdown_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  "html" \
  HIV_Program_Changes-`echo $TODAY`.html
