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

import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.HccCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HccRegister extends ApzuDataExportManager {

    public static final String MONTHLY_SCHEDULED_REQUEST_UUID = "8bc5d836-5c71-11e5-a151-e82aea237783";

	@Autowired
	private DataFactory df;

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

	public HccRegister() {}

	@Override
	public String getUuid() {
		return "eb6c9c1f-62f7-49fc-a8fd-748e77b9f806";
	}

	@Override
	public String getName() {
		return "HIV - HCC Register";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(df.getEndDateParameter());
		l.add(df.getOptionalLocationParameter());
		l.add(new Parameter("includeOldPreArtPatients", "Include Old Pre-ART Patients?", Boolean.class, null, Boolean.FALSE));
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
		dsd.setName(getName());
		dsd.addParameters(getParameters());
		dsd.addSortCriteria("HCC #", SortDirection.ASC);

		rd.addDataSetDefinition("patients", Mapped.mapStraightThrough(dsd));

		// Rows are defined as all patients who ever have been in PRE-ART or Exposed Child
		CohortDefinition hccCohort = new HccCohortDefinition();
		hccCohort.getParameters().addAll(getParameters());
		dsd.addRowFilter(Mapped.mapStraightThrough(hccCohort));

		// Columns to include

		addColumn(dsd, "PID", builtInPatientData.getPatientId());
		addColumn(dsd, "HCC #", hivPatientData.getHccNumberAtLocation());
		addColumn(dsd, "All HCC #s (not filtered)", hivPatientData.getAllHccNumbers());
		addColumn(dsd, "All ARV #s (not filtered)", hivPatientData.getAllArvNumbers());
		addColumn(dsd, "Pre-ART initial visit date", hivPatientData.getFirstPreArtInitialEncounterDateByEndDate());
		addColumn(dsd, "Pre-ART initial visit location", hivPatientData.getFirstPreArtInitialEncounterLocationByEndDate());
		addColumn(dsd, "Pre-ART state date", hivPatientData.getEarliestPreArtStateStartDateByEndDate());
		addColumn(dsd, "Pre-ART state location", hivPatientData.getEarliestPreArtStateLocationByEndDate());
		addColumn(dsd, "Exposed initial visit date", hivPatientData.getFirstExposedChildInitialEncounterDateByEndDate());
		addColumn(dsd, "Exposed initial visit location", hivPatientData.getFirstExposedChildInitialEncounterLocationByEndDate());
		addColumn(dsd, "Exposed state date", hivPatientData.getEarliestExposedChildStateStartDateByEndDate());
		addColumn(dsd, "Exposed state location", hivPatientData.getEarliestExposedChildStateLocationByEndDate());
		addColumn(dsd, "Given name", builtInPatientData.getPreferredGivenName());
		addColumn(dsd, "Last name", builtInPatientData.getPreferredFamilyName());
		addColumn(dsd, "Birthdate", basePatientData.getBirthdate());
		addColumn(dsd, "Current Age (yr)", basePatientData.getAgeAtEndInYears());
		addColumn(dsd, "Current Age (mth)", basePatientData.getAgeAtEndInMonths());
		addColumn(dsd, "M/F", builtInPatientData.getGender());
		addColumn(dsd, "Village", basePatientData.getVillage());
		addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
		addColumn(dsd, "District", basePatientData.getDistrict());
		addColumn(dsd, "Outcome in HCC", hivPatientData.getFirstStateAfterExposedChildOrPreArtStateAtLocationByEndDate());
		addColumn(dsd, "Outcome in HCC change date", hivPatientData.getFirstStateStartDateAfterExposedChildOrPreArtStateAtLocationByEndDate());
		addColumn(dsd, "Last Outcome in DB (not filtered)", hivPatientData.getMostRecentHivTreatmentStatusStateByEndDate());
		addColumn(dsd, "Last Outcome change date", hivPatientData.getMostRecentHivTreatmentStatusStateStartDateByEndDate());
		addColumn(dsd, "Last Outcome change loc", hivPatientData.getMostRecentHivTreatmentStatusStateLocationByEndDate());
		addColumn(dsd, "Mother ART Number", hivPatientData.getFirstEidInitialMotherArtNumber());
		addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());

		// Add encounter details for 1st, 2nd, 3rd, 4th, and most recent HIV encounters
		EncounterDataSetDefinition encDsd = new EncounterDataSetDefinition();
		encDsd.addParameters(getParameters());
		encDsd.addRowFilter(Mapped.mapStraightThrough(encounterQueries.getHivAndExposedChildEncountersByEndDate()));
		addColumn(encDsd, "encounterDate", builtInEncounterData.getEncounterDatetime());
		addColumn(encDsd, "locationName", builtInEncounterData.getLocationName());
		addColumn(encDsd, "appointmentDate", baseEncounterData.getNextAppointmentDateObsValue());
		addColumn(encDsd, "encounterTypeName", builtInEncounterData.getEncounterTypeName());
		encDsd.addSortCriteria("encounterDate", SortDirection.ASC);

		PatientDataSetDataDefinition followups = new PatientDataSetDataDefinition(encDsd);

		for (int i=0; i<4; i++) {
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

		addColumn(dsd, "Last CD4 count", hivPatientData.getLatestCd4CountValueByEndDate());
		addColumn(dsd, "Last CD4 count Date", hivPatientData.getLatestCd4CountDateByEndDate());
		addColumn(dsd, "Last TB status", hivPatientData.getLatestTbStatusByEndDate());
		addColumn(dsd, "Last TB status Date", hivPatientData.getLatestTbStatusDateByEndDate());
		addColumn(dsd, "Last Malawi ART side effects", hivPatientData.getLatestArtSideEffectsByEndDate());
		addColumn(dsd, "Last Malawi ART side effects Date", hivPatientData.getLatestArtSideEffectsDateByEndDate());
        addColumn(dsd, "Last Height (cm)", basePatientData.getLatestHeight());
        addColumn(dsd, "Last Weight (kg)", basePatientData.getLatestWeight());
        addColumn(dsd, "Last Weight date", basePatientData.getLatestWeightDate());
        
		addColumn(dsd, "All Enrollments (not filtered)", df.getAllActiveStatesOnEndDate(df.getActiveStatesAsStringConverter()));

		return rd;
	}

    @Override
    public String getExcelDesignUuid() {
        return "ae928860-4a4e-48d4-bbc2-50902babcfc0";
    }

    @Override
    public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
        List<ReportRequest> l = super.constructScheduledRequests(reportDefinition);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(df.getEndDateParameter().getName(), "${now}");
        parameters.put("includeOldPreArtPatients", Boolean.FALSE);
        l.add(createMonthlyScheduledReportRequest(MONTHLY_SCHEDULED_REQUEST_UUID, getExcelDesignUuid(), parameters, reportDefinition));
        return l;
    }
}
