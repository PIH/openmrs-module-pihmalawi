package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.AppointmentStatusDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.Date;

@SkipBaseSetup
public class AppointmentStatusDataDefinitionEvaluatorTest extends BaseMalawiTest {

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    ChronicCareMetadata chronicCareMetadata;

    @Autowired
    @Qualifier("reportingPatientDataService")
    PatientDataService patientDataService;

    Date asthmaEncDate = DateUtil.getDateTime(2010, 4, 5, 10, 15, 0, 0);
    Date beforeEnc = DateUtil.getDateTime(2010, 4, 1);
    Date beforeAppt = DateUtil.getDateTime(2010, 6, 15);
    Date afterAppt = DateUtil.getDateTime(2010, 9, 20);
    Date apptDate = DateUtil.getDateTime(2010, 9, 15);

    Date artEncDate = DateUtil.getDateTime(2010, 4, 5, 11, 30, 0, 0);

    @Test
    public void shouldRequireActiveState() throws Exception {
        EncounterType asthmaEncType = chronicCareMetadata.getAsthmaFollowupEncounterType();
        ProgramWorkflowState onTxState = ccMetadata.getChronicCareStatusOnTreatment();

        Patient p = createPatient().save();
        Encounter encWithApptObs = createEncounter(p, asthmaEncType, asthmaEncDate).save();
        createObs(encWithApptObs, hivMetadata.getAppointmentDateConcept(), apptDate).save();

        AppointmentStatusDataDefinition def = new AppointmentStatusDataDefinition();
        def.setActiveStates(Arrays.asList(onTxState));
        def.setEncounterType(asthmaEncType);
        def.setOnDate(beforeAppt);

        check(evaluate(p, def), new AppointmentInfo(beforeAppt, false, asthmaEncType.getName(), asthmaEncDate, apptDate));

        PatientProgram pp = createPatientProgram(p, chronicCareMetadata.getChronicCareProgram(), beforeEnc, null).save();
        createState(pp, onTxState, beforeEnc);

        check(evaluate(p, def), new AppointmentInfo(beforeAppt, true, asthmaEncType.getName(), asthmaEncDate, apptDate));
    }

    @Test
    public void shouldReturnDifferentInfoRelativeToEffectiveDate() throws Exception {
        EncounterType asthmaEncType = chronicCareMetadata.getAsthmaFollowupEncounterType();
        ProgramWorkflowState onTxState = ccMetadata.getChronicCareStatusOnTreatment();

        Patient p = createPatient().save();
        Encounter encWithApptObs = createEncounter(p, asthmaEncType, asthmaEncDate).save();
        createObs(encWithApptObs, hivMetadata.getAppointmentDateConcept(), apptDate).save();
        PatientProgram pp = createPatientProgram(p, chronicCareMetadata.getChronicCareProgram(), beforeEnc, null).save();
        createState(pp, onTxState, beforeEnc);

        AppointmentStatusDataDefinition def = new AppointmentStatusDataDefinition();
        def.setActiveStates(Arrays.asList(onTxState));
        def.setEncounterType(asthmaEncType);
        def.setOnDate(beforeEnc);
        check(evaluate(p, def), new AppointmentInfo(beforeEnc, true, asthmaEncType.getName(), null, null));
        def.setOnDate(beforeAppt);
        check(evaluate(p, def), new AppointmentInfo(beforeAppt, true, asthmaEncType.getName(), asthmaEncDate, apptDate));
        def.setOnDate(afterAppt);
        check(evaluate(p, def), new AppointmentInfo(afterAppt, true, asthmaEncType.getName(), asthmaEncDate, apptDate));
    }

    @Test
    public void shouldReturnApptDatesFromOtherEncountersOnSameDate() throws Exception {
        EncounterType asthmaEncType = chronicCareMetadata.getAsthmaFollowupEncounterType();
        EncounterType artEncType = hivMetadata.getArtFollowupEncounterType();
        ProgramWorkflowState onArvsState = hivMetadata.getOnArvsState();

        Patient p = createPatient().save();
        Encounter encWithApptObs = createEncounter(p, asthmaEncType, asthmaEncDate).save();
        createObs(encWithApptObs, hivMetadata.getAppointmentDateConcept(), apptDate).save();
        PatientProgram pp = createPatientProgram(p, hivMetadata.getHivProgram(), beforeEnc, null).save();
        createState(pp, onArvsState, beforeEnc);
        createEncounter(p, artEncType, artEncDate).save();

        AppointmentStatusDataDefinition def = new AppointmentStatusDataDefinition();
        def.setActiveStates(Arrays.asList(onArvsState));
        def.setEncounterType(artEncType);
        def.setOnDate(afterAppt);
        check(evaluate(p, def), new AppointmentInfo(afterAppt, true, artEncType.getName(), artEncDate, apptDate));
    }

    protected void check(AppointmentInfo expected, AppointmentInfo actual) {
        if (expected == null) {
            Assert.assertNull(actual);
        }
        else {
            Assert.assertEquals(expected, actual);
        }
    }

    protected AppointmentInfo evaluate(Patient p, AppointmentStatusDataDefinition def) throws EvaluationException {
        EvaluationContext context = new EvaluationContext();
        Cohort baseCohort = new Cohort();
        baseCohort.addMember(p.getId());
        context.setBaseCohort(baseCohort);
        PatientData data = patientDataService.evaluate(def, context);
        return (AppointmentInfo) data.getData().get(p.getId());
    }
}
