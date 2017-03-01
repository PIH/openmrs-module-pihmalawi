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

import org.openmrs.module.pihmalawi.common.TraceConstants.Category;
import org.openmrs.module.pihmalawi.common.TraceConstants.ReturnVisitCategory;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.pihmalawi.common.TraceConstants.Category.*;
import static org.openmrs.module.pihmalawi.common.TraceConstants.ReturnVisitCategory.*;

/**
 * A simple object that contains information about the status of a patient's trace eligibility
 */
public class TraceCriteria {

    //** PROPERTIES

    private List<Category> categories = new ArrayList<Category>();

    //***** CONSTRUCTORS *****

    public TraceCriteria() {
    }

    //***** METHODS *****

    public boolean isTraceEligible() {
        return !getCategories().isEmpty();
    }

    public boolean hasMissedVisit() {
        return hasAnyCategory(LATE_HIV_VISIT, LATE_NCD_VISIT_NORMAL_PRIORITY, LATE_NCD_VISIT_HIGH_PRIORITY);
    }

    public boolean hasLabResultsReady() {
        return hasAnyCategory(HIGH_VIRAL_LOAD, EID_POSITIVE_6_WK, EID_NEGATIVE);
    }

    public boolean dueForLabWork() {
        return hasAnyCategory(REPEAT_VIRAL_LOAD, EID_12_MONTH_TEST, EID_24_MONTH_TEST);
    }

    public boolean isPriorityPatient() {
        return hasAnyCategory(LATE_HIV_VISIT, REPEAT_VIRAL_LOAD, EID_POSITIVE_6_WK, LATE_NCD_VISIT_HIGH_PRIORITY);
    }

    public ReturnVisitCategory getReturnVisitCategory() {
        if (hasAnyCategory(LATE_HIV_VISIT, EID_POSITIVE_6_WK, EID_NEGATIVE)) {
            return TODAY;
        }
        if (hasAnyCategory(HIGH_VIRAL_LOAD, LATE_NCD_VISIT_NORMAL_PRIORITY, LATE_NCD_VISIT_HIGH_PRIORITY)) {
            return NEXT_CLINIC_DAY;
        }
        return APPOINTMENT_DATE;
    }

    @Override
    public String toString() {
        return OpenmrsUtil.join(getCategories(), ", ");
    }

    //***** ACCESSORS ******

    public List<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<Category>();
        }
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        getCategories().add(category);
    }

    protected boolean hasAnyCategory(Category... categories) {
        if (categories != null) {
            for (Category c : categories) {
                if (getCategories().contains(c)) {
                    return true;
                }
            }
        }
        return false;
    }
}