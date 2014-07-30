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
package org.openmrs.module.pihmalawi.metadata.reference;

import org.openmrs.Concept;

/**
 * Reference to a Concept
 */
public class ConceptReference implements MetadataReference<Concept> {

	private String reference;

	public ConceptReference(String reference) {
		this.reference = reference;
	}

	@Override
	public String getReference() {
		return reference;
	}

	@Override
	public Class<Concept> getType() {
		return Concept.class;
	}

	@Override
	public Concept getTarget() {
		return MetadataLookup.getConcept(reference);
	}
}