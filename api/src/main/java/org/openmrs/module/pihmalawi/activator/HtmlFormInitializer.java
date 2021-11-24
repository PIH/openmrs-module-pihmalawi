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

package org.openmrs.module.pihmalawi.activator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.openmrs.Form;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Initializes all the htmlforms
 */
public class HtmlFormInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(HtmlFormInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		Map<Integer, String> forms = new LinkedHashMap<Integer, String>();

        HtmlFormEntryService hfes = Context.getService(HtmlFormEntryService.class);
		for (Integer formId : forms.keySet()) {
			HtmlForm htmlForm = hfes.getHtmlForm(formId);

            Form form = htmlForm.getForm();
            Hibernate.initialize(form);
            if (form instanceof HibernateProxy) {
                form = (Form)((HibernateProxy) form).getHibernateLazyInitializer().getImplementation();
            }
            htmlForm.setForm(form);

            String formResourcePath = "org/openmrs/module/pihmalawi/htmlforms/"+forms.get(formId);
			log.warn("Updating form: " + forms.get(formId));
			InputStream is = null;
			try {
				is = OpenmrsClassLoader.getInstance().getResourceAsStream(formResourcePath);
                htmlForm.setXmlData(IOUtils.toString(is, "UTF-8"));
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Error reading resource from the classpath", e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
			hfes.saveHtmlForm(htmlForm);
		}

        List<String> uiHtmlForms = Arrays.asList(
                "pihmalawi:htmlforms/art_mastercard.xml",
                "pihmalawi:htmlforms/art_visit.xml",
                "pihmalawi:htmlforms/preart_mastercard.xml",
                "pihmalawi:htmlforms/preart_visit.xml",
				"pihmalawi:htmlforms/eid_mastercard.xml",
                "pihmalawi:htmlforms/eid_test_results.xml",
				"pihmalawi:htmlforms/eid_visit.xml",
                "pihmalawi:htmlforms/asthma_hospital.xml",
                "pihmalawi:htmlforms/asthma_mastercard.xml",
                "pihmalawi:htmlforms/asthma_peak_flow.xml",
                "pihmalawi:htmlforms/asthma_visit.xml",
                "pihmalawi:htmlforms/htn_dm_mastercard.xml",
                "pihmalawi:htmlforms/htn_dm_visit.xml",
                "pihmalawi:htmlforms/htn_dm_labs.xml",
                "pihmalawi:htmlforms/htn_dm_hospital.xml",
                "pihmalawi:htmlforms/epilepsy_mastercard.xml",
                "pihmalawi:htmlforms/epilepsy_visit.xml" ,
                "pihmalawi:htmlforms/mental_health_mastercard.xml",
                "pihmalawi:htmlforms/mental_health_visit.xml",
                "pihmalawi:htmlforms/ks_header.xml",
                "pihmalawi:htmlforms/ks_chemo.xml",
                "pihmalawi:htmlforms/ks_eval.xml",
                "pihmalawi:htmlforms/ncd_mastercard.xml",
                "pihmalawi:htmlforms/ncd_visit.xml",
                "pihmalawi:htmlforms/art_annual_screening.xml",
				"pihmalawi:htmlforms/viral_load_test_results.xml",
				"pihmalawi:htmlforms/palliative_mastercard.xml",
				"pihmalawi:htmlforms/palliative_visit.xml",
                "pihmalawi:htmlforms/chf_annual_screening.xml",
                "pihmalawi:htmlforms/chf_history_of_hospitalizations.xml",
                "pihmalawi:htmlforms/chf_quarterly_screening.xml",
                "pihmalawi:htmlforms/chf_mastercard.xml",
                "pihmalawi:htmlforms/chf_visit.xml", 
                "pihmalawi:htmlforms/ckd_mastercard.xml", 
                "pihmalawi:htmlforms/ckd_annual_screening.xml", 
                "pihmalawi:htmlforms/ckd_history_of_hospitalizations.xml",
                "pihmalawi:htmlforms/ckd_quarterly_screening.xml", 
                "pihmalawi:htmlforms/ckd_visit.xml",
				"pihmalawi:htmlforms/ncd_other_visit.xml",
				"pihmalawi:htmlforms/ncd_other_mastercard.xml",
				"pihmalawi:htmlforms/ncd_other_quarterly_screening.xml",
				"pihmalawi:htmlforms/ncd_other_annual_screening.xml",
				"pihmalawi:htmlforms/ncd_other_hospitalizations.xml",
				"pihmalawi:htmlforms/trace_mastercard.xml",
				"pihmalawi:htmlforms/trace_visit.xml",
				"pihmalawi:htmlforms/pdc_mastercard.xml",
				"pihmalawi:htmlforms/pdc_visit.xml",
				"pihmalawi:htmlforms/pdc_developmental_delay_mastercard.xml",
				"pihmalawi:htmlforms/pdc_developmental_delay_visit.xml",
				"pihmalawi:htmlforms/pdc_cleft_lip_palate_visit.xml",
				"pihmalawi:htmlforms/pdc_cleft_lip_palate_mastercard.xml",
				"pihmalawi:htmlforms/pdc_trisomy_21_visit.xml",
		        "pihmalawi:htmlforms/pdc_other_diagnosis_visit.xml",
                "pihmalawi:htmlforms/pdc_trisomy_21_mastercard.xml");


        if (uiHtmlForms != null) {
            ResourceFactory resourceFactory = ResourceFactory.getInstance();
            FormService formService = Context.getFormService();
            for (String htmlform : uiHtmlForms) {
                try {
                    HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, hfes, htmlform);
                } catch (IOException e) {
                    log.error("Unable to load HTML forms");
                }
            }
        }

	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}
}
