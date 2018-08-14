package org.openmrs.module.pihmalawi.metadata.deploy.bundle;


import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.PalliativeCareMetadata;
import org.openmrs.module.pihmalawi.metadata.PihMalawiPatientIdentifierTypes;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ChwManagementConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.IC3ScreeningConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.MasterCardConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.PalliativeCareConcepts;
import org.springframework.stereotype.Component;

@Component
@Requires( {PalliativeCareConcepts.class, MasterCardConcepts.class, ChwManagementConcepts.class, IC3ScreeningConcepts.class} )
public class PihMalawiMetadataBundle extends AbstractMetadataBundle{

    @Override
    public void install() throws Exception {
        install(PihMalawiPatientIdentifierTypes.PALLIATIVE_CARE_NUMBER);
        install(PalliativeCareMetadata.PALLIATIVE_CARE_PROGRAM);
    }
}
