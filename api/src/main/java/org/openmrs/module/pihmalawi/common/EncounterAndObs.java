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
package org.openmrs.module.pihmalawi.common;


import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a holder of an Encounter and one or more Observations in that Encounter
 */
public class EncounterAndObs {

	//****** PROPERTIES ******

	private Encounter encounter;
	private Map<Concept, Obs> obs = new HashMap<Concept, Obs>();

	//****** CONSTRUCTORS ******

	public EncounterAndObs() {}

	public EncounterAndObs(Encounter encounter) {
		this.encounter = encounter;
	}

	//****** PROPERTY ACCESS ******

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public Map<Concept, Obs> getObs() {
		if (obs == null) {
			obs = new HashMap<Concept, Obs>();
		}
		return obs;
	}

	public void addObs(Obs o) {
		getObs().put(o.getConcept(), o);
	}

	public void setObs(Map<Concept, Obs> obs) {
		this.obs = obs;
	}
}
