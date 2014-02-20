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

package org.openmrs.module.pihmalawi.reporting.reports;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the methods in the PatientDataFactory
 */
@Ignore
public class ArtRegisterTest extends BaseModuleContextSensitiveTest {

	@Autowired
	ArtRegister artRegister;

	@Autowired
	ReportDefinitionService reportDefinitionService;

	@Autowired
	HivMetadata metadata;

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	@Test
	public void shouldRunReportWithoutErrors() throws Exception {
		ReportDefinition reportDefinition = artRegister.constructReportDefinition();
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("location", metadata.getChifungaHc());
		context.addParameterValue("endDate", DateUtil.getDateTime(2014,1,31));
		ReportData data = reportDefinitionService.evaluate(reportDefinition, context);
		for (String dsName : data.getDataSets().keySet()) {
			System.out.println(dsName);
			System.out.println("--------------------");
			DataSetUtil.printDataSet(data.getDataSets().get(dsName), System.out);
			System.out.println("");
		}
	}
}
