package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.ArtRegimen;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.RegimenChangeConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ArtRegimenHistoryDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArtRegimenHistoryDataEvaluatorTest extends BaseMalawiTest {

    @Autowired
    HivMetadata hivMetadata;

	@Test
	public void shouldTestArtRegimenHistory() throws Exception {

        Patient testPatient = createPatient().save();

        Date d1 = DateUtil.getDateTime(2010, 4, 5);
        Concept reg1 = hivMetadata.getArvRegimen2aConcept();
        Encounter artInitial = createEncounter(testPatient, hivMetadata.getArtInitialEncounterType(), d1).save();
        Obs regChange1 = createObs(artInitial, hivMetadata.getArvDrugsChange1Concept(), reg1).save();
        Obs regDate1 = createObs(artInitial, hivMetadata.getDateOfStartingFirstLineArvsConcept(), d1).save();

        Date d2 = DateUtil.getDateTime(2013, 10, 7);
        Concept reg2 = hivMetadata.getArvRegimen4aConcept();
        Obs regChange2 = createObs(artInitial, hivMetadata.getArvDrugsChange2Concept(), reg2).save();
        Obs regDate2 = createObs(artInitial, hivMetadata.getDateOfStartingAlternativeFirstLineArvsConcept(), d2).save();
        createFollowup(testPatient, reg2, d2);

        Date d3 = DateUtil.getDateTime(2017, 11, 29);
        createFollowup(testPatient, reg2, d3);

        Date d4 = DateUtil.getDateTime(2018, 2, 10);
        createFollowup(testPatient, reg2, d4);

        Date d5 = DateUtil.getDateTime(2018, 6, 17);
        Concept reg3 = hivMetadata.getArvRegimen6aConcept();
        Obs regChange3 = createObs(artInitial, hivMetadata.getArvDrugsChange3Concept(), reg3).save();
        Obs regDate3 = createObs(artInitial, hivMetadata.getDateOfStartingSecondLineArvsConcept(), d5).save();
        createFollowup(testPatient, reg3, d5);

        Date d6 = DateUtil.getDateTime(2018, 8, 11);
        createFollowup(testPatient, reg3, d6);

        // Test all regimens returned
        List<ArtRegimen> regimenData = evaluate(testPatient);
        Assert.assertEquals(8, regimenData.size());
        check(regimenData, 1, d1, reg1);
        check(regimenData, 2, d2, reg2);
        check(regimenData, 3, d2, reg2);
        check(regimenData, 4, d3, reg2);
        check(regimenData, 5, d4, reg2);
        check(regimenData, 6, d5, reg3);
        check(regimenData, 7, d5, reg3);
        check(regimenData, 8, d6, reg3);

        // Test change converter
        RegimenChangeConverter changeConverter = new RegimenChangeConverter();
        List<ArtRegimen> changes = (List<ArtRegimen>)changeConverter.convert(regimenData);
        Assert.assertEquals(3, changes.size());
        check(changes, 1, d1, reg1);
        check(changes, 2, d2, reg2);
        check(changes, 3, d5, reg3);
	}

	protected void check(List<ArtRegimen> regimenData, int whichRegimen, Date regimenDate, Concept regimenConcept) {
	    ArtRegimen regimen = regimenData.get(whichRegimen-1);
	    Assert.assertEquals(regimenDate, regimen.getRegimenDate());
	    Assert.assertEquals(regimenConcept, regimen.getRegimen());
    }

	protected Encounter createFollowup(Patient patient, Concept regimen, Date followupDate) {
        Encounter e = createEncounter(patient, hivMetadata.getArtInitialEncounterType(), followupDate).save();
        Obs o = createObs(e, hivMetadata.getArvDrugsReceivedConcept(), regimen).save();
        return e;
    }

    protected List<ArtRegimen> evaluate(Patient p) throws EvaluationException {
        ArtRegimenHistoryDataDefinition def = new ArtRegimenHistoryDataDefinition();
        EvaluationContext context = new EvaluationContext();
        Cohort baseCohort = new Cohort();
        baseCohort.addMember(p.getId());
        context.setBaseCohort(baseCohort);
        PatientData data = patientDataService.evaluate(def, context);
        List<ArtRegimen> ret = (List<ArtRegimen>) data.getData().get(p.getId());
        return (ret == null ? new ArrayList<ArtRegimen>() : ret);
    }
}
