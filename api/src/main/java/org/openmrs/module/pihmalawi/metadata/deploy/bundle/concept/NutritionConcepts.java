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
public class NutritionConcepts extends VersionedPihConceptBundle {

    public static final String NUTRITION_PROGRAM_CONCEPT_UUID = "655bd768-977f-11e1-8993-905e29aff6c1";
    public static final String SEVERE_MALNUTRITION_STATE_CONCEPT = "960B1212-C7A6-4247-8176-84DC462361BB";
    public static final String NUTRITION_PROGRAM_STATUS_CONCEPT_UUID = "3B0DB54D-A283-43D8-9B5E-1EF0B4CE9B78";


    public static final String LACTOGEN_TINS_CONCEPT_UUID = "b063e72d-3ddf-4e6c-8415-396375971940";

    public static final String ENROLLED_IN_NCD_UUID="0514c008-3738-458c-99de-9f43eca8c414";

    public static final String  MATERNAL_DEATH_UUID="0e529088-9f42-470e-bf9e-b6bc0af0c4e5";

    public static final String  SEVERE_MATERNAL_ILLNESS_UUID="5f3ab1b3-d900-419b-ae2f-3014d4a5ab58";

    public static final String  MULTIPLE_BIRTHS_UUID="748e0e6d-9701-4c2f-9d61-2e23858b7dcb";

    @Override
    public int getVersion() {
        return 3;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

        install(new ConceptBuilder(SEVERE_MALNUTRITION_STATE_CONCEPT)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("E54BC6D6-39C7-4848-8BFE-443FE557CC61", "Severe malnutrition state", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder(NUTRITION_PROGRAM_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("603FE192-B374-4FAD-87A9-98C8D48796A2", "Nutrition program treatment status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("25FC913D-EB92-4500-9B05-E28FFAA4136C")
                        .type(sameAs).ensureTerm(pih, "Nutrition program treatment status").build())
                .build());

        install(new ConceptBuilder(LACTOGEN_TINS_CONCEPT_UUID)
                .datatype(numeric)
                .conceptClass(misc)
                .name("d5724661-b82f-430d-be6a-6add5c6994d7", "Number of lactogen tins", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());


        // Enrolled in NCD
        Concept enrolledInNCD = install(new ConceptBuilder(ENROLLED_IN_NCD_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("2eb5c266-913b-4168-82fe-0fda5346862b", "Enrolled in NCD",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());


        // Maternal death
        Concept maternalDeath = install(new ConceptBuilder(MATERNAL_DEATH_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("5f607c43-10b1-4b62-b635-b7b19958df49", "Maternal Death",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        //Severe maternal illness
        Concept severeMaternalIllness= install(new ConceptBuilder(SEVERE_MATERNAL_ILLNESS_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("9cd28236-4d7d-49e1-bc27-0f1f8e7badc1", "Severe Maternal Illness",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        //Multiple births
        Concept multipleBirths= install(new ConceptBuilder(MULTIPLE_BIRTHS_UUID)
                .datatype(coded)
                .conceptClass(question)
                .name("df755ee5-1410-4243-b0e8-0a3e4e03ec3b", "Multiple Births",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());
    }
}
