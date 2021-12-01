package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class IC3AppointmentReportNewTest extends ReportManagerTest {

    @Autowired
    IC3AppointmentReportNew ic3AppointmentReportNew;

    @Autowired
    LocationService locationService;

    @Override
    public ReportManager getReportManager() {
        return ic3AppointmentReportNew;
    }

    @Override
    public EvaluationContext getEvaluationContext() {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", DateUtil.getDateTime(2022, 03, 01));
        Location location = locationService.getLocation(2);
        context.addParameterValue("location",location);
        context.addParameterValue("advancedCare",false);
        return context;
    }

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    public boolean enableReportOutput() {
        return true;
    }
}
