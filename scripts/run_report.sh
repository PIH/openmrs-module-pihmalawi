#!/bin/bash

if [ $# -ne 6 ]; then
  echo ""
  echo "Automatically runs Reporting module reports"
  echo ""
  echo "Make sure a file run_report.config with the connection details for"
  echo "OpenMRS and MySQL can be sourced from the current directory"
  echo ""
  echo "Usage: $0 "
  echo "          <report_name> <report parameters> "
  echo "          <report design> <report renderer> <report renderer extension>"
  echo "          <outputfile>"
  echo ""
  echo "Example:"
  cat <<EOF
run_report.sh \
  "HIV PROGRAM Changes_" \
  "userEnteredParams%5BstartDate%5D=$ONE_WEEK_AGO&userEnteredParams%5BendDate%5D=$NOW&userEnteredParams%5Blocation%5D=6" \
  "HIV PROGRAM Changes Breakdown_" \
  org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer \
  html \
  HIV_Program_Changes-`echo $TODAY`.html
EOF
  exit
fi

PARAMETERS=$2
OUTPUTFILE=$6
REPORT_RENDERER=$4
REPORT_NAME=$1
REPORT_DESIGN=$3
REPORT_RENDERER_EXTENSION=$5

source run_report.config

echo "Run OpenMRS report $REPORT_NAME"
echo "  Fetch UUIDs from DB"
echo "select uuid from serialized_object where name = '`echo $REPORT_NAME`';" > sql
REPORT_UUID=`mysql -s -N -u $MYSQL_USER -p$MYSQL_PW $MYSQL_DB <sql`
echo "select uuid from reporting_report_design where name='`echo $REPORT_DESIGN`';" >sql2
REPORT_DESIGN_UUID=`mysql -s -N -u $MYSQL_USER -p$MYSQL_PW $MYSQL_DB  <sql2`
if [ -z $REPORT_RENDERER_EXTENSION ]; then
	SELECTED_RENDERER=`echo $REPORT_RENDERER`%21`echo $REPORT_DESIGN_UUID`
else
	SELECTED_RENDERER=`echo $REPORT_RENDERER`%21`echo $REPORT_DESIGN_UUID`%3A`echo $REPORT_RENDERER_EXTENSION`
fi

echo "  OpenMRS Login"
wget --quiet --keep-session-cookies --save-cookies cookies.txt --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet
echo "  Run report"
#echo "  wget --keep-session-cookies --load-cookies cookies.txt --output-document=$OUTPUTFILE --post-data $PARAMETERS&selectedRenderer=$SELECTED_RENDERER $BASE_URL/module/reporting/run/runReport.form?reportId=$REPORT_UUID"
wget --quiet --keep-session-cookies --load-cookies cookies.txt --output-document=$OUTPUTFILE --post-data "$PARAMETERS&selectedRenderer=$SELECTED_RENDERER" $BASE_URL/module/reporting/run/runReport.form?reportId=$REPORT_UUID
echo "  Cleanup"
rm -f cookies.txt index.htm sql sql2

