package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;
import org.springframework.stereotype.Component;

@Component("tbProgramMetadata")
public class TbProgramMetadata extends CommonMetadata {

    public Program getTbProgram(){
        return getProgram(PihMalawiConfigConstants.PROGRAM_TB_UUID);
    }

    public PatientIdentifierType getTbProgramIdentifier() {
        return getPatientIdentifierType(PihMalawiConfigConstants.PATIENTIDENTIFIERTYPE_TB_PROGRAM_IDENTIFIER_NAME);
    }
}
