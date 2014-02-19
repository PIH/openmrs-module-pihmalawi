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
package org.openmrs.module.pihmalawi.reporting.data.converter;

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
	 * @see org.openmrs.module.reporting.data.converter.DataConverter#convert(Object)
	 * @should convert a PatientIdentifier to a PIH Malawi standard representation
	 */
	public Object convert(Object original) {
		PatientIdentifier pi = (PatientIdentifier)original;
		if (pi == null) {
			return "(none)";
		}
		String id = pi.getIdentifier();

		if (id.endsWith(" HCC")) {
			int firstSpace = id.indexOf(" ");
			int lastSpace = id.lastIndexOf(" ");
			String number = id.substring(firstSpace + 1, lastSpace);
			try {
				DecimalFormat f = new java.text.DecimalFormat("0000");
				number = f.format(new Integer(number));
			}
			catch (Exception e) {
				return id; // Error while converting
			}
			return id.substring(0, firstSpace) + "-" + number + "-HCC";
		}
		else {
			if (id.lastIndexOf(" ") > 0) {
				// For now assume that an id without leading zeros is there when there is a space
				String number = id.substring(id.lastIndexOf(" ") + 1);
				try {
					DecimalFormat f = new java.text.DecimalFormat("0000");
					number = f.format(new Integer(number));
				}
				catch (Exception e) {
					return id; // Error while converting
				}
				return id.substring(0, id.lastIndexOf(" ")) + "-" + number;
			}
			return id;
		}
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