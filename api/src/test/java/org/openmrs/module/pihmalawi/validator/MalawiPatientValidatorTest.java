package org.openmrs.module.pihmalawi.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.PihMalawiPatientIdentifierTypes;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MalawiPatientValidatorTest extends BaseModuleContextSensitiveTest {

    private final String ANOTHER_IDENTIFIER_TYPE_UUID = "30a69e5f-0db8-49c3-8a33-3427ed7eb61f";

    @Before
    public void before() {
        // for some reason setting this up via the bundle wasn't working
        PatientIdentifierType tempIdType = new PatientIdentifierType();
        tempIdType.setName(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.name());
        tempIdType.setDescription(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.description());
        tempIdType.setFormat(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.format());
        tempIdType.setFormatDescription(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.formatDescription());
        tempIdType.setRequired(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.required());
        tempIdType.setLocationBehavior(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.locationBehavior());
        tempIdType.setUuid(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.uuid());
        Context.getPatientService().savePatientIdentifierType(tempIdType);

       /* PatientIdentifierType anotherIdentiferType = new PatientIdentifierType();
        anotherIdentiferType.setName("Another identifier type");
        anotherIdentiferType.setDescription("Another identifier type");;
        anotherIdentiferType.setRequired(false);
        anotherIdentiferType.setLocationBehavior(PatientIdentifierType.LocationBehavior.NOT_USED);
        anotherIdentiferType.setUuid(ANOTHER_IDENTIFIER_TYPE_UUID);
        Context.getPatientService().savePatientIdentifierType(anotherIdentiferType);*/
    }

    @Test
    public void shouldAssignIC3IdentifierIfNeededWhenSavingPatient() {

        PatientIdentifierType tempIdType = Context.getPatientService().getPatientIdentifierTypeByUuid(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.uuid());
        Location location = Context.getLocationService().getLocation(1);

        Patient patient = new Patient();

        PersonName name = new PersonName();
        name.setFamilyName("Family");
        name.setGivenName("Given");

        patient.setGender("F");
        patient.addName(name);

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifierType(tempIdType);
        patientIdentifier.setLocation(location);
        patient.addIdentifier(patientIdentifier);

        Context.getPatientService().savePatient(patient);

        Assert.assertEquals(1, patient.getActiveIdentifiers().size());
        Assert.assertEquals(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.uuid(),
                patient.getActiveIdentifiers().get(0).getIdentifierType().getUuid());


    }

    /*@Test
    public void shouldNotAssignIdentifierIfPatientAlreadyHasOne() {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByUuid(ANOTHER_IDENTIFIER_TYPE_UUID); // from standard test dataset

        Patient patient = new Patient();

        PersonName name = new PersonName();
        name.setFamilyName("Family");
        name.setGivenName("Given");

        patient.setGender("F");
        patient.addName(name);

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifierType(pit);
        patientIdentifier.setIdentifier("123abc");

        patient.addIdentifier(patientIdentifier);

        Context.getPatientService().savePatient(patient);

        Assert.assertEquals(1, patient.getActiveIdentifiers().size());
        Assert.assertEquals(ANOTHER_IDENTIFIER_TYPE_UUID,
                patient.getActiveIdentifiers().get(0).getIdentifierType().getUuid());


    }*/
}
