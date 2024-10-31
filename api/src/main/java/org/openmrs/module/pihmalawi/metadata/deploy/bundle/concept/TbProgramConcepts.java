package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class TbProgramConcepts extends VersionedPihConceptBundle {

    public static final String OLD_TB_PROGRAM_CONCEPT_UUID = "655abe64-977f-11e1-8993-905e29aff6c1";
    public static final String TB_PROGRAM_CONCEPT_UUID = "9E32CDCE-7781-4184-ADD4-39B91BFA0A96";
    public static final String TB_PROGRAM_WORKFLOW_CONCEPT_UUID = "A802A051-ABE2-44EC-BE8E-A118A3D747E7";
    public static final String TREATMENT_COMPLETED_CONCEPT_UUID = "4DB1A692-386A-485B-B007-7B8F7F0C866F";
    public static final String TREATMENT_FAILED_CONCEPT_UUID = "0848CF92-3AE1-4ADB-BC60-FF4B7575ED90";
    public static final String NOT_EVALUATED_CONCEPT_UUID = "668FB2FB-084E-48C6-8950-9018B422462F";
    public static final String TREATMENT_SUCCESS_CONCEPT_UUID = "0D951F01-926B-46CB-A99C-EAF48533C5CB";
    public static final String ON_MDR_TREATMENT_CONCEPT_UUID = "CCD8059E-968D-42D2-A7E0-DBC6B2FA9BE4";

    @Override
    public int getVersion() {
        return 3;
    }

    @Override
    protected void installNewVersion() throws Exception {

        install(new ConceptBuilder(OLD_TB_PROGRAM_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(program)
                .name("65f7f3a0-977f-11e1-8993-905e29aff6c1", "Old Tuberculosis program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("661752c2-977f-11e1-8993-905e29aff6c1", "Old TB program", Locale.ENGLISH, null)
                .name("66278d04-977f-11e1-8993-905e29aff6c1", "TB", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        install(new ConceptBuilder(TB_PROGRAM_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("69E865C6-EAF7-4FBD-88D2-1C6C236E2151", "Tuberculosis program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("E42A90FC-D6A6-4D2D-B406-44C36F2CD098", "TB program", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        install(new ConceptBuilder(TB_PROGRAM_WORKFLOW_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("06D7F5A1-A97E-4D3C-BA3C-D0379157591B", "TB program workflow status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
        install(new ConceptBuilder(TREATMENT_COMPLETED_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("5D50CA16-33B2-49E6-A80D-847832CDDAB3", "Treatment completed", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("8EDF1A96-9670-4917-B3EE-312AA7FFA890", "A person who completed treatment as recommended by the national policy, whose outcome doesn’t meet the definition of cure or treatment failure", Locale.ENGLISH)
                .build());
        install(new ConceptBuilder(TREATMENT_FAILED_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("149D8DA6-D1D8-4906-B60E-791B12844DD8", "Treatment failed", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("7AFC056A-4B7C-4E5B-B4AD-4193D1E86256", "A patient whose treatment regimen need to be terminated or permanently changed to a new treatment strategy", Locale.ENGLISH)
                .build());
        install(new ConceptBuilder(NOT_EVALUATED_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("08E3C104-E822-4EFA-AFB0-712F89C7BB6B", "Not evaluated", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("3EC8331F-A552-4B70-9E0F-64121ADE5258", "A patient for whom no treatment was assigned", Locale.ENGLISH)
                .build());
        install(new ConceptBuilder(TREATMENT_SUCCESS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("A598AE0D-A811-4BB5-AFE5-5494980530E6", "Treatment success", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("FF0D6247-81FE-47E9-8F32-6A3540F22617", "The sum of cured and treatment completed", Locale.ENGLISH)
                .build());
        install(new ConceptBuilder(ON_MDR_TREATMENT_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(state)
                .name("8384F703-B54B-406D-971C-3652648A0C8A", "On Multidrug resistant treatment", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("1AE8C8E7-D989-4CF1-98BC-AD1A361FBBE3", "Patient still undergoing TB treatment for drug resistant TB", Locale.ENGLISH)
                .build());
    }
}
