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
import org.openmrs.module.pihmalawi.reporting.definition.renderer.TraceMissedAppointmentReportRenderer;
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
public class TraceMissedAppointmentReport extends ApzuReportManager {

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
		return "TRACE Missed Appointment Report";
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

        CohortDefinition activeInHivProgram = hivCohorts.getActivelyEnrolledInHivProgramAtLocationOnEndDate();
        CohortDefinition underSixWeeksLateForHivVisit = df.getPatientsLateForAppointment(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes(), 7*2, 7*6-1);
        CohortDefinition sixPlusWeeksLateForHivVisit = df.getPatientsLateForAppointment(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes(), 7*6, 7*12-1);
        PatientDataDefinition hivAppointmentStatus = df.getAppointmentStatus(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes());

        CohortDefinition activeInCCProgram = ccCohorts.getActivelyEnrolledInChronicCareProgramAtLocationOnEndDate();
        CohortDefinition underSixWeeksLateForNcdVisit = df.getPatientsLateForAppointment(ccMetadata.getActiveChronicCareStates(), ccMetadata.getChronicCareScheduledVisitEncounterTypes(), 7*2, 7*6-1);
        CohortDefinition sixPlusWeeksLateForNcdisit = df.getPatientsLateForAppointment(ccMetadata.getActiveChronicCareStates(), ccMetadata.getChronicCareScheduledVisitEncounterTypes(), 7*6, 7*12-1);

        List<ProgramWorkflowState> allStates = new ArrayList<ProgramWorkflowState>(hivMetadata.getActiveHivStates());
        allStates.addAll(ccMetadata.getActiveChronicCareStates());
        List<EncounterType> allEncounterTypes = new ArrayList<EncounterType>(hivMetadata.getHivEncounterTypes());
        allEncounterTypes.addAll(ccMetadata.getChronicCareScheduledVisitEncounterTypes());
        PatientDataDefinition allAppointmentStatus = df.getAppointmentStatus(allStates, allEncounterTypes);

        CohortDefinition twoWeekHivPatients = df.getPatientsInAll(activeInHivProgram, underSixWeeksLateForHivVisit);
        CohortDefinition twoWeekNcdPatients = df.getPatientsInAll(activeInCCProgram, underSixWeeksLateForNcdVisit);
        CohortDefinition twoWeekAllPatients = df.getPatientsInAny(twoWeekHivPatients, twoWeekNcdPatients);

        CohortDefinition sixWeekHivPatients = df.getPatientsInAll(activeInHivProgram, sixPlusWeeksLateForHivVisit);
        CohortDefinition sixWeekNcdPatients = df.getPatientsInAll(activeInCCProgram, sixPlusWeeksLateForNcdisit);
        CohortDefinition sixWeekAllPatients = df.getPatientsInAny(sixWeekHivPatients, sixWeekNcdPatients);

        List<Location> locations = hivMetadata.getHivStaticSystemLocations();

        for (Location location : locations) {

            for (int i=1; i<=2; i++) {

                boolean is2wP1 = (i == 1 && location.hasTag(LocationTags.TRACE_PHASE_1_LOCATION.name()));
                PatientDataDefinition apptInfoDef = (is2wP1 ? hivAppointmentStatus : allAppointmentStatus);

                PatientDataSetDefinition dsd = new PatientDataSetDefinition();
                dsd.addParameter(ReportingConstants.LOCATION_PARAMETER);
                dsd.addParameter(new Parameter("phase", "Phase", Integer.class));
                dsd.addParameter(new Parameter("numWeeks", "Num Weeks", Integer.class));

                addColumn(dsd, "ARV_NUMBER", hivPatientData.getArvNumberAtLocation());
                if (!is2wP1) {
                    addColumn(dsd, "NCD_NUMBER", ccPatientData.getChronicCareNumberAtLocation());
                }
                addColumn(dsd, "FIRST_NAME", builtInPatientData.getPreferredGivenName());
                addColumn(dsd, "LAST_NAME", builtInPatientData.getPreferredFamilyName());
                addColumn(dsd, "VILLAGE", basePatientData.getVillage());
                addColumn(dsd, "VHW", basePatientData.getChw());
                if (!is2wP1) {
                    addColumn(dsd, "DIAGNOSES", basePatientData.getDiagnosesBasedOnMastercards());
                }
                addColumn(dsd, "LAST_VISIT_DATE", df.convert(apptInfoDef, new PropertyConverter(AppointmentInfo.class, "lastEncounterDate")));
                addColumn(dsd, "NEXT_APPT_DATE", df.convert(apptInfoDef, new PropertyConverter(AppointmentInfo.class, "nextScheduledDate")));
                addColumn(dsd, "WEEKS_OUT_OF_CARE", df.convert(apptInfoDef, new PropertyConverter(AppointmentInfo.class, "weeksOutOfCare")));
                if (!is2wP1) {
                    addColumn(dsd, "PRIORITY_PATIENT", basePatientData.getPriorityPatientForTrace());
                }
                dsd.addSortCriteria("VILLAGE", SortCriteria.SortDirection.ASC);
                dsd.addSortCriteria("LAST_NAME", SortCriteria.SortDirection.ASC);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("location", location);

                if (i == 1) {
                    params.put("numWeeks", 2);

                    if (location.hasTag(LocationTags.TRACE_PHASE_1_LOCATION.name())) {
                        params.put("phase", 1);
                        dsd.addRowFilter(Mapped.mapStraightThrough(twoWeekHivPatients));
                    }
                    else {
                        params.put("phase", 2);
                        dsd.addRowFilter(Mapped.mapStraightThrough(twoWeekAllPatients));
                    }
                }
                else {
                    params.put("numWeeks", 6);
                    params.put("phase", 0);
                    dsd.addRowFilter(Mapped.mapStraightThrough(sixWeekAllPatients));
                }

                rd.addDataSetDefinition(location.getName() + " - " + params.get("numWeeks") + " weeks", dsd, params);
            }
        }

        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign design = new ReportDesign();
        design.setUuid(getReportDesignUuid());
        design.setName("Excel");
        design.setReportDefinition(reportDefinition);
        design.setRendererType(TraceMissedAppointmentReportRenderer.class);
        l.add(design);
        return l;
    }
}
