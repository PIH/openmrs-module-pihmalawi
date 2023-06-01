package org.openmrs.module.pihmalawi.metadata;


import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.NutritionConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("nutritionProgramMetadata")
public class NutritionProgramMetadata extends CommonMetadata{

    public static ProgramWorkflowStateDescriptor ON_TREATMENT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "4F148482-8B25-4ACD-A23C-B2A1D4701C2D"; }
    };

    public static ProgramWorkflowStateDescriptor TRANSFERRED_OUT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "228C3CE4-3685-459C-ABA3-41BAD3DED1D7"; }
    };

    public static ProgramWorkflowStateDescriptor DIED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "51E67592-5751-491B-8DA5-D5737D320AC5"; }
    };

    public static ProgramWorkflowStateDescriptor DISCHARGE_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "7988D58A-94B1-4E5F-8891-300F40D50D5B"; }
    };

    public static ProgramWorkflowStateDescriptor DEFAULTED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "9144608D-BE07-42E0-B9C3-7BF7B2E70B4B"; }
    };

    public static ProgramWorkflowStateDescriptor TREATMENT_STOPPED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "B633E826-943D-427F-BEC9-C19DBB31DAAE"; }
    };

    public static ProgramWorkflowStateDescriptor SEVERE_MALNUTRITION_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return NutritionConcepts.SEVERE_MALNUTRITION_STATE_CONCEPT; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "35AF01AE-AAC7-4F9B-A9A1-0EFDEB84AD5B"; }
    };


    public static ProgramWorkflowDescriptor NUTRITION_PROGRAM_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return NutritionConcepts.NUTRITION_PROGRAM_STATUS_CONCEPT_UUID; }
        public String uuid() { return "FDD077BC-A96C-49BB-9A87-28992B2CBABE"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    ON_TREATMENT_STATE,
                    TRANSFERRED_OUT_STATE,
                    DIED_STATE,
                    DISCHARGE_STATE,
                    DEFAULTED_STATE,
                    TREATMENT_STOPPED_STATE,
                    SEVERE_MALNUTRITION_STATE
                    ));
        }
    };

    public static ProgramDescriptor NUTRITION_PROGRAM = new ProgramDescriptor() {
        public String conceptUuid() { return NutritionConcepts.NUTRITION_PROGRAM_CONCEPT_UUID; }
        public String name() { return "Nutrition program"; }
        public String description() { return "Nutrition program"; }
        @Override public String outcomesConceptUuid()  { return ProgramConcepts.GENERIC_OUTCOME_CONCEPT_UUID; }
        public String uuid() { return "FECD888E-D547-4E1D-A012-56CA8874D2E1"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() {
            return Collections.singleton(NUTRITION_PROGRAM_STATUS);
        }
    };
}
