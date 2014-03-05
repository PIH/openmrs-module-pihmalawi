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
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.ArrayList;
import java.util.List;

/**
 * CHW Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("pihmalawi.EncounterAndObsPatientDataDefinition")
public class EncounterAndObsPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	@ConfigurationProperty
	private Mapped<? extends EncountersForPatientDataDefinition> whichEncounters;

	@ConfigurationProperty
	private List<Concept> concepts;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public EncounterAndObsPatientDataDefinition() {
		super();
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}

	//***** PROPERTY ACCESS *****

	public Mapped<? extends EncountersForPatientDataDefinition> getWhichEncounters() {
		return whichEncounters;
	}

	public void setWhichEncounters(Mapped<? extends EncountersForPatientDataDefinition> whichEncounters) {
		this.whichEncounters = whichEncounters;
	}

	public List<Concept> getConcepts() {
		return concepts;
	}

	public void addConcept(Concept concept) {
		if (concepts == null) {
			concepts = new ArrayList<Concept>();
		}
		concepts.add(concept);
	}

	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}
}