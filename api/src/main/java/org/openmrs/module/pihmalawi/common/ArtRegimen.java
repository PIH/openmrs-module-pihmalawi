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
 * A simple object that represents an ART regimen
 * If a specific obs is entered to indicate the regimen date, use it.  Otherwise, use the encounter date.
 */
public class ArtRegimen {

    //** PROPERTIES

    private Obs regimenObs;
    private Obs dateObs;

    //***** CONSTRUCTORS *****

    public ArtRegimen(Obs regimenObs) {
        this.regimenObs = regimenObs;
    }

    //***** METHODS *****

    public Date getRegimenDate() {
        return dateObs != null ? dateObs.getValueDate() : (regimenObs != null ? regimenObs.getEncounter().getEncounterDatetime() : null);
    }

    public Concept getRegimen() {
        return regimenObs != null ? regimenObs.getValueCoded() : null;
    }

    //***** ACCESSORS ******

    public Obs getRegimenObs() {
        return regimenObs;
    }

    public void setRegimenObs(Obs regimenObs) {
        this.regimenObs = regimenObs;
    }

    public Obs getDateObs() {
        return dateObs;
    }

    public void setDateObs(Obs dateObs) {
        this.dateObs = dateObs;
    }
}