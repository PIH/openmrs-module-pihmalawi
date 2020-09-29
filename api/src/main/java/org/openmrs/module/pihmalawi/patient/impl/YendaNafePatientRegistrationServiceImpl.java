package org.openmrs.module.pihmalawi.patient.impl;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.module.pihmalawi.patient.YendaNafePatientRegistrationService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 *  Class implements patient creation operations for patients captured from medic mobile
 */
@Component
public class YendaNafePatientRegistrationServiceImpl implements YendaNafePatientRegistrationService {
    @Authorized( { PrivilegeConstants.ADD_PATIENTS })
    @Transactional
    @Override
    public Patient createPatient(YendaNafePatientRegistrationModel yendaNafePatientRegistrationModel, String creatorUUID, PersonService personService, PatientService patientService, UserService userService, LocationService locationService,String patientIdentifierUuid) {
        User user = userService.getUserByUuid(creatorUUID);

        String fullName = yendaNafePatientRegistrationModel.name;
        String[] nameSplitted = fullName.split("\\s+",2);
        String firstName = nameSplitted[0];
        String lastName = nameSplitted[1];
        String gender;
        if(yendaNafePatientRegistrationModel.sex.toLowerCase().trim().equals("male"))
        {
            gender = "M";
        }
        else if(yendaNafePatientRegistrationModel.sex.toLowerCase().trim().equals("female"))
        {
            gender = "F";
        }
        else
        {
            gender = "U";
        }

        PersonName personName = new PersonName();
        personName.setGivenName(firstName);
        personName.setFamilyName(lastName);

        PersonAddress personAddress = new PersonAddress();
        personAddress.setCountyDistrict("Malawi");
        personAddress.setCountyDistrict("Neno");
        personAddress.setCityVillage(yendaNafePatientRegistrationModel.village);

        Person newPerson = new Person();
        newPerson.setGender(gender);
        newPerson.setCreator(user);
        newPerson.setBirthdate(DateUtil.parseYmd(yendaNafePatientRegistrationModel.date_of_birth));
        newPerson.addName(personName);
        newPerson.addAddress(personAddress);

        Person savedPerson = personService.savePerson(newPerson);
        Location patient_location = locationService.getLocationByUuid(yendaNafePatientRegistrationModel.location_uuid);
        PatientIdentifierType patientIdentifierType =  patientService.getPatientIdentifierTypeByUuid(patientIdentifierUuid);
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier(yendaNafePatientRegistrationModel._id);
        patientIdentifier.setIdentifierType(patientIdentifierType);
        patientIdentifier.setLocation(patient_location);
        Patient newPatient = new Patient(savedPerson);
        newPatient.addIdentifier(patientIdentifier);
       // newPatient.setBirthdate(DateUtil.parseYmd(yendaNafePatientRegistrationModel.date_of_birth));
        return patientService.savePatient(newPatient);
    }
}
