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
import org.openmrs.EncounterType;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.HighBloodPressureCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;

@Handler(supports = { HighBloodPressureCohortDefinition.class })
public class HighBloodPressureCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
    DataSetDefinitionService dataSetDefinitionService;

	@Autowired
    DataFactory df;

	@Autowired
    ChronicCareMetadata ccMetadata;

	@Autowired
    BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        HighBloodPressureCohortDefinition cd = (HighBloodPressureCohortDefinition) cohortDefinition;
		EvaluationContext childContext = context.shallowCopy();
		childContext.addParameterValue("endDate", cd.getEndDate());

        Cohort c = new Cohort();

        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);

        List<EncounterType> types = ccMetadata.getChronicCareEncounterTypes();
        types.addAll(ccMetadata.getHtnDiabetesEncounterTypes());
        dsd.addRowFilter(Mapped.mapStraightThrough(df.getEncountersOfTypeByEndDate(types)));

        dsd.addColumn("PID", builtInEncounterData.getPatientId(), "");
        dsd.addColumn("SYS", df.getSingleObsValueNumericForEncounter(ccMetadata.getSystolicBloodPressureConcept()), "");
        dsd.addColumn("DIAS", df.getSingleObsValueNumericForEncounter(ccMetadata.getDiastolicBloodPressureConcept()), "");

        DataSet ds = dataSetDefinitionService.evaluate(dsd, childContext);
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

		return new EvaluatedCohort(c, cd, context);
	}
}