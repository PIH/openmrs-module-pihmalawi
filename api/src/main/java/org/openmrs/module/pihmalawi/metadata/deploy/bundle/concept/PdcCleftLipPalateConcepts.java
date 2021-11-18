package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.PdcMetadata;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class PdcCleftLipPalateConcepts extends VersionedPihConceptBundle{
    public static String FEEDING_ISSUES = "d7ce3c38-2844-4f46-9dfe-395738b056aa";
    public static String SMALL_JAW = "7a138236-764e-479e-9723-1a0bf01dadce";
    public static String DIFFICULT_BREATHING = "49ab956b-ef54-427a-b0bb-440c47164012";
    public static String HEART_MURMUR = "aec2dff8-9e02-4962-802b-af3cd03e1d96";
    public static String FACIAL_ABNORMALITIES = "b2cd3341-7be7-4824-aa61-824731e54dd5";
    public static String OTHER_ASSESSMENTS = "58e9b7e1-8e7b-4ece-823f-903bf66d7bf6";
    public static String SCHEDULED = "09270f6c-9bf3-4715-8d36-d5a985fbc301";
    public static final String REFERRED_OUT = "da88696b-bf1d-4a31-b49d-997326e4a777";
    public static String DATE_SCHEDULED = "184bb470-130a-4e39-bc58-6f6c302d836d";
    public static String CONTINUE_FOLLOWUP = "696c6ade-1f06-40d1-aadd-4c2b257167db";
    public static String SUPPORT_GROUP = "5ccb82d9-1d0e-4331-a9cc-0a6d6f51322a";
    public static String FEEDING_COUNSELLING = "f17754f9-c3f8-40d0-a9aa-903737a98f86";
    public static String FOOD_SUPPLEMENT = "f17754f9-c3f8-40d0-a9aa-903737a98f86";
    public static String PDC_CLEFT_LIP_PALATE_ASSESSMENTS_SET = "73262ce0-6a29-4cd7-b081-5b6c09ea866a";
    public static String PDC_CLEFT_LIP_PALATE_RECOMMENDATION_SET = "a5e92641-ad5e-4c51-b80f-b4566abfc331";
    public static String PDC_CLEFT_LIP_PALATE_ADDITIONAL_PLANS_SET = "e0c6b43e-3128-49d5-9b38-ca77ba5330d7";
    public static String SURGERY_SET = "1e225eaa-d88e-41d6-b032-fc2c3c7c7ee6";
    public static String DATE_OF_SURGERY_DATE = "65634228-977f-11e1-8993-905e29aff6c1";


    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);
        Concept dateOfSurgery = MetadataUtils.existing(Concept.class,DATE_OF_SURGERY_DATE);
        Concept referredOut = MetadataUtils.existing(Concept.class,REFERRED_OUT);

        // Feeding Issues
        Concept feedingIssues = install(new ConceptBuilder(FEEDING_ISSUES)
                .datatype(coded)
                .conceptClass(question)
                .name("65acfde4-8630-4b0e-86e5-bbc6985dc988", "Feeding issues",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Small jaw
        Concept smallJaw = install(new ConceptBuilder(SMALL_JAW)
                .datatype(coded)
                .conceptClass(question)
                .name("492a3b92-4c2f-4ef1-b232-5d92c20546e3", "Small jaw",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Difficult breathing
        Concept difficultBreathing = install(new ConceptBuilder(DIFFICULT_BREATHING)
                .datatype(coded)
                .conceptClass(question)
                .name("9b7a24d2-d231-4521-b94b-62718891eb96", "Difficult breathing",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Heart Murmur
        Concept heartMurmur = install(new ConceptBuilder(HEART_MURMUR)
                .datatype(coded)
                .conceptClass(question)
                .name("b48811a5-0049-4b04-a6dd-087641c07fed", "Heart murmur",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Facial Abnormalities
        Concept facialAbnormalities = install(new ConceptBuilder(FACIAL_ABNORMALITIES)
                .datatype(coded)
                .conceptClass(question)
                .name("339c13f1-38ec-4adf-ab5f-3a449a3f6e00", "Facial abnormalities",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Other assessments
        Concept otherAssessments = install(new ConceptBuilder(OTHER_ASSESSMENTS)
                .datatype(coded)
                .conceptClass(question)
                .name("94c962f2-3796-42fe-99ce-4e103d6302ce", "Other assessments",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Scheduled
        Concept scheduled = install(new ConceptBuilder(SCHEDULED)
                .datatype(coded)
                .conceptClass(question)
                .name("f6a47072-fd84-44a4-9387-6c93d2c5a14e", "Scheduled",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // date Scheduled
        Concept dateScheduled = install(new ConceptBuilder(DATE_SCHEDULED)
                .datatype(coded)
                .conceptClass(question)
                .name("c410b923-3451-4263-8210-eb65aef8ab53", "Date scheduled",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Surgery set
        install(new ConceptBuilder(SURGERY_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("57f185d3-251d-4298-99f3-f86b1aee2f26", "Surgery set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(scheduled,dateOfSurgery)
                .build());

        // Continue Followup
        Concept continueFollowup = install(new ConceptBuilder(CONTINUE_FOLLOWUP)
                .datatype(coded)
                .conceptClass(question)
                .name("2c522c2d-6b84-4559-8789-b129371b103a", "Continue followup",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());


        // Support Group
        Concept supportGroup = install(new ConceptBuilder(SUPPORT_GROUP)
                .datatype(coded)
                .conceptClass(question)
                .name("9d148610-477d-49d9-8445-d12a8325dfc2", "Support group",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Feeding Counselling
        Concept feedingCounselling = install(new ConceptBuilder(FEEDING_COUNSELLING)
                .datatype(coded)
                .conceptClass(question)
                .name("84f5195c-8864-49f5-aa63-2101c5f25bf7", "Feeding counselling",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Food Supplement
        Concept foodSupplement = install(new ConceptBuilder(FOOD_SUPPLEMENT)
                .datatype(coded)
                .conceptClass(question)
                .name("84f5195c-8864-49f5-aa63-2101c5f25bf7", "Food supplement",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        //PDC Cleft lip palate assessments set
        install(new ConceptBuilder(PDC_CLEFT_LIP_PALATE_ASSESSMENTS_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("7a840272-bdfc-4f72-9c16-1e5d8ffe1d66", "PDC Cleft lip palate assessments set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(feedingIssues, smallJaw, difficultBreathing, heartMurmur, facialAbnormalities, otherAssessments)
                .build());

        //PDC Cleft lip palate recommendation set
        install(new ConceptBuilder(PDC_CLEFT_LIP_PALATE_RECOMMENDATION_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("ae0d5c59-eb01-4c89-8254-676e9c8e7bf9", "PDC Cleft lip palate recommendation set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(continueFollowup,referredOut)
                .build());

        //PDC Cleft lip palate additional plans set
        install(new ConceptBuilder(PDC_CLEFT_LIP_PALATE_ADDITIONAL_PLANS_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("30c960fe-cd6d-4372-8086-74fc56c19a95", "PDC Cleft lip palate additional plans set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(supportGroup,feedingCounselling,foodSupplement)
                .build());
    }
}
