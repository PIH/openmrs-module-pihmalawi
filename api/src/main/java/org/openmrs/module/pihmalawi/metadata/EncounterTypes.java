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
 * Constants for all defined encounter types
 */
public class EncounterTypes {

    // TODO: Add the others currently defined in here, and to the bundle

    public static EncounterTypeDescriptor ADMINISTRATION = new EncounterTypeDescriptor() {
        public String uuid() { return "664b9280-977f-11e1-8993-905e29aff6c1"; }
        public String name() { return "ADMINISTRATION"; }
        public String description() { return "An administration encounter"; }
    };

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

    public static EncounterTypeDescriptor HTN_DIABETES_ANNUAL_TESTS = new EncounterTypeDescriptor() {
        public String uuid() { return "76f2759c-d729-11e6-8072-dce9fc320581"; }
        public String name() { return "ANNUAL DIABETES HYPERTENSION LAB TESTS"; }
        public String description() { return "Annual Lab tests for Hypertension and Diabetes patients"; }
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

    public static EncounterTypeDescriptor ASTHMA_PEAKFLOW = new EncounterTypeDescriptor() {
        public String uuid() { return "46028b88-c538-11e5-9912-ba0be0483c18"; }
        public String name() { return "ASTHMA PEAK FLOW"; }
        public String description() { return "Peak flow / Spirometry for Lung Disease"; }
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

    public static EncounterTypeDescriptor MENTAL_HEALTH_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "3F94849C-F245-4593-BCC8-879EAEA29168"; }
        public String name() { return "MENTAL_HEALTH_INITIAL"; }
        public String description() { return "Top section of the Mental Health Mastercard"; }
    };

    public static EncounterTypeDescriptor MENTAL_HEALTH_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "D51F45F8-0EEA-4231-A7E9-C45D57F1CBA1"; }
        public String name() { return "MENTAL_HEALTH_FOLLOWUP"; }
        public String description() { return "Visit rows in the Mental Health Mastercard"; }
    };

    public static EncounterTypeDescriptor CHRONIC_CARE_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "664bb6de-977f-11e1-8993-905e29aff6c1"; }
        public String name() { return "CHRONIC_CARE_INITIAL"; }
        public String description() { return "Top section of the Chronic Care Mastercard"; }
    };  

    public static EncounterTypeDescriptor CHRONIC_CARE_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "664bb896-977f-11e1-8993-905e29aff6c1"; }
        public String name() { return "CHRONIC_CARE_FOLLOWUP"; }
        public String description() { return "Visit rows in the Chronic Care Mastercard"; }
    };  

    public static EncounterTypeDescriptor ART_ANNUAL_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "ebaa2ad8-baaa-11e6-91a8-5622a9e78e10"; }
        public String name() { return "ANNUAL ART SCREENING"; }
        public String description() { return "Annual ART Screening"; }
    };

    public static EncounterTypeDescriptor ART_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "664b8574-977f-11e1-8993-905e29aff6c1"; }
        public String name() { return "ART_INITIAL"; }
        public String description() { return "Initial HIV clinic registration encounter"; }
    };

    public static EncounterTypeDescriptor ART_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "664b8650-977f-11e1-8993-905e29aff6c1"; }
        public String name() { return "ART_FOLLOWUP"; }
        public String description() { return "Followup visit for ART patient"; }
    };

    public static EncounterTypeDescriptor PALLIATIVE_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "e0822140-955d-11e7-abc4-cec278b6b50a"; }
        public String name() { return "PALLIATIVE_INITIAL"; }
        public String description() { return "Initial Palliative care encounter"; }
    };

    public static EncounterTypeDescriptor PALLIATIVE_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "e082235c-955d-11e7-abc4-cec278b6b50a"; }
        public String name() { return "PALLIATIVE_FOLLOWUP"; }
        public String description() { return "Followup visit for Palliative care patient"; }
    };

    public static EncounterTypeDescriptor CHF_INITIAL = new EncounterTypeDescriptor() {
        public String uuid() { return "cb337ef3-f5cb-4e10-af8d-8d717a3a139f"; }
        public String name() { return "CHF_INITIAL"; }
        public String description() { return "Initial Chronic Heart Disease encounter"; }
    };

    public static EncounterTypeDescriptor CHF_FOLLOWUP = new EncounterTypeDescriptor() {
        public String uuid() { return "1f6ad830-6e94-4819-b1fd-8c4146e77280"; }
        public String name() { return "CHF_FOLLOWUP"; }
        public String description() { return "Followup visit for Chronic Heart Disease patient"; }
    };

    public static EncounterTypeDescriptor CHF_ANNUAL_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "dbab9217-29ec-477d-8d11-161cd4fd2b34"; }
        public String name() { return "CHF_ANNUAL_SCREENING"; }
        public String description() { return "Annual screening for Chronic Heart Disease"; }
    };

    public static EncounterTypeDescriptor CHF_QUARTERLY_HIV_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "df5389a8-7715-462b-a41e-e8b1946c8c60"; }
        public String name() { return "CHF_QUARTERLY_HIV_SCREENING"; }
        public String description() { return "Quarterly HIV screening for Chronic Heart Disease"; }
    };

    public static EncounterTypeDescriptor CHF_HOSPITALIZATIONS = new EncounterTypeDescriptor() {
        public String uuid() { return "0d3f097f-206c-4c31-9fcb-2ba61145b69f"; }
        public String name() { return "CHF_HOSPITALIZATIONS"; }
        public String description() { return "Hospitalizations for Chronic Heart Disease patients"; }
    };

    // IC3 Screening POC system
    public static EncounterTypeDescriptor CHECK_IN = new EncounterTypeDescriptor() {
        public String uuid() { return "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b"; }
        public String name() { return "Check-in"; }
        public String description() { return "Check-in encounter"; }
    };
    public static EncounterTypeDescriptor BLOOD_PRESSURE_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "0C36F6FB-660E-485F-AF04-249579C9EAC9"; }
        public String name() { return "Blood pressure screening"; }
        public String description() { return "Blood pressure screening encounter"; }
    };
    public static EncounterTypeDescriptor NUTRITION_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "6265F6BC-EBC0-4181-91F3-28B70BBFDB61"; }
        public String name() { return "Nutrition screening"; }
        public String description() { return "Nutrition screening encounter"; }
    };
    public static EncounterTypeDescriptor NURSE_EVALUATION = new EncounterTypeDescriptor() {
        public String uuid() { return "1e2f8be8-8ae3-41f1-b908-84f168f26325"; }
        public String name() { return "Nurse evaluation"; }
        public String description() { return "Nurse evaluation encounter"; }
    };
    public static EncounterTypeDescriptor HTC_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "5B7238C1-23C6-4214-957F-7912A5BE87A9"; }
        public String name() { return "HTC Screening"; }
        public String description() { return "HTC screening test encounter"; }
    };
    public static EncounterTypeDescriptor VIRAL_LOAD_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "9959A261-2122-4AE1-A89D-1CA444B712EA"; }
        public String name() { return "Viral Load Screening"; }
        public String description() { return "Viral Load screening test encounter"; }
    };
    public static EncounterTypeDescriptor EID_LOAD_SCREENING = new EncounterTypeDescriptor() {
        public String uuid() { return "8383DE35-5145-4953-A018-34876B797F3E"; }
        public String name() { return "EID Screening"; }
        public String description() { return "EID screening test encounter"; }
    };
}
