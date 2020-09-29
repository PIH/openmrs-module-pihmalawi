package org.openmrs.module.pihmalawi.location.impl;


import org.openmrs.module.pihmalawi.location.LocationUuidHandler;
import org.openmrs.module.pihmalawi.metadata.Locations;
import org.springframework.stereotype.Component;

/**
 *  Implements getting UUIDs for locations stored in memory
 */
@Component
public class LocationUuidHandlerImpl implements LocationUuidHandler {


    @Override
    public String getLocationUiidByLocationName(String location) {
        String formattedLocation = location.trim().toLowerCase();
        if(formattedLocation.equals(Locations.DAMBE_CLINIC.name().toLowerCase()))
        {
            return Locations.DAMBE_CLINIC.uuid();
        }
        if(formattedLocation.equals(Locations.LIGOWE_HC.name().toLowerCase()))
        {
            return Locations.LIGOWE_HC.uuid();
        }
        if(formattedLocation.equals(Locations.LUWANI_RHC.name().toLowerCase()))
        {
            return Locations.LUWANI_RHC.uuid();
        }
        if(formattedLocation.equals(Locations.MAGALETA_HC.name().toLowerCase()))
        {
            return Locations.MAGALETA_HC.uuid();
        }
        if(formattedLocation.equals(Locations.MATANDANI_RHC.name().toLowerCase()))
        {
            return Locations.MATANDANI_RHC.uuid();
        }
        if(formattedLocation.equals(Locations.NENO_DHO.name().toLowerCase()))
        {
            return Locations.NENO_DHO.uuid();
        }
        if(formattedLocation.equals(Locations.NENO_INWARD_PATIENTS.name().toLowerCase())){
            return Locations.NENO_INWARD_PATIENTS.uuid();
        }
        if(formattedLocation.equals(Locations.NENO_MISSION_HC.name().toLowerCase()))
        {
            return Locations.NENO_MISSION_HC.uuid();
        }
        return Locations.UNKNOWN.uuid();
    }
}
