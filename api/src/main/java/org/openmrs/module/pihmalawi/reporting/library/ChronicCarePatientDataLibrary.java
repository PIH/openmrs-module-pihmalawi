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

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PatientIdentifierConverter;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.CollectionElementConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ChronicCarePatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCareMetadata metadata;

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.patientData.chronicCare.";
    }

	@Override
	public Class<? super PatientDataDefinition> getDefinitionType() {
		return PatientDataDefinition.class;
	}

	@DocumentedDefinition("chronicCareNumberAtLocation")
	public PatientDataDefinition getChronicCareNumberAtLocation() {
		PatientIdentifierType pit = metadata.getChronicCareNumber();
		Program program = metadata.getChronicCareProgram();
		return df.getPreferredProgramIdentifierAtLocation(pit, program, new PatientIdentifierConverter());
	}

	@DocumentedDefinition("firstChronicCareInitialEncounter.date")
	public PatientDataDefinition getFirstChronicCareInitialEncounterDateByEndDate() {
		return df.getFirstEncounterOfTypeByEndDate(metadata.getChronicCareInitialEncounterType(), df.getEncounterDatetimeConverter());
	}

	@DocumentedDefinition("firstChronicCareInitialEncounter.location")
	public PatientDataDefinition getFirstChronicCareInitialEncounterLocationByEndDate() {
		return df.getFirstEncounterOfTypeByEndDate(metadata.getChronicCareInitialEncounterType(), df.getEncounterLocationNameConverter());
	}

	@DocumentedDefinition("latestChronicCareTreatmentStatusStateAtLocation")
	public PatientDataDefinition getMostRecentChronicCareTreatmentStatusStateAtLocationByEndDate() {
		ProgramWorkflow wf = metadata.getChronicCareTreatmentStatusWorkflow();
		return df.getMostRecentStateForWorkflowAtLocationByEndDate(wf, df.getStateNameConverter());
	}

	@DocumentedDefinition("latestChronicCareTreatmentStatusStateAtLocation.date")
	public PatientDataDefinition getMostRecentChronicCareTreatmentStatusStateStartDateAtLocationByEndDate() {
		ProgramWorkflow wf = metadata.getChronicCareTreatmentStatusWorkflow();
		return df.getMostRecentStateForWorkflowAtLocationByEndDate(wf, df.getStateStartDateConverter());
	}

	@DocumentedDefinition("latestChronicCareTreatmentStatusStateAtLocation.location")
	public PatientDataDefinition getMostRecentChronicCareTreatmentStatusStateLocationAtLocationByEndDate() {
		ProgramWorkflow wf = metadata.getChronicCareTreatmentStatusWorkflow();
		return df.getMostRecentStateForWorkflowAtLocationByEndDate(wf, df.getStateLocationConverter());
	}

	@DocumentedDefinition("latestChronicCareAppointmentDate")
	public PatientDataDefinition getMostRecentChronicCareAppointmentDateByEndDate() {
		return df.convert(df.getMostRecentObsByEndDate(metadata.getAppointmentDateConcept()), df.getObsValueDatetimeConverter());
	}

	// Helper methods

	public PatientDataDefinition getSingleObsFromChronicCareInitialVisitByEndDate(Concept question, DataConverter converter) {
		return df.getFirstObsByEndDate(question, Arrays.asList(metadata.getChronicCareInitialEncounterType()), converter);
	}

	public PatientDataDefinition getChronicCareInitialDiagnosisPresentByEndDate(Concept diagnosis) {
		Concept question = metadata.getChronicCareDiagnosisConcept();
		List<EncounterType> ccInitialEncounters = Arrays.asList(metadata.getChronicCareInitialEncounterType());
		ChainedConverter c = new ChainedConverter();
		c.addConverter(new CollectionConverter(df.getObsValueCodedConverter(), true, null));
		c.addConverter(new CollectionElementConverter(diagnosis, "TRUE", ""));
		return df.getAllObsByEndDate(question, ccInitialEncounters, c);
	}

}
