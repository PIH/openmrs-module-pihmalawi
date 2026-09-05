package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;
import org.openmrs.module.pihmalawi.metadata.concept.IC3ScreeningConcepts;
import org.springframework.stereotype.Component;

@Component
public class IC3ScreeningMetadata extends CommonMetadata {

    public static String SPUTUM_COLLECTED_CONCEPT_UUID = "165252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";


    // ENCOUNTERS

    public EncounterType getCheckInEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID);
    }

    public EncounterType getBloodPressureScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_BLOOD_PRESSURE_SCREENING_UUID);
    }

    public EncounterType getClinicianScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_IC3_CLINICIAN_PLAN_UUID);
    }

    public EncounterType getNutritionScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_NUTRITION_SCREENING_UUID);
    }

    public EncounterType getAdherenceScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_ADHERENCE_COUNSELING_UUID);
    }

    public EncounterType getEIDScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_DNA_PCR_SCREENING_UUID);
    }

    public EncounterType getHTCScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_HTC_SCREENING_UUID);
    }

    public EncounterType getVLScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_VIRAL_LOAD_SCREENING_UUID);
    }

    public EncounterType getTBScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_TB_SCREENING_UUID);
    }

    public EncounterType getTBTestResultsEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_TB_TEST_RESULTS_UUID);
    }


    public EncounterType getNurseScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_NURSE_EVALUATION_UUID);
    }

    public EncounterType getCervicalScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_CERVICAL_CANCER_SCREENING_UUID);
    }

    public EncounterType getBloodSugarScreeningEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_BLOOD_SUGAR_SCREENING_UUID);
    }

    public EncounterType getLabStationResultsEncounterType() {
        return getEncounterType(PihMalawiConfigConstants.ENCOUNTERTYPE_LAB_STATION_SCREENING_UUID);
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

    public Concept getBloodSugarTestSetConcept() {
        return getConcept(IC3ScreeningConcepts.BLOOD_SUGAR_TEST_SET_CONCEPT_UUID);
    }
}


