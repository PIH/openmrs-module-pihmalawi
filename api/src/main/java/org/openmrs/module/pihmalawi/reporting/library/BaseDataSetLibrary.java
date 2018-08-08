/*
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
package org.openmrs.module.pihmalawi.reporting.library;

import org.openmrs.module.pihmalawi.reporting.reports.IC3AppointmentReport;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseDataSetLibrary extends BaseDefinitionLibrary<DataSetDefinition> {

    @Autowired
    IC3AppointmentReport ic3AppointmentReport;

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.dataset.";
    }

	@Override
	public Class<? super DataSetDefinition> getDefinitionType() {
		return DataSetDefinition.class;
	}

	// Data Sets

	@DocumentedDefinition(value = "ic3AppointmentData")
	public DataSetDefinition getIC3AppointmentData() {
        return ic3AppointmentReport.constructDataSetDefinition();
	}
}
