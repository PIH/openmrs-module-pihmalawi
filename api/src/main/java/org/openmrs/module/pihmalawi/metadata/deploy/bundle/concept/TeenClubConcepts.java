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
    public static final String NORMAL_NUTRITION_SCREENING_FOR_MUAC_CONCEPT_UUID = "0929A831-8B0D-4FEF-8738-A72F950A0566";
    public static final String MENTAL_HEALTH_SCREENED_CONCEPT_UUID = "0382C74D-22F9-4B74-9D8F-D8560B0F8514";
    public static final String MENTAL_HEALTH_REFERRAL_CONCEPT_UUID = "08DCD9CC-E729-450C-AC18-001D6C0149D5";
    public static final String MENTAL_HEALTH_REGISTERED_CONCEPT_UUID = "09454ECC-7B29-468B-9963-90867D4F09C6";
    public static final String TB_SCREENING_OUTCOME_CONCEPT_UUID = "73c40559-32AA-4609-A322-bde569196bd2";
    @Override
    public int getVersion() {
        return 8;
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
        install(new ConceptBuilder(NORMAL_NUTRITION_SCREENING_FOR_MUAC_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("88D9799D-7876-42EB-9099-1C75975EECAB", "Normal nutrition screening for MUAC",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());
        install(new ConceptBuilder(MENTAL_HEALTH_SCREENED_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("E09F8CE9-BE1D-4BB3-88B7-5829FCDC38D4", "Mental health screened",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());
        install(new ConceptBuilder(MENTAL_HEALTH_REFERRAL_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("D525C924-0169-4656-B483-223F906FC1F2", "If yes,Adolescent referred?",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());
        install(new ConceptBuilder(MENTAL_HEALTH_REGISTERED_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("ED1951C5-2C41-42DE-9E8F-F09CCE517167", "If yes,Adolescent registered?",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());
        install(new ConceptBuilder(TB_SCREENING_OUTCOME_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("6E576E9B-114F-466E-8A91-A74FC688D0C0", "TB screening outcome",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(positive,negative)
                .build());
    }
}
