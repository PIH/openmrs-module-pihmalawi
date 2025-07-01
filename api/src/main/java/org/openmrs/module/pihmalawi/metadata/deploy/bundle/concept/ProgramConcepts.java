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
    public static final String MENTAL_HEALTH_TREATMENT_STATUS_CONCEPT_UUID = "48D4C24C-5AED-43CF-BCB6-D0D5A2DEB619";
    public static final String EPILEPSY_TREATMENT_STATUS_CONCEPT_UUID = "ad7b930a-a1d9-4fff-bd62-212d5e55e5b2";
    public static final String SICKLE_CELL_DISEASE_TREATMENT_STATUS_CONCEPT_UUID = "60125522-00AF-4261-B837-4A9162264523";
    public static final String CKD_TREATMENT_STATUS_CONCEPT_UUID = "eb4f508e-2dc0-40ca-8cf1-8b17b9dee21b";
    public static final String CHF_TREATMENT_STATUS_CONCEPT_UUID = "64156b64-9ce2-4f76-9489-e026fe48e3fb";
    public static final String DIABETES_HYPERTENSION_TREATMENT_STATUS_CONCEPT_UUID = "57f5c094-2dfa-466b-8295-1b45db99e1b0";

    public static final String LOST_TO_FOLLOWUP_STATUS_CONCEPT_UUID = "92964825-6952-407e-8331-8bbd0df7ef5c";
    public static final String PATIENT_CURED_STATUS_CONCEPT_UUID = "655b6256-977f-11e1-8993-905e29aff6c1";


    @Override
    public int getVersion() {
        return 11;
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


        install(new ConceptBuilder(PATIENT_CURED_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("65f88efa-977f-11e1-8993-905e29aff6c1", "Patient cured", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65b7d46e-977f-11e1-8993-905e29aff6c1", "A pulmonary TB patient with bacteriologically confirmed TB at the beginning of treatment who completed treatment as recommended by the national policy, with evidence of bacteriological response and no evidence of failure", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder(LOST_TO_FOLLOWUP_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("8AF4D96B-9B07-4D2B-8A0F-AF4DB3916134", "Lost to follow-up status workflow", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("10EE30B7-3CAD-43E7-ABF0-C8AE7D209ECA", "A patient who didn’t start treatment or whose treatment was interrupted for 2 consecutive months or more", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder(EPILEPSY_TREATMENT_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("746e9799-bea0-4eb4-9793-2b6e85b49cea", "Epilepsy treatment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("47481D29-50EB-4D87-BBC9-DEA9B2DA81E1")
                        .type(sameAs).ensureTerm(pih, "Epilepsy treatment status workflow").build())
                .build());

        install(new ConceptBuilder(MENTAL_HEALTH_TREATMENT_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("243E4A3D-6A3D-4B96-BB5A-C56F51F75116", "Mental health treatment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("942F01FD-61A5-4640-85E5-7E4285C2D685")
                        .type(sameAs).ensureTerm(pih, "Mental health treatment status workflow").build())
                .build());

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
        install(new ConceptBuilder(SICKLE_CELL_DISEASE_TREATMENT_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("5303120E-11A4-4B57-81D7-B058B5D6D841", "Sickle cell disease treatment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("DF839B68-C011-4399-BDF3-3A356D71AC33")
                        .type(sameAs).ensureTerm(pih, "Sickle cell disease treatment status workflow").build())
                .build());

        install(new ConceptBuilder(CKD_TREATMENT_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("f12a24c6-1200-4e3a-b988-8b8e0ba94897", "Chronic Kidney disease treatment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("121662c7-faeb-43bf-88ad-a142ab515b5f")
                        .type(sameAs).ensureTerm(pih, "Chronic Kidney disease treatment status workflow").build())
                .build());
        install(new ConceptBuilder(CHF_TREATMENT_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("3b61d32b-2849-4f4d-9efb-b9b7a194ccc5", "Chronic Heart Failure treatment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("f86e189c-fa90-4548-8622-cd9caef5c9af")
                        .type(sameAs).ensureTerm(pih, "Chronic Heart Failure treatment status workflow").build())
                .build());
        install(new ConceptBuilder(DIABETES_HYPERTENSION_TREATMENT_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("687251c1-762b-4b7a-ab8d-adbf72d65450", "Diabetes Hypertension treatment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("ea43a323-b898-45c4-964f-f7c46b1a4ca9")
                        .type(sameAs).ensureTerm(pih, "Diabetes Hypertension treatment status workflow").build())
                .build());
    }
}
