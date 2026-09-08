package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;
import org.springframework.stereotype.Component;

@Component("pdcMetadata")
public class PdcMetadata extends CommonMetadata{

    public static String PREMATURE_BIRTH = "f541084c-84c7-48a6-b502-d9ddbb3bb3b9";
    public static String HYDROCEPHALUS = "26071668-6ad4-4d30-b661-a7a07cece1ac";
    public static String DOWNS_SYNDROME = "722824d7-8392-4d62-a8c7-4b329f5094cd";
    public static String CLEFT_LIP_OR_PALATE = "735a9376-abde-4538-8f28-c73f998e5e47";
    public static String OTHER_DEVELOPMENTAL_DELAY = "07a093b4-989e-4128-b015-38961791a714";
    public static String SEVERE_MALNUTRITION = "a94e5963-f6b1-4c91-b676-48dfb370a1f8";
    public static String AGE_OF_GUARDIAN = "fa7a921c-bd5f-4d5d-8e4f-4dc5060e37b7";
    public static String LOW_BIRTH_WEIGHT = "6575742a-977f-11e1-8993-905e29aff6c1";
    public static String CENTRAL_NERVOUS_SYSTEM = "657169d4-977f-11e1-8993-905e29aff6c1";
    public static String REASON_FOR_REFERRAL = "98b17e13-659f-41e7-8459-b370fdbffe0f";
    public static String CLEFT_LIP = "c415db67-75e8-4077-a0f2-ba2864ae52b1";
    public static String CLEFT_PALATE = "abe71d88-3f2c-4380-854b-c49b74946a01";
    public static String TRISOMY_21 = "fc4bf95c-b445-44e3-959b-435145e79f01";
    public static String HIE = "76e0ba08-d931-4baf-9651-9946543cc623";

    public Program getPdcProgram() {
        return getProgram(PihMalawiConfigConstants.PROGRAM_PDC_UUID);
    }

    public PatientIdentifierType getPdcCareNumber() {
        return getPatientIdentifierType(PihMalawiConfigConstants.PATIENTIDENTIFIERTYPE_PDC_IDENTIFIER_NAME);
    }
}
