package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupArtMissedAppointment;
import org.openmrs.module.pihmalawi.reporting.SetupArtWeeklyVisit;
import org.openmrs.module.pihmalawi.reporting.SetupHivWeeklyOutcome;
import org.openmrs.module.pihmalawi.reporting.SetupPreArtMissedAppointment;
import org.openmrs.module.pihmalawi.reporting.SetupPreArtWeekly;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PihReportFormController {
	
	@RequestMapping("/module/pihmalawi/remove_partmissedappointment.form")
	public void removePreArtMissedAppointment() {
		new SetupPreArtMissedAppointment(new Helper()).deleteReportElements();
	}
	
	@RequestMapping("/module/pihmalawi/register_partmissedappointment.form")
	public void registerPreArtMissedAppointment() throws Exception {
		new SetupPreArtMissedAppointment(new Helper()).setup(false);
	}
	
	@RequestMapping("/module/pihmalawi/remove_artmissedappointment.form")
	public void removeArtMissedAppointment() {
		new SetupArtMissedAppointment(new Helper()).deleteReportElements();
	}
	
	@RequestMapping("/module/pihmalawi/register_artmissedappointment.form")
	public void registerArtMissedAppointment() throws Exception {
		new SetupArtMissedAppointment(new Helper()).setup(false);
	}
	
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
		new SetupHivWeeklyOutcome(new Helper()).setup(false);
	}
	
	@RequestMapping("/module/pihmalawi/remove_hivweeklyoutcome.form")
	public void removeHivWeeklyOutcome() {
		new SetupHivWeeklyOutcome(new Helper()).delete();
	}
	
	@RequestMapping("/module/pihmalawi/register_preartweekly.form")
	public void registerPreArtWeekly() throws Exception {
		new SetupPreArtWeekly(new Helper()).setup(false);
	}
	
	@RequestMapping("/module/pihmalawi/remove_preartweekly.form")
	public void removePreArtWeekly() {
		new SetupPreArtWeekly(new Helper()).delete();
	}
	

}
