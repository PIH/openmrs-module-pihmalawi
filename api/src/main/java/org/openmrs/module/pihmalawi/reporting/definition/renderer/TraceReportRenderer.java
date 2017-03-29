/*
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
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.ApzuReportUtil;
import org.openmrs.module.pihmalawi.reporting.reports.TraceReport;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ExcelBuilder;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.DataSetRowList;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Renderer for the TraceReport
 */
@Handler
public class TraceReportRenderer extends ExcelTemplateRenderer {

    private Log log = LogFactory.getLog(this.getClass());

    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {

        log.debug("Rendering Trace Report...");
        ExcelBuilder builder = new ExcelBuilder();
        Date reportDate = reportData.getContext().getEvaluationDate();

        Calendar nextMonday = ApzuReportUtil.nextDayOfWeek(reportDate, Calendar.MONDAY);
        Calendar nextSecondWednesday = ApzuReportUtil.nextDayOfWeek(reportDate, Calendar.WEDNESDAY);
        nextSecondWednesday.add(Calendar.DAY_OF_MONTH, 7);

        for (String key : reportData.getDataSets().keySet()) {
            SimpleDataSet ds = (SimpleDataSet)reportData.getDataSets().get(key);
            if (ds.getRows().size() > 0) {

                String locationName = getParameterValue(ds, TraceReport.LOCATION_NAME_PARAM, String.class);
                boolean isPhase1 = getParameterValue(ds, TraceReport.PHASE_1_PARAM, Boolean.class);
                Integer minWks = getParameterValue(ds, TraceReport.MIN_WKS_PARAM, Integer.class);
                Integer maxWks = getParameterValue(ds, TraceReport.MAX_WKS_PARAM, Integer.class);

                builder.newSheet(key);

                builder.hideGridlinesInCurrentSheet();
                builder.setLandscape();
                builder.fitColumnsToPage();

                String traceLabel = "TRACE" + (minWks == 2 ? " PHASE " + (isPhase1 ? "1" : "2") : "");
                String reportLabel = minWks + "w Report - ";

                if (minWks == 2) {
                    reportLabel += "VHW Site Supervisor";
                }
                else if (minWks == 6) {
                    reportLabel += "HIV Coordinator";
                }
                else if (minWks == 12) {
                    reportLabel += "Clinical Team / POSER";
                }

                int lastColMerge = isPhase1 ? 13 : 15;

                String topRowStyle = "bold,size=18,color=" + HSSFColor.WHITE.index + ",background-color=" + HSSFColor.BLACK.index;
                builder.addCell("");
                builder.addCell(traceLabel, topRowStyle).merge(5, 0);
                builder.addCell(reportLabel, topRowStyle).merge(lastColMerge, 0);
                builder.nextRow();

                builder.addCell("");
                builder.addCell(locationName, "bold,size=22,color=" + HSSFColor.BLUE.index).merge(5, 0);
                builder.addCell(builder.createRichTextString(
                        "Instructions: \n", "bold,italics,size=8",
                        "For each patient listed here, look at Reason for Contact. For patients who missed a visit, first verify using the mastercards whether they have truly missed a visit. If they have not missed an appointment, add patient name and visit details to \"Mastercard Update\" report. VHWs should then visit all other patients, provide counseling, and advise them to visit the facility on the date provided. Patients marked high-priority should be visited right away. For patients with a missed visit, record the outcome. Return the reports to Chisomo (Upper Neno) or Maxwell (Lower Neno) by the due date. If the due during the week of a Site Supervisor Meeting, you are encouraged to bring the report with you to the PIH office on that day. Otherwise, you may send the report with another vehicle or call (Chisomo-0884784429/ Maxwell-0884789517).", "italics,size=8"), "size=8,wraptext,valign=center").merge(lastColMerge, 5);
                builder.nextRow();

                builder.addCell("");
                builder.addCell(minWks + (maxWks != null ? "- <" + maxWks : "") + " weeks missed appointment", "bold");
                builder.nextRow();

                builder.addCell("");
                builder.addCell(builder.createRichTextString("Patient tracking for week of ", "bold", DateUtil.formatDate(nextMonday.getTime(), "EEEE, dd-MMM-yyyy"), "color=" + HSSFColor.BLUE.index + ",bold"), null);
                builder.nextRow();

                builder.addCell("");
                builder.addCell(builder.createRichTextString("Date Report Printed: ", "bold", DateUtil.formatDate(reportDate, "EEEE, dd-MMM-yyyy"), "color=" + HSSFColor.BLUE.index + ",bold"), null);
                builder.nextRow();

                builder.addCell("");
                builder.addCell(builder.createRichTextString("Date Report due back to Chisomo/Maxwell:  ", "bold", DateUtil.formatDate(nextSecondWednesday.getTime(), "EEEE, dd-MMM-yyyy"), "color=" + HSSFColor.BLUE.index + ",bold"), null);

                builder.nextRow();
                builder.nextRow();

                String headerStyle1 = "bold,size=11,wraptext,border=top";
                String headerStyle2 = headerStyle1 + ",rotation=90";
                String headerStyle3 = headerStyle2 + ",size=8";
                String headerStyle1Centered = headerStyle1 + ",align=center";
                String leftBorderedLight = ",border=left:grey_40_percent";
                String rightBorderedLight = ",border=right:grey_40_percent";
                String blackout = ",background-color=" + HSSFColor.BLACK.index;

                builder.addCell("", null, 4);

                builder.addCell("Village", headerStyle1 + ",border=left", 25);
                builder.addCell("VHW", headerStyle1, 20);
                builder.addCell("First", headerStyle1, 12);
                builder.addCell("Last", headerStyle1, 15);
                builder.addCell("ART#", headerStyle1, 12);
                builder.addCell("EID#", headerStyle1, 15);
                if (!isPhase1) {
                    builder.addCell("NCD#", headerStyle1, 12);
                }

                builder.addCell("(1) Missed visit", headerStyle2 + leftBorderedLight + rightBorderedLight, 4);
                builder.addCell("(2) Lab results ready", headerStyle2 + leftBorderedLight + rightBorderedLight, 4);
                builder.addCell("(3) Due for lab work\n(viral load for EID)", headerStyle2 + leftBorderedLight + rightBorderedLight, 8);

                builder.addCell("Date\nPatient\nShould Visit", headerStyle1Centered, 18);
                builder.addCell("Priority\nPatient", headerStyle1Centered, 8);
                if (!isPhase1) {
                    builder.addCell("Diagnoses", headerStyle1Centered, 20);
                }

                builder.addCell("Last IC3\nVisit Date", headerStyle1Centered + leftBorderedLight, 12);
                builder.addCell("Appointment\nDate", headerStyle1Centered, 14);
                builder.addCell("Weeks\nout of\nCare", headerStyle1Centered, 8);

                builder.addCell(builder.createRichTextString("Patient actually\nvisited clinic.", headerStyle2, "\nComplete Mastercard Update", headerStyle3), headerStyle2 + leftBorderedLight, 8);
                builder.addCell("Transferred Out", headerStyle2, 4);
                builder.addCell("Died", headerStyle2, 4);
                builder.addCell("Stopped", headerStyle2, 4);
                builder.addCell("Missed Appt", headerStyle2, 4);
                builder.addCell("Patient Not Found", headerStyle2 + ",border=right", 4);

                // Set this row to repeat when printing on subsequent pages
                int rowNum = builder.getCurrentRowNum();
                int colNum = builder.getCurrentColNum();
                builder.getCurrentSheet().setRepeatingRows(new CellRangeAddress(rowNum, rowNum, 0, colNum));

                builder.nextRow();

                DataSetRowList rows = ds.getRows();
                addExtraRowsToDataSet(rows);

                for (int i = 0; i < rows.size(); i++) {
                    builder.addCell(i + 1, "color=" + HSSFColor.GREY_50_PERCENT.index);
                    DataSetRow row = rows.get(i);

                    String rowStyle = "border=top";
                    if (i + 1 == rows.size()) {
                        rowStyle += ",border=bottom";
                    }
                    if (i % 2 == 0) {
                        rowStyle += ",background-color=242x242x242";
                    }
                    String centeredRowStyle = rowStyle + ",align=center";
                    String dateRowStyle = centeredRowStyle + ",date";

                    builder.addCell(row.getColumnValue("village"), rowStyle + ",border=left");
                    builder.addCell(row.getColumnValue("vhw"), rowStyle);
                    builder.addCell(row.getColumnValue("first_name"), rowStyle);
                    builder.addCell(row.getColumnValue("last_name"), rowStyle);
                    builder.addCell(row.getColumnValue("art_number"), rowStyle);
                    builder.addCell(row.getColumnValue("eid_number"), rowStyle);

                    if (!isPhase1) {
                        builder.addCell(row.getColumnValue("ncd_number"), rowStyle);
                    }

                    Number weeksOutOfCare = (Number)row.getColumnValue("art_weeks_out_of_care");
                    Date lastVisitDate = (Date)row.getColumnValue("art_last_visit_date");
                    Date lastApptDate = (Date)row.getColumnValue("art_last_appt_date");

                    Number eidWeeksOutOfCare = (Number)row.getColumnValue("eid_weeks_out_of_care");
                    if (eidWeeksOutOfCare != null) {
                        if (weeksOutOfCare == null || eidWeeksOutOfCare.doubleValue() > weeksOutOfCare.doubleValue()) {
                            weeksOutOfCare = eidWeeksOutOfCare;
                            lastVisitDate = (Date)row.getColumnValue("eid_last_visit_date");
                            lastApptDate = (Date)row.getColumnValue("eid_last_appt_date");
                        }
                    }

                    if (!isPhase1) {
                        Number ncdWeeksOutOfCare = (Number)row.getColumnValue("ncd_weeks_out_of_care");
                        if (ncdWeeksOutOfCare != null) {
                            if (weeksOutOfCare == null || ncdWeeksOutOfCare.doubleValue() > weeksOutOfCare.doubleValue()) {
                                weeksOutOfCare = ncdWeeksOutOfCare;
                                lastVisitDate = (Date)row.getColumnValue("ncd_last_visit_date");
                                lastApptDate = (Date)row.getColumnValue("ncd_last_appt_date");
                            }
                        }
                    }

                    String traceCriteria = (String) row.getColumnValue("trace_criteria");

                    boolean lateHiv = hasTraceCriteria(traceCriteria, "LATE_ART", "LATE_EID");
                    boolean lateNcd = hasTraceCriteria("LATE_NCD");
                    boolean lateVisit = lateHiv || lateNcd;

                    boolean labReady = hasTraceCriteria(traceCriteria, "HIGH_VIRAL_LOAD", "EID_POSITIVE_6_WK", "EID_NEGATIVE");
                    boolean labDue = hasTraceCriteria(traceCriteria, "REPEAT_VIRAL_LOAD", "EID_12_MONTH_TEST", "EID_24_MONTH_TEST");

                    String priorityCriteria = (String) row.getColumnValue("priority_criteria");
                    boolean isPriorityPatient = lateHiv || hasTraceCriteria(traceCriteria, "REPEAT_VIRAL_LOAD", "EID_POSITIVE_6_WK") || (lateNcd && ObjectUtil.notNull(priorityCriteria));

                    String dateToVisit = "";
                    if (lateHiv || hasTraceCriteria(traceCriteria, "EID_POSITIVE_6_WK", "EID_NEGATIVE")) {
                        dateToVisit = "Today";
                    }
                    else if (hasTraceCriteria(traceCriteria, "HIGH_VIRAL_LOAD", "LATE_NCD")) {
                        dateToVisit = "Next Clinic Day";
                    }
                    else if (ObjectUtil.notNull(traceCriteria)) {
                        dateToVisit = "Appointment Date";
                    }

                    builder.addCell((lateVisit ? "✓" : ""), centeredRowStyle + leftBorderedLight + rightBorderedLight); // MISSED VISIT
                    builder.addCell((labReady ? "✓" : ""), centeredRowStyle + leftBorderedLight + rightBorderedLight); // LAB RESULTS READY
                    builder.addCell((labDue ? "✓" : ""), centeredRowStyle + leftBorderedLight + rightBorderedLight); // DUE FOR LAB WORK (VIRAL LOAD FOR EID)
                    builder.addCell(dateToVisit, rowStyle + ",align=center");

                    builder.addCell(isPriorityPatient ? "!!!" : "", centeredRowStyle + ",color=" + HSSFColor.RED.index);

                    if (!isPhase1) {
                        builder.addCell(row.getColumnValue("DIAGNOSES"), centeredRowStyle);
                    }

                    String redactIfNeeded = (ObjectUtil.isNull(traceCriteria) || lateVisit ? "" : blackout);

                    builder.addCell(lastVisitDate, dateRowStyle + leftBorderedLight + redactIfNeeded);
                    builder.addCell(lastApptDate, dateRowStyle + redactIfNeeded);
                    builder.addCell(weeksOutOfCare, centeredRowStyle + ",format=0.0" + redactIfNeeded);

                    for (int j = 0; j < 6; j++) {
                        String border = (j == 0 ? leftBorderedLight : j == 5 ? ",border=right" : "");
                        builder.addCell("☐", centeredRowStyle + ",size=18" + border + redactIfNeeded);
                    }
                    builder.nextRow();
                }
            }
        }

        builder.write(out);
    }

    private void addExtraRowsToDataSet(DataSetRowList ds) {
        for (int i=0; i<5; i++) {
            ds.add(new DataSetRow());
        }
    }

    private boolean hasTraceCriteria(String traceCriteria, String... checkAny) {
        if (traceCriteria != null && checkAny != null) {
            for (String s : checkAny) {
                if (traceCriteria.toLowerCase().trim().contains(s.toLowerCase().trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private <T> T getParameterValue(DataSet ds, String name, Class<T> type) {
        return (T) ds.getContext().getParameterValue(name);
    }

    /**
     * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
     */
    @Override
    public String getFilename(ReportRequest request) {
        return getFilenameBase(request) + ".xlsx";
    }
}