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
import org.openmrs.module.pihmalawi.reporting.definition.renderer.TraceReportRenderer;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IC3TraceReport extends ApzuReportManager {

    public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/trace-data.sql";
    public static final Parameter LAB_WEEKS_PARAMETER = new Parameter("labWeeks", "Weeks of lab results to include", Integer.class);
    public static final String DATA_SET_KEY = "trace";

    @Override
	public String getUuid() {
		return "00ac1e0f-7d67-4566-b88f-2f4f06537ffa";
	}

	@Override
	public String getName() {
		return "IC3 - TRACE Report";
	}

    @Override
    public String getDescription() {
        return "TRACE Report, revision April 2026";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<>();
        l.add(ReportingConstants.END_DATE_PARAMETER);
        l.add(ReportingConstants.LOCATION_PARAMETER);
        l.add(LAB_WEEKS_PARAMETER);
        return l;
    }

	public String getReportDesignUuid() {
		return "12d1c2e0-687f-4abe-b93f-674af5ada06f";
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
        dsd.setConnectionPropertyFile(PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
        dsd.setSqlResource(SQL_DATA_SET_RESOURCE);
        dsd.setMetadataParameterConversion(SqlFileDataSetDefinition.MetadataParameterConversion.NAME);
        dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);
        dsd.addParameter(ReportingConstants.LOCATION_PARAMETER);
        dsd.addParameter(LAB_WEEKS_PARAMETER);
        rd.addDataSetDefinition(DATA_SET_KEY, Mapped.mapStraightThrough(dsd));

        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<>();
        ReportDesign design = new ReportDesign();
        design.setUuid(getReportDesignUuid());
        design.setName("Excel");
        design.setReportDefinition(reportDefinition);
        design.setRendererType(TraceReportRenderer.class);
        l.add(design);
        return l;
    }
}
