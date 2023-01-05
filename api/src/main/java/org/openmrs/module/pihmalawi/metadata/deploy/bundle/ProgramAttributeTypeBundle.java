package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.springframework.stereotype.Component;

import static org.openmrs.module.pihmalawi.metadata.deploy.PihConstructors.programAttributeType;

@Component
public class ProgramAttributeTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        install(programAttributeType(
                "Transferred out location",
                "Transferred out facility location",
                "org.openmrs.customdatatype.datatype.FreeTextDatatype",
                "Health Facility",
                1,
                1,
                PihMalawiConstants.TRANSFERRED_OUT_PROGRAM_ATTRIBUTE_TYPE));
    }
}
