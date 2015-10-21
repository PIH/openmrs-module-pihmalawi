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
import org.openmrs.module.pihmalawi.metadata.group.HccTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HccMissedAppointmentReport extends ApzuMissedAppointmentReport {

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	HccTreatmentGroup hccTreatmentGroup;

	@Override
	public String getUuid() {
		return "19fdad06-2a3d-11e4-8981-e82aea237783";
	}

	@Override
	public String getName() {
		return "HCC Missed Appointment";
	}

	@Override
	public String getReportDesignUuid() {
		return "2ef7a230-2a3d-11e4-8c21-0800200c9a66";
	}

	/**
	 * The whole report should be filtered on only those patients actively in HCC
	 */
	@Override
	public CohortDefinition getBaseCohort() {
		CohortDefinition inHivProgram = df.getActivelyEnrolledInProgramAtLocationOnEndDate(hivMetadata.getHivProgram());
		CohortDefinition inHcc = hivCohorts.getInPreArtOrExposedChildStateWithHccNumberAtLocationOnEndDate();
		CohortDefinition transferredIn = hivCohorts.getInTransferredInternallyAtLocationOnEndDate();
		CohortDefinition cd = df.createPatientComposition(inHivProgram, "AND (", inHcc, "OR", transferredIn, ")");
		cd.setName("In HCC");
		return cd;
	}

	@Override
	public List<Location> getLocations() {
		return hivMetadata.getHivStaticSystemLocations();
	}

	@Override
	public TreatmentGroup getTreatmentGroup() {
		return hccTreatmentGroup;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
}
