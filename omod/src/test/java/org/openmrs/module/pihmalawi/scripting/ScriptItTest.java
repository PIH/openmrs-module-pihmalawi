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
package org.openmrs.module.pihmalawi.scripting;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

@Ignore
public class ScriptItTest extends BaseModuleContextSensitiveTest {

	protected Log log = LogFactory.getLog(this.getClass());

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	@Test
	@Rollback(false)
	public void script() {
		PatientIdentifierType arvNumberType = Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number");
		String[] arvNumbers = { "NNO 6", "NNO 25", "NNO 33", "NNO 74",
				"NNO 99", "NNO 102", "NNO 120", "NNO 121", "NNO 134",
				"NNO 144", "NNO 151", "NNO 178", "NNO 186", "NNO 187",
				"NNO 189", "NNO 200", "NNO 204", "NNO 209", "NNO 220",
				"NNO 230", "NNO 241", "NNO 262", "NNO 279", "NNO 293",
				"NNO 309", "NNO 310", "NNO 369", "NNO 420", "NNO 422",
				"NNO 426", "NNO 427", "NNO 449", "NNO 492", "NNO 527",
				"NNO 540", "NNO 547", "NNO 560", "NNO 586", "NNO 592",
				"NNO 610", "NNO 612", "NNO 613", "NNO 614", "NNO 617",
				"NNO 618", "NNO 624", "NNO 630", "NNO 631", "NNO 659",
				"NNO 661", "NNO 700", "NNO 703", "NNO 711", "NNO 721",
				"NNO 722", "NNO 743", "NNO 744", "NNO 752", "NNO 765",
				"NNO 770", "NNO 773", "NNO 795", "NNO 861", "NNO 901",
				"NNO 921", "NNO 951", "NNO 970", "NNO 974", "NNO 1021",
				"NNO 1072", "NNO 1126", "NNO 1139", "NNO 1153", "NNO 1172",
				"NNO 1184", "NNO 1189", "NNO 1198", "NNO 1211", "NNO 1220",
				"NNO 1237", "NNO 1258", "NNO 1271", "NNO 1291", "NNO 1294",
				"NNO 1304", "NNO 1331", "NNO 1338", "NNO 1342", "NNO 1347",
				"NNO 1361", "NNO 1365", "NNO 1375", "NNO 1379", "NNO 1383",
				"NNO 1386", "NNO 1389", "NNO 1392", "NNO 1397", "NNO 1433",
				"NNO 1459", "NNO 1473", "NNO 1476", "NNO 1494", "NNO 1508",
				"NNO 1530", "NNO 1541", "NNO 1544", "NNO 1564", "NNO 1624",
				"NNO 1626", "NNO 1654", "NNO 1658", "NNO 1676", "NNO 1748",
				"NNO 1749", "NNO 1776", "NNO 1784", "NNO 1785", "NNO 1799",
				"NNO 1800", "NNO 1811", "NNO 1814", "NNO 1821", "NNO 1826",
				"NNO 1832", "NNO 1833", "NNO 1847", "NNO 1854", "NNO 1858",
				"NNO 1877", "NNO 1881", "NNO 1899", "NNO 1900", "NNO 1918",
				"NNO 1798", "NNO 1026", "NNO 1235", "NNO 1735", "NOP 41",
				"NNO 1855", "NNO 1215", "NNO 1221", "NNO 1100", "NNO 1165",
				"NNO 1857", "NNO 1944" };

		for (String number : arvNumbers) {
			List<Patient> patients = Context.getPatientService().getPatients(
					null, number, Arrays.asList(arvNumberType), true);
			if (patients.size() == 1) {
				doItForThePatient(patients.get(0));
			} else {
				log.warn("Patient not found or not unique, ignoring: " + number);
			}
		}
	}

	public void doItForThePatient(Patient p) {
		addStatePatientTransferedOutAfterEveryTransferInternallyState(p);
	}

	private void addStatePatientTransferedOutAfterEveryTransferInternallyState(
			Patient p) {
		Program program = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		ProgramWorkflow programWorkflow = program
				.getWorkflowByName("TREATMENT STATUS");
		ProgramWorkflowState internalTransfer = programWorkflow
				.getState("Transferred internally");
		ProgramWorkflowState transferOut = programWorkflow
				.getState("Patient transferred out");

		List<PatientProgram> patientPrograms = Context
				.getProgramWorkflowService().getPatientPrograms(p, program,
						null, null, null, null, false);
		for (PatientProgram pp : patientPrograms) {
			try {
				PatientState ps = pp.getCurrentState(programWorkflow);
				if (ps != null
						&& ps.getState() != null
						&& !ps.isVoided()
						&& !ps.getState().isRetired()
						&& ps.getState().getId()
								.equals(internalTransfer.getId())) {
					// found a program with last state internal transfer, add
					// transferred out
					log.warn("Adding transferred out to " + p.getId());
					pp.transitionToState(transferOut, ps.getStartDate());
					Context.getProgramWorkflowService().savePatientProgram(pp);

				}
			} catch (Throwable t) {
				log.error(t);
			}
		}
	}

}