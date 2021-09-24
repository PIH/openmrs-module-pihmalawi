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
public class PdcDevelopmentalDelay extends VersionedPihConceptBundle {

    public static final String INCREASE_IN_MUSCLE_TONE = "4bdff377-bec0-444c-b3de-00dbe4ba4b45";
    public static final String DECREASE_IN_MUSCLE_TONE = "2a500878-8f72-4527-a165-339b4548e9d4";
    public static final String SIGNS_OF_CEREBRAL_PALSY = "06debeb7-4dcd-4eb0-a954-1d4a18760559";
    public static final String PHYSIOTHERAPY= "dcc54588-806f-4bb9-b281-25215b07053d";
    public static final String PLAY_THERAPY = "47729d48-1ab6-4c27-86fe-4700d734909d";
    public static final String REFERRED_OUT = "da88696b-bf1d-4a31-b49d-997326e4a777";
    public static final String DELAYS_IN_MOTOR_DEVT = "99c7a129-5d96-4825-b8ef-eff5fa6e7727";
    public static final String LARGE_TONGUE = "43740820-b125-47e0-91e4-d0f9f46eba32";
    public static final String COURSE_HAIR= "5db55bf0-5222-454b-8dab-7096276b857b";
    public static final String RESULT_NORMAL = "339ebc90-d4fd-4e57-a1b2-996403f44d57";
    public static final String SUSPECTED = "7667e58e-4332-4e83-bd85-0490bcb394fb";
    public static final String  DONE= "584fbc24-9eda-4db4-93d2-30b77067a5c6";
    public static final String  POOR_GROWTH= "977c1d68-6ffa-46d5-ae89-00404e8b5904";
    public static final String  DEVELOPMENTAL_DELAY_PLAN_SET= "57589345-27e4-4774-9897-ac5ba3412b14";
    public static final String  DEVELOPMENTAL_DELAY_ASSESSMENTS_SET= "2996699c-2d48-41d3-9c66-120ffbeb5104";
    public static final String  DEVELOPMENTAL_DELAY_POTENTIAL_CAUSES_SET= "a4dfe545-8a85-41ca-ac47-b372b681391f";
    public static final String  DEVELOPMENTAL_DELAY_TSH_TEST_SET= "d6039802-0da6-49ec-bd71-aaaf375673dd";


    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);


        // Physiotherapy
        Concept physiotherapy = install(new ConceptBuilder(PHYSIOTHERAPY)
                .datatype(coded)
                .conceptClass(question)
                .name("8aee437e-d4c4-4284-8888-caeb788afee1", "Physiotherapy",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

// Play therapy
        Concept playTherapy = install(new ConceptBuilder(PLAY_THERAPY)
                .datatype(coded)
                .conceptClass(question)
                .name("59bc36dc-eb9a-4f56-bafb-6802779fba07", "Play therapy",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

// Delaysinmotordevt
        Concept delaysInMotorDevt = install(new ConceptBuilder(DELAYS_IN_MOTOR_DEVT)
                .datatype(coded)
                .conceptClass(question)
                .name("03709a93-2878-4300-81c3-e4c11d80f623", "Delays in Motor Devt",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Large Tongue
        Concept largeTongue = install(new ConceptBuilder(LARGE_TONGUE)
                .datatype(coded)
                .conceptClass(question)
                .name("fe6205af-377e-4892-a52e-cdfe64047d38", "Large tongue",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Course Hair
        Concept courseHair = install(new ConceptBuilder(COURSE_HAIR)
                .datatype(coded)
                .conceptClass(question)
                .name("de514db4-df90-410f-abcc-5a542aea610b", "Course Hair",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Result Normal
        Concept resultNormal = install(new ConceptBuilder(RESULT_NORMAL)
                .datatype(coded)
                .conceptClass(question)
                .name("7761edc7-75d1-4b23-8eeb-c6c67bd0d9a5", "Result Normal",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Suspected
        Concept suspected = install(new ConceptBuilder(SUSPECTED)
                .datatype(coded)
                .conceptClass(question)
                .name("09e4b4f2-5025-4631-983e-30737a0f3234", "Suspected",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Increase in muscle tone
        Concept increaseInMuscleTone = install(new ConceptBuilder(INCREASE_IN_MUSCLE_TONE)
                .datatype(coded)
                .conceptClass(question)
                .name("665b8bde-18e1-47dd-a673-0dcfb7098384", "Increase in muscle tone",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Decrease in muscle tone
        Concept decreaseInMuscleTone = install(new ConceptBuilder(DECREASE_IN_MUSCLE_TONE)
                .datatype(coded)
                .conceptClass(question)
                .name("f39bd7d0-3cf5-4c7b-a89c-210ad3c2836c", "Decrease in muscle tone",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Referred out
        Concept referredOut = install(new ConceptBuilder(REFERRED_OUT)
                .datatype(coded)
                .conceptClass(question)
                .name("8bd1a603-b2ce-4a63-b5fe-6572bc73f4b3", "Referred out",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());
        //Signs of cerebral palsy
        Concept signsOfCerebralPalsy= install(new ConceptBuilder(SIGNS_OF_CEREBRAL_PALSY)
                .datatype(coded)
                .conceptClass(question)
                .name("8203a864-3afe-4da2-9874-d71293e2a14b", "Signs of Cerebral Palsy",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        //Done
        Concept doneDevDelay= install(new ConceptBuilder(DONE)
                .datatype(coded)
                .conceptClass(question)
                .name("351bb134-0aa5-4f38-9c1d-a77c49b45719", "Done",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        //Poor Growth
        Concept poorGrowth = install(new ConceptBuilder(POOR_GROWTH)
                .datatype(coded)
                .conceptClass(question)
                .name("374db2ca-c1a9-47c6-a0e9-3ec1c2962dd1", "Poor Growth",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        //Developmental Delay Assessments Set
        install(new ConceptBuilder(DEVELOPMENTAL_DELAY_ASSESSMENTS_SET)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("4d72e73f-adee-444f-b9d6-8efbe4ed963c", "Developmental Delay Assessment Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers( increaseInMuscleTone, decreaseInMuscleTone, delaysInMotorDevt,signsOfCerebralPalsy)
                .build());


        //Developmental Delay Plan Set
        install(new ConceptBuilder(DEVELOPMENTAL_DELAY_PLAN_SET)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("277b2115-c2ac-48ab-bc93-165be27f90dc", "Developmental Delay Plan Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers( physiotherapy, playTherapy, referredOut)
                .build());
        //Developmental Delay Potential causes Set
        install(new ConceptBuilder(DEVELOPMENTAL_DELAY_POTENTIAL_CAUSES_SET)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("d6efe5bc-4bc6-4b60-bb3f-422b94331312", "Developmental Delay Potential Causes Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(largeTongue, courseHair, poorGrowth)
                .build());

        //Developmental Delay TSH Test Set
        install(new ConceptBuilder(DEVELOPMENTAL_DELAY_TSH_TEST_SET)
                .datatype(notApplicable)

                .conceptClass(convSet)
                .name("1f61b143-ad1b-4c18-b802-3931a7d1c80f", "Developmental Delay TSH Test Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(largeTongue, courseHair, poorGrowth)
                .build());




    }
}
