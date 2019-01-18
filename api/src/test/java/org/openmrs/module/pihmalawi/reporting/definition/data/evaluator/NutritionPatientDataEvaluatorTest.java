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

        Patient patient = createPatient().save();

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(patient.getId());

        NutritionPatientDataDefinition dd = new NutritionPatientDataDefinition();
        dd.setEndDate(DateUtil.getStartOfDay(new Date()));

        Encounter enc1 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2016, 8, 3)).save();  // just use a random encounter type
        Encounter enc2 = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2015, 8, 3)).save();  // just use a random encounter type

        createObs(enc1, ccMetadata.getWeightConcept(), 105).save();
        createObs(enc1, ccMetadata.getHeightConcept(), 100).save();
        Double expectedBmiEnc1 = bmi(105, 100);

        createObs(enc2, ccMetadata.getWeightConcept(), 95).save();
        createObs(enc2, ccMetadata.getHeightConcept(), 90).save();
        Double expectedBmiEnc2 = bmi(95, 90);


        try {
            List<Object> obsAndBmi = (List<Object>) patientDataService.evaluate(dd, context).getData().get(patient.getId());
            Assert.assertEquals(6, obsAndBmi.size());
            Assert.assertEquals(new Double(90), ((Obs) obsAndBmi.get(0)).getValueNumeric());
            Assert.assertEquals(new Double(95), ((Obs) obsAndBmi.get(1)).getValueNumeric());
            Assert.assertEquals(expectedBmiEnc2, (Double) ((BMI) obsAndBmi.get(2)).getNumericValue());
            Assert.assertEquals(new Double(100), ((Obs) obsAndBmi.get(3)).getValueNumeric());
            Assert.assertEquals(new Double(105), ((Obs) obsAndBmi.get(4)).getValueNumeric());
            Assert.assertEquals(expectedBmiEnc1, (Double) ((BMI) obsAndBmi.get(5)).getNumericValue());
        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    private double bmi(double weight, double height) {
        return weight/Math.pow(height/100,2);
    }

}
