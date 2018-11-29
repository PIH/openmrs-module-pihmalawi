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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.common.ObjectUtil;

import java.util.Date;

/**
 * A simple object that represents an obs with an optional date obs associated with it
 */
public class CodedValueAndDate implements JsonSerializable {

    //** PROPERTIES

    private Obs valueObs;
    private Obs dateObs;

    //***** CONSTRUCTORS *****

    public CodedValueAndDate(Obs valueObs) {
        this.valueObs = valueObs;
    }

    //***** METHODS *****

    public Date getDate() {
        return dateObs != null && dateObs.getValueDate() != null ? dateObs.getValueDate() : (valueObs != null ? valueObs.getEncounter().getEncounterDatetime() : null);
    }

    public Concept getValue() {
        return valueObs != null ? valueObs.getValueCoded() : null;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject ret = new JsonObject();
        ret.put("date", getDate());
        ret.put("value", getValue());
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        CodedValueAndDate that = (CodedValueAndDate)obj;
        return new EqualsBuilder().append(this.getValueObs(), that.getValueObs()).append(this.getDateObs(), that.getDateObs()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValueObs()).append(getDateObs()).toHashCode();
    }

    @Override
    public String toString() {
        if (getValueObs() != null) {
            return getValueObs().getValueCoded().getDisplayString() + " - " + ObjectUtil.format(getDate());
        }
        return super.toString();
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