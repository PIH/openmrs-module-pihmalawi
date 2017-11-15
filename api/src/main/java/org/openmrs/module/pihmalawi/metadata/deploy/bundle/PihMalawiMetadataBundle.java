package org.openmrs.module.pihmalawi.metadata.deploy.bundle;


import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.PalliativeCareConcepts;
import org.springframework.stereotype.Component;

@Component
@Requires( {PalliativeCareConcepts.class} )
public class PihMalawiMetadataBundle extends AbstractMetadataBundle{

    @Override
    public void install() throws Exception {

    }
}
