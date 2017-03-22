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

package org.openmrs.module.pihmalawi.sql;

import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the MysqlRunner class
 */
public class SqlRunnerTest extends StandaloneContextSensitiveTest {

	@Autowired
    EvaluationService evaluationService;

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    protected void performTest() throws Exception {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("gender", "F");

        SqlResult result = SqlRunner.executeSqlResource("org/openmrs/module/pihmalawi/sql/simpleScript.sql", parameters);

        System.out.println(result.getData().size() + " results found");
        System.out.println("------ RESULTS -----");
        System.out.println(OpenmrsUtil.join(result.getColumns(), "\t\t"));
        for (Map<String, String> row : result.getData()) {
            System.out.println(OpenmrsUtil.join(row.values(), "\t\t"));
        }
        System.out.println("------ ERRORS -----");
        for (String error : result.getErrors()) {
            System.out.println(error);
        }
    }
}
