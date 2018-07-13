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
public class ChronicHeartFailureConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept raised = MetadataUtils.existing(Concept.class, "65779f0c-977f-11e1-8993-905e29aff6c1");
        Concept lower = MetadataUtils.existing(Concept.class, "6566289e-977f-11e1-8993-905e29aff6c1");
        Concept same = MetadataUtils.existing(Concept.class, "655e3b84-977f-11e1-8993-905e29aff6c1");
        Concept none = MetadataUtils.existing(Concept.class, "6557987e-977f-11e1-8993-905e29aff6c1");
        Concept weaklyPos = MetadataUtils.existing(Concept.class, "65598fb2-977f-11e1-8993-905e29aff6c1");
        Concept modPos = MetadataUtils.existing(Concept.class, "65597cde-977f-11e1-8993-905e29aff6c1");
        Concept strongPos = MetadataUtils.existing(Concept.class, "65598706-977f-11e1-8993-905e29aff6c1");
        Concept trace = MetadataUtils.existing(Concept.class, "6572cf72-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("3790bbe2-13b3-45b4-94d1-a9719f5ac51d")
                .datatype(coded)
                .conceptClass(question)
                .name("be9004d2-62ce-4ab0-9377-5d3e9a1a6adf", "Mental health referral", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("d69886ae-5eda-40ab-b2df-6ea9794a30d5")
                .datatype(coded)
                .conceptClass(question)
                .name("5dc235be-8435-45c8-b0a9-f63361c73725", "Palliative care referral", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("5f91e983-f825-4f41-81de-077288a5d860")
                .datatype(coded)
                .conceptClass(question)
                .name("d4271ac3-8806-4b10-b0e5-1e6c59ecdca2", "Took medications today", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("bd057502-c23b-4f80-b24c-b996e8a84dfc")
                .datatype(coded)
                .conceptClass(finding)
                .name("674f7ff9-274b-402b-827c-8c6fd599b28f", "Oedema", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(none, weaklyPos, modPos, strongPos, trace)
                .build());

        install(new ConceptBuilder("57476df7-e885-4444-b151-152cfcdac05b")
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("6abfb879-6bc7-4a4c-a3e0-1b38b00b80da", "Restrictive cardiomyopathy", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("e3ed7b54-5fcb-453f-9058-605f443bf78d")
                .datatype(coded)
                .conceptClass(finding)
                .name("30db9a58-4777-4b72-87ec-b850c663b040", "Bibasilar crackles", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());
       
        install(new ConceptBuilder("e513cb96-6782-4a2f-bc8e-e4f9ebaf0286")
                .datatype(coded)
                .conceptClass(question)
                .name("d7229b95-2056-4bb6-8570-c768326f6e59", "Salt or fluid restricted", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("95b1429a-34fd-4209-9cf9-20c66abc0348")
                .datatype(coded)
                .conceptClass(question)
                .name("0a83b2a7-454a-43ae-8fd6-22c5a54ab6c3", "Concern for depression or anxiety", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("dc277a10-f5dd-461c-b936-8bcdeb808b8b")
                .datatype(coded)
                .conceptClass(finding)
                .name("86e12ce2-c860-4c24-a989-bdf45bb8a44d", "Jugular venous pressure elevated", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("51f56e0e-0038-42fd-88d5-d0d7d47b56b6")
                .datatype(text)
                .conceptClass(drug)
                .name("4da6406b-19ca-4de4-b7c4-a2d68e419eb5", "Other chronic heart failure drugs", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("a02a2c07-521e-44e0-b83c-5b6a9302f8d6")
                .datatype(coded)
                .conceptClass(symptom)
                .name("e8bfaa00-2bab-4b09-9519-4d3849b8c5b2", "Dyspnea on extertion", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("b97edeb3-ab9d-4577-8b90-6dcd942aa8b3")
                .datatype(coded)
                .conceptClass(symptom)
                .name("6e416ebb-a09a-490e-97d9-a8f763a71f84", "Level of fatigue", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("d3e06bb8-97e2-40cf-a72b-3d9672ff6366")
                .datatype(coded)
                .conceptClass(symptom)
                .name("f336be70-b187-4960-8da2-1cace2437856", "Level of dry cough", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("91329a35-1f77-4cef-8870-1db5c5ceaf72")
                .datatype(coded)
                .conceptClass(symptom)
                .name("51050ac7-785b-4718-b638-46e4c6793232", "Level of orthopnea", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("f4674f9b-3eda-4b5c-b8ad-9f8532aba1a2")
                .datatype(coded)
                .conceptClass(symptom)
                .name("d9ea4ab6-bb4d-481a-94ac-8646f00c3d51", "Level of paroxysmal nocturnal dyspnea", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("1995a751-6f80-49f5-bc89-5cc6ae767eff")
                .datatype(text)
                .conceptClass(diagnosis)
                .name("f34edfb3-6f88-4c8a-8bae-c05c6d7047dd", "ECHO imaging result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

    }
}
