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
package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.SimpleHtmlReportRenderer;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
@Handler
@Localized("pihmalawi.reporting.PreviewReportRenderer")
public class PreviewReportRenderer extends SimpleHtmlReportRenderer {
	
	public void outputColumnValue(Writer w, String name, Object colValue) throws IOException {
		if (colValue != null) {
			if (colValue instanceof Cohort) {
				Cohort c = (Cohort)colValue;
				if (c.isEmpty()) {
					w.write(""+c.size());
				}
				else {
					String n = URLEncoder.encode(name, "UTF-8");
					String url = "/openmrs/module/pihmalawi/viewCohort.htm?title="+n+"&patientIds=" + c.getCommaSeparatedPatientIds();
					w.write("<a href=\"" + url + "\" target=\"_blank\">" + c.size() + "</a>");
				}
			}
			else {
				w.write(colValue.toString());
			}
		}
	}
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new PrintWriter(out);
		w.write("<html><head></head><body>");
		w.write("<style>");
		w.write("table td, th { text-align:left; padding-right:10px; }");
		w.write("table th { }");
		w.write("</style>");
		for (String key : results.getDataSets().keySet()) {
			w.write("<h4>" + key + "</h4>");
			w.write("<table id=\"preview-dataset-" + key + "\" class=\"preview-dataset\" border=1><tr>");
			DataSet dataset = results.getDataSets().get(key);
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();

			if (dataset instanceof MapDataSet) {
				DataSetRow data = dataset.iterator().next();
				if (dataset.getDefinition() instanceof CohortCrossTabDataSetDefinition) {
					CohortCrossTabDataSetDefinition cdd = (CohortCrossTabDataSetDefinition) dataset.getDefinition();
					
					List<String> rows = new ArrayList<String>(cdd.getRows().keySet());
					List<String> cols = new ArrayList<String>(cdd.getColumns().keySet());
					
					if (rows.isEmpty()) {
						for (String colName : cols) {
							w.write("<tr><td>" + colName + ": </td><td>");
							outputColumnValue(w, colName, data.getColumnValue(colName));
							w.write("</td></tr>");
						}
					}
					else if (cols.isEmpty()) {
						for (String rowName : rows) {
							w.write("<tr><td>" + rowName + ": </td><td>");
							outputColumnValue(w, rowName, data.getColumnValue(rowName));
							w.write("</td></tr>");
						}
					}
					else {
						w.write("<tr><td>&nbsp;</td>");
						for (String colName : cols) {
							w.write("<td>"+colName + "</td>");
						}
						w.write("</tr>");
						for (String rowName : rows) {
							w.write("<tr><td>" + rowName + "</td>");
							for (String colName : cols) {
								w.write("<td>");
								String dataKey = ObjectUtil.decodeStr(rowName, "", rowName + ".") + colName;
								outputColumnValue(w, rowName + " - " + colName, data.getColumnValue(dataKey));
								w.write("</td>");
							}
							w.write("</tr>");
						}
					}
				}
				else {
					for (DataSetColumn column : columns) {
						w.write("<tr><td>" + column.getLabel() + "<td>");
						outputColumnValue(w, column.getLabel(), data.getColumnValue(column));
						w.write("</td><td>");
					}
				}
			}
			else {
				for (DataSetColumn column : columns) {
					w.write("<th>"+column.getName()+"</th>");
				}
				w.write("</tr>");

				for (DataSetRow row : dataset) {
					w.write("<tr>");
					for (DataSetColumn column : columns) {
						w.write("<td>");
						outputColumnValue(w, column.getLabel(), row.getColumnValue(column.getName()));
						w.write("</td>");
					}
					w.write("</tr>");
				}
			}
			w.write("</table>");
		}
		w.write("</body>");
		w.write("</head>");		
		w.write("</html>");
		w.flush();
	}
}
