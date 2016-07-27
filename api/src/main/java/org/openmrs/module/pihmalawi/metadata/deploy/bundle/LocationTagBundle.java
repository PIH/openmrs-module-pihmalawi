package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihmalawi.metadata.LocationTags;
import org.springframework.stereotype.Component;

@Component
public class LocationTagBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(LocationTags.HIV_STATIC);
        install(LocationTags.HIV_OUTREACH);
        install(LocationTags.CHRONIC_CARE_LOCATION);
        install(LocationTags.UPPER_NENO);
        install(LocationTags.LOWER_NENO);
        install(LocationTags.LOGIN_LOCATION);
        install(LocationTags.VISIT_LOCATION);
        install(LocationTags.ADMISSION_LOCATION);
        install(LocationTags.TRANSFER_LOCATION);
        install(LocationTags.DISPENSING_LOCATION);
        install(LocationTags.TRACE_PHASE_1_LOCATION);
    }

    protected void install(LocationTagDescriptor d) {
        install(CoreConstructors.locationTag(d.name(), d.description(), d.uuid()));
    }

}
