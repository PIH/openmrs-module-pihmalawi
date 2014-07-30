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
package org.openmrs.module.pihmalawi.metadata.builder;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.Concepts;
import org.openmrs.module.pihmalawi.metadata.reference.ConceptClassReference;
import org.openmrs.module.pihmalawi.metadata.reference.ConceptDatatypeReference;
import org.openmrs.module.pihmalawi.metadata.reference.ConceptReference;
import org.openmrs.module.pihmalawi.metadata.reference.MetadataLookup;

import java.util.Locale;

/**
 * Provides factory methods and builder methods for easily creating new concepts
 */
public class ConceptBuilder {

	private Concept concept;

	private ConceptBuilder(ConceptClassReference conceptClass, ConceptDatatypeReference datatype) {
		this(conceptClass, datatype, false);
	}

	private ConceptBuilder(ConceptClassReference conceptClass, ConceptDatatypeReference datatype, boolean isSet) {
		concept = new Concept();
		concept.setConceptClass(conceptClass.getTarget());
		concept.setDatatype(datatype.getTarget());
		concept.setSet(isSet);
	}

	public ConceptBuilder id(Integer id) {
		concept.setConceptId(id);
		return this;
	}

	public ConceptBuilder uuid(String uuid) {
		concept.setUuid(uuid);
		return this;
	}

	public ConceptBuilder fullySpecifiedName(String name, Locale locale, String uuid) {
		concept.addName(conceptName(name, locale, ConceptNameType.FULLY_SPECIFIED, true, uuid));
		return this;
	}

	/**
	 * Convenience method to set the fully specified and preferred name in English
	 */
	public ConceptBuilder named(String name, String uuid) {
		return fullySpecifiedName(name, Locale.ENGLISH, uuid);
	}

	public ConceptBuilder shortName(String name, Locale locale, String uuid) {
		concept.addName(conceptName(name, locale, ConceptNameType.SHORT, false, uuid));
		return this;
	}

	public ConceptBuilder indexTerm(String name, Locale locale, String uuid) {
		concept.addName(conceptName(name, locale, ConceptNameType.INDEX_TERM, false, uuid));
		return this;
	}

	public ConceptBuilder withSetMembers(ConceptReference... members) {
		this.concept.setSet(true);
		for (ConceptReference cr : members) {
			this.concept.addSetMember(cr.getTarget());
		}
		return this;
	}

	public ConceptBuilder withAnswers(ConceptReference... answers) {
		for (ConceptReference cr : answers) {
			concept.addAnswer(new ConceptAnswer(cr.getTarget()));
		}
		return this;
	}

	public void saveIfNew() {
		if (MetadataLookup.getConcept(concept.getUuid()) == null) {
			Context.getConceptService().saveConcept(concept);
		}
	}

	public Concept getConcept() {
		return concept;
	}

	//***** FACTORY METHODS *****

	public static ConceptBuilder codedQuestion() {
		return new ConceptBuilder(Concepts.QUESTION_CLASS, Concepts.CODED_CONCEPT_DATATYPE);
	}

	public static ConceptBuilder diagnosis() {
		return new ConceptBuilder(Concepts.DIAGNOSIS_CLASS, Concepts.NA_CONCEPT_DATATYPE);
	}

	public static ConceptBuilder numericTest() {
		return new ConceptBuilder(Concepts.TEST_CLASS, Concepts.NUMERIC_CONCEPT_DATATYPE);
	}

	public static ConceptBuilder drug() {
		return new ConceptBuilder(Concepts.DRUG_CLASS, Concepts.NA_CONCEPT_DATATYPE);
	}

	public static ConceptBuilder miscConcept() {
		return new ConceptBuilder(Concepts.MISC_CLASS, Concepts.NA_CONCEPT_DATATYPE);
	}

	public static ConceptBuilder medSet() {
		return new ConceptBuilder(Concepts.MED_SET_CLASS, Concepts.NA_CONCEPT_DATATYPE, true);
	}

	public static ConceptBuilder labSet() {
		return new ConceptBuilder(Concepts.LAB_SET_CLASS, Concepts.NA_CONCEPT_DATATYPE, true);
	}

	//***** UTILITY METHODS *****

	protected static ConceptName conceptName(String name, Locale locale, ConceptNameType type, boolean preferred, String uuid) {
		ConceptName cn = new ConceptName(name, locale);
		cn.setUuid(uuid);
		cn.setConceptNameType(type);
		cn.setLocalePreferred(preferred);
		return cn;
	}
}
