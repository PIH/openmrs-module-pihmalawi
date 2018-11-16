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

import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Collection;

/**

 * Returns the max value in the source collection
 */
public class MaxValueConverter<T extends Comparable> implements DataConverter {

    public MaxValueConverter() {
    }

    @Override
    public Object convert(Object original) {
        Collection<T> c = (Collection<T>) original;
        if (c == null) {
            return null;
        }
        Comparable max = null;
        for (Comparable o : c) {
            if (max == null || o.compareTo(max) > 0) {
                max = o;
            }
        }
        return max;
    }

    @Override
    public Class<?> getInputDataType() {
        return Collection.class;
    }

    @Override
    public Class<?> getDataType() {
        return Comparable.class;
    }
}
