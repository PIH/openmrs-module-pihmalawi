package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.IC3ScreeningConcepts;
import org.springframework.stereotype.Component;

@Component
public class IC3ScreeningMetadata extends CommonMetadata {

    public static String SPUTUM_COLLECTED_CONCEPT_UUID = "165252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";


    // ENCOUNTERS

    public EncounterType getCheckInEncounterType() {
        return getEncounterType(EncounterTypes.CHECK_IN.uuid());
    }

    public EncounterType getBloodPressureScreeningEncounterType() {
        return getEncounterType(EncounterTypes.BLOOD_PRESSURE_SCREENING.uuid());
    }

    public EncounterType getClinicianScreeningEncounterType() {
        return getEncounterType(EncounterTypes.IC3_CLINICIAN_PLAN.uuid());
    }

    public EncounterType getNutritionScreeningEncounterType() {
        return getEncounterType(EncounterTypes.NUTRITION_SCREENING.uuid());
    }

    public EncounterType getAdherenceScreeningEncounterType() {
        return getEncounterType(EncounterTypes.ADHERENCE_COUNSELING.uuid());
    }

    public EncounterType getEIDScreeningEncounterType() {
        return getEncounterType(EncounterTypes.DNA_PCR_SCREENING.uuid());
    }

    public EncounterType getHTCScreeningEncounterType() {
        return getEncounterType(EncounterTypes.HTC_SCREENING.uuid());
    }

    public EncounterType getVLScreeningEncounterType() {
        return getEncounterType(EncounterTypes.VIRAL_LOAD_SCREENING.uuid());
    }

    public EncounterType getTBScreeningEncounterType() {
        return getEncounterType(EncounterTypes.TB_SCREENING.uuid());
    }

    public EncounterType getTBTestResultsEncounterType() {
        return getEncounterType(EncounterTypes.TB_TEST_RESULTS.uuid());
    }


    public EncounterType getNurseScreeningEncounterType() {
        return getEncounterType(EncounterTypes.NURSE_EVALUATION.uuid());
    }

    public EncounterType getCervicalScreeningEncounterType() {
        return getEncounterType(EncounterTypes.CERVICAL_CANCER_SCREENING.uuid());
    }


    // CONCEPTS
    public Concept getReferralSourceConcept() {
        return getConcept(IC3ScreeningConcepts.REFERRAL_SOURCE_CONCEPT);
    }

    public Concept getHealthCenterReferralConcept() {
        return getConcept(IC3ScreeningConcepts.HEALTH_CENTER_REFERRAL);
    }

    public Concept getIC3AppointmentConcept() {
        return getConcept(IC3ScreeningConcepts.IC3_APPOINTMENT);
    }

    public Concept getReferToScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.REFER_TO_SCREENING_STATION_UUID);
    }

    public Concept getBPScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.BP_SCREENING_STATION_CONCEPT_UUID);
    }

    public Concept getNutritionScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.NUTRITION_SCREENING_STATION_CONCEPT_UUID);
    }

    public Concept getAdherenceScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.ADHERENCE_SCREENING_STATION_CONCEPT_UUID);
    }

    public Concept getEIDScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.EID_SCREENING_STATION_CONCEPT_UUID);
    }

    public Concept getHTCScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.HTC_SCREENING_STATON_CONCEPT_UUID);
    }

    public Concept getVLScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.VL_SCREENING_STATION_CONCEPT_UUID);
    }

    public Concept getTBScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.TB_SCREENING_STATION_CONCEPT_UUID);
    }

    public Concept getNurseScreeningStationConcept() {
        return getConcept(IC3ScreeningConcepts.NURSE_STATION_CONCEPT_UUID);
    }

    public Concept getRecommendedNextStepsConcept() {
        return getConcept(IC3ScreeningConcepts.RECOMMENDED_NEXT_STEPS);
    }

    public Concept getReferToClinicianConcept() {
        return getConcept(IC3ScreeningConcepts.REFER_TO_CLINICIAN);
    }

    public Concept getSputumCollectedConcept() {
        return getConcept(SPUTUM_COLLECTED_CONCEPT_UUID);
    }

    public Concept getCervicalCancerScreeningConstructConcept() {
        return getConcept(IC3ScreeningConcepts.CERVICAL_CANCER_SCREENING_SET_CONCEPT_UUID);
    }
    public Concept getCervicalCancerScreeningResultsConcept() {
        return getConcept(IC3ScreeningConcepts.CERVICAL_CANCER_SCREENING_RESULTS_CONCEPT_UUID);
    }
    public Concept getNormalConcept() {
        return getConcept(IC3ScreeningConcepts.NORMAL_CONCEPT_UUID);
    }
}


