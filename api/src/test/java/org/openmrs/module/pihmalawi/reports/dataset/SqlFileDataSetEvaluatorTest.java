package org.openmrs.module.pihmalawi.reports.dataset;

import org.junit.Assert;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.activator.ReportInitializer;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

public class SqlFileDataSetEvaluatorTest extends StandaloneContextSensitiveTest {

    @Autowired @Qualifier("reportingReportDefinitionService")
    ReportDefinitionService reportDefinitionService;

    @Autowired
    HivMetadata metadata;

	@Override
	public void performTest() throws Exception {
        ReportInitializer initializer = new ReportInitializer();
        initializer.loadSqlReports();

        ReportData data = runHighViralLoadReport();
        //ReportData data = runEidNeedingTestingReport();
        //ReportData data = runIC3Register();
        //ReportData data = runNeedViralLoadReport();
        //ReportData data = runPatientsNeedingCd4();
        //ReportData data = runPatientWeightChange();

        for (String dsName : data.getDataSets().keySet()) {
            System.out.println(dsName);
            System.out.println("--------------------------");
            DataSetUtil.printDataSet(data.getDataSets().get(dsName), System.out);
            System.out.println("--------------------------");
            System.out.println(" ");
        }
	}

	public ReportData runHighViralLoadReport() throws EvaluationException {
        ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("2fe281be-3ff4-11e6-9d69-0f1641034c73");
        Assert.assertNotNull(rd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", DateUtil.getDateTime(2017, 1, 1));
        context.addParameterValue("min_vl", 1000);
        return reportDefinitionService.evaluate(rd, context);
    }

    public ReportData runEidNeedingTestingReport() throws EvaluationException {
        ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("5d445530-63c5-11e5-a9f6-d60697e5b5db");
        Assert.assertNotNull(rd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", new Date());
        context.addParameterValue("location", metadata.getNenoHospital());
        return reportDefinitionService.evaluate(rd, context);
    }

    public ReportData runIC3Register() throws EvaluationException {
        ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("897C0E0A-1F8A-4ABD-AFE2-054146227668");
        Assert.assertNotNull(rd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("reportEndDate", new Date());
        return reportDefinitionService.evaluate(rd, context);
    }

    public ReportData runNeedViralLoadReport() throws EvaluationException {
        ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("7b3473c8-4005-11e6-9d69-0f1641034c73");
        Assert.assertNotNull(rd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", new Date());
        context.addParameterValue("location", metadata.getNenoHospital());
        return reportDefinitionService.evaluate(rd, context);
    }

    public ReportData runPatientsNeedingCd4() throws EvaluationException {
        ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("56dce9c9-5d4b-11e5-a151-e82aea237783");
        Assert.assertNotNull(rd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", new Date());
        context.addParameterValue("location", metadata.getNenoHospital());
        return reportDefinitionService.evaluate(rd, context);
    }

    public ReportData runPatientWeightChange() throws EvaluationException {
        ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("a222543e-63c2-11e5-a9f6-d60697e5b5db");
        Assert.assertNotNull(rd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", new Date());
        context.addParameterValue("obsWithin", 3);
        context.addParameterValue("monthsBack", 3);
        return reportDefinitionService.evaluate(rd, context);
    }

    @Override
    protected boolean isEnabled() {
        return false;
    }
}
