package org.openmrs.module.pihmalawi.metadata.deploy.bundle;


import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihmalawi.metadata.MentalHealthMetadata;
import org.openmrs.module.pihmalawi.metadata.NutritionProgramMetadata;
import org.openmrs.module.pihmalawi.metadata.PalliativeCareMetadata;
import org.openmrs.module.pihmalawi.metadata.PdcMetadata;
import org.openmrs.module.pihmalawi.metadata.TbProgramMetadata;
import org.openmrs.module.pihmalawi.metadata.TeenClubProgramMetadata;
import org.springframework.stereotype.Component;

@Component
public class PihMalawiMetadataBundle extends AbstractMetadataBundle{

    @Override
    public void install() throws Exception {
        install(PalliativeCareMetadata.PALLIATIVE_CARE_PROGRAM);
        install(MentalHealthMetadata.MH_CARE_PROGRAM);
        install(PdcMetadata.PDC_PROGRAM);
        install(NutritionProgramMetadata.NUTRITION_PROGRAM);
        install(TeenClubProgramMetadata.TEEN_CLUB_PROGRAM);
        install(TbProgramMetadata.OLD_TB_PROGRAM);
        install(TbProgramMetadata.TB_PROGRAM);
    }
}
