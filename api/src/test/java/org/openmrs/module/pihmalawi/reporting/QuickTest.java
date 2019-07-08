package org.openmrs.module.pihmalawi.reporting;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ViralLoadDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class QuickTest extends StandaloneContextSensitiveTest {

	@Autowired
	DataFactory df;

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	ChronicCareMetadata ccMetadata;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	HivEncounterQueryLibrary hivEncounterQueries;

    @Autowired @Qualifier("reportingPatientDataService")
    PatientDataService patientDataService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {
		LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.TRACE);

		EvaluationContext context = new EvaluationContext();

        ViralLoadDataDefinition dd = new ViralLoadDataDefinition();
        PatientData data = patientDataService.evaluate(dd, new EvaluationContext());
        for (Integer pId : data.getData().keySet()) {
            List<ViralLoad> viralLoads = (List<ViralLoad>)data.getData().get(pId);
            for (ViralLoad vl: viralLoads) {
                StringBuilder sb = new StringBuilder();
                sb.append("PID: " + pId);
                if (vl.getSpecimenDate() == null) {
                    sb.append(", No specimen collection date.  Group ID: " + vl.getGroupId() + "; Encounter ID: " + vl.getEncounterId());
                }
                if (vl.getSpecimenDate() != null && vl.getResultDate() == null) {
                    sb.append(", Pending");
                }
                if (vl.getReasonForTest() != null) {
                    sb.append(", Reason: " + vl.getReasonForTest());
                }
                if (vl.getReasonNoResult() != null) {
                    sb.append(", No Result: " + vl.getReasonNoResult());
                }
                System.out.println(sb.toString());
            }
        }
	}
}
