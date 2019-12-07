package org.openmrs.module.pihmalawi.metadata;

import org.springframework.stereotype.Component;
import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("mentalHealthMetadata")
public class MentalHealthMetadata extends CommonMetadata{

    public static ProgramDescriptor MH_CARE_PROGRAM = new ProgramDescriptor() {
        public String uuid() { return "60357F01-536E-4B59-A851-B000F801FB13"; }
        public String name() { return "MENTAL HEALTH CARE PROGRAM"; }
        public String description() { return "Mental Health Care Program"; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_PROGRAM_CONCEPT; }
        public Set<ProgramWorkflowDescriptor> workflows() { return Collections.singleton(MH_CARE_TREATMENT_STATUS); }
    };

    public static ProgramWorkflowDescriptor MH_CARE_TREATMENT_STATUS = new ProgramWorkflowDescriptor() {
        public String uuid() { return "261BF8C5-3189-45F5-852F-5AE2C0AB9167"; }
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
        public String uuid() { return "5925718D-EA5E-43EB-9AE2-1CB342D8E318"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_IN_ADVANCED_CARE = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "E0381FF3-2976-41F0-B853-28E842400E84"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_TRANSFERRED_OUT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "41AF39C1-7CE6-47E0-9BA7-9FD7C0354C12"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DIED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "D79B02C2-B473-47F1-A51C-6D40B2242B9C"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DISCHARGED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "42ACC789-C2BB-4EAA-8AC2-0BE7D0F5D4E8"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DEFAULTED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "19CEF51A-0823-4876-A8AF-7285B7077494"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_TREATMENT_STOPPED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "9F6F188C-42AB-45D8-BC8B-DBE78948072D"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT; }
    };
}
