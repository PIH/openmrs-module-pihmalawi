package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class AdvancedNCDMastercardConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

        //Next appointment location
        Concept ic3 = MetadataUtils.existing(Concept.class, "3064BCF8-56F7-43C9-A8CF-D90D42EEF739");

        Concept advancedNCDclinic = install(new ConceptBuilder("2bc82b28-e84f-11e8-9f32-f2801f1b9fd1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2bc82dbc-e84f-11e8-9f32-f2801f1b9fd1", "Advanced NCD clinic", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("f68aeeea-e84f-11e8-9f32-f2801f1b9fd1", "IC3 Visit Location ", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder("9f262c0e-e850-11e8-9f32-f2801f1b9fd1")
                .datatype(coded)
                .conceptClass(question)
                .name("9f262ec0-e850-11e8-9f32-f2801f1b9fd1", "Next appointment location", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        ic3,
                        advancedNCDclinic)
                .build());

        //Medications changed?
        Concept nevirapineIncreased = MetadataUtils.existing(Concept.class, "655e7e28-977f-11e1-8993-905e29aff6c1");
        Concept otherNonCodedChange = MetadataUtils.existing(Concept.class, "656cce7e-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("655e8076-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("65fb9186-977f-11e1-8993-905e29aff6c1", "Has the treatment changed at this visit?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        nevirapineIncreased,
                        otherNonCodedChange,
                        yes,
                        no)
                .build());
    }
}
