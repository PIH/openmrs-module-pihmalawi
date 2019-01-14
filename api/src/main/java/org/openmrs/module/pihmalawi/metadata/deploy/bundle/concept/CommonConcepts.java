package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * A small number of very common concepts
 */
@Component
@Requires(CoreConceptMetadataBundle.class)
public class CommonConcepts extends VersionedPihConceptBundle {

    public static final class Concepts {
        public static final String UNKNOWN = "65576584-977f-11e1-8993-905e29aff6c1";
        public static final String YES = "65576354-977f-11e1-8993-905e29aff6c1";
        public static final String NO = "6557646c-977f-11e1-8993-905e29aff6c1";
        public static final String OTHER = "656cce7e-977f-11e1-8993-905e29aff6c1";
        public static final String NONE = "6557987e-977f-11e1-8993-905e29aff6c1";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected void installNewVersion() throws Exception {

    }
}
