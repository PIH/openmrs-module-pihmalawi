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
 * A simple object that represents an HIV Test Result
 * There are multiple concepts that have stored test results, both questions and answers
 */
public class HivTestResult {

    //** PROPERTIES

    private Obs testResultObs;
    private Obs testTypeObs;
    private Obs specimenDateObs;
    private Obs resultDateObs;
    private Date effectiveDate;
    private Concept testType;
    private Concept testResult;

    //***** CONSTRUCTORS *****

    public HivTestResult(Obs testResultObs) {
        this.testResultObs = testResultObs;
    }

    //***** METHODS *****

    //***** ACCESSORS ******

    public Obs getTestResultObs() {
        return testResultObs;
    }

    public void setTestResultObs(Obs testResultObs) {
        this.testResultObs = testResultObs;
    }

    public Obs getTestTypeObs() {
        return testTypeObs;
    }

    public void setTestTypeObs(Obs testTypeObs) {
        this.testTypeObs = testTypeObs;
    }

    public Obs getSpecimenDateObs() {
        return specimenDateObs;
    }

    public void setSpecimenDateObs(Obs specimenDateObs) {
        this.specimenDateObs = specimenDateObs;
    }

    public Obs getResultDateObs() {
        return resultDateObs;
    }

    public void setResultDateObs(Obs resultDateObs) {
        this.resultDateObs = resultDateObs;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Concept getTestType() {
        return testType;
    }

    public void setTestType(Concept testType) {
        this.testType = testType;
    }

    public Concept getTestResult() {
        return testResult;
    }

    public void setTestResult(Concept testResult) {
        this.testResult = testResult;
    }
}