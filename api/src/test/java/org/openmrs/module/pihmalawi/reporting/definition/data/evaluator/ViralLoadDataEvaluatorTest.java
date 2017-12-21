package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.openmrs.Cohort;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ViralLoadDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;

public class ViralLoadDataEvaluatorTest extends StandaloneContextSensitiveTest {

	@Autowired
    HivPatientDataLibrary hivPatientDataLibrary;

    @Autowired
    ChronicCarePatientDataLibrary chronicCarePatientDataLibrary;

	@Autowired
    @Qualifier("reportingPatientDataService")
    PatientDataService patientDataService;

    @Autowired
    HivMetadata hivMetadata;

	@Override
	protected boolean isEnabled() {
		return true;
	}

	@Override
	public void performTest() throws Exception {
        Date endDate = DateUtil.getDateTime(2017, 12, 1);

        Date testDate = DateUtil.getDateTime(2016, 8, 22);
        testViralLoad(16025, 2, 1, endDate, testDate, 93487d, null);

        testDate = DateUtil.getDateTime(2017, 1, 23);
        testViralLoad(16025, 2, 2, endDate, testDate, 97017d, null);

        testDate = DateUtil.getDateTime(2017, 6, 1);
        testViralLoad(15892, 2, 2, endDate, testDate, null, true);
	}

    protected void testViralLoad(Integer pId, int totalNum, Integer whichNum, Date endDate, Date testDate, Double numericResult, Boolean ldlResult) {
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(pId);
        ViralLoadDataDefinition dd = new ViralLoadDataDefinition();
        dd.setEndDate(endDate);
        try {
            List<ViralLoad> viralLoads = (List<ViralLoad>) patientDataService.evaluate(dd, context).getData().get(pId);
            Assert.assertEquals(totalNum, viralLoads.size());
            if (whichNum != null) {
                ViralLoad viralLoad = viralLoads.get(whichNum-1);
                Assert.assertEquals(testDate, viralLoad.getResultDate());
                Assert.assertEquals(numericResult, viralLoad.getResultNumeric());
                Assert.assertEquals(ldlResult, viralLoad.getResultLdl());
            }
        }
        catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
    }
}
