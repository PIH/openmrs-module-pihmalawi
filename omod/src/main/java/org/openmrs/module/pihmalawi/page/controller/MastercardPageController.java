/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.page.controller;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class MastercardPageController {

    public static final String EDIT_HEADER_MODE = "editHeader";
	
	public void controller(@RequestParam(value="patientId", required=false) Patient patient,
                           @RequestParam(value="encounterId", required=false) Encounter encounter,
                           @RequestParam(value="mode", required=false) String mode,
                           UiUtils ui, PageModel model,
	                       @InjectBeans PatientDomainWrapper patientDomainWrapper) {

        if (patient == null && encounter != null) {
            patient = encounter.getPatient();
        }
		patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("encounter", encounter);
        model.addAttribute("mode", mode);

        model.addAttribute("headerForm", "art_emastercard");

        model.addAttribute("headerFragmentProvider", "htmlformentryui");

        String headerFragmentName = "htmlform/viewEncounterWithHtmlForm";

        if (EDIT_HEADER_MODE.equals(mode)) {
            headerFragmentName = "htmlform/enterHtmlForm";
        }

        model.addAttribute("headerFragmentName", headerFragmentName);

        model.addAttribute("returnUrl", ui.pageLink("pihmalawi", "mastercard"));
	}
}
