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

import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi Chronic Care Program
 */
@Component("pihMalawiChronicCareEncounterQueryLibrary")
public class ChronicCareEncounterQueryLibrary extends BaseDefinitionLibrary<EncounterQuery> {

    public static final String PREFIX = "pihmalawi.encounterQuery.chronicCare.";

    @Autowired
    private ChronicCareMetadata metadata;

	@Autowired
	private DataFactory df;

    @Override
    public Class<? super EncounterQuery> getDefinitionType() {
        return EncounterQuery.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

	@DocumentedDefinition(value = "chronicCareFollowupEncountersDuringPeriod")
	public EncounterQuery getChronicCareFollowupEncountersDuringPeriod() {
		BasicEncounterQuery q = new BasicEncounterQuery();
		q.addEncounterType(metadata.getChronicCareFollowupEncounterType());
		q.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
		q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
		return new MappedParametersEncounterQuery(q, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate"));
	}

	@DocumentedDefinition(value = "chronicCareFollowupEncountersByEndDate")
	public EncounterQuery getChronicCareFollowupEncountersByEndDate() {
		BasicEncounterQuery q = new BasicEncounterQuery();
		q.addEncounterType(metadata.getChronicCareFollowupEncounterType());
		q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
		return new MappedParametersEncounterQuery(q, ObjectUtil.toMap("onOrBefore=endDate"));
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithPeakFlowRecordedDuringPeriod")
	public EncounterQuery getChronicCareEncountersWithPeakFlowRecordedDuringPeriod() {
		return df.getEncountersWithObsRecordedDuringPeriod(metadata.getPeakFlowConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithWeightRecordedDuringPeriod")
	public EncounterQuery getChronicCareEncountersWithWeightRecordedDuringPeriod() {
		return df.getEncountersWithObsRecordedDuringPeriod(metadata.getWeightConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithSerumGlucoseRecordedDuringPeriod")
	public EncounterQuery getChronicCareEncountersWithSerumGlucoseRecordedDuringPeriod() {
		return df.getEncountersWithObsRecordedDuringPeriod(metadata.getSerumGlucoseConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithSerumGlucoseGreaterThan200DuringPeriod")
	public EncounterQuery getChronicCareEncountersWithSerumGlucoseGreaterThan200DuringPeriod() {
		return df.getEncountersWithNumericObsValuesRecordedDuringPeriod(metadata.getSerumGlucoseConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 200.0);
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithSeizuresRecordedDuringPeriod")
	public EncounterQuery getChronicCareEncountersWithSeizuresRecordedDuringPeriod() {
		return df.getEncountersWithObsRecordedDuringPeriod(metadata.getNumberOfSeizuresConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithSystolicBloodPressureRecordedDuringPeriod")
	public EncounterQuery getChronicCareEncountersWithSystolicBloodPressureRecordedDuringPeriod() {
		return df.getEncountersWithObsRecordedDuringPeriod(metadata.getSystolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithSystolicBloodPressureOver180DuringPeriod")
	public EncounterQuery getChronicCareEncountersWithSystolicBloodPressureOver180DuringPeriod() {
		return df.getEncountersWithNumericObsValuesRecordedDuringPeriod(metadata.getSystolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 180.0);
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithDiastolicBloodPressureOver110DuringPeriod")
	public EncounterQuery getChronicCareEncountersWithDiastolicBloodPressureOver110DuringPeriod() {
		return df.getEncountersWithNumericObsValuesRecordedDuringPeriod(metadata.getDiastolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 110.0);
	}

	@DocumentedDefinition(value = "chronicCareEncountersWithPreferredTreatmentStockedOutDuringPeriod")
	public EncounterQuery getChronicCareEncountersWithPreferredTreatmentStockedOutDuringPeriod() {
		return df.getEncountersWithCodedObsValuesRecordedDuringPeriod(metadata.getNumberOfSeizuresConcept(), metadata.getChronicCareEncounterTypes(), metadata.getYesConcept());
	}
}
