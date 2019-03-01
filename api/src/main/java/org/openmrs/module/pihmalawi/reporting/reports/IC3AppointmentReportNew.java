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

import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.IC3AppoinmentDataSetDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IC3AppointmentReportNew extends ApzuReportManager {

    public static final String EXCEL_REPORT_DESIGN_UUID = "4941857d-0b4e-4742-9302-390de0b826d1";
    public static final Parameter ADVANCED_CARE_FILTER_PARAMETER = new Parameter("advancedCare", "Advanced Care Patients", Boolean.class);

	@Override
	public String getUuid() {
		return "99c079a8-9297-47f4-b1a3-69888b3aa544";
	}

	@Override
	public String getName() {
		return "IC3 - Appointment Report New";
	}

    @Override
    public String getDescription() {
        return "IC3 Appointment Report, revision March 2019";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        l.add(ReportingConstants.END_DATE_PARAMETER);
        l.add(ReportingConstants.LOCATION_PARAMETER);
        l.add(ADVANCED_CARE_FILTER_PARAMETER);
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
        rd.addDataSetDefinition("apptRpt", Mapped.mapStraightThrough(constructDataSetDefinition()));
        return rd;
    }

    public DataSetDefinition constructDataSetDefinition() {
        IC3AppoinmentDataSetDefinition dsd = new IC3AppoinmentDataSetDefinition();
        dsd.addParameters(getParameters());
        return dsd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, "AppointmentReport.xls");
        design.addPropertyValue("repeatingSections", "sheet:1,row:5,dataset:apptRpt");
        l.add(design);
        return l;
    }
}
