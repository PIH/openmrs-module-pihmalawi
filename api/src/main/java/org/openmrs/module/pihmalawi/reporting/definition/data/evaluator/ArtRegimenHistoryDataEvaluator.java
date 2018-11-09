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
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.ArtRegimen;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ArtRegimenHistoryDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.common.BeanPropertyComparator;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a ArtRegimenHistoryDataDefinition to produce a PatientData
 */
@Handler(supports = ArtRegimenHistoryDataDefinition.class, order = 50)
public class ArtRegimenHistoryDataEvaluator implements PatientDataEvaluator {

    protected static final Log log = LogFactory.getLog(ArtRegimenHistoryDataEvaluator.class);

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private PatientDataService patientDataService;

    @Autowired
    private DataFactory df;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        ArtRegimenHistoryDataDefinition def = (ArtRegimenHistoryDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

        PatientDataDefinition arvDrugsReceived =  df.getAllObsByEndDate(hivMetadata.getArvDrugsReceivedConcept(), null, null);
		addData(c, arvDrugsReceived, null, context);

		PatientDataDefinition arvDrugsChange1 =  df.getAllObsByEndDate(hivMetadata.getArvDrugsChange1Concept(), null, null);
        PatientDataDefinition arvDrugsChange1Date =  df.getAllObsByEndDate(hivMetadata.getDateOfStartingFirstLineArvsConcept(), null, null);
        addData(c, arvDrugsChange1, arvDrugsChange1Date, context);

        PatientDataDefinition arvDrugsChange2 =  df.getAllObsByEndDate(hivMetadata.getArvDrugsChange2Concept(), null, null);
        PatientDataDefinition arvDrugsChange2Date =  df.getAllObsByEndDate(hivMetadata.getDateOfStartingAlternativeFirstLineArvsConcept(), null, null);
        addData(c, arvDrugsChange2, arvDrugsChange2Date, context);

        PatientDataDefinition arvDrugsChange3 =  df.getAllObsByEndDate(hivMetadata.getArvDrugsChange3Concept(), null, null);
        PatientDataDefinition arvDrugsChange3Date =  df.getAllObsByEndDate(hivMetadata.getDateOfStartingSecondLineArvsConcept(), null, null);
        addData(c, arvDrugsChange3, arvDrugsChange3Date, context);

        // Sort Data by date asc
        for (Integer pId : c.getData().keySet()) {
            List<ArtRegimen> regimenList = (List<ArtRegimen>) c.getData().get(pId);
            Collections.sort(regimenList, new BeanPropertyComparator("regimenDate asc"));
        }

		return c;
	}

	protected void addData(EvaluatedPatientData c, PatientDataDefinition regimenObsDef, PatientDataDefinition dateObsDef, EvaluationContext context) throws EvaluationException {

	    PatientData regimenObsData = patientDataService.evaluate(regimenObsDef, context);
	    PatientData dateObsData = null;
	    if (dateObsDef != null) {
            dateObsData = patientDataService.evaluate(dateObsDef, context);
        }

        for (Integer pId: regimenObsData.getData().keySet()) {

            List<ArtRegimen> regimensForPatient = (List<ArtRegimen>) c.getData().get(pId);
            if (regimensForPatient == null) {
                regimensForPatient = new ArrayList<ArtRegimen>();
                c.getData().put(pId, regimensForPatient);
            }

            Map<Integer, ArtRegimen> regimenForEncounter = new HashMap<Integer, ArtRegimen>();
            List<Obs> regimenObsList = (List<Obs>) regimenObsData.getData().get(pId);
            for (Obs regimenObs : regimenObsList) {
                ArtRegimen regimen = new ArtRegimen(regimenObs);
                regimenForEncounter.put(regimenObs.getEncounter().getEncounterId(), regimen);
            }
            if (dateObsData != null) {
                List<Obs> regimenDates = (List<Obs>)dateObsData.getData().get(pId);
                if (regimenDates != null) {
                    for (Obs regimenDateObs : regimenDates) {
                        ArtRegimen regimen = regimenForEncounter.get(regimenDateObs.getEncounter().getEncounterId());
                        if (regimen == null) {
                            log.debug("Regimen Date Obs found without a matching Regimen Obs: " + regimenDateObs);
                        }
                        else {
                            regimen.setDateObs(regimenDateObs);
                        }
                    }
                }
            }
            regimensForPatient.addAll(regimenForEncounter.values());
        }
    }
}
