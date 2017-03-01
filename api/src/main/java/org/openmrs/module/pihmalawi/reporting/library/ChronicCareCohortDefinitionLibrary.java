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
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.HighBloodPressureCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PresenceOrAbsenceCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.pihmalawi.common.TraceConstants.HighPriorityCategory;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi
 */
@Component
public class ChronicCareCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.chronicCare.";

	@Autowired
	private DataFactory df;

    @Autowired
    private HivCohortDefinitionLibrary hivCohorts;

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

    @DocumentedDefinition
    public CohortDefinition getPatientsWithMostRecentSeverePersistentAsthmaByEndDate() {
        return df.getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(metadata.getAsthmaClassificationConcept(), null, metadata.getSeverePersistentConcept(), metadata.getSevereUncontrolledConcept());
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

    @DocumentedDefinition
    public CohortDefinition getPatientsWithMoreThanFiveSeizuresPerMonthRecordedInLastVisitByEndDate() {
        return df.getPatientsWithMostRecentNumericObsAtLocationByEnd(metadata.getNumberOfSeizuresConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 5.0);
    }

	@DocumentedDefinition(value = "patientsWithSystolicBloodPressureOver180AtLocationDuringPeriod")
	public CohortDefinition getPatientsWithSystolicBloodPressureOver180AtLocationDuringPeriod() {
		return df.getPatientsWithNumericObsAtLocationDuringPeriod(metadata.getSystolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 180.0);
	}

	@DocumentedDefinition(value = "patientsWithDiastolicBloodPressureOver110AtLocationDuringPeriod")
	public CohortDefinition getPatientsWithDiastolicBloodPressureOver110AtLocationDuringPeriod() {
		return df.getPatientsWithNumericObsAtLocationDuringPeriod(metadata.getDiastolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.GREATER_THAN, 110.0);
	}

    @DocumentedDefinition
    public CohortDefinition getPatientsWithHighBloodPressureByEnd() {
        HighBloodPressureCohortDefinition cd = new HighBloodPressureCohortDefinition();
        cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
        return cd;
    }

    @DocumentedDefinition
    public CohortDefinition getPatientsWithMostRecentSystolicBloodPressureUnder180AtLocation() {
        return df.getPatientsWithMostRecentNumericObsAtLocationByEnd(metadata.getSystolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.LESS_THAN, 180.0);
    }

    @DocumentedDefinition
    public CohortDefinition getPatientsWithMostRecentDiastolicBloodPressureUnder110AtLocation() {
        return df.getPatientsWithMostRecentNumericObsAtLocationByEnd(metadata.getDiastolicBloodPressureConcept(), metadata.getChronicCareEncounterTypes(), RangeComparator.LESS_THAN, 110.0);
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

    @DocumentedDefinition
	public Map<HighPriorityCategory, CohortDefinition> getHighPriorityForTraceCohortsAtEnd() {
        Map<HighPriorityCategory, CohortDefinition> ret = new LinkedHashMap<HighPriorityCategory, CohortDefinition>();
        ret.put(HighPriorityCategory.HIV, hivCohorts.getEverEnrolledInHivProgramByEndDate());  // 1. HIV patients (all)
        ret.put(HighPriorityCategory.HIGH_BP, getPatientsWithHighBloodPressureByEnd()); // 2. Hypertension patients with BP ever greater than 180/110 (both systolic and diastolic should exceed threshold)
        ret.put(HighPriorityCategory.ON_INSULIN, df.getPatientsWithCodedObsByEndDate(metadata.getCurrentDrugsUsedConcept(), Arrays.asList(metadata.getInsulinConcept()))); // 3. Diabetes patients on insulin
        ret.put(HighPriorityCategory.SEVERE_ASTHMA, getPatientsWithMostRecentSeverePersistentAsthmaByEndDate()); // 4. Asthma patients with severity of “severe persistent” at last visit
        ret.put(HighPriorityCategory.HIGH_SIEZURES, getPatientsWithMoreThanFiveSeizuresPerMonthRecordedInLastVisitByEndDate()); // 5. Epilepsy patients reporting over 5 seizures per month at last visit
        ret.put(HighPriorityCategory.SICKLE_CELL, df.getPatientsWithAnyObsByEndDate(metadata.getSickleCellDiseaseConcept())); // 6. Sickle cell disease patients (all)
        ret.put(HighPriorityCategory.CHRONIC_KIDNEY_DISEASE, df.getPatientsWithAnyObsByEndDate(metadata.getChronicKidneyDiseaseConcept())); // 7. Chronic kidney disease patients (all)
        ret.put(HighPriorityCategory.RHEUMATIC_HEART_DISEASE, df.getPatientsWithAnyObsByEndDate(metadata.getRheumaticHeartDiseaseConcept())); // 8. Rheumatic Heart Disease patients (all)
        ret.put(HighPriorityCategory.CONGESTIVE_HEART_FAILURE, df.getPatientsWithAnyObsByEndDate(metadata.getCongestiveHeartFailureConcept())); // 9. Congestive Heart Failure patients (all)
        return ret;
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
		return df.getPatientsWithAnyObsAtLocationDuringPeriod(metadata.getSourceOfReferralConcept(), metadata.getChronicCareInitialEncounterTypes());
	}

	// Programs

    @DocumentedDefinition
    public CohortDefinition getActivelyEnrolledInChronicCareProgramAtLocationOnEndDate() {
        return df.getActivelyEnrolledInProgramAtLocationOnEndDate(metadata.getChronicCareProgram());
    }

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
