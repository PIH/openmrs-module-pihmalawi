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
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.DiagnosesBasedOnMastercardsPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Evaluates a DiagnosesBasedOnMastercardsPatientDataDefinition to produce a PatientData
 */
@Handler(supports = DiagnosesBasedOnMastercardsPatientDataDefinition.class, order = 50)
public class DiagnosesBasedOnMastercardsPatientDataEvaluator implements PatientDataEvaluator {

    @Autowired
    private DataFactory df;

    @Autowired
    private HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    private ChronicCareMetadata ccMetadata;

    @Autowired
    private ChronicCareCohortDefinitionLibrary ccCohorts;

    @Autowired
    private ChronicCarePatientDataLibrary ccPatientData;

    @Autowired
    private PatientDataService patientDataService;

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedPatientData pd = new EvaluatedPatientData(definition, context);

        add(pd, "ART", hivCohorts.getPatientsWithAnArtEncounterByEndDate(), context);
        add(pd, "Epilepsy", df.getAnyEncounterOfTypesByEndDate(ccMetadata.getEpilepsyEncounterTypes()), context);
        add(pd, "Mental Health", df.getAnyEncounterOfTypesByEndDate(ccMetadata.getMentalHealthEncounterTypes()), context);

        Map<Concept, String> diagnosesDisplay = new LinkedHashMap<Concept, String>();
        diagnosesDisplay.put(ccMetadata.getCopdConcept(), "COPD");
        diagnosesDisplay.put(ccMetadata.getType1DiabetesConcept(), "Type 1 DM");
        diagnosesDisplay.put(ccMetadata.getType2DiabetesConcept(), "Type 2 DM");
        diagnosesDisplay.put(ccMetadata.getHypertensionConcept(), "Hypertension");

        PatientData diagosisData = patientDataService.evaluate(ccPatientData.getAllChronicCareDiagnosisObsByEndDate(), context);
        for (Integer pId : diagosisData.getData().keySet()) {
            List<Obs> diagnosisObs = (List<Obs>) diagosisData.getData().get(pId);
            for (Obs o : diagnosisObs) {
                Concept diagnosis = o.getValueCoded();
                String diagnosisName = ObjectUtil.nvl(diagnosesDisplay.get(diagnosis), ObjectUtil.format(o.getValueCoded()));
                addData(pd, pId, diagnosisName);
            }
        }

		return pd;
	}

    public void add(EvaluatedPatientData data, String key, CohortDefinition cd, EvaluationContext context) throws EvaluationException {
        Cohort c = cohortDefinitionService.evaluate(cd, context);
        add(data, key, c);
    }

    public void add(EvaluatedPatientData data, String key, Cohort cohort) throws EvaluationException {
        for (Integer pId : cohort.getMemberIds()) {
            addData(data, pId, key);
        }
    }

    public void addData(EvaluatedPatientData data, Integer pId, String key) {
        Set<String> l = (TreeSet<String>)data.getData().get(pId);
        if (l == null) {
            l = new TreeSet<String>();
            data.getData().put(pId, l);
        }
        l.add(key);
    }
}
