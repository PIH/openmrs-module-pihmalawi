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
import org.junit.Test;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the methods in the PatientDataFactory
 */
@SkipBaseSetup
public class PatientDataFactoryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	HivMetadata metadata;

	@Autowired
	PatientDataService patientDataService;

	@Autowired
	PatientDataFactory pdf;

	@Test
	public void testMostRecentObs() throws Exception {
		PatientDataDefinition pdd = pdf.getFirstObsByEndDate(metadata.getCd4CountConcept(), null, pdf.getObsValueNumericConverter());
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("endDate", DateUtil.getDateTime(2014, 1, 31));
		PatientData pd = patientDataService.evaluate(pdd, context);
		Assert.assertEquals(1, pd.getData().size());
		Assert.assertEquals(150, pd.getData().get(7));
	}
}
