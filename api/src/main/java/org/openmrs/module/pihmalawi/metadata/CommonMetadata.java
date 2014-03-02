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
	public static final String WEIGHT = "Weight (kg)";
	public static final String CD4_COUNT = "CD4 count";
	public static final String CLINICIAN_REPORTED_CD4 = "Clinician reported to CD4";

	public static final String KS_SIDE_EFFECTS_WORSENING_ON_ARVS = "Kaposis sarcoma side effects worsening while on ARVs?";
	public static final String TB_TREATMENT_STATUS = "Tuberculosis treatment status";
	public static final String WHO_STAGE = "WHO stage";
	public static final String CD4_PERCENT = "Cd4%";
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

	public Concept getAppointmentDateConcept() {
		return getConcept(APPOINTMENT_DATE);
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
}