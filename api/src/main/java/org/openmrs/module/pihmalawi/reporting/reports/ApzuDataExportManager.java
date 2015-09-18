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

import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
public abstract class ApzuDataExportManager extends ApzuReportManager {

    /**
     * @return the uuid for the report design for exporting to Excel
     */
    public abstract String getExcelDesignUuid();

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign excelDesign = createExcelDesign(getExcelDesignUuid(), reportDefinition);
        excelDesign.addPropertyValue(XlsReportRenderer.PASSWORD_PROPERTY, "apzu123");
        l.add(excelDesign);
        return l;
    }
}
