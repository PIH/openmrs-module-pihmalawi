package org.openmrs.module.pihmalawi.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.alert.AlertDefinition;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Is.is;

@SkipBaseSetup
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

    @Test
    public void shouldReturnRoutineCreatinineAlert() throws Exception {

        Patient patient = createPatient().age(32).save();
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        List<User> allUsers = Context.getUserService().getAllUsers();

        Date date1 = DateUtil.getDateTime(2014, 2, 22);
        Encounter enc1 = createEncounter(patient, ccMetadata.getHtnDiabetesInitialEncounterType(), date1).save();
        Obs type1Diabetes = createObs(enc1, ccMetadata.getChronicCareDiagnosisConcept(), ccMetadata.getType1DiabetesConcept()).save();

        Date date2 = DateUtil.getDateTime(2017, 9, 22);
        Encounter enc2 = createEncounter(patient, ccMetadata.getHtnDiabetesTestsEncounterType(), date2).save();
        Obs creatinineResult = createObs(enc2, ccMetadata.getCreatinineConcept(), 1.4).save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(2019, 2, 27),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("routine-creatinine"))));
        Assert.assertTrue(allPatients != null && allPatients.size() > 0);

    }
}
