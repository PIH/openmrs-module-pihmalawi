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

import org.openmrs.Obs;
import org.openmrs.module.reporting.common.ObjectUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A simple object that represents a patient BMI
 */
public class BMI implements JsonSerializable {

    //** PROPERTIES

    private Obs weightObs;
    private Obs heightObs;

    //***** CONSTRUCTORS *****

    public BMI(Obs weightObs, Obs heightObs) {
        this.weightObs = weightObs;
        this.heightObs = heightObs;
    }

    //***** METHODS *****

    public double getNumericValue() {
        double wt = weightObs.getValueNumeric();
        double ht = heightObs.getValueNumeric();

        if (ht == 0 || wt == 0) {
            return 0;
        }
        else{
            return wt/Math.pow(ht/100, 2);
        }
    }

    public double getNumericValueRounded(int scale) {
        BigDecimal bmi = BigDecimal.valueOf(this.getNumericValue());
        bmi = bmi.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        return bmi.doubleValue();
    }


    @Override
    public JsonObject toJsonObject() {
        JsonObject ret = new JsonObject();
        ret.put("value", getNumericValue());
        ret.put("valueAsText", getValueAsText());
        return ret;
    }

    public String getValueAsText() {
        return ObjectUtil.format(getNumericValue(), "1");
    }

    public Date getHeightDate() {
        return heightObs.getObsDatetime();
    }

    public Date getWeightDate() {
        return weightObs.getObsDatetime();
    }

    public String toString() {
        return getValueAsText();
    }

    //***** ACCESSORS ******

    public Obs getWeightObs() {
        return weightObs;
    }

    public void setWeightObs(Obs weightObs) {
        this.weightObs = weightObs;
    }

    public Obs getHeightObs() {
        return heightObs;
    }

    public void setHeightObs(Obs heightObs) {
        this.heightObs = heightObs;
    }
}
