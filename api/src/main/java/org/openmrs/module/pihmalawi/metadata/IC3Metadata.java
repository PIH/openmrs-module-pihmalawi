/*
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

import org.openmrs.EncounterType;
import org.openmrs.ProgramWorkflowState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("ic3Metadata")
public class IC3Metadata extends CommonMetadata {

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    ChronicCareMetadata ccMetadata;

    /**
     * @return list of all active states a patient could be in to be considered active in IC3
     */
    public List<ProgramWorkflowState> getActiveStates() {
        List<ProgramWorkflowState> allStates = new ArrayList<ProgramWorkflowState>(hivMetadata.getActiveHivStates());
        allStates.addAll(ccMetadata.getActiveChronicCareStates());
        return allStates;
    }

    /**
     * @return list of all encounter types a patient could make and attend scheduled IC3 visits
     */
    public List<EncounterType> getEncounterTypes() {
        List<EncounterType> allEncounterTypes = new ArrayList<EncounterType>(hivMetadata.getHivEncounterTypes());
        allEncounterTypes.addAll(ccMetadata.getChronicCareScheduledVisitEncounterTypes());
        return allEncounterTypes;
    }

}