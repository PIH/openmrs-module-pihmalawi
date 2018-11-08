/**
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
package org.openmrs.module.pihmalawi.reporting.definition.data.definition;

import org.openmrs.Concept;
import org.openmrs.module.pihmalawi.common.CodedValueAndDate;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

/**
 * Definition that returns a CodedValueAndDate using the configured concepts
 */
public class CodedValueAndDatePatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

    @ConfigurationProperty
    private Concept codedValueQuestion;

    @ConfigurationProperty
    private Concept dateValueQuestion;

    @ConfigurationProperty
    private Boolean matchEncounterOnly;

    @ConfigurationProperty
    private Date endDate;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public CodedValueAndDatePatientDataDefinition() {
		super();
	}

	/**
	 * Name only Constructor
	 */
	public CodedValueAndDatePatientDataDefinition(String name) {
		super(name);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return CodedValueAndDate.class;
	}

    //***** ACCESSOR METHODS *****

    public Concept getCodedValueQuestion() {
        return codedValueQuestion;
    }

    public void setCodedValueQuestion(Concept codedValueQuestion) {
        this.codedValueQuestion = codedValueQuestion;
    }

    public Concept getDateValueQuestion() {
        return dateValueQuestion;
    }

    public void setDateValueQuestion(Concept dateValueQuestion) {
        this.dateValueQuestion = dateValueQuestion;
    }

    public Boolean getMatchEncounterOnly() {
        return matchEncounterOnly;
    }

    public void setMatchEncounterOnly(Boolean matchEncounterOnly) {
        this.matchEncounterOnly = matchEncounterOnly;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}