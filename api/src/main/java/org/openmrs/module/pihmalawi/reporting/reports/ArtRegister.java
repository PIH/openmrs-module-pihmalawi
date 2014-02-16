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

import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArtRegister extends BaseReportManager {

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData;

	public ArtRegister() {}

	@Override
	public String getUuid() {
		return "fa20c1ac-94ea-11e3-96de-0023156365e4";
	}

	@Override
	public String getName() {
		return "ART Register";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(ReportingConstants.END_DATE_PARAMETER);
		l.add(ReportingConstants.LOCATION_PARAMETER);
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		PatientDataSetDefinition dsd = new PatientDataSetDefinition();

		// Rows are defined as all patients who ever have been in the On Antiretrovirals state at the given location
		CohortDefinition everEnrolled = hivCohorts.getEverEnrolledInArtAtLocationByEndDate();
		dsd.addRowFilter(Mapped.mapStraightThrough(everEnrolled));

		dsd.addColumn("#", builtInPatientData.getPatientId(), "");
		dsd.addColumn("Given Name", builtInPatientData.getPreferredGivenName(), "");
		dsd.addColumn("Family Name", builtInPatientData.getPreferredFamilyName(), "");

		// TODO:  Add "Facility" column for the location of the report from parameter value
		// TODO:  Add "Identifier", pdh.preferredIdentifierAtLocation(p, patientIdentifierType, location))

		dsd.addColumn("Birthdate", builtInPatientData.getBirthdate(), "");
		dsd.addColumn("M/F", builtInPatientData.getGender(), "");

		// TODO:  Add "Village", pdh.getVillage(p)
		// TODO:  Add "VHW", pdh.vhwName(p, false)

		// This formerly created an indicator backing this, and added to period indicator report as "breakdown"

		// 2 output formats:
		//  1. 		HtmlBreakdownDataSetDefinition dsd = new HtmlBreakdownDataSetDefinition();
		//			dsd.setPatientIdentifierType(Context.getPatientService().getPatientIdentifierTypeByName("ARV Number"));
		//			dsd.setHtmlBreakdownPatientRowClassname(ArtRegisterBreakdownRenderer.class.getName());
		//			rendered using Helper.createHtmlBreakdown
		//
		//  2. 		ApzuBMIPatientDataSetDefinition dsd = new ApzuBMIPatientDataSetDefinition();
		//			dsd.setNumericConcept(Context.getConceptService().getConcept(5089));

		// Adherence
		// Name:  ART Register Appointment Adherence
		// Parameters: SD, ED, Loc
		// rowCohort:  Same as ART Register (hivCohorts.getEverEnrolledInArtAtLocationOnDate();)
		// columns:  Defined in:
		//		dsd = new AppointmentAdherencePatientDataSetDefinition();
		//		dsd.setEncounterTypes(Arrays.asList(Metadata.encounterType("ART_FOLLOWUP")));
		//		dsd.setPatientIdentifierType(Context.getPatientService().getPatientIdentifierTypeByName("ARV Number"));
		//		rendered using Helper.createHtmlBreakdown

		// For all locations
		// Same as above, just rowCohort is not limited to a location - ApzuReportElementsArt.artEverEnrolledOnDate(prefix);
		// createHtmlBreakdown(rd, "ART Register For All Locations_");

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		return null;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
}
