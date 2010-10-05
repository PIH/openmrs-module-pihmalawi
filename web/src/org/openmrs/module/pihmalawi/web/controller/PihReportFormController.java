package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupArtWeeklyVisit;
import org.openmrs.module.pihmalawi.reporting.SetupHivWeeklyOutcome;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PihReportFormController {
	
	@RequestMapping("/module/pihmalawi/remove_artweeklyvisit.form")
	public void removeArtWeeklyVisit() {
		new SetupArtWeeklyVisit(new Helper()).deleteReportElements();
	}
	
	@RequestMapping("/module/pihmalawi/register_artweeklyvisit.form")
	public void registerArtWeeklyVisit() throws Exception {
		new SetupArtWeeklyVisit(new Helper()).setupHivWeekly(false);
	}
	
	@RequestMapping("/module/pihmalawi/register_hivweeklyoutcome.form")
	public void registerHivWeeklyOutcome() throws Exception {
		new SetupHivWeeklyOutcome(new Helper()).setupHivWeekly(false);
	}
	
	@RequestMapping("/module/pihmalawi/remove_hivweeklyoutcome.form")
	public void removeHivWeeklyOutcome() {
		new SetupHivWeeklyOutcome(new Helper()).deleteReportElements();
	}
	

}
