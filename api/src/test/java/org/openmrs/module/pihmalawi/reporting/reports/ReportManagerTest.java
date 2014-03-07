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

import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.ReportInitializer;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Tests the methods in the PatientDataFactory
 */
public abstract class ReportManagerTest extends BaseModuleContextSensitiveTest {

	public abstract ReportManager getReportManager();

	public abstract EvaluationContext getEvaluationContext();

	@Autowired
	ReportInitializer reportInitializer;

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
		reportInitializer.setupReport(getReportManager());
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_neno_nov_19x?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		return p;
	}

	@Test
	public void shouldRunReportWithoutErrors() throws Exception {
		ReportManager rm = getReportManager();
		ReportDefinition rd = reportDefinitionService.getDefinitionByUuid(rm.getUuid());
		Assert.assertEquals(rm.getName(), rd.getName());
		Assert.assertEquals(rm.getDescription(), rd.getDescription());
		for (ReportDesign design : rm.constructReportDesigns(rd)) {
			ReportDesign dbDesign = reportService.getReportDesignByUuid(design.getUuid());
			Assert.assertEquals(design.getName(), dbDesign.getName());
			Assert.assertEquals(design.getRendererType(), dbDesign.getRendererType());
			Assert.assertEquals(design.getResources().size(), dbDesign.getResources().size());
		}

		EvaluationContext context = getEvaluationContext();
		ReportData data = reportDefinitionService.evaluate(rd, context);
		Assert.assertTrue(data.getDataSets().size() > 0);
		for (RenderingMode renderingMode : reportService.getRenderingModes(rd)) {
			ReportRenderer renderer = renderingMode.getRenderer();
			if (!(renderer instanceof WebReportRenderer)) {
				String argument = renderingMode.getArgument();
				File outFile = new File(SystemUtils.getJavaIoTmpDir(), renderer.getFilename(rd, argument));
				FileOutputStream fos = new FileOutputStream(outFile);
				renderer.render(data, argument, fos);
				fos.close();
			}
		}
		if (enableReportOutput()) {
			for (String dsName : data.getDataSets().keySet()) {
				System.out.println(dsName);
				System.out.println("---------------------------------");
				DataSetUtil.printDataSet(data.getDataSets().get(dsName), System.out);
			}
		}
	}

	/**
	 * @return true if a subclass wants to print out the report contents to System.out
	 */
	public boolean enableReportOutput() {
		return false;
	}
}
