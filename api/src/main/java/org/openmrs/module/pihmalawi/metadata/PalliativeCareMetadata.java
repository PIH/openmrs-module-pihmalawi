package org.openmrs.module.pihmalawi.metadata;


import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.PalliativeCareConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("palliativeCareMetadata")
public class PalliativeCareMetadata extends CommonMetadata{

    public static ProgramWorkflowStateDescriptor ON_TREATMENT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.ON_TREATMENT_CONCEPT_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "7c1f852e-5120-4371-8136-f64614f5dfc7"; }
    };
    public static ProgramWorkflowStateDescriptor TREATMENT_STOPPED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.TREATMENT_STOPPED_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "b35ed57c-7d54-4795-b678-f0947a135fda"; }
    };
    public static ProgramWorkflowStateDescriptor TRANSFERRED_OUT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.TRANSFERRED_OUT_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "e92017b9-45cf-41b9-bc69-a5b0232544c1"; }
    };
    public static ProgramWorkflowStateDescriptor DEFAULTED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.DEFAULTED_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "0f034ef4-3f70-4514-a020-5fb928fc3394"; }
    };
    public static ProgramWorkflowStateDescriptor DIED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.DIED_STATE_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "4bed1c08-1fe9-4972-8e7e-e93323c9f2c4"; }
    };
    public static ProgramWorkflowStateDescriptor DISCHARGE_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.DISCHARGE_STATE_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "13490dc8-e5bd-11e7-80c1-9a214cf093ae"; }
    };


    public static ProgramWorkflowDescriptor PALLIATIVE_CARE_CLINICAL_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.PALLIATIVE_CARE_PROGRAM_STATUS_CONCEPT_UUID; }
        public String uuid() { return "F2D47AE5-7083-4901-B630-51860D09A884"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    DISCHARGE_STATE,
                    ON_TREATMENT_STATE,
                    TREATMENT_STOPPED_STATE,
                    TRANSFERRED_OUT_STATE,
                    DEFAULTED_STATE,
                    DIED_STATE));
        }
    };

    public static ProgramDescriptor PALLIATIVE_CARE_PROGRAM = new ProgramDescriptor() {
        public String conceptUuid() { return PalliativeCareConcepts.PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID; }
        public String name() { return "Palliative care program"; }
        public String description() { return "Palliative care program"; }
        @Override public String outcomesConceptUuid()  { return ProgramConcepts.GENERIC_OUTCOME_CONCEPT_UUID; }
        public String uuid() { return "acbd87f3-566f-4386-a11e-877e612d3911"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() {
            return Collections.singleton(PALLIATIVE_CARE_CLINICAL_STATUS);
        }
    };

}
