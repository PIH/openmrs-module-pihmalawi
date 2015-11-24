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

import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;

/**
 * Constants for all defined person attribute types
 */
public class EncounterTypes {

    // TODO: Add the others currently defined in here, and to the bundle

	public static EncounterTypeDescriptor ASTHMA_INITIAL = new EncounterTypeDescriptor() {
		public String uuid() { return "a95dc43f-925c-11e5-a1de-e82aea237783"; }
		public String name() { return "ASTHMA_INITIAL"; }
		public String description() { return "Top section of the Asthma Mastercard"; }
	};

    public static EncounterTypeDescriptor ASTHMA_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "f4596df5-925c-11e5-a1de-e82aea237783"; }
        public String name() { return "ASTHMA_FOLLOWUP"; }
        public String description() { return "Visit rows in the Asthma Mastercard"; }
    };

}