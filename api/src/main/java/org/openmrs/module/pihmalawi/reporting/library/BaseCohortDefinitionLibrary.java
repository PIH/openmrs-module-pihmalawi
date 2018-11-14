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
package org.openmrs.module.pihmalawi.reporting.library;

import org.openmrs.User;
import org.openmrs.api.FormService;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.VisitCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Defines all of the General Cohort Definition instances we want to expose for Pih Malawi
 */
@Component("pihmalawiBaseCohortDefinitionLibrary")
public class BaseCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.";

	@Autowired
	DataFactory df;

	@Autowired
	FormService formService;

	@Autowired
    HivMetadata hivMetadata;

    @Autowired
    ChronicCareMetadata ccMetadata;

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

	@DocumentedDefinition
	public CohortDefinition getAge0to14ByEnd() {
		return df.getAgeByEndDate(null, 14);
	}

	@DocumentedDefinition
	public CohortDefinition getAge15UpByEnd() {
		return df.getAgeByEndDate(15, null);
	}

	@DocumentedDefinition(value = "turned13WeeksOldDuringPeriod")
	public CohortDefinition getPatientsWhoTurned13WeeksDuringPeriod() {
		return df.getPatientsWhoTurnedWeeksOldDuringPeriod(13);
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsMoreThan25MonthsOldByEndDate() {
		AgeCohortDefinition cd = new AgeCohortDefinition();
		cd.setMinAge(26);
		cd.setMinAgeUnit(DurationUnit.MONTHS);
		cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		return df.convert(cd, ObjectUtil.toMap("effectiveDate=endDate"));
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsForWhomUserWasMostRecentToEnterAForm() {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setTimeQualifier(TimeQualifier.LAST);
		cd.setFormList(formService.getAllForms());
		cd.addParameter(new Parameter("createdBy", "Created By", User.class));
		return df.convert(cd, ObjectUtil.toMap("createdBy=user"));
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWithScheduledAppointmentOnEndDate() {
        DateObsCohortDefinition cd = new DateObsCohortDefinition();
        cd.setQuestion(hivMetadata.getAppointmentDateConcept());
        cd.setOperator1(RangeComparator.EQUAL);
        cd.addParameter(new Parameter("value1", "Date Value", Date.class));
        return df.convert(cd, ObjectUtil.toMap("value1=endDate"));
    }

    @DocumentedDefinition
    public CohortDefinition getPatientsWithAnActiveVisit() {
        VisitCohortDefinition cd = new VisitCohortDefinition();
        cd.setActive(true);
        return cd;
    }

    @DocumentedDefinition
    public CohortDefinition getPatientsWithACompletedVisitOnEndDate() {
        VisitCohortDefinition cd = new VisitCohortDefinition();
        cd.addParameter(new Parameter("stoppedOnOrAfter", "Stopped on or after", Date.class));
        cd.addParameter(new Parameter("stoppedOnOrBefore", "Stopped on or before", Date.class));
        return df.convert(cd, ObjectUtil.toMap("stoppedOnOrAfter=endDate,stoppedOnOrBefore=endDate"));
    }

    @DocumentedDefinition
    public CohortDefinition getPatientsActiveInHivOrChronicCareProgramOnEndDate() {
        InProgramCohortDefinition cd = new InProgramCohortDefinition();
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addProgram(hivMetadata.getHivProgram());
        cd.addProgram(ccMetadata.getChronicCareProgram());
        return df.convert(cd, ObjectUtil.toMap("onDate=endDate"));
    }
}
