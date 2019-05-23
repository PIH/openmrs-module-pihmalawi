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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a ViralLoadDataDefinition to produce a PatientData
 */
@Handler(supports = ViralLoadDataDefinition.class, order = 50)
public class ViralLoadDataEvaluator implements PatientDataEvaluator {

    protected static final Log log = LogFactory.getLog(ViralLoadDataEvaluator.class);

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

		// Viral Load Specimen Date (Bled)

        {
            HqlQueryBuilder q = new HqlQueryBuilder();
            q.select("o.personId", "e.encounterId", "g.obsId", "e.encounterDatetime");
            q.from(Obs.class, "o");
            q.innerJoin("o.encounter", "e");
            q.leftOuterJoin("o.obsGroup", "g");
            q.wherePersonIn("o.personId", context);
            q.whereEqual("o.concept", metadata.getHivViralLoadSpecimenCollectedConcept());
            q.whereEqual("o.valueCoded", metadata.getTrueConcept());
            q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
            q.orderAsc("o.obsDatetime");

            List<Object[]> results = evaluationService.evaluateToList(q, context);
            for (Object[] row : results) {
                ViralLoad vl = getViralLoadForPatient(c.getData(), (Integer) row[0], (Integer) row[1], (Integer) row[2]);
                vl.setSpecimenDate((Date) row[3]);
            }
        }

        // Numeric Viral Load Results

        {
            HqlQueryBuilder q = new HqlQueryBuilder();
            q.select("o.personId", "e.encounterId", "g.obsId", "o.obsDatetime", "o.valueNumeric", "e.encounterDatetime");
            q.from(Obs.class, "o");
            q.innerJoin("o.encounter", "e");
            q.leftOuterJoin("o.obsGroup", "g");
            q.wherePersonIn("o.personId", context);
            q.whereEqual("o.concept", metadata.getHivViralLoadConcept());
            q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
            q.orderAsc("o.obsDatetime");

            List<Object[]> results = evaluationService.evaluateToList(q, context);
            for (Object[] row : results) {
                ViralLoad vl = getViralLoadForPatient(c.getData(), (Integer) row[0], (Integer) row[1], (Integer) row[2]);
                vl.setResultDate((Date) row[3]);
                vl.setResultNumeric((Double) row[4]);
                if (vl.getSpecimenDate() == null) {
                    vl.setSpecimenDate((Date) row[5]); // If result does not match up with a collection obs, use encounter date as specimen collection date
                }
            }
        }


        // Numeric Less Than Viral Load Results

        {
            HqlQueryBuilder q = new HqlQueryBuilder();
            q.select("o.personId", "e.encounterId", "g.obsId", "o.obsDatetime", "o.valueNumeric", "e.encounterDatetime");
            q.from(Obs.class, "o");
            q.innerJoin("o.encounter", "e");
            q.leftOuterJoin("o.obsGroup", "g");
            q.wherePersonIn("o.personId", context);
            q.whereEqual("o.concept", metadata.getHivLessThanViralLoadConcept());
            q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
            q.orderAsc("o.obsDatetime");

            List<Object[]> results = evaluationService.evaluateToList(q, context);
            for (Object[] row : results) {
                ViralLoad vl = getViralLoadForPatient(c.getData(), (Integer) row[0], (Integer) row[1], (Integer) row[2]);
                vl.setResultDate((Date) row[3]);
                vl.setLessThanResultNumeric((Double) row[4]);
                if (vl.getSpecimenDate() == null) {
                    vl.setSpecimenDate((Date) row[5]); // If result does not match up with a collection obs, use encounter date as specimen collection date
                }
            }
        }

        // LDL Viral Load Results

        {
            HqlQueryBuilder q = new HqlQueryBuilder();
            q.select("o.personId", "e.encounterId", "g.obsId", "o.obsDatetime", "e.encounterDatetime");
            q.from(Obs.class, "o");
            q.innerJoin("o.encounter", "e");
            q.leftOuterJoin("o.obsGroup", "g");
            q.wherePersonIn("o.personId", context);
            q.whereEqual("o.concept", metadata.getHivLDLConcept());
            q.whereEqual("o.valueCoded", metadata.getTrueConcept());
            q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
            q.orderAsc("o.obsDatetime");

            List<Object[]> results = evaluationService.evaluateToList(q, context);
            for (Object[] row : results) {
                ViralLoad vl = getViralLoadForPatient(c.getData(), (Integer) row[0], (Integer) row[1], (Integer) row[2]);
                vl.setResultDate((Date) row[3]);
                vl.setResultLdl(true);
                if (vl.getSpecimenDate() == null) {
                    vl.setSpecimenDate((Date) row[4]); // If result does not match up with a collection obs, use encounter date as specimen collection date
                }
            }
        }

        // Reason For Test and Reason No Result

        Concept reasonForTest = metadata.getReasonForTestingConcept();
		Concept reasonNoResult = metadata.getReasonNoResultConcept();

        {
            HqlQueryBuilder q = new HqlQueryBuilder();
            q.select("o.personId", "e.encounterId", "g.obsId", "o.concept", "o.valueCoded", "o.obsDatetime");
            q.from(Obs.class, "o");
            q.innerJoin("o.encounter", "e");
            q.leftOuterJoin("o.obsGroup", "g");
            q.wherePersonIn("o.personId", context);
            q.whereIn("o.concept", Arrays.asList(metadata.getReasonForTestingConcept(), metadata.getReasonNoResultConcept()));
            q.whereNotNull("o.obsGroup");
            q.whereEqual("o.obsGroup.concept", metadata.getHivViralLoadTestSetConcept());
            q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
            q.orderAsc("o.obsDatetime");

            List<Object[]> results = evaluationService.evaluateToList(q, context);
            for (Object[] row : results) {
                ViralLoad vl = getViralLoadForPatient(c.getData(), (Integer) row[0], (Integer) row[1], (Integer) row[2]);
                Concept question = (Concept)row[3];
                Concept answer = (Concept)row[4];
                if (question.equals(reasonForTest)) {
                    vl.setReasonForTest(answer);
                }
                else if (question.equals(reasonNoResult)) {
                    vl.setReasonNoResult(answer);
                    vl.setResultDate((Date)row[5]);
                }
                else {
                    throw new EvaluationException("Unhandled question: " + question.getUuid());
                }
            }
        }

        for (Integer pId : c.getData().keySet()) {
		    List<ViralLoad> l = (List<ViralLoad>) c.getData().get(pId);
		    if (l != null) {
                for (Iterator<ViralLoad> i = l.iterator(); i.hasNext();) {
                    ViralLoad vl = i.next();
                    if (vl.getSpecimenDate() == null && vl.getResultDate() == null) {
                        log.debug("Dangling viral load obs group found: " + vl.getGroupId());
                        i.remove();  // Remove any viral loads that have neither a collection nor any result data
                    }
                }
                Collections.sort(l, new BeanPropertyComparator("effectiveDate asc"));
            }
        }

		return c;
	}

	protected ViralLoad getViralLoadForPatient(Map<Integer, Object> data, Integer pId, Integer encId, Integer groupId) {
        List<ViralLoad> l = (List<ViralLoad>) data.get(pId);
        if (l == null) {
            l = new ArrayList<ViralLoad>();
            data.put(pId, l);
        }
        ViralLoad vl = getMatchingViralLoad(l, encId, groupId);
        if (vl == null) {
            vl = new ViralLoad();
            l.add(vl);
        }
        vl.setEncounterId(encId);
        vl.setGroupId(groupId);
        return vl;
    }

    protected ViralLoad getMatchingViralLoad(List<ViralLoad> viralLoads, Integer encId, Integer obsGroupId) {
	    for (ViralLoad vl : viralLoads) {
	        if (ObjectUtil.areEqual(vl.getGroupId(), obsGroupId)) {
	            return vl;
            }
            if (vl.getGroupId() == null && obsGroupId == null && ObjectUtil.areEqual(vl.getEncounterId(), encId)) {
                return vl;
            }
        }
        return null;
    }
}
