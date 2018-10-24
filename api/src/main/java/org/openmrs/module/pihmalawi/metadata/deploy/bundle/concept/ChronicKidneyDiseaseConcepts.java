package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;

import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNumericBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class ChronicKidneyDiseaseConcepts extends VersionedPihConceptBundle {

    @Override
    public int getVersion() {
        return 3;
    }

    @Override
     protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);
        Concept hypertensionDx = MetadataUtils.existing(Concept.class, "654abfc8-977f-11e1-8993-905e29aff6c1");
        Concept diabetesDx = MetadataUtils.existing(Concept.class, "6567426a-977f-11e1-8993-905e29aff6c1");
        Concept ckdEtiHIV = MetadataUtils.existing(Concept.class, "654a9598-977f-11e1-8993-905e29aff6c1");
        Concept ckdEtiNephro = MetadataUtils.existing(Concept.class, "65696f40-977f-11e1-8993-905e29aff6c1");
        Concept ckdEtiOther = MetadataUtils.existing(Concept.class, "657140f8-977f-11e1-8993-905e29aff6c1");
        Concept ckdEtiUknown = MetadataUtils.existing(Concept.class, "6577bb7c-977f-11e1-8993-905e29aff6c1");
		
		Concept drugsEtiologyGeneral = install(new ConceptBuilder("0754f2f8-d797-11e8-9f8b-f2801f1b9fd1")
                .datatype(text)
                .conceptClass(question)
                .name("0754f7b2-d797-11e8-9f8b-f2801f1b9fd1", "drugs", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build()); 

        Concept lowSodium = install(new ConceptBuilder("5e7a1d78-aecc-4b47-bc81-d144dc91dda1")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("ad006112-cf81-41b8-a795-7a40a7494241", "Low sodium", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept lowPotassium = install(new ConceptBuilder("0ddfbe32-04c3-4b03-a057-ca3b5524cbb5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("202c97a3-d0d7-4328-97cb-f2970708db80", "Low potassium", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept lowProtein = install(new ConceptBuilder("f8770f87-1834-4331-a8f5-88df5e6f9573")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("6ad1c75e-7825-4db0-a383-5ba0c4f58ef6", "Low protein", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept highCalories = install(new ConceptBuilder("3cfde72e-8626-4ee2-97e8-6f40172953f5")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("55dfdb36-aa4c-4c26-ad21-6cad6e7d1bf8", "High calories", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept stageOneTwo = install(new ConceptBuilder("d45b3164-bdb1-4411-9f22-d998c9af116c")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("8b5f5c6e-6093-44af-9994-d47fbd3d51b5", "CKD stage 1-2", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept stageThree= install(new ConceptBuilder("358c0e8f-708a-48f3-ba2c-e7fe16a24cfd")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("cea56cac-53b5-4116-a295-d73a400ce03b", "CKD stage 3", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        Concept stageFourFive = install(new ConceptBuilder("d0a884e6-8bb5-451c-a321-f63242d216b7")
                .datatype(notApplicable)
                .conceptClass(misc)
                .name("a5f5e527-0606-46c5-8eab-9035a6903641", "CKD stage 4-5", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("ccc842f5-c7ee-4b82-865c-be19053c4db3")
                .datatype(coded)
                .conceptClass(misc)
                .name("2d0818ee-6cf5-408e-899f-0d3dc8315960", "Diet recommendations", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(lowSodium, lowPotassium, lowProtein, highCalories)
                .build());

        install(new ConceptBuilder("fd79ad7e-53ea-461a-b075-27beee44a6cc")
                .datatype(coded)
                .conceptClass(question)
                .name("1d82148c-7839-4a23-90b0-05e9ef68633b", "CKD stage", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(stageOneTwo, stageThree, stageFourFive)
                .build());

        install(new ConceptBuilder("71195f66-a9c7-4c33-b6be-90ef9ff9535f")
                .datatype(coded)
                .conceptClass(finding)
                .name("9ed0d53e-2d8f-438c-8e96-66652be7a517", "Patient has fatigue", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("104cb8fb-8ff9-4ea4-b547-10fed3afdea2")
                .datatype(coded)
                .conceptClass(finding)
                .name("be3daa41-a095-4b20-9ef6-b1cba4f6bb8c", "Patient has nausea", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        
        install(new ConceptBuilder("ac7a7aae-6f3a-4a04-a147-97019597886f")
                .datatype(coded)
                .conceptClass(finding)
                .name("9f6b53fa-542c-4deb-a20f-74fd8c5ba684", "Patient has anorexia", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("ca2cd277-cdfc-42b0-9958-3f1281480ad8")
                .datatype(coded)
                .conceptClass(finding)
                .name("336bf3c1-c21a-4a7d-9c6b-481b720cfddf", "Patient has pruritus", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());

        install(new ConceptBuilder("2b0f959a-8877-434b-8cc4-430682e7c9f4")
                .datatype(coded)
                .conceptClass(finding)
                .name("2a4744f8-ab8e-11e8-98d0-529269fb1459", " Patient has ascites", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());    

        install(new ConceptBuilder("c8a4d207-0a73-4471-955c-c1f47cbbe2b5")
                .datatype(text)
                .conceptClass(drug)
                .name("2a474390-ab8e-11e8-98d0-529269fb1459", "Other medication", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("8dcb0708-feb4-44e8-bdb8-ed90be94c4d0")
                .datatype(text)
                .conceptClass(question)
                .name("1b3527ac-375a-4a91-b07f-a3793a19c83d", "History of dialysis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());   

        install(new ConceptBuilder("b822cbed-d108-4387-8876-b430179a5945")
                .datatype(text)
                .conceptClass(diagnosis)
                .name("ad1f4c4f-7fff-4fd4-a350-e5285f8acd17", "Ultrasound imaging result", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build()); 

        install(new ConceptBuilder("3b55eeb2-d56a-4aab-b5bf-f00668feee75")
                .datatype(date)
                .conceptClass(question)
                .name("09b3e3cc-2baf-4b8b-88bf-25c3d7938615", "Date of dialysis", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());   

        install(new ConceptBuilder("9868d4ee-3325-49a7-9eee-ebb8416e2728")
                .datatype(coded)
                .conceptClass(question)
                .name("773e8c89-998e-4dcb-b432-eb2fde13abeb", "Presumed chronic kidney disease etiology", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(hypertensionDx, diabetesDx, ckdEtiHIV, ckdEtiNephro, ckdEtiOther, ckdEtiUknown, drugsEtiologyGeneral)
                .build());

        install(new ConceptBuilder("89a3d604-43f9-4d4e-95ad-8b8577f80108")
                .datatype(coded)
                .conceptClass(question)
                .name("50c96a22-1c92-4312-997a-85384a28ce31", "Nonsteroidal anti-inflammatory drug use", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes, no)
                .build());
				
		install(new ConceptNumericBuilder("a0d7e2b0-d69c-11e8-9f8b-f2801f1b9fd1")
                .datatype(numeric)
                .conceptClass(test)
                .name("a9c2039c-d69c-11e8-9f8b-f2801f1b9fd1", "Glomerular filtration rate", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .units("mL/min")
                .build());
    }
}
