package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component("mentalHealthMetadata")
public class MentalHealthMetadata extends CommonMetadata{

    public static ProgramDescriptor MH_CARE_PROGRAM = new ProgramDescriptor() {
        public String uuid() { return "60357F01-536E-4B59-A851-B000F801FB13"; }
        public String name() { return "MENTAL HEALTH CARE PROGRAM"; }
        public String description() { return "Mental Health Care Program"; }
        public String conceptUuid() { return ProgramConcepts.MH_CARE_PROGRAM_CONCEPT; }
        public Set<ProgramWorkflowDescriptor> workflows() {
            return new HashSet<ProgramWorkflowDescriptor>(Arrays.asList(MH_TREATMENT_WORKFLOW, EPILEPSY_TREATMENT_WORKFLOW)); }
    };

    public static ProgramWorkflowDescriptor MH_CARE_TREATMENT_STATUS = new ProgramWorkflowDescriptor() {
        public String uuid() { return "261BF8C5-3189-45F5-852F-5AE2C0AB9167"; }

        @Override
        public boolean retired() {
            return true;
        }

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

    public static ProgramWorkflowStateDescriptor EPILEPSY_STATE_ON_TREATMENT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "CB86C6FE-4263-4A4C-AF54-49D5308459D4"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor MH_STATE_ON_TREATMENT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "2F76D426-56A9-4651-B253-A2299B442C09"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor EPILEPSY_STATE_IN_ADVANCED_CARE = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "61190A43-95FF-4C84-8A3F-DD7F5354171C"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor MH_STATE_IN_ADVANCED_CARE = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "79F2CAB1-E674-433E-AF42-447678FDB443"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor EPILEPSY_STATE_TRANSFERRED_OUT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "F63ED5E5-1707-43FA-BCA0-CE271C338AE2"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor MH_STATE_TRANSFERRED_OUT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "CE543EAB-40A0-4021-9264-E8FFE835759F"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor EPILEPSY_STATE_DIED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "FB0B61BD-A641-499B-BA87-421DC7E1CA2C"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor MH_STATE_DIED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "5FB84A00-8AEC-42F5-8CE0-6006A1B58653"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor EPILEPSY_STATE_DISCHARGED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "B7FADD7E-6143-4BA8-90F3-629F79D02CD9"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor MH_STATE_DISCHARGED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "6C704865-5355-412B-9A9F-46489C301B6B"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor EPILEPSY_STATE_DEFAULTED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "96D5D27B-31CB-4BC7-AA5C-C6EC28B121DE"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor MH_STATE_DEFAULTED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "A1F672EA-EB8D-4B7D-8193-146C309AF348"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor EPILEPSY_STATE_TREATMENT_STOPPED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "AAE431AF-96E6-477F-B15E-2E5C66B20AEF"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor MH_STATE_TREATMENT_STOPPED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "6633F174-E20C-4D03-B6CB-3EBD2433EE75"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT; }
    };

    public static ProgramWorkflowDescriptor MH_TREATMENT_WORKFLOW = new ProgramWorkflowDescriptor() {
        public String uuid() { return "DA69BBCB-01FE-4C59-9D46-8A2659ABBD73"; }

        @Override
        public boolean retired() {
            return false;
        }

        public String conceptUuid() { return ProgramConcepts.MENTAL_HEALTH_TREATMENT_STATUS_CONCEPT_UUID; }
        public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    MH_STATE_ON_TREATMENT,
                    MH_STATE_IN_ADVANCED_CARE,
                    MH_STATE_TRANSFERRED_OUT,
                    MH_STATE_DIED,
                    MH_STATE_DISCHARGED,
                    MH_STATE_DEFAULTED,
                    MH_STATE_TREATMENT_STOPPED));
        }
    };

    public static ProgramWorkflowDescriptor EPILEPSY_TREATMENT_WORKFLOW = new ProgramWorkflowDescriptor() {
        public String uuid() { return "26FD314D-138F-4A5C-8890-E01791C06336"; }

        @Override
        public boolean retired() {
            return false;
        }

        public String conceptUuid() { return ProgramConcepts.EPILEPSY_TREATMENT_STATUS_CONCEPT_UUID; }
        public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    EPILEPSY_STATE_ON_TREATMENT,
                    EPILEPSY_STATE_IN_ADVANCED_CARE,
                    EPILEPSY_STATE_TRANSFERRED_OUT,
                    EPILEPSY_STATE_DIED,
                    EPILEPSY_STATE_DISCHARGED,
                    EPILEPSY_STATE_DEFAULTED,
                    EPILEPSY_STATE_TREATMENT_STOPPED));
        }
    };
}
