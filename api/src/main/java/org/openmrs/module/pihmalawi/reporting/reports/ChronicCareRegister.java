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
import org.openmrs.Obs;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.CollectionElementConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.EarliestCreatedConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
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
		dsd.addSortCriteria("Chronic Care #", SortDirection.ASC);

		rd.addDataSetDefinition("patients", Mapped.mapStraightThrough(dsd));

		// Rows are defined as all patients who have ever had a Chronic Care Encounter at the location by the end date
		CohortDefinition haveChronicCareEncounter = df.getAnyEncounterOfTypesAtLocationByEndDate(metadata.getChronicCareEncounterTypes());
		dsd.addRowFilter(Mapped.mapStraightThrough(haveChronicCareEncounter));

		// Columns to include

		addColumn(dsd, "PID", builtInPatientData.getPatientId());
		addColumn(dsd, "Chronic Care #", ccPatientData.getChronicCareNumberAtLocation());
        addColumn(dsd, "HCC #", hivPatientData.getHccNumberAtLocation());
        addColumn(dsd, "ARV #", hivPatientData.getArvNumberAtLocation());
		addColumn(dsd, "Chronic Care initial date", df.getFirstEncounterOfTypeByEndDate(metadata.getChronicCareInitialEncounterTypes(), df.getEncounterDatetimeConverter()));
		addColumn(dsd, "Chronic Care initial location", df.getFirstEncounterOfTypeByEndDate(metadata.getChronicCareInitialEncounterTypes(), df.getEncounterLocationNameConverter()));
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
        addColumn(dsd, "HIV Outcome", hivPatientData.getMostRecentHivTreatmentStatusStateAtLocationByEndDate());
        addColumn(dsd, "HIV Outcome change date", hivPatientData.getMostRecentHivTreatmentStatusStateStartDateAtLocationByEndDate());
        addColumn(dsd, "HIV Outcome location", hivPatientData.getMostRecentHivTreatmentStatusStateLocationAtLocationByEndDate());
        addColumn(dsd, "Last ART Visit Date", hivPatientData.getMostRecentArtEncounterDateByEndDate());

		for (Concept diagnosis : metadata.getChronicCareDiagnosisAnswerConcepts()) {
			String columnName = ObjectUtil.format(diagnosis) + " Diagnosis from Initial Visit";
            Concept question = metadata.getChronicCareDiagnosisConcept();
            List<EncounterType> ccInitialEncounters = metadata.getChronicCareInitialEncounterTypes();
            ChainedConverter c = new ChainedConverter();
            c.addConverter(new CollectionConverter(df.getObsValueCodedConverter(), true, null));
            c.addConverter(new CollectionElementConverter(diagnosis, "TRUE", ""));;
			addColumn(dsd, columnName, df.getAllObsByEndDate(question, ccInitialEncounters, c));
		}

		for (Concept c : metadata.getAgeOfDiagnosisConcepts()) {
			String columnName = ObjectUtil.format(c) + " from Initial Visit";
			DataConverter dc = new ChainedConverter(new EarliestCreatedConverter(Obs.class), df.getObsValueNumericConverter());
			addColumn(dsd, columnName, df.getAllObsByEndDate(c, metadata.getChronicCareInitialEncounterTypes(), dc));
		}

		addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());

		EncounterDataSetDefinition encDsd = new EncounterDataSetDefinition();
		encDsd.addParameters(getParameters());

        BasicEncounterQuery ccFollowupQuery = new BasicEncounterQuery();
        ccFollowupQuery.setEncounterTypes(metadata.getChronicCareFollowupEncounterTypes());
        ccFollowupQuery.addParameter(new Parameter("onOrBefore", "On or before", Date.class));

		encDsd.addRowFilter(Mapped.map(ccFollowupQuery, "onOrBefore=${endDate}"));
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
            DataConverter dc = new ChainedConverter(new CollectionConverter(converter, true, null), new ObjectFormatter());
			addColumn(dsd, columnName, df.getAllObsByEndDate(c, metadata.getChronicCareInitialEncounterTypes(), dc));
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
}
