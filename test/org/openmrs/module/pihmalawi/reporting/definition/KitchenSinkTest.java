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
package org.openmrs.module.pihmalawi.reporting.definition;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.EncounterAfterProgramStateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.EncounterAfterProgramStateEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Supports rendering a series of Cohorts with particular datasets
 */
public class KitchenSinkTest extends
		BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	@Test
	public void run() throws Exception {
		// ReportDefinition rds[] = new SetupWeeklyEncounter(new
		// Helper()).setup(false);
		// EvaluationContext context = new EvaluationContext();
		// context.addParameterValue("startDate", new Date());
		// context.addParameterValue("endDate", new Date());
		// context.addParameterValue("location",
		// Context.getLocationService().getLocation(2));
		// executeReport(context, rds[0], null, "/tmp/by_user", "xls");

		// ReportDefinition rds[] = new SetupSurvivalAnalysis(new
		// Helper()).setup();
		// EvaluationContext context = new EvaluationContext();
		// context.addParameterValue("startDate", new Date());
		// context.addParameterValue("endDate", new Date());
		// context.addParameterValue("location",
		// Context.getLocationService().getLocation(2));
		// executeReport(context, rds[0], null, "/tmp/by_user", "xls");

		Program hiv = Context.getProgramWorkflowService().getProgramByName(
				"HIV PROGRAM");
		ProgramWorkflowState died = hiv.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("PATIENT DIED");
		ProgramWorkflowState stopped = hiv
				.getWorkflowByName("TREATMENT STATUS").getStateByName(
						"TREATMENT STOPPED");
		ProgramWorkflowState transferredOut = hiv.getWorkflowByName(
				"TREATMENT STATUS").getStateByName("PATIENT TRANSFERRED OUT");

		Helper h = new Helper();
		EncounterAfterProgramStateEvaluator e = new EncounterAfterProgramStateEvaluator();
		EncounterAfterProgramStateCohortDefinition d = new EncounterAfterProgramStateCohortDefinition();
		d.setOnDate(new Date());
		d.setClinicLocations(Arrays.asList(h.location("Magaleta HC")));
		d.setEnrollmentLocation(h.location("Magaleta HC"));
		d.setEncounterTypesAfterChangeToTerminalState(null);
		d.setTerminalStates(Arrays.asList(died, stopped, transferredOut));
		Cohort c = e.evaluate(d, null);
		System.out.println(c);
	}

	public void executeReport(EvaluationContext context,
			ReportDefinition report, final ReportDesign design,
			String filename, String extension) throws Exception {

		ReportService r = Context.getService(ReportService.class);
		;
		ReportRenderer renderer = r.getReportRenderer(XlsReportRenderer.class
				.getName());

		ReportDefinitionService rs = Context
				.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);

		// We demonstrate here how we can use this renderer to output to HTML
		FileOutputStream fos = new FileOutputStream(filename); // You will need
																// to change
																// this if you
																// have no /tmp
																// directory
		renderer.render(data, "xxx:" + extension, fos);
		fos.close();

		// We demonstrate here how we can use this renderer to output to Excel
		fos = new FileOutputStream("/tmp/test.xls"); // You will need to change
														// this if you have no
														// /tmp directory
		renderer.render(data, "xxx:" + extension, fos);
		fos.close();
	}

}