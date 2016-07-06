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
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.pihmalawi.metadata.LocationAttributeTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MetadataInitializer implements Initializer {

    protected static final Log log = LogFactory.getLog(MetadataInitializer.class);

    /**
     * @see Initializer#started()
     */
    @Override
    public synchronized void started() {

        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);
        deployService.installBundles(Context.getRegisteredComponents(MetadataBundle.class));

        // TODO: This is a one-off, and can be deleted once this is installed into both production servers.  MS 4/15/06
        Map<String, String> locationCodes = new HashMap<String, String>();

        locationCodes.put("Lisungwi Community Hospital", "LSI");
        locationCodes.put("Matope HC", "MTE");
        locationCodes.put("Chifunga HC", "CFGA");
        locationCodes.put("Zalewa HC", "ZLA");
        locationCodes.put("Nkhula Falls RHC", "NKA");
        locationCodes.put("Luwani RHC", "LWAN");
        locationCodes.put("Neno District Hospital", "NNO");
        locationCodes.put("Matandani Rural Health Center", "MTDN");
        locationCodes.put("Ligowe HC", "LGWE");
        locationCodes.put("Magaleta HC", "MGT");
        locationCodes.put("Neno Mission HC", "NOP");
        locationCodes.put("Nsambe HC", "NSM");

        for (String locationName : locationCodes.keySet()) {
            Location location = Context.getLocationService().getLocation(locationName);
            String code = locationCodes.get(locationName);
            if (location == null) {
                throw new ModuleException("Cannot find location with name: " + locationName);
            }
            LocationAttributeType type = Context.getLocationService().getLocationAttributeTypeByUuid(LocationAttributeTypes.LOCATION_CODE.uuid());
            List<LocationAttribute> existingAttributes = location.getActiveAttributes(type);
            if (existingAttributes.isEmpty()) {
                LocationAttribute att = new LocationAttribute();
                att.setLocation(location);
                att.setAttributeType(type);
                att.setValue(code);
                location.addAttribute(att);
                Context.getLocationService().saveLocation(location);
                log.warn("Added location code of " + code + " to " + locationName);
            }
        }


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
                c.addAnswer(new ConceptAnswer(cs.getConcept(8443))); // Second-hand smoke
                c.addAnswer(new ConceptAnswer(cs.getConcept(6958))); // Alcohol
                c.addAnswer(new ConceptAnswer(cs.getConcept(8493))); // Marijuana
                c.addAnswer(new ConceptAnswer(cs.getConcept(8494))); // Traditional meds
                c.addAnswer(new ConceptAnswer(cs.getConcept(3675))); // Pig/pork
                c.addAnswer(new ConceptAnswer(cs.getConcept(7633))); // Other medications
                c.addAnswer(new ConceptAnswer(cs.getConcept(8430))); // Occupational exposure
                c.addAnswer(new ConceptAnswer(cs.getConcept(2133))); // Contact with TB
                c.addAnswer(new ConceptAnswer(cs.getConcept(6408))); // Other

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
                c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
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
            c.addAnswer(new ConceptAnswer(cs.getConcept(4060))); // Sodium Valproate (SV)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8498))); // Risperidone (RIS)
            c.addAnswer(new ConceptAnswer(cs.getConcept(4045))); // Fluoxetine (FLX)

            // Adding new answers for Epilepsy
            c.addAnswer(new ConceptAnswer(cs.getConcept(238)));  // Phenobarbital (PB)
            c.addAnswer(new ConceptAnswer(cs.getConcept(273)));  // Phenytoin (PHT)
            cs.saveConcept(c);
        }

        {
            // ToDo:  Add the dose and frequency concepts to this when determined by Malawi
            Integer id = 8501;
            String uuid = "447aaa7a-cd03-11e5-9956-625662870761";
            String name = "Current Chronic Care Medication Construct";
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
                c.addSetMember(cs.getConcept(1193)); // Current drugs used
                cs.saveConcept(c);
            }
        }

        {
            // Not sure if this is used (Ellen)
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

        {
            Integer id = 8502;
            String uuid = "b2fafcfa-ce9f-11e5-ab30-625662870761";
            String name = "Medication history (text)";
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


        {
            Integer id = 8503;
            String uuid = "145044AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Clonic seizures";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8504;
            String uuid = "150525AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Absence seizures";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8505;
            String uuid = "112356AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Tonic seizures";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8506;
            String uuid = "115424AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Myoclonic seizures";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8507;
            String uuid = "148223AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Atonic seizures";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8508;
            String uuid = "b2faee68-ce9f-11e5-ab30-625662870761";
            String name = "Simple partial seizure";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8509;
            String uuid = "b2faf2c8-ce9f-11e5-ab30-625662870761";
            String name = "Complex partial seizure";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8510;
            String uuid = "123826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Unclassified epileptic seizures";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Diagnosis"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8511;
            String uuid = "161249AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Seizure type";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                c.addAnswer(new ConceptAnswer(cs.getConcept(7100))); // Tonic-clonic
                c.addAnswer(new ConceptAnswer(cs.getConcept(8503))); // Clonic
                c.addAnswer(new ConceptAnswer(cs.getConcept(8504))); // Absence
                c.addAnswer(new ConceptAnswer(cs.getConcept(8505))); // Tonic
                c.addAnswer(new ConceptAnswer(cs.getConcept(8506))); // Myoclonic
                c.addAnswer(new ConceptAnswer(cs.getConcept(8507))); // Atonic
                c.addAnswer(new ConceptAnswer(cs.getConcept(8508))); // Simple
                c.addAnswer(new ConceptAnswer(cs.getConcept(8509))); // Complex
                c.addAnswer(new ConceptAnswer(cs.getConcept(8510))); // Unclassified
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8512;
            String uuid = "b2faf9a8-ce9f-11e5-ab30-625662870761";
            String name = "Year of onset";
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
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8513;
            String uuid = "b2fafb7e-ce9f-11e5-ab30-625662870761";
            String name = "Month of onset";
            Double lowAb = 1.0;
            Double hiAb = 12.0;
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
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8514;
            String uuid = "b2fafe94-ce9f-11e5-ab30-625662870761";
            String name = "Epilepsy complications";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addAnswer(new ConceptAnswer(cs.getConcept(148)));  // Injury
                c.addAnswer(new ConceptAnswer(cs.getConcept(135)));  // Burn
                c.addAnswer(new ConceptAnswer(cs.getConcept(7721))); // Status epilepticus
                c.addAnswer(new ConceptAnswer(cs.getConcept(219)));  // Psychosis
                c.addAnswer(new ConceptAnswer(cs.getConcept(1581))); // Drug related
                c.addAnswer(new ConceptAnswer(cs.getConcept(6408))); // Other
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8515;
            String uuid = "b2fb002e-ce9f-11e5-ab30-625662870761";
            String name = "Injury, trauma, surgery of head";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8516;
            String uuid = "b2fb01f0-ce9f-11e5-ab30-625662870761";
            String name = "History of seizure";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8517;
            String uuid = "b2fb06dc-ce9f-11e5-ab30-625662870761";
            String name = "Complications at birth";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8518;
            String uuid = "82b4827e-cf4d-11e5-ab30-625662870761";
            String name = "Neonatal infection, Cerebral Malaria, and/or Meningitis";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8519;
            String uuid = "82b48760-cf4d-11e5-ab30-625662870761";
            String name = "Medical and surgical history (coded)";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addAnswer(new ConceptAnswer(cs.getConcept(8515))); // Head injury/trauma/surgery
                c.addAnswer(new ConceptAnswer(cs.getConcept(8516))); // History of seizure
                c.addAnswer(new ConceptAnswer(cs.getConcept(8517))); // Complications at birth
                c.addAnswer(new ConceptAnswer(cs.getConcept(8518))); // Neonatal infection/cerebral malaria/meningitis
                c.addAnswer(new ConceptAnswer(cs.getConcept(6022))); // Delayed milestones
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8520;
            String uuid = "82b48d50-cf4d-11e5-ab30-625662870761";
            String name = "Pre-ictal warning";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8521;
            String uuid = "82b48f4e-cf4d-11e5-ab30-625662870761";
            String name = "Post-ictal headache";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8522;
            String uuid = "82b49106-cf4d-11e5-ab30-625662870761";
            String name = "Post-ictal drowsiness";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8523;
            String uuid = "82b4926e-cf4d-11e5-ab30-625662870761";
            String name = "Post-ictal poor concentration";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8524;
            String uuid = "82b4944e-cf4d-11e5-ab30-625662870761";
            String name = "Post-ictal poor verbal or cognition";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8525;
            String uuid = "82b495d4-cf4d-11e5-ab30-625662870761";
            String name = "Post-ictal paralysis";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8526;
            String uuid = "82b49b7e-cf4d-11e5-ab30-625662870761";
            String name = "Post-ictal disorientation";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8527;
            String uuid = "82b49d4a-cf4d-11e5-ab30-625662870761";
            String name = "Post-ictal nausea";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8528;
            String uuid = "7e8aa840-cf5c-11e5-ab30-625662870761";
            String name = "Post-ictal memory loss";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8529;
            String uuid = "7e8aabb0-cf5c-11e5-ab30-625662870761";
            String name = "Post-ictal hyperactivity";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8530;
            String uuid = "7e8ab07e-cf5c-11e5-ab30-625662870761";
            String name = "Post-ictal feature";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addAnswer(new ConceptAnswer(cs.getConcept(8521))); // headache
                c.addAnswer(new ConceptAnswer(cs.getConcept(8522))); // drowsy
                c.addAnswer(new ConceptAnswer(cs.getConcept(8523))); // poor concentation
                c.addAnswer(new ConceptAnswer(cs.getConcept(8524))); // poor verbal
                c.addAnswer(new ConceptAnswer(cs.getConcept(8525))); // paralysis
                c.addAnswer(new ConceptAnswer(cs.getConcept(8526))); // disorientation
                c.addAnswer(new ConceptAnswer(cs.getConcept(8527))); // nausea
                c.addAnswer(new ConceptAnswer(cs.getConcept(8528))); // memory loss
                c.addAnswer(new ConceptAnswer(cs.getConcept(8529))); // hyperactivity
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8531;
            String uuid = "7e8ab286-cf5c-11e5-ab30-625662870761";
            String name = "Alcohol trigger";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8532;
            String uuid = "7e8ab5b0-cf5c-11e5-ab30-625662870761";
            String name = "Fever trigger";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8533;
            String uuid = "7e8ab740-cf5c-11e5-ab30-625662870761";
            String name = "Sound, light, and touch";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8534;
            String uuid = "7e8abbdc-cf5c-11e5-ab30-625662870761";
            String name = "Emotional stress, anger, boredom";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8535;
            String uuid = "7e8abdbc-cf5c-11e5-ab30-625662870761";
            String name = "Sleep deprivation and overtired";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8536;
            String uuid = "7e8abefc-cf5c-11e5-ab30-625662870761";
            String name = "Missed medication trigger";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8537;
            String uuid = "7e8abfd8-cf5c-11e5-ab30-625662870761";
            String name = "Menstruation trigger";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8538;
            String uuid = "5f51fecc-cf63-11e5-ab30-625662870761";
            String name = "Epilepsy trigger";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(8531))); // Alcohol
                c.addAnswer(new ConceptAnswer(cs.getConcept(8532))); // Fever
                c.addAnswer(new ConceptAnswer(cs.getConcept(8533))); // Sound/light
                c.addAnswer(new ConceptAnswer(cs.getConcept(8534))); // Stress
                c.addAnswer(new ConceptAnswer(cs.getConcept(8535))); // Sleep
                c.addAnswer(new ConceptAnswer(cs.getConcept(8536))); // Missed meds
                c.addAnswer(new ConceptAnswer(cs.getConcept(8537))); // Menstruation
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8539;
            String uuid = "5f52025a-cf63-11e5-ab30-625662870761";
            String name = "Seizure since last visit";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8540;
            String uuid = "5f520476-cf63-11e5-ab30-625662870761";
            String name = "Any seizure trigger";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8541;
            String uuid = "5f520642-cf63-11e5-ab30-625662870761";
            String name = "Seizure activity";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(8539))); // Seizure since last visit
                c.addAnswer(new ConceptAnswer(cs.getConcept(8540))); // Any trigger
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8542;
            String uuid = "159506AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Tongue biting";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8543;
            String uuid = "117211AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Incontinence";
            String synonym_1 = "Loss of bladder or bowel control";
            String synonym_2 = "Incontinent";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym_1, Locale.ENGLISH));
                c.addName(new ConceptName(synonym_2, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8544;
            String uuid = "159502AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Symptoms during seizure";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Finding"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                c.addAnswer(new ConceptAnswer(cs.getConcept(8542))); // Tongue biting
                c.addAnswer(new ConceptAnswer(cs.getConcept(8543))); // Incontinent
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8545;
            String uuid = "5f521038-cf63-11e5-ab30-625662870761";
            String name = "Date of exposure";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Date"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);

            }
        }

        {
            Integer id = 8546;
            String uuid = "5f52124a-cf63-11e5-ab30-625662870761";
            String name = "Exposure construct";
            Concept c = cs.getConcept(id);
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Exposure construct")) {
                log.warn("Updating " + name);
                c.setConceptClass(cs.getConceptClassByName("ConvSet"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.getFullySpecifiedName(Locale.ENGLISH).setName(name);
                c.setSet(true);
                c.getConceptSets().clear();
                c.addSetMember(cs.getConcept(8495)); // Exposure
                c.addSetMember(cs.getConcept(8545)); // Date of exposure
                c.addSetMember(cs.getConcept(2241)); // Duration (years)
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8547;
            String uuid = "5f5213e4-cf63-11e5-ab30-625662870761";
            String name = "Date of complication";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Date"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8548;
            String uuid = "5f52156a-cf63-11e5-ab30-625662870761";
            String name = "Epilepsy complication construct";
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
                c.addSetMember(cs.getConcept(8514)); // Complication
                c.addSetMember(cs.getConcept(8547)); // Date of complication
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8549;
            String uuid = "365a3434-f226-40fa-beb6-a503be77d8bf";
            String name = "Date result to guardian";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Date"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8550;
            String uuid = "7ba090ee-1d2d-11e6-b6ba-3e1d05defe78";
            String name = "Age (convenience set)";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8551;
            String uuid = "d72fc9c8-b95f-44a5-95a5-233c43849819";
            String name = "At enrollment";
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
            Integer id = 8552;
            String uuid = "5c4367e3-64d8-4242-b016-1447f7da830d";
            String name = "From 12 months";
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
            Integer id = 8553;
            String uuid = "a33786e4-930c-452c-9d8e-4ec0e744c891";
            String name = "From 24 months";
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
            Integer id = 8554;
            String uuid = "7f5721be-2803-4b09-bfd0-cd4f07cf226a";
            String name = "HIV test time period";
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

                c.addAnswer(new ConceptAnswer(cs.getConcept(8551))); // Enrollment
                c.addAnswer(new ConceptAnswer(cs.getConcept(8552))); // From 12 months
                c.addAnswer(new ConceptAnswer(cs.getConcept(8553))); // From 24 months
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 2168;
            String name = "Child HIV serology construct";
            Concept c = cs.getConcept(id);
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Child HIV serology construct")) {
                log.warn("Updating " + name);
                c.setConceptClass(cs.getConceptClassByName("ConvSet"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.getFullySpecifiedName(Locale.ENGLISH).setName(name);
                c.setSet(true);
                c.getConceptSets().clear();
                c.addSetMember(cs.getConceptByName("HIV test type"));           // Rapid test or DNA PCR
                c.addSetMember(cs.getConceptByName("Result of HIV test"));      // Neg, pos, indeterminate
                c.addSetMember(cs.getConceptByName("Date of blood draw"));      // Sample Date
                c.addSetMember(cs.getConceptByName("Lab test serial number"));  // Sample ID or HTC serial no
                c.addSetMember(cs.getConceptByName("Date of returned result")); // Date received from lab

                // Not used for EID
                c.addSetMember(cs.getConceptByName("HIV test date"));
                c.addSetMember(cs.getConceptByName("Location where test took place"));

                // Historical information from EID (Version 1)
                c.addSetMember(cs.getConceptByName("HIV test time period"));
                c.addSetMember(cs.getConceptByName("Age"));
                c.addSetMember(cs.getConceptByName("Units of age of child"));
                c.addSetMember(cs.getConceptByName("Date result to guardian"));

                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8555;
            String uuid = "5ff3ca52-0651-11e6-b512-3e1d05defe78";
            String name = "Sample taken for CD4 count (coded)";
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

                // Migrate data from 3658
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8556;
            String uuid = "fd9f79ec-0a23-11e6-b512-3e1d05defe78";
            String name = "Age at start of Neviripine construct";
            String synonym = "Age starting NVP construct";
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
                c.addName(new ConceptName(synonym, Locale.ENGLISH));
                c.addSetMember(cs.getConcept(8550)); // Age from set
                c.addSetMember(cs.getConcept(2286)); // Age units
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8557;
            String uuid = "f1904502-319d-4681-9030-e642111e7ce2";
            String name = "Time units";
            String synonym_1 = "Duration units";
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
                c.addAnswer(new ConceptAnswer(cs.getConcept(1072))); // Days
                c.addAnswer(new ConceptAnswer(cs.getConcept(1073))); // Weeks
                c.addAnswer(new ConceptAnswer(cs.getConcept(1074))); // Months
                c.addAnswer(new ConceptAnswer(cs.getConcept(2287))); // Years
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8558;
            String uuid = "159368AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Medication duration";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Numeric"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8559;
            String uuid = "fd9f7d52-0a23-11e6-b512-3e1d05defe78";
            String name = "Duration of NVP construct";
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
                c.addSetMember(cs.getConcept(8557));  // Duration units
                c.addSetMember(cs.getConcept(8558));  // Medication Duration
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8560;
            String uuid = "07a0190e-10b1-11e6-a148-3e1d05defe78";
            String name = "HIV testing completion (coded)";
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
                c.addAnswer(new ConceptAnswer(cs.getConcept(1118))); // Not done
                c.addAnswer(new ConceptAnswer(cs.getConcept(3658))); // DBS taken (PCR)
                c.addAnswer(new ConceptAnswer(cs.getConcept(3657))); // Rapid test done
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 2286;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for Units of age of child");
            c.getAnswers().clear();
            c.addAnswer(new ConceptAnswer(cs.getConcept(2287))); // Years
            c.addAnswer(new ConceptAnswer(cs.getConcept(1074))); // Months
            c.addAnswer(new ConceptAnswer(cs.getConcept(1073))); // Weeks
            c.addAnswer(new ConceptAnswer(cs.getConcept(1072))); // Days
            c.addAnswer(new ConceptAnswer(cs.getConcept(2416))); // Did not answer
            c.addAnswer(new ConceptAnswer(cs.getConcept(1067))); // Unknown
            cs.saveConcept(c);
        }

        {
            Integer id = 8561;
            String uuid = "e97b36a2-16f5-11e6-b6ba-3e1d05defe78";
            String name = "Lower than Detection Limit";
            String shortName = "LDL";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Boolean"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.setShortName(new ConceptName(shortName, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8562;
            String uuid = "121716AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Alcohol-induced mental and behavior disorder";
            String synonym = "Alcohol-induced mental disorder";
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
                c.addName(new ConceptName(synonym, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8563;
            String uuid = "90ec5559-3ba2-4fc3-abc1-614727b17141";
            String name = "Drug-induced mental and behavior disorder";
            String synonym = "Drug-induced mental disorder";
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
                c.addName(new ConceptName(synonym, Locale.ENGLISH));
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
            c.addAnswer(new ConceptAnswer(cs.getConcept(8491))); // Bipolar Affective Disorder, Manic
            c.addAnswer(new ConceptAnswer(cs.getConcept(2719))); // Anxiety
            c.addAnswer(new ConceptAnswer(cs.getConcept(8562))); // Alcohol use mental disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8563))); // Drug use mental disorder
            cs.saveConcept(c);
        }

        {
            Integer id = 8564;
            String uuid = "139146AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Hallucinations";
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

        {
            Integer id = 8565;
            String uuid = "142600AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Delusions";
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

        {
            Integer id = 8566;
            String uuid = "118905AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Disruptive Behavior Disorder";
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

        {
            Integer id = 8567;
            String uuid = "159538AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Abnormal speech";
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

        {
            Integer id = 8568;
            String uuid = "142563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Depressive Disorder";
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

        {
            Integer id = 8569;
            String uuid = "0b7892bc-43bb-11e6-beb8-9e71128cae77";
            String name = "Mental health chief complaint";
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
                c.addAnswer(new ConceptAnswer(cs.getConcept(8564))); // Hallucinations
                c.addAnswer(new ConceptAnswer(cs.getConcept(8565))); // Delusions
                c.addAnswer(new ConceptAnswer(cs.getConcept(8566))); // Disruptive Behavior Disorder
                c.addAnswer(new ConceptAnswer(cs.getConcept(8567))); // Abnormal speech
                c.addAnswer(new ConceptAnswer(cs.getConcept(8568))); // Depressive Disorder

                c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // other
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8570;
            String uuid = "0b78965e-43bb-11e6-beb8-9e71128cae77";
            String name = "Mental health chief complaint construct";
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
                c.addSetMember(cs.getConcept(8569));
                c.addSetMember(cs.getConcept(6774)); // Date
                cs.saveConcept(c);
            }
        }
        /* Not using this since we'll use the previous concept
            "Age" in weeks without units
        {
            Integer id = ;
            String uuid = "";
            String name = "Enrollment age construct";
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
                c.addSetMember(cs.getConcept(8550)); // Age from set
                c.addSetMember(cs.getConcept(2286)); // Age units
                cs.saveConcept(c);
            }
        }
        */
    }

    @Override
    public void stopped() {
    }
}