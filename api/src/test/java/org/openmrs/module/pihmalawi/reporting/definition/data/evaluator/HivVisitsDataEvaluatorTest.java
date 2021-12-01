package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.BaseMalawiTest;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.IC3ScreeningMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterIdDataDefinition;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.DataSetRowList;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class HivVisitsDataEvaluatorTest extends BaseMalawiTest {

    public static final String NENO_GENE_XPERT_LAB = "e08214c0-955d-11e7-abc4-cec278b6b50a";
    public static final String CONTAMINATED_RESULT = "65597a5e-977f-11e1-8993-905e29aff6c1";

    @Autowired
    HivMetadata hivMetadata;

    @Autowired
    private IC3ScreeningMetadata screeningMetadata;

    @Autowired
    private BuiltInEncounterDataLibrary builtInEncounterData;

    @Autowired
    BaseEncounterDataLibrary baseEncounterData;

    protected Encounter createVisitEncounter(Patient patient, EncounterType encounterType, Concept regimen, Number height, Number weight, Number systolicBP, Number diastolicBP, Date nextAppointmentDate, Date followupDate) {
        Encounter e = createEncounter(patient, encounterType, followupDate).save();
        if (regimen != null) {
            createObs(e, hivMetadata.getArvDrugsReceivedConcept(), regimen).save();
        }
        if (height !=null) {
            createObs(e, hivMetadata.getHeightConcept(), height).save();
        }
        if (weight != null) {
            createObs(e, hivMetadata.getWeightConcept(), weight).save();
        }
        if (systolicBP != null) {
            createObs(e, hivMetadata.getSystolicBloodPressureConcept(), systolicBP).save();
        }
        if (diastolicBP != null) {
            createObs(e, hivMetadata.getDiastolicBloodPressureConcept(), diastolicBP).save();
        }
        if (nextAppointmentDate != null) {
            createObs(e, hivMetadata.getAppointmentDateConcept(), nextAppointmentDate).save();
        }

        return e;
    }

    protected Encounter createVLTestEncounter(
            Patient patient,
            EncounterType encounterType,
            Concept reasonForTesting,
            Concept labLocation,
            Concept bled,
            Number vlResult,
            Number lessThanLimit,
            Concept ldl,
            Concept reasonForNoSample,
            Date encounterDate) {

        Encounter e = createEncounter(patient, encounterType, encounterDate).save();

        if (reasonForTesting != null) {
            createObs(e, hivMetadata.getReasonForTestingConcept(), reasonForTesting).save();
        }
        if (labLocation != null) {
            createObs(e, hivMetadata.getConcept(hivMetadata.LAB_LOCATION), labLocation).save();
        }
        if (bled != null) {
            createObs(e, hivMetadata.getConcept(hivMetadata.HIV_VIRAL_LOAD_SPECIMEN_COLLECTED), bled).save();
        }
        if (vlResult != null) {
            createObs(e, hivMetadata.getHivViralLoadConcept(), vlResult).save();
        }
        if (lessThanLimit != null) {
            createObs(e, hivMetadata.getHivLessThanViralLoadConcept(), lessThanLimit).save();
        }
        if (ldl != null) {
            createObs(e, hivMetadata.getHivLDLConcept(), ldl).save();
        }
        if (reasonForNoSample != null) {
            createObs(e, hivMetadata.getReasonNoResultConcept(), reasonForNoSample).save();
        }

        return e;
    }

    @Test
    public void shouldTestHivVisitsHistory() throws Exception {

        EncounterType artFollowupEncounterType = hivMetadata.getArtFollowupEncounterType();
        EncounterType nutritionScreeningEncounterType = screeningMetadata.getNutritionScreeningEncounterType();
        EncounterType bloodPressureScreeningEncounterType = screeningMetadata.getBloodPressureScreeningEncounterType();
        EncounterType clinicianScreeningEncounterType = screeningMetadata.getClinicianScreeningEncounterType();
        EncounterType vlScreeningEncounterType = screeningMetadata.getVLScreeningEncounterType();

        EncounterEvaluationContext context = new EncounterEvaluationContext();

        Cohort baseCohort = new Cohort();
        Patient testPatient = createPatient().save();
        baseCohort.addMember(testPatient.getPatientId());
        context.setBaseCohort(baseCohort);

        Date d1 = DateUtil.getDateTime(2010, 4, 5);
        Concept reg1 = hivMetadata.getArvRegimen2aConcept();
        Encounter artInitial = createEncounter(testPatient, hivMetadata.getArtInitialEncounterType(), d1).save();
        Obs regChange1 = createObs(artInitial, hivMetadata.getArvDrugsChange1Concept(), reg1).save();
        Obs regDate1 = createObs(artInitial, hivMetadata.getDateOfStartingFirstLineArvsConcept(), d1).save();

        Date d2 = DateUtil.getDateTime(2013, 10, 7);
        Concept reg2 = hivMetadata.getArvRegimen4aConcept();
        Encounter e1 = createVisitEncounter(testPatient, artFollowupEncounterType, reg2, null, null, null, null, DateUtil.getDateTime(2013, 11, 7), d2);
        Encounter e1_nutrition = createVisitEncounter(testPatient, nutritionScreeningEncounterType, null, 169, 60, null, null, null, d2);
        Encounter e1_bp = createVisitEncounter(testPatient, bloodPressureScreeningEncounterType, null, null, null, 160, 60, null, d2);

        Date d3 = DateUtil.getDateTime(2017, 11, 29);
        Encounter e2 = createVisitEncounter(testPatient, artFollowupEncounterType, reg2, 170, 63, null, null, DateUtil.getDateTime(2017, 12, 28), d3);

        Date d4 = DateUtil.getDateTime(2018, 2, 10);
        Encounter e3 = createVisitEncounter(testPatient, artFollowupEncounterType, reg2, 170, 62, null, null, DateUtil.getDateTime(2018, 3, 10), d4);

        Date d5 = DateUtil.getDateTime(2018, 6, 17);
        Concept reg3 = hivMetadata.getArvRegimen6aConcept();
        Encounter e4 = createVisitEncounter(testPatient, artFollowupEncounterType, reg3, 170, 65, null, null, DateUtil.getDateTime(2018, 7, 17), d5);

        Date d6 = DateUtil.getDateTime(2018, 8, 11);
        Date nextAppointmentDate = DateUtil.getDateTime(2018, 9, 11);
        Encounter e5 = createVisitEncounter(testPatient, artFollowupEncounterType, reg3, null, null, null, null, null, d6);
        Encounter e5_nutrition = createVisitEncounter(testPatient, nutritionScreeningEncounterType, null, 170, 66, null, null, null, d6);
        Encounter e5_bp = createVisitEncounter(testPatient, bloodPressureScreeningEncounterType, null, null, null, 158, 47, null, d6);
        Encounter e5_clinician_plan = createVisitEncounter(testPatient, clinicianScreeningEncounterType, null, null, null, null, null, nextAppointmentDate, d6);

        createVLTestEncounter(
                testPatient,
                vlScreeningEncounterType,
                hivMetadata.getRoutineConcept(),
                hivMetadata.getConcept(NENO_GENE_XPERT_LAB),
                hivMetadata.getTrueConcept(),
                null,
                321,
                hivMetadata.getTrueConcept(),
                hivMetadata.getConcept(CONTAMINATED_RESULT),
                d6);

        EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();

        dsd.addColumn("ENCOUNTER_ID", new EncounterIdDataDefinition(), null);	// Test a basic encounter data item
        dsd.addColumn("ENCOUNTER_TYPE", builtInEncounterData.getEncounterTypeName(), null);
        dsd.addColumn("EMR_ID", new PatientIdDataDefinition(), null); 			// Test a basic patient data item
        dsd.addColumn("BIRTHDATE", new BirthdateDataDefinition(), null); 		// Test a basic person data item
        dsd.addColumn("ENCOUNTER_DATE", new EncounterDatetimeDataDefinition(), null, new DateConverter("dd/MMM/yyyy"));  // Test a column with a converter

        EncounterDataDefinition nextAppointmentDateDefinition = baseEncounterData.getNextAppointmentDateObsReferenceValue();
        dsd.addColumn("IC3_NEXT_APPOINTMENT_DATE", nextAppointmentDateDefinition, ObjectUtil.toString(Mapped.straightThroughMappings(nextAppointmentDateDefinition), "=", ","));

        EncounterDataDefinition weightObsDef = baseEncounterData.getWeightObsReferenceValue();
        dsd.addColumn("IC3_WEIGHT", weightObsDef, ObjectUtil.toString(Mapped.straightThroughMappings(weightObsDef), "=", ","));
        dsd.addColumn("IC3_SYSTOLIC_BP", baseEncounterData.getSystolicBPObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getSystolicBPObsReferenceValue()), "=", ","));
        dsd.addColumn("IC3_DIASTOLIC_BP", baseEncounterData.getDiastolicBPObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getDiastolicBPObsReferenceValue()), "=", ","));


        dsd.addColumn("VL_REASON_FOR_TESTING", baseEncounterData.getReasonForTestObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getReasonForTestObsReferenceValue()), "=", ","));
        dsd.addColumn("VL_LAB_LOCATION", baseEncounterData.getLabLocationObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getLabLocationObsReferenceValue()), "=", ","));
        dsd.addColumn("VL_BLED", baseEncounterData.getBledObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getBledObsReferenceValue()), "=", ","));
        dsd.addColumn("VL_RESULT", baseEncounterData.getVLResultObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getVLResultObsReferenceValue()), "=", ","));
        dsd.addColumn("VL_LESS_THAN_LIMIT", baseEncounterData.getVLLessThanLimitObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getVLLessThanLimitObsReferenceValue()), "=", ","));
        dsd.addColumn("VL_LDL", baseEncounterData.getLdlObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getLdlObsReferenceValue()), "=", ","));
        dsd.addColumn("VL_REASON_FOR_NO_RESULT", baseEncounterData.getReasonNoResultObsReferenceValue(), ObjectUtil.toString(Mapped.straightThroughMappings(baseEncounterData.getReasonNoResultObsReferenceValue()), "=", ","));

        context.setBaseEncounters(new EncounterIdSet(e1.getId(), e2.getId(), e3.getId(), e4.getId(), e5.getId() ));

        DataSet dataset = Context.getService(DataSetDefinitionService.class).evaluate(dsd, context);
        DataSetRowList rows = ((SimpleDataSet) dataset).getRows();
        Assert.assertEquals(rows.size(), context.getBaseEncounters().getSize());

        DataSetRow rowByField = getRowByField(rows, "ENCOUNTER_ID", e5.getId().toString());
        Assert.assertNotNull(rowByField);
        // retrieves the Weight Obs entered via the Nutrition encounter which was not part of the BaseEncounters set
        Assert.assertEquals( Double.compare((Double) rowByField.getColumnValue("IC3_WEIGHT"), 66), 0);

        // retrieves the Systolic BP Obs entered via the IC3 BP encounter which was not part of the BaseEncounters set
        Assert.assertEquals( Double.compare((Double) rowByField.getColumnValue("IC3_SYSTOLIC_BP"), 158), 0);

        Assert.assertEquals( rowByField.getColumnValue("IC3_NEXT_APPOINTMENT_DATE"), nextAppointmentDate );

    }

    DataSetRow getRowByField(DataSetRowList rows, String fieldName, String fieldValue) {
        DataSetRow row = null;

        if (rows != null && rows.size() > 0 ) {
            for (DataSetRow dataSetRow : rows) {
                Object encounter_id = dataSetRow.getColumnValue(fieldName);
                if (StringUtils.equals(encounter_id.toString(), fieldValue)) {
                    return dataSetRow;
                }
            }
        }
        return row;
    }
}
