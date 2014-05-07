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

package org.openmrs.module.pihmalawi.reporting.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

/**
 * Tests the methods in the PatientDataFactory
 */
@Ignore
public class DataFactoryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	HivMetadata metadata;

	@Autowired
	PatientDataService patientDataService;

	@Autowired
	DataFactory pdf;

	@Test
	public void testStateNameAndDateFormatter() {
		PatientState state = Context.getProgramWorkflowService().getPatientState(21418);
		Object converted = pdf.getStateNameAndDateFormatter().convert(state);
		System.out.println(converted);
	}

	@Test
	public void testMostRecentObs() throws Exception {
		PatientDataDefinition pdd = pdf.getFirstObsByEndDate(metadata.getCd4CountConcept(), null, pdf.getObsValueNumericConverter());
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("endDate", DateUtil.getDateTime(2014, 1, 31));
		PatientData pd = patientDataService.evaluate(pdd, context);
		Assert.assertEquals(1, pd.getData().size());
		Assert.assertEquals(150.0, pd.getData().get(7));
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

	@Before
	public void setup() throws Exception {
		authenticate();
	}
}
