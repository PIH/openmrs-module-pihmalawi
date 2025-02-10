package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
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
        return 6;
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

        Concept initiation = install(new ConceptBuilder("67B0DD4F-2CD2-44CC-A023-FF3DCD629BB0")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("A1276BB0-1716-4C49-A99B-D148ED302FF1", "Initiation", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("0994CECF-6421-4E3F-88C0-15B31B47E5F3", "TB test at initiation", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("BA38533A-E9E1-4824-BD60-1CEB75FBB052").type(sameAs).ensureTerm(pihMalawi, "TB INITIATION TEST").build())
                .build());
        Concept month_two = install(new ConceptBuilder("9F9A8E13-D8D3-474F-9BFD-46F6F1515F7A")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("37552710-FC01-4390-9A61-DC61515C08A9", "Month 2", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("E09A6678-2E22-4759-8D73-3EE71D56BBCD", "TB test at month two", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("C5419B20-B490-42AB-8526-92FA88AE210C").type(sameAs).ensureTerm(pihMalawi, "TB MONTH TWO TEST").build())
                .build());
        Concept month_three = install(new ConceptBuilder("BF8B20B1-C0DC-469E-8016-2D1C59884EEA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("B8B0ED14-9C6B-4DF8-8492-3C9A4E4403FC", "Month 3", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("369B510A-8EF8-437F-BB47-369A00AF1441", "TB test at month three", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("EE79C9D4-2104-4238-8BD6-0A4C024BBA4C").type(sameAs).ensureTerm(pihMalawi, "TB MONTH THREE TEST").build())
                .build());
        Concept month_five = install(new ConceptBuilder("4059558D-BAEC-41A9-9E23-8D25897F1A8C")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6841F38E-864D-440B-B7D9-D1B3CE40E4C3", "Month 5", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("816BBDE9-7197-43F9-8D34-4A6155B398CF", "TB test at month five", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("FA530AA6-B8C3-40A5-95C5-10A6842728BB").type(sameAs).ensureTerm(pihMalawi, "TB MONTH FIVE TEST").build())
                .build());
        Concept month_six = install(new ConceptBuilder("ED533FD1-DBBA-479A-8FC3-2F6D2F983FAC")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("FE1CAC6A-FAF2-4681-A6EB-BDC7217E5A3A", "Month 6", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("5A3C3E73-841C-4CE0-A0A9-EB5FE8A423A8", "TB test at month six", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("1160513A-3CFE-4440-808B-7CBEF252F6AC").type(sameAs).ensureTerm(pihMalawi, "TB MONTH SIX TEST").build())
                .build());
        install(new ConceptBuilder("63A03D36-9D91-4013-BFCA-F29E80459947")
                .datatype(coded)
                .conceptClass(question)
                .name("A905537E-A2C9-4C50-B890-4731E40BB2F2", "TB Test time", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("F6B0691D-2202-4342-9480-A706DE302C99","The time of the TB test",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("195CAAA4-7D58-4F09-958A-5B2388AF327E").type(sameAs).ensureTerm(pihMalawi, "TB TEST INSTANCE").build())
                .answers(initiation, month_two, month_three, month_five, month_six)
                .build());
    }
}
