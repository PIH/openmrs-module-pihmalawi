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
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.pihmalawi.reporting.reports.ApzuReportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
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

	private void removeOldReports() {
		String gpVal = Context.getAdministrationService().getGlobalProperty("pihmalawi.oldReportsRemoved");
		if (ObjectUtil.isNull(gpVal)) {
			AdministrationService as = Context.getAdministrationService();
			log.warn("Removing all reports");
			as.executeSQL("delete from reporting_report_design_resource;", false);
			as.executeSQL("delete from reporting_report_design;", false);
			as.executeSQL("delete from reporting_report_request;", false);
			as.executeSQL("delete from serialized_object;", false);
			ReportUtil.updateGlobalProperty("pihmalawi.oldReportsRemoved", "true");
		}
	}
}