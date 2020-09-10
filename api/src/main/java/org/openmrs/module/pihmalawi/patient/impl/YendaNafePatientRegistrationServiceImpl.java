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
import org.openmrs.module.pihmalawi.metadata.PihMalawiPatientIdentifierTypes;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.module.pihmalawi.patient.YendaNafePatientRegistrationService;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.SortedSet;
import java.util.TreeSet;
/**
 *  Class implements patient creation operations for patients captured from medic mobile
 */
@Component
public class YendaNafePatientRegistrationServiceImpl implements YendaNafePatientRegistrationService {
    @Authorized( { PrivilegeConstants.ADD_PATIENTS })
    @Transactional
    @Override
    public Patient createPatient(YendaNafePatientRegistrationModel yendaNafePatientRegistrationModel, String creatorUUID, PersonService personService, PatientService patientService, UserService userService, LocationService locationService) {
        User user = userService.getUserByUuid(creatorUUID);

        String fullName = yendaNafePatientRegistrationModel.name;
        String[] nameSplitted = fullName.split("\\s+",2);
        String firstName = nameSplitted[0];
        String lastName = nameSplitted[1];

        SortedSet<PersonName> personNames = new TreeSet<PersonName>();

        PersonName personName = new PersonName();
        personName.setGivenName(firstName);
        personName.setFamilyName(lastName);
        personNames.add(personName);

        SortedSet<PersonAddress> personAddresses = new TreeSet<PersonAddress>();
        PersonAddress personAddress = new PersonAddress();
        personAddress.setCountyDistrict("Malawi");
        personAddress.setCountyDistrict("Neno");
        personAddress.setCityVillage(yendaNafePatientRegistrationModel.village);
        personAddresses.add(personAddress);

        Person newPerson = new Person();
        newPerson.setGender(yendaNafePatientRegistrationModel.sex);
        newPerson.setNames(personNames);
        newPerson.setCreator(user);
       // newPerson.setBirthdate(DateUtil.parseYmd(yendaNafePatientRegistrationModel.date_of_birth));
        newPerson.setAddresses(personAddresses);
        newPerson.addName(personName);

        Person savedPerson = personService.savePerson(newPerson);
        Location patient_location = locationService.getLocationByUuid(yendaNafePatientRegistrationModel.location_uuid);
        PatientIdentifierType patientIdentifierType =  patientService.getPatientIdentifierTypeByUuid(PihMalawiPatientIdentifierTypes.YENDANAFE_IDENTIFIER.uuid());
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        SortedSet<PatientIdentifier> patientIdentifiers = new TreeSet<PatientIdentifier>();
        patientIdentifier.setIdentifier(yendaNafePatientRegistrationModel._id);
        patientIdentifier.setIdentifierType(patientIdentifierType);
        patientIdentifier.setLocation(patient_location);
        patientIdentifiers.add(patientIdentifier);
        Patient newPatient = new Patient(savedPerson);
        newPatient.setIdentifiers(patientIdentifiers);
       // newPatient.setBirthdate(DateUtil.parseYmd(yendaNafePatientRegistrationModel.date_of_birth));
        return patientService.savePatient(newPatient);
    }
}
