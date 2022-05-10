package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.PihMalawiConstants;
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
public class IC3DStudyReport extends ApzuReportManager{

	public static final String EXCEL_REPORT_DESIGN_UUID = "c64dda03-9a64-4ae7-9caa-314c5f05b38b";
	public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/ic3d-study-report.sql";

	@Override
	public String getUuid() {
		return "a22d40d2-f97e-44d1-92d1-a46aa1b4506d";
	}

	@Override
	public String getName() {
		return "IC3D Study Report";
	}

	@Override
	public String getDescription() {
		return "IC3D Study Report - This report will provide an extract for the IC3D study dataset to be used for analysis.";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(ReportingConstants.START_DATE_PARAMETER);
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

		SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();
		dsd.setParameters(getParameters());
		dsd.setConnectionPropertyFile(PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
		dsd.setSqlResource(SQL_DATA_SET_RESOURCE);

		rd.addDataSetDefinition("ic3dRpt", Mapped.mapStraightThrough(dsd));

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, "IC3DStudyReport.xls");
		design.addPropertyValue("repeatingSections", "sheet:1,row:6,dataset:ic3dRpt");
		l.add(design);
		return l;
	}
}