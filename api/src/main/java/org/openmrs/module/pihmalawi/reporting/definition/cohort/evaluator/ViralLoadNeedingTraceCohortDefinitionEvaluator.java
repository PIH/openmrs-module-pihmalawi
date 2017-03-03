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
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.IC3Metadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.ViralLoadNeedingTraceCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Handler(supports = { ViralLoadNeedingTraceCohortDefinition.class })
public class ViralLoadNeedingTraceCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
    PatientDataService patientDataService;

	@Autowired
    DataFactory df;

	@Autowired
    HivPatientDataLibrary hivData;

	@Autowired
    BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    IC3Metadata ic3Metadata;


	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        ViralLoadNeedingTraceCohortDefinition cd = (ViralLoadNeedingTraceCohortDefinition) cohortDefinition;

        EvaluationContext childContext = context.shallowCopy();
        childContext.addParameterValue(ReportingConstants.LOCATION_PARAMETER.getName(), cd.getLocation());

        PatientDataDefinition viralLoadDataDefinition = hivData.getViralLoadsForTraceAtLocation(1000d, cd.getMinDaysInPast(), cd.getMaxDaysInPast());
        PatientData viralLoads = patientDataService.evaluate(Mapped.mapStraightThrough(viralLoadDataDefinition), childContext);

        Cohort ret = new Cohort(viralLoads.getData().keySet());

        if (cd.getEnsureNoSubsequentVisit()) {

            List<EncounterType> encounterTypes = ic3Metadata.getEncounterTypesForTrace(cd.getTraceType());
            PatientDataDefinition latestEncounterDataDef = df.getMostRecentEncounterOfTypesAtLocationByEndDate(encounterTypes, df.getEncounterDatetimeConverter());
            PatientData latestEncounterDates = patientDataService.evaluate(Mapped.mapStraightThrough(latestEncounterDataDef), childContext);

            // If the patient has no IC3 encounters, or has matching viral load obs that were created after their latest IC3 encounter date, return them

            Cohort keep = new Cohort();
            for (Integer pId : viralLoads.getData().keySet()) {
                List<Obs> obsList = (List<Obs>) viralLoads.getData().get(pId);
                Date encDate = (Date) latestEncounterDates.getData().get(pId);
                if (encDate == null) {
                    keep.addMember(pId);
                }
                else {
                    Date encDateEndOfDay = DateUtil.getEndOfDay(encDate);
                    for (Obs o : obsList) {
                        if (o.getDateCreated().after(encDateEndOfDay)) {
                            keep.addMember(pId);
                        }
                    }
                }
            }

            ret = Cohort.intersect(ret, keep);
        }

		return new EvaluatedCohort(ret, cd, context);
	}

}