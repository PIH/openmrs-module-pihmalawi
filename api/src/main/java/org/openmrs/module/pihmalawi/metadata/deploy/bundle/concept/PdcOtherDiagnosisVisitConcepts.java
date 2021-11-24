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
public class PdcOtherDiagnosisVisitConcepts extends VersionedPihConceptBundle {

    public static final String  USED_MDAT= "a02e4855-b282-45ba-bf24-7e888cc18e6d";
    public static final String NORMAL_MDAT = "d34cf0b3-073c-4f4e-ac4c-1c1e8e4c9165";
    public static final String PERSONAL_CONCERNS = "ac3cd477-e8bb-4299-9bc7-482f0deec914";
    public static final String MUSCLES_CONCERNS = "af0d29c2-a22c-4334-a2f8-f61266d32f64";
    public static final String PROBLEM_SOLVING_CONCERNS = "6eb1d8df-af38-459a-a9e9-51065eb71e90";
    public static final String GROUP_SESSION_COUNSEL = "9bc7b853-15ab-4e81-aad4-06a8816346ab";
    public static final String NONE_CONCERNS = "895dc050-f490-417e-9163-a22a768f4cfc";
    public static final String POOR_SUCK_CONCERNS = "7e63f69b-b515-4cef-bd64-4a2056e1dc5d";
    public static final String REFS_TO_FEED_CONCERNS= "6019736c-9e55-49a2-8a37-7a2985fb03a5";
    public static final String LESS_FEED_CONCERNS = "d6dd0f8c-2a90-41d7-8e53-9b36f0a758ee";
    public static final String ANY_SINCE_LAST_VISIT_CONVULSIONS = "a2d8ba82-cbc5-4fbb-983a-48f46b1b2936";
    public static final String ANT_CONVULSANT = "5c0cda83-99c2-4d33-8e70-74d4b95b1265";
    public static final String ADJUST_DOSE_CONVULSIONS = "9ad80dac-4656-4315-8045-bb4ec92373f2";
    public static final String NOT_REQUIRED_CONVULSION = "e389e7c1-2025-40b4-9ce7-fbe13d3c841e";
    public static final String FOOD_PACKAGE_CONVULSIONS = "77d019b9-3f20-4af5-be8a-df9a7f075c51";
    public static final String TRANSPORT_CONVULSION = "1d091be5-3ad4-496b-b5d0-8b907a0a81fa";
    public static final String DEVELOPMENT_COUNSEL = "9d5991aa-01e7-4eed-b38a-b76615a6c3ae";
    public static final String DEVELOPMENT_CONCERNS = "2312fec9-f0a6-4b4c-ab3d-cc07fbb90821";
    public static final String FEEDING_CONCERNS = "e552ebb0-40ad-42a2-a2cd-f21f143a38d0";
    public static final String PDC_CONVULSIONS = "2f8744bd-3a88-4aaa-913f-26266fe4d41a";
    public static final String SOCIAL_SUPPORT = "0a437bf7-8a46-4204-b886-95b1bb16501e";
    public static final String COMPLICATIONS_SINCE_LAST_VISIT = "e384a58c-4a73-11ec-81d3-0242ac130003";
    public static final String COMMUNICATION_DEVELOPMENT = "3a39e086-4a6f-11ec-81d3-0242ac130003";
    public static final String DEV_COUNSEL = "e38498b2-4a73-11ec-81d3-0242ac130003";


    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);


        // Used
        Concept usedMdat = install(new ConceptBuilder(USED_MDAT)
                .datatype(coded)
                .conceptClass(question)
                .name("c7c66d53-ae50-4aaf-a589-0097de3aa49b", "Used",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Normal
        Concept normalMdat = install(new ConceptBuilder(NORMAL_MDAT)
                .datatype(coded)
                .conceptClass(question)
                .name("79325353-edfb-44ee-9b39-c4bfd0934fd1", "Normal(Generic)",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Personal
        Concept personalConcerns = install(new ConceptBuilder(PERSONAL_CONCERNS)
                .datatype(coded)
                .conceptClass(question)
                .name("fa216cb2-f3b5-44ea-aa8c-bbcbe952bcfc", "Personal",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Muscles
        Concept musclesConcerns = install(new ConceptBuilder(MUSCLES_CONCERNS)
                .datatype(coded)
                .conceptClass(question)
                .name("4b4ec893-033b-4791-a9b1-112dbcca7207", "Muscles",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Problem Solving
        Concept problemSolvingConcerns = install(new ConceptBuilder(PROBLEM_SOLVING_CONCERNS)
                .datatype(coded)
                .conceptClass(question)
                .name("6befbe8b-93e6-4188-aa21-596fc412a3ab", "Problem Solving",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Group Session
        Concept groupSessionCounsel = install(new ConceptBuilder(GROUP_SESSION_COUNSEL)
                .datatype(coded)
                .conceptClass(question)
                .name("17194f8b-5828-4b29-9d2b-847a0403008b", "Group Session",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // None Concerns
        Concept noneConcerns = install(new ConceptBuilder(NONE_CONCERNS)
                .datatype(coded)
                .conceptClass(question)
                .name("054cb4cd-32f4-4201-bcdc-4b95ef1aab3d", "None Concerns",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Poor Suck
        Concept poorSuckConcerns = install(new ConceptBuilder(POOR_SUCK_CONCERNS)
                .datatype(coded)
                .conceptClass(question)
                .name("0b977ddb-cb5f-4e04-b2d8-883a375ce4bd", "Poor Suck",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Refs to Feed
        Concept refsToFeedConcerns = install(new ConceptBuilder(REFS_TO_FEED_CONCERNS)
                .datatype(coded)
                .conceptClass(question)
                .name("5fb1cf3d-463e-4e1b-83d0-d86fa1998f49", "Refs to feed",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Less Feed
        Concept lessFeedConcerns = install(new ConceptBuilder(LESS_FEED_CONCERNS)
                .datatype(coded)
                .conceptClass(question)
                .name("9f0f91f7-b62c-4353-af86-3afb476df5d1", "Less feed",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());



        // Any since last visit
        Concept  anySinceLastVisit= install(new ConceptBuilder(ANY_SINCE_LAST_VISIT_CONVULSIONS)
                .datatype(coded)
                .conceptClass(question)
                .name("b4504070-7955-4a67-928b-05e4d2bebcff", "Any since last visit",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Anticonvulsant
        Concept antConvulsions = install(new ConceptBuilder(ANT_CONVULSANT)
                .datatype(coded)
                .conceptClass(question)
                .name("eb104dd7-9fbd-421c-88e3-7866eb6669c5", "Anticonvulsant",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Adjust dose
        Concept adjustDose = install(new ConceptBuilder(ADJUST_DOSE_CONVULSIONS)
                .datatype(coded)
                .conceptClass(question)
                .name("547dc1d1-e0fa-492d-9908-508bad000a27", "Adjust dose",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Not required
        Concept notRequiredC = install(new ConceptBuilder(NOT_REQUIRED_CONVULSION)
                .datatype(coded)
                .conceptClass(question)
                .name("ea90c2b5-6773-42f0-a21d-024fb82afb6f", "Not required",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Food package
        Concept foodPackage = install(new ConceptBuilder(FOOD_PACKAGE_CONVULSIONS)
                .datatype(coded)
                .conceptClass(question)
                .name("1d5f41b3-a889-4cb1-ad7a-0f31af630b87", "Food Package",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Transport
        Concept  transportConvulsion= install(new ConceptBuilder(TRANSPORT_CONVULSION)
                .datatype(coded)
                .conceptClass(question)
                .name("3afe27ad-cc39-4eac-a56c-8eb8dfb82c86", "Transportc",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        //Developmental Concerns
        install(new ConceptBuilder(DEVELOPMENT_CONCERNS)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("2ca39698-ed1c-4c89-abf7-49160c019684", "Developmental Concerns Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(personalConcerns, musclesConcerns, problemSolvingConcerns)
                .build());

        //Developmental Counsel
        install(new ConceptBuilder(DEVELOPMENT_COUNSEL)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("e96202a8-cc8d-450c-96ad-4026ae7002da", "Development Counsel Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(groupSessionCounsel)
                .build());

        //Feeding Concerns
        install(new ConceptBuilder(FEEDING_CONCERNS)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("f3bcfb1e-68a3-4328-9840-8d538bf9064a", "Feeding Concerns", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(noneConcerns, poorSuckConcerns, refsToFeedConcerns, lessFeedConcerns)
                .build());

        //Convulsions
        install(new ConceptBuilder(PDC_CONVULSIONS)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("0eed28db-f9a4-4504-80c5-c81f8f3e61a4", "PDC Convulsions", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(anySinceLastVisit, antConvulsions, adjustDose)
                .build());

        //Social Support
        install(new ConceptBuilder(SOCIAL_SUPPORT)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("fc926e16-459f-4977-a174-7af9ea851d7f", "PDC Social Support", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(notRequiredC, foodPackage, transportConvulsion)
                .build());

        // Complications Since Last Visit
        Concept complications_since_last_visit = install(new ConceptBuilder(COMPLICATIONS_SINCE_LAST_VISIT)
                .datatype(coded)
                .conceptClass(question)
                .name("c21e5a6a-4a68-11ec-81d3-0242ac130003", "Complications since last visit",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Communication Development
        Concept commDev= install(new ConceptBuilder(COMMUNICATION_DEVELOPMENT)
                .datatype(coded)
                .conceptClass(question)
                .name("3a39de74-4a6f-11ec-81d3-0242ac130003", "Communication Development",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Development Counsel`
        Concept devCounsel= install(new ConceptBuilder(DEV_COUNSEL)
                .datatype(coded)
                .conceptClass(question)
                .name("e38497e0-4a73-11ec-81d3-0242ac130003", "Development Counsel",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());


    }

}
