package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;


import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class IC3ScreeningConcepts extends VersionedPihConceptBundle {

    //public static final String REFERRAL_SOURCE_CONCEPT  = "65664fc2-977f-11e1-8993-905e29aff6c1";

    @Override
    public int getVersion() {
        return 22;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept other = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER);
        Concept unknown = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.UNKNOWN);

        Concept sharc = install(new ConceptBuilder("6f48dfac-9ffa-11e8-98d0-529269fb1459")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6f48e362-9ffa-11e8-98d0-529269fb1459", "SHARC", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description(
                        "6f48ef7e-9ffa-11e8-98d0-529269fb1459",
                        "Screening for Health and Referral in the Community (SHARC)", Locale.ENGLISH)
                .build());

        Concept outsideNeno = install(new ConceptBuilder("6f48e5b0-9ffa-11e8-98d0-529269fb1459")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6f48e808-9ffa-11e8-98d0-529269fb1459", "Outside Neno district", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept healthCenter = install(new ConceptBuilder("6566905e-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("660899e4-977f-11e1-8993-905e29aff6c1", "Health center", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("7317de41-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "3566").build())
                .build());

        Concept outpatient = install(new ConceptBuilder("655ac68e-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("65f7f652-977f-11e1-8993-905e29aff6c1", "Outpatient consultation", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65b74ea4-977f-11e1-8993-905e29aff6c1", "A mode of admission", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("72b2c2ce-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "1651").build())
                .build());

        Concept community = install(new ConceptBuilder("5fdce932-6a8c-4658-8b53-f8f893f0b217")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("264a6a77-cf14-4803-aba3-7945735df177", "Community Event", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .mapping(new ConceptMapBuilder("73b28b15-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "8397").build())
                .build());

        Concept prenatalClinic = install(new ConceptBuilder("655e0cea-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("66136bbc-977f-11e1-8993-905e29aff6c1", "Maternal health clinic", Locale.ENGLISH, null)
                .name("65fb1184-977f-11e1-8993-905e29aff6c1", "Antenatal clinic", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("66149316-977f-11e1-8993-905e29aff6c1", "Prenatal clinic", Locale.ENGLISH, null)
                .name("6610fac6-977f-11e1-8993-905e29aff6c1", "CPN program", Locale.ENGLISH, null)
                .description("65b9df48-977f-11e1-8993-905e29aff6c1", "Antenatal clinic", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("72d09f9c-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "2232").build())
                .build());

        Concept ward = install(new ConceptBuilder("6578ee5c-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("66261172-977f-11e1-8993-905e29aff6c1", "Ward", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("73972b3b-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "7829").build())
                .build());

        Concept healthClinic = install(new ConceptBuilder("655d166e-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("65fa5a28-977f-11e1-8993-905e29aff6c1", "Health clinic", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65b929cc-977f-11e1-8993-905e29aff6c1", "health clinic", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("72c83b47-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "2069").build())
                .build());

        Concept hospital = install(new ConceptBuilder("655d1772-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("65fa5b0e-977f-11e1-8993-905e29aff6c1", "Hospital", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65b92ab2-977f-11e1-8993-905e29aff6c1", "hospital", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("72c84797-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "2070").build())
                .build());

        Concept primaryCareClinic = install(new ConceptBuilder("655e1366-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("65fb159e-977f-11e1-8993-905e29aff6c1", "Primary care clinic", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65b9e0ba-977f-11e1-8993-905e29aff6c1", "Clinic that offers primary care for outpatients", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("72d0b676-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "2233").build())
                .build());

        Concept linkToCare = install(new ConceptBuilder("5f941306-3f64-11e9-b210-d663bd873d93")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5f9417ac-3f64-11e9-b210-d663bd873d93", "Linkage to care", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("5f94191e-3f64-11e9-b210-d663bd873d93", "Linkage of individuals to appropriate services", Locale.ENGLISH)
                .build());

        Concept ic3Appointment = install(new ConceptBuilder("5004B09D-16D6-4439-9481-C9EAA2E939B5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("419E7374-9965-459C-B70C-1D0E275B3A29", "IC3 Appointment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("5C040ED0-E3EB-468A-8484-AB96D58E8DA4", "Patient has an appointment at the IC3 clinic", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder("65664fc2-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("66093d9a-977f-11e1-8993-905e29aff6c1", "Type of referring clinic or hospital", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("66198902-977f-11e1-8993-905e29aff6c1", "Patient referred by", Locale.ENGLISH, null)
                .description("65c70b0a-977f-11e1-8993-905e29aff6c1", "Who was the new patient referred by?", Locale.ENGLISH)
                .description("65cb6236-977f-11e1-8993-905e29aff6c1", "Who was the new patient referred by?", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("7311c63f-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "3509").build())
                .answers(sharc,outsideNeno,hospital,
                        primaryCareClinic,healthClinic, other, ward, prenatalClinic,community,outpatient,healthCenter,linkToCare,ic3Appointment)
                .build());

        // HTC
        Concept noBlood = install(new ConceptBuilder("0e447720-a180-11e8-98d0-529269fb1459")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0e447b26-a180-11e8-98d0-529269fb1459", "Unable to draw blood", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept needsCounseling = install(new ConceptBuilder("bc7bd9f2-b21d-11e8-96f8-529269fb1459")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("bc7bddda-b21d-11e8-96f8-529269fb1459", "Needs counseling", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept patientRefused = MetadataUtils.existing(Concept.class, "6566a4ae-977f-11e1-8993-905e29aff6c1");
        Concept noMaterials    = MetadataUtils.existing(Concept.class, "655dc866-977f-11e1-8993-905e29aff6c1");

        Concept noSampleReason = install(new ConceptBuilder("0e447d92-a180-11e8-98d0-529269fb1459")
                .datatype(coded)
                .conceptClass(question)
                .name("0e447fe0-a180-11e8-98d0-529269fb1459", "Reason for no sample", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("0e4485da-a180-11e8-98d0-529269fb1459","Lack of sample reason", Locale.ENGLISH,null)
                .answers(noBlood,patientRefused,noMaterials,needsCounseling)
                .build());

        Concept noResultReason = MetadataUtils.existing(Concept.class, "656fa450-977f-11e1-8993-905e29aff6c1");
        Concept bled           = MetadataUtils.existing(Concept.class, "f792f2f9-9c24-4d6e-98fd-caffa8f2383f");
        Concept vl             = MetadataUtils.existing(Concept.class, "654a7694-977f-11e1-8993-905e29aff6c1");
        Concept ldl            = MetadataUtils.existing(Concept.class, "e97b36a2-16f5-11e6-b6ba-3e1d05defe78");
        Concept testReason     = MetadataUtils.existing(Concept.class, "164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept labLocation    = MetadataUtils.existing(Concept.class, "6fc0ab50-9492-11e7-abc4-cec278b6b50a");

        install(new ConceptBuilder("83931c6d-0e5a-4302-b8ce-a31175b6475e")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("4405c71b-3081-4fc9-87cf-3fdc1315817f", "Viral Load Test Set, IC3", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(bled, vl, ldl, testReason, labLocation, noResultReason, noSampleReason)
                .build());

        // TB Screening
        Concept cough           = MetadataUtils.existing(Concept.class, CommonMetadata.COUGH_CONCEPT);
        Concept cough2Weeks     = MetadataUtils.existing(Concept.class, "655fdac0-977f-11e1-8993-905e29aff6c1");
        Concept fever           = MetadataUtils.existing(Concept.class, CommonMetadata.FEVER_CONCEPT);
        Concept weightLoss      = MetadataUtils.existing(Concept.class, CommonMetadata.WEIGHT_LOSS_CONCEPT);
        Concept nightSweats     = MetadataUtils.existing(Concept.class, CommonMetadata.NIGHT_SWEATS_CONCEPT);

        Concept feverLastNight = install(new ConceptBuilder("bc7be082-b21d-11e8-96f8-529269fb1459")
                .datatype(notApplicable)
                .conceptClass(symptomFinding)
                .name("bc7be32a-b21d-11e8-96f8-529269fb1459", "Fever last night",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept recentTBcontact = install(new ConceptBuilder(CommonMetadata.RECENT_CONTACT_WITH_ACTIVE_TB_CONCEPT)
                .datatype(notApplicable)
                .conceptClass(finding)
                .name("bc7be960-b21d-11e8-96f8-529269fb1459", "Recent contact with active TB ",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept lymphNodePain = install(new ConceptBuilder(CommonMetadata.PAINFUL_NECK_AND_ARMPIT_LYMPH_NODES_CONCEPT)
                .datatype(notApplicable)
                .conceptClass(symptomFinding)
                .name("10458164-b223-11e8-96f8-529269fb1459", "Painful cervical and axillary lymph nodes",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept sob = MetadataUtils.existing(Concept.class, "656ea87a-977f-11e1-8993-905e29aff6c1");
        Concept hemoptysis = MetadataUtils.existing(Concept.class, "654b3c96-977f-11e1-8993-905e29aff6c1");
        Concept meningitis = MetadataUtils.existing(Concept.class, "6545d36e-977f-11e1-8993-905e29aff6c1");
        Concept fatigue = MetadataUtils.existing(Concept.class, "656e9c9a-977f-11e1-8993-905e29aff6c1");
        Concept recurFever = MetadataUtils.existing(Concept.class, "65791c92-977f-11e1-8993-905e29aff6c1");
        Concept chestPain = MetadataUtils.existing(Concept.class, "65463250-977f-11e1-8993-905e29aff6c1");
        Concept failToThrive = MetadataUtils.existing(Concept.class, "65698fb6-977f-11e1-8993-905e29aff6c1");
        Concept crackles = MetadataUtils.existing(Concept.class, "65639408-977f-11e1-8993-905e29aff6c1");
        Concept bronchialBreathing = MetadataUtils.existing(Concept.class, "65791b84-977f-11e1-8993-905e29aff6c1");
        Concept persistCough = MetadataUtils.existing(Concept.class, "6559be42-977f-11e1-8993-905e29aff6c1");

        Concept tbSymptoms = install(new ConceptBuilder("655a5712-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("65f76d36-977f-11e1-8993-905e29aff6c1", "TB symptoms", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65b6bc6e-977f-11e1-8993-905e29aff6c1", "Question about the patient's presenting symptoms for TB", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("72ae411b-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "1560").build())
                .setMembers(bronchialBreathing,sob,crackles,failToThrive,chestPain,recurFever,fatigue,hemoptysis,
                        nightSweats,persistCough,meningitis,cough,cough2Weeks,fever,weightLoss,feverLastNight,
                        recentTBcontact,lymphNodePain,other)
                .answers(fever,other,weightLoss,hemoptysis,sob)
                .build());

        Concept tbSymptomPresent = install(new ConceptBuilder("aa97f7c9-1a05-4ab6-9caa-66da6718a85f")
                .datatype(coded)
                .conceptClass(question)
                .name("10458678-b223-11e8-96f8-529269fb1459", "TB symptom present",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept tbSymptomAbsent = install(new ConceptBuilder("25aef676-3253-42c0-8c8f-0f775d0b696a")
                .datatype(coded)
                .conceptClass(question)
                .name("1045879a-b223-11e8-96f8-529269fb1459", "TB symptom absent",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("10458542-b223-11e8-96f8-529269fb1459", "Tuberculosis screening set", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(tbSymptoms,tbSymptomPresent,tbSymptomAbsent)
                .build());

        // Cervical cancer screening
        Concept normal           = MetadataUtils.existing(Concept.class, "6557a15c-977f-11e1-8993-905e29aff6c1");
        Concept abnormal         = MetadataUtils.existing(Concept.class, "6557a274-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("162816AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(procedure)
                .name("126850BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Colposcopy of cervix with acetic acid",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("126849BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB","VIA (visual inspection with acetic acid)",
                        Locale.ENGLISH, null)
                .answers(normal,abnormal)
                .mapping(new ConceptMapBuilder("d0e78ae4-b6be-11e8-96f8-529269fb1459")
                    .type(sameAs).ensureTerm(ciel,"162816").build())
                .build());

        // Family planning
        Concept maleCondom = install(new ConceptBuilder("164813AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("141020BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Male condom",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("2bad80b0-b45c-11e8-96f8-529269fb1459")
                        .type(sameAs).ensureTerm(snomedCt, "442450006").build())
                .mapping(new ConceptMapBuilder("d0e7925a-b6be-11e8-96f8-529269fb1459")
                        .type(sameAs).ensureTerm(ciel,"164813").build())
                .build());

        Concept femaleCondom = install(new ConceptBuilder("164814AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("141021BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Female condom",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("2bad8344-b45c-11e8-96f8-529269fb1459")
                        .type(sameAs).ensureTerm(snomedCt, "442288006").build())
                .mapping(new ConceptMapBuilder("d0e79534-b6be-11e8-96f8-529269fb1459")
                        .type(sameAs).ensureTerm(ciel,"164814").build())
                .build());

        Concept sterilization = MetadataUtils.existing(Concept.class, "6560b7ec-977f-11e1-8993-905e29aff6c1");
        Concept none          = MetadataUtils.existing(Concept.class, "6557987e-977f-11e1-8993-905e29aff6c1");
        Concept notApp = MetadataUtils.existing(Concept.class,"65583dce-977f-11e1-8993-905e29aff6c1");
        Concept depo          = MetadataUtils.existing(Concept.class,"654acd88-977f-11e1-8993-905e29aff6c1");
        Concept snip          = MetadataUtils.existing(Concept.class,"655b43de-977f-11e1-8993-905e29aff6c1");
        Concept tubalLigation          = MetadataUtils.existing(Concept.class,"655b41d6-977f-11e1-8993-905e29aff6c1");
        Concept norplant          = MetadataUtils.existing(Concept.class,"655b40d2-977f-11e1-8993-905e29aff6c1");
        Concept abstain          = MetadataUtils.existing(Concept.class,"655b42da-977f-11e1-8993-905e29aff6c1");
        Concept rhythm          = MetadataUtils.existing(Concept.class,"656ade34-977f-11e1-8993-905e29aff6c1");
        Concept birthCtrlPill = MetadataUtils.existing(Concept.class,"654a1eb0-977f-11e1-8993-905e29aff6c1");
        Concept condom          = MetadataUtils.existing(Concept.class,"6546b98c-977f-11e1-8993-905e29aff6c1");
        Concept hysterectomy          = MetadataUtils.existing(Concept.class,"656add26-977f-11e1-8993-905e29aff6c1");
        Concept iud = MetadataUtils.existing(Concept.class,"656adc0e-977f-11e1-8993-905e29aff6c1");
        Concept diaphragm     = MetadataUtils.existing(Concept.class,"656adf4c-977f-11e1-8993-905e29aff6c1");
        Concept injectable = MetadataUtils.existing(Concept.class,"656ae064-977f-11e1-8993-905e29aff6c1");

        install(new ConceptBuilder("6547ac8e-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("66137e5e-977f-11e1-8993-905e29aff6c1", "Method of birth control", Locale.ENGLISH, null)
                .name("65f1e032-977f-11e1-8993-905e29aff6c1", "Method of family planning", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("66267f54-977f-11e1-8993-905e29aff6c1", "Family planning method", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("7283ea68-0496-11e6-b545-0010f345c8d0")
                        .type(sameAs).ensureTerm(pihMalawi, "374").build())
                .answers(maleCondom,femaleCondom,condom,other,none,snip,injectable,diaphragm,sterilization,
                        notApp,depo,tubalLigation,norplant,abstain,rhythm,birthCtrlPill,hysterectomy,iud)
                .build());

        install(new ConceptBuilder("1382AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(question)
                .name("1507BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Family planning counseling", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("8fef32c4-b4d2-3372-9b55-b9662a37273b")
                        .type(sameAs).ensureTerm(ciel, "1382").build())
                .mapping(new ConceptMapBuilder("ab5158cd-e8b0-3d10-9204-218f2bee4f6b")
                        .type(sameAs).ensureTerm(snomedCt, "410290005").build())
                .answers(yes,no)
                .build());

        install(new ConceptBuilder("163757AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(date)
                .conceptClass(question)
                .name("06b1eec8-b6cc-11e8-96f8-529269fb1459","Family planning start date",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("06b1f27e-b6cc-11e8-96f8-529269fb1459","Date family planning administered", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("06b1f4fe-b6cc-11e8-96f8-529269fb1459")
                        .type(sameAs).ensureTerm(ciel, "163757").build())
                .build());

        // Adherence counseling
        Concept first = install(new ConceptBuilder("697e9461-f2d6-4ab1-a140-48f768ce002a")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3fe10be2-f148-4256-9b7c-dfb2e3b2e1d4", "1st", Locale.ENGLISH, null)
                .name("3fed5fa8-65e5-49bb-827f-f9ed0a1bcb61", "First", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("32ca38c8-89d4-48b0-b884-bacba7881fd2").type(sameAs).ensureTerm(pih, "First").build())
                .build());

        Concept second = install(new ConceptBuilder("11c0f708-6950-4e94-b080-5c76174a4947")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("ee45ae72-9466-4f18-a15f-f62e76b8b53e", "Second", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("ee9c230b-672e-4bf2-8e71-287c576e8cb4", "2nd", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("41908ecc-fbb3-44e4-8ea6-08e03ca9c460").type(sameAs).ensureTerm(pih, "Second").build())
                .build());

        Concept third = install(new ConceptBuilder("224e3d57-f6d1-4244-bbe2-b81a574ba7aa")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2524ebcf-7eb1-411d-8771-4f301c94388d", "Third", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("c140030f-feaa-4a9d-b030-bb71dd0621bf", "3rd", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("90b316d0-4ded-4b6a-b5d5-dd6de82a4327").type(sameAs).ensureTerm(pih, "Third").build())
                .build());


        install(new ConceptBuilder("06b1f7d8-b6cc-11e8-96f8-529269fb1459")
                .datatype(coded)
                .conceptClass(question)
                .name("06b1fa44-b6cc-11e8-96f8-529269fb1459", "Adherence session number", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(first,second,third)
                .build());

        install(new ConceptBuilder("06b2005c-b6cc-11e8-96f8-529269fb1459")
                .datatype(coded)
                .conceptClass(question)
                .name("06b202f0-b6cc-11e8-96f8-529269fb1459", "Adherence counseling (coded)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        install(new ConceptNumericBuilder("20E91F16-BA4F-4058-B17A-998A82F4B803")
                .units("%")
                .lowAbsolute(0d)
                .hiAbsolute(100d)
                .datatype(numeric)
                .conceptClass(finding)
                .name("BF7BFB51-8F8E-4DEC-908F-4B447A4EAD30", "Medication Adherence percent", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("06b20a2a-b6cc-11e8-96f8-529269fb1459")
                .datatype(coded)
                .conceptClass(question)
                .name("7886b4a2-b6d6-11e8-96f8-529269fb1459", "Viral load counseling", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("7886b7fe-b6d6-11e8-96f8-529269fb1459", "VL counseling", Locale.ENGLISH, null)
                .answers(yes,no)
                .build());

        // TB screening
        Concept sampleCollected = install(new ConceptBuilder("165252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(question)
                .name("92e4d218-7f72-4112-b731-6e2fc9563e0c", "Sample collected", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("80594576-a5ef-4b11-8db9-f9a257609544", "Was a sample collected for testing", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("f8438a32-2e08-4083-8234-f88636127cfb").type(sameAs).ensureTerm(ciel, "165252").build())
                .mapping(new ConceptMapBuilder("78b5426d-4776-407e-8d26-cb7987f1cb59").type(narrowerThan).ensureTerm(snomedNp, "364708003").build())
                .answers(yes,no,unknown)
                .build());

        Concept satisfactory = MetadataUtils.existing(Concept.class, "6559dde6-977f-11e1-8993-905e29aff6c1");
        Concept unsatisfactory = MetadataUtils.existing(Concept.class, "656fa55e-977f-11e1-8993-905e29aff6c1");

        Concept poorSampleQuality = install(new ConceptBuilder("1304AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1391BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Poor sample quality", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f8b7dea6-e911-443e-969d-6dc43d1adf9f", "Mauvaise qualité d'échantillon", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("24cc715c-c6fd-4972-b3a5-f8c97e251328", "Echantiyon kalite pòv", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("86785BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "PSQ", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("97068BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "PSQ", Locale.ENGLISH, null)
                .name("138769BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "ECHANTILLON DE PAUVRE QUALITÉ", Locale.FRENCH, null)
                .mapping(new ConceptMapBuilder("1807244b-ea37-4ebb-b09d-d825f396930b").type(sameAs).ensureTerm(ciel, "1304").build())
                .mapping(new ConceptMapBuilder("1086294d-1c28-45b6-8ec5-6658eafbc3e1").type(sameAs).ensureTerm(pih, "7450").build())
                .mapping(new ConceptMapBuilder("756cd8a2-a16e-4a2c-893c-21f3b5a47732").type(sameAs).ensureTerm(pih, "POOR SAMPLE QUALITY").build())
                .mapping(new ConceptMapBuilder("137561ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "123038009").build())
                .mapping(new ConceptMapBuilder("134979ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ampath, "1304").build())
                .build());

        Concept sampleQuality = install(new ConceptBuilder("165253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(question)
                .name("4fec528c-622e-4512-879b-d830be24a958", "Sample quality", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("1a6dae0f-6e0d-49ca-b7d6-5f2a5501a233").type(sameAs).ensureTerm(ciel, "165253").build())
                .mapping(new ConceptMapBuilder("820e4def-3d52-4169-8c00-c7a87cd7a00b").type(narrowerThan).ensureTerm(snomedNp, "364708003").build())
                .answers(satisfactory, poorSampleQuality, unsatisfactory)
                .build());

        Concept tbMicroscopy= MetadataUtils.existing(Concept.class, "65628356-977f-11e1-8993-905e29aff6c1") ;
        Concept geneXNeno = MetadataUtils.existing(Concept.class, "e08214c0-955d-11e7-abc4-cec278b6b50a");
        Concept geneXLisung = MetadataUtils.existing(Concept.class, "e08212b8-955d-11e7-abc4-cec278b6b50a");
        Concept centralLab = MetadataUtils.existing(Concept.class, "e0820552-955d-11e7-abc4-cec278b6b50a");

        // ToDo:  Add location.  Not sure if this will work since we're adding one new answer to an existing concept
        install(new ConceptBuilder("6fc0ab50-9492-11e7-abc4-cec278b6b50a")
                .datatype(coded)
                .conceptClass(question)
                .name("e45d8f94-efdb-4b94-88da-b7e844cc91e3", "Location of laboratory", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(tbMicroscopy, geneXLisung, geneXNeno, centralLab)
                .build());


        Concept detected = install(new ConceptBuilder("1301AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1388BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Detected", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("138839BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Détecté", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("138840BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "detekte", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .description("1302FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Descriptive term:  to discover or ascertain the existence, presence, or fact of.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("137373ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "260373001").build())
                .mapping(new ConceptMapBuilder("134976ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ampath, "1301").build())
                .mapping(new ConceptMapBuilder("171553ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "1301").build())
                .build());

        Concept undetected = install(new ConceptBuilder("1302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1389BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Not detected", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("5e2c7c4a-d756-479a-bb77-0e66b4e3bb03", "Non-détecté", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .description("1303FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Descriptive term:  unable to discover or ascertain the existence, presence, or fact of.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("137374ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "260415000").build())
                .mapping(new ConceptMapBuilder("171554ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "1302").build())
                .mapping(new ConceptMapBuilder("134977ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ampath, "1302").build())
                .build());

        Concept geneXpertTest = install(new ConceptBuilder("162202AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(test)
                .name("125027BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "GeneXpert MTB/RIF", Locale.ENGLISH, null)
                .name("125026BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Tuberculosis polymerase chain reaction with rifampin resistance checking", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("c696819f-14fa-4a28-9ba8-bbba60431544", "Gene Xpert MTB/RIF", Locale.ENGLISH, null)
                .name("125025BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Xpert MTB/RIF", Locale.ENGLISH, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("274858ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "9718006").build())
                .mapping(new ConceptMapBuilder("274859ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162202").build())
                .answers(detected, undetected, none)
                .build());

        Concept indetRif = install(new ConceptBuilder("164104AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("139913BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Mycobacterium tuberculosis detected with indeterminate rifampin resistance", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("18175FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Result of PCR test determining MTB present with only unknown or indeterminate rifampin resistance", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("281190ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "365691004").build())
                .mapping(new ConceptMapBuilder("281192ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "164104").build())
                .build());

        Concept rifResist = install(new ConceptBuilder("162203AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("125028BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Mycobacterium tuberculosis detected with rifampin resistance", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125030BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MTB detected with rifampin resistance", Locale.ENGLISH, null)
                .description("17171FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Result of PCR test determining MTB present with rifampin resistance", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("274860ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "365691004").build())
                .mapping(new ConceptMapBuilder("274864ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "415345001").build())
                .mapping(new ConceptMapBuilder("274861ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162203").build())
                .build());

        Concept noRifResist = install(new ConceptBuilder("162204AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("125029BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Mycobacterium tuberculosis detected without rifampin resistance", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125031BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MTB detected without rifampin resistance", Locale.ENGLISH, null)
                .description("17172FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Result of PCR test for MTB showing no rifampin resistance", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("274862ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "365691004").build())
                .mapping(new ConceptMapBuilder("274863ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162204").build())
                .build());

        Concept tbRifStatus = install(new ConceptBuilder("164937AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(question)
                .name("141218BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Mycobacterium tuberculosis rifampin resistance detection status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("282959ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "164937").build())
                .mapping(new ConceptMapBuilder("282958ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "373064003").build())
                .answers(indetRif,rifResist,noRifResist)
                .build());

        Concept geneXlabSet = install(new ConceptBuilder("164945AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(labSet)
                .name("141236BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "GeneXpert MTB/Rif Lab set", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("282980ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "164945").build())
                .setMembers(geneXpertTest, tbRifStatus)
                .build());

        Concept tbTestType = install(new ConceptBuilder("38c4512a-5aef-487d-a450-ecea4bc5df7e")
                .datatype(coded)
                .conceptClass(question)
                .name("87d4a0e8-b773-44f1-b7ab-9b8423b8caf2", "Tuberculosis test type", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("2ff362a1-f422-48fb-896e-c60f89373fb1", "Type of TB test", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("fbfc8709-e404-491b-947c-82e4d0e70852").type(sameAs).ensureTerm(ciel, "165254").build())
                .mapping(new ConceptMapBuilder("28f31d20-fa39-40e7-a101-542bccd73faa").type(narrowerThan).ensureTerm(snomedNp, "416342005").build())
                .answers(tbMicroscopy, geneXlabSet)
                .build());

        Concept contaminated = MetadataUtils.existing(Concept.class, "65597a5e-977f-11e1-8993-905e29aff6c1");
        Concept missingResult = MetadataUtils.existing(Concept.class, "e0822140-955d-11e7-abc4-cec278b6b50a");
        Concept techProb = MetadataUtils.existing(Concept.class, "656fa662-977f-11e1-8993-905e29aff6c1");

        Concept equipFail = install(new ConceptBuilder("165179AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("371b9b8e-7829-4aa9-aa84-222a6a128b81", "Equipment failure", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("a91450fd-e41e-42ab-a4c2-1ed96af0b5b6").type(sameAs).ensureTerm(ciel, "165179").build())
                .build());

        Concept coagSample = install(new ConceptBuilder("165180AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("830d1c0d-ac7c-4c0f-9bc8-228e51645a19", "Blood sample coagulated", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("9a8634b4-a713-4b15-b4d4-5a8f662806cc").type(sameAs).ensureTerm(ciel, "165180").build())
                .build());

        Concept badContain = install(new ConceptBuilder("165181AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("17b7172f-c4bb-41c4-a079-f75646a29056", "Inappropriate sample container used", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("e4678185-4700-4382-981a-1a06729b1186", "Wrong container", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("1030f50c-e651-4bbf-9dcb-f44cc8947953").type(sameAs).ensureTerm(ciel, "165181").build())
                .build());

        Concept noSupply = install(new ConceptBuilder("165183AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("383979ca-473b-4686-b1ba-c9f8180d0512", "Supplies not available", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f672024c-d5db-102d-ad2a-000c29c2a5d7", "PAS DE FOURNITURE", Locale.FRENCH, null)
                .name("4902a1a1-70cf-4255-9759-57f8ab0f4950", "Lack of materials", Locale.ENGLISH, null)
                .description("ecf0b184-07fe-102c-b5fa-0017a47871b2", "No supplies as a reason for not being able to do something", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("7586f954-4943-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "NO SUPPLIES").build())
                .mapping(new ConceptMapBuilder("685db8e6-544e-40a4-a744-40508060e16d").type(sameAs).ensureTerm(ciel, "165183").build())
                .mapping(new ConceptMapBuilder("b220aec6-4864-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "2166").build())
                .build());

        Concept reasonNoResult = install(new ConceptBuilder("656fa450-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("6618934e-977f-11e1-8993-905e29aff6c1", "Reason for no result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("66189434-977f-11e1-8993-905e29aff6c1", "Reason no result obtained", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("73448768-0496-11e6-b545-0010f345c8d0").type(sameAs).ensureTerm(pihMalawi, "6112").build())
                .answers(contaminated, unsatisfactory, equipFail, techProb, missingResult, coagSample, badContain, noSupply, other)
                .build());

        Concept tbSmearResult = MetadataUtils.existing(Concept.class, "65628568-977f-11e1-8993-905e29aff6c1") ;

        Concept tbTestSet = install(new ConceptBuilder("4c92373c-28d6-11e9-b210-d663bd873d93")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("4c9239da-28d6-11e9-b210-d663bd873d93", "Tuberculosis test set", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("4c923b24-28d6-11e9-b210-d663bd873d93", "TB test construct", Locale.ENGLISH, null)
                .setMembers(sampleCollected, sampleQuality, labLocation, tbSmearResult, tbTestType, geneXpertTest, reasonNoResult, tbRifStatus)
                .build());

        Concept morning = MetadataUtils.existing(Concept.class, "656f9bc2-977f-11e1-8993-905e29aff6c1") ;
        Concept evening = MetadataUtils.existing(Concept.class, "656f9cc6-977f-11e1-8993-905e29aff6c1") ;

        Concept qualTime = install(new ConceptBuilder("4c923fca-28d6-11e9-b210-d663bd873d93")
                .datatype(coded)
                .conceptClass(question)
                .name("4c924132-28d6-11e9-b210-d663bd873d93", "Qualitative time", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("8d6f2fba-297d-11e9-b210-d663bd873d93", "AM or PM", Locale.ENGLISH, null)
                .answers(morning,evening)
                .build());

        Concept nextVisit = MetadataUtils.existing(Concept.class, "6569cbd4-977f-11e1-8993-905e29aff6c1");
        Concept reasonVisit = MetadataUtils.existing(Concept.class, "6573364c-977f-11e1-8993-905e29aff6c1");
        Concept locationVisit = MetadataUtils.existing(Concept.class, "65736a36-977f-11e1-8993-905e29aff6c1");

        Concept nextVisitSet = install(new ConceptBuilder("65733750-977f-11e1-8993-905e29aff6c1")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("661d6be4-977f-11e1-8993-905e29aff6c1", "Appointment set", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .name("8d6f3866-297d-11e9-b210-d663bd873d93", "Appointment date construct", Locale.ENGLISH, null)
                .setMembers(nextVisit, reasonVisit, locationVisit, qualTime)
                .build());

        Concept otherOutcome = install(new ConceptBuilder("558a7114-2990-11e9-b210-d663bd873d93")
                .datatype(text)
                .conceptClass(question)
                .name("558a7696-2990-11e9-b210-d663bd873d93", "Other outcome", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .build());

        Concept reasonForExit = install(new ConceptBuilder("558a783a-2990-11e9-b210-d663bd873d93")
                .datatype(text)
                .conceptClass(question)
                .name("558a7984-2990-11e9-b210-d663bd873d93", "Reason to stop care (text)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .build());

    }
}
