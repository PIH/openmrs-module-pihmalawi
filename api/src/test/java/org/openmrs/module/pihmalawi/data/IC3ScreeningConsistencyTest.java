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
import java.util.*;

/**
 * Tests the IC3ScreeningData
 */
public class IC3ScreeningConsistencyTest extends StandaloneContextSensitiveTest {

    private final static Log log = LogFactory.getLog(IC3ScreeningConsistencyTest.class);


    class IC3Location {
        String name;
        int clinicDay = 0; //Clinic days 1=Sunday, 7=Saturday

        public IC3Location() {
        }

        public IC3Location(String name, int clinicDay) {
            this.name = name;
            this.clinicDay = clinicDay;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getClinicDay() {
            return clinicDay;
        }

        public void setClinicDay(int clinicDay) {
            this.clinicDay = clinicDay;
        }
    }

    public final static List<IC3Location> IC_3_LOCATIONS;

    static {
        IC3ScreeningConsistencyTest test = new IC3ScreeningConsistencyTest();
        IC_3_LOCATIONS = new ArrayList<IC3Location>();

        IC_3_LOCATIONS.add( test.new IC3Location("Magaleta HC", Calendar.MONDAY));
        IC_3_LOCATIONS.add( test.new IC3Location("Matandani Rural Health Center", Calendar.MONDAY));
        IC_3_LOCATIONS.add( test.new IC3Location("Dambe Clinic", Calendar.WEDNESDAY));
        IC_3_LOCATIONS.add( test.new IC3Location("Luwani RHC", Calendar.WEDNESDAY));
        IC_3_LOCATIONS.add( test.new IC3Location("Neno Mission HC", Calendar.WEDNESDAY));
        IC_3_LOCATIONS.add( test.new IC3Location("Neno District Hospital", Calendar.THURSDAY));
        IC_3_LOCATIONS.add( test.new IC3Location("Ligowe HC", Calendar.FRIDAY));
        IC_3_LOCATIONS.add( test.new IC3Location("Nsambe HC", Calendar.FRIDAY));

    }

    public static Calendar getNextWeekDay(int dayOfTheWeek){
        Calendar cal = Calendar.getInstance();
        if (dayOfTheWeek >=0 && dayOfTheWeek < 7) {
            int day_of_the_week = cal.get(Calendar.DAY_OF_WEEK);
            while (cal.get(Calendar.DAY_OF_WEEK) != dayOfTheWeek) {
                cal.add(Calendar.DATE, 1);
            }
        }
        return cal;
    }

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
        for (IC3Location ic3Location : IC_3_LOCATIONS) {
            Calendar nextClinicDay = getNextWeekDay(ic3Location.getClinicDay());
            log.warn(ic3Location.getName() + ": next Clinic day: " + (nextClinicDay.get(Calendar.DAY_OF_MONTH)));
            testDate(nextClinicDay.get(Calendar.YEAR), (nextClinicDay.get(Calendar.MONTH) +1), nextClinicDay.get(Calendar.DAY_OF_MONTH), metadata.getLocation(ic3Location.getName()));
        }

        //testDate(2018, 11, 8, metadata.getNenoHospital());
        //testDate(2019, 1, 23, metadata.getDambeClinic());
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
            Object patientId = revised.get("internal_id");


            if (current == null) {
                log.warn("patientId= " + patientId + "; PATIENT " + patientUuid + " IS NEW IN REVISED REPORT");
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
                    if ("eid_number".equals(property)) {
                        curVal = curVal == null ? null : curVal.toString();
                        revVal = (revised.get("hcc_number") == null ? null : ((String)revised.get("hcc_number")));
                    }
                    if (!"alert".equals(property) && !"actions".equals(property) && !"labTests".equals(property)) {
                        if (!ObjectUtil.areEqual(curVal, revVal)) {
                            counter.increment(property);
                            log.warn("patientId= " + patientId);
                            log.warn("\t" +property + " OLD: " + curVal + " , NEW: " + revVal);
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
        String url = "jdbc:mysql://localhost:3306/upperneno_warehouse?autoReconnect=true&sessionVariables=storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8";
        String user = "openmrs";
        String password = "openmrs";
        File propertiesFile = new File(OpenmrsConstants.APPLICATION_DATA_DIRECTORY, PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
        Properties properties = new Properties();
        properties.put("connection.url", url);
        properties.put("connection.username", user);
        properties.put("connection.password", password);
        properties.store(new FileOutputStream(propertiesFile), null);
    }
}
