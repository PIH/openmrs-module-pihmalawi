package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.pihmalawi.metadata.LocationTags.*;

public class Locations {

    public static LocationDescriptor UNKNOWN = new LocationDescriptor() {
        public String uuid() { return "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"; }
        public String name() { return "Unknown Location"; }
        public String description() { return "Unknown Location"; }
    };

    // UPPER NENO LOCATIONS

    public static LocationDescriptor NENO_DISTRICT_HOSPITAL = new LocationDescriptor() {
        public String uuid() { return "0d414ce2-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Neno District Hospital"; }
        public String description() { return "Neno District Hospital, formerly Neno Rural Hospital (ID=750)"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.NNO); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, HIV, HIV_STATIC, HEALTH_FACILITY, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor DAMBE = new LocationDescriptor() {
        public String uuid() { return "976dcd06-c40e-4e2e-a0de-35a54c7a52ef"; }
        public String name() { return "Dambe Clinic"; }
        public String description() { return "_DESCRIPTION"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.DAM); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION, HIV, HEALTH_FACILITY, HIV_STATIC, TRANSFER_LOCATION, CHRONIC_CARE_LOCATION);
        }
    };

    public static LocationDescriptor LIGOWE = new LocationDescriptor() {
        public String uuid() { return "0d417e38-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Ligowe HC"; }
        public String description() { return "Ligowe Health Center"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.LGWE); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION, HIV, HEALTH_FACILITY, HIV_STATIC);
        }
    };

    public static LocationDescriptor LIGOWE_HTC_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "f1306c67-dff2-484a-94ba-5544fdf37e57"; }
        public String name() { return "Ligowe HTC Outreach"; }
        public String description() { return "Ligowe outreach activities."; }
    };

    public static LocationDescriptor LUWANI_PRISON = new LocationDescriptor() {
        public String uuid() { return "a95ce8a5-8f64-4215-909d-2a0f0b6659ee"; }
        public String name() { return "Luwani Prison"; }
        public String description() { return "Luwani Prison"; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, HIV_OUTREACH, HIV);
        }
    };

    public static LocationDescriptor LUWANI = new LocationDescriptor() {
        public String uuid() { return "0d416506-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Luwani RHC"; }
        public String description() { return "Luwani Rural Health Center"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.LWAN); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, HIV_STATIC, HIV, TRACE_PHASE_1_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor MAGALETA = new LocationDescriptor() {
        public String uuid() { return "0d414eae-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Magaleta HC"; }
        public String description() { return "Magaleta Health Center (ID=751)"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.MGT); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION, HIV, HEALTH_FACILITY, HIV_STATIC);
        }
    };

    public static LocationDescriptor MAGALETA_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "ecc9878f-5bfa-4ebb-b3af-f19410ab1309"; }
        public String name() { return "Magaleta Outreach Clinic"; }
        public String description() { return "HIV Outreach Clinic"; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, HIV_OUTREACH, HIV);
        }
    };

    public static LocationDescriptor MATANDANI = new LocationDescriptor() {
        public String uuid() { return "0d415200-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Matandani Rural Health Center"; }
        public String description() { return "(ID=753)"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.MTDN); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION, HIV, HEALTH_FACILITY, HIV_STATIC);
        }
    };

    public static LocationDescriptor NENO_INWARD = new LocationDescriptor() {
        public String uuid() { return "985193ce-761a-4011-9d3e-24ddf61eba0f"; }
        public String name() { return "Neno inward patients"; }
        public String description() { return "Neno District Hospital"; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, HIV_STATIC, HIV);
        }
    };

    public static LocationDescriptor NENO_MISSION = new LocationDescriptor() {
        public String uuid() { return "0d41505c-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Neno Mission HC"; }
        public String description() { return "Neno Mission Health Center (ID=752)"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.NOP_NENO_MISSION); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, HIV_STATIC, HIV);
        }
    };

    public static LocationDescriptor NENO_PARISH = new LocationDescriptor() {
        public String uuid() { return "ca86238f-eab4-4c55-b244-2a8c82e86ecd"; }
        public String name() { return "Neno Parish Outreach Clinic"; }
        public String description() { return "HIV Outreach Clinic"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.NOP_NENO_PARISH); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, HIV_OUTREACH, HIV, HEALTH_FACILITY);
        }
    };

    public static LocationDescriptor NSAMBE = new LocationDescriptor() {
        public String uuid() { return "0d416830-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Nsambe HC"; }
        public String description() { return "Nsambe Health Center"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.NSM); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION, HIV, HEALTH_FACILITY, HIV_STATIC);
        }
    };

    public static LocationDescriptor NENO_HTC_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "83784d9a-3e96-485d-8f1f-64b2815b0efc"; }
        public String name() { return "Neno District Hospital HTC Outreach"; }
        public String description() { return "Neno District Hospital outreach activities."; }
    };


    // NDH CHILD LOCATIONS

    public static LocationDescriptor BINJE_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "3093e2ab-0eee-4bc2-aacf-8d51d77c7698"; }
        public String name() { return "Binje Outreach Clinic"; }
        public String description() { return "Outreach Clinics with ART services"; }
        public LocationDescriptor parent() { return NENO_DISTRICT_HOSPITAL; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor FPAM = new LocationDescriptor() {
        public String uuid() { return "d22f6ae0-3398-4f96-a0f8-a2435eedfe32"; }
        public String name() { return "FPAM"; }
        public String description() { return ""; }
        public LocationDescriptor parent() { return NENO_DISTRICT_HOSPITAL; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO);
        }
    };

    public static LocationDescriptor GOLDEN_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "6c090943-f5a3-47ed-b16a-b69cc5750a49"; }
        public String name() { return "Golden Outreach Clinic"; }
        public String description() { return "Outreach with ART services"; }
        public LocationDescriptor parent() { return NENO_DISTRICT_HOSPITAL; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor NTAJA_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "22e76417-b6d9-41a2-84ee-bb07175d2ddf"; }
        public String name() { return "Ntaja Outreach Clinic"; }
        public String description() { return "HIV Outreach Clinic"; }
        public LocationDescriptor parent() { return NENO_DISTRICT_HOSPITAL; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor TEDZANI = new LocationDescriptor() {
        public String uuid() { return "3b6e3a6a-dd26-4484-beda-a62fe7a75f72"; }
        public String name() { return "Tedzani Clinic"; }
        public String description() { return ""; }
        public LocationDescriptor parent() { return NENO_DISTRICT_HOSPITAL; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(UPPER_NENO);
        }
    };

    // LOWER NENO LOCATIONS

    public static LocationDescriptor CHIFUNGA = new LocationDescriptor() {
        public String uuid() { return "0d4166a0-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Chifunga HC"; }
        public String description() { return "Chifunga Health Center"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.CFGA); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_STATIC, HEALTH_FACILITY, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor CHIFUNGA_HTC_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "fd428b07-c723-4f09-b9b3-b2dae6c6a700"; }
        public String name() { return "Chifunga HTC Outreach"; }
        public String description() { return "Chifunga outreach activities."; }
    };

    public static LocationDescriptor FELEMU_OUTREACH = new LocationDescriptor() {
        public String uuid() { return ""; }
        public String name() { return ""; }
        public String description() { return ""; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_OUTREACH, TRACE_PHASE_1_LOCATION, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor KASAMBA_OUTREACH = new LocationDescriptor() {
        public String uuid() { return ""; }
        public String name() { return ""; }
        public String description() { return ""; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_OUTREACH, TRACE_PHASE_1_LOCATION, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor LISUNGWI = new LocationDescriptor() {
        public String uuid() { return "0d416376-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Lisungwi Community Hospital"; }
        public String description() { return "Lisungwi Community Hospital (ID=754)"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.LSI); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_STATIC, HEALTH_FACILITY, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor LISUNGWI_HTC_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "3e4dca9f-2619-4de2-a8a2-30ec5cafff16"; }
        public String name() { return "Lisungwi HTC Outreach"; }
        public String description() { return "Lisungwi outreach activities."; }
    };

    public static LocationDescriptor LISUNGWI_INWARD = new LocationDescriptor() {
        public String uuid() { return "dc656bcb-6146-4d7b-9c0f-ef715922d085"; }
        public String name() { return "Lisungwi inward patients"; }
        public String description() { return "Lisungwi Health Centre"; }
    };

    public static LocationDescriptor MATOPE = new LocationDescriptor() {
        public String uuid() { return "0d416b3c-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Matope HC"; }
        public String description() { return "Matope Health Center"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.MTE); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_STATIC, HEALTH_FACILITY, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor MIDZEMBA = new LocationDescriptor() {
        public String uuid() { return "0d4182e8-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Midzemba HC"; }
        public String description() { return "Midzemba HC"; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_STATIC, TRACE_PHASE_1_LOCATION, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor MIDZEMBA_HTC_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "c95df7e5-ca03-4971-bb87-bd0bd5bd2462"; }
        public String name() { return "Midzemba HTC Outreach"; }
        public String description() { return "HTC Outreach near Midzemba."; }
    };

    public static LocationDescriptor NKHULA_FALLS = new LocationDescriptor() {
        public String uuid() { return "0d4169b6-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Nkhula Falls RHC"; }
        public String description() { return "Nkhula Falls Rural Health Center"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.NKA); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_STATIC, HEALTH_FACILITY, TRACE_PHASE_1_LOCATION, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor ZALEWA = new LocationDescriptor() {
        public String uuid() { return "0d417fd2-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Zalewa HC"; }
        public String description() { return "Zalewa Health Center"; }
        public List<LocationAttributeDescriptor> attributes() { return Arrays.asList(LocationCodes.ZLA); }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO, HIV, HIV_STATIC, HEALTH_FACILITY, LOGIN_LOCATION, VISIT_LOCATION);
        }
    };

    public static LocationDescriptor ZALEWA_OUTREACH = new LocationDescriptor() {
        public String uuid() { return "80e2a374-b871-42bb-8afd-2cd740e93c6c"; }
        public String name() { return "Zalewa Outreach Clinic"; }
        public String description() { return "HIV Outreach Clinic"; }
        public List<LocationTagDescriptor> tags() {
            return Arrays.asList(LOWER_NENO);
        }
    };

    // LEGACY LOCATIONS

    public static LocationDescriptor HIV_CLINICIAN_STATION = new LocationDescriptor() {
        public String uuid() { return "0d417c9e-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "HIV Clinician Station"; }
        public String description() { return "Generic HIV Clinician Station"; }
    };

    public static LocationDescriptor HIV_NURSE_STATION = new LocationDescriptor() {
        public String uuid() { return "0d417b0e-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "HIV Nurse Station"; }
        public String description() { return "Generic HIV Nurse Station"; }
    };

    public static LocationDescriptor HIV_RECEPTION = new LocationDescriptor() {
        public String uuid() { return "0d417974-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "HIV Reception"; }
        public String description() { return "Generic HIV Reception"; }
    };

    public static LocationDescriptor KCH = new LocationDescriptor() {
        public String uuid() { return "0d415ec6-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "KCH"; }
        public String description() { return "Kamuzu Central Hospital"; }
    };

    public static LocationDescriptor KUNTUMANJI = new LocationDescriptor() {
        public String uuid() { return "0d417000-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Kuntumanji"; }
        public String description() { return "Traditional authority"; }
    };

    public static LocationDescriptor MALAWI = new LocationDescriptor() {
        public String uuid() { return "0d41539a-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Malawi"; }
        public String description() { return "The Country of Malawi"; }
    };

    public static LocationDescriptor MLAMBE_HOSPITAL = new LocationDescriptor() {
        public String uuid() { return "0d416042-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Mlambe Hospital"; }
        public String description() { return "Mlambe Hospital in Lunzu"; }
    };

    public static LocationDescriptor MULANJE_DISTRICT_HOSPITAL = new LocationDescriptor() {
        public String uuid() { return "0d4161dc-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Mulanje District Hospital"; }
        public String description() { return "Mulanje District Hospital"; }
    };

    public static LocationDescriptor MWANZA_DISTRICT_HOSPITAL = new LocationDescriptor() {
        public String uuid() { return "0d415b9c-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Mwanza District Hospital"; }
        public String description() { return "Mwanza District Hospital"; }
    };

    public static LocationDescriptor OUTPATIENT_LOCATION = new LocationDescriptor() {
        public String uuid() { return "0d415a02-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Outpatient"; }
        public String description() { return "Outpatient Department at Neno District Hospital (ID=750)"; }
    };

    public static LocationDescriptor QECH = new LocationDescriptor() {
        public String uuid() { return "0d415d36-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "QECH"; }
        public String description() { return "Queen Elizabeth Central Hospital"; }
    };

    public static LocationDescriptor REGISTRATION_LOCATION = new LocationDescriptor() {
        public String uuid() { return "0d41552a-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Registration"; }
        public String description() { return "Registration desk at Neno Rural Hospital (ID=750)"; }
    };

    public static LocationDescriptor TB_RECEPTION = new LocationDescriptor() {
        public String uuid() { return "0d4185ea-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "TB Reception"; }
        public String description() { return "Generic TB reception"; }
    };

    public static LocationDescriptor TB_SPUTUM_SUBMISSION_STATION = new LocationDescriptor() {
        public String uuid() { return "0d4185ea-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "TB Sputum Submission Station"; }
        public String description() { return "A generic TB sputum submission station"; }
    };

    public static LocationDescriptor THYOLLO = new LocationDescriptor() {
        public String uuid() { return "0d416cc2-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Thyollo District Hospital"; }
        public String description() { return "Thyollo District Hospital"; }
    };

    public static LocationDescriptor VITALS_LOCATION = new LocationDescriptor() {
        public String uuid() { return "0d4156d8-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Vitals"; }
        public String description() { return "Vitals recorded at Neno District Hospital (ID=750)"; }
    };
}
