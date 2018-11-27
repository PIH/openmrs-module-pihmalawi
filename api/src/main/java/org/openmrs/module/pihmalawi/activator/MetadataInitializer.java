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
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.metadata.EncounterTypes;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VisitTypeBundle;
import org.openmrs.util.OpenmrsConstants;

public class MetadataInitializer implements Initializer {

    protected static final Log log = LogFactory.getLog(MetadataInitializer.class);

    public GlobalProperty saveGlobalProperty(String name, String value) {
        GlobalProperty gp = null;

        if (StringUtils.isNotBlank(name)) {
            gp = Context.getAdministrationService().getGlobalPropertyObject(name);
            if (gp == null) {
                gp = new GlobalProperty(name, "");
            }
            gp.setPropertyValue(value);
            Context.getAdministrationService().saveGlobalProperty(gp);
        }

        return gp;
    }
    /**
     * @see Initializer#started()
     */
    @Override
    public synchronized void started() {

        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);
        saveGlobalProperty(PihMalawiConstants.HEALTH_FACILITY_GP_NAME, PihMalawiConstants.HEALTH_FACILITY_GP_VALUE);
        saveGlobalProperty(PihMalawiConstants.DASHBOARD_IDENTIFIERS_GP_NAME, PihMalawiConstants.DASHBOARD_IDENTIFIERS_GP_VALUE);
        saveGlobalProperty(PihMalawiConstants.PATIENT_IDENTIFIER_IMPORTANT_TYPES_GP_NAME, PihMalawiConstants.PATIENT_IDENTIFIER_IMPORTANT_TYPES_GP_VALUE);

        // enable visits, and automatically create a new visit when checking in if a visit is not already open
        saveGlobalProperty(EmrApiConstants.GP_VISIT_ASSIGNMENT_HANDLER_ENCOUNTER_TYPE_TO_VISIT_TYPE_MAP,
                EncounterTypes.CHECK_IN.uuid() + ":" + VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT);
        saveGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ENABLE_VISITS, "true");
        saveGlobalProperty(OpenmrsConstants.GP_VISIT_TYPES_TO_AUTO_CLOSE, VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT);
        saveGlobalProperty(OpenmrsConstants.GP_ENCOUNTER_TYPE_TO_VISIT_TYPE_MAPPING,
                EncounterTypes.CHECK_IN.uuid() + ":" + VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT);

        // TODO: Clean this up.  One option:
        // Create some scripts that:
        //  select all concepts associated with obs, programs, etc. as well as the answers and sets associated with them
        //  organize these by datatype and class
        // Do the same for other metadata (encounter types, etc)
        // Export these out as a series of CSVs
        //
        // Create generated source / class files for these via maven plugin
        // Associate with versions and



        deployService.installBundles(Context.getRegisteredComponents(MetadataBundle.class));
    }


    @Override
    public void stopped() {
    }
}
