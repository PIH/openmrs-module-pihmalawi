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
public abstract class LiveDataSet {

    private final static Log log = LogFactory.getLog(LiveDataSet.class);

    @Autowired
    DataSetDefinitionService dataSetService;

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Autowired
    BaseCohortDefinitionLibrary baseCohorts;

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
    private Map<String, SimpleObject> dataByUuid;

    public Map<String, SimpleObject> getDataByUuid() {
        return dataByUuid;
    }

    public boolean isRefreshingData() {
        return refreshingData;
    }

    public Date getLastRefreshDate() {
        return lastRefreshDate;
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
     * Updates the data for a single patient, using the passed date as the effectiveDate.  Intended to be called after a patient record is updated
     */
    public void refresh(Integer patientId, Date effectiveDate) {
        Cohort c = new Cohort();
        c.addMember(patientId);
        refresh(c, effectiveDate);
    }

    public void refresh(Date effectiveDate) {
        refreshingData = true;
        try {
            dataByUuid = null;
            log.debug("Refreshing: " + getClass().getSimpleName());
            StopWatch sw = new StopWatch();
            sw.start();
            EvaluationContext context = new EvaluationContext();
            context.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), effectiveDate);
            CohortDefinition cd = constructCohortDefinition();
            try {
                Cohort c = cohortDefinitionService.evaluate(cd, context);
                log.debug("Number of patients to refresh: " + c.size());
                refresh(c, effectiveDate);
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
    public void refresh(Cohort cohort, Date effectiveDate) {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), effectiveDate);
        context.setBaseCohort(cohort);
        DataSetDefinition dsd = constructDataSetDefinition();
        try {
            DataSet ds = dataSetService.evaluate(dsd, context);
            for (DataSetRow row : ds) {
                SimpleObject so = new SimpleObject();
                for (DataSetColumn c : row.getColumnValues().keySet()) {
                    so.put(c.getName(), row.getColumnValues().get(c));
                }
                updateData((String)row.getColumnValue(getUuidColumn()), so);
            }
        }
        catch (EvaluationException e) {
            throw new RuntimeException("Error refreshing data set for " + getClass(), e);
        }
    }

    /**
     * Called to update the data for a given uuid
     */
    protected void updateData(String uuid, SimpleObject data) {
        if (dataByUuid == null) {
            dataByUuid = new HashMap<String, SimpleObject>();
        }
        dataByUuid.put(uuid, data);
    }

    /**
     * Utility/convenience method to map straight through the mappings
      */
    protected void addColumn(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
        dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd));
    }
}
