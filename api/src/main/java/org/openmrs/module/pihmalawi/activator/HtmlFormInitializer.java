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
import org.openmrs.OpenmrsObject;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlFormExporter;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Initializes all the htmlforms
 */
public class HtmlFormInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(HtmlFormInitializer.class);

	private static final Map<String, String> conceptMap;

	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("SampleCollected", "165252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		aMap.put("SampleQuality", "165253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		aMap.put("SystolicBloodPressure", "6569bffe-977f-11e1-8993-905e29aff6c1");
		aMap.put("DiastolicBloodPressure", "6569c116-977f-11e1-8993-905e29aff6c1");
		aMap.put("Height", "6569c562-977f-11e1-8993-905e29aff6c1");
		aMap.put("Weight", "6569c44a-977f-11e1-8993-905e29aff6c1");
		aMap.put("MUAC", "6558d09a-977f-11e1-8993-905e29aff6c1");
		aMap.put("Pregnant", "656fbd28-977f-11e1-8993-905e29aff6c1'");
		aMap.put("BMI", "655d615a-977f-11e1-8993-905e29aff6c1");
		aMap.put("HIV_TEST_CONSTRUCT", "655dca78-977f-11e1-8993-905e29aff6c");
		aMap.put("HIV_TEST_TYPE", "655bee06-977f-11e1-8993-905e29aff6c1");
		aMap.put("DNA_PCR_TEST_RESULT", "654a6960-977f-11e1-8993-905e29aff6c1");
		aMap.put("HIV_TEST_RESULTS", "655dcb7c-977f-11e1-8993-905e29aff6c1");
		aMap.put("AdherenceSession", "06b1f7d8-b6cc-11e8-96f8-529269fb1459");
		aMap.put("NameOfCounselor", "6562b4fc-977f-11e1-8993-905e29aff6c1");
		aMap.put("CounseledOnPillCounts", "06b2005c-b6cc-11e8-96f8-529269fb1459");
		aMap.put("DrugAdherencePercentage", "20E91F16-BA4F-4058-B17A-998A82F4B803'");
		aMap.put("CounseledOnViralLoad", "06b20a2a-b6cc-11e8-96f8-529269fb1459");
		aMap.put("ViralLoadTestSet", "83931c6d-0e5a-4302-b8ce-a31175b6475");
		aMap.put("ViralLoad", "654a7694-977f-11e1-8993-905e29aff6c1");
		aMap.put("ViralLoadLessThanLimit", "69e87644-5562-11e9-8647-d663bd873d93");
		aMap.put("HIVViralLoadStatus", "163310AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		aMap.put("ViralLoadLowerThanDetectionLimit", "e97b36a2-16f5-11e6-b6ba-3e1d05defe78");
		aMap.put("ViralLoadLowerThanDetectionLimitTrue", "655e2f90-977f-11e1-8993-905e29aff6c1");
		aMap.put("ViralLoadDetectablelowerLimit", "53cb83ed-5d55-4b63-922f-d6b8fc67a5f8");
		aMap.put("Bled", "f792f2f9-9c24-4d6e-98fd-caffa8f2383f");
		aMap.put("ReasonForNoSample", "0e447d92-a180-11e8-98d0-529269fb145");
		aMap.put("ReasonForTesting", "164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		aMap.put("LabLocation", "6fc0ab50-9492-11e7-abc4-cec278b6b50a");
		aMap.put("TuberculosisTestScreeningSet", "4c92373c-28d6-11e9-b210-d663bd873d93");
		aMap.put("TuberculosisScreeningSet", "6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478");
		aMap.put("SymptomPresent", "6558d3ba-977f-11e1-8993-905e29aff6c1");
		aMap.put("SymptomAbsent", "655b50fe-977f-11e1-8993-905e29aff6c1");
		aMap.put("SOURCE_OF_REFERRAL", "65664fc2-977f-11e1-8993-905e29aff6c1");
		aMap.put("TBTestType", "38c4512a-5aef-487d-a450-ecea4bc5df7e");
		aMap.put("GeneXpert", "162202AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		aMap.put("Smear", "65628568-977f-11e1-8993-905e29aff6c1");
		aMap.put("ReasonForNoResult", "656fa450-977f-11e1-8993-905e29aff6c1");
		aMap.put("RifampinResistance", "164937AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		aMap.put("ClinicalNotes", "655928e2-977f-11e1-8993-905e29aff6c");
		aMap.put("Outcome", "6571d95a-977f-11e1-8993-905e29aff6c1");
		aMap.put("NextAppointmentDate", "6569cbd4-977f-11e1-8993-905e29aff6c1");
		aMap.put("QualitativeTime", "4c923fca-28d6-11e9-b210-d663bd873d93");
		aMap.put("TransferFacility", "65624b3e-977f-11e1-8993-905e29aff6c1");
		aMap.put("ReasonToStopCare", "558a783a-2990-11e9-b210-d663bd873d93");
		aMap.put("OtherOutcome", "558a7114-2990-11e9-b210-d663bd873d93");
		aMap.put("BreastFeeding", "657a289e-977f-11e1-8993-905e29aff6c1");

		conceptMap = Collections.unmodifiableMap(aMap);
	}

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
               // "pihmalawi:htmlforms/ks_eval.xml",
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
				"pihmalawi:htmlforms/ncd_other_hospitalizations.xml");

        if (uiHtmlForms != null) {
            ResourceFactory resourceFactory = ResourceFactory.getInstance();
            FormService formService = Context.getFormService();
            for (String htmlform : uiHtmlForms) {
                try {
                    HtmlForm form = new HtmlFormExporter(HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, hfes, htmlform)).export(false, false, false, false);
					System.out.println("Loading form " + htmlform);
					for (OpenmrsObject metadata : form.getDependencies())
                    	for (Map.Entry<String, String> concept : conceptMap.entrySet()) {
                    		if (metadata.getUuid().equals(concept.getValue())) {
                    			System.out.println("Uses " + concept.getKey());
							}
 						}
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
