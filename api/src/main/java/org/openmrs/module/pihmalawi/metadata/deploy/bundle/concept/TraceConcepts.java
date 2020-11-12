package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;


import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class TraceConcepts extends VersionedPihConceptBundle {


    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {


        install(new ConceptBuilder("C0730C1B-E6D3-43DD-A11B-99E9D22C5048")
                .datatype(text)
                .conceptClass(question)
                .name("D16240B6-25E4-4A97-86E8-4C65BD3C094D", "Tracker", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("60A8AD9D-9A59-45D2-BD3D-4830EC02782E")
                .datatype(date)
                .conceptClass(question)
                .name("2934DEDA-7FCB-4785-86F9-C11DFCED3C23", "Date of Next Tracking Attempt", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
    }
}
