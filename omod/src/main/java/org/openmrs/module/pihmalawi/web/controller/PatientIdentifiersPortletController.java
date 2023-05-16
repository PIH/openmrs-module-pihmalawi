package org.openmrs.module.pihmalawi.web.controller;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Order(40)
public class PatientIdentifiersPortletController {

    @RequestMapping(value = "**/patientIdentifiers.portlet")
    public String viewPortlet() {
        return "/module/pihmalawi/portlets/patientIdentifiers";
    }
}
