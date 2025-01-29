package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
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
    public static final String FOUR_PP = "1fb83022-491b-4a0b-bd81-a1a48cfc082f";
    public static final String FOUR_PA = "0c812dc8-cb40-4e2e-926e-ebd9fb3376ff";
    public static final String FIVE_A = "657ac056-977f-11e1-8993-905e29aff6c1";
    public static final String SIX_A = "657ac164-977f-11e1-8993-905e29aff6c1";
    public static final String SEVEN_A = "657ac268-977f-11e1-8993-905e29aff6c1";
    public static final String EIGHT_A = "657ac36c-977f-11e1-8993-905e29aff6c1";
    public static final String NINE_A = "8a795372-ba39-11e6-91a8-5622a9e78e10";
    public static final String NINE_P = "657ac470-977f-11e1-8993-905e29aff6c1";
    public static final String NINE_PP = "84e96887-5963-4cc9-8feb-b2ccd1447465";
    public static final String NINE_PA = "e9478866-c8ce-4ca7-a593-03e8ba0166d7";
    public static final String TEN_A = "7ebc782a-baa2-11e6-91a8-5622a9e78e10";
    public static final String ELEVEN_A = "8bb7294e-baa2-11e6-91a8-5622a9e78e10";
    public static final String ELEVEN_P = "91bcdad2-baa2-11e6-91a8-5622a9e78e10";
    public static final String ELEVEN_PP = "98ec6848-15bf-41ef-b844-5a16d7dbb0f8";
    public static final String ELEVEN_PA = "ec5edf44-a088-4117-8a05-b23b018ec075";
    public static final String TWELVE_PP = "3b861411-5818-430a-8a90-2deabf89f328";
    public static final String TWELVE_PA = "007f685d-bb78-4f16-91c4-073aa8107943";
    public static final String THIRTEEN_A = "53009e3a-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FOURTEEN_A = "5e16f0b2-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FOURTEEN_P = "cf770b14-4c9d-11ea-b77f-2e728ce88125";
    public static final String FOURTEEN_PP = "8fe981ba-2349-41e4-99d8-2d6051f24718";
    public static final String FOURTEEN_PA = "e901ec7c-f45c-413f-a186-4c779227ac08";
    public static final String FIFTEEN_A = "6764fc5e-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String FIFTEEN_P = "e2ec7c88-4c9d-11ea-b77f-2e728ce88125";
    public static final String FIFTEEN_PP = "e58595c7-8354-48ea-a2b8-f75584bdf594";
    public static final String FIFTEEN_PA = "fb26bc7f-ae9c-4655-9823-ce0066d8081a";
    public static final String SIXTEEN_A = "e2ec805c-4c9d-11ea-b77f-2e728ce88125";
    public static final String SIXTEEN_P = "e2ec82b4-4c9d-11ea-b77f-2e728ce88125";
    public static final String SEVENTEEN_A = "e2ec871e-4c9d-11ea-b77f-2e728ce88125";
    public static final String SEVENTEEN_P = "e2ec899e-4c9d-11ea-b77f-2e728ce88125";
    public static final String SEVENTEEN_PP = "e13a8993-5604-40c9-90ea-8217d7d047f0";
    public static final String SEVENTEEN_PA = "fd5521f9-00e1-42dc-98cf-103c44e64bc1";
    public static final String NON_STANDARD = "826b65ba-dc53-11e8-9f8b-f2801f1b9fd1";
    public static final String TWELVE_A = "43b86ce6-dc3f-11e8-9f8b-f2801f1b9fd1";
    public static final String VIRAL_LOAD_SAMPLE_ID = "a8a56930-6b16-11ea-b6dd-8f1bd7e7fd41";
    public static final String URINE_LAM = "a047e2ec-f07e-47a6-8849-7c5150550e9e";
    public static final String POSITIVE = "6549be7a-977f-11e1-8993-905e29aff6c1";
    public static final String NEGATIVE = "654994c2-977f-11e1-8993-905e29aff6c1";
    public static final String TEST_RESULT = "522849a5-be77-41b2-98e4-a755096df7ed";
    public static final String PASS = "20f6787b-3f0e-4c98-91c2-ee34f771f3ff";
    public static final String FAIL = "a2a69a69-ecc7-450f-952e-bc8830aaabe7";
    public static final String LEFT_EAR = "44ed8c4c-abfd-4527-819b-457cb3b5c76a";
    public static final String RIGHT_EAR = "65d881c2-4e5d-4837-959e-0f097fcb93e7";

    // Existing NCD drugs concepts
    public static final String DAPSONE = "6545f8b2-977f-11e1-8993-905e29aff6c1";
    public static final String STREPTO = "65484176-977f-11e1-8993-905e29aff6c1";
    public static final String ISONIAZ = "65498bbc-977f-11e1-8993-905e29aff6c1";
    public static final String RIFAPENTINE = "af85c07d-adce-4a5d-a8d9-fa640f41e82d";
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

    // Dosing units UUID
    public static final String UNIT_AMPULE = "656923d2-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_APPL = "65692c92-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_BOTTLE = "656922d8-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_CAPSULE = "6569265c-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_DROPS = "65692b66-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_GRAMS = "6562ca50-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_TABLET = "656921d4-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_TUBE = "65692760-977f-11e1-8993-905e29aff6c1";
    public static final String UNIT_SACHET = "656924c2-977f-11e1-8993-905e29aff6c1";

    // Drug frequencies
    public static final String TWICE_A_DAY = "6563fd26-977f-11e1-8993-905e29aff6c1";
    public static final String ONCE_A_DAY = "6563fc18-977f-11e1-8993-905e29aff6c1";
    public static final String IMMEDIATE = "655a9a10-977f-11e1-8993-905e29aff6c1";
    public static final String OTHERFREQ = "657140f8-977f-11e1-8993-905e29aff6c1";
    public static final String ONCE_A_MONTH = "6563ff2e-977f-11e1-8993-905e29aff6c1";
    public static final String THREE_A_DAY = "6563fe2a-977f-11e1-8993-905e29aff6c1";
    public static final String TWICE_A_MONTH = "65640032-977f-11e1-8993-905e29aff6c1";
    public static final String FOUR_TIMES_DAY = "656f969a-977f-11e1-8993-905e29aff6c1";
    public static final String FIVE_TIMES_DAY = "656f979e-977f-11e1-8993-905e29aff6c1";
    public static final String IN_THE_MORN = "656f9bc2-977f-11e1-8993-905e29aff6c1";
    public static final String IN_THE_EVE = "656f9cc6-977f-11e1-8993-905e29aff6c1";
    public static final String EVERY_DAY = "656f99b0-977f-11e1-8993-905e29aff6c1";
    public static final String SIX_TIMES_DAY = "656f98ac-977f-11e1-8993-905e29aff6c1";
    public static final String ONCE_A_WEEK = "655d3a7c-977f-11e1-8993-905e29aff6c1";
    public static final String ONCE_A_DAY_AT_NIGHT = "656f9ab4-977f-11e1-8993-905e29aff6c1";
    public static final String VARIABLE = "65745fcc-977f-11e1-8993-905e29aff6c1";
    public static final String ONCE_A_DAY_AT_NOON = "65747d90-977f-11e1-8993-905e29aff6c1";
    public static final String GEN_DRUG_FREQ = "65636514-977f-11e1-8993-905e29aff6c1";

    // Dispensing concepts
    public static final String AMOUNT_DISPENSED = "65614392-977f-11e1-8993-905e29aff6c1";
    public static final String DURATION_MEDS = "159368AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String TIME_UNITS = "f1904502-319d-4681-9030-e642111e7ce2";

    @Override
    public int getVersion() {
        return 31;
        // current version  30
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
        Concept positive = MetadataUtils.existing(Concept.class, POSITIVE);
        Concept negative = MetadataUtils.existing(Concept.class, NEGATIVE);

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

        // Existing dosing units
        Concept unitAmpule = MetadataUtils.existing(Concept.class, UNIT_AMPULE);
        Concept unitApplic = MetadataUtils.existing(Concept.class, UNIT_APPL);
        Concept unitBottle = MetadataUtils.existing(Concept.class, UNIT_BOTTLE);
        Concept unitCapsule = MetadataUtils.existing(Concept.class, UNIT_CAPSULE);
        Concept unitDrops = MetadataUtils.existing(Concept.class, UNIT_DROPS);
        Concept unitGrams = MetadataUtils.existing(Concept.class, UNIT_GRAMS);
        Concept unitTablet = MetadataUtils.existing(Concept.class, UNIT_TABLET);
        Concept unitTube = MetadataUtils.existing(Concept.class, UNIT_TUBE);
        Concept unitSachet = MetadataUtils.existing(Concept.class, UNIT_SACHET);

        // Existing drug frequencies
        Concept twiceAday = MetadataUtils.existing(Concept.class, TWICE_A_DAY);
        Concept onceAday = MetadataUtils.existing(Concept.class, ONCE_A_DAY);
        Concept immediate = MetadataUtils.existing(Concept.class, IMMEDIATE);
        Concept otherFreq = MetadataUtils.existing(Concept.class, OTHERFREQ);
        Concept onceAmonth = MetadataUtils.existing(Concept.class, ONCE_A_MONTH);
        Concept threeADay = MetadataUtils.existing(Concept.class, THREE_A_DAY);
        Concept twiceAmonth = MetadataUtils.existing(Concept.class, TWICE_A_MONTH);
        Concept fourTimesAday = MetadataUtils.existing(Concept.class, FOUR_TIMES_DAY);
        Concept fiveTimesAday = MetadataUtils.existing(Concept.class, FIVE_TIMES_DAY);
        Concept inTheMorn = MetadataUtils.existing(Concept.class, IN_THE_MORN);
        Concept inTheEve= MetadataUtils.existing(Concept.class, IN_THE_EVE);
        Concept everyDay = MetadataUtils.existing(Concept.class, EVERY_DAY);
        Concept sixTimesAday = MetadataUtils.existing(Concept.class, SIX_TIMES_DAY);
        Concept onceAweek = MetadataUtils.existing(Concept.class, ONCE_A_WEEK);
        Concept onceAdayAtnight = MetadataUtils.existing(Concept.class, ONCE_A_DAY_AT_NIGHT);
        Concept variable = MetadataUtils.existing(Concept.class, VARIABLE);
        Concept onceAdatAtnoon = MetadataUtils.existing(Concept.class, ONCE_A_DAY_AT_NOON);
        Concept generalDrugFreq = MetadataUtils.existing(Concept.class, GEN_DRUG_FREQ);

        // Dispensing concepts
        Concept amountDispensed = MetadataUtils.existing(Concept.class, AMOUNT_DISPENSED);
        Concept timeUnits = MetadataUtils.existing(Concept.class, TIME_UNITS);

        install(new ConceptBuilder("e0d31892-690e-4063-9570-73d103c8efb0")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("a30ab46a-4838-48b5-80eb-60268920005b", "Hours", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("5974007e-f810-4208-8983-3ec98c7f87ba", "Hrs", Locale.ENGLISH, null)
                .description("33cf9207-7aac-4fe5-b0fe-27146290dd75", "A duration unit, 60 minutes = 1 hour.", Locale.ENGLISH)
                .description("15878FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "A duration unit, 60 minutes = 1 hour.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("b2fe077f-a24a-4379-b04b-d3440ac39a30").type(sameAs).ensureTerm(ciel, "1822").build())
                .mapping(new ConceptMapBuilder("b2b26eba-4864-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "6902").build())
                .mapping(new ConceptMapBuilder("75b230d8-4943-102e-96e9-000c29c2a5d7").type(sameAs).ensureTerm(pih, "Hours").build())
                .mapping(new ConceptMapBuilder("b599c20c-715d-4fc6-8f4e-8b0a328c5e3f").type(sameAs).ensureTerm(snomedCt, "258702006").build())
                .build());


        install(new ConceptBuilder(HOSPITALIZED_SINCE_LAST_VISIT_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("65f83b26-977f-11e1-8993-905e29aff6c1", "Patient hospitalized since last visit", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept fourPP = install(new ConceptBuilder(FOUR_PP)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e01d2669-3df9-43aa-839f-a442eba068ff", "4PP: AZT 60 / 3TC 30 + EFV 200", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fourPA = install(new ConceptBuilder(FOUR_PA)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("16e8c201-46ce-490c-8838-f4f3e39dc4cf", "4PA: AZT 300 / 3TC 150 + EFV 200", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept ninePP = install(new ConceptBuilder(NINE_PP)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("86bd5436-46f9-4404-9863-3bf0b7eef261", "9PP: ABC 120 / 3TC 60 + LPV/r 100/25", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept ninePA = install(new ConceptBuilder(NINE_PA)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("86bd5436-46f9-4404-9863-3bf0b0eef261", "9PA: ABC 600 / 3TC 300 + LPV/r 100/25", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept elevenP = install(new ConceptBuilder(ELEVEN_P)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("ba40105b-673d-4fa4-9263-70ce5f228230", "11P: AZT / 3TC + LPV/r (previous AZT3TCLPV)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept elevenPP = install(new ConceptBuilder(ELEVEN_PP)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("0f73f5bf-ec73-401a-8c9b-088f4c912e1c", "11PP: AZT 60 / 3TC 30 + LPV/r 100/25", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept elevenPA = install(new ConceptBuilder(ELEVEN_PA)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("c375f3b7-adca-4707-b497-d32f7f2b7b01", "11PA: AZT 300 / 3TC 150 + LPV/r 100/25", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept twelvePP = install(new ConceptBuilder(TWELVE_PP)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("3db6e561-fed5-443f-8f69-f0f3d3b4cc04", "12PP: DRV 150 + r 50 + DTG 10 (± NRTIs)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept twelvePA = install(new ConceptBuilder(TWELVE_PA)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("05ba7f65-72a4-49fe-b54f-1e455444bd67", "12PA: DRV 150 + r 50 + DTG 50", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fourteenP = install(new ConceptBuilder(FOURTEEN_P)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("cf770dd0-4c9d-11ea-b77f-2e728ce88125", "14P: AZT / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fourteenPP = install(new ConceptBuilder(FOURTEEN_PP)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("55e43fc2-4977-4f0d-884f-40276b3831a7", "14PP: AZT 60 / 3TC 30 + DTG 10", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fourteenPA = install(new ConceptBuilder(FOURTEEN_PA)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("0f91810f-f9db-460f-920d-6b3493992534", "14PA: AZT 60 / 3TC 30 + DTG 50", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fifteenP = install(new ConceptBuilder(FIFTEEN_P)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("e2ec7f12-4c9d-11ea-b77f-2e728ce88125", "15P: ABC / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fifteenPp = install(new ConceptBuilder(FIFTEEN_PP)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("870dd964-61dd-4013-9a75-89b09994c6e6", "15PP: ABC / 3TC + DTG", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fifteenPA = install(new ConceptBuilder(FIFTEEN_PA)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("b326573a-ba4a-48ef-8afa-1bb991f3331e", "15PA: ABC 120 / 3TC 60 + DTG 50", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
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

        Concept seventeenPP = install(new ConceptBuilder(SEVENTEEN_PP)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("cb4e01be-7e35-4e72-b971-cf47d2ba6f11", "17PP: ABC 120 / 3TC 60 + EFV 200", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept seventeenPA = install(new ConceptBuilder(SEVENTEEN_PA)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("12a9268f-e06d-4d6d-b4d6-29312e79bd8c", "17PA: ABC 600 / 3TC 300 + EFV 200", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
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
                        fourPP,
                        fourPA,
                        fiveA,
                        sixA,
                        sevenA,
                        eightA,
                        nineA,
                        nineP,
                        ninePP,
                        ninePA,
                        tenA,
                        elevenA,
                        elevenP,
                        elevenPP,
                        elevenPA,
                        twelveA,
                        twelvePP,
                        twelvePA,
                        thirteenA,
                        fourteenA,
                        fourteenP,
                        fourteenPP,
                        fourteenPA,
                        fifteenA,
                        fifteenP,
                        fifteenPp,
                        fifteenPA,
                        sixteenA,
                        sixteenP,
                        seventeenA,
                        seventeenP,
                        seventeenPP,
                        seventeenPA,
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
                        fifteenPp,
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
                        fifteenPp,
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
                        fifteenPp,
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
        // Lab ID
        install(new ConceptBuilder("4A3CD51E-F542-4638-AAD1-0C19B742C31E")
                .datatype(text)
                .conceptClass(misc)
                .name("8CD19701-F3E4-49F0-841C-8F3C427E6172", "Lab ID", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
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
        Concept currentDrugs = install(new ConceptBuilder("65585192-977f-11e1-8993-905e29aff6c1")
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

        Concept concept10305 = install(new ConceptBuilder("162351AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125554BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Bar", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125555BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Bar(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10335 = install(new ConceptBuilder("162354AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125560BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Can", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125561BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Can(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10324 = install(new ConceptBuilder("162355AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125562BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Container", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125563BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Container(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10306 = install(new ConceptBuilder("162357AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125566BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Enema", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125567BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Enema(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10322 = install(new ConceptBuilder("162358AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125569BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Fluid ounce", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125568BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Oz", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125570BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Oz(s)", Locale.ENGLISH, null)
                .name("125571BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Fluid ounce(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10336 = install(new ConceptBuilder("162359AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125573BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Gallon", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125572BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "gal", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125574BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Gallon(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10313 = install(new ConceptBuilder("162360AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125578BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Gum", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125577BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Gum(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10312 = install(new ConceptBuilder("162361AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125580BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Inch", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125579BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "in", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125581BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Inch(es)", Locale.ENGLISH, null)
                .build());

        Concept concept10329 = install(new ConceptBuilder("162264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("125319BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "International units", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125320BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "IU", Locale.ENGLISH, ConceptNameType.SHORT)
                .description("17187FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Unit of measurement", Locale.ENGLISH)
                .build());

        Concept concept10304 = install(new ConceptBuilder("162362AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125584BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Kilogram", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125582BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "kg", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125583BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Kilogram(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10309 = install(new ConceptBuilder("162262AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125315BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Liter", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125318BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "L", Locale.ENGLISH, ConceptNameType.SHORT)
                .description("17185FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Unit of measurement, 1000 milliliters", Locale.ENGLISH)
                .build());

        Concept concept10328 = install(new ConceptBuilder("162363AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(text)
                .conceptClass(unitsOfMeasure)
                .name("125585BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Lozenge", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125588BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Troche(s)", Locale.ENGLISH, null)
                .name("125587BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Lozenge(s)", Locale.ENGLISH, null)
                .name("125586BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Troche", Locale.ENGLISH, null)
                .build());

        Concept concept10308 = install(new ConceptBuilder("162364AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125592BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Milliequivalent", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125590BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MEq", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125589BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Milliequivalent(s)", Locale.ENGLISH, null)
                .name("125591BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MEqs", Locale.ENGLISH, null)
                .build());

        Concept concept10315 = install(new ConceptBuilder("162365AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125593BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Metric drop", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125594BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Metric drop(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10319 = install(new ConceptBuilder("162366AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125595BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Microgram", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125598BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Microgram(s)", Locale.ENGLISH, null)
                .name("125597BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "mcg", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125596BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "ug", Locale.ENGLISH, null)
                .build());

        Concept concept10311 = install(new ConceptBuilder("161553AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("111176BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Milligram", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("111177BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "mg", Locale.ENGLISH, ConceptNameType.SHORT)
                .description("17091FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Unit of measurement of mass", Locale.ENGLISH)
                .build());

        Concept concept10321 = install(new ConceptBuilder("162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125316BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Milliliter", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125317BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "mL", Locale.ENGLISH, ConceptNameType.SHORT)
                .description("17186FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "Unit of measurement 1/1000 of a liter", Locale.ENGLISH)
                .build());

        Concept concept10300 = install(new ConceptBuilder("162367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125600BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Million units", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125599BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "MU", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept concept10301 = install(new ConceptBuilder("162368AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125602BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Nebulizer", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125604BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Neb", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125601BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Nebulizer(s)", Locale.ENGLISH, null)
                .name("125603BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Neb(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10302 = install(new ConceptBuilder("162369AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125606BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Pad", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125605BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Pad(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10327 = install(new ConceptBuilder("162370AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125607BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Patch", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125608BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Patch(es)", Locale.ENGLISH, null)
                .build());

        Concept concept10332 = install(new ConceptBuilder("162371AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125609BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Pint", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125610BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Pint(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10299 = install(new ConceptBuilder("162372AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125612BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Puff", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125611BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Puff(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10325 = install(new ConceptBuilder("162373AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125616BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Ring pessary", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125614BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Ring pessary(ies)", Locale.ENGLISH, null)
                .name("125615BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Ring suppository", Locale.ENGLISH, null)
                .name("125613BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Ring suppository(ies)", Locale.ENGLISH, null)
                .build());

        Concept concept10331 = install(new ConceptBuilder("162374AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125619BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Scoopful", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125618BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Scoop(s)", Locale.ENGLISH, null)
                .name("125617BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Scoop", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept concept10316 = install(new ConceptBuilder("162375AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125620BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Spray", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125621BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Spray(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10320 = install(new ConceptBuilder("1518AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("1775BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Suppository", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("1776BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Suppositoire", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("125622BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Supp", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125623BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Suppository(ies)", Locale.ENGLISH, null)
                .build());

        Concept concept10303 = install(new ConceptBuilder("162377AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125627BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Syringe", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125626BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Syringe(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10317 = install(new ConceptBuilder("162378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125628BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Tablespoon", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125629BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Tablespoon(s)", Locale.ENGLISH, null)
                .name("125630BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Tbsp", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept concept10333 = install(new ConceptBuilder("162379AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125631BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Teaspoon", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125632BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Teaspoon(s)", Locale.ENGLISH, null)
                .name("125633BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Tsp", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept concept10334 = install(new ConceptBuilder("162381AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125636BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Unit", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125637BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Unit(s)", Locale.ENGLISH, null)
                .name("125638BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "U", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept concept10326 = install(new ConceptBuilder("162382AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125640BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Vial", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125639BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Vial(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10330 = install(new ConceptBuilder("162383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("125641BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Biscuit wafer", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125642BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Wafer", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125643BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Wafer(s)", Locale.ENGLISH, null)
                .build());

        Concept concept10743 = install(new ConceptBuilder("67404d33-6a38-454f-806c-065db9aa5b9f")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5dc8ae38-44f2-4ce5-a661-864b60ec6465", "Pump", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("295e9a7a-b664-4d83-8018-1f8ca2968d27", "Pompe", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("8ec527dc-31bb-4c10-a3c4-9a98723ad5e6", "Ponp", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept concept11679 = install(new ConceptBuilder("f2071d5f-9329-457e-89c7-574447fe3980")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("ded56519-3594-4532-a921-b201df84b759", "Milligram per square meter", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("09bda43d-1873-4f0c-9f38-1eaf4de977e1", "mg / m**2", Locale.ENGLISH, null)
                .name("b922eede-df02-4561-aca7-479cda37b119", "mg / m^2", Locale.ENGLISH, null)
                .build());

        Concept conceptDosingUnit = install(new ConceptBuilder("162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(coded)
                .conceptClass(misc)
                .name("125644BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Dosing unit", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("f8b90258-b3f3-4c60-96aa-5d9acc6b9335", "Unités par dose", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("125645BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Dose unit", Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(unitAmpule,
                        unitApplic,
                        concept10305,
                        unitBottle,
                        concept10335,
                        unitCapsule,
                        concept10324,
                        unitDrops,
                        concept10306,
                        concept10322,
                        concept10336,
                        unitGrams,
                        concept10313,
                        concept10312,
                        concept10329,
                        concept10304,
                        concept10309,
                        concept10328,
                        concept10308,
                        concept10315,
                        concept10319,
                        concept10311,
                        concept10321,
                        concept10300,
                        concept10301,
                        concept10302,
                        concept10327,
                        concept10332,
                        concept10299,
                        concept10325,
                        concept10331,
                        concept10316,
                        concept10320,
                        concept10303,
                        concept10334,
                        unitTablet,
                        concept10317,
                        concept10333,
                        unitTube,
                        concept10326,
                        concept10330,
                        unitSachet,
                        concept10743,
                        concept11679)
                .build());

        Concept concept9367 = install(new ConceptBuilder("9b0068ac-4104-4bea-ba76-851e5faa9f2a")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("9aff4f41-1830-403e-923b-ddddb8086d67", "Seven times a day (7D)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("e5665e74-1a19-41be-937c-2e3a28501abe", "7D", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept concept9368 = install(new ConceptBuilder("7cc0fa66-0467-4552-8dd7-501f2b325319")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("c071b610-2b96-4261-b8e5-25865594353f", "Eight times a day (8D)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("a721bdd4-3562-410f-b8ad-5bd1d9a3b92c", "Huit fois par jour", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("4e90a33d-2d49-4ca7-b964-e641802e7b3a", "8D", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept concept9369 = install(new ConceptBuilder("f28e1f49-2b56-4639-9312-fbe856fa0c51")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("d2c8e2a5-dd0e-4ddf-a530-ccddd59bcbd3", "Nine times a day (9D)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("70f526d7-c0ee-4c35-8d38-3dbd3cfa8698", "Neuf fois par jour", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("eb3c1080-1483-41b0-832a-cdadd5ee4410", "Nèf fwa nan yon jounen", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("182bffcd-3f1f-474e-860d-9219dc6820de", "9D", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("67a1c141-d68f-4b51-8eba-f28a0c3b54ec", "9D", Locale.FRENCH, ConceptNameType.SHORT)
                .name("00df8676-814f-480d-a6ca-55a545705554", "9D", locale_HAITI, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("379c5009-5233-4bcd-af6c-849ca5f6e3f6").type(sameAs).ensureTerm(pih, "9D").build())
                .mapping(new ConceptMapBuilder("1e0017cb-4764-4048-8f5b-a11c6b33370c").type(sameAs).ensureTerm(pih, "Nine times a day").build())
                .mapping(new ConceptMapBuilder("43420874-0ac4-4ab9-a092-310869522210").type(sameAs).ensureTerm(pih, "9369").build())
                .build());

        Concept concept9370 = install(new ConceptBuilder("059ac2ba-ce52-41e5-bac5-46707bac285a")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("1ffadcff-551f-42b1-aa21-d5aa48f08bfd", "Every 2 hours (Q2H)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("19ca2419-460c-496e-8edb-0db6ebfab3a7", "Tous les 2 heures", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("669743e9-b805-409b-b59c-27a830a144ae", "Chak 2 zèdtan", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("125236BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every two hours", Locale.ENGLISH, null)
                .name("dbc1a906-879b-4d25-adca-795f1ae5f2cf", "Q2H", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("828f306e-074c-4f98-953e-98232a72dec0", "Q2H", Locale.FRENCH, ConceptNameType.SHORT)
                .name("f306d778-c4bb-405e-b44e-29c645b08073", "Q2H", locale_HAITI, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("274969ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162245").build())
                .mapping(new ConceptMapBuilder("38f17d27-cbb6-4a8e-a721-3505f589a46d").type(sameAs).ensureTerm(pih, "9370").build())
                .mapping(new ConceptMapBuilder("f42dd7bf-abbd-4a0c-a9ed-5501531175e2").type(sameAs).ensureTerm(pih, "Q2H").build())
                .mapping(new ConceptMapBuilder("274968ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "225750008").build())
                .mapping(new ConceptMapBuilder("9ffcc450-32c7-4f83-a7ad-014aa8efed55").type(sameAs).ensureTerm(pih, "Every 2 hours").build())
                .build());

        Concept concept9371 = install(new ConceptBuilder("f295e71a-0d7b-4a70-a193-2a6c3a73e9ed")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("29d95ac4-64b7-466c-8760-8ee734850d58", "Every 3 hours (Q3H)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("aaa54da9-e016-494e-a1c3-6935252be20e", "Tous les 3 heures", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("baddd195-147b-4d5e-8bb6-56e90f9f3c48", "Chak 3 zèdtan", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("b31e0830-45d5-49f4-b82d-3fdae858c43e", "Q3H", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125238BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every three hours", Locale.ENGLISH, null)
                .name("42b5384e-b9d6-41f9-8065-0c4bccd5ee87", "Q3H", Locale.FRENCH, ConceptNameType.SHORT)
                .name("c3750ca8-395c-4b90-8446-bf65f9a76a17", "Q3H", locale_HAITI, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("c192d78c-3415-48b6-ac13-b885a7b663fd").type(sameAs).ensureTerm(pih, "Every 3 hours").build())
                .mapping(new ConceptMapBuilder("274971ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162246").build())
                .mapping(new ConceptMapBuilder("fc605342-4074-4f82-ab27-740e21d4f925").type(sameAs).ensureTerm(pih, "9371").build())
                .mapping(new ConceptMapBuilder("274970ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "225753005").build())
                .mapping(new ConceptMapBuilder("78245e52-1a85-45c6-a368-08a7716fb836").type(sameAs).ensureTerm(pih, "Q3H").build())
                .build());

        Concept concept9372 = install(new ConceptBuilder("4c443187-7db1-4678-81b5-83a0c4213cfa")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("65ed571d-0cf7-4d8c-a5e1-8ac31666ebf9", "Every 4 hours (Q4H)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("a77f57c8-cea7-46df-8248-925355d93cda", "Tous les 4 heures", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("9c7de514-da34-44a1-835f-d2167021ee51", "Chak 4 èdtan", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("6a71f863-43b2-4fe5-b684-d6706697379f", "Q4H", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125239BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every four hours", Locale.ENGLISH, null)
                .name("9e59e05d-f793-46a8-a7d4-285ff55729af", "Q4H", Locale.FRENCH, ConceptNameType.SHORT)
                .name("3a46df5f-042a-44ed-882d-2a68791da7bc", "Q4H", locale_HAITI, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("244fc711-4632-4822-a981-f1e0a0c0a42e").type(sameAs).ensureTerm(pih, "Every 4 hours").build())
                .mapping(new ConceptMapBuilder("274973ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162247").build())
                .mapping(new ConceptMapBuilder("461385cb-c736-4429-86fc-d3e958e0f6ad").type(sameAs).ensureTerm(pih, "Q4H").build())
                .mapping(new ConceptMapBuilder("274972ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "225756002").build())
                .mapping(new ConceptMapBuilder("c9818fff-7c69-4fd6-b88a-632842379658").type(sameAs).ensureTerm(pih, "9372").build())
                .build());

        Concept concept9373 = install(new ConceptBuilder("513e8901-7909-4ad3-9bea-1874ed9a6ba2")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("7904ac05-4761-484e-b11d-12da3721405e", "Every 6 hours (Q6H)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("4335addf-c2b3-49ce-80a3-55f4711cd5f8", "Tous les 6 heures", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("e00dc6cc-cd41-4f93-a627-a78d67063b5f", "Chak 6 zèdtan", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("125244BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every six hours", Locale.ENGLISH, null)
                .name("db35b0b0-9f99-4d80-8c36-f418472e5219", "Q6H", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("d9e3b19b-bd52-4767-8eb5-550221ac753f", "Q6H", Locale.FRENCH, ConceptNameType.SHORT)
                .name("c505ddc1-cef1-4dfd-a08e-15631b8d1063", "Q6H", locale_HAITI, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("46ff07ae-9d0d-4d69-87fb-efe3fb5f0fc9").type(sameAs).ensureTerm(pih, "Q6H").build())
                .mapping(new ConceptMapBuilder("274976ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "307468000").build())
                .mapping(new ConceptMapBuilder("d091d00e-6326-40f0-a79e-dde4304a6304").type(sameAs).ensureTerm(pih, "9373").build())
                .mapping(new ConceptMapBuilder("9bdff2c6-0b64-400f-8d61-2c37dc18e162").type(sameAs).ensureTerm(pih, "Every 6 hours").build())
                .mapping(new ConceptMapBuilder("274977ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162249").build())
                .build());

        Concept concept9374 = install(new ConceptBuilder("894ef033-1135-4c52-8284-7de9b157e824")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("7fca7e80-1136-4397-93ee-b85699e39846", "Every 12 hours (Q12H)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("b38cddc8-bdac-49d1-b4aa-25a4a1ff74de", "Tous les 12 heures", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("28b18adf-2546-4bdc-8140-ee854e4a399e", "Chak 12 zèdtan", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("125247BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every twelve hours", Locale.ENGLISH, null)
                .name("03e442de-986a-49c2-9203-09ad1cc8c786", "Q12H", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("b607c9b4-bc36-43d5-b450-f16945f1132e", "Q12H", Locale.FRENCH, ConceptNameType.SHORT)
                .name("60004d6d-582a-4a2f-beb4-9837cacb4a6e", "Q12H", locale_HAITI, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("8eab4b4c-cdd5-4432-91f7-6c064499ecbf").type(sameAs).ensureTerm(pih, "9374").build())
                .mapping(new ConceptMapBuilder("274981ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162251").build())
                .mapping(new ConceptMapBuilder("274980ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "307470009").build())
                .mapping(new ConceptMapBuilder("10120f6e-0d65-4f17-a23a-ac47a3b0b074").type(sameAs).ensureTerm(pih, "Every 12 hours").build())
                .mapping(new ConceptMapBuilder("3b33cee0-e230-4ad6-a2a1-023b3c479d4e").type(sameAs).ensureTerm(pih, "Q12H").build())
                .build());

        Concept concept11035 = install(new ConceptBuilder("160863AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109558BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Once daily, at bedtime (qHS)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109559BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "qHS", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("109557BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Once a day, at bedtime", Locale.ENGLISH, null)
                .name("125287BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Once per day, at bedtime", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("217981ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160863").build())
                .mapping(new ConceptMapBuilder("145039ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396142006").build())
                .build());

        Concept concept11025 = install(new ConceptBuilder("160865AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109563BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Once daily, in the morning (qAM)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109564BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "qAM", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125289BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Once per day, in the morning", Locale.ENGLISH, null)
                .name("109565BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Once a day, in the morning", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("217983ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160865").build())
                .mapping(new ConceptMapBuilder("145041ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "229797004").build())
                .build());

        Concept concept11031 = install(new ConceptBuilder("160861AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109552BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice daily with meals (BIDWM)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109551BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "BIDWM", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125285BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice per day with meals", Locale.ENGLISH, null)
                .name("109550BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice a day with meals", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("217979ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160861").build())
                .mapping(new ConceptMapBuilder("145037ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "421589008").build())
                .build());

        Concept concept11034 = install(new ConceptBuilder("160860AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109548BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice daily after meals (BIDPC)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125284BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice per day after meals", Locale.ENGLISH, null)
                .name("109549BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "BIDPC", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("109547BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice a day after meals", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("217978ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160860").build())
                .mapping(new ConceptMapBuilder("145036ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "422231003").build())
                .build());

        Concept concept11027 = install(new ConceptBuilder("160859AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109546BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice daily before meals (BIDAC)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109544BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "BIDAC", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("109545BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice a day before meals", Locale.ENGLISH, null)
                .name("125283BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Twice per day before meals", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("217977ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160859").build())
                .mapping(new ConceptMapBuilder("145035ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "422231003").build())
                .build());

        Concept concept11029 = install(new ConceptBuilder("160867AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109571BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Thrice daily, after meals (TIDPC)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("126447BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "3 times per day, after meals", Locale.ENGLISH, null)
                .name("109569BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Three times a day, after meals", Locale.ENGLISH, null)
                .name("125291BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Three times per day, after meals", Locale.ENGLISH, null)
                .name("109570BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "TIDPC", Locale.ENGLISH, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("145043ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "421409007").build())
                .mapping(new ConceptMapBuilder("217985ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160867").build())
                .build());

        Concept concept11036 = install(new ConceptBuilder("160868AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109572BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Thrice daily, before meals (TIDAC)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125292BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Three times per day, before meals", Locale.ENGLISH, null)
                .name("109573BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Three times a day, before meals", Locale.ENGLISH, null)
                .name("109574BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "TIDAC", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("126449BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "3 times per day, before meals", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("145044ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396145008").build())
                .mapping(new ConceptMapBuilder("217986ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160868").build())
                .build());

        Concept concept11018 = install(new ConceptBuilder("160869AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109576BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Thrice daily, with meals (TIDWM)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109577BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "TIDWM", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125293BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Three times per day, with meals", Locale.ENGLISH, null)
                .name("126448BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "3 times per day, with meals", Locale.ENGLISH, null)
                .name("109575BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Three times a day, with meals", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("217987ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160869").build())
                .mapping(new ConceptMapBuilder("145045ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396146009").build())
                .build());

        Concept concept11038 = install(new ConceptBuilder("160871AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109582BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Four times daily, after meals and at bedtime (QIDPCHS)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109583BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Four times a day, after meals and at bedtime", Locale.ENGLISH, null)
                .name("125295BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Four times per day, after meals and at bedtime", Locale.ENGLISH, null)
                .name("109584BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "QIDPCHS", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("126452BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "4 times per day, after meals and at bedtime", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("145047ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "421969000").build())
                .mapping(new ConceptMapBuilder("217989ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160871").build())
                .build());

        Concept concept11023 = install(new ConceptBuilder("160872AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109586BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Four times daily, before meals and at bedtime (QIDACHS)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125296BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Four times a day, before meals and at bedtime", Locale.ENGLISH, null)
                .name("109587BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "QIDACHS", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("126451BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "4 times per day, before meals and at bedtime", Locale.ENGLISH, null)
                .name("109585BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "qac and qhs", Locale.ENGLISH, null)
                .name("125297BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Four times per day, before meals and at bedtime", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("145048ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "420660006").build())
                .mapping(new ConceptMapBuilder("217990ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160872").build())
                .build());

        Concept concept11024 = install(new ConceptBuilder("162135AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("124906BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "One time", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125298BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Once", Locale.ENGLISH, null)
                .name("124907BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Single event", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("237146ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162135").build())
                .mapping(new ConceptMapBuilder("237145ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "307486002").build())
                .build());

        Concept concept11037 = install(new ConceptBuilder("162136AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("124909BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "One time STAT", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("124908BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Single event immediately", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("237147ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(narrowerThan).ensureTerm(snomedNp, "307486002").build())
                .mapping(new ConceptMapBuilder("237148ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162136").build())
                .build());

        Concept concept11030 = install(new ConceptBuilder("162243AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125231BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every 30 min (q30m)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125230BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q30m", Locale.ENGLISH, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("274964ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "225767001").build())
                .mapping(new ConceptMapBuilder("274965ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162243").build())
                .build());

        Concept concept11028 = install(new ConceptBuilder("162244AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125232BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every hour (q1h)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125233BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q1h", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125234BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "hourly", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("274966ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "225768006").build())
                .mapping(new ConceptMapBuilder("274967ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162244").build())
                .build());

        Concept concept11019 = install(new ConceptBuilder("162248AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125242BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every five hours (q5h)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125270BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every 5 hours", Locale.ENGLISH, null)
                .name("125241BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q5h", Locale.ENGLISH, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("274974ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396137003").build())
                .mapping(new ConceptMapBuilder("274975ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162248").build())
                .build());

        Concept concept11032 = install(new ConceptBuilder("162250AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125246BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every eight hours (q8h)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125272BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every 8 hours", Locale.ENGLISH, null)
                .name("125245BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q8h", Locale.ENGLISH, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("274979ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162250").build())
                .mapping(new ConceptMapBuilder("274978ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "307469008").build())
                .build());

        Concept concept11017 = install(new ConceptBuilder("162252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125250BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every twenty-four hours (q24h)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125274BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every 24 hours", Locale.ENGLISH, null)
                .name("125249BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q24h", Locale.ENGLISH, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("274983ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162252").build())
                .mapping(new ConceptMapBuilder("274982ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396125000").build())
                .build());

        Concept concept11021 = install(new ConceptBuilder("162254AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125254BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every thirty-six hours (q36h)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125253BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q36h", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125276BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every 36 hours", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("274987ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162254").build())
                .mapping(new ConceptMapBuilder("274986ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396126004").build())
                .build());

        Concept concept11020 = install(new ConceptBuilder("162253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125251BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every forty-eight hours (q48h)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125252BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q48h", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125275BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every 48 hours", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("274984ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396125000").build())
                .mapping(new ConceptMapBuilder("274985ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162253").build())
                .build());

        Concept concept11022 = install(new ConceptBuilder("162255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125255BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every seventy-two hours (q72h)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125256BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "q72h", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("125277BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every 72 hours", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("274989ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162255").build())
                .mapping(new ConceptMapBuilder("274988ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "396126004").build())
                .build());

        Concept concept11026 = install(new ConceptBuilder("162256AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("125266BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Every Monday, Wednesday and Friday (qMWF)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("125264BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Three times a week on Monday, Wednesday and Friday", Locale.ENGLISH, null)
                .name("125265BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "qMWF", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("126450BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "3 times a week on Monday, Wednesday and Friday", Locale.ENGLISH, null)
                .mapping(new ConceptMapBuilder("274991ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "444763000").build())
                .mapping(new ConceptMapBuilder("274992ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "162256").build())
                .build());

        Concept whenRequired = install(new ConceptBuilder("160857AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(notApplicable)
                .conceptClass(frequency)
                .name("109540BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "When required (PRN)", Locale.ENGLISH, null)
                .name("1542bc96-9c5f-4100-875d-271a65302bfa", "Si nécessaire", Locale.FRENCH, ConceptNameType.FULLY_SPECIFIED)
                .name("cce30aaf-a45e-4d8f-b6b2-5e7d6fefaba5", "Lè sa nesesè", locale_HAITI, ConceptNameType.FULLY_SPECIFIED)
                .name("109539BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "On an as needed basis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("109538BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "PRN", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("8eea2920-efe7-47a0-8017-1eb6b61c2e6d", "PRN", Locale.FRENCH, ConceptNameType.SHORT)
                .name("66f48261-72c1-4c2e-bf52-42006ca2c8bd", "PRN", locale_HAITI, ConceptNameType.SHORT)
                .mapping(new ConceptMapBuilder("217975ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160857").build())
                .mapping(new ConceptMapBuilder("145033ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(snomedCt, "225761000").build())
                .mapping(new ConceptMapBuilder("65add4ca-6f2f-4eaa-9915-cca8b128fcc2").type(sameAs).ensureTerm(pih, "PRN").build())
                .build());

        Concept medsFreq = install(new ConceptBuilder("6563fb14-977f-11e1-8993-905e29aff6c1")
                .datatype(coded)
                .conceptClass(question)
                .name("66077366-977f-11e1-8993-905e29aff6c1", "Drug frequency coded", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("7b4ad1d5-4b11-463d-90b8-82c2027eb06a", "Freq", Locale.ENGLISH, ConceptNameType.SHORT)
                .answers(onceAday, twiceAday, onceAmonth, threeADay, fourTimesAday, fiveTimesAday, sixTimesAday, onceAweek, concept9367, concept9368, concept9369, inTheMorn, onceAdayAtnight, variable, onceAdatAtnoon, whenRequired, immediate, concept9370, concept9371, concept9372, concept9373, concept9374, otherFreq, concept11035, inTheEve, everyDay, concept11025, concept11031, concept11034, concept11027, concept11029, concept11036, concept11018, concept11038, concept11023, concept11024, concept11037, concept11030, concept11028, concept11019, concept11032, concept11017, concept11021, concept11020, concept11022, concept11026)
                .build());

        Concept concept9072 = install(new ConceptBuilder("ef7f742b-76e6-4a83-84ca-534ad6705494")
                .datatype(text)
                .conceptClass(miscOrder)
                .name("4369a18f-1c8d-483e-a2b0-9b382964afc6", "Prescription instructions, non-coded", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("49dfe9e7-d6ee-4c2c-9155-4dab31668db4", "Instructions for how often, in what quantity, and in what form a prescribed medication should be administered.", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("01360d6f-6c8c-49e4-86dc-7521c0824cbe").type(sameAs).ensureTerm(pih, "9072").build())
                .mapping(new ConceptMapBuilder("6c3ba3e4-6812-4cd8-ac20-229508dacd86").type(sameAs).ensureTerm(pih, "Prescription instructions non-coded").build())
                .mapping(new ConceptMapBuilder("1a5f86ed-cf9c-4db5-9378-6454d6b4c870").type(sameAs).ensureTerm(emrapi, "Prescription instructions non-coded").build())
                .build());

        Concept concept12651 = install(new ConceptBuilder("b08011b8-b1c7-4fd0-b48d-65a475397639")
                .datatype(coded)
                .conceptClass(question)
                .name("48cd1100-0d05-460f-965e-39e8bf8136e4", "Routes of administration (coded)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("5e5631eb-bfc6-488b-9105-c0fdefd7d974").type(sameAs).ensureTerm(pih, "12651").build())
                .build());

        Concept concept10634 = install(new ConceptBuilder("f541afbd-db59-4c48-88da-ce17b8184963")
                .datatype(coded)
                .conceptClass(drug)
                .name("28e3e738-8e05-419e-8bbf-e33598718ce1", "Mental health medication", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("9940a83f-28c1-4449-a620-1161d7102565").type(sameAs).ensureTerm(pih, "Mental health medication").build())
                .mapping(new ConceptMapBuilder("725dba8b-1aa8-4d53-8c7a-cc807632fb00").type(sameAs).ensureTerm(pih, "10634").build())
                .build());

        Concept durationMeds = install(new ConceptNumericBuilder(DURATION_MEDS)
                .conceptClass(question)
                .datatype(numeric)
                .precise(false)
                .name("94ffc78f-24d5-4c55-bc13-8b9d035ac2f5", "Medication duration", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("D94DD149-D38E-4007-BC04-46424F76EFA0")
                        .type(sameAs).ensureTerm(ciel, "159368").build())
                .build());

        Concept quantityPerDose = install(new ConceptNumericBuilder("160856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .datatype(numeric)
                .conceptClass(question)
                .precise(true)
                .name("109537BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "Quantity of medication prescribed per dose", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("16909FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "The amount of a medication to be taken in a given dose, for example, take 2 pills every four hours (2 is the quantity, pills are the form and every four hours is the frequency)", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("217974ABBBBBBBBBBBBBBBBBBBBBBBBBBBBB").type(sameAs).ensureTerm(ciel, "160856").build())
                .mapping(new ConceptMapBuilder("ed6cb426-0cf3-11ec-9f8b-aa0059ea79c6").type(sameAs).ensureTerm(pih, "9073").build())
                .build());

        Concept medication_prescription_construct = install(new ConceptBuilder("3269F65B-1A28-42EE-8578-B9658387AA00")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("E31457B1-6A34-4596-AB9A-394DC5D01B61", "Prescription construct", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("98832E23-4AAE-413E-99C2-87AFBC9074A6", "Collects all pieces of information about a medication or product that was prescribed for the patient by a clinician", Locale.ENGLISH)
                .setMembers(currentDrugs, amountDispensed, quantityPerDose, conceptDosingUnit, medsFreq, durationMeds, timeUnits, generalDrugFreq, concept9072, concept12651)
                .build());

        medication_prescription_construct.setSet(true);

        install(new ConceptBuilder(URINE_LAM)
                .datatype(coded)
                .conceptClass(test)
                .name("98f9594c-e0fd-4710-967a-ebb5a52f2362", "Urine LAM / CrAg Result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(positive,negative)
                .build());

        install(new ConceptBuilder(RIFAPENTINE)
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("5883ced2-8065-4c87-82f4-df9c984ec1e1", "Rifapentine", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("a6c7b840-30f2-448d-a12a-f3de3516af45","RFP",Locale.ENGLISH,ConceptNameType.SHORT)
                .description("059019b8-d0e5-48bb-a64c-c8cb061745a3","Antibiotic used in the treatment of tuberculosis",Locale.ENGLISH)
                .build());

        install(new ConceptBuilder(TEST_RESULT)
                .datatype(coded)
                .conceptClass(question)
                .name("74bc8c1e-2438-4029-8d29-44e004d4a04d", "Test Result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept pass = install(new ConceptBuilder(PASS)
                .datatype(coded)
                .conceptClass(question)
                .name("54d42b74-6bb2-423f-af02-c31cd21c086f", "Pass", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept fail = install(new ConceptBuilder(FAIL)
                .datatype(coded)
                .conceptClass(question)
                .name("ff0a837c-3c76-4981-bbb3-9249043822c1", "Fail", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder(TEST_RESULT)
                .datatype(coded)
                .conceptClass(question)
                .name("74bc8c1e-2438-4029-8d29-44e004d4a04d", "Test Result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(pass, fail)
                .build());

        Concept leftEar = install(new ConceptBuilder(LEFT_EAR)
                .datatype(coded)
                .conceptClass(question)
                .name("4e98b9ae-7a57-41b0-8b5e-516dd3b1d8ea", "Left Ear", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(pass, fail)
                .build());

        Concept rightEar = install(new ConceptBuilder(RIGHT_EAR)
                .datatype(coded)
                .conceptClass(question)
                .name("d68cb0ef-6f44-48e3-806e-ca77c1e65f2f", "Right Ear", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(pass, fail)
                .build());

        Concept threeHp = install(new ConceptBuilder("0f233aab-1c6b-4135-883f-47bd95f62432")
                .datatype(notApplicable)
                .conceptClass(unitsOfMeasure)
                .name("86035c48-55f7-4697-8a2b-5e92a3dc3274", "3HP (Rifapentine and Isoniazid)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .name("052983eb-e55b-492d-b2df-16924af8b04f", "3HP (RFP/INH)", Locale.ENGLISH, ConceptNameType.SHORT)
                .build());

        Concept phqninescore = install(new ConceptBuilder("e1bc7567-aec8-48b6-987e-b4a53d15787b")
                .datatype(numeric)
                .conceptClass(misc)
                .name("ceb4fb8f-c43d-4b88-95cc-e495e29f4962","PHQ 9 Score",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept tbXpert=install(new ConceptBuilder("2eedf1c7-1c59-4f77-83ea-1196ebf14e12")
                .datatype(coded)
                .conceptClass(test)
                .name("7f48b0ac-5576-400d-b032-67e49bb4cbc0","TB Xpert",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .answers(positive, negative)
                .build());

        Concept tbXpertDate=install(new ConceptBuilder("c3d512e4-dd15-4943-aa5e-9c52164eea5e")
                .datatype(date)
                .conceptClass(question)
                .name("30cf5bc8-6582-4efd-a786-a9b7405af497","Date of TB Xpert test",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept crag=install(new ConceptBuilder("b2b2aa80-fd10-4a05-9a63-3eaf335135a9")
                .datatype(coded)
                .conceptClass(test)
                .name("aea38233-2e13-4eca-8d69-0a5512e65533","CrAg Results",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .answers(positive, negative)
                .build());

        Concept cragDate=install(new ConceptBuilder("3e033e46-5bdf-41d6-9b9a-26457f6b0da5")
                .datatype(date)
                .conceptClass(question)
                .name("115b57c3-13b4-448b-b1ca-5b40826d6280","Date of CrAg test",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept urineLamDate = install(new ConceptBuilder("255188ca-7ee5-426f-a733-7381f0d78f21")
                .datatype(date)
                .conceptClass(question)
                .name("f06433e4-73f0-4977-9884-df1108559def", "Date of Urine Lam", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());


        Concept oilGiven = install(new ConceptBuilder("0b4cd73b-967a-4bec-acfd-c95ac0b214f0")
                .datatype(numeric)
                .conceptClass(question)
                .name("d3b47ef7-e6eb-4621-bf87-95defc407819", "Oil given to patient(Liters)", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());


        Concept indirectBilirubin = install(new ConceptNumericBuilder("7aa2e05d-d9f4-49ab-b340-11ebb1f48120")
                .datatype(numeric)
                .conceptClass(test)
                .units("mg/dL")
                .precise(false)
                .name("787b7a43-d225-46da-acd9-6809b6b026a2", "In Bili", Locale.ENGLISH, ConceptNameType.SHORT)
                .name("815a078e-39dd-4f6f-8204-2a9404ea1220", "Indirect bilirubin", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("80f14d86-edab-40f5-bdf7-4f6b9123ba97", "a test that shows excessive breakdown of red blood cells or some other conditions in the Liver", Locale.ENGLISH)
                .build());

        Concept microscopy = install(new ConceptBuilder("d91e1b96-c342-4d0a-9e4c-91226987e22b")
                .datatype(coded)
                .conceptClass(question)
                .name("1f20d9fe-75e8-4348-8d8b-01a60fe7b972", "Microscopy", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("84a94f84-3012-4cc1-9943-b96c338d8c87","observation of a patient's peripheral blood samples under a microscope and counting sickle cells",Locale.ENGLISH)
                .answers(yes, no)
                .build());

        Concept microscopyDate = install(new ConceptBuilder("a90b4936-1d2d-464e-a1d4-aed11c822956")
                .datatype(date)
                .conceptClass(question)
                .name("05363237-7abd-4d19-8146-56408f45fc3f", "Microscopy date", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("a5861c93-cf8a-4e7d-8480-9d137fe801f9","Date of Microscopy",Locale.ENGLISH)
                .build());

        Concept hbElectrophoresis = install(new ConceptBuilder("fe8ce76a-021b-4ffa-a993-4362734be411")
                .datatype(coded)
                .conceptClass(question)
                .name("bc6815d0-60ba-4487-8e33-91103c643260", "HB electrophoresis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("cbf659dc-7dc0-42b7-b70c-fdc5468e246e","a test that measures the different types of hemoglobin in the blood",Locale.ENGLISH)
                .answers(yes, no)
                .build());

        Concept hbElectrophoresisDate = install(new ConceptBuilder("2f7d5eef-9857-4f61-98af-1a928d2586d5")
                .datatype(date)
                .conceptClass(question)
                .name("1e961897-4267-4010-839b-fd3d83944bcc", "HB electrophoresis date", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("ad06304f-8244-464c-bf6e-4d5a9a660c9e","Date of HB electrophoresis",Locale.ENGLISH)
                .build());

        Concept medicationSideEffects = install(new ConceptBuilder("d046e48d-7eda-4a6f-b583-c13281d9640e")
                .datatype(coded)
                .conceptClass(question)
                .name("4af4e245-4b21-4aca-8ef4-faa261a1800b", "Medication Side Effects", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("bada305e-0aaf-45e1-884b-39900a3a5ea0","has the patient experienced any side effects due to the current medication",Locale.ENGLISH)
                .answers(yes, no)
                .build());

        Concept hydroxyurea = install(new ConceptBuilder("e1742ab7-b110-4378-8718-50933c4cfd7c")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("fa344423-0b7c-44a4-a0c0-2456b8340c5e", "Hydroxyurea", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("fa344423-0b7c-44a4-a0c0-2456b8340c5e","oral medicine for sickle cell, recommended daily use for children and adults with hemoglobin SS or Sβ0 thalassemia who have frequent painful episodes, recurrent chest crises, or severe anemia",Locale.ENGLISH)
                .build());

        Concept rapidTesting = install(new ConceptBuilder("cfccd5fb-f9dd-48b6-ab0c-c4ee78b4341d")
                .datatype(coded)
                .conceptClass(question)
                .name("f4fc6e39-a31f-4098-9441-25fa21fe094d", "Rapid Testing", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept rapidTestingDate = install(new ConceptBuilder("84d09dae-f6a8-4117-adae-4fb3b0c1d352")
                .datatype(date)
                .conceptClass(question)
                .name("9201efb4-7f51-4f76-a66f-9bf677dfaa52", "Rapid testing date", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("9ed2d9f0-c744-4d4a-9bed-584dc9e8f22c","Date of rapid testing",Locale.ENGLISH)
                .build());

        Concept parent = install(new ConceptBuilder("adfee692-442e-4e36-8fac-b6ca4249c6b8")
                .datatype(coded)
                .conceptClass(question)
                .name("6819dca1-b3a0-439e-93f9-b76a7632fa03", "Parent relationship", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("6819dca1-b3a0-439e-93f9-b76a7632fa03","family member of the patient with a relationship of parent",Locale.ENGLISH)
                .answers(yes, no)
                .build());

        Concept sibling = install(new ConceptBuilder("76bfbe9a-9f7c-44aa-85af-d2d5f08e4baa")
                .datatype(coded)
                .conceptClass(question)
                .name("89bab811-c1fe-4025-ab62-0f445104bcb3", "Sibling relationship", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("933ece89-cb86-40ee-ba1c-712ba2d68733","family member of the patient with a relationship of sibling",Locale.ENGLISH)
                .answers(yes, no)
                .build());

        Concept enlargedLiver = install(new ConceptBuilder("5c05bcc5-a25e-4e36-98ef-579060c4c460")
                .datatype(coded)
                .conceptClass(question)
                .name("7fa5052c-5adf-4b55-ae44-8f758a01d2fc", "Enlarged Liver", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("11574923-8a9d-4031-b59a-dff004c4d3ba","a sign of an underlying problem, such as liver disease, congestive heart failure or cancer.",Locale.ENGLISH)
                .answers(yes, no)
                .build());

        Concept cotrimoxazoleStartDate = install(new ConceptBuilder("434fd4bb-41a9-4c7c-8dcd-48c574a9f1cc")
                .datatype(date)
                .conceptClass(question)
                .name("64f94082-dfd5-45a4-be7b-22b46b132418", "Cotrimoxazole Start Date", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("3ccdf720-69f2-4c26-ba37-9ccf171eda0e","The date which the patient started cotrimoxazole prophylaxis ",Locale.ENGLISH)
                .build());

        Concept A=install(new ConceptBuilder("4301ebd1-8675-4977-ba23-2b1c9bbe12bc")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("c8cef104-0122-4514-a687-e131ed1c31b4","A",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("e18b6d12-4d8b-4e20-a932-71a485195d3a","Started ARV before TB treatment",Locale.ENGLISH)
                .build());

        Concept B=install(new ConceptBuilder("cf3e6b3e-d1cf-42b1-b2d9-4597d86d6c0b")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6af48f6a-7795-495b-afd8-4afb2f85ffe3","B",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("ae91926f-c8a8-4e54-a138-cd009c140961","Started ARV while on TB treatment",Locale.ENGLISH)
                .build());

        Concept C=install(new ConceptBuilder("8478ca70-596d-432b-b0f2-b8059c63b4a5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6b3d53a5-9ac9-485b-bca4-30cd1ed7ab86","C",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("ea64b80c-daee-4223-9a89-af74c584ccab","ARV not started on discharge from TB treatment",Locale.ENGLISH)
                .build());

        Concept tbArvstatus= install(new ConceptBuilder("38362892-90eb-41d0-b50b-49ecc2152d2e")
                .datatype(coded)
                .conceptClass(question)
                .name("31a0f7dd-472d-403d-9348-bbf5e3ed3850","TB ARV Status",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("843779bf-d5c7-4db8-8990-aef308f73a34","ARV status during TB registration",Locale.ENGLISH)
                .answers(A,B,C)
                .build());

        Concept clinicallyConfirmed=install(new ConceptBuilder("0614a1b0-4cb1-4214-81a3-7a65729fd23f")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("b126b4d6-9085-4226-b10c-625f99d1a4a6","TB Clinically Diagnosed",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("c4169462-3253-4173-b259-75a320f7af19","Active TB cases diagnosed by a clinician or other medical practitioner who has decided to give the patient a full course of TB treatment",Locale.ENGLISH)
                .build());

        Concept bacteriologicallyConfirmed=install(new ConceptBuilder("e7edeb84-dc6f-4fb5-84e9-5468fb9e3234")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1c63deaf-fd97-47f3-aaf1-f26243b15679","TB Bacteriologically Confirmed",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("aae1c1dc-6345-4071-bb43-49527186e2aa","Biological specimen is positive by smear microscopy, culture or WRD (e.g., Xpert MTB/RIF)",Locale.ENGLISH)
                .build());

        Concept tbCaseConfirmation=install(new ConceptBuilder("005bed28-d8f9-465d-a2e9-48c3aec2873f")
                .datatype(coded)
                .conceptClass(question)
                .name("5cd5e635-f157-421d-8422-56c6643e0ef5","TB Case Confirmation",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .description("89f3c2b8-0ac0-40b6-bbc5-d150056fdc09","Any person meeting the clinical or the bacteriological criteria for case confirmation",Locale.ENGLISH)
                .answers(clinicallyConfirmed,bacteriologicallyConfirmed)
                .build());

        Concept rhRegimen = install(new ConceptBuilder("89a51505-984f-41e3-af75-bfd80295a4ae")
                .datatype(numeric)
                .conceptClass(misc)
                .name("efd9cc40-3e09-41ee-b20e-d5bdd9c40e20","RH Regimen Tablets",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .description("1b5eb949-24cc-4497-bdf4-838b0e70503b","Number of RH tablets prescribed for TB",Locale.ENGLISH)
                .build());

        Concept rhMeningitis = install(new ConceptBuilder("b4600273-eda4-46be-9385-d314a676b66f")
                .datatype(numeric)
                .conceptClass(misc)
                .name("d2872be3-bd56-4574-8f80-b13169da2b7c","RH Meningitis Tablets",Locale.ENGLISH,ConceptNameType.FULLY_SPECIFIED)
                .description("d1f05343-b47f-4e73-b100-00489959a29e","Number of RH tablets prescribed for TB Meningitis",Locale.ENGLISH)
                .build());
    }
}
