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

package org.openmrs.module.pihmalawi.alert;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests the AlertEngine
 */
public class AlertEngineTest {

    AlertEngine engine = new AlertEngine();

    @Test
    public void shouldTestFunctionsAndConstants() throws Exception {

        test("yearsBetween", 2000, 3, 1, 1980, 2, 1, 20);
        test("yearsBetween", 2000, 3, 1, 1980, 3, 1, 20);
        test("yearsBetween", 2000, 3, 1, 1980, 3, 2, 19);
        test("yearsBetween", 2000, 3, 1, 1980, 2, 28, 20);
        test("yearsBetween", 2000, 1, 1, 1999, 12, 31, 0);

        test("monthsBetween", 2005, 3, 5, 2000, 2, 5, 61);
        test("monthsBetween", 2005, 3, 5, 2000, 12, 6, 50);
        test("monthsBetween", 2005, 3, 5, 2000, 10, 4, 53);

        test("daysBetween", 2020, 3, 1, 2020, 2, 1, 29);
        test("daysBetween", 2021, 3, 1, 2021, 2, 1, 28);
        test("daysBetween", 2005, 1, 31, 2004, 10, 24, 99);

        ScriptEngine scriptEngine = engine.createScriptEngine(ObjectUtil.toMap("hello", "world"));

        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing()"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing(null)"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing(\"\")"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("missing('')"));
        Assert.assertEquals(Boolean.FALSE, scriptEngine.eval("missing('hello')"));
        Assert.assertEquals(Boolean.FALSE, scriptEngine.eval("missing(hello)"));
        Assert.assertEquals(Boolean.TRUE, scriptEngine.eval("has([1, 2, 3], 1)"));
        Assert.assertEquals(Boolean.FALSE, scriptEngine.eval("has([1, 2, 3], 4)"));

        Assert.assertNotNull(scriptEngine.eval("active_art"));
    }

    protected void test(String function, int y1, int m1, int d1, int y2, int m2, int d2, double expected) throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("t1", DateUtil.getDateTime(y1, m1, d1).getTime());
        variables.put("t2", DateUtil.getDateTime(y2, m2, d2).getTime());
        Object res = engine.createScriptEngine(variables).eval(function + "(t1, t2)");
        Assert.assertEquals(expected, res);
    }

    @Test
    public void shouldTestLoadingAlerts() throws Exception {
        List<AlertDefinition> alertDefinitions = engine.getAlertDefinitions();
        Assert.assertTrue(alertDefinitions.size() > 0);
    }
}
