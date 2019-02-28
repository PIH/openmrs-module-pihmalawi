package org.openmrs.module.pihmalawi.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.alert.AlertDefinition;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.reporting.common.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Is.is;

public class IC3ScreeningDataBasicTest extends BaseMalawiTest {

    private final static Log log = LogFactory.getLog(IC3ScreeningDataBasicTest.class);

    @Autowired
    IC3ScreeningData screeningData;

    /**
     * @verifies return the json data and BP eligibility alert
     * @see LivePatientDataSet#getDataForPatient(Integer, java.util.Date, org.openmrs.Location, boolean)
     */
    @Test
    public void getDataForPatient_shouldReturnEligibleForBPAlert() throws Exception {

        Patient patient = createPatient().age(32).save();
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        List<User> allUsers = Context.getUserService().getAllUsers();

        Program hivProgram = hivMetadata.getHivProgram();
        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(2019, 2, 27),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        if (patientData != null && !patientData.isEmpty()) {
            log.warn("alert= " + patientData.get("alerts"));
        }
        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eligible-for-bp-screening"))));
        Assert.assertTrue(allPatients != null && allPatients.size() > 0);

    }

}
