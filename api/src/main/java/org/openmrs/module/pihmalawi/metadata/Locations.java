package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;

public class Locations {

    public static LocationDescriptor UNKNOWN = new LocationDescriptor() {
        public String uuid() { return "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"; }
        public String name() { return "Unknown Location"; }
        public String description() { return "Unknown Location"; }
    };

    // Extracted from DB to improve querying
    public static LocationDescriptor BINJE_OUTREACH_CLINIC = new LocationDescriptor() {
        public String uuid() { return "3093e2ab-0eee-4bc2-aacf-8d51d77c7698"; }
        public String name() { return "Binje Outreach Clinic"; }
        public String description() { return "Outreach Clinics with ART services"; }
    };
    public static LocationDescriptor NENO_DHO = new LocationDescriptor() {
        public String uuid() { return "0d414ce2-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Neno District Hospital"; }
        public String description() { return "Neno District Hospital, formerly Neno Rural Hospital (ID=750)"; }
    };
    public static LocationDescriptor DAMBE_CLINIC = new LocationDescriptor() {
        public String uuid() { return "976dcd06-c40e-4e2e-a0de-35a54c7a52ef"; }
        public String name() { return "Dambe Clinic"; }
        public String description() { return "Dambe Clinic"; }
    };
    public static LocationDescriptor FELEMU_OUTREACH_CLINIC = new LocationDescriptor() {
        public String uuid() { return "794df119-65e6-4098-8e12-851063267217"; }
        public String name() { return "Felemu Outreach Clinic"; }
        public String description() { return "Felemu village from Chifunga"; }
    };
    public static LocationDescriptor LIGOWE_HC = new LocationDescriptor() {
        public String uuid() { return "0d417e38-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Ligowe HC"; }
        public String description() { return "Ligowe Health Center"; }
    };
    public static LocationDescriptor LUWANI_RHC = new LocationDescriptor() {
        public String uuid() { return "0d416506-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Luwani RHC"; }
        public String description() { return "Luwani Rural Health Center"; }
    };
    public static LocationDescriptor KASAMBA_OUTREACH_CLINIC = new LocationDescriptor() {
        public String uuid() { return "6368bada-6e65-44dc-a093-c3a17a0f40f8"; }
        public String name() { return "Kasamba Outreach Clinic"; }
        public String description() { return "HIV Outreach Clinic"; }
    };
    public static LocationDescriptor MAGALETA_HC = new LocationDescriptor() {
        public String uuid() { return "0d414eae-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Magaleta HC"; }
        public String description() { return "Magaleta Health Center (ID=751)"; }
    };
    public static LocationDescriptor MATANDANI_RHC = new LocationDescriptor() {
        public String uuid() { return "0d415200-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Matandani Rural Health Center"; }
        public String description() { return "(ID=753)"; }
    };
    public static LocationDescriptor NENO_INWARD_PATIENTS= new LocationDescriptor() {
        public String uuid() { return "985193ce-761a-4011-9d3e-24ddf61eba0f"; }
        public String name() { return "Neno inward patients"; }
        public String description() { return "Neno District Hospital"; }
    };
    public static LocationDescriptor NENO_MISSION_HC= new LocationDescriptor() {
        public String uuid() { return "0d416830-5ab4-11e0-870c-9f6107fee88e"; }
        public String name() { return "Nsambe HC"; }
        public String description() { return "Nsambe Health Center"; }
    };
}
