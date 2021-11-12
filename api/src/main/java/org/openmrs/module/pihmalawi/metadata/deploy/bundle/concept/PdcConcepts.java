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
public class PdcConcepts extends VersionedPihConceptBundle {

    public static String SOURCE_OF_REFERRAL = "eb76915e-104d-4500-b56e-726f3e2a75f9";
    public static String COMMUNITY = "390c74d3-216b-4573-b38a-68503cc4e69e";
    public static String TERTIARY = "be50b470-9744-4f04-b9a8-bad7b4b239ea";
    public static String REFERRAL_FORM_FILLED = "8f985039-f018-4542-a531-f693fca2906b";
    public static String DEVELOPMENT_DELAY = "1be62437-3093-4530-b4ab-1cd4626b9704";
    public static String ENROUTE = "50fe5939-7380-4a01-abe4-6a7806280bdb";
    public static String CARE_LINKED = "0d52f232-8d34-45d4-9006-dd592bc7bf24";
    public static String CLINICAL = "6566b8c2-977f-11e1-8993-905e29aff6c1";
    public static String NRU = "f0bb3ac5-0e84-487a-8a2d-59b96378a797";
    public static String OGT = "fe7d2795-3cfe-47fa-933c-cb8e9b1c42d5";
    public static String APGAR = "687a622a-302b-49c9-87a7-2f7558549548";
    public static String POSER_SUPPORT = "5b49fbf4-2645-4ab2-974e-4e6c961162b1";
    public static String ENROLLED_IN_PDC = "92148ae3-642c-47f5-ae7c-2c4efa9796e8";
    public static String BREAST_MILK = "ba528fcf-d9ee-43c3-af15-c169adcec937";
    public static String TYPE_OF_FEED = "7f5c591e-8b74-4fa9-8bbf-f4154a0d92d3";
    public static String INFANT_FORMULA = "656ac570-977f-11e1-8993-905e29aff6c1";
    public static String INCOME_SOURCE = "de85f47e-0830-4840-b393-e7adcd641064";

    @Override
    public int getVersion() {
        return 11;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept hospital = MetadataUtils.existing(Concept.class,"655d1772-977f-11e1-8993-905e29aff6c1");
        Concept healthFacility = MetadataUtils.existing(Concept.class,"6566905e-977f-11e1-8993-905e29aff6c1");
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);

        // Perinatal Infection
        Concept perinatalInfection = install(new ConceptBuilder("5c1f2ade-4224-46c3-99f5-7236aab13f13")
                .datatype(coded)
                .conceptClass(question)
                .name("50474960-c5b2-48a9-8bb7-ec4ff193d52d", "Perinatal infection",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no,unknown)
                .build());

        // Gestational age
        Concept gestationalAge  = install(new ConceptNumericBuilder("31bf4e2a-0574-47f3-9c68-9d530ebff898")
                .datatype(numeric)
                .conceptClass(question)
                .units("weeks")
                .name("06c508fa-d080-45b1-984b-412067e01800", "Gestational Age", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("4f3e0805-3059-4226-abcd-6100cddd9543", "Gestational age in weeks", Locale.ENGLISH)
                .build());

        // Cup-feeding
        Concept cupFeeding = install(new ConceptBuilder("4f3e0805-3059-4226-abcd-6100cddd9543")
                .datatype(coded)
                .conceptClass(question)
                .name("5cecddc6-edbd-48f8-bdd9-51d62a074382", "Cup-feeding",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        //Premature Birth
        Concept prematureBirth = install(new ConceptBuilder(PdcMetadata.PREMATURE_BIRTH)
                .datatype(coded)
                .conceptClass(finding)
                .name("730c4b07-8060-4690-bb23-d5674d39ce5d", "Premature Birth",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Hydrocephalus
        Concept hydrocephalus = install(new ConceptBuilder(PdcMetadata.HYDROCEPHALUS)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("59cee21f-12c3-4f31-aeca-18d4fc75107f", "Hydrocephalus",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Downs Syndrome
        Concept downsSyndrome = install(new ConceptBuilder(PdcMetadata.DOWNS_SYNDROME)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("cd46206a-a25e-4131-ad99-9aa9c9c2107a", "Downs Syndrome (Suspected or Confirmed)",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Cleft Lip
        Concept cleftLipOrPalate = install(new ConceptBuilder(PdcMetadata.CLEFT_LIP_OR_PALATE)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("87a58bee-850e-4fda-b625-3daa674e78fd", "Cleft Lip or Palate",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Cleft Lip
        Concept cleftLip = install(new ConceptBuilder(PdcMetadata.CLEFT_LIP)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("f9261161-cb6f-49e4-9fa3-4a404897b9d8", "Cleft Lip",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Cleft Palate
        Concept cleftPalate = install(new ConceptBuilder(PdcMetadata.CLEFT_PALATE)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("88449258-b76d-459d-93f6-de2f9effb889", "Cleft Palate",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Trisomy21
        Concept trisomy21 = install(new ConceptBuilder(PdcMetadata.TRISOMY_21)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("a528d470-c061-43a4-833a-987aec47b90b", "Trisomy 21",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        // Hypoxic ischemic encephalopathy (HIE)
        Concept hie = install(new ConceptBuilder(PdcMetadata.HIE)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("9c1552be-0c61-4e2e-a172-843f38ceb333", "Hypoxic Ischemic Encephalopathy",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("3a1f5ee8-db05-47a9-97af-78734c992a5f", "HIE", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        //Other Developmental Delay
        Concept otherDevelopmentDelay =install(new ConceptBuilder(PdcMetadata.OTHER_DEVELOPMENTAL_DELAY)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("208cb938-9c1c-4916-8f03-b957956e9869", "Other Developmental Delay, Suspected Neuromuscular " +
                                "Disease, and Suspected Genetic Syndromes",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        // Developmental Delay
        Concept DevelopmentDelay =install(new ConceptBuilder(DEVELOPMENT_DELAY)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("5a0cea8d-2a7e-407d-8d0f-c1e955742038", "Developmental Delay",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Severe malnutrition requiring club foot, hospitalization (infant < 12 months)
        Concept severeMalnutrition = install(new ConceptBuilder(PdcMetadata.SEVERE_MALNUTRITION)
                .datatype(coded)
                .conceptClass(diagnosis)
                .name("0428535f-3efc-4c38-a5b3-179f43ac29b1", "Severe malnutrition",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        //Age of guardian
        Concept ageOfGuardian = install(new ConceptNumericBuilder(PdcMetadata.AGE_OF_GUARDIAN)
                .datatype(numeric)
                .conceptClass(question)
                .units("Years")
                .precise(false)
                .name("ea645ac7-e75f-4a21-9058-9124267838d1", "Age of guardian", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("4ba1b6dd-15e1-4863-b83b-05360f8ee569", "Guardian age in years", Locale.ENGLISH)
                .build());

        Concept lowBirthWeight = MetadataUtils.existing(Concept.class, PdcMetadata.LOW_BIRTH_WEIGHT);
        Concept centralNervousSystem = MetadataUtils.existing(Concept.class,PdcMetadata.CENTRAL_NERVOUS_SYSTEM);
        // Reasons for referral
        Concept reasonsForReferral = install(new ConceptBuilder(PdcMetadata.REASON_FOR_REFERRAL)
                .datatype(coded)
                .conceptClass(question)
                .name("b71eb2d2-7326-4646-97e0-a60265ce101f", "PDC Reasons for referral",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(perinatalInfection,gestationalAge,lowBirthWeight,prematureBirth,centralNervousSystem,hydrocephalus,downsSyndrome,
                        cleftLipOrPalate,otherDevelopmentDelay,severeMalnutrition,trisomy21,hie)
                .description("fe5c86fa-68e8-42f4-b762-8cccb0e621e5","Reason for referral (e.g PDC)",Locale.ENGLISH)
                .build());

        // Community
        Concept community = install(new ConceptBuilder(COMMUNITY)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("15e98466-be5a-45d2-9c4b-4b506ffb7d51", "Community",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        // Tertiary
        Concept tertiary = install(new ConceptBuilder(TERTIARY)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3bb2410b-ecd1-46f6-b2a8-970efd025d6f", "Tertiary",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        // referral source
        Concept referralSource = install(new ConceptBuilder(SOURCE_OF_REFERRAL)
                .datatype(coded)
                .conceptClass(question)
                .name("ab55f3ee-55fb-4595-8005-fe813c79a70f", "Referral Source",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(tertiary,hospital,healthFacility,community)
                .build());

        // referral form filled
        Concept referralFormFilled = install(new ConceptBuilder(REFERRAL_FORM_FILLED)
                .datatype(coded)
                .conceptClass(question)
                .name("f4078e25-b62a-43ad-a20f-47732d886a3c", "Referral Form Filled",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Enroute
        Concept enroute = install(new ConceptBuilder(ENROUTE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5ba41739-40aa-4764-9e56-bdbe7aabf844", "Enroute",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        // NRU
        Concept nru = install(new ConceptBuilder(NRU)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("c4cf01a2-20d1-4bfc-959a-4bbaefeab99c", "Nutrition Rehabilitation Unit",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("b7f8cc51-ca6c-4ee6-b670-4ceede819000", "NRU",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept clinical = MetadataUtils.existing(Concept.class, CLINICAL);
        // OGT
        Concept ogt = install(new ConceptBuilder(OGT)
                .datatype(coded)
                .conceptClass(misc)
                .name("a3a8decb-44b4-4133-be02-ecbd99214d5b", "Orogastric tube",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("a9a9f4e6-0bec-45ec-8c87-d45b5e046558", "OGT",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        // APGAR
        Concept apgar = install(new ConceptBuilder(APGAR)
                .datatype(notApplicable)
                .conceptClass(procedure)
                .name("88e06bb2-0725-46cd-8a25-38fbc465c0c0", "Appearance, Grimace, Pulse rate, Activity, Respirations",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("90990dbe-32c4-483f-b170-0cc7c8273358", "AGPAR",
                        Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        // Care linked
        Concept careLinked = install(new ConceptBuilder(CARE_LINKED)
                .datatype(coded)
                .conceptClass(procedure)
                .name("f5abe191-ad67-456d-a960-316d12b2be5f", "Care Linked",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(clinical,nru)
                .build());

        // POSER support
        Concept poserSupport = install(new ConceptBuilder(POSER_SUPPORT)
                .datatype(coded)
                .conceptClass(question)
                .name("6e30176b-1d99-49db-be3e-95bc291d3fbd", "Poser Support",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Enrolled in PDC
        Concept enrolledInPdc = install(new ConceptBuilder(ENROLLED_IN_PDC)
                .datatype(coded)
                .conceptClass(question)
                .name("59e438ff-a1ca-4661-84bb-40f93267ee50", "Enrolled in PDC",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        // Breast Milk
        Concept breastMilk = install(new ConceptBuilder(BREAST_MILK)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("7db23cd5-c299-4009-86fc-b3bb86e74fe1", "Breast Milk",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept infantFormula = MetadataUtils.existing(Concept.class, INFANT_FORMULA);

        // Type of Feed
        Concept typeOfFeed = install(new ConceptBuilder(TYPE_OF_FEED)
                .datatype(coded)
                .conceptClass(question)
                .name("84901fa4-8312-4efa-a242-57f6dac2213c", "Type of Feed",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(breastMilk,infantFormula)
                .build());

        // Income Source (specify)
        Concept incomeSource = install(new ConceptBuilder(INCOME_SOURCE)
                .datatype(text)
                .conceptClass(misc)
                .name("7e0a94eb-b7d1-4ea1-8f71-1d94131b0b41", "Income Source", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("fb37aa88-efcb-4f41-bdc0-9a74bbdc879f", "Specify source of income", Locale.ENGLISH)
                .build());

    }
}
