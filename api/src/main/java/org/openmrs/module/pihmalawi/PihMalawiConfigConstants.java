package org.openmrs.module.pihmalawi;

/**
 * Flat UUID/name constants for pihmalawi metadata, replacing the descriptor-based
 * constant classes that depended on the metadatadeploy module (removed in MLW-1732).
 */
public class PihMalawiConfigConstants {

    // Locations
    public static final String LOCATION_UNKNOWN_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
    public static final String LOCATION_DAMBE_CLINIC_UUID = "976dcd06-c40e-4e2e-a0de-35a54c7a52ef";
    public static final String LOCATION_DAMBE_CLINIC_NAME = "Dambe Clinic";
    public static final String LOCATION_LIGOWE_HC_UUID = "0d417e38-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_LIGOWE_HC_NAME = "Ligowe HC";
    public static final String LOCATION_LUWANI_RHC_UUID = "0d416506-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_LUWANI_RHC_NAME = "Luwani RHC";
    public static final String LOCATION_MAGALETA_HC_UUID = "0d414eae-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_MAGALETA_HC_NAME = "Magaleta HC";
    public static final String LOCATION_MATANDANI_RHC_UUID = "0d415200-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_MATANDANI_RHC_NAME = "Matandani Rural Health Center";
    public static final String LOCATION_NENO_DHO_UUID = "0d414ce2-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_NENO_DHO_NAME = "Neno District Hospital";
    public static final String LOCATION_NENO_INWARD_PATIENTS_UUID = "985193ce-761a-4011-9d3e-24ddf61eba0f";
    public static final String LOCATION_NENO_INWARD_PATIENTS_NAME = "Neno inward patients";
    public static final String LOCATION_NENO_MISSION_HC_UUID = "0d416830-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_NENO_MISSION_HC_NAME = "Nsambe HC";

    // Location tags
    public static final String LOCATIONTAG_UPPER_NENO_NAME = "Upper Neno";
    public static final String LOCATIONTAG_LOWER_NENO_NAME = "Lower Neno";
    public static final String LOCATIONTAG_CHRONIC_CARE_LOCATION_NAME = "Chronic Care Location";
    public static final String LOCATIONTAG_HIV_STATIC_NAME = "Static HIV";
    public static final String LOCATIONTAG_MEDIC_MOBILE_FACILITY_UUID = "7ae7db90-a601-41e7-bb09-fcdbbfbeaa87";
    public static final String LOCATIONTAG_TRACE_PHASE_1_LOCATION_NAME = "TRACE PHASE 1";

    // Location attribute types
    public static final String LOCATIONATTRIBUTETYPE_LOCATION_CODE_UUID = "62eb8441-0326-11e6-8c93-e82aea237783";

    // Patient identifier types
    public static final String PATIENTIDENTIFIERTYPE_NUTRITION_PROGRAM_NUMBER_UUID = "C9888967-8584-4F36-86B8-51AC368BC720";
    public static final String PATIENTIDENTIFIERTYPE_NUTRITION_PROGRAM_NUMBER_NAME = "Nutrition Program Number";
    public static final String PATIENTIDENTIFIERTYPE_PALLIATIVE_CARE_NUMBER_UUID = "f2b29f9b-69d0-4339-b1aa-55a511672558";
    public static final String PATIENTIDENTIFIERTYPE_PALLIATIVE_CARE_NUMBER_NAME = "Palliative Care Number";
    public static final String PATIENTIDENTIFIERTYPE_IC3_IDENTIFIER_UUID = "f51dfa3a-95de-4040-b4eb-52d2de718a74";
    public static final String PATIENTIDENTIFIERTYPE_IC3_IDENTIFIER_NAME = "IC3 Identifier";
    public static final String PATIENTIDENTIFIERTYPE_IC3_IDENTIFIER_DESCRIPTION = "ID assigned to patients at IC3 clinic who have not be enrolled in a program";
    public static final String PATIENTIDENTIFIERTYPE_YENDANAFE_IDENTIFIER_UUID = "e4a1a524-d557-11ea-87d0-0242ac130003";
    public static final String PATIENTIDENTIFIERTYPE_YENDANAFE_IDENTIFIER_NAME = "Yendanafe Identifier";
    public static final String PATIENTIDENTIFIERTYPE_IC3D_IDENTIFIER_UUID = "70690634-6522-4552-ba66-43eda7c30217";
    public static final String PATIENTIDENTIFIERTYPE_IC3D_IDENTIFIER_NAME = "IC3D Identifier";
    public static final String PATIENTIDENTIFIERTYPE_PDC_IDENTIFIER_UUID = "f7de1b97-013e-49ad-a596-4ada6ede1053";
    public static final String PATIENTIDENTIFIERTYPE_PDC_IDENTIFIER_NAME = "PDC Identifier";
    public static final String PATIENTIDENTIFIERTYPE_TB_PROGRAM_IDENTIFIER_UUID = "F4319B47-4141-48DF-9F41-5CF7E6301EC6";
    public static final String PATIENTIDENTIFIERTYPE_TB_PROGRAM_IDENTIFIER_NAME = "TB program identifier";

    // Provider attribute types
    public static final String PROVIDERATTRIBUTETYPE_HEALTH_FACILITY_UUID = "94047146-7918-4927-9401-F4284A10C7FD";
}
