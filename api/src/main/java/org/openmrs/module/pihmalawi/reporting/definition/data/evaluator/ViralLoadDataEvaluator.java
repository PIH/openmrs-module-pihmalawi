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

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ViralLoadDataDefinition;
import org.openmrs.module.reporting.common.BeanPropertyComparator;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a ViralLoadDataDefinition to produce a PatientData
 */
@Handler(supports = ViralLoadDataDefinition.class, order = 50)
public class ViralLoadDataEvaluator implements PatientDataEvaluator {

	@Autowired
	private HivMetadata metadata;

	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		ViralLoadDataDefinition def = (ViralLoadDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		// Numeric Viral Load Results

        {
            HqlQueryBuilder q = new HqlQueryBuilder();
            q.select("o.personId", "o.obsDatetime", "o.obsGroup", "o.valueNumeric");
            q.from(Obs.class, "o");
            q.wherePersonIn("o.personId", context);
            q.whereEqual("o.concept", metadata.getHivViralLoadConcept());
            q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
            q.orderAsc("o.obsDatetime");

            List<Object[]> results = evaluationService.evaluateToList(q, context);
            for (Object[] row : results) {
                ViralLoad vl = createAndAddViralLoadForPatient(c.getData(), (Integer) row[0]);
                vl.setResultDate((Date) row[1]);
                vl.setGroupId(row[2] == null ? null : ((Obs) row[2]).getObsId());
                vl.setResultNumeric((Double) row[3]);
            }
        }

        // LDL Viral Load Results

        {
            HqlQueryBuilder q = new HqlQueryBuilder();
            q.select("o.personId", "o.obsDatetime", "o.obsGroup");
            q.from(Obs.class, "o");
            q.wherePersonIn("o.personId", context);
            q.whereEqual("o.concept", metadata.getHivLDLConcept());
            q.whereEqual("o.valueCoded", metadata.getTrueConcept());
            q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
            q.orderAsc("o.obsDatetime");

            List<Object[]> results = evaluationService.evaluateToList(q, context);
            for (Object[] row : results) {
                ViralLoad vl = createAndAddViralLoadForPatient(c.getData(), (Integer) row[0]);
                vl.setResultDate((Date) row[1]);
                vl.setGroupId(row[2] == null ? null : ((Obs) row[2]).getObsId());
                vl.setResultLdl(true);
            }
        }

        for (Integer pId : c.getData().keySet()) {
		    List<ViralLoad> l = (List<ViralLoad>) c.getData().get(pId);
		    if (l != null) {
                Collections.sort(l, new BeanPropertyComparator("resultDate asc"));
            }
        }

		return c;
	}

	protected ViralLoad createAndAddViralLoadForPatient(Map<Integer, Object> data, Integer pId) {
        ViralLoad vl = new ViralLoad();
        List<ViralLoad> forPatient = (List<ViralLoad>) data.get(pId);
        if (forPatient == null) {
            forPatient = new ArrayList<ViralLoad>();
            data.put(pId, forPatient);
        }
        forPatient.add(vl);
        return vl;
    }
}
