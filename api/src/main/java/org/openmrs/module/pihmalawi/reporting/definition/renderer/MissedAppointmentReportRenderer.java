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
package org.openmrs.module.pihmalawi.reporting.definition.renderer;

import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MissedAppointmentDataSetDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.util.ReportUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Renderer for the ApzuMissedAppointmentReport subclasses, which enables 2 different types of output.
 * The first is an overview report based on a template.
 * The second is a standard Xls report, with one data set per tab
 */
@Handler
public class MissedAppointmentReportRenderer extends ReportDesignRenderer {

	@Override
	public String getRenderedContentType(ReportRequest request) {
		return "application/vnd.ms-excel";
	}

	@Override
	public String getFilename(ReportRequest request) {
		Date endDate = (Date)request.getReportDefinition().getParameterMappings().get("endDate");
		if (endDate == null) {
			endDate = new Date();
		}
		return request.getReportDefinition().getParameterizable().getName() + "_" + DateUtil.formatDate(endDate, "yyyyMMdd") + ".xls";
	}

	@Override
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		MissedAppointmentDataSetDefinition.Mode mode = (MissedAppointmentDataSetDefinition.Mode)reportData.getContext().getParameterValue("mode");
		ReportDesign rd = getDesign(argument);
		if (mode == MissedAppointmentDataSetDefinition.Mode.OVERVIEW) {
			RenderingMode renderingMode = ReportUtil.renderingModeFromResource("Excel", "org/openmrs/module/pihmalawi/reporting/reports/ApzuMissedAppointmentReport.xls");
			ReportDesign design = ((ReportDesignRenderer)renderingMode.getRenderer()).getDesign("");
			design.setProperties(rd.getProperties());
			design.addPropertyValue("repeatingSections", "sheet:1,column:2-6,dataset:data");
			renderingMode.getRenderer().render(reportData, renderingMode.getArgument(), out);
		}
		else {
			ReportData data = new ReportData();
			data.setContext(reportData.getContext());
			data.setDefinition(reportData.getDefinition());
			SimpleDataSet ds = (SimpleDataSet)reportData.getDataSets().values().iterator().next();
			for (DataSetRow row : ds.getRows()) {
				for (DataSetColumn column : row.getColumnValues().keySet()) {
					Object columnValue = row.getColumnValue(column);
					if (columnValue instanceof DataSet) {
						DataSet indicatorDataSet = (DataSet)columnValue;
						indicatorDataSet.getDefinition().setName(column.getLabel());
						indicatorDataSet.getContext().getParameterValues().remove("mode");
						data.getDataSets().put(column.getName(), indicatorDataSet);
					}
				}
			}
			XlsReportRenderer xlsReportRenderer = new XlsReportRenderer() {
				public boolean getIncludeDataSetNameAndParameters(ReportDesign design) {
					return true;
				}
			};
			xlsReportRenderer.render(data, "", out);
		}
	}
}