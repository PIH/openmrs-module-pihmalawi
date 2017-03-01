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
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.TraceConstants.HighPriorityCategory;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.PriorityPatientForTracePatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Evaluates a PriorityPatientForTracePatientDataDefinition to produce a PatientData
 */
@Handler(supports = PriorityPatientForTracePatientDataDefinition.class, order = 50)
public class PriorityPatientForTraceDataEvaluator implements PatientDataEvaluator {

    @Autowired
    private ChronicCareCohortDefinitionLibrary ccCohorts;


    @Autowired
    private CohortDefinitionService cohortDefinitionService;


	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPatientData pd = new EvaluatedPatientData(definition, context);
        Map<HighPriorityCategory, CohortDefinition> categories = ccCohorts.getHighPriorityForTraceCohortsAtEnd();
        for (HighPriorityCategory category : categories.keySet()) {
            String label = category.getDescription();
            CohortDefinition cd = categories.get(category);
            Cohort c = cohortDefinitionService.evaluate(cd, context);
            add(pd, label, c);
        }
		return pd;
	}

    public void add(EvaluatedPatientData data, String priorityReason, Cohort cohort) throws EvaluationException {
        for (Integer pId : cohort.getMemberIds()) {
            Set<String> l = (TreeSet<String>) data.getData().get(pId);
            if (l == null) {
                l = new TreeSet<String>();
                data.getData().put(pId, l);
            }
            l.add(priorityReason);
        }
    }
}
