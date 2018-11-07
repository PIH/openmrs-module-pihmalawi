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
import org.openmrs.module.pihmalawi.common.CodedValueAndDate;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.CodedValueAndDatePatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.BeanPropertyComparator;
import org.openmrs.module.reporting.common.ObjectUtil;
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
import java.util.Iterator;
import java.util.List;

/**
 * Evaluates a CodedValueAndDatePatientDataDefinition to produce a PatientData
 */
@Handler(supports = CodedValueAndDatePatientDataDefinition.class, order = 50)
public class CodedValueAndDatePatientDataEvaluator implements PatientDataEvaluator {

    protected static final Log log = LogFactory.getLog(CodedValueAndDatePatientDataEvaluator.class);

	@Autowired
	private DataFactory df;

	@Autowired
	private PatientDataService patientDataService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        CodedValueAndDatePatientDataDefinition def = (CodedValueAndDatePatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		boolean matchEncOnly = def.getMatchEncounterOnly() != null && def.getMatchEncounterOnly().booleanValue();

		EvaluationContext childContext = context.shallowCopy();
		if (def.getEndDate() != null) {
		    // Remove end date parameter if passed in, as we need to do the date restriction in code below, due to date obs
		    childContext.getParameterValues().remove(ReportingConstants.END_DATE_PARAMETER.getName());
        }

        PatientDataDefinition codedValDef =  df.getAllObsByEndDate(def.getCodedValueQuestion(), null, null);
        PatientDataDefinition dateValDefs =  df.getAllObsByEndDate(def.getDateValueQuestion(), null, null);

        PatientData codedValData = patientDataService.evaluate(codedValDef, childContext);
        PatientData dateValData = patientDataService.evaluate(dateValDefs, childContext);

        for (Integer pId : codedValData.getData().keySet()) {
            List<CodedValueAndDate> valsForPatient = new ArrayList<CodedValueAndDate>();
            List<Obs> codedVals = (List<Obs>)codedValData.getData().get(pId);
            for (Obs codedVal : codedVals) {
                valsForPatient.add(new CodedValueAndDate(codedVal));
            }
            List<Obs> dateVals = (List<Obs>)dateValData.getData().get(pId);
            if (dateVals != null) {
                for (Obs dateVal : dateVals) {
                    CodedValueAndDate existing = getMatch(valsForPatient, matchEncOnly, dateVal);
                    if (existing == null) {
                        log.warn("Found date obs " + def.getDateValueQuestion() + " where not value obs exists. Obs ID: " + dateVal.getId());
                    }
                    else {
                        existing.setDateObs(dateVal);
                    }
                }
            }
            // Remove any needed if a date constraint passed in
            if (def.getEndDate() != null) {
                for (Iterator<CodedValueAndDate> i = valsForPatient.iterator(); i.hasNext();) {
                    CodedValueAndDate v = i.next();
                    if (v.getDate().after(def.getEndDate())) {
                        i.remove();
                    }
                }
            }
            // Sort by date ascending
            Collections.sort(valsForPatient, new BeanPropertyComparator("date asc"));
            c.getData().put(pId, valsForPatient);
        }

		return c;
	}

	protected CodedValueAndDate getMatch(List<CodedValueAndDate> valsForPatient, boolean matchEncOnly, Obs dateObs) {
        for (CodedValueAndDate v : valsForPatient) {
            boolean encountersMatch = ObjectUtil.areEqual(v.getValueObs().getEncounter(), dateObs.getEncounter());
            if (encountersMatch) {
                boolean groupsMatch = matchEncOnly || ObjectUtil.areEqual(v.getValueObs().getObsGroup(), dateObs.getObsGroup());
                if (groupsMatch) {
                    return v;
                }
            }
        }
        return null;
    }
}
