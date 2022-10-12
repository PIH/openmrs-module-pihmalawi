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
public class MohTptNewInitiationReport extends ApzuReportManager {

    public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/moh-tpt-initiation.sql";
    public static final String EXCEL_REPORT_DESIGN_UUID = "916ca8dc-0af7-4bc0-bb0a-4331d71c4a42";


    @Override
    public String getUuid() {
        return "8cd64285-8795-48be-98c8-c54777f2aba1    ";
    }

    @Override
    public String getName() {
        return "MOH TPT New Initiation Report";
    }

    @Override
    public String getDescription() {
        return "The report disaggregates patients initiated on TB Preventive Therapy 3HP (3 months of Isoniazid and Rifapentine) and 6H(6 months of daily Isoniazid for TB preventive therapy).";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        l.add(ReportingConstants.START_DATE_PARAMETER);
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

        rd.addDataSetDefinition("mohTptNewInitiation", Mapped.mapStraightThrough(dsd));

        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, "MohTptNewInitiation.xls");
        design.addPropertyValue("repeatingSections", "sheet:1,row:5,dataset: mohTptNewInitiation");
        l.add(design);
        return l;
    }
}
