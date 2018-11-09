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

import org.junit.Assert;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.reporting.common.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Tests the IC3ScreeningData
 */
public class IC3ScreeningDataTest extends StandaloneContextSensitiveTest {

    @Autowired
    IC3ScreeningDataForTesting screeningData;

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    protected void performTest() throws Exception {

        // No data before initialization
        Assert.assertEquals(0, screeningData.getDataByUuid().size());

        screeningData.setEvaluationDate(DateUtil.getDateTime(2017, 12, 26));
        screeningData.refresh();

        Map<String, SimpleObject> data = screeningData.getDataByUuid();
        System.out.println("ScreeningData: " + data.size());
        for (String pId : data.keySet()) {
            System.out.println(pId + ": " + data.get(pId));
        }
    }
}
