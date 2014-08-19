package org.openmrs.module.pihmalawi;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.test.context.ContextConfiguration;

import java.util.Properties;

@ContextConfiguration(locations = {"classpath:openmrs-servlet.xml"}, inheritLocations = true)
@SkipBaseSetup
public abstract class StandaloneContextSensitiveTest extends BaseModuleContextSensitiveTest {

	/**
	 * @return whether or not to run the test.  By default will only run if standalone
	 */
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public Boolean useInMemoryDatabase() {
		return !isEnabled();
	}

	/**
	 * @return MS Note: use port 3306 as standard, 5538 for sandbox 5.5 mysql environment
	 */
	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		if (isEnabled()) {
			p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_neno?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		}
		return p;
	}

	@Test
	public void runTest() throws Exception {
		if (isEnabled()) {
			if (!Context.isSessionOpen()) {
				Context.openSession();
			}
			Context.clearSession();
			authenticate();
			performTest();
		}
		else {
			System.out.println("TEST NOT ENABLED");
		}
	}

	protected abstract void performTest() throws Exception;

	@Override
	public void deleteAllData() throws Exception {
	}
}
