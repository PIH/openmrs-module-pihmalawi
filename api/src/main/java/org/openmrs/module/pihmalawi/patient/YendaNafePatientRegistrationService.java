package org.openmrs.module.pihmalawi.patient;

import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

public interface YendaNafePatientRegistrationService  {
    @Authorized( { PrivilegeConstants.ADD_PATIENTS })
    @Transactional
    public Patient createPatient(YendaNafePatientRegistrationModel yendaNafePatientRegistrationModel, String creatorUUID, PersonService personService, PatientService patientService, UserService userService, LocationService locationService);

    }
