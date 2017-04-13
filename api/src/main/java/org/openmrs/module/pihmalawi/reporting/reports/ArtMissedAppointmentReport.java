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
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.group.ArtTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Deprecated
public class ArtMissedAppointmentReport extends ApzuMissedAppointmentReport {

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	ArtTreatmentGroup artTreatmentGroup;

	@Override
	public String getUuid() {
		return "08c63059-9017-4767-8bf3-c16d0ace92a8";
	}

	@Override
	public String getName() {
		return "ART Missed Appointment";
	}

	@Override
	public String getReportDesignUuid() {
		return "96b3abec-3d5a-4d46-80a4-777e60ec92e6";
	}

	/**
	 * The whole report should be filtered on only those patients actively on ART
	 */
	@Override
	public CohortDefinition getBaseCohort() {
		CohortDefinition inHivProgram = df.getActivelyEnrolledInProgramAtLocationOnEndDate(hivMetadata.getHivProgram());
		CohortDefinition onArt = hivCohorts.getInOnArtStateAtLocationOnEndDate();
		CohortDefinition transferredIn = hivCohorts.getInTransferredInternallyAtLocationOnEndDate();
		CohortDefinition cd = df.createPatientComposition(inHivProgram, "AND (", onArt, "OR", transferredIn, ")");
		cd.setName("On ART");
		return cd;
	}

	@Override
	public List<Location> getLocations() {
		return hivMetadata.getHivStaticSystemLocations();
	}

	@Override
	public TreatmentGroup getTreatmentGroup() {
		return artTreatmentGroup;
	}
}
