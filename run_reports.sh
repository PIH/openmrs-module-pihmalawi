#!/bin/sh

# login
USER=cneumann
PW=FILL_ME
BASE_URL=http://localhost:8080/openmrs

# doesn't work for now
#MYSQL_USER=root
#MYSQL_PW=FILL_ME
#MYSQL_DB=openmrs_nno

TODAY=`date +%Y%m%d`

run_report(){
  #wget --keep-session-cookies --load-cookies cookies.txt --post-data 'userEnteredParams%5BstartDate%5D=01%2F12%2F2010&userEnteredParams%5BendDate%5D=31%2F12%2F2010&userEnteredParams%5Blocation%5D=6&selectedRenderer=org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer%21ccae1a63-5f9a-457f-8764-f105ef1fab74%3Ahtml' http://localhost:8080/openmrs/module/reporting/run/runReport.form?reportId=1b457a85-ea39-4100-aac3-7ef9adbbbcf4
  #wget --keep-session-cookies --load-cookies cookies.txt --output-document=$OUTPUTFILE --post-data "userEnteredParams%5BstartDate%5D=$START_DATE&userEnteredParams%5BendDate%5D=$END_DATE&userEnteredParams%5Blocation%5D=6&selectedRenderer=$REPORT_RENDERER" $BASE_URL/module/reporting/run/runReport.form?reportId=$REPORT_UUID
  wget --keep-session-cookies --load-cookies cookies.txt --output-document=$OUTPUTFILE --post-data "userEnteredParams%5BstartDate%5D=$START_DATE&userEnteredParams%5BendDate%5D=$END_DATE&userEnteredParams%5Blocation%5D=6&selectedRenderer=$REPORT_RENDERER" $BASE_URL/module/reporting/run/runReport.form?reportId=$REPORT_UUID
}

######################################
# login
#wget --keep-session-cookies --save-cookies cookies.txt --post-data 'uname=cneumann&pw=cneumann2&redirect=&refererURL=http%3A%2F%2Flocalhost%3A8080%2Fopenmrs%2Findex.htm' http://localhost:8080/openmrs/loginServlet http://localhost:8080/openmrs/admin
wget --keep-session-cookies --save-cookies cookies.txt --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet

######################################
# HIV PROGRAM Changes
REPORT_NAME="HIV PROGRAM Changes_"
REPORT_DESIGN="HIV PROGRAM Changes Breakdown_"
START_DATE=01%2F12%2F2010
END_DATE=31%2F12%2F2010
echo "select uuid from serialized_object where name = '`echo $REPORT_NAME`';" > sql
REPORT_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql`
echo "select uuid from reporting_report_design where name='`echo $REPORT_DESIGN`';" >sql2
REPORT_DESIGN_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql2`
REPORT_RENDERER=org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer%21`echo $REPORT_DESIGN_UUID`%3Ahtml
#REPORT_RENDERER=org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer%21ccae1a63-5f9a-457f-8764-f105ef1fab74%3Ahtml
OUTPUTFILE=HIV_Program_Changes-`echo $TODAY`.html

run_report

######################################
# Weekly Encounter By Location
REPORT_NAME="Weekly Encounter By Location_"
REPORT_DESIGN="Weekly Encounter By Location.xls (Excel)_"
END_DATE=31%2F12%2F2010
echo "select uuid from serialized_object where name = '`echo $REPORT_NAME`';" > sql
REPORT_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql`
echo "select uuid from reporting_report_design where name='`echo $REPORT_DESIGN`';" >sql2
REPORT_DESIGN_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql2`
#userEnteredParams%5BendDate%5D=22%2F12%2F2010&selectedRenderer=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%21396960b9-d1dd-4778-9efd-b29a4414daad
REPORT_RENDERER=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%21`echo $REPORT_DESIGN_UUID`
#REPORT_RENDERER=org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer%21ccae1a63-5f9a-457f-8764-f105ef1fab74%3Ahtml
OUTPUTFILE=Weekly_Encounter_By_Location-`echo $TODAY`.xls

run_report

######################################
# Weekly Encounter By User
REPORT_NAME="Weekly Encounter By User_"
REPORT_DESIGN="Weekly Encounter By User.xls (Excel)_"
END_DATE=31%2F12%2F2010
echo "select uuid from serialized_object where name = '`echo $REPORT_NAME`';" > sql
REPORT_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql`
echo "select uuid from reporting_report_design where name='`echo $REPORT_DESIGN`';" >sql2
REPORT_DESIGN_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql2`
#userEnteredParams%5BendDate%5D=22%2F12%2F2010&selectedRenderer=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%21396960b9-d1dd-4778-9efd-b29a4414daad
REPORT_RENDERER=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%21`echo $REPORT_DESIGN_UUID`
OUTPUTFILE=Weekly_Encounter_By_User-`echo $TODAY`.xls

run_report

######################################
# Pre-ART Missed Appointment
REPORT_NAME="Pre-ART Missed Appointment_"
REPORT_DESIGN="Pre-ART Missed Appointment Overview (Excel)_"
END_DATE=31%2F12%2F2010
echo "select uuid from serialized_object where name = '`echo $REPORT_NAME`';" > sql
REPORT_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql`
echo "select uuid from reporting_report_design where name='`echo $REPORT_DESIGN`';" >sql2
REPORT_DESIGN_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql2`
#userEnteredParams%5BendDate%5D=22%2F12%2F2010&selectedRenderer=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%21396960b9-d1dd-4778-9efd-b29a4414daad
REPORT_RENDERER=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%21`echo $REPORT_DESIGN_UUID`
OUTPUTFILE=Pre_ART_Missed_Appointment-`echo $TODAY`.xls

run_report

######################################
# ART Missed Appointment
REPORT_NAME="ART Missed Appointment_"
REPORT_DESIGN="ART Missed Appointment Overview (Excel)_"
END_DATE=31%2F12%2F2010
echo "select uuid from serialized_object where name = '`echo $REPORT_NAME`';" > sql
REPORT_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql`
echo "select uuid from reporting_report_design where name='`echo $REPORT_DESIGN`';" >sql2
REPORT_DESIGN_UUID=`mysql -s -N -u root -pFILLME openmrs_nno <sql2`
#userEnteredParams%5BstartDate%5D=01%2F12%2F2010&userEnteredParams%5BendDate%5D=31%2F12%2F2010&userEnteredParams%5Blocation%5D=18&selectedRenderer=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%2159a5a0d5-3e8f-4223-88d0-23b1ad1f1309
REPORT_RENDERER=org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer%21`echo $REPORT_DESIGN_UUID`
OUTPUTFILE=ART_Missed_Appointment-`echo $TODAY`.xls

run_report

rm -f cookies.txt index.htm sql sql2
