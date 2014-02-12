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
package org.openmrs.module.pihmalawi.web.extension;

import org.openmrs.module.web.extension.BoxExt;

public class EMastercardsBoxExt extends BoxExt {

	/**
	 * @see org.openmrs.module.web.extension.BoxExt#getRequiredPrivilege()
	 */
	@Override
	public String getRequiredPrivilege() {
		return "View Patients";
	}

	/**
	 * @see org.openmrs.module.web.extension.BoxExt#getPortletUrl()
	 */
	@Override
	public String getPortletUrl() {
		return "eMastercards";
	}

	/**
	 * @see org.openmrs.module.web.extension.BoxExt#getTitle()
	 */
	@Override
	public String getTitle() {
		return "pihmalawi.emastercards.sectionTitle";
	}

	/**
	 * @see org.openmrs.module.web.extension.BoxExt#getContent()
	 */
	@Override
	public String getContent() {
		return "";
	}
}
