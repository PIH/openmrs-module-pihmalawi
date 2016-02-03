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
package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.openmrs.Concept;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DecimalFormat;

/**
 * Who Stage data converter
 */
public class WhoStageConverter implements DataConverter  {

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public WhoStageConverter() { }

	//***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#convert(Object)
	 */
	public Object convert(Object original) {
		String ret = ObjectUtil.format(original);
        return ret.replace("adult", "").replace("and", "").replace("peds", "").trim();
	}

	/**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}

	/**
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Concept.class;
	}
}