package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
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
    public static String DETAILS_OF_COMPLICATIONS = "86eacb84-51ea-11ec-bf63-0242ac130002";
    public static String TYPE_OF_FEED_SET = "38725750-df54-477a-a9b0-42527f94b709";
    public static String PERINATAL_INFECTION_SET = "60227263-6073-44be-a6d8-c41f18b453a4";
    public static String LESS_OR_EQUAL_TO_FORTY_EIGHT_HOURS = "ecdd9e50-ce94-49f4-a400-55421da645ab";
    public static String SEVEN_DAYS = "50300b30-0a03-4806-85f7-9a5c0239321c";
    public static String GREATER_THAN_SEVEN_DAYS = "7340a0aa-b965-4cc6-81e8-9a48236d1096";
    public static String DURATION_CODED = "89562b1e-9b39-4bdf-b57e-cf0e2448d815";
    public static String ANTIBIOTICS_SET = "e2a64391-76c0-4f4b-b946-f51b05495c17";
    public static String ANTIBIOTICS = "6575888e-977f-11e1-8993-905e29aff6c1";
    public static String REASONS_FOR_REFERRAL_SET = "7a37ec7b-2804-46c8-be48-4707959192be";
    public static String CARE_LINKED_SET = "657dbc1d-f753-49b8-9a6f-4c55e55c95ee";
    public static String SOURCE_OF_REFERRAL_SET = "b5346c8a-246c-46aa-bce5-17a77534bc30";
    public static String CONDITIONS_AT_ENROLLMENT = "cbd2080b-ffb5-4d6f-aabd-dd80b2938d5a";
    public static String DIAGNOSIS = "656292d8-977f-11e1-8993-905e29aff6c1";
    public static String CLINICAL_CONDITIONS = "657a53d2-977f-11e1-8993-905e29aff6c1";
    public static String OTHER_DIAGNOSIS = "65780d0c-977f-11e1-8993-905e29aff6c1";
    public static String CARE_LINKED_TYPE_QUESTION = "24C2F817-C37B-4746-A052-18C4E0C2D93E";
    public static String CARE_LINKED_IC3 = "3064BCF8-56F7-43C9-A8CF-D90D42EEF739";
    public static String CARE_LINKED_ADVANCED_NCD = "2bc82b28-e84f-11e8-9f32-f2801f1b9fd1";
    public static String CARE_LINKED_MCH_CLINIC = "9CA705D5-5DAA-40E8-8B69-F3F7C0B29E95";
    public static String CARE_LINKED_PALIATIVE_CARE = "BB020202-552B-4253-B442-E2B7153E587C";
    public static String CARE_LINKED_PHYSIOTHERAPY = "394B1F7B-7AD8-4038-AF3A-5B673BD4F42A";
    public static String AGE_AT_INTAKE = "E1F83AA4-FAFE-4150-9AA5-C13B0602B985";
    public static String TIME_UNTIS = "f1904502-319d-4681-9030-e642111e7ce2";
    public static String AGE_OF_CHILD = "655e54a2-977f-11e1-8993-905e29aff6c1";
    public static final String  DONE= "584fbc24-9eda-4db4-93d2-30b77067a5c6";
    public static final String NORMAL = "d34cf0b3-073c-4f4e-ac4c-1c1e8e4c9165";
    public static final String TSH_RESULT_CONSTRUCT = "4E062C22-BB8D-4684-BA1E-A1F8E476A4E2";

    @Override
    public int getVersion() {
        return 25;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept hospital = MetadataUtils.existing(Concept.class,"655d1772-977f-11e1-8993-905e29aff6c1");
        Concept healthFacility = MetadataUtils.existing(Concept.class,"6566905e-977f-11e1-8993-905e29aff6c1");
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);
        Concept otherNoneCodedText = MetadataUtils.existing(Concept.class,"d57e3a20-5802-11e6-8b77-86f30ca893d3");
        Concept antibiotics = MetadataUtils.existing(Concept.class,ANTIBIOTICS);
        Concept patientDiagnosis = MetadataUtils.existing(Concept.class,DIAGNOSIS);
        Concept clinicalConditions = MetadataUtils.existing(Concept.class,CLINICAL_CONDITIONS);
        Concept otherDiagnosis = MetadataUtils.existing(Concept.class,OTHER_DIAGNOSIS);

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
        Concept developmentDelay =install(new ConceptBuilder(DEVELOPMENT_DELAY)
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

        // Details of Complications
        Concept detailsOfComplications = install(new ConceptBuilder(DETAILS_OF_COMPLICATIONS)
                .datatype(text)
                .conceptClass(misc)
                .name("a0e220fa-51ea-11ec-bf63-0242ac130002", "Details of Complications", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("ada20cc4-51ea-11ec-bf63-0242ac130002", "Enter details of complications", Locale.ENGLISH)
                .build());

        // <= 48 Hours
        Concept lessThanOrEqualToFortyEightHours = install(new ConceptBuilder(LESS_OR_EQUAL_TO_FORTY_EIGHT_HOURS)
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("b7433624-8fc3-49d1-b467-c75d7f637dda", "Less than or equal to 48 hours", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("ddab8330-bb80-451b-939d-cc14d3ea6642", "<= 48 Hours", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        // <= 7 days
        Concept sevenDays = install(new ConceptBuilder(SEVEN_DAYS)
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("54ed7af6-80cd-41a2-9910-f8754ca1c90e", "Seven Days", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("c55c9760-2f4b-4136-b36f-d0a0acf1e2d6", "7 Days", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        // > 7 days
        Concept greaterThanSevenDays = install(new ConceptBuilder(GREATER_THAN_SEVEN_DAYS)
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("7fe34306-50ba-44c4-9a06-beaa3b1c3f20", "Greater Than Seven Days", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("4756a657-13ad-4595-8a9f-3379f7d082d4", "> 7 Days", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        // Duration coded
        Concept durationCoded = install(new ConceptBuilder(DURATION_CODED)
                .datatype(coded)
                .conceptClass(question)
                .name("522a4245-f435-4775-b4cc-f6e5b1014159", "Duration (coded)",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(lessThanOrEqualToFortyEightHours,sevenDays,greaterThanSevenDays)
                .build());

        // Type of Feed Set
        Concept typeOfFeedSet = install(new ConceptBuilder(TYPE_OF_FEED_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("40019699-87b1-4183-85d5-b082033b30e8", "Type of Feed Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(typeOfFeed,otherNoneCodedText)
                .build());

        // Perinatal Infection Set
        Concept perinatalInfectionSet = install(new ConceptBuilder(PERINATAL_INFECTION_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("df32705c-12c4-4980-9ec6-fc4c4c41a238", "Perinatal Infection Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(perinatalInfection,otherNoneCodedText)
                .build());

        // Antibiotic Set
        Concept antibioticSet = install(new ConceptBuilder(ANTIBIOTICS_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("256d59d3-beda-4cb3-aa87-cbb809109bdf", "Antibiotic Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(antibiotics,durationCoded)
                .build());

        // Reasons for referral Set
        Concept reasonForReferralSet = install(new ConceptBuilder(REASONS_FOR_REFERRAL_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("80085777-fd69-46cd-bf88-3a9a1a00ccb0", "Reasons for referral set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .description("faf9f212-6ecd-4f06-9576-bf0d143fae0c","Set for referrals",Locale.ENGLISH)
                .setMembers(reasonsForReferral,otherNoneCodedText)
                .build());

        // Care linked Set
        Concept careLinkedSet = install(new ConceptBuilder(CARE_LINKED_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("5ef34bdb-24e2-4c5b-abc7-28647e89ac9c", "Care Linked Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(careLinked,otherNoneCodedText)
                .build());

        // Source of Referral Set
        Concept sourceOfReferralSet = install(new ConceptBuilder(SOURCE_OF_REFERRAL_SET)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("36172654-c042-4831-80a8-2d0a43efa94c", "Source Of Referral Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(careLinked,otherNoneCodedText)
                .build());

        // Conditions at Enrollment Set
        Concept conditionsAtEnrollment = install(new ConceptBuilder(CONDITIONS_AT_ENROLLMENT)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("69e6f10d-d077-411d-b9e8-cead33613118", "Conditions At Enrollment Set", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .setMembers(patientDiagnosis,clinicalConditions,otherDiagnosis)
                .build());

        install(new ConceptBuilder("71C441A7-08EF-4B4C-90DC-0EA36751556B")
                .datatype(date)
                .conceptClass(question)
                .name("966D574C-D83D-436D-912F-777592469E5E","Date of enrollment",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("EDF0A41A-82E5-408E-B30E-2041AE66EB86")
                        .type(sameAs).ensureTerm(pih, "DATE OF ENROLLMENT").build())
                .build());

        Concept careLinkedIc3 = MetadataUtils.existing(Concept.class, CARE_LINKED_IC3);
        Concept careLinkedAdvancedNcd = MetadataUtils.existing(Concept.class, CARE_LINKED_ADVANCED_NCD);

        Concept careLinkedMchClinic = install(new ConceptBuilder(CARE_LINKED_MCH_CLINIC)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("355CE3BD-90D4-4F34-ADD2-ED636650D1F0", "Mental Health Clinic", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("BC9B03E0-9CC0-4B27-9D00-DBA406265766", "Mental Health Clinic care linked", Locale.ENGLISH)
                .build());

        Concept careLinkedPaliativeCare = install(new ConceptBuilder(CARE_LINKED_PALIATIVE_CARE)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("DF24B181-A306-49E8-87BA-F76A983F035A", "Paliative linked care", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("EFED0F0B-9B6D-4F1A-AE61-FA36103980B9", "Paliative care linked care", Locale.ENGLISH)
                .build());

        Concept careLinkedPhysiotherapy = install(new ConceptBuilder(CARE_LINKED_PHYSIOTHERAPY)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("B958860A-1F49-41A1-9601-E31EB16D09E8", "Physiotherapy linked care", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("D90400DD-5933-43C5-A96B-2DFB1A7AB545", "Physiotherapy linked care", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder(CARE_LINKED_TYPE_QUESTION)
                .datatype(coded)
                .conceptClass(question)
                .name("87823C31-8155-412D-8C40-BB09D1AD5618", "Care Linked Type", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .description("7CDD6696-245D-49E4-BE47-DBA364954CBD","Set of possible care linked types",Locale.ENGLISH)
                .answers(clinical, nru, careLinkedIc3, careLinkedAdvancedNcd, careLinkedMchClinic, careLinkedPaliativeCare,careLinkedPhysiotherapy)
                .build());

        Concept ageOfChild = MetadataUtils.existing(Concept.class, AGE_OF_CHILD);
        Concept timeUntis = MetadataUtils.existing(Concept.class, TIME_UNTIS);
        install(new ConceptBuilder(AGE_AT_INTAKE)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("32415A86-AE4F-4B8C-BF8B-CB1C2FA5CF74", "Age at intake", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("C5C7C9E2-516A-446E-B170-AEDC2A86AC55")
                        .type(sameAs).ensureTerm(pih, "AGE AT INTAKE").build())
                .setMembers(ageOfChild,timeUntis)
                .build());

        Concept dateOfResult = MetadataUtils.existing(Concept.class, "656fa234-977f-11e1-8993-905e29aff6c1");
        Concept doneResult = MetadataUtils.existing(Concept.class, DONE);
        Concept normalResult = MetadataUtils.existing(Concept.class, NORMAL);
        install(new ConceptBuilder(TSH_RESULT_CONSTRUCT)
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("14C63FFE-714F-4BDF-BB91-4CD8325E1073", "TSH result construct", Locale.ENGLISH,
                        ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("AE1995AE-49A6-4754-A4BC-55A6927F1F70")
                        .type(sameAs).ensureTerm(pih, "TSH RESULT CONSTRUCT").build())
                .setMembers(dateOfResult, doneResult, normalResult)
                .build());


    }
}
