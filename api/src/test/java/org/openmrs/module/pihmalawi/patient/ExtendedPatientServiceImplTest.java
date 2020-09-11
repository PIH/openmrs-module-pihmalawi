package org.openmrs.module.pihmalawi.patient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ExtendedPatientServiceImplTest extends BaseModuleContextSensitiveTest {
    protected static final String CREATE_PATIENT_XML = "org/openmrs/api/include/PatientServiceTest-createPatient.xml";
    protected static final String FIND_USERS_XML = "org/openmrs/api/include/UserServiceTest.xml";
    protected static final String FIND_LOCATION_XML = "org/openmrs/api/include/LocationServiceTest-initialData.xml";
    protected static final String CREATE_PATIENT_IDENTIFIER = "org/openmrs/api/include/PatientServiceTest-createPatientIdentifierType.xml";

    public static final String YENDANAFEUIID ="658db69a-e53c-11de-8404-001e378eb67e";
    public static final String PATIENT_IDENTIFIER_UIID = "2fgh0aa8-1d73-43b7-81b5-01f0c0dfa53c";
    public static final String LOCATION_UUID = "f08ba64b-ea57-4a41-b33c-9dfc59b0c60a";

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PersonService personService;

    @Autowired
    private PatientService patientService;

    @Autowired
    YendaNafePatientRegistrationService yendaNafePatientRegistrationService;

    @Before
    public void runBeforeAllTests() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet(CREATE_PATIENT_XML);
        executeDataSet(FIND_USERS_XML);
        executeDataSet(FIND_LOCATION_XML);
        executeDataSet(CREATE_PATIENT_IDENTIFIER);
    }
    @Test
    public void createPatientInDb_PatientDetailsProvided_ExpectsToCreaePatientInDb()
    {
        YendaNafePatientRegistrationModel yendaNafePatientRegistrationModel = new YendaNafePatientRegistrationModel();
        yendaNafePatientRegistrationModel._id  = "1234";
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

        Patient createdPatient = yendaNafePatientRegistrationService.createPatient(yendaNafePatientRegistrationModel,YENDANAFEUIID,personService,patientService,userService,locationService, PATIENT_IDENTIFIER_UIID);
        Assert.assertNotNull(createdPatient);
    }
}
