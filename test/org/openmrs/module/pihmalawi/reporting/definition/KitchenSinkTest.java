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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.ApzuKsPatientDataSetEvaluator;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupChronicCareRegister;
import org.openmrs.module.pihmalawi.reporting.duplicateSpotter.DuplicatePatientsSpotter;
import org.openmrs.module.pihmalawi.reporting.duplicateSpotter.SetupDuplicateHivPatients;
import org.openmrs.module.pihmalawi.reporting.duplicateSpotter.SoundexMatcher;
import org.openmrs.module.pihmalawi.reporting.extension.AppointmentAdherenceCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Supports rendering a series of Cohorts with particular datasets
 */
public class KitchenSinkTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	//@Test
	public void appointAdherence() throws Exception {
		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
			    HibernatePihMalawiQueryDao.class).get(0);
			AppointmentAdherenceCohortDefinition d = new AppointmentAdherenceCohortDefinition();
			d.setEncounterTypes(Arrays.asList(Context.getEncounterService().getEncounterType("ART_INITIAL"), Context.getEncounterService().getEncounterType("ART_FOLLOWUP")));
			d.setAppointmentConcept(Context.getConceptService().getConceptByName("APPOINTMENT DATE"));
			d.setFromDate(new Date("2011/01/01"));
			d.setToDate(new Date("2011/03/31"));
			d.setMinimumAdherence(-1);
			d.setMaximumAdherence(100);
			
			Cohort c = q.getPatientsAppointmentAdherence(d.getEncounterTypes(), d.getAppointmentConcept(), d.getFromDate(), d.getToDate(), d.getMinimumAdherence(), d.getMaximumAdherence());
			System.out.println(c);
	}
	
//	@Test
	public void executeCohortDetailRendererReport() throws Exception {
		ReportDefinition rds[] = new SetupChronicCareRegister(new Helper()).setup();
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", Context.getLocationService()
				.getLocation(2));
		List<ReportDesign> designs = Context.getService(ReportService.class).getReportDesigns(rds[0], CohortDetailReportRenderer.class, false);
		executeReportHtml(context, rds[0], designs.get(0), "/tmp/by_user", "html");
	}

	private final static int WEEKS_MISSED_bUT_STILL_CONSIDERED_IN_CARE = 3;
	private final static long MILLISECONDS_PER_WEEK = (long) 7 * 24 * 60 * 60
	* 1000;

	private long weeksDifference(Date from, Date to) {
		return (to.getTime() - from.getTime()) / MILLISECONDS_PER_WEEK;
	}

	private Date addWeeks(Date date, int weeks) {
		return new Date(date.getTime() + weeks * MILLISECONDS_PER_WEEK);
	}


	@Test
	public void nix() {
		long i = weeksDifference(new Date(), addWeeks(new Date(), 3));
		System.out.println(i);
		
	}
	protected Object calcObsValue(Obs o, Date endDate) {
		return bmi(o.getValueNumeric(), mostRecentHeight(o.getPerson(), endDate));
	}

	private double bmi(double weightInKG, double heightInCM) {
		if (weightInKG == 0 || heightInCM == 0) 
			return 0;
		return (weightInKG / ((heightInCM/100) * (heightInCM/100)));
	}
	
	private double mostRecentHeight(Person p, Date endDate) {
		final Concept HEIGHT = Context.getConceptService().getConcept(5090);
		List<Obs> obses = Context.getObsService().getObservations(
				Arrays.asList(p), null,
				Arrays.asList(HEIGHT), null, null, null, null, 1,
				null, null, endDate, false);
		if (obses.isEmpty()) {
			return 0;
		} else {
			return obses.get(0).getValueNumeric();
		}
	}

//	 @Test
	public void run() throws Exception {

		// ReportDefinition rds[] = new SetupSurvivalAnalysis(new
		// Helper()).setup();
		// EvaluationContext context = new EvaluationContext();
		// context.addParameterValue("startDate", new Date());
		// context.addParameterValue("endDate", new Date());
		// context.addParameterValue("location",
		// Context.getLocationService().getLocation(2));
		// executeReport(context, rds[0], null, "/tmp/by_user", "xls");

		// Program hiv = Context.getProgramWorkflowService().getProgramByName(
		// "HIV PROGRAM");
		// ProgramWorkflowState died = hiv.getWorkflowByName("TREATMENT STATUS")
		// .getStateByName("PATIENT DIED");
		// ProgramWorkflowState stopped = hiv
		// .getWorkflowByName("TREATMENT STATUS").getStateByName(
		// "TREATMENT STOPPED");
		// ProgramWorkflowState transferredOut = hiv.getWorkflowByName(
		// "TREATMENT STATUS").getStateByName("PATIENT TRANSFERRED OUT");
		//
		// Helper h = new Helper();
		// EncounterAfterProgramStateEvaluator e = new
		// EncounterAfterProgramStateEvaluator();
		// EncounterAfterProgramStateCohortDefinition d = new
		// EncounterAfterProgramStateCohortDefinition();
		// d.setOnDate(new Date());
		// d.setClinicLocations(Arrays.asList(h.location("Magaleta HC")));
		// d.setEnrollmentLocation(h.location("Magaleta HC"));
		// d.setEncounterTypesAfterChangeToTerminalState(null);
		// d.setTerminalStates(Arrays.asList(died, stopped, transferredOut));
		// Cohort c = e.evaluate(d, null);
		// System.out.println(c);

		Helper h = new Helper();
		Location touchscreenNno = h
				.location("_to_be_voided_Neno District Hospital - ART Clinic (NNO)");
		Location nno = h.location("Neno District Hospital");
		List<Location> locations = Arrays.asList(nno);
		List<EncounterType> encounterTypes = Arrays
				.asList(h.encounterType("ART_INITIAL"),
						h.encounterType("ART_FOLLOWUP"),
						h.encounterType("PART_INITIAL"),
						h.encounterType("PART_FOLLOWUP"),
						h.encounterType("EID_INITIAL"),
						h.encounterType("EID_FOLLOWUP"));
		List<EncounterType> touchscreenEncounterTypes = Arrays.asList(h
				.encounterType("REGISTRATION"));

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -6);
		Date SIX_MONTHS_AGO = c.getTime();

		ReportDefinition rds = new SetupDuplicateHivPatients(new Helper())
				.setup();
		ReportService r = Context.getService(ReportService.class);
		ReportDefinitionService rs = Context
				.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(rds, new EvaluationContext());

		DuplicatePatientsSpotter s = new DuplicatePatientsSpotter();
		SoundexMatcher sm = new SoundexMatcher();

		Iterator<DataSetRow> i = data.getDataSets().get("defaultDataSet")
				.iterator();
		if (i.hasNext()) {
			DataSetRow dsr = i.next();
			CohortIndicatorAndDimensionResult coadr = (CohortIndicatorAndDimensionResult) dsr
					.getColumnValue("entryHIV");
			Set<Integer> ids = coadr.getCohortIndicatorResult().getCohort()
					.getMemberIds();
			// Set<Integer> ids = new TreeSet();
			// ids.add(55073);
			// HibernateCohortQueryDAO dao = (HibernateCohortQueryDAO) Context
			// .getRegisteredComponents(HibernateCohortQueryDAO.class)
			// .get(0);
			// Set<Integer> patientIds = dao.getPatientsHavingEncounters(
			// SIX_MONTHS_AGO, null, locations, encounterTypes, null, null,
			// null).getMemberIds();
			for (Integer id : ids) {

				Patient p = Context.getPatientService().getPatient(id);
				// List<Patient> ps = s.spot(p, SIX_MONTHS_AGO, patientIds);
				Collection<Patient> ps = sm.soundexMatches(p, encounterTypes,
						false);
				String m = p.getId() + ";";
				for (Patient potential : ps) {
					m += potential.getId() + ";";
				}
				// todo, fill in the patient merge dialog for every possible
				// match
				System.out.println(m);
			}
		}

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

	public void executeReportHtml(EvaluationContext context,
			ReportDefinition report, final ReportDesign design,
			String filename, String extension) throws Exception {

		ReportService r = Context.getService(ReportService.class);
		ReportRenderer renderer = r
				.getReportRenderer(CohortDetailReportRenderer.class.getName());

		ReportDefinitionService rs = Context
				.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);

		FileOutputStream fos = new FileOutputStream(filename);
		renderer.render(data, design.getUuid() + ":"
				+ extension, fos);
		fos.close();
	}

}