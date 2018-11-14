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

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.Date;

/**
 * Extends EvaluationContext to provide easier methods to control evaluation
 * The effectiveDate passed in allows the data to be based on a different effectiveDate
 * If this is left null, then effectiveDate will be set to the current date
 * This provides a means to look at what a refresh would look like based on a different effectiveDate
 */
public class DataRefreshContext {

    //***** PROPERTIES *****

    private EvaluationContext evaluationContext;
    private boolean replaceAllData = false;

    //***** CONSTRUCTORS *****

    public DataRefreshContext() {
        this(new Date());
    }

    public DataRefreshContext(Date effectiveDate) {
        setEffectiveDate(effectiveDate);
    }

    //***** METHODS *****

    public Date getEffectiveDate() {
        return evaluationContext == null ? null : (Date) evaluationContext.getParameterValue(ReportingConstants.END_DATE_PARAMETER.getName());
    }

    public void setEffectiveDate(Date effectiveDate) {
        if (evaluationContext == null) {
            evaluationContext = new EvaluationContext();
        }
        evaluationContext.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), effectiveDate);
    }

    //***** PROPERTY ACCESS *****

    public EvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    public void setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    public boolean isReplaceAllData() {
        return replaceAllData;
    }

    public void setReplaceAllData(boolean replaceAllData) {
        this.replaceAllData = replaceAllData;
    }
}