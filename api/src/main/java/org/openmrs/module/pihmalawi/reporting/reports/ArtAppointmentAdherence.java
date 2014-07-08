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

import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reports.dataset.AppointmentAdherencePatientDataSetDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ArtAppointmentAdherence extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "f2ba89b4-b6b7-4e69-91d7-68a5653ed1a6";

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	public ArtAppointmentAdherence() {}

	@Override
	public String getUuid() {
		return "d37cadb3-e422-42d6-b877-456b0e48fc30";
	}

	@Override
	public String getName() {
		return "ART Register Appointment Adherence";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(df.getStartDateParameter());
		l.add(df.getEndDateParameter());
		l.add(df.getOptionalLocationParameter());
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		CohortDefinition everEnrolledInArt = hivCohorts.getEverEnrolledInArtAtLocationByEndDate();
		rd.setBaseCohortDefinition(Mapped.mapStraightThrough(everEnrolledInArt));

		AppointmentAdherencePatientDataSetDefinition dsd = new AppointmentAdherencePatientDataSetDefinition();
		dsd.setEncounterTypes(Arrays.asList(hivMetadata.getArtFollowupEncounterType()));
		dsd.setPatientIdentifierType(hivMetadata.getArvNumberIdentifierType());
		rd.addDataSetDefinition("patients", Mapped.mapStraightThrough(dsd));

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(createExcelDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition));
		return l;
	}

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
