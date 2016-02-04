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
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;

import java.util.Locale;

public class MetadataInitializer implements Initializer {

    protected static final Log log = LogFactory.getLog(MetadataInitializer.class);

    /**
     * @see Initializer#started()
     */
    @Override
    public synchronized void started() {

        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);
        deployService.installBundles(Context.getRegisteredComponents(MetadataBundle.class));

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
            Integer id = 8396;

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
            c.addAnswer(new ConceptAnswer(cs.getConcept(3716))); // COPD
            c.addAnswer(new ConceptAnswer(cs.getConcept(207)));  // Depression
            c.addAnswer(new ConceptAnswer(cs.getConcept(8418))); // Substance abuse
            c.addAnswer(new ConceptAnswer(cs.getConcept(8419))); // Acute psychotic disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8420))); // Other mental health diagnosis
            c.addAnswer(new ConceptAnswer(cs.getConcept(6409))); // Type 1 Diabetes
            c.addAnswer(new ConceptAnswer(cs.getConcept(6410))); // Type 2 Diabetes
            c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // Other non-coded
            cs.saveConcept(c);
        }

        {
            Integer id = 8396;
            String units = "servings per day";
            ConceptNumeric c = (ConceptNumeric) cs.getConcept(id);
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Number of servings of fruits and vegetables consumed per day")) {
                log.warn("Add units for Number of servings of fruits and vegetables consumed per day");
                c.setUnits(units);
                cs.saveConcept(c);
            }
        }

        {
            {
                Integer id = 8445;
                String uuid = "6db168f1-0f38-42d9-9f0e-90946a3d8e72";
                String name = "Chronic Care Diagnosis Construct";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("ConvSet"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addSetMember(cs.getConcept(3683));
                    c.addSetMember(cs.getConcept(6774));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8446;
                String uuid = "161261AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Days per week of moderate exercise";
                Double hiAb = 7.0;
                Double lowAb = 0.0;
                String units = "days per week";
                ConceptNumeric c = (ConceptNumeric) cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new ConceptNumeric();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                    c.setSet(false);
                    c.setHiAbsolute(hiAb);
                    c.setLowAbsolute(lowAb);
                    c.setUnits(units);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8447;
                String uuid = "160914AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Post-prandial blood glucose measurement (mg/dL)";
                String synonym_1 = "Glucose measurement after a meal ";
                Double lowAb = 0.0;
                String units = "mg/dL";
                ConceptNumeric c = (ConceptNumeric) cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new ConceptNumeric();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Test"));
                    c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                    c.setSet(false);
                    c.setLowAbsolute(lowAb);
                    c.setUnits(units);
                    c.setPrecise(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8448;
                String uuid = "160912AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Fasting blood glucose measurement (mg/dL)";
                String synonym_1 = "Fasting blood glucose";
                Double lowAb = 0.0;
                String units = "mg/dL";
                ConceptNumeric c = (ConceptNumeric) cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new ConceptNumeric();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Test"));
                    c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                    c.setSet(false);
                    c.setLowAbsolute(lowAb);
                    c.setUnits(units);
                    c.setPrecise(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8449;
                String uuid = "126580AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Sexual Disorder";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8450;
                String uuid = "115268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Neuropathy";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8451;
                String uuid = "3237c804-ada3-11e5-bf7f-feff819cdc9f";
                String name = "Neuropathy and Peripheral Vascular Disease";
                String synonym_1 = "Neuropathy and/or PVD";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8452;
                String uuid = "113257AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Retinopathy";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8453;
                String uuid = "3237c2a0-ada3-11e5-bf7f-feff819cdc9f";
                String name = "Year of Tuberculosis diagnosis";
                String synonym_1 = "TB diagnosis year";
                Double lowAb = 1950.0;
                Double hiAb = 2050.0;
                ConceptNumeric c = (ConceptNumeric) cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new ConceptNumeric(id);
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                    c.setLowAbsolute(lowAb);
                    c.setHiAbsolute(hiAb);
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8455;
                String uuid = "0e47b44c-a9b9-11e5-bf7f-feff819cdc9f";
                String name = "Stroke and Transient Ischemic Attack";
                String synonym_1 = "Stroke and TIA";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8456;
                String uuid = "142677AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Deformity of foot";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8457;
                String uuid = "53377988-ada7-11e5-bf7f-feff819cdc9f";
                String name = "Foot exam findings";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                    c.addAnswer(new ConceptAnswer(cs.getConcept(8451))); // Neuropathy/PVD
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8456))); // Deformity of foot
                    c.addAnswer(new ConceptAnswer(cs.getConcept(6566))); // Foot ulcer
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8458;
                String uuid = "53377d8e-ada7-11e5-bf7f-feff819cdc9f";
                String name = "Family history of hypertension";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                    c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                    c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                    c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8454;
                String uuid = "1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Past medical history, coded";
                String synonym_1 = "Past medical history";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));

                    c.addAnswer(new ConceptAnswer(cs.getConcept(8455))); // Stroke and tia
                    c.addAnswer(new ConceptAnswer(cs.getConcept(996)));  // Cardiovascular disease
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8452))); // Retinopathy
                    c.addAnswer(new ConceptAnswer(cs.getConcept(6033))); // Renal disease
                    c.addAnswer(new ConceptAnswer(cs.getConcept(7586))); // Peripheral vascular disease
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8450))); // Neuropathy
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8449))); // Sexual dysfunction
                    cs.saveConcept(c);
                }
            }
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

            {
                {
                    Integer id = 8426;
                    String uuid = "37519f36-8c2e-11e5-80a3-c0430f805837";
                    String name = "Asthma family history";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        c.addAnswer(new ConceptAnswer(cs.getConcept(703))); // positive
                        c.addAnswer(new ConceptAnswer(cs.getConcept(664))); // negative
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // unknown
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8427;
                    String uuid = "41afdae4-8c31-11e5-80a3-c0430f805837";
                    String name = "COPD family history";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        c.addAnswer(new ConceptAnswer(cs.getConcept(703))); // positive
                        c.addAnswer(new ConceptAnswer(cs.getConcept(664))); // negative
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // unknown
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8428;
                    String uuid = "6df61adc-8c31-11e5-80a3-c0430f805837";
                    String name = "Age at cough onset";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8429;
                    String uuid = "fd9e5898-8c31-11e5-80a3-c0430f805837";
                    String name = "Date of contact with TB+ person";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Date"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8430;
                    String uuid = "6780fd96-8c33-11e5-80a3-c0430f805837";
                    String name = "Occupational exposure";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Misc"));
                        c.setDatatype(cs.getConceptDatatypeByName("Boolean"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8431;
                    String uuid = "941bc17e-8c33-11e5-80a3-c0430f805837";
                    String name = "Name of community health worker";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Text"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8432;
                    String uuid = "af397d66-8c33-11e5-80a3-c0430f805837";
                    String name = "Last time person used tobacco";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Date"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8433;
                    String uuid = "cf066e56-8c33-11e5-80a3-c0430f805837";
                    String name = "Last time person was exposed to second hand smoke";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Date"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8434;
                    String uuid = "ec4c3db0-8c33-11e5-80a3-c0430f805837";
                    String name = "Date of last exposure to occupational hazard";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Date"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    // Use Scheduled visit (1246)?
                    Integer id = 8435;
                    String uuid = "0bea98a6-8c34-11e5-80a3-c0430f805837";
                    String name = "Planned visit";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // yes
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // no
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8436;
                    String uuid = "257fb56c-8c34-11e5-80a3-c0430f805837";
                    String name = "Daytime symptom frequency";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8437;
                    String uuid = "53e5e188-8c34-11e5-80a3-c0430f805837";
                    String name = "Nighttime symptom frequency";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8438;
                    String uuid = "79a37b88-8c34-11e5-80a3-c0430f805837";
                    String name = "Inhaler use per day";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8439;
                    String uuid = "baf99a7c-8c34-11e5-80a3-c0430f805837";
                    String name = "Inhaler use per week";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8440;
                    String uuid = "b60d8d98-8c34-11e5-80a3-c0430f805837";
                    String name = "Inhaler use per month";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8441;
                    String uuid = "af73fa84-8c35-11e5-80a3-c0430f805837";
                    String name = "Number of times inhaler is used in a year";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8442;
                    String uuid = "359808c6-8c36-11e5-80a3-c0430f805837";
                    String name = "Daily inhaled steroid use";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // yes
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // no
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8443;
                    String uuid = "e51be8fa-8c34-11e5-80a3-c0430f805837";
                    String name = "Exposed to second hand smoke?";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // yes
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // no
                        cs.saveConcept(c);
                    }
                }
            }
            {
                {
                    Integer id = 8444;
                    String uuid = "2198f6d8-8c35-11e5-80a3-c0430f805837";
                    String name = "Asthma exacerbation today";
                    Concept c = cs.getConcept(id);
                    if (c == null) {
                        log.warn("Creating " + name);
                        c = new Concept();
                        c.setConceptId(id);
                        c.setUuid(uuid);
                        c.setConceptClass(cs.getConceptClassByName("Question"));
                        c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                        c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // yes
                        c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // no
                        cs.saveConcept(c);
                    }
                }
            }

        {
            {
                Integer id = 8459;
                String uuid = "158204AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Overweight (BMI 25.0-29.9)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8460;
                String uuid = "163214AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Cardiovascular risk score";
                String synonym_1 = "CV risk score";
                Double lowAb = 0.0;
                Double hiAb = 100.0;
                String units = "%";
                ConceptNumeric c = (ConceptNumeric) cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new ConceptNumeric();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Finding"));
                    c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                    c.setLowAbsolute(lowAb);
                    c.setHiAbsolute(hiAb);
                    c.setUnits(units);
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8461;
                String uuid = "162879AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Reason for admission (text)";
                String synonym_1 = "Reason for hospitalization (text)";
                String synonym_2 = "Reason for hospital admission (text)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Text"));
                    c.setSet(false);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_2, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8462;
                String uuid = "162307AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Statins";
                String synonym_1 = "Hydroxymethylglutaryl-coenzyme A reductase inhibitors";
                String synonym_2 = "HMG CoA reductase inhibitors";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_2, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8463;
                String uuid = "163211AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Beta blockers";
                String synonym_1 = "Beta-Adrenergic antagonists";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8464;
                String uuid = "162298AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "ACE inhibitors";
                String synonym_1 = "Angiotensin-converting enzyme inhibitors drug class";
                String synonym_2 = "ACE-inhibitors";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_2, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8465;
                String uuid = "163213AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Calcium channel blockers";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8466;
                String uuid = "163212AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                String name = "Diuretics";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8467;
                String uuid = "484499f2-b58b-11e5-9f22-ba0be0483c18";
                String name = "Body Mass Index, coded";
                String synonym_1 = "BMI, coded";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                    c.addAnswer(new ConceptAnswer(cs.getConcept(3812))); // Low BMI
                    c.addAnswer(new ConceptAnswer(cs.getConcept(1115))); // Normal
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8459))); // High BMI
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8468;
                String uuid = "48449ca4-b58b-11e5-9f22-ba0be0483c18";
                String name = "Visual acuity (text)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Finding"));
                    c.setDatatype(cs.getConceptDatatypeByName("Text"));
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8469;
                String uuid = "0ed9abe4-b982-11e5-9912-ba0be0483c18";
                String name = "Discharge diagnosis (text)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                    c.setDatatype(cs.getConceptDatatypeByName("Text"));
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8470;
                String uuid = "316bc014-ba1f-11e5-9912-ba0be0483c18";
                String name = "Discharge medications (text)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Text"));
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8471;
                String uuid = "60ae316c-c15f-11e5-9912-ba0be0483c18";
                String name = "Beta-agonists (inhaled)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8472;
                String uuid = "60ae3554-c15f-11e5-9912-ba0be0483c18";
                String name = "Steroid (oral)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8473;
                String uuid = "60ae373e-c15f-11e5-9912-ba0be0483c18";
                String name = "Steroid (inhaled)";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Pharmacologic Drug Class"));
                    c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                    c.setSet(true);
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    cs.saveConcept(c);
                }
            }
        }

        {
            {
                Integer id = 8474;
                String uuid = "60ae390a-c15f-11e5-9912-ba0be0483c18";
                String name = "Chronic lung disease treatment";
                Concept c = cs.getConcept(id);
                if (c == null) {
                    log.warn("Creating " + name);
                    c = new Concept();
                    c.setConceptId(id);
                    c.setUuid(uuid);
                    c.setConceptClass(cs.getConceptClassByName("Question"));
                    c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                    c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8471))); // B-agonist
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8472))); // Steroid (inhaled)
                    c.addAnswer(new ConceptAnswer(cs.getConcept(8473))); // Steroid (oral)
                    c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // Other
                    cs.saveConcept(c);
                }
            }
        }

        {
            Integer id = 8475;
            String uuid = "163125AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Other Christianity";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8476;
            String uuid = "127116BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
            String name = "Catholic";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8477;
            String uuid = "7057c894-c5dd-11e5-9912-ba0be0483c18";
            String name = "Presbyterian ";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8478;
            String uuid = "163124AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Hinduism";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8479;
            String uuid = "162935AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Seventh Day Adventist";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8480;
            String uuid = "162933AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Muslim";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8481;
            String uuid = "162932AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Baptist";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8482;
            String uuid = "162929AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Religious affiliation";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addAnswer(new ConceptAnswer(cs.getConcept(8476))); // Catholic
                c.addAnswer(new ConceptAnswer(cs.getConcept(8477))); // Presbyterian
                c.addAnswer(new ConceptAnswer(cs.getConcept(8478))); // Hinduism
                c.addAnswer(new ConceptAnswer(cs.getConcept(8479))); // Adventist
                c.addAnswer(new ConceptAnswer(cs.getConcept(8480))); // Muslim
                c.addAnswer(new ConceptAnswer(cs.getConcept(8481))); // Baptist
                c.addAnswer(new ConceptAnswer(cs.getConcept(8475))); // Other Christianity (non-Catholic)
                c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // Other
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8483;
            String uuid = "7057cc90-c5dd-11e5-9912-ba0be0483c18";
            String name = "Psychiatry history (text)";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Text"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8484;
            String uuid = "152450AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Family history of epilepsy";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8485;
            String uuid = "7057d000-c5dd-11e5-9912-ba0be0483c18";
            String name = "Family history of mental illness";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8486;
            String uuid = "7057d3ac-c5dd-11e5-9912-ba0be0483c18";
            String name = "Family history of behavioral problems";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8487;
            String uuid = "127132AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Schizoaffective Disorder";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8488;
            String uuid = "7057d712-c5dd-11e5-9912-ba0be0483c18";
            String name = "Organic mental disorder (acute)";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8489;
            String uuid = "7057d8b6-c5dd-11e5-9912-ba0be0483c18";
            String name = "Organic mental disorder (chronic)";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8490;
            String uuid = "160200AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Mental disorder due to alcohol or illicit drug use";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8491;
            String uuid = "115924AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Bipolar Affective Disorder, Manic";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8492;
            String uuid = "7057de06-c5dd-11e5-9912-ba0be0483c18";
            String name = "Smoking";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8493;
            String uuid = "7057dfb4-c5dd-11e5-9912-ba0be0483c18";
            String name = "Cannabis";
            String synonym_1 = "Marijuana";
            String synonym_2 = "Chamba"; // Nickname used in Malawi
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Drug"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.setPreferredName(new ConceptName(synonym_1, Locale.ENGLISH));
                c.addName(new ConceptName(synonym_2, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8494;
            String uuid = "7057e27a-c5dd-11e5-9912-ba0be0483c18";
            String name = "Traditional medicine";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Drug"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8495;
            String uuid = "7057db5e-c5dd-11e5-9912-ba0be0483c18";
            String name = "Exposure";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                c.addAnswer(new ConceptAnswer(cs.getConcept(8492))); // Smoking
                c.addAnswer(new ConceptAnswer(cs.getConcept(6958))); // Alcohol
                c.addAnswer(new ConceptAnswer(cs.getConcept(8493))); // Marijuana
                c.addAnswer(new ConceptAnswer(cs.getConcept(8494))); // Traditional meds
                c.addAnswer(new ConceptAnswer(cs.getConcept(7633))); // Other medications
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8496;
            String uuid = "163044AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Mental status examination (text)";
            String synonym_1 = "MSE (text)";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Text"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym_1,Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8497;
            String uuid = "112411AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Suicide risk";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // no
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8498;
            String uuid = "83405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Risperidone";
            String shortName = "RIS";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.setShortName(new ConceptName(shortName, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 1193;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for Current Drugs Used"); // For NCD
            c.getAnswers().clear();

            // Replacing existing answers
            c.addAnswer(new ConceptAnswer(cs.getConcept(92)));   // Dapsone (92)
            c.addAnswer(new ConceptAnswer(cs.getConcept(438)));  // Streptomycin (438)
            c.addAnswer(new ConceptAnswer(cs.getConcept(656)));  // Isoniazid (656)
            c.addAnswer(new ConceptAnswer(cs.getConcept(745)));  // Ethambutol (745)
            c.addAnswer(new ConceptAnswer(cs.getConcept(747)));  // Fluconazole (747)
            c.addAnswer(new ConceptAnswer(cs.getConcept(1131))); // Rifampicin Isoniazid Pyrazinamide Ethambutol (1131)
            c.addAnswer(new ConceptAnswer(cs.getConcept(1194))); // Rifampicin and isoniazid (1194)
            c.addAnswer(new ConceptAnswer(cs.getConcept(768)));  // Rifampicin isoniazid and pyrazinamide (768)
            c.addAnswer(new ConceptAnswer(cs.getConcept(916)));  // Trimethoprim and sulfamethoxazole (916)
            c.addAnswer(new ConceptAnswer(cs.getConcept(919)));  // Nystatin (919)
            c.addAnswer(new ConceptAnswer(cs.getConcept(1195))); // Antibiotics (1195)
            c.addAnswer(new ConceptAnswer(cs.getConcept(5839))); // Antimalarial medications (5839)
            c.addAnswer(new ConceptAnswer(cs.getConcept(5841))); // Herbal traditional medications (5841)
            c.addAnswer(new ConceptAnswer(cs.getConcept(461)));  // Multivitamin (461)
            c.addAnswer(new ConceptAnswer(cs.getConcept(5843))); // Minerals iron supplements (5843)
            c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // Other non-coded (5622)
            c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown (1067)

            // Adding new answers for Hypertension and Diabetes
            c.addAnswer(new ConceptAnswer(cs.getConcept(6750))); // longActingInsulin=6750
            c.addAnswer(new ConceptAnswer(cs.getConcept(282)));  // shortActingRegularInsulin=282
            c.addAnswer(new ConceptAnswer(cs.getConcept(4052))); // metformin=4052
            c.addAnswer(new ConceptAnswer(cs.getConcept(4046))); // glibenclamide=4046
            c.addAnswer(new ConceptAnswer(cs.getConcept(8466))); // diuretic=8466
            c.addAnswer(new ConceptAnswer(cs.getConcept(8465))); // ccBlocker=8465
            c.addAnswer(new ConceptAnswer(cs.getConcept(8464))); // aceInhibit=8464
            c.addAnswer(new ConceptAnswer(cs.getConcept(8463))); // betaBlocker=8463
            c.addAnswer(new ConceptAnswer(cs.getConcept(88)));   // aspirin=88
            c.addAnswer(new ConceptAnswer(cs.getConcept(8462))); // statin=8462

            // Adding new answers for Mental Health
            c.addAnswer(new ConceptAnswer(cs.getConcept(914)));  // Chlorpromazine (CPZ)
            c.addAnswer(new ConceptAnswer(cs.getConcept(4047))); // Haloperidol (HLP)
            c.addAnswer(new ConceptAnswer(cs.getConcept(927)));  // Fluphenazine (FPZ)
            c.addAnswer(new ConceptAnswer(cs.getConcept(920)));  // Carbamazepine (CBZ)
            c.addAnswer(new ConceptAnswer(cs.getConcept(4020))); // Sodium Valproate (SV)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8498))); // Risperidone (RIS)
            c.addAnswer(new ConceptAnswer(cs.getConcept(4045))); // Fluoxetine (FLX)
            cs.saveConcept(c);
        }
        
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
                c.addSetMember(cs.getConceptByName("Risperidone"));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 3683;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for Chronic care diagnosis"); // For NCD
            c.getAnswers().clear();

            // Replacing existing answers
            c.addAnswer(new ConceptAnswer(cs.getConcept(5)));    // Asthma
            c.addAnswer(new ConceptAnswer(cs.getConcept(903)));  // Hypertension (903)
            c.addAnswer(new ConceptAnswer(cs.getConcept(155)));  // Epilepsy (155)
            c.addAnswer(new ConceptAnswer(cs.getConcept(3720))); // Diabetes (3720)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6409))); // Type 1 diabetes (6409)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6410))); // Type 2 diabetes (6410)
            c.addAnswer(new ConceptAnswer(cs.getConcept(3468))); // Heart failure (3468)
            c.addAnswer(new ConceptAnswer(cs.getConcept(7623))); // Chronic kidney disease (7623)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6421))); // Stroke (6421)
            c.addAnswer(new ConceptAnswer(cs.getConcept(3716))); // Chronic obstructive pulmonary disease (3716)
            c.addAnswer(new ConceptAnswer(cs.getConcept(207)));  // Depression (207)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8418))); // Substance abuse (8418)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8419))); // Acute Psychotic disorder (8419)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8420))); // Other Mental Health Diagnosis non-coded (8420)
            c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // Other non-coded (5622)

            // Adding new answers for Mental Health
            c.addAnswer(new ConceptAnswer(cs.getConcept(467)));  // Schizophrenia (467)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8487))); // Schizoaffective Disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8488))); // Organic mental disorder (acute)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8489))); // Organic mental disorder (chronic)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8490))); // Mental disorder from drug use
            c.addAnswer(new ConceptAnswer(cs.getConcept(8491))); // Bipolar Affective Disorder, Manic

            cs.saveConcept(c);
        }

        {
            Integer id = 8499;
            String uuid = "d5930c3a-cb57-11e5-9956-625662870761";
            String name = "0A: ABC/3TC + NVP";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Drug"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8500;
            String uuid = "d59315a4-cb57-11e5-9956-625662870761";
            String name = "0P: ABC/3TC + NVP";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Drug"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8169;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for ART Regimen");
            c.getAnswers().clear();

            c.addAnswer(new ConceptAnswer(cs.getConcept(8499))); // 0A: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8500))); // 0P: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8155))); // 1A: d4T / 3TC / NVP (previous 1L) (8155)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8156))); // 1P: d4T / 3TC / NVP (8156)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8157))); // 2A: AZT / 3TC / NVP (previous AZT) (8157)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8158))); // 2P: AZT / 3TC / NVP (8158)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8159))); // 3A: d4T / 3TC + EFV (previous EFV) (8159)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8160))); // 3P: d4T / 3TC + EFV (8160)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8162))); // 4A: AZT / 3TC + EFV (previous AZTEFV) (8162)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8163))); // 4P: AZT / 3TC + EFV (8163)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8164))); // 5A: TDF + 3TC + EFV (8164)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8165))); // 6A: TDF / 3TC + NVP (8165)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8166))); // 7A: TDF / 3TC + LPV/r (8166)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8167))); // 8A: AZT / 3TC + LPV/r (8167)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8168))); // 9P: ABC / 3TC + LPV/r (8168)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6408))); // Other (6408)

            cs.saveConcept(c);
        }

        {
            Integer id = 8170;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for ART Regimen 1");
            c.getAnswers().clear();

            c.addAnswer(new ConceptAnswer(cs.getConcept(8499))); // 0A: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8500))); // 0P: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8155))); // 1A: d4T / 3TC / NVP (previous 1L) (8155)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8156))); // 1P: d4T / 3TC / NVP (8156)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8157))); // 2A: AZT / 3TC / NVP (previous AZT) (8157)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8158))); // 2P: AZT / 3TC / NVP (8158)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8159))); // 3A: d4T / 3TC + EFV (previous EFV) (8159)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8160))); // 3P: d4T / 3TC + EFV (8160)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8162))); // 4A: AZT / 3TC + EFV (previous AZTEFV) (8162)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8163))); // 4P: AZT / 3TC + EFV (8163)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8164))); // 5A: TDF + 3TC + EFV (8164)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8165))); // 6A: TDF / 3TC + NVP (8165)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8166))); // 7A: TDF / 3TC + LPV/r (8166)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8167))); // 8A: AZT / 3TC + LPV/r (8167)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8168))); // 9P: ABC / 3TC + LPV/r (8168)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6408))); // Other (6408)

            cs.saveConcept(c);
        }

        {
            Integer id = 8171;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for ART Regimen 2");
            c.getAnswers().clear();

            c.addAnswer(new ConceptAnswer(cs.getConcept(8499))); // 0A: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8500))); // 0P: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8155))); // 1A: d4T / 3TC / NVP (previous 1L) (8155)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8156))); // 1P: d4T / 3TC / NVP (8156)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8157))); // 2A: AZT / 3TC / NVP (previous AZT) (8157)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8158))); // 2P: AZT / 3TC / NVP (8158)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8159))); // 3A: d4T / 3TC + EFV (previous EFV) (8159)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8160))); // 3P: d4T / 3TC + EFV (8160)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8162))); // 4A: AZT / 3TC + EFV (previous AZTEFV) (8162)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8163))); // 4P: AZT / 3TC + EFV (8163)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8164))); // 5A: TDF + 3TC + EFV (8164)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8165))); // 6A: TDF / 3TC + NVP (8165)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8166))); // 7A: TDF / 3TC + LPV/r (8166)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8167))); // 8A: AZT / 3TC + LPV/r (8167)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8168))); // 9P: ABC / 3TC + LPV/r (8168)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6408))); // Other (6408)

            cs.saveConcept(c);
        }

        {
            Integer id = 8172;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for ART Regimen 3");
            c.getAnswers().clear();

            c.addAnswer(new ConceptAnswer(cs.getConcept(8499))); // 0A: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8500))); // 0P: ABC/3TC + NVP
            c.addAnswer(new ConceptAnswer(cs.getConcept(8155))); // 1A: d4T / 3TC / NVP (previous 1L) (8155)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8156))); // 1P: d4T / 3TC / NVP (8156)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8157))); // 2A: AZT / 3TC / NVP (previous AZT) (8157)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8158))); // 2P: AZT / 3TC / NVP (8158)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8159))); // 3A: d4T / 3TC + EFV (previous EFV) (8159)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8160))); // 3P: d4T / 3TC + EFV (8160)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8162))); // 4A: AZT / 3TC + EFV (previous AZTEFV) (8162)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8163))); // 4P: AZT / 3TC + EFV (8163)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8164))); // 5A: TDF + 3TC + EFV (8164)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8165))); // 6A: TDF / 3TC + NVP (8165)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8166))); // 7A: TDF / 3TC + LPV/r (8166)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8167))); // 8A: AZT / 3TC + LPV/r (8167)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8168))); // 9P: ABC / 3TC + LPV/r (8168)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6408))); // Other (6408)

            cs.saveConcept(c);
        }

        }

        @Override
        public void stopped () {
        }
    }