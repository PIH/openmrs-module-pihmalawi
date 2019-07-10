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
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.common.ArtRegimen;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.group.ArtTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.HccTreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.HivTestResultListConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.MostRecentViralLoadResultConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.ObsValueBooleanYesNoConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PatientIdentifierConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PregnantLactatingConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.RegimenChangeConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.TbStatusConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.ViralLoadValueConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.WhoStageConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ArtRegimenHistoryDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.Cd4DataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.FirstStateAfterStatePatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.HivTestResultPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ReasonForStartingArvsPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ViralLoadDataDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.MapConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class HivPatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

	@Autowired
	private DataFactory pdf;

	@Autowired
	private HivMetadata hivMetadata;

    @Autowired
    private ArtTreatmentGroup artTreatmentGroup;

    @Autowired
    private HccTreatmentGroup hccTreatmentGroup;

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
		Program hivProgram = hivMetadata.getHivProgram();
		return pdf.getPreferredProgramIdentifierAtLocation(pit, hivProgram, new PatientIdentifierConverter());
	}

	@DocumentedDefinition("hccNumberAtLocation")
	public PatientDataDefinition getHccNumberAtLocation() {
		PatientIdentifierType pit = hivMetadata.getHccNumberIdentifierType();
		Program hivProgram = hivMetadata.getHivProgram();
		return pdf.getPreferredProgramIdentifierAtLocation(pit, hivProgram, new PatientIdentifierConverter());
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

    @DocumentedDefinition
    public PatientDataDefinition getFirstHivEncounterDateByEndDate() {
        return pdf.getFirstEncounterOfTypeByEndDate(hivMetadata.getHivEncounterTypes(), pdf.getEncounterDatetimeConverter());
    }

	@DocumentedDefinition("firstPreArtInitialEncounter.date")
	public PatientDataDefinition getFirstPreArtInitialEncounterDateByEndDate() {
		return getFirstPreArtInitialEncounterByEndDate(pdf.getEncounterDatetimeConverter());
	}

	@DocumentedDefinition("firstPreArtInitialEncounter.location")
	public PatientDataDefinition getFirstPreArtInitialEncounterLocationByEndDate() {
		return getFirstPreArtInitialEncounterByEndDate(pdf.getEncounterLocationNameConverter());
	}

	@DocumentedDefinition("firstExposedChildInitialEncounter.date")
	public PatientDataDefinition getFirstExposedChildInitialEncounterDateByEndDate() {
		return getFirstExposedChildInitialEncounterByEndDate(pdf.getEncounterDatetimeConverter());
	}

	@DocumentedDefinition("firstExposedChildInitialEncounter.location")
	public PatientDataDefinition getFirstExposedChildInitialEncounterLocationByEndDate() {
		return getFirstExposedChildInitialEncounterByEndDate(pdf.getEncounterLocationNameConverter());
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

	@DocumentedDefinition("firstEidInitialEncounter.motherArtNumber")
	public PatientDataDefinition getFirstEidInitialMotherArtNumber() {
		return getObsOnEidInitialEncounter(hivMetadata.getMotherArtNumberConcept(), pdf.getObsValueTextConverter());
	}

    @DocumentedDefinition("firstArtInitialEncounter.cd4Percent")
    public PatientDataDefinition getFirstArtInitialCd4Percent() {
        return getObsOnArtInitialEncounter(hivMetadata.getCd4PercentConcept(), pdf.getObjectFormatter());
    }

    @DocumentedDefinition("firstArtInitialEncounter.cd4Date")
    public PatientDataDefinition getFirstArtInitialCd4Date() {
        return getObsOnArtInitialEncounter(hivMetadata.getCd4DateConcept(), pdf.getObsValueDatetimeConverter());
    }

	@DocumentedDefinition("firstArtInitialEncounter.ksSideEffectsWorseningOnArvs")
	public PatientDataDefinition getFirstArtInitialKsSideEffectsWorsening() {
		return getObsOnArtInitialEncounter(hivMetadata.getKsSideEffectsWorseningOnArvsConcept(), new ObsValueBooleanYesNoConverter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.tbTreatmentStatus")
	public PatientDataDefinition getFirstArtInitialTbTreatmentStatus() {
		return getObsOnArtInitialEncounter(hivMetadata.getTbTreatmentStatusConcept(), new TbStatusConverter());
	}

	@DocumentedDefinition("firstArtInitialEncounter.whoStage")
	public PatientDataDefinition getFirstArtInitialWhoStage() {
		return getObsOnArtInitialEncounter(hivMetadata.getWhoStageConcept(), new WhoStageConverter());
	}

    @DocumentedDefinition("firstArtInitialEncounter.whoClinicalConditions")
    public PatientDataDefinition getFirstArtInitialWhoClinicalConditions() {
        return getObsOnArtInitialEncounter(hivMetadata.getWhoClinicalConditionsConcept(), pdf.getObsValueTextConverter());
    }

	@DocumentedDefinition("firstArtInitialEncounter.presumedSevereHivPresent")
	public PatientDataDefinition getFirstArtInitialPresumedSevereHivPresent() {
		return getObsOnArtInitialEncounter(hivMetadata.getPresumedSevereHivCriteriaPresentConcept(), pdf.getObjectFormatter());
	}

    @DocumentedDefinition("firstArtInitialEncounter.pregnantLactating")
    public PatientDataDefinition getFirstArtInitialPregnantLactating() {
        return getObsOnArtInitialEncounter(hivMetadata.getPregnantOrLactatingConcept(), new PregnantLactatingConverter());
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
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getDateOfStartingFirstLineArvsConcept()), pdf.getObsValueDatetimeConverter());
	}

    @DocumentedDefinition
    public PatientDataDefinition getLatestBreastfeedingStatusValueByEndDate() {
        return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getBreastfeedingStatusConcept()), pdf.getObsValueCodedConverter());
    }

    @DocumentedDefinition
    public PatientDataDefinition getCd4CountObservations() {
        return new Cd4DataDefinition();
    }

	@DocumentedDefinition("latestCd4Count")
	public PatientDataDefinition getLatestCd4CountValueByEndDate() {
		Cd4DataDefinition cd4Def = new Cd4DataDefinition();
		return pdf.convert(cd4Def, pdf.getLastListItemConverter(pdf.getObsValueNumericConverter()));
	}

	@DocumentedDefinition("latestCd4Count.date")
	public PatientDataDefinition getLatestCd4CountDateByEndDate() {
		Cd4DataDefinition cd4Def = new Cd4DataDefinition();
		return pdf.convert(cd4Def, pdf.getLastListItemConverter(pdf.getObsDatetimeConverter()));
	}

    //************* HIV TESTS *********************

    @DocumentedDefinition
    public PatientDataDefinition getAllHivTestResultsByEndDate() {
        HivTestResultPatientDataDefinition def = new HivTestResultPatientDataDefinition();
        def.addParameter(ReportingConstants.END_DATE_PARAMETER);
        return def;
    }

    public PatientDataDefinition getHivTestResultPropertyByEndDate(Concept type, String property) {
        PatientDataDefinition results = getAllHivTestResultsByEndDate();
        HivTestResultListConverter converter = new HivTestResultListConverter(type, property);
        return pdf.convert(results, converter);
    }

	@DocumentedDefinition
	public PatientDataDefinition getFirstHivTestResultsByEndDate() {
		HivTestResultPatientDataDefinition def = new HivTestResultPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.addParameter(ReportingConstants.END_DATE_PARAMETER);
		return def;
	}

	public PatientDataDefinition getFirstHivTestResultPropertyByEndDate(Concept type, String property) {
		PatientDataDefinition results = getFirstHivTestResultsByEndDate();
		HivTestResultListConverter converter = new HivTestResultListConverter(type, property);
		return pdf.convert(results, converter);
	}

	//************* VIRAL LOADS *********************

    @DocumentedDefinition
    public ViralLoadDataDefinition getAllViralLoadsByEndDate() {
        ViralLoadDataDefinition def = new ViralLoadDataDefinition();
        def.addParameter(ReportingConstants.END_DATE_PARAMETER);
        return def;
    }

    @DocumentedDefinition("latestViralLoad.value")
    public PatientDataDefinition getLatestViralLoadValueByEndDate(Object valueIfLdl) {
        ViralLoadDataDefinition def = getAllViralLoadsByEndDate();
        MostRecentViralLoadResultConverter mlvrc = new MostRecentViralLoadResultConverter();
        ViralLoadValueConverter vlvc = new ViralLoadValueConverter(valueIfLdl);
        return pdf.convert(def, new ChainedConverter(mlvrc, vlvc));
	}

    @DocumentedDefinition("latestViralLoad.resultDate")
    public PatientDataDefinition getLatestViralLoadResultDateByEndDate() {
        ViralLoadDataDefinition def = getAllViralLoadsByEndDate();
        MostRecentViralLoadResultConverter mlvrc = new MostRecentViralLoadResultConverter();
        PropertyConverter dateConverter = new PropertyConverter(ViralLoad.class, "resultDate");
        return pdf.convert(def, new ChainedConverter(mlvrc, dateConverter));
    }

    public PatientDataDefinition getLatestViralLoadByEndDate(String property) {
        ViralLoadDataDefinition def = getAllViralLoadsByEndDate();
        PropertyConverter propertyConverter = new PropertyConverter(ViralLoad.class, property);
        return pdf.convert(def, pdf.getLastListItemConverter(propertyConverter));
    }

    //************* ARV REGIMEN DATA *********************

    @DocumentedDefinition("arvRegimenChanges")
    public PatientDataDefinition getArvRegimenChangesByEndDate() {
        ArtRegimenHistoryDataDefinition def = new ArtRegimenHistoryDataDefinition();
        def.addParameter(ReportingConstants.END_DATE_PARAMETER);
        return pdf.convert(def, new RegimenChangeConverter());
    }

    public PatientDataDefinition getLatestArtRegimenChangeByEndDate(String property) {
        PatientDataDefinition changes = getArvRegimenChangesByEndDate();
        return pdf.convert(changes, pdf.getLastListItemConverter(new PropertyConverter(ArtRegimen.class, property)));
    }

	@DocumentedDefinition("latestArvDrugsReceived.value")
	public PatientDataDefinition getLatestArvDrugsReceivedByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getArvDrugsReceivedConcept()), new ObjectFormatter());
	}

	@DocumentedDefinition("latestArvDrugsReceived.date")
	public PatientDataDefinition getLatestArvDrugsReceivedDateByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getArvDrugsReceivedConcept()), pdf.getObsDatetimeConverter());
	}

    //************* TB Status *********************

    @DocumentedDefinition("latestTbStatus")
    public PatientDataDefinition getLatestTbStatusObs() {
        return pdf.getMostRecentObsByEndDate(hivMetadata.getTbStatusConcept());
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

	@DocumentedDefinition("latestArtSideEffects.date")
	public PatientDataDefinition getLatestArtSideEffectsDateByEndDate() {
		return pdf.convert(pdf.getMostRecentObsByEndDate(hivMetadata.getArtSideEffectsConcept()), pdf.getObsDatetimeConverter());
	}

    @DocumentedDefinition("eidAppointmentStatus")
    public PatientDataDefinition getEidAppointmentStatus() {
        return pdf.getAppointmentStatus(Arrays.asList(hivMetadata.getExposedChildState()), hivMetadata.getExposedChildFollowupEncounterType());
    }

    @DocumentedDefinition("artAppointmentStatus")
    public PatientDataDefinition getArtAppointmentStatus() {
		return pdf.getAppointmentStatus(Arrays.asList(hivMetadata.getOnArvsState()), hivMetadata.getArtFollowupEncounterType());
    }

	@DocumentedDefinition
	public PatientDataDefinition getEarliestHivProgramEnrollmentDateByEndDate() {
		return pdf.getEarliestProgramEnrollmentByEndDate(hivMetadata.getHivProgram(), pdf.getProgramEnrollmentDateConverter());
	}

    public PatientDataDefinition getMostRecentHivTreatmentStatusStateByEndDate(DataConverter converter) {
        ProgramWorkflow wf = hivMetadata.getTreatmentStatusWorkfow();
        return pdf.getMostRecentStateForWorkflowByEndDate(wf, converter);
    }

    @DocumentedDefinition("latestHivTreatmentStatusState")
    public PatientDataDefinition getMostRecentHivTreatmentStatusStateByEndDate() {
        return getMostRecentHivTreatmentStatusStateByEndDate(new PropertyConverter(PatientState.class, "state"));
    }

    @DocumentedDefinition("latestHivTreatmentStatusStateConcept")
    public PatientDataDefinition getMostRecentHivTreatmentStatusStateConceptByEndDate() {
        return getMostRecentHivTreatmentStatusStateByEndDate(new PropertyConverter(PatientState.class, "state.concept"));
    }

	@DocumentedDefinition("latestHivTreatmentStatusStateName")
	public PatientDataDefinition getMostRecentHivTreatmentStatusStateNameByEndDate() {
		return getMostRecentHivTreatmentStatusStateByEndDate(pdf.getStateNameConverter());
	}

	@DocumentedDefinition("latestHivTreatmentStatusState.date")
	public PatientDataDefinition getMostRecentHivTreatmentStatusStateStartDateByEndDate() {
        return getMostRecentHivTreatmentStatusStateByEndDate(pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition("latestHivTreatmentStatusState.location")
	public PatientDataDefinition getMostRecentHivTreatmentStatusStateLocationByEndDate() {
        return getMostRecentHivTreatmentStatusStateByEndDate(pdf.getStateLocationConverter());
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

	@DocumentedDefinition
	public PatientDataDefinition getEarliestDefaultedStateStartDateByEndDate() {
		return pdf.getEarliestStateByEndDate(hivMetadata.getDefaultedState(), pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition
	public PatientDataDefinition getEarliestTreatmentStoppedStateStartDateByEndDate() {
		return pdf.getEarliestStateByEndDate(hivMetadata.getTreatmentStoppedState(), pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition("firstStateAfterExposedChildOrPreArtStateAtLocationByEndDate")
	public PatientDataDefinition getFirstStateAfterExposedChildOrPreArtStateAtLocationByEndDate() {
		return getFirstStateAfterExposedChildOrPreArtStateAtLocationByEndDate(pdf.getStateNameConverter());
	}

	@DocumentedDefinition("firstStateAfterExposedChildOrPreArtStateAtLocationByEndDate.startDate")
	public PatientDataDefinition getFirstStateStartDateAfterExposedChildOrPreArtStateAtLocationByEndDate() {
		return getFirstStateAfterExposedChildOrPreArtStateAtLocationByEndDate(pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition
	public PatientDataDefinition getMostRecentOnArvsStateStartDateByEndDate() {
		return pdf.getMostRecentStateByEndDate(hivMetadata.getOnArvsState(), pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition
	public PatientDataDefinition getMostRecentOnArvsStateStartDateAtLocationByEndDate() {
		return pdf.getMostRecentStateAtLocationByEndDate(hivMetadata.getOnArvsState(), pdf.getStateStartDateConverter());
	}

	@DocumentedDefinition
	public PatientDataDefinition getMostRecentDiedStateStartDateAtLocationByEndDate() {
		return pdf.getMostRecentStateAtLocationByEndDate(hivMetadata.getDiedState(), pdf.getStateStartDateConverter());
	}

    @DocumentedDefinition
    public PatientDataDefinition getMostRecentArtEncounterDateByEndDate() {
        return pdf.getMostRecentEncounterOfTypesByEndDate(hivMetadata.getArtEncounterTypes(), pdf.getEncounterDatetimeConverter());
    }

    @DocumentedDefinition
    public PatientDataDefinition getMostRecentHivEncounterDateByEndDate() {
        return pdf.getMostRecentEncounterOfTypesByEndDate(hivMetadata.getHivEncounterTypes(), pdf.getEncounterDatetimeConverter());
    }

	// ***** CONVENIENCE METHODS

	protected PatientDataDefinition getFirstPreArtInitialEncounterByEndDate(DataConverter converter) {
		return pdf.getFirstEncounterOfTypeByEndDate(Arrays.asList(hivMetadata.getPreArtInitialEncounterType()), converter);
	}

	protected PatientDataDefinition getFirstExposedChildInitialEncounterByEndDate(DataConverter converter) {
		return pdf.getFirstEncounterOfTypeByEndDate(Arrays.asList(hivMetadata.getExposedChildInitialEncounterType()), converter);
	}

	protected PatientDataDefinition getFirstArtInitialEncounterByEndDate(DataConverter converter) {
		return pdf.getFirstEncounterOfTypeByEndDate(Arrays.asList(hivMetadata.getArtInitialEncounterType()), converter);
	}

	protected PatientDataDefinition getObsOnArtInitialEncounter(Concept question, DataConverter converter) {
		EncounterType arvInitial = hivMetadata.getArtInitialEncounterType();
		return pdf.getFirstObsByEndDate(question, Arrays.asList(arvInitial), converter);
	}

	protected PatientDataDefinition getObsOnEidInitialEncounter(Concept question, DataConverter converter) {
		EncounterType eidInitial = hivMetadata.getEidInitialEncounterType();
		return pdf.getFirstObsByEndDate(question, Arrays.asList(eidInitial), converter);
	}

	protected PatientDataDefinition getFirstStateAfterExposedChildOrPreArtStateAtLocationByEndDate(DataConverter converter) {
		FirstStateAfterStatePatientDataDefinition def = new FirstStateAfterStatePatientDataDefinition();
		def.addParameter(new Parameter("startedOnOrBefore", "Started On Or Before", Date.class));
		def.addParameter(new Parameter("location", "Location", Location.class));
		def.setPrecedingStates(Arrays.asList(hivMetadata.getExposedChildState(), hivMetadata.getPreArtState()));
		return pdf.convert(def, ObjectUtil.toMap("startedOnOrBefore=endDate"), converter);
	}
}
