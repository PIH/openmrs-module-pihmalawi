package org.openmrs.module.pihmalawi.location.impl;

import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;
import org.openmrs.module.pihmalawi.location.LocationUuidHandler;
import org.springframework.stereotype.Component;

/**
 *  Implements getting UUIDs for locations stored in memory
 */
@Component
public class LocationUuidHandlerImpl implements LocationUuidHandler {

    @Override
    public String getLocationUiidByLocationName(String location) {
        String formattedLocation = location.trim().toLowerCase();
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_DAMBE_CLINIC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_DAMBE_CLINIC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_LIGOWE_HC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_LIGOWE_HC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_LUWANI_RHC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_LUWANI_RHC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_MAGALETA_HC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_MAGALETA_HC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_MATANDANI_RHC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_MATANDANI_RHC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_NENO_DHO_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_NENO_DHO_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_NENO_INWARD_PATIENTS_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_NENO_INWARD_PATIENTS_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_NENO_MISSION_HC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_NENO_MISSION_HC_UUID;
        }
        return PihMalawiConfigConstants.LOCATION_UNKNOWN_UUID;
    }
}
