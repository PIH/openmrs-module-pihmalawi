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
package org.openmrs.module.pihmalawi.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

import java.util.Locale;

public class MetadataInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(MetadataInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {

		// TODO: Clean this up.  One option:
		// Create some scripts that:
		//  select all concepts associated with obs, programs, etc. as well as the answers and sets associated with them
		//  organize these by datatype and class
		// Do the same for other metadata (encounter types, etc)
		// Export these out as a series of CSVs
		//
		// Create generated source / class files for these via maven plugin
		// Associate with versions and

		ConceptService cs = Context.getConceptService();
		{
			Integer id = 8417;
			String uuid = "521f8e75-4113-4870-bcbb-9ec1d727c627";
			String name = "Chronic Care Medication Set";
			Concept c = cs.getConceptByUuid(uuid);
			if (c == null) {
				log.warn("Creating Chronic Care Medication Set");
				c = new Concept();
				c.setConceptId(id);
				c.setUuid(uuid);
				c.setConceptClass(cs.getConceptClassByName("MedSet"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
				c.setSet(true);
				c.addSetMember(cs.getConceptByName("Salbutamol"));
				c.addSetMember(cs.getConceptByName("Beclomethasone"));
				c.addSetMember(cs.getConceptByName("Hydrochlorothiazide"));
				c.addSetMember(cs.getConceptByName("Captopril"));
				c.addSetMember(cs.getConceptByName("Amlodipine"));
				c.addSetMember(cs.getConceptByName("Enalapril"));
				c.addSetMember(cs.getConceptByName("Nifedipine"));
				c.addSetMember(cs.getConceptByName("Atenolol"));
				c.addSetMember(cs.getConceptByName("Lisinopril"));
				c.addSetMember(cs.getConceptByName("Propranolol"));
				c.addSetMember(cs.getConceptByName("Phenobarbital"));
				c.addSetMember(cs.getConceptByName("Phenytoin"));
				c.addSetMember(cs.getConceptByName("Carbamazepine"));
				c.addSetMember(cs.getConceptByName("Insulin"));
				c.addSetMember(cs.getConceptByName("Metformin"));
				c.addSetMember(cs.getConceptByName("Glibenclamide"));
				c.addSetMember(cs.getConceptByName("Furosemide"));
				c.addSetMember(cs.getConceptByName("Spironolactone"));
				cs.saveConcept(c);
			}
		}
		{
			Integer id = 6872;
			String name = "Hypertension Medication Set";
			Concept c = cs.getConcept(id);
			if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Hypertension medication")) {
				log.warn("Updating " + name);
				c.setConceptClass(cs.getConceptClassByName("MedSet"));
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c.getFullySpecifiedName(Locale.ENGLISH).setName(name);
				c.setSet(true);
				c.getConceptSets().clear();
				c.addSetMember(cs.getConceptByName("Hydrochlorothiazide"));
				c.addSetMember(cs.getConceptByName("Captopril"));
				c.addSetMember(cs.getConceptByName("Amlodipine"));
				c.addSetMember(cs.getConceptByName("Enalapril"));
				c.addSetMember(cs.getConceptByName("Nifedipine"));
				c.addSetMember(cs.getConceptByName("Atenolol"));
				c.addSetMember(cs.getConceptByName("Lisinopril"));
				c.addSetMember(cs.getConceptByName("Propranolol"));
				cs.saveConcept(c);
			}
		}
		{
			{
				Integer id = 8418;
				String uuid = "5ea979aa-1369-11e4-a125-54ee7513a7ff";
				String name = "Substance abuse";
				Concept c = cs.getConcept(id);
				if (c == null) {
					log.warn("Creating " + name);
					c = new Concept();
					c.setConceptId(id);
					c.setUuid(uuid);
					c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
					c.setDatatype(cs.getConceptDatatypeByName("N/A"));
					c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
					cs.saveConcept(c);
				}
			}
		}
		{
			{
				Integer id = 8419;
				String uuid = "93e9be37-1369-11e4-a125-54ee7513a7ff";
				String name = "Acute Psychotic disorder";
				Concept c = cs.getConcept(id);
				if (c == null) {
					log.warn("Creating " + name);
					c = new Concept();
					c.setConceptId(id);
					c.setUuid(uuid);
					c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
					c.setDatatype(cs.getConceptDatatypeByName("N/A"));
					c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
					cs.saveConcept(c);
				}
			}
		}
		{
			{
				Integer id = 8420;
				String uuid = "aad4c0e9-1369-11e4-a125-54ee7513a7ff";
				String name = "Other Mental Health Diagnosis non-coded";
				Concept c = cs.getConcept(id);
				if (c == null) {
					log.warn("Creating " + name);
					c = new Concept();
					c.setConceptId(id);
					c.setUuid(uuid);
					c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
					c.setDatatype(cs.getConceptDatatypeByName("N/A"));
					c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
					cs.saveConcept(c);
				}
			}
		}
		{

            Integer id = 3683;

			Concept c = cs.getConcept(id);
			log.warn("Updating answers for Chronic Care Diagnosis");
			c.getAnswers().clear();
			c.addAnswer(new ConceptAnswer(cs.getConcept(5)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(903)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(155)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(3720)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(3468)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(7623)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(6421)));
			c.addAnswer(new ConceptAnswer(cs.getConcept(207))); // Depression
			c.addAnswer(new ConceptAnswer(cs.getConcept(8418))); // Substance abuse
			c.addAnswer(new ConceptAnswer(cs.getConcept(8419))); // Acute psychotic disorder
			c.addAnswer(new ConceptAnswer(cs.getConcept(8420))); // Other mental health diagnosis
			c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // Other non-coded
			cs.saveConcept(c);

		}

        {
            {
                Integer id = 8421;
                String uuid = "f792f2f9-9c24-4d6e-98fd-caffa8f2383f";
                String name = "Sample taken for Viral Load";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Boolean"));
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

    }

	@Override
	public void stopped() {
	}
}