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
public class NCDOtherMastercardConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 3;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

        //chronic care diagnoses
        Concept asthma = MetadataUtils.existing(Concept.class, "65459124-977f-11e1-8993-905e29aff6c1");
        Concept hypertension = MetadataUtils.existing(Concept.class, "654abfc8-977f-11e1-8993-905e29aff6c1");
        Concept epilepsy = MetadataUtils.existing(Concept.class, "6546938a-977f-11e1-8993-905e29aff6c1");
        Concept diabetes = MetadataUtils.existing(Concept.class, "6567426a-977f-11e1-8993-905e29aff6c1");
        Concept type1diabetes = MetadataUtils.existing(Concept.class, "65714206-977f-11e1-8993-905e29aff6c1");
        Concept type2diabetes = MetadataUtils.existing(Concept.class, "65714314-977f-11e1-8993-905e29aff6c1");
        Concept heartFailure = MetadataUtils.existing(Concept.class, "6566257e-977f-11e1-8993-905e29aff6c1");
        Concept chronicKidneyDisease = MetadataUtils.existing(Concept.class, "6577c7a2-977f-11e1-8993-905e29aff6c1");
        Concept stroke = MetadataUtils.existing(Concept.class, "65714e68-977f-11e1-8993-905e29aff6c1");
        Concept chronicObstructive = MetadataUtils.existing(Concept.class, "65673e96-977f-11e1-8993-905e29aff6c1");
        Concept depression = MetadataUtils.existing(Concept.class, "6546cbd4-977f-11e1-8993-905e29aff6c1");
        Concept acutePsychotic = MetadataUtils.existing(Concept.class, "93e9be37-1369-11e4-a125-54ee7513a7ff");
        Concept otherMentalNonCoded = MetadataUtils.existing(Concept.class, "aad4c0e9-1369-11e4-a125-54ee7513a7ff");
        Concept otherMentalDiagnosis1 = MetadataUtils.existing(Concept.class, "f97271c0-63ed-11e6-8b77-86f30ca893d3");
        Concept otherMentalDiagnosis2 = MetadataUtils.existing(Concept.class, "f972763e-63ed-11e6-8b77-86f30ca893d3");
        Concept otherNonCoded = MetadataUtils.existing(Concept.class, "656cce7e-977f-11e1-8993-905e29aff6c1");
        Concept schizophrenia = MetadataUtils.existing(Concept.class, "654860c0-977f-11e1-8993-905e29aff6c1");
        Concept schizoaffectiveDisorder = MetadataUtils.existing(Concept.class, "127132AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept organicMentalAcute = MetadataUtils.existing(Concept.class, "7057d712-c5dd-11e5-9912-ba0be0483c18");
        Concept organicMentalChronic = MetadataUtils.existing(Concept.class, "7057d8b6-c5dd-11e5-9912-ba0be0483c18");
        Concept bipolarAffectiveManic = MetadataUtils.existing(Concept.class, "115924AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept anxietyDisorder = MetadataUtils.existing(Concept.class, "6560bf08-977f-11e1-8993-905e29aff6c1");
        Concept alcoholInducedMental = MetadataUtils.existing(Concept.class, "121716AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept drugInducedMental = MetadataUtils.existing(Concept.class, "90ec5559-3ba2-4fc3-abc1-614727b17141");
        Concept reactionSevereStress = MetadataUtils.existing(Concept.class, "160196AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept dissociativeConversion = MetadataUtils.existing(Concept.class, "115261AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept somatoformDisorder = MetadataUtils.existing(Concept.class, "112874AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept puerperalDisorder = MetadataUtils.existing(Concept.class, "dae4ec94-659e-11e6-8b77-86f30ca893d3");
        Concept personalityDisorder = MetadataUtils.existing(Concept.class, "114193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept mentalRetardation = MetadataUtils.existing(Concept.class, "115809AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept psychologicalDevelopment = MetadataUtils.existing(Concept.class, "159238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept hyperkineticBehavior = MetadataUtils.existing(Concept.class, "117468AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept sickleCellDisease = MetadataUtils.existing(Concept.class, "65774b06-977f-11e1-8993-905e29aff6c1");
        Concept tropicalSplenomegaly = MetadataUtils.existing(Concept.class, "6567c9d8-977f-11e1-8993-905e29aff6c1");
        Concept cirrhosis = MetadataUtils.existing(Concept.class, "65673cac-977f-11e1-8993-905e29aff6c1");
        Concept rheumaticHeart = MetadataUtils.existing(Concept.class, "6546dad4-977f-11e1-8993-905e29aff6c1");
        Concept congestiveHeartFailure = MetadataUtils.existing(Concept.class, "6560c016-977f-11e1-8993-905e29aff6c1");
        Concept polycysticKidneyDisease = MetadataUtils.existing(Concept.class, "dae4eed8-659e-11e6-8b77-86f30ca893d3");
        Concept rheumatoidArthritis = MetadataUtils.existing(Concept.class, "6546c670-977f-11e1-8993-905e29aff6c1");
        Concept hepatitisB = MetadataUtils.existing(Concept.class, "6545b140-977f-11e1-8993-905e29aff6c1");
        Concept deepVeinThrombosis = MetadataUtils.existing(Concept.class, "65717898-977f-11e1-8993-905e29aff6c1");
        Concept congenitalHeart = MetadataUtils.existing(Concept.class, "6562dcd4-977f-11e1-8993-905e29aff6c1");
        Concept otherDiagnosisText = MetadataUtils.existing(Concept.class, "65780d0c-977f-11e1-8993-905e29aff6c1");
        Concept dilatedHeartD = MetadataUtils.existing(Concept.class, "65703154-977f-11e1-8993-905e29aff6c1");
        Concept restrictiveHeartD = MetadataUtils.existing(Concept.class, "57476df7-e885-4444-b151-152cfcdac05b");
        Concept valvularHeartD = MetadataUtils.existing(Concept.class, "6578972c-977f-11e1-8993-905e29aff6c1");
        Concept unknownDiag = MetadataUtils.existing(Concept.class, "65576584-977f-11e1-8993-905e29aff6c1");
        Concept irregularHeartRhythm = MetadataUtils.existing(Concept.class, "65680bfa-977f-11e1-8993-905e29aff6c1");
        Concept pulmonaryEmbolism = MetadataUtils.existing(Concept.class, "657179a6-977f-11e1-8993-905e29aff6c1");

        Concept coronaryArteryDisease = install(new ConceptBuilder("86c2fd36-e7f1-11e8-9f32-f2801f1b9fd1")
                .datatype(notApplicable)
                .conceptClass(diagnosis)
                .name("86c2ffe8-e7f1-11e8-9f32-f2801f1b9fd1", "Coronary artery disease", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("65671c9a-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("66094394-977f-11e1-8993-905e29aff6c1", "Chronic care diagnosis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        asthma,
                        hypertension,
                        epilepsy,
                        diabetes,
                        type1diabetes,
                        type2diabetes,
                        heartFailure,
                        chronicKidneyDisease,
                        stroke,
                        chronicObstructive,
                        depression,
                        acutePsychotic,
                        otherMentalNonCoded,
                        otherMentalDiagnosis1,
                        otherMentalDiagnosis2,
                        otherNonCoded,
                        schizophrenia,
                        schizoaffectiveDisorder,
                        organicMentalAcute,
                        organicMentalChronic,
                        bipolarAffectiveManic,
                        anxietyDisorder,
                        alcoholInducedMental,
                        drugInducedMental,
                        reactionSevereStress,
                        dissociativeConversion,
                        somatoformDisorder,
                        puerperalDisorder,
                        personalityDisorder,
                        mentalRetardation,
                        psychologicalDevelopment,
                        hyperkineticBehavior,
                        sickleCellDisease,
                        tropicalSplenomegaly,
                        cirrhosis,
                        rheumaticHeart,
                        congestiveHeartFailure,
                        polycysticKidneyDisease,
                        rheumatoidArthritis,
                        hepatitisB,
                        deepVeinThrombosis,
                        congenitalHeart,
                        coronaryArteryDisease,
                        otherDiagnosisText,
                        dilatedHeartD,
                        restrictiveHeartD,
                        valvularHeartD,
                        unknownDiag,
                        irregularHeartRhythm,
                        pulmonaryEmbolism)
                .build());

        //Next appointment location
        Concept ic3 = MetadataUtils.existing(Concept.class, "3064BCF8-56F7-43C9-A8CF-D90D42EEF739");

        Concept advancedNCDclinic = install(new ConceptBuilder("2bc82b28-e84f-11e8-9f32-f2801f1b9fd1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2bc82dbc-e84f-11e8-9f32-f2801f1b9fd1", "Advanced NCD clinic", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("f68aeeea-e84f-11e8-9f32-f2801f1b9fd1", "IC3 Visit Location ", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder("9f262c0e-e850-11e8-9f32-f2801f1b9fd1")
                .datatype(coded)
                .conceptClass(question)
                .name("9f262ec0-e850-11e8-9f32-f2801f1b9fd1", "Next appointment location", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        ic3,
                        advancedNCDclinic)
                .build());

        //Medications changed?
        Concept nevirapineIncreased = MetadataUtils.existing(Concept.class, "655e7e28-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("655e8076-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("65fb9186-977f-11e1-8993-905e29aff6c1", "Has the treatment changed at this visit?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        nevirapineIncreased,
                        otherNonCoded,
                        yes,
                        no)
                .build());

        //reinstall comorbidities
        Concept malaria = MetadataUtils.existing(Concept.class, "65461b8a-977f-11e1-8993-905e29aff6c1");
        Concept pruritis = MetadataUtils.existing(Concept.class, "654a8fd0-977f-11e1-8993-905e29aff6c1");
        Concept sexuallyTransmittedInfection = MetadataUtils.existing(Concept.class, "6546a848-977f-11e1-8993-905e29aff6c1");
        Concept kaposisSarcoma = MetadataUtils.existing(Concept.class, "6548955e-977f-11e1-8993-905e29aff6c1");
        Concept meningitis = MetadataUtils.existing(Concept.class, "6545d36e-977f-11e1-8993-905e29aff6c1");
        Concept tuberculosis = MetadataUtils.existing(Concept.class, "6545d15c-977f-11e1-8993-905e29aff6c1");
        Concept herpesZoster = MetadataUtils.existing(Concept.class, "654a5b14-977f-11e1-8993-905e29aff6c1");
        Concept candidiasisEsophageal = MetadataUtils.existing(Concept.class, "655a96f0-977f-11e1-8993-905e29aff6c1");
        Concept oralCandidiasis = MetadataUtils.existing(Concept.class, "656b23c6-977f-11e1-8993-905e29aff6c1");
        Concept Pneumonia = MetadataUtils.existing(Concept.class, "6545c176-977f-11e1-8993-905e29aff6c1");
        Concept candidiasisVaginal = MetadataUtils.existing(Concept.class, "654752d4-977f-11e1-8993-905e29aff6c1");
        Concept urethritis = MetadataUtils.existing(Concept.class, "6547a838-977f-11e1-8993-905e29aff6c1");
        Concept genitalSores = MetadataUtils.existing(Concept.class, "654a7f68-977f-11e1-8993-905e29aff6c1");
        Concept diarrheaChronic = MetadataUtils.existing(Concept.class, "6559bf46-977f-11e1-8993-905e29aff6c1");
        Concept trichomonas = MetadataUtils.existing(Concept.class, "6549aa2a-977f-11e1-8993-905e29aff6c1");
        Concept bacterialVaginosis = MetadataUtils.existing(Concept.class, "655da444-977f-11e1-8993-905e29aff6c1");
        Concept vaginalDischarge = MetadataUtils.existing(Concept.class, "656ed246-977f-11e1-8993-905e29aff6c1");
        Concept pneumopathy = MetadataUtils.existing(Concept.class, "655a843a-977f-11e1-8993-905e29aff6c1");
        Concept nodularRash = MetadataUtils.existing(Concept.class, "65592f2c-977f-11e1-8993-905e29aff6c1");
        Concept weightLossGreater = MetadataUtils.existing(Concept.class, "65591c3a-977f-11e1-8993-905e29aff6c1");
        Concept acutePneumonia = MetadataUtils.existing(Concept.class, "65591a28-977f-11e1-8993-905e29aff6c1");
        Concept subAcutePneumonia = MetadataUtils.existing(Concept.class, "65591b2c-977f-11e1-8993-905e29aff6c1");
        Concept typhoidFever = MetadataUtils.existing(Concept.class, "65463fca-977f-11e1-8993-905e29aff6c1");
        Concept pulmonaryTuberculosis = MetadataUtils.existing(Concept.class, "655a4bbe-977f-11e1-8993-905e29aff6c1");
        Concept extrapulmonaryTuberculosis = MetadataUtils.existing(Concept.class, "655a49ac-977f-11e1-8993-905e29aff6c1");
        Concept psychosis = MetadataUtils.existing(Concept.class, "6546d8ae-977f-11e1-8993-905e29aff6c1");
        Concept herpesSimplex = MetadataUtils.existing(Concept.class, "6559324c-977f-11e1-8993-905e29aff6c1");
        Concept syphilis = MetadataUtils.existing(Concept.class, "6546dcfa-977f-11e1-8993-905e29aff6c1");
        Concept humanPapillomavirus = MetadataUtils.existing(Concept.class, "65586790-977f-11e1-8993-905e29aff6c1");
        Concept pelvicInflammatoryDisease = MetadataUtils.existing(Concept.class, "654abe38-977f-11e1-8993-905e29aff6c1");
        Concept severeDysphagiaPresumed = MetadataUtils.existing(Concept.class, "65592072-977f-11e1-8993-905e29aff6c1");
        Concept meningitisSubAcuteTuberculosis = MetadataUtils.existing(Concept.class, "655924aa-977f-11e1-8993-905e29aff6c1");
        Concept meningitisSubAcuteCryptococcus = MetadataUtils.existing(Concept.class, "6559239c-977f-11e1-8993-905e29aff6c1");
        Concept diarrheaParasite = MetadataUtils.existing(Concept.class, "6545a416-977f-11e1-8993-905e29aff6c1");
        Concept diarrheaBacterial = MetadataUtils.existing(Concept.class, "6560d588-977f-11e1-8993-905e29aff6c1");
        Concept hIVEncephalopathy = MetadataUtils.existing(Concept.class, "655926c6-977f-11e1-8993-905e29aff6c1");
        Concept chlamydiaTrachomatis = MetadataUtils.existing(Concept.class, "6559313e-977f-11e1-8993-905e29aff6c1");
        Concept genitalDischarge = MetadataUtils.existing(Concept.class, "655bb45e-977f-11e1-8993-905e29aff6c1");
        Concept gonorrhea = MetadataUtils.existing(Concept.class, "654a9eda-977f-11e1-8993-905e29aff6c1");
        Concept genitalUlcersChancroid = MetadataUtils.existing(Concept.class, "6560d790-977f-11e1-8993-905e29aff6c1");
        Concept genitalUlcersLgv = MetadataUtils.existing(Concept.class, "6560d68c-977f-11e1-8993-905e29aff6c1");
        Concept hIVWastingSyndrome = MetadataUtils.existing(Concept.class, "654a4d04-977f-11e1-8993-905e29aff6c1");
        Concept syphilisWithoutUlcers = MetadataUtils.existing(Concept.class, "6561583c-977f-11e1-8993-905e29aff6c1");
        Concept syphilisWithUlcers = MetadataUtils.existing(Concept.class, "65615940-977f-11e1-8993-905e29aff6c1");
        Concept vaginalUrethralDischarge = MetadataUtils.existing(Concept.class, "6561616a-977f-11e1-8993-905e29aff6c1");
        Concept tuberculoma = MetadataUtils.existing(Concept.class, "65615fb2-977f-11e1-8993-905e29aff6c1");
        Concept toxoplasmosis = MetadataUtils.existing(Concept.class, "656b481a-977f-11e1-8993-905e29aff6c1");
        Concept convulsionsOrNeurological = MetadataUtils.existing(Concept.class, "65592e1e-977f-11e1-8993-905e29aff6c1");
        Concept meningitisAcuteBacterial = MetadataUtils.existing(Concept.class, "6559228e-977f-11e1-8993-905e29aff6c1");
        Concept rashOther = MetadataUtils.existing(Concept.class, "6561fc42-977f-11e1-8993-905e29aff6c1");
        Concept otherEnteritis = MetadataUtils.existing(Concept.class, "6561fb3e-977f-11e1-8993-905e29aff6c1");
        Concept encephalopathyPresumed = MetadataUtils.existing(Concept.class, "655927d4-977f-11e1-8993-905e29aff6c1");
        Concept scabies = MetadataUtils.existing(Concept.class, "654638ea-977f-11e1-8993-905e29aff6c1");
        Concept tineaCapitis = MetadataUtils.existing(Concept.class, "65464e70-977f-11e1-8993-905e29aff6c1");
        Concept helminthiasis = MetadataUtils.existing(Concept.class, "6546c33c-977f-11e1-8993-905e29aff6c1");
        Concept meningitisViral = MetadataUtils.existing(Concept.class, "6562c730-977f-11e1-8993-905e29aff6c1");
        Concept seizures = MetadataUtils.existing(Concept.class, "6546cabc-977f-11e1-8993-905e29aff6c1");
        Concept gastritisNos = MetadataUtils.existing(Concept.class, "6546a618-977f-11e1-8993-905e29aff6c1");
        Concept gastritisWithHemorrhage = MetadataUtils.existing(Concept.class, "6562c41a-977f-11e1-8993-905e29aff6c1");
        Concept meningitisOtherCause = MetadataUtils.existing(Concept.class, "6562c62c-977f-11e1-8993-905e29aff6c1");
        Concept cardiovascularDisease = MetadataUtils.existing(Concept.class, "654b5c58-977f-11e1-8993-905e29aff6c1");
        Concept diarrheaBloody = MetadataUtils.existing(Concept.class, "655d5f66-977f-11e1-8993-905e29aff6c1");
        Concept dermatitisAllergic = MetadataUtils.existing(Concept.class, "65461842-977f-11e1-8993-905e29aff6c1");
        Concept genitourinaryProblem = MetadataUtils.existing(Concept.class, "6562c83e-977f-11e1-8993-905e29aff6c1");
        Concept focalNeurologicalDeficit = MetadataUtils.existing(Concept.class, "654b3ea8-977f-11e1-8993-905e29aff6c1");
        Concept dermatophytosis = MetadataUtils.existing(Concept.class, "654b196e-977f-11e1-8993-905e29aff6c1");
        Concept feverOfUnknownOrigin = MetadataUtils.existing(Concept.class, "654abca8-977f-11e1-8993-905e29aff6c1");
        Concept gastritisWithoutHemorrhage = MetadataUtils.existing(Concept.class, "6562c528-977f-11e1-8993-905e29aff6c1");
        Concept skinRash = MetadataUtils.existing(Concept.class, "65489e00-977f-11e1-8993-905e29aff6c1");
        Concept dysenteryBacilliary = MetadataUtils.existing(Concept.class, "655929e6-977f-11e1-8993-905e29aff6c1");
        Concept tuberculousEnteritis = MetadataUtils.existing(Concept.class, "65666b9c-977f-11e1-8993-905e29aff6c1");
        Concept otitisExterna = MetadataUtils.existing(Concept.class, "6546d7a0-977f-11e1-8993-905e29aff6c1");
        Concept acuteDiarrheaMoreStools = MetadataUtils.existing(Concept.class, "6566b20a-977f-11e1-8993-905e29aff6c1");
        Concept encephalopathy = MetadataUtils.existing(Concept.class, "656b3794-977f-11e1-8993-905e29aff6c1");
        Concept encephalopathyNonFocal = MetadataUtils.existing(Concept.class, "655925b8-977f-11e1-8993-905e29aff6c1");
        Concept diabetesDiag = MetadataUtils.existing(Concept.class, "6567426a-977f-11e1-8993-905e29aff6c1");
        Concept ckdDiag = MetadataUtils.existing(Concept.class, "6577c7a2-977f-11e1-8993-905e29aff6c1");
        Concept otherDiagText = MetadataUtils.existing(Concept.class, "65780d0c-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("655a8d90-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("65f79b58-977f-11e1-8993-905e29aff6c1", "Current opportunistic infection or comorbidity, confirmed or presumed", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("660bcd94-977f-11e1-8993-905e29aff6c1", "OI or comorbidity", Locale.ENGLISH, null)
                .name("661152aa-977f-11e1-8993-905e29aff6c1", "Diagnostics and syndromes", Locale.ENGLISH, null)
                .name("661495d2-977f-11e1-8993-905e29aff6c1", "Presumptive diagnosis", Locale.ENGLISH, null)
                .name("66192412-977f-11e1-8993-905e29aff6c1", "Confirmed or presumed", Locale.ENGLISH, null)
                .name("661924f8-977f-11e1-8993-905e29aff6c1", "Current opportunistic infection or comorbidity", Locale.ENGLISH, null)
                .description("65b6f38c-977f-11e1-8993-905e29aff6c1", "An opportunistic infection or co-morbid condition that the patient had (either confirmed or presumptive diagnosis) at the time the question was aske", Locale.ENGLISH)
                .answers(
                        malaria,
                        pruritis,
                        sexuallyTransmittedInfection,
                        kaposisSarcoma,
                        meningitis,
                        tuberculosis,
                        herpesZoster,
                        candidiasisEsophageal,
                        oralCandidiasis,
                        Pneumonia,
                        candidiasisVaginal,
                        urethritis,
                        genitalSores,
                        diarrheaChronic,
                        trichomonas,
                        bacterialVaginosis,
                        otherNonCoded,
                        vaginalDischarge,
                        pneumopathy,
                        nodularRash,
                        weightLossGreater,
                        acutePneumonia,
                        subAcutePneumonia,
                        typhoidFever,
                        pulmonaryTuberculosis,
                        extrapulmonaryTuberculosis,
                        depression,
                        psychosis,
                        anxietyDisorder,
                        hypertension,
                        congestiveHeartFailure,
                        herpesSimplex,
                        syphilis,
                        humanPapillomavirus,
                        pelvicInflammatoryDisease,
                        severeDysphagiaPresumed,
                        meningitisSubAcuteTuberculosis,
                        meningitisSubAcuteCryptococcus,
                        diarrheaParasite,
                        diarrheaBacterial,
                        hIVEncephalopathy,
                        chlamydiaTrachomatis,
                        genitalDischarge,
                        gonorrhea,
                        genitalUlcersChancroid,
                        genitalUlcersLgv,
                        hIVWastingSyndrome,
                        syphilisWithoutUlcers,
                        syphilisWithUlcers,
                        vaginalUrethralDischarge,
                        tuberculoma,
                        toxoplasmosis,
                        convulsionsOrNeurological,
                        meningitisAcuteBacterial,
                        rashOther,
                        otherEnteritis,
                        encephalopathyPresumed,
                        scabies,
                        tineaCapitis,
                        helminthiasis,
                        meningitisViral,
                        seizures,
                        gastritisNos,
                        gastritisWithHemorrhage,
                        meningitisOtherCause,
                        cardiovascularDisease,
                        diarrheaBloody,
                        dermatitisAllergic,
                        genitourinaryProblem,
                        focalNeurologicalDeficit,
                        dermatophytosis,
                        feverOfUnknownOrigin,
                        gastritisWithoutHemorrhage,
                        skinRash,
                        dysenteryBacilliary,
                        tuberculousEnteritis,
                        otitisExterna,
                        acuteDiarrheaMoreStools,
                        encephalopathy,
                        encephalopathyNonFocal,
                        diabetesDiag,
                        ckdDiag,
                        otherDiagText)
                .build());

        install(new ConceptBuilder("DA1FC8AF-FC15-4AC0-B980-EB8B0FFC95CD")
                .datatype(coded)
                .conceptClass(question)
                .name("091F832E-9D5D-4B29-B9A8-97961844AA6A", "Any seizure triggers present", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        yes,
                        no)
                .build());
    }
}
