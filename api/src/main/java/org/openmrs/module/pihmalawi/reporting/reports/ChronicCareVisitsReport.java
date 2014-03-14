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

import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
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
import java.util.Date;
import java.util.List;

//@Component // TODO: Work in progress
public class ChronicCareVisitsReport extends BaseReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "22420fee-e069-4682-bb0d-c39f65103fac";

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCareMetadata metadata;

	@Autowired
	private ChronicCareEncounterQueryLibrary encounterQueries;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData ;

	@Autowired
	private BasePatientDataLibrary basePatientData ;

	@Autowired
	private ChronicCarePatientDataLibrary ccPatientData ;

	@Autowired
	private BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	private BaseEncounterDataLibrary baseEncounterData;

	public ChronicCareVisitsReport() {}

	@Override
	public String getUuid() {
		return "d1378b66-5e6b-41c2-9db1-9ebbe4ddeaf7";
	}

	@Override
	public String getName() {
		return "Chronic Care Visits";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("whichVisits", "Which Visits", TimeQualifier.class, null, TimeQualifier.ANY, null));
		l.add(new Parameter("startDate", "From Date", Date.class));
		l.add(new Parameter("endDate", "To Date", Date.class));
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

		EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
		dsd.setName(getName());
		dsd.addParameters(getParameters());
		dsd.addSortCriteria("ENCOUNTER_DATETIME", SortDirection.ASC);

		rd.addDataSetDefinition("visits", Mapped.mapStraightThrough(dsd));

		// Row filters



		// Columns to include

		PatientDataSetDefinition pdsd = new PatientDataSetDefinition();
		addColumn(pdsd, "PID", builtInPatientData.getPatientId());
		addColumn(pdsd, "Chronic Care #", ccPatientData.getChronicCareNumberAtLocation());
		addColumn(pdsd, "Chronic Care initial date", ccPatientData.getFirstChronicCareInitialEncounterDateByEndDate());
		addColumn(pdsd, "Chronic Care initial location", ccPatientData.getFirstChronicCareInitialEncounterLocationByEndDate());
		addColumn(pdsd, "Given name", builtInPatientData.getPreferredGivenName());
		addColumn(pdsd, "Last name", builtInPatientData.getPreferredFamilyName());
		addColumn(pdsd, "Birthdate", basePatientData.getBirthdate());
		addColumn(pdsd, "Current Age (yr)", basePatientData.getAgeAtEndInYears());
		addColumn(pdsd, "Current Age (mth)", basePatientData.getAgeAtEndInMonths());
		addColumn(pdsd, "M/F", builtInPatientData.getGender());
		addColumn(pdsd, "Village", basePatientData.getVillage());
		addColumn(pdsd, "TA", basePatientData.getTraditionalAuthority());
		addColumn(pdsd, "District", basePatientData.getDistrict());
		addColumn(pdsd, "Outcome", ccPatientData.getMostRecentChronicCareTreatmentStatusStateAtLocationByEndDate());
		addColumn(pdsd, "Outcome change date", ccPatientData.getMostRecentChronicCareTreatmentStatusStateStartDateAtLocationByEndDate());
		addColumn(pdsd, "Outcome location", ccPatientData.getMostRecentChronicCareTreatmentStatusStateLocationAtLocationByEndDate());

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
