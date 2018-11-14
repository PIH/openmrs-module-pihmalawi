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
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.alert.AlertDefinition;
import org.openmrs.module.pihmalawi.alert.AlertEngine;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectCounter;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Tests the IC3ScreeningData
 */
public class IC3ScreeningDataPerformanceTest extends StandaloneContextSensitiveTest {

    private final static Log log = LogFactory.getLog(IC3ScreeningDataPerformanceTest.class);

    @Autowired
    IC3ScreeningData screeningData;

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected void performTest() throws Exception {
        testDate(2018, 11, 8);
        //testDate(2017, 12, 27);
    }

    protected void testDate(int year, int month, int day) throws Exception {

        Date effectiveDate = DateUtil.getDateTime(year, month, day);

        log.info("----- TEST for " + effectiveDate + " -----");

        // No data before initialization
        Assert.assertEquals(0, screeningData.getDataByUuid().size());

        DataRefreshContext refreshContext = new DataRefreshContext();
        refreshContext.setEffectiveDate(effectiveDate);

        StopWatch sw = new StopWatch();
        sw.start();
        screeningData.refresh(refreshContext);
        sw.stop();
        log.info("Refreshed Data in: " + sw.toString());

        AlertEngine alertEngine = new AlertEngine();
        List<AlertDefinition> alertDefinitions = alertEngine.getAlertDefinitions();

        sw.reset();
        sw.start();
        Map<String, JsonObject> data = screeningData.getDataByUuid();

        ObjectCounter counter = new ObjectCounter();
        for (String patientUuid : data.keySet()) {
            JsonObject patientData = data.get(patientUuid);
            List<AlertDefinition> matches = alertEngine.evaluateMatchingAlerts(alertDefinitions, patientData);
            for (AlertDefinition match : matches) {
                counter.increment(match.getName());
            }
        }
        sw.stop();

        log.info("Evaluated Alerts for " + screeningData.getDataByUuid().size() + " patients in: " + sw.toString());
        for (AlertDefinition alertDefinition : alertDefinitions) {
            Object count = counter.getCount(alertDefinition.getName());
            log.info(alertDefinition.getName() + ": " + ObjectUtil.nvlStr(count, "0"));
        }

        screeningData.getDataByUuid().clear();
    }
}
