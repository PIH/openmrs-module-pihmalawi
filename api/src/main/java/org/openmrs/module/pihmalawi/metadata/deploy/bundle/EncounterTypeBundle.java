package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;
import org.openmrs.module.pihmalawi.metadata.EncounterTypes;
import org.springframework.stereotype.Component;

@Component
public class EncounterTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing EncounterTypes");

        // New BP Screening
        install(EncounterTypes.ART_ANNUAL_SCREENING);

        // ART Initial + Followup
        install(EncounterTypes.ART_INITIAL);
        install(EncounterTypes.ART_FOLLOWUP);

        // Hypertension and Diabetes
        install(EncounterTypes.HTN_DIABETES_INITIAL);
        install(EncounterTypes.HTN_DIABETES_FOLLOWUP);
        install(EncounterTypes.HTN_DIABETES_TESTS);
        install(EncounterTypes.HTN_DIABETES_ANNUAL_TESTS);
        install(EncounterTypes.HTN_DIABETES_HOSPITALIZATIONS);

        // Chronic lung disease (Reuse Asthma encounter types)
        install(EncounterTypes.ASTHMA_INITIAL);
        install(EncounterTypes.ASTHMA_FOLLOWUP);
        install(EncounterTypes.ASTHMA_HOSPITALIZATION);
        install(EncounterTypes.ASTHMA_PEAKFLOW);

        // Epilepsy
        install(EncounterTypes.EPILEPSY_INITIAL);
        install(EncounterTypes.EPILEPSY_FOLLOWUP);

        // Mental Health
        install(EncounterTypes.MENTAL_HEALTH_INITIAL);
        install(EncounterTypes.MENTAL_HEALTH_FOLLOWUP);

        // Chronic Heart Disease
        install(EncounterTypes.CHF_INITIAL);
        install(EncounterTypes.CHF_FOLLOWUP);
        install(EncounterTypes.CHF_ANNUAL_SCREENING);
        install(EncounterTypes.CHF_QUARTERLY_HIV_SCREENING);
        install(EncounterTypes.CHF_HOSPITALIZATIONS);
        install(EncounterTypes.CHF_ECHOCARDIOGRAM);
        install(EncounterTypes.CHF_ELECTROCARDIOGRAPHIC);
        install(EncounterTypes.CHF_CHEST_XRAY);

        //Chronic Kidney Disease
        install(EncounterTypes.CKD_INITIAL);
        install(EncounterTypes.CKD_FOLLOWUP);
        install(EncounterTypes.CKD_ANNUAL_SCREENING);
        install(EncounterTypes.CKD_QUARTERLY_SCREENING);
        install(EncounterTypes.CKD_HOSPITALIZATIONS);
        install(EncounterTypes.CKD_IMAGING);

        //NCD Other
        install(EncounterTypes.NCD_OTHER_INITIAL);
        install(EncounterTypes.NCD_OTHER_FOLLOWUP);
        install(EncounterTypes.NCD_OTHER_QUARTERLY_LABS);
        install(EncounterTypes.NCD_OTHER_ANNUAL_LABS);
        install(EncounterTypes.NCD_OTHER_HOSPITAL);

        // Palliative care
        install(EncounterTypes.PALLIATIVE_INITIAL);
        install(EncounterTypes.PALLIATIVE_FOLLOWUP);

        // IC3 Screening POC system
        install(EncounterTypes.CHECK_IN);
        install(EncounterTypes.BLOOD_PRESSURE_SCREENING);
        install(EncounterTypes.BLOOD_SUGAR_SCREENING);
        install(EncounterTypes.LAB_STATION_SCREENING);
        install(EncounterTypes.NUTRITION_SCREENING);
        install(EncounterTypes.NURSE_EVALUATION);
        install(EncounterTypes.HTC_SCREENING);
        install(EncounterTypes.CERVICAL_CANCER_SCREENING);
        install(EncounterTypes.VIRAL_LOAD_SCREENING);
        install(EncounterTypes.DNA_PCR_SCREENING);
        install(EncounterTypes.ADHERENCE_COUNSELING);
        install(EncounterTypes.TB_SCREENING);
        install(EncounterTypes.TB_TEST_RESULTS);
        install(EncounterTypes.IC3_CLINICIAN_PLAN);


        // Trace
        install(EncounterTypes.TRACE_INITIAL);
        install(EncounterTypes.TRACE_FOLLOWUP);

        // PDC Encounters
        install(EncounterTypes.PDC_INITIAL);
        install(EncounterTypes.PDC_FOLLOWUP);
        install(EncounterTypes.PDC_TRISOMY21_INITIAL);
        install(EncounterTypes.PDC_TRISOMY21_FOLLOWUP);
        install(EncounterTypes.PDC_CLEFT_CLIP_PALLET_INITIAL);
        install(EncounterTypes.PDC_CLEFT_CLIP_PALLET_FOLLOWUP);
        install(EncounterTypes.PDC_DEVELOPMENTAL_DELAY_FOLLOWUP);
        install(EncounterTypes.PDC_DEVELOPMENTAL_DELAY_INITIAL);
        install(EncounterTypes.PDC_DEVELOPMENTAL_DELAY_FOLLOWUP);
        install(EncounterTypes.PDC_OTHER_DIAGNOSIS_INITIAL);
        install(EncounterTypes.PDC_OTHER_DIAGNOSIS_FOLLOWUP);
        install(EncounterTypes.PDC_HOSPITALIZATION_HISTORY);
        install(EncounterTypes.VISION_TEST);
        install(EncounterTypes.HEARING_TEST);
        install(EncounterTypes.RADIOLOGY_SCREENING);
        install(EncounterTypes.PDC_COMPLICATIONS);
        install(EncounterTypes.PDC_TRISOMY21_LAB_TESTS);
        install(EncounterTypes.PDC_HB_AND_OTHER_LAB_TESTS);
        install(EncounterTypes.HIE_AND_DEV_DELAY_LAB_TESTS);

        install(EncounterTypes.NUTRITION_INITIAL);
        install(EncounterTypes.NUTRITION_FOLLOWUP);

    }

    //***** BUNDLE INSTALLATION METHODS FOR DESCRIPTORS

    protected void install(EncounterTypeDescriptor d) {
        install(CoreConstructors.encounterType(d.name(), d.description(), d.uuid()));
    }
}
