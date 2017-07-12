package org.openmrs.module.pihmalawi.web.extension;


import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.web.extension.LinkExt;

public class ChwHeaderLinkExt extends LinkExt {
    @Override
    public String getLabel() {
        return Context.getMessageSourceService().getMessage("pihmalawi.chw.management.app");
    }

    @Override
    public String getUrl() {
        return "coreapps/providermanagement/providerList.page";
    }

    @Override
    public String getRequiredPrivilege() {
        return PihMalawiConstants.PRIV_CHW_MANAGEMENT_APP;
    }
}
