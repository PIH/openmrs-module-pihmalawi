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
import org.openmrs.RelationshipType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommonMetadata extends Metadata {

	public static final String CURRENT_SYSTEM_LOCATION_TAG_GLOBAL_PROPERTY = "pihmalawi.systemLocationsTag";

	public static final String CHW_RELATIONSHIP_TYPE = "Patient/Village Health Worker";
	public static final String GUARDIAN_RELATIONSHIP_TYPE = "Patient/Guardian";

	public RelationshipType getChwRelationshipType() {
		return getRelationshipType(CHW_RELATIONSHIP_TYPE);
	}

	public RelationshipType getGuardianRelationshipType() {
		return getRelationshipType(GUARDIAN_RELATIONSHIP_TYPE);
	}

	public static final String LAB_ENCOUNTER_TYPE = "LAB";

	public EncounterType getLabEncounterType() {
		return getEncounterType(LAB_ENCOUNTER_TYPE);
	}

	public static final String APPOINTMENT_DATE = "Appointment date";
	public static final String HEIGHT = "Height (cm)";
	public static final String WEIGHT = "Weight (kg)";
	public static final String CD4_COUNT = "CD4 count";
	public static final String CLINICIAN_REPORTED_CD4 = "Clinician reported to CD4";

	public static final String KS_SIDE_EFFECTS_WORSENING_ON_ARVS = "Kaposis sarcoma side effects worsening while on ARVs?";
	public static final String TB_TREATMENT_STATUS = "Tuberculosis treatment status";
	public static final String WHO_STAGE = "WHO stage";
	public static final String CD4_PERCENT = "CD4 percent";
	public static final String PRESUMED_SEVERE_HIV_CRITERIA_PRESENT = "Presumed severe HIV criteria present";
	public static final String FIRST_LINE_ARV_START_DATE = "Start date 1st line ARV";
	public static final String ARV_DRUGS_RECEIVED = "Malawi Antiretroviral drugs received";
	public static final String TB_STATUS = "TB status";
	public static final String ART_SIDE_EFFECTS = "Malawi ART side effects";
	public static final String HIV_DNA_PCR = "HIV DNA polymerase chain reaction";
	public static final String DNA_PCR_RESULT = "DNA-PCR Testing Result";
	public static final String DNA_PCR_RESULT_2 = "DNA-PCR Testing Result 2";
	public static final String DNA_PCR_RESULT_3 = "DNA-PCR Testing Result 3";
	public static final String NEGATIVE = "Negative";
	public static final String POSITIVE = "Positive";
	public static final String INDETERMINATE = "Indeterminate";

	public static final String HIV_STATUS_CONCEPT = "HIV status";
	public static final String ON_ART_CONCEPT = "On ART";
	public static final String TB_HISTORY_CONCEPT = "Previous or current tuberculosis treatment";
	public static final String CLINIC_TRAVEL_TIME_HOURS_CONCEPT = "Clinic travel time in hours";
	public static final String CLINIC_TRAVEL_TIME_MINUTES_CONCEPT = "Clinic travel time in minutes";
	public static final String HIGH_RISK_PATIENT_CONCEPT = "High risk patient";
	public static final String WALL_MATERIAL_CONCEPT = "Wall material";
	public static final String ROOF_MATERIAL_CONCEPT = "Roof material";
	public static final String HOME_ELECTRICITY_CONCEPT = "Home electricity";
	public static final String PATIENT_OWNS_RADIO_CONCEPT = "Patient owns radio";
	public static final String ACCESS_TO_BICYCLE_CONCEPT = "Access to bicycle";
	public static final String LOCATION_OF_COOKING_CONCEPT = "Location of cooking";
	public static final String FUEL_SOUCE_CONCEPT = "Fuel source";
	public static final String NUMBER_FRUIT_AND_VEGETABLES_CONCEPT = "Number of servings of fruits and vegetables consumed per day";
	public static final String SMOKING_HISTORY_CONCEPT = "Smoking history";
	public static final String NUMBER_CIGARRETES_PER_DAY_CONCEPT = "Number of cigarettes smoked per day";
	public static final String HISTORY_OF_ALCOHOL_USE_CONCEPT = "History of alcohol use";
	public static final String LITERS_ALCOHOL_PER_DAY_CONCEPT = "Liters per day";
	public static final String SOURCE_OF_REFERRAL_CONCEPT = "Type of referring clinic or hospital";

	public static final String CURRENT_DRUGS_USED_CONCEPT = "Current drugs used";
	public static final String PREFERRED_TX_OUT_OF_STOCK_CONCEPT = "Preferred treatment out of stock";
	public static final String CHANGE_IN_TREATMENT_CONCEPT = "Change in treatment";
	public static final String HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT = "Patient hospitalized since last visit";
	public static final String HOSPITALIZED_FOR_NCD_SINCE_LAST_VISIT_CONCEPT = "Hospitalized for non-communicable disease since last visit";
	public static final String SYSTOLIC_BLOOD_PRESSURE_CONCEPT = "Systolic blood pressure";
	public static final String DIASTOLIC_BLOOD_PRESSURE_CONCEPT = "Diastolic blood pressure";
	public static final String NYHA_CLASS_CONCEPT = "Nyha class";
	public static final String SERUM_GLUCOSE_CONCEPT = "Serum glucose";
	public static final String BLOOD_SUGAR_TEST_TYPE_CONCEPT = "Blood sugar test type";
	public static final String NUMBER_OF_SEIZURES_CONCEPT = "NUMBER OF SEIZURES";
	public static final String PEAK_FLOW_CONCEPT = "PEAK FLOW";
	public static final String PEAK_FLOW_PREDICTED_CONCEPT = "PEAK FLOW PREDICTED";
	public static final String ASTHMA_CLASSIFICATION_CONCEPT = "Asthma classification";
	public static final String PATIENT_VISIT_COMPLETED_WITH_ALL_SERVICES_CONCEPT = "Patient visit completed with all services delivered";
	public static final String DATA_CLERK_COMMENTS_CONCEPT = "Data clerk comments";



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

	public Concept getClinicianReportedCd4Concept() {
		return getConcept(CLINICIAN_REPORTED_CD4);
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

	public Concept getCd4PercentConcept() {
		return getConcept(CD4_PERCENT);
	}

	public Concept getPresumedSevereHivCriteriaPresentConcept() {
		return getConcept(PRESUMED_SEVERE_HIV_CRITERIA_PRESENT);
	}

	public Concept getFirstLineArvStartDateConcept() {
		return getConcept(FIRST_LINE_ARV_START_DATE);
	}

	public Concept getArvDrugsReceivedConcept() {
		return getConcept(ARV_DRUGS_RECEIVED);
	}

	public Concept getTbStatusConcept() {
		return getConcept(TB_STATUS);
	}

	public Concept getArtSideEffectsConcept() {
		return getConcept(ART_SIDE_EFFECTS);
	}

	public Concept getHivDnaPcrTestConcept() {
		return getConcept(HIV_DNA_PCR);
	}

	public Concept getDnaPcrResultConcept() {
		return getConcept(DNA_PCR_RESULT);
	}

	public Concept getDnaPcrResult2Concept() {
		return getConcept(DNA_PCR_RESULT_2);
	}

	public Concept getDnaPcrResult3Concept() {
		return getConcept(DNA_PCR_RESULT_3);
	}

	public Concept getNegativeConcept() {
		return getConcept(NEGATIVE);
	}

	public Concept getPositiveConcept() {
		return getConcept(POSITIVE);
	}

	public Concept getIndeterminateConcept() {
		return getConcept(INDETERMINATE);
	}

	public Concept getHivStatusConcept() {
		return getConcept(HIV_STATUS_CONCEPT);
	}

	public Concept getOnArtConcept() {
		return getConcept(ON_ART_CONCEPT);
	}

	public Concept getTbHistoryConcept() {
		return getConcept(TB_HISTORY_CONCEPT);
	}

	public Concept getClinicTravelTimeInHoursConcept() {
		return getConcept(CLINIC_TRAVEL_TIME_HOURS_CONCEPT);
	}

	public Concept getClinicTravelTimeInMinutesConcept() {
		return getConcept(CLINIC_TRAVEL_TIME_MINUTES_CONCEPT);
	}

	public Concept getHighRiskPatientConcept() {
		return getConcept(HIGH_RISK_PATIENT_CONCEPT);
	}

	public Concept getWallMaterialConcept() {
		return getConcept(WALL_MATERIAL_CONCEPT);
	}

	public Concept getRoofMaterialConcept() {
		return getConcept(ROOF_MATERIAL_CONCEPT);
	}

	public Concept getHomeElectricityConcept() {
		return getConcept(HOME_ELECTRICITY_CONCEPT);
	}

	public Concept getPatientOwnsRadioConcept() {
		return getConcept(PATIENT_OWNS_RADIO_CONCEPT);
	}

	public Concept getAccessToBicycleConcept() {
		return getConcept(ACCESS_TO_BICYCLE_CONCEPT);
	}

	public Concept getLocationOfCookingConcept() {
		return getConcept(LOCATION_OF_COOKING_CONCEPT);
	}

	public Concept getFuelSourceConcept() {
		return getConcept(FUEL_SOUCE_CONCEPT);
	}

	public Concept getNumberOfFruitAndVegatablesConcept() {
		return getConcept(NUMBER_FRUIT_AND_VEGETABLES_CONCEPT);
	}

	public Concept getSmokingHistoryConcept() {
		return getConcept(SMOKING_HISTORY_CONCEPT);
	}

	public Concept getNumberOfCigarettesPerDayConcept() {
		return getConcept(NUMBER_CIGARRETES_PER_DAY_CONCEPT);
	}

	public Concept getHistoryOfAlcoholUseConcept() {
		return getConcept(HISTORY_OF_ALCOHOL_USE_CONCEPT);
	}

	public Concept getLitersOfAlcoholPerDayConcept() {
		return getConcept(LITERS_ALCOHOL_PER_DAY_CONCEPT);
	}

	public Concept getSourceOfReferralConcept() {
		return getConcept(SOURCE_OF_REFERRAL_CONCEPT);
	}

	public Concept getCurrentDrugsUsedConcept() {
		return getConcept(CURRENT_DRUGS_USED_CONCEPT);
	}

	public Concept getPreferredTreatmentOutOfStockConcept() {
		return getConcept(PREFERRED_TX_OUT_OF_STOCK_CONCEPT);
	}

	public Concept getChangeInTreatmentConcept() {
		return getConcept(CHANGE_IN_TREATMENT_CONCEPT);
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

	public Concept getNyhaClassConcept() {
		return getConcept(NYHA_CLASS_CONCEPT);
	}

	public Concept getSerumGlucoseConcept() {
		return getConcept(SERUM_GLUCOSE_CONCEPT);
	}

	public Concept getBloodSugarTestTypeConcept() {
		return getConcept(BLOOD_SUGAR_TEST_TYPE_CONCEPT);
	}

	public Concept getNumberOfSeizuresConcept() {
		return getConcept(NUMBER_OF_SEIZURES_CONCEPT);
	}

	public Concept getPeakFlowConcept() {
		return getConcept(PEAK_FLOW_CONCEPT);
	}

	public Concept getPeakFlowPredictedConcept() {
		return getConcept(PEAK_FLOW_PREDICTED_CONCEPT);
	}

	public Concept getAsthmaClassificationConcept() {
		return getConcept(ASTHMA_CLASSIFICATION_CONCEPT);
	}

	public Concept getPatientVisitCompletedWithAllServicesConcept() {
		return getConcept(PATIENT_VISIT_COMPLETED_WITH_ALL_SERVICES_CONCEPT);
	}

	public Concept getDataClerkCommentsConcept() {
		return getConcept(DATA_CLERK_COMMENTS_CONCEPT);
	}

	public static String NENO_HOSPITAL = "Neno District Hospital";
	public static String OUTPATIENT_LOCATION = "Outpatient";
	public static String REGISTRATION_LOCATION = "Registration";
	public static String VITALS_LOCATION = "Vitals";
	public static String MAGALETA_HC  = "Magaleta HC";
	public static String NSAMBE_HC  = "Nsambe HC";
	public static String LIGOWE_HC  = "Ligowe HC";
	public static String MATANDANI_HC = "Matandani Rural Health Center";
	public static String NENO_MISSION_HC = "Neno Mission HC";
	public static String LUWANI_HC = "Luwani RHC";
	public static String LISUNGWI_HOSPITAL = "Lisungwi Community Hospital";
	public static String MIDZEMBA_HC = "Midzemba HC";
	public static String CHIFUNGA_HC = "Chifunga HC";
	public static String MATOPE_HC = "Matope HC";
	public static String NKHULA_FALLS_HC = "Nkhula Falls RHC";
	public static String ZALEWA_HC = "Zalewa HC";

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

	public Location getMagaletaHc() {
		return getLocation(MAGALETA_HC);
	}

	public Location getNsambeHc() {
		return getLocation(NSAMBE_HC);
	}

	public Location getLigoweHc() {
		return getLocation(LIGOWE_HC);
	}

	public Location getMatandaniHc() {
		return getLocation(MATANDANI_HC);
	}

	public Location getNenoMissionHc() {
		return getLocation(NENO_MISSION_HC);
	}

	public Location getLuwaniHc() {
		return getLocation(LUWANI_HC);
	}

	public Location getLisungwiHospital() {
		return getLocation(LISUNGWI_HOSPITAL);
	}

	public Location getMidzembaHc() {
		return getLocation(MIDZEMBA_HC);
	}

	public Location getChifungaHc() {
		return getLocation(CHIFUNGA_HC);
	}

	public Location getMatopeHc() {
		return getLocation(MATOPE_HC);
	}

	public Location getNkhulaFallsHc() {
		return getLocation(NKHULA_FALLS_HC);
	}

	public Location getZalewaHc() {
		return getLocation(ZALEWA_HC);
	}

	public List<Location> getPrimaryFacilities() {
		List<Location> l = new ArrayList<Location>();
		l.add(getNenoHospital());
		l.add(getMagaletaHc());
		l.add(getNsambeHc());
		l.add(getNenoMissionHc());
		l.add(getLigoweHc());
		l.add(getMatandaniHc());
		l.add(getLuwaniHc());
		l.add(getLisungwiHospital());
		l.add(getMatopeHc());
		l.add(getChifungaHc());
		l.add(getZalewaHc());
		l.add(getMidzembaHc());
		l.add(getNkhulaFallsHc());
		return l;
	}

	public List<Location> getUpperNenoFacilities() {
		List<Location> l = new ArrayList<Location>();
		l.add(getNenoHospital());
		l.add(getMagaletaHc());
		l.add(getNsambeHc());
		l.add(getNenoMissionHc());
		l.add(getLigoweHc());
		l.add(getMatandaniHc());
		l.add(getLuwaniHc());
		return l;
	}

	public List<Location> getLowerNenoFacilities() {
		List<Location> l = new ArrayList<Location>();
		l.add(getLisungwiHospital());
		l.add(getMatopeHc());
		l.add(getChifungaHc());
		l.add(getZalewaHc());
		l.add(getMidzembaHc());
		l.add(getNkhulaFallsHc());
		return l;
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

	public List<Location> getSystemLocations() {
		String tagName = getGlobalProperty(CURRENT_SYSTEM_LOCATION_TAG_GLOBAL_PROPERTY, null);
		if (tagName == null) {
			return getPrimaryFacilities();
		}
		return getLocationsForTag(tagName);
	}
}