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
public class PalliativeCareConcepts extends VersionedPihConceptBundle{

    public static final String PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID = "14ce86d7-025e-4dcf-9437-67d26185c6ab";
    public static final String PALLIATIVE_CARE_PROGRAM_STATUS_CONCEPT_UUID = "e7a7c2ca-7433-4851-8687-67e8541ca40b";
    public static final String PALLIATIVE_CARE_PROGRAM_OUTCOME_CONCEPT_UUID  = "73eb05c2-e4be-4d82-bcad-ffec1be67d01";
    public static final String ON_TREATMENT_CONCEPT_UUID = "65664784-977f-11e1-8993-905e29aff6c1";
    public static final String TREATMENT_STOPPED_CONCEPT_UUID = "655a6acc-977f-11e1-8993-905e29aff6c1";
    public static final String TRANSFERRED_OUT_CONCEPT_UUID = "655b604e-977f-11e1-8993-905e29aff6c1";
    public static final String DEFAULTED_CONCEPT_UUID = "655b5f4a-977f-11e1-8993-905e29aff6c1";
    public static final String DIED_STATE_CONCEPT_UUID = "655b5e46-977f-11e1-8993-905e29aff6c1";


    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    protected void installNewVersion() throws Exception {

        install(new ConceptBuilder(PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(program)
                .name("5ecb5fb3-8b87-4c1e-99ac-e2e0be21d4dd", "Palliative Care Program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("66fb0c94-0498-11e6-9622-0010f345ca38")
                        .type(sameAs).ensureTerm(pih, "8415").build())
                .build());

        install(new ConceptBuilder(PALLIATIVE_CARE_PROGRAM_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("33f74a77-13cb-4fb1-88ee-5d56aae2ae0c", "Palliative Care Treatment Status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("66fb952f-0498-11e6-9622-0010f345ca38")
                        .type(sameAs).ensureTerm(pih, "8416").build())
                .build());

        install(new ConceptBuilder(PALLIATIVE_CARE_PROGRAM_OUTCOME_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(workflow)
                .name("e72c1cfc-3e7a-48e3-9b3b-19b539cc7070", "Generic outcome", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("da99e814-6b44-4bec-a89d-20dddf61e115")
                        .type(sameAs).ensureTerm(pih, "Generic outcome").build())
                .mapping(new ConceptMapBuilder("41a01c04-cc22-4c69-bfea-8bfe1ed002b6")
                        .type(sameAs).ensureTerm(pih, "11505").build())
                .build());
    }
}
