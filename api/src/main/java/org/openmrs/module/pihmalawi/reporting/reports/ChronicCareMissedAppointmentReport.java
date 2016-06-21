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
package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.Location;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.group.ChronicCareTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChronicCareMissedAppointmentReport extends ApzuMissedAppointmentReport {

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCareMetadata ccMetadata;

	@Autowired
	private ChronicCareCohortDefinitionLibrary ccCohorts;

	@Autowired
	ChronicCareTreatmentGroup chronicCareTreatmentGroup;

	@Override
	public String getUuid() {
		return "86751b60-2a41-11e4-8c21-0800200c9a66";
	}

	@Override
	public String getName() {
		return "Chronic Care Missed Appointment";
	}

	@Override
	public String getReportDesignUuid() {
		return "86751b61-2a41-11e4-8c21-0800200c9a66";
	}

	@Override
	public String getModes() {
		return "OVERVIEW,MORE_THAN_A_WEEK_LATE";
	}

	/**
	 * The whole report should be filtered on only those patients actively in CC
	 */
	@Override
	public CohortDefinition getBaseCohort() {
		CohortDefinition cd = df.getActivelyEnrolledInProgramAtLocationOnEndDate(ccMetadata.getChronicCareProgram());
		cd.setName("In CC");
		return cd;
	}

	@Override
	public List<Location> getLocations() {
		return ccMetadata.getChronicCareSystemLocations();
	}

	@Override
	public TreatmentGroup getTreatmentGroup() {
		return chronicCareTreatmentGroup;
	}
}
