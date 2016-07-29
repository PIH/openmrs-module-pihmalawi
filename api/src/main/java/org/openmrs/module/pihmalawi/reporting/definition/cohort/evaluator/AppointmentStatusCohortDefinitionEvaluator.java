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
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.AppointmentStatusCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.AppointmentStatusDataDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { AppointmentStatusCohortDefinition.class })
public class AppointmentStatusCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        AppointmentStatusCohortDefinition cd = (AppointmentStatusCohortDefinition) cohortDefinition;

        AppointmentStatusDataDefinition dd = new AppointmentStatusDataDefinition();
        dd.setActiveStates(cd.getActiveStates());
        dd.setEncounterTypes(cd.getEncounterTypes());
        dd.setLocations(cd.getLocations());
        dd.setOnDate(cd.getOnDate());

        PatientData apptInfoForPats = Context.getService(PatientDataService.class).evaluate(dd, context);

        Cohort c = new Cohort();
        for (Integer pId : apptInfoForPats.getData().keySet()) {
            AppointmentInfo ai = (AppointmentInfo)apptInfoForPats.getData().get(pId);
            Integer daysToAppointment = ai.getDaysToAppointment();
            if (daysToAppointment == null) {
                if (cd.getNoAppointmentIncluded() != null && cd.getNoAppointmentIncluded() && ai.getNextScheduledDate() == null && ai.isCurrentlyEnrolled()) {
                    c.addMember(pId);
                }
            }
            else if (daysToAppointment < 0) {
                int daysOverdue = daysToAppointment * -1;
                boolean minOk = (cd.getMinDaysOverdue() == null || cd.getMinDaysOverdue() <= daysOverdue);
                boolean maxOk = (cd.getMaxDaysOverdue() == null || cd.getMaxDaysOverdue() >= daysOverdue);
                if (minOk && maxOk) {
                    c.addMember(pId);
                }
            }
        }

		return new EvaluatedCohort(c, cd, context);
	}
}