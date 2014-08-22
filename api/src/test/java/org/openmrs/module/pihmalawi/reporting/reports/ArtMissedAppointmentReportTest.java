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

import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MissedAppointmentDataSetDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the methods in the PatientDataFactory
 */
public class ArtMissedAppointmentReportTest extends ReportManagerTest {

	@Autowired
	HivMetadata metadata;

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	ArtMissedAppointmentReport artMissedAppointmentReport;

	@Override
	public ReportManager getReportManager() {
		return artMissedAppointmentReport;
	}

	@Override
	public EvaluationContext getEvaluationContext() {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("endDate", DateUtil.getDateTime(2014,6,8));
		context.addParameterValue("mode", MissedAppointmentDataSetDefinition.Mode.BETWEEN_2_AND_3_WEEKS_LATE);
		return context;
	}

	@Override
	protected boolean isEnabled() {
		return true;
	}

	@Override
	public boolean enableReportOutput() {
		return true;
	}
}
