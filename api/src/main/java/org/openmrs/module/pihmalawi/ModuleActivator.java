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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.pihmalawi.reporting.reports.ApzuReportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class ModuleActivator extends BaseModuleActivator {

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void started() {
		log.info("pihmalawi module started");
		registerMalawiAddressTemplate();
		installConcepts();
		removeOldReports();
		ReportManagerUtil.setupAllReports(ApzuReportManager.class);
		ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
	}

	@Override
	public void stopped() {
		log.info("pihmalawi module stopped");
		unregisterMalawiAddressTemplate();
	}

	private void installConcepts() {
		ConceptService cs = Context.getConceptService();
		{
			Integer id = 8417;
			String uuid = "521f8e75-4113-4870-bcbb-9ec1d727c627";
			String name = "Chronic Care Medication Set";
			Concept c = cs.getConceptByUuid(uuid);
			if (c == null) {
				log.warn("Creating Chronic Care Medication Set");
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("MedSet"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				c.setSet(true);
				c.addSetMember(cs.getConceptByName("Salbutamol"));
				c.addSetMember(cs.getConceptByName("Beclomethasone"));
				c.addSetMember(cs.getConceptByName("Hydrochlorothiazide"));
				c.addSetMember(cs.getConceptByName("Captopril"));
				c.addSetMember(cs.getConceptByName("Amlodipine"));
				c.addSetMember(cs.getConceptByName("Enalapril"));
				c.addSetMember(cs.getConceptByName("Nifedipine"));
				c.addSetMember(cs.getConceptByName("Atenolol"));
				c.addSetMember(cs.getConceptByName("Lisinopril"));
				c.addSetMember(cs.getConceptByName("Propranolol"));
				c.addSetMember(cs.getConceptByName("Phenobarbital"));
				c.addSetMember(cs.getConceptByName("Phenytoin"));
				c.addSetMember(cs.getConceptByName("Carbamazepine"));
				c.addSetMember(cs.getConceptByName("Insulin"));
				c.addSetMember(cs.getConceptByName("Metformin"));
				c.addSetMember(cs.getConceptByName("Glibenclamide"));
				c.addSetMember(cs.getConceptByName("Furosemide"));
				c.addSetMember(cs.getConceptByName("Spironolactone"));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 3683;
			Concept c = cs.getConcept(id);
			log.warn("Updating answers for Chronic Care Diagnosis");
			c.getAnswers().clear();
			c.addAnswer(new ConceptAnswer(cs.getConcept(5)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(903)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(155)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(3720)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(3468)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(7623)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(6421)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(5622)));
			cs.saveConcept(c);
		}
	}

	private void removeOldReports() {

		AdministrationService as = Context.getAdministrationService();

		log.warn("Removing old Chronic Care Register_ report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id = (select report_design_id from reporting_report_design where name = 'Chronic Care Register_');", false);
		as.executeSQL("delete from reporting_report_design where name = 'Chronic Care Register_';", false);
		as.executeSQL("delete from serialized_object where name = 'Chronic Care Register_ Data Set';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid = (select uuid from serialized_object where name = 'Chronic Care Register_');", false);
		as.executeSQL("delete from serialized_object where name = 'Chronic Care Register_';", false);
		as.executeSQL("delete from serialized_object where name like 'chronic:%';", false);

		log.warn("Removing old Chronic Care Visits_ report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id = (select report_design_id from reporting_report_design where name = 'Chronic Care Visits_');", false);
		as.executeSQL("delete from reporting_report_design where name = 'Chronic Care Visits_';", false);
		as.executeSQL("delete from serialized_object where name = 'chronicvisits: encounters';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid = (select uuid from serialized_object where name = 'Chronic Care Visits_');", false);
		as.executeSQL("delete from serialized_object where name = 'Chronic Care Visits_';", false);

		log.warn("Removing old HCC Register report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id = (select report_design_id from reporting_report_design where name like 'HCC Register_%');", false);
		as.executeSQL("delete from reporting_report_design where name like 'HCC Register_%';", false);
		as.executeSQL("delete from serialized_object where name like 'HCC Register_%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name like 'HCC Register_%');", false);
		as.executeSQL("delete from serialized_object where name like 'HCC Register For All Locations_%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name like 'HCC Register For All Locations_%');", false);
		as.executeSQL("delete from serialized_object where name like 'hccreg%';", false);
		as.executeSQL("delete from serialized_object where name like 'hccregcomplete%';", false);

		log.warn("Removing old HCC Quarterly report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id = (select report_design_id from reporting_report_design where name = 'HCC Quarterly (Excel)_');", false);
		as.executeSQL("delete from reporting_report_design where name = 'HCC Quarterly (Excel)_';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid = (select uuid from serialized_object where name = 'HCC Quarterly_');", false);
		as.executeSQL("delete from serialized_object where name like 'hccquarterly%';", false);
		as.executeSQL("delete from serialized_object where name like 'HCC Quarterly_%';", false);

		log.warn("Removing old ARV Quarterly report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id = (select report_design_id from reporting_report_design where name = 'ARV QUARTERLY (Excel)_');", false);
		as.executeSQL("delete from reporting_report_design where name = 'ARV QUARTERLY (Excel)_';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid = (select uuid from serialized_object where name = 'ARV Quarterly_');", false);
		as.executeSQL("delete from serialized_object where name like 'arvquarterly%';", false);
		as.executeSQL("delete from serialized_object where name like 'ARV Quarterly_%';", false);

		// New after 3.7

		log.warn("Removing old HIV Visits report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id = (select report_design_id from reporting_report_design where name = 'HIV Visits_');", false);
		as.executeSQL("delete from reporting_report_design where name = 'HIV Visits_';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name = 'HIV Visits_');", false);
		as.executeSQL("delete from serialized_object where name like 'hivvisits:%';", false);
		as.executeSQL("delete from serialized_object where name like 'HIV Visits_%';", false);

		log.warn("Removing old KS Register report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id = (select report_design_id from reporting_report_design where name = 'KS Register_');", false);
		as.executeSQL("delete from reporting_report_design where name = 'KS Register_';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name = 'KS Register_');", false);
		as.executeSQL("delete from serialized_object where name like 'ks:%';", false);
		as.executeSQL("delete from serialized_object where name like 'KS Register_%';", false);

		log.warn("Removing old TB Register report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id in (select report_design_id from reporting_report_design where name like 'Tuberculosis Register%');", false);
		as.executeSQL("delete from reporting_report_design where name like 'Tuberculosis Register%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name = 'Tuberculosis Register_');", false);
		as.executeSQL("delete from serialized_object where name like 'tb:%';", false);
		as.executeSQL("delete from serialized_object where name like 'Tuberculosis Register_%';", false);

		log.warn("Removing old Pre-ART Register");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id in (select report_design_id from reporting_report_design where name like 'Pre-ART Register%');", false);
		as.executeSQL("delete from reporting_report_design where name like 'Pre-ART Register%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name = 'Pre-ART Register (incl. old patients)_');", false);
		as.executeSQL("delete from serialized_object where name like 'partregcomplete%';", false);
		as.executeSQL("delete from serialized_object where name like 'Pre-ART Register (incl. old patients)_%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name = 'Pre-ART Register For All Locations (incl. old patients) (SLOW)_');", false);
		as.executeSQL("delete from serialized_object where name like 'partreg%';", false);
		as.executeSQL("delete from serialized_object where name like 'Pre-ART Register For All Locations (incl. old patients) (SLOW)_%';", false);

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
