package org.openmrs.module.pihmalawi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;

public class ModuleActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());

	public void startup() {
		log.info("Starting pihmalawi Module");
	}
	
	public void shutdown() {
		log.info("Shutting down pihmalawi Module");
	}
}
