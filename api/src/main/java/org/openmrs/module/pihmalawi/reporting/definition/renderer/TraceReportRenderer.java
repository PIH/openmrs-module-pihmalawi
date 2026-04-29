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
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.ApzuReportUtil;
import org.openmrs.module.pihmalawi.reporting.reports.IC3TraceReport;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ExcelBuilder;
import org.openmrs.module.reporting.common.ObjectUtil;
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
import java.util.Date;

/**
 * Renderer for the TraceReport
 */
@Handler
public class TraceReportRenderer extends ExcelTemplateRenderer {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {

        log.debug("Rendering Trace Report...");
        ExcelBuilder builder = new ExcelBuilder();
        Date reportDate = (Date) reportData.getContext().getParameterValue(ReportingConstants.END_DATE_PARAMETER.getName());
        Location location = (Location) reportData.getContext().getParameterValue(ReportingConstants.LOCATION_PARAMETER.getName());

        String reportDateStr = DateUtil.formatDate(reportDate, "dd-MMM-yyyy");
        String locationName = location.getName();

        builder.newSheet("TRACE - " + locationName);

        builder.hideGridlinesInCurrentSheet();
        builder.setLandscape();
        builder.fitColumnsToPage();

        builder.addCell("");
        builder.addCell("TRACE - " + locationName + ", " + reportDateStr, "bold,size=18").merge(10, 0);
        builder.nextRow();

        for (int i = 0; i < 12; i++) {
            builder.addCell("");
        }
        builder.addCell("Reason for Contact", "size=10,align=center").merge(2, 0);
        for (int i = 0; i < 8; i++) {
            builder.addCell("");
        }
        builder.addCell("Outcome (Missed Visit Only)", "size=10,align=center").merge(6, 0);

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
        builder.addCell("TA", headerStyle1, 20);
        builder.addCell("CHW", headerStyle1, 20);
        builder.addCell("First", headerStyle1, 12);
        builder.addCell("Last", headerStyle1, 15);
        builder.addCell("Birthdate", headerStyle1Centered, 12);
        builder.addCell("ART#", headerStyle1, 15);
        builder.addCell("EID#", headerStyle1, 15);
        builder.addCell("NCD#", headerStyle1, 15);
        builder.addCell("PDC#", headerStyle1, 15);
        builder.addCell("Mental Health#", headerStyle1, 15);
        builder.addCell("(1) Missed visit", headerStyle2 + leftBorderedLight + rightBorderedLight, 4);
        builder.addCell("(2) Lab results ready", headerStyle2 + leftBorderedLight + rightBorderedLight, 4);
        builder.addCell("(3) Due for lab work\n(viral load for EID)", headerStyle2 + leftBorderedLight + rightBorderedLight, 8);
        builder.addCell("Date\nPatient\nShould Visit", headerStyle1Centered, 18);
        builder.addCell("Priority\nPatient", headerStyle1Centered, 8);
        builder.addCell("Diagnoses", headerStyle1Centered, 20);
        builder.addCell("PDC Conditions", headerStyle1Centered, 20);
        builder.addCell("Visit missed", headerStyle1Centered, 20);
        builder.addCell("Last IC3\nVisit Date", headerStyle1Centered + leftBorderedLight, 12);
        builder.addCell("Appointment\nDate", headerStyle1Centered, 14);
        builder.addCell("Weeks\nout of\nCare", headerStyle1Centered, 8);
        builder.addCell(builder.createRichTextString("Patient actually\nvisited clinic.", headerStyle2, "\nComplete Mastercard Update", headerStyle3), headerStyle2 + leftBorderedLight, 8);
        builder.addCell("Transferred Out", headerStyle2, 4);
        builder.addCell("Died", headerStyle2, 4);
        builder.addCell("Stopped", headerStyle2, 4);
        builder.addCell("Missed Appt", headerStyle2, 4);
        builder.addCell("Patient Not Found", headerStyle2 + ",border=right", 4);
        builder.addCell("Patient Outside Neno", headerStyle2 + ",border=right", 4);

        // Set this row to repeat when printing on subsequent pages
        int rowNum = builder.getCurrentRowNum();
        int colNum = builder.getCurrentColNum();
        builder.getCurrentSheet().setRepeatingRows(new CellRangeAddress(rowNum, rowNum, 0, colNum));

        builder.nextRow();

        SimpleDataSet ds = (SimpleDataSet)reportData.getDataSets().get(IC3TraceReport.DATA_SET_KEY);
        DataSetRowList rows = ds.getRows();
        addExtraRowsToDataSet(rows);

        for (int i = 0; i < rows.size(); i++) {

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

            builder.addCell(i + 1, "color=" + HSSFColor.GREY_50_PERCENT.index);

            builder.addCell(row.getColumnValue("village"), rowStyle + ",border=left");
            builder.addCell(row.getColumnValue("traditional_authority"), rowStyle);
            builder.addCell(row.getColumnValue("chw"), rowStyle);
            builder.addCell(row.getColumnValue("first_name"), rowStyle);
            builder.addCell(row.getColumnValue("last_name"), rowStyle);
            builder.addCell(row.getColumnValue("birthdate"), dateRowStyle);
            builder.addCell(row.getColumnValue("art_number"), rowStyle);
            builder.addCell(row.getColumnValue("eid_number"), rowStyle);
            builder.addCell(row.getColumnValue("ncd_number"), rowStyle);
            builder.addCell(row.getColumnValue("pdc_number"), rowStyle);
            builder.addCell(row.getColumnValue("ncd_number"), rowStyle);

            Number weeksOutOfCare = (Number) row.getColumnValue("art_weeks_out_of_care");
            Date lastVisitDate = (Date) row.getColumnValue("art_last_visit_date");
            Date lastApptDate = (Date) row.getColumnValue("art_last_appt_date");

            Number eidWeeksOutOfCare = (Number) row.getColumnValue("eid_weeks_out_of_care");
            if (eidWeeksOutOfCare != null) {
                if (weeksOutOfCare == null || eidWeeksOutOfCare.doubleValue() > weeksOutOfCare.doubleValue()) {
                    weeksOutOfCare = eidWeeksOutOfCare;
                    lastVisitDate = (Date) row.getColumnValue("eid_last_visit_date");
                    lastApptDate = (Date) row.getColumnValue("eid_last_appt_date");
                }
            }

            Number ncdWeeksOutOfCare = (Number) row.getColumnValue("ncd_weeks_out_of_care");
            if (ncdWeeksOutOfCare != null) {
                if (weeksOutOfCare == null || ncdWeeksOutOfCare.doubleValue() > weeksOutOfCare.doubleValue()) {
                    weeksOutOfCare = ncdWeeksOutOfCare;
                    lastVisitDate = (Date) row.getColumnValue("ncd_last_visit_date");
                    lastApptDate = (Date) row.getColumnValue("ncd_last_appt_date");
                }
            }

            Number pdcWeeksOutOfCare = (Number) row.getColumnValue("pdc_weeks_out_of_care");
            if (pdcWeeksOutOfCare != null) {
                if (weeksOutOfCare == null || pdcWeeksOutOfCare.doubleValue() > weeksOutOfCare.doubleValue()) {
                    weeksOutOfCare = pdcWeeksOutOfCare;
                    lastVisitDate = (Date) row.getColumnValue("pdc_last_visit_date");
                    lastApptDate = (Date) row.getColumnValue("pdc_last_appt_date");
                }
            }

            String traceCriteria = (String) row.getColumnValue("trace_criteria");

            boolean lateHiv = hasTraceCriteria(traceCriteria, "LATE_ART", "LATE_EID");
            boolean lateNcd = hasTraceCriteria(traceCriteria, "LATE_NCD");
            boolean latePdc = hasTraceCriteria(traceCriteria, "LATE_PDC");
            boolean lateVisit = lateHiv || lateNcd || latePdc;

            boolean labReady = hasTraceCriteria(traceCriteria, "HIGH_VIRAL_LOAD", "EID_POSITIVE_6_WK", "EID_NEGATIVE");
            boolean labDue = hasTraceCriteria(traceCriteria, "REPEAT_VIRAL_LOAD", "EID_12_MONTH_TEST", "EID_24_MONTH_TEST", "EID_6_WEEK_TEST");

            String priorityCriteria = (String) row.getColumnValue("priority_criteria");
            boolean isPriorityPatient = lateHiv || hasTraceCriteria(traceCriteria, "REPEAT_VIRAL_LOAD", "EID_POSITIVE_6_WK") || (lateNcd && ObjectUtil.notNull(priorityCriteria));

            String dateToVisit = "";
            if (lateHiv || hasTraceCriteria(traceCriteria, "EID_POSITIVE_6_WK", "EID_NEGATIVE")) {
                dateToVisit = "Today";
            } else if (hasTraceCriteria(traceCriteria, "HIGH_VIRAL_LOAD", "LATE_NCD", "LATE_PDC")) {
                dateToVisit = "Next Clinic Day";
            } else if (ObjectUtil.notNull(traceCriteria)) {
                dateToVisit = "Appointment Date";
            }

            builder.addCell((lateVisit ? "✓" : ""), centeredRowStyle + leftBorderedLight + rightBorderedLight); // MISSED VISIT
            builder.addCell((labReady ? "✓" : ""), centeredRowStyle + leftBorderedLight + rightBorderedLight); // LAB RESULTS READY
            builder.addCell((labDue ? "✓" : ""), centeredRowStyle + leftBorderedLight + rightBorderedLight); // DUE FOR LAB WORK (VIRAL LOAD FOR EID)
            builder.addCell(dateToVisit, rowStyle + ",align=center");
            builder.addCell(isPriorityPatient ? "!!!" : "", centeredRowStyle + ",color=" + HSSFColor.RED.index);
            builder.addCell(row.getColumnValue("DIAGNOSES"), rowStyle);
            String pdcConditions = (String) row.getColumnValue("pdc_conditions");
            String pdcNonCodedConditions = (String) row.getColumnValue("pdc_non_coded_conditions");
            builder.addCell((pdcConditions == null ? "" : pdcConditions) + (pdcNonCodedConditions == null ? "" : (pdcConditions == null ? pdcNonCodedConditions : ", " + pdcNonCodedConditions)), rowStyle);
            String lastVisitType = null;
            if (lateHiv) {
                lastVisitType = "ART_FOLLOWUP";
            }
            if (lateNcd) {
                lastVisitType = (lastVisitType != null) ? (lastVisitType + ", " + row.getColumnValue("NCD_LAST_VISIT_TYPE")) : (String) row.getColumnValue("NCD_LAST_VISIT_TYPE");
            }
            if (latePdc) {
                lastVisitType = (lastVisitType != null) ? (lastVisitType + ", " + row.getColumnValue("PDC_LAST_VISIT_TYPE")) : (String) row.getColumnValue("PDC_LAST_VISIT_TYPE");
            }
            builder.addCell(lastVisitType, rowStyle);
            String redactIfNeeded = (ObjectUtil.isNull(traceCriteria) || lateVisit ? "" : blackout);

            builder.addCell(lastVisitDate, dateRowStyle + leftBorderedLight + redactIfNeeded);
            builder.addCell(lastApptDate, dateRowStyle + redactIfNeeded);
            builder.addCell(weeksOutOfCare, centeredRowStyle + ",format=0.0" + redactIfNeeded);

            for (int j = 0; j < 7; j++) {
                String border = (j == 0 ? leftBorderedLight : j == 5 ? ",border=right" : "");
                builder.addCell("☐", centeredRowStyle + ",size=18" + border + redactIfNeeded);
            }
            builder.nextRow();
        }

        builder.write(out, ApzuReportUtil.getExcelPassword());
    }

    private void addExtraRowsToDataSet(DataSetRowList ds) {
        for (int i=0; i<5; i++) {
            ds.add(new DataSetRow());
        }
    }

    private boolean hasTraceCriteria(String traceCriteria, String... checkAny) {
        if (traceCriteria != null && checkAny != null) {
            for (String s : checkAny) {
                if (traceCriteria.toLowerCase().trim().contains(s.toLowerCase().trim()))  {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
     */
    @Override
    public String getFilename(ReportRequest request) {
        return getFilenameBase(request) + ".xlsx";
    }
}
