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
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.LocationUtility;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MastercardPageController {

	public void controller(@RequestParam(value="patientId", required=false) Patient patient,
                           @RequestParam(value="headerForm") String headerForm,
                           @RequestParam(value="visitForm") String visitForm,
                           UiUtils ui, PageModel model,
                           @SpringBean("htmlFormEntryService") HtmlFormEntryService htmlFormEntryService,
                           @SpringBean("formService") FormService formService,
                           @SpringBean("coreResourceFactory") ResourceFactory resourceFactory,
                           @SpringBean("reportingPatientDataService") PatientDataService patientDataService,
	                       @InjectBeans PatientDomainWrapper patientDomainWrapper) {

		patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("headerForm", headerForm);
        model.addAttribute("visitForm", visitForm);

        Location defaultLocation = LocationUtility.getUserDefaultLocation();
        model.addAttribute("defaultLocationId", defaultLocation == null ? null : defaultLocation.getLocationId());

        List<String> alerts = new ArrayList<String>();

        String headerFormResource = "pihmalawi:htmlforms/" + headerForm + ".xml";
        String visitFormResource = "pihmalawi:htmlforms/" + visitForm + ".xml";

        HtmlForm headerHtmlForm = getHtmlFormFromResource(headerFormResource, resourceFactory, formService, htmlFormEntryService);
        Encounter headerEncounter = null;
        List<Encounter> headerEncounters = getEncountersForForm(patient, headerHtmlForm);
        if (headerEncounters.size() > 0) {
            headerEncounter = headerEncounters.get(headerEncounters.size() - 1); // Most recent
            if (headerEncounters.size() > 1) {
                alerts.add("WARNING:  More than one " + headerHtmlForm.getName() + " encounters exist for this patient.  Displaying the most recent only.");
            }
        }
        model.addAttribute("headerEncounter", headerEncounter);

        HtmlForm visitHtmlForm = getHtmlFormFromResource(visitFormResource, resourceFactory, formService, htmlFormEntryService);
        List<Encounter> visitEncounters = getEncountersForForm(patient, visitHtmlForm);
        Collections.reverse(visitEncounters);
        model.addAttribute("visitEncounters", visitEncounters);

        model.addAttribute("alerts", alerts);

        model.addAttribute("returnUrl", ui.pageLink("pihmalawi", "mastercard", SimpleObject.create("patientId", patient.getId(), "headerForm", headerForm, "visitForm", visitForm)));
	}

    /**
     * @return an HtmlForm that is represented by the given resource in the web project
     */
    protected HtmlForm getHtmlFormFromResource(String resource, ResourceFactory rf, FormService fs, HtmlFormEntryService hfs) {
        try {
            HtmlForm form = HtmlFormUtil.getHtmlFormFromUiResource(rf, fs, hfs, resource);
            if (form == null) {
                throw new IllegalArgumentException("No form found for resource " + resource);
            }
            return form;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load htmlform from resource " + resource);
        }
    }

    /**
     * @return all encounters for the given patient that have the same encounter type as the given form
     */
    protected List<Encounter> getEncountersForForm(Patient p, HtmlForm form) {
        EncountersForPatientDataDefinition edd = new EncountersForPatientDataDefinition();
        edd.addType(form.getForm().getEncounterType());
        List<Encounter> ret = DataUtil.evaluateForPatient(edd, p.getPatientId(), List.class);
        return ret == null ? new ArrayList<Encounter>() : ret;
    }
}
