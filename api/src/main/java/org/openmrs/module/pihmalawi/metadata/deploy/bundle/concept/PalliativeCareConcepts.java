package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class PalliativeCareConcepts extends VersionedPihConceptBundle{

    public static final String PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID = "14ce86d7-025e-4dcf-9437-67d26185c6ab";


    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {

        install(new ConceptBuilder(PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(program)
                .name("5ecb5fb3-8b87-4c1e-99ac-e2e0be21d4dd", "Palliative Care Program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("66fb0c94-0498-11e6-9622-0010f345ca38")
                        .type(sameAs).ensureTerm(pih, "8415").build())
                .build());
    }
}
