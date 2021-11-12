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
public class PdcTrisomy21Concepts extends VersionedPihConceptBundle{

    public static String PASSAGE_NORMAL = "91bd457a-b7a8-4424-a4fa-9c4a2bae35bd";
    public static String DIARRHEA_PERSISTENT = "91bd457a-b7a8-4424-a4fa-9c4a2bae35bd";
    public static String PATIENT_VOMITING = "fcb7b0f5-7190-4554-9b4d-8358af84dac6";
    public static String MECONIUM_DELAY = "9af02405-2861-4c98-87b0-6b92a4606a7d";
    public static String PAIN = "e26e1f17-0bf5-4600-801c-16e23180d60e";
    public static String DISCHARGE = "a9fb94ba-b3a9-4d2a-98e4-53bf19dff3d5";
    public static String APNEA = "d62a989c-055c-4fbe-bc3b-2fcc8357263c";
    public static String CHOKING = "f6f6e463-4178-44c2-a647-1d7f699808a9";
    public static String WEAK = "64af04b8-81bb-4cea-8334-a07b1072e3ef";
    public static String SUPPORT_GROUP = "836ed5c1-3d74-4e13-b695-bc429880bc52";
    public static String GROUP_COUNSELING = "cef3471e-eb0a-4b14-b402-9b24742e3869";
    public static String GI_CONCERNS = "8ba308ba-e077-4137-83c6-50685f9277c7";
    public static String GI_CONCERNS_STOOL_SET = "f6b86b98-b054-43b4-87aa-772e5be53620";
    public static String OTHER_CONCERNS_EAR_SET = "99fd40e3-b255-455a-ad7b-70b64bea4a3f";
    public static String OTHER_CONCERNS_SLEEP_SET = "85cab2be-8d95-44b8-b7ea-f1620ac05217";
    public static String OTHER_CONCERNS_EXTREMITIES_SET = "851e95dc-463c-497b-a4ad-6f64aac570e6";

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {



        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

        // Passage Normal
        Concept passageNormal = install(new ConceptBuilder(PASSAGE_NORMAL)
                .datatype(coded)
                .conceptClass(question)
                .name("422dbaa1-bb64-4a7a-a78d-cc511a4e50bd", "Passage Normal",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Diarrhea persistent
        Concept diarrheaPersistent = install(new ConceptBuilder(DIARRHEA_PERSISTENT)
                .datatype(coded)
                .conceptClass(question)
                .name("e01ba93c-5d47-417c-ad6e-770e9ef74943", "Diarrhea Persistent",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Vomiting
        Concept patientVomiting = install(new ConceptBuilder(PATIENT_VOMITING)
                .datatype(coded)
                .conceptClass(question)
                .name("c2d66317-288a-4529-82d6-48fc2283ef3b", "Patient Vomiting",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Meconium delayed
        Concept meconiumDelayed = install(new ConceptBuilder(MECONIUM_DELAY)
                .datatype(coded)
                .conceptClass(question)
                .name("3edff5e0-857d-43bc-97de-bcb38ad6d1a2", "Meconium delayed",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Pain
        Concept pain = install(new ConceptBuilder(PAIN)
                .datatype(coded)
                .conceptClass(question)
                .name("99b41dc2-c80a-447e-92f6-69ed982da4b7", "Pain",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Discharge
        Concept discharge = install(new ConceptBuilder(DISCHARGE)
                .datatype(coded)
                .conceptClass(question)
                .name("be090d81-de10-4b49-a65f-6a2d141a10d9", "Discharge",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Apnea
        Concept apnea = install(new ConceptBuilder(APNEA)
                .datatype(coded)
                .conceptClass(question)
                .name("799195de-332e-4b66-933d-6378bab6f10e", "Apnea",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Choking
        Concept choking = install(new ConceptBuilder(CHOKING)
                .datatype(coded)
                .conceptClass(question)
                .name("1ede99f4-3dc3-4680-9b65-421aa20e17d5", "Choking",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Weakness
        Concept weakness = install(new ConceptBuilder(WEAK)
                .datatype(coded)
                .conceptClass(question)
                .name("624d6d7d-9f20-4311-86bc-2d91f0803bab", "Weak",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Group Counselling
        Concept groupCounselling = install(new ConceptBuilder(GROUP_COUNSELING)
                .datatype(coded)
                .conceptClass(question)
                .name("da853d43-545f-4629-84d1-99c5a928b876", "Group Counselling",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Stool Set
        Concept stoolSet = install(new ConceptBuilder(GI_CONCERNS_STOOL_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("321f45a9-3d1b-48ed-a9c6-f52da15713f4", "Gastrointestinal Concerns - Stool set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .name("d1cc2839-f0dc-48a9-b2ae-cc7d32f8bc98", "GI Concerns - Stool set", Locale.ENGLISH,
                        ConceptNameType.SHORT)
                .setMembers(passageNormal, diarrheaPersistent)
                .build());

        // Grouping concepts under GI Concerns Set
        Concept giConcernsSet = install(new ConceptBuilder(GI_CONCERNS)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("3a1d7a07-94a5-4e89-9255-fb2fe622f119", "Gastrointestinal Concerns", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .name("d1cc2839-f0dc-48a9-b2ae-cc7d32f8bc98", "GI Concerns", Locale.ENGLISH,
                        ConceptNameType.SHORT)
                .setMembers(stoolSet, patientVomiting)
                .build());

        // Grouping concepts under Other Concerns - Ear
        Concept otherConcernsEarSet = install(new ConceptBuilder(OTHER_CONCERNS_EAR_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("7437086d-dbde-4f50-a25b-0a78683155c2", "Other Concerns - Ear", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(pain, discharge)
                .build());

        // Grouping concepts under Other Concerns - Sleep
        Concept otherConcernsSleepSet = install(new ConceptBuilder(OTHER_CONCERNS_SLEEP_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("b3e66949-2d3f-474e-affa-04789d3978f1", "Other Concerns - Sleep", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(apnea, choking)
                .build());

        // Grouping concepts under Other Concerns - Extremities
        Concept otherConcernsExtremitiesSet =install(new ConceptBuilder(OTHER_CONCERNS_EXTREMITIES_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("a2666c16-c8aa-4e37-b7ce-71ceab3fffd2", "Other Concerns - Extremities", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(pain, weakness)
                .build());

    }
}
