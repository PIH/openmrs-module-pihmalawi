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

import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.pihmalawi.metadata.group.ArtTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.ChronicCareTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.HccTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentReport extends ApzuDataExportManager {

    @Autowired
    private DataFactory df;

    @Autowired
    private ArtTreatmentGroup artTreatmentGroup;

    @Autowired
    private ChronicCareTreatmentGroup chronicCareTreatmentGroup;

    @Autowired
    private HccTreatmentGroup hccTreatmentGroup;


    @Autowired
    private CommonMetadata commonMetadata;

    @Autowired
    private HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    private HivEncounterQueryLibrary encounterQueries;

    @Autowired
    private BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    private BasePatientDataLibrary basePatientData;

    @Autowired
    private HivPatientDataLibrary hivPatientData;

    @Autowired
    private ChronicCarePatientDataLibrary chronicCarePatientData;

    @Autowired
    private BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    private BaseEncounterDataLibrary baseEncounterData;

    public AppointmentReport() {}

    @Override
    public String getUuid() {
        return "F29C2955-C278-4F24-AADB-BE89D55542E1";
    }

    @Override
    public String getName() {
        return "Appointment Report";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        l.add(df.getStartDateParameter());
        l.add(df.getEndDateParameter());
        l.add(df.getRequiredLocationParameter());
        return l;
    }

    public List<TreatmentGroup> getTreatmentGroups(){

        List<TreatmentGroup> treatmentGroups = new ArrayList<TreatmentGroup>();
        treatmentGroups.add(hccTreatmentGroup);
        treatmentGroups.add(artTreatmentGroup);
        treatmentGroups.add(chronicCareTreatmentGroup);

        return treatmentGroups;
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
        dsd.setParameters(getParameters());
        rd.addDataSetDefinition(getName(), Mapped.mapStraightThrough(dsd));

        dsd.addSortCriteria("PID", SortDirection.ASC);

        // Rows are patients who have a next appointment date obs date in the given date range, associated with the given location
        CohortDefinition rowFilter = df.getPatientsWhoseObsValueDateIsBetweenStartDateAndEndDateAtLocation(commonMetadata.getAppointmentDateConcept(), null);
        dsd.addRowFilter(Mapped.mapStraightThrough(rowFilter));

        // Columns to include

        addColumn(dsd, "PID", builtInPatientData.getPatientId());
        addColumn(dsd, "HCC Number", hivPatientData.getHccNumberAtLocation());
        addColumn(dsd, "ARV Number", hivPatientData.getArvNumberAtLocation());
        addColumn(dsd, "Chronic Care Number", chronicCarePatientData.getChronicCareNumberAtLocation());
        addColumn(dsd, "Given name", builtInPatientData.getPreferredGivenName());
        addColumn(dsd, "Last name", builtInPatientData.getPreferredFamilyName());
        addColumn(dsd, "M/F", builtInPatientData.getGender());
        addColumn(dsd, "Birthdate", basePatientData.getBirthdate());
        addColumn(dsd, "Current Age (yr)", basePatientData.getAgeAtEndInYears());
        addColumn(dsd, "Current Age (mth)", basePatientData.getAgeAtEndInMonths());
        addColumn(dsd, "Village", basePatientData.getVillage());
        addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
        addColumn(dsd, "District", basePatientData.getDistrict());
        addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());
        addColumn(dsd, "Appointment Date", basePatientData.getAppointmentDatesAtLocationDuringPeriod());

        return rd;
    }

    @Override
    public String getExcelDesignUuid() {
        return "27ED6505-AC31-4D13-AEAC-8D06143BB348";
    }

    @Override
    public String getVersion() {
        return "1.0-SNAPSHOT";
    }
}
