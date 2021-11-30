package org.openmrs.module.pihmalawi.db;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.pihmalawi.patient.ExtendedPatientService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


public class ExtendedPatientServiceTest extends BaseModuleContextSensitiveTest {
    protected static final String FIND_PATIENTS_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";

    @Autowired
    ExtendedPatientService extendedPatientService;

    @Before
    public void runBeforeAllTests() throws Exception {
        executeDataSet(FIND_PATIENTS_XML);
    }
    @Test
    public void getPatientIdentifierInDb_IdentifierExistInDb_ReturnIdentifierInDb()
    {
        PatientIdentifier getPatientIdentifier = extendedPatientService.getPatientIdentifierByIdentifier("1234");
        String expectedIdentifierUiid = "e997ac86-4d8a-40f3-bedb-da84d35917b8";
        Assert.assertEquals(expectedIdentifierUiid,getPatientIdentifier.getUuid());
    }
    @Test
    public void getPatientIdentifierInDb_IdentifierDoesExistInDb_ReturnEmptyObject()
    {
        PatientIdentifier getPatientIdentifier = extendedPatientService.getPatientIdentifierByIdentifier("12342345323");
        Assert.assertNull(getPatientIdentifier);
    }
}
