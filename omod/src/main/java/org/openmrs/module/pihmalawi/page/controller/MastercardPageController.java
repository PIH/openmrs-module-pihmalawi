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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MastercardPageController {

	public void controller(@RequestParam(value="patientId", required=false) Patient patient,
                           @RequestParam(value="headerForm") String headerForm,
                           @RequestParam(value="flowsheets") String[] flowsheets,
                           @RequestParam(value="viewOnly", required = false) Boolean viewOnly,
                           UiUtils ui, PageModel model,
                           @SpringBean("htmlFormEntryService") HtmlFormEntryService htmlFormEntryService,
                           @SpringBean("formService") FormService formService,
                           @SpringBean("coreResourceFactory") ResourceFactory resourceFactory,
                           @SpringBean("reportingPatientDataService") PatientDataService patientDataService,
	                       @InjectBeans PatientDomainWrapper patientDomainWrapper) {

		patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("headerForm", headerForm);
        model.addAttribute("flowsheets", flowsheets);

        Location defaultLocation = LocationUtility.getUserDefaultLocation();

        List<Encounter> allEncounters = new ArrayList<Encounter>();

        List<String> alerts = new ArrayList<String>();

        String headerFormResource = "pihmalawi:htmlforms/" + headerForm + ".xml";

        HtmlForm headerHtmlForm = getHtmlFormFromResource(headerFormResource, resourceFactory, formService, htmlFormEntryService);
        model.addAttribute("headerForm", headerForm);

        Encounter headerEncounter = null;
        List<Encounter> headerEncounters = getEncountersForForm(patient, headerHtmlForm);
        if (headerEncounters.size() > 0) {
            headerEncounter = headerEncounters.get(headerEncounters.size() - 1); // Most recent
            if (headerEncounters.size() > 1) {
                alerts.add("WARNING:  More than one " + headerHtmlForm.getName() + " encounters exist for this patient.  Displaying the most recent only.");
            }
            allEncounters.add(headerEncounter);
        }
        model.addAttribute("headerEncounter", headerEncounter);

        Map<String, HtmlForm> flowsheetForms = new LinkedHashMap<String, HtmlForm>();
        Map<String, List<Integer>> flowsheetEncounters = new LinkedHashMap<String, List<Integer>>();
        if (flowsheets != null) {
            for (String flowsheet : flowsheets) {
                String flowsheetResource = "pihmalawi:htmlforms/" + flowsheet + ".xml";
                HtmlForm htmlForm = getHtmlFormFromResource(flowsheetResource, resourceFactory, formService, htmlFormEntryService);
                flowsheetForms.put(flowsheet, htmlForm);
                List<Integer> encIds = new ArrayList<Integer>();
                List<Encounter> encounters = getEncountersForForm(patient, htmlForm);
                for (Encounter e : encounters) {
                    encIds.add(e.getEncounterId());
                    allEncounters.add(e);
                }
                flowsheetEncounters.put(flowsheet, encIds);
            }
        }
        model.addAttribute("flowsheetForms", flowsheetForms);
        model.addAttribute("flowsheetEncounters", flowsheetEncounters);

        model.addAttribute("alerts", alerts);

        if (defaultLocation == null) {
            Date maxDate = null;
            if (allEncounters.size() > 0) {
                for (Encounter e : allEncounters) {
                    if (maxDate == null || maxDate.compareTo(e.getEncounterDatetime()) < 0) {
                        maxDate = e.getEncounterDatetime();
                        defaultLocation = e.getLocation();
                    }
                }
            }
        }
        model.addAttribute("defaultLocationId", defaultLocation == null ? null : defaultLocation.getLocationId());
        model.addAttribute("viewOnly", viewOnly == Boolean.TRUE);

        model.addAttribute("returnUrl", ui.pageLink("pihmalawi", "mastercard", SimpleObject.create("patientId", patient.getId(), "headerForm", headerForm, "flowsheets", flowsheets, "viewOnly", viewOnly)));
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
