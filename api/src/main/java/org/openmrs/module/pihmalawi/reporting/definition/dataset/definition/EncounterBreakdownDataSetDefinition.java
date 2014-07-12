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

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Returns a breakdown of the number of encounters per configured time period, by user or location
 */
public class EncounterBreakdownDataSetDefinition extends BaseDataSetDefinition {

	public enum Grouping {
		User, Location
	}

	@ConfigurationProperty
	private Date endDate;

	@ConfigurationProperty
	private List<EncounterType> types;

	@ConfigurationProperty
	private Integer numberOfWeeks = 12;

	@ConfigurationProperty
	private Grouping grouping;

	/**
	 * Constructor
	 */
	public EncounterBreakdownDataSetDefinition() {}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Grouping getGrouping() {
		return grouping;
	}

	public void setGrouping(Grouping grouping) {
		this.grouping = grouping;
	}

	public List<EncounterType> getTypes() {
		return types;
	}

	public void setTypes(List<EncounterType> types) {
		this.types = types;
	}

	public void addType(EncounterType type) {
		if (types == null) {
			types = new ArrayList<EncounterType>();
		}
		types.add(type);
	}

	public Integer getNumberOfWeeks() {
		return numberOfWeeks;
	}

	public void setNumberOfWeeks(Integer numberOfWeeks) {
		this.numberOfWeeks = numberOfWeeks;
	}
}
