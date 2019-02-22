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
public class AdvancedNCDMastercardConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 2;
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
                        otherDiagnosisText)
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
        Concept otherNonCodedChange = MetadataUtils.existing(Concept.class, "656cce7e-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("655e8076-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("65fb9186-977f-11e1-8993-905e29aff6c1", "Has the treatment changed at this visit?", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        nevirapineIncreased,
                        otherNonCodedChange,
                        yes,
                        no)
                .build());
    }
}
