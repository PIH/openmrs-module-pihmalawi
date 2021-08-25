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

package org.openmrs.module.pihmalawi.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.layout.address.AddressSupport;
import org.openmrs.layout.address.AddressTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Installs and removes the address template for pihmalawi
 */
public class AddressTemplateInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(AddressTemplateInitializer.class);

	public static final String CODE_NAME = "malawi";

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Registering address template for Malawi");

		AddressTemplate at = AddressSupport.getInstance().getLayoutTemplateByCodeName(CODE_NAME);

		if (at == null) {
			at = new AddressTemplate(CODE_NAME);
			AddressSupport.getInstance().getLayoutTemplates().add(at);
		}

		at.setDisplayName("Malawi Address Format");
		at.setCodeName(CODE_NAME);
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
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
		log.info("Un-registering address template for Malawi");
		AddressTemplate at = AddressSupport.getInstance().getLayoutTemplateByCodeName(CODE_NAME);
		if (at != null) {
			AddressSupport.getInstance().getLayoutTemplates().remove(at);
		}
	}
}
