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
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;

import java.util.Collections;

public class MetadataInitializer implements Initializer {

    protected static final Log log = LogFactory.getLog(MetadataInitializer.class);

    /**
     * @see Initializer#started()
     */
    @Override
    public synchronized void started() {
        InitializerService initializerService = Context.getService(InitializerService.class);
        for (Loader loader : initializerService.getLoaders()) {
            log.info("Loading from Initializer: " + loader.getDomainName());
            try {
                loader.loadUnsafe(Collections.<String>emptyList(), true);
            }
            catch (Exception e) {
                throw new IllegalStateException("An error occurred while loading Initializer domain: " + loader.getDomainName(), e);
            }
        }
    }

    @Override
    public void stopped() {
    }
}
