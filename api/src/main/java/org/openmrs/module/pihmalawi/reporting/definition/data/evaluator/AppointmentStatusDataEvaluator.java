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
package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.AppointmentStatusDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a Cd4DataDefinition to produce a PatientData
 */
@Handler(supports = AppointmentStatusDataDefinition.class, order = 50)
public class AppointmentStatusDataEvaluator implements PatientDataEvaluator {

    @Autowired
    CohortDefinitionService cohortDefinitionService;

	@Autowired
	private HivMetadata metadata;

    @Autowired
    private DataFactory df;

	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		AppointmentStatusDataDefinition def = (AppointmentStatusDataDefinition) definition;

        EvaluatedPatientData c = new EvaluatedPatientData(def, context);

        if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
            return c;
        }

        List<ProgramWorkflowState> activeStates = def.getActiveStates();
        List<Integer> encounterTypes = new ArrayList<Integer>();
        for (EncounterType et : def.getEncounterTypes()) {
            encounterTypes.add(et.getEncounterTypeId());
        }
        List<Integer> locationIds = null;
        if (def.getLocations() != null) {
            locationIds = new ArrayList<Integer>();
            for (Location l : def.getLocations()) {
                locationIds.add(l.getLocationId());
            }
        }

        // First, only those patients who are actively enrolled will have an appointment status
        Cohort enrolled = getPatientsActivelyEnrolled(activeStates, def.getLocations(), def.getOnDate(), context);

        HqlQueryBuilder lastEncounterBuilder = new HqlQueryBuilder();
        lastEncounterBuilder.select("e.patient.patientId", "max(e.encounterDatetime) as lastActualDate");
        lastEncounterBuilder.from(Encounter.class, "e");
        lastEncounterBuilder.wherePatientIn("e.patient.patientId", context);
        lastEncounterBuilder.where("e.encounterType.encounterTypeId in (" + OpenmrsUtil.join(encounterTypes, ",") + ")");
        lastEncounterBuilder.whereLessOrEqualTo("e.encounterDatetime", def.getOnDate());
        lastEncounterBuilder.whereIn("e.location.locationId", locationIds);
        lastEncounterBuilder.groupBy("e.patient.patientId");
        Map<Integer, Date> lastEncounterDates = evaluationService.evaluateToMap(lastEncounterBuilder, Integer.class, Date.class, context);

        HqlQueryBuilder nextScheduledBuilder = new HqlQueryBuilder();
        nextScheduledBuilder.select("o.personId", "max(o.valueDatetime) as lastPlannedDate");
        nextScheduledBuilder.from(Obs.class, "o");
        nextScheduledBuilder.where("o.encounter.encounterType.encounterTypeId in (" + OpenmrsUtil.join(encounterTypes, ",") + ")");
        nextScheduledBuilder.whereLessOrEqualTo("o.encounter.encounterDatetime", def.getOnDate());
        nextScheduledBuilder.whereIn("o.encounter.location.locationId", locationIds);
        nextScheduledBuilder.wherePersonIn("o.personId", context);
        nextScheduledBuilder.whereInAny("o.concept", metadata.getAppointmentDateConcept());
        nextScheduledBuilder.groupBy("o.personId");
        Map<Integer, Date> nextScheduledDates = evaluationService.evaluateToMap(nextScheduledBuilder, Integer.class, Date.class, context);

        Date effectiveDate = (def.getOnDate() == null ? new Date() : def.getOnDate());

		for (Integer pId : nextScheduledDates.keySet()) {
            Date nextScheduledDate = enrolled.contains(pId) ? nextScheduledDates.get(pId) : null;
            Date lastEncounterDate = lastEncounterDates.get(pId);
            c.addData(pId, new AppointmentInfo(effectiveDate, enrolled.contains(pId), lastEncounterDate, nextScheduledDate));
		}

        // Ensure that any members of the original base cohort are in the return data, even if they have no data found
        if (context.getBaseCohort() != null) {
            for (Integer pId : context.getBaseCohort().getMemberIds()) {
                if (!c.getData().containsKey(pId)) {
                    c.addData(pId, new AppointmentInfo(effectiveDate));
                }
            }
        }

		return c;
	}

    protected Cohort getPatientsActivelyEnrolled(List<ProgramWorkflowState> activeStates, List<Location> locations, Date endDate, EvaluationContext context) throws EvaluationException {
        InStateCohortDefinition cd = new InStateCohortDefinition();
        cd.setStates(activeStates);
        cd.setLocations(locations);
        cd.setOnDate(endDate);
        return cohortDefinitionService.evaluate(cd, context);
    }
}
