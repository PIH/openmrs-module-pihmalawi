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
public class ArtMissedAppointmentReport extends BaseReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "96b3abec-3d5a-4d46-80a4-777e60ec92e6";

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

	public ArtMissedAppointmentReport() {}

	@Override
	public String getUuid() {
		return "08c63059-9017-4767-8bf3-c16d0ace92a8";
	}

	@Override
	public String getName() {
		return "ART Missed Appointment";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(df.getEndDateParameter());
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());



		return rd;
	}

	public PatientDataSetDefinition createBasePatientDataSetDefinition() {
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.addParameters(getParameters());
		dsd.addSortCriteria("ARV #", SortDirection.ASC);

		addColumn(dsd, "All ARV #s", hivPatientData.getAllArvNumbers());
		addColumn(dsd, "Given name", builtInPatientData.getPreferredGivenName());
		addColumn(dsd, "Last name", builtInPatientData.getPreferredFamilyName());
		addColumn(dsd, "Birthdate", basePatientData.getBirthdate());
		addColumn(dsd, "Current Age (yr)", basePatientData.getAgeAtEndInYears());
		addColumn(dsd, "Current Age (mth)", basePatientData.getAgeAtEndInMonths());
		addColumn(dsd, "M/F", builtInPatientData.getGender());
		addColumn(dsd, "Village", basePatientData.getVillage());
		addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
		addColumn(dsd, "District", basePatientData.getDistrict());
		addColumn(dsd, "Outcome", hivPatientData.getMostRecentHivTreatmentStatusStateByEndDate());
		addColumn(dsd, "Outcome change date", hivPatientData.getMostRecentHivTreatmentStatusStateStartDateByEndDate());
		addColumn(dsd, "Outcome location", hivPatientData.getMostRecentHivTreatmentStatusStateLocationByEndDate());
		addColumn(dsd, "Last Outcome in DB (not filtered)", hivPatientData.getMostRecentHivTreatmentStatusStateByEndDate());
		addColumn(dsd, "Last Outcome change date", hivPatientData.getMostRecentHivTreatmentStatusStateStartDateByEndDate());
		addColumn(dsd, "Last Outcome change loc", hivPatientData.getMostRecentHivTreatmentStatusStateLocationByEndDate());
		addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());

		EncounterDataSetDefinition encDsd = new EncounterDataSetDefinition();
		encDsd.addParameters(getParameters());
		encDsd.addRowFilter(Mapped.mapStraightThrough(encounterQueries.getArtFollowupEncountersByEndDate()));
		addColumn(encDsd, "encounterDate", builtInEncounterData.getEncounterDatetime());
		addColumn(encDsd, "locationName", builtInEncounterData.getLocationName());
		addColumn(encDsd, "appointmentDate", baseEncounterData.getNextAppointmentDateObsValue());
		addColumn(encDsd, "encounterTypeName", builtInEncounterData.getEncounterTypeName());
		encDsd.addSortCriteria("encounterDate", SortDirection.ASC);

		PatientDataSetDataDefinition followups = new PatientDataSetDataDefinition(encDsd);
		addColumn(dsd, "Last Visit date in ART (not filtered)", df.convert(followups, df.getLastDataSetItemConverter("encounterDate", "(no encounter found)")));
		addColumn(dsd, "Last Visit loc", df.convert(followups, df.getLastDataSetItemConverter("locationName", "")));
		addColumn(dsd, "Last Visit appt date", df.convert(followups, df.getLastDataSetItemConverter("appointmentDate", "")));
		addColumn(dsd, "Last Visit type", df.convert(followups, df.getLastDataSetItemConverter("encounterTypeName", "")));

		addColumn(dsd, "Confirmed Missed Appt", null);
		addColumn(dsd, "Unable to Verify", null);
		addColumn(dsd, "Missed Data Entry", null);

		return dsd;
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
