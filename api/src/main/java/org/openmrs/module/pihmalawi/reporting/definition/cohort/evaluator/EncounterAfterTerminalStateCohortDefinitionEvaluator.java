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
import org.openmrs.Encounter;
import org.openmrs.PatientState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.EncounterAfterTerminalStateCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Handler(supports = { EncounterAfterTerminalStateCohortDefinition.class })
public class EncounterAfterTerminalStateCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		EncounterAfterTerminalStateCohortDefinition cd = (EncounterAfterTerminalStateCohortDefinition) cohortDefinition;

		Cohort c = new Cohort();

		HqlQueryBuilder maxStateStart = new HqlQueryBuilder();
		maxStateStart.select("ps.patientProgram.patient.patientId", "ps.patientProgram.location.locationId", "max(ps.startDate)");
		maxStateStart.from(PatientState.class, "ps");
		maxStateStart.wherePatientIn("ps.patientProgram.patient.patientId", context);
		maxStateStart.whereEqual("ps.patientProgram.program", cd.getProgram());
		maxStateStart.whereIn("ps.patientProgram.location", cd.getLocations());
		maxStateStart.groupBy("ps.patientProgram.patient.patientId").groupBy("ps.patientProgram.location.locationId");

		Map<Integer, Map<Integer, Date>> maxStateDates = getPatientLocationDateMap(maxStateStart, context);

		HqlQueryBuilder maxTerminalStart = new HqlQueryBuilder();
		maxTerminalStart.select("ps.patientProgram.patient.patientId", "ps.patientProgram.location.locationId", "max(ps.startDate)");
		maxTerminalStart.from(PatientState.class, "ps");
		maxTerminalStart.wherePatientIn("ps.patientProgram.patient.patientId", context);
		maxTerminalStart.whereEqual("ps.patientProgram.program", cd.getProgram());
		maxTerminalStart.whereIn("ps.patientProgram.location", cd.getLocations());
		maxTerminalStart.whereEqual("ps.state.terminal", true);
		maxTerminalStart.groupBy("ps.patientProgram.patient.patientId").groupBy("ps.patientProgram.location.locationId");

		Map<Integer, Map<Integer, Date>>  maxTerminalStateDates = getPatientLocationDateMap(maxTerminalStart, context);

		HqlQueryBuilder encounterQuery = new HqlQueryBuilder();
		encounterQuery.select("e.patient.patientId", "e.location.locationId", "max(e.encounterDatetime)");
		encounterQuery.from(Encounter.class, "e");
		encounterQuery.wherePatientIn("e.patient.patientId", context);
		encounterQuery.whereEqual("e.patient.voided", false);
		encounterQuery.whereIn("e.encounterType", cd.getEncounterTypes());
		encounterQuery.whereIn("e.location", cd.getLocations());
		encounterQuery.groupBy("e.patient.patientId").groupBy("e.location.locationId");

		Map<Integer, Map<Integer, Date>> maxEncounterDates = getPatientLocationDateMap(encounterQuery, context);

		for (Integer pId : maxEncounterDates.keySet()) {
			Map<Integer, Date> encounterDateMap = maxEncounterDates.get(pId);
			Map<Integer, Date> maxStateDateMap = maxStateDates.get(pId);
			Map<Integer, Date> maxTerminalStateDateMap = maxTerminalStateDates.get(pId);

			for (Integer locationId : encounterDateMap.keySet()) {
				Date encounterDate = encounterDateMap.get(locationId);
				Date stateDate = (maxStateDateMap != null ? maxStateDateMap.get(locationId) : null);
				Date terminalDate = (maxTerminalStateDateMap != null ? maxTerminalStateDateMap.get(locationId) : null);
				if (terminalDate != null && encounterDate.after(terminalDate) && terminalDate.equals(stateDate)) {
					c.addMember(pId);
				}
			}
		}

		return new EvaluatedCohort(c, cd, context);
	}

	protected Map<Integer, Map<Integer, Date>> getPatientLocationDateMap(HqlQueryBuilder query, EvaluationContext context) {
		Map<Integer, Map<Integer, Date>> m = new HashMap<Integer, Map<Integer, Date>>();
		List<Object[]> queryResults = evaluationService.evaluateToList(query, context);
		for (Object[] row : queryResults) {
			Map<Integer, Date> locationMap = m.get(row[0]);
			if (locationMap == null) {
				locationMap = new HashMap<Integer, Date>();
				m.put((Integer)row[0], locationMap);
			}
			locationMap.put((Integer)row[1], (Date)row[2]);
		}
		return m;
	}
}