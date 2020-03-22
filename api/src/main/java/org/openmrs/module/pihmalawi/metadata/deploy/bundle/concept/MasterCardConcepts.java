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
    public static final String TWO_P = "657ab6d8-977f-11e1-8993-905e29aff6c1";
    public static final String TWO_A = "657ab520-977f-11e1-8993-905e29aff6c1";
    public static final String FOUR_P = "657abf48-977f-11e1-8993-905e29aff6c1";
    public static final String FOUR_A = "657abd9a-977f-11e1-8993-905e29aff6c1";
    public static final String FIVE_A = "657ac056-977f-11e1-8993-905e29aff6c1";
    public static final String SIX_A = "657ac164-977f-11e1-8993-905e29aff6c1";
    public static final String THIRTEEN_A = "53009e3a-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FOURTEEN_A = "5e16f0b2-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FIFTEEN_A = "6764fc5e-dc3f-11e8-9f8b-f2801f1b9fd1";

    public static final String SEVEN_A = "657ac268-977f-11e1-8993-905e29aff6c1";
    public static final String EIGHT_A = "657ac36c-977f-11e1-8993-905e29aff6c1";
    public static final String NINE_P = "657ac470-977f-11e1-8993-905e29aff6c1";
    public static final String NINE_A = "8a795372-ba39-11e6-91a8-5622a9e78e10";
    public static final String TEN_A = "7ebc782a-baa2-11e6-91a8-5622a9e78e10";
    public static final String ELEVEN_P = "91bcdad2-baa2-11e6-91a8-5622a9e78e10";
    public static final String ELEVEN_A = "8bb7294e-baa2-11e6-91a8-5622a9e78e10";

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

	    //reinstalling/adding some ART drugs concepts to match with 2018 MoH guidelines
        Concept zeroA = MetadataUtils.existing(Concept.class, ZERO_A);
        Concept zeroP = MetadataUtils.existing(Concept.class, ZERO_P);
        Concept oneA = MetadataUtils.existing(Concept.class, ONE_A);
        Concept oneP = MetadataUtils.existing(Concept.class, "657ab372-977f-11e1-8993-905e29aff6c1");
        Concept twoA = MetadataUtils.existing(Concept.class, TWO_A);
        Concept twoP = MetadataUtils.existing(Concept.class, TWO_P);
        Concept threeA = MetadataUtils.existing(Concept.class, "657ab886-977f-11e1-8993-905e29aff6c1");
        Concept threeP = MetadataUtils.existing(Concept.class, "657aba3e-977f-11e1-8993-905e29aff6c1");
        Concept fourA = MetadataUtils.existing(Concept.class, FOUR_A);
        Concept fourP = MetadataUtils.existing(Concept.class, FOUR_P);
        Concept sixA = MetadataUtils.existing(Concept.class, SIX_A);
        Concept nineA = MetadataUtils.existing(Concept.class, NINE_A);
        Concept nineP = MetadataUtils.existing(Concept.class, NINE_P);
        Concept tenA = MetadataUtils.existing(Concept.class, TEN_A);
        Concept elevenA = MetadataUtils.existing(Concept.class, ELEVEN_A);
        Concept elevenP = MetadataUtils.existing(Concept.class, ELEVEN_P);

        install(new ConceptBuilder(HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("65f83b26-977f-11e1-8993-905e29aff6c1", "Patient hospitalized since last visit", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept fiveA = install(new ConceptBuilder(FIVE_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("6628679c-977f-11e1-8993-905e29aff6c1", "5A: TDF / 3TC / EFV", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept sevenA = install(new ConceptBuilder(SEVEN_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("66286dbe-977f-11e1-8993-905e29aff6c1", "7A: TDF / 3TC + ATV/r", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept eightA = install(new ConceptBuilder(EIGHT_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("66286e9a-977f-11e1-8993-905e29aff6c1", "8A: AZT / 3TC + ATV/r", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept twelveA = install(new ConceptBuilder(TWELFE_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("4dbe31e4-dc3f-11e8-9f8b-f2801f1b9fd1", "12A: DRV + r + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept thirteenA = install(new ConceptBuilder(THIRTEEN_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("580173f0-dc3f-11e8-9f8b-f2801f1b9fd1", "13A: TDF / 3TC / DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fourteenA = install(new ConceptBuilder(FOURTEEN_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("624b723e-dc3f-11e8-9f8b-f2801f1b9fd1", "14A: AZT / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fifteenA = install(new ConceptBuilder(FIFTEEN_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("6c9da91e-dc3f-11e8-9f8b-f2801f1b9fd1", "15A: ABC / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
				
	    Concept nonStandard = install(new ConceptBuilder("826b65ba-dc53-11e8-9f8b-f2801f1b9fd1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("826b6876-dc53-11e8-9f8b-f2801f1b9fd1", "Non standard", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
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
                        fifteenA,
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
                        fifteenA,
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
                        fifteenA,
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
                        fifteenA,
                        nonStandard)
                .build());

        Concept viralLoadSampleId = install(new ConceptBuilder(VIRAL_LOAD_SAMPLE_ID)
                .datatype(text)
                .conceptClass(misc)
                .name("F3C29B6C-7FBD-460F-A7D8-CE8377268FE5", "Viral Load Sample ID", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
    }
}
