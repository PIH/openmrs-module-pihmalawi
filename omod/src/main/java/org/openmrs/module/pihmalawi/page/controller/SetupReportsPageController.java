package org.openmrs.module.pihmalawi.page.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.activator.ReportInitializer;
import org.openmrs.module.pihmalawi.reporting.ApzuReportUtil;
import org.openmrs.module.pihmalawi.reporting.reports.ApzuReportManager;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Quick page to re-initiate setting up reports to avoid having to restart the system
 */
public class SetupReportsPageController {

    public void get(PageModel model) {
        Map<ReportManager, ReportDefinition> m = new TreeMap<ReportManager, ReportDefinition>(new Comparator<ReportManager>() {
            @Override
            public int compare(ReportManager o1, ReportManager o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (ApzuReportManager rm : getAllReportManagers()) {
            ReportDefinition rd = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(rm.getUuid());
            m.put(rm, rd);
        }
        model.addAttribute("reportMap", m);
    }

    public String post(PageModel model, UiUtils ui, @RequestParam("mode") String mode, @RequestParam("uuid") String uuid) {
        List<String> messages = new ArrayList<String>();
        if ("delete".equalsIgnoreCase(mode)) {
            ReportInitializer.removeReport(uuid);
        }
        else if ("create".equalsIgnoreCase(mode)) {
            for (ReportManager reportManager : getAllReportManagers()) {
                if (reportManager.getUuid().equals(uuid)) {
                    ReportManagerUtil.setupReport(reportManager);
                    messages.add(reportManager.getName() + " version " + reportManager.getVersion() + " setup.");
                }
            }
        }
        model.addAttribute("messages", messages);
        return "redirect:" + ui.pageLink(PihMalawiConstants.MODULE_ID, "setupReports");
    }

    protected List<ApzuReportManager> getAllReportManagers() {
        return Context.getRegisteredComponents(ApzuReportManager.class);
    }
}
