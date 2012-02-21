package org.openmrs.module.pihmalawi.web.controller;

import java.util.Arrays;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.pihmalawi.reports.experimental.historicAppointmentAdherence.SetupAppointmentAdherence;
import org.openmrs.module.pihmalawi.reports.setup.SetupAppointmentsForLocation;
import org.openmrs.module.pihmalawi.reports.setup.SetupArtMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupArtRegister;
import org.openmrs.module.pihmalawi.reports.setup.SetupArvQuarterly;
import org.openmrs.module.pihmalawi.reports.setup.SetupChronicCareMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupChronicCareRegister;
import org.openmrs.module.pihmalawi.reports.setup.SetupChronicCareVisits;
import org.openmrs.module.pihmalawi.reports.setup.SetupFindPatientsToMergeSoundex;
import org.openmrs.module.pihmalawi.reports.setup.SetupHccMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.SetupHccRegister;
import org.openmrs.module.pihmalawi.reports.setup.SetupHivDataQuality;
import org.openmrs.module.pihmalawi.reports.setup.SetupHivVisits;
import org.openmrs.module.pihmalawi.reports.setup.SetupTbRegister;
import org.openmrs.module.pihmalawi.reports.setup.SetupWeeklyEncounter;
import org.openmrs.module.pihmalawi.reports.setup.outdated.SetupHivWeeklyOutcome;
import org.openmrs.module.pihmalawi.reports.setup.outdated.SetupPreArtMissedAppointment;
import org.openmrs.module.pihmalawi.reports.setup.outdated.SetupPreArtWeekly;
import org.openmrs.module.pihmalawi.reports.setup.outdated.SetupProgramChanges;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PihReportFormController {

	@RequestMapping("/module/pihmalawi/remove_tb_register.form")
	public void removeTbRegister() {
		new SetupTbRegister(new Helper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_tb_register.form")
	public void registerTbRegister() throws Exception {
		new SetupTbRegister(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_cc_visits.form")
	public void removeCcVisits() {
		new SetupChronicCareVisits(new Helper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_cc_visits.form")
	public void registerCcVisits() throws Exception {
		new SetupChronicCareVisits(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_hiv_visits.form")
	public void removeHivVisits() {
		new SetupHivVisits(new Helper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_hiv_visits.form")
	public void registerHivVisits() throws Exception {
		new SetupHivVisits(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_hccmissedappointment_lowerneno.form")
	public void removeHccMissedAppointmentLowerNeno() {
		new SetupHccMissedAppointment(new Helper(), false)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_hccmissedappointment_lowerneno.form")
	public void registerHccMissedAppointmentLowerNeno() throws Exception {
		new SetupHccMissedAppointment(new Helper(), false).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_partmissedappointment_lowerneno.form")
	public void removePreArtMissedAppointmentLowerNeno() {
		new SetupPreArtMissedAppointment(new Helper(), false)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_partmissedappointment_lowerneno.form")
	public void registerPreArtMissedAppointmentLowerNeno() throws Exception {
		new SetupPreArtMissedAppointment(new Helper(), false).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_artmissedappointment_lowerneno.form")
	public void removeArtMissedAppointmentLowerNeno() {
		new SetupArtMissedAppointment(new Helper(), false)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_artmissedappointment_lowerneno.form")
	public void registerArtMissedAppointmentLowerNeno() throws Exception {
		new SetupArtMissedAppointment(new Helper(), false).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_partmissedappointment.form")
	public void removePreArtMissedAppointment() {
		new SetupPreArtMissedAppointment(new Helper(), true)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_partmissedappointment.form")
	public void registerPreArtMissedAppointment() throws Exception {
		new SetupPreArtMissedAppointment(new Helper(), true).setup(false);
	}

	@RequestMapping("/module/pihmalawi/register_hccmissedappointment.form")
	public void registerHccMissedAppointment() throws Exception {
		new SetupHccMissedAppointment(new Helper(), true).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_hccmissedappointment.form")
	public void removeHccMissedAppointment() {
		new SetupHccMissedAppointment(new Helper(), true)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/remove_hccregister.form")
	public void removeHccRegister() {
		new SetupHccRegister(new Helper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_hccregister.form")
	public void registerHccRegister() throws Exception {
		new SetupHccRegister(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_artregister.form")
	public void removeArtRegister() {
		new SetupArtRegister(new Helper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_artregister.form")
	public void registerArtRegister() throws Exception {
		new SetupArtRegister(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_artmissedappointment.form")
	public void removeArtMissedAppointment() {
		new SetupArtMissedAppointment(new Helper(), true)
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_artmissedappointment.form")
	public void registerArtMissedAppointment() throws Exception {
		new SetupArtMissedAppointment(new Helper(), true).setup(false);
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

	@RequestMapping("/module/pihmalawi/register_weeklyencounter.form")
	public void registerWeeklyEncounter() throws Exception {
		new SetupWeeklyEncounter(new Helper()).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_weeklyencounter.form")
	public void removeWeeklyEncounter() {
		new SetupWeeklyEncounter(new Helper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_hivprogramchanges.form")
	public void registerHivProgramChanges() throws Exception {
		new SetupProgramChanges(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_hivprogramchanges.form")
	public void removeHivProgramChanges() {
		new SetupProgramChanges(new Helper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_arvquarterly.form")
	public void registerArvQuarterly() throws Exception {
		new SetupArvQuarterly(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_arvquarterly.form")
	public void removeArvQuarterly() {
		new SetupArvQuarterly(new Helper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_hivdataquality.form")
	public void registerHivDataQuality() throws Exception {
		new SetupHivDataQuality(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_hivdataquality.form")
	public void removeHivDataQuality() {
		new SetupHivDataQuality(new Helper()).delete();
	}

	@RequestMapping("/module/pihmalawi/remove_findpatientstomerge.form")
	public void removeDuplicateHivPatients() {
		new SetupFindPatientsToMergeSoundex(new Helper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_findpatientstomerge.form")
	public void registerDuplicateHivPatients() throws Exception {
		new SetupFindPatientsToMergeSoundex(new Helper()).setup();
	}

	// Chronic Care
	@RequestMapping("/module/pihmalawi/remove_chroniccaremissedappointment.form")
	public void removeChronicCareMissedAppointment() {
		new SetupChronicCareMissedAppointment(new Helper())
				.deleteReportElements();
	}

	@RequestMapping("/module/pihmalawi/register_chroniccaremissedappointment.form")
	public void registerChronicCareMissedAppointment() throws Exception {
		new SetupChronicCareMissedAppointment(new Helper()).setup(false);
	}

	@RequestMapping("/module/pihmalawi/remove_chroniccareregister.form")
	public void removeChronicCareRegister() {
		new SetupChronicCareRegister(new Helper()).delete();
	}

	@RequestMapping("/module/pihmalawi/register_chroniccareregister.form")
	public void registerChronicCareRegister() throws Exception {
		new SetupChronicCareRegister(new Helper()).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_chroniccareappadherence.form")
	public void removeChronicCareAppAdherence() {
		new SetupAppointmentAdherence(new Helper(), "adcc", "Chronic Care", null,
				Arrays.asList(Context.getEncounterService().getEncounterType(
						"CHRONIC_CARE_FOLLOWUP")), false).delete();
	}

	@RequestMapping("/module/pihmalawi/register_chroniccareappadherence.form")
	public void registerChronicCareAppAdherence() throws Exception {
		new SetupAppointmentAdherence(new Helper(), "adcc", "Chronic Care", null,
				Arrays.asList(Context.getEncounterService().getEncounterType(
						"CHRONIC_CARE_FOLLOWUP")), false).setup();
	}

	@RequestMapping("/module/pihmalawi/remove_artappadherence.form")
	public void removeArtAppAdherence() {
		new SetupAppointmentAdherence(new Helper(), "adart", "ART", Context
				.getProgramWorkflowService().getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"), Arrays.asList(Context
				.getEncounterService().getEncounterType("ART_INITIAL"), Context
				.getEncounterService().getEncounterType("ART_FOLLOWUP")), false)
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_artappadherence.form")
	public void registerArtAppAdherence() throws Exception {
		new SetupAppointmentAdherence(new Helper(), "adart", "ART", Context
				.getProgramWorkflowService().getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"), Arrays.asList(Context
				.getEncounterService().getEncounterType("ART_INITIAL"), Context
				.getEncounterService().getEncounterType("ART_FOLLOWUP")), false)
				.setup();
	}

	@RequestMapping("/module/pihmalawi/remove_appointments.form")
	public void removeAppointments() {
		new SetupAppointmentsForLocation(new Helper())
				.delete();
	}

	@RequestMapping("/module/pihmalawi/register_appointments.form")
	public void registerAppointments() throws Exception {
		new SetupAppointmentsForLocation(new Helper())
				.setup();
	}

	@RequestMapping("/module/pihmalawi/remove_all.form")
	public void removeAll() {
		removeArtAppAdherence();
		removeArtMissedAppointment();
		removeArtMissedAppointmentLowerNeno();
		removeArvQuarterly();
		removeArtRegister();
		removeHccRegister();
		removeChronicCareAppAdherence();
		removeChronicCareMissedAppointment();
		removeChronicCareRegister();
		removeDuplicateHivPatients();
		removeHivDataQuality();
		removeHivProgramChanges();
		removeHivWeeklyOutcome();
		removePreArtMissedAppointment();
		removePreArtMissedAppointmentLowerNeno();
		removePreArtWeekly();
		removeWeeklyEncounter();
		removeAppointments();
	}

	@RequestMapping("/module/pihmalawi/register_all.form")
	public void registerAll() throws Exception {
//		registerArtAppAdherence();
		registerArtMissedAppointment();
//		registerArtMissedAppointmentLowerNeno();
		registerArvQuarterly();
		registerArtRegister();
		registerHccRegister();
//		registerChronicCareAppAdherence();
		registerChronicCareMissedAppointment();
		registerChronicCareRegister();
//		registerDuplicateHivPatients();
		registerHivDataQuality();
//		registerHivProgramChanges();
//		registerHivWeeklyOutcome();
		registerHccMissedAppointment();
//		registerPreArtMissedAppointment();
//		registerPreArtMissedAppointmentLowerNeno();
//		registerPreArtWeekly();
		registerWeeklyEncounter();
		registerAppointments();
	}

}
