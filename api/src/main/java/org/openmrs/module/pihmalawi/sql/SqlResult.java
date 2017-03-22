/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the result from executing a script with MysqlRunner
 */
public class SqlResult {

    private List<String> columns;
    private List<Map<String, String>> data;
    private List<String> errors;

    public List<String> getColumns() {
        if (columns == null) {
            columns = new ArrayList<String>();
        }
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Map<String, String>> getData() {
        if (data == null) {
            data = new ArrayList<Map<String, String>>();
        }
        return data;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public List<String> getErrors() {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
