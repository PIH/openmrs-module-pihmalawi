package org.openmrs.module.pihmalawi.reporting.reports;


import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
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
public class ChronicCareVisitReport extends ApzuDataExportManager{
    public static final String MONTHLY_SCHEDULED_REQUEST_UUID = "3B95927F-AEDF-486B-9288-5E6058BAE9F4";

    @Autowired
    private DataFactory df;

    @Autowired
    private ChronicCareMetadata metadata;

    @Autowired
    private BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    private BaseEncounterDataLibrary baseEncounterData;

    @Autowired
    private BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    private BasePatientDataLibrary basePatientData;

    @Autowired
    private HivPatientDataLibrary hivPatientData ;

    @Autowired
    private ChronicCarePatientDataLibrary ccPatientData ;

    public ChronicCareVisitReport() {}

    @Override
    public String getUuid() {
        return "9EDF1A4F-42DD-4865-8E9A-E375369F1DD9";
    }

    @Override
    public String getName() {
        return "NCD - Visits Report";
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

        addDataSet(rd, "oldccc_initial", metadata.getOldChronicCareInitialEncounterType());
        addDataSet(rd, "oldccc_followup", metadata.getOldChronicCareFollowupEncounterType());

        addDataSet(rd, "diabetes_initial", metadata.getHtnDiabetesInitialEncounterType());
        addDataSet(rd, "diabetes_followup", metadata.getHtnDiabetesFollowupEncounterType());
        addDataSet(rd, "diabetes_hospitalization", metadata.getHtnDiabetesHospitalizationsEncounterType());
        addDataSet(rd, "diabetes_tests", metadata.getHtnDiabetesTestsEncounterType());

        addDataSet(rd, "epilepsy_initial", metadata.getEpilepsyInitialEncounterType());
        addDataSet(rd, "epilepsy_followup", metadata.getEpilepsyFollowupEncounterType());

        addDataSet(rd, "asthma_initial", metadata.getAsthmaInitialEncounterType());
        addDataSet(rd, "asthma_followup", metadata.getAsthmaFollowupEncounterType());
        addDataSet(rd, "asthma_peakflow", metadata.getAsthmaPeakFlowEncounterType());
        addDataSet(rd, "asthma_hospitalization", metadata.getAsthmaHospitalizationsEncounterType());

        addDataSet(rd, "mentalhealth_initial", metadata.getMentalHealthInitialEncounterType());
        addDataSet(rd, "mentalhealth_followup", metadata.getMentalHealthFollowupEncounterType());

        return rd;
    }

    protected void addDataSet(ReportDefinition rd, String key, EncounterType encounterType) {
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

        dsd.addColumn("ENCOUNTER_TYPE", builtInEncounterData.getEncounterTypeName(), "");
        dsd.addColumn("CHRONIC_CARE_NUMBER", ccPatientData.getChronicCareNumberAtLocation(), "");
        dsd.addColumn("HCC_NUMBER", hivPatientData.getHccNumberAtLocation(), "");
        dsd.addColumn("ARV_NUMBER", hivPatientData.getArvNumberAtLocation(), "");
        dsd.addColumn("PID", builtInPatientData.getPatientId(), "");
        dsd.addColumn("FIRST_NAME", builtInPatientData.getPreferredGivenName(), "");
        dsd.addColumn("LAST_NAME", builtInPatientData.getPreferredFamilyName(), "");
        dsd.addColumn("BIRTHDATE", basePatientData.getBirthdate(), "");
        dsd.addColumn("AGE_AT_VISIT_YRS", baseEncounterData.getAgeAtEncounterDateInYears(), "");
        dsd.addColumn("AGE_AT_VISIT_MTHS", baseEncounterData.getAgeAtEncounterDateInMonths(), "");
        dsd.addColumn("M/F", builtInPatientData.getGender(), "");
        dsd.addColumn("VILLAGE", basePatientData.getVillage(), "");
        dsd.addColumn("TA", basePatientData.getTraditionalAuthority(), "");
        dsd.addColumn("DISTRICT", basePatientData.getDistrict(), "");

        rd.addDataSetDefinition(key, Mapped.mapStraightThrough(dsd));
    }

    @Override
    public String getExcelDesignUuid() {
        return "4C7E22A7-84B3-412F-B4F6-778D2C573F2C";
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
