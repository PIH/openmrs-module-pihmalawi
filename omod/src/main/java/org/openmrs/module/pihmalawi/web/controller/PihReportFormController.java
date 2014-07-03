package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.experimental.historicAppointmentAdherence.SetupAppointmentAdherence;
import org.openmrs.module.pihmalawi.reports.setup.SetupAppointmentsForLocation;
import org.openmrs.module.pihmalawi.reports.setup.SetupArtMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupArvQuarterly;
import org.openmrs.module.pihmalawi.reports.setup.SetupChronicCareMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupFindPatientsToMergeSoundex;
import org.openmrs.module.pihmalawi.reports.setup.SetupHccMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupHivDataQuality;
import org.openmrs.module.pihmalawi.reports.setup.SetupKsRegister;
import org.openmrs.module.pihmalawi.reports.setup.SetupPreArtRegister;
import org.openmrs.module.pihmalawi.reports.setup.SetupTbRegister;
import org.openmrs.module.pihmalawi.reports.setup.SetupWeeklyEncounter;
import org.openmrs.module.pihmalawi.reports.setup.outdated.SetupPreArtMissedAppointment;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
public class PihReportFormController {

	@RequestMapping("/module/pihmalawi/remove_tb_register.form")
	public void removeTbRegister() {
		new SetupTbRegister(new ReportHelper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_tb_register.form")
	public void registerTbRegister() throws Exception {
		new SetupTbRegister(new ReportHelper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_hccmissedappointment_lowerneno.form")
	public void removeHccMissedAppointmentLowerNeno() {
		new SetupHccMissedAppointment(new ReportHelper(), false)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_hccmissedappointment_lowerneno.form")
	public void registerHccMissedAppointmentLowerNeno() throws Exception {
		new SetupHccMissedAppointment(new ReportHelper(), false).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_partregister.form")
	public void removePreArtRegister() {
		new SetupPreArtRegister(new ReportHelper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_partregister.form")
	public void registerPreArtRegister() throws Exception {
		new SetupPreArtRegister(new ReportHelper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_partmissedappointment_lowerneno.form")
	public void removePreArtMissedAppointmentLowerNeno() {
		new SetupPreArtMissedAppointment(new ReportHelper(), false)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_partmissedappointment_lowerneno.form")
	public void registerPreArtMissedAppointmentLowerNeno() throws Exception {
		new SetupPreArtMissedAppointment(new ReportHelper(), false).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_artmissedappointment_lowerneno.form")
	public void removeArtMissedAppointmentLowerNeno() {
		new SetupArtMissedAppointment(new ReportHelper(), false)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_artmissedappointment_lowerneno.form")
	public void registerArtMissedAppointmentLowerNeno() throws Exception {
		new SetupArtMissedAppointment(new ReportHelper(), false).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_partmissedappointment.form")
	public void removePreArtMissedAppointment() {
		new SetupPreArtMissedAppointment(new ReportHelper(), true)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_partmissedappointment.form")
	public void registerPreArtMissedAppointment() throws Exception {
		new SetupPreArtMissedAppointment(new ReportHelper(), true).setup(false);
	}

	@RequestMapping("/module/pihmalawi/register_hccmissedappointment.form")
	public void registerHccMissedAppointment() throws Exception {
		new SetupHccMissedAppointment(new ReportHelper(), true).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_hccmissedappointment.form")
	public void removeHccMissedAppointment() {
		new SetupHccMissedAppointment(new ReportHelper(), true)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/remove_artmissedappointment.form")
	public void removeArtMissedAppointment() {
		new SetupArtMissedAppointment(new ReportHelper(), true)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_artmissedappointment.form")
	public void registerArtMissedAppointment() throws Exception {
		new SetupArtMissedAppointment(new ReportHelper(), true).setup(false);
	}

	@RequestMapping("/module/pihmalawi/register_weeklyencounter.form")
	public void registerWeeklyEncounter() throws Exception {
		new SetupWeeklyEncounter(new ReportHelper()).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_weeklyencounter.form")
	public void removeWeeklyEncounter() {
		new SetupWeeklyEncounter(new ReportHelper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_arvquarterly.form")
	public void registerArvQuarterly() throws Exception {
		new SetupArvQuarterly(new ReportHelper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_arvquarterly.form")
	public void removeArvQuarterly() {
		new SetupArvQuarterly(new ReportHelper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_hivdataquality.form")
	public void registerHivDataQuality() throws Exception {
		new SetupHivDataQuality(new ReportHelper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_hivdataquality.form")
	public void removeHivDataQuality() {
		new SetupHivDataQuality(new ReportHelper()).delete();
	}

	@RequestMapping("/module/pihmalawi/remove_findpatientstomerge.form")
	public void removeDuplicateHivPatients() {
		new SetupFindPatientsToMergeSoundex(new ReportHelper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_findpatientstomerge.form")
	public void registerDuplicateHivPatients() throws Exception {
		new SetupFindPatientsToMergeSoundex(new ReportHelper()).setup();
	}

	// Chronic Care
	@RequestMapping("/module/pihmalawi/remove_chroniccaremissedappointment.form")
	public void removeChronicCareMissedAppointment() {
		new SetupChronicCareMissedAppointment(new ReportHelper())
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_chroniccaremissedappointment.form")
	public void registerChronicCareMissedAppointment() throws Exception {
		new SetupChronicCareMissedAppointment(new ReportHelper()).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_chroniccareappadherence.form")
	public void removeChronicCareAppAdherence() {
		new SetupAppointmentAdherence(new ReportHelper(), "adcc", "Chronic Care", null,
				Arrays.asList(Context.getEncounterService().getEncounterType(
						"CHRONIC_CARE_FOLLOWUP")), false).delete();
	}

	@RequestMapping("/module/pihmalawi/register_chroniccareappadherence.form")
	public void registerChronicCareAppAdherence() throws Exception {
		new SetupAppointmentAdherence(new ReportHelper(), "adcc", "Chronic Care", null,
				Arrays.asList(Context.getEncounterService().getEncounterType(
						"CHRONIC_CARE_FOLLOWUP")), false).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_artappadherence.form")
	public void removeArtAppAdherence() {
		new SetupAppointmentAdherence(new ReportHelper(), "adart", "ART", Context
				.getProgramWorkflowService().getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"), Arrays.asList(Context
				.getEncounterService().getEncounterType("ART_INITIAL"), Context
				.getEncounterService().getEncounterType("ART_FOLLOWUP")), false)
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_artappadherence.form")
	public void registerArtAppAdherence() throws Exception {
		new SetupAppointmentAdherence(new ReportHelper(), "adart", "ART", Context
				.getProgramWorkflowService().getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"), Arrays.asList(Context
				.getEncounterService().getEncounterType("ART_INITIAL"), Context
				.getEncounterService().getEncounterType("ART_FOLLOWUP")), false)
				.setup();
	}

	@RequestMapping("/module/pihmalawi/remove_appointments.form")
	public void removeAppointments() {
		new SetupAppointmentsForLocation(new ReportHelper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_appointments.form")
	public void registerAppointments() throws Exception {
		new SetupAppointmentsForLocation(new ReportHelper())
				.setup();
	}

	@RequestMapping("/module/pihmalawi/register_ksregister.form")
	public String registerKsRegister() throws Exception {
		ReportDefinition[] rds = new SetupKsRegister(new ReportHelper()).setup();
		if (rds.length == 1) {
			return "redirect:/module/reporting/run/runReport.form?reportId=" + rds[0].getUuid();
		}
		else {
			return "redirect:/module/reporting/dashboard/index.form";
		}
	}

	@RequestMapping("/module/pihmalawi/remove_ksregister.form")
	public String removeKsRegister() {
		new SetupKsRegister(new ReportHelper()).delete();
		return "redirect:/module/reporting/dashboard/index.form";
	}
}
