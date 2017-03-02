/**
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
package org.openmrs.module.pihmalawi.reporting.definition.cohort.evaluator;

import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.common.TraceConstants;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.IC3Metadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.RepeatViralLoadNeedingTraceCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Handler(supports = { RepeatViralLoadNeedingTraceCohortDefinition.class })
public class RepeatViralLoadNeedingTraceCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
    PatientDataService patientDataService;

	@Autowired
    DataFactory df;

	@Autowired
    HivPatientDataLibrary hivData;

	@Autowired
    BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    IC3Metadata ic3Metadata;

    /**
     * patient had a viral load > 1000, it was entered 84-168 days ago, patient hasn't visited since, and patient has appointment 2-4 weeks in the future
     *  viral load obs has (
     *      valueNumeric > 1000 and
     *      today-dateCreated between [84, 168) days and
     *      lastVisitDate-dateCreated > 0
     *  )
     *  and appointmentDate-today [14, 28)
     */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        RepeatViralLoadNeedingTraceCohortDefinition cd = (RepeatViralLoadNeedingTraceCohortDefinition) cohortDefinition;

        List<EncounterType> encounterTypes = (cd.getTraceType().isPhase1Only() ? hivMetadata.getHivEncounterTypes() : ic3Metadata.getEncounterTypes());
        List<ProgramWorkflowState> states = (cd.getTraceType().isPhase1Only() ? hivMetadata.getActiveHivStates() : ic3Metadata.getActiveStates());

        PatientDataDefinition viralLoadDataDefinition = getViralLoadsForTraceAtLocation(cd.getTraceType());
        PatientData viralLoads = patientDataService.evaluate(Mapped.mapStraightThrough(viralLoadDataDefinition), context);

        PatientDataDefinition latestEncounterDataDef = df.getMostRecentEncounterOfTypesAtLocationByEndDate(encounterTypes, df.getEncounterDatetimeConverter());
        PatientData latestEncounterDates = patientDataService.evaluate(Mapped.mapStraightThrough(latestEncounterDataDef), context);

        // If the patient has no IC3 encounters, or has matching viral load obs that were created after their latest IC3 encounter date, return them
        Cohort viralLoadQualifies = new Cohort();
        for (Integer pId : viralLoads.getData().keySet()) {
            List<Obs> obsList = (List<Obs>) viralLoads.getData().get(pId);
            Date encDate = (Date)latestEncounterDates.getData().get(pId);
            if (encDate == null) {
                viralLoadQualifies.addMember(pId);
            }
            else {
                Date encDateEndOfDay = DateUtil.getEndOfDay(encDate);
                for (Obs o : obsList) {
                    if (o.getDateCreated().after(encDateEndOfDay)) {
                        viralLoadQualifies.addMember(pId);
                    }
                }
            }
        }

        PatientDataDefinition appointmentDataDef = df.getAppointmentStatus(states, encounterTypes);
        PatientData appointments = patientDataService.evaluate(Mapped.mapStraightThrough(appointmentDataDef), context);
        Cohort appointmentDateQualifies = new Cohort();
        for (Integer pId : appointments.getData().keySet()) {
            AppointmentInfo appt = (AppointmentInfo) appointments.getData().get(pId);
            if (appt != null) {
                Integer daysToAppt = appt.getDaysToAppointment();
                if (daysToAppt != null && daysToAppt >= 14 && daysToAppt < 28) {
                    appointmentDateQualifies.addMember(pId);
                }
            }
        }

        Cohort ret = Cohort.intersect(viralLoadQualifies, appointmentDateQualifies);
		return new EvaluatedCohort(ret, cd, context);
	}

    public PatientDataDefinition getViralLoadsForTraceAtLocation(TraceConstants.TraceType traceType) {
        Date today = DateUtil.getStartOfDay(new Date());
        Date fromDate = (DateUtil.adjustDate(today, -168, DurationUnit.DAYS));
        Date toDate = (DateUtil.adjustDate(today, -84, DurationUnit.DAYS));

        ObsForPersonDataDefinition viralLoads = new ObsForPersonDataDefinition();
        viralLoads.setQuestion(hivMetadata.getHivViralLoadConcept());
        viralLoads.setValueNumericGreaterThan(1000d);
        viralLoads.setCreatedOnOrAfter(fromDate);
        viralLoads.setCreatedOnOrBefore(toDate);
        viralLoads.addParameter(new Parameter("locationList", "Location List", List.class));

        return df.convert(viralLoads, ObjectUtil.toMap("locationList=location"), null);
    }
}