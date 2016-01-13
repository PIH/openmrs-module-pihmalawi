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

    public static EncounterTypeDescriptor HTN_DIABETES_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "664b9442-977f-11e1-8993-905e29aff6c1"; }
        public String name() { return "DIABETES HYPERTENSION INITIAL VISIT"; }
        public String description() { return "Diabetes hypertension initial visit"; }
    };

    public static EncounterTypeDescriptor HTN_DIABETES_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "66079de4-a8df-11e5-bf7f-feff819cdc9f"; }
        public String name() { return "DIABETES HYPERTENSION FOLLOWUP"; }
        public String description() { return "Visit rows on the Hypertension and Diabetes card"; }
    };

    public static EncounterTypeDescriptor HTN_DIABETES_TESTS = new EncounterTypeDescriptor() {
        public String uuid() { return "6607a082-a8df-11e5-bf7f-feff819cdc9f"; }
        public String name() { return "DIABETES HYPERTENSION LAB TESTS"; }
        public String description() { return "Lab tests for Hypertension and Diabetes patients"; }
    };

    public static EncounterTypeDescriptor HTN_DIABETES_HOSPITALIZATIONS = new EncounterTypeDescriptor() {
        public String uuid() { return "6607a186-a8df-11e5-bf7f-feff819cdc9f"; }
        public String name() { return "DIABETES HYPERTENSION HOSPITALIZATIONS"; }
        public String description() { return "Hospitalizations for Hypertension and Diabetes patients"; }
    };

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

    public static EncounterTypeDescriptor ASTHMA_HOSPITALIZATION = new EncounterTypeDescriptor() {
        public String uuid() { return "2478C35B-9A71-4BD5-86B6-E8CCE594E934"; }
        public String name() { return "ASTHMA HOSPITALIZATION"; }
        public String description() { return "Hospitalization for Lung Disease"; }
    };

    public static EncounterTypeDescriptor EPILEPSY_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "D8CBF1B9-EC74-4858-8764-2350E2A9925B"; }
        public String name() { return "EPILEPSY_INITIAL"; }
        public String description() { return "Top section of the Epilepsy Mastercard"; }
    };

    public static EncounterTypeDescriptor EPILEPSY_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "1EEDD2F6-EF28-4409-8E8C-F4FEC0746E72"; }
        public String name() { return "EPILEPSY_FOLLOWUP"; }
        public String description() { return "Visit rows in the Epilepsy Mastercard"; }
    };
}