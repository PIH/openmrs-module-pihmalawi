package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.metadatadeploy.bundle.PackageDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.pihmalawi.metadata.Locations;
import org.springframework.stereotype.Component;

@Component
public class LocationBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(Locations.TEDZANI_CLINIC_HC);
    }

    @Override
    protected void install(LocationDescriptor d) {
        install(CoreConstructors.location(d.name(), d.description(), d.uuid()));
    }
}
