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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.ApzuReportUtil;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MysqlCmdDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.reports.ApzuReportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.ArrayList;
import java.util.List;

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
		// removeOldReports();
		//ReportManagerUtil.setupAllReports(ApzuReportManager.class);
        // loadSqlReports();
		// ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
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

    public static void loadSqlReports() {
        PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resourceResolver.getResources("classpath*:/org/openmrs/module/pihmalawi/reporting/reports/sql/*");
            if (resources != null) {
                for (Resource r : resources) {
                    log.info("Loading " + r.getFilename());
                    List<String> lineByLineContents = IOUtils.readLines(r.getInputStream(), "UTF-8");

                    ReportDefinition rd = new ReportDefinition();
                    String designUuid = null;
                    StringBuilder sql = new StringBuilder();

                    for (String line : lineByLineContents) {
                        if (line.startsWith("-- ##")) {
                            String[] keyValue = StringUtils.splitByWholeSeparator(line.substring(5, line.length()), "=");
                            String key = keyValue[0].trim().toLowerCase();
                            String value = keyValue[1].trim();
                            if (key.equals("report_uuid")) {
                                rd.setUuid(value);
                            }
                            else if (key.equals("report_name")) {
                                rd.setName(value);
                            }
                            else if (key.equals("report_description")) {
                                rd.setDescription(value);
                            }
                            else if (key.equals("parameter")) {
                                String[] paramElements = StringUtils.splitByWholeSeparator(value, "|");
                                Parameter p = new Parameter();
                                p.setName(paramElements[0]);
                                p.setLabel(paramElements[1]);
                                p.setType(Context.loadClass(paramElements[2]));
                                rd.addParameter(p);
                            }
                            else if (key.equals("design_uuid")) {
                                designUuid = value;
                            }
                        }
                        sql.append(line).append(System.getProperty("line.separator"));
                    }

                    if (rd.getUuid() == null || rd.getName() == null || designUuid == null) {
                        throw new IllegalArgumentException("SQL resource" + r.getFilename() + " must define a report_name, report_uuid and design_uuid at minimum");
                    }

                    MysqlCmdDataSetDefinition dsd = new MysqlCmdDataSetDefinition();
                    dsd.setSql(sql.toString());
                    dsd.setParameters(rd.getParameters());

                    rd.addDataSetDefinition(r.getFilename(), Mapped.mapStraightThrough(dsd));

                    List<ReportDesign> designs = new ArrayList<ReportDesign>();
                    designs.add(ApzuReportUtil.createExcelDesign(designUuid, rd));

                    ReportManagerUtil.setupReportDefinition(rd, designs, null);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to load SQL reports from classpath", e);
        }
    }
}