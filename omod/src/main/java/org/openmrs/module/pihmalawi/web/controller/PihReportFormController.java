package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.setup.SetupArvQuarterly;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PihReportFormController {

	@RequestMapping("/module/pihmalawi/registerReports.form")
	public void registerReports() { }

	@RequestMapping("/module/pihmalawi/register_arvquarterly.form")
	public String registerArvQuarterly() throws Exception {
		new SetupArvQuarterly(new ReportHelper()).setup();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_arvquarterly.form")
	public String removeArvQuarterly() {
		new SetupArvQuarterly(new ReportHelper()).delete();
		return "redirect:/module/pihmalawi/registerReports.form";
	}
}
