package org.openmrs.module.pihmalawi.reporting.definition;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupWeeklyEncounter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class WeeklyEncounterPersistentSetup extends BaseModuleContextSensitiveTest {
	
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
			new SetupWeeklyEncounter(new Helper()).setup(false);
	}
	
}
