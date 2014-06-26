/*
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

package org.openmrs.module.pihmalawi.reporting;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Properties;

/**
 * Tests the methods in the PatientDataFactory
 */
public class PerformanceTest extends BaseModuleContextSensitiveTest {

	@Autowired
	EvaluationService evaluationService;

	@Autowired
	EncounterQueryService encounterQueryService;

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Test
	public void testPerformance() throws Exception {
		if (getLoadCount() == 1) {
			authenticate();
			HqlQueryBuilder encounterQuery = new HqlQueryBuilder();
			encounterQuery.select("e.encounterId", "e.patient.patientId");
			encounterQuery.from(Encounter.class, "e");
			//encounterQuery.whereLessOrEqualTo("e.encounterId", 10000);

			EncounterEvaluationContext eec = new EncounterEvaluationContext();
			eec.setBaseEncounters(new EncounterIdSet());
			eec.setBaseCohort(new Cohort());

			List<Object[]> l = evaluationService.evaluateToList(encounterQuery, eec);
			for (Object[] row : l) {
				eec.getBaseEncounters().add((Integer) row[0]);
				eec.getBaseCohort().addMember((Integer) row[1]);
			}

			System.out.println("Running a query on " + eec.getBaseEncounters().getSize() + " encounters; " + eec.getBaseCohort().size() + " patients");

			HqlQueryBuilder q = new HqlQueryBuilder();
			q.select("e.encounterDatetime");
			q.from(Encounter.class, "e");
			q.whereEncounterIn("e.encounterId", eec);

			System.out.println("Starting evaluation...");
			StopWatch sw = new StopWatch();
			sw.start();
			evaluationService.evaluateToList(q, eec);
			sw.stop();
			System.out.println("Evaluated in: " + sw.toString());
		}
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_neno?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		return p;
	}

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
