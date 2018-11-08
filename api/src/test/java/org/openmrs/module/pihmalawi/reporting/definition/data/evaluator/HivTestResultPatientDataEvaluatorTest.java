package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.HivTestResult;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.HivTestResultPatientDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.SkipBaseSetup;

import java.util.Date;
import java.util.List;

import static org.openmrs.module.pihmalawi.metadata.HivMetadata.*;

@SkipBaseSetup
public class HivTestResultPatientDataEvaluatorTest extends BaseMalawiTest {

    Patient patient = null;
    Encounter encounter = null;
    Date encounterDate = DateUtil.getDateTime(2018, 8, 3);

    @Before
    public void shouldSetupBaseData() {
        patient = createPatient().save();
        encounter = createEncounter(patient, hivMetadata.getEidInitialEncounterType(), encounterDate).save();
    }

	@Test
	public void shouldSupportVariousTestQuestionsAndAnswersFilterOnDateAndSortAscending() throws Exception {

        Date endDate = new Date();

        String[] questions = {HIV_TEST_RESULT, HIV_RAPID_TEST, HIV_DNA_PCR, DNA_PCR_RESULT, DNA_PCR_RESULT_2, DNA_PCR_RESULT_3};
        String[] expectedTypes = {null, HIV_RAPID_TEST, HIV_DNA_PCR, HIV_DNA_PCR, HIV_DNA_PCR, HIV_DNA_PCR};

        String[] answers = {POSITIVE, REACTIVE, NEGATIVE, NON_REACTIVE, INDETERMINATE, REACTIVE};
        String[] expectedResults = {POSITIVE, POSITIVE, NEGATIVE, NEGATIVE, INDETERMINATE, POSITIVE};

        int[] resultYears = {2018, 2017, 2016, 2015, 2014, 2013};

        for (int i=0; i<questions.length; i++) {
            Date obsDate = DateUtil.getDateTime(resultYears[i], 05, 30);
            Concept question = hivMetadata.getConcept(questions[i]);
            Concept answer = hivMetadata.getConcept(answers[i]);
            Obs obs = createObs(encounter, question, answer).obsDatetime(obsDate).save();
        }

        List<HivTestResult> results = evaluate(endDate);
        testSize(results, questions.length);
        for (int i=0; i<results.size(); i++) {
            HivTestResult r = results.get(i);

            // Because of sorting, they should be in reverse order
            int expectedIndex = results.size()-i-1;
            Concept expectedType = (expectedTypes[expectedIndex] == null ? null : hivMetadata.getConcept(expectedTypes[expectedIndex]));
            Concept expectedResult = hivMetadata.getConcept(expectedResults[expectedIndex]);
            testResult(r, r.getTestResultObs().getObsDatetime(), expectedType, expectedResult);
        }

        // Test limiting by endDate
        for (int i=0; i<questions.length; i++) {
            endDate = DateUtil.getDateTime(resultYears[i], 12, 31);
            results = evaluate(endDate);
            testSize(results, questions.length-i);
        }
	}

    @Test
    public void shouldSupportGroupedTypeObs() throws Exception {

        Date endDate = new Date();
        Obs result = createObs(encounter, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();

        List<HivTestResult> results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), result.getObsDatetime(), null, hivMetadata.getPositiveConcept());

        Obs type = createObs(encounter, hivMetadata.getHivTestType(), hivMetadata.getHivDnaPcrTest()).save();
        Obs group = createObs(encounter, hivMetadata.getChildHivSerologyConstruct(), null).member(type).member(result).save();

        results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), result.getObsDatetime(), hivMetadata.getHivDnaPcrTest(), hivMetadata.getPositiveConcept());
    }

    @Test
    public void shouldSupportGroupedSampleDateObs() throws Exception {

        Date endDate = new Date();
        Obs result = createObs(encounter, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();

        List<HivTestResult> results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), result.getObsDatetime(), null, hivMetadata.getPositiveConcept());

        Date sampleDate = DateUtil.getDateTime(2018, 2, 2);

        Obs sampleDateObs = createObs(encounter, hivMetadata.getHivTestSampleDateConcept(), sampleDate).save();
        Obs group = createObs(encounter, hivMetadata.getChildHivSerologyConstruct(), null).member(sampleDateObs).member(result).save();

        results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), sampleDate, null, hivMetadata.getPositiveConcept());
    }

    @Test
    public void shouldSupportGroupedResultDateObs() throws Exception {

        Date endDate = new Date();
        Obs result = createObs(encounter, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();

        List<HivTestResult> results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), result.getObsDatetime(), null, hivMetadata.getPositiveConcept());

        Date resultDate = DateUtil.getDateTime(2018, 2, 2);

        Obs resultDateObs = createObs(encounter, hivMetadata.getHivTestResultDateConcept(), resultDate).save();
        Obs group = createObs(encounter, hivMetadata.getChildHivSerologyConstruct(), null).member(resultDateObs).member(result).save();

        results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), resultDate, null, hivMetadata.getPositiveConcept());
    }

    @Test
    public void shouldFavorSampleDateOverResultDate() throws Exception {

        Date endDate = new Date();
        Obs result = createObs(encounter, hivMetadata.getHivTestResult(), hivMetadata.getPositiveConcept()).save();

        List<HivTestResult> results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), result.getObsDatetime(), null, hivMetadata.getPositiveConcept());

        Date sampleDate = DateUtil.getDateTime(2018, 2, 2);
        Date resultDate = DateUtil.getDateTime(2018, 3, 2);

        Obs sampleDateObs = createObs(encounter, hivMetadata.getHivTestSampleDateConcept(), sampleDate).save();
        Obs resultDateObs = createObs(encounter, hivMetadata.getHivTestResultDateConcept(), resultDate).save();
        Obs group = createObs(encounter, hivMetadata.getChildHivSerologyConstruct(), null).member(sampleDateObs).member(resultDateObs).member(result).save();

        results = evaluate(endDate);
        testSize(results, 1);
        testResult(results.get(0), sampleDate, null, hivMetadata.getPositiveConcept());
    }

	protected List<HivTestResult> evaluate(Date endDate) throws Exception {
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(patient.getId());
        HivTestResultPatientDataDefinition dd = new HivTestResultPatientDataDefinition();
        dd.setEndDate(endDate);
        PatientData data = patientDataService.evaluate(dd, context);
        return (List<HivTestResult>)data.getData().get(patient.getId());
    }

    protected void testSize(List<HivTestResult> data, int totalNum) {
        Assert.assertEquals(totalNum, (data == null ? 0 : data.size()));
    }

    protected void testResult(HivTestResult result, Date testDate, Concept testType, Concept testResult) {
        assertBothNullOrEqual(testDate, result.getEffectiveDate());
        assertBothNullOrEqual(testType, result.getTestType());
        assertBothNullOrEqual(testResult, result.getTestResult());
    }
}
