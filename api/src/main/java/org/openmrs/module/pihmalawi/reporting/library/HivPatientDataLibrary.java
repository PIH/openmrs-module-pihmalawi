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
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.data.converter.PatientIdentifierConverter;
import org.openmrs.module.pihmalawi.reporting.data.definition.ReasonForStartingArvsPatientDataDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.MapConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class HivPatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

	@Autowired
	private PatientDataFactory pdf;

	@Autowired
	private HivMetadata hivMetadata;

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.patientData.hiv.";
    }

	@Override
	public Class<? super PatientDataDefinition> getDefinitionType() {
		return PatientDataDefinition.class;
	}

	@DocumentedDefinition("arvNumberAtLocation")
	public PatientDataDefinition getArvNumberAtLocation() {
		PatientIdentifierType pit = hivMetadata.getArvNumberIdentifierType();
		return pdf.getPreferredIdentifierAtLocation(pit, new PatientIdentifierConverter());
	}

	@DocumentedDefinition("allHccNumbers")
	public PatientDataDefinition getAllHccNumbers() {
		PatientIdentifierType pit = hivMetadata.getHccNumberIdentifierType();
		return pdf.getAllIdentifiersOfType(pit, pdf.getIdentifierCollectionConverter());
	}

	@DocumentedDefinition("allArvNumbers")
	public PatientDataDefinition getAllArvNumbers() {
		PatientIdentifierType pit = hivMetadata.getArvNumberIdentifierType();
		return pdf.getAllIdentifiersOfType(pit, pdf.getIdentifierCollectionConverter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.date")
	public PatientDataDefinition getFirstArtInitialEncounterDateByEndDate() {
		return getFirstArtInitialEncounterByEndDate(pdf.getEncounterDatetimeConverter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.location")
	public PatientDataDefinition getFirstArtInitialEncounterLocationByEndDate() {
		return getFirstArtInitialEncounterByEndDate(pdf.getEncounterLocationNameConverter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.cd4Count")
	public PatientDataDefinition getFirstArtInitialCd4Count() {
		return getObsOnArtInitialEncounter(hivMetadata.getCd4CountConcept(), pdf.getObsValueNumericConverter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.ksSideEffectsWorseningOnArvs")
	public PatientDataDefinition getFirstArtInitialKsSideEffectsWorsening() {
		return getObsOnArtInitialEncounter(hivMetadata.getKsSideEffectsWorseningOnArvsConcept(), pdf.getObjectFormatter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.tbTreatmentStatus")
	public PatientDataDefinition getFirstArtInitialTbTreatmentStatus() {
		return getObsOnArtInitialEncounter(hivMetadata.getTbTreatmentStatusConcept(), pdf.getObjectFormatter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.whoStage")
	public PatientDataDefinition getFirstArtInitialWhoStage() {
		return getObsOnArtInitialEncounter(hivMetadata.getWhoStageConcept(), pdf.getObjectFormatter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.cd4Percent")
	public PatientDataDefinition getFirstArtInitialCd4Percent() {
		return getObsOnArtInitialEncounter(hivMetadata.getCd4PercentConcept(), pdf.getObjectFormatter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.presumedSevereHivPresent")
	public PatientDataDefinition getFirstArtInitialPresumedSevereHivPresent() {
		return getObsOnArtInitialEncounter(hivMetadata.getPresumedSevereHivCriteriaPresentConcept(), pdf.getObjectFormatter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.reasonForStartingArvs")
	public PatientDataDefinition getFirstArtInitialReasonForStartingArvs() {
		ReasonForStartingArvsPatientDataDefinition def = new ReasonForStartingArvsPatientDataDefinition();
		def.addParameter(ReportingConstants.END_DATE_PARAMETER);
		MapConverter c = new MapConverter(": ", ", ", null, new ObjectFormatter());
		return pdf.convert(def, c);
	}

	@DocumentedDefinition("latestFirstLineArvStartDate")
	public PatientDataDefinition getLatestFirstLineArvStartDateByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getFirstLineArvStartDateConcept()), pdf.getObsValueDatetimeConverter());
	}

	@DocumentedDefinition("latestCd4Count")
	public PatientDataDefinition getLatestCd4CountValueByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getCd4CountConcept()), pdf.getObsValueNumericConverter());
	}

	@DocumentedDefinition("latestArvDrugsReceived.value")
	public PatientDataDefinition getLatestArvDrugsReceivedByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getArvDrugsReceivedConcept()), new ObjectFormatter());
	}

	@DocumentedDefinition("latestArvDrugsReceived.date")
	public PatientDataDefinition getLatestArvDrugsReceivedDateByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getArvDrugsReceivedConcept()), pdf.getObsDatetimeConverter());
	}

	@DocumentedDefinition("latestTbStatus.value")
	public PatientDataDefinition getLatestTbStatusByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getTbStatusConcept()), new ObjectFormatter());
	}

	@DocumentedDefinition("latestTbStatus.date")
	public PatientDataDefinition getLatestTbStatusDateByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getTbStatusConcept()), pdf.getObsDatetimeConverter());
	}

	@DocumentedDefinition("latestArtSideEffects.value")
	public PatientDataDefinition getLatestArtSideEffectsByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getArtSideEffectsConcept()), new ObjectFormatter());
	}

	@DocumentedDefinition("latestArvDrugsReceived.date")
	public PatientDataDefinition getLatestArtSideEffectsDateByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getArtSideEffectsConcept()), pdf.getObsDatetimeConverter());
	}

	@DocumentedDefinition("latestHivTreatmentStatusStateAtLocation")
	public PatientDataDefinition getMostRecentHivTreatmentStatusStateAtLocationByEndDate() {
		ProgramWorkflow wf = hivMetadata.getTreatmentStatusWorkfow();
		return pdf.getMostRecentStateForWorkflowAtLocationByEndDate(wf, pdf.getStateNameConverter());
	}

	@DocumentedDefinition("latestHivTreatmentStatusStateAtLocation.date")
	public PatientDataDefinition getMostRecentHivTreatmentStatusStateStartDateAtLocationByEndDate() {
		ProgramWorkflow wf = hivMetadata.getTreatmentStatusWorkfow();
		return pdf.getMostRecentStateForWorkflowAtLocationByEndDate(wf, pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition("latestHivTreatmentStatusStateAtLocation.location")
	public PatientDataDefinition getMostRecentHivTreatmentStatusStateLocationAtLocationByEndDate() {
		ProgramWorkflow wf = hivMetadata.getTreatmentStatusWorkfow();
		return pdf.getMostRecentStateForWorkflowAtLocationByEndDate(wf, pdf.getStateLocationConverter());
	}

	@DocumentedDefinition("earliestOnArvsStateAtLocationByEndDate")
	public PatientDataDefinition getEarliestOnArvsStateAtLocationByEndDate() {
		ProgramWorkflowState state = hivMetadata.getOnArvsState();
		return pdf.getEarliestStateAtLocationByEndDate(state, pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition("earliestOnArvsStateByEndDate.enrollmentDate")
	public PatientDataDefinition getEarliestOnArvsStateEnrollmentDateByEndDate() {
		ProgramWorkflowState state = hivMetadata.getOnArvsState();
		return pdf.getEarliestStateByEndDate(state, pdf.getStateProgramEnrollmentDateConverter());
	}

	@DocumentedDefinition("earliestOnArvsStateByEndDate.date")
	public PatientDataDefinition getEarliestOnArvsStateStartDateByEndDate() {
		ProgramWorkflowState state = hivMetadata.getOnArvsState();
		return pdf.getEarliestStateByEndDate(state, pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition("earliestOnArvsStateByEndDate.location")
	public PatientDataDefinition getEarliestOnArvsStateLocationByEndDate() {
		ProgramWorkflowState state = hivMetadata.getOnArvsState();
		return pdf.getEarliestStateByEndDate(state, pdf.getStateLocationConverter());
	}

	@DocumentedDefinition("earliestPreArtStateByEndDate.date")
	public PatientDataDefinition getEarliestPreArtStateStartDateByEndDate() {
		ProgramWorkflowState state = hivMetadata.getPreArtState();
		return pdf.getEarliestStateByEndDate(state, pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition("earliestPreArtStateByEndDate.location")
	public PatientDataDefinition getEarliestPreArtStateLocationByEndDate() {
		ProgramWorkflowState state = hivMetadata.getPreArtState();
		return pdf.getEarliestStateByEndDate(state, pdf.getStateLocationConverter());
	}

	@DocumentedDefinition("earliestExposedChildStateByEndDate.date")
	public PatientDataDefinition getEarliestExposedChildStateStartDateByEndDate() {
		ProgramWorkflowState state = hivMetadata.getExposedChildState();
		return pdf.getEarliestStateByEndDate(state, pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition("earliestExposedChildStateByEndDate.location")
	public PatientDataDefinition getEarliestExposedChildStateLocationByEndDate() {
		ProgramWorkflowState state = hivMetadata.getExposedChildState();
		return pdf.getEarliestStateByEndDate(state, pdf.getStateLocationConverter());
	}

	// TODO: Get earliest patientState startDate and enrollment Location for the "On antiretrovirals" state on or before the end date
	//addColumn(dsd, "1st time in ART date", null);
	//addColumn(dsd, "1st time in ART location", null);

	// ***** CONVENIENCE METHODS

	protected PatientDataDefinition getFirstArtInitialEncounterByEndDate(DataConverter converter) {
		EncounterType arvInitial = hivMetadata.getArtInitialEncounterType();
		return pdf.getFirstEncounterOfTypeByEndDate(arvInitial, converter);
	}

	protected PatientDataDefinition getObsOnArtInitialEncounter(Concept question, DataConverter converter) {
		EncounterType arvInitial = hivMetadata.getArtInitialEncounterType();
		return pdf.getFirstObsByEndDate(question, Arrays.asList(arvInitial), converter);
	}
}
