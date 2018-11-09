/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.data;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a foundational class to retain in-memory data for use by the system
 */
public abstract class LivePatientDataSet {

    private final static Log log = LogFactory.getLog(LivePatientDataSet.class);

    @Autowired
    PatientService patientService;

    @Autowired
    DataSetDefinitionService dataSetService;

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Autowired
    BaseCohortDefinitionLibrary baseCohorts;

    @Autowired
    BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    BasePatientDataLibrary basePatientData;

    @Autowired
    HivPatientDataLibrary hivPatientData;

    @Autowired
    ChronicCarePatientDataLibrary ccPatientData;

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    ChronicCareMetadata ccMetadata;

    @Autowired
    DataFactory df;

    //***** PROPERTIES *****

    private boolean refreshingData;
    private Date lastRefreshDate;
    private boolean lastRefreshSuccessful;
    private Map<String, JsonObject> dataByUuid;

    public Map<String, JsonObject> getDataByUuid() {
        if (dataByUuid == null) {
            dataByUuid = new HashMap<String, JsonObject>();
        }
        return dataByUuid;
    }

    public boolean isRefreshingData() {
        return refreshingData;
    }

    public boolean isLastRefreshSuccessful() {
        return lastRefreshSuccessful;
    }

    public Date getLastRefreshDate() {
        return lastRefreshDate;
    }

    public void setLastRefreshDate(Date lastRefreshDate) {
        this.lastRefreshDate = lastRefreshDate;
    }

    //***** ABSTRACT METHODS *****

    /**
     * @return the DataSetDefinition for this data
     */
    public abstract DataSetDefinition constructDataSetDefinition();

    /**
     * @return the CohortDefinition to determine which patients should be loaded refresh
     */
    public abstract CohortDefinition constructCohortDefinition();

    /**
     * @return the column in the dataset used to identify a unique piece of data
     */
    public String getUuidColumn() {
        return "uuid";
    }

    //***** METHODS *****

    /**
     * @return the data for a patient matching the given uuid, loading the data if necessary
     */
    public JsonObject getDataForPatient(String patientUuid) {
        JsonObject data = getDataByUuid().get(patientUuid);
        if (data == null) {
            Patient p = patientService.getPatientByUuid(patientUuid);
            if (p == null) {
                throw new IllegalArgumentException("Unable to find patient with uuid: " + patientUuid);
            }
            refresh(p, new DataRefreshContext());
        }
        return getDataByUuid().get(patientUuid);
    }

    /**
     * Updates the data for a single patient, using the passed date as the effectiveDate.  Intended to be called after a patient record is updated
     */
    public void refresh(Patient p, DataRefreshContext dataRefreshContext) {
        Cohort c = new Cohort();
        c.addMember(p.getPatientId());
        refresh(c, dataRefreshContext);
    }

    /**
     * This would be used to do a full refresh, based on the configured cohort
     */
    public void refresh(DataRefreshContext refreshContext) {
        refreshingData = true;
        try {
            log.debug("Refreshing: " + getClass().getSimpleName());
            StopWatch sw = new StopWatch();
            sw.start();
            EvaluationContext context = getEvaluationContext(refreshContext);
            CohortDefinition cd = constructCohortDefinition();
            try {
                Cohort c = cohortDefinitionService.evaluate(cd, context);
                log.debug("Number of patients to refresh: " + c.size());
                refresh(c, refreshContext);
            }
            catch (Exception e) {
                lastRefreshSuccessful = false;
                throw new RuntimeException("Error evaluating base refresh cohort for " + getClass(), e);
            }
            sw.stop();
            log.debug("Refresh time: " + sw.toString());
            lastRefreshSuccessful = true;
            lastRefreshDate = new Date();
        }
        finally {
            refreshingData = false;
        }
    }

    /**
     * Loads/updates the data for a Cohort of patients.  Intended to be called when initializing or refreshing the data
     */
    public void refresh(Cohort cohort, DataRefreshContext refreshContext) {
        EvaluationContext context = getEvaluationContext(refreshContext);
        context.setBaseCohort(cohort);
        DataSetDefinition dsd = constructDataSetDefinition();
        try {
            DataSet ds = dataSetService.evaluate(dsd, context);
            Map<String, JsonObject> newDataByUuid = new HashMap<String, JsonObject>();
            for (DataSetRow row : ds) {
                JsonObject so = new JsonObject();
                so.put("today", refreshContext.getEffectiveDate().getTime());
                for (DataSetColumn c : row.getColumnValues().keySet()) {
                    so.put(c.getName(), row.getColumnValues().get(c));
                }
                String uuid = (String)row.getColumnValue(getUuidColumn());
                if (uuid == null) {
                    throw new RuntimeException("No uuid found for data set row: " + row);
                }
                newDataByUuid.put(uuid, so);
            }
            if (refreshContext.isReplaceAllData()) {
                dataByUuid = newDataByUuid;
            }
            else {
                getDataByUuid().putAll(newDataByUuid);
            }
        }
        catch (EvaluationException e) {
            throw new RuntimeException("Error refreshing data set for " + getClass(), e);
        }
    }

    /**
     * Utility/convenience method to map straight through the mappings
      */
    protected void addColumn(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
        dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd));
    }

    /**
     * Get initial evaluation context, which contains the date parameter for the report
     */
    protected EvaluationContext getEvaluationContext(DataRefreshContext refreshContext) {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), refreshContext.getEffectiveDate());
        return context;
    }
}
