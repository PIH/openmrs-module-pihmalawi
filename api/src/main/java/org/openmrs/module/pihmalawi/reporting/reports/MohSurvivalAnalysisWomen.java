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
public class MohSurvivalAnalysisWomen extends ApzuReportManager {

    public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/moh-survival-analysis-women.sql";
    public static final String EXCEL_REPORT_DESIGN_UUID = "86da0482-4d6f-4eb4-8f61-da401eb3576a";
    public static final String LOCATION_NAME_PARAM = "location";


    @Override
    public String getUuid() {
        return "2071ed4d-0d63-41c7-ad3e-0c89fe255bb7";
    }

    @Override
    public String getName() {
        return "MOH - SURVIVAL ANALYSIS WOMEN Report";
    }

    @Override
    public String getDescription() {
        return "MOH Survival Analysis, Revision February 2022";
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

        rd.addDataSetDefinition("mohSurvivalAnalysisReport", Mapped.mapStraightThrough(dsd));

        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, "mohSurvivalAnalysisWomen.xls");
        design.addPropertyValue("repeatingSections", "sheet:1,row:5,dataset: mohSurvivalAnalysisReport");
        l.add(design);
        return l;
    }

}
