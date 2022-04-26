package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class ChronicHeartFailureConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 6;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept raised = MetadataUtils.existing(Concept.class, "65779f0c-977f-11e1-8993-905e29aff6c1");
        Concept lower = MetadataUtils.existing(Concept.class, "6566289e-977f-11e1-8993-905e29aff6c1");
        Concept same = MetadataUtils.existing(Concept.class, "655e3b84-977f-11e1-8993-905e29aff6c1");
        Concept none = MetadataUtils.existing(Concept.class, "6557987e-977f-11e1-8993-905e29aff6c1");
        Concept weaklyPos = MetadataUtils.existing(Concept.class, "65598fb2-977f-11e1-8993-905e29aff6c1");
        Concept modPos = MetadataUtils.existing(Concept.class, "65597cde-977f-11e1-8993-905e29aff6c1");
        Concept strongPos = MetadataUtils.existing(Concept.class, "65598706-977f-11e1-8993-905e29aff6c1");
        Concept extremePos = MetadataUtils.existing(Concept.class, "E1B2AF76-4B69-4BA4-9DDC-D801649BC212");

        install(new ConceptBuilder("3790bbe2-13b3-45b4-94d1-a9719f5ac51d")
                .datatype(coded)
                .conceptClass(question)
                .name("be9004d2-62ce-4ab0-9377-5d3e9a1a6adf", "Mental health referral", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("d69886ae-5eda-40ab-b2df-6ea9794a30d5")
                .datatype(coded)
                .conceptClass(question)
                .name("5dc235be-8435-45c8-b0a9-f63361c73725", "Palliative care referral", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("5f91e983-f825-4f41-81de-077288a5d860")
                .datatype(coded)
                .conceptClass(question)
                .name("d4271ac3-8806-4b10-b0e5-1e6c59ecdca2", "Took medications today", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("bd057502-c23b-4f80-b24c-b996e8a84dfc")
                .datatype(coded)
                .conceptClass(finding)
                .name("674f7ff9-274b-402b-827c-8c6fd599b28f", "Oedema", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(none, weaklyPos, modPos, strongPos, extremePos)
                .build());

        install(new ConceptBuilder("e3ed7b54-5fcb-453f-9058-605f443bf78d")
                .datatype(coded)
                .conceptClass(finding)
                .name("30db9a58-4777-4b72-87ec-b850c663b040", "Bibasilar crackles", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());
       
        install(new ConceptBuilder("e513cb96-6782-4a2f-bc8e-e4f9ebaf0286")
                .datatype(coded)
                .conceptClass(question)
                .name("d7229b95-2056-4bb6-8570-c768326f6e59", "Salt or fluid restricted", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("95b1429a-34fd-4209-9cf9-20c66abc0348")
                .datatype(coded)
                .conceptClass(question)
                .name("0a83b2a7-454a-43ae-8fd6-22c5a54ab6c3", "Concern for depression or anxiety", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("dc277a10-f5dd-461c-b936-8bcdeb808b8b")
                .datatype(coded)
                .conceptClass(finding)
                .name("86e12ce2-c860-4c24-a989-bdf45bb8a44d", "Jugular venous pressure elevated", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("51f56e0e-0038-42fd-88d5-d0d7d47b56b6")
                .datatype(text)
                .conceptClass(drug)
                .name("4da6406b-19ca-4de4-b7c4-a2d68e419eb5", "Other chronic heart failure drugs", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("a02a2c07-521e-44e0-b83c-5b6a9302f8d6")
                .datatype(coded)
                .conceptClass(symptom)
                .name("e8bfaa00-2bab-4b09-9519-4d3849b8c5b2", "Dyspnea on extertion", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("b97edeb3-ab9d-4577-8b90-6dcd942aa8b3")
                .datatype(coded)
                .conceptClass(symptom)
                .name("6e416ebb-a09a-490e-97d9-a8f763a71f84", "Level of fatigue", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("d3e06bb8-97e2-40cf-a72b-3d9672ff6366")
                .datatype(coded)
                .conceptClass(symptom)
                .name("f336be70-b187-4960-8da2-1cace2437856", "Level of dry cough", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("91329a35-1f77-4cef-8870-1db5c5ceaf72")
                .datatype(coded)
                .conceptClass(symptom)
                .name("51050ac7-785b-4718-b638-46e4c6793232", "Level of orthopnea", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("f4674f9b-3eda-4b5c-b8ad-9f8532aba1a2")
                .datatype(coded)
                .conceptClass(symptom)
                .name("d9ea4ab6-bb4d-481a-94ac-8646f00c3d51", "Level of paroxysmal nocturnal dyspnea", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(raised, lower, same, none)
                .build());

        install(new ConceptBuilder("1be55e42-9a11-11e8-9eb6-529269fb1459")
                .datatype(text)
                .conceptClass(question)
                .name("1be55fc8-9a11-11e8-9eb6-529269fb1459", "Weight change", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());      

        //Echocardiogram construct - reinstalling with textual imaging result added to set members
        Concept nameOfDoctor = MetadataUtils.existing(Concept.class, "6563e23c-977f-11e1-8993-905e29aff6c1");
        Concept dateOfTest = MetadataUtils.existing(Concept.class, "6563e098-977f-11e1-8993-905e29aff6c1");
        Concept locactionTest = MetadataUtils.existing(Concept.class, "655dcc8a-977f-11e1-8993-905e29aff6c1");
        Concept otherLabs = MetadataUtils.existing(Concept.class, "655dfc32-977f-11e1-8993-905e29aff6c1");
        Concept previousEcho = MetadataUtils.existing(Concept.class, "6563deea-977f-11e1-8993-905e29aff6c1");
        Concept dbdEchoResult = MetadataUtils.existing(Concept.class, "6567b970-977f-11e1-8993-905e29aff6c1");

        Concept echoImaging = install(new ConceptBuilder("1995a751-6f80-49f5-bc89-5cc6ae767eff")
                .datatype(text)
                .conceptClass(diagnosis)
                .name("f34edfb3-6f88-4c8a-8bae-c05c6d7047dd", "ECHO imaging result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("6563e3ea-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("660764fc-977f-11e1-8993-905e29aff6c1", "Echocardiogram construct", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65c54554-977f-11e1-8993-905e29aff6c1", "Various questions related to echocardiograms.", Locale.ENGLISH)
                .setMembers(
                        nameOfDoctor, 
                        dateOfTest, 
                        locactionTest, 
                        otherLabs, 
                        previousEcho, 
                        dbdEchoResult, 
                        echoImaging)
                .build());

        //Heart failure diagnosis - reinstalling after adding a new answer of "Restrictive Cardiomyopathy"
        Concept cardiomyopathy = MetadataUtils.existing(Concept.class, "6569659a-977f-11e1-8993-905e29aff6c1");
        Concept rheumaticHeart = MetadataUtils.existing(Concept.class, "6546dad4-977f-11e1-8993-905e29aff6c1");
        Concept otherNonCodedDiagnosis = MetadataUtils.existing(Concept.class, "656cce7e-977f-11e1-8993-905e29aff6c1");
        Concept hypertensiveHeart = MetadataUtils.existing(Concept.class, "656298fa-977f-11e1-8993-905e29aff6c1");
        Concept mitralStenosis = MetadataUtils.existing(Concept.class, "65632b6c-977f-11e1-8993-905e29aff6c1");
        Concept idiopathicCardio = MetadataUtils.existing(Concept.class, "6562d9be-977f-11e1-8993-905e29aff6c1");
        Concept atrialFibrillation = MetadataUtils.existing(Concept.class, "6563362a-977f-11e1-8993-905e29aff6c1");
        Concept endomyocardialFibrosis = MetadataUtils.existing(Concept.class, "6562ddd8-977f-11e1-8993-905e29aff6c1");
        Concept acuteRheumaticFever = MetadataUtils.existing(Concept.class, "6563eade-977f-11e1-8993-905e29aff6c1");
        Concept pericardialDisease = MetadataUtils.existing(Concept.class, "6563e930-977f-11e1-8993-905e29aff6c1");
        Concept hypertension = MetadataUtils.existing(Concept.class, "654abfc8-977f-11e1-8993-905e29aff6c1");
        Concept congenitalHeart = MetadataUtils.existing(Concept.class, "6562dcd4-977f-11e1-8993-905e29aff6c1");
        Concept peripatumCardio = MetadataUtils.existing(Concept.class, "6562dac2-977f-11e1-8993-905e29aff6c1");
        Concept nonCardiac = MetadataUtils.existing(Concept.class, "6563f236-977f-11e1-8993-905e29aff6c1");
        Concept hivAssociated = MetadataUtils.existing(Concept.class, "6562dbd0-977f-11e1-8993-905e29aff6c1");
        Concept pulmonaryHyper = MetadataUtils.existing(Concept.class, "6563ed2c-977f-11e1-8993-905e29aff6c1");
        Concept endocarditis = MetadataUtils.existing(Concept.class, "6568c270-977f-11e1-8993-905e29aff6c1");
        Concept aorticAneurysm = MetadataUtils.existing(Concept.class, "6568c360-977f-11e1-8993-905e29aff6c1");
        Concept nonRheumaticValvular = MetadataUtils.existing(Concept.class, "65695460-977f-11e1-8993-905e29aff6c1");
        Concept postOperation = MetadataUtils.existing(Concept.class, "6563379c-977f-11e1-8993-905e29aff6c1");
        Concept alcoholicCardio = MetadataUtils.existing(Concept.class, "6568c73e-977f-11e1-8993-905e29aff6c1");
        Concept corPulmonale = MetadataUtils.existing(Concept.class, "6568c554-977f-11e1-8993-905e29aff6c1");
        Concept dilatedCardio = MetadataUtils.existing(Concept.class, "65703154-977f-11e1-8993-905e29aff6c1");
        Concept valvularCHFdiag = MetadataUtils.existing(Concept.class, "6578972c-977f-11e1-8993-905e29aff6c1");
        Concept unknownCHFdiag = MetadataUtils.existing(Concept.class, "65576584-977f-11e1-8993-905e29aff6c1");

        Concept restrictiveCardio = install(new ConceptBuilder("57476df7-e885-4444-b151-152cfcdac05b")
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("6abfb879-6bc7-4a4c-a3e0-1b38b00b80da", "Restrictive cardiomyopathy", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("6567bb82-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("66099aba-977f-11e1-8993-905e29aff6c1", "Heart failure diagnosis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)   
                .description("65c7711c-977f-11e1-8993-905e29aff6c1", "Coded description of preliminary diagnosis of heart failure ", Locale.ENGLISH)
                .answers(
                        cardiomyopathy,
                        rheumaticHeart,
                        otherNonCodedDiagnosis,
                        hypertensiveHeart,
                        mitralStenosis,
                        idiopathicCardio,
                        atrialFibrillation,
                        endomyocardialFibrosis,
                        acuteRheumaticFever,
                        pericardialDisease,
                        hypertension,
                        congenitalHeart,
                        peripatumCardio,
                        nonCardiac,
                        hivAssociated,
                        pulmonaryHyper,
                        endocarditis,
                        aorticAneurysm,
                        nonRheumaticValvular,
                        postOperation,
                        alcoholicCardio,
                        corPulmonale,
                        dilatedCardio,
                        restrictiveCardio,
						unknownCHFdiag,
						valvularCHFdiag)
                .build());

        //add Benzathine Penicillin to current chronic drugs used
        Concept dapsone = MetadataUtils.existing(Concept.class, "6545f8b2-977f-11e1-8993-905e29aff6c1");
        Concept streptomycin = MetadataUtils.existing(Concept.class, "65484176-977f-11e1-8993-905e29aff6c1");
        Concept isoniazid = MetadataUtils.existing(Concept.class, "65498bbc-977f-11e1-8993-905e29aff6c1");
        Concept ethambutol = MetadataUtils.existing(Concept.class, "6549f0b6-977f-11e1-8993-905e29aff6c1");
        Concept fluconazole = MetadataUtils.existing(Concept.class, "6549f2dc-977f-11e1-8993-905e29aff6c1");
        Concept rifampicinIsoniazidPyE = MetadataUtils.existing(Concept.class, "6557b32c-977f-11e1-8993-905e29aff6c1");
        Concept rifampicinisoniazid = MetadataUtils.existing(Concept.class, "655852b4-977f-11e1-8993-905e29aff6c1");
        Concept rifampicinIsoP = MetadataUtils.existing(Concept.class, "654a1190-977f-11e1-8993-905e29aff6c1");
        Concept triSulfamethoxazole = MetadataUtils.existing(Concept.class, "654b02da-977f-11e1-8993-905e29aff6c1");
        Concept nystatin = MetadataUtils.existing(Concept.class, "654b0618-977f-11e1-8993-905e29aff6c1");
        Concept antibiotics = MetadataUtils.existing(Concept.class, "655853cc-977f-11e1-8993-905e29aff6c1");
        Concept antimalarialMedications = MetadataUtils.existing(Concept.class, "656e1702-977f-11e1-8993-905e29aff6c1");
        Concept herbalTraditionalMedications = MetadataUtils.existing(Concept.class, "656e191e-977f-11e1-8993-905e29aff6c1");
        Concept multivitamin = MetadataUtils.existing(Concept.class, "65485a58-977f-11e1-8993-905e29aff6c1");
        Concept mineralsIronSupplements = MetadataUtils.existing(Concept.class, "656e1b44-977f-11e1-8993-905e29aff6c1");
        Concept otherNonCodedDrug = MetadataUtils.existing(Concept.class, "656cce7e-977f-11e1-8993-905e29aff6c1");
        Concept unknown = MetadataUtils.existing(Concept.class, "65576584-977f-11e1-8993-905e29aff6c1");
        Concept longActingInsulin = MetadataUtils.existing(Concept.class, "6573132e-977f-11e1-8993-905e29aff6c1");
        Concept insulinSoluble = MetadataUtils.existing(Concept.class, "6547414a-977f-11e1-8993-905e29aff6c1");
        Concept metformin = MetadataUtils.existing(Concept.class, "65694308-977f-11e1-8993-905e29aff6c1");
        Concept glibenclamide = MetadataUtils.existing(Concept.class, "65693cf0-977f-11e1-8993-905e29aff6c1");
        Concept diuretics = MetadataUtils.existing(Concept.class, "163212AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept ccBlockers = MetadataUtils.existing(Concept.class, "163213AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept aceInhibitors = MetadataUtils.existing(Concept.class, "162298AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept betaBlockers = MetadataUtils.existing(Concept.class, "163211AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept aspirin = MetadataUtils.existing(Concept.class, "6545efde-977f-11e1-8993-905e29aff6c1");
        Concept statins = MetadataUtils.existing(Concept.class, "162307AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept chlorpromazine = MetadataUtils.existing(Concept.class, "654b00aa-977f-11e1-8993-905e29aff6c1");
        Concept haloperidol = MetadataUtils.existing(Concept.class, "65693df4-977f-11e1-8993-905e29aff6c1");
        Concept fluphenazine = MetadataUtils.existing(Concept.class, "654b0eb0-977f-11e1-8993-905e29aff6c1");
        Concept carbamazepine = MetadataUtils.existing(Concept.class, "654b0726-977f-11e1-8993-905e29aff6c1");
        Concept sodiumValproate = MetadataUtils.existing(Concept.class, "65694b32-977f-11e1-8993-905e29aff6c1");
        Concept risperidone = MetadataUtils.existing(Concept.class, "83405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept fluoxetine = MetadataUtils.existing(Concept.class, "65693bec-977f-11e1-8993-905e29aff6c1");
        Concept phenobarbital = MetadataUtils.existing(Concept.class, "6546f3c0-977f-11e1-8993-905e29aff6c1");
        Concept phenytoin = MetadataUtils.existing(Concept.class, "65473768-977f-11e1-8993-905e29aff6c1");
        Concept benzathinePcn = MetadataUtils.existing(Concept.class, "656930ac-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("65585192-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("65f5c1a2-977f-11e1-8993-905e29aff6c1", "Current drugs used", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)   
                .description("65b496f0-977f-11e1-8993-905e29aff6c1", " Question on encounter forms: \"Is the patient currently taking, or has the patient ever taken, any of the following other medications?\" This particular concept stores a history of active use of the associated medications.", Locale.ENGLISH)
                .answers(
                        dapsone,
                        streptomycin,
                        isoniazid,
                        ethambutol,
                        fluconazole,
                        rifampicinisoniazid,
                        rifampicinIsoP,
                        triSulfamethoxazole,
                        nystatin,
                        antibiotics,
                        antimalarialMedications,
                        herbalTraditionalMedications,
                        multivitamin,
                        mineralsIronSupplements,
                        otherNonCodedDrug,
                        unknown,
                        longActingInsulin,
                        insulinSoluble,
                        metformin,
                        glibenclamide,
                        diuretics,
                        ccBlockers,
                        aceInhibitors,
                        betaBlockers,
                        aspirin,
                        statins,
                        chlorpromazine,
                        haloperidol,
                        fluphenazine,
                        carbamazepine,
                        sodiumValproate,
                        risperidone,
                        fluoxetine,
                        phenobarbital,
                        phenytoin,
                        benzathinePcn)
        .build());

        install(new ConceptBuilder("50FD8EE2-8E27-4811-991E-42011829CF37")
                .datatype(text)
                .conceptClass(diagnosis)
                .name("DED17A9F-272F-49C4-BA82-AE58AC580037", "Chest X-ray(CXR) imaging result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("D3A49FE2-819B-491A-941F-E0B8A0F76C38")
                .datatype(text)
                .conceptClass(diagnosis)
                .name("6BFF5FAB-25DE-4BBC-A27C-23F10F26E78C", "Electrocardiographic(EKG) imaging result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("4c5236cf-412f-4ed2-ab20-869c42f09e3e")
                .datatype(coded)
                .conceptClass(question)
                .name("250b9cea-6ea7-43f5-ac00-cd186f2bdec2", "Right ventricle dimension", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f99b16d5-5a54-470a-8586-38f7f0034f31", "Ventricle droit dimension", Locale.FRENCH, null)
                .name("f4ac887e-d213-4f1a-9535-43269af114e3", "Right ventricular volume estimated from ultrasound (qualitative)", Locale.ENGLISH, null)
                .name("c4f590c6-35fa-4115-a4b5-84866d9d3500", "Ventricle droit taille", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("0d917827-c005-4659-8553-e94080a8eb16").type(sameAs).ensureTerm(pih, "11997").build())
                .mapping(new ConceptMapBuilder("3f899241-f832-413c-b686-556624f831be").type(sameAs).ensureTerm(ciel, "166871").build())
                .mapping(new ConceptMapBuilder("ea2f2980-2d94-4a36-994a-f3c546e09e4d").type(sameAs).ensureTerm(pih, "Right ventricle dimension").build())
                .build());

        install(new ConceptBuilder("ed61396e-0835-4237-bc3d-93e990f64d1d")
                .datatype(notApplicable)
                .conceptClass(finding)
                .name("d5962e3a-02fe-489a-a017-45148a763fef", "No pericardial effusion", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("0a8d73dd-aaf6-4579-8293-273c60a0b8bf", "Pas d’épanchement péricardique", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("8b71fb25-461d-404b-825e-cea354dff15b").type(narrowerThan).ensureTerm(snomedCt, "169254007").build())
                .mapping(new ConceptMapBuilder("eefe36c9-7410-47d1-a1c0-63814d8a80ce").type(sameAs).ensureTerm(pih, "No pericardial effusion").build())
                .mapping(new ConceptMapBuilder("bbd5efd9-0b91-4da8-9aa0-3bf8faece453").type(sameAs).ensureTerm(pih, "12000").build())
                .mapping(new ConceptMapBuilder("ff876364-0d2a-4aee-87fe-a982de7a2c12").type(sameAs).ensureTerm(ciel, "166946").build())
                .build());
    }
}
