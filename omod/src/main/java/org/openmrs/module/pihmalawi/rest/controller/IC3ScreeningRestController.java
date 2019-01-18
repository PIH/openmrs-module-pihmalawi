package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.data.IC3ScreeningData;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class IC3ScreeningRestController {

    public static final String PIHMALAWI = "/pihmalawi";
    public static final String IC3_SCREENING_DATA = "/ic3ScreeningData";
    public static final String IC3_APPOINTMENTS = "/ic3Appointments";
    public static final String IC3_PATIENTS = "/ic3Patients";

    public static final String PATIENTS_WITH_APPOINTMENT = "patientsWithAppointment";
    public static final String PATIENTS_WITH_VISIT = "patientsWithVisit";

    @Autowired
    IC3ScreeningData ic3ScreeningData;

    @Autowired
    BaseCohortDefinitionLibrary baseCohorts;

    @Autowired
    DataFactory df;

    @Autowired
    PatientService patientService;

    @RequestMapping(method = RequestMethod.GET, value="/rest/" + RestConstants.VERSION_1 + PIHMALAWI + IC3_SCREENING_DATA)
    @ResponseBody
    public Object getScreeningData(
            @RequestParam(value="patients") String[] patientUuids,
            @RequestParam(required = false, value="location") Location location,
            @RequestParam(required = false, value="endDate") String endDateStr,
            @RequestParam(required = false, value = "useCachedValues") Boolean useCachedValues) {

        Date endDate = getEndDate(endDateStr);
        boolean useCache = useCachedValues == null || useCachedValues;
        if (patientUuids == null) {
            throw new IllegalArgumentException("You must specify which patients you wish to retrieve data for");
        }
        Cohort cohort = getCohort(patientUuids);
        Map<Integer, JsonObject> data =  ic3ScreeningData.getDataForCohort(cohort, endDate, location, useCache);
        return data.values();
    }

    @RequestMapping(method = RequestMethod.GET, value="/rest/" + RestConstants.VERSION_1 + PIHMALAWI + IC3_APPOINTMENTS)
    @ResponseBody
    public Object getAppointments(
            @RequestParam(required = false, value="endDate") String endDateStr,
            @RequestParam(required = false, value="location") Location location,
            @RequestParam(required = false, value = "useCachedValues") Boolean useCachedValues) {

        Date endDate = getEndDate(endDateStr);
        boolean useCache = useCachedValues == null || useCachedValues;
        Cohort activeWithAppt = ic3ScreeningData.getPatientsWithAppointmentsAtLocation(endDate, location);
        Map<Integer, JsonObject> data = ic3ScreeningData.getDataForCohort(activeWithAppt, endDate, location, useCache);
        return data.values();
    }

    @RequestMapping(method = RequestMethod.GET, value="/rest/" + RestConstants.VERSION_1 + PIHMALAWI + IC3_PATIENTS)
    @ResponseBody
    public Object getPatients(
            @RequestParam(required = false, value="endDate") String endDateStr,
            @RequestParam(required = false, value="location") Location location,
            @RequestParam(required = false, value="cohorts") List<String> cohorts,
            @RequestParam(required = false, value = "useCachedValues") Boolean useCachedValues) {

        Date endDate = getEndDate(endDateStr);
        boolean useCache = useCachedValues == null || useCachedValues;

        Cohort baseCohort = null;
        if (ObjectUtil.isNull(cohorts) || cohorts.size() == 0 || cohorts.contains(PATIENTS_WITH_APPOINTMENT)) {
            Cohort c = ic3ScreeningData.getPatientsWithAppointmentsAtLocation(endDate, location);
            baseCohort = (baseCohort == null ? c : PatientIdSet.union(baseCohort, c));
        }
        if (ObjectUtil.isNull(cohorts) || cohorts.size() == 0 || cohorts.contains(PATIENTS_WITH_VISIT)) {
            Cohort c = ic3ScreeningData.getPatientsWithAVisitAtLocation(endDate, location);
            baseCohort = (baseCohort == null ? c : PatientIdSet.union(baseCohort, c));
        }
        Map<Integer, JsonObject> data = ic3ScreeningData.getDataForCohort(baseCohort, endDate, location, useCache);
        return data.values();
   }

   protected Date getEndDate(String endDateStr) {
        if (ObjectUtil.isNull(endDateStr)) {
            return DateUtil.getStartOfDay(new Date());
        }
        try {
            long l = Long.parseLong(endDateStr);
            return DateUtil.getStartOfDay(new Date(l));
        }
        catch (Exception e) {}
        try {
            return DateUtil.parseYmd(endDateStr);
        }
        catch (Exception e) {}
        throw new IllegalArgumentException("Unable to parse " + endDateStr + " into a valid date");
   }

    protected Cohort getCohort(String... patientUuids) {
        HqlQueryBuilder qb = new HqlQueryBuilder();
        qb.select("patientId").from(Patient.class).whereInAny("uuid", patientUuids);
        List<Integer> pIds = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, new EvaluationContext());
        return new Cohort(pIds);
    }
}
