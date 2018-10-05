package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.Programs;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ProgramConcepts;
import org.springframework.stereotype.Component;

@Component
@Requires( {ProgramConcepts.class})
public class ProgramBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(Programs.CHRONIC_CARE_PROGRAM);
    }
}
