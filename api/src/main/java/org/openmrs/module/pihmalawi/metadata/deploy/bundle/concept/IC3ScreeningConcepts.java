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
public class IC3ScreeningConcepts extends VersionedPihConceptBundle {

    //public static final String REFERRAL_SOURCE_CONCEPT  = "65664fc2-977f-11e1-8993-905e29aff6c1";

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {

        // Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        // Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept other = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.OTHER);

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
                        primaryCareClinic,healthClinic, other, ward, prenatalClinic,community,outpatient,healthCenter)
                .build());

        // HTC
        Concept noBlood = install(new ConceptBuilder("0e447720-a180-11e8-98d0-529269fb1459")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0e447b26-a180-11e8-98d0-529269fb1459", "Unable to draw blood", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept patientRefused = MetadataUtils.existing(Concept.class, "6566a4ae-977f-11e1-8993-905e29aff6c1");

        Concept noSampleReason = install(new ConceptBuilder("0e447d92-a180-11e8-98d0-529269fb1459")
                .datatype(coded)
                .conceptClass(question)
                .name("0e447fe0-a180-11e8-98d0-529269fb1459", "Reason for no sample", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("0e4485da-a180-11e8-98d0-529269fb1459","Lack of sample reason", Locale.ENGLISH,null)
                .answers(noBlood,patientRefused)
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
                .name("4405c71b-3081-4fc9-87cf-3fdc1315817f", "Viral Load Test Set", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED) // locale-preferred
                .setMembers(bled, vl, ldl, testReason, labLocation, noResultReason, noSampleReason)
                .build());

    }
}
