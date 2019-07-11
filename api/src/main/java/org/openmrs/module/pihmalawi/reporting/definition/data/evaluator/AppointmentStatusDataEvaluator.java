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
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.AppointmentStatusDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Evaluates a AppointmentStatusDataDefinition to produce a PatientData
 */
@Handler(supports = AppointmentStatusDataDefinition.class, order = 50)
public class AppointmentStatusDataEvaluator implements PatientDataEvaluator {

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Autowired
    PatientDataService patientDataService;

	@Autowired
	private HivMetadata metadata;

    @Autowired
    private DataFactory df;

	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		AppointmentStatusDataDefinition def = (AppointmentStatusDataDefinition) definition;

        EvaluatedPatientData c = new EvaluatedPatientData(def, context);

        if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
            return c;
        }

        Date onDate = ObjectUtil.nvl(def.getOnDate(), new Date());

        CohortDefinition inCorrectState = df.getActiveInStatesAtLocationOnEndDate(def.getActiveStates());
        PatientDataDefinition latestEncs = df.getMostRecentEncounterOfTypeByEndDate(def.getEncounterType(), null);
        PatientDataDefinition apptDates = df.getAllObsByEndDate(metadata.getAppointmentDateConcept(), null, null);

        EvaluationContext ctx = context.shallowCopy();
        ctx.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), onDate);

        Cohort inState = cohortDefinitionService.evaluate(inCorrectState, ctx);
        PatientData encs = patientDataService.evaluate(latestEncs, ctx);
        PatientData appts = patientDataService.evaluate(apptDates, ctx);

        for (Integer pId : context.getBaseCohort().getMemberIds()) {
            Encounter latestEnc = (Encounter) encs.getData().get(pId);
            List<Obs> apptDateObsList = (List<Obs>) appts.getData().get(pId);
            AppointmentInfo ai = new AppointmentInfo(onDate);
            ai.setCurrentlyEnrolled(inState.contains(pId));
            ai.setEncounterType(def.getEncounterType().getName());
            if (latestEnc != null) {
                ai.setLastEncounterDate(latestEnc.getEncounterDatetime());
                if (apptDateObsList != null) {
                    Date appDate = null;
                    for (Obs o : apptDateObsList) {
                        if (latestEnc != null && latestEnc.equals(o.getEncounter())) {
                            ai.setNextScheduledDate(o.getValueDatetime());
                        } else {
                            Date obsEncDate = (o.getEncounter() == null ? o.getObsDatetime() : o.getEncounter().getEncounterDatetime());
                            Date encDate = latestEnc.getEncounterDatetime();
                            if (DateUtil.datesMatchWithFormat(obsEncDate, encDate, "yyyy-MM-dd")) {
                                appDate = o.getValueDatetime();
                            }
                        }
                    }
                    if (ai.getNextScheduledDate() == null) {
                        ai.setNextScheduledDate(appDate);
                    }
                }
            }
            c.addData(pId, ai);
        }

		return c;
	}
}
