/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Custom Age Converter that converts an Age to a number in the specified full units
 */
public class AgeInDaysConverter implements DataConverter {
	
	//***** CONSTRUCTORS *****
	
	public AgeInDaysConverter() {}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 */
	public Object convert(Object original) {
		Age age = (Age) original;
		if (age == null) {
            return null;
        }
        return DateUtil.getDaysBetween(age.getBirthDate(), age.getCurrentDate());
	}

    /**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
	    return Integer.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Age.class;
	}
}