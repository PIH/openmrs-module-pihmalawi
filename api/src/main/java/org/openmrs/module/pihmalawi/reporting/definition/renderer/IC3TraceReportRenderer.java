/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reporting.definition.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.ApzuReportUtil;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ExcelBuilder;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetMetaData;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.DataSetRowList;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.pihmalawi.reporting.reports.IC3TraceReport.*;

/**
 * Renderer for the TraceMissedAppointmentReport
 */
@Handler
public class IC3TraceReportRenderer extends ExcelTemplateRenderer {

    private Log log = LogFactory.getLog(this.getClass());

    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {

        ExcelBuilder builder = new ExcelBuilder();
        Date reportDate = reportData.getContext().getEvaluationDate();

        Calendar nextMonday = ApzuReportUtil.nextDayOfWeek(reportDate, Calendar.MONDAY);
        Calendar nextSecondWednesday = ApzuReportUtil.nextDayOfWeek(reportDate, Calendar.WEDNESDAY);
        nextSecondWednesday.add(Calendar.DAY_OF_MONTH, 7);

        for (String key : reportData.getDataSets().keySet()) {
            SimpleDataSet ds = (SimpleDataSet)reportData.getDataSets().get(key);
            if (ds.getRows().size() > 0) {

                DataSetMetaData metaData = ds.getMetaData();
                Location location = getParameterValue(ds, ReportingConstants.LOCATION_PARAMETER, Location.class);
                Integer minWk = getParameterValue(ds, MIN_WEEKS_PARAM, Integer.class);
                Integer maxWk = getParameterValue(ds, MAX_WEEKS_PARAM, Integer.class);
                Boolean isPhase1 = ObjectUtil.nvl(getParameterValue(ds, PHASE_1_ONLY_PARAM, Boolean.class), false);

                builder.newSheet(key);

                builder.hideGridlinesInCurrentSheet();
                builder.setLandscape();
                builder.fitColumnsToPage();

                Map<String, Object> headerCellValues = getHeaderCellValues(minWk, isPhase1);
                int lastColMerge = isPhase1 ? 9 : location != null ? 12 : 13;

                String topRowStyle = "bold,size=18,color=" + HSSFColor.WHITE.index + ",background-color=" + HSSFColor.BLACK.index;
                builder.addCell(headerCellValues.get("traceLabel"), topRowStyle).merge(5, 0);
                builder.addCell(headerCellValues.get("reportLabel"), topRowStyle).merge(lastColMerge, 0);
                builder.nextRow();

                if (location != null) {
                    builder.addCell(location.getName(), "bold,size=22,color=" + HSSFColor.BLUE.index).merge(5, 0);
                    builder.addCell(builder.createRichTextString(
                            "Instructions: ", "bold",
                            "For each patient listed here, verify using the mastercards whether they have truly missed an appointment. If they have really missed the appointment, please find the patient and record the outcome.If they have not missed an appointment, add client name and visit details to \"Mastercard Update\" report. ", null,
                            "Upper Neno: ", "bold",
                            "Return all findings to Chisomo (0884784429). ", null,
                            "Lower Neno: ", "bold",
                            "Return all findings to Maxwell (0884784429).", null), "size=12,wraptext,valign=center").merge(lastColMerge, 5);
                    builder.nextRow();

                    builder.addCell(minWk + "- <" + maxWk + " weeks missed appointment", "bold");
                    builder.nextRow();

                    builder.addCell(builder.createRichTextString("Patient tracking for week of ", "bold", DateUtil.formatDate(nextMonday.getTime(), "EEEE, dd-MMM-yyyy"), "color=" + HSSFColor.BLUE.index + ",bold"), null);
                    builder.nextRow();
                    builder.addCell(builder.createRichTextString("Date Report Printed: ", "bold", DateUtil.formatDate(reportDate, "EEEE, dd-MMM-yyyy"), "color=" + HSSFColor.BLUE.index + ",bold"), null);
                    builder.nextRow();
                    builder.addCell(builder.createRichTextString("Date Report due back to Chisomo/Maxwell:  ", "bold", DateUtil.formatDate(nextSecondWednesday.getTime(), "EEEE, dd-MMM-yyyy"), "color=" + HSSFColor.BLUE.index + ",bold"), null);
                }
                else {
                    builder.nextRow();
                    builder.addCell("12 weeks missed appointment", "bold").merge(5, 0);
                    builder.addCell(builder.createRichTextString(
                            "Instructions: ", "bold",
                            "When complete, return all findings to Chisomo (0884784429) in Upper Neno and Maxwell (0884784429) in Lower Neno", null), "size=12,wraptext,valign=center").merge(lastColMerge, 2);
                    builder.nextRow();
                    builder.addCell(builder.createRichTextString("Date Report Printed: ", "bold", DateUtil.formatDate(reportDate, "EEEE, dd-MMM-yyyy"), "color=" + HSSFColor.BLUE.index + ",bold"), null).merge(5, 0);
                }
                builder.nextRow();
                builder.nextRow();

                String headerStyle1 = "bold,size=11,wraptext,border=top";
                String headerStyle2 = headerStyle1 + ",rotation=90";
                String headerStyle3 = headerStyle2 + ",size=8";

                builder.addCell("", null, 6);
                if (metaData.getColumn("parameter.location") != null) {
                    builder.addCell("Facility", headerStyle1 + ",border=left", 20);
                    builder.addCell("ARV#", headerStyle1);
                }
                else {
                    builder.addCell("ARV#", headerStyle1 + ",border=left", 12);
                }
                if (metaData.getColumn("NCD_NUMBER") != null) {
                    builder.addCell("NCD#", headerStyle1, 12);
                }
                builder.addCell("First", headerStyle1, 12);
                builder.addCell("Last", headerStyle1, 15);
                builder.addCell("Village", headerStyle1, 30);
                builder.addCell("VHW", headerStyle1, 20);
                if (metaData.getColumn("DIAGNOSES") != null) {
                    builder.addCell("Diagnoses", headerStyle1, 20);
                }
                builder.addCell("Last IC3 Visit Date", headerStyle1, 18);
                builder.addCell("Last Visit Appt Date", headerStyle1, 18);
                builder.addCell("Weeks out of Care", headerStyle1, 8);
                if (metaData.getColumn("PRIORITY_PATIENT") != null) {
                    builder.addCell("Priority Patient", headerStyle1, 8);
                }
                builder.addCell(builder.createRichTextString("Patient actually\nvisited clinic.", headerStyle2, "\nComplete Mastercard Update", headerStyle3), headerStyle2, 8);
                builder.addCell("Transferred Out", headerStyle2, 4);
                builder.addCell("Died", headerStyle2, 4);
                builder.addCell("Stopped", headerStyle2, 4);
                builder.addCell("Missed Appt", headerStyle2, 4);
                builder.addCell("Patient Not Found", headerStyle2, 4);
                builder.addCell("Remarks", headerStyle1 + ",border=right", 25);

                // Set this row to repeat when printing on subsequent pages
                int rowNum = builder.getCurrentRowNum();
                int colNum = builder.getCurrentColNum();
                builder.getCurrentSheet().setRepeatingRows(new CellRangeAddress(rowNum, rowNum, 0, colNum));

                builder.nextRow();

                String rowStyle = "border=top";

                DataSetRowList rows = ds.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    builder.addCell(i + 1, "color=" + HSSFColor.GREY_50_PERCENT.index);
                    DataSetRow row = rows.get(i);
                    if (i + 1 == rows.size()) {
                        rowStyle += ",border=bottom";
                    }
                    if (metaData.getColumn("parameter.location") != null) {
                        Location facility = (Location)row.getColumnValue("parameter.location");
                        builder.addCell(facility.getName(), rowStyle + ",border=left");
                        builder.addCell(row.getColumnValue("ARV_NUMBER"), rowStyle);
                    }
                    else {
                        builder.addCell(row.getColumnValue("ARV_NUMBER"), rowStyle + ",border=left");
                    }

                    if (metaData.getColumn("NCD_NUMBER") != null) {
                        builder.addCell(row.getColumnValue("NCD_NUMBER"), rowStyle);
                    }
                    builder.addCell(row.getColumnValue("FIRST_NAME"), rowStyle);
                    builder.addCell(row.getColumnValue("LAST_NAME"), rowStyle);
                    builder.addCell(row.getColumnValue("VILLAGE"), rowStyle);
                    builder.addCell(row.getColumnValue("VHW"), rowStyle);
                    if (metaData.getColumn("DIAGNOSES") != null) {
                        builder.addCell(row.getColumnValue("DIAGNOSES"), rowStyle);
                    }
                    builder.addCell(row.getColumnValue("LAST_VISIT_DATE"), rowStyle + ",date");
                    builder.addCell(row.getColumnValue("NEXT_APPT_DATE"), rowStyle + ",date");
                    builder.addCell(row.getColumnValue("WEEKS_OUT_OF_CARE"), rowStyle + ",format=0.0");
                    if (metaData.getColumn("PRIORITY_PATIENT") != null) {
                        Set<String> s = (Set<String>) row.getColumnValue("PRIORITY_PATIENT");
                        builder.addCell(s != null && !s.isEmpty() ? "!!!" : "", rowStyle+",color=" + HSSFColor.RED.index+",align=center");
                    }
                    for (int j = 0; j < 6; j++) {
                        builder.addCell("☐", rowStyle + ",align=center,size=18");
                    }
                    builder.addCell("", rowStyle + ",border=right");
                    builder.nextRow();
                }
            }
        }

        builder.write(out);
    }

    public Map<String, Object> getHeaderCellValues(Integer minWeeks, Boolean isPhase1) {
        Map<String, Object> m = new HashMap<String, Object>();
        if (minWeeks == null || minWeeks == 12) {
            m.put("traceLabel", "TRACE");
            m.put("reportLabel", "12w Report - Clinical Team / POSER");
        }
        else {
            m.put("traceLabel", "TRACE" + (minWeeks == 2 ? " PHASE " + (isPhase1 ? "I" : "II") : ""));
            m.put("reportLabel", (minWeeks == 2 ? "2w Report -  VHW Site Supervisor" : "6w Report -  LPT"));
        }
        return m;
    }

    public <T> T getParameterValue(DataSet ds, Parameter p, Class<T> type) {
        return (T) ds.getContext().getParameterValue(p.getName());
    }

    /**
     * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
     */
    @Override
    public String getFilename(ReportRequest request) {
        return getFilenameBase(request) + ".xlsx";
    }
}