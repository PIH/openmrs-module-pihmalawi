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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.namephonetics.NamePhoneticsConstants;
import org.openmrs.module.reporting.report.util.ReportUtil;

/**
 * Initializes Soundex Configuration requirements
 */
public class SoundexInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(SoundexInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		ReportUtil.updateGlobalProperty(NamePhoneticsConstants.GIVEN_NAME_GLOBAL_PROPERTY, "Chichewa Soundex");
		ReportUtil.updateGlobalProperty(NamePhoneticsConstants.FAMILY_NAME_GLOBAL_PROPERTY, "Chichewa Soundex");
		ReportUtil.updateGlobalProperty(NamePhoneticsConstants.FAMILY_NAME2_GLOBAL_PROPERTY, "Chichewa Soundex");
	}

	@Override
	public void stopped() {
	}
}