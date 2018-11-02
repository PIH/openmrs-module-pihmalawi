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

import java.util.List;

/**
 * Who Stage data converter
 */
public class MostRecentViralLoadResultConverter implements DataConverter  {

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public MostRecentViralLoadResultConverter() {
	}

    @Override
    public Class<?> getInputDataType() {
        return List.class;
    }

    //***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#convert(Object)
	 */
	public Object convert(Object original) {
	    ViralLoad ret = null;
	    if (original != null) {
            List<ViralLoad> viralLoads = (List<ViralLoad>) original;
            for (ViralLoad vl : viralLoads) {
                if (vl.getResultLdl() != null || vl.getResultNumeric() != null) {
                    if (ret == null || ret.getResultDate().before(vl.getResultDate())) {
                        ret = vl;
                    }
                }
            }
        }
        return ret;
	}

	/**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return ViralLoad.class;
	}
}