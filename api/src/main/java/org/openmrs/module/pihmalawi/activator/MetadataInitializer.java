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
import org.openmrs.GlobalProperty;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.pihmalawi.PihMalawiConstants;

import java.util.Collection;
import java.util.Locale;

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
        GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(PihMalawiConstants.HEALTH_FACILITY_GP_NAME);
        if (gp == null) {
            gp = new GlobalProperty(PihMalawiConstants.HEALTH_FACILITY_GP_NAME, "");
        }
        gp.setPropertyValue(PihMalawiConstants.HEALTH_FACILITY_GP_VALUE);
        Context.getAdministrationService().saveGlobalProperty(gp);

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
            Integer id = 8415;
            String uuid = "14ce86d7-025e-4dcf-9437-67d26185c6ab";
            String name = "Palliative Care Program";
            String synonym = "PC Program";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Program"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        {
            Integer id = 8416;
            String uuid = "e7a7c2ca-7433-4851-8687-67e8541ca40b";
            String name = "Palliative Care Treatment Status";
            String synonym = "PC Treatment Status";
            Concept c = cs.getConcept(id);
            if (c == null) {
                log.warn("Creating " + name);
                c = new Concept();
                c.setConceptId(id);
                c.setUuid(uuid);
                c.setConceptClass(cs.getConceptClassByName("Workflow"));
                c.setDatatype(cs.getConceptDatatypeByName("N/A"));
                c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
                c.addName(new ConceptName(synonym, Locale.ENGLISH));
                cs.saveConcept(c);
            }
        }

        deployService.installBundles(Context.getRegisteredComponents(MetadataBundle.class));
    }





    @Override
    public void stopped() {
    }
}