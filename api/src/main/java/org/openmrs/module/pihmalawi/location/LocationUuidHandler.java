package org.openmrs.module.pihmalawi.location;

/**
 *  Gets UUIDs for locations stored in memory
 */
public interface LocationUuidHandler {
    String getLocationUiidByLocationName(String  location);
}
