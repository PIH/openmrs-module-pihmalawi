package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
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
        return 12;
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


        Concept day_1=install(new ConceptBuilder("F4BB0B8A-84BF-49AE-A16F-7326A3ED069C")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("A51E5D5F-30CF-4DE9-900B-0CECD8DB6152","Day 1 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("31BD48BB-BB0C-4AD9-BAF6-FFCD89237F21","Day 1 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("54A0E935-2706-40F6-99AE-CB28F1A56BCB").type(sameAs).ensureTerm(pihMalawi, "DAY_1 ADHERENCE ").build())
                .build());
        Concept day_2=install(new ConceptBuilder("CC2B65B5-6C3E-4F1E-AC9E-384644EC0F85")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("AEBEC044-DA9F-48E1-93B2-ECB1F90A91DA","Day 2 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("905B8998-AAA5-49FE-8338-C9666F676458","Day 2 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("3105C469-A55B-4082-A217-F8E4EF00F1FC").type(sameAs).ensureTerm(pihMalawi, "DAY_2 ADHERENCE ").build())
                .build());
        Concept day_3=install(new ConceptBuilder("8F66269E-F8BD-4719-8BD1-E14E5D7DE57C")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0E8B5A7E-E0AB-415D-8736-B58CBC3EAC6B","Day 3 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("DCCCB60E-E0D2-4171-9DB1-C1EDB71AFA7C","Day 3 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("C32F524E-0234-4F9E-8D48-A843230A21C2").type(sameAs).ensureTerm(pihMalawi, "DAY_3 ADHERENCE ").build())
                .build());
        Concept day_4=install(new ConceptBuilder("5217FCE4-C5D3-47AD-B7CC-16D9CE1F59BF")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1121571E-3B97-45AA-B79B-3FF3908B3E23","Day 4 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("8E5405DA-47D4-4A67-B9DC-246451A11267","Day 4 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("930ABF5F-2F62-4780-870F-84B7EB1983E0").type(sameAs).ensureTerm(pihMalawi, "DAY_4 ADHERENCE ").build())
                .build());
        Concept day_5=install(new ConceptBuilder("7380E2EA-02B5-4238-8A55-A88F98237113")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("146D2D16-7380-4475-9A00-E30991EB0E92","Day 5 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("DF7B4258-1072-4B5C-93E2-DC0246CF3076","Day 5 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("EC3E1C0F-9A7A-43D2-A3AF-53BD340E42CF").type(sameAs).ensureTerm(pihMalawi, "DAY_5 ADHERENCE ").build())
                .build());
        Concept day_6=install(new ConceptBuilder("3CCB10CE-6740-4B43-9582-01D69EE13D32")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("B4EFB0E6-EFA8-43EA-8E30-AFE5D250629E","Day 6 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("C189E89C-73DE-41A7-93B4-3BFAF35B5B28","Day 6 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("0346B781-7787-48F8-82FF-23BBBFFB7B70").type(sameAs).ensureTerm(pihMalawi, "DAY_6 ADHERENCE ").build())
                .build());
        Concept day_7=install(new ConceptBuilder("63AC328C-5348-4109-A478-2BE9D5E883BE")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2ED9A1B7-038A-4589-BAAB-99977C5B255A","Day 7 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("7B2EC1AD-280C-4D40-8E83-CAA413C990F0","Day 7 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("B8F657A9-682A-4D31-AFC2-5497F449FE49").type(sameAs).ensureTerm(pihMalawi, "DAY_7 ADHERENCE ").build())
                .build());
        Concept day_8=install(new ConceptBuilder("64CBB6C7-2D56-4B76-A294-2F6229ACDC78")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("A47BB4CE-0A08-48C9-90AD-AE207128E7A0","Day 8 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("0731F939-C280-4CE6-AD91-7552A61B5FAA","Day 8 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("49418A16-3B90-4BCE-858D-2103A63DF153").type(sameAs).ensureTerm(pihMalawi, "DAY_8 ADHERENCE ").build())
                .build());
        Concept day_9=install(new ConceptBuilder("AEEA5AB9-D0D3-4941-B47C-D7F748B49C25")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("9499D073-5338-40D0-B331-8B73580790A1","Day 9 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("28FB3272-EED7-4D4A-B583-9E1894014DD0","Day 9 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("D075D57B-A5BD-422E-B4A6-2069F86DBB04").type(sameAs).ensureTerm(pihMalawi, "DAY_9 ADHERENCE ").build())
                .build());
        Concept day_10=install(new ConceptBuilder("50CCEC10-A0D0-4D17-9467-8E271CB48EC4")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("5EA41189-BDDC-43E2-8810-F8D938B2C3A3","Day 10 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("E031E840-785C-4219-A0CA-25D712FCC317","Day 10 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("010BDECB-9726-417C-8C2B-A8D9036B72A3").type(sameAs).ensureTerm(pihMalawi, "DAY_10 ADHERENCE ").build())
                .build());
        Concept day_11=install(new ConceptBuilder("EAEA481B-DDD2-4254-824A-21942BBCD730")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("670BE909-7F07-4EBE-BDFB-EA4BA1D243E4","Day 11 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("3E3F0B0A-7DC8-405B-BC80-D20F2B54F439","Day 11 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("2C31A9D9-C9C7-43D3-B93F-77C9E110C3B1").type(sameAs).ensureTerm(pihMalawi, "DAY_11 ADHERENCE ").build())
                .build());
        Concept day_12=install(new ConceptBuilder("AA3D8C4E-ADDF-43F4-A340-D3ED7CABF161")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("86D2B128-58F2-4A72-9E38-16DE08B10BC5","Day 12 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("34104A65-34F5-4C85-A14E-451ADD1D46C4","Day 12 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("8611A912-157B-4CD0-9F39-922A6C510AAC").type(sameAs).ensureTerm(pihMalawi, "DAY_12 ADHERENCE ").build())
                .build());
        Concept day_13=install(new ConceptBuilder("A4553208-905B-4D71-83FD-2BA45BBC0EB6")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1D14B41E-9844-4B00-9D56-139B05B55A0D","Day 13 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B60E6264-180F-4913-B241-256D587ACED3","Day 13 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("73D8FC21-5338-4BAE-9B65-B114DBE74104").type(sameAs).ensureTerm(pihMalawi, "DAY_13 ADHERENCE ").build())
                .build());
        Concept day_14=install(new ConceptBuilder("AEF5CE77-4FBC-411F-AD8D-8093CB19720C")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("7D9454C2-D8A8-41AD-B985-7230868E7F78","Day 14 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B6C0D307-1D43-4257-8F3B-F1B8D7A3F5F7","Day 14 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("383BAD03-DD9F-4B00-914A-102861B28F4B").type(sameAs).ensureTerm(pihMalawi, "DAY_14 ADHERENCE ").build())
                .build());
        Concept day_15=install(new ConceptBuilder("96F3D9BF-FD23-483F-9201-B517089D690A")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1587851F-AD36-4236-833E-8CC5EBA375B6","Day 15 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("94F28F6C-B2A6-4C62-BA10-7BC285839179","Day 15 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("34368BB8-8054-470A-97F9-104728A0D171").type(sameAs).ensureTerm(pihMalawi, "DAY_15 ADHERENCE ").build())
                .build());
        Concept day_16=install(new ConceptBuilder("40F7AC4C-84D0-4D4B-9A79-0915D92BCE8D")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("AA6AF5E1-2053-48F2-9D10-341B75CC2473","Day 16 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B75044AB-74FA-418B-9F40-C40F12C31F1E","Day 16 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("C9CDF5B5-A3C4-454B-AE86-879F887E7B12").type(sameAs).ensureTerm(pihMalawi, "DAY_16 ADHERENCE ").build())
                .build());
        Concept day_17=install(new ConceptBuilder("3019E3D3-C91D-4D03-B82D-A0A917AE1DC3")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2F082366-639D-475A-BB02-0D7F956CB3E2","Day 17 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("9E237AA1-7FD7-4AA4-B46A-D3ECC8BBB7BE","Day 17 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("C0AE6AC3-E1F9-4DA8-A6E3-9C5347A032B7").type(sameAs).ensureTerm(pihMalawi, "DAY_17 ADHERENCE ").build())
                .build());
        Concept day_18=install(new ConceptBuilder("B0043305-848E-45D6-97B7-4B6EA31BFC00")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("4978A988-B7A3-415B-AA47-12A7B07B1177","Day 18 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("488538C3-33A3-4F8F-9114-4A88FF90B01D","Day 18 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("5366AB96-F011-445E-A839-C7068F501D94").type(sameAs).ensureTerm(pihMalawi, "DAY_18 ADHERENCE ").build())
                .build());
        Concept day_19=install(new ConceptBuilder("2292AE07-AC1B-4A5F-AE5B-A8555B58C4CC")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("B8B6DAE1-6A4D-4F54-ADD8-7CF838FA6339","Day 19 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("0E04207E-4248-4FC7-BF7D-6A4A298AD185","Day 19 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("2E82BF57-8CA4-44C8-ADBB-6D04B0BE990B").type(sameAs).ensureTerm(pihMalawi, "DAY_19 ADHERENCE ").build())
                .build());
        Concept day_20=install(new ConceptBuilder("1E297083-8107-448C-AF00-ED9558B20829")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("BF1C730D-2834-438D-8378-579054C86C50","Day 20 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("F0CEF9FA-61DE-4AC1-89DE-C215D1C3526B","Day 20 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("0A83C3FE-814D-4E85-B30E-92FDADD532B1").type(sameAs).ensureTerm(pihMalawi, "DAY_20 ADHERENCE ").build())
                .build());
        Concept day_21=install(new ConceptBuilder("7BC41622-7F16-419C-AF97-0356862DF446")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("91E87BE0-6C39-435F-A8D3-0EEA967182F4","Day 21 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("6DFFA895-E153-44A0-BA37-B8C20436785E","Day 21 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("DE3396B9-E71C-4D06-923A-922840F8D3E8").type(sameAs).ensureTerm(pihMalawi, "DAY_21 ADHERENCE ").build())
                .build());
        Concept day_22=install(new ConceptBuilder("0BFCDE58-96BE-41E6-A160-A0657ED9BCC8")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8339F1AF-936D-4980-8E64-083404223AEC","Day 22 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("0908DBF5-EE00-4D44-8ACB-E478D57FC2EE","Day 22 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("60C795CA-D837-4D65-B21A-00FDF011BED4").type(sameAs).ensureTerm(pihMalawi, "DAY_22 ADHERENCE ").build())
                .build());
        Concept day_23=install(new ConceptBuilder("70C6E727-15B8-429E-AB22-D030F6DCDEA5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("88B40EF2-94F4-46D6-BDD8-E3ADD6E06F5C","Day 23 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("0D80A2BC-EFD2-479F-972F-C62CCE50FDBC","Day 23 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("ECBCA7AD-7E9E-4F55-B4C3-8EC09C0A9600").type(sameAs).ensureTerm(pihMalawi, "DAY_23 ADHERENCE ").build())
                .build());
        Concept day_24=install(new ConceptBuilder("1E6B1F98-E52D-43E4-9681-69690618A65D")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("AB714C30-4404-4C55-87B0-CB6F4D5D2DE4","Day 24 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("4413A316-AE9A-4AA5-8B49-9B681E7B7DC2","Day 24 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("38413A44-2C83-49C5-A86B-3AE7B8C10F98").type(sameAs).ensureTerm(pihMalawi, "DAY_24 ADHERENCE ").build())
                .build());
        Concept day_25=install(new ConceptBuilder("1A39667A-A2FA-42D9-8EC8-C07D04754ABE")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("4A5DC700-C11A-4F8D-83AA-9B1A8715A46B","Day 25 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("61077589-6F84-46C3-89C5-034444DA5639","Day 25 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("66BA4608-4602-4FF2-8943-C0E9414D680C").type(sameAs).ensureTerm(pihMalawi, "DAY_25 ADHERENCE ").build())
                .build());
        Concept day_26=install(new ConceptBuilder("053B0025-5D5D-4C66-941A-06A40B1061DB")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("E596F20E-F231-4C0E-A31C-B6756581B500","Day 26 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("60C7847A-1A75-4E3E-B9CF-00099A9C6ADD","Day 26 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("E32182A9-6B5C-4222-AF64-AC72FB9EB26D").type(sameAs).ensureTerm(pihMalawi, "DAY_26 ADHERENCE ").build())
                .build());
        Concept day_27=install(new ConceptBuilder("0C53E0FD-184F-405B-B36A-0B06A85CBA44")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3B81B0FB-8952-42EB-90C5-19E96D46FADC","Day 27 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B15890A1-3B28-4A20-8398-083B8D130FA3","Day 27 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("DADF8EE9-2E31-40C7-A357-2F1112D201CB").type(sameAs).ensureTerm(pihMalawi, "DAY_27 ADHERENCE ").build())
                .build());
        Concept day_28=install(new ConceptBuilder("27CCD84F-87B0-49BE-8DBF-6CFF4DDAD450")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("FC2E5032-0897-4502-BD25-25C6331B5FF0","Day 28 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("29616DD2-4E57-4057-A1C9-8F1A14D8DBB9","Day 28 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("8CF62224-FB81-41FF-9ECC-B620D026B0E0").type(sameAs).ensureTerm(pihMalawi, "DAY_28 ADHERENCE ").build())
                .build());
        Concept day_29=install(new ConceptBuilder("C1D39592-C770-4638-88EC-1EFC29353C9C")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("E985D22A-915C-4003-8604-6C2309813F79","Day 29 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("1F3955D5-02D1-4D7B-B9B9-1EC13D424CEA","Day 29 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("CD51E11F-D6A7-4AB9-8447-1E0105C793F4").type(sameAs).ensureTerm(pihMalawi, "DAY_29 ADHERENCE ").build())
                .build());
        Concept day_30=install(new ConceptBuilder("68512CE9-39CA-4D16-A770-83F10F494E0C")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("1507018F-BC04-4063-B9C7-3DC89AC45376","Day 30 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("CEA646FB-1164-4EC4-B603-5ECD91AD9F0C","Day 30 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("EA336BE8-8FB2-4322-B402-030750B7268D").type(sameAs).ensureTerm(pihMalawi, "DAY_30 ADHERENCE ").build())
                .build());
        Concept day_31=install(new ConceptBuilder("E15C70B5-98DF-4B14-9F16-D108AFDC1662")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("BC6AF01C-C1F5-48F7-9E6B-2F38BD3E997A","Day 31 medication adherence",Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("27206F75-4832-4A4B-8519-85E2599B42D1","Day 31 of the medication adherence month",Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("1C78C6D1-96CE-4198-B237-7A1B257DA74E").type(sameAs).ensureTerm(pihMalawi, "DAY_31 ADHERENCE ").build())
                .build());
        Concept adherence_day = install(new ConceptBuilder("BE14153B-E5B1-481D-A94B-EF93AEF4C99B")
                .datatype(coded)
                .conceptClass(question)
                .name("EFC41515-C436-452C-8FD2-9054A2DD84D3", "Medication adherence day", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B31569BC-B0EF-46DE-9E54-1764CFA9E276", "Medication adherence day", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("7842A043-3332-4166-8281-132F077D4AEE").type(sameAs).ensureTerm(pihMalawi, "MEDICATION ADHERENCE DAY").build())
                .answers(day_1, day_2, day_3, day_4, day_5, day_6, day_7, day_8, day_9, day_10, day_11, day_12, day_13, day_14, day_15, day_16, day_17, day_18, day_19, day_20, day_21, day_22, day_23, day_24, day_25, day_26, day_27, day_28, day_29, day_30, day_31)
                .build());

        Concept adherence_month = install(new ConceptNumericBuilder("9C22DD14-B514-4C84-8D24-2BE9354C5042")
                .datatype(numeric)
                .conceptClass(question)
                .name("71D18A09-FC06-4D80-BE5C-3F6B065C83BC", "Medication adherence month", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("9EFDDAEF-A84D-4D6E-8818-38E2435C0DA4", "Medication adherence month", Locale.ENGLISH)
                .mapping(new ConceptMapBuilder("05694BF9-2ACF-4C75-B45D-13959D9592F8").type(sameAs).ensureTerm(pihMalawi, "MEDICATION ADHERENCE MONTH").build())
                .build());

        Concept medication_adherence_construct = install(new ConceptBuilder("9AB96FE6-86D5-4E12-95B0-5BFCC87A0DC3")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("F3EC8B84-746E-4824-82C6-C0A6BFDD2A7B", "Medication adherence construct", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("8C4BC9BC-CE6A-4999-B13E-FF1565BCD89D", "Records patient's self-reported medication adherence", Locale.ENGLISH)
                .setMembers(adherence_month, adherence_day)
                .build());
    }
}
