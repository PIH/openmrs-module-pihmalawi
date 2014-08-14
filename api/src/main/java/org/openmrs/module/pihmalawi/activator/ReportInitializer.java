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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.reports.ApzuReportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;

/**
 * Initializes reports
 */
public class ReportInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(ReportInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		removeOldReports();
		ReportManagerUtil.setupAllReports(ApzuReportManager.class);
		ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}

	/**
	 * TODO: Clean this up
	 */
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

		log.warn("Removing Appointments report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id in (select report_design_id from reporting_report_design where name like 'Appointments%');", false);
		as.executeSQL("delete from reporting_report_design where name like 'Appointments%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name = 'Appointments_');", false);
		as.executeSQL("delete from serialized_object where name like 'appt%';", false);
		as.executeSQL("delete from serialized_object where name like 'Appointments_%';", false);

		log.warn("Removing Find Patients To Merge report");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id in (select report_design_id from reporting_report_design where name like 'Find patients to merge%');", false);
		as.executeSQL("delete from reporting_report_design where name like 'Find patients to merge%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name = 'Find patients to merge (SLOW)_');", false);
		as.executeSQL("delete from serialized_object where name like 'merge:%';", false);
		as.executeSQL("delete from serialized_object where name like 'Find patients to merge (SLOW)_%';", false);

		log.warn("Removing Weekly Encounter Reports");

		as.executeSQL("delete from reporting_report_design_resource where report_design_id in (select report_design_id from reporting_report_design where name like 'Weekly Encounter%');", false);
		as.executeSQL("delete from reporting_report_design where name like 'Weekly Encounter%';", false);
		as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name like 'Weekly Encounter%');", false);
		as.executeSQL("delete from serialized_object where name like 'enc:%';", false);
		as.executeSQL("delete from serialized_object where name like 'Weekly Encounter%';", false);

		log.warn("Removing HIV Data Quality Reports");
		deleteAll("HIV Data Quality For All Users.xls (Excel)_", "HIV Data Quality_", "HIV Data Quality By User_", "HIV Data Quality For All Users (SLOW)_", "hivdq:");
	}

	protected void deleteAll(String... prefixes) {
		AdministrationService as = Context.getAdministrationService();
		for (String prefix : prefixes) {
			deleteReportDesigns(prefix);
			as.executeSQL("delete from reporting_report_request where report_definition_uuid in (select uuid from serialized_object where name like '"+prefix+"%');", false);
			as.executeSQL("delete from serialized_object where name like '"+prefix+"%';", false);
		}
	}

	protected void deleteReportDesigns(String prefix) {
		AdministrationService as = Context.getAdministrationService();
		as.executeSQL("delete from reporting_report_design_resource where report_design_id in (select report_design_id from reporting_report_design where name like '"+prefix+"%');", false);
		as.executeSQL("delete from reporting_report_design where name like '"+prefix+"%';", false);
	}

}