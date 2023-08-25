package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.TeenClubConcepts;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("teenClubProgramMetadata")
public class TeenClubProgramMetadata extends CommonMetadata{

    public static ProgramWorkflowStateDescriptor TRANSFER_IN_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.TRANSFER_IN_STATE_CONCEPT_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "F179C6EA-6AF7-4F66-8587-AFE23521400C"; }
    };

    public static ProgramWorkflowStateDescriptor FIRST_TIME_INITIATION_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.FIRST_TIME_INITIATION_STATE_CONCEPT_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "3E9BB98B-6BB0-431D-BCD5-B3E277922C04"; }
    };
    public static ProgramWorkflowStateDescriptor TRANSFERRED_OUT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "BDAF0BE3-5135-423E-B22B-4A87DE3C5C4A"; }
    };

    public static ProgramWorkflowStateDescriptor DIED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "3700B846-6F87-454A-AB1C-1EB19BF0D48B"; }
    };

    public static ProgramWorkflowStateDescriptor DEFAULTED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "11DFAE63-7185-452B-9BE4-E0773AC91D34"; }
    };

    public static ProgramWorkflowStateDescriptor PATIENT_PREGNANT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.PATIENT_PREGNANT_STATE_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "051AB431-5F98-4351-A541-9EFE0829AE7C"; }
    };

    public static ProgramWorkflowStateDescriptor PATIENT_MARRIED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.PATIENT_MARRIED_STATE_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "BFB0D25A-96C7-44EB-B58A-8CA6DCDE36D8"; }
    };
    public static ProgramWorkflowStateDescriptor PATIENT_GRADUATED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.PATIENT_GRADUATED_STATE_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "E6C1B44A-8B02-4D47-944D-09A2E94D61F3"; }
    };

    public static ProgramWorkflowDescriptor TEEN_CLUB_PROGRAM_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return TeenClubConcepts.TEEN_CLUB_PROGRAM_STATUS_CONCEPT_UUID; }
        public String uuid() { return "9AB24A22-80B6-4E93-91FC-DEF586FA46EE"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    TRANSFER_IN_STATE,
                    FIRST_TIME_INITIATION_STATE,
                    TRANSFERRED_OUT_STATE,
                    DIED_STATE,
                    DEFAULTED_STATE,
                    PATIENT_PREGNANT_STATE,
                    PATIENT_MARRIED_STATE,
                    PATIENT_GRADUATED_STATE
            ));
        }
    };

    public static ProgramDescriptor TEEN_CLUB_PROGRAM = new ProgramDescriptor() {
        public String conceptUuid() { return TeenClubConcepts.TEEN_CLUB_PROGRAM_CONCEPT_UUID; }
        public String name() { return "Teen club program"; }
        public String description() { return "Teen club program"; }
        @Override public String outcomesConceptUuid()  { return ProgramConcepts.GENERIC_OUTCOME_CONCEPT_UUID; }
        public String uuid() { return "54100564-4759-4CBD-9A73-B38D6DBAC7B9"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() {
            return Collections.singleton(TEEN_CLUB_PROGRAM_STATUS);
        }
    };
}
