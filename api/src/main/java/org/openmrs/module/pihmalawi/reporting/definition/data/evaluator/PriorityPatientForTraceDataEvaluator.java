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
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.PriorityPatientForTracePatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Evaluates a DiagnosesBasedOnMastercardsPatientDataDefinition to produce a PatientData
 */
@Handler(supports = PriorityPatientForTracePatientDataDefinition.class, order = 50)
public class PriorityPatientForTraceDataEvaluator implements PatientDataEvaluator {

    @Autowired
    private DataFactory df;

    @Autowired
    private ChronicCareMetadata ccMetadata;

    @Autowired
    private HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    private ChronicCareCohortDefinitionLibrary ccCohorts;

    @Autowired
    private BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

	@Autowired
	private DataSetDefinitionService dataSetDefinitionService;

	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		EvaluatedPatientData pd = new EvaluatedPatientData(definition, context);
        add(pd, "HIV", hivCohorts.getEverEnrolledInHivProgramByEndDate(), context);  // 1. HIV patients (all)
        add(pd, "BP > 180/110", getPatientsEverWithHighBloodPressureByEndDate(context)); // 2. Hypertension patients with BP ever greater than 180/110 (both systolic and diastolic should exceed threshold)
        add(pd, "On Insulin", df.getPatientsWithCodedObsByEndDate(ccMetadata.getCurrentDrugsUsedConcept(), Arrays.asList(ccMetadata.getInsulinConcept())), context); // 3. Diabetes patients on insulin
        add(pd, "Severe Persistent Asthma", ccCohorts.getPatientsWithMostRecentSeverePersistentAsthmaByEndDate(), context); // 4. Asthma patients with severity of “severe persistent” at last visit
        add(pd, "> 5 Siezures per month", ccCohorts.getPatientsWithMoreThanFiveSeizuresPerMonthRecordedInLastVisitByEndDate(), context); // 5. Epilepsy patients reporting over 5 seizures per month at last visit
        add(pd, "Sickle Cell Disease", df.getPatientsWithAnyObsByEndDate(ccMetadata.getSickleCellDiseaseConcept()), context); // 6. Sickle cell disease patients (all)
        add(pd, "Chronic Kidney Disease", df.getPatientsWithAnyObsByEndDate(ccMetadata.getChronicKidneyDiseaseConcept()), context); // 7. Chronic kidney disease patients (all)
        add(pd, "Rheumatic Heart Disease", df.getPatientsWithAnyObsByEndDate(ccMetadata.getRheumaticHeartDiseaseConcept()), context); // 8. Rheumatic Heart Disease patients (all)
        add(pd, "Congestive Heart Failure", df.getPatientsWithAnyObsByEndDate(ccMetadata.getCongestiveHeartFailureConcept()), context); // 9. Congestive Heart Failure patients (all)

		return pd;
	}

    public Cohort getPatientsEverWithHighBloodPressureByEndDate(EvaluationContext context) throws EvaluationException {
        Cohort c = new Cohort();

        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);

        List<EncounterType> types = ccMetadata.getChronicCareEncounterTypes();
        types.addAll(ccMetadata.getHtnDiabetesEncounterTypes());
        dsd.addRowFilter(Mapped.mapStraightThrough(df.getEncountersOfTypeByEndDate(types)));

        dsd.addColumn("PID", builtInEncounterData.getPatientId(), "");
        dsd.addColumn("SYS", df.getSingleObsValueNumericForEncounter(ccMetadata.getSystolicBloodPressureConcept()), "");
        dsd.addColumn("DIAS", df.getSingleObsValueNumericForEncounter(ccMetadata.getDiastolicBloodPressureConcept()), "");

        DataSet ds = dataSetDefinitionService.evaluate(dsd, context);
        for (Iterator<DataSetRow> iterator = ds.iterator(); iterator.hasNext();) {
            DataSetRow row = iterator.next();
            Integer pId = (Integer) row.getColumnValue("PID");
            Double sys = (Double) row.getColumnValue("SYS");
            Double dias = (Double) row.getColumnValue("DIAS");
            // Hypertension patients with BP ever greater than 180/110 (both systolic and diastolic should exceed threshold)
            if (sys != null && dias != null && sys > 180 && dias > 110) {
                c.addMember(pId);
            }
        }

        return c;
    }

    public void add(EvaluatedPatientData data, String priorityReason, CohortDefinition cd, EvaluationContext context) throws EvaluationException {
        Cohort c = cohortDefinitionService.evaluate(cd, context);
        add(data, priorityReason, c);
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
