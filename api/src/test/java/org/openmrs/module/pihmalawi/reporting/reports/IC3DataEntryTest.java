package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.springframework.beans.factory.annotation.Autowired;

public class IC3DataEntryTest extends ReportManagerTest{

    @Autowired
    IC3DataEntry ic3AppointmentDataEntry;

    @Override
    public ReportManager getReportManager() {
        return ic3AppointmentDataEntry;
    }

    @Override
    public EvaluationContext getEvaluationContext() {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", DateUtil.getDateTime(2021, 01, 20));
        context.addParameterValue("location",4);
        return context;
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    public boolean enableReportOutput() {
        return true;
    }
}
