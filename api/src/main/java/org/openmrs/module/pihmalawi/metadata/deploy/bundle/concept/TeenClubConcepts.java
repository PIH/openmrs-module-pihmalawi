package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class TeenClubConcepts extends VersionedPihConceptBundle {

    public static final String TEEN_CLUB_PROGRAM_STATUS_CONCEPT_UUID = "08304AC4-E618-43A0-8B6D-B6788EE7B780";
    public static final String TEEN_CLUB_PROGRAM_CONCEPT_UUID = "6BDEA14E-7B58-4AEA-97A7-167905B25B96";
    public static final String STI_REFERRAL_CONCEPT_UUID = "CF05E412-2707-4850-87B0-08E117B66009";
    public static final String STI_SCREENING_CONCEPT_UUID = "B5983F8D-7253-4A37-8B70-514B7AD17BD0";
    public static final String NUTRITION_REFERRAL_CONCEPT_UUID = "85F19F2E-EC7D-4F54-9C01-EB960AF2B315";

    @Override
    public int getVersion() {
        return 3;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept positive = MetadataUtils.existing(Concept.class, CommonMetadata.POSITIVE);
        Concept negative = MetadataUtils.existing(Concept.class, CommonMetadata.NEGATIVE);

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

        install(new ConceptBuilder(STI_REFERRAL_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("AE1BBBE2-0746-4D4A-A40B-607CB59A37AF", "STI referral",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());
        install(new ConceptBuilder(STI_SCREENING_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("EA372274-CB78-4B9C-B01F-631C0563C986", "STI screening outcome",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(positive,negative)
                .build());
        install(new ConceptBuilder(NUTRITION_REFERRAL_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("C225E8C6-E7E1-4D2A-9BC4-EBEE7DC88A5D", "Nutrition referral",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());
    }
}
