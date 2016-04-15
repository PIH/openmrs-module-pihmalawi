package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeTypeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihmalawi.metadata.LocationAttributeTypes;
import org.openmrs.module.pihmalawi.metadata.LocationTags;
import org.springframework.stereotype.Component;

@Component
public class LocationAttributeTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(LocationAttributeTypes.LOCATION_CODE);
    }

    protected void install(LocationAttributeTypeDescriptor d) {
        install(CoreConstructors.locationAttributeType(d.name(), d.description(), d.datatype(), d.datatypeConfig(), d.minOccurs(), d.maxOccurs(), d.uuid()));
    }

}
