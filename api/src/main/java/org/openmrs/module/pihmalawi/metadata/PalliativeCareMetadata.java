package org.openmrs.module.pihmalawi.metadata;


import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component("palliativeCareMetadata")
public class PalliativeCareMetadata extends CommonMetadata{

    public static String PALLIATIVE_CARE_NUMBER = "Palliative Care Number";

    public static String PALLIATIVE_CARE_PROGRAM = "Palliative care program";


//    public static ProgramDescriptor PALLIATIVE_CARE = new ProgramDescriptor() {
//        public String conceptUuid() { return PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID; }
//        public String name() { return "NCD"; }
//        public String description() { return "NCD Program"; }
//        @Override public String outcomesConceptUuid()  { return NCD_PROGRAM_OUTCOMES_CONCEPT_UUID; }   // this concept is installed via metadata package
//        public String uuid() { return "515796ec-bf3a-11e7-abc4-cec278b6b50a"; }
//        @Override public Set<ProgramWorkflowDescriptor> workflows() { return Collections.singleton(NCD_CLINICAL_STATUS); }
//    };

}
