package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.contrib.testdata.builder.ObsBuilder;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ViralLoadDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.SkipBaseSetup;

import java.util.Date;
import java.util.List;

@SkipBaseSetup
public class ViralLoadDataEvaluatorTest extends BaseMalawiTest {

	@Test
	public void shouldTestViralLoad() throws Exception {

        Patient patient = createPatient().save();
        Encounter encounter = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2016, 8, 3)).save();
        Date effectiveDate = new Date();

        ViralLoad expectedVl = new ViralLoad();
        expectedVl.setEncounterId(encounter.getId());
        expectedVl.setSpecimenDate(encounter.getEncounterDatetime());

        // Test bled only
        Obs bled = createObs(encounter, hivMetadata.getHivViralLoadSpecimenCollectedConcept(), hivMetadata.getTrueConcept()).save();
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);

        // Test bled and LDL
        Obs ldlResult = createObs(encounter, hivMetadata.getHivLDLConcept(), hivMetadata.getTrueConcept()).save();
        expectedVl.setResultLdl(true);
        expectedVl.setResultDate(ldlResult.getObsDatetime());
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);

        // Test bled and numeric
        voidObs(ldlResult);
        Obs numericResult = createObs(encounter, hivMetadata.getHivViralLoadConcept(), 1500L).save();
        expectedVl.setResultLdl(null);
        expectedVl.setResultNumeric(numericResult.getValueNumeric());
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);

        // Test numeric only
        bled = voidObs(bled);
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);

        // Test Obs Group
        ObsBuilder groupObsBuilder = createObs(encounter, hivMetadata.getHivViralLoadTestSetConcept(), null);
        groupObsBuilder.member(numericResult);
        Obs groupObs = groupObsBuilder.save();
        expectedVl.setGroupId(groupObs.getObsId());
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);

        // Test reason for test
        Obs reason = createObs(encounter, hivMetadata.getReasonForTestingConcept(), hivMetadata.getRoutineConcept()).save();
        groupObs.addGroupMember(reason);
        groupObs = tdm.getObsService().saveObs(groupObs, "Add to group");
        expectedVl.setGroupId(groupObs.getObsId());
        expectedVl.setReasonForTest(hivMetadata.getRoutineConcept());
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);

        // Test reason no result
        Obs reasonNoResult = createObs(encounter, hivMetadata.getReasonNoResultConcept(), hivMetadata.getDiedConcept()).save();
        groupObs.addGroupMember(reasonNoResult);
        groupObs = tdm.getObsService().saveObs(groupObs, "Add to group");
        expectedVl.setGroupId(groupObs.getObsId());
        expectedVl.setReasonNoResult(hivMetadata.getDiedConcept());
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);
	}

    @Test
    public void shouldTestViralLoadWithLessThanResultNumeric() throws Exception {

        Patient patient = createPatient().save();
        Encounter encounter = createEncounter(patient, hivMetadata.getArtFollowupEncounterType(), DateUtil.getDateTime(2016, 8, 3)).save();
        Date effectiveDate = new Date();

        ViralLoad expectedVl = new ViralLoad();
        expectedVl.setEncounterId(encounter.getId());
        expectedVl.setSpecimenDate(encounter.getEncounterDatetime());

        createObs(encounter, hivMetadata.getHivViralLoadSpecimenCollectedConcept(), hivMetadata.getTrueConcept()).save();
        Obs lessThanResult = createObs(encounter, hivMetadata.getHivLessThanViralLoadConcept(), 1500L).save();
        expectedVl.setResultLdl(null);
        expectedVl.setLessThanResultNumeric(lessThanResult.getValueNumeric());
        expectedVl.setResultDate(lessThanResult.getObsDatetime());
        testViralLoad(patient.getId(), 1, 1, effectiveDate, expectedVl);

    }

    protected void testViralLoad(Integer pId, int totalNum, Integer whichNum, Date endDate, ViralLoad expectedVl) {
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(pId);
        ViralLoadDataDefinition dd = new ViralLoadDataDefinition();
        dd.setEndDate(endDate);
        try {
            List<ViralLoad> viralLoads = (List<ViralLoad>) patientDataService.evaluate(dd, context).getData().get(pId);
            Assert.assertEquals(totalNum, (viralLoads == null ? 0 : viralLoads.size()));
            if (whichNum != null) {
                ViralLoad vl = viralLoads.get(whichNum-1);
                if (expectedVl != null) {
                    assertBothNullOrEqual(expectedVl.getEncounterId(), vl.getEncounterId());
                    assertBothNullOrEqual(expectedVl.getGroupId(), vl.getGroupId());
                    assertBothNullOrEqual(expectedVl.getSpecimenDate(), vl.getSpecimenDate());
                    assertBothNullOrEqual(expectedVl.getResultDate(), vl.getResultDate());
                    assertBothNullOrEqual(expectedVl.getResultNumeric(), vl.getResultNumeric());
                    assertBothNullOrEqual(expectedVl.getResultLdl(), vl.getResultLdl());
                    assertBothNullOrEqual(expectedVl.getReasonForTest(), vl.getReasonForTest());
                    assertBothNullOrEqual(expectedVl.getReasonNoResult(), vl.getReasonNoResult());
                }
            }
        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
    }
}
