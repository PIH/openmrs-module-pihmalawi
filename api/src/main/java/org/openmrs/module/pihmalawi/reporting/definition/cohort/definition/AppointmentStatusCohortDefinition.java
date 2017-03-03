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

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.List;

/**
 * Return patients whose appointment status matches the given criteria
 */
public class AppointmentStatusCohortDefinition extends BaseCohortDefinition {

    @ConfigurationProperty
    private List<ProgramWorkflowState> activeStates;

    @ConfigurationProperty
    private List<EncounterType> encounterTypes;

	@ConfigurationProperty
	private List<Location> locations;

	@ConfigurationProperty
	private Date onDate;

    @ConfigurationProperty
    private Boolean noAppointmentIncluded;

    @ConfigurationProperty
    private Integer minDaysOverdue;

    @ConfigurationProperty
    private Integer maxDaysOverdue;

    @ConfigurationProperty
    private Integer minDaysToAppointment;

    @ConfigurationProperty
    private Integer maxDaysToAppointment;

	public AppointmentStatusCohortDefinition() {
		super();
	}

    public List<ProgramWorkflowState> getActiveStates() {
        return activeStates;
    }

    public void setActiveStates(List<ProgramWorkflowState> activeStates) {
        this.activeStates = activeStates;
    }

    public List<EncounterType> getEncounterTypes() {
        return encounterTypes;
    }

    public void setEncounterTypes(List<EncounterType> encounterTypes) {
        this.encounterTypes = encounterTypes;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public Date getOnDate() {
        return onDate;
    }

    public void setOnDate(Date onDate) {
        this.onDate = onDate;
    }

    public Boolean getNoAppointmentIncluded() {
        return noAppointmentIncluded;
    }

    public void setNoAppointmentIncluded(Boolean noAppointmentIncluded) {
        this.noAppointmentIncluded = noAppointmentIncluded;
    }

    public Integer getMinDaysOverdue() {
        return minDaysOverdue;
    }

    public void setMinDaysOverdue(Integer minDaysOverdue) {
        this.minDaysOverdue = minDaysOverdue;
    }

    public Integer getMaxDaysOverdue() {
        return maxDaysOverdue;
    }

    public void setMaxDaysOverdue(Integer maxDaysOverdue) {
        this.maxDaysOverdue = maxDaysOverdue;
    }

    public Integer getMinDaysToAppointment() {
        return minDaysToAppointment;
    }

    public void setMinDaysToAppointment(Integer minDaysToAppointment) {
        this.minDaysToAppointment = minDaysToAppointment;
    }

    public Integer getMaxDaysToAppointment() {
        return maxDaysToAppointment;
    }

    public void setMaxDaysToAppointment(Integer maxDaysToAppointment) {
        this.maxDaysToAppointment = maxDaysToAppointment;
    }
}