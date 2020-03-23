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
public class MasterCardConcepts extends VersionedPihConceptBundle {

    public static final String HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT  = "655b3dc6-977f-11e1-8993-905e29aff6c1";

    public static final String ZERO_A = "d5930c3a-cb57-11e5-9956-625662870761";
    public static final String ZERO_P = "d59315a4-cb57-11e5-9956-625662870761";
    public static final String ONE_A = "657ab1ba-977f-11e1-8993-905e29aff6c1";
    public static final String ONE_P = "657ab372-977f-11e1-8993-905e29aff6c1";
    public static final String TWO_A = "657ab520-977f-11e1-8993-905e29aff6c1";
    public static final String TWO_P = "657ab6d8-977f-11e1-8993-905e29aff6c1";
    public static final String THREE_A = "657ab886-977f-11e1-8993-905e29aff6c1";
    public static final String THREE_P = "657aba3e-977f-11e1-8993-905e29aff6c1";
    public static final String FOUR_A = "657abd9a-977f-11e1-8993-905e29aff6c1";
    public static final String FOUR_P = "657abf48-977f-11e1-8993-905e29aff6c1";
    public static final String FIVE_A = "657ac056-977f-11e1-8993-905e29aff6c1";
    public static final String SIX_A = "657ac164-977f-11e1-8993-905e29aff6c1";
    public static final String SEVEN_A = "657ac268-977f-11e1-8993-905e29aff6c1";
    public static final String EIGHT_A = "657ac36c-977f-11e1-8993-905e29aff6c1";
    public static final String NINE_A = "8a795372-ba39-11e6-91a8-5622a9e78e10";
    public static final String NINE_P = "657ac470-977f-11e1-8993-905e29aff6c1";
    public static final String TEN_A = "7ebc782a-baa2-11e6-91a8-5622a9e78e10";
    public static final String ELEVEN_A = "8bb7294e-baa2-11e6-91a8-5622a9e78e10";

    public static final String ELEVEN_P = "91bcdad2-baa2-11e6-91a8-5622a9e78e10";
    public static final String THIRTEEN_A = "53009e3a-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FOURTEEN_A = "5e16f0b2-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FIFTEEN_A = "6764fc5e-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String NON_STANDARD = "826b65ba-dc53-11e8-9f8b-f2801f1b9fd1";
    public static final String TWELFE_A = "43b86ce6-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String VIRAL_LOAD_SAMPLE_ID = "a8a56930-6b16-11ea-b6dd-8f1bd7e7fd41";



    @Override
    public int getVersion() {
        return 4;
    }

    @Override
    protected void installNewVersion() throws Exception {
        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

	// reinstalling/adding some ART drugs concepts to match with 2018 MoH guidelines 
	// (addendum to 4th edition of the Malawi integrated guidelines and standard operating procedures for clinical HIV services)
        Concept zeroA = MetadataUtils.existing(Concept.class, ZERO_A);
        Concept zeroP = MetadataUtils.existing(Concept.class, ZERO_P);
        Concept oneA = MetadataUtils.existing(Concept.class, ONE_A);
        Concept oneP = MetadataUtils.existing(Concept.class, ONE_P);
        Concept twoA = MetadataUtils.existing(Concept.class, TWO_A);
        Concept twoP = MetadataUtils.existing(Concept.class, TWO_P);
        Concept threeA = MetadataUtils.existing(Concept.class, THREE_A);
        Concept threeP = MetadataUtils.existing(Concept.class, THREE_P);
        Concept fourA = MetadataUtils.existing(Concept.class, FOUR_A);
        Concept fourP = MetadataUtils.existing(Concept.class, FOUR_P);
	Concept fiveA = MetadataUtils.existing(Concept.class, FIVE_A);
        Concept sixA = MetadataUtils.existing(Concept.class, SIX_A);
	Concept sevenA = MetadataUtils.existing(Concept.class, SEVEN_A);
	Concept eightA = MetadataUtils.existing(Concept.class, EIGHT_A);
        Concept nineA = MetadataUtils.existing(Concept.class, NINE_A);
        Concept nineP = MetadataUtils.existing(Concept.class, NINE_P);
        Concept tenA = MetadataUtils.existing(Concept.class, TEN_A);
        Concept elevenA = MetadataUtils.existing(Concept.class, ELEVEN_A);
	Concept twelveA = MetadataUtils.existing(Concept.class, TWELVE_A);
	Concept thirteenA = MetadataUtils.existing(Concept.class, THIRTEEN_A);
	Concept fourteenA = MetadataUtils.existing(Concept.class, FOURTEEN_A);
	Concept fifteenA = MetadataUtils.existing(Concept.class, FIFTEEN_A);
	Concept nonStandard = MetadataUtils.existing(Concept.class, NON_STANDARD);

        install(new ConceptBuilder(HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT)
                .datatype(coded)
                .conceptClass(question)			
                .name("65f83b26-977f-11e1-8993-905e29aff6c1", "Patient hospitalized since last visit", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)			
                .build());

	Concept elevenP = install(new ConceptBuilder(ELEVEN_P)
                 .datatype(notApplicable)
                 .conceptClass(drug)
                 .name("ba40105b-673d-4fa4-9263-70ce5f228230", "11P: AZT / 3TC + LPV/r (previous AZT3TCLPV)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                 .build());

        Concept fourteenP = install(new ConceptBuilder("cf770b14-4c9d-11ea-b77f-2e728ce88125")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("cf770dd0-4c9d-11ea-b77f-2e728ce88125", "14P: AZT / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fifteenP = install(new ConceptBuilder("e2ec7c88-4c9d-11ea-b77f-2e728ce88125")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec7f12-4c9d-11ea-b77f-2e728ce88125", "15P: ABC / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept sixteenA = install(new ConceptBuilder("e2ec805c-4c9d-11ea-b77f-2e728ce88125")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec8188-4c9d-11ea-b77f-2e728ce88125", "16A: ABC / 3TC + RAL", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept sixteenP = install(new ConceptBuilder("e2ec82b4-4c9d-11ea-b77f-2e728ce88125")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec83e0-4c9d-11ea-b77f-2e728ce88125", "16P: ABC / 3TC + RAL", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept seventeenA = install(new ConceptBuilder("e2ec871e-4c9d-11ea-b77f-2e728ce88125")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec8868-4c9d-11ea-b77f-2e728ce88125", "17A: ABC / 3TC + EFV", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept seventeenP = install(new ConceptBuilder("e2ec899e-4c9d-11ea-b77f-2e728ce88125")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec8c14-4c9d-11ea-b77f-2e728ce88125", "17P: ABC / 3TC + EFV", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("657ac57e-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(misc)
                .name("66287052-977f-11e1-8993-905e29aff6c1", "Malawi Antiretroviral drugs received", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        zeroA,
                        zeroP,
                        oneA,
                        oneP,
                        twoA,
                        twoP,
                        threeA,
                        threeP,
                        fourA,
                        fourP,
                        fiveA,
                        sixA,
                        sevenA,
                        eightA,
                        nineA,
                        nineP,
                        tenA,
                        elevenA,
                        elevenP,
                        twelveA,
                        thirteenA,
                        fourteenA,
                        fourteenP,
                        fifteenA,
                        fifteenP,
                        sixteenA,
                        sixteenP,
                        seventeenA,
                        seventeenP,
                        nonStandard)
                .build());

        install(new ConceptBuilder("657ac678-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(misc)
                .name("6628712e-977f-11e1-8993-905e29aff6c1", "Malawi Antiretroviral drugs change 1", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        zeroA,
                        zeroP,
                        oneA,
                        oneP,
                        twoA,
                        twoP,
                        threeA,
                        threeP,
                        fourA,
                        fourP,
                        fiveA,
                        sixA,
                        sevenA,
                        eightA,
                        nineA,
                        nineP,
                        tenA,
                        elevenA,
                        elevenP,
                        twelveA,
                        thirteenA,
                        fourteenA,
                        fourteenP,
                        fifteenA,
                        fifteenP,
                        sixteenA,
                        sixteenP,
                        seventeenA,
                        seventeenP,
                        nonStandard)
                .build());

        install(new ConceptBuilder("657ac7c2-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(misc)
                .name("6628755c-977f-11e1-8993-905e29aff6c1", "Malawi Antiretroviral drugs change 2", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        zeroA,
                        zeroP,
                        oneA,
                        oneP,
                        twoA,
                        twoP,
                        threeA,
                        threeP,
                        fourA,
                        fourP,
                        fiveA,
                        sixA,
                        sevenA,
                        eightA,
                        nineA,
                        nineP,
                        tenA,
                        elevenA,
                        elevenP,
                        twelveA,
                        thirteenA,
                        fourteenA,
                        fourteenP,
                        fifteenA,
                        fifteenP,
                        sixteenA,
                        sixteenP,
                        seventeenA,
                        seventeenP,
                        nonStandard)
                .build());

        install(new ConceptBuilder("657ac8d0-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(misc)
                .name("66287c8c-977f-11e1-8993-905e29aff6c1", "Malawi Antiretroviral drugs change 3", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(
                        zeroA,
                        zeroP,
                        oneA,
                        oneP,
                        twoA,
                        twoP,
                        threeA,
                        threeP,
                        fourA,
                        fourP,
                        fiveA,
                        sixA,
                        sevenA,
                        eightA,
                        nineA,
                        nineP,
                        tenA,
                        elevenA,
                        elevenP,
                        twelveA,
                        thirteenA,
                        fourteenA,
                        fourteenP,
                        fifteenA,
                        fifteenP,
                        sixteenA,
                        sixteenP,
                        seventeenA,
                        seventeenP,
                        nonStandard)
                .build());

        Concept viralLoadSampleId = install(new ConceptBuilder(VIRAL_LOAD_SAMPLE_ID)
                .datatype(text)
                .conceptClass(misc)
                .name("F3C29B6C-7FBD-460F-A7D8-CE8377268FE5", "Viral Load Sample ID", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
    }
}
