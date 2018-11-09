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
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("chronicCareMetadata")
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

    public List<ProgramWorkflowState> getActiveChronicCareStates() {
        List<ProgramWorkflowState> l = new ArrayList<ProgramWorkflowState>();
        l.add(getChronicCareStatusOnTreatment());
        return l;
    }

	public EncounterType getOldChronicCareInitialEncounterType() {
		return getEncounterType(CHRONIC_CARE_INITIAL);
	}

	public EncounterType getOldChronicCareFollowupEncounterType() {
		return getEncounterType(CHRONIC_CARE_FOLLOWUP);
	}

	public EncounterType getHtnDiabetesInitialEncounterType() {
	    return getEncounterType(EncounterTypes.HTN_DIABETES_INITIAL.uuid());
    }

    public EncounterType getHtnDiabetesFollowupEncounterType() {
        return getEncounterType(EncounterTypes.HTN_DIABETES_FOLLOWUP.uuid());
    }

    public EncounterType getHtnDiabetesTestsEncounterType() {
        return getEncounterType(EncounterTypes.HTN_DIABETES_TESTS.uuid());
    }

    public EncounterType getHtnDiabetesHospitalizationsEncounterType() {
        return getEncounterType(EncounterTypes.HTN_DIABETES_HOSPITALIZATIONS.uuid());
    }

    public List<EncounterType> getHtnDiabetesEncounterTypes() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.add(getHtnDiabetesInitialEncounterType());
        l.add(getHtnDiabetesFollowupEncounterType());
        l.add(getHtnDiabetesHospitalizationsEncounterType());
        l.add(getHtnDiabetesTestsEncounterType());
        return l;
    }

    public EncounterType getAsthmaInitialEncounterType() {
        return getEncounterType(EncounterTypes.ASTHMA_INITIAL.uuid());
    }

    public EncounterType getAsthmaFollowupEncounterType() {
        return getEncounterType(EncounterTypes.ASTHMA_FOLLOWUP.uuid());
    }

    public EncounterType getAsthmaPeakFlowEncounterType() {
        return getEncounterType(EncounterTypes.ASTHMA_PEAKFLOW.uuid());
    }

    public EncounterType getAsthmaHospitalizationsEncounterType() {
        return getEncounterType(EncounterTypes.ASTHMA_HOSPITALIZATION.uuid());
    }

    public List<EncounterType> getAsthmaEncounterTypes() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.add(getAsthmaInitialEncounterType());
        l.add(getAsthmaFollowupEncounterType());
        l.add(getAsthmaPeakFlowEncounterType());
        l.add(getAsthmaHospitalizationsEncounterType());
        return l;
    }

    public EncounterType getEpilepsyInitialEncounterType() {
        return getEncounterType(EncounterTypes.EPILEPSY_INITIAL.uuid());
    }

    public EncounterType getEpilepsyFollowupEncounterType() {
        return getEncounterType(EncounterTypes.EPILEPSY_FOLLOWUP.uuid());
    }

    public List<EncounterType> getEpilepsyEncounterTypes() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.add(getEpilepsyInitialEncounterType());
        l.add(getEpilepsyFollowupEncounterType());
        return l;
    }

    public EncounterType getMentalHealthInitialEncounterType() {
        return getEncounterType(EncounterTypes.MENTAL_HEALTH_INITIAL.uuid());
    }

    public EncounterType getMentalHealthFollowupEncounterType() {
        return getEncounterType(EncounterTypes.MENTAL_HEALTH_FOLLOWUP.uuid());
    }

    public List<EncounterType> getMentalHealthEncounterTypes() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.add(getMentalHealthInitialEncounterType());
        l.add(getMentalHealthFollowupEncounterType());
        return l;
    }

    public List<EncounterType> getChronicCareInitialEncounterTypes() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.add(getOldChronicCareInitialEncounterType());
        l.add(getHtnDiabetesInitialEncounterType());
        l.add(getAsthmaInitialEncounterType());
        l.add(getEpilepsyInitialEncounterType());
        l.add(getMentalHealthInitialEncounterType());
        return l;
    }

    public List<EncounterType> getChronicCareFollowupEncounterTypes() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.add(getOldChronicCareFollowupEncounterType());
        l.add(getHtnDiabetesFollowupEncounterType());
        l.add(getAsthmaFollowupEncounterType());
        l.add(getEpilepsyFollowupEncounterType());
        l.add(getMentalHealthFollowupEncounterType());
        return l;
    }

    public List<EncounterType> getChronicCareScheduledVisitEncounterTypes() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.addAll(getChronicCareInitialEncounterTypes());
        l.addAll(getChronicCareFollowupEncounterTypes());
        return l;
    }

    public List<EncounterType> getChronicCareEncounterTypes() {
        List<EncounterType> l = getChronicCareScheduledVisitEncounterTypes();
        l.add(getHtnDiabetesTestsEncounterType());
        l.add(getHtnDiabetesHospitalizationsEncounterType());
        l.add(getAsthmaPeakFlowEncounterType());
        l.add(getAsthmaHospitalizationsEncounterType());
        return l;
    }

	public List<Location> getChronicCareSystemLocations() {
		List<Location> l = getLocationsForTag(LocationTags.CHRONIC_CARE_LOCATION.name());
		l.retainAll(getSystemLocations());
		return l;
	}

	public static final String CHRONIC_CARE_DIAGNOSIS = "CHRONIC CARE DIAGNOSIS";
	public static final String ASTHMA = "Asthma";
    public static final String COPD = "Chronic obstructive pulmonary disease";
	public static final String DIABETES = "6567426a-977f-11e1-8993-905e29aff6c1";
    public static final String TYPE_1_DIABETES = "65714206-977f-11e1-8993-905e29aff6c1";
    public static final String TYPE_2_DIABETES = "65714314-977f-11e1-8993-905e29aff6c1";
	public static final String EPILEPSY = "Epilepsy";
	public static final String HEART_FAILURE = "Heart failure";
	public static final String HYPERTENSION = "654abfc8-977f-11e1-8993-905e29aff6c1";
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
	public static final String HBA1C_CONCEPT = "65714f76-977f-11e1-8993-905e29aff6c1";
	public static final String BLOOD_SUGAR_TEST_RESULT_CONCEPT = "654a98b8-977f-11e1-8993-905e29aff6c1";
	public static final String BLOOD_SUGAR_TEST_TYPE_CONCEPT = "65711e3e-977f-11e1-8993-905e29aff6c1";
	public static final String CREATININE_CONCEPT = "657170a0-977f-11e1-8993-905e29aff6c1";
	public static final String FAMILY_HISTORY_DIABETES = "657308fc-977f-11e1-8993-905e29aff6c1";

	public Concept getChronicCareDiagnosisConcept() {
		return getConcept(CHRONIC_CARE_DIAGNOSIS);
	}

	public Concept getAsthmaConcept() {
		return getConcept(ASTHMA);
	}

	public Concept getCopdConcept() {
	    return getConcept(COPD);
    }

	public Concept getDiabetesConcept() {
		return getConcept(DIABETES);
	}

	public Concept getType1DiabetesConcept() {
	    return getConcept(TYPE_1_DIABETES);
    }

    public Concept getType2DiabetesConcept() {
        return getConcept(TYPE_2_DIABETES);
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

	public Concept getHbA1cConcept() { return getConcept(HBA1C_CONCEPT); }

    public Concept getBloodSugarTestResultConcept() { return getConcept(BLOOD_SUGAR_TEST_RESULT_CONCEPT); }

    public Concept getBloodSugarTestTypeConcept() { return getConcept(BLOOD_SUGAR_TEST_TYPE_CONCEPT); }

    public Concept getCreatinineConcept() { return getConcept(CREATININE_CONCEPT); }

    public Concept getFamilyHistoryOfDiabetesConcept() { return getConcept(FAMILY_HISTORY_DIABETES); }
}