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

/**
 * Constants for all defined location tags
 */
public class LocationTags {

    // TODO: Add existing location tags to this class, and to the LocationTagBundle

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

	public static LocationTagDescriptor TRANSFER_LOCAITON = new LocationTagDescriptor() {
		public String uuid() { return "9783aba6-df7b-4969-be6e-1e03e7a08965"; }
		public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER; }
		public String description() { return "Patients may only be transfer to inpatient care in a location with this tag"; }
	};

    public static LocationTagDescriptor DISPENSING_LOCATION = new LocationTagDescriptor() {
        public String uuid() { return "c42b7bc3-b34d-4b8f-9796-09208f9dfd72"; }
        public String name() { return EmrApiConstants.LOCATION_TAG_SUPPORTS_DISPENSING; }
        public String description() { return "A location where a pharmacist or pharmacist aide can dispensed medication."; }
    };
}