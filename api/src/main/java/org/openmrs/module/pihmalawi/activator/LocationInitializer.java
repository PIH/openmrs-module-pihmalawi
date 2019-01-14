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

package org.openmrs.module.pihmalawi.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes location-related metadata
 */
public class LocationInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(LocationInitializer.class);

    private static List<String> UPPER_NENO_LOGIN_AND_VISIT_LOCATIONS = Arrays.asList(
            "Binje Outreach Clinic",
            "Dambe Clinic",
            "Golden Outreach Clinic",
            "Ligowe HC",
            "Luwani RHC",
            "Luwani Prison",
            "Magaleta HC",
            "Matandani Rural Health Center",
            "Neno District Hospital",
            "Neno Mission HC",
            "Nsambe HC",
            "Ntaja Outreach Clinic"
    );

	private static List<String> LOWER_NENO_LOGIN_AND_VISIT_LOCATIONS = Arrays.asList(
	        "Chifunga HC",
            "Felemu Outreach Clinic",
            "Lisungwi Community Hospital",
            "Kasamba Outreach Clinic",
            "Matope HC",
            "Midzemba HC",
            "Nkhula Falls RHC",
            "Zalewa HC"
    );

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
        if (CommonMetadata.UPPER_NENO.equalsIgnoreCase(getSystemLocation())) {
            setLocationTags(UPPER_NENO_LOGIN_AND_VISIT_LOCATIONS);
        }
        else if (CommonMetadata.LOWER_NENO.equalsIgnoreCase(getSystemLocation())) {
            setLocationTags(LOWER_NENO_LOGIN_AND_VISIT_LOCATIONS);
        }
        else {
            setLocationTags(new ArrayList<String>());
        }
    }

    public void setLocationTags(List<String> locationNames) {

        LocationTag loginTag = getLocationService().getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_LOGIN);
        LocationTag visitTag = getLocationService().getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS);

        for (Location l : getLocationService().getAllLocations(true)) {
            boolean shouldHaveTag = locationNames.contains(l.getName());
            boolean hasLoginLocation = l.hasTag(loginTag.getName());
            boolean hasVisitLocation = l.hasTag(visitTag.getName());
            if (shouldHaveTag) {
                if (!hasLoginLocation) {
                    l.addTag(loginTag);
                }
                if (!hasVisitLocation) {
                    l.addTag(visitTag);
                }
            }
            else {
                if (hasLoginLocation) {
                    l.removeTag(loginTag);
                }
                if (hasVisitLocation) {
                    l.removeTag(visitTag);
                }
            }
            getLocationService().saveLocation(l);
        }
    }

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}

    /**
     * @return true if the system is configured for Lower Neno
     */
	private String getSystemLocation() {
        AdministrationService as = Context.getAdministrationService();
        return as.getGlobalProperty(CommonMetadata.CURRENT_SYSTEM_LOCATION_TAG_GLOBAL_PROPERTY);
    }

    private LocationService getLocationService() {
        return Context.getLocationService();
    }
}
