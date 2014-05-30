/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArtRegister extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "cea86583-9ca5-4ad9-94e4-e20081a57619";

	@Autowired
	private DataFactory df;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	private HivEncounterQueryLibrary encounterQueries;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData ;

	@Autowired
	private BasePatientDataLibrary basePatientData ;

	@Autowired
	private HivPatientDataLibrary hivPatientData ;

	@Autowired
	private BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	private BaseEncounterDataLibrary baseEncounterData;

	public ArtRegister() {}

	@Override
	public String getUuid() {
		return "fa20c1ac-94ea-11e3-96de-0023156365e4";
	}

	@Override
	public String getName() {
		return "ART Register";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(df.getEndDateParameter());
		l.add(df.getLocationParameter());
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("ART Register");
		dsd.addParameters(getParameters());
		dsd.addSortCriteria("ARV #", SortDirection.ASC);

		rd.addDataSetDefinition("patients", Mapped.mapStraightThrough(dsd));

		// Rows are defined as all patients who ever have been in the On Antiretrovirals state at the given location
		CohortDefinition everEnrolled = hivCohorts.getEverEnrolledInArtAtLocationByEndDate();
		dsd.addRowFilter(Mapped.mapStraightThrough(everEnrolled));

		addColumn(dsd, "PID", builtInPatientData.getPatientId());
		addColumn(dsd, "ARV #", hivPatientData.getArvNumberAtLocation());
		addColumn(dsd, "All HCC #s (not filtered)", hivPatientData.getAllHccNumbers());
		addColumn(dsd, "All ARV #s (not filtered)", hivPatientData.getAllArvNumbers());

		addColumn(dsd, "ART initial date", hivPatientData.getFirstArtInitialEncounterDateByEndDate());
		addColumn(dsd, "ART initial location", hivPatientData.getFirstArtInitialEncounterLocationByEndDate());

		addColumn(dsd, "Given name", builtInPatientData.getPreferredGivenName());
		addColumn(dsd, "Last name", builtInPatientData.getPreferredFamilyName());
		addColumn(dsd, "Birthdate", basePatientData.getBirthdate());
		addColumn(dsd, "Current Age (yr)", basePatientData.getAgeAtEndInYears());
		addColumn(dsd, "Current Age (mth)", basePatientData.getAgeAtEndInMonths());
		addColumn(dsd, "M/F", builtInPatientData.getGender());
		addColumn(dsd, "Village", basePatientData.getVillage());
		addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
		addColumn(dsd, "District", basePatientData.getDistrict());

		addColumn(dsd, "Outcome", hivPatientData.getMostRecentHivTreatmentStatusStateAtLocationByEndDate());
		addColumn(dsd, "Outcome change date", hivPatientData.getMostRecentHivTreatmentStatusStateStartDateAtLocationByEndDate());
		addColumn(dsd, "Outcome location", hivPatientData.getMostRecentHivTreatmentStatusStateLocationAtLocationByEndDate());

		addColumn(dsd, "Last Outcome in DB (not filtered)", hivPatientData.getMostRecentHivTreatmentStatusStateByEndDate());
		addColumn(dsd, "Last Outcome change date", hivPatientData.getMostRecentHivTreatmentStatusStateStartDateByEndDate());
		addColumn(dsd, "Last Outcome change loc", hivPatientData.getMostRecentHivTreatmentStatusStateLocationByEndDate());

		addColumn(dsd, "Enrollment date at location (ART or HCC) (not filtered)", hivPatientData.getEarliestOnArvsStateAtLocationByEndDate());
		addColumn(dsd, "1st time enrollment (ART or HCC) (not filtered)", hivPatientData.getEarliestOnArvsStateEnrollmentDateByEndDate());
		addColumn(dsd, "1st time enrollment (ART or HCC) (not filtered) location", hivPatientData.getEarliestOnArvsStateLocationByEndDate());
		addColumn(dsd, "1st time in Pre-ART date", hivPatientData.getEarliestPreArtStateStartDateByEndDate());
		addColumn(dsd, "1st time in Pre-ART location", hivPatientData.getEarliestPreArtStateLocationByEndDate());
		addColumn(dsd, "1st time in Exposed Child date", hivPatientData.getEarliestExposedChildStateStartDateByEndDate());
		addColumn(dsd, "1st time in Exposed Child location", hivPatientData.getEarliestExposedChildStateLocationByEndDate());
		addColumn(dsd, "1st time in ART date", hivPatientData.getEarliestOnArvsStateStartDateByEndDate());
		addColumn(dsd, "1st time in ART location", hivPatientData.getEarliestOnArvsStateLocationByEndDate());

		addColumn(dsd, "ARV start reasons", hivPatientData.getFirstArtInitialReasonForStartingArvs());
		addColumn(dsd, "Last Date of starting first line antiretroviral regimen", hivPatientData.getLatestFirstLineArvStartDateByEndDate());
		addColumn(dsd, "Last CD4 count", hivPatientData.getLatestCd4CountValueByEndDate());
		addColumn(dsd, "Last CD4 count Date", hivPatientData.getLatestCd4CountDateByEndDate());

		addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());

		// Add encounter details for 1st, 2nd, 3rd, and most recent hiv follow-up encounters
		EncounterDataSetDefinition encDsd = new EncounterDataSetDefinition();
		encDsd.addParameters(getParameters());
		encDsd.addRowFilter(Mapped.mapStraightThrough(encounterQueries.getHivFollowupEncountersByEndDate()));
		addColumn(encDsd, "encounterDate", builtInEncounterData.getEncounterDatetime());
		addColumn(encDsd, "locationName", builtInEncounterData.getLocationName());
		addColumn(encDsd, "appointmentDate", baseEncounterData.getNextAppointmentDateObsValue());
		addColumn(encDsd, "encounterTypeName", builtInEncounterData.getEncounterTypeName());
		encDsd.addSortCriteria("encounterDate", SortCriteria.SortDirection.ASC);

		PatientDataSetDataDefinition followups = new PatientDataSetDataDefinition(encDsd);

		for (int i=0; i<3; i++) {
			String prefix = "Visit # " + (i+1);
			addColumn(dsd, prefix + " date in HIV (not filtered)", df.convert(followups, df.getDataSetItemConverter(i, "encounterDate", "(no encounter found)")));
			addColumn(dsd, prefix + " loc", df.convert(followups, df.getDataSetItemConverter(i, "locationName", "")));
			addColumn(dsd, prefix + " appt date", df.convert(followups, df.getDataSetItemConverter(i, "appointmentDate", "")));
			addColumn(dsd, prefix + " type", df.convert(followups, df.getDataSetItemConverter(i, "encounterTypeName", "")));
		}
		addColumn(dsd, "Last Visit date in HIV (not filtered)", df.convert(followups, df.getLastDataSetItemConverter("encounterDate", "(no encounter found)")));
		addColumn(dsd, "Last Visit loc", df.convert(followups, df.getLastDataSetItemConverter("locationName", "")));
		addColumn(dsd, "Last Visit appt date", df.convert(followups, df.getLastDataSetItemConverter("appointmentDate", "")));
		addColumn(dsd, "Last Visit type", df.convert(followups, df.getLastDataSetItemConverter("encounterTypeName", "")));

		addColumn(dsd, "Last Malawi Antiretroviral drugs received", hivPatientData.getLatestArvDrugsReceivedByEndDate());
		addColumn(dsd, "Last Malawi Antiretroviral drugs received Date", hivPatientData.getLatestArvDrugsReceivedDateByEndDate());
		addColumn(dsd, "Last TB status", hivPatientData.getLatestTbStatusByEndDate());
		addColumn(dsd, "Last TB status Date", hivPatientData.getLatestTbStatusDateByEndDate());
		addColumn(dsd, "Last Malawi ART side effects", hivPatientData.getLatestArtSideEffectsByEndDate());
		addColumn(dsd, "Last Malawi ART side effects Date", hivPatientData.getLatestArtSideEffectsDateByEndDate());

		addColumn(dsd, "All Enrollments (not filtered)", df.getAllActiveStatesOnEndDate(df.getActiveStatesAsStringConverter()));

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(createExcelDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition));
		return l;
	}

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
