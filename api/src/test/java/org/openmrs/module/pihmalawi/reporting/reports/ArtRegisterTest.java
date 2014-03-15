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

import org.junit.Ignore;
import org.openmrs.Cohort;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the methods in the PatientDataFactory
 */
@Ignore
public class ArtRegisterTest extends ReportManagerTest {

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	ArtRegister artRegister;

	@Override
	public ReportManager getReportManager() {
		return artRegister;
	}

	@Override
	public EvaluationContext getEvaluationContext() {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("59342,61822,20684,16351"));
		context.addParameterValue("endDate", DateUtil.getDateTime(2014,2,1));
		context.addParameterValue("location", null);
		return context;
	}
}
