package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

public class UserRoleBundle extends AbstractMetadataBundle {

    public static final class UserRoles {

        public static final String VIEW_CLINICAL_DATA_PRIVILEGE = "cbbfac84-e94f-42a9-9925-6e99f7a8a1b9";
        public static final String VIEW_CLINICAL_DATA = "9c419a7e-f11f-4d66-8944-aab24b538f9c";
    }

    @Override
    public void install() throws Exception {

        install(privilege("View clinical data", "View patient's clinical data", UserRoles.VIEW_CLINICAL_DATA_PRIVILEGE));
        install(role("Clinical data","Access to clinical data", null, idSet("View clinical data"), UserRoles.VIEW_CLINICAL_DATA));
    }
}
