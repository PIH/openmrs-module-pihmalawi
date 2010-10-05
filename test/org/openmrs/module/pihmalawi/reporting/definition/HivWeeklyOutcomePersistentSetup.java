package org.openmrs.module.pihmalawi.reporting.definition;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupHivWeeklyOutcome;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class HivWeeklyOutcomePersistentSetup extends BaseModuleContextSensitiveTest {
	
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
			new SetupHivWeeklyOutcome(new Helper()).setupHivWeekly(false);
	}
	
}
