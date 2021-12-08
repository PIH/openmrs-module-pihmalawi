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
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

/**
 * Tests the TraceReport
 */
@Ignore
public class IC3MonthlyIndicatorReportTest extends ReportManagerTest {

	@Autowired
	HivMetadata metadata;

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
    IC3MonthlyIndicatorReport report;

	@Override
	public ReportManager getReportManager() {
		return report;
	}

    @Override
    public void performTest() throws Exception {
        String url = "jdbc:mysql://localhost:3308/openmrs_warehouse?autoReconnect=true&sessionVariables=storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String password = "root";

        File propertiesFile = new File(OpenmrsUtil.getApplicationDataDirectory(), PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
        Properties properties = new Properties();
        properties.put("connection.url", url);
        properties.put("connection.username", user);
        properties.put("connection.password", password);
        properties.store(new FileOutputStream(propertiesFile), null);

        super.performTest();
    }

    @Override
	public EvaluationContext getEvaluationContext() {
		EvaluationContext context = new EvaluationContext();
		Date today = new Date();
		context.addParameterValue("startDate", DateUtil.getStartOfMonth(today));
        context.addParameterValue("endDate", DateUtil.getEndOfMonth(today));
		return context;
	}

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public boolean enableReportOutput() {
		return true;
	}
}
