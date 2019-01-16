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

package org.openmrs.module.pihmalawi.alert;

import java.util.List;

/**
 * Represents an Alert, typically defined in JSON as follows
     {
         "name": "due-for-initial-routine-viral-load",
         "categories": ["viral-load"],
         "conditions": [
             "age_years >= 3",
             "hiv_treatment_status = active_art",
             "missing(last_viral_load_date) || (last_viral_load_date < last_art_regimen_change_date)",
             "daysSince(last_art_regimen_change_date) > 182"
         ],
         "alert": "Due for routine VL",
         "notes": "Any description of notes that should be associated with this alert"
     },
 */
public class AlertDefinition extends AlertNotification{

    //** PROPERTIES
    private List<String> conditions;
    private String notes;
    private boolean enabled = true;

    //***** CONSTRUCTORS *****

    public AlertDefinition() {
        super();
    }

    //***** METHODS *****

    @Override
    public String toString() {
        return getName();
    }


    // ***** ACCESSORS *****

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
