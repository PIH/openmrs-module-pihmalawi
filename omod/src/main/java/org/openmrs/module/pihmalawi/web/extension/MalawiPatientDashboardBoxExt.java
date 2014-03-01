package org.openmrs.module.pihmalawi.web.extension;

import org.openmrs.module.web.extension.BoxExt;

public class MalawiPatientDashboardBoxExt extends BoxExt {

    @Override
    public String getRequiredPrivilege() {
        return "View Patients";
    }

    @Override
    public String getPortletUrl() {
        return "malawiPatientDashboard";
    }

    @Override
    public String getTitle() {
        return "pihmalawi.emastercards.sectionTitle";
    }

    @Override
    public String getContent() {
        return "";
    }
}
