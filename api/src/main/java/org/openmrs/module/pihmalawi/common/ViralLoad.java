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
import org.openmrs.module.reporting.common.ObjectUtil;

import java.util.Date;

/**
 * A simple object that represents a viral load result
 */
public class ViralLoad {

    //** PROPERTIES

    private Integer encounterId;
    private Integer groupId;
    private Date specimenDate;
    private Date resultDate;
    private Double resultNumeric;
    private Boolean resultLdl;
    private Concept reasonForTest;
    private Concept reasonNoResult;

    //***** CONSTRUCTORS *****

    public ViralLoad() {}

    //***** METHODS *****

    public Date getEffectiveDate() {
        return ObjectUtil.nvl(getSpecimenDate(), getResultDate());
    }

    //***** ACCESSORS ******


    public Integer getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Date getSpecimenDate() {
        return specimenDate;
    }

    public void setSpecimenDate(Date specimenDate) {
        this.specimenDate = specimenDate;
    }

    public Date getResultDate() {
        return resultDate;
    }

    public void setResultDate(Date resultDate) {
        this.resultDate = resultDate;
    }

    public Double getResultNumeric() {
        return resultNumeric;
    }

    public void setResultNumeric(Double resultNumeric) {
        this.resultNumeric = resultNumeric;
    }

    public Boolean getResultLdl() {
        return resultLdl;
    }

    public void setResultLdl(Boolean resultLdl) {
        this.resultLdl = resultLdl;
    }

    public Concept getReasonForTest() {
        return reasonForTest;
    }

    public void setReasonForTest(Concept reasonForTest) {
        this.reasonForTest = reasonForTest;
    }

    public Concept getReasonNoResult() {
        return reasonNoResult;
    }

    public void setReasonNoResult(Concept reasonNoResult) {
        this.reasonNoResult = reasonNoResult;
    }
}