package org.openmrs.module.pihmalawi.reporting.definition;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.setup.SetupHccMissedAppointment;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class HccMissedAppointmentPersistentSetup extends BaseModuleContextSensitiveTest {
	
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
	public void setupHivWeekly() throws Exception {
		new SetupHccMissedAppointment(new ReportHelper(), true).setup();
	}
}
