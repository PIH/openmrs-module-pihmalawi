package org.openmrs.module.pihmalawi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.Activator;

public class ModuleActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());

	public void startup() {
		log.info("Starting pihmalawi Module");
		registerMalawiAddressTemplate();
	}

	public void shutdown() {
		log.info("Shutting down pihmalawi Module");
		unregisterMalawiAddressTemplate();
	}

	private void registerMalawiAddressTemplate() {
		AddressTemplate at = new AddressTemplate();
		at.setDisplayName("Malawi Address Format");
		at.setCodeName("malawi");
		at.setCountry("Malawi");

		/*
		 * Original values from openmrs-servlet.xml for Malawi
		 * 
		 * <prop key="stateProvince">Location.district</prop> <prop
		 * key="countyDistrict">Location.traditionalAuthority</prop> <prop
		 * key="cityVillage">Location.village</prop> <prop
		 * key="neighborhoodCell">Location.neighborhood</prop> <prop
		 * key="address1">Location.address1</prop>
		 */
		Map<String, String> nameMappings = new HashMap<String, String>();
		// nameMappings.put("country", "pihmalawi.address.country");
		nameMappings.put("countyDistrict", "pihmalawi.address.countyDistrict");
		nameMappings.put("stateProvince", "pihmalawi.address.stateProvince");
		nameMappings.put("cityVillage", "pihmalawi.address.cityVillage");
		// nameMappings.put("neighborhoodCell", "pihmalawi.address.neighborhoodCell");
		// nameMappings.put("address1", "pihmalawi.address.address1");
		// nameMappings.put("address2", "pihmalawi.address.address2");
		at.setNameMappings(nameMappings);

		Map<String, String> sizeMappings = new HashMap<String, String>();
		sizeMappings.put("countyDistrict", "40");
		sizeMappings.put("stateProvince", "40");
		sizeMappings.put("cityVillage", "40");
		at.setSizeMappings(sizeMappings);

		Map<String, String> elementDefaults = new HashMap<String, String>();
		elementDefaults.put("country", "Malawi");
		at.setElementDefaults(elementDefaults);

		at.setLineByLineFormat(Arrays.asList("cityVillage", "countyDistrict",
				"stateProvince"));

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
