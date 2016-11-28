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

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class BaseEncounterQueryLibrary extends BaseDefinitionLibrary<EncounterQuery> {

	@Autowired
	private DataFactory df;

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.encounterQuery.";
    }

	@Override
	public Class<? super EncounterQuery> getDefinitionType() {
		return EncounterQuery.class;
	}

	public EncounterQuery getEncountersAtLocationDuringPeriod(List<EncounterType> types) {
		BasicEncounterQuery q = new BasicEncounterQuery();
		if (types != null && types.size() > 0) {
			q.setEncounterTypes(types);
		}
		q.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
		q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
		q.addParameter(new Parameter("locationList", "Locations", Date.class));
		return new MappedParametersEncounterQuery(q, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
	}

	public EncounterQuery getMostRecentEncountersAtLocationDuringPeriod(List<EncounterType> types) {
		BasicEncounterQuery q = new BasicEncounterQuery();
		if (types != null && types.size() > 0) {
			q.setEncounterTypes(types);
		}
		q.setWhich(TimeQualifier.LAST);
		q.setWhichNumber(1);
		q.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
		q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
		q.addParameter(new Parameter("locationList", "Locations", Date.class));
		return new MappedParametersEncounterQuery(q, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
	}
}
