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
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.RelativeDateCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Handler(supports = { RelativeDateCohortDefinition.class })
public class RelativeDateCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	PatientDataService patientDataService;

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		RelativeDateCohortDefinition cd = (RelativeDateCohortDefinition) cohortDefinition;

		Cohort c = new Cohort();

		PatientData data1 = patientDataService.evaluate(cd.getEarlierDateDefinition(), context);
		PatientData data2 = patientDataService.evaluate(cd.getLaterDateDefinition(), context);

		Set<Integer> allPats = new HashSet<Integer>(data1.getData().keySet());
		allPats.addAll(data2.getData().keySet());

		RangeComparator operator = cd.getDifferenceOperator();

		for (Integer pId : allPats) {
			Date d1 = (Date) data1.getData().get(pId);
			Date d2 = (Date) data2.getData().get(pId);

			boolean includePatient = false;
			if (d1 == null) {
				if (cd.isPassIfDate1Null()) {
					includePatient = true;
				}
			}
			else if (d2 == null) {
				if (cd.isPassIfDate2Null()) {
					includePatient = true;
				}
			}
			else {
				Integer diffNum = cd.getDifferenceNumber();
				DurationUnit diffUnit = cd.getDifferenceUnit();
				if (diffNum == null) {
					int comparison = d1.compareTo(d2);
					if (comparison < 0) {
						if (operator == RangeComparator.LESS_THAN || operator == RangeComparator.LESS_EQUAL) {
							includePatient = true;
						}
					}
					else if (comparison > 0) {
						if (operator == RangeComparator.GREATER_THAN || operator == RangeComparator.GREATER_EQUAL) {
							includePatient = true;
						}
					}
					else {
						if (operator == RangeComparator.EQUAL || operator == RangeComparator.LESS_EQUAL || operator == RangeComparator.GREATER_EQUAL) {
							includePatient = true;
						}
					}
				}
				else {
					Date adjustedD1 = DateUtil.adjustDate(d1, diffNum, diffUnit);
					int comparison = adjustedD1.compareTo(d2);
					if (comparison < 0) { // This means that more than the configured units has gone by
						if (operator == RangeComparator.GREATER_THAN || operator == RangeComparator.GREATER_EQUAL) {
							includePatient = true;
						}
					}
					else if (comparison > 0) {
						if (operator == RangeComparator.LESS_THAN || operator == RangeComparator.LESS_EQUAL) {
							includePatient = true;
						}
					}
					else {
						if (operator == RangeComparator.EQUAL || operator == RangeComparator.LESS_EQUAL || operator == RangeComparator.GREATER_EQUAL) {
							includePatient = true;
						}
					}
				}
			}

			if (includePatient) {
				c.addMember(pId);
			}
		}

		return new EvaluatedCohort(c, cd, context);
	}
}