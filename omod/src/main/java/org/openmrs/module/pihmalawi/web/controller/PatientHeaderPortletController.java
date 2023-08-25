package org.openmrs.module.pihmalawi.web.controller;

public class PatientHeaderPortletController extends PihMalawiPortletController{
    @Override
    protected String getPortletUrl() {
        return "/module/pihmalawi/portlets/patientHeader";
    }
}
