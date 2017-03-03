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
package org.openmrs.module.pihmalawi.reporting.definition.cohort.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.TraceConstants;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.IC3Metadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.EidTestCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Handler(supports = { EidTestCohortDefinition.class })
public class EidTestCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	CohortDefinitionService cohortDefinitionService;

    @Autowired
    BasePatientDataLibrary baseData;

    @Autowired
    PatientDataService patientDataService;

    @Autowired
    EvaluationService evaluationService;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    IC3Metadata ic3Metadata;

    @Autowired
    DataFactory df;

    /**
     * active eid with no test results since birthdate+12m
     * active eid with no no value for test since ( 6 weeks after? ) breastfeeding stopped
     * 6wk eid test result is positive
     * today-lastEidTestResultDate <= X days
     * second to last eid test result is positive
     * last eid test result is negative
     */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        EidTestCohortDefinition cd = (EidTestCohortDefinition) cohortDefinition;

        Cohort ret = new Cohort();

        Date today = DateUtil.getStartOfDay(new Date());
        Date maxTestResultDate = (cd.getLastTestResultWithinLastDays() != null ? DateUtil.getEndOfDay(DateUtil.adjustDate(today, cd.getLastTestResultWithinLastDays(), DurationUnit.DAYS)) : null);

        Cohort eidPats = cohortDefinitionService.evaluate(hivCohorts.getPatientsInExposedChildStateAtLocationOnEndDate(), context);
        PatientData hivResults = getHivTestResults(context);
        PatientData birthdates = null;
        PatientData breastfeedingStatuses = null;
        PatientData lastVisitDates = null;

        for (Integer pId : eidPats.getMemberIds()) {

            List<Boolean> allChecks = new ArrayList<Boolean>();

            List<Obs> hivObs = ObjectUtil.nvl((List<Obs>)hivResults.getData().get(pId), new ArrayList<Obs>());

            // NO TEST RESULT AFTER 12 MONTHS OLD

            if (cd.isNoTestResultsAfter12MonthsOld()) {
                if (birthdates == null) {
                    birthdates = getBirthdates(context);
                }
                boolean hasResult = false;
                Date birthdate = (Date) birthdates.getData().get(pId);
                if (birthdate != null) {
                    Date dateToCheck = DateUtil.adjustDate(birthdate, 1, DurationUnit.YEARS);
                    for (Obs o : hivObs) {
                        if (o.getDateCreated().compareTo(dateToCheck) >= 0) {
                            hasResult = true;
                        }
                    }
                }
                allChecks.add(!hasResult);
            }

            // NO TEST RESULT AFTER BREASTFEEDING STOPPED FOR >= 6 WEEKS
            // TODO: What about getEidHivTestingCompletedCodedConcept() value on or after this obs date?  Should this be instead or in addition to result entry dates?

            if (cd.isNoTestResultsAfterBreastfeeding()) {
                if (breastfeedingStatuses == null) {
                    breastfeedingStatuses = getLatestBreastfeedingStatus(context);
                }
                boolean hasResult = false;
                Obs breastfeedingStatus = (Obs) breastfeedingStatuses.getData().get(pId);
                if (breastfeedingStatus != null && breastfeedingStatus.getValueCoded().equals(hivMetadata.getEidBreastfeedingStoppedOver6WeeksAgoConcept())) {
                    Date dateToCheck = breastfeedingStatus.getObsDatetime();

                    for (Obs o : hivObs) {
                        if (o.getDateCreated().compareTo(dateToCheck) >= 0) {
                            hasResult = true;
                        }
                    }
                }
                allChecks.add(!hasResult);
            }

            // CHECKS ON TEST RESULTS

            int numTestResults = hivObs.size();
            Date lastTestResultDateCreated = null;
            Concept lastTestResult = null;
            Concept secondToLastTestResult = null;
            if (numTestResults > 0) {
                Obs lastTestObs = hivObs.get(numTestResults - 1);
                lastTestResult = lastTestObs.getValueCoded();
                lastTestResultDateCreated = lastTestObs.getDateCreated();
            }
            if (numTestResults > 1) {
                secondToLastTestResult = hivObs.get(numTestResults - 2).getValueCoded();
            }

            // MAXIMUM NUMBER OF TEST RESULTS

            if (cd.getMaximumNumberOfTestResults() != null) {
                allChecks.add(numTestResults <= cd.getMaximumNumberOfTestResults());
            }

            // LAST TEST RESULT WITHIN THE LAST X DAYS

            if (cd.getLastTestResultWithinLastDays() != null) {
                allChecks.add(lastTestResultDateCreated != null && lastTestResultDateCreated.compareTo(maxTestResultDate) <= 0);
            }

            // NO VISIT SINCE LAST TEST RESULT

            if (cd.isNoVisitSinceLastTestResult()) {
                if (lastVisitDates == null) {
                    lastVisitDates = getLastVisitDates(cd.getTraceType(), context);
                }
                Date lastVisitDate = (Date)lastVisitDates.getData().get(pId);
                allChecks.add(lastTestResultDateCreated != null && (lastVisitDate == null || lastVisitDate.compareTo(DateUtil.getStartOfDay(lastTestResultDateCreated)) < 0));
            }

            // LAST TEST RESULT VALUE

            if (cd.getLastTestResult() != null) {
                allChecks.add(lastTestResult != null && lastTestResult.equals(cd.getLastTestResult()));
            }

            // SECOND TO LAST TEST RESULT VALUE

            if (cd.getSecondToLastTestResult() != null) {
                allChecks.add(secondToLastTestResult != null && secondToLastTestResult.equals(cd.getSecondToLastTestResult()));
            }

            // Add patient to cohort if all checks pass, and at least one of the checks was run

            boolean addPatient = !allChecks.isEmpty();
            for (boolean b : allChecks) {
                addPatient = addPatient && b;
            }
            if (addPatient) {
                ret.addMember(pId);
            }
        }

        return new EvaluatedCohort(ret, cd, context);
	}

    protected PatientData getBirthdates(EvaluationContext context) throws EvaluationException {
        return patientDataService.evaluate(baseData.getBirthdate(), context);
    }

	protected PatientData getHivTestResults(EvaluationContext context) throws EvaluationException {
        ObsForPersonDataDefinition obsDef = new ObsForPersonDataDefinition();
        obsDef.setQuestion(hivMetadata.getResultOfHivTestConcept());
        return patientDataService.evaluate(df.convert(obsDef, null), context);
    }

    protected PatientData getLatestBreastfeedingStatus(EvaluationContext context) throws EvaluationException {
        ObsForPersonDataDefinition obsDef = new ObsForPersonDataDefinition();
        obsDef.setWhich(TimeQualifier.LAST);
        obsDef.setQuestion(hivMetadata.getEidBreastfeedingStatusConcept());
        return patientDataService.evaluate(df.convert(obsDef, null), context);
    }

    protected PatientData getLastVisitDates(TraceConstants.TraceType traceType, EvaluationContext context) throws EvaluationException {
	    PatientDataDefinition def = df.getMostRecentEncounterOfTypesByEndDate(ic3Metadata.getEncounterTypesForTrace(traceType), null);
	    return patientDataService.evaluate(def, context);
    }
}