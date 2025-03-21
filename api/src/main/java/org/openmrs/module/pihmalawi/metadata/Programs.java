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

import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants for Programs
 */
public class Programs {

    // TODO: Add the others currently defined in here, and to the bundle

    // CHRONIC CARE PROGRAM

    public static ProgramDescriptor CHRONIC_CARE_PROGRAM = new ProgramDescriptor() {
        public String uuid() { return "6685164a-977f-11e1-8993-905e29aff6c1"; }
        public String name() { return "CHRONIC CARE PROGRAM"; }
        public String description() { return "Chronic Care Program"; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_PROGRAM_CONCEPT; }
        public Set<ProgramWorkflowDescriptor> workflows() {
            return new HashSet<ProgramWorkflowDescriptor>(Arrays.asList(CHRONIC_CARE_TREATMENT_STATUS, SICKLE_CELL_DISEASE_TREATMENT_WORKFLOW));  }
    };

    public static ProgramWorkflowDescriptor CHRONIC_CARE_TREATMENT_STATUS = new ProgramWorkflowDescriptor() {
        public String uuid() { return "6687086a-977f-11e1-8993-905e29aff6c1"; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_TREATMENT_STATUS_CONCEPT; }
        public Set<ProgramWorkflowStateDescriptor> states() { return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                STATUS_ON_TREATMENT, STATUS_IN_ADVANCED_CARE, STATUS_TRANSFERRED_OUT, STATUS_DIED, STATUS_DISCHARGED, STATUS_DEFAULTED, STATUS_TREATMENT_STOPPED));
        }
    };

    public static ProgramWorkflowDescriptor SICKLE_CELL_DISEASE_TREATMENT_WORKFLOW = new ProgramWorkflowDescriptor() {
        public String uuid() { return "1A6C2438-99D7-41FF-8EB4-516DFCD1D199"; }

        @Override
        public boolean retired() {
            return false;
        }

        public String conceptUuid() { return ProgramConcepts.SICKLE_CELL_DISEASE_TREATMENT_STATUS_CONCEPT_UUID; }
        public Set<ProgramWorkflowStateDescriptor> states() {
            return new HashSet<ProgramWorkflowStateDescriptor>(Arrays.asList(
                    SCD_STATE_ON_TREATMENT,
                    SCD_STATE_IN_ADVANCED_CARE,
                    SCD_STATE_DISCHARGED,
                    SCD_STATE_DEFAULTED,
                    SCD_STATE_TREATMENT_STOPPED,
                    SCD_STATE_TRANSFERRED_OUT,
                    SCD_STATE_DIED));
        }
    };

    public static ProgramWorkflowStateDescriptor STATUS_ON_TREATMENT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "66882650-977f-11e1-8993-905e29aff6c1"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_IN_ADVANCED_CARE = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "7c4d2e56-c8c2-11e8-9bc6-0242ac110001"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_TRANSFERRED_OUT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "6688275e-977f-11e1-8993-905e29aff6c1"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DIED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "6688286c-977f-11e1-8993-905e29aff6c1"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DISCHARGED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "6688297a-977f-11e1-8993-905e29aff6c1"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_DEFAULTED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "3a4eb919-b942-4c9c-ba0e-defcebe5cd4b"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor STATUS_TREATMENT_STOPPED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "dbe76d47-dbc4-4608-a578-97b6b62d9f63"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT; }
    };

    public static ProgramWorkflowStateDescriptor SCD_STATE_ON_TREATMENT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "C2B106C6-18B6-4342-B2E7-FAA0540E6DC2"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor SCD_STATE_IN_ADVANCED_CARE = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "03A8A8DF-E95E-4875-B730-2D3CD86502EF"; }
        public Boolean initial() { return true; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor SCD_STATE_DISCHARGED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "A3EA7AD8-FB32-4567-A19E-C3F6E9E33C7B"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor SCD_STATE_TREATMENT_STOPPED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "15072B28-46E9-412D-BCA4-3C96803C15AD"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor SCD_STATE_DEFAULTED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "E850CEB4-B01B-47E0-AED2-4BAB1EE2A645"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor SCD_STATE_TRANSFERRED_OUT = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "A843A2AE-FB7B-48B2-A5C2-73A82890D709"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return false; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT; }
    };
    public static ProgramWorkflowStateDescriptor SCD_STATE_DIED = new ProgramWorkflowStateDescriptor() {
        public String uuid() { return "5E228F5D-BA90-4F25-9524-E79ABAEFA01F"; }
        public Boolean initial() { return false; }
        public Boolean terminal() { return true; }
        public String conceptUuid() { return ProgramConcepts.CHRONIC_CARE_STATUS_DIED_CONCEPT; }
    };

}