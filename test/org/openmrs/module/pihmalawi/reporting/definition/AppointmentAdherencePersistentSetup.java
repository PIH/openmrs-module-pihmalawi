package org.openmrs.module.pihmalawi.reporting.definition;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupAppointmentAdherence;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class AppointmentAdherencePersistentSetup extends
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
	@Rollback(false)
	public void setupReport() throws Exception {
		new SetupAppointmentAdherence(new Helper(), "adcc", "CC", null, Arrays.asList(Context
				.getEncounterService().getEncounterType("CHRONIC_CARE_FOLLOWUP")))
				.setup();
		new SetupAppointmentAdherence(new Helper(), "adart", "ART", Context
				.getProgramWorkflowService().getProgramByName("HIV PROGRAM")
				.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("ON ANTIRETROVIRALS"), Arrays.asList(Context
				.getEncounterService().getEncounterType("ART_INITIAL"), Context
				.getEncounterService().getEncounterType("ART_FOLLOWUP")))
				.setup();

	}
}
