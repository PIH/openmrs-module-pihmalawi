package org.openmrs.module.pihmalawi.web.controller;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.experimental.historicAppointmentAdherence.SetupAppointmentAdherence;
import org.openmrs.module.pihmalawi.reports.setup.SetupArtMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupArvQuarterly;
import org.openmrs.module.pihmalawi.reports.setup.SetupChronicCareMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupHccMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.outdated.SetupPreArtMissedAppointment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class PihReportFormController {

	@RequestMapping("/module/pihmalawi/registerReports.form")
	public void registerReports() {

	}

	@RequestMapping("/module/pihmalawi/remove_hccmissedappointment_lowerneno.form")
	public String removeHccMissedAppointmentLowerNeno() {
		new SetupHccMissedAppointment(new ReportHelper(), false).deleteReportElements();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_hccmissedappointment_lowerneno.form")
	public String registerHccMissedAppointmentLowerNeno() throws Exception {
		new SetupHccMissedAppointment(new ReportHelper(), false).setup(false);
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_partmissedappointment_lowerneno.form")
	public String removePreArtMissedAppointmentLowerNeno() {
		new SetupPreArtMissedAppointment(new ReportHelper(), false).deleteReportElements();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_partmissedappointment_lowerneno.form")
	public String registerPreArtMissedAppointmentLowerNeno() throws Exception {
		new SetupPreArtMissedAppointment(new ReportHelper(), false).setup(false);
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_artmissedappointment_lowerneno.form")
	public String removeArtMissedAppointmentLowerNeno() {
		new SetupArtMissedAppointment(new ReportHelper(), false).deleteReportElements();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_artmissedappointment_lowerneno.form")
	public String registerArtMissedAppointmentLowerNeno() throws Exception {
		new SetupArtMissedAppointment(new ReportHelper(), false).setup(false);
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_partmissedappointment.form")
	public String removePreArtMissedAppointment() {
		new SetupPreArtMissedAppointment(new ReportHelper(), true).deleteReportElements();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_partmissedappointment.form")
	public String registerPreArtMissedAppointment() throws Exception {
		new SetupPreArtMissedAppointment(new ReportHelper(), true).setup(false);
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_hccmissedappointment.form")
	public String registerHccMissedAppointment() throws Exception {
		new SetupHccMissedAppointment(new ReportHelper(), true).setup(false);
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_hccmissedappointment.form")
	public String removeHccMissedAppointment() {
		new SetupHccMissedAppointment(new ReportHelper(), true).deleteReportElements();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_artmissedappointment.form")
	public String removeArtMissedAppointment() {
		new SetupArtMissedAppointment(new ReportHelper(), true).deleteReportElements();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_artmissedappointment.form")
	public String registerArtMissedAppointment() throws Exception {
		new SetupArtMissedAppointment(new ReportHelper(), true).setup(false);
		return "redirect:/module/pihmalawi/registerReports.form";
	}

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

	// Chronic Care
	@RequestMapping("/module/pihmalawi/remove_chroniccaremissedappointment.form")
	public String removeChronicCareMissedAppointment() {
		new SetupChronicCareMissedAppointment(new ReportHelper()).deleteReportElements();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_chroniccaremissedappointment.form")
	public String registerChronicCareMissedAppointment() throws Exception {
		new SetupChronicCareMissedAppointment(new ReportHelper()).setup(false);
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_chroniccareappadherence.form")
	public String removeChronicCareAppAdherence() {
		List<EncounterType> l = Arrays.asList(Context.getEncounterService().getEncounterType("CHRONIC_CARE_FOLLOWUP"));
		new SetupAppointmentAdherence(new ReportHelper(), "adcc", "Chronic Care", null, l, false).delete();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_chroniccareappadherence.form")
	public String registerChronicCareAppAdherence() throws Exception {
		List<EncounterType> l = Arrays.asList(Context.getEncounterService().getEncounterType("CHRONIC_CARE_FOLLOWUP"));
		new SetupAppointmentAdherence(new ReportHelper(), "adcc", "Chronic Care", null, l, false).setup();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/remove_artappadherence.form")
	public String removeArtAppAdherence() {
		new SetupAppointmentAdherence(new ReportHelper(), "adart", "ART", Context
				.getProgramWorkflowService().getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"), Arrays.asList(Context
				.getEncounterService().getEncounterType("ART_INITIAL"), Context
				.getEncounterService().getEncounterType("ART_FOLLOWUP")), false)
				.delete();
		return "redirect:/module/pihmalawi/registerReports.form";
	}

	@RequestMapping("/module/pihmalawi/register_artappadherence.form")
	public String registerArtAppAdherence() throws Exception {
		new SetupAppointmentAdherence(new ReportHelper(), "adart", "ART", Context
				.getProgramWorkflowService().getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"), Arrays.asList(Context
				.getEncounterService().getEncounterType("ART_INITIAL"), Context
				.getEncounterService().getEncounterType("ART_FOLLOWUP")), false)
				.setup();
		return "redirect:/module/pihmalawi/registerReports.form";
	}
}
