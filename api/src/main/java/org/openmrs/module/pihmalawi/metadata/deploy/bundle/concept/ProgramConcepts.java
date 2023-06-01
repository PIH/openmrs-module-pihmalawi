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
public class ProgramConcepts extends VersionedPihConceptBundle {

    public static final String CHRONIC_CARE_PROGRAM_CONCEPT  = "655f4f42-977f-11e1-8993-905e29aff6c1";
    public static final String MH_CARE_PROGRAM_CONCEPT  = "406AD643-79A3-4019-9888-3EFBB9B24FB0";
    public static final String PDC_PROGRAM_CONCEPT = "74f09d38-4e1e-4acb-a8d0-04b7090fcb77";
    public static final String CHRONIC_CARE_TREATMENT_STATUS_CONCEPT = "65766b96-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT = "65664784-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT = "9af03945-c8c1-11e8-9bc6-0242ac110001";
    public static final String CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT = "655b604e-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_DIED_CONCEPT = "655b5e46-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT = "6566dba4-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT = "655b5f4a-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT = "655a6acc-977f-11e1-8993-905e29aff6c1";
    public static final String GENERIC_OUTCOME_CONCEPT_UUID = "73eb05c2-e4be-4d82-bcad-ffec1be67d01";
    public static final String TRANSFER_IN_STATE_CONCEPT_UUID = "7602D441-4A44-41A9-AA49-A59853AEDC65";
    public static final String FIRST_TIME_INITIATION_STATE_CONCEPT_UUID = "C874F67C-7C3C-4BAE-93CE-E3B8FFABF496";
    public static final String PATIENT_PREGNANT_STATE_CONCEPT_UUID = "77FF0F7E-ECEF-4D32-AE1A-7DD35F212EA4";
    public static final String PATIENT_MARRIED_STATE_CONCEPT_UUID = "0EC11643-6EEB-4EBE-BF4C-136FD8AC6B89";
    public static final String PATIENT_GRADUATED_STATE_CONCEPT_UUID = "539F6CAC-DDCD-4F7D-BB02-4ADEE912CF95";


    @Override
    public int getVersion() {
        return 4;
    }

    @Override
    protected void installNewVersion() throws Exception {
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_PROGRAM_CONCEPT);
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_TREATMENT_STATUS_CONCEPT);
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT);
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT);
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_STATUS_DIED_CONCEPT);
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT);
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT);
        MetadataUtils.existing(Concept.class, CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT);

        install(new ConceptBuilder(CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("508515ce-c8c2-11e8-9bc6-0242ac110001", "In advanced care", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder(MH_CARE_PROGRAM_CONCEPT)
                .datatype(notApplicable)
                .conceptClass(program)
                .name("66840734-B1BB-4EC9-A151-12578F301790", "Mental Health Care Program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder(PDC_PROGRAM_CONCEPT)
                .datatype(notApplicable)
                .conceptClass(program)
                .name("139c704f-0188-4d4f-af8f-fac59488f9d1", "Pediatric Development Clinic Program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder(TRANSFER_IN_STATE_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("40B61167-B63A-40B4-8B5C-D1B303BBF163", "Patient transfer in", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("58E7548F-49D2-4FE5-8C9C-22A843E903BB")
                        .type(sameAs).ensureTerm(pih, "Transfer in program state").build())
                .build());
        install(new ConceptBuilder(FIRST_TIME_INITIATION_STATE_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("0D7101AD-D04F-4751-98FA-F2BC916CED82", "First time initiation", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("82B622A5-D39B-4B55-8B92-73D1217D046D")
                        .type(sameAs).ensureTerm(pih, "First time initiation").build())
                .build());
        install(new ConceptBuilder(PATIENT_PREGNANT_STATE_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("D62E4EA4-5810-4040-B8E7-27F4B65A2974", "Pregnant", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("5A1E8016-64EF-403C-890E-B4FE5E302135")
                        .type(sameAs).ensureTerm(pih, "Patient pregnant program state").build())
                .build());
        install(new ConceptBuilder(PATIENT_MARRIED_STATE_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("A08DBE22-275F-4009-9CD4-7F9612A08960", "Patient married", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("B5A12EC0-CFCA-4CCD-BC07-03F365834F32")
                        .type(sameAs).ensureTerm(pih, "Patient married program state").build())
                .build());
        install(new ConceptBuilder(PATIENT_GRADUATED_STATE_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("823E7E5C-5C6F-4B58-A9B8-229E52FE648F", "Graduated", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("24DC4859-344E-4456-B2FA-167CDF5DF50E")
                        .type(sameAs).ensureTerm(pih, "Patient graduated program state").build())
                .build());
    }
}
