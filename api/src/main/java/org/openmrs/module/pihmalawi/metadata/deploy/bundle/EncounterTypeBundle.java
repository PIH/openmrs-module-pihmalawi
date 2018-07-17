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

        // Palliative care
        install(EncounterTypes.PALLIATIVE_INITIAL);
        install(EncounterTypes.PALLIATIVE_FOLLOWUP);

        // IC3 Screening POC system
        install(EncounterTypes.BLOOD_PRESSURE_SCREENING);
        install(EncounterTypes.NUTRITION_SCREENING);
    }

    //***** BUNDLE INSTALLATION METHODS FOR DESCRIPTORS

    protected void install(EncounterTypeDescriptor d) {
        install(CoreConstructors.encounterType(d.name(), d.description(), d.uuid()));
    }
}
