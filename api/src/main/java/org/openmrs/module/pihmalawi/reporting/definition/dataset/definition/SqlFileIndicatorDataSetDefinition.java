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
package org.openmrs.module.pihmalawi.reporting.definition.dataset.definition;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Extends SqlFileDataSetDefinition but produces a MapDataSet for an Indicator Report
 */
public class SqlFileIndicatorDataSetDefinition extends SqlFileDataSetDefinition {

	@ConfigurationProperty
	private String indicatorNameColumn;

    @ConfigurationProperty
    private String indicatorDescriptionColumn;

    @ConfigurationProperty
    private String indicatorValueColumn;

	/**
	 * Constructor
	 */
	public SqlFileIndicatorDataSetDefinition() {}

    public String getIndicatorNameColumn() {
        return indicatorNameColumn;
    }

    public void setIndicatorNameColumn(String indicatorNameColumn) {
        this.indicatorNameColumn = indicatorNameColumn;
    }

    public String getIndicatorDescriptionColumn() {
        return indicatorDescriptionColumn;
    }

    public void setIndicatorDescriptionColumn(String indicatorDescriptionColumn) {
        this.indicatorDescriptionColumn = indicatorDescriptionColumn;
    }

    public String getIndicatorValueColumn() {
        return indicatorValueColumn;
    }

    public void setIndicatorValueColumn(String indicatorValueColumn) {
        this.indicatorValueColumn = indicatorValueColumn;
    }
}
