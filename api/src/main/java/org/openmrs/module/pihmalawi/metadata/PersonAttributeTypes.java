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
import org.openmrs.module.metadatadeploy.descriptor.PersonAttributeTypeDescriptor;

/**
 * Constants for all defined person attribute types
 */
public class PersonAttributeTypes {

    // TODO: Add the others currently defined in here, and to the bundle

	public static PersonAttributeTypeDescriptor TEST_PATIENT = new PersonAttributeTypeDescriptor() {
		public String uuid() { return EmrApiConstants.TEST_PATIENT_ATTRIBUTE_UUID; }
		public String name() { return "Test Patient"; }
		public String description() { return "Flag to describe if the patient was created for demonstration or testing purposes"; }
		public Class<?> format() { return Boolean.class; }
		public double sortWeight() { return 8; }
	};

	public static PersonAttributeTypeDescriptor UNKNOWN_PATIENT = new PersonAttributeTypeDescriptor() {
		public String uuid() { return "8b56eac7-5c76-4b9c-8c6f-1deab8d3fc47"; }
		public String name() { return "Unknown patient"; }
		public String description() { return "Used to flag patients that cannot be identified during the check-in process"; }
		public double sortWeight() { return 13; }
	};
}