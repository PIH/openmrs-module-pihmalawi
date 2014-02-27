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
package org.openmrs.module.pihmalawi.reporting.data.evaluator;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.data.definition.ProgramPatientIdentifierDataDefinition;
import org.openmrs.module.reporting.common.BeanPropertyComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a ProgramPatientIdentifierDataDefinition to produce a PatientData
 */
@Handler(supports=ProgramPatientIdentifierDataDefinition.class, order=50)
public class ProgramPatientIdentifierDataEvaluator implements PatientDataEvaluator {

	@Autowired
	PatientDataService patientDataService;

	/**
	 * @see org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		ProgramPatientIdentifierDataDefinition def = (ProgramPatientIdentifierDataDefinition) definition;

		// If location is passed in, just return the preferred identifier at that location
		if (def.getLocation() != null) {
			PreferredIdentifierDataDefinition preferredIdentifierDef = new PreferredIdentifierDataDefinition();
			preferredIdentifierDef.setIdentifierType(def.getIdentifierType());
			preferredIdentifierDef.setLocation(def.getLocation());
			return patientDataService.evaluate(preferredIdentifierDef, context);
		}

		// Otherwise, get the location from where the patient was most recently enrolled in the configured program

		if (def.getLocation() == null) {
			ProgramEnrollmentsForPatientDataDefinition programDef = new ProgramEnrollmentsForPatientDataDefinition();
			programDef.setWhichEnrollment(TimeQualifier.LAST);
			programDef.setProgram(def.getProgram());
			EvaluatedPatientData programData = patientDataService.evaluate(programDef, context);

			PatientIdentifierDataDefinition identifierDef = new PatientIdentifierDataDefinition();
			identifierDef.setTypes(Arrays.asList(def.getIdentifierType()));
			EvaluatedPatientData identifierData = patientDataService.evaluate(identifierDef, context);

			EvaluatedPatientData ret = new EvaluatedPatientData(def, context);

			for (Integer pId : identifierData.getData().keySet()) {
				PatientProgram pp = (PatientProgram)programData.getData().get(pId);
				List<PatientIdentifier> matches = new ArrayList<PatientIdentifier>();
				if (pp != null) {
					if (pp.getLocation() != null) {
						List<PatientIdentifier> idsForPatient = (List<PatientIdentifier>)identifierData.getData().get(pId);
						if (idsForPatient != null && !idsForPatient.isEmpty()) {
							for (PatientIdentifier pi : idsForPatient) {
								if (pp.getLocation().equals(pi.getLocation())) {
									matches.add(pi);
								}
							}
						}
					}
				}
				if (!matches.isEmpty()) {
					Collections.sort(matches, new BeanPropertyComparator("preferred desc,dateCreated desc"));
					ret.getData().put(pId, matches.get(0));
				}
			}

			return ret;
		}





		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if ((context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) || def.getIdentifierType() == null) {
			return c;
		}
		
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("select		pi.patient.patientId, pi ");
		hql.append("from		PatientIdentifier as pi ");
		hql.append("where 		voided = false ");
		hql.append("and 		pi.identifierType = :idType ");
		m.put("idType", def.getIdentifierType());

		if (context.getBaseCohort() != null) {
			hql.append("and 	pi.patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}

		if (def.getLocation() != null) {
			hql.append("and 	pi.location = :location) ");
			m.put("location", def.getLocation());
		}

		// Order to ensure that the preferred is based on the preferred flag first, dateCreated second
		hql.append("order by 	pi.preferred asc, pi.dateCreated asc");

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);
		for (Object o : queryResult) {
			Object[] parts = (Object[]) o;
			c.addData((Integer)parts[0], parts[1]);
		}
		return c;
	}
}
