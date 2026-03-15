package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihmalawi.metadata.Programs;
import org.springframework.stereotype.Component;

@Component
public class ProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(Programs.CHRONIC_CARE_PROGRAM);
    }
}
