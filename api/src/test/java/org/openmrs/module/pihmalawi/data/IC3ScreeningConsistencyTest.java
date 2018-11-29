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

package org.openmrs.module.pihmalawi.data;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.api.IC3Service;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectCounter;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Tests the IC3ScreeningData
 */
public class IC3ScreeningConsistencyTest extends StandaloneContextSensitiveTest {

    private final static Log log = LogFactory.getLog(IC3ScreeningConsistencyTest.class);

    @Autowired
    IC3ScreeningData screeningData;

    @Autowired
    IC3Service ic3Service;

    @Autowired
    HivMetadata metadata;

    @Autowired
    BaseCohortDefinitionLibrary baseCohorts;

    @Autowired
    DataFactory df;

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    protected void performTest() throws Exception {
        loadWarehouseConfigProperties();
        testDate(2018, 11, 8, metadata.getNenoHospital());
        testDate(2018, 11, 8, metadata.getNenoHospital());
        testDate(2018, 11, 8, metadata.getNenoHospital());
    }

    protected void testDate(int year, int month, int day, Location location) throws Exception {

        Date endDate = DateUtil.getDateTime(year, month, day);
        log.warn("Testing for " + location + " on " + endDate);

        StopWatch sw = new StopWatch();
        sw.start();
        Map<String, JsonObject> currentData = getCurrentData(endDate, location);
        sw.stop();
        log.warn("Old data produced in: " + sw.toString());
        sw.reset();
        sw.start();
        Map<String, JsonObject> revisedData = getRevisedData(endDate, location);
        sw.stop();
        log.warn("New data produced in: " + sw.toString());

        Set<String> patientUuids = new TreeSet<String>();
        patientUuids.addAll(currentData.keySet());
        patientUuids.addAll(revisedData.keySet());

        log.warn("Num current: " + currentData.size());
        log.warn("Num revised: " + revisedData.size());

        ObjectCounter<String> counter = new ObjectCounter();

        for (String patientUuid : patientUuids) {
            JsonObject current = currentData.get(patientUuid);
            JsonObject revised = revisedData.get(patientUuid);

            if (current == null) {
                log.warn("PATIENT " + patientUuid + " IS NEW IN REVISED REPORT");
            }
            else {
                Assert.assertNotNull("PATIENT IN CURRENT APPOINTMENT REPORT IS NOT IN REVISED REPORT", revised);
                for (String property : current.keySet()) {
                    Object curVal = current.get(property);
                    Object revVal = revised.get(property);
                    if ("age".equals(property)) {
                        curVal = curVal == null ? null : ((Number)curVal).intValue();
                        revVal = (revised.get("age_years") == null ? null : ((Number)revised.get("age_years")).intValue());
                    }
                    if (!"alert".equals(property) && !"actions".equals(property) && !"labTests".equals(property)) {
                        if (!ObjectUtil.areEqual(curVal, revVal)) {
                            counter.increment(property);
                            log.warn(property + " OLD: " + curVal + " , NEW: " + revVal);
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> counterEntry : counter.getAllObjectCounts().entrySet()) {
            log.warn(counterEntry.getKey() + ": " + counterEntry.getValue());
        }
    }

    protected Map<String, JsonObject> getRevisedData(Date endDate, Location location) {
        StopWatch sw = new StopWatch();
        sw.start();
        Cohort cohort = screeningData.getPatientsWithAppointmentsAtLocation(endDate, location);
        sw.stop();
        log.warn("Got cohort in " + sw.toString());
        sw.reset();
        sw.start();
        Map<Integer, JsonObject> m = screeningData.getDataForCohort(cohort, endDate, location, true);
        Map<String, JsonObject> ret = new HashMap<String, JsonObject>();
        for (JsonObject o : m.values()) {
            ret.put((String)o.get("patient_uuid"), o);
        }
        sw.stop();
        log.warn("Got data in " + sw.toString());
        return ret;
    }

    protected Map<String, JsonObject> getCurrentData(Date endDate, Location location) {
        Map<String, JsonObject> ret = new HashMap<String, JsonObject>();
        String locationUuid = (location == null ? null : location.getUuid());
        String endDateStr = DateUtil.formatDate(endDate, "yyyy-MM-dd");
        List<Map<String, Object>> currentDataRaw = ic3Service.getIC3AppointmentData(locationUuid, endDateStr, null);
        for (Map<String, Object> p : currentDataRaw) {
            JsonObject o = new JsonObject();
            o.putAll(p);
            ret.put((String)p.get("patient_uuid"), o);
        }
        return ret;
    }

    protected void loadWarehouseConfigProperties() throws Exception {
        String url = "jdbc:mysql://localhost:3308/openmrs_warehouse?autoReconnect=true&sessionVariables=storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String password = "root";
        File propertiesFile = new File(OpenmrsConstants.APPLICATION_DATA_DIRECTORY, PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
        Properties properties = new Properties();
        properties.put("connection.url", url);
        properties.put("connection.username", user);
        properties.put("connection.password", password);
        properties.store(new FileOutputStream(propertiesFile), null);
    }
}
