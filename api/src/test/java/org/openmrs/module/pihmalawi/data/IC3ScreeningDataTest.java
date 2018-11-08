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
    IC3ScreeningData screeningData;

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected void performTest() throws Exception {
        Date testDate = DateUtil.getDateTime(2018, 11, 8);
        screeningData.refresh(testDate);
        Map<String, SimpleObject> data = screeningData.getDataByUuid();
        System.out.println("ScreeningData: " + data.size());
        for (String pId : data.keySet()) {
            System.out.println(pId + ": " + data.get(pId));
        }
    }
}
