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
import org.junit.Test;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.ReportInitializer;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Tests the methods in the PatientDataFactory
 */
public class ArtRegisterTest extends BaseModuleContextSensitiveTest {

	@Autowired
	ReportInitializer reportInitializer;

	@Autowired
	ArtRegister artRegister;

	@Autowired
	ReportDefinitionService reportDefinitionService;

	@Autowired
	ReportService reportService;

	@Autowired
	HivMetadata metadata;

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
		reportInitializer.setupReport(artRegister);
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_neno_19x?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		return p;
	}

	@Test
	public void shouldRunReportWithoutErrors() throws Exception {
		ReportDefinition reportDefinition = reportDefinitionService.getDefinitionByUuid(artRegister.getUuid());
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("location", metadata.getChifungaHc());
		context.addParameterValue("endDate", DateUtil.getDateTime(2014,2,1));
		ReportData data = reportDefinitionService.evaluate(reportDefinition, context);
		for (String dsName : data.getDataSets().keySet()) {
			System.out.println(dsName);
			System.out.println("--------------------");
			DataSetUtil.printDataSet(data.getDataSets().get(dsName), System.out);
			System.out.println("");
		}
		ReportDesign design = reportService.getReportDesignByUuid(ArtRegister.EXCEL_REPORT_DESIGN_UUID);
		ReportRenderer renderer = design.getRendererType().newInstance();
		FileOutputStream fos = new FileOutputStream("/home/mseaton/Desktop/Art_Register_New_Ligowe_2014-02-01.xls");
		renderer.render(data, design.getUuid(), fos);
		fos.close();
	}
}
