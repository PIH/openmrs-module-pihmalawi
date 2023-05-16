package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.api.ConceptNameType;
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
    public static final String GENERIC_OUTCOME_CONCEPT_UUID = "73eb05c2-e4be-4d82-bcad-ffec1be67d01";

    public static final String LACTOGEN_TINS_CONCEPT_UUID = "b063e72d-3ddf-4e6c-8415-396375971940";

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {

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

    }
}
