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
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.InputStream;
import java.util.LinkedHashMap;
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
		forms.put(14, "chronic_care_emastercard_initial.xml");
		forms.put(15, "chronic_care_emastercard_visit.xml");

		HtmlFormEntryService hfes = Context.getService(HtmlFormEntryService.class);
		for (Integer formId : forms.keySet()) {
			HtmlForm form = hfes.getHtmlForm(formId);
			String formResourcePath = "org/openmrs/module/pihmalawi/htmlforms/"+forms.get(formId);
			log.warn("Updating form: " + forms.get(formId));
			InputStream is = null;
			try {
				is = OpenmrsClassLoader.getInstance().getResourceAsStream(formResourcePath);
				form.setXmlData(IOUtils.toString(is, "UTF-8"));
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Error reading resource from the classpath", e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
			hfes.saveHtmlForm(form);
		}
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
	}
}
