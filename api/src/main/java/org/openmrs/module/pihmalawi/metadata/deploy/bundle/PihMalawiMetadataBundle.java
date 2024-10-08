package org.openmrs.module.pihmalawi.metadata.deploy.bundle;


import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.NutritionProgramMetadata;
import org.openmrs.module.pihmalawi.metadata.PalliativeCareMetadata;
import org.openmrs.module.pihmalawi.metadata.MentalHealthMetadata;
import org.openmrs.module.pihmalawi.metadata.PdcMetadata;
import org.openmrs.module.pihmalawi.metadata.TeenClubProgramMetadata;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ChronicHeartFailureConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ChronicKidneyDiseaseConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ChwManagementConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.IC3ScreeningConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.MasterCardConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.NutritionConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.PalliativeCareConcepts;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.TraceConcepts;
import org.springframework.stereotype.Component;

@Component
@Requires( {
        PalliativeCareConcepts.class,
        MasterCardConcepts.class,
        ChwManagementConcepts.class,
        ChronicHeartFailureConcepts.class,
        ChronicKidneyDiseaseConcepts.class,
        IC3ScreeningConcepts.class,
        TraceConcepts.class,
        NutritionConcepts.class} )
public class PihMalawiMetadataBundle extends AbstractMetadataBundle{

    @Override
    public void install() throws Exception {
        install(PalliativeCareMetadata.PALLIATIVE_CARE_PROGRAM);
        //install(MentalHealthMetadata.MH_CARE_PROGRAM);
        install(PdcMetadata.PDC_PROGRAM);
        install(NutritionProgramMetadata.NUTRITION_PROGRAM);
        install(TeenClubProgramMetadata.TEEN_CLUB_PROGRAM);
    }
}
