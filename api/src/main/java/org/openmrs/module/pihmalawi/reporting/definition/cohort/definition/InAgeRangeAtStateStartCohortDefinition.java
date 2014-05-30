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
package org.openmrs.module.pihmalawi.reporting.definition.cohort.definition;

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

public class InAgeRangeAtStateStartCohortDefinition extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState state;
	
	@ConfigurationProperty
	private Date startedOnOrAfter;
	
	@ConfigurationProperty
	private Date startedOnOrBefore;
	
	@ConfigurationProperty(required = false)
	private Integer minAge;
	
	@ConfigurationProperty(required = false)
	private Age.Unit minAgeUnit = Age.Unit.YEARS;
	
	@ConfigurationProperty(required = false)
	private Integer maxAge;
	
	@ConfigurationProperty(required = false)
	private Age.Unit maxAgeUnit = Age.Unit.YEARS;

	@ConfigurationProperty(required = false)
	private Location location = null;
	
	public InAgeRangeAtStateStartCohortDefinition() {
		super();
	}
	
	public Date getStartedOnOrAfter() {
		return startedOnOrAfter;
	}
	
	public void setStartedOnOrAfter(Date startedOnOrAfter) {
		this.startedOnOrAfter = startedOnOrAfter;
	}
	
	public Date getStartedOnOrBefore() {
		return startedOnOrBefore;
	}
	
	public void setStartedOnOrBefore(Date startedOnOrBefore) {
		this.startedOnOrBefore = startedOnOrBefore;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public ProgramWorkflowState getState() {
		return state;
	}

	public void setState(ProgramWorkflowState state) {
		this.state = state;
	}
	
	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}
	
	public Age.Unit getMinAgeUnit() {
		return minAgeUnit;
	}

	public void setMinAgeUnit(Age.Unit minAgeUnit) {
		this.minAgeUnit = minAgeUnit;
	}
	
	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	
	public Age.Unit getMaxAgeUnit() {
		return maxAgeUnit;
	}

	public void setMaxAgeUnit(Age.Unit maxAgeUnit) {
		this.maxAgeUnit = maxAgeUnit;
	}
}