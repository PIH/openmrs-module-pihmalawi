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
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.converter.DataConverter;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChronicCareRegister extends ApzuDataExportManager {

    public static final String MONTHLY_SCHEDULED_REQUEST_UUID = "8ad333aa-5c71-11e5-a151-e82aea237783";

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCareMetadata metadata;

	@Autowired
	private ChronicCareCohortDefinitionLibrary chronicCareCohorts;

	@Autowired
	private ChronicCareEncounterQueryLibrary encounterQueries;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData ;

	@Autowired
	private BasePatientDataLibrary basePatientData ;

    @Autowired
    private HivPatientDataLibrary hivPatientData;

	@Autowired
	private ChronicCarePatientDataLibrary ccPatientData ;

	@Autowired
	private BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	private BaseEncounterDataLibrary baseEncounterData;

	public ChronicCareRegister() {}

	@Override
	public String getUuid() {
		return "b68f0a4b-24f4-4cca-8095-99c195ccfdae";
	}

	@Override
	public String getName() {
		return "Chronic Care Register";
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
		dsd.addSortCriteria("PID", SortDirection.ASC);

		rd.addDataSetDefinition("patients", Mapped.mapStraightThrough(dsd));

		// Rows are defined as all patients who have ever had a Chronic Care Encounter at the location by the end date
		CohortDefinition haveChronicCareEncounter = chronicCareCohorts.getPatientsWithAChronicCareEncounterAtLocationByEndDate();
		dsd.addRowFilter(Mapped.mapStraightThrough(haveChronicCareEncounter));

		// Columns to include

		addColumn(dsd, "PID", builtInPatientData.getPatientId());
		addColumn(dsd, "Chronic Care #", ccPatientData.getChronicCareNumberAtLocation());
        addColumn(dsd, "HCC #", hivPatientData.getHccNumberAtLocation());
        addColumn(dsd, "ARV #", hivPatientData.getArvNumberAtLocation());
		addColumn(dsd, "Chronic Care initial date", ccPatientData.getFirstChronicCareInitialEncounterDateByEndDate());
		addColumn(dsd, "Chronic Care initial location", ccPatientData.getFirstChronicCareInitialEncounterLocationByEndDate());
        addColumn(dsd, "1st time in Pre-ART date", hivPatientData.getEarliestPreArtStateStartDateByEndDate());
        addColumn(dsd, "1st time in Pre-ART location", hivPatientData.getEarliestPreArtStateLocationByEndDate());
        addColumn(dsd, "1st time in Exposed Child date", hivPatientData.getEarliestExposedChildStateStartDateByEndDate());
        addColumn(dsd, "1st time in Exposed Child location", hivPatientData.getEarliestExposedChildStateLocationByEndDate());
        addColumn(dsd, "1st time in ART date", hivPatientData.getEarliestOnArvsStateStartDateByEndDate());
        addColumn(dsd, "1st time in ART location", hivPatientData.getEarliestOnArvsStateLocationByEndDate());
        addColumn(dsd, "Given name", builtInPatientData.getPreferredGivenName());
		addColumn(dsd, "Last name", builtInPatientData.getPreferredFamilyName());
		addColumn(dsd, "Birthdate", basePatientData.getBirthdate());
		addColumn(dsd, "Current Age (yr)", basePatientData.getAgeAtEndInYears());
		addColumn(dsd, "Current Age (mth)", basePatientData.getAgeAtEndInMonths());
		addColumn(dsd, "M/F", builtInPatientData.getGender());
		addColumn(dsd, "Village", basePatientData.getVillage());
		addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
		addColumn(dsd, "District", basePatientData.getDistrict());
		addColumn(dsd, "Outcome", ccPatientData.getMostRecentChronicCareTreatmentStatusStateAtLocationByEndDate());
		addColumn(dsd, "Outcome change date", ccPatientData.getMostRecentChronicCareTreatmentStatusStateStartDateAtLocationByEndDate());
		addColumn(dsd, "Outcome location", ccPatientData.getMostRecentChronicCareTreatmentStatusStateLocationAtLocationByEndDate());

		for (Concept c : metadata.getChronicCareDiagnosisAnswerConcepts()) {
			String columnName = ObjectUtil.format(c) + " Diagnosis from Initial Visit";
			addColumn(dsd, columnName, ccPatientData.getChronicCareInitialDiagnosisPresentByEndDate(c));
		}

		for (Concept c : metadata.getAgeOfDiagnosisConcepts()) {
			String columnName = ObjectUtil.format(c) + " from Initial Visit";
			DataConverter avc = df.getObsValueNumericConverter();
			addColumn(dsd, columnName, ccPatientData.getSingleObsFromChronicCareInitialVisitByEndDate(c, avc));
		}

		addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());

		EncounterDataSetDefinition encDsd = new EncounterDataSetDefinition();
		encDsd.addParameters(getParameters());
		encDsd.addRowFilter(Mapped.mapStraightThrough(encounterQueries.getChronicCareFollowupEncountersByEndDate()));
		addColumn(encDsd, "encounterDate", builtInEncounterData.getEncounterDatetime());
		addColumn(encDsd, "locationName", builtInEncounterData.getLocationName());
		addColumn(encDsd, "appointmentDate", baseEncounterData.getNextAppointmentDateObsValue());
		addColumn(encDsd, "encounterTypeName", builtInEncounterData.getEncounterTypeName());
		encDsd.addSortCriteria("encounterDate", SortCriteria.SortDirection.ASC);

		PatientDataSetDataDefinition followups = new PatientDataSetDataDefinition(encDsd);
		addColumn(dsd, "Last Visit date", df.convert(followups, df.getLastDataSetItemConverter("encounterDate", "(no encounter found)")));
		addColumn(dsd, "Last Visit loc", df.convert(followups, df.getLastDataSetItemConverter("locationName", "")));
		addColumn(dsd, "Last Visit appt date", df.convert(followups, df.getLastDataSetItemConverter("appointmentDate", "")));
		addColumn(dsd, "Last Visit type", df.convert(followups, df.getLastDataSetItemConverter("encounterTypeName", "")));

		addColumn(dsd, "All Enrollments (not filtered)", df.getAllActiveStatesOnEndDate(df.getActiveStatesAsStringConverter()));

		addColumn(dsd, "Last Height (cm)", basePatientData.getLatestHeight());
		addColumn(dsd, "Last Weight (kg)", basePatientData.getLatestWeight());
		addColumn(dsd, "Last Weight date", basePatientData.getLatestWeightDate());

		Map<Concept, DataConverter> questionsToShow = new LinkedHashMap<Concept, DataConverter>();
		questionsToShow.put(metadata.getHivStatusConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getOnArtConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getTbHistoryConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getClinicTravelTimeInHoursConcept(), df.getObsValueNumericConverter());
		questionsToShow.put(metadata.getClinicTravelTimeInMinutesConcept(), df.getObsValueNumericConverter());
		questionsToShow.put(metadata.getHighRiskPatientConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getWallMaterialConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getRoofMaterialConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getHomeElectricityConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getPatientOwnsRadioConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getAccessToBicycleConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getLocationOfCookingConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getFuelSourceConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getNumberOfFruitAndVegatablesConcept(), df.getObsValueNumericConverter());
		questionsToShow.put(metadata.getSmokingHistoryConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getNumberOfCigarettesPerDayConcept(), df.getObsValueNumericConverter());
		questionsToShow.put(metadata.getHistoryOfAlcoholUseConcept(), df.getObsValueCodedNameConverter());
		questionsToShow.put(metadata.getLitersOfAlcoholPerDayConcept(), df.getObsValueNumericConverter());
		questionsToShow.put(metadata.getSourceOfReferralConcept(), df.getObsValueCodedNameConverter());

		for (Concept c : questionsToShow.keySet()) {
			String columnName = ObjectUtil.format(c) + " at Initial Visit";
			DataConverter converter = questionsToShow.get(c);
			addColumn(dsd, columnName, ccPatientData.getSingleObsFromChronicCareInitialVisitByEndDate(c, converter));
		}

		return rd;
	}

    @Override
    public String getExcelDesignUuid() {
        return "5993f924-bd00-479e-b556-17ae77763bd1";
    }

    @Override
    public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
        List<ReportRequest> l = super.constructScheduledRequests(reportDefinition);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(df.getEndDateParameter().getName(), "${now}");
        l.add(createMonthlyScheduledReportRequest(MONTHLY_SCHEDULED_REQUEST_UUID, getExcelDesignUuid(), parameters, reportDefinition));
        return l;
    }

	@Override
	public String getVersion() {
		return "1.1-SNAPSHOT";
	}
}
