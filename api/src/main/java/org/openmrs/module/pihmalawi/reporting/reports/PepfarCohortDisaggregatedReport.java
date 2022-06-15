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
package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PepfarCohortDisaggregatedReport extends ApzuReportManager {

    public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/pepfar-cohort-disaggregated.sql";
    public static final String EXCEL_REPORT_DESIGN_UUID = "8f3f891f-c092-4f5d-8ce0-69f57ec22cde";


    @Override
    public String getUuid() {
        return "e450988d-42d7-41b1-a99c-ad82a8b06226";
    }

    @Override
    public String getName() {
        return "PEPFAR - Cohort Disaggregated Report";
    }

    @Override
    public String getDescription() {
        return "PEPFAR Cohort Disaggregated Report, Revision June 2022";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        l.add(ReportingConstants.START_DATE_PARAMETER);
        l.add(ReportingConstants.END_DATE_PARAMETER);
        l.add(ReportingConstants.LOCATION_PARAMETER);
        return l;
    }

    /**
     * @see ApzuReportManager#constructReportDefinition()
     */
    @Override
    public ReportDefinition constructReportDefinition() {

        ReportDefinition rd = new ReportDefinition();
        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.setParameters(getParameters());

        SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();
        dsd.setParameters(getParameters());
        dsd.setConnectionPropertyFile(PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
        dsd.setSqlResource(SQL_DATA_SET_RESOURCE);

        rd.addDataSetDefinition("pepfarCohort", Mapped.mapStraightThrough(dsd));

        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, "PepfarCohortDisaggregatedReport.xls");
        design.addPropertyValue("repeatingSections", "sheet:1,row:5,dataset: pepfarCohort");
        l.add(design);
        return l;
    }

}
