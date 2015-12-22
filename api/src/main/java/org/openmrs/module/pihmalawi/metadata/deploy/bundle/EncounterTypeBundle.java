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

        // Hypertension and Diabetes
        install(EncounterTypes.HTN_DIABETES_INITIAL);
        install(EncounterTypes.HTN_DIABETES_FOLLOWUP);
        install(EncounterTypes.HTN_DIABETES_TESTS);
        install(EncounterTypes.HTN_DIABETES_HOSPITALIZATIONS);

        // ToDo: Chronic lung disease?  Reuse Asthma?
        install(EncounterTypes.ASTHMA_INITIAL);
        install(EncounterTypes.ASTHMA_FOLLOWUP);
    }

    //***** BUNDLE INSTALLATION METHODS FOR DESCRIPTORS

    protected void install(EncounterTypeDescriptor d) {
        install(CoreConstructors.encounterType(d.name(), d.description(), d.uuid()));
    }
}
