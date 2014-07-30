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

import org.openmrs.module.pihmalawi.metadata.reference.ConceptClassReference;
import org.openmrs.module.pihmalawi.metadata.reference.ConceptDatatypeReference;
import org.openmrs.module.pihmalawi.metadata.reference.ConceptReference;

public class Concepts {

	// Concept Class

	public static final ConceptClassReference TEST_CLASS = new ConceptClassReference("Test");
	public static final ConceptClassReference PROCEDURE_CLASS = new ConceptClassReference("Procedure");
	public static final ConceptClassReference DRUG_CLASS = new ConceptClassReference("Drug");
	public static final ConceptClassReference DIAGNOSIS_CLASS = new ConceptClassReference("Diagnosis");
	public static final ConceptClassReference FINDING_CLASS = new ConceptClassReference("Finding");
	public static final ConceptClassReference ANATOMY_CLASS = new ConceptClassReference("Anatomy");
	public static final ConceptClassReference QUESTION_CLASS = new ConceptClassReference("Question");
	public static final ConceptClassReference LAB_SET_CLASS = new ConceptClassReference("LabSet");
	public static final ConceptClassReference MED_SET_CLASS = new ConceptClassReference("MedSet");
	public static final ConceptClassReference CONVENIENCE_SET_CLASS = new ConceptClassReference("ConvSet");
	public static final ConceptClassReference MISC_CLASS = new ConceptClassReference("Misc");
	public static final ConceptClassReference SYMPTOM_CLASS = new ConceptClassReference("Symptom");
	public static final ConceptClassReference SYMPTOM_FINDING_CLASS = new ConceptClassReference("Symptom/Finding");
	public static final ConceptClassReference SPECIMEN_CLASS = new ConceptClassReference("Specimen");
	public static final ConceptClassReference MISC_ORDER_CLASS = new ConceptClassReference("Misc Order");
	public static final ConceptClassReference PROGRAM_CLASS = new ConceptClassReference("Program");
	public static final ConceptClassReference WORKFLOW_CLASS = new ConceptClassReference("Workflow");
	public static final ConceptClassReference STATE_CLASS = new ConceptClassReference("State");
	public static final ConceptClassReference REQUEST_CLASS = new ConceptClassReference("Request");
	public static final ConceptClassReference SPECIMEN_SAMPLE_TYPE_CLASS = new ConceptClassReference("Specimen Sample Type");

	// Concept Datatype

	public static final ConceptDatatypeReference NUMERIC_CONCEPT_DATATYPE = new ConceptDatatypeReference("Numeric");
	public static final ConceptDatatypeReference CODED_CONCEPT_DATATYPE = new ConceptDatatypeReference("Coded");
	public static final ConceptDatatypeReference TEXT_CONCEPT_DATATYPE = new ConceptDatatypeReference("Text");
	public static final ConceptDatatypeReference NA_CONCEPT_DATATYPE = new ConceptDatatypeReference("N/A");
	public static final ConceptDatatypeReference DOCUMENT_CONCEPT_DATATYPE = new ConceptDatatypeReference("Document");
	public static final ConceptDatatypeReference DATE_CONCEPT_DATATYPE = new ConceptDatatypeReference("Date");
	public static final ConceptDatatypeReference TIME_CONCEPT_DATATYPE = new ConceptDatatypeReference("Time");
	public static final ConceptDatatypeReference DATETIME_CONCEPT_DATATYPE = new ConceptDatatypeReference("Datetime");
	public static final ConceptDatatypeReference BOOLEAN_CONCEPT_DATATYPE = new ConceptDatatypeReference("Boolean");
	public static final ConceptDatatypeReference RULE_CONCEPT_DATATYPE = new ConceptDatatypeReference("Rule");
	public static final ConceptDatatypeReference STRUCTURED_NUMERIC_CONCEPT_DATATYPE = new ConceptDatatypeReference("Structured Numeric");
	public static final ConceptDatatypeReference COMPLEX_CONCEPT_DATATYPE = new ConceptDatatypeReference("Complex");

	// Drug Concepts

	public static final ConceptReference SALBUTAMOL = new ConceptReference("Salbutamol");
	public static final ConceptReference BECLOMETHASONE = new ConceptReference("Beclomethasone");
	public static final ConceptReference HYDROCHLOROTHIAZIDE = new ConceptReference("Hydrochlorothiazide");
	public static final ConceptReference CAPTOPRIL = new ConceptReference("Captopril");
	public static final ConceptReference AMLODIPINE = new ConceptReference("Amlodipine");
	public static final ConceptReference ENLAPRIL = new ConceptReference("Enalapril");
	public static final ConceptReference NIFEDIPINE = new ConceptReference("Nifedipine");
	public static final ConceptReference ATENOLOL = new ConceptReference("Atenolol");
	public static final ConceptReference LISINOPRIL = new ConceptReference("Lisinopril");
	public static final ConceptReference PROPRANOLOL = new ConceptReference("Propranolol");
	public static final ConceptReference PHENOBARBITOL = new ConceptReference("Phenobarbital");
	public static final ConceptReference PHENYTOIN = new ConceptReference("Phenytoin");
	public static final ConceptReference CARBAMAZEPINE = new ConceptReference("Carbamazepine");
	public static final ConceptReference INSULIN = new ConceptReference("Insulin");
	public static final ConceptReference METFORMIN = new ConceptReference("Metformin");
	public static final ConceptReference GLIBENCLAMIDE = new ConceptReference("Glibenclamide");
	public static final ConceptReference FUROSEMIDE = new ConceptReference("Furosemide");
	public static final ConceptReference SPIRONOLACTONE = new ConceptReference("Spironolactone");

	// Diagnosis Concepts

	public static final ConceptReference ASTHMA = new ConceptReference("Asthma");
	public static final ConceptReference HYPERTENSION = new ConceptReference("Hypertension");
	public static final ConceptReference EPILEPSY = new ConceptReference("Epilepsy");
	public static final ConceptReference DIABETES = new ConceptReference("Diabetes");
	public static final ConceptReference HEART_FAILURE = new ConceptReference("Heart failure");
	public static final ConceptReference CHRONIC_KIDNEY_DISEASE = new ConceptReference("Chronic kidney disease");
	public static final ConceptReference STROKE = new ConceptReference("Stroke");
	public static final ConceptReference DEPRESSION = new ConceptReference("Depression");
	public static final ConceptReference SUBSTANCE_ABUSE = new ConceptReference("Substance abuse");
	public static final ConceptReference ACUTE_PSYCHOTIC_DISORDER = new ConceptReference("Acute Psychotic disorder");
	public static final ConceptReference OTHER_MENTAL_HEALTH_DIAGNOSIS_NON_CODED = new ConceptReference("Other Mental Health Diagnosis non-coded");
	public static final ConceptReference OTHER_NON_CODED = new ConceptReference("Other non-coded");

	// Questions

	public static final ConceptReference CHRONIC_CARE_DIAGNOSIS = new ConceptReference("Chronic care diagnosis");
	public static final ConceptReference APPOINTMENT_DATE = new ConceptReference("Appointment date");
	public static final ConceptReference HEIGHT = new ConceptReference("Height (cm)");
	public static final ConceptReference WEIGHT = new ConceptReference("Weight (kg)");
	public static final ConceptReference CD4_COUNT = new ConceptReference("CD4 count");
	public static final ConceptReference CLINICIAN_REPORTED_CD4 = new ConceptReference("Clinician reported to CD4");
	public static final ConceptReference KS_SIDE_EFFECTS_WORSENING_ON_ARVS = new ConceptReference("Kaposis sarcoma side effects worsening while on ARVs?");
	public static final ConceptReference TB_TREATMENT_STATUS = new ConceptReference("Tuberculosis treatment status");
	public static final ConceptReference WHO_STAGE = new ConceptReference("WHO stage");
	public static final ConceptReference CD4_PERCENT = new ConceptReference("CD4 percent");
	public static final ConceptReference PRESUMED_SEVERE_HIV_CRITERIA_PRESENT = new ConceptReference("Presumed severe HIV criteria present");
	public static final ConceptReference FIRST_LINE_ARV_START_DATE = new ConceptReference("Start date 1st line ARV");
	public static final ConceptReference ARV_DRUGS_RECEIVED = new ConceptReference("Malawi Antiretroviral drugs received");
	public static final ConceptReference TB_STATUS = new ConceptReference("TB status");
	public static final ConceptReference ART_SIDE_EFFECTS = new ConceptReference("Malawi ART side effects");
	public static final ConceptReference HIV_DNA_PCR = new ConceptReference("HIV DNA polymerase chain reaction");
	public static final ConceptReference DNA_PCR_RESULT = new ConceptReference("DNA-PCR Testing Result");
	public static final ConceptReference DNA_PCR_RESULT_2 = new ConceptReference("DNA-PCR Testing Result 2");
	public static final ConceptReference DNA_PCR_RESULT_3 = new ConceptReference("DNA-PCR Testing Result 3");
	public static final ConceptReference NEGATIVE = new ConceptReference("Negative");
	public static final ConceptReference POSITIVE = new ConceptReference("Positive");
	public static final ConceptReference INDETERMINATE = new ConceptReference("Indeterminate");
	public static final ConceptReference HIV_STATUS_CONCEPT = new ConceptReference("HIV status");
	public static final ConceptReference ON_ART_CONCEPT = new ConceptReference("On ART");
	public static final ConceptReference TB_HISTORY_CONCEPT = new ConceptReference("Previous or current tuberculosis treatment");
	public static final ConceptReference CLINIC_TRAVEL_TIME_HOURS_CONCEPT = new ConceptReference("Clinic travel time in hours");
	public static final ConceptReference CLINIC_TRAVEL_TIME_MINUTES_CONCEPT = new ConceptReference("Clinic travel time in minutes");
	public static final ConceptReference HIGH_RISK_PATIENT_CONCEPT = new ConceptReference("High risk patient");
	public static final ConceptReference WALL_MATERIAL_CONCEPT = new ConceptReference("Wall material");
	public static final ConceptReference ROOF_MATERIAL_CONCEPT = new ConceptReference("Roof material");
	public static final ConceptReference HOME_ELECTRICITY_CONCEPT = new ConceptReference("Home electricity");
	public static final ConceptReference PATIENT_OWNS_RADIO_CONCEPT = new ConceptReference("Patient owns radio");
	public static final ConceptReference ACCESS_TO_BICYCLE_CONCEPT = new ConceptReference("Access to bicycle");
	public static final ConceptReference LOCATION_OF_COOKING_CONCEPT = new ConceptReference("Location of cooking");
	public static final ConceptReference FUEL_SOUCE_CONCEPT = new ConceptReference("Fuel source");
	public static final ConceptReference NUMBER_FRUIT_AND_VEGETABLES_CONCEPT = new ConceptReference("Number of servings of fruits and vegetables consumed per day");
	public static final ConceptReference SMOKING_HISTORY_CONCEPT = new ConceptReference("Smoking history");
	public static final ConceptReference NUMBER_CIGARRETES_PER_DAY_CONCEPT = new ConceptReference("Number of cigarettes smoked per day");
	public static final ConceptReference HISTORY_OF_ALCOHOL_USE_CONCEPT = new ConceptReference("History of alcohol use");
	public static final ConceptReference LITERS_ALCOHOL_PER_DAY_CONCEPT = new ConceptReference("Liters per day");
	public static final ConceptReference SOURCE_OF_REFERRAL_CONCEPT = new ConceptReference("Type of referring clinic or hospital");
	public static final ConceptReference CURRENT_DRUGS_USED_CONCEPT = new ConceptReference("Current drugs used");
	public static final ConceptReference PREFERRED_TX_OUT_OF_STOCK_CONCEPT = new ConceptReference("Preferred treatment out of stock");
	public static final ConceptReference CHANGE_IN_TREATMENT_CONCEPT = new ConceptReference("Change in treatment");
	public static final ConceptReference HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT = new ConceptReference("Patient hospitalized since last visit");
	public static final ConceptReference HOSPITALIZED_FOR_NCD_SINCE_LAST_VISIT_CONCEPT = new ConceptReference("Hospitalized for non-communicable disease since last visit");
	public static final ConceptReference SYSTOLIC_BLOOD_PRESSURE_CONCEPT = new ConceptReference("Systolic blood pressure");
	public static final ConceptReference DIASTOLIC_BLOOD_PRESSURE_CONCEPT = new ConceptReference("Diastolic blood pressure");
	public static final ConceptReference NYHA_CLASS_CONCEPT = new ConceptReference("Nyha class");
	public static final ConceptReference SERUM_GLUCOSE_CONCEPT = new ConceptReference("Serum glucose");
	public static final ConceptReference BLOOD_SUGAR_TEST_TYPE_CONCEPT = new ConceptReference("Blood sugar test type");
	public static final ConceptReference NUMBER_OF_SEIZURES_CONCEPT = new ConceptReference("NUMBER OF SEIZURES");
	public static final ConceptReference PEAK_FLOW_CONCEPT = new ConceptReference("PEAK FLOW");
	public static final ConceptReference PEAK_FLOW_PREDICTED_CONCEPT = new ConceptReference("PEAK FLOW PREDICTED");
	public static final ConceptReference ASTHMA_CLASSIFICATION_CONCEPT = new ConceptReference("Asthma classification");
	public static final ConceptReference PATIENT_VISIT_COMPLETED_WITH_ALL_SERVICES_CONCEPT = new ConceptReference("Patient visit completed with all services delivered");
	public static final ConceptReference DATA_CLERK_COMMENTS_CONCEPT = new ConceptReference("Data clerk comments");
	public static final ConceptReference MODERATE_PERSISTENT_CONCEPT = new ConceptReference("Moderate persistent");
	public static final ConceptReference SEVERE_PERSISTENT_CONCEPT = new ConceptReference("Severe persistent");
	public static final ConceptReference SEVERE_UNCONTROLLED_CONCEPT = new ConceptReference("Severe uncontrolled");
	public static final ConceptReference OTHER_NON_CODED_CONCEPT = new ConceptReference("Other non-coded");
	public static final ConceptReference TRUE_CONCEPT = new ConceptReference("True");
	public static final ConceptReference FALSE_CONCEPT = new ConceptReference("False");
	public static final ConceptReference YES_CONCEPT = new ConceptReference("Yes");
	public static final ConceptReference NO_CONCEPT = new ConceptReference("No");
	public static final ConceptReference BECLOMETHASONE_CONCEPT = new ConceptReference("Beclomethasone");
	public static final ConceptReference INSULIN_CONCEPT = new ConceptReference("Insulin");
	public static final ConceptReference OUTPATIENT_CONSULTATION_CONCEPT = new ConceptReference("Outpatient consultation");
	public static final ConceptReference INPATIENT_WARD_CONCEPT = new ConceptReference("Ward");
	public static final ConceptReference HEALTH_CENTER_CONCEPT = new ConceptReference("Health center");

}