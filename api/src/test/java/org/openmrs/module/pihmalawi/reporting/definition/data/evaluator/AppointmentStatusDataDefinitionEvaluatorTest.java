package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

public class AppointmentStatusDataDefinitionEvaluatorTest extends StandaloneContextSensitiveTest {

    private Integer pId = 16193;

	@Autowired
    HivPatientDataLibrary hivPatientDataLibrary;

    @Autowired
    ChronicCarePatientDataLibrary chronicCarePatientDataLibrary;

	@Autowired
    PatientDataService patientDataService;

    @Autowired
    EvaluationService evaluationService;

    @Autowired
    HivMetadata hivMetadata;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(pId);
        printData(patientDataService.evaluate(hivPatientDataLibrary.getHccAppointmentStatus(), context));
        printData(patientDataService.evaluate(hivPatientDataLibrary.getArtAppointmentStatus(), context));
        printData(patientDataService.evaluate(chronicCarePatientDataLibrary.getChronicCareAppointmentStatus(), context));
	}

    public void printData(PatientData data) throws Exception {
        AppointmentInfo ai = (AppointmentInfo) data.getData().get(pId);
        System.out.println("----------------");
        System.out.println("Enrolled: " + ai.isCurrentlyEnrolled());
        System.out.println("Last visit: " + ai.getLastEncounterDate());
        System.out.println("Next schedule: " + ai.getNextScheduledDate());
        System.out.println("Days to appointment: " + ai.getDaysToAppointment());
    }
}
