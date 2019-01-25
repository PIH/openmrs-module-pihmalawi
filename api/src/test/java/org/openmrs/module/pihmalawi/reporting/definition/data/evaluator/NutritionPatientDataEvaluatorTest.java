package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.BMI;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.NutritionPatientDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.List;

public class NutritionPatientDataEvaluatorTest extends BaseMalawiTest {

    @Test
    public void shouldRetrieveNutritionAndCalculatedBmi() {

        Patient patient = createPatient().birthdate("1985-1-1").save();

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(patient.getId());

        NutritionPatientDataDefinition dd = new NutritionPatientDataDefinition();
        dd.setEndDate(DateUtil.getStartOfDay(new Date()));

        Encounter enc1 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2017, 8, 3)).save();  // just use a random encounter type
        Encounter enc2 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2016, 8, 3)).save();  // just use a random encounter type
        Encounter enc3 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2015, 8, 3)).save();  // just use a random encounter type
        Encounter enc4 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2010, 8, 3)).save();  // just use a random encounter type
        Encounter enc5 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2009, 8, 3)).save();  // just use a random encounter type
        Encounter enc6 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2005, 8, 3)).save();  // just use a random encounter type
        Encounter enc7 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2000, 8, 3)).save();  // just use a random encounter type

        createObs(enc1, ccMetadata.getWeightConcept(), 110).save();
        Double expectedBmiEnc1 = bmi(110, 100);

        createObs(enc2, ccMetadata.getWeightConcept(), 105).save();
        createObs(enc2, ccMetadata.getHeightConcept(), 100).save();
        Double expectedBmiEnc2 = bmi(105, 100);

        createObs(enc3, ccMetadata.getWeightConcept(), 95).save();
        createObs(enc3, ccMetadata.getHeightConcept(), 90).save();
        Double expectedBmiEnc3 = bmi(95, 90);

        createObs(enc4, ccMetadata.getWeightConcept(), 120).save();
        createObs(enc4, ccMetadata.getHeightConcept(), 100).save();
        createObs(enc4, ccMetadata.getIsPatientPregnantConcept(), ccMetadata.getYesConcept()).save();

        createObs(enc5, ccMetadata.getWeightConcept(), 108).save();
        createObs(enc5, ccMetadata.getHeightConcept(), 100).save();
        createObs(enc5, ccMetadata.getPregnantOrLactatingConcept(), ccMetadata.getPatientPregnantConcept()).save();

        createObs(enc6, ccMetadata.getWeightConcept(), 100).save();

        createObs(enc7, ccMetadata.getWeightConcept(), 50).save();
        createObs(enc7, ccMetadata.getHeightConcept(), 50).save();
        createObs(enc7, ccMetadata.getMUACConcept(), 14).save();


        try {
            List<Object> obsAndBmi = (List<Object>) patientDataService.evaluate(dd, context).getData().get(patient.getId());

            Assert.assertEquals(18, obsAndBmi.size());

            // encounter in 2017
            Assert.assertEquals(expectedBmiEnc1, (Double) ((BMI) obsAndBmi.get(0)).getNumericValue());
            Assert.assertEquals(new Double(110), ((Obs) obsAndBmi.get(1)).getValueNumeric());   // Weight

            // encounter in 2016
            Assert.assertEquals(expectedBmiEnc2, (Double) ((BMI) obsAndBmi.get(2)).getNumericValue());
            Assert.assertEquals(new Double(105), ((Obs) obsAndBmi.get(3)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(4)).getValueNumeric());   // Height

            // encounter in 2015
            Assert.assertEquals(expectedBmiEnc3, (Double) ((BMI) obsAndBmi.get(5)).getNumericValue());
            Assert.assertEquals(new Double(95), ((Obs) obsAndBmi.get(6)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(90), ((Obs) obsAndBmi.get(7)).getValueNumeric());   // Height

            // encounter in 2010
            // no BMI because patient is pregnant
            Assert.assertEquals(new Double(120), ((Obs) obsAndBmi.get(8)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(9)).getValueNumeric());   // Height
            Assert.assertEquals(ccMetadata.getYesConcept(), ((Obs) obsAndBmi.get(10)).getValueCoded());  // pregnancy

            // encounter in 2009
            // no BMI because patient is pregnant
            Assert.assertEquals(new Double(108), ((Obs) obsAndBmi.get(11)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(12)).getValueNumeric());   // Height
            Assert.assertEquals(ccMetadata.getPatientPregnantConcept(), ((Obs) obsAndBmi.get(13)).getValueCoded());  // pregnancy

            // encounter in 2005
            // no height because we haven't collected a date since they have become an adult
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(14)).getValueNumeric());   // Weight

            // encounter int 2000
            Assert.assertEquals(new Double(14), ((Obs) obsAndBmi.get(15)).getValueNumeric());
            Assert.assertEquals(new Double(50), ((Obs) obsAndBmi.get(16)).getValueNumeric());   // Weight
            Assert.assertEquals(new Double(50), ((Obs) obsAndBmi.get(17)).getValueNumeric());   // Height

        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    private double bmi(double weight, double height) {
        return weight/Math.pow(height/100,2);
    }

}
