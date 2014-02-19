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
package org.openmrs.module.pihmalawi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.pihmalawi.reporting.ReportInitializer;

public class ModuleActivator extends BaseModuleActivator {

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void started() {
		log.info("pihmalawi module started");
		registerMalawiAddressTemplate();
		ReportInitializer reportInitializer = Context.getRegisteredComponents(ReportInitializer.class).get(0);
		reportInitializer.setupReports();
	}

	@Override
	public void stopped() {
		log.info("pihmalawi module stopped");
		unregisterMalawiAddressTemplate();
	}

	private void registerMalawiAddressTemplate() {

		log.warn("Hard-coded configuration of address template for Malawi");
		AddressTemplate at = new AddressTemplate("pihmalawi");
		at.setDisplayName("Malawi Address Format");
		at.setCodeName("malawi");
		at.setCountry("Malawi");

		Map<String, String> nameMappings = new HashMap<String, String>();
		nameMappings.put("countyDistrict", "pihmalawi.address.countyDistrict");
		nameMappings.put("stateProvince", "pihmalawi.address.stateProvince");
		nameMappings.put("cityVillage", "pihmalawi.address.cityVillage");
		at.setNameMappings(nameMappings);

		Map<String, String> sizeMappings = new HashMap<String, String>();
		sizeMappings.put("countyDistrict", "40");
		sizeMappings.put("stateProvince", "40");
		sizeMappings.put("cityVillage", "40");
		at.setSizeMappings(sizeMappings);

		Map<String, String> elementDefaults = new HashMap<String, String>();
		elementDefaults.put("country", "Malawi");
		at.setElementDefaults(elementDefaults);

		at.setLineByLineFormat(Arrays.asList("cityVillage", "countyDistrict", "stateProvince"));

		AddressSupport.getInstance().getLayoutTemplates().add(at);
	}

	private void unregisterMalawiAddressTemplate() {
		for (Iterator<AddressTemplate> i = AddressSupport.getInstance().getLayoutTemplates().iterator(); i.hasNext();) {
			AddressTemplate at = i.next();
			if ("malawi".equals(at.getCodeName())) {
				i.remove();
			}
		}
	}
}
