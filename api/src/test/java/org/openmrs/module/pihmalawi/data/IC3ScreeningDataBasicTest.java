package org.openmrs.module.pihmalawi.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.builder.ObsBuilder;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.alert.AlertDefinition;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

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

    }

    @Test
    public void shouldReturnRoutineCreatinineAlert() throws Exception {

        Patient patient = createPatient().age(32).save();

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

    }

    @Test
    public void shouldReturnRoutineViralLoadAlert() throws Exception {

        Patient patient = createPatient().age(32).save();

        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(2015, 10, 22));
        // Patient on ART
        PatientState patientState = new PatientState();
        patientState.setStartDate(DateUtil.getDateTime(2015, 10, 22));
        ProgramWorkflowState onArvsState = hivMetadata.getOnArvsState();
        patientState.setState(onArvsState);
        patientProgram.getStates().add(patientState);
        PatientProgram savePatientProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // Patient starts first line regiment
        Date d1 = DateUtil.getDateTime(2017, 2, 13);
        Concept reg1 = hivMetadata.getArvRegimen2aConcept();
        Encounter reg1Encounter = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), d1).save();
        Obs reg1Obs = createObs(reg1Encounter, hivMetadata.getArvDrugsChange1Concept(), reg1).save();
        Obs regDate1 = createObs(reg1Encounter, hivMetadata.getDateOfStartingFirstLineArvsConcept(), d1).save();

        // Viral Load Test
        Date lastViralLoadDate = DateUtil.getDateTime(2018, 1, 22);
        Encounter enc1 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), lastViralLoadDate).save();
        ViralLoad expectedVl = new ViralLoad();
        expectedVl.setEncounterId(enc1.getId());
        expectedVl.setSpecimenDate(enc1.getEncounterDatetime());
        // bled
        Obs bled = createObs(enc1, hivMetadata.getHivViralLoadSpecimenCollectedConcept(), hivMetadata.getTrueConcept()).save();
        Obs numericResult = createObs(enc1, hivMetadata.getHivViralLoadConcept(), 1500L).save();
        expectedVl.setResultLdl(null);
        expectedVl.setResultNumeric(numericResult.getValueNumeric());

        // Patient changes ART regiment
        Date d3 = DateUtil.getDateTime(2018, 3, 7);
        Concept reg2 = hivMetadata.getArvRegimen4aConcept();
        Encounter reg2Encounter = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), d3).save();
        Obs regChange2 = createObs(reg2Encounter, hivMetadata.getArvDrugsChange2Concept(), reg2).save();
        Obs regDate2 = createObs(reg2Encounter, hivMetadata.getDateOfStartingAlternativeFirstLineArvsConcept(), d3).save();


        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(2019, 2, 27),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("due-for-routine-viral-load-1"))));


    }

    @Test
    public void shouldReturnNotNullLastAdherenceCounselingSessionDate() throws Exception {

        Patient patient = createPatient().age(22).save();

        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(2015, 10, 22));
        // Patient on ART
        PatientState patientState = new PatientState();
        patientState.setStartDate(DateUtil.getDateTime(2015, 10, 22));
        ProgramWorkflowState onArvsState = hivMetadata.getOnArvsState();
        patientState.setState(onArvsState);
        patientProgram.getStates().add(patientState);
        PatientProgram savePatientProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // Patient has first Adherence Counseling Session
        Date d1 = DateUtil.getDateTime(2018, 9, 13);
        Concept adherenceCounselingConcept = hivMetadata.getAdherenceCounselingSessionNumberConcept();
        Concept firstSessionConcept = hivMetadata.getConcept(hivMetadata.FIRST_CONCEPT);
        Encounter adherenceCounselingEncounter = createEncounter(patient, hivMetadata.getEncounterType(hivMetadata.ADHERENCE_COUNSELING_ENCOUNTER_TYPE), d1).save();
        Obs adherenceCounselingObs = createObs(adherenceCounselingEncounter, adherenceCounselingConcept, firstSessionConcept).save();


        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(2019, 4, 1),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat((String) patientData.get("last_adherence_counselling_session_number"), CoreMatchers.containsString(firstSessionConcept.getUuid().toString()));
        assertNotNull(patientData.get("last_adherence_counselling_session_date"));
        assertEquals(patientData.get("last_adherence_counselling_session_date"), d1);

    }

    @Test
    public void shouldReturnDueForAdherenceCounselingAlert() throws Exception {

        Patient patient = createPatient().age(22).save();

        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(2015, 10, 22));
        // Patient on ART
        PatientState patientState = new PatientState();
        patientState.setStartDate(DateUtil.getDateTime(2015, 10, 22));
        ProgramWorkflowState onArvsState = hivMetadata.getOnArvsState();
        patientState.setState(onArvsState);
        patientProgram.getStates().add(patientState);
        PatientProgram savePatientProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // Viral Load Test
        Date lastViralLoadDate = DateUtil.getDateTime(2018, 9, 22);
        Encounter enc1 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), lastViralLoadDate).save();
        ObsBuilder groupObsBuilder = createObs(enc1, hivMetadata.getHivViralLoadTestSetConcept(), null);
        Obs routineTest = createObs(enc1, hivMetadata.getReasonForTestingConcept(), hivMetadata.getRoutineConcept()).save();
        // bled
        Obs bled = createObs(enc1, hivMetadata.getHivViralLoadSpecimenCollectedConcept(), hivMetadata.getTrueConcept()).save();
        Obs numericResult = createObs(enc1, hivMetadata.getHivViralLoadConcept(), 1500L).save();

        groupObsBuilder.member(routineTest);
        groupObsBuilder.member(bled);
        groupObsBuilder.member(numericResult);
        groupObsBuilder.save();

        // Patient has first Adherence Counseling Session
        Date d1 = DateUtil.getDateTime(2018, 10, 21);
        Concept adherenceCounselingConcept = hivMetadata.getAdherenceCounselingSessionNumberConcept();
        Concept firstSessionConcept = hivMetadata.getConcept(hivMetadata.FIRST_CONCEPT);
        Encounter adherenceCounselingEncounter = createEncounter(patient, hivMetadata.getEncounterType(hivMetadata.ADHERENCE_COUNSELING_ENCOUNTER_TYPE), d1).save();
        Obs adherenceCounselingObs = createObs(adherenceCounselingEncounter, adherenceCounselingConcept, firstSessionConcept).save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(2019, 4, 1),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("due-for-adherence-counselling"))));

    }
}
