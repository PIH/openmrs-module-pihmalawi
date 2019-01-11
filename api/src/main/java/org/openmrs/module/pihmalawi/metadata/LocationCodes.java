/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeTypeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;

/**
 * Constants for all defined location attributes
 */
public class LocationCodes {

	public static LocationAttributeDescriptor NNO = new LocationAttributeDescriptor() {
		public String uuid() { return "85e64e73-e542-4fc4-82b2-6100b127a009"; }
		public LocationDescriptor location() { return Locations.NENO_DISTRICT_HOSPITAL; }
		public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
		public String value() { return "NNO"; }
	};

    public static LocationAttributeDescriptor CFGA = new LocationAttributeDescriptor() {
        public String uuid() { return "b46e4ec6-1e32-4aab-b862-3e7ff04ff6fa"; }
        public LocationDescriptor location() { return Locations.CHIFUNGA; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "CFGA"; }
    };

    public static LocationAttributeDescriptor DAM = new LocationAttributeDescriptor() {
        public String uuid() { return "d65e7c2a-d2aa-49ae-b925-94951cdaee6f"; }
        public LocationDescriptor location() { return Locations.DAMBE; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "DAM"; }
    };

    public static LocationAttributeDescriptor LWAN = new LocationAttributeDescriptor() {
        public String uuid() { return "278f2fed-8677-4646-90be-5762330922bf"; }
        public LocationDescriptor location() { return Locations.LUWANI; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "LWAN"; }
    };

    public static LocationAttributeDescriptor LGWE = new LocationAttributeDescriptor() {
        public String uuid() { return "c0e796f8-9454-4352-a68b-7288ff9c3e47"; }
        public LocationDescriptor location() { return Locations.LIGOWE; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "LGWE"; }
    };

    public static LocationAttributeDescriptor MTDN = new LocationAttributeDescriptor() {
        public String uuid() { return "84bc454b-e050-4e70-af7e-01470af670f5"; }
        public LocationDescriptor location() { return Locations.MATANDANI; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "MTDN"; }
    };

    public static LocationAttributeDescriptor MGT = new LocationAttributeDescriptor() {
        public String uuid() { return "55141aa7-9eb8-4ca4-a6bd-c9cf025f8bfb"; }
        public LocationDescriptor location() { return Locations.MAGALETA; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "MGT"; }
    };

    public static LocationAttributeDescriptor ZLA = new LocationAttributeDescriptor() {
        public String uuid() { return "cb4a7e49-4b99-478f-bd5a-55031305a78d"; }
        public LocationDescriptor location() { return Locations.ZALEWA; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "ZLA"; }
    };

    public static LocationAttributeDescriptor NKA = new LocationAttributeDescriptor() {
        public String uuid() { return "e1c867fc-a709-4f0f-8576-b0c61755d7e5"; }
        public LocationDescriptor location() { return Locations.NKHULA_FALLS; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "NKA"; }
    };

    public static LocationAttributeDescriptor MTE = new LocationAttributeDescriptor() {
        public String uuid() { return "11f9a566-3b52-4151-9a7b-1642cdca626a"; }
        public LocationDescriptor location() { return Locations.MATOPE; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "MTE"; }
    };

    public static LocationAttributeDescriptor LSI = new LocationAttributeDescriptor() {
        public String uuid() { return "8cdc8ba4-8649-4165-b20e-b80fa66485e0"; }
        public LocationDescriptor location() { return Locations.LISUNGWI; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "LSI"; }
    };

    public static LocationAttributeDescriptor NSM = new LocationAttributeDescriptor() {
        public String uuid() { return "f43fbcf3-abbf-47e0-9214-1f7f1a967c60"; }
        public LocationDescriptor location() { return Locations.NSAMBE; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "NSM"; }
    };

    public static LocationAttributeDescriptor NOP_NENO_MISSION = new LocationAttributeDescriptor() {
        public String uuid() { return "3414a215-2b6a-4de8-9bfb-80ebb543eec9"; }
        public LocationDescriptor location() { return Locations.NENO_MISSION; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "NOP"; }
    };

    public static LocationAttributeDescriptor NOP_NENO_PARISH = new LocationAttributeDescriptor() {
        public String uuid() { return "57ddf25c-5693-4f53-bc27-9240306ed657"; }
        public LocationDescriptor location() { return Locations.NENO_PARISH; }
        public LocationAttributeTypeDescriptor type() { return LocationAttributeTypes.LOCATION_CODE; }
        public String value() { return "NOP"; }
    };
}