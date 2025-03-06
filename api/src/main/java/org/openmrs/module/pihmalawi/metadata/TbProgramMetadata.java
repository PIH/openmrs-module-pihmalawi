package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.TbProgramConcepts;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("tbProgramMetadata")
public class TbProgramMetadata extends CommonMetadata {

    public static ProgramDescriptor OLD_TB_PROGRAM = new ProgramDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.OLD_TB_PROGRAM_CONCEPT_UUID; }
        public String name() { return "Old TB PROGRAM"; }
        public String description() { return "Old Tuberculosis Program"; }
        public String uuid() { return "66850d9e-977f-11e1-8993-905e29aff6c1"; }
    };

    public static ProgramWorkflowStateDescriptor PATIENT_CURED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.PATIENT_CURED_STATUS_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "C7B0ACA9-1EBA-4220-BCFC-14D0C9DA24BF"; }
    };
    public static ProgramWorkflowStateDescriptor TREATMENT_COMPLETED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.TREATMENT_COMPLETED_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "7EA302F6-7D2F-4969-BA01-B55E762CDEA5"; }
    };
    public static ProgramWorkflowStateDescriptor TREATMENT_FAILED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.TREATMENT_FAILED_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "4FEC03C0-A8C4-4709-8F03-88801A5EB9DF"; }
    };
    public static ProgramWorkflowStateDescriptor ON_TREATMENT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "5088F779-AD8D-4EEF-A504-9B5C2D96ED62"; }
    };
    public static ProgramWorkflowStateDescriptor ON_MDR_TREATMENT_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.ON_MDR_TREATMENT_CONCEPT_UUID; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String uuid() { return "77DCD910-2E24-4ECD-B71F-EBCA67F02478"; }
    };

    public static ProgramWorkflowStateDescriptor LOST_TO_FOLLOWUP_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.LOST_TO_FOLLOWUP_STATUS_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "5ED5276E-808D-4670-8A86-43E020D886A7"; }
    };
    public static ProgramWorkflowStateDescriptor NOT_EVALUATED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.NOT_EVALUATED_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "F2BF7C70-4C04-43A0-A3F9-3DDC53FD55CF"; }
    };
    public static ProgramWorkflowStateDescriptor DIED_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String uuid() { return "B0EE543D-42D5-48BC-8D20-3FF6AF93FC9B"; }
    };
    public static ProgramWorkflowStateDescriptor TREATMENT_SUCCESS_STATE = new ProgramWorkflowStateDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.TREATMENT_SUCCESS_CONCEPT_UUID; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String uuid() { return "857314BC-BA66-426A-BEEF-7ACD59CB0EE9"; }
    };

    public static ProgramWorkflowDescriptor TB_PROGRAM_WORKFLOW_STATUS = new ProgramWorkflowDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.TB_PROGRAM_WORKFLOW_CONCEPT_UUID; }
        public String uuid() { return "6D1AD63F-8286-4191-A5DE-D747326B9883"; }
        @Override public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    PATIENT_CURED_STATE,
                    TREATMENT_COMPLETED_STATE,
                    TREATMENT_FAILED_STATE,
                    LOST_TO_FOLLOWUP_STATE,
                    NOT_EVALUATED_STATE,
                    /*TREATMENT_SUCCESS_STATE,*/
                    ON_TREATMENT_STATE,
                    ON_MDR_TREATMENT_STATE,
                    DIED_STATE));
        }
    };
    public static ProgramDescriptor TB_PROGRAM = new ProgramDescriptor() {
        public String conceptUuid() { return TbProgramConcepts.TB_PROGRAM_CONCEPT_UUID; }
        public String name() { return "TB PROGRAM"; }
        public String description() { return "Tuberculosis Program"; }
        public String uuid() { return "52D0036A-AB35-475E-A4D4-1826CCD985D6"; }
        @Override public Set<ProgramWorkflowDescriptor> workflows() {
            return Collections.singleton(TB_PROGRAM_WORKFLOW_STATUS);
        }
    };

    public Program getTbProgram(){
        return getProgram(TB_PROGRAM.name());
    }

    public PatientIdentifierType getTbProgramIdentifier() {
        return getPatientIdentifierType(PihMalawiPatientIdentifierTypes.TB_PROGRAM_IDENTIFIER.name());
    }
}
