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
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;

import javax.script.ScriptEngine;
import java.util.*;

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

    @Test
    public void shouldTestHasAnyFunction() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();

        List<String> symptomsList = Arrays.asList("656f10da-977f-11e1-8993-905e29aff6c1", "654a56be-977f-11e1-8993-905e29aff6c1");

        String[] tb_symptoms = new String[] {"fever", "nightSweats", "cough"};
        List<String> tbSymptomsList = Arrays.asList("656f10da-977f-11e1-8993-905e29aff6c1", "654a56be-977f-11e1-8993-905e29aff6c1");

        variables.put("current_symptoms", symptomsList); //nightSweats, weightLoss

        ScriptEngine scriptEngine = engine.createScriptEngine(variables);
        Object res = scriptEngine.eval( "hasAny(current_symptoms, " +
                "['656f10da-977f-11e1-8993-905e29aff6c1', '654a56be-977f-11e1-8993-905e29aff6c1'])");

        Assert.assertEquals(true, res);

    }

    @Test
    public void shouldReturnEligibleForBPScreening() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 year and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414eae-5ab4-11e0-870c-9f6107fee88e"); //Magaleta HC
        patientData.put("age_years", "18");
        // 18 years and 1 month old
        patientData.put("age_months", "217");
        patientData.put("age_days", "6480");
        patientData.put("current_systolic_bp", null);
        patientData.put("current_diastolic_bp", null);

        List<String> conditions = Arrays.asList(
                "age_years >= 18",
                "missing(current_systolic_bp) || missing(current_diastolic_bp)"
        );


        createAndEvaluateAlert(
                "eligible-for-bp-screening",
                Arrays.asList("bp", "screening-eligibility"),
                conditions,
                "Due for BP Screening",
                patientData);
    }

    @Test
    public void shouldReturnEligibleForBloodGlucoseScreeningNotEnrolled() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 year and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d417e38-5ab4-11e0-870c-9f6107fee88e"); //Ligowe HC
        patientData.put("age_years", "55");
        patientData.put("art_number", "LGWE-0143");

        patientData.put("last_bmi", "22.65");
        patientData.put("last_blood_sugar_result_date", cal.getTime());
        patientData.put("family_history_diabetes", null);
        // cough, hypertension
        List<HashMap<String, String>> chronic_care_diagnoses = Arrays.asList(new HashMap<String, String>() {{
            put("date", "1265086800000");
            put("value", "65460a32-977f-11e1-8993-905e29aff6c1");
        }}, new HashMap<String, String>() {{
            put("date", "1265086800000");
            put("value", "654abfc8-977f-11e1-8993-905e29aff6c1");
        }});
        patientData.put("chronic_care_diagnoses", chronic_care_diagnoses);

        List<String> conditions = Arrays.asList(
                "missing(chronic_care_diagnoses) || (!missing(chronic_care_diagnoses) && !hasChronicCareDiagnosis(chronic_care_diagnoses, [diabetes, diabetes_type_1, diabetes_type_2]))",
                "missing(last_blood_sugar_result_date) || (!missing(last_blood_sugar_result_date) && yearsBetween(today, last_blood_sugar_result_date) >= 1)",
                "(age_years > 30) || (age_years > 18 && last_bmi > 25) || (!missing(chronic_care_diagnoses) && hasChronicCareDiagnosis(chronic_care_diagnoses, [hypertension])) || (family_history_diabetes == true)"
        );


        createAndEvaluateAlert(
                "eligible-for-blood-glucose-screening-not-enrolled",
                Arrays.asList("blood-glucose", "screening-eligibility"),
                conditions,
                "Enroll if confirmed by clinician meets criteria",
                patientData);
    }

    @Test
    public void shouldReturnRoutineBloodSugarHighRiskAlert() throws Exception {

        JsonObject patientData = getTestPatient("55");
        patientData.put("last_bmi", "24");
        // 1 year and 3 months ago
        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.DAY_OF_YEAR, -10);
        patientData.put("last_blood_sugar_result_date", cal.getTime());
        patientData.put("family_history_diabetes", true);
        patientData.put("cc_treatment_status", null);

        List<HashMap<String, String>> chronic_care_diagnoses = Arrays.asList(
                new HashMap<String, String>() {{
                    put("date", "1311652800000"); //Tuesday, July 26, 2011
                    put("value", "656cce7e-977f-11e1-8993-905e29aff6c1"); //Other non-coded diagnosis
                }}, new HashMap<String, String>() {{
                    put("date", "1499832000000"); //Wednesday, July 12, 2017
                    put("value", "654abfc8-977f-11e1-8993-905e29aff6c1"); // hypertension
                }});
        patientData.put("chronic_care_diagnoses", chronic_care_diagnoses);

        List<String> conditions = Arrays.asList(
                "age_years >= 30",
                "!hasChronicCareDiagnosis(chronic_care_diagnoses, [diabetes,diabetes_type_1,diabetes_type_2])",
                "yearsBetween(today, last_blood_sugar_result_date) >= 1 || last_bmi > 25 || hasChronicCareDiagnosis(chronic_care_diagnoses, [hypertension]) || family_history_diabetes == true"
        );

        createAndEvaluateAlert(
                "routine-blood-sugar-high-risk",
                Arrays.asList("diabetes"),
                conditions,
                "Routine Blood Sugar for high risk population",
                patientData);
    }

    @Test
    public void shouldReturnRoutineCreatinineAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d41505c-5ab4-11e0-870c-9f6107fee88e"); //NOP
        patientData.put("age_years", "55");
        patientData.put("ncd_number", "NOP 452 CCC");

        // 1 year and 3 months ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.DAY_OF_YEAR, -10);
        patientData.put("last_creatinine_result_date", cal.getTime());
        patientData.put("last_creatinine_result", "0.5");

        List<HashMap<String, String>> chronic_care_diagnoses = Arrays.asList(new HashMap<String, String>() {{
            put("date", "688971600000"); //Friday, November 1, 1991
            put("value", "65714206-977f-11e1-8993-905e29aff6c1"); //diabetes_type_1
        }}, new HashMap<String, String>() {{
            put("date", "1311652800000"); //Tuesday, July 26, 2011
            put("value", "656cce7e-977f-11e1-8993-905e29aff6c1"); //Other non-coded diagnosis
        }}, new HashMap<String, String>() {{
            put("date", "1499832000000"); //Wednesday, July 12, 2017
            put("value", "654abfc8-977f-11e1-8993-905e29aff6c1"); // hypertension
        }});
        patientData.put("chronic_care_diagnoses", chronic_care_diagnoses);


        List<String> conditions = Arrays.asList(
                "hasChronicCareDiagnosis(chronic_care_diagnoses, [diabetes, diabetes_type_1, diabetes_type_2, hypertension])",
                "yearsBetween(today, last_creatinine_result_date) >= 1",
                "missing(last_creatinine_result) || last_creatinine_result < 1.5"
        );


        createAndEvaluateAlert(
                "routine-creatinine",
                Arrays.asList("diabetes"),
                conditions,
                "Routine Creatinine for patients with diabetes or hypertension",
                patientData);
    }

    @Test
    public void shouldReturnHighCreatinineAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        JsonObject patientData = getTestPatient("55");

        // 1 year and 7 months 10 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -7);
        cal.add(Calendar.DAY_OF_YEAR, -10);
        patientData.put("last_creatinine_result_date", cal.getTime());
        patientData.put("last_creatinine_result", "1.7");

        List<HashMap<String, String>> chronic_care_diagnoses = Arrays.asList(new HashMap<String, String>() {{
            put("date", "688971600000"); //Friday, November 1, 1991
            put("value", "65714206-977f-11e1-8993-905e29aff6c1"); //diabetes_type_1
        }}, new HashMap<String, String>() {{
            put("date", "1311652800000"); //Tuesday, July 26, 2011
            put("value", "656cce7e-977f-11e1-8993-905e29aff6c1"); //Other non-coded diagnosis
        }}, new HashMap<String, String>() {{
            put("date", "1499832000000"); //Wednesday, July 12, 2017
            put("value", "654abfc8-977f-11e1-8993-905e29aff6c1"); // hypertension
        }});
        patientData.put("chronic_care_diagnoses", chronic_care_diagnoses);


        List<String> conditions = Arrays.asList(
                "hasChronicCareDiagnosis(chronic_care_diagnoses, [diabetes, diabetes_type_1, diabetes_type_2, hypertension])",
                "monthsBetween(today, last_creatinine_result_date) >= 6",
                "last_creatinine_result >= 1.5"
        );


        createAndEvaluateAlert(
                "high-creatinine",
                Arrays.asList("diabetes"),
                conditions,
                "Creatinine for patient with creatinine >1.5 with diabetes or hypertention",
                patientData);
    }

    @Test
    public void shouldReturnEligibleForA1CScreeningType1Alert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 3 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414ce2-5ab4-11e0-870c-9f6107fee88e"); //Neno DHO
        patientData.put("age_years", "70");
        patientData.put("ncd_number", "NNO 452 CCC");

        patientData.put("last_bmi", "28.30");
        patientData.put("last_hba1c_result_date", cal.getTime());
        patientData.put("last_blood_sugar_result_date", cal.getTime());
        patientData.put("family_history_diabetes", null);
        patientData.put("cc_treatment_status", "66882650-977f-11e1-8993-905e29aff6c1"); //on_treatment

        // diabetes_type_1, hypertension
        List<HashMap<String, String>> chronic_care_diagnoses = Arrays.asList(new HashMap<String, String>() {{
            put("date", "1378958400000");
            put("value", "65714206-977f-11e1-8993-905e29aff6c1");
        }}, new HashMap<String, String>() {{
            put("date", "1376539200000");
            put("value", "6567426a-977f-11e1-8993-905e29aff6c1");
        }});
        patientData.put("chronic_care_diagnoses", chronic_care_diagnoses);

        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("eligible-for-a1c-screening-type-1");
        alert.setCategories(Arrays.asList("a1c", "screening-eligibility"));
        alert.setConditions(Arrays.asList(
                "(cc_treatment_status == on_treatment) || (cc_treatment_status == in_advanced_care)",
                "hasChronicCareDiagnosis(chronic_care_diagnoses, [diabetes_type_1])",
                "missing(last_hba1c_result_date) || (!missing(last_hba1c_result_date) && monthsBetween(today, last_hba1c_result_date) >= 3)"));

        alert.setAlert("Action: for disease monitoring");
        alert.setAction("Eligible for a1c screening");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("eligible-for-a1c-screening-type-1"), 0);
    }

    @Test
    public void shouldReturnEligibleForA1CScreeningType2Alert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 6 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414ce2-5ab4-11e0-870c-9f6107fee88e"); //Neno DHO
        patientData.put("age_years", "70");
        patientData.put("ncd_number", "NNO 452 CCC");

        patientData.put("last_bmi", "28.30");
        patientData.put("last_hba1c_result_date", cal.getTime());
        patientData.put("last_blood_sugar_result_date", cal.getTime());
        patientData.put("family_history_diabetes", null);
        patientData.put("cc_treatment_status", "66882650-977f-11e1-8993-905e29aff6c1"); //on_treatment

        // diabetes_type_2, hypertension
        List<HashMap<String, String>> chronic_care_diagnoses = Arrays.asList(new HashMap<String, String>() {{
            put("date", "1378958400000");
            put("value", "65714314-977f-11e1-8993-905e29aff6c1");
        }}, new HashMap<String, String>() {{
            put("date", "1376539200000");
            put("value", "6567426a-977f-11e1-8993-905e29aff6c1");
        }});
        patientData.put("chronic_care_diagnoses", chronic_care_diagnoses);

        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("eligible-for-a1c-screening-type-2");
        alert.setCategories(Arrays.asList("a1c", "screening-eligibility"));
        alert.setConditions(Arrays.asList(
                "(cc_treatment_status == on_treatment) || (cc_treatment_status == in_advanced_care)",
                "hasChronicCareDiagnosis(chronic_care_diagnoses, [diabetes_type_2])",
                "missing(last_hba1c_result_date) || (!missing(last_hba1c_result_date) && monthsBetween(today, last_hba1c_result_date) >= 6)"));

        alert.setAlert("Action: Diabetes Type 2 disease monitoring");
        alert.setAction("Eligible for a1c screening");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("eligible-for-a1c-screening-type-2"), 0);
    }


    @Test
    public void shouldReturnDueForAdherenceCounselingAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414ce2-5ab4-11e0-870c-9f6107fee88e"); //Neno DHO
        patientData.put("age_years", "28");
        patientData.put("art_number", "NNO 3517");

        patientData.put("last_bmi", "28.30");
        patientData.put("hiv_treatment_status", "6687fa7c-977f-11e1-8993-905e29aff6c1"); //active_art
        cal.add(Calendar.MONTH, -2); // 2 months ago
        patientData.put("last_viral_load_date", cal.getTime());
        patientData.put("last_viral_load_type", "e0821812-955d-11e7-abc4-cec278b6b50a"); //routine_viral_load
        patientData.put("last_viral_load_numeric", 1000);
        patientData.put("last_viral_load_ldl", null);
        patientData.put("last_adherence_counselling_session_number", 1);
        cal.add(Calendar.MONTH, 1); // 1 month ago
        patientData.put("last_adherence_counselling_session_date", cal.getTime());


        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("due-for-adherence-counselling");
        alert.setCategories(Arrays.asList("viral-load", "abnormal-result"));
        alert.setConditions(Arrays.asList(
                "age_years >= 3",
                "hiv_treatment_status == active_art",
                "last_viral_load_type == routine_viral_load",
                "last_viral_load_numeric > 0",
                "last_viral_load_ldl != true",
                "last_adherence_counselling_session_number < 3",
                "last_adherence_counselling_session_date > last_viral_load_date"
        ));

        alert.setAlert("High Viral Load. Do adherence counseling for 3 consecutive months.");
        alert.setAction("High Viral Load. Do adherence counseling for 3 consecutive months.");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("due-for-adherence-counselling"), 0);
    }

    @Test
    public void shouldReturnDueForConfirmatoryViralLoadAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414ce2-5ab4-11e0-870c-9f6107fee88e"); //Neno DHO
        patientData.put("age_years", "28");
        patientData.put("art_number", "NNO 3517");

        patientData.put("last_bmi", "28.30");
        patientData.put("hiv_treatment_status", "6687fa7c-977f-11e1-8993-905e29aff6c1"); //active_art
        cal.add(Calendar.MONTH, -3); // 2 months ago
        patientData.put("last_viral_load_date", cal.getTime());
        patientData.put("last_viral_load_result_date", cal.getTime());
        patientData.put("last_viral_load_type", null); // "e0821812-955d-11e7-abc4-cec278b6b50a"); //routine_viral_load
        patientData.put("last_viral_load_numeric", 1000);
        patientData.put("last_viral_load_ldl", null);
        patientData.put("last_adherence_counselling_session_number", 1);
        cal.add(Calendar.MONTH, 1); // 1 month ago
        patientData.put("last_adherence_counselling_session_date", cal.getTime());


        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("due-for-confirmatory-viral-load");
        alert.setCategories(Arrays.asList("viral-load", "abnormal-result"));
        alert.setConditions(Arrays.asList(
                "age_years >= 3",
                "hiv_treatment_status == active_art",
                "missing(last_viral_load_type) || (last_viral_load_type == routine_viral_load)",
                "last_viral_load_numeric > 0",
                "last_viral_load_ldl != true",
                "daysBetween(today, last_viral_load_result_date) >= 90"
        ));

        alert.setAlert("Due for confirmatory VL");
        alert.setAction("Due for confirmatory VL");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("due-for-confirmatory-viral-load"), 0);
    }

    @Test
    public void shouldReturnAbnormalBmiAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414ce2-5ab4-11e0-870c-9f6107fee88e"); //Neno DHO
        patientData.put("age_years", "28");
        patientData.put("art_number", "NNO 3517");

        patientData.put("last_bmi", "20.1");
        patientData.put("hiv_treatment_status", "6687fa7c-977f-11e1-8993-905e29aff6c1"); //active_art
        patientData.put("current_weight", "50");
        patientData.put("current_height", null);
        patientData.put("last_height", "170");
        patientData.put("is_pregnant", null);

        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("abnormal-result-bmi");
        alert.setCategories(Arrays.asList("nutrition", "abnormal-result"));
        alert.setConditions(Arrays.asList(
                "age_years > 18",
                "!isTrue(is_pregnant)",
                "!missing(current_weight)",
                "!missing(current_height) || !missing(last_height)",
                "!missing(current_height) ? (( current_weight / ( current_height * current_height) ) * 10000).toFixed(1) >= 16 : (( current_weight / ( last_height * last_height) ) * 10000).toFixed(1) >= 16",
                "!missing(current_height) ? (( current_weight / ( current_height * current_height) ) * 10000).toFixed(1) <= 18.4 : (( current_weight / ( last_height * last_height) ) * 10000).toFixed(1) <= 18.4"
        ));

        alert.setAlert("Moderate malnutrition");
        alert.setAction("Action: Enroll in NCST");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("abnormal-result-bmi"), 0);
    }

    @Test
    public void shouldReturnCriticalBmiAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414ce2-5ab4-11e0-870c-9f6107fee88e"); //Neno DHO
        patientData.put("age_years", "28");
        patientData.put("art_number", "NNO 3517");

        patientData.put("last_bmi", "17.1");
        patientData.put("hiv_treatment_status", "6687fa7c-977f-11e1-8993-905e29aff6c1"); //active_art
        patientData.put("current_weight", "45");
        patientData.put("current_height", "170");
        patientData.put("last_height", "170");
        patientData.put("is_pregnant", null);

        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("critical-result-bmi");
        alert.setCategories(Arrays.asList("nutrition", "critical-result"));
        alert.setConditions(Arrays.asList(
                "age_years > 18",
                "!isTrue(is_pregnant)",
                "!missing(current_weight)",
                "!missing(current_height) || !missing(last_height)",
                "!missing(current_height) ? (( current_weight / ( current_height * current_height) ) * 10000).toFixed(1) < 16 : (( current_weight / ( last_height * last_height) ) * 10000).toFixed(1) < 16"
        ));

        alert.setAlert("Severe malnutrition");
        alert.setAction("Action: Enroll in NCST");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("critical-result-bmi"), 0);
    }

    @Test
    public void shouldReturnEligibleForHtcAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d414ce2-5ab4-11e0-870c-9f6107fee88e"); //Neno DHO
        patientData.put("age_years", "83");

        patientData.put("hiv_treatment_status", null);
        patientData.put("last_hiv_rapid_test_result", null);
        patientData.put("last_hiv_rapid_test_result_date", null);

        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("eligible-for-htc");
        alert.setCategories(Arrays.asList("htc", "screening-eligibility"));
        alert.setConditions(Arrays.asList(
                "age_years >= 15",
                "hiv_treatment_status !== active_art",
                "hiv_treatment_status !== active_eid",
                "last_hiv_rapid_test_result == negative || missing(last_hiv_rapid_test_result)",
                "missing(last_hiv_rapid_test_result_date) || monthsBetween(today, last_hiv_rapid_test_result_date) > 3"
        ));

        alert.setAlert("Eligible for HIV test");
        alert.setAction("Eligible for HIV test");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("eligible-for-htc"), 0);
    }

    @Test
    public void shouldReturnEid12MonthRapidTestAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        // and 1 months and 1 day ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d41505c-5ab4-11e0-870c-9f6107fee88e"); //Neno Mission HC
        patientData.put("birthdate", "1512277200000");
        patientData.put("age_years", "1");
        patientData.put("age_months", "14");
        patientData.put("age_days", "444");
        patientData.put("hcc_number", "NOP-0327-HCC");

        patientData.put("hiv_treatment_status", "668847a2-977f-11e1-8993-905e29aff6c1"); //Active EID
        patientData.put("eid_start_date", "1516770000000");
        patientData.put("last_breastfeeding_status", "657a29a2-977f-11e1-8993-905e29aff6c1");
        // last Rapid test was soon after birthday, before patient's first birthday
        patientData.put("last_hiv_rapid_test_result_date", "1512277200010");
        patientData.put("last_hiv_dna_pcr_result", null);


        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("eid-routine-12-month-rapid-test");
        alert.setCategories(Arrays.asList("eid", "screening-eligibility"));
        alert.setConditions(Arrays.asList(
                "hiv_treatment_status == active_eid",
                "age_years >= 1",
                "missing(last_hiv_rapid_test_result_date) || yearsBetween(last_hiv_rapid_test_result_date, birthdate) < 1"
        ));

        alert.setAlert("Due for routine rapid HIV test");
        alert.setAction("Due for routine rapid HIV test");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("eid-routine-12-month-rapid-test"), 0);
    }

    @Test
    public void shouldReturnEid24MonthRapidTestAlert() throws Exception {
        //today
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -3);


        JsonObject patientData = new JsonObject();
        patientData.put("today", effectiveDate);
        patientData.put("location", "0d41505c-5ab4-11e0-870c-9f6107fee88e"); //Neno Mission HC
        //patient was born 3 years ago
        patientData.put("birthdate", cal.getTime());
        patientData.put("age_years", "3");
        patientData.put("age_months", "36");

        patientData.put("hcc_number", "NOP-0327-HCC");

        patientData.put("hiv_treatment_status", "668847a2-977f-11e1-8993-905e29aff6c1"); //Active EID
        cal.add(Calendar.MONTH, 3);
        //enrolled in EID 3 months after birth
        patientData.put("eid_start_date", cal.getTime());
        patientData.put("last_breastfeeding_status", "657a29a2-977f-11e1-8993-905e29aff6c1");
        cal.add(Calendar.YEAR, 1);
        // when patient was about 15 months old
        patientData.put("last_hiv_rapid_test_result_date", cal.getTime());
        patientData.put("last_hiv_dna_pcr_result", null);

        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName("eid-routine-24-month-rapid-test");
        alert.setCategories(Arrays.asList("eid", "screening-eligibility"));
        alert.setConditions(Arrays.asList(
                "hiv_treatment_status == active_eid",
                "age_years >= 2",
                "missing(last_hiv_rapid_test_result_date) || yearsBetween(last_hiv_rapid_test_result_date, birthdate) < 2"
        ));

        alert.setAlert("Due for routine rapid HIV test if stopped breastfeeding >/= 6 weeks ago");
        alert.setEnabled(true);
        alertDefinitions.add(alert);

        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo("eid-routine-24-month-rapid-test"), 0);
    }

    @Test
    public void shouldTestChronicCareDiagnoses() throws Exception {

        Map<String, Object> variables = new HashMap<String, Object>();
        //diabetes, hypertension
        List<HashMap<String, String>> hashMapList = Arrays.asList(new HashMap<String, String>() {{
            put("date", "1265086800000");
            put("value", "6567426a-977f-11e1-8993-905e29aff6c1");
        }}, new HashMap<String, String>() {{
            put("date", "1265086800000");
            put("value", "654abfc8-977f-11e1-8993-905e29aff6c1");
        }});
        variables.put("chronic_care_diagnoses", hashMapList);
        ScriptEngine scriptEngine = engine.createScriptEngine(variables);

        Object res = scriptEngine.eval( "hasChronicCareDiagnosis(chronic_care_diagnoses, " +
                "['6567426a-977f-11e1-8993-905e29aff6c1', '65714206-977f-11e1-8993-905e29aff6c1'])"); //diabetes, diabetes_type_1

        Assert.assertEquals(true, res);

    }

    @Test
    public void shouldTestGetTestFirstDateByTypeAndValue() throws Exception {

        final Long startOfDay = (DateUtil.getStartOfDay(new Date())).getTime();
        Map<String, Object> variables = new HashMap<String, Object>();
        //diabetes, hypertension
        List<HashMap<String, String>> hashMapList = Arrays.asList(new HashMap<String, String>() {{
            put("specimenDate", "1525726800000");
            put("resultDate", null);
            put("effectiveDate", "1525726800000");
            put("result", "654994c2-977f-11e1-8993-905e29aff6c1");
            put("testType", "654a6960-977f-11e1-8993-905e29aff6c1");
        }}, new HashMap<String, String>() {{
            put("specimenDate", "1553299200000");
            put("resultDate", null);
            put("effectiveDate", "1553299200000");
            put("result", "654994c2-977f-11e1-8993-905e29aff6c1");
            put("testType", "654a6960-977f-11e1-8993-905e29aff6c1");
        }}, new HashMap<String, String>() {{
            put("specimenDate", String.valueOf(startOfDay));
            put("resultDate", null);
            put("effectiveDate", String.valueOf(startOfDay));
            put("result", "6549be7a-977f-11e1-8993-905e29aff6c1");
            put("testType", "654a6960-977f-11e1-8993-905e29aff6c1");
        }});
        variables.put("hiv_tests", hashMapList);
        variables.put("today", String.valueOf(startOfDay));
        ScriptEngine scriptEngine = engine.createScriptEngine(variables);
        Object res = scriptEngine.eval( "daysBetween(today, getTestFirstDateByTypeAndValue(hiv_tests, hiv_dna_pcr, positive, 'effectiveDate')) == 0");

        Assert.assertEquals(true, res);
    }

    protected JsonObject getTestPatient(String years){
        Date effectiveDate = DateUtil.getStartOfDay(new Date());

        JsonObject patientObject = new JsonObject();
        patientObject.put("today", effectiveDate);
        patientObject.put("location", "0d41505c-5ab4-11e0-870c-9f6107fee88e"); //NOP
        patientObject.put("age_years", years);
        patientObject.put("ncd_number", "NNO 101 CCC");

        return patientObject;
    }

    protected void createAndEvaluateAlert(
            String name,
            List<String> categories,
            List<String> conditions,
            String alertVerbiage,
            JsonObject patientData) {

        List<AlertDefinition> alertDefinitions = new ArrayList<AlertDefinition>();
        AlertDefinition alert = new AlertDefinition();
        alert.setName(name);
        alert.setCategories(categories);
        alert.setConditions(conditions);
        alert.setAlert(alertVerbiage);
        alert.setEnabled(true);
        alertDefinitions.add(alert);
        List<AlertDefinition> evaluateMatchingAlerts = engine.evaluateMatchingAlerts(alertDefinitions, patientData);
        Assert.assertEquals(evaluateMatchingAlerts != null && evaluateMatchingAlerts.size() > 0, true);
        Assert.assertEquals(((AlertDefinition)evaluateMatchingAlerts.get(0)).getName().compareTo(alert.getName()), 0);
    }

    protected void test(String function, int y1, int m1, int d1, int y2, int m2, int d2, double expected) throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("t1", DateUtil.getDateTime(y1, m1, d1).getTime());
        variables.put("t2", DateUtil.getDateTime(y2, m2, d2).getTime());
        Object res = engine.createScriptEngine(variables).eval(function + "(t1, t2)");
        Assert.assertEquals(expected, res);
    }

    @Test
    public void shouldTestInlineVariables() throws Exception {

        Map<String, Object> variables = new HashMap<String, Object>();
        // t1 = March 4, 2005: 1109973600000
        // t2 = February 5, 2000: 949701600000
        Object res = engine.createScriptEngine(variables).eval("var t1='1109973600000'; var t2='949701600000'; monthsBetween(t1, t2)");
        Assert.assertEquals(61.0, res);
    }

    @Test
    public void shouldTestLoadingAlerts() throws Exception {
        List<AlertDefinition> alertDefinitions = engine.getAlertDefinitions();
        Assert.assertTrue(alertDefinitions.size() > 0);
    }
}
