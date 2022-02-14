package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.SqlFileIndicatorDataSetDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NCDQuarterlyReport extends ApzuReportManager {


    public static final String EXCEL_REPORT_DESIGN_UUID = "B7BDBA39-3FBB-4CAD-A310-F81F345F7564";
    public static final String SQL_DATA_SET_RESOURCE = "org/openmrs/module/pihmalawi/reporting/datasets/sql/ncd-quarterly.sql";

    @Override
    public String getUuid() {
        return "29CEEADC-0807-44B5-B7AA-2FA503BE448A";
    }

    @Override
    public String getName() {
        return "NCD - Quarterly Report";
    }

    @Override
    public String getDescription() {
        return "NCD Quarterly Report by facility";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(ReportingConstants.LOCATION_PARAMETER);
        parameters.add(ReportingConstants.START_DATE_PARAMETER);
        parameters.add(ReportingConstants.END_DATE_PARAMETER);
        return parameters;
    }

    @Override
    public ReportDefinition constructReportDefinition() {
        ReportDefinition rd = new ReportDefinition();
        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.setParameters(getParameters());

        SqlFileIndicatorDataSetDefinition dsd = new SqlFileIndicatorDataSetDefinition();
        dsd.setParameters(getParameters());
        dsd.setConnectionPropertyFile(PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
        dsd.setSqlResource(SQL_DATA_SET_RESOURCE);
        dsd.setIndicatorNameColumn("indicator");
        dsd.setIndicatorDescriptionColumn("description");
        dsd.setIndicatorValueColumn("indicator_value");

        rd.addDataSetDefinition("NCDQuarterlyIndicatorReport", Mapped.mapStraightThrough(dsd));
        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {

        List<ReportDesign> reportDesign = new ArrayList<ReportDesign>();
        ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, "NCDQuarterlyIndicatorReport.xls");
        reportDesign.add(design);

        return reportDesign;
    }
}
