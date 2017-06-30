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

import org.openmrs.Location;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.LocationTags;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.renderer.TraceReportRenderer;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
// import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class NewAppointmentReport extends ApzuDataExportManager {

    public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/appointment-data.sql";

	@Autowired
	private HivMetadata hivMetadata;

	@Override
	public String getUuid() {
		return "5ffdcf34-5db2-11e7-be82-dfb5eb799ead";
	}

	@Override
	public String getName() {
		return "New Appointment Report";
	}

    @Override
    public String getDescription() {
        return "Appointment Report, revision June 2017";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        l.add(ReportingConstants.END_DATE_PARAMETER);
        l.add(ReportingConstants.LOCATION_PARAMETER);        
        return l;
    }

	public String getReportDesignUuid() {
		return "82359302-5db2-11e7-be82-dfb5eb799ead";
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

        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        dsd.setName(getName());
        dsd.setParameters(getParameters());
        rd.addDataSetDefinition(getName(), Mapped.mapStraightThrough(dsd));

        return rd;
    }

    public SqlFileDataSetDefinition getBaseDsd() {
        SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();
        dsd.setConnectionPropertyFile(PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE.getAbsolutePath());
        dsd.setSqlResource(SQL_DATA_SET_RESOURCE);
        dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);
        dsd.addParameter(ReportingConstants.LOCATION_PARAMETER);
        return dsd;
    }

    @Override
    public String getExcelDesignUuid() {
        return "5eb12066-5db4-11e7-be82-dfb5eb799ead";
    }
}
