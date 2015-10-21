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
package org.openmrs.module.pihmalawi.fragment.controller;

import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Provides encounter-specific actions
 */
public class EncounterActionFragmentController {

    public SimpleObject delete(@SpringBean("encounterService") EncounterService encounterService,
                               @RequestParam("encounter") Encounter encounter,
                               @RequestParam("reason") String reason) throws Exception {

        SimpleObject ret = new SimpleObject();
        ret.put("success", true);
        try {
            encounterService.voidEncounter(encounter, reason);
        }
        catch (Exception e) {
            ret.put("success", false);
            ret.put("message", e.getMessage());
        }
        return ret;
    }
}

