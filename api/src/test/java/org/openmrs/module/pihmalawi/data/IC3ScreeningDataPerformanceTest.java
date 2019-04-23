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
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.alert.AlertDefinition;
import org.openmrs.module.pihmalawi.alert.AlertEngine;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectCounter;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
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

    @Autowired
    PatientService patientService;

    @Autowired
    BaseCohortDefinitionLibrary baseCohorts;

    @Autowired
    HivMetadata metadata;

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    protected void performTest() throws Exception {

        //Moderate malnutrition,  16 <= BMI <= 18.4
        JsonObject patient = getPatient(
                "0dff0a3f-10d4-4b13-b727-c21b9a6d1cb0",
                "Neno District Hospital",
                2019, 1, 28);

        log.warn(patient.get("cc_treatment_status"));
        log.warn(patient.get("alerts"));

        // Severe malnutrition
        patient = getPatient(
                "68f95c58-bc26-4006-bcec-55b276027213",
                "Neno District Hospital",
                2019, 1, 24);

        log.warn(patient.get("alerts"));

        //Is patient pregnant, MUAC
        patient = getPatient(
                "1dc4da36-7d32-4ee3-95b8-b81a94bd53c2",
                "Neno District Hospital",
                2019, 1, 24);

        log.warn(patient.get("cc_treatment_status"));
        log.warn(patient.get("alerts"));


        //Severe malnutrition under 18
        patient = getPatient(
                "c4f3f568-2695-102d-b4c2-001d929acb54",
                "Nsambe HC",
                2019, 1, 25);

        log.warn(patient);
        log.warn(patient.get("alerts"));

        //TB Screening Abnormal
        patient = getPatient(
                "f58495e0-3dfc-4409-b1a1-31f1ecddcd75",
                "Luwani RHC",
                2019, 1, 30);

        log.warn(patient);
        log.warn(patient.get("alerts"));

        //Chronic Care Diagnoses
        patient = getPatient(
                "c59d3ba0-2695-102d-b4c2-001d929acb54",
                "Matandani Rural Health Center",
                2019, 2, 4);

        log.warn(patient);
        log.warn(patient.get("alerts"));

        //Not Enrolled Patients:  eligible-for-blood-glucose-screening alert
        patient = getPatient(
                "27fea436-fb54-4cf2-87a0-3fd8fbfa5d1e",
                "Ligowe HC",
                2019, 2, 8);

        log.warn(patient);
        log.warn(patient.get("alerts"));

        // IS-13, fix "last_adherence_counselling_session_date": null
        patient = getPatient(
                "c514a3d0-2695-102d-b4c2-001d929acb54",
                "Neno District Hospital",
                2019, 4, 10);

        log.warn("last_adherence_counselling_session_date: " + patient.get("last_adherence_counselling_session_date"));
        log.warn("last_adherence_counselling_session_number: " + patient.get("last_adherence_counselling_session_number"));
        log.warn(patient.get("alerts"));

        //testDate(2018, 11, 8);
        //testDate(2017, 12, 27);

        //patient = getPatient("c5489230-2695-102d-b4c2-001d929acb54", 2017, 12, 27);
        //log.warn(patient);
    }

    protected JsonObject getPatient(String uuid, String clinic, int year, int month, int day) {
        StopWatch sw = new StopWatch();
        sw.start();
        Patient p = patientService.getPatientByUuid(uuid);
        if (p == null) {
            log.error("Patient with uuid = " + uuid + " does not exists");
            return null;
        }
        JsonObject ret = screeningData.getDataForPatient(
                p.getPatientId(),
                DateUtil.getDateTime(year, month, day),
                metadata.getLocation(clinic),
                false);
        sw.stop();
        log.info("Refreshed Data for single patient in: " + sw.toString());
        return ret;
    }

    protected void testDate(int year, int month, int day) throws Exception {

        Date effectiveDate = DateUtil.getDateTime(year, month, day);
        Location location = null;

        log.info("----- TEST for " + effectiveDate + " -----");

        Cohort cohort = screeningData.getPatientsWithAppointmentsAtLocation(effectiveDate, location);

        log.info("Found " + cohort.size() + " patients with an appointment on " + effectiveDate + " at " + location);

        StopWatch sw = new StopWatch();
        sw.start();
        Map<Integer, JsonObject> data = screeningData.getDataForCohort(cohort, effectiveDate, location, true);
        sw.stop();
        log.info("Refreshed Data in: " + sw.toString());

        AlertEngine alertEngine = new AlertEngine();
        List<AlertDefinition> alertDefinitions = alertEngine.getAlertDefinitions();

        sw.reset();
        sw.start();

        ObjectCounter counter = new ObjectCounter();
        for (Integer pId : data.keySet()) {
            JsonObject patientData = data.get(pId);
            List<AlertDefinition> matches = alertEngine.evaluateMatchingAlerts(alertDefinitions, patientData);
            for (AlertDefinition match : matches) {
                counter.increment(match.getName());
            }
        }
        sw.stop();

        log.info("Evaluated Alerts in: " + sw.toString());
        for (AlertDefinition alertDefinition : alertDefinitions) {
            Object count = counter.getCount(alertDefinition.getName());
            log.info(alertDefinition.getName() + ": " + ObjectUtil.nvlStr(count, "0"));
        }
    }
}
