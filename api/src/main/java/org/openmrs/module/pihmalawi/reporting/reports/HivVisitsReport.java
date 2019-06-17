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

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.IC3ScreeningMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HivVisitsReport extends ApzuDataExportManager {

    public static final String MONTHLY_SCHEDULED_REQUEST_UUID = "8c123e90-5c71-11e5-a151-e82aea237783";

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata metadata;

	@Autowired
	private IC3ScreeningMetadata screeningMetadata;

	@Autowired
	private BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	private BaseEncounterDataLibrary baseEncounterData;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData;

	@Autowired
	private BasePatientDataLibrary basePatientData;

	public HivVisitsReport() {}

	@Override
	public String getUuid() {
		return "6d97d62c-02d1-11e4-a73c-54ee7513a7ff";
	}

	@Override
	public String getName() {
		return "HIV - Visits Report";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("startDate", "From Date", Date.class));
		l.add(new Parameter("endDate", "To Date", Date.class));
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());
		addDataSet(rd, "art_initial", metadata.getArtInitialEncounterType(), metadata.getArvNumberIdentifierType());
		addDataSet(rd, "art_followup", metadata.getArtFollowupEncounterType(), metadata.getArvNumberIdentifierType());
		addDataSet(rd, "viral_load", screeningMetadata.getVLScreeningEncounterType(), metadata.getArvNumberIdentifierType());
		addDataSet(rd, "part_initial", metadata.getPreArtInitialEncounterType(), metadata.getHccNumberIdentifierType());
		addDataSet(rd, "part_followup", metadata.getPreArtFollowupEncounterType(), metadata.getHccNumberIdentifierType());
		addDataSet(rd, "exposed_child_initial", metadata.getExposedChildInitialEncounterType(), metadata.getHccNumberIdentifierType());
		addDataSet(rd, "exposed_child_followup", metadata.getExposedChildFollowupEncounterType(), metadata.getHccNumberIdentifierType());
		return rd;
	}

	protected void addDataSet(ReportDefinition rd, String key, EncounterType encounterType, PatientIdentifierType identifierType) {
		EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();
		dsd.setParameters(getParameters());

		BasicEncounterQuery rowFilter = new BasicEncounterQuery();
		rowFilter.addEncounterType(encounterType);
		rowFilter.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
		rowFilter.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
		MappedParametersEncounterQuery q = new MappedParametersEncounterQuery(rowFilter, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate"));
		dsd.addRowFilter(Mapped.mapStraightThrough(q));

		dsd.addColumn("ENCOUNTER_ID", builtInEncounterData.getEncounterId(), "");
		dsd.addColumn("ENCOUNTER_DATETIME", builtInEncounterData.getEncounterDatetime(), "");
		dsd.addColumn("LOCATION", builtInEncounterData.getLocationName(), "");
		dsd.addColumn("INTERNAL_PATIENT_ID", builtInEncounterData.getPatientId(), "");
		dsd.addColumn(identifierType.getName(), df.getAllIdentifiersOfType(identifierType, df.getIdentifierCollectionConverter()), "");
		dsd.addColumn("Birthdate", basePatientData.getBirthdate(), "");
		dsd.addColumn("Age at encounter (yr)", baseEncounterData.getAgeAtEncounterDateInYears(), "");
		dsd.addColumn("Age at encounter (mth)", baseEncounterData.getAgeAtEncounterDateInMonths(), "");
		dsd.addColumn("M/F", builtInPatientData.getGender(), "");
		dsd.addColumn("District", basePatientData.getDistrict(), "");
		dsd.addColumn("T/A", basePatientData.getTraditionalAuthority(), "");
		dsd.addColumn("Village", basePatientData.getVillage(), "");
		dsd.addColumn("IC3_WEIGHT", baseEncounterData.getWeightObsReferenceValue(), "");
		dsd.addColumn("IC3_HEIGHT", baseEncounterData.getWeightObsReferenceValue(), "");
		dsd.addColumn("IC3_SYSTOLIC_BP", baseEncounterData.getSystolicBPObsReferenceValue(), "");
		dsd.addColumn("IC3_DIASTOLIC_BP", baseEncounterData.getDiastolicBPObsReferenceValue(), "");
		dsd.addColumn("IC3_NEXT_APPOINTMENT_DATE", baseEncounterData.getNextAppointmentDateObsReferenceValue(), "");


		rd.addDataSetDefinition(key, Mapped.mapStraightThrough(dsd));
	}

    @Override
    public String getExcelDesignUuid() {
        return "6c5fcaf3-02d1-11e4-a73c-54ee7513a7ff";
    }

    @Override
    public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
        List<ReportRequest> l = super.constructScheduledRequests(reportDefinition);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(df.getStartDateParameter().getName(), "${start_of_last_month}");
        parameters.put(df.getEndDateParameter().getName(), "${end_of_last_month}");
        l.add(createMonthlyScheduledReportRequest(MONTHLY_SCHEDULED_REQUEST_UUID, getExcelDesignUuid(), parameters, reportDefinition));
        return l;
    }
}
