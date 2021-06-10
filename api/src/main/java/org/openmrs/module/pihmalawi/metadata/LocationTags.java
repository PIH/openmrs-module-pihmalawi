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

import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihmalawi.PihMalawiConstants;

/**
 * Constants for all defined location tags
 */
public class LocationTags {

    public static LocationTagDescriptor HIV_STATIC = new LocationTagDescriptor() {
        public String uuid() { return "50df77f9-7c8b-4c23-a552-82814195c56c"; }
        public String name() { return "Static HIV"; }
        public String description() { return "HIV Care Clinic Location (static clinic that is not outreach)."; }
    };

    public static LocationTagDescriptor HIV_OUTREACH = new LocationTagDescriptor() {
        public String uuid() { return "ed4b1503-3235-4ba6-8e7b-03786b0a744c"; }
        public String name() { return "Outreach HIV"; }
        public String description() { return "HIV outreach site."; }
    };

    public static LocationTagDescriptor CHRONIC_CARE_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "7ce6075b-02ad-11e6-8c93-e82aea237783"; }
        public String name() { return "Chronic Care Location"; }
        public String description() { return "Location where patients in Chronic Care program are seen."; }
    };

    public static LocationTagDescriptor UPPER_NENO = new LocationTagDescriptor() {
        public String uuid() { return "7e7d5e03-6306-47f6-abfb-66e5d0e5df3b"; }
        public String name() { return "Upper Neno"; }
        public String description() { return "Locations are in the Upper Neno catchment."; }
    };

    public static LocationTagDescriptor LOWER_NENO = new LocationTagDescriptor() {
        public String uuid() { return "855c9ab3-3cd6-42c1-9430-38546a0e51c2"; }
        public String name() { return "Lower Neno"; }
        public String description() { return "Locations are in the Lower Neno catchment."; }
    };

	public static LocationTagDescriptor LOGIN_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "b8bbf83e-645f-451f-8efe-a0db56f09676"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN; }
		public String description() { return "When a user logs in and chooses a session location, they may only choose one with this tag"; }
	};

	public static LocationTagDescriptor VISIT_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "0967d73d-f3c9-492b-abed-356478610a94"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS; }
		public String description() { return "Visits are only allowed to happen at locations tagged with this location tag or at locations that descend from a location tagged with this tag."; }
	};

	public static LocationTagDescriptor ADMISSION_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "f5b9737b-14d5-402b-8475-dd558808e172"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION; }
		public String description() { return "Patients may only be admitted to inpatient care in a location with this tag"; }
	};

	public static LocationTagDescriptor TRANSFER_LOCATION = new LocationTagDescriptor() {
		public String uuid() { return "9783aba6-df7b-4969-be6e-1e03e7a08965"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER; }
		public String description() { return "Patients may only be transfer to inpatient care in a location with this tag"; }
	};

    public static LocationTagDescriptor DISPENSING_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "c42b7bc3-b34d-4b8f-9796-09208f9dfd72"; }
        public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_DISPENSING; }
        public String description() { return "A location where a pharmacist or pharmacist aide can dispensed medication."; }
    };

    public static LocationTagDescriptor TRACE_PHASE_1_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "8c572a12-5379-11e6-8bd4-54ee7513a7ff"; }
        public String name() { return "TRACE PHASE 1"; }
        public String description() { return "Indicates that this location is TRACE phase 1"; }
    };

    public static LocationTagDescriptor HEALTH_FACILITY = new LocationTagDescriptor() {
        public String uuid() { return "56595574-EF31-4731-B3BF-776922480EC3"; }
        public String name() { return PihMalawiConstants.HEALTH_FACILITY_GP_VALUE; }
        public String description() { return "Locations designated as Health Facilities."; }
    };

    public static LocationTagDescriptor MEDIC_MOBILE_FACILITY = new LocationTagDescriptor() {
        public String uuid() { return "7ae7db90-a601-41e7-bb09-fcdbbfbeaa87"; }
        public String name() { return PihMalawiConstants.MEDIC_MOBILE_FACILITY; }
        public String description() { return "Locations designated as Health Facilities."; }
    };
}