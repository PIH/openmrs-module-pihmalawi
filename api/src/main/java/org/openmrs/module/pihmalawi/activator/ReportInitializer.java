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
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.ApzuReportUtil;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.reports.ApzuReportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.config.DesignDescriptor;
import org.openmrs.module.reporting.config.ReportDescriptor;
import org.openmrs.module.reporting.config.ReportLoader;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
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
	    removeReport("9880C308-8734-444E-812B-05D95ADE63BE"); // Can be removed once "IC3 - Patient Future Appointments Report is confirmed removed
	    for (ReportManager reportManager : Context.getRegisteredComponents(ApzuReportManager.class)) {
	        if (reportManager.getClass().getAnnotation(Deprecated.class) != null) {
	            log.warn("Report " +reportManager.getName() + " is deprecated.  Removing it from use.");
	            removeReport(reportManager);
            }
            else {
                log.warn("Setting up report " +reportManager.getName() + "...");
                ReportManagerUtil.setupReport(reportManager);
            }
        }
        loadSqlReports();
        loadReportsFromConfig();
		ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
	}

	public static void removeReport(ReportManager reportManager) {
	    ReportDefinition rd = reportManager.constructReportDefinition();
	    removeReport(rd.getUuid());
	}

    public static void removeReport(String reportUuid) {
	    if (StringUtils.isNotBlank(reportUuid)) {
            ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
            ReportDefinition rd = rds.getDefinitionByUuid(reportUuid);
            if (rd != null) {
                Context.getService(ReportDefinitionService.class).purgeDefinition(rd);
            }
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

                    boolean deprecated = false;

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
                            else if (key.equalsIgnoreCase("DEPRECATED")) {
                                deprecated = true;
                            }
                        }
                        sql.append(line).append(System.getProperty("line.separator"));
                    }

                    if (rd.getUuid() == null || rd.getName() == null || designUuid == null) {
                        throw new IllegalArgumentException("SQL resource" + r.getFilename() + " must define a report_name, report_uuid and design_uuid at minimum");
                    }

                    SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();
                    dsd.setSql(sql.toString());
                    dsd.setParameters(rd.getParameters());

                    rd.addDataSetDefinition(r.getFilename(), Mapped.mapStraightThrough(dsd));

                    List<ReportDesign> designs = new ArrayList<ReportDesign>();
                    designs.add(ApzuReportUtil.createExcelDesign(designUuid, rd));

                    if (deprecated) {
                        removeReport(rd.getUuid());
                    }
                    else {
                        ReportManagerUtil.setupReportDefinition(rd, designs, null);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to load SQL reports from classpath", e);
        }
    }

    public static void loadReportsFromConfig() {
        log.warn("Loading reports from configuration");
        List<String> reportsLoaded = new ArrayList<>();
        for(ReportDescriptor reportDescriptor : ReportLoader.loadReportDescriptors()) {
            reportsLoaded.add(reportDescriptor.getUuid());
            for (DesignDescriptor designDescriptor : reportDescriptor.getDesigns()) {
                if (designDescriptor.getType().equalsIgnoreCase("excel")) {
                    designDescriptor.getProperties().put(ExcelTemplateRenderer.PASSWORD_PROPERTY, ApzuReportUtil.getExcelPassword());
                }
            }
            ReportLoader.loadReportFromDescriptor(reportDescriptor);
            log.warn("Loaded " + reportDescriptor.getName());
        }
        for (String uuid : reportsLoaded) {
            ReportDefinition reportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
            List<ReportDesign> existingDesigns = Context.getService(ReportService.class).getReportDesigns(reportDefinition, null, false);
            for (ReportDesign reportDesign : existingDesigns) {
                reportDesign.setName(MessageUtil.translate(reportDesign.getName()));
                Context.getService(ReportService.class).saveReportDesign(reportDesign);
            }
        }
        log.warn("Reports loaded from configuration");
    }

    /**
     * @see Initializer#stopped()
     */
    @Override
    public void stopped() {
    }
}
