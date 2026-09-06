package org.openmrs.module.pihmalawi.metadata;


import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;
import org.springframework.stereotype.Component;

@Component("palliativeCareMetadata")
public class PalliativeCareMetadata extends CommonMetadata{

    public Program getPalliativeCareProgram() {
        return getProgram(PihMalawiConfigConstants.PROGRAM_PALLIATIVE_CARE_UUID);
    }

    public PatientIdentifierType getPalliativeCareNumber() {
        return getPatientIdentifierType(PihMalawiConfigConstants.PATIENTIDENTIFIERTYPE_PALLIATIVE_CARE_NUMBER_NAME);
    }
}
