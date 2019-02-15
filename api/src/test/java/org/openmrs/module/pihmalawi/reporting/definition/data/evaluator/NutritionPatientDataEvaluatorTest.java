package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.ConceptNumeric;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.BMI;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.NutritionHistoryPatientDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.List;

public class NutritionPatientDataEvaluatorTest extends BaseMalawiTest {

    @Test
    public void shouldRetrieveNutritionAndCalculatedBmi() {

        ConceptNumeric height = (ConceptNumeric) ccMetadata.getHeightConcept();
        height.setLowAbsolute(null);  // temporarily remove low absolute so we can test heights = 0 (which exist in the system)

        Patient patient = createPatient().birthdate("1985-1-1").save();

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(patient.getId());

        NutritionHistoryPatientDataDefinition dd = new NutritionHistoryPatientDataDefinition();
        dd.setEndDate(DateUtil.getDateTime(2018, 1, 1));

        Encounter enc0 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2018, 8, 3)).save();  // just use a random encounter type
        Encounter enc1 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2017, 12, 3)).save();  // just use a random encounter type
        Encounter enc2 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2017, 8, 3)).save();  // just use a random encounter type
        Encounter enc3 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2016, 8, 3)).save();  // just use a random encounter type
        Encounter enc4 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2015, 8, 3)).save();  // just use a random encounter type
        Encounter enc5 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2010, 8, 3)).save();  // just use a random encounter type
        Encounter enc6 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2009, 8, 3)).save();  // just use a random encounter type
        Encounter enc7 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2005, 8, 3)).save();  // just use a random encounter type
        Encounter enc8 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2000, 8, 3)).save();  // just use a random encounter type

        createObs(enc0, ccMetadata.getWeightConcept(), 120).save();  // this should be skipped because we will set an end date before this

        createObs(enc1, ccMetadata.getWeightConcept(), 110).save();
        Double expectedBmiEnc1 = bmi(110, 100);

        createObs(enc2, ccMetadata.getWeightConcept(), 108).save();
        createObs(enc2, ccMetadata.getHeightConcept(), 0).save();  // should ignore height concepts with 0
        Double expectedBmiEnc2 = bmi(108, 100);

        createObs(enc3, ccMetadata.getWeightConcept(), 105).save();
        createObs(enc3, ccMetadata.getHeightConcept(), 100).save();
        Double expectedBmiEnc3 = bmi(105, 100);

        createObs(enc4, ccMetadata.getWeightConcept(), 95).save();
        createObs(enc4, ccMetadata.getHeightConcept(), 90).save();
        Double expectedBmiEnc4 = bmi(95, 90);

        createObs(enc5, ccMetadata.getWeightConcept(), 120).save();
        createObs(enc5, ccMetadata.getHeightConcept(), 100).save();
        createObs(enc5, ccMetadata.getIsPatientPregnantConcept(), ccMetadata.getYesConcept()).save();

        createObs(enc6, ccMetadata.getWeightConcept(), 108).save();
        createObs(enc6, ccMetadata.getHeightConcept(), 100).save();
        createObs(enc6, ccMetadata.getPregnantOrLactatingConcept(), ccMetadata.getPatientPregnantConcept()).save();

        createObs(enc7, ccMetadata.getWeightConcept(), 100).save();

        createObs(enc8, ccMetadata.getWeightConcept(), 50).save();
        createObs(enc8, ccMetadata.getHeightConcept(), 50).save();
        createObs(enc8, ccMetadata.getMUACConcept(), 14).save();


        try {
            List<Object> obsAndBmi = (List<Object>) patientDataService.evaluate(dd, context).getData().get(patient.getId());

            Assert.assertEquals(20, obsAndBmi.size());

            // later encounter in 2017
            Assert.assertEquals(expectedBmiEnc1, (Double) ((BMI) obsAndBmi.get(0)).getNumericValue());
            Assert.assertEquals(new Double(110), ((Obs) obsAndBmi.get(1)).getValueNumeric());   // Weight

            // earlier encounter in 2017
            Assert.assertEquals(expectedBmiEnc2, (Double) ((BMI) obsAndBmi.get(2)).getNumericValue());
            Assert.assertEquals(new Double(108), ((Obs) obsAndBmi.get(3)).getValueNumeric());   // Weight
            // no height, since this was a bad obs with height = 0

            // encounter in 2016
            Assert.assertEquals(expectedBmiEnc3, (Double) ((BMI) obsAndBmi.get(4)).getNumericValue());
            Assert.assertEquals(new Double(105), ((Obs) obsAndBmi.get(5)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(6)).getValueNumeric());   // Height

            // encounter in 2015
            Assert.assertEquals(expectedBmiEnc4, (Double) ((BMI) obsAndBmi.get(7)).getNumericValue());
            Assert.assertEquals(new Double(95), ((Obs) obsAndBmi.get(8)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(90), ((Obs) obsAndBmi.get(9)).getValueNumeric());   // Height

            // encounter in 2010
            // no BMI because patient is pregnant
            Assert.assertEquals(new Double(120), ((Obs) obsAndBmi.get(10)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(11)).getValueNumeric());   // Height
            Assert.assertEquals(ccMetadata.getYesConcept(), ((Obs) obsAndBmi.get(12)).getValueCoded());  // pregnancy

            // encounter in 2009
            // no BMI because patient is pregnant
            Assert.assertEquals(new Double(108), ((Obs) obsAndBmi.get(13)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(14)).getValueNumeric());   // Height
            Assert.assertEquals(ccMetadata.getPatientPregnantConcept(), ((Obs) obsAndBmi.get(15)).getValueCoded());  // pregnancy

            // encounter in 2005
            // no height because we haven't collected a date since they have become an adult
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(16)).getValueNumeric());   // Weight

            // encounter int 2000
            Assert.assertEquals(new Double(14), ((Obs) obsAndBmi.get(17)).getValueNumeric());
            Assert.assertEquals(new Double(50), ((Obs) obsAndBmi.get(18)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(50), ((Obs) obsAndBmi.get(19)).getValueNumeric());   // Height

        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    private double bmi(double weight, double height) {
        return weight/Math.pow(height/100,2);
    }

}
