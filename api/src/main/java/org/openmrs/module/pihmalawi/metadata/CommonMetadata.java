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
package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("commonMetadata")
public class CommonMetadata extends Metadata {

	public static final String CURRENT_SYSTEM_LOCATION_TAG_GLOBAL_PROPERTY = "pihmalawi.upperOrLowerNeno";
	public static final String UPPER_NENO = "UPPER_NENO";
	public static final String LOWER_NENO = "LOWER_NENO";

	public static final String CHW_RELATIONSHIP_TYPE = "Patient/Village Health Worker";
	public static final String GUARDIAN_RELATIONSHIP_TYPE = "Patient/Guardian";

	public RelationshipType getChwRelationshipType() {
		return getRelationshipType(CHW_RELATIONSHIP_TYPE);
	}

	public RelationshipType getGuardianRelationshipType() {
		return getRelationshipType(GUARDIAN_RELATIONSHIP_TYPE);
	}

	public static final String LAB_ENCOUNTER_TYPE = "LAB";
	public static final String APPOINTMENT_ENCOUNTER_TYPE = "APPOINTMENT";

	public EncounterType getLabEncounterType() {
		return getEncounterType(LAB_ENCOUNTER_TYPE);
	}

	public EncounterType getAppointmentEncounterType() {
		return getEncounterType(APPOINTMENT_ENCOUNTER_TYPE);
	}

	public static final String APPOINTMENT_DATE = "6569cbd4-977f-11e1-8993-905e29aff6c1";
	public static final String HEIGHT = "Height (cm)";
	public static final String WEIGHT = "Weight (kg)";
	public static final String CD4_COUNT = "CD4 count";
	public static final String CLINICIAN_REPORTED_CD4 = "Clinician reported to CD4";
    public static final String CD4_DATE = "Date of CD4 count";

    // HIV TESTING

    public static final String CHILD_HIV_SEROLOGY_CONSTRUCT = "655dca78-977f-11e1-8993-905e29aff6c1"; // Group for Child Results
    public static final String HIV_TEST_SAMPLE_DATE = "656f9ed8-977f-11e1-8993-905e29aff6c1";
    public static final String HIV_TEST_RESULT_DATE = "656fa234-977f-11e1-8993-905e29aff6c1";
    public static final String HIV_TEST_TYPE = "655bee06-977f-11e1-8993-905e29aff6c1"; // Answers Rapid Test, DNA PCR
    public static final String HIV_RAPID_TEST = "654b983a-977f-11e1-8993-905e29aff6c1"; // Answer and Question with answers Indeterminate/Negative/Postitive/Not Done
    public static final String HIV_TEST_RESULT = "655dcb7c-977f-11e1-8993-905e29aff6c1"; // Answers Indeterminate/Negative/Positive/Reactive/Non-reactive
    public static final String HIV_DNA_PCR = "654a6960-977f-11e1-8993-905e29aff6c1"; // Answer and Question with answers Indeterminate/Negative/Postitive/Not Done
    public static final String DNA_PCR_RESULT = "657a3a14-977f-11e1-8993-905e29aff6c1";  // Values Negative/Positive
    public static final String DNA_PCR_RESULT_2 = "657a3b22-977f-11e1-8993-905e29aff6c1";  // Values Negative/Positive
    public static final String DNA_PCR_RESULT_3 = "657a3c26-977f-11e1-8993-905e29aff6c1";  // Values Negative/Positive
    public static final String NEGATIVE = "654994c2-977f-11e1-8993-905e29aff6c1";
    public static final String POSITIVE = "6549be7a-977f-11e1-8993-905e29aff6c1";
    public static final String REACTIVE = "65587802-977f-11e1-8993-905e29aff6c1";
    public static final String NON_REACTIVE = "6558791a-977f-11e1-8993-905e29aff6c1";
    public static final String INDETERMINATE = "6557baa2-977f-11e1-8993-905e29aff6c1";

    public static final List<String> HIV_DNA_PCR_RESULT_CONCEPTS = Arrays.asList(
            HIV_DNA_PCR, DNA_PCR_RESULT, DNA_PCR_RESULT_2, DNA_PCR_RESULT_3
    );

    public static final String CONFIRMATORY_HIV_TEST_TYPE_CONCEPT = "65792d4a-977f-11e1-8993-905e29aff6c1";
    public static final String FIRST_POSITIVE_HIV_TEST_TYPE_CONCEPT = "6574552c-977f-11e1-8993-905e29aff6c1";

    // VIRAL LOAD TESTING CONSTANTS

    public static final String HIV_VIRAL_LOAD_TEST_SET = "83931c6d-0e5a-4302-b8ce-a31175b6475e";
    public static final String HIV_VIRAL_LOAD_SPECIMEN_COLLECTED = "f792f2f9-9c24-4d6e-98fd-caffa8f2383f";
    public static final String HIV_VIRAL_LOAD = "654a7694-977f-11e1-8993-905e29aff6c1";
	public static final String HIV_VIRAL_LDL = "e97b36a2-16f5-11e6-b6ba-3e1d05defe78";
	public static final String REASON_FOR_TESTING = "164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String ROUTINE = "e0821812-955d-11e7-abc4-cec278b6b50a";
	public static final String CONFIRMED = "65590f06-977f-11e1-8993-905e29aff6c1";
	public static final String TARGETED = "e0821df8-955d-11e7-abc4-cec278b6b50a";
	public static final String LAB_LOCATION = "6fc0ab50-9492-11e7-abc4-cec278b6b50a";
	public static final String REASON_NO_RESULT = "656fa450-977f-11e1-8993-905e29aff6c1";
	public static final String RESULT_MISSING = "e0822140-955d-11e7-abc4-cec278b6b50a";
	public static final String UNSATISFACTORY_SAMPLE = "656fa55e-977f-11e1-8993-905e29aff6c1";

	// ADHERENCE COUNSELLING

    public static final String ADHERENCE_COUNSELING_SESSION_NUMBER_CONCEPT = "06b1f7d8-b6cc-11e8-96f8-529269fb1459";
    public static final String FIRST_CONCEPT = "697e9461-f2d6-4ab1-a140-48f768ce002a";
    public static final String SECOND_CONCEPT = "11c0f708-6950-4e94-b080-5c76174a4947";
    public static final String THIRD_CONCEPT = "224e3d57-f6d1-4244-bbe2-b81a574ba7aa";

	public static final String EVER_RECEIVED_ART_CONCEPT = "Ever received ART?";
	public static final String KS_SIDE_EFFECTS_WORSENING_ON_ARVS = "Kaposis sarcoma side effects worsening while on ARVs?";
	public static final String TB_TREATMENT_STATUS = "Tuberculosis treatment status";
	public static final String WHO_STAGE = "WHO stage";
    public static final String WHO_CLINICAL_CONDITIONS = "Clinical Conditions Text";
	public static final String CD4_PERCENT = "CD4 percent";
	public static final String PRESUMED_SEVERE_HIV_CRITERIA_PRESENT = "Presumed severe HIV criteria present";
	public static final String ARV_DRUGS_RECEIVED = "Malawi Antiretroviral drugs received";
    public static final String ARV_DRUGS_CHANGE_1 = "657ac678-977f-11e1-8993-905e29aff6c1";
    public static final String ARV_DRUGS_CHANGE_2 = "657ac7c2-977f-11e1-8993-905e29aff6c1";
    public static final String ARV_DRUGS_CHANGE_3 = "657ac8d0-977f-11e1-8993-905e29aff6c1";
    public static final String DATE_OF_STARTING_FIRST_LINE_ARVS = "656fbe36-977f-11e1-8993-905e29aff6c1";
    public static final String DATE_OF_STARTING_ALTERNATIVE_FIRST_LINE_ARVS = "655fabfe-977f-11e1-8993-905e29aff6c1";
    public static final String DATE_OF_STARTING_SECOND_LINE_ARVS = "655fad02-977f-11e1-8993-905e29aff6c1";
	public static final String TB_STATUS = "TB status";
	public static final String MOTHER_ART_NUMBER = "Mother ART registration number";
	public static final String TB_NOT_SUSPECTED_CONCEPT = "TB NOT suspected";
	public static final String TB_SUSPECTED_CONCEPT = "TB suspected";
	public static final String TB_CONFIRMED_NOT_ON_TX_CONCEPT = "Confirmed TB NOT on treatment";
	public static final String TB_CONFIRMED_ON_TX_CONCEPT = "Confirmed TB on treatment";
	public static final String ART_SIDE_EFFECTS = "Malawi ART side effects";

	public static final String PREGNANT_OR_LACTATING_CONCEPT = "Pregnant/Lactating";
	public static final String PATIENT_PREGNANT_CONCEPT = "Patient pregnant";
	public static final String SOURCE_OF_REFERRAL_CONCEPT = "Type of referring clinic or hospital";

	public static final String CURRENT_DRUGS_USED_CONCEPT = "Current drugs used";
	public static final String HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT = "Patient hospitalized since last visit";
	public static final String HOSPITALIZED_FOR_NCD_SINCE_LAST_VISIT_CONCEPT = "Hospitalized for non-communicable disease since last visit";
	public static final String SYSTOLIC_BLOOD_PRESSURE_CONCEPT = "Systolic blood pressure";
	public static final String DIASTOLIC_BLOOD_PRESSURE_CONCEPT = "Diastolic blood pressure";
	public static final String BLOOD_SUGAR_TEST_TYPE_CONCEPT = "Blood sugar test type";
	public static final String NUMBER_OF_SEIZURES_CONCEPT = "NUMBER OF SEIZURES";
	public static final String ASTHMA_CLASSIFICATION_CONCEPT = "Asthma classification";

	public static final String MODERATE_PERSISTENT_CONCEPT = "Moderate persistent";
	public static final String SEVERE_PERSISTENT_CONCEPT = "Severe persistent";
	public static final String SEVERE_UNCONTROLLED_CONCEPT = "Severe uncontrolled";

	public static final String OTHER_NON_CODED_CONCEPT = "Other non-coded";
	public static final String TRUE_CONCEPT = "True";
	public static final String YES_CONCEPT = "Yes";
	public static final String OTHER_CONCEPT = "Other";

	public static final String BECLOMETHASONE_CONCEPT = "Beclomethasone";
	public static final String INSULIN_CONCEPT = "Insulin";

	public static final String OUTPATIENT_CONSULTATION_CONCEPT = "Outpatient consultation";
	public static final String INPATIENT_WARD_CONCEPT = "Ward";
	public static final String HEALTH_CENTER_CONCEPT = "Health center";

	public static final String REASON_FOR_EXITING_CARE_CONCEPT = "REASON FOR EXITING CARE";
	public static final String DIED_CONCEPT = "Patient died";

    public static final String ARV_REGIMEN_2A_CONCEPT = "657ab520-977f-11e1-8993-905e29aff6c1";
    public static final String ARV_REGIMEN_4A_CONCEPT = "657abd9a-977f-11e1-8993-905e29aff6c1";
    public static final String ARV_REGIMEN_6A_CONCEPT = "657ac164-977f-11e1-8993-905e29aff6c1";

	public static final String PERIPHERAL_NEUROPATHY_CONCEPT = "Peripheral neuropathy";
	public static final String HEPATITIS_CONCEPT = "Hepatitis";
	public static final String SKIN_RASH_CONCEPT = "Skin rash";
	public static final String LIPODYSTROPHY_CONCEPT = "Lipodystrophy";

    public static final String SICKLE_CELL_DISEASE_CONCEPT = "Sickle cell disease";
    public static final String CHRONIC_KIDNEY_DISEASE_CONCEPT = "Chronic kidney disease";
    public static final String RHEUMATIC_HEART_DISEASE_CONCEPT = "Rheumatic heart disease";
    public static final String CONGESTIVE_HEART_FAILURE_CONCEPT = "Congestive heart failure";

    public static final String DIAGNOSIS_DATE = "Diagnosis date";

	public Concept getAppointmentDateConcept() {
		return getConcept(APPOINTMENT_DATE);
	}

	public Concept getHeightConcept() {
		return getConcept(HEIGHT);
	}

	public Concept getWeightConcept() {
		return getConcept(WEIGHT);
	}

	public Concept getCd4CountConcept() {
		return getConcept(CD4_COUNT);
	}

    public Concept getCd4DateConcept() {
        return getConcept(CD4_DATE);
    }

	public Concept getClinicianReportedCd4Concept() {
		return getConcept(CLINICIAN_REPORTED_CD4);
	}

	// HIV TESTING

    public Concept getChildHivSerologyConstruct() { return getConcept(CHILD_HIV_SEROLOGY_CONSTRUCT); }

    public Concept getHivTestSampleDateConcept() { return getConcept(HIV_TEST_SAMPLE_DATE); }

    public Concept getHivTestResultDateConcept() { return getConcept(HIV_TEST_RESULT_DATE); }

    public Concept getHivTestResult() { return getConcept(HIV_TEST_RESULT); }

    public Concept getHivTestType() { return getConcept(HIV_TEST_TYPE); }

    public Concept getHivRapidTest() { return getConcept(HIV_RAPID_TEST); }

    public Concept getHivDnaPcrTest() { return getConcept(HIV_DNA_PCR); }

    public List<Concept> getAllHivTestResultConcepts() {
	    List<Concept> ret = new ArrayList<Concept>();
	    ret.add(getHivTestResult());
	    ret.addAll(getHivRapidTestResultConcepts());
	    ret.addAll(getHivDnaPcrTestResultConcepts());
	    return ret;
    }

    public List<Concept> getHivRapidTestResultConcepts() {
        return Arrays.asList(getConcept(HIV_RAPID_TEST));
    }

    public List<Concept> getHivDnaPcrTestResultConcepts() {
	    List<Concept> l = new ArrayList<Concept>();
	    for (String uuid : HIV_DNA_PCR_RESULT_CONCEPTS) {
	        l.add(getConcept(uuid));
        }
        return l;
    }

    public Concept getReactiveConcept() {
        return getConcept(REACTIVE);
    }

    public Concept getPositiveConcept() {
        return getConcept(POSITIVE);
    }

    public Concept getNonReactiveConcept() {
        return getConcept(NON_REACTIVE);
    }

    public Concept getNegativeConcept() {
        return getConcept(NEGATIVE);
    }

    public List<Concept> getHivPositiveResultConcepts() {
        return Arrays.asList(getConcept(POSITIVE), getConcept(REACTIVE));
    }

    public List<Concept> getHivNegativeResultConcepts() {
        return Arrays.asList(getConcept(NEGATIVE), getConcept(NON_REACTIVE));
    }

    // Viral Load Testing

    public Concept getHivViralLoadTestSetConcept() {
        return getConcept(HIV_VIRAL_LOAD_TEST_SET);
    }

    public Concept getHivViralLoadConcept() {
        return getConcept(HIV_VIRAL_LOAD);
    }

    public Concept getHivLDLConcept() {
        return getConcept(HIV_VIRAL_LDL);
    }

    public Concept getHivViralLoadSpecimenCollectedConcept() {
	    return getConcept(HIV_VIRAL_LOAD_SPECIMEN_COLLECTED);
    }

	public Concept getReasonForTestingConcept() {
		return getConcept(REASON_FOR_TESTING);
	}

    public Concept getRoutineConcept() {
        return getConcept(ROUTINE);
    }

    public Concept getReasonNoResultConcept() {
        return getConcept(REASON_NO_RESULT);
    }

    // Adherence Counselling

    public Concept getAdherenceCounselingSessionNumberConcept() { return getConcept(ADHERENCE_COUNSELING_SESSION_NUMBER_CONCEPT); }

	// Other

	public Concept getEverReceivedArtConcept() {
		return getConcept(EVER_RECEIVED_ART_CONCEPT);
	}

	public Concept getKsSideEffectsWorseningOnArvsConcept() {
		return getConcept(KS_SIDE_EFFECTS_WORSENING_ON_ARVS);
	}

	public Concept getTbTreatmentStatusConcept() {
		return getConcept(TB_TREATMENT_STATUS);
	}

	public Concept getWhoStageConcept() {
		return getConcept(WHO_STAGE);
	}

    public Concept getWhoClinicalConditionsConcept() {
        return getConcept(WHO_CLINICAL_CONDITIONS);
    }

	public Concept getCd4PercentConcept() {
		return getConcept(CD4_PERCENT);
	}

	public Concept getPresumedSevereHivCriteriaPresentConcept() {
		return getConcept(PRESUMED_SEVERE_HIV_CRITERIA_PRESENT);
	}

	public Concept getArvDrugsReceivedConcept() {
		return getConcept(ARV_DRUGS_RECEIVED);
	}

    public Concept getArvDrugsChange1Concept() {
        return getConcept(ARV_DRUGS_CHANGE_1);
    }

    public Concept getArvDrugsChange2Concept() {
        return getConcept(ARV_DRUGS_CHANGE_2);
    }

    public Concept getArvDrugsChange3Concept() {
        return getConcept(ARV_DRUGS_CHANGE_3);
    }

    public Concept getDateOfStartingFirstLineArvsConcept() {
        return getConcept(DATE_OF_STARTING_FIRST_LINE_ARVS);
    }

    public Concept getDateOfStartingAlternativeFirstLineArvsConcept() {
        return getConcept(DATE_OF_STARTING_ALTERNATIVE_FIRST_LINE_ARVS);
    }

    public Concept getDateOfStartingSecondLineArvsConcept() {
        return getConcept(DATE_OF_STARTING_SECOND_LINE_ARVS);
    }

	public Concept getTbStatusConcept() {
		return getConcept(TB_STATUS);
	}
	public Concept getMotherArtNumberConcept() {
		return getConcept(MOTHER_ART_NUMBER);
	}

	public Concept getTbNotSuspectedConcept() {
		return getConcept(TB_NOT_SUSPECTED_CONCEPT);
	}

	public Concept getTbSuspectedConcept() {
		return getConcept(TB_SUSPECTED_CONCEPT);
	}

	public Concept getTbConfirmedNotOnTreatmentConcept() {
		return getConcept(TB_CONFIRMED_NOT_ON_TX_CONCEPT);
	}

	public Concept getTbConfirmedOnTreatmentConcept() {
		return getConcept(TB_CONFIRMED_ON_TX_CONCEPT);
	}

	public Concept getArtSideEffectsConcept() {
		return getConcept(ART_SIDE_EFFECTS);
	}

	public Concept getPregnantOrLactatingConcept() {
		return getConcept(PREGNANT_OR_LACTATING_CONCEPT);
	}

	public Concept getPatientPregnantConcept() {
		return getConcept(PATIENT_PREGNANT_CONCEPT);
	}

	public Concept getSourceOfReferralConcept() {
		return getConcept(SOURCE_OF_REFERRAL_CONCEPT);
	}

	public Concept getCurrentDrugsUsedConcept() {
		return getConcept(CURRENT_DRUGS_USED_CONCEPT);
	}

	public Concept getHospitalizedSinceLastVisitConcept() {
		return getConcept(HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT);
	}

	public Concept getHospitalizedForNcdSinceLastVisitConcept() {
		return getConcept(HOSPITALIZED_FOR_NCD_SINCE_LAST_VISIT_CONCEPT);
	}

	public Concept getSystolicBloodPressureConcept() {
		return getConcept(SYSTOLIC_BLOOD_PRESSURE_CONCEPT);
	}

	public Concept getDiastolicBloodPressureConcept() {
		return getConcept(DIASTOLIC_BLOOD_PRESSURE_CONCEPT);
	}

	public Concept getBloodSugarTestTypeConcept() {
		return getConcept(BLOOD_SUGAR_TEST_TYPE_CONCEPT);
	}

	public Concept getNumberOfSeizuresConcept() {
		return getConcept(NUMBER_OF_SEIZURES_CONCEPT);
	}

	public Concept getAsthmaClassificationConcept() {
		return getConcept(ASTHMA_CLASSIFICATION_CONCEPT);
	}

	public Concept getModeratePersistentConcept() {
		return getConcept(MODERATE_PERSISTENT_CONCEPT);
	}

	public Concept getSeverePersistentConcept() {
		return getConcept(SEVERE_PERSISTENT_CONCEPT);
	}

	public Concept getSevereUncontrolledConcept() {
		return getConcept(SEVERE_UNCONTROLLED_CONCEPT);
	}

	public Concept getOtherNonCodedConcept() {
		return getConcept(OTHER_NON_CODED_CONCEPT);
	}

	public Concept getTrueConcept() {
		return getConcept(TRUE_CONCEPT);
	}

	public Concept getYesConcept() {
		return getConcept(YES_CONCEPT);
	}

	public Concept getOtherConcept() {
		return getConcept(OTHER_CONCEPT);
	}

	public Concept getBeclomethasoneConcept() {
		return getConcept(BECLOMETHASONE_CONCEPT);
	}

	public Concept getInsulinConcept() {
		return getConcept(INSULIN_CONCEPT);
	}

	public Concept getOutpatientConsultationConcept() {
		return getConcept(OUTPATIENT_CONSULTATION_CONCEPT);
	}

	public Concept getInpatientWardConcept() {
		return getConcept(INPATIENT_WARD_CONCEPT);
	}

	public Concept getHealthCenterConcept() {
		return getConcept(HEALTH_CENTER_CONCEPT);
	}

	public Concept getReasonForExitingCareConcept() {
		return getConcept(REASON_FOR_EXITING_CARE_CONCEPT);
	}

	public Concept getDiedConcept() {
		return getConcept(DIED_CONCEPT);
	}

	public Concept getArvRegimen2aConcept() {
		return getConcept(ARV_REGIMEN_2A_CONCEPT);
	}

	public Concept getArvRegimen4aConcept() {
		return getConcept(ARV_REGIMEN_4A_CONCEPT);
	}

	public Concept getArvRegimen6aConcept() {
		return getConcept(ARV_REGIMEN_6A_CONCEPT);
	}

	public Concept getPeripheralNeuropathyConcept() {
		return getConcept(PERIPHERAL_NEUROPATHY_CONCEPT);
	}

	public Concept getHepatitisConcept() {
		return getConcept(HEPATITIS_CONCEPT);
	}

	public Concept getSkinRashConcept() {
		return getConcept(SKIN_RASH_CONCEPT);
	}

	public Concept getLipodystrophyyConcept() {
		return getConcept(LIPODYSTROPHY_CONCEPT);
	}

    public Concept getSickleCellDiseaseConcept() {
        return getConcept(SICKLE_CELL_DISEASE_CONCEPT);
    }

    public Concept getChronicKidneyDiseaseConcept() {
        return getConcept(CHRONIC_KIDNEY_DISEASE_CONCEPT);
    }

    public Concept getRheumaticHeartDiseaseConcept() {
        return getConcept(RHEUMATIC_HEART_DISEASE_CONCEPT);
    }

    public Concept getCongestiveHeartFailureConcept() {
        return getConcept(CONGESTIVE_HEART_FAILURE_CONCEPT);
    }

    public Concept getDiagnosisDateConcept() {
        return getConcept(DIAGNOSIS_DATE);
    }

	public static String NENO_HOSPITAL = "Neno District Hospital";
	public static String OUTPATIENT_LOCATION = "Outpatient";
	public static String REGISTRATION_LOCATION = "Registration";
	public static String VITALS_LOCATION = "Vitals";
	public static String LIGOWE_HC  = "Ligowe HC";
	public static String LISUNGWI_HOSPITAL = "Lisungwi Community Hospital";
	public static String MIDZEMBA_HC = "Midzemba HC";

	public Location getNenoHospital() {
		return getLocation(NENO_HOSPITAL);
	}

	public Location getOutpatientLocation() {
		return getLocation(OUTPATIENT_LOCATION);
	}

	public Location getRegistrationLocation() {
		return getLocation(REGISTRATION_LOCATION);
	}

	public Location getVitalsLocation() {
		return getLocation(VITALS_LOCATION);
	}

	public Location getLigoweHc() {
		return getLocation(LIGOWE_HC);
	}

	public Location getLisungwiHospital() {
		return getLocation(LISUNGWI_HOSPITAL);
	}

	public Location getMidzembaHc() {
		return getLocation(MIDZEMBA_HC);
	}

	public List<Location> getPrimaryFacilities() {
		List<Location> l = getUpperNenoFacilities();
        l.addAll(getLowerNenoFacilities());
		return l;
	}

	public List<Location> getUpperNenoFacilities() {
        return getLocationsForTag(LocationTags.UPPER_NENO.name());
	}

	public List<Location> getLowerNenoFacilities() {
        return getLocationsForTag(LocationTags.LOWER_NENO.name());
	}

	public List<Location> getAllLocations(Location primaryFacility) {
		List<Location> l = new ArrayList<Location>();
		l.add(primaryFacility);
		if (primaryFacility.equals(getNenoHospital())) {
			l.add(getOutpatientLocation());
			l.add(getRegistrationLocation());
			l.add(getVitalsLocation());
		}
		else if (primaryFacility.equals(getLisungwiHospital())) {
			l.add(getMidzembaHc());
		}
		return l;
	}

	public String getCurrentSystemLocationTag() {
        return getGlobalProperty(CURRENT_SYSTEM_LOCATION_TAG_GLOBAL_PROPERTY, "");
    }

	public List<Location> getSystemLocations() {
		String upperOrLower = getGlobalProperty(CURRENT_SYSTEM_LOCATION_TAG_GLOBAL_PROPERTY, null);
		if (UPPER_NENO.equals(upperOrLower)) {
			return getUpperNenoFacilities();
		}
		else if (LOWER_NENO.equals(upperOrLower)) {
			return getLowerNenoFacilities();
		}
		return getPrimaryFacilities();
	}

	public Map<Location, String> getLocationShortNames() {
		Map<Location, String> locationShortNames = new HashMap<Location, String>();
        LocationAttributeType locationCode = getLocationAttributeType(LocationAttributeTypes.LOCATION_CODE.uuid());
        for (Location l : Context.getLocationService().getAllLocations()) {
            String code = l.getName();
            List<LocationAttribute> codes = l.getActiveAttributes(locationCode);
            if (codes != null && codes.size() > 0) {
                code = codes.get(0).getValueReference();
            }
            locationShortNames.put(l, code);
        }
        return locationShortNames;
	}
}