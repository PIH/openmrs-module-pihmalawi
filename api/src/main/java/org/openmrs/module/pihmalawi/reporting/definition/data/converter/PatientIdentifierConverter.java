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

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DecimalFormat;

/**
 * Boolean data converter
 */
public class PatientIdentifierConverter implements DataConverter  {

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public PatientIdentifierConverter() { }

	//***** INSTANCE METHODS *****

	/**
     * Attempts to convert a patient identifier value into a String of consistent length with leading zeros for numeric component for easier sorting.
	 * @should convert a PatientIdentifier to a PIH Malawi standard representation
	 */
	public Object convert(Object original) {
		PatientIdentifier pi = (PatientIdentifier)original;
		String id = "(none)";
		if (pi != null) {
			id = pi.getIdentifier();
			try {
                StringBuilder ret = new StringBuilder();
				DecimalFormat f = new java.text.DecimalFormat("0000");
                String[] split = StringUtils.splitByWholeSeparator(id, " ");
                for (int i=0; i<split.length; i++) {
                    ret.append(i == 0 ? "" : "-");
                    ret.append(i == 1 ? f.format(Integer.valueOf(split[i])) : split[i]);
                }
                id = ret.toString();
			}
			catch (Exception e) {
				// If something unexpected happens, just return the identifier as is
			}
		}
		return id;
	}

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return String.class;
	}

	/**
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return PatientIdentifier.class;
	}
}