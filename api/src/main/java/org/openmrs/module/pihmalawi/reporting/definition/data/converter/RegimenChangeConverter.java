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

import org.openmrs.module.pihmalawi.common.ArtRegimen;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes in a List of Art Regimens and returns a reduced List in which all entries that do not represent a change to regimen are removed
 */
public class RegimenChangeConverter implements DataConverter  {

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public RegimenChangeConverter() {
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
	    List<ArtRegimen> ret = null;
	    if (original != null) {
	        ret = new ArrayList<ArtRegimen>();
            List<ArtRegimen> fullList = (List<ArtRegimen>) original;
            ArtRegimen last = null;
            for (ArtRegimen r : fullList) {
                if (last == null || !last.getRegimen().equals(r.getRegimen())) {
                    ret.add(r);
                }
                last = r;
            }
        }
        return ret;
	}

	/**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}
}