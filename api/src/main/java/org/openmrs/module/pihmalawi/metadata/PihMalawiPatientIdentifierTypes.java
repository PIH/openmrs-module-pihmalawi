package org.openmrs.module.pihmalawi.metadata;


import org.openmrs.PatientIdentifierType;
import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor;

public class PihMalawiPatientIdentifierTypes {


    public static PatientIdentifierTypeDescriptor PALLIATIVE_CARE_NUMBER = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "f2b29f9b-69d0-4339-b1aa-55a511672558"; }
        public String name() { return "Palliative Care Number"; }
        public String description() { return "Number assigned to patient on palliative care."; }
        public String formatDescription() {return "Ex. NNO 101 PC";}
        public PatientIdentifierType.LocationBehavior locationBehavior() {
            return PatientIdentifierType.LocationBehavior.REQUIRED;
        }
    };

    public static PatientIdentifierTypeDescriptor IC3_IDENTIFIER = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "f51dfa3a-95de-4040-b4eb-52d2de718a74"; }
        public String name() { return "IC3 Identifier"; }
        public String description() { return "ID assigned to patients at IC3 clinic who have not be enrolled in a program"; }
        public PatientIdentifierType.LocationBehavior locationBehavior() {
            return PatientIdentifierType.LocationBehavior.REQUIRED;
        }
    };

    public static PatientIdentifierTypeDescriptor YENDANAFE_IDENTIFIER = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "e4a1a524-d557-11ea-87d0-0242ac130003"; }
        public String name() { return "Yendanafe Identifier"; }
        public String description() { return "ID assigned to patients when registering them from Yendanafe Application"; }
        public PatientIdentifierType.LocationBehavior locationBehavior() {
            return PatientIdentifierType.LocationBehavior.REQUIRED;
        }
    };

    public static PatientIdentifierTypeDescriptor IC3D_IDENTIFIER = new PatientIdentifierTypeDescriptor() {
        public String uuid() { return "70690634-6522-4552-ba66-43eda7c30217"; }
        public String name() { return "IC3D Identifier"; }
        public String description() { return "ID assigned to patients when enrolled in IC3D Study"; }
        public PatientIdentifierType.LocationBehavior locationBehavior() {
            return PatientIdentifierType.LocationBehavior.REQUIRED;
        }
    };

}
