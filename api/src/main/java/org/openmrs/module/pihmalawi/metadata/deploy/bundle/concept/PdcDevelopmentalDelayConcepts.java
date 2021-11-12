package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;
@Component
@Requires({CoreConceptMetadataBundle.class})
public class PdcDevelopmentalDelayConcepts extends VersionedPihConceptBundle {

    public static final String HYPO = "4bdff377-bec0-444c-b3de-00dbe4ba4b45";
    public static final String HYPER = "2a500878-8f72-4527-a165-339b4548e9d4";
    public static final String SIGNS_OF_CEREBRAL_PALSY = "06debeb7-4dcd-4eb0-a954-1d4a18760559";
    public static final String PHYSIOTHERAPY= "dcc54588-806f-4bb9-b281-25215b07053d";
    public static final String PLAY_THERAPY = "47729d48-1ab6-4c27-86fe-4700d734909d";
    public static final String REFERRED_OUT = "da88696b-bf1d-4a31-b49d-997326e4a777";
    public static final String MDAT_SUMMARY_CODED = "99c7a129-5d96-4825-b8ef-eff5fa6e7727";
    public static String CONTINUE_FOLLOWUP = "696c6ade-1f06-40d1-aadd-4c2b257167db";
    public static final String INEQUALITIES = "db194157-db69-41b2-b661-96ed124b0466";
    public static final String COURSE_HAIR= "5db55bf0-5222-454b-8dab-7096276b857b";
    public static final String IRRITABILITY = "339ebc90-d4fd-4e57-a1b2-996403f44d57";
    public static final String SUSPECTED = "7667e58e-4332-4e83-bd85-0490bcb394fb";
    public static final String  DONE= "584fbc24-9eda-4db4-93d2-30b77067a5c6";
    public static final String REFERRED_TO_POSER_SUPPORT = "977c1d68-6ffa-46d5-ae89-00404e8b5904";
    public static final String  DEVELOPMENTAL_DELAY_PLAN_SET= "57589345-27e4-4774-9897-ac5ba3412b14";
    public static final String  DEVELOPMENTAL_DELAY_ASSESSMENTS_SET= "2996699c-2d48-41d3-9c66-120ffbeb5104";
    public static final String REFERRED_OUT_SET = "a4dfe545-8a85-41ca-ac47-b372b681391f";
    public static final String  DEVELOPMENTAL_DELAY_TSH_TEST_SET= "d6039802-0da6-49ec-bd71-aaaf375673dd";
    public static final String TONE_SET ="26DDB016-AE2F-43ED-A177-F0C26DD40392";
    public static final String FEEDING_SET = "5af3740e-c1a1-4e86-a87e-280817bbde0b";
    public static final String NORMAL = "d34cf0b3-073c-4f4e-ac4c-1c1e8e4c9165";
    public static final String CUP = "4f3e0805-3059-4226-abcd-6100cddd9543";
    public static final String BREAST_FEEDING = "657a289e-977f-11e1-8993-905e29aff6c1";
    public static final String OGT = "fe7d2795-3cfe-47fa-933c-cb8e9b1c42d5";
    public static final String MALNUTRITION = "657a2268-977f-11e1-8993-905e29aff6c1";
    public static final String WEIGHT = "6569c44a-977f-11e1-8993-905e29aff6c1";
    public static final String HEIGHT = "6569c562-977f-11e1-8993-905e29aff6c1";
    public static final String MUAC = "6558d09a-977f-11e1-8993-905e29aff6c1";
    public static final String OTHER_NONE_CODED_TEXT="d57e3a20-5802-11e6-8b77-86f30ca893d3";
    public static final String MDAT_SUMMARY_SET = "f2848791-263a-44bf-90b3-16cf3f302dc1";
    public static final String LESS_THAN = "ac19c969-94d9-4bf5-b4af-d23f8f8bbd07";
    public static final String LESS_THAN_OR_EQUAL_TO = "d3c7729a-1e9e-4b27-80ac-9f8a60432a36";
    public static final String GREATER_THAN = "0f2543b6-be7a-4253-8082-2aebec5eced2";
    public static final String GREATER_THAN_OR_EQUAL_TO = "8eaf255f-b7f6-44ec-b39a-c856ddcbe109";
    public static final String NUMERICAL_VALUE = "7ba4b6f9-f99e-4c1f-9beb-a6ccaf04a8ca";
    public static final String WEIGHT_AGAINST_AGE = "e3d43e73-c831-421a-bf77-7e35bd11678a";
    public static final String WEIGHT_AGAINST_HEIGHT = "eaf64a61-4526-47b3-b8e2-be402c8568f2";
    public static final String GROUP_COUNSELLING = "cef3471e-eb0a-4b14-b402-9b24742e3869";


    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);
        Concept normal = MetadataUtils.existing(Concept.class,NORMAL);
        Concept cup = MetadataUtils.existing(Concept.class,CUP);
        Concept breastFeeding = MetadataUtils.existing(Concept.class,BREAST_FEEDING);
        Concept ogt = MetadataUtils.existing(Concept.class,OGT);
        Concept malnutrition = MetadataUtils.existing(Concept.class,MALNUTRITION);
        Concept weight = MetadataUtils.existing(Concept.class,WEIGHT);
        Concept height = MetadataUtils.existing(Concept.class,HEIGHT);
        Concept muac = MetadataUtils.existing(Concept.class,MUAC);
        Concept otherNoneCodedText = MetadataUtils.existing(Concept.class,OTHER_NONE_CODED_TEXT);
        Concept groupCounselling = MetadataUtils.existing(Concept.class,GROUP_COUNSELLING);
        Concept continueFollowUp = MetadataUtils.existing(Concept.class,CONTINUE_FOLLOWUP);

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

        // MDAT Summary
        Concept mdatSummary = install(new ConceptBuilder(MDAT_SUMMARY_CODED)
                .datatype(coded)
                .conceptClass(question)
                .name("03709a93-2878-4300-81c3-e4c11d80f623", "Malawi Developmental Assessment Tool Summary (Normal)-(Coded)",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("89370a76-e05e-4bad-b99c-a0f005b5eb0f", "MDAT Summary (Normal)-(Coded)",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(yes,no)
                .build());

        // MDAT Summary Set
        Concept mdatSummarySet = install(new ConceptBuilder(MDAT_SUMMARY_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("547d89f1-ddfb-4b08-94d3-88c7b2620c00", "Malawi Developmental Assessment Tool Summary (Normal) Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .name("27a24850-5d08-4954-adc4-788b12bb47d7", "MDAT Summary (Normal) Set",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .setMembers(mdatSummary,otherNoneCodedText)
                .build());

        // Less than
        Concept lessThan = install(new ConceptBuilder(LESS_THAN)
                .datatype(notApplicable)
                .conceptClass(question)
                .name("333374d0-2d50-4979-885e-b11fa8ba7519", "Less Than",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("29575ecb-1e75-4149-80eb-b21392b468c0", "<",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(yes,no,unknown)
                .build());

        // Less than or equal to
        Concept lessThanOrEqualTo = install(new ConceptBuilder(LESS_THAN_OR_EQUAL_TO)
                .datatype(notApplicable)
                .conceptClass(question)
                .name("f171ed93-3c1e-4545-817a-c54c597878ac", "Less Than or equal to",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("0e4185db-8602-4c9d-8750-8f72bc42e331", "≤",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(yes,no,unknown)
                .build());

        // Greater than
        Concept greaterThan = install(new ConceptBuilder(GREATER_THAN)
                .datatype(notApplicable)
                .conceptClass(question)
                .name("705ebc5f-532d-49e2-8929-51e187d4c018", "Greater Than",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("7c897fcb-ccd4-4fc2-b43a-7e970bafbd99", ">",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(yes,no,unknown)
                .build());

        // Greater than or equal to
        Concept greaterThanOrEqualTo = install(new ConceptBuilder(GREATER_THAN_OR_EQUAL_TO)
                .datatype(notApplicable)
                .conceptClass(question)
                .name("19113852-a63e-412e-a0e1-fc9213c24b72", "Greater Than or equal to",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("72575fc4-244d-4db8-9867-794483586361", "≥",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(yes,no,unknown)
                .build());

        // Inequalities
        Concept inequalities = install(new ConceptBuilder(INEQUALITIES)
                .datatype(coded)
                .conceptClass(question)
                .name("cd9b0f07-e7ab-4890-a840-a1b68588a76b", "Inequalities / Angle Brackets",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("01d69333-b543-46fa-b8af-dd007261c1e7", "Inequalities",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(lessThan,lessThanOrEqualTo,greaterThan,greaterThanOrEqualTo)
                .build());

        // Numeric Value
        Concept numericValue  = install(new ConceptNumericBuilder(NUMERICAL_VALUE)
                .datatype(numeric)
                .conceptClass(test)
                .name("6af87e11-c056-4f8d-a575-b639b8f02eb8", "Numeric value or result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("eda8c4ad-d35c-4678-8209-5a38b10ade8d", "Number",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .description("1781cea7-9cf0-4b12-81c5-c57ea43ff7c7", "Any numeric value", Locale.ENGLISH)
                .build());

        // Weight against Age
        Concept weightAgainstAge = install(new ConceptBuilder(WEIGHT_AGAINST_AGE)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("1e359f47-72b7-4610-86fa-8cf57ddaaeae", "Weight against age", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .name("c968b078-6ffc-446e-830b-4875df129ef2", "W/A", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(numericValue,inequalities)
                .build());

        // Weight against Height
        Concept heightAgainstAge = install(new ConceptBuilder(WEIGHT_AGAINST_HEIGHT)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("f3473434-8765-4887-807f-7ec8cdd8cb5a", "Weight against Height", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .name("ad6f5a77-00d3-4eb2-b1d3-3def71645ba3", "W/H", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(numericValue,inequalities)
                .build());

        // Course Hair
        Concept courseHair = install(new ConceptBuilder(COURSE_HAIR)
                .datatype(coded)
                .conceptClass(question)
                .name("de514db4-df90-410f-abcc-5a542aea610b", "Course Hair",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Irritability
        Concept irritability = install(new ConceptBuilder(IRRITABILITY)
                .datatype(coded)
                .conceptClass(question)
                .name("7761edc7-75d1-4b23-8eeb-c6c67bd0d9a5", "Irritability (coded)",
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

        // Hypo
        Concept hypo = install(new ConceptBuilder(HYPO)
                .datatype(coded)
                .conceptClass(question)
                .name("665b8bde-18e1-47dd-a673-0dcfb7098384", "Hypo",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Hyper
        Concept hyper = install(new ConceptBuilder(HYPER)
                .datatype(coded)
                .conceptClass(question)
                .name("f39bd7d0-3cf5-4c7b-a89c-210ad3c2836c", "Hyper",
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

        // Referred Out Set
        install(new ConceptBuilder(REFERRED_OUT_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("d6efe5bc-4bc6-4b60-bb3f-422b94331312", "Referred Out Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(referredOut, otherNoneCodedText)
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

        // Referred to POSER support
        Concept poorGrowth = install(new ConceptBuilder(REFERRED_TO_POSER_SUPPORT)
                .datatype(coded)
                .conceptClass(question)
                .name("374db2ca-c1a9-47c6-a0e9-3ec1c2962dd1", "Referred to POSER support",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("8c145885-6991-4fd5-87c0-3715e16cabee","POSER is a program at Partners In Health which addresses the social inequalities that put our patients at increased risk of disease by providing nutritional support, building houses, paying for school fees and installing well caps or filtering systems to ensure access to clean drinking water.", Locale.ENGLISH)
                .answers(yes,no,unknown)
                .build());

        // Tone Set
        Concept toneSet = install(new ConceptBuilder(TONE_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("C1CEE207-321E-42D3-9C66-2FE38CE77B3C", "Tone Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(normal,hypo,hyper)
                .build());

        // Feeding Set
        Concept feedingSet = install(new ConceptBuilder(FEEDING_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("953bb181-e383-4325-bc3c-ba654530bd79", "Feeding Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(breastFeeding,cup,ogt)
                .build());

        //Developmental Delay Assessments Set
        install(new ConceptBuilder(DEVELOPMENTAL_DELAY_ASSESSMENTS_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("4d72e73f-adee-444f-b9d6-8efbe4ed963c", "Developmental Delay Assessment Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(weight,height,muac,weightAgainstAge,heightAgainstAge,malnutrition,irritability,toneSet,feedingSet,signsOfCerebralPalsy)
                .build());

        //Developmental Delay Plan Set
        install(new ConceptBuilder(DEVELOPMENTAL_DELAY_PLAN_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("277b2115-c2ac-48ab-bc93-165be27f90dc", "Developmental Delay Plan Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(continueFollowUp,physiotherapy, playTherapy,groupCounselling)
                .build());

        //Developmental Delay TSH Test Set
        install(new ConceptBuilder(DEVELOPMENTAL_DELAY_TSH_TEST_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("1f61b143-ad1b-4c18-b802-3931a7d1c80f", "Developmental Delay TSH Test Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(courseHair, poorGrowth)
                .build());
    }
}
