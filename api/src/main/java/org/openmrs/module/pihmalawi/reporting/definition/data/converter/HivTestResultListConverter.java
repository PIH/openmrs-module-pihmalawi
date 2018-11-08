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
import org.openmrs.module.htmlwidgets.util.ReflectionUtil;
import org.openmrs.module.pihmalawi.common.HivTestResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.List;

/**
 * Takes in a List<HivTestResult>, finds the most recent of the given type and returns the value of the property passed in
 * If type is null, matches on all.  If property is null, returns the HivTestResult
 */
public class HivTestResultListConverter implements DataConverter  {

    private Concept type;
    private String property;

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public HivTestResultListConverter() {
	}

	public HivTestResultListConverter(Concept type, String property) {
	    this.type = type;
	    this.property = property;
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
	    if (original != null) {
	        HivTestResult match = null;
            List<HivTestResult> fullList = (List<HivTestResult>) original;
            for (HivTestResult r : fullList) {
                if (type == null || type.equals(r.getTestType())) {
                    match = r;
                }
            }
            if (match != null) {
                if (property != null) {
                    return ReflectionUtil.getPropertyValue(match, property);
                }
                return match;
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

    public Concept getType() {
        return type;
    }

    public void setType(Concept type) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}