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

import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.ApzuReportUtil;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.BaseReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
public abstract class ApzuReportManager extends BaseReportManager {

    @Autowired
    DataFactory df;

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    ChronicCareMetadata ccMetadata;

    @Autowired
    HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    HivEncounterQueryLibrary encounterQueries;

    @Autowired
    BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    BasePatientDataLibrary basePatientData;

    @Autowired
    HivPatientDataLibrary hivPatientData;

    @Autowired
    ChronicCarePatientDataLibrary ccPatientData;

    @Autowired
    BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    BaseEncounterDataLibrary baseEncounterData;

	protected void addColumn(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd));
	}

	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		addColumn(dsd, columnName, new PatientToEncounterDataDefinition(pdd));
	}

	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, EncounterDataDefinition edd) {
		dsd.addColumn(columnName, edd, ObjectUtil.toString(Mapped.straightThroughMappings(edd), "=", ","));
	}

    protected void addColumn(ObsDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
        addColumn(dsd, columnName, new PatientToObsDataDefinition(pdd));
    }

    protected void addColumn(ObsDataSetDefinition dsd, String columnName, EncounterDataDefinition edd) {
        addColumn(dsd, columnName, new EncounterToObsDataDefinition(edd));
    }

    protected void addColumn(ObsDataSetDefinition dsd, String columnName, ObsDataDefinition odd) {
        dsd.addColumn(columnName, odd, ObjectUtil.toString(Mapped.straightThroughMappings(odd), "=", ","));
    }

	protected ReportDesign createExcelTemplateDesign(String reportDesignUuid, ReportDefinition reportDefinition, String templatePath) {
		String resourcePath = ReportUtil.getPackageAsPath(getClass()) + "/" + templatePath;
		return ReportManagerUtil.createExcelTemplateDesign(reportDesignUuid, reportDefinition, resourcePath);
	}

    protected ReportDesign createExcelDesign(String reportDesignUuid, ReportDefinition reportDefinition) {
		return ApzuReportUtil.createExcelDesign(reportDesignUuid, reportDefinition);
	}

    protected ReportRequest createMonthlyScheduledReportRequest(String requestUuid, String reportDesignUuid, Map<String, Object> parameters, ReportDefinition reportDefinition) {
        try {
            ReportRequest rr = new ReportRequest();
            rr.setUuid(requestUuid);
            rr.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, parameters));
            rr.setPriority(ReportRequest.Priority.NORMAL);
            rr.setProcessAutomatically(true);
            rr.setRenderingMode(new RenderingMode(XlsReportRenderer.class.newInstance(), "Excel", reportDesignUuid, Integer.MAX_VALUE));
            rr.setSchedule("0 0 4 1 * ?"); // Run monthly on the first of the month at 4:00am
            rr.setMinimumDaysToPreserve(45);
            return rr;
        }
        catch (Exception e) {
            throw new IllegalStateException("Error constructing scheduled report", e);
        }
    }

    public <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
        if (parameterizable == null) {
            throw new NullPointerException("Programming error: missing parameterizable");
        }
        if (mappings == null) {
            mappings = ""; // probably not necessary, just to be safe
        }
        return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }

    @Override
    public String getVersion() {
        return "1.2-SNAPSHOT";
    }
}
