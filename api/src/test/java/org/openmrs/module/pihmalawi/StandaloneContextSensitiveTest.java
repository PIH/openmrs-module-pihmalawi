package org.openmrs.module.pihmalawi;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.util.Properties;

@SkipBaseSetup
public abstract class StandaloneContextSensitiveTest extends BaseModuleContextSensitiveTest {

	static {
		System.setProperty("databaseUrl", "jdbc:mysql://localhost:3308/neno?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
		System.setProperty("databaseUsername", "root");
		System.setProperty("databasePassword", "root");
		System.setProperty("databaseDriver", "com.mysql.jdbc.Driver");
		System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
		System.setProperty("useInMemoryDatabase", "false");
	}

	protected boolean isEnabled() {
		return false;
	}

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty("junit.username", "admin");
		p.setProperty("junit.password", "Admin123");
		return p;
	}

	@Test
	public void runTest() throws Exception {
		if (isEnabled() ) {
			authenticate();
			performTest();
		}
		else {
			System.out.println("TEST NOT ENABLED");
		}
	}

	protected abstract void performTest() throws Exception;

	@Override
	public void deleteAllData() {
	}
}
