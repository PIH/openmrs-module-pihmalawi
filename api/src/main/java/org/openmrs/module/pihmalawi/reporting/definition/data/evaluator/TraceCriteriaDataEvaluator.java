/**
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
package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.TraceConstants.HighPriorityCategory;
import org.openmrs.module.pihmalawi.common.TraceCriteria;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
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
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
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
    private HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    private ChronicCareCohortDefinitionLibrary ccCohorts;

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

	@Autowired
	private DataSetDefinitionService dataSetDefinitionService;

	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        TraceCriteriaPatientDataDefinition dd = (TraceCriteriaPatientDataDefinition) definition;
        Location location = dd.getLocation();
        TraceType traceType = dd.getTraceType();

        Map<String, Object> mappings = new HashMap<String, Object>();
        mappings.put(ReportingConstants.LOCATION_PARAMETER.getName(), location);

	    EvaluatedPatientData pd = new EvaluatedPatientData(definition, context);

		add(pd, LATE_HIV_VISIT, getLateHiv(traceType), mappings, context);
        //add(pd, HIGH_VIRAL_LOAD, getHighViralLoad(traceType), mappings, context);
        //add(pd, REPEAT_VIRAL_LOAD, getRepeatViralLoad(traceType), mappings, context);
        //add(pd, EID_12_MONTH_TEST, getEid12Month(traceType), mappings, context);
        //add(pd, EID_24_MONTH_TEST, getEid24Month(traceType), mappings, context);
        //add(pd, EID_POSITIVE_6_WK, getEidPositive6Week(traceType), mappings, context);
        //add(pd, EID_NEGATIVE, getEidNegative(traceType), mappings, context);

        if (!traceType.isPhase1Only()) {
            add(pd, LATE_NCD_VISIT_NORMAL_PRIORITY, df.createPatientComposition(getLateNcd(traceType), "AND NOT ", getHighPriority()), mappings, context);
            add(pd, LATE_NCD_VISIT_HIGH_PRIORITY, df.getPatientsInAll(getLateNcd(traceType), getHighPriority()), mappings, context);
        }

		return pd;
	}

	public CohortDefinition getLateHiv(TraceType traceType) throws EvaluationException {

	    // 2 wk report:  If patient has ART# AND today's date minus appointment date is greater than/equal to 14 days and less than 42 days
        // 6 wk report:  If patient has ART# AND today's date minus appointment date is greater than/equal to 42 days and less than 84 days

        CohortDefinition activeInHivProgram = hivCohorts.getActivelyEnrolledInHivProgramAtLocationOnEndDate();
        CohortDefinition lateForHivVisit = df.getPatientsLateForAppointment(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes(), traceType.getMinDaysInclusive(), traceType.getMaxDaysInclusive());
        return df.getPatientsInAll(activeInHivProgram, lateForHivVisit);
    }

    public CohortDefinition getHighViralLoad() throws EvaluationException {

	    /*
	        2 wk report:

	        If today's date minus last viral load result data entry date (date modified time stamp) is less than/equal to 14 days AND
	        viral load result is greater than 1,000 (anything less than 1,000 is reported as less than the detectable limit or "<LDL")

	        NOTE: When the patient has a viral load test done, it is recorded on the mastercard with the visit date. When the result
	        is entered into the EMR from the lab, they should enter the result to the corresponding visit date.

	     */
	    return null; // TODO:
    }

    public CohortDefinition getRepeatViralLoad() {
        /*
            Logic with stamp: If appointment date minus intensive adherence intervention date is greater than/equal to 70 days and less than 154 days AND
            appointment date minus today's date is greater than/equal to 14 days and less than 28 days

            Alternative logic until stamp finished: if last viral load was greater than 1,000 AND today's date minus  last viral load result
            data entry date (date modified time stamp) is greater than/equal to 84 days and
            less than 168 days AND last visit date minus last viral load result data entry date (date modified time stamp) is greater than 0 AND appointment date
            minus today's date is greater than/equal to 14 days and less than 28 days

            NOTE: I confirmed that Kondwani wants to create a stamp that will go on the mastercard to indicate that the intensive adherence intervention has taken place.
            Can we add a tick box to the visit line so that the EMR has a place to enter if the intervention has taken place?
        */
        return null; // TODO
    }

    public CohortDefinition getEid12Month() {
        /*
            If age is greater than/equal to 12 months AND no value test since 12 months of age AND appointment date minus today's date is greater than/equal to 14 days and less than 28 days
        */
        return null; // TODO
    }

    public CohortDefinition getEid24Month() {
        /*
            If age is greater than/equal to 12 months AND mom has stopped breastfeeding for at least 6 weeks AND no value for test since breastfeeding stopped AND
            appointment date minus today's date is greater than/equal to 14 days and less than 28 days

            NOTE: I confirmed with Kondwani that this last test can take place as soon as the baby has stopped breastfeeding for 6 weeks, even if he/she is less than 24 months.
            After the last test, they are discharged from EID.
        */
        return null; // TODO
    }

    public CohortDefinition getEidPositive6Week() {
        /*
            If today's date minus last test result data entry date is less than/equal to 14 days AND 6-week test result is positive
        */
        return null; // TODO
    }

    public CohortDefinition getEidNegative() {
        /*
            If today's date minus last test result data entry date is less than/equal to 14 days AND result is negative AND second-to-last result was positive

            NOTE: This is a new category requested by Kondwani… these are the kids who had a false positive and now need to be taken off of ART
        */
        return null; // TODO
    }

    public CohortDefinition getLateNcd(TraceType traceType) {
        // 2 wk report:  If patient has NCD# AND today's date minus appointment date is greater than/equal to 14 days and less than 42 days
        // 6 wk report:  If patient has NCD# AND today's date minus appointment date is greater than/equal to 42 days and less than 84 days

        CohortDefinition activeInCCProgram = ccCohorts.getActivelyEnrolledInChronicCareProgramAtLocationOnEndDate();
        CohortDefinition lateForCCVisit = df.getPatientsLateForAppointment(ccMetadata.getActiveChronicCareStates(), ccMetadata.getChronicCareScheduledVisitEncounterTypes(), traceType.getMinDaysInclusive(), traceType.getMaxDaysInclusive());
        return df.getPatientsInAll(activeInCCProgram, lateForCCVisit);
    }

    public CohortDefinition getHighPriority() {
	    List<CohortDefinition> l = new ArrayList<CohortDefinition>();
	    Map<HighPriorityCategory, CohortDefinition> m = ccCohorts.getHighPriorityForTraceCohortsAtEnd();
        for (HighPriorityCategory category :  m.keySet()) {
            l.add(m.get(category));
        }
        return df.getPatientsInAny(l.toArray(new CohortDefinition[]{}));
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
