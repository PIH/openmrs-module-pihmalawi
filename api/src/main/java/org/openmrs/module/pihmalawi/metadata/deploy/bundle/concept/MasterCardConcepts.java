package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
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
    public static final String FOURTEEN_P = "cf770b14-4c9d-11ea-b77f-2e728ce88125";
    public static final String FIFTEEN_A = "6764fc5e-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FIFTEEN_P = "e2ec7c88-4c9d-11ea-b77f-2e728ce88125";
    public static final String SIXTEEN_A = "e2ec805c-4c9d-11ea-b77f-2e728ce88125";
    public static final String SIXTEEN_P = "e2ec82b4-4c9d-11ea-b77f-2e728ce88125";
    public static final String SEVENTEEN_A = "e2ec871e-4c9d-11ea-b77f-2e728ce88125";
    public static final String SEVENTEEN_P = "e2ec899e-4c9d-11ea-b77f-2e728ce88125";
    public static final String NON_STANDARD = "826b65ba-dc53-11e8-9f8b-f2801f1b9fd1";
    public static final String TWELVE_A = "43b86ce6-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String VIRAL_LOAD_SAMPLE_ID = "a8a56930-6b16-11ea-b6dd-8f1bd7e7fd41";

    // Existing NCD drugs concepts
    public static final String DAPSONE = "6545f8b2-977f-11e1-8993-905e29aff6c1";
    public static final String STREPTO = "65484176-977f-11e1-8993-905e29aff6c1";
    public static final String ISONIAZ = "65498bbc-977f-11e1-8993-905e29aff6c1";
    public static final String ETHAM = "6549f0b6-977f-11e1-8993-905e29aff6c1";
    public static final String FLUCON = "6549f2dc-977f-11e1-8993-905e29aff6c1";
    public static final String RIFAMP_ISO = "655852b4-977f-11e1-8993-905e29aff6c1";
    public static final String RIFAMP_PYRA = "654a1190-977f-11e1-8993-905e29aff6c1";
    public static final String TRIMETH = "654b02da-977f-11e1-8993-905e29aff6c1";
    public static final String NYSTAT = "654b0618-977f-11e1-8993-905e29aff6c1";
    public static final String ANTIBIO = "655853cc-977f-11e1-8993-905e29aff6c1";
    public static final String ANTIMAL = "656e1702-977f-11e1-8993-905e29aff6c1";
    public static final String HERBAL = "656e191e-977f-11e1-8993-905e29aff6c1";
    public static final String MULTIVIT = "65485a58-977f-11e1-8993-905e29aff6c1";
    public static final String MINERAL = "656e1b44-977f-11e1-8993-905e29aff6c1";
    public static final String OTHER_NON_CO = "656cce7e-977f-11e1-8993-905e29aff6c1";
    public static final String UNKNOWN = "65576584-977f-11e1-8993-905e29aff6c1";
    public static final String LONG_INSU = "6573132e-977f-11e1-8993-905e29aff6c1";
    public static final String INSU_SOLU = "6547414a-977f-11e1-8993-905e29aff6c1";
    public static final String METFO = "65694308-977f-11e1-8993-905e29aff6c1";
    public static final String GLIB = "65693cf0-977f-11e1-8993-905e29aff6c1";
    public static final String DIURE = "163212AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String CCB = "163213AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ACE_I = "162298AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String BB = "163211AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ASP = "6545efde-977f-11e1-8993-905e29aff6c1";
    public static final String STATIN = "162307AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String CHLORP = "654b00aa-977f-11e1-8993-905e29aff6c1";
    public static final String HALIP = "65693df4-977f-11e1-8993-905e29aff6c1";
    public static final String FLUPHE = "654b0eb0-977f-11e1-8993-905e29aff6c1";
    public static final String CARBA = "654b0726-977f-11e1-8993-905e29aff6c1";
    public static final String SODIUM_V = "65694b32-977f-11e1-8993-905e29aff6c1";
    public static final String RISP = "83405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String FLUOX = "65693bec-977f-11e1-8993-905e29aff6c1";
    public static final String PHENOB = "6546f3c0-977f-11e1-8993-905e29aff6c1";
    public static final String PHENYT = "65473768-977f-11e1-8993-905e29aff6c1";
    public static final String BENZATH = "656930ac-977f-11e1-8993-905e29aff6c1";
    public static final String HCTZ = "65588df6-977f-11e1-8993-905e29aff6c1";
    public static final String FUROS = "6546003c-977f-11e1-8993-905e29aff6c1";
    public static final String SPIRO = "65694c36-977f-11e1-8993-905e29aff6c1";
    public static final String AML = "65635ef2-977f-11e1-8993-905e29aff6c1";
    public static final String NIF = "654704dc-977f-11e1-8993-905e29aff6c1";
    public static final String ENAL = "65588cde-977f-11e1-8993-905e29aff6c1";
    public static final String CAPT = "6563597a-977f-11e1-8993-905e29aff6c1";
    public static final String LISIN = "65635a74-977f-11e1-8993-905e29aff6c1";
    public static final String ATEN = "65635d58-977f-11e1-8993-905e29aff6c1";
    public static final String BIS = "72247AAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String PROP = "65470f18-977f-11e1-8993-905e29aff6c1";
    public static final String SIMVA = "83936AAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String PRAVA = "82411AAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String ATORVA = "657aefc2-977f-11e1-8993-905e29aff6c1";
    public static final String HYD = "654b10d6-977f-11e1-8993-905e29aff6c1";
    public static final String ISSMN = "6574f45a-977f-11e1-8993-905e29aff6c1";
    public static final String INHALED_B = "60ae316c-c15f-11e5-9912-ba0be0483c18";
    public static final String INHALED_STE = "60ae3554-c15f-11e5-9912-ba0be0483c18";
    public static final String ORAL_STE = "60ae373e-c15f-11e5-9912-ba0be0483c18";
    public static final String OTHER = "656cce7e-977f-11e1-8993-905e29aff6c1";
    public static final String OLANZA = "dae4e2e4-659e-11e6-8b77-86f30ca893d3";
    public static final String CLOZAP = "dae4e5e6-659e-11e6-8b77-86f30ca893d3";
    public static final String TRIFLU = "657b0bba-977f-11e1-8993-905e29aff6c1";
    public static final String CLOPIXOL = "dae4eb4a-659e-11e6-8b77-86f30ca893d3";

    @Override
    public int getVersion() {
        return 7;
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

        // NCD Meds - existing
        Concept dapson = MetadataUtils.existing(Concept.class, DAPSONE);
        Concept strep = MetadataUtils.existing(Concept.class, STREPTO);
        Concept ison = MetadataUtils.existing(Concept.class, ISONIAZ);
        Concept etha = MetadataUtils.existing(Concept.class, ETHAM);
        Concept fluco = MetadataUtils.existing(Concept.class, FLUCON);
        Concept rifa_iso = MetadataUtils.existing(Concept.class, RIFAMP_ISO);
        Concept rifa_py = MetadataUtils.existing(Concept.class, RIFAMP_PYRA);
        Concept trime = MetadataUtils.existing(Concept.class, TRIMETH);
        Concept nysta = MetadataUtils.existing(Concept.class, NYSTAT);
        Concept antib = MetadataUtils.existing(Concept.class, ANTIBIO);
        Concept antima = MetadataUtils.existing(Concept.class, ANTIMAL);
        Concept herb = MetadataUtils.existing(Concept.class, HERBAL);
        Concept multiv = MetadataUtils.existing(Concept.class, MULTIVIT);
        Concept minera = MetadataUtils.existing(Concept.class, MINERAL);
        Concept other_non_c = MetadataUtils.existing(Concept.class, OTHER_NON_CO);
        Concept unknow = MetadataUtils.existing(Concept.class, UNKNOWN);
        Concept long_in = MetadataUtils.existing(Concept.class, LONG_INSU);
        Concept insu_sol = MetadataUtils.existing(Concept.class, INSU_SOLU);
        Concept metf = MetadataUtils.existing(Concept.class, METFO);
        Concept glibe = MetadataUtils.existing(Concept.class, GLIB);
        Concept diuret = MetadataUtils.existing(Concept.class, DIURE);
        Concept ccblock = MetadataUtils.existing(Concept.class, CCB);
        Concept aceI = MetadataUtils.existing(Concept.class, ACE_I);
        Concept betab = MetadataUtils.existing(Concept.class, BB);
        Concept aspi = MetadataUtils.existing(Concept.class, ASP);
        Concept stati = MetadataUtils.existing(Concept.class, STATIN);
        Concept chlor = MetadataUtils.existing(Concept.class, CHLORP);
        Concept haliper = MetadataUtils.existing(Concept.class, HALIP);
        Concept fluph = MetadataUtils.existing(Concept.class, FLUPHE);
        Concept carbam = MetadataUtils.existing(Concept.class, CARBA);
        Concept sodiumval = MetadataUtils.existing(Concept.class, SODIUM_V);
        Concept rispe = MetadataUtils.existing(Concept.class, RISP);
        Concept fluxe = MetadataUtils.existing(Concept.class, FLUOX);
        Concept phenorb = MetadataUtils.existing(Concept.class, PHENOB);
        Concept pheny = MetadataUtils.existing(Concept.class, PHENYT);
        Concept benzat = MetadataUtils.existing(Concept.class, BENZATH);
        Concept hctzz = MetadataUtils.existing(Concept.class, HCTZ);
        Concept furose = MetadataUtils.existing(Concept.class, FUROS);
        Concept spirol = MetadataUtils.existing(Concept.class, SPIRO);
        Concept amlo = MetadataUtils.existing(Concept.class, AML);
        Concept nifa = MetadataUtils.existing(Concept.class, NIF);
        Concept enala = MetadataUtils.existing(Concept.class, ENAL);
        Concept captt = MetadataUtils.existing(Concept.class, CAPT);
        Concept lisinn = MetadataUtils.existing(Concept.class, LISIN);
        Concept ateno = MetadataUtils.existing(Concept.class, ATEN);
        Concept biso = MetadataUtils.existing(Concept.class, BIS);
        Concept propa = MetadataUtils.existing(Concept.class, PROP);
        Concept simv = MetadataUtils.existing(Concept.class, SIMVA);
        Concept prav = MetadataUtils.existing(Concept.class, PRAVA);
        Concept atorv = MetadataUtils.existing(Concept.class, ATORVA);
        Concept hydd = MetadataUtils.existing(Concept.class, HYD);
        Concept issm = MetadataUtils.existing(Concept.class, ISSMN);
        Concept inhaledb = MetadataUtils.existing(Concept.class, INHALED_B);
        Concept inhaledste = MetadataUtils.existing(Concept.class, INHALED_STE);
        Concept oralste = MetadataUtils.existing(Concept.class, ORAL_STE);
        Concept otherd = MetadataUtils.existing(Concept.class, OTHER);
        Concept olanz = MetadataUtils.existing(Concept.class, OLANZA);
        Concept cloza = MetadataUtils.existing(Concept.class, CLOZAP);
        Concept trifl = MetadataUtils.existing(Concept.class, TRIFLU);
        Concept clopix = MetadataUtils.existing(Concept.class, CLOPIXOL);

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

        Concept fourteenP = install(new ConceptBuilder(FOURTEEN_P)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("cf770dd0-4c9d-11ea-b77f-2e728ce88125", "14P: AZT / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fifteenP = install(new ConceptBuilder(FIFTEEN_P)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec7f12-4c9d-11ea-b77f-2e728ce88125", "15P: ABC / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept sixteenA = install(new ConceptBuilder(SIXTEEN_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec8188-4c9d-11ea-b77f-2e728ce88125", "16A: ABC / 3TC + RAL", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept sixteenP = install(new ConceptBuilder(SIXTEEN_P)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec83e0-4c9d-11ea-b77f-2e728ce88125", "16P: ABC / 3TC + RAL", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept seventeenA = install(new ConceptBuilder(SEVENTEEN_A)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec8868-4c9d-11ea-b77f-2e728ce88125", "17A: ABC / 3TC + EFV", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept seventeenP = install(new ConceptBuilder(SEVENTEEN_P)
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

        Concept HbA1c = install(new ConceptNumericBuilder(ChronicCareMetadata.HBA1C_CONCEPT)
                .datatype(numeric)
                .conceptClass(test)
                .units("%")
                .lowAbsolute(2d)
                .hiAbsolute(16d)
                .precise(true)
                .name("661b31d0-977f-11e1-8993-905e29aff6c1", "HbA1c", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("661b30f4-977f-11e1-8993-905e29aff6c1", "Glycated hemoglobin", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65cd18ec-977f-11e1-8993-905e29aff6c1", "a lab test that shows the average amount of sugar in the blood over the last two to three months", Locale.ENGLISH)
                .build());

        // re-install drugs used (NCD)
        install(new ConceptBuilder("65585192-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("65f5c1a2-977f-11e1-8993-905e29aff6c1", "Current drugs used", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("65b496f0-977f-11e1-8993-905e29aff6c1", "Question on encounter forms: \"Is the patient currently taking, or has the patient ever taken, any of the following other medications?\" This particular concept stores a history of active use of the associated medications.", Locale.ENGLISH)
                .answers(
                        dapson,
                        strep,
                        ison,
                        etha,
                        fluco,
                        rifa_iso,
                        rifa_py,
                        trime,
                        nysta,
                        antib,
                        antima,
                        herb,
                        multiv,
                        minera,
                        other_non_c,
                        unknow,
                        long_in,
                        insu_sol,
                        metf,
                        glibe,
                        diuret,
                        ccblock,
                        aceI,
                        betab,
                        aspi,
                        stati,
                        chlor,
                        haliper,
                        fluph,
                        carbam,
                        sodiumval,
                        rispe,
                        fluxe,
                        phenorb,
                        pheny,
                        benzat,
                        hctzz,
                        furose,
                        spirol,
                        amlo,
                        nifa,
                        enala,
                        captt,
                        lisinn,
                        ateno,
                        biso,
                        propa,
                        simv,
                        prav,
                        atorv,
                        hydd,
                        issm,
                        inhaledb,
                        inhaledste,
                        oralste,
                        otherd,
                        olanz,
                        cloza,
                        trifl,
                        clopix)
                .build());
    }
}
