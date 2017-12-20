package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class PalliativeCareConcepts extends VersionedPihConceptBundle{

    public static final String PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID = "14ce86d7-025e-4dcf-9437-67d26185c6ab";
    public static final String PALLIATIVE_CARE_PROGRAM_STATUS_CONCEPT_UUID = "e7a7c2ca-7433-4851-8687-67e8541ca40b";
    public static final String PALLIATIVE_CARE_PROGRAM_OUTCOME_CONCEPT_UUID  = "73eb05c2-e4be-4d82-bcad-ffec1be67d01";
    public static final String ON_TREATMENT_CONCEPT_UUID = "65664784-977f-11e1-8993-905e29aff6c1";
    public static final String TREATMENT_STOPPED_CONCEPT_UUID = "655a6acc-977f-11e1-8993-905e29aff6c1";
    public static final String TRANSFERRED_OUT_CONCEPT_UUID = "655b604e-977f-11e1-8993-905e29aff6c1";
    public static final String DEFAULTED_CONCEPT_UUID = "655b5f4a-977f-11e1-8993-905e29aff6c1";
    public static final String DIED_STATE_CONCEPT_UUID = "655b5e46-977f-11e1-8993-905e29aff6c1";

    public static final String LOCATION_OF_VISIT_CONCEPT  = "1F5A206E-9E08-4CDA-BC83-3B1DB492F3C5";
    public static final String FUNCTIONAL_STATUS_CONCEPT  = "61EB2A1C-3DB0-4453-8E89-6FBD09563A96";
    public static final String PATIENT_ON_LAXATIVE_CONCEPT  = "2D452120-1DC7-427F-89FC-7FEE85A1E709";
    public static final String PC_REFERRAL_CONCEPT  = "44EFAABD-5A8A-4D17-B3E5-E6A4834A7659";

    public static final String PACLITAXEL_CONCEPT  = "6566f788-977f-11e1-8993-905e29aff6c1";
    public static final String CHEMO_AND_BLEOMYCIN_CONCEPT  = "AD27EE9F-6AD7-426C-A970-05DF10BAB96D";

    public static final String COUNSELING_CONCEPT  = "480E00E5-D428-45F1-B819-413FE5FAEAC0";


    @Override
    public int getVersion() {
        return 17;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

        install(new ConceptBuilder(PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(program)
                .name("5ecb5fb3-8b87-4c1e-99ac-e2e0be21d4dd", "Palliative Care Program", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("66fb0c94-0498-11e6-9622-0010f345ca38")
                        .type(sameAs).ensureTerm(pih, "8415").build())
                .build());

        install(new ConceptBuilder(PALLIATIVE_CARE_PROGRAM_STATUS_CONCEPT_UUID)
                .datatype(notApplicable)
                .conceptClass(workflow)
                .name("33f74a77-13cb-4fb1-88ee-5d56aae2ae0c", "Palliative Care Treatment Status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("66fb952f-0498-11e6-9622-0010f345ca38")
                        .type(sameAs).ensureTerm(pih, "8416").build())
                .build());

        install(new ConceptBuilder(PALLIATIVE_CARE_PROGRAM_OUTCOME_CONCEPT_UUID)
                .datatype(coded)
                .conceptClass(workflow)
                .name("e72c1cfc-3e7a-48e3-9b3b-19b539cc7070", "Generic outcome", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .mapping(new ConceptMapBuilder("da99e814-6b44-4bec-a89d-20dddf61e115")
                        .type(sameAs).ensureTerm(pih, "Generic outcome").build())
                .mapping(new ConceptMapBuilder("41a01c04-cc22-4c69-bfea-8bfe1ed002b6")
                        .type(sameAs).ensureTerm(pih, "11505").build())
                .build());


        install(new ConceptBuilder(COUNSELING_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("B9B25269-FFA2-4547-B51F-03FA40B5C799", "Counseling", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());


        Concept home_visit_location = install(new ConceptBuilder("29EB5E06-9815-42D8-A3B1-37A5FE88B4F8")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3EDEEA24-7637-45BB-9D70-5C3FEC588B13", "Home", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("AEAE3734-156D-44BC-B2F2-3D795E37CA21", "Home Visit Location", Locale.ENGLISH)
                .build());
        Concept ic3_visit_location = install(new ConceptBuilder("3064BCF8-56F7-43C9-A8CF-D90D42EEF739")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("FE356DE3-765B-43D5-AF38-98459C305C1D", "IC3", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("16256F06-02B5-4892-8BD6-8D3B24A20E06", "IC3 Visit Location", Locale.ENGLISH)
                .build());
        Concept hospital_ward_location = install(new ConceptBuilder("20B8B0FB-F0D7-4692-96BF-5B516CA0D624")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("E50B802A-AD0D-4949-B930-A2030E9F1714", "Hospital Ward", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("D672F8E8-F68A-4469-AEF3-16FCF7B42021", "Hospital Ward Location", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder(LOCATION_OF_VISIT_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("C71A9356-BDDC-4726-8B8F-87A3071D9386", "Location of Visit", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("4E85365B-E89C-40E8-9738-CF48C9469481", "Location of visit", Locale.ENGLISH)
                .answers(
                        home_visit_location,ic3_visit_location,hospital_ward_location)
                .build());

        Concept pc_no_support = install(new ConceptBuilder("C745EF20-19A1-4E43-998D-E34658580828")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("BEF65FCB-BBD9-496A-86AD-42FB7F5D8EB3", "No support", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B429201D-4406-4109-BBA6-3831431A5F99", "Palliative Care no support", Locale.ENGLISH)
                .build());
        Concept pc_needs = install(new ConceptBuilder("1FD42AA9-AA92-48FB-AD9C-C985CA7FD8F1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("0565B1E9-3435-4F8B-A689-C0CBBF0173DA", "Needs", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("F8622A6C-6750-44EB-8CDA-70427FE79177", "Palliative Care needs", Locale.ENGLISH)
                .build());
        Concept pc_bed_ridden = install(new ConceptBuilder("4C6822BE-88EA-4E71-A7EC-4D3E7403B3D5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("3E2C5116-36DE-4F33-AA09-43C376412AF6", "Bed ridden", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("0DD73AE1-BB36-4B26-A287-F013A99937CB", "Palliative Care bed ridden", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder(FUNCTIONAL_STATUS_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("A5BD81DE-934F-4BD6-AD26-E30C155A5391", "Functional status", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("FCB7A29D-277F-4A67-AF7C-2C30AD9839B6", "Functional status", Locale.ENGLISH)
                .answers(
                        pc_no_support,pc_needs,pc_bed_ridden)
                .build());

        install(new ConceptBuilder(PATIENT_ON_LAXATIVE_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("630A1C0A-A72C-477F-835E-1F9E5E26DBC4", "Patient on laxative", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        Concept poser_referral = install(new ConceptBuilder("6F4191F7-AB10-41DE-8B23-DD8F6B66D6D1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6269DA8C-34BE-4C9D-9E7B-EDFBDFDEAFD8", "POSER Referral", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("521409B0-86CF-46A6-8672-ECE620C53448", "PC POSER Referral", Locale.ENGLISH)
                .build());
        Concept qech_referral = install(new ConceptBuilder("8EF58590-DD8F-4117-B73B-435DBEB0B58B")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6266BF43-E1CA-432A-B97F-62E37641106B", "QECH Referral", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B7B1B68C-1AB3-4E6E-A46F-33DF8AD0AA30", "PC QECH Referral", Locale.ENGLISH)
                .build());

        install(new ConceptBuilder(PC_REFERRAL_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("99819C4E-4043-4249-9EA0-3D4C76C25D33", "PC Referral", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("7745BD8C-BCE7-4B5F-ADED-56C4F229AB2E", "PC Referral", Locale.ENGLISH)
                .answers(
                        poser_referral,qech_referral)
                .build());

        install(new ConceptBuilder("16D8FC88-0180-4858-95D5-2479CF7BD6EE")
                .datatype(text)
                .conceptClass(misc)
                .name("12500A67-E7EF-43F0-872B-27C26846A486", "Management Plan Comments", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("A2B5824F-0B80-49BD-B465-933B7DF5E047", "Management Plan Comments", Locale.ENGLISH)
                .build());


        Concept vincristine_and_bleomycin = install(new ConceptBuilder("462D9714-CB2B-4362-9268-AE98898F4D65")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("2AAC2411-A5B2-46C7-916D-25AE35C236AF", "Vincristine and Bleomycin", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("AF31B049-A8D6-4C87-B739-3B7E52AE061B", "Vincristine 2mg and Bleomycin 15iu", Locale.ENGLISH)
                .build());


        install(new ConceptBuilder(CHEMO_AND_BLEOMYCIN_CONCEPT)
                .datatype(coded)
                .conceptClass(question)
                .name("2913C395-6063-4D7C-A49A-F09849051FB6", "Chemotherapy and Bleomycin", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("BA1CD3C3-6C4C-47C5-B5CC-F231721E59EE", "Chemotherapy and Bleomycin", Locale.ENGLISH)
                .answers(
                        vincristine_and_bleomycin)
                .build());


        Concept morphineImmediateRelease = install(new ConceptBuilder("BF09FD11-13AE-4398-90B0-33B66F4A1C0F")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("6AA5039E-A229-4251-924D-B1421498043F", "Morphine Immediate Release", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("C5912A17-8283-4A2D-91CF-0FD884E19339", "Morphine Immediate Release", Locale.ENGLISH)
                .build());

        Concept morphineModifiedRelease = install(new ConceptBuilder("23E28B0C-BEB7-46B1-8B54-D53549E15B61")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("E3409AB7-B11F-430C-A31D-32E5804BF8A2", "Morphine Modified Release", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("164999B4-DEDE-4CEA-8E1F-EE431E3D1F81", "Morphine Modified Release", Locale.ENGLISH)
                .build());

        Concept morphineLiquid = install(new ConceptBuilder("1FB97E61-626C-44A4-B55C-AE60BAAFAA49")
                .datatype(notApplicable)
                .conceptClass(drug)
                .name("D470873C-09E3-4029-80A4-692D60C30833", "Morphine Liquid", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("F9B1D5DB-CE27-4455-B091-8881903D80C6", "Morphine Liquid", Locale.ENGLISH)
                .build());
        Concept tramadol = MetadataUtils.existing(Concept.class, "657351a4-977f-11e1-8993-905e29aff6c1");
        Concept codeine = MetadataUtils.existing(Concept.class, "6574f360-977f-11e1-8993-905e29aff6c1");

        Concept pcMedication = install(new ConceptBuilder("38625279-00DD-40B0-B926-ADB5549C8F8A")
                .datatype(coded)
                .conceptClass(question)
                .name("3E9910A5-5DDB-4998-B128-C983D6D34D77", "Palliative care medication", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("4B8905FC-FA90-4FB5-9C62-9EC4785BBC87", "Palliative care medication", Locale.ENGLISH)
                .answers(
                        morphineImmediateRelease, morphineModifiedRelease, morphineLiquid, tramadol, codeine)
                .build());

        Concept doseInMg = MetadataUtils.existing(Concept.class, "6562d39c-977f-11e1-8993-905e29aff6c1");
        Concept frequency = MetadataUtils.existing(Concept.class, "65636514-977f-11e1-8993-905e29aff6c1");
        Concept generalDuration = install(new ConceptBuilder("0F3AA0EC-C3FB-47E7-B17D-FA8AF2B0B33C")
                .datatype(text)
                .conceptClass(question)
                .name("1F6B5670-C2CC-4928-9729-38A538D57E97", "Duration", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("27964240-9227-45CB-9039-5F6D8ABC2D05", "Prescribed duration of drug consumption", Locale.ENGLISH)
                .build());

        Concept pcMedicationConstruct = install(new ConceptBuilder("B0AAF81F-3E74-4DD6-BB72-D9F704236CFC")
                .datatype(notApplicable)
                .conceptClass(convSet)
                .name("80C4EFF9-0253-46EB-A3C2-19410A7595E6", "Palliative care medication construct", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("B3C85B3D-C5EA-49D6-A9AA-402D407CD432", "Palliative care medication construct", Locale.ENGLISH)
                .setMembers(
                        pcMedication, doseInMg, frequency, generalDuration)
                .build());

    }
}
