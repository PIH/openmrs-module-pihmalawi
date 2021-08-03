package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihmalawi.metadata.PihMalawiPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class PatientIdentifierBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(PihMalawiPatientIdentifierTypes.PALLIATIVE_CARE_NUMBER);
        install(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER);
        install(PihMalawiPatientIdentifierTypes.YENDANAFE_IDENTIFIER);
        install(PihMalawiPatientIdentifierTypes.IC3D_IDENTIFIER);
        install(PihMalawiPatientIdentifierTypes.PDC_IDENTIFIER);
    }
}
