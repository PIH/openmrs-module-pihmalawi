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
import org.openmrs.Location;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ObsInEncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PresenceOrAbsenceCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi
 */
@Component
public class ChronicCareCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.chronicCare.";

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCarePatientDataLibrary ccPatientData;

    @Autowired
    private ChronicCareMetadata metadata;

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

	// Encounters

	@DocumentedDefinition(value = "hadChronicCareInitialVisitAtLocationDuringPeriod")
	public CohortDefinition getPatientsWithChronicCareInitialVisitAtLocationDuringPeriod() {
		return df.getAnyEncounterOfTypesAtLocationDuringPeriod(Arrays.asList(metadata.getChronicCareInitialEncounterType()));
	}

	@DocumentedDefinition(value = "hadChronicCareEncounterAtLocationByEndDate")
	public CohortDefinition getPatientsWithAChronicCareEncounterAtLocationByEndDate() {
		return df.getAnyEncounterOfTypesAtLocationByEndDate(metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "hadChronicCareEncounterAtLocationWithin3MonthsOfEndDate")
	public CohortDefinition getPatientsWithChronicCareEncounterAtLocationWithin3MonthsOfEndDate() {
		return df.getAnyEncounterOfTypesAtLocationWithinMonthsByEndDate(metadata.getChronicCareEncounterTypes(), 3);
	}

	@DocumentedDefinition(value = "hadChronicCareEncounterAtLocationWithin1MonthOfEndDate")
	public CohortDefinition getPatientsWithNoChronicCareEncounterAtLocationWithin1MonthOfEndDate() {
		return df.getAnyEncounterOfTypesAtLocationWithinMonthsByEndDate(metadata.getChronicCareEncounterTypes(), 1);
	}

	// Obs

	@DocumentedDefinition(value = "hasAsthmaDiagnosisByEndDate")
	public CohortDefinition getPatientsWithAsthmaDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getAsthmaConcept());
	}

	@DocumentedDefinition(value = "hasDiabetesDiagnosisByEndDate")
	public CohortDefinition getPatientsWithDiabetesDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getDiabetesConcept());
	}

	@DocumentedDefinition(value = "hasEpilepsyDiagnosisByEndDate")
	public CohortDefinition getPatientsWithEpilepsyDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getEpilepsyConcept());
	}

	@DocumentedDefinition(value = "hasHeartFailureDiagnosisByEndDate")
	public CohortDefinition getPatientsWithHeartFailureDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getHeartFailureConcept());
	}

	@DocumentedDefinition(value = "hasHypertensionDiagnosisByEndDate")
	public CohortDefinition getPatientsWithHypertensionDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getHypertensionConcept());
	}

	@DocumentedDefinition(value = "hasOtherNonCodedDiagnosisByEndDate")
	public CohortDefinition getPatientsWithOtherNonCodedDiagnosisByEndDate() {
		return hasDiagnosisByEndDate(metadata.getOtherNonCodedConcept());
	}

	@DocumentedDefinition(value = "hospitalizedAtLocationDuringPeriod")
	public CohortDefinition getPatientsHospitalizedAtLocationDuringPeriod() {
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getHospitalizedSinceLastVisitConcept(), Arrays.asList(metadata.getTrueConcept()));
	}

	@DocumentedDefinition(value = "hospitalizedForNcdAtLocationDuringPeriod")
	public CohortDefinition getPatientsHospitalizedForNcdAtLocationDuringPeriod() {
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getHospitalizedForNcdSinceLastVisitConcept(), Arrays.asList(metadata.getYesConcept()));
	}

	@DocumentedDefinition(value = "patientsOnBeclomethasoneAtLocationDuringPeriod")
	public CohortDefinition getPatientsOnBeclomethasoneAtLocationDuringPeriod() {
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getCurrentDrugsUsedConcept(), Arrays.asList(metadata.getBeclomethasoneConcept()));
	}

	@DocumentedDefinition(value = "patientsWithMoreThanMildPersistentAsthmaAtLocationDuringPeriod")
	public CohortDefinition getPatientsWithMoreThanMildPersistentAsthmaAtLocationDuringPeriod() {
		List<Concept> answers = Arrays.asList(metadata.getModeratePersistentConcept(), metadata.getSeverePersistentConcept(), metadata.getSevereUncontrolledConcept());
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getAsthmaClassificationConcept(), answers);
	}

	@DocumentedDefinition(value = "patientsOnInsulinAtLocationDuringPeriod")
	public CohortDefinition getPatientsOnInsulinAtLocationDuringPeriod() {
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getCurrentDrugsUsedConcept(), Arrays.asList(metadata.getInsulinConcept()));
	}

	@DocumentedDefinition(value = "patientsWithNumberOfSeizuresRecordedAtLocationDuringPeriod")
	public CohortDefinition getPatientsWithNumberOfSeizuresRecordedAtLocationDuringPeriod() {
		return df.getPatientsWithAnyObsAtLocationDuringPeriod(metadata.getNumberOfSeizuresConcept(), metadata.getChronicCareEncounterTypes());
	}

	@DocumentedDefinition(value = "patientsWithMoreThanTwoSeizuresPerMonthRecordedAtLocationDuringPeriod")
	public CohortDefinition getPatientsWithMoreThanTwoSeizuresPerMonthRecordedAtLocationDuringPeriod() {
		return df.getPatientsWithNumericObsAtLocationDuringPeriod(metadata.getNumberOfSeizuresConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 2.0);
	}

	@DocumentedDefinition(value = "patientsWithSystolicBloodPressureOver180AtLocationDuringPeriod")
	public CohortDefinition getPatientsWithSystolicBloodPressureOver180AtLocationDuringPeriod() {
		return df.getPatientsWithNumericObsAtLocationDuringPeriod(metadata.getSystolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 180.0);
	}

	@DocumentedDefinition(value = "patientsWithDiastolicBloodPressureOver110AtLocationDuringPeriod")
	public CohortDefinition getPatientsWithDiastolicBloodPressureOver110AtLocationDuringPeriod() {
		return df.getPatientsWithNumericObsAtLocationDuringPeriod(metadata.getDiastolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 110.0);
	}

	@DocumentedDefinition(value = "patientsOnMoreThanOneHypertensionMedicationAtLocationDuringPeriod")
	public CohortDefinition getPatientsOnMoreThanOneHypertensionMedicationAtLocationDuringPeriod() {
		PresenceOrAbsenceCohortDefinition cd = new PresenceOrAbsenceCohortDefinition();
		for (Concept med : metadata.getHypertensionMedicationConcepts()) {
			CohortDefinition onDrug = df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getCurrentDrugsUsedConcept(), Arrays.asList(med));
			cd.addCohortToCheck(Mapped.mapStraightThrough(onDrug));
		}
		cd.setPresentInAtLeast(2);
		return cd;
	}

	@DocumentedDefinition(value = "newPatientsReferredFromOPDAtLocationDuringPeriod")
	public CohortDefinition getNewPatientsReferredFromOPDAtLocationDuringPeriod() {
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getOutpatientConsultationConcept()));
	}

	@DocumentedDefinition(value = "newPatientsReferredFromInpatientWardAtLocationDuringPeriod")
	public CohortDefinition getNewPatientsReferredFromInpatientWardAtLocationDuringPeriod() {
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getInpatientWardConcept()));
	}

	@DocumentedDefinition(value = "newPatientsReferredFromHealthCenterAtLocationDuringPeriod")
	public CohortDefinition getNewPatientsReferredFromHealthCenterAtLocationDuringPeriod() {
		return df.getPatientsWithCodedObsAtLocationDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getHealthCenterConcept()));
	}

	@DocumentedDefinition(value = "newPatientsReferredAtLocationDuringPeriod")
	public CohortDefinition getNewPatientsReferredAtLocationDuringPeriod() {
		return df.getPatientsWithAnyObsAtLocationDuringPeriod(metadata.getSourceOfReferralConcept(), Arrays.asList(metadata.getChronicCareInitialEncounterType()));
	}

	@DocumentedDefinition(value = "patientsWithoutAChronicCareVisitMoreThanOneMonthPastTheirLastScheduleAppointmentAtLocationByEndDate")
	public CohortDefinition getPatientsWithoutAChronicCareVisitMoreThanOneMonthPastTheirLastScheduleAppointmentAtLocationByEndDate() {
		ObsInEncounterCohortDefinition cd = new ObsInEncounterCohortDefinition();
		cd.setWhichEncounter(TimeQualifier.LAST);
		cd.setEncounterTypes(Arrays.asList(metadata.getChronicCareFollowupEncounterType()));
		cd.addParameter(new Parameter("encounterOnOrBefore", "Encounter On Or Before", Date.class));
		cd.addParameter(new Parameter("encounterLocations", "Encounter Locations", Location.class));
		cd.setQuestion(metadata.getAppointmentDateConcept());
		cd.setValueOperator1(RangeComparator.LESS_EQUAL);
		cd.addParameter(new Parameter("valueDatetime1", "Date value", Date.class));
		return df.convert(cd, ObjectUtil.toMap("encounterOnOrBefore=endDate,encounterLocations=location,valueDatetime1=${endDate-1m}"));
	}

	// Programs

	@DocumentedDefinition(value = "enrolledInChronicCareProgramDuringPeriod")
	public CohortDefinition getPatientsEnrolledInChronicCareProgramDuringPeriod() {
		return df.getEnrolledInProgramDuringPeriod(metadata.getChronicCareProgram());
	}

	@DocumentedDefinition(value = "inOnTreatmentStateAtLocationOnEndDate")
	public CohortDefinition getPatientsInOnTreatmentStateAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(metadata.getChronicCareStatusOnTreatment());
	}

	// Helper methods

	protected CohortDefinition hasDiagnosisByEndDate(Concept diagnosis) {
		return df.getPatientsWithCodedObsByEndDate(metadata.getChronicCareDiagnosisConcept(), Arrays.asList(diagnosis));
	}
}
