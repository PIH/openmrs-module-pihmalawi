package org.openmrs.module.pihmalawi.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.module.pihmalawi.patient.ExtendedPatientService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class YendaNafePatientRegistrationValidatorTest extends BaseModuleContextSensitiveTest {
    protected static final String FIND_PATIENTS_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";
    protected static final String FIND_USERS_XML = "org/openmrs/api/include/UserServiceTest.xml";
    protected static final String FIND_LOCATION_XML = "org/openmrs/api/include/LocationServiceTest-initialData.xml";
    protected static final String CREATE_PATIENT_IDENTIFIER = "org/openmrs/api/include/PatientServiceTest-createPatientIdentifierType.xml";

    public static final String LOCATION_UUID = "f08ba64b-ea57-4a41-b33c-9dfc59b0c60a";

    YendaNafePatientRegistrationModel yendaNafePatientRegistrationModel = new YendaNafePatientRegistrationModel();

    @Autowired
    ExtendedPatientService extendedPatientService;
    @Autowired
    LocationService locationService;
    @Autowired
    YendaNafePatientRegistrationValidator yendaNafePatientRegistrationValidator;

    @Before
    public void runBeforeAllTests() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet(FIND_PATIENTS_XML);
        executeDataSet(FIND_USERS_XML);
        executeDataSet(FIND_LOCATION_XML);
        executeDataSet(CREATE_PATIENT_IDENTIFIER);

        yendaNafePatientRegistrationModel._id  = "1234-cad-12";
        yendaNafePatientRegistrationModel.type = "contact";
        yendaNafePatientRegistrationModel.name = "Another Patient";
        yendaNafePatientRegistrationModel.sex = "female";
        yendaNafePatientRegistrationModel.date_of_birth = "1974-09-22";
        yendaNafePatientRegistrationModel.knows_dob = "";
        yendaNafePatientRegistrationModel.art_start_date ="2010-06-27";
        yendaNafePatientRegistrationModel.tb_treatment_start_date = "2019-06-25";
        yendaNafePatientRegistrationModel.primary_phone_number = "0110000000";
        yendaNafePatientRegistrationModel.secondary_phone_number = "";
        yendaNafePatientRegistrationModel.conditions = "art";
        yendaNafePatientRegistrationModel.conditions_other ="";
        yendaNafePatientRegistrationModel.malnutrition_program ="";
        yendaNafePatientRegistrationModel.ncds="";
        yendaNafePatientRegistrationModel.ncds_other = "";
        yendaNafePatientRegistrationModel.mental_health_type ="";
        yendaNafePatientRegistrationModel.mental_health_type_other ="";
        yendaNafePatientRegistrationModel.eid_emr_id_source="";
        yendaNafePatientRegistrationModel.eid_emr_id_source_other ="";
        yendaNafePatientRegistrationModel.art_emr_id_source = "";
        yendaNafePatientRegistrationModel.art_emr_id_source_other="";
        yendaNafePatientRegistrationModel.ncd_emr_id_source ="";
        yendaNafePatientRegistrationModel.ncd_emr_id_source_other = "";
        yendaNafePatientRegistrationModel.eid_id ="";
        yendaNafePatientRegistrationModel.art_id = "";
        yendaNafePatientRegistrationModel.ncd_id = "";
        yendaNafePatientRegistrationModel.notes =  "";
        yendaNafePatientRegistrationModel.reported_date = "1561479580888";
        yendaNafePatientRegistrationModel.patient_id ="00012311";
        yendaNafePatientRegistrationModel.contact_type = "person";
        yendaNafePatientRegistrationModel.chw ="Another Doc";
        yendaNafePatientRegistrationModel.traditional_authority = "Zomba";
        yendaNafePatientRegistrationModel.village = "Some Village";
        yendaNafePatientRegistrationModel.health_surveillance_assistant = "Some Assistant";
        yendaNafePatientRegistrationModel.site = "Zomba";
        yendaNafePatientRegistrationModel.location_uuid = LOCATION_UUID;
    }
    @Test
    public void validatePatientDetails_patientExistInDB_returnsValidationErrorForPatientExist()
    {
        yendaNafePatientRegistrationModel._id = "1234";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "_id 1234 already exist in the system. _id has to be unique";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_nameNotGiven_returnsValidationErrorForNameOfPatient()
    {
        yendaNafePatientRegistrationModel.name = "";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "Name of patient not given.";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_onlyFirstNameGiven_returnsValidationErrorForIncompleteName()
    {
        yendaNafePatientRegistrationModel.name="Doe";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "Name is incomplete. Please provide first name and last name.";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_invalidGenderGiven_returnsValidationErrorForGender()
    {
        yendaNafePatientRegistrationModel.sex = "Invalid Gender";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "Invalid sex given "+ yendaNafePatientRegistrationModel.sex+". Sex should be male, female or unknown.";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_dateOfBirthNotGiven_returnsValidationErrorForDateOfBirth()
    {
        yendaNafePatientRegistrationModel.date_of_birth = "";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "Date of birth is not given.";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_invalidDateOfBirthFormat_returnsValidationErrorForDateOfBirth()
    {
        String invalidDateOfBirth = "Invalid Date";
        yendaNafePatientRegistrationModel.date_of_birth = invalidDateOfBirth;
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "Invalid date format. Given date is "+ invalidDateOfBirth+".";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_invalidLocationUuid_returnsValidationErrorForLocationUuid()
    {
        String locationUUID = "Invalid Location UUID";
        yendaNafePatientRegistrationModel.location_uuid = locationUUID;
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "Given location uuid "+ locationUUID +" does not exist in the EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_ncdsFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.ncds = "Patient NCD Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_ncds_otherFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.ncds_other = "Patient NCD Other Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_mental_health_typeFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.mental_health_type = "Patient Mental Health Type Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_mental_health_type_otherFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.mental_health_type_other = "Patient Mental Health Type Other Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_eid_emr_id_sourceFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.eid_emr_id_source = "Patient EID EMR ID Source Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_eid_emr_id_source_otherFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.eid_emr_id_source_other = "Patient EID EMR ID Source Other Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_art_emr_id_sourceFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.art_emr_id_source = "Patient ART EMR ID Source Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_art_emr_id_source_otherFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.art_emr_id_source_other = "Patient ART EMR ID Source Other Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_ncd_emr_id_sourceFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.ncd_emr_id_source = "Patient NCD EMR ID Source Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_ncd_emr_id_source_otherFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.ncd_emr_id_source_other = "Patient NCD EMR ID Source Other Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_eid_idFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.eid_id = "Patient EID ID Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_art_idFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.eid_id = "Patient ART ID Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_ncd_idFieldNotEmpty_returnsValidationErrorForUserRegisteredInEMR()
    {
        yendaNafePatientRegistrationModel.ncd_id = "Patient NCD ID Number";
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "User is registered in EMR";
        Assert.assertEquals(expectedError,error);
    }
    @Test
    public void validatePatientDetails_validModel_returnsNoErrorMessage()
    {
        String error = yendaNafePatientRegistrationValidator.validateRegistrationModel(yendaNafePatientRegistrationModel,extendedPatientService,locationService);
        String expectedError = "";
        Assert.assertEquals(expectedError,error);
    }
}
