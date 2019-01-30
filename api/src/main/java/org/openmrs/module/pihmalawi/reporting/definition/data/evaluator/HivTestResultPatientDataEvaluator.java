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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.htmlwidgets.util.ReflectionUtil;
import org.openmrs.module.pihmalawi.common.HivTestResult;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.HivTestResultPatientDataDefinition;
import org.openmrs.module.reporting.common.BeanPropertyComparator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a HivTestResultPatientDataDefinition to produce a PatientData
 */
@Handler(supports = HivTestResultPatientDataDefinition.class, order = 50)
public class HivTestResultPatientDataEvaluator implements PatientDataEvaluator {

    protected static final Log log = LogFactory.getLog(HivTestResultPatientDataEvaluator.class);

	@Autowired
	private HivMetadata metadata;

	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        HivTestResultPatientDataDefinition def = (HivTestResultPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

        Map<Integer, HivTestResult> resultsByGroup = new HashMap<Integer, HivTestResult>();

        // Initialize results to return by querying the result concept

        List<Obs> resultObsList = getObs(metadata.getAllHivTestResultConcepts(), context);
        for (Obs resultObs : resultObsList) {
            HivTestResult result = new HivTestResult(resultObs);
            if (resultObs.getObsGroup() != null) {
                resultsByGroup.put(resultObs.getObsGroup().getObsId(), result);
            }
            List<HivTestResult> resultsForPatient = (List<HivTestResult>)c.getData().get(resultObs.getPersonId());
            if (resultsForPatient == null) {
                resultsForPatient = new ArrayList<HivTestResult>();
                c.getData().put(resultObs.getPersonId(), resultsForPatient);
            }
            resultsForPatient.add(result);
        }

        // Add in all associated data by matching on obs groups

        List<Obs> typeObsList = getObs(metadata.getHivTestType(), context);
        addIfMatches(resultsByGroup, typeObsList, "testTypeObs");

        List<Obs> sampleDateObsList = getObs(metadata.getHivTestSampleDateConcept(), context);
        addIfMatches(resultsByGroup, sampleDateObsList, "specimenDateObs");

        List<Obs> resultDateObsList = getObs(metadata.getHivTestResultDateConcept(), context);
        addIfMatches(resultsByGroup, resultDateObsList, "resultDateObs");

        // Sort and filter for each patient

        Concept positive = metadata.getPositiveConcept();
        Concept negative = metadata.getNegativeConcept();
        Concept reactive = metadata.getReactiveConcept();
        Concept nonReactive = metadata.getNonReactiveConcept();
        Concept hivRapidTest = metadata.getHivRapidTest();
        Concept hivDnaPcrTest = metadata.getHivDnaPcrTest();
        List<Concept> dnaPcrResultConcepts = metadata.getHivDnaPcrTestResultConcepts();

        for (Integer pId : c.getData().keySet()) {

            List<HivTestResult> l = (List<HivTestResult>)c.getData().get(pId);

            // Iterate over results

            for (Iterator<HivTestResult> i = l.iterator(); i.hasNext(); ) {
                HivTestResult r = i.next();

                // Effective Date
                if (r.getSpecimenDateObs() != null) {
                    r.setEffectiveDate(r.getSpecimenDateObs().getValueDatetime());
                }
                else if (r.getResultDateObs() != null) {
                    r.setEffectiveDate(r.getResultDateObs().getValueDate());
                }
                else {
                    r.setEffectiveDate(r.getTestResultObs().getObsDatetime());
                }

                // Test Type
                if (r.getTestTypeObs() != null) {
                    r.setTestType(r.getTestTypeObs().getValueCoded());
                }
                else if (r.getTestResultObs() != null) {
                    if (r.getTestResultObs().getConcept().equals(hivRapidTest)) {
                        r.setTestType(r.getTestResultObs().getConcept());
                    }
                    else if (dnaPcrResultConcepts.contains(r.getTestResultObs().getConcept())) {
                        r.setTestType(hivDnaPcrTest);
                    }
                }
                if (r.getTestType() == null ) {
                    r.setTestType(hivRapidTest);
                }

                // Test Result
                if (r.getTestResultObs() != null) {
                    Concept valueCoded = r.getTestResultObs().getValueCoded();
                    if (valueCoded.equals(reactive)) {
                        valueCoded = positive;
                    }
                    else if (valueCoded.equals(nonReactive)) {
                        valueCoded = negative;
                    }
                    r.setTestResult(valueCoded);
                }

                // Filter out results based on effective date
                if (def.getEndDate() != null && DateUtil.getStartOfDay(r.getEffectiveDate()).after(def.getEndDate())) {
                    i.remove();
                }
            }

            // Sort by effective date ascending

            Collections.sort(l, new BeanPropertyComparator("effectiveDate asc"));
        }

		return c;
	}

	protected void addIfMatches(Map<Integer, HivTestResult> resultsByGroup, List<Obs> obsToAdd, String propertyToSet) {
        for (Obs o : obsToAdd) {
            HivTestResult match = null;
            if (o.getObsGroup() != null) {
                match = resultsByGroup.get(o.getObsGroup().getObsId());
            }
            if (match != null) {
                ReflectionUtil.setPropertyValue(match, propertyToSet, o);
            }
            else {
                log.trace("Found " + propertyToSet + " Obs with no corresponding HIV Result Obs. Obs ID: " + o.getObsId());
            }
        }
    }

    protected List<Obs> getObs(Concept question, EvaluationContext context) {
	    return getObs(Arrays.asList(question), context);
    }

	protected List<Obs> getObs(List<Concept> questions, EvaluationContext context) {
        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("o");
        q.from(Obs.class, "o");
        q.wherePersonIn("o.personId", context);
        q.whereIn("o.concept", questions);
        return evaluationService.evaluateToList(q, Obs.class, context);
    }
}
