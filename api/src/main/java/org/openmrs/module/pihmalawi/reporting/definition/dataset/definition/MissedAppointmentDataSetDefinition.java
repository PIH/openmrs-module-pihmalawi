/*
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

import org.openmrs.Location;
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.List;

public class MissedAppointmentDataSetDefinition extends BaseDataSetDefinition {

	public enum Mode {
		OVERVIEW, BETWEEN_2_AND_3_WEEKS_LATE, MORE_THAN_3_WEEKS_LATE
	}

	@ConfigurationProperty
	private Mode mode;

	@ConfigurationProperty
	private Date endDate;

	@ConfigurationProperty
	private CohortDefinition baseCohort;

	@ConfigurationProperty
	private List<Location> locations;

	@ConfigurationProperty
	private TreatmentGroup treatmentGroup;

	public MissedAppointmentDataSetDefinition() {}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public CohortDefinition getBaseCohort() {
		return baseCohort;
	}

	public void setBaseCohort(CohortDefinition baseCohort) {
		this.baseCohort = baseCohort;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public TreatmentGroup getTreatmentGroup() {
		return treatmentGroup;
	}

	public void setTreatmentGroup(TreatmentGroup treatmentGroup) {
		this.treatmentGroup = treatmentGroup;
	}
}
