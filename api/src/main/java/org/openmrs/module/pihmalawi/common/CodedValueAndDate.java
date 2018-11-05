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

package org.openmrs.module.pihmalawi.common;

import org.openmrs.Concept;
import org.openmrs.Obs;

import java.util.Date;

/**
 * A simple object that represents an obs with an optional date obs associated with it
 */
public class CodedValueAndDate {

    //** PROPERTIES

    private Obs valueObs;
    private Obs dateObs;

    //***** CONSTRUCTORS *****

    public CodedValueAndDate(Obs valueObs) {
        this.valueObs = valueObs;
    }

    //***** METHODS *****

    public Date getDate() {
        return dateObs != null ? dateObs.getValueDate() : (valueObs != null ? valueObs.getEncounter().getEncounterDatetime() : null);
    }

    public Concept getValue() {
        return valueObs != null ? valueObs.getValueCoded() : null;
    }

    //***** ACCESSORS ******


    public Obs getValueObs() {
        return valueObs;
    }

    public void setValueObs(Obs valueObs) {
        this.valueObs = valueObs;
    }

    public Obs getDateObs() {
        return dateObs;
    }

    public void setDateObs(Obs dateObs) {
        this.dateObs = dateObs;
    }
}