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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.util.OpenmrsClassLoader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Creates a context and evalutes alerts for this context
 */
public class AlertEngine {

    private final static Log log = LogFactory.getLog(AlertEngine.class);

    public static final String SCRIPT_ENGINE_NAME = "JavaScript";
    public static final String FUNCTIONS_RESOURCE = "org/openmrs/module/pihmalawi/alert/functions.js";
    public static final String ALERT_DEFINITIONS_RESOURCE = "org/openmrs/module/pihmalawi/alert/definitions";
    public static final String CONSTANTS_RESOURCE = "org/openmrs/module/pihmalawi/alert/constants.json";

    private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private ObjectMapper objectMapper = new ObjectMapper();


    //***** CONSTRUCTORS *****

    public AlertEngine() {
    }

    //***** METHODS *****

    /**
     * Returns the alert with the given name
     */
    public AlertDefinition getAlertDefinition(String name) {
        for (AlertDefinition d : getAlertDefinitions()) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        return null;
    }

    /**
     * @return all of the alert definitions defined as JSON in the classpath
     */
    public List<AlertDefinition> getAlertDefinitions() {
        List<AlertDefinition> ret = new ArrayList<AlertDefinition>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(OpenmrsClassLoader.getInstance().getResourceAsStream(ALERT_DEFINITIONS_RESOURCE)));
            for (String resource = reader.readLine(); resource != null; resource = reader.readLine()) {
                String json = ReportUtil.readStringFromResource(ALERT_DEFINITIONS_RESOURCE + "/" + resource);
                List<AlertDefinition> alertDefinitionList = objectMapper.readValue(json, new TypeReference<List<AlertDefinition>>() { });
                ret.addAll(alertDefinitionList);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to load alert definitions", e);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
        return ret;
    }

    /**
     * Given a List of alerts, and variables to check, return a new List of alerts that conditionally pass given variables
     */
    public List<AlertDefinition> evaluateMatchingAlerts(List<AlertDefinition> alerts, Map<String, Object> variableBindings) {
        ScriptEngine scriptEngine = createScriptEngine(variableBindings);
        List<AlertDefinition> ret = new ArrayList<AlertDefinition>();
        if (alerts != null) {
            for (AlertDefinition alert : alerts) {
                boolean include = true;
                if (alert.isEnabled()) {
                    log.debug("Evaluating alert: " + alert.getName());
                    for (String condition : alert.getConditions()) {
                        try {
                            if (include) {
                                include = include && ((Boolean) scriptEngine.eval(condition));
                                log.debug(condition + " -> " + include);
                            }
                        }
                        catch (Exception e) {
                            throw new RuntimeException("Error evaluating condition: " + condition, e);
                        }
                    }
                    if (include) {
                        ret.add(alert);
                    }
                }
                else {
                    log.debug("Alert " + alert.getName() + " is not enabled");
                }
            }
        }
        return ret;
    }

    /**
     * Evaluates the Javascript from the classpath at the given resource in the given engine
     */
    public void evaluateResource(ScriptEngine scriptEngine, String resource) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(OpenmrsClassLoader.getInstance().getResourceAsStream(resource));
            scriptEngine.eval(reader);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to initialize script engine", e);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Evaluates the Javascript from the classpath at the given resource in the given engine
     */
    public <T> T evaluateExpression(ScriptEngine scriptEngine, String expression, Class<T> type) {
        try {
            return (T) scriptEngine.eval(expression);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error evaluating expression -- " + expression + " --");
        }
    }

    /**
     * Initialize a new script engine with all of the functions, and the passed variables bound
     */
    protected ScriptEngine createScriptEngine(Map<String, Object> variables) {
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(SCRIPT_ENGINE_NAME);
        scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put("polyglot.js.allowAllAccess", true);
        evaluateResource(scriptEngine, FUNCTIONS_RESOURCE);
        JsonObject constants = JsonObject.fromJsonResource(CONSTANTS_RESOURCE);
        for (String key : constants.keySet()) {
            Object val = constants.get(key);
            scriptEngine.put(key, val);
            log.debug("Added constant " + key + " = " + val + (val == null ? "" : " (" + val.getClass().getSimpleName() + ")"));
        }
        if (variables != null) {
            for (String key : variables.keySet()) {
                if (scriptEngine.get(key) != null) {
                    throw new IllegalArgumentException("Unable to create script engine.  Variable " + key + " already defined.");
                }
                Object val = variables.get(key);
                if (val instanceof Date) {
                    val = ((Date)val).getTime();
                }
                else if (val instanceof OpenmrsObject) {
                    val = ((OpenmrsObject)val).getUuid();
                }
                scriptEngine.put(key, val);
                log.debug("Added variable " + key + " = " + val + (val == null ? "" : " (" + val.getClass().getSimpleName() + ")"));

            }
        }
        return scriptEngine;
    }
}
