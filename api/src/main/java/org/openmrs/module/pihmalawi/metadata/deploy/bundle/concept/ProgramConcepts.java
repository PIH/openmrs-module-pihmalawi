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
public class ProgramConcepts extends VersionedPihConceptBundle {

    public static final String CHRONIC_CARE_PROGRAM_CONCEPT  = "655f4f42-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_TREATMENT_STATUS_CONCEPT = "65766b96-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_ON_TREATMENT_CONCEPT = "65664784-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_IN_ADVANCED_CARE_CONCEPT = "9af03945-c8c1-11e8-9bc6-0242ac110001";
    public static final String CHRONIC_CARE_STATUS_TRANSFERRED_OUT_CONCEPT = "655b604e-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_DIED_CONCEPT = "655b5e46-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_DISCHARGED_CONCEPT = "6566dba4-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_DEFAULTED_CONCEPT = "655b5f4a-977f-11e1-8993-905e29aff6c1";
    public static final String CHRONIC_CARE_STATUS_TREATMENT_STOPPED_CONCEPT = "655a6acc-977f-11e1-8993-905e29aff6c1";

    @Override
    public int getVersion() {
        return 1;
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
    }
}
