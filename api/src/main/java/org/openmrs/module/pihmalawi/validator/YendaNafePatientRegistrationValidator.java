package org.openmrs.module.pihmalawi.validator;

import org.openmrs.api.LocationService;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.module.pihmalawi.patient.ExtendedPatientService;
/**
 *  Validates model for adding patient from medic mobile data model for patient registration
 */
public interface YendaNafePatientRegistrationValidator {
    public String validateRegistrationModel(YendaNafePatientRegistrationModel yendaNafePatientRequestBody, ExtendedPatientService extendedPatientService, LocationService locationService);
}
