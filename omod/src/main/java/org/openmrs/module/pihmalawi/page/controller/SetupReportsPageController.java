package org.openmrs.module.pihmalawi.page.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.reports.ApzuReportManager;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick page to re-initiate setting up reports to avoid having to restart the system
 */
public class SetupReportsPageController {

    public void get(PageModel model) {
        List<String> messages = new ArrayList<String>();
        for (ReportManager reportManager : Context.getRegisteredComponents(ApzuReportManager.class)) {
            ReportManagerUtil.setupReport(reportManager);
            messages.add(reportManager.getName() + " version " + reportManager.getVersion() + " setup.");
        }
        model.addAttribute("messages", messages);
    }
}
