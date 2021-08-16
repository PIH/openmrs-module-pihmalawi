package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("pdcMetadata")
public class PdcMetadata extends CommonMetadata{

    public static String PREMATURE_BIRTH = "f541084c-84c7-48a6-b502-d9ddbb3bb3b9";
    public static String HYDROCEPHALUS = "26071668-6ad4-4d30-b661-a7a07cece1ac";
    public static String DOWNS_SYNDROME = "722824d7-8392-4d62-a8c7-4b329f5094cd";
    public static String CLEFT_LIP = "735a9376-abde-4538-8f28-c73f998e5e47";
    public static String OTHER_DEVELOPMENTAL_DELAY = "07a093b4-989e-4128-b015-38961791a714";
    public static String SEVERE_MALNUTRITION = "a94e5963-f6b1-4c91-b676-48dfb370a1f8";
    public static String AGE_OF_GUARDIAN = "fa7a921c-bd5f-4d5d-8e4f-4dc5060e37b7";
    public static String LOW_BIRTH_WEIGHT = "6575742a-977f-11e1-8993-905e29aff6c1";
    public static String CENTRAL_NERVOUS_SYSTEM = "657169d4-977f-11e1-8993-905e29aff6c1";
    public static String REASON_FOR_REFERRAL = "98b17e13-659f-41e7-8459-b370fdbffe0f";




    public static ProgramDescriptor PDC_PROGRAM = new ProgramDescriptor() {
        public String uuid() { return "cffd61d1-f087-41df-86c7-fbd6b6e9ab1e"; }
        public String name() { return "PEDIATRIC DEVELOPMENT CLINIC PROGRAM"; }
        public String description() { return "Pediatric Development Clinic Program"; }
        public String conceptUuid() { return ProgramConcepts.PDC_PROGRAM_CONCEPT; }
        public Set<ProgramWorkflowDescriptor> workflows() { return Collections.singleton(PDC_TREATMENT_STATUS); }
    };

    public static ProgramWorkflowDescriptor PDC_TREATMENT_STATUS = new ProgramWorkflowDescriptor() {
        public String uuid() { return "31d71aad-304a-4cba-8e1f-b59ed2c8f8a1"; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_TREATMENT_STATUS_CONCEPT; }
        public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    STATUS_ON_TREATMENT,
                    STATUS_IN_ADVANCED_CARE,
                    STATUS_TRANSFERRED_OUT,
                    STATUS_DIED,
                    STATUS_DISCHARGED,
                    STATUS_DEFAULTED,
                    STATUS_TREATMENT_STOPPED));
        }
    };

    public static ProgramWorkflowStateDescriptor STATUS_ON_TREATMENT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "b84735a5-82ae-4e3b-87db-250c43113977"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_IN_ADVANCED_CARE = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "28f67846-3204-4dc5-9c7f-043acc3e4b6c"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_TRANSFERRED_OUT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "4b2027c3-f6a1-4cdd-81c3-a6c2068ef9a9"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DIED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "7451d3db-2400-4d38-b981-133dabf1558e"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DISCHARGED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "38c0cc2f-eeda-4bf3-8496-4bb11736021e"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DEFAULTED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "49853073-c864-4421-8ba4-41bb743107bd"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_TREATMENT_STOPPED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "4c03e13a-4372-42f5-bbb1-ac0bf77c233b"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT; }
    };
}
