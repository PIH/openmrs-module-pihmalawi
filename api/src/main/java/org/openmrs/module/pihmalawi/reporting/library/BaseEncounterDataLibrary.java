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

	public EncounterDataDefinition getNextAppointmentDateObsValue() {
		EncounterDataDefinition def = df.getSingleObsForEncounter(hivMetadata.getAppointmentDateConcept());
		return df.convert(def, df.getObsValueDatetimeConverter());
	}

	public EncounterDataDefinition getAgeAtEncounterDateInYears() {
		return df.convert(getAgeAtEncounterDate(), new AgeConverter(AgeConverter.YEARS));
	}

	public EncounterDataDefinition getAgeAtEncounterDateInMonths() {
		return df.convert(getAgeAtEncounterDate(), new AgeConverter(AgeConverter.MONTHS));
	}

	public EncounterDataDefinition getAgeAtEncounterDate() {
		return new AgeAtEncounterDataDefinition();
	}
}
