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

import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Who Stage data converter
 */
public class ViralLoadValueConverter implements DataConverter  {

    private Object valueForLdl;

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public ViralLoadValueConverter(Object valueForLdl) {
        this.valueForLdl = valueForLdl;
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#convert(Object)
	 */
	public Object convert(Object original) {
        ViralLoad vl = (ViralLoad) original;
        if (vl != null) {
            if (vl.getResultNumeric() != null) {
                return vl.getResultNumeric();
            }
            else if (vl.getResultLdl() != null && vl.getResultLdl()) {
                return valueForLdl;
            }
            else if (vl.getLessThanResultNumeric() != null) {
            	return "<" + vl.getLessThanResultNumeric();
			}
        }
        return null;
	}

	/**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return Object.class;
	}

	/**
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return ViralLoad.class;
	}

    public Object getValueForLdl() {
        return valueForLdl;
    }

    public void setValueForLdl(Object valueForLdl) {
        this.valueForLdl = valueForLdl;
    }
}
