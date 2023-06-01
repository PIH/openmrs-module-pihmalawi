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
public class TeenClubConcepts extends VersionedPihConceptBundle {

    public static final String TEEN_CLUB_PROGRAM_STATUS_CONCEPT_UUID = "08304AC4-E618-43A0-8B6D-B6788EE7B780";
    public static final String TEEN_CLUB_PROGRAM_CONCEPT_UUID = "6BDEA14E-7B58-4AEA-97A7-167905B25B96";

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {

        install(new ConceptBuilder(TEEN_CLUB_PROGRAM_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("F1EA3928-6EFA-4545-A367-FE4CAB06EB91", "Teen club program treatment status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("A227897C-9F42-4AAE-AB51-8BA8221A2ED1")
                        .type(sameAs).ensureTerm(pih, "Teen club program treatment status").build())
                .build());

        install(new ConceptBuilder(TEEN_CLUB_PROGRAM_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(program)
                .name("D36C586B-9290-42FB-BFB0-A0FDB4DCB93A", "Teen Club Program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
    }
}
