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

import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseEncounterDataLibrary extends BaseDefinitionLibrary<EncounterDataDefinition> {

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.encounterData.";
    }

	@Override
	public Class<? super EncounterDataDefinition> getDefinitionType() {
		return EncounterDataDefinition.class;
	}

	@DocumentedDefinition(value = "nextAppointmentDateObsValue")
	public EncounterDataDefinition getNextAppointmentDateObsValue() {
		EncounterDataDefinition def = df.getSingleObsForEncounter(hivMetadata.getAppointmentDateConcept());
		return df.convert(def, df.getObsValueDatetimeConverter());
	}

	@DocumentedDefinition(value = "nextAppointmentDateObsReferenceValue")
	public EncounterDataDefinition getNextAppointmentDateObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getAppointmentDateConcept());
		return df.convert(def, df.getObsValueDatetimeConverter());
	}

	@DocumentedDefinition(value = "weightObsValue")
	public EncounterDataDefinition getWeightObsValue() {
		EncounterDataDefinition def = df.getSingleObsForEncounter(hivMetadata.getWeightConcept());
		return df.convert(def, df.getObsValueNumericConverter());
	}

	@DocumentedDefinition(value = "weightObsReferenceValue")
	public EncounterDataDefinition getWeightObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getWeightConcept());
		return df.convert(def, df.getObsValueNumericConverter());
	}

	@DocumentedDefinition(value = "heightObsReferenceValue")
	public EncounterDataDefinition getHeightObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getHeightConcept());
		return df.convert(def, df.getObsValueNumericConverter());
	}

	@DocumentedDefinition(value = "systolicBPObsReferenceValue")
	public EncounterDataDefinition getSystolicBPObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getSystolicBloodPressureConcept());
		return df.convert(def, df.getObsValueNumericConverter());
	}

	@DocumentedDefinition(value = "diastolicBPObsReferenceValue")
	public EncounterDataDefinition getDiastolicBPObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getDiastolicBloodPressureConcept());
		return df.convert(def, df.getObsValueNumericConverter());
	}

	@DocumentedDefinition(value = "reasonForTestObsReferenceValue")
	public EncounterDataDefinition getReasonForTestObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getReasonForTestingConcept());
		return df.convert(def, df.getObsValueCodedNameConverter());
	}

	@DocumentedDefinition(value = "labLocationObsReferenceValue")
	public EncounterDataDefinition getLabLocationObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getConcept(hivMetadata.LAB_LOCATION));
		return df.convert(def, df.getObsValueCodedNameConverter());
	}

	@DocumentedDefinition(value = "bledObsReferenceValue")
	public EncounterDataDefinition getBledObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getConcept(hivMetadata.HIV_VIRAL_LOAD_SPECIMEN_COLLECTED));
		return df.convert(def, df.getObsValueCodedNameConverter());
	}

	@DocumentedDefinition(value = "vlResultObsReferenceValue")
	public EncounterDataDefinition getVLResultObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getHivViralLoadConcept());
		return df.convert(def, df.getObsValueNumericConverter());
	}

	@DocumentedDefinition(value = "vlLessThanLimitObsReferenceValue")
	public EncounterDataDefinition getVLLessThanLimitObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getHivLessThanViralLoadConcept());
		return df.convert(def, df.getObsValueNumericConverter());
	}

	@DocumentedDefinition(value = "ldlObsReferenceValue")
	public EncounterDataDefinition getLdlObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getHivLDLConcept());
		return df.convert(def, df.getObsValueCodedNameConverter());
	}

	@DocumentedDefinition(value = "reasonNoResultObsReferenceValue")
	public EncounterDataDefinition getReasonNoResultObsReferenceValue() {
		EncounterDataDefinition def = df.getSingleObsForEncountersOnSameDate(hivMetadata.getReasonNoResultConcept());
		return df.convert(def, df.getObsValueCodedNameConverter());
	}

	@DocumentedDefinition(value = "ageAtEncounterDateInYears")
	public EncounterDataDefinition getAgeAtEncounterDateInYears() {
		return df.convert(getAgeAtEncounterDate(), new AgeConverter(AgeConverter.YEARS));
	}

	@DocumentedDefinition(value = "ageAtEncounterDateInMonths")
	public EncounterDataDefinition getAgeAtEncounterDateInMonths() {
		return df.convert(getAgeAtEncounterDate(), new AgeConverter(AgeConverter.MONTHS));
	}

	public EncounterDataDefinition getAgeAtEncounterDate() {
		return new AgeAtEncounterDataDefinition();
	}
}
