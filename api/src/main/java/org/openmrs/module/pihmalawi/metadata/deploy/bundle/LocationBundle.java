package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihmalawi.metadata.*;
import org.springframework.stereotype.Component;

@Component
public class LocationBundle extends AbstractMetadataBundle {
    @Override
    public void install() throws Exception {
        install(Locations.BINJE_OUTREACH_CLINIC);
        install(Locations.FELEMU_OUTREACH_CLINIC);
        install(Locations.KASAMBA_OUTREACH_CLINIC);
    }
}
