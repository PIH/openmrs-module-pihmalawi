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

package org.openmrs.module.pihmalawi.data;

import java.util.Date;

/**
 * Represents parameters to control how a Data Refresh should occur
 * The effectiveDate passed in allows the data to be based on a different effectiveDate
 * If this is left null, then effectiveDate will be set to the current date
 * But this provides a means to look at what a refresh would look like based on a different effectiveDate
 */
public class DataRefreshContext {

    //** PROPERTIES

    private Date effectiveDate = new Date();
    private boolean replaceAllData = false;

    //***** CONSTRUCTORS *****

    public DataRefreshContext() {}

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public boolean isReplaceAllData() {
        return replaceAllData;
    }

    public void setReplaceAllData(boolean replaceAllData) {
        this.replaceAllData = replaceAllData;
    }
}