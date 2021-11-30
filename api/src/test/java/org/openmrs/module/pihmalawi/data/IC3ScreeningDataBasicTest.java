package org.openmrs.module.pihmalawi.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.builder.ObsBuilder;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.alert.AlertDefinition;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.metadata.IC3ScreeningMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class IC3ScreeningDataBasicTest extends BaseMalawiTest {

    private final static Log log = LogFactory.getLog(IC3ScreeningDataBasicTest.class);

    @Autowired
    IC3ScreeningData screeningData;

    @Autowired
    IC3ScreeningMetadata screeningMetadata;

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
    public void shouldReturnHighCreatinineAlert() throws Exception {

        Patient patient = createPatient().age(35).save();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Date date1 = DateUtil.getDateTime(currentYear - 1, currentMonth, currentDay - 4);
        Encounter enc1 = createEncounter(patient, ccMetadata.getHtnDiabetesInitialEncounterType(), date1).save();
        Obs type1Diabetes = createObs(enc1, ccMetadata.getChronicCareDiagnosisConcept(), ccMetadata.getType1DiabetesConcept()).save();

        Date date2 = DateUtil.getDateTime(currentYear, currentMonth - 8, currentDay - 4);
        Encounter enc2 = createEncounter(patient, ccMetadata.getHtnDiabetesTestsEncounterType(), date2).save();
        Obs creatinineResult = createObs(enc2, ccMetadata.getCreatinineConcept(), 1.7).save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("high-creatinine"))));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                not((Matcher) hasItem(hasProperty("name", is("routine-creatinine")))));
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

        // Patient changes to 2nd line ART regiment
        Date d3 = DateUtil.getDateTime(2018, 5, 7);
        Concept reg2 = hivMetadata.getArvRegimen7aConcept();
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

        //groupObsBuilder.member(routineTest);
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


    @Test
    public void shouldReturnPossibleHIVInfectionAlert() throws Exception {

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Patient patient = createPatient().age(1).save();

        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(currentYear, currentMonth -11, 2));

        // Exposed Child State
        PatientState childState = new PatientState();
        childState.setStartDate(DateUtil.getDateTime(currentYear, currentMonth -11, 19));
        ProgramWorkflowState exposedChildState = hivMetadata.getExposedChildState();
        childState.setState(exposedChildState);
        patientProgram.getStates().add(childState);
        PatientProgram childProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // HIV DNA-PCR test
        Date date2 = DateUtil.getDateTime(currentYear, currentMonth - 10, 15);
        Encounter enc2 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), date2).save();
        // Child HIV serology construct
        ObsBuilder groupObs = createObs(enc2, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs dnaPcrTest = createObs(enc2, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs positiveResult = createObs(enc2, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObs.member(dnaPcrTest);
        groupObs.member(positiveResult);
        groupObs.save();


        Encounter enc1 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), cal.getTime()).save();
        // Child HIV serology construct
        ObsBuilder groupObsBuilder = createObs(enc1, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs rapidTest = createObs(enc1, hivMetadata.getHivTestType(), hivMetadata.getHivRapidTest()).save();
        Obs positiveRapidTestResult = createObs(enc1, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObsBuilder.member(rapidTest);
        groupObsBuilder.member(positiveRapidTestResult);
        groupObsBuilder.save();


        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(currentYear, currentMonth, currentDay),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eid-positive-rapid-test-today"))));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                not((Matcher) hasItem(hasProperty("name", is("eid-positive-rapid-test")))));

    }

    @Test
    public void shouldReturnPositiveDnaPcrTestTodayAlert() throws Exception {


        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Patient patient = createPatient().birthdate(DateUtil.getDateTime(currentYear, currentMonth -8, currentDay)).save();
        // FIRST HIV DNA-PCR test
        Date date1 = DateUtil.getDateTime(currentYear, currentMonth -1, currentDay);
        Encounter enc1 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), date1).save();
        // Child HIV serology construct
        ObsBuilder firstGroupObs = createObs(enc1, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs firstDnaPcrTest = createObs(enc1, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs firstPositiveResult = createObs(enc1, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        firstGroupObs.member(firstDnaPcrTest);
        firstGroupObs.member(firstPositiveResult);
        firstGroupObs.save();

        // LAST HIV DNA-PCR test
        Encounter enc2 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), cal.getTime()).save();
        // Child HIV serology construct
        ObsBuilder groupObs = createObs(enc2, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs dnaPcrTest = createObs(enc2, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs positiveResult = createObs(enc2, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObs.member(dnaPcrTest);
        groupObs.member(positiveResult);
        groupObs.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(currentYear, currentMonth, currentDay),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eid-positive-dna-pcr-test-today"))));

    }

    @Test
    public void shouldReturnFirstPositiveDnaPcrTestTodayAlert() throws Exception {


        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Patient patient = createPatient().birthdate(DateUtil.getDateTime(currentYear, currentMonth -8, currentDay)).save();

        // FIRST POSITIVE HIV DNA-PCR test today
        Encounter enc2 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), cal.getTime()).save();
        // Child HIV serology construct
        ObsBuilder groupObs = createObs(enc2, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs dnaPcrTest = createObs(enc2, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs positiveResult = createObs(enc2, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObs.member(dnaPcrTest);
        groupObs.member(positiveResult);
        groupObs.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(currentYear, currentMonth, currentDay),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eid-new-positive-dna-pcr-test"))));

    }

    @Test
    public void shouldReturnEidPositiveDnaPcrTestAlert() throws Exception {

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Patient patient = createPatient().birthdate(DateUtil.getDateTime(currentYear, currentMonth -11, 22)).save();

        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(currentYear, currentMonth -9, 22));

        // Exposed Child State
        PatientState childState = new PatientState();
        childState.setStartDate(DateUtil.getDateTime(currentYear, currentMonth -9, 22));
        ProgramWorkflowState exposedChildState = hivMetadata.getExposedChildState();
        childState.setState(exposedChildState);
        patientProgram.getStates().add(childState);
        PatientProgram childProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // FIRST HIV DNA-PCR test: negative
        Date date1 = DateUtil.getDateTime(currentYear, currentMonth -8, currentDay);
        Encounter enc1 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), date1).save();
        // Child HIV serology construct
        ObsBuilder firstGroupObs = createObs(enc1, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs firstDnaPcrTest = createObs(enc1, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs negativeResult = createObs(enc1, hivMetadata.getHivTestResult(), hivMetadata.getNegativeConcept()).save();
        firstGroupObs.member(firstDnaPcrTest);
        firstGroupObs.member(negativeResult);
        firstGroupObs.save();

        // SECOND HIV DNA-PCR test: negative
        Date date2 = DateUtil.getDateTime(currentYear, currentMonth -6, currentDay);
        Encounter enc2 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), date2).save();
        // Child HIV serology construct
        ObsBuilder secondGroupObs = createObs(enc2, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs secondDnaPcrTest = createObs(enc2, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs firstPositiveResult = createObs(enc2, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        secondGroupObs.member(secondDnaPcrTest);
        secondGroupObs.member(firstPositiveResult);
        secondGroupObs.save();

        // Today HIV DNA-PCR test: positive
        Date date3 = DateUtil.getDateTime(currentYear, currentMonth, currentDay -2);
        Encounter enc3 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), date3).save();
        // Child HIV serology construct
        ObsBuilder groupObs = createObs(enc3, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs dnaPcrTest = createObs(enc3, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs todayPositiveResult = createObs(enc3, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObs.member(dnaPcrTest);
        groupObs.member(todayPositiveResult);
        groupObs.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(currentYear, currentMonth, currentDay),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eid-positive-dna-pcr-test"))));

    }

    @Test
    public void shouldReturnFirstNewPositiveDnaPcrTestAlert() throws Exception {

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Patient patient = createPatient().birthdate(DateUtil.getDateTime(currentYear, currentMonth -7, 22)).save();

        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(currentYear, currentMonth -6, 22));

        // Exposed Child State
        PatientState childState = new PatientState();
        childState.setStartDate(DateUtil.getDateTime(currentYear, currentMonth -6, 22));
        ProgramWorkflowState exposedChildState = hivMetadata.getExposedChildState();
        childState.setState(exposedChildState);
        patientProgram.getStates().add(childState);
        PatientProgram childProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // FIRST HIV DNA-PCR test: negative
        Date date1 = DateUtil.getDateTime(currentYear, currentMonth -1, currentDay);
        Encounter enc1 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), date1).save();
        // Child HIV serology construct
        ObsBuilder firstGroupObs = createObs(enc1, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs firstDnaPcrTest = createObs(enc1, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs negativeResult = createObs(enc1, hivMetadata.getHivTestResult(), hivMetadata.getNegativeConcept()).save();
        firstGroupObs.member(firstDnaPcrTest);
        firstGroupObs.member(negativeResult);
        firstGroupObs.save();

        // Today HIV DNA-PCR test: positive
        Encounter enc2 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), Calendar.getInstance().getTime()).save();
        // Child HIV serology construct
        ObsBuilder groupObs = createObs(enc2, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs dnaPcrTest = createObs(enc2, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs positiveResult = createObs(enc2, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObs.member(dnaPcrTest);
        groupObs.member(positiveResult);
        groupObs.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(currentYear, currentMonth, currentDay),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eid-new-positive-dna-pcr-test"))));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                not((Matcher) hasItem(hasProperty("name", is("eid-positive-dna-pcr-test")))));

    }

    @Test
    public void shouldReturnRoutineViralLoad2pAlert() throws Exception {

        Patient patient = createPatient().birthdate(DateUtil.getDateTime(2008, 11, 22)).save();

        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(2010, 2, 22));

        // Patient on ART
        PatientState patientState = new PatientState();
        patientState.setStartDate(DateUtil.getDateTime(2011, 2, 12));
        ProgramWorkflowState onArvsState = hivMetadata.getOnArvsState();
        patientState.setState(onArvsState);
        patientProgram.getStates().add(patientState);
        PatientProgram savePatientProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // Patient changes ART regiment
        Date d3 = DateUtil.getDateTime(2017, 3, 22);
        Concept reg2 = hivMetadata.getArvRegimen4aConcept();
        Encounter reg2Encounter = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), d3).save();
        Obs regChange2 = createObs(reg2Encounter, hivMetadata.getArvDrugsChange2Concept(), reg2).save();
        Obs regDate2 = createObs(reg2Encounter, hivMetadata.getDateOfStartingAlternativeFirstLineArvsConcept(), d3).save();

        // Viral Load Test
        Date lastViralLoadDate = DateUtil.getDateTime(2018, 2, 20);
        Encounter enc1 = createEncounter(patient, hivMetadata.getExposedChildFollowupEncounterType(), lastViralLoadDate).save();
        ObsBuilder groupObsBuilder = createObs(enc1, hivMetadata.getHivViralLoadTestSetConcept(), null);
        Obs routineTest = createObs(enc1, hivMetadata.getReasonForTestingConcept(), hivMetadata.getRoutineConcept()).save();
        // bled
        Obs bled = createObs(enc1, hivMetadata.getHivViralLoadSpecimenCollectedConcept(), hivMetadata.getTrueConcept()).save();
        Obs numericResult = createObs(enc1, hivMetadata.getHivViralLoadConcept(), 0L).save();

        groupObsBuilder.member(routineTest);
        groupObsBuilder.member(bled);
        groupObsBuilder.member(numericResult);
        groupObsBuilder.save();


        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(2019, 4, 20),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("due-for-routine-viral-load-2"))));

    }

    @Test
    public void shouldGetMostRecentBloodPressureScreeningEncounterDateForPatient() {

        Patient patient = createPatient().birthdate(DateUtil.getDateTime(2016, 11, 22)).save();
        createEncounter(patient, screeningMetadata.getBloodPressureScreeningEncounterType(), DateUtil.getDateTime(2016, 11, 22)).save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                new Date(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat((Date) patientData.get("last_bp_screening_datetime"), is( DateUtil.getDateTime(2016, 11, 22)));


    }

    @Test
    public void getDataForPatient_shouldReturnEligibleForCervicalCancerScreening() throws Exception {
        //https://pihemr.atlassian.net/browse/IS-204

        Patient patient = createPatient().age(32).save();
        patient.setGender("F");
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);


        Encounter htcEncounter = createEncounter(patient, screeningMetadata.getHTCScreeningEncounterType(), DateUtil.getDateTime(currentYear -2 , currentMonth, currentDay)).save();
        // HTC test about 2 years ago
        // hivMetadata.getChildHivSerologyConstruct() is the same as HIV Test Construct
        ObsBuilder htcGroupObsBuilder = createObs(htcEncounter, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs rapidTest = createObs(htcEncounter, hivMetadata.getHivTestType(), hivMetadata.getHivRapidTest()).save();
        Obs neagativeRapidTestResult = createObs(htcEncounter, hivMetadata.getHivTestResult(), hivMetadata.getNegativeConcept()).save();
        htcGroupObsBuilder.member(rapidTest);
        htcGroupObsBuilder.member(neagativeRapidTestResult);
        htcGroupObsBuilder.save();

        // more than 3 years ago
        Encounter enc1 = createEncounter(patient, screeningMetadata.getCervicalScreeningEncounterType(), DateUtil.getDateTime(currentYear -3 , currentMonth-2, currentDay)).save();
        ObsBuilder groupObsBuilder = createObs(enc1, screeningMetadata.getCervicalCancerScreeningConstructConcept(), null);
        Obs cervicalScreening = createObs(enc1, screeningMetadata.getCervicalCancerScreeningResultsConcept(), screeningMetadata.getNormalConcept()).save();
        groupObsBuilder.member(cervicalScreening);
        groupObsBuilder.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eligible-for-cervical-cancer-screening"))));

    }

    @Test
    public void getDataForPatient_shouldReturnEligibleForFamilyPlanning() throws Exception {

        Patient patient = createPatient().age(16).save();
        patient.setGender("F");
        Calendar cal = Calendar.getInstance();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("eligible-for-family-planning"))));

    }

    @Test
    public void getDataForPatient_shouldReturnFastTrackHivPatients() throws Exception {

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Patient patient = createPatient().birthdate(DateUtil.getDateTime(currentYear - 34, currentMonth - 2, 22)).save();
        patient.setGender("M");


        Program hivProgram = hivMetadata.getHivProgram();
        PatientProgram patientProgram = new PatientProgram();
        patientProgram.setPatient(patient);
        patientProgram.setProgram(hivProgram);
        patientProgram.setDateEnrolled(DateUtil.getDateTime(currentYear -3, currentMonth - 2, 22));

        // Patient on ART
        PatientState patientState = new PatientState();
        patientState.setStartDate(DateUtil.getDateTime(currentYear - 3, currentMonth + 1, 22));
        ProgramWorkflowState onArvsState = hivMetadata.getOnArvsState();
        patientState.setState(onArvsState);
        patientProgram.getStates().add(patientState);
        PatientProgram savePatientProgram = Context.getProgramWorkflowService().savePatientProgram(patientProgram);

        // Patient starts first line regiment
        Date regDate = DateUtil.getDateTime(currentYear - 2, currentMonth - 2, 13);
        Concept reg1 = hivMetadata.getArvRegimen2aConcept();
        Encounter reg1Encounter = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), regDate).save();
        Obs reg1Obs = createObs(reg1Encounter, hivMetadata.getArvDrugsChange1Concept(), reg1).save();
        Obs regDate1 = createObs(reg1Encounter, hivMetadata.getDateOfStartingFirstLineArvsConcept(), regDate).save();

        Date d1 = DateUtil.getDateTime(currentYear - 2, currentMonth + 2, 23);
        Encounter enc1 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), d1).save();
        Obs weightObs = createObs(enc1, ccMetadata.getWeightConcept(), 71.0).save();
        Obs heightObs = createObs(enc1, ccMetadata.getHeightConcept(), 165).save();

        // Viral Load Test
        Date lastViralLoadDate = DateUtil.getDateTime(currentYear , currentMonth  - 10, 20);
        Encounter vlEncounter = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), lastViralLoadDate).save();
        ObsBuilder groupObsBuilder = createObs(vlEncounter, hivMetadata.getHivViralLoadTestSetConcept(), null);
        Obs routineTest = createObs(vlEncounter, hivMetadata.getReasonForTestingConcept(), hivMetadata.getRoutineConcept()).save();
        // bled
        Obs bled = createObs(vlEncounter, hivMetadata.getHivViralLoadSpecimenCollectedConcept(), hivMetadata.getTrueConcept()).save();
        //Obs numericResult = createObs(vlEncounter, hivMetadata.getHivViralLoadConcept(), 1500L).save();
        Obs ldlResult = createObs(vlEncounter, hivMetadata.getHivLDLConcept(), hivMetadata.getTrueConcept()).save();

        groupObsBuilder.member(routineTest);
        groupObsBuilder.member(bled);
        groupObsBuilder.member(ldlResult);
        groupObsBuilder.save();

        Encounter enc2 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), cal.getTime()).save();
        createObs(enc2, ccMetadata.getWeightConcept(), 70.0).save();
        createObs(enc2, ccMetadata.getHeightConcept(), 165).save();

        Encounter enc3 = createEncounter(patient, ccMetadata.getTBScreeningEncounterType(), cal.getTime()).save();
        ObsBuilder tbSymptomsGroupObs = createObs(enc3, ccMetadata.getTbScreeningSetConcept(), null);
        //Obs tbCoughPresent = createObs(enc3, ccMetadata.getSymptomPresentConcept(), ccMetadata.getCoughConcept()).save();
        Obs tbCoughAbsent = createObs(enc3, ccMetadata.getSymptomAbsentConcept(), ccMetadata.getCoughConcept()).save();
        tbSymptomsGroupObs.member(tbCoughAbsent);
        tbSymptomsGroupObs.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("fast-track-hiv-patients"))));

    }

    @Test
    public void shouldReturnCheckedInPatientWithoutAppointment() throws Exception {

        Patient patient = createPatient().age(36).save();
        patient.setGender("M");
        Calendar cal = Calendar.getInstance();


        Encounter enc1 = createEncounter(patient, screeningMetadata.getCheckInEncounterType(), cal.getTime()).save();
        Obs referral = createObs(enc1, screeningMetadata.getSourceOfReferralConcept(), screeningMetadata.getHealthCenterReferralConcept()).save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("checked-in-patients-without-appointment"))));

    }

    @Test
    public void shouldReturnTBReferToClinicianAlert() throws Exception {

        Patient patient = createPatient().age(32).save();
        patient.setGender("F");
        Calendar cal = Calendar.getInstance();


        Encounter enc1 = createEncounter(patient, screeningMetadata.getCheckInEncounterType(), cal.getTime()).save();
        Obs referral = createObs(enc1, screeningMetadata.getSourceOfReferralConcept(), screeningMetadata.getHealthCenterReferralConcept()).save();

        Encounter tbTestResultEncounter = createEncounter(patient, screeningMetadata.getTBTestResultsEncounterType(), cal.getTime()).save();
        createObs(tbTestResultEncounter, screeningMetadata.getRecommendedNextStepsConcept(), screeningMetadata.getReferToClinicianConcept()).save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));
        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("tb-refer-to-clinician"))));

    }

    @Test
    public void shouldReturnPositivePriorHivTestAlert() throws Exception {

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        Patient patient = createPatient().age(28).save();

        // one month ago
        Date date1 = DateUtil.getDateTime(currentYear, currentMonth - 1, currentDay - 3);

        Encounter enc1 = createEncounter(patient, screeningMetadata.getHTCScreeningEncounterType(), date1).save();
        // HTC test
        // hivMetadata.getChildHivSerologyConstruct() is the same as HIV Test Construct
        ObsBuilder groupObsBuilder = createObs(enc1, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs rapidTest = createObs(enc1, hivMetadata.getHivTestType(), hivMetadata.getHivRapidTest()).save();
        Obs positiveRapidTestResult = createObs(enc1, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObsBuilder.member(rapidTest);
        groupObsBuilder.member(positiveRapidTestResult);
        groupObsBuilder.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                DateUtil.getDateTime(currentYear, currentMonth, currentDay),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("positive-prior-hiv-test"))));

    }

    @Test
    public void shouldReturnPositiveHivTestTodayAlert() throws Exception {

        Calendar cal = Calendar.getInstance();

        Patient patient = createPatient().age(28).save();

        // today
        Encounter enc1 = createEncounter(patient, screeningMetadata.getHTCScreeningEncounterType(), cal.getTime()).save();
        // HTC test
        // hivMetadata.getChildHivSerologyConstruct() is the same as HIV Test Construct
        ObsBuilder groupObsBuilder = createObs(enc1, hivMetadata.getChildHivSerologyConstruct(), null);
        Obs rapidTest = createObs(enc1, hivMetadata.getHivTestType(), hivMetadata.getHivRapidTest()).save();
        Obs positiveRapidTestResult = createObs(enc1, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();
        groupObsBuilder.member(rapidTest);
        groupObsBuilder.member(positiveRapidTestResult);
        groupObsBuilder.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("positive-hiv-test-today"))));

    }


    @Test
    public void shouldReturnAbnormalGlucoselAlert() throws Exception {

        Calendar cal = Calendar.getInstance();

        Patient patient = createPatient().age(28).save();

        // today
        Encounter enc1 = createEncounter(patient, screeningMetadata.getBloodSugarScreeningEncounterType(), cal.getTime()).save();
        // Blood Sugar Test Set
        ObsBuilder groupObsBuilder = createObs(enc1, screeningMetadata.getBloodSugarTestSetConcept(), null);

        Obs fastingTest = createObs(enc1, ccMetadata.getBloodSugarTestTypeConcept(), ccMetadata.getFastingBloodSugarTestTypeConcept()).save();
        // we are only using
        Obs randomGlucoseLevel = createObs(enc1, ccMetadata.getBloodSugarTestResultConcept(), 140).save();
        groupObsBuilder.member(fastingTest);
        groupObsBuilder.member(randomGlucoseLevel);
        groupObsBuilder.save();

        JsonObject patientData = screeningData.getDataForPatient(
                patient.getPatientId(),
                cal.getTime(),
                hivMetadata.getLocation("Neno District Hospital"),
                false);

        assertThat(patientData.size(), greaterThan(0));

        assertThat(
                (List<AlertDefinition>)patientData.get("alerts"),
                (Matcher) hasItem(hasProperty("name", is("abnormal-result-glucose-level"))));

    }
}
