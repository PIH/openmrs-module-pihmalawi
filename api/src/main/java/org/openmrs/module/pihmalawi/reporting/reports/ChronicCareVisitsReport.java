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

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.ConditionalParameterEncounterQuery;
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
public class ChronicCareVisitsReport extends ApzuDataExportManager {

    public static final String MONTHLY_SCHEDULED_REQUEST_UUID = "8b70c61f-5c71-11e5-a151-e82aea237783";

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCareMetadata metadata;

	@Autowired
	private BaseEncounterQueryLibrary encounterQueries;

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
		l.add(new Parameter("startDate", "From Date", Date.class));
		l.add(new Parameter("endDate", "To Date", Date.class));
		l.add(df.getOptionalLocationParameter());
		l.add(new Parameter("latestOnly", "Include most recent visit only?", Boolean.class, null, Boolean.FALSE, null));
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		addDataSet(rd, "initial", metadata.getChronicCareInitialEncounterType());
		addDataSet(rd, "visits", metadata.getChronicCareFollowupEncounterType());

		return rd;
	}

	protected void addDataSet(ReportDefinition rd, String key, EncounterType encounterType) {

		EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
		dsd.setName(getName());
		dsd.addParameters(getParameters());
		dsd.addSortCriteria("ENCOUNTER_DATETIME", SortDirection.ASC);
		rd.addDataSetDefinition(key, Mapped.mapStraightThrough(dsd));

		// Row filters
		ConditionalParameterEncounterQuery filter = new ConditionalParameterEncounterQuery();
		filter.setParameters(getParameters());
		filter.setParameterToCheck("latestOnly");
		filter.addConditionalQuery(Boolean.TRUE, Mapped.mapStraightThrough(encounterQueries.getMostRecentEncountersAtLocationDuringPeriod(encounterType)));
		filter.setDefaultQuery(Mapped.mapStraightThrough(encounterQueries.getEncountersAtLocationDuringPeriod(encounterType)));
		dsd.addRowFilter(Mapped.mapStraightThrough(filter));

		// Columns to include

		boolean isInitial = "initial".equals(key);
		boolean isVisit = !isInitial;

		addColumn(dsd, "ENCOUNTER_ID", builtInEncounterData.getEncounterId());
		addColumn(dsd, "ENCOUNTER_DATETIME", builtInEncounterData.getEncounterDatetime());
		addColumn(dsd, "LOCATION", builtInEncounterData.getLocationName());
		addColumn(dsd, "ENCOUNTER_TYPE", builtInEncounterData.getEncounterTypeName());
		addColumn(dsd, "CHRONIC_CARE_NUMBER", ccPatientData.getChronicCareNumberAtLocation());
		addColumn(dsd, "PID", builtInPatientData.getPatientId());
		addColumn(dsd, "FIRST_NAME", builtInPatientData.getPreferredGivenName());
		addColumn(dsd, "LAST_NAME", builtInPatientData.getPreferredFamilyName());
		addColumn(dsd, "BIRTHDATE", basePatientData.getBirthdate());
		addColumn(dsd, "AGE_AT_VISIT_YRS", baseEncounterData.getAgeAtEncounterDateInYears());
		addColumn(dsd, "AGE_AT_VISIT_MTHS", baseEncounterData.getAgeAtEncounterDateInMonths());
		addColumn(dsd, "M/F", builtInPatientData.getGender());
		addColumn(dsd, "VILLAGE", basePatientData.getVillage());
		addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
		addColumn(dsd, "DISTRICT", basePatientData.getDistrict());

		if (isVisit) {
			Concept currentDrugsUsed = metadata.getCurrentDrugsUsedConcept();
			for (Concept medication : metadata.getChronicCareMedicationConcepts()) {
				String medicationName = ObjectUtil.format(medication);
				addColumn(dsd, "Taking " + medicationName, df.getObsValueCodedPresentInEncounter(currentDrugsUsed, medication));
			}

			addColumn(dsd, "PREFERRED TREATMENT UNAVAILABLE", df.getSingleObsValueCodedNameForEncounter(metadata.getPreferredTreatmentOutOfStockConcept()));
			addColumn(dsd, "CHANGE IN TREATMENT", df.getSingleObsValueCodedNameForEncounter(metadata.getChangeInTreatmentConcept()));
			addColumn(dsd, "HOSPITALIZED SINCE LAST VISIT", df.getSingleObsValueCodedNameForEncounter(metadata.getHospitalizedSinceLastVisitConcept()));
			addColumn(dsd, "HOSPITALIZED FOR NCD SINCE LAST VISIT", df.getSingleObsValueCodedNameForEncounter(metadata.getHospitalizedForNcdSinceLastVisitConcept()));
		}

		Concept chronicCareDiagnosis = metadata.getChronicCareDiagnosisConcept();
		for (Concept diagnosis : metadata.getChronicCareDiagnosisAnswerConcepts()) {
			String diagnosisName = ObjectUtil.format(diagnosis);
			addColumn(dsd, diagnosisName + " Diagnosis", df.getObsValueCodedPresentInEncounter(chronicCareDiagnosis, diagnosis));
		}

		if (isVisit) {
			addColumn(dsd, "HT", df.getSingleObsValueNumericForEncounter(metadata.getHeightConcept()));
			addColumn(dsd, "WT", df.getSingleObsValueNumericForEncounter(metadata.getWeightConcept()));
			addColumn(dsd, "SBP", df.getSingleObsValueNumericForEncounter(metadata.getSystolicBloodPressureConcept()));
			addColumn(dsd, "DBP", df.getSingleObsValueNumericForEncounter(metadata.getDiastolicBloodPressureConcept()));
			addColumn(dsd, "CHF CLASSIFICATION", df.getSingleObsValueCodedNameForEncounter(metadata.getNyhaClassConcept()));
			addColumn(dsd, "BLOOD SUGAR", df.getSingleObsValueNumericForEncounter(metadata.getSerumGlucoseConcept()));
			addColumn(dsd, "BLOOD SUGAR TEST TYPE", df.getSingleObsValueCodedNameForEncounter(metadata.getBloodSugarTestTypeConcept()));
			addColumn(dsd, "NUMBER OF SEIZURES", df.getSingleObsValueNumericForEncounter(metadata.getNumberOfSeizuresConcept()));
			addColumn(dsd, "PEAK FLOW", df.getSingleObsValueNumericForEncounter(metadata.getPeakFlowConcept()));
			addColumn(dsd, "PEAK FLOW PREDICTED", df.getSingleObsValueNumericForEncounter(metadata.getPeakFlowPredictedConcept()));
			addColumn(dsd, "ASTHMA CLASSIFICATION", df.getSingleObsValueCodedNameForEncounter(metadata.getAsthmaClassificationConcept()));
			addColumn(dsd, "HIGH RISK PATIENT", df.getSingleObsValueCodedNameForEncounter(metadata.getHighRiskPatientConcept()));
			addColumn(dsd, "VISIT FULLY COMPLETED", df.getSingleObsValueCodedNameForEncounter(metadata.getPatientVisitCompletedWithAllServicesConcept()));
			addColumn(dsd, "DATA CLERK COMMENTS", df.getSingleObsValueTextForEncounter(metadata.getDataClerkCommentsConcept()));
			addColumn(dsd, "NEXT APPOINTMENT DATE", df.getSingleObsValueDatetimeForEncounter(metadata.getAppointmentDateConcept()));
		}

		if (isInitial) {
			addColumn(dsd, "SOURCE OF REFERRAL", df.getSingleObsValueCodedNameForEncounter(metadata.getSourceOfReferralConcept()));
		}
	}

    @Override
    public String getExcelDesignUuid() {
        return "22420fee-e069-4682-bb0d-c39f65103fac";
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

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
