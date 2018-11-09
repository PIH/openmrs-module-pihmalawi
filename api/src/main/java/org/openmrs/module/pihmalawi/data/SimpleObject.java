/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.data;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.OpenmrsObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Based on SimpleObject from webservices.rest
 */
public class SimpleObject extends LinkedHashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	public SimpleObject() {
		super();
	}

    @Override
    public Object put(String key, Object value) {
        return super.put(key, convert(value));
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
	    for (Map.Entry e : m.entrySet()) {
	        put((String)e.getKey(), e.getValue());
        }
        super.putAll(m);
    }

    /**
	 * Creates an instance from the given json string.
	 */
	public static SimpleObject fromJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
            return objectMapper.readValue(json, SimpleObject.class);
        }
        catch (Exception e) {
		    throw new IllegalArgumentException("Unable to parse json into SimpleObject", e);
        }
	}

    /**
     * Serializes to a JSON string
     */
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse json into SimpleObject", e);
        }
    }

    @Override
    public String toString() {
        return toJson();
    }

    /**
     * Converts objects passed in to standard data types
     * TODO: DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"; ??
     */
    protected Object convert(Object o) {
        if (o instanceof OpenmrsObject) {
            return ((OpenmrsObject)o).getUuid();
        }
        else if (o instanceof Collection) {
            List<Object> l = new ArrayList<Object>();
            for (Object v : ((Collection)o)) {
                l.add(convert(v));
            }
            return l;
        }
        return o;
    }
}
