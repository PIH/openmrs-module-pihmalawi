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

import org.apache.commons.collections.comparators.ComparableComparator;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.pihmalawi.common.CodedValueAndDate;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.group.ChronicCareTreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PatientIdentifierConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.CodedValueAndDatePatientDataDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChronicCarePatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCareMetadata metadata;

    @Autowired
    private ChronicCareTreatmentGroup treatmentGroup;

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

    @DocumentedDefinition
    public PatientDataDefinition getAllChronicCareDiagnosisObsByEndDate() {
        return df.getAllObsByEndDate(metadata.getChronicCareDiagnosisConcept(), null, null);
    }

	@DocumentedDefinition
	public PatientDataDefinition getAllChronicCareDiagnosesByEndDate() {
        CodedValueAndDatePatientDataDefinition dd = new CodedValueAndDatePatientDataDefinition();
        dd.setCodedValueQuestion(metadata.getChronicCareDiagnosisConcept());
        dd.setDateValueQuestion(metadata.getDiagnosisDateConcept());
        dd.addParameter(ReportingConstants.END_DATE_PARAMETER);

        PropertyConverter codedValueConvertor = new PropertyConverter(CodedValueAndDate.class, "value");
        CollectionConverter c = new CollectionConverter(codedValueConvertor, true, new ComparableComparator());

        return df.convert(dd, c);
	}



	@DocumentedDefinition
	public PatientDataDefinition getIsPatientHighRisk() {
		return df.getCodedObsPresentByEndDate(metadata.getHighRiskPatientConcept(), metadata.getYesConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition
	public PatientDataDefinition getTimeSpentTravelingToClinicInHours() {
		return df.getMostRecentObsByEndDate(metadata.getClinicTravelTimeInHoursConcept());
	}

	@DocumentedDefinition
	public PatientDataDefinition getTimeSpentTravelingToClinicInMinutes() {
		return df.getMostRecentObsByEndDate(metadata.getClinicTravelTimeInMinutesConcept());
	}

	@DocumentedDefinition
	public PatientDataDefinition getMostRecentEncounterDateByEndDate() {
		return df.getMostRecentEncounterOfTypesByEndDate(metadata.getChronicCareEncounterTypes(), df.getEncounterDatetimeConverter());
	}

	@DocumentedDefinition
	public PatientDataDefinition getMostRecentAppointmentDateByEndDate() {
		return df.getMostRecentObsByEndDate(metadata.getAppointmentDateConcept(), metadata.getChronicCareEncounterTypes(), df.getObsValueDatetimeConverter());
	}

    @DocumentedDefinition
    public PatientDataDefinition getChronicCareAppointmentStatus() {
        return df.getAppointmentStatus(treatmentGroup);
    }

    public PatientDataDefinition getFirstEncounterDateByEndDate(List<EncounterType> encounterTypes) {
        return df.getFirstEncounterOfTypeByEndDate(encounterTypes, df.getEncounterDatetimeConverter());
    }

    public PatientDataDefinition getMostRecentEncounterDateByEndDate(List<EncounterType> encounterTypes) {
        return df.getMostRecentEncounterOfTypesByEndDate(encounterTypes, df.getEncounterDatetimeConverter());
    }
}
