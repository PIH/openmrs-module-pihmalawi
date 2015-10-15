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
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This class simply renders an htmlform without anything additional
 */
public class HtmlFormPageController {

    public void controller(@RequestParam(value="patient") Patient patient,
                           @RequestParam(value="formName") String formName,
                           @RequestParam(value="editMode") Boolean editMode,
                           @RequestParam(value="encounter", required=false) Encounter encounter,
                           @RequestParam(value="returnUrl", required=false) String returnUrl,
                           UiUtils ui, PageModel model) {

        model.addAttribute("fragmentProvider", "htmlformentryui");
        model.addAttribute("fragmentName", editMode ? "htmlform/enterHtmlForm" : "htmlform/viewEncounterWithHtmlForm");
        model.addAttribute("formName", formName);
        model.addAttribute("patient", patient);
        model.addAttribute("encounter", encounter);
        model.addAttribute("returnUrl", returnUrl);
    }
}