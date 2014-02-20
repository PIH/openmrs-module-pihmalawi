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

import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasePatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

	@Autowired
	private PatientDataFactory pdf;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData;

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.patientData.";
    }

	@Override
	public Class<? super PatientDataDefinition> getDefinitionType() {
		return PatientDataDefinition.class;
	}

	// Address Data

	@DocumentedDefinition("village")
	public PatientDataDefinition getVillage() {
		return pdf.getPreferredAddress("cityVillage");
	}

	@DocumentedDefinition("traditionalAuthority")
	public PatientDataDefinition getTraditionalAuthority() {
		return pdf.getPreferredAddress("countyDistrict");
	}

	@DocumentedDefinition("district")
	public PatientDataDefinition getDistrict() {
		return pdf.getPreferredAddress("stateProvince");
	}

	@DocumentedDefinition("ageAtEndInMonths")
	public PatientDataDefinition getAgeAtEndInMonths() {
		PatientDataDefinition ageAtEnd = builtInPatientData.getAgeAtEnd();
		return pdf.convert(ageAtEnd, new AgeConverter(AgeConverter.MONTHS));
	}
}
