package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.contrib.testdata.builder.ObsBuilder;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.CodedValueAndDate;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.CodedValueAndDatePatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ViralLoadDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.SkipBaseSetup;

import java.util.Date;
import java.util.List;

public class CodedValueAndDateDataEvaluatorTest extends BaseMalawiTest {

	@Test
	public void shouldTestValueAndDate() throws Exception {

        Patient patient = createPatient().save();
        EvaluationContext context = new EvaluationContext();
        Cohort baseCohort = new Cohort();
        baseCohort.addMember(patient.getId());
        context.setBaseCohort(baseCohort);

        Encounter encounter = createEncounter(patient, ccMetadata.getHtnDiabetesInitialEncounterType(), DateUtil.getDateTime(2016, 8, 3)).save();

        Obs diabetes = createObs(encounter, ccMetadata.getChronicCareDiagnosisConcept(), ccMetadata.getDiabetesConcept()).save();

        Obs htn = createObs(encounter, ccMetadata.getChronicCareDiagnosisConcept(), ccMetadata.getHypertensionConcept()).get();
        Obs htnDate = createObs(encounter, ccMetadata.getDiagnosisDateConcept(), DateUtil.getDateTime(2011, 3, 18)).get();
        Obs htnGroup = createObs(encounter, ccMetadata.getHivViralLoadTestSetConcept(), null).member(htn).member(htnDate).save();

        CodedValueAndDatePatientDataDefinition pdd = new CodedValueAndDatePatientDataDefinition();
        pdd.setCodedValueQuestion(ccMetadata.getChronicCareDiagnosisConcept());
        pdd.setDateValueQuestion(ccMetadata.getDiagnosisDateConcept());

        PatientData data = patientDataService.evaluate(pdd, context);

        Assert.assertEquals(1, data.getData().size());

        List<CodedValueAndDate> patientData = (List<CodedValueAndDate>)data.getData().get(patient.getId());
        Assert.assertEquals(2, patientData.size());

        CodedValueAndDate expectedHtn = patientData.get(0);
        Assert.assertEquals(htn, expectedHtn.getValueObs());
        Assert.assertEquals(htnDate, expectedHtn.getDateObs());
        Assert.assertEquals(ccMetadata.getHypertensionConcept(), expectedHtn.getValue());
        Assert.assertEquals(htnDate.getValueDatetime(), expectedHtn.getDate());

        CodedValueAndDate expectedDiabetes = patientData.get(1);
        Assert.assertEquals(diabetes, expectedDiabetes.getValueObs());
        Assert.assertNull(expectedDiabetes.getDateObs());
        Assert.assertEquals(ccMetadata.getDiabetesConcept(), expectedDiabetes.getValue());
        Assert.assertEquals(encounter.getEncounterDatetime(), expectedDiabetes.getDate());

        // Test that endDate is respected with and without a date obs
        pdd.setEndDate(DateUtil.getDateTime(2015, 1, 1));
        data = patientDataService.evaluate(pdd, context);

        patientData = (List<CodedValueAndDate>)data.getData().get(patient.getId());
        Assert.assertEquals(1, patientData.size());
        Assert.assertEquals(expectedHtn, patientData.get(0));

        pdd.setEndDate(DateUtil.getDateTime(2011, 3, 17));
        data = patientDataService.evaluate(pdd, context);
        patientData = (List<CodedValueAndDate>)data.getData().get(patient.getId());
        Assert.assertEquals(0, patientData.size());
	}
}
