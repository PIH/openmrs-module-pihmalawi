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

import org.apache.commons.lang.StringUtils;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MetadataInitializer implements Initializer {

    protected static final Log log = LogFactory.getLog(MetadataInitializer.class);

    public boolean hasAnswer(Collection<ConceptAnswer> answers, Concept concept){
        if ( answers != null && answers.size() > 0 ){
            for (ConceptAnswer answer : answers) {
                if (StringUtils.equals(answer.getAnswerConcept().getUuid(), concept.getUuid())) {
                    return true;
                }
            }
        }
        return false;
    }

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
            c.addAnswer(new ConceptAnswer(cs.getConcept(8601))); // 9A: ABC / 3TC + LPV/r (8601)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8168))); // 9P: ABC / 3TC + LPV/r (8168)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8602))); // 10A: TDF / 3TC + LPV/r (8602)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8603))); // 11A: AZT / 3TC + LPV/r (8603)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8604))); // 11P: AZT / 3TC + LPV/r (8604)
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
            c.addAnswer(new ConceptAnswer(cs.getConcept(8601))); // 9A: ABC / 3TC + LPV/r (8601)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8168))); // 9P: ABC / 3TC + LPV/r (8168)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8602))); // 10A: TDF / 3TC + LPV/r (8602)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8603))); // 11A: AZT / 3TC + LPV/r (8603)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8604))); // 11P: AZT / 3TC + LPV/r (8604)
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

            c.addAnswer(new ConceptAnswer(cs.getConcept(8601))); // 9A: ABC / 3TC + LPV/r (8601)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8168))); // 9P: ABC / 3TC + LPV/r (8168)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8602))); // 10A: TDF / 3TC + LPV/r (8602)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8603))); // 11A: AZT / 3TC + LPV/r (8603)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8604))); // 11P: AZT / 3TC + LPV/r (8604)
            c.addAnswer(new ConceptAnswer(cs.getConcept(6408))); // Other (6408)

            cs.saveConcept(c);
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
            Integer id = 8569;
            Concept c = cs.getConcept(id);
            log.warn("Updating answers for Mental health chief complaint");
            c.getAnswers().clear();

            c.addAnswer(new ConceptAnswer(cs.getConcept(8564))); // Hallucinations
            c.addAnswer(new ConceptAnswer(cs.getConcept(8565))); // Delusions
            c.addAnswer(new ConceptAnswer(cs.getConcept(8566))); // Disruptive Behavior Disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8567))); // Abnormal speech
            c.addAnswer(new ConceptAnswer(cs.getConcept(8568))); // Depressive Disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8578))); // Elevated mood
            c.addAnswer(new ConceptAnswer(cs.getConcept(8579))); // Insight
            c.addAnswer(new ConceptAnswer(cs.getConcept(5622))); // other
            cs.saveConcept(c);
        }

        {
            Integer id = 8594;
            String uuid = "f63a586c-58f1-11e6-8b77-86f30ca893d3";
            String name = "Mental health diagnosis set";
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
                c.addSetMember(cs.getConcept(467));  // Schizophrenia (467)
                c.addSetMember(cs.getConcept(8491)); // Bipolar Affective Disorder, Manic
                c.addSetMember(cs.getConcept(207));  // Depression (207)
                c.addSetMember(cs.getConcept(8419)); // Acute Psychotic disorder (8419)
                c.addSetMember(cs.getConcept(8487)); // Schizoaffective Disorder
                c.addSetMember(cs.getConcept(2719)); // Anxiety
                c.addSetMember(cs.getConcept(8488)); // Organic mental disorder (acute)
                c.addSetMember(cs.getConcept(8489)); // Organic mental disorder (chronic)
                c.addSetMember(cs.getConcept(8563)); // Drug use mental disorder
                c.addSetMember(cs.getConcept(8562)); // Alcohol use mental disorder
                c.addSetMember(cs.getConcept(8420)); // Other Mental Health Diagnosis non-coded
                c.addSetMember(cs.getConcept(8585)); // Stress, and adjustment disorder
                c.addSetMember(cs.getConcept(8586)); // Dissociative, Conversion and Factitious Disorders
                c.addSetMember(cs.getConcept(8587)); // Somatoform Disorder
                c.addSetMember(cs.getConcept(8588)); // Puerperal disorder
                c.addSetMember(cs.getConcept(8589)); // Personality Disorder
                c.addSetMember(cs.getConcept(8590)); // Mental retardation
                c.addSetMember(cs.getConcept(8591)); // Psychological development disorder
                c.addSetMember(cs.getConcept(8592)); // Hyperkinetic behavior
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
            c.addAnswer(new ConceptAnswer(cs.getConcept(8419))); // Acute Psychotic disorder (8419)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8420))); // Other Mental Health Diagnosis non-coded (8420)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8580))); // Other Mental Health Diagnosis 1
            c.addAnswer(new ConceptAnswer(cs.getConcept(8581))); // Other Mental Health Diagnosis 2
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
            c.addAnswer(new ConceptAnswer(cs.getConcept(8585))); // Stress, and adjustment disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8586))); // Dissociative, Conversion and Factitious Disorders
            c.addAnswer(new ConceptAnswer(cs.getConcept(8587))); // Somatoform Disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8588))); // Puerperal disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8589))); // Personality Disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8590))); // Mental retardation
            c.addAnswer(new ConceptAnswer(cs.getConcept(8591))); // Psychological development disorder
            c.addAnswer(new ConceptAnswer(cs.getConcept(8592))); // Hyperkinetic behavior

            // Adding new answers for "other" diagnoses
            c.addAnswer(new ConceptAnswer(cs.getConcept(7518))); // Sickle Cell Disease
            c.addAnswer(new ConceptAnswer(cs.getConcept(3779))); // Tropical Splenomegaly Syndrome (3779)
            c.addAnswer(new ConceptAnswer(cs.getConcept(3714))); // Liver Cirrhosis (3714)
            c.addAnswer(new ConceptAnswer(cs.getConcept(7623))); // Chronic Kidney Disease (7623)
            c.addAnswer(new ConceptAnswer(cs.getConcept(221)));  // Rheumatic Heart disease (221)
            c.addAnswer(new ConceptAnswer(cs.getConcept(2720))); // Congestive Heart Disease (2720)
            c.addAnswer(new ConceptAnswer(cs.getConcept(8595))); // Polycystic kidney disease (PKD)
            c.addAnswer(new ConceptAnswer(cs.getConcept(202)));  // Rheumatoid arthritis (202)
            c.addAnswer(new ConceptAnswer(cs.getConcept(27)));   // Hepatitis B (27)

            cs.saveConcept(c);
        }

        {
            // 	HbA1c, Glycated hemoglobin
            Integer id = 6422;
            // String uuid = "65714f76-977f-11e1-8993-905e29aff6c1";
            ConceptNumeric c = (ConceptNumeric) cs.getConcept(id);
            if (c != null) {
                // the concept should already exist
                String name = "Glycated hemoglobin";
                log.warn("Updating " + name);
                c.setUnits("%");
                c.setPrecise(true);
                cs.saveConcept(c);
            }
        }

        {
            // ToDo:  Add the dose and frequency concepts to this when determined by Malawi
            Integer id = 8501;
            // String uuid = "447aaa7a-cd03-11e5-9956-625662870761";
            String name = "Current Chronic Care Medication Construct";
            Concept c = cs.getConcept(id);
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Current Chronic Care Medication Construct")) {
                log.warn("Updating " + name);
                c.setConceptClass(cs.getConceptClassByName("ConvSet"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.getFullySpecifiedName(Locale.ENGLISH).setName(name);
                c.setSet(true);
                c.getConceptSets().clear();
                c.addSetMember(cs.getConcept(1193)); // Current drugs used
                c.addSetMember(cs.getConcept(8575)); // non-coded drug name
                cs.saveConcept(c);
            }
        }
        {
            Integer id = 8600;
            String uuid = "48964d18-ba37-11e6-91a8-5622a9e78e10";
            String name = "Child HCC Registration Number";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("Text"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8601;
            String uuid = "8a795372-ba39-11e6-91a8-5622a9e78e10";
            String name = "9A: ABC / 3TC + LPV/r";
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
            Integer id = 8602;
            String uuid = "7ebc782a-baa2-11e6-91a8-5622a9e78e10";
            String name = "10A: TDF / 3TC + LPV/r";
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
            Integer id = 8603;
            String uuid = "8bb7294e-baa2-11e6-91a8-5622a9e78e10";
            String name = "11A: AZT / 3TC + LPV";
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
            Integer id = 8604;
            String uuid = "91bcdad2-baa2-11e6-91a8-5622a9e78e10";
            String name = "11P: AZT / 3TC + LPV";
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
            Integer id = 8605;
            String uuid = "c8489048-c027-11e6-bb84-f5cb1a1f4e2a";
            String name = "CPT + IPT: Cotrimoxazole and Isoniazid Preventive Therapy";
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
            // ToDo:  Add the dose and frequency concepts to this when determined by Malawi
            Integer id = 8606;
            String uuid = "07b4909c-c028-11e6-bb84-f5cb1a1f4e2a";
            String name = "HIV Preventive Therapy";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.setSet(false);
                c.getConceptSets().clear();
                c.addSetMember(cs.getConcept(1193)); // Current drugs used
                c.addSetMember(cs.getConcept(2834)); // pills dispensed
                c.addAnswer(new ConceptAnswer(cs.getConcept(916)));  // CPT: Trimethoprim and sulfamethoxazole
                c.addAnswer(new ConceptAnswer(cs.getConcept(656)));  // IPT: Isoniazid
                c.addAnswer(new ConceptAnswer(cs.getConcept(8605)));  // CPT + IPT
                cs.saveConcept(c);
            }
        }
        {
            Integer id = 8607;
            String uuid = "07b4909c-c028-11e6-bb84-f5cb1a1f4e2a";
            String name = "HIV Preventive Therapy Construct";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptClass(cs.getConceptClassByName("ConvSet"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.setSet(true);
                c.getConceptSets().clear();
                c.addSetMember(cs.getConcept(8606)); // Current drugs used
                c.addSetMember(cs.getConcept(2834)); // pills dispensed
                cs.saveConcept(c);
            }
        }
        {
            Integer id = 8608;
            String uuid = "E1B2AF76-4B69-4BA4-9DDC-D801649BC212";
            String name = "Extremely positive";
            String synonym = "4++++";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Misc"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }
        {
            Integer id = 6447; // Urine protein
            Concept c = cs.getConcept(id);
            if (c != null) {
                log.warn("Updating answers for Proteinuria");
                Concept proteinuriaFourPlus = cs.getConcept(8608); // Proteinuria 4+ over 3000mg/24 hours
                Collection<ConceptAnswer> answers = c.getAnswers();
                if (!hasAnswer(answers, proteinuriaFourPlus)) {
                    c.addAnswer(new ConceptAnswer(proteinuriaFourPlus));
                    cs.saveConcept(c);
                }

            }
        }

        {
            Integer id = 2169; //Result of HIV test
            Concept c = cs.getConcept(id);
            if (c != null) {
                log.warn("Updating answers for HIV Test result");
                Collection<ConceptAnswer> answers = c.getAnswers();
                Concept reactive = cs.getConcept(1228); // Reactive
                Concept nonReactive = cs.getConcept(1229); // Non-Reactive
                if (!hasAnswer(answers, reactive)) {
                    c.addAnswer(new ConceptAnswer(reactive));
                    cs.saveConcept(c);
                }
                if (!hasAnswer(answers, nonReactive)) {
                    c.addAnswer(new ConceptAnswer(nonReactive));
                    cs.saveConcept(c);
                }
            }
        }

        {
            Integer id = 8609;
            String uuid = "6623242A-F374-46C1-83D7-D17551E48A79";
            String name = "High lipids";
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
            Integer id = 8610;
            String uuid = "F7A42154-66D8-41A9-97CD-C16DDE4C0F3B";
            String name = "High Triglycerides";
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
            Integer id = 8611;
            String uuid = "68CFA0A5-3BBC-4E7E-9CDE-9C2527385CF7";
            String name = "Lipid profile";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptClass(cs.getConceptClassByName("Question"));
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));

                c.addAnswer(new ConceptAnswer(cs.getConcept(1115))); // Normal
                c.addAnswer(new ConceptAnswer(cs.getConcept(8609))); // High lipids
                c.addAnswer(new ConceptAnswer(cs.getConcept(8610))); // High TGs

                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8473;
            Concept c = cs.getConcept(id);
            String name = "Oral steroid";
            String voidName = "Steroid (inhaled)";
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals(voidName)) {
                log.warn("Replacing name and description for Steroid (inhaled) to Oral steroid");
                ConceptName vcn = c.getFullySpecifiedName(Locale.ENGLISH);
                vcn.setVoided(true);
                vcn.setVoidReason("Swap name with oral");
                ConceptName cn = new ConceptName(name, Locale.ENGLISH);
                cn.setLocalePreferred(true);
                c.setFullySpecifiedName(cn);
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8472;
            Concept c = cs.getConcept(id);
            String name = "Inhaled steroid";
            String voidName = "Steroid (oral)";
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals(voidName)) {
                log.warn("Replacing name and description for Steroid (oral) to Inhaled steroid");
                ConceptName vcn = c.getFullySpecifiedName(Locale.ENGLISH);
                vcn.setVoided(true);
                vcn.setVoidReason("Swap name with inhaled");
                ConceptName cn = new ConceptName(name, Locale.ENGLISH);
                cn.setLocalePreferred(true);
                c.setFullySpecifiedName(cn);
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8612;
            String uuid = "72247AAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Bisoprolol";
            Concept c = cs.getConcept(id);
            String synonym1 = "Emcor";
            String synonym2 = "Emcor LS";
            String synonym3 = "Monocor";
            String synonym4 = "Zebeta";
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Drug"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym1, Locale.ENGLISH));
                c.addName(new ConceptName(synonym2, Locale.ENGLISH));
                c.addName(new ConceptName(synonym3, Locale.ENGLISH));
                c.addName(new ConceptName(synonym4, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8613;
            String uuid = "83936AAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Simvastatin";
            Concept c = cs.getConcept(id);
            String synonym = "Zocor";
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Drug"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }   
        {
            Integer id = 8614;
            String uuid = "82411AAAAAAAAAAAAAAAAAAAAAAAAAAA";
            String name = "Pravastatin";
            Concept c = cs.getConcept(id);
            String synonym1 = "Pravachol";
            String synonym2 = "Pravigard Pac";
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Drug"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setSet(false);
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym1, Locale.ENGLISH));
                c.addName(new ConceptName(synonym2, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }    
        {
            Integer id = 8451;
            String name = "Neuropathy and Peripheral Vascular Disease";
            Concept c = cs.getConcept(id);
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Neuropathy and Peripheral Vascular Disease")) {
                log.warn("Updating " + name);
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                cs.saveConcept(c);
            }
        }    
        {
            Integer id = 8456;
            String name = "Deformity of foot";
            Concept c = cs.getConcept(id);
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Deformity of foot")) {
                log.warn("Updating " + name);
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                cs.saveConcept(c);
            }
        }    
        {
            Integer id = 6566;
            String name = "Foot ulcer or infection";
            Concept c = cs.getConcept(id);
            if (c.getFullySpecifiedName(Locale.ENGLISH).getName().equals("Foot ulcer or infection")) {
                log.warn("Updating " + name);
                c.setDatatype(cs.getConceptDatatypeByName("Coded"));
                c.addAnswer(new ConceptAnswer(cs.getConcept(1065))); // Yes
                c.addAnswer(new ConceptAnswer(cs.getConcept(1066))); // No
                cs.saveConcept(c);
            }
        }                            
    }

    @Override
    public void stopped() {
    }
}