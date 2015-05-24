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
		String id = "(none)";
		if (pi != null) {
			id = pi.getIdentifier();
			try {
				DecimalFormat f = new java.text.DecimalFormat("0000");
				if (id.endsWith(" HCC")) {
					int firstSpace = id.indexOf(" ");
					int lastSpace = id.lastIndexOf(" ");
					String number = f.format(new Integer(id.substring(firstSpace + 1, lastSpace)));
					id = id.substring(0, firstSpace) + "-" + number + "-HCC";
				}
				else {
					if (id.lastIndexOf(" ") > 0) {
						// For now assume that an id without leading zeros is there when there is a space
						String number = f.format(new Integer(id.substring(id.lastIndexOf(" ") + 1)));
						id = id.substring(0, id.lastIndexOf(" ")) + "-" + number;
					}
				}
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