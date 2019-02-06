package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.BMI;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.BmiPatientDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.SkipBaseSetup;

import java.util.Date;

@SkipBaseSetup
public class BmiPatientDataEvaluatorTest extends BaseMalawiTest {

	@Test
	public void shouldTestBmi() throws Exception {

        Patient patient = createPatient().save();

        Date date1 = DateUtil.getDateTime(2016, 8, 3);
        Date date2 = DateUtil.getDateTime(2017, 9, 4);
        Date date3 = DateUtil.getDateTime(2018, 10, 5);

        Encounter encounter1 = createEncounter(patient, ccMetadata.getAsthmaFollowupEncounterType(), date1).save();
        Encounter encounter2 = createEncounter(patient, ccMetadata.getAsthmaFollowupEncounterType(), date2).save();
        Encounter encounter3 = createEncounter(patient, ccMetadata.getAsthmaFollowupEncounterType(), date3).save();

        Obs weight1 = createObs(encounter1, ccMetadata.getWeightConcept(), 50).save();
        Obs weight2 = createObs(encounter2, ccMetadata.getWeightConcept(), 75).save();
        Obs weight3 = createObs(encounter3, ccMetadata.getWeightConcept(), 100).save();

        Obs height1 = createObs(encounter2, ccMetadata.getHeightConcept(), 170).save();

        testBmi(patient, date1, null, null);
        testBmi(patient, date2, 75.0, 170.0);
        testBmi(patient, date3, 100.0, 170.0);
	}

	protected void testBmi(Patient patient, Date date, Double weight, Double height) throws Exception {

        EvaluationContext context = new EvaluationContext();
        Cohort baseCohort = new Cohort();
        baseCohort.addMember(patient.getId());
        context.setBaseCohort(baseCohort);

        BmiPatientDataDefinition pdd = new BmiPatientDataDefinition();
        pdd.setEndDate(date);

        PatientData data = patientDataService.evaluate(pdd, context);

        Assert.assertEquals(1, data.getData().size());

        BMI bmi = (BMI) data.getData().get(patient.getId());
        if (weight == null && height == null) {
            Assert.assertNull(bmi);
        }
        else {
            Assert.assertEquals(weight, bmi.getWeightObs().getValueNumeric());
            Assert.assertEquals(height, bmi.getHeightObs().getValueNumeric());
            Assert.assertEquals(new Double(weight/((height/100)*(height/100))), new Double(bmi.getNumericValue()));
        }
    }
}
