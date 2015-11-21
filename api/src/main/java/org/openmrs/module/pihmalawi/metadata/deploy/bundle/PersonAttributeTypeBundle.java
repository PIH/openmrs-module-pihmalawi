package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.metadatadeploy.descriptor.PersonAttributeTypeDescriptor;
import org.openmrs.module.pihmalawi.metadata.PersonAttributeTypes;
import org.springframework.stereotype.Component;

@Component
public class PersonAttributeTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing PersonAttributeTypes");
        install(PersonAttributeTypes.TEST_PATIENT);
        install(PersonAttributeTypes.UNKNOWN_PATIENT);
    }

    //***** BUNDLE INSTALLATION METHODS FOR DESCRIPTORS

    protected void install(PersonAttributeTypeDescriptor d) {
        install(CoreConstructors.personAttributeType(d.name(), d.description(), d.format(), d.foreignKey(), d.searchable(), d.sortWeight(), d.uuid()));
    }
}
