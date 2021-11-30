package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IC3DataEntry extends ApzuReportManager{

	public static final String EXCEL_REPORT_DESIGN_UUID = "63a89a29-6b95-493e-876e-e963445a39e6";
	public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/appointment-data-entry-data.sql";

	@Override
	public String getUuid() {
		return "8d240c74-d533-454d-82bb-3b45bdcec68b";
	}

	@Override
	public String getName() {
		return "IC3 Data Entry Report";
	}

	@Override
	public String getDescription() {
		return "IC3 - Data Entry Report, 2020. This report is able to show the number of records recorded in the EMR based on the appointment of patients";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(ReportingConstants.END_DATE_PARAMETER);
		l.add(ReportingConstants.LOCATION_PARAMETER);
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());
		rd.addDataSetDefinition("apptRpt", Mapped.mapStraightThrough(constructDataSetDefinition()));
		return rd;
	}

	public DataSetDefinition constructDataSetDefinition() {
		SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();
		dsd.setParameters(getParameters());
		dsd.setSqlResource(SQL_DATA_SET_RESOURCE);
		return dsd;
	}


	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, "IC3DataEntryReport.xls");
		design.addPropertyValue("repeatingSections", "sheet:1,row:5,dataset:apptRpt");
		l.add(design);
		return l;
	}
}