/*
  The contents of this file are subject to the OpenMRS Public License
  Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://license.openmrs.org

  Software distributed under the License is distributed on an "AS IS"
  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  License for the specific language governing rights and limitations
  under the License.

  Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.TraceConstants.HighPriorityCategory;
import org.openmrs.module.pihmalawi.common.TraceCriteria;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.IC3Metadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.EidTestCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.TraceCriteriaPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.pihmalawi.common.TraceConstants.Category;
import static org.openmrs.module.pihmalawi.common.TraceConstants.Category.*;
import static org.openmrs.module.pihmalawi.common.TraceConstants.TraceType;

/**
 * Evaluates a TraceCriteriaPatientDataDefinition to produce a PatientData
 */
@Handler(supports = TraceCriteriaPatientDataDefinition.class, order = 50)
public class TraceCriteriaDataEvaluator implements PatientDataEvaluator {

    @Autowired
    private DataFactory df;

    @Autowired
    private HivMetadata hivMetadata;

    @Autowired
    private ChronicCareMetadata ccMetadata;

    @Autowired
    private IC3Metadata ic3Metadata;

    @Autowired
    private HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    private ChronicCareCohortDefinitionLibrary ccCohorts;

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        TraceCriteriaPatientDataDefinition dd = (TraceCriteriaPatientDataDefinition) definition;
        Location location = dd.getLocation();
        TraceType traceType = dd.getTraceType();

        Map<String, Object> mappings = new HashMap<String, Object>();
        mappings.put(ReportingConstants.LOCATION_PARAMETER.getName(), location);

	    EvaluatedPatientData pd = new EvaluatedPatientData(definition, context);

		add(pd, LATE_HIV_VISIT, getLateHiv(traceType), mappings, context);
        add(pd, HIGH_VIRAL_LOAD, getHighViralLoad(traceType), mappings, context);
        add(pd, EID_POSITIVE_6_WK, getEidPositive6Week(traceType), mappings, context);

        if (traceType.getMinWeeks() == 2) {
            add(pd, REPEAT_VIRAL_LOAD, getRepeatViralLoad(traceType), mappings, context);
            add(pd, EID_12_MONTH_TEST, getEid12Month(traceType), mappings, context);
            add(pd, EID_24_MONTH_TEST, getEid24Month(traceType), mappings, context);
            add(pd, EID_NEGATIVE, getEidNegative(traceType), mappings, context);
        }

        if (!traceType.isPhase1Only()) {
            add(pd, LATE_NCD_VISIT_NORMAL_PRIORITY, df.createPatientComposition(getLateNcd(traceType), " AND NOT ", getHighPriority()), mappings, context);
            add(pd, LATE_NCD_VISIT_HIGH_PRIORITY, df.getPatientsInAll(getLateNcd(traceType), getHighPriority()), mappings, context);
        }

		return pd;
	}

    /**
     * Late HIV:  Active in HIV program and late for a appointment by the specified range
     */
	public CohortDefinition getLateHiv(TraceType traceType) {
        CohortDefinition lateForHivVisit = df.getPatientsLateForAppointment(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes(), traceType.getMinDaysInclusive(), traceType.getMaxDaysInclusive());
        return df.getPatientsInAll(getActiveHiv(), lateForHivVisit);
    }

    /**
     * Active in HIV program and
     * 2 wk report:  Viral Load Obs DateCreated/DateChanged <= 14 days and viral load > 1000
     * 6 wk report:  Viral Load Obs DateCreated/DateChanged <= 56 days and viral load > 1000 and no visit date > last viral load entry date
     */
    public CohortDefinition getHighViralLoad(TraceType traceType) {
        Integer minDaysInPast = null;
        Integer maxDaysInPast = (traceType.getMinWeeks() == 2 ? 14 : traceType.getMinWeeks() == 6 ? 56 : null);
        boolean ensureNoSubsequentVisit = (traceType.getMinWeeks() == null || traceType.getMinWeeks() == 2);
        CohortDefinition viralLoadQualifies = hivCohorts.getPatientsWithViralLoadNeedingTraceAtLocation(traceType, minDaysInPast, maxDaysInPast, ensureNoSubsequentVisit);
        return df.getPatientsInAll(getActiveHiv(), viralLoadQualifies);
    }

    /**
     * Active in HIV program and
     * patient had a viral load > 1000, it was entered 84-168 days ago, patient hasn't visited since, and patient has appointment 2-4 weeks in the future
     *  viral load obs has (
     *      valueNumeric > 1000 and
     *      today-dateCreated between [84, 168) days and
     *      lastVisitDate-dateCreated > 0
     *  )
     *  and appointmentDate-today [14, 28)
     */
    public CohortDefinition getRepeatViralLoad(TraceType traceType) {
        CohortDefinition viralLoadQualifies = hivCohorts.getPatientsWithViralLoadNeedingTraceAtLocation(traceType, 84, 168, true);
        return df.getPatientsInAll(getActiveHiv(), viralLoadQualifies, getUpcomingIc3Appointment(traceType, 14, 27));
    }

    /**
     * age >= 12m
     * no test results since birthdate+12m
     * and appointmentDate-today [14, 28)
     */
    public CohortDefinition getEid12Month(TraceType traceType) {
        CohortDefinition over12m = df.getAgeByEndDate(1, null);
        CohortDefinition upcomingAppt = getUpcomingIc3Appointment(traceType, 14, 27);
        EidTestCohortDefinition noTestsAfter12m = getBaseEidTestCohortDefinition(traceType);
        noTestsAfter12m.setNoTestResultsAfter12MonthsOld(true);
        return df.getPatientsInAll(getActiveEid(), over12m, noTestsAfter12m, upcomingAppt);
    }

    /**
     * age >= 12m
     * AND mom has stopped breastfeeding for at least 6 weeks
     * AND no value for test since breastfeeding stopped
     * and appointmentDate-today [14, 28)
     */
    public CohortDefinition getEid24Month(TraceType traceType) {
        CohortDefinition over12m = df.getAgeByEndDate(1, null);
        CohortDefinition upcomingAppt = getUpcomingIc3Appointment(traceType, 14, 27);
        EidTestCohortDefinition noTestsAfterBreastfeeding = getBaseEidTestCohortDefinition(traceType);
        noTestsAfterBreastfeeding.setNoTestResultsAfterBreastfeeding(true);
        return df.getPatientsInAll(getActiveEid(), over12m, noTestsAfterBreastfeeding, upcomingAppt);
    }

    /**
     * 2wk:  6wk eid test result is positive AND today-lastEidTestResultDate <= 14d
     * 6wk:  6wk eid test result is positive AND today-lastEidTestResultDate <= 56d AND no visit since lastEidTestResultDate
     * TODO: My assumption is that the 6 week test result is the first test result.  Confirm this.
     */
    public CohortDefinition getEidPositive6Week(TraceType traceType) {
        EidTestCohortDefinition testsQualify = getBaseEidTestCohortDefinition(traceType);
        testsQualify.setLastTestResult(hivMetadata.getPositiveConcept());
        testsQualify.setMaximumNumberOfTestResults(1);
        if (traceType.getMinWeeks() == 2) {
            testsQualify.setLastTestResultWithinLastDays(14);
        }
        if (traceType.getMinWeeks() == 6) {
            testsQualify.setLastTestResultWithinLastDays(56);
            testsQualify.setNoVisitSinceLastTestResult(true);
        }
        return df.getPatientsInAll(getActiveEid(), testsQualify);
    }

    /**
     * second to last eid test result is positive
     * last eid test result is negative
     * today-lastEidTestResultDate <= 14d
     */
    public CohortDefinition getEidNegative(TraceType traceType) {
        EidTestCohortDefinition testsQualify = getBaseEidTestCohortDefinition(traceType);
        testsQualify.setSecondToLastTestResult(hivMetadata.getPositiveConcept());
        testsQualify.setLastTestResult(hivMetadata.getNegativeConcept());
        testsQualify.setLastTestResultWithinLastDays(14);
        return df.getPatientsInAll(getActiveEid(), testsQualify);
    }

    public CohortDefinition getLateNcd(TraceType traceType) {
        // 2 wk report:  If patient has NCD# AND today's date minus appointment date is greater than/equal to 14 days and less than 42 days
        // 6 wk report:  If patient has NCD# AND today's date minus appointment date is greater than/equal to 42 days and less than 84 days

        CohortDefinition activeInCCProgram = ccCohorts.getActivelyEnrolledInChronicCareProgramAtLocationOnEndDate();
        CohortDefinition lateForCCVisit = df.getPatientsLateForAppointment(ccMetadata.getActiveChronicCareStates(), ccMetadata.getChronicCareScheduledVisitEncounterTypes(), traceType.getMinDaysInclusive(), traceType.getMaxDaysInclusive());
        return df.getPatientsInAll(activeInCCProgram, lateForCCVisit);
    }

    public CohortDefinition getActiveHiv() {
        return hivCohorts.getActivelyEnrolledInHivProgramAtLocationOnEndDate();
    }

    public CohortDefinition getActiveEid() {
        return hivCohorts.getPatientsInExposedChildStateOnEndDate();
    }

    public CohortDefinition getUpcomingIc3Appointment(TraceType traceType, Integer fromDays, Integer toDays) {
        List<ProgramWorkflowState> states = ic3Metadata.getActiveStatesForTrace(traceType);
        List<EncounterType> encounterTypes = ic3Metadata.getEncounterTypesForTrace(traceType);
        return df.getPatientsWithUpcomingAppointmentOnEndDateAtLocation(states, encounterTypes, fromDays, toDays);
    }

    public CohortDefinition getHighPriority() {
	    List<CohortDefinition> l = new ArrayList<CohortDefinition>();
	    Map<HighPriorityCategory, CohortDefinition> m = ccCohorts.getHighPriorityForTraceCohortsAtEnd();
        for (HighPriorityCategory category :  m.keySet()) {
            l.add(m.get(category));
        }
        return df.getPatientsInAny(l.toArray(new CohortDefinition[]{}));
    }

    public EidTestCohortDefinition getBaseEidTestCohortDefinition(TraceType traceType) {
        EidTestCohortDefinition cd = new EidTestCohortDefinition();
        cd.addParameter(ReportingConstants.LOCATION_PARAMETER);
        cd.setTraceType(traceType);
        return cd;
    }

    // ***************** Utilities ********************

    public void add(EvaluatedPatientData data, Category category, CohortDefinition cd, Map<String, Object> mappings, EvaluationContext context) throws EvaluationException {
        Mapped<CohortDefinition> mapped = new Mapped<CohortDefinition>(cd, mappings);
        Cohort c = cohortDefinitionService.evaluate(mapped, context);
        add(data, category, c);
    }

    public void add(EvaluatedPatientData data, Category category, Cohort cohort) throws EvaluationException {
        for (Integer pId : cohort.getMemberIds()) {
            TraceCriteria c = (TraceCriteria) data.getData().get(pId);
            if (c == null) {
                c = new TraceCriteria();
                data.getData().put(pId, c);
            }
            c.addCategory(category);
        }
    }


}
