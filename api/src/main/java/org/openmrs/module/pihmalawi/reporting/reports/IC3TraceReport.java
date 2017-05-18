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
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.LocationTags;
import org.openmrs.module.pihmalawi.metadata.group.HccTreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.definition.renderer.IC3TraceReportRenderer;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Deprecated
public class IC3TraceReport extends ApzuReportManager {

    public static final Parameter MIN_WEEKS_PARAM = new Parameter("minWeeks", "Min Weeks", Integer.class);
    public static final Parameter MAX_WEEKS_PARAM = new Parameter("maxWeeks", "Max Weeks", Integer.class);
    public static final Parameter PHASE_1_ONLY_PARAM = new Parameter("phase1Only", "Phase 1 Only", Boolean.class);

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

    @Autowired
    private ChronicCareMetadata ccMetadata;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    private ChronicCareCohortDefinitionLibrary ccCohorts;

    @Autowired
    private BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    private BasePatientDataLibrary basePatientData;

    @Autowired
    private HivPatientDataLibrary hivPatientData;

    @Autowired
    private ChronicCarePatientDataLibrary ccPatientData;

	@Autowired
	HccTreatmentGroup hccTreatmentGroup;

	@Override
	public String getUuid() {
		return "dd538832-4eae-11e6-b0fb-e82aea237783";
	}

	@Override
	public String getName() {
		return "Old IC3 TRACE Report";
	}

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        return l;
    }

	public String getReportDesignUuid() {
		return "e4320808-4eae-11e6-b0fb-e82aea237783";
	}

    /**
     * @see ApzuReportManager#constructReportDefinition()
     */
    @Override
    public ReportDefinition constructReportDefinition() {

        ReportDefinition rd = new ReportDefinition();
        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.setParameters(getParameters());

        List<Location> locations = hivMetadata.getHivStaticSystemLocations();

        // Group the location-specific datasets together for [2,6) and [6,12) weeks
        Integer[][] weekBoundarySet = { { 2,6 }, { 6,12 } };
        for (Location location : locations) {
            for (Integer[] bounds : weekBoundarySet) {
                Integer minWk = bounds[0];
                Integer maxWk = bounds[1];
                boolean isPhase1 = (minWk == 2 && location.hasTag(LocationTags.TRACE_PHASE_1_LOCATION.name()));
                PatientDataSetDefinition dsd = getPatientDataSet(minWk, maxWk, isPhase1);
                Map<String, Object> params = getDataSetMappings(location);
                addParameterAndValue(dsd, params, MIN_WEEKS_PARAM, minWk);
                addParameterAndValue(dsd, params, MAX_WEEKS_PARAM, maxWk);
                addParameterAndValue(dsd, params, PHASE_1_ONLY_PARAM, isPhase1);
                rd.addDataSetDefinition(location.getName() + " - " + minWk + " weeks", dsd, params);
            }
        }

        // Last, add a single dataset for all locations for 12+ weeks
        MultiParameterDataSetDefinition dsd12wk = new MultiParameterDataSetDefinition();
        dsd12wk.setBaseDefinition(getPatientDataSet(12, null, false));
        for (Location location : locations) {
            dsd12wk.addIteration(getDataSetMappings(location));
        }
        rd.addDataSetDefinition("12 weeks", Mapped.noMappings(dsd12wk));

        return rd;
    }

    /**
     * @return the patient data set for a given iteration
     */
    public PatientDataSetDefinition getPatientDataSet(Integer minWeeks, Integer maxWeeks, boolean phase1Only) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        dsd.addParameter(ReportingConstants.LOCATION_PARAMETER);

        dsd.addRowFilter(Mapped.mapStraightThrough(getMissedAppointmentCohort(minWeeks, maxWeeks, phase1Only)));

        addColumn(dsd, "ARV_NUMBER", hivPatientData.getArvNumberAtLocation());

        if (!phase1Only) {
            addColumn(dsd, "NCD_NUMBER", ccPatientData.getChronicCareNumberAtLocation());
        }

        addColumn(dsd, "FIRST_NAME", builtInPatientData.getPreferredGivenName());
        addColumn(dsd, "LAST_NAME", builtInPatientData.getPreferredFamilyName());
        addColumn(dsd, "VILLAGE", basePatientData.getVillage());
        addColumn(dsd, "VHW", basePatientData.getChw());

        if (!phase1Only) {
            addColumn(dsd, "DIAGNOSES", basePatientData.getDiagnosesBasedOnMastercards());
        }
        addColumn(dsd, "LAST_VISIT_DATE", getAppointmentStatusData(phase1Only, "lastEncounterDate"));
        addColumn(dsd, "NEXT_APPT_DATE", getAppointmentStatusData(phase1Only, "nextScheduledDate"));
        addColumn(dsd, "WEEKS_OUT_OF_CARE", getAppointmentStatusData(phase1Only, "weeksOutOfCare"));

        if (!phase1Only) {
            addColumn(dsd, "PRIORITY_PATIENT", basePatientData.getPriorityPatientForTrace());
        }

        dsd.addSortCriteria("VILLAGE", SortCriteria.SortDirection.ASC);
        dsd.addSortCriteria("LAST_NAME", SortCriteria.SortDirection.ASC);

        return dsd;
    }

    /**
     * Return a Cohort Definition, parameterized by location, of patients who are enrolled and have a missed appointment in IC3 within the given range
     * If the phase1Only flag is set, limit this only to HIV enrolled patients who are late for an HIV visit
     */
    public CohortDefinition getMissedAppointmentCohort(Integer minWeeks, Integer maxWeeks, boolean phase1Only) {
        Integer minDays = minWeeks*7;
        Integer maxDays = (maxWeeks == null ? null : maxWeeks*7-1);
        if (phase1Only) {
            CohortDefinition activeInHivProgram = hivCohorts.getActivelyEnrolledInHivProgramAtLocationOnEndDate();
            CohortDefinition lateForVisit = df.getPatientsLateForAppointment(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes(), minDays, maxDays);
            return df.getPatientsInAll(activeInHivProgram, lateForVisit);
        }
        else {
            CohortDefinition activeInHivProgram = hivCohorts.getActivelyEnrolledInHivProgramAtLocationOnEndDate();
            CohortDefinition activeInCCProgram = ccCohorts.getActivelyEnrolledInChronicCareProgramAtLocationOnEndDate();
            CohortDefinition activeInIC3 = df.getPatientsInAny(activeInHivProgram, activeInCCProgram);
            CohortDefinition lateForVisit = df.getPatientsLateForAppointment(getIC3ActiveStates(), getIC3EncounterTypes(), minDays, maxDays);
            return df.getPatientsInAll(activeInIC3, lateForVisit);
        }
    }

    /**
     * Return a definition representing a property of the patient's appointment status in IC3
     * If the phase1Only flag is set, limit this only to HIV enrolled patients and HIV visits
     */
    public PatientDataDefinition getAppointmentStatusData(boolean phase1Only, String property) {
        PatientDataDefinition pdd = df.getAppointmentStatus(getIC3ActiveStates(), getIC3EncounterTypes());
        if (phase1Only) {
            pdd = df.getAppointmentStatus(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes());
        }
        return df.convert(pdd, new PropertyConverter(AppointmentInfo.class, property));
    }

    /**
     * @return list of all active states a patient could be in to be considered active in IC3
     */
    public List<ProgramWorkflowState> getIC3ActiveStates() {
        List<ProgramWorkflowState> allStates = new ArrayList<ProgramWorkflowState>(hivMetadata.getActiveHivStates());
        allStates.addAll(ccMetadata.getActiveChronicCareStates());
        return allStates;
    }

    /**
     * @return list of all encounter types a patient could make and attend scheduled IC3 visits
     */
    public List<EncounterType> getIC3EncounterTypes() {
        List<EncounterType> allEncounterTypes = new ArrayList<EncounterType>(hivMetadata.getHivEncounterTypes());
        allEncounterTypes.addAll(ccMetadata.getChronicCareScheduledVisitEncounterTypes());
        return allEncounterTypes;
    }

    /**
     * @return the mappings by which we want to vary the patient data set for a given iteration
     */
    private Map<String, Object> getDataSetMappings(Location location) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("location", location);
        return m;
    }

    /**
     * Convenience method to add a parameter to a dsd and the value of this parameter to a map
     */
    private void addParameterAndValue(DataSetDefinition dsd, Map<String, Object> mappings, Parameter parameter, Object value) {
        dsd.addParameter(parameter);
        mappings.put(parameter.getName(), value);
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign design = new ReportDesign();
        design.setUuid(getReportDesignUuid());
        design.setName("Excel");
        design.setReportDefinition(reportDefinition);
        design.setRendererType(IC3TraceReportRenderer.class);
        l.add(design);
        return l;
    }
}
