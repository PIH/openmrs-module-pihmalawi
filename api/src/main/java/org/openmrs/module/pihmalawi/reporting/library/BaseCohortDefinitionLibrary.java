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
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
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
	private DataFactory df;

	@Autowired
	FormService formService;

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
}
