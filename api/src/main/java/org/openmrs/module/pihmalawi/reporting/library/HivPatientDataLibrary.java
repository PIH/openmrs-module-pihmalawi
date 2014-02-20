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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.data.converter.PatientIdentifierConverter;
import org.openmrs.module.pihmalawi.reporting.data.definition.ReasonForStartingArvsPatientDataDefinition;
import org.openmrs.module.reporting.data.converter.MapConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	public PatientDataDefinition getFirstArtInitialEncounterDate() {
		return pdf.convert(getFirstArtInitialEncounter(), new PropertyConverter(Encounter.class, "encounterDatetime"));
	}

	@DocumentedDefinition("firstArtInitialEncounter.location")
	public PatientDataDefinition getFirstArtInitialEncounterLocation() {
		return pdf.convert(getFirstArtInitialEncounter(), new PropertyConverter(Encounter.class, "location"), new ObjectFormatter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.cd4Count")
	public PatientDataDefinition getFirstArtInitialCd4Count() {
		return getObsOnArtInitialEncounter(hivMetadata.getCd4CountConcept());
	}

	@DocumentedDefinition("firstArtInitialEncounter.ksSideEffectsWorseningOnArvs")
	public PatientDataDefinition getFirstArtInitialKsSideEffectsWorsening() {
		return getObsOnArtInitialEncounter(hivMetadata.getKsSideEffectsWorseningOnArvsConcept());
	}

	@DocumentedDefinition("firstArtInitialEncounter.tbTreatmentStatus")
	public PatientDataDefinition getFirstArtInitialTbTreatmentStatus() {
		return getObsOnArtInitialEncounter(hivMetadata.getTbTreatmentStatusConcept());
	}

	@DocumentedDefinition("firstArtInitialEncounter.whoStage")
	public PatientDataDefinition getFirstArtInitialWhoStage() {
		return getObsOnArtInitialEncounter(hivMetadata.getWhoStageConcept());
	}

	@DocumentedDefinition("firstArtInitialEncounter.cd4Percent")
	public PatientDataDefinition getFirstArtInitialCd4Percent() {
		return getObsOnArtInitialEncounter(hivMetadata.getCd4PercentConcept());
	}

	@DocumentedDefinition("firstArtInitialEncounter.presumedSevereHivPresent")
	public PatientDataDefinition getFirstArtInitialPresumedSevereHivPresent() {
		return getObsOnArtInitialEncounter(hivMetadata.getPresumedSevereHivCriteriaPresentConcept());
	}

	@DocumentedDefinition("firstArtInitialEncounter.reasonForStartingArvs")
	public PatientDataDefinition getFirstArtInitialReasonForStartingArvs() {
		MapConverter c = new MapConverter(": ", ", ", null, new ObjectFormatter());
		return pdf.convert(new ReasonForStartingArvsPatientDataDefinition(), c);
	}

	@DocumentedDefinition("latestFirstLineArvStartDate")
	public PatientDataDefinition getLatestFirstLineArvStartDateByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getFirstLineArvStartDateConcept()), pdf.getObsValueDatetimeConverter());
	}

	@DocumentedDefinition("latestCd4Count")
	public PatientDataDefinition getLatestCd4CountValueByEndDate() {
		PropertyConverter c = new PropertyConverter(Obs.class, "valueNumeric");
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

	// ***** CONVENIENCE METHODS

	protected PatientDataDefinition getFirstArtInitialEncounter() {
		EncounterType arvInitial = hivMetadata.getArtInitialEncounterType();
		return pdf.getFirstEncounterOfTypeByEndDate(arvInitial);
	}

	protected PatientDataDefinition getObsOnArtInitialEncounter(Concept question) {
		EncounterType arvInitial = hivMetadata.getArtInitialEncounterType();
		return pdf.getFirstObsByEndDate(question, arvInitial);
	}
}
