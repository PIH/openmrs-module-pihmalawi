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
import org.openmrs.ConceptAnswer;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChronicCareMetadata extends CommonMetadata {

	public static String CHRONIC_CARE_NUMBER = "Chronic Care Number";

	public PatientIdentifierType getChronicCareNumber() {
		return getPatientIdentifierType(CHRONIC_CARE_NUMBER);
	}

	public static String CHRONIC_CARE_PROGRAM = "Chronic care program";
	public static String CHRONIC_CARE_PROGRAM_TREATMENT_STATUS = "Chronic care treatment status";
	public static String CHRONIC_CARE_STATUS_ON_TREATMENT = "On treatment";

	public static String CHRONIC_CARE_INITIAL = "CHRONIC_CARE_INITIAL";
	public static String CHRONIC_CARE_FOLLOWUP = "CHRONIC_CARE_FOLLOWUP";

	public Program getChronicCareProgram() {
		return getProgram(CHRONIC_CARE_PROGRAM);
	}

	public ProgramWorkflow getChronicCareTreatmentStatusWorkflow() {
		return getProgramWorkflow(CHRONIC_CARE_PROGRAM, CHRONIC_CARE_PROGRAM_TREATMENT_STATUS);
	}

	public ProgramWorkflowState getChronicCareStatusOnTreatment() {
		return getProgramWorkflowState(CHRONIC_CARE_PROGRAM, CHRONIC_CARE_PROGRAM_TREATMENT_STATUS, CHRONIC_CARE_STATUS_ON_TREATMENT);
	}

	public EncounterType getChronicCareInitialEncounterType() {
		return getEncounterType(CHRONIC_CARE_INITIAL);
	}

	public EncounterType getChronicCareFollowupEncounterType() {
		return getEncounterType(CHRONIC_CARE_FOLLOWUP);
	}

	public List<EncounterType> getChronicCareEncounterTypes() {
		List<EncounterType> l = new ArrayList<EncounterType>();
		l.add(getChronicCareInitialEncounterType());
		l.add(getChronicCareFollowupEncounterType());
		return l;
	}

	public static final String CHRONIC_CARE_DIAGNOSIS = "CHRONIC CARE DIAGNOSIS";
	public static final String ASTHMA = "Asthma";
	public static final String DIABETES = "Diabetes";
	public static final String EPILEPSY = "Epilepsy";
	public static final String HEART_FAILURE = "Heart failure";
	public static final String HYPERTENSION = "Hypertension";
	public static final String CHRONIC_KIDNEY_DISEASE = "Chronic kidney disease";
	public static final String STROKE = "Stroke";
	public static final String DEPRESSION = "Depression";
	public static final String AGE_OF_ASTHMA_DIAGNOSIS_CONCEPT = "Age of asthma diagnosis";
	public static final String AGE_OF_DIABETES_DIAGNOSIS_CONCEPT = "Age of diabetes diagnosis";
	public static final String AGE_OF_EPILEPSY_DIAGNOSIS_CONCEPT = "Age of epilepsy diagnosis";
	public static final String AGE_OF_HEART_FAILURE_DIAGNOSIS_CONCEPT = "Age of heart failure diagnosis";
	public static final String AGE_OF_HYPERTENSION_DIAGNOSIS_CONCEPT = "Age of hypertension diagnosis";
	public static final String CHRONIC_CARE_MEDICATION_CONCEPT_SET = "Chronic Care Medication Set";
	public static final String HYPERTENSION_MEDICATION_CONCEPT_SET = "Hypertension Medication Set";

	public Concept getChronicCareDiagnosisConcept() {
		return getConcept(CHRONIC_CARE_DIAGNOSIS);
	}

	public Concept getAsthmaConcept() {
		return getConcept(ASTHMA);
	}

	public Concept getDiabetesConcept() {
		return getConcept(DIABETES);
	}

	public Concept getEpilepsyConcept() {
		return getConcept(EPILEPSY);
	}

	public Concept getHeartFailureConcept() {
		return getConcept(HEART_FAILURE);
	}

	public Concept getHypertensionConcept() {
		return getConcept(HYPERTENSION);
	}

	public Concept getChronicKidneyDiseaseConcept() {
		return getConcept(CHRONIC_KIDNEY_DISEASE);
	}

	public Concept getStrokeConcept() {
		return getConcept(STROKE);
	}

	public Concept getDepressionConcept() {
		return getConcept(DEPRESSION);
	}

	public List<Concept> getChronicCareDiagnosisAnswerConcepts() {
		List<Concept> l = new ArrayList<Concept>();
		Concept c = getConcept(CHRONIC_CARE_DIAGNOSIS);
		for (ConceptAnswer ca : c.getAnswers()) {
			l.add(ca.getAnswerConcept());
		}
		return l;
	}

	public Concept getAgeOfAsthmaDiagnosisConcept() {
		return getConcept(AGE_OF_ASTHMA_DIAGNOSIS_CONCEPT);
	}

	public Concept getAgeOfDiabetesDiagnosisConcept() {
		return getConcept(AGE_OF_DIABETES_DIAGNOSIS_CONCEPT);
	}

	public Concept getAgeOfEpilepsyDiagnosisConcept() {
		return getConcept(AGE_OF_EPILEPSY_DIAGNOSIS_CONCEPT);
	}

	public Concept getAgeOfHeartFailureDiagnosisConcept() {
		return getConcept(AGE_OF_HEART_FAILURE_DIAGNOSIS_CONCEPT);
	}

	public Concept getAgeOfHypertensionDiagnosisConcept() {
		return getConcept(AGE_OF_HYPERTENSION_DIAGNOSIS_CONCEPT);
	}

	public List<Concept> getAgeOfDiagnosisConcepts() {
		List<Concept> l = new ArrayList<Concept>();
		l.add(getAgeOfAsthmaDiagnosisConcept());
		l.add(getAgeOfDiabetesDiagnosisConcept());
		l.add(getAgeOfEpilepsyDiagnosisConcept());
		l.add(getAgeOfHeartFailureDiagnosisConcept());
		l.add(getAgeOfHypertensionDiagnosisConcept());
		return l;
	}

	public List<Concept> getChronicCareMedicationConcepts() {
		return getConceptsInSet(CHRONIC_CARE_MEDICATION_CONCEPT_SET);
	}

	public List<Concept> getHypertensionMedicationConcepts() {
		return getConceptsInSet(HYPERTENSION_MEDICATION_CONCEPT_SET);
	}
}