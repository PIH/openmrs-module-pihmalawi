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
public class TeenClubIntakeSurveyConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 1;
    }
    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept other = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER);

        Concept interviewerName = install(new ConceptBuilder("9562d3b9-11cf-4522-a062-e9aaa94f38b4")
                .datatype(text)
                .conceptClass(misc)
                .name("a854433a-00fb-46bd-b652-785d416eac63", "Interviewer Name", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept getaJob = install(new ConceptBuilder("b0cccb19-5744-4d7c-a20f-5357d8eedfa8")
                .datatype(text)
                .conceptClass(misc)
                .name("193c465b-abb1-4dd6-97eb-7edd5fca16de", "Get a job", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept business = install(new ConceptBuilder("3b7d62ef-c044-4cfa-bf44-ba3fcdea70ee")
                .datatype(text)
                .conceptClass(misc)
                .name("17ec4545-1c48-4456-a567-5f04692d28bc", "Business", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept furtherEducation = install(new ConceptBuilder("534c8fba-8077-445a-a2dd-ab90d5af8090")
                .datatype(text)
                .conceptClass(misc)
                .name("dcd182e6-38e6-4675-851b-21d7950a8fb5", "Further Education", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept farming = install(new ConceptBuilder("aac9cf84-ebe7-41c1-b2e9-06514af2c1f8")
                .datatype(text)
                .conceptClass(misc)
                .name("b6bee996-dbff-403b-bb7b-f41eef535d88", "Farming", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
        Concept plansAfterSchool = install(new ConceptBuilder("e1eef37b-98f5-4c86-9560-c090e5b1731f")
                .datatype(coded)
                .conceptClass(misc)
                .name("21b809cb-4295-4df7-b96a-7bc6dedd2482", "Plans after school", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(getaJob, business, furtherEducation, farming, other)
                .build());

        Concept biologicalParent = install(new ConceptBuilder("ce0af2a6-d6f4-4d13-8af3-ea4ad378a653")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("17f5435b-700f-4fba-9070-077edb124636", "Biological Parent(Single)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept biologicalParents = install(new ConceptBuilder("49e133bf-efc9-49f9-91af-1cc26fa88ec6")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("9e98b982-37d1-41e1-b0f7-b78714a4ae36", "Biological Parents-(both)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept grandparents = install(new ConceptBuilder("aa109c28-7745-480e-a3c2-d0bc9f1a2719")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8bbcbb11-fa1d-4713-aa68-58f296cb92ff", "Grandparents", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept elderRelative = install(new ConceptBuilder("d0c7636d-9d5e-4c1c-9656-e76689398ce5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0b16179a-3add-4555-94bb-aa129d75be3b", "Elder relative", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept notRelative = install(new ConceptBuilder("dadec55d-864d-4f33-bfc2-06fccccd6a2a")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("ada90dce-79af-4322-b1b1-c13907f9b9d3", "Non-Relative (neighbor,friend,community person)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept primaryGuardian = install(new ConceptBuilder("807bcfed-6fb9-4ca3-bb46-8286a51906a2")
                .datatype(coded)
                .conceptClass(misc)
                .name("55b1db3e-b0d7-4a1f-a045-a247432232dd", "Primary Guardian", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(biologicalParent,biologicalParents,grandparents,elderRelative,notRelative,other)
                .build());

        Concept location = install(new ConceptBuilder("b7d8d419-2451-44ec-9b6b-9b855f9cc443")
                .datatype(text)
                .conceptClass(misc)
                .name("ea48311c-aa4f-48c2-a3a4-901b3ae9be6b", "Parents location", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept parentsStatus = install(new ConceptBuilder("eed747bb-d4c1-40b0-89e1-f7dd30a80cec")
                .datatype(text)
                .conceptClass(misc)
                .name("62efec4f-69c5-4b48-b8d2-9efce85fa565", "Parents Status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept divorce = install(new ConceptBuilder("bf061dbe-7ead-43a9-a8db-a6869b2daea1")
                .datatype(text)
                .conceptClass(misc)
                .name("ea720df9-df40-4b64-99aa-6f89113a1f7e", "Parents divorced", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept died = install(new ConceptBuilder("c4c3dd23-48a1-4f24-9415-8865ef45b9e0")
                .datatype(text)
                .conceptClass(misc)
                .name("14cc0668-d5e5-452c-9940-33d641cb0753", "Parents died", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept childStaysWithSingleParent = install(new ConceptBuilder("0b6a3722-c4d3-44ba-afd4-6185eba83849")
                .datatype(coded)
                .conceptClass(misc)
                .name("3c0fdeaa-3519-4a0d-bb9f-8ee8b907f20c", "Reason for staying with single parent", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(divorce, died, other)
                .build());

        Concept numberOfPeopleInHousehold = install(new ConceptBuilder("441ed2b2-623a-42a0-aa7b-d16327cfc697")
                .datatype(numeric)
                .conceptClass(misc)
                .name("36127940-869d-4ab5-b957-3d671c6bd54b", "Total number of household members", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept electricity = install(new ConceptBuilder("4f3d9103-a1d2-40cb-953b-9aeeb9950147")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("46080c96-af7d-413e-bf31-432f377eaf93", "Electricity", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept solar = install(new ConceptBuilder("8f8f610c-5db5-4631-85d8-4e4c413e65c5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("7814af8f-e465-416a-8c77-63fc3dafdd43", "Solar", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept gas = install(new ConceptBuilder("03fb4666-62b6-4ae3-a086-f6c0e6d9b9c3")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("176eb891-580c-4d32-bea6-a05c91a29ff1", "From gas", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept paraffinLamp = install(new ConceptBuilder("1ecdd3d4-3dbc-4e50-a17a-d64f84ed9886")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8d79afc4-80cc-48f1-8609-5093095d9309", "Paraffin Lamp", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept torch = install(new ConceptBuilder("499d2f21-86e8-4e81-93fa-f44b3c3e09b6")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("f778dc78-645d-46e0-8ed4-19b6470542c2", "Torch", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept pipedWater = install(new ConceptBuilder("41b2529c-e8d9-4f28-92cd-4b7847c23f5e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("ff9fe2b3-91c3-42f3-9d61-408179144fc9", "Piped Water", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fromWell = install(new ConceptBuilder("d9ba6164-60ca-4540-a080-01b4c24f64d2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("24745e1e-a028-4ff1-83c1-ad8fc01ca904", "Dug Well", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept spring = install(new ConceptBuilder("2d901246-1c15-495c-8102-02b3a7fbfbc3")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("e59837f5-4d19-44a6-8968-005544f84062", "Water from spring", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept bottledWater = install(new ConceptBuilder("0db5816d-8349-400d-a617-013f1e9658f7")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("deb51bd5-6bce-4053-81e6-9aa731f07b7b", "Bottled Water", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept borehole = install(new ConceptBuilder("c07c34ad-08e7-469c-a3d5-3c93f46ff5f8")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("831e938f-fb8e-4ede-a15c-b1ba841a394c", "Water from Borehole", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept toiletAtHome = install(new ConceptBuilder("9bcdf631-c727-48e7-98fd-795fcd544d32")
                .datatype(coded)
                .conceptClass(misc)
                .name("d1291b0e-d670-4ebb-b900-aa7b04b2c99e", "Do you have a toilet at home?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept flushToilet = install(new ConceptBuilder("219fa3bd-df79-456c-a188-3b57cbe9440e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("31c18b4e-7598-42b6-895c-aa6668819651", "Flush Toilet", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept pitLatrine = install(new ConceptBuilder("76a54a76-5a16-4767-9655-d3e1f0f6f13f")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6cce6b2b-36d2-4e01-9c2d-2c825bfee75e", "Pit Latrine", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept kindOfToilet = install(new ConceptBuilder("353df499-ffb9-4ca5-a63a-0ec6fb029e94")
                .datatype(coded)
                .conceptClass(misc)
                .name("60592ce9-e129-4471-8c78-1afd748e6078", "Kind of toilet facility used by household members", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(flushToilet,pitLatrine,other)
                .build());

        Concept firewood = install(new ConceptBuilder("ae3b79fe-4d32-46a0-a756-f6bfc4c19c73")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("21a9e890-2026-447c-a70b-8cd8992feecb", "Firewood", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept charcoal = install(new ConceptBuilder("13635bd8-b5f2-470a-9ff1-80fd465f6f53")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("adeca7fa-ba9f-4229-a7b7-2ffe64e10c92", "charcoal for cooking", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept paraffin = install(new ConceptBuilder("673079b3-1af7-4213-a6ed-02c20905cbf2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("4bfa9e20-106e-4508-ae37-997e8919a4e3", "Paraffin/Kerosone", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept bottledGas = install(new ConceptBuilder("235ff344-df36-4ae9-bbf6-a8d7828ec5ff")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2987b242-165f-4ee1-8f00-9bb18ccb0805", "Bottled Gas", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept doNotKnow = install(new ConceptBuilder("1fa4b4a6-b849-4442-94d0-f46f4460ce38")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1fa4b4a6-b849-4442-94d0-f46f4460ce38", "Don't Know", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept enegryForLighting = install(new ConceptBuilder("79f2c8bd-68ee-4dde-9af4-3f594052df1b")
                .datatype(coded)
                .conceptClass(misc)
                .name("9243dd06-bb9a-4b7d-8bbc-e82d16c30e65", "Main energy source for household lighting", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(electricity,gas, solar,paraffinLamp, torch, other)
                .build());

        Concept waterForDrinking = install(new ConceptBuilder("9243dd06-bb9a-4b7d-8bbc-e82d16c30e65")
                .datatype(coded)
                .conceptClass(misc)
                .name("4357b198-b99b-4be1-862d-238f29a993a3", "Main source of drinking water for household", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(pipedWater, fromWell, spring, borehole, bottledWater, other)
                .build());

        Concept energyForCooking = install(new ConceptBuilder("5755fdfc-586b-4816-9223-f45a1422e20a")
                .datatype(coded)
                .conceptClass(misc)
                .name("e3b93fa3-2a28-4884-b78f-d7f55d64bb47", "Fuel for household cooking", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(electricity, firewood, charcoal,bottledGas,paraffin,other)
                .build());

        Concept earth = install(new ConceptBuilder("47baac9f-9d34-4d5f-961a-cdfaac2c590c")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("26c83fbd-e2fc-41a7-bf01-ab18aed9b74e", "Earth/Sand", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept cement = install(new ConceptBuilder("254e6d65-e086-40c8-94fe-887200292c60")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("17f81230-bdc8-42cd-bf6f-c4d0d0c6a945", "Building cement", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept tiles = install(new ConceptBuilder("2ea253ad-2a61-4d2d-8896-42fda71fd170")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("d0303e15-eee9-46dd-9604-d550ec7921b2", "Tiles", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept typeOfHouseFloor = install(new ConceptBuilder("07f1ce88-dd02-47ff-8f0c-b39fc8616345")
                .datatype(coded)
                .conceptClass(misc)
                .name("babc3d83-a2c5-4433-8007-09c9aea6460c", "Type of house floor", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(earth, cement, tiles, other)
                .build());

        Concept mud = install(new ConceptBuilder("64f04067-ba95-42d1-84c0-d1b9fee1d03c")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1774c921-3157-4f48-9a07-ff622f64b1c0", "Mud for building", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept typeOfHouseWalls = install(new ConceptBuilder("9c7bd3d9-096f-4f33-9781-2b3a23fee8ed")
                .datatype(coded)
                .conceptClass(misc)
                .name("afe52ff4-6bd3-4e03-b06e-9569d39beea2", "Type of house walls", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(cement, mud, other)
                .build());

        Concept grass = install(new ConceptBuilder("5eec9066-13b0-4f9f-a236-0fbaa8d7a836")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("19faabb5-4904-41d8-b8ac-2f05786453eb", "Grass", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept typeOfHouseRoof = install(new ConceptBuilder("7ee0bf27-9473-48d0-b9e5-cdfe67d34ab2")
                .datatype(coded)
                .conceptClass(misc)
                .name("60e83213-a119-4092-aa57-802a2d47f36f", "Type of house roof", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(tiles,grass, other)
                .build());

        Concept enrolledInTeenClub = install(new ConceptBuilder("d3dde05f-9296-4482-8e2c-788b615c5e91")
                .datatype(coded)
                .conceptClass(misc)
                .name("62ab9493-f6b4-4361-9ae4-614a4ebf53c2", "Enrolled in teen club", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept empowerAdoleToBuildPosRelationships = install(new ConceptBuilder("4f68cc43-7f2c-4ce8-8eb5-1ce1bfb796b0")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("45a185d6-631f-4f77-bca2-481ea4e184a6", "To empower adolescent to build positive relationships", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept improveSelfEsteem = install(new ConceptBuilder("be34f352-5c86-4ffa-b6bb-c3a4e2dc00fc")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("7884a5c4-003f-4d6c-837f-600abad228a6", "Improve their self esteem", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept acquireLifeSkills = install(new ConceptBuilder("9a35b9c4-7e60-4ac7-aa81-9626bd0906f0")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("f0c4e231-cd35-4375-9683-0a44dc1d76aa", "Acquire life skills through peer mentorship, adult role-modeling and structured activities", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept teenClubPurpose = install(new ConceptBuilder("3d6d5b03-14be-4baa-84e2-0c661a71f263")
                .datatype(coded)
                .conceptClass(misc)
                .name("4df485e7-56a7-4507-bce8-4edaf38eb0c5", "Purpose of teen clubs", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(empowerAdoleToBuildPosRelationships, improveSelfEsteem, acquireLifeSkills, other)
                .build());

        Concept games = install(new ConceptBuilder("b594b14c-3ee0-4c85-ae4c-85906796a758")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("e0963743-817e-4373-8815-373d28657f2f", "Games", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept sports = install(new ConceptBuilder("76a918e7-4c7a-4e99-9957-6fe33073199f")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("466ee6e1-022d-42bc-9b3f-32038cfd0c56", "Sports", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept drama = install(new ConceptBuilder("495d9e10-4a59-4f45-93d9-07989a73c34a")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5462c80f-7ca9-49c6-8211-68e49037f2b8", "Drama", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept artSessions = install(new ConceptBuilder("ec6f9777-6f77-4aea-828a-5a7f2c5ea031")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("12838e63-2b28-49c5-8371-f1c50fd770a8", "Art sessions", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept hivDiscloreAdherence = install(new ConceptBuilder("383900ef-2863-4d95-a08a-3bf699586a56")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8f07e242-c86f-4977-8efe-26c3d5bf86e0", "HIV education, disclosure, adherence", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  goalSetting = install(new ConceptBuilder("a3050ad2-d8ec-4fc6-b295-594055ca90cd")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("ccd38fe3-e497-4afd-a82c-f1933528714f", "Goal-setting", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  sexualAndReproHealth = install(new ConceptBuilder("049f6531-47ca-4378-b3b8-d8693cf97c84")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2168d47f-b530-4755-becb-fdaa9feaa319", "Sexual and reproductive health", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  stigmaAndDiscrimination = install(new ConceptBuilder("69cd9b84-3c76-4528-986b-22d473f2ea54")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("d3339ea2-115d-42c4-bb8c-679afc999f3d", "Coping with stigma and discrimination", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept teenClubActivities = install(new ConceptBuilder("80d6d139-8804-464a-820d-99c07bde4f89")
                .datatype(coded)
                .conceptClass(misc)
                .name("67141e1e-968b-468a-bfc1-97d7246da288", "Teen club activities", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(games, drama, sports, artSessions, other)
                .build());

        Concept teenClubTopics = install(new ConceptBuilder("0e9df979-802c-4e8b-8396-15a556424183")
                .datatype(coded)
                .conceptClass(misc)
                .name("029292f6-f6af-43fe-99ec-39b5c31c4af6", "Topic covered in teen club", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(hivDiscloreAdherence,goalSetting,sexualAndReproHealth, stigmaAndDiscrimination,other)
                .build());

        Concept socialSupport = install(new ConceptBuilder("7ed120af-0973-4650-b184-ebd31f78f015")
                .datatype(coded)
                .conceptClass(misc)
                .name("226f4c98-c253-4a4f-95e0-4637c2703b0e", "Social support from teen club", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        Concept  schoolSupport = install(new ConceptBuilder("6eebff3c-37fb-4dd6-bc38-c2bbb508e3c6")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0753c012-909d-4bc0-9938-e122e257c427", "School support", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  happy = install(new ConceptBuilder("267d6802-7ecb-450e-9b4e-5436c762b511")
                .datatype(coded)
                .conceptClass(misc)
                .name("9b3860a3-be91-44fd-ad39-b4a1a0ac5a31", "Are you happy with teen club?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        Concept  likeMost = install(new ConceptBuilder("c31911f3-038b-42ee-a929-6c14a96ae508")
                .datatype(text)
                .conceptClass(misc)
                .name("550bae23-536c-4ac3-b480-b79e9c4237d5", "What you like most at teen club?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  dontLike = install(new ConceptBuilder("d7172d9b-da71-4f79-9a62-52a9ff550e88")
                .datatype(text)
                .conceptClass(misc)
                .name("6095b224-d94b-438e-bfdb-c5e420f3f8c0", "What you don't like at teen club?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  verySatisfied = install(new ConceptBuilder("8a723427-24ef-4d25-bc4c-539ddc1f8ae2")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8573b741-554c-420d-a7bf-eba317f0e6c0", "Very satisfied", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  notSatisfied = install(new ConceptBuilder("a93e3448-d71a-4cba-851e-0d328a927b94")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("11702899-f33c-4043-8d22-02675e3aeeb7", "Not satisfied", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  littleSatisfied = install(new ConceptBuilder("971caad2-d10f-4202-9d53-b6c80545a23c")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("9ff8aa89-72b7-495b-b1a7-62c6b1aea12c", "Little satisfied", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  satisfied = install(new ConceptBuilder("267d6802-7ecb-450e-9b4e-5436c762b511")
                .datatype(coded)
                .conceptClass(misc)
                .name("9b3860a3-be91-44fd-ad39-b4a1a0ac5a31", "Are you happy with teen club?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        Concept  rateSupport = install(new ConceptBuilder("5a86edcb-21e8-4e3a-9472-de9a6d3fbb2b")
                .datatype(coded)
                .conceptClass(misc)
                .name("46baf959-5559-4d2c-9d59-17c87f50e898", "How do you rate the support on a scale of 1 to 3?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(verySatisfied, notSatisfied, littleSatisfied)
                .build());

        Concept  rateLifeChanges = install(new ConceptBuilder("4fbb5e58-b08a-4043-9798-ec22ff5571bf")
                .datatype(coded)
                .conceptClass(misc)
                .name("ac85b6fd-a26a-41cd-a5ed-002bebb9178d", "Can you rate the changes that happened to your life from the social support a scale of 1 to 3?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(verySatisfied, notSatisfied, littleSatisfied)
                .build());

        Concept  challenges = install(new ConceptBuilder("a73a438b-d3f6-47b5-bcfd-bbceae8e7458")
                .datatype(text)
                .conceptClass(misc)
                .name("128743a1-9844-44f3-9570-0d812a36a7c2", "Challenges you face in daily life", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  veryLow = install(new ConceptBuilder("1e9d2c9b-106d-4ad1-8384-0791f9db7ff3")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5e9ccba4-6e03-4efc-aab9-2ade0a1f94a9", "Very low", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  low = install(new ConceptBuilder("05b2aac2-56c6-43da-a1dc-6c4eca4efaca")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("cbe19f38-1c73-4d9d-aca8-11e3fce4463f", "low", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  high = install(new ConceptBuilder("a41eaeb1-1769-4b02-b511-8028e4ae85ec")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("20afac10-6780-493d-a84c-86260ac6cd85", "High", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  noResponse = install(new ConceptBuilder("81bc5d0f-6a58-4504-9f24-ea6345cb2647")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("c6e1ff7d-5ac7-40d3-a6cb-e2ee8ccecec5", "No Response", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  rateHIVKnowledge = install(new ConceptBuilder("5705c64d-41f8-4332-be83-c5ebb2df9693")
                .datatype(coded)
                .conceptClass(misc)
                .name("45251c20-8cf3-4115-9a40-18e044ed8a58", "Rate your HIV knowledge", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(veryLow, low,high,noResponse)
                .build());

        Concept  mosquitoBite = install(new ConceptBuilder("86daa7ce-0c8a-429a-9679-ad02d8fb0b73")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("f711a40a-aa8c-48df-8c32-d92b802b2793", "Mosquito bite", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  kissing = install(new ConceptBuilder("764ecc35-f17d-4193-bf18-d9d701b6d564")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("67eba481-8989-4e59-87c0-e9a92c90412a", "Kissing", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  sexualIntercourse = install(new ConceptBuilder("eadc4425-2228-4741-950e-d1f3be048e65")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5bdc6272-6e8f-44f9-921b-a2df86060134", "Sexual Intercourse", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  bloodTransfusion = install(new ConceptBuilder("938d8bcb-d128-4ac7-8179-a5ccfd10604f")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("28a0ddb3-01fa-4ee0-952c-ce01fa9bb3ee", "Through blood transfusion", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  m2cTransmission = install(new ConceptBuilder("2c01d171-701e-4848-acde-d5a2258d7b0e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2adc6bba-9c20-4ef1-9cb2-db8846459895", "Mother-to-child transmission", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  hivTransmission = install(new ConceptBuilder("2aefa143-9f49-44c6-8848-56f2596ff1c5")
                .datatype(coded)
                .conceptClass(misc)
                .name("f92a4dbf-155e-4e76-bc2f-b84efd7ef8f1", "How is HIV transmitted?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(mosquitoBite, kissing, sexualIntercourse, bloodTransfusion, m2cTransmission)
                .build());

        Concept  thinkingAboutFuture = install(new ConceptBuilder("f4d4508e-c2a3-464d-804c-8f68e6fcb070")
                .datatype(coded)
                .conceptClass(misc)
                .name("12b0b279-b33b-4a52-b7b9-1982eaaa9d35", "Have you started thinking about your future career and job aspirations?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept  job = install(new ConceptBuilder("7d2a7965-9e3a-4f7b-822f-f5728f2c8596")
                .datatype(text)
                .conceptClass(misc)
                .name("3f0eedcc-bc08-458a-afa9-d75b7910970f", "What career/job would you like to do when you finish school?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  scienceAndMath = install(new ConceptBuilder("ca2fcfbe-3d92-46fa-8fe3-844aac33376c")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("fb2b039a-aac7-42a2-8a46-5550b5787400", "Science and Math", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  artsAndHumanities = install(new ConceptBuilder("5d56164d-4b9e-4b50-ba37-b9df6597642a")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("7e4cdcbf-0563-4953-a2b2-9a74165145ad", "Arts and Humanities", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  businessAndEntrepreneurship = install(new ConceptBuilder("df51523a-559e-4e81-b883-f2615a3a5043")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("538df70c-674a-431c-bd18-96ce01087d04", "Business and Entrepreneurship", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  areasOfStudyInterest = install(new ConceptBuilder("fb69f4f8-118f-4362-b054-1b29ece01de7")
                .datatype(coded)
                .conceptClass(misc)
                .name("2077f695-450d-4863-8b1d-392a71c90d97", "What subjects or areas of study interest you the most?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(scienceAndMath, artsAndHumanities, businessAndEntrepreneurship, other)
                .build());

        Concept  healthAndMedicine = install(new ConceptBuilder("1d0345e8-7565-454c-9bf7-19f4dfcfc61e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("693f2e3b-8027-4ac8-9ba3-7e14e5c21ca2", "Healthcare and medicine", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  techAndIT = install(new ConceptBuilder("5f201508-2345-4501-bdd0-f5a8623c7162")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("79c7d9e5-fec6-40c8-8620-3996ac1f2a1b", "Technology and IT", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  artsAndDesign = install(new ConceptBuilder("67be71c3-9ec6-4cca-9f2e-9cb0a52a3f4e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("bb4107af-fcd1-40a9-9349-218cabfadc83", "Creative arts and design", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  industries = install(new ConceptBuilder("dab43cd5-1de7-440a-af65-b50f224c025a")
                .datatype(coded)
                .conceptClass(misc)
                .name("207a00b8-03c5-45e7-a630-dfef902d5782", "Are there any specific careers or industries that you are curious about or would like to learn more about?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(healthAndMedicine, techAndIT,artsAndDesign, businessAndEntrepreneurship, other)
                .build());

        Concept  haveRoleModels = install(new ConceptBuilder("8dcf744f-4f22-484a-8d6d-c14ebbc53f44")
                .datatype(coded)
                .conceptClass(misc)
                .name("779ad5cc-5202-4a89-88ee-9992585af083", "Are there any role models or professionals in your desired field that you look up to?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept  roleModels = install(new ConceptBuilder("3530488d-4213-4316-adb3-9cf434400a79")
                .datatype(text)
                .conceptClass(misc)
                .name("a6cc5804-0ffd-4171-a45e-a31c1a3ae650", "Name of role model(s)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  mentorship = install(new ConceptBuilder("df52436f-99fd-425d-ad3b-b96bcd089605")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("c3f19738-5e94-4838-9f36-f9185b6d5fbd", "Mentorship and guidance from professionals or role models", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  educationalScholarship = install(new ConceptBuilder("b1f909bc-7921-4f3d-aa57-d84f82334659")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("38a38341-e7c8-40dd-bc0d-de7b94be187b", "Access to educational scholarships or vocational training programs", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  peerSupport = install(new ConceptBuilder("ed2e3078-6b47-49dc-a6a4-ebfaf8648058")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("195fa60b-6d73-47f0-9438-9715aa0a9acc", "Peer support groups or networks", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  finacialSupport = install(new ConceptBuilder("b198d051-b460-4330-a802-d558faf29432")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("be5ca9f5-1093-4403-bf8f-3d2fd6ebda55", "Financial support", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  resources = install(new ConceptBuilder("78814f7a-151a-4298-86a4-c265675ab54a")
                .datatype(coded)
                .conceptClass(misc)
                .name("a694c978-f30f-4532-9176-e536c3339944", "What kind of support or resources do you feel would be helpful in achieving your aspirations and goals?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(mentorship, educationalScholarship, peerSupport, finacialSupport,other)
                .build());

        Concept  yesRelevantTraining = install(new ConceptBuilder("240fe672-00be-4201-838b-3e0a686d05b3")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8bceb676-9004-4578-9272-4a41c5e4004f", "Yes, I have relevant training or certifications", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  yesPlanning = install(new ConceptBuilder("8779854a-9e9e-40fe-b3e4-2594b79d2dd7")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("4000f856-52a2-458e-9833-46da7e8c0148", "No, but I'm planning to pursue training or certifications", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  notInterested = install(new ConceptBuilder("c024e008-b537-4f8f-83e9-03451e55f826")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1862757d-014c-4ebc-9da0-529bdb018a6a", "No, I am not interested in vocational training or certifications", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  gotTraining = install(new ConceptBuilder("6c05aef4-e1ca-4772-aef5-581266b44135")
                .datatype(coded)
                .conceptClass(misc)
                .name("6c4e0296-a05d-4625-8c5f-2103597609b0", "Have you obtained any vocational training or certifications in your desired field?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yesRelevantTraining, yesPlanning, notInterested)
                .build());

        Concept  courses = install(new ConceptBuilder("7eaafacb-5a43-4f7a-abd7-3c340ea68420")
                .datatype(text)
                .conceptClass(misc)
                .name("61ed5abe-9cfe-4d22-b63f-993a68335b7c", "What courses or what do you want to do life?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  exactly = install(new ConceptBuilder("1d1d41cd-9493-489a-ae58-f8442b50dcd6")
                .datatype(text)
                .conceptClass(misc)
                .name("a54c66bd-f120-4643-93ef-463623da12c7", "What exactly do you want to do in this life?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  lackOfExp = install(new ConceptBuilder("dee8c72b-c1fb-48fb-a3ef-f56fd40ad20d")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("c2225361-0616-4fc4-af95-20582c6b7f60", "Lack of experience or qualifications", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  limitedJobOpp = install(new ConceptBuilder("a5d749ef-c840-4896-be3c-75cb9c13505d")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("094705ae-3f01-44ed-ad86-e17bf53528d1", "Limited job opportunities", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  financialConstraints = install(new ConceptBuilder("a517de4c-093b-46eb-b6b4-a98a65b7422c")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("464bcce5-65aa-43b4-96fa-02c54c9dfdaf", "Finacial contraints", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  lackOfMentorship = install(new ConceptBuilder("b4068cf3-ee69-4b23-b732-21cdfa321ba0")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("732dd5af-7204-4e28-a21a-5d52bfd30eb8", "Lack of guidance or mentorship", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  challengesInPursuingGoals = install(new ConceptBuilder("9230e9b6-5d9e-4a9b-995c-c03bedd5eadd")
                .datatype(coded)
                .conceptClass(misc)
                .name("6cab5e42-0e9c-475f-a724-6d7985df1bc7", "What are some challenges you face or have faced in pursuing your career goals outside of school?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(limitedJobOpp, lackOfExp, financialConstraints, lackOfMentorship, other)
                .build());


        Concept  extracurricularActivities = install(new ConceptBuilder("4bd44d80-5409-4c7f-a2d2-d623fdfdfc93")
                .datatype(coded)
                .conceptClass(misc)
                .name("3c9accaf-8b0a-48eb-a372-ebd99a64e23e", "Are you currently involved in any extracurricular activities or clubs related to your career interests?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept  extracurricularActivitiesList = install(new ConceptBuilder("a8de56b1-833c-4293-8cc3-1281cd8cbd02")
                .datatype(text)
                .conceptClass(misc)
                .name("a7d72a73-c428-470d-b622-ac9024b73a45", "Extracurricular activities", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  accommodation = install(new ConceptBuilder("ab31cf84-823a-491f-8713-ed209e15eac0")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0cf0cbd1-e650-4eb0-a856-7c257ef54227", "Accommodation( housing repair and basic household items)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  vocationalTraining = install(new ConceptBuilder("af310bb8-9593-4430-bd63-db91a557db99")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("9832b238-e7b4-4f18-9014-606d09811a93", "vocational Training", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  agriNutriFishing = install(new ConceptBuilder("88e769d2-fea2-4500-abe5-d3b174607deb")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("884b663b-c21b-4684-b8ad-c3065f726889", "Agriculture/nutrition support(Fishing)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  agriNutriCropFarming = install(new ConceptBuilder("ec2f3f77-6a07-468b-ab68-da61a67ead2e")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("42fa8136-3ada-4c6b-aed0-cc813dd5e836", "Agriculture/nutrition support(Crop Farming)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  agriNutriAnimalFarming = install(new ConceptBuilder("a49c8069-789e-4cf9-bc68-f16cc7342696")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("c1b860cf-983a-4df9-9e4b-44500308b188", "Agriculture/nutrition support(Animal Farming)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  linkageToJobs = install(new ConceptBuilder("23b50f00-e02e-43ea-89d2-ccf82ffd9a5f")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("31dd5faa-0084-4f7d-96d3-a88ee8ec13c7", "Linkage to jobs", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept  supportFromTeensClub = install(new ConceptBuilder("88220f57-2d23-4949-83cc-a9ddf9081a60")
                .datatype(coded)
                .conceptClass(misc)
                .name("eb8a85e9-9dbc-408b-885e-9d78195bb5ae", "What support would you like to get from teens club?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(schoolSupport, accommodation, vocationalTraining, agriNutriFishing, agriNutriCropFarming, agriNutriAnimalFarming, linkageToJobs,other)
                .build());

        Concept  ownARadio = install(new ConceptBuilder("4b09025d-f92c-444c-a972-828a98a88bf0")
                .datatype(coded)
                .conceptClass(misc)
                .name("ecf90fce-5079-422a-ad4c-0478f1c1efa7", "Own a radio", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());

        Concept  ownATv = install(new ConceptBuilder("5c46ba89-e523-463c-ba37-635c90d23443")
                .datatype(coded)
                .conceptClass(misc)
                .name("bc88d01e-b713-4b1a-a159-f13b6fdad44e", "Own a Tv", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());

        Concept  ownAMobileTelephone = install(new ConceptBuilder("205ab520-0ac5-4004-90e4-f2ba10f79a2b")
                .datatype(coded)
                .conceptClass(misc)
                .name("f635e0e3-7af6-4de0-8a8c-9f63483df34c", "Own a mobile telephone", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());

        Concept  ownARefrigerator = install(new ConceptBuilder("d531e6b5-cf09-41de-8f27-f846b42c6c6f")
                .datatype(coded)
                .conceptClass(misc)
                .name("169a8439-d1f5-44be-889b-65a6ed7949d5", "Own a refrigerator", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());

        Concept  ownABicycle = install(new ConceptBuilder("b12adc9a-233b-4ac1-bed6-680dd00c7946")
                .datatype(coded)
                .conceptClass(misc)
                .name("519c1076-a7cb-4648-b478-2e2173434495", "Own a bicycle", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());

        Concept  ownAMotorcycle = install(new ConceptBuilder("75c35596-4e81-4764-9e0a-7b06b87ddceb")
                .datatype(coded)
                .conceptClass(misc)
                .name("8ca08fd7-3ce6-49cf-acd4-f818823f33ee", "Own a motorcycle", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());

        Concept  ownAnimalDrawnCart = install(new ConceptBuilder("a85c5276-3f4a-4e5a-bfd4-dc0595662cb3")
                .datatype(coded)
                .conceptClass(misc)
                .name("01b4dec7-eea0-487a-94c7-b661da764043", "Own an animal-drawn Cart", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());

        Concept car = install(new ConceptBuilder("05742f26-375a-4e38-b9ee-7c7d0780c538")
                .datatype(coded)
                .conceptClass(misc)
                .name("8198e333-bb82-4da9-aeba-f4d6feaffaeb", "Own an car/truck", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no, doNotKnow)
                .build());
    }
}
